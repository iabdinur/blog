package com.iabdinur;

import com.iabdinur.service.S3Service;
import com.iabdinur.s3.S3Buckets;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import software.amazon.awssdk.services.s3.S3Client;

@TestConfiguration
public class TestConfig {
    // PasswordEncoder is already provided by SecurityConfig, no need to override

    @Bean
    @Primary
    public S3Client s3Client() {
        // Provide a mock S3Client for tests when AWS credentials are not configured
        return Mockito.mock(S3Client.class);
    }

    @Bean
    @Primary
    public S3Service s3Service(S3Client s3Client, S3Buckets s3Buckets) {
        // Provide S3Service for tests
        return new S3Service(s3Client, s3Buckets);
    }
}
