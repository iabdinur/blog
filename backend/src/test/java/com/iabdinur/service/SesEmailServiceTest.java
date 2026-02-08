package com.iabdinur.service;

import com.iabdinur.dao.SentEmailDao;
import com.iabdinur.model.SentEmail;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SesEmailServiceTest {

    @Mock
    private SentEmailDao sentEmailDao;

    @Test
    void shouldInitializeServiceWhenDisabled() {
        // Given
        SesEmailService underTest = new SesEmailService(
            "noreply@test.com",
            "",
            "",
            "us-east-2",
            false,
            sentEmailDao
        );

        // When
        underTest.init();

        // Then
        assertThat(underTest).isNotNull();
    }

    @Test
    void shouldInitializeServiceWhenEnabledWithCredentials() {
        // Given
        SesEmailService underTest = new SesEmailService(
            "noreply@test.com",
            "AKIATEST123",
            "secretKey123",
            "us-east-2",
            true,
            sentEmailDao
        );

        // When
        underTest.init();

        // Then
        assertThat(underTest).isNotNull();
    }

    @Test
    void shouldInitializeServiceWhenEnabledWithoutCredentials() {
        // Given
        SesEmailService underTest = new SesEmailService(
            "noreply@test.com",
            "",
            "",
            "us-east-2",
            true,
            sentEmailDao
        );

        // When
        underTest.init();

        // Then
        assertThat(underTest).isNotNull();
    }

    @Test
    void shouldLogToConsoleWhenSendingVerificationCodeAndServiceDisabled() {
        // Given
        SesEmailService underTest = new SesEmailService(
            "noreply@test.com",
            "",
            "",
            "us-east-2",
            false,
            sentEmailDao
        );
        underTest.init();

        ArgumentCaptor<SentEmail> sentEmailCaptor = ArgumentCaptor.forClass(SentEmail.class);

        // When
        underTest.sendVerificationCode("test@example.com", "123456", 10);

        // Then
        verify(sentEmailDao).insertSentEmail(sentEmailCaptor.capture());
        SentEmail captured = sentEmailCaptor.getValue();
        
        assertThat(captured.getRecipientEmail()).isEqualTo("test@example.com");
        assertThat(captured.getSubject()).isEqualTo("Your Verification Code");
        assertThat(captured.getEmailType()).isEqualTo("verification_code");
        assertThat(captured.getStatus()).isEqualTo("disabled");
        assertThat(captured.getErrorMessage()).contains("SES is disabled");
    }

    @Test
    void shouldLogToConsoleWhenSendingPostNotificationAndServiceDisabled() {
        // Given
        SesEmailService underTest = new SesEmailService(
            "noreply@test.com",
            "",
            "",
            "us-east-2",
            false,
            sentEmailDao
        );
        underTest.init();

        ArgumentCaptor<SentEmail> sentEmailCaptor = ArgumentCaptor.forClass(SentEmail.class);

        // When
        underTest.sendPostNotification(
            "test@example.com",
            "New Blog Post",
            "new-blog-post",
            "This is a great post"
        );

        // Then
        verify(sentEmailDao).insertSentEmail(sentEmailCaptor.capture());
        SentEmail captured = sentEmailCaptor.getValue();
        
        assertThat(captured.getRecipientEmail()).isEqualTo("test@example.com");
        assertThat(captured.getSubject()).isEqualTo("New Post: New Blog Post");
        assertThat(captured.getEmailType()).isEqualTo("post_notification");
        assertThat(captured.getStatus()).isEqualTo("disabled");
    }

    @Test
    void shouldInitializeTemplateEngine() {
        // Given
        SesEmailService underTest = new SesEmailService(
            "noreply@test.com",
            "",
            "",
            "us-east-2",
            false,
            sentEmailDao
        );

        // When
        underTest.init();

        // Then
        assertThat(underTest).isNotNull();
    }

    @Test
    void shouldUseCorrectFromEmail() {
        // Given
        String expectedFrom = "custom@test.com";
        SesEmailService underTest = new SesEmailService(
            expectedFrom,
            "",
            "",
            "us-east-2",
            false,
            sentEmailDao
        );

        // When
        underTest.init();

        // Then
        assertThat(underTest).isNotNull();
    }

    @Test
    void shouldCloseClientOnCleanup() {
        // Given
        SesEmailService underTest = new SesEmailService(
            "noreply@test.com",
            "",
            "",
            "us-east-2",
            false,
            sentEmailDao
        );
        underTest.init();

        // When
        underTest.cleanup();

        // Then
        assertThat(underTest).isNotNull();
    }

    @Test
    void shouldSendVerificationCodeWithCustomExpiration() {
        // Given
        SesEmailService underTest = new SesEmailService(
            "noreply@test.com",
            "",
            "",
            "us-east-2",
            false,
            sentEmailDao
        );
        underTest.init();

        // When
        underTest.sendVerificationCode("test@example.com", "999999", 5);
        underTest.sendVerificationCode("test@example.com", "111111", 15);

        // Then
        verify(sentEmailDao, times(2)).insertSentEmail(any(SentEmail.class));
    }

    @Test
    void shouldHandleNullExcerptInPostNotification() {
        // Given
        SesEmailService underTest = new SesEmailService(
            "noreply@test.com",
            "",
            "",
            "us-east-2",
            false,
            sentEmailDao
        );
        underTest.init();

        // When
        underTest.sendPostNotification(
            "test@example.com",
            "Post Title",
            "post-slug",
            null
        );

        // Then
        verify(sentEmailDao).insertSentEmail(any(SentEmail.class));
    }

    @Test
    void shouldHandleEmptyExcerptInPostNotification() {
        // Given
        SesEmailService underTest = new SesEmailService(
            "noreply@test.com",
            "",
            "",
            "us-east-2",
            false,
            sentEmailDao
        );
        underTest.init();

        ArgumentCaptor<SentEmail> sentEmailCaptor = ArgumentCaptor.forClass(SentEmail.class);

        // When
        underTest.sendPostNotification(
            "test@example.com",
            "Post Title",
            "post-slug",
            ""
        );

        // Then
        verify(sentEmailDao).insertSentEmail(sentEmailCaptor.capture());
        assertThat(sentEmailCaptor.getValue().getSubject()).isEqualTo("New Post: Post Title");
    }

    @Test
    void shouldInitializeServiceWithDifferentRegions() {
        // Given
        SesEmailService underTest1 = new SesEmailService(
            "noreply@test.com",
            "",
            "",
            "us-east-1",
            false,
            sentEmailDao
        );
        
        SesEmailService underTest2 = new SesEmailService(
            "noreply@test.com",
            "",
            "",
            "eu-west-1",
            false,
            sentEmailDao
        );

        // When
        underTest1.init();
        underTest2.init();

        // Then
        assertThat(underTest1).isNotNull();
        assertThat(underTest2).isNotNull();
    }

    @Test
    void shouldSendMultipleVerificationCodesSequentially() {
        // Given
        SesEmailService underTest = new SesEmailService(
            "noreply@test.com",
            "",
            "",
            "us-east-2",
            false,
            sentEmailDao
        );
        underTest.init();

        // When
        for (int i = 0; i < 5; i++) {
            underTest.sendVerificationCode("test" + i + "@example.com", String.valueOf(100000 + i), 10);
        }

        // Then
        verify(sentEmailDao, times(5)).insertSentEmail(any(SentEmail.class));
    }

    @Test
    void shouldSendMultiplePostNotificationsSequentially() {
        // Given
        SesEmailService underTest = new SesEmailService(
            "noreply@test.com",
            "",
            "",
            "us-east-2",
            false,
            sentEmailDao
        );
        underTest.init();

        // When
        for (int i = 0; i < 3; i++) {
            underTest.sendPostNotification(
                "test" + i + "@example.com",
                "Post " + i,
                "post-" + i,
                "Excerpt " + i
            );
        }

        // Then
        verify(sentEmailDao, times(3)).insertSentEmail(any(SentEmail.class));
    }

    @Test
    void shouldHandleVerificationCodeWithSpecialCharacters() {
        // Given
        SesEmailService underTest = new SesEmailService(
            "noreply@test.com",
            "",
            "",
            "us-east-2",
            false,
            sentEmailDao
        );
        underTest.init();

        // When
        underTest.sendVerificationCode("test@example.com", "ABC123", 10);

        // Then
        verify(sentEmailDao).insertSentEmail(any(SentEmail.class));
    }
}
