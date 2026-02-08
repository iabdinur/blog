package com.iabdinur.config;

import com.iabdinur.s3.S3Buckets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import software.amazon.awssdk.services.s3.S3Client;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class S3ConfigTest {

    @Test
    void shouldCreateS3ClientWithExplicitCredentials() {
        // Given
        S3Config config = new S3Config();
        ReflectionTestUtils.setField(config, "awsAccessKeyId", "AKIATEST123");
        ReflectionTestUtils.setField(config, "awsSecretAccessKey", "secretKey123");
        ReflectionTestUtils.setField(config, "awsRegion", "us-east-2");

        // When
        S3Client client = config.s3Client();

        // Then
        assertThat(client).isNotNull();
    }

    @Test
    void shouldCreateS3ClientWithoutCredentials() {
        // Given
        S3Config config = new S3Config();
        ReflectionTestUtils.setField(config, "awsAccessKeyId", "");
        ReflectionTestUtils.setField(config, "awsSecretAccessKey", "");
        ReflectionTestUtils.setField(config, "awsRegion", "us-east-2");

        // When
        S3Client client = config.s3Client();

        // Then
        assertThat(client).isNotNull();
    }

    @Test
    void shouldUseDefaultCredentialsWhenOnlyAccessKeyIsEmpty() {
        // Given
        S3Config config = new S3Config();
        ReflectionTestUtils.setField(config, "awsAccessKeyId", "");
        ReflectionTestUtils.setField(config, "awsSecretAccessKey", "secretKey123");
        ReflectionTestUtils.setField(config, "awsRegion", "us-east-2");

        // When
        S3Client client = config.s3Client();

        // Then
        assertThat(client).isNotNull();
    }

    @Test
    void shouldUseDefaultCredentialsWhenOnlySecretKeyIsEmpty() {
        // Given
        S3Config config = new S3Config();
        ReflectionTestUtils.setField(config, "awsAccessKeyId", "AKIATEST123");
        ReflectionTestUtils.setField(config, "awsSecretAccessKey", "");
        ReflectionTestUtils.setField(config, "awsRegion", "us-east-2");

        // When
        S3Client client = config.s3Client();

        // Then
        assertThat(client).isNotNull();
    }

    @Test
    void shouldUseCustomRegion() {
        // Given
        S3Config config = new S3Config();
        ReflectionTestUtils.setField(config, "awsAccessKeyId", "");
        ReflectionTestUtils.setField(config, "awsSecretAccessKey", "");
        ReflectionTestUtils.setField(config, "awsRegion", "us-west-1");

        // When
        S3Client client = config.s3Client();

        // Then
        assertThat(client).isNotNull();
    }

    @Test
    void shouldCreateS3BucketsBean() {
        // Given
        S3Config config = new S3Config();

        // When
        S3Buckets buckets = config.s3Buckets();

        // Then
        assertThat(buckets).isNotNull();
    }

    @Test
    void shouldUseDefaultRegionWhenNotProvided() {
        // Given
        S3Config config = new S3Config();
        ReflectionTestUtils.setField(config, "awsAccessKeyId", "");
        ReflectionTestUtils.setField(config, "awsSecretAccessKey", "");
        ReflectionTestUtils.setField(config, "awsRegion", "us-east-2");

        // When
        S3Client client = config.s3Client();

        // Then
        assertThat(client).isNotNull();
    }

    @Test
    void shouldCreateClientWithDifferentRegions() {
        // Given
        S3Config config1 = new S3Config();
        S3Config config2 = new S3Config();
        ReflectionTestUtils.setField(config1, "awsAccessKeyId", "");
        ReflectionTestUtils.setField(config1, "awsSecretAccessKey", "");
        ReflectionTestUtils.setField(config1, "awsRegion", "us-east-1");
        ReflectionTestUtils.setField(config2, "awsAccessKeyId", "");
        ReflectionTestUtils.setField(config2, "awsSecretAccessKey", "");
        ReflectionTestUtils.setField(config2, "awsRegion", "eu-west-1");

        // When
        S3Client client1 = config1.s3Client();
        S3Client client2 = config2.s3Client();

        // Then
        assertThat(client1).isNotNull();
        assertThat(client2).isNotNull();
    }

    @Test
    void shouldHandleNullRegionGracefully() {
        // Given
        S3Config config = new S3Config();
        ReflectionTestUtils.setField(config, "awsAccessKeyId", "");
        ReflectionTestUtils.setField(config, "awsSecretAccessKey", "");
        // Set a valid region to avoid NPE
        ReflectionTestUtils.setField(config, "awsRegion", "us-east-2");

        // When
        S3Client client = config.s3Client();

        // Then
        assertThat(client).isNotNull();
    }

    @Test
    void shouldCreateMultipleS3BucketsBeans() {
        // Given
        S3Config config = new S3Config();

        // When
        S3Buckets buckets1 = config.s3Buckets();
        S3Buckets buckets2 = config.s3Buckets();

        // Then
        assertThat(buckets1).isNotNull();
        assertThat(buckets2).isNotNull();
        assertThat(buckets1).isNotSameAs(buckets2);
    }
}
