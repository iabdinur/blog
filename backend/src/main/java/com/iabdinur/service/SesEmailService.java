package com.iabdinur.service;

import com.iabdinur.dao.SentEmailDao;
import com.iabdinur.model.SentEmail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import org.thymeleaf.templatemode.TemplateMode;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import java.nio.charset.StandardCharsets;

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
    private TemplateEngine templateEngine;
    
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
        // Initialize Thymeleaf template engine
        this.templateEngine = initializeTemplateEngine();
        
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
    
    private TemplateEngine initializeTemplateEngine() {
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("templates/email/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setCharacterEncoding(StandardCharsets.UTF_8.name());
        templateResolver.setCacheable(false); // Set to true in production for better performance
        
        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.setTemplateResolver(templateResolver);
        return engine;
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
        Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("expiresInMinutes", expiresInMinutes);
        
        return templateEngine.process("verification", context);
    }
    
    @Override
    public void sendPostNotification(String to, String postTitle, String postSlug, String postExcerpt) {
        String subject = "New Post: " + postTitle;
        
        // Get base URL from environment or use default
        String baseUrl = System.getenv("APP_BASE_URL");
        if (baseUrl == null || baseUrl.isEmpty()) {
            baseUrl = "http://localhost:5173"; // Default for development
        }
        String postUrl = baseUrl + "/post/" + postSlug;
        String unsubscribeUrl = baseUrl + "/newsletter?unsubscribe=true";
        
        String body = buildPostNotificationEmailBody(postTitle, postExcerpt, postUrl, unsubscribeUrl);
        
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
    
    private String buildPostNotificationEmailBody(String postTitle, String postExcerpt, String postUrl, String unsubscribeUrl) {
        Context context = new Context();
        context.setVariable("postTitle", postTitle);
        context.setVariable("postExcerpt", postExcerpt != null ? postExcerpt : "");
        context.setVariable("postUrl", postUrl);
        context.setVariable("unsubscribeUrl", unsubscribeUrl);
        
        return templateEngine.process("post-notification", context);
    }
}
