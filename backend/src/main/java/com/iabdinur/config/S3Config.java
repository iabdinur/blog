package com.iabdinur.config;

import com.iabdinur.s3.S3Buckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${aws.access-key-id:}")
    private String awsAccessKeyId;

    @Value("${aws.secret-access-key:}")
    private String awsSecretAccessKey;

    @Value("${aws.region:us-east-1}")
    private String awsRegion;

    @Bean
    @ConditionalOnExpression("!'${aws.access-key-id:}'.isEmpty() && !'${aws.secret-access-key:}'.isEmpty()")
    public S3Client s3Client() {
        // Only create bean if credentials are provided (non-empty)
        // Tests will provide a mock S3Client via TestConfig
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
            awsAccessKeyId,
            awsSecretAccessKey
        );

        return S3Client.builder()
            .region(Region.of(awsRegion))
            .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
            .build();
    }

    @Bean
    public S3Buckets s3Buckets() {
        return new S3Buckets();
    }
}
