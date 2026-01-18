package com.iabdinur.service;

import com.iabdinur.dao.SentEmailDao;
import com.iabdinur.model.SentEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

@Service
public class SesEmailService implements EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(SesEmailService.class);
    
    private final String fromEmail;
    private final String awsAccessKeyId;
    private final String awsSecretAccessKey;
    private final String awsRegion;
    private final boolean enabled;
    private final SentEmailDao sentEmailDao;
    
    private SesClient sesClient;
    
    public SesEmailService(
            @Value("${app.email.from:noreply@iabdinur.com}") String fromEmail,
            @Value("${aws.access-key-id:}") String awsAccessKeyId,
            @Value("${aws.secret-access-key:}") String awsSecretAccessKey,
            @Value("${aws.region:us-east-1}") String awsRegion,
            @Value("${app.email.enabled:false}") boolean enabled,
            SentEmailDao sentEmailDao) {
        this.fromEmail = fromEmail;
        this.awsAccessKeyId = awsAccessKeyId;
        this.awsSecretAccessKey = awsSecretAccessKey;
        this.awsRegion = awsRegion;
        this.enabled = enabled;
        this.sentEmailDao = sentEmailDao;
    }
    
    @PostConstruct
    public void init() {
        if (enabled && !awsAccessKeyId.isEmpty() && !awsSecretAccessKey.isEmpty()) {
            try {
                AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
                    awsAccessKeyId,
                    awsSecretAccessKey
                );
                
                this.sesClient = SesClient.builder()
                    .region(Region.of(awsRegion))
                    .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                    .build();
                
                logger.info("SES Email Service initialized successfully");
            } catch (Exception e) {
                logger.error("Failed to initialize SES client", e);
                this.sesClient = null;
            }
        } else {
            logger.warn("Email service is disabled or AWS credentials are not configured. " +
                       "Emails will be logged to console only.");
            this.sesClient = null;
        }
    }
    
    @PreDestroy
    public void cleanup() {
        if (sesClient != null) {
            sesClient.close();
        }
    }
    
    @Override
    public void sendVerificationCode(String to, String code, int expiresInMinutes) {
        String subject = "Your Verification Code";
        String body = buildVerificationEmailBody(code, expiresInMinutes);
        
        if (sesClient == null || !enabled) {
            // Fallback to console logging if SES is not configured
            logger.info("=== EMAIL (Not Sent - SES Disabled) ===");
            logger.info("To: {}", to);
            logger.info("Subject: {}", subject);
            logger.info("Verification Code: {}", code);
            logger.info("Code expires in: {} minutes", expiresInMinutes);
            logger.info("(Full HTML email body would be sent if SES was enabled)");
            logger.info("========================================");
            
            // Store email record even when disabled (for audit trail)
            SentEmail sentEmail = new SentEmail(to, subject, "verification_code", null, "disabled");
            sentEmail.setErrorMessage("SES is disabled or not configured");
            sentEmailDao.insertSentEmail(sentEmail);
            return;
        }
        
        try {
            SendEmailRequest emailRequest = SendEmailRequest.builder()
                .source(fromEmail)
                .destination(Destination.builder()
                    .toAddresses(to)
                    .build())
                .message(Message.builder()
                    .subject(Content.builder()
                        .data(subject)
                        .charset("UTF-8")
                        .build())
                    .body(Body.builder()
                        .html(Content.builder()
                            .data(body)
                            .charset("UTF-8")
                            .build())
                        .build())
                    .build())
                .build();
            
            SendEmailResponse response = sesClient.sendEmail(emailRequest);
            String messageId = response.messageId();
            
            logger.info("Verification code email sent successfully to {}. MessageId: {}", 
                       to, messageId);
            
            // Store successful email record
            SentEmail sentEmail = new SentEmail(to, subject, "verification_code", messageId, "sent");
            sentEmailDao.insertSentEmail(sentEmail);
            
        } catch (Exception e) {
            logger.error("Failed to send verification code email to {}", to, e);
            
            // Store failed email record
            SentEmail sentEmail = new SentEmail(to, subject, "verification_code", null, "failed");
            sentEmail.setErrorMessage(e.getMessage());
            sentEmailDao.insertSentEmail(sentEmail);
        }
    }
    
    private String buildVerificationEmailBody(String code, int expiresInMinutes) {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Verification Code</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background-color: #f4f4f4; padding: 20px; border-radius: 5px;">
                    <h1 style="color: #2c3e50; margin-top: 0;">Verification Code</h1>
                    <p>Hello,</p>
                    <p>You requested a verification code to access your account. Please use the code below:</p>
                    <div style="background-color: #ffffff; border: 2px dashed #3498db; padding: 20px; text-align: center; margin: 20px 0; border-radius: 5px;">
                        <h2 style="color: #3498db; margin: 0; font-size: 32px; letter-spacing: 5px;">%s</h2>
                    </div>
                    <p><strong>This code will expire in %d minutes.</strong></p>
                    <p>If you didn't request this code, please ignore this email.</p>
                    <hr style="border: none; border-top: 1px solid #eee; margin: 20px 0;">
                    <p style="color: #7f8c8d; font-size: 12px;">This is an automated message. Please do not reply to this email.</p>
                </div>
            </body>
            </html>
            """.formatted(code, expiresInMinutes);
    }
    
    @Override
    public void sendPostNotification(String to, String postTitle, String postSlug, String postExcerpt) {
        String subject = "New Post: " + postTitle;
        String body = buildPostNotificationEmailBody(postTitle, postSlug, postExcerpt);
        
        // Get base URL from environment or use default
        String baseUrl = System.getenv("APP_BASE_URL");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "http://localhost:5173"; // Default for development
        }
        String postUrl = baseUrl + "/post/" + postSlug;
        
        if (sesClient == null || !enabled) {
            // Fallback to console logging if SES is not configured
            logger.info("=== POST NOTIFICATION EMAIL (Not Sent - SES Disabled) ===");
            logger.info("To: {}", to);
            logger.info("Subject: {}", subject);
            logger.info("Post: {} - {}", postTitle, postUrl);
            logger.info("=========================================================");
            
            // Store email record even when disabled
            SentEmail sentEmail = new SentEmail(to, subject, "post_notification", null, "disabled");
            sentEmail.setErrorMessage("SES is disabled or not configured");
            sentEmailDao.insertSentEmail(sentEmail);
            return;
        }
        
        try {
            SendEmailRequest emailRequest = SendEmailRequest.builder()
                .source(fromEmail)
                .destination(Destination.builder()
                    .toAddresses(to)
                    .build())
                .message(Message.builder()
                    .subject(Content.builder()
                        .data(subject)
                        .charset("UTF-8")
                        .build())
                    .body(Body.builder()
                        .html(Content.builder()
                            .data(body)
                            .charset("UTF-8")
                            .build())
                        .build())
                    .build())
                .build();
            
            SendEmailResponse response = sesClient.sendEmail(emailRequest);
            String messageId = response.messageId();
            
            logger.info("Post notification email sent successfully to {}. MessageId: {}", 
                       to, messageId);
            
            // Store successful email record
            SentEmail sentEmail = new SentEmail(to, subject, "post_notification", messageId, "sent");
            sentEmailDao.insertSentEmail(sentEmail);
            
        } catch (Exception e) {
            logger.error("Failed to send post notification email to {}", to, e);
            
            // Store failed email record
            SentEmail sentEmail = new SentEmail(to, subject, "post_notification", null, "failed");
            sentEmail.setErrorMessage(e.getMessage());
            sentEmailDao.insertSentEmail(sentEmail);
        }
    }
    
    private String buildPostNotificationEmailBody(String postTitle, String postSlug, String postExcerpt) {
        String baseUrl = System.getenv("APP_BASE_URL");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "http://localhost:5173";
        }
        String postUrl = baseUrl + "/post/" + postSlug;
        
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>New Post: %s</title>
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; max-width: 600px; margin: 0 auto; padding: 20px;">
                <div style="background-color: #f4f4f4; padding: 20px; border-radius: 5px;">
                    <h1 style="color: #2c3e50; margin-top: 0;">New Post Published!</h1>
                    <h2 style="color: #3498db;">%s</h2>
                    %s
                    <div style="margin: 30px 0; text-align: center;">
                        <a href="%s" style="background-color: #3498db; color: white; padding: 12px 24px; text-decoration: none; border-radius: 5px; display: inline-block;">
                            Read Full Post
                        </a>
                    </div>
                    <hr style="border: none; border-top: 1px solid #eee; margin: 20px 0;">
                    <p style="color: #7f8c8d; font-size: 12px;">
                        You're receiving this email because you subscribed to our newsletter.
                        <a href="%s/newsletter?unsubscribe=true" style="color: #7f8c8d;">Unsubscribe</a>
                    </p>
                </div>
            </body>
            </html>
            """.formatted(
                postTitle,
                postTitle,
                postExcerpt != null && !postExcerpt.isEmpty() 
                    ? "<p style=\"font-size: 16px; color: #555;\">" + postExcerpt + "</p>" 
                    : "",
                postUrl,
                baseUrl
            );
    }
}
