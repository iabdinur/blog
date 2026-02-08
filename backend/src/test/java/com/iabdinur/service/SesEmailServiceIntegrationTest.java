package com.iabdinur.service;

import com.iabdinur.dao.SentEmailDao;
import com.iabdinur.model.SentEmail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.ses.SesClient;
import software.amazon.awssdk.services.ses.model.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SesEmailServiceIntegrationTest {

    private SesEmailService underTest;
    
    @Mock
    private SesClient sesClient;
    
    @Mock
    private SentEmailDao sentEmailDao;

    @BeforeEach
    void setUp() {
        // Create service with email ENABLED but inject mocked SesClient
        underTest = new SesEmailService(
            "noreply@iabdinur.com",
            "AKIATEST123",
            "secretKey123",
            "us-east-2",
            true, // ENABLED
            sentEmailDao
        );
        underTest.init();
        
        // Inject mocked SesClient using reflection
        ReflectionTestUtils.setField(underTest, "sesClient", sesClient);
    }

    @Test
    void shouldSendVerificationCodeSuccessfully() {
        // Given
        String to = "test@example.com";
        String code = "123456";
        int expiresInMinutes = 10;
        
        SendEmailResponse mockResponse = SendEmailResponse.builder()
            .messageId("0100019test-message-id-123")
            .build();
        
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(mockResponse);
        
        ArgumentCaptor<SendEmailRequest> requestCaptor = ArgumentCaptor.forClass(SendEmailRequest.class);
        ArgumentCaptor<SentEmail> sentEmailCaptor = ArgumentCaptor.forClass(SentEmail.class);

        // When
        underTest.sendVerificationCode(to, code, expiresInMinutes);

        // Then
        verify(sesClient).sendEmail(requestCaptor.capture());
        SendEmailRequest capturedRequest = requestCaptor.getValue();
        
        assertThat(capturedRequest.source()).isEqualTo("noreply@iabdinur.com");
        assertThat(capturedRequest.destination().toAddresses()).contains(to);
        assertThat(capturedRequest.message().subject().data()).isEqualTo("Your Verification Code");
        assertThat(capturedRequest.message().body().html().data()).contains(code);
        assertThat(capturedRequest.message().body().html().charset()).isEqualTo("UTF-8");
        
        verify(sentEmailDao).insertSentEmail(sentEmailCaptor.capture());
        SentEmail sentEmail = sentEmailCaptor.getValue();
        
        assertThat(sentEmail.getRecipientEmail()).isEqualTo(to);
        assertThat(sentEmail.getStatus()).isEqualTo("sent");
        assertThat(sentEmail.getSesMessageId()).isEqualTo("0100019test-message-id-123");
        assertThat(sentEmail.getEmailType()).isEqualTo("verification_code");
    }

    @Test
    void shouldSendPostNotificationSuccessfully() {
        // Given
        String to = "subscriber@example.com";
        String postTitle = "New Blog Post";
        String postSlug = "new-blog-post";
        String postExcerpt = "This is an exciting post";
        
        SendEmailResponse mockResponse = SendEmailResponse.builder()
            .messageId("0100019post-notification-456")
            .build();
        
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(mockResponse);
        
        ArgumentCaptor<SendEmailRequest> requestCaptor = ArgumentCaptor.forClass(SendEmailRequest.class);
        ArgumentCaptor<SentEmail> sentEmailCaptor = ArgumentCaptor.forClass(SentEmail.class);

        // When
        underTest.sendPostNotification(to, postTitle, postSlug, postExcerpt);

        // Then
        verify(sesClient).sendEmail(requestCaptor.capture());
        SendEmailRequest capturedRequest = requestCaptor.getValue();
        
        assertThat(capturedRequest.source()).isEqualTo("noreply@iabdinur.com");
        assertThat(capturedRequest.destination().toAddresses()).contains(to);
        assertThat(capturedRequest.message().subject().data()).isEqualTo("New Post: " + postTitle);
        assertThat(capturedRequest.message().body().html().data()).contains(postTitle);
        assertThat(capturedRequest.message().body().html().data()).contains(postExcerpt);
        
        verify(sentEmailDao).insertSentEmail(sentEmailCaptor.capture());
        SentEmail sentEmail = sentEmailCaptor.getValue();
        
        assertThat(sentEmail.getRecipientEmail()).isEqualTo(to);
        assertThat(sentEmail.getStatus()).isEqualTo("sent");
        assertThat(sentEmail.getSesMessageId()).isEqualTo("0100019post-notification-456");
        assertThat(sentEmail.getEmailType()).isEqualTo("post_notification");
    }

    @Test
    void shouldHandleMessageRejectedException() {
        // Given
        String to = "unverified@example.com";
        String code = "123456";
        
        MessageRejectedException exception = (MessageRejectedException) MessageRejectedException.builder()
            .message("Email address is not verified")
            .build();
        
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenThrow(exception);
        
        ArgumentCaptor<SentEmail> sentEmailCaptor = ArgumentCaptor.forClass(SentEmail.class);

        // When
        underTest.sendVerificationCode(to, code, 10);

        // Then
        verify(sesClient).sendEmail(any(SendEmailRequest.class));
        verify(sentEmailDao).insertSentEmail(sentEmailCaptor.capture());
        
        SentEmail sentEmail = sentEmailCaptor.getValue();
        assertThat(sentEmail.getStatus()).isEqualTo("failed");
        assertThat(sentEmail.getErrorMessage()).contains("Email address is not verified");
        assertThat(sentEmail.getSesMessageId()).isNull();
    }

    @Test
    void shouldHandleThrottlingException() {
        // Given
        String to = "test@example.com";
        
        SesException throttlingException = (SesException) SesException.builder()
            .message("Throttling: Maximum sending rate exceeded")
            .build();
        
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenThrow(throttlingException);
        
        ArgumentCaptor<SentEmail> sentEmailCaptor = ArgumentCaptor.forClass(SentEmail.class);

        // When
        underTest.sendVerificationCode(to, "123456", 10);

        // Then
        verify(sentEmailDao).insertSentEmail(sentEmailCaptor.capture());
        SentEmail sentEmail = sentEmailCaptor.getValue();
        
        assertThat(sentEmail.getStatus()).isEqualTo("failed");
        assertThat(sentEmail.getErrorMessage()).contains("Throttling");
    }

    @Test
    void shouldHandleNetworkException() {
        // Given
        String to = "test@example.com";
        
        when(sesClient.sendEmail(any(SendEmailRequest.class)))
            .thenThrow(new RuntimeException("Network timeout"));
        
        ArgumentCaptor<SentEmail> sentEmailCaptor = ArgumentCaptor.forClass(SentEmail.class);

        // When
        underTest.sendVerificationCode(to, "123456", 10);

        // Then
        verify(sentEmailDao).insertSentEmail(sentEmailCaptor.capture());
        SentEmail sentEmail = sentEmailCaptor.getValue();
        
        assertThat(sentEmail.getStatus()).isEqualTo("failed");
        assertThat(sentEmail.getErrorMessage()).contains("Network timeout");
    }

    @Test
    void shouldBuildCorrectSendEmailRequest() {
        // Given
        String to = "test@example.com";
        String code = "ABCD12";
        
        SendEmailResponse mockResponse = SendEmailResponse.builder()
            .messageId("test-message-id")
            .build();
        
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(mockResponse);
        
        ArgumentCaptor<SendEmailRequest> requestCaptor = ArgumentCaptor.forClass(SendEmailRequest.class);

        // When
        underTest.sendVerificationCode(to, code, 10);

        // Then
        verify(sesClient).sendEmail(requestCaptor.capture());
        SendEmailRequest request = requestCaptor.getValue();
        
        // Verify request structure
        assertThat(request.source()).isNotNull();
        assertThat(request.destination()).isNotNull();
        assertThat(request.destination().toAddresses()).hasSize(1);
        assertThat(request.message()).isNotNull();
        assertThat(request.message().subject()).isNotNull();
        assertThat(request.message().subject().charset()).isEqualTo("UTF-8");
        assertThat(request.message().body()).isNotNull();
        assertThat(request.message().body().html()).isNotNull();
        assertThat(request.message().body().html().charset()).isEqualTo("UTF-8");
    }

    @Test
    void shouldReuseSesClientForMultipleEmails() {
        // Given
        SendEmailResponse mockResponse = SendEmailResponse.builder()
            .messageId("test-message-id")
            .build();
        
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(mockResponse);

        // When - Send 10 emails
        for (int i = 0; i < 10; i++) {
            underTest.sendVerificationCode("test" + i + "@example.com", "12345" + i, 10);
        }

        // Then
        verify(sesClient, times(10)).sendEmail(any(SendEmailRequest.class));
        verify(sentEmailDao, times(10)).insertSentEmail(any(SentEmail.class));
    }

    @Test
    void shouldHandleLongSubjectInPostNotification() {
        // Given
        String longTitle = "A".repeat(500);
        
        SendEmailResponse mockResponse = SendEmailResponse.builder()
            .messageId("test-message-id")
            .build();
        
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(mockResponse);
        
        ArgumentCaptor<SendEmailRequest> requestCaptor = ArgumentCaptor.forClass(SendEmailRequest.class);

        // When
        underTest.sendPostNotification("test@example.com", longTitle, "slug", "excerpt");

        // Then
        verify(sesClient).sendEmail(requestCaptor.capture());
        SendEmailRequest request = requestCaptor.getValue();
        
        // Subject should be "New Post: " + title (may be truncated by SES, but we build it)
        assertThat(request.message().subject().data()).startsWith("New Post: ");
    }

    @Test
    void shouldStoreMessageIdFromSesResponse() {
        // Given
        String expectedMessageId = "0100019abcdef-1234-5678-90ab-cdefghijklmn";
        
        SendEmailResponse mockResponse = SendEmailResponse.builder()
            .messageId(expectedMessageId)
            .build();
        
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(mockResponse);
        
        ArgumentCaptor<SentEmail> sentEmailCaptor = ArgumentCaptor.forClass(SentEmail.class);

        // When
        underTest.sendVerificationCode("test@example.com", "123456", 10);

        // Then
        verify(sentEmailDao).insertSentEmail(sentEmailCaptor.capture());
        SentEmail sentEmail = sentEmailCaptor.getValue();
        
        assertThat(sentEmail.getSesMessageId()).isEqualTo(expectedMessageId);
    }

    @Test
    void shouldSendMultipleDifferentEmailTypes() {
        // Given
        SendEmailResponse mockResponse = SendEmailResponse.builder()
            .messageId("test-message-id")
            .build();
        
        when(sesClient.sendEmail(any(SendEmailRequest.class))).thenReturn(mockResponse);
        
        ArgumentCaptor<SentEmail> sentEmailCaptor = ArgumentCaptor.forClass(SentEmail.class);

        // When
        underTest.sendVerificationCode("user1@example.com", "123456", 10);
        underTest.sendPostNotification("user2@example.com", "Post Title", "slug", "excerpt");
        underTest.sendVerificationCode("user3@example.com", "654321", 10);

        // Then
        verify(sesClient, times(3)).sendEmail(any(SendEmailRequest.class));
        verify(sentEmailDao, times(3)).insertSentEmail(sentEmailCaptor.capture());
        
        var sentEmails = sentEmailCaptor.getAllValues();
        assertThat(sentEmails.get(0).getEmailType()).isEqualTo("verification_code");
        assertThat(sentEmails.get(1).getEmailType()).isEqualTo("post_notification");
        assertThat(sentEmails.get(2).getEmailType()).isEqualTo("verification_code");
    }
}
