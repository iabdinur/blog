package com.iabdinur.config;

import com.iabdinur.s3.S3Buckets;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${aws.region:us-east-2}")
    private String awsRegion;

    @Bean
    public S3Client s3Client() {
        // If explicit credentials are provided, use them (for local dev)
        // Otherwise, use default credential chain (IAM roles for production)
        var builder = S3Client.builder()
                .region(Region.of(awsRegion));

        if (!awsAccessKeyId.isEmpty() && !awsSecretAccessKey.isEmpty()) {
            // Use explicit credentials if provided
            AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
                awsAccessKeyId,
                awsSecretAccessKey
            );
            builder.credentialsProvider(StaticCredentialsProvider.create(awsCredentials));
        }
        // If credentials are empty, SDK will automatically use default credential chain
        // which includes: environment variables, IAM roles, etc.

        return builder.build();
    }

    @Bean
    public S3Buckets s3Buckets() {
        return new S3Buckets();
    }
}
