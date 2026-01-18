package com.iabdinur.service;

import com.iabdinur.s3.S3Buckets;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.UUID;

@Service
@ConditionalOnBean(S3Client.class)
public class S3Service {

    private final S3Client s3Client;
    private final S3Buckets s3Buckets;

    public S3Service(S3Client s3Client, S3Buckets s3Buckets) {
        this.s3Client = s3Client;
        this.s3Buckets = s3Buckets;
    }

    private boolean isS3Enabled() {
        return s3Client != null && s3Buckets != null && 
               s3Buckets.getUsers() != null && !s3Buckets.getUsers().isEmpty();
    }

    public void putObject(String key, byte[] file, String contentType) {
        if (!isS3Enabled()) {
            throw new IllegalStateException("S3 is not configured. Please provide AWS credentials.");
        }

        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(s3Buckets.getUsers())
            .key(key)
            .contentType(contentType)
            .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file));
    }

    public byte[] getObject(String key) {
        if (!isS3Enabled()) {
            throw new IllegalStateException("S3 is not configured. Please provide AWS credentials.");
        }

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(s3Buckets.getUsers())
            .key(key)
            .build();

        try {
            return s3Client.getObjectAsBytes(getObjectRequest).asByteArray();
        } catch (S3Exception e) {
            throw new RuntimeException("Failed to retrieve object from S3: " + e.getMessage(), e);
        }
    }

    public void deleteObject(String key) {
        if (!isS3Enabled()) {
            throw new IllegalStateException("S3 is not configured. Please provide AWS credentials.");
        }

        s3Client.deleteObject(b -> b.bucket(s3Buckets.getUsers()).key(key));
    }

    public String generateKey(String prefix, String filename) {
        return prefix + UUID.randomUUID() + "_" + filename;
    }
}
