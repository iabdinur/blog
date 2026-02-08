package com.iabdinur;

import com.iabdinur.service.EmailService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test configuration for mocking external services
 */
@TestConfiguration
public class TestConfig {

    /**
     * Mock EmailService to avoid actual AWS SES calls during tests
     */
    @Bean
    @Primary
    public EmailService mockEmailService() {
        EmailService mock = mock(EmailService.class);
        
        // Mock sendVerificationCode to log instead of sending
        doAnswer(invocation -> {
            String to = invocation.getArgument(0);
            String code = invocation.getArgument(1);
            int expiresInMinutes = invocation.getArgument(2);
            System.out.println("MOCK EMAIL: Verification code " + code + 
                             " sent to " + to + 
                             " (expires in " + expiresInMinutes + " min)");
            return null;
        }).when(mock).sendVerificationCode(anyString(), anyString(), anyInt());
        
        // Mock sendPostNotification to log instead of sending
        doAnswer(invocation -> {
            String to = invocation.getArgument(0);
            String postTitle = invocation.getArgument(1);
            String postSlug = invocation.getArgument(2);
            String postExcerpt = invocation.getArgument(3);
            System.out.println("MOCK EMAIL: Post notification for '" + postTitle + 
                             "' sent to " + to);
            return null;
        }).when(mock).sendPostNotification(anyString(), anyString(), anyString(), anyString());
        
        return mock;
    }
}
