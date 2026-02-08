package com.iabdinur.service;

import com.iabdinur.s3.S3Buckets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3ServiceTest {

    private S3Service underTest;

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Buckets s3Buckets;

    @BeforeEach
    void setUp() {
        underTest = new S3Service(s3Client, s3Buckets);
    }

    @Test
    void shouldPutObjectSuccessfully() {
        // Given
        String key = "test-key.jpg";
        byte[] file = new byte[]{1, 2, 3};
        String contentType = "image/jpeg";
        
        when(s3Buckets.getUsers()).thenReturn("test-bucket");

        // When
        underTest.putObject(key, file, contentType);

        // Then
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void shouldThrowExceptionWhenPutObjectAndS3NotConfigured() {
        // Given
        when(s3Buckets.getUsers()).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> underTest.putObject("key", new byte[]{}, "type"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("S3 is not configured");
    }

    @Test
    void shouldThrowExceptionWhenPutObjectAndBucketIsEmpty() {
        // Given
        when(s3Buckets.getUsers()).thenReturn("");

        // When & Then
        assertThatThrownBy(() -> underTest.putObject("key", new byte[]{1, 2, 3}, "type"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("S3 is not configured");
    }

    @Test
    void shouldGetObjectSuccessfully() {
        // Given
        String key = "test-key.jpg";
        byte[] expectedData = new byte[]{1, 2, 3};
        
        when(s3Buckets.getUsers()).thenReturn("test-bucket");
        
        @SuppressWarnings("unchecked")
        ResponseBytes<GetObjectResponse> responseBytes = mock(ResponseBytes.class);
        when(responseBytes.asByteArray()).thenReturn(expectedData);
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(responseBytes);

        // When
        byte[] result = underTest.getObject(key);

        // Then
        assertThat(result).isEqualTo(expectedData);
        verify(s3Client).getObjectAsBytes(any(GetObjectRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenGetObjectNotFound() {
        // Given
        String key = "non-existent.jpg";
        
        when(s3Buckets.getUsers()).thenReturn("test-bucket");
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
            .thenThrow(S3Exception.builder().message("Not found").build());

        // When & Then
        assertThatThrownBy(() -> underTest.getObject(key))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to retrieve object from S3");
    }

    @Test
    void shouldThrowExceptionWhenGetObjectAndS3NotConfigured() {
        // Given
        when(s3Buckets.getUsers()).thenReturn("");

        // When & Then
        assertThatThrownBy(() -> underTest.getObject("key"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("S3 is not configured");
    }

    @Test
    void shouldDeleteObjectSuccessfully() {
        // Given
        String key = "test-key.jpg";
        
        when(s3Buckets.getUsers()).thenReturn("test-bucket");

        // When
        underTest.deleteObject(key);

        // Then
        verify(s3Client).deleteObject(any(Consumer.class));
    }

    @Test
    void shouldThrowExceptionWhenDeleteObjectAndS3NotConfigured() {
        // Given
        when(s3Buckets.getUsers()).thenReturn(null);

        // When & Then
        assertThatThrownBy(() -> underTest.deleteObject("key"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("S3 is not configured");
    }

    @Test
    void shouldGenerateKeyWithPrefixAndFilename() {
        // Given
        String prefix = "profile-images/";
        String filename = "user123.jpg";

        // When
        String key = underTest.generateKey(prefix, filename);

        // Then
        assertThat(key).startsWith(prefix);
        assertThat(key).endsWith("_" + filename);
        assertThat(key).contains("-"); // UUID contains hyphens
    }

    @Test
    void shouldGenerateUniqueKeys() {
        // Given
        String prefix = "images/";
        String filename = "photo.jpg";

        // When
        String key1 = underTest.generateKey(prefix, filename);
        String key2 = underTest.generateKey(prefix, filename);

        // Then
        assertThat(key1).isNotEqualTo(key2);
    }

    @Test
    void shouldBeEnabledWhenAllParametersValid() {
        // Given
        when(s3Buckets.getUsers()).thenReturn("test-bucket");

        // When
        underTest.putObject("key", new byte[]{1}, "type");

        // Then
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void shouldPutObjectWithDifferentContentTypes() {
        // Given
        when(s3Buckets.getUsers()).thenReturn("test-bucket");
        
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);

        // When
        underTest.putObject("image.jpg", new byte[]{1, 2, 3}, "image/jpeg");
        underTest.putObject("doc.pdf", new byte[]{4, 5, 6}, "application/pdf");
        underTest.putObject("video.mp4", new byte[]{7, 8, 9}, "video/mp4");

        // Then
        verify(s3Client, times(3)).putObject(requestCaptor.capture(), any(RequestBody.class));
        
        assertThat(requestCaptor.getAllValues().get(0).contentType()).isEqualTo("image/jpeg");
        assertThat(requestCaptor.getAllValues().get(1).contentType()).isEqualTo("application/pdf");
        assertThat(requestCaptor.getAllValues().get(2).contentType()).isEqualTo("video/mp4");
    }

    @Test
    void shouldPutObjectWithEmptyByteArray() {
        // Given
        when(s3Buckets.getUsers()).thenReturn("test-bucket");

        // When
        underTest.putObject("empty.txt", new byte[]{}, "text/plain");

        // Then
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void shouldPutObjectWithLargeByteArray() {
        // Given
        when(s3Buckets.getUsers()).thenReturn("test-bucket");
        byte[] largeFile = new byte[1024 * 1024]; // 1MB

        // When
        underTest.putObject("large.bin", largeFile, "application/octet-stream");

        // Then
        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    void shouldGenerateKeyWithEmptyPrefix() {
        // Given
        String prefix = "";
        String filename = "test.jpg";

        // When
        String key = underTest.generateKey(prefix, filename);

        // Then
        assertThat(key).endsWith("_" + filename);
        assertThat(key).contains("-"); // UUID
    }

    @Test
    void shouldGenerateKeyWithSpecialCharactersInFilename() {
        // Given
        String prefix = "uploads/";
        String filename = "my-file (1).jpg";

        // When
        String key = underTest.generateKey(prefix, filename);

        // Then
        assertThat(key).startsWith(prefix);
        assertThat(key).endsWith("_" + filename);
    }

    @Test
    void shouldThrowExceptionWhenGetObjectAndClientIsNull() {
        // Given
        when(s3Buckets.getUsers()).thenReturn("test-bucket");
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class)))
            .thenThrow(S3Exception.builder().message("Client error").build());

        // When & Then
        assertThatThrownBy(() -> underTest.getObject("key"))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("Failed to retrieve object from S3");
    }

    @Test
    void shouldHandleS3ExceptionDuringDelete() {
        // Given
        when(s3Buckets.getUsers()).thenReturn("test-bucket");
        doThrow(S3Exception.builder().message("Delete failed").build())
            .when(s3Client).deleteObject(any(Consumer.class));

        // When & Then
        assertThatThrownBy(() -> underTest.deleteObject("key"))
            .isInstanceOf(S3Exception.class)
            .hasMessageContaining("Delete failed");
    }

    @Test
    void shouldVerifyCorrectBucketUsedInPutObject() {
        // Given
        String expectedBucket = "my-test-bucket";
        when(s3Buckets.getUsers()).thenReturn(expectedBucket);
        
        ArgumentCaptor<PutObjectRequest> requestCaptor = ArgumentCaptor.forClass(PutObjectRequest.class);

        // When
        underTest.putObject("test.jpg", new byte[]{1, 2, 3}, "image/jpeg");

        // Then
        verify(s3Client).putObject(requestCaptor.capture(), any(RequestBody.class));
        assertThat(requestCaptor.getValue().bucket()).isEqualTo(expectedBucket);
    }

    @Test
    void shouldVerifyCorrectBucketUsedInGetObject() {
        // Given
        String expectedBucket = "my-test-bucket";
        when(s3Buckets.getUsers()).thenReturn(expectedBucket);
        
        @SuppressWarnings("unchecked")
        ResponseBytes<GetObjectResponse> responseBytes = mock(ResponseBytes.class);
        when(responseBytes.asByteArray()).thenReturn(new byte[]{1, 2, 3});
        when(s3Client.getObjectAsBytes(any(GetObjectRequest.class))).thenReturn(responseBytes);
        
        ArgumentCaptor<GetObjectRequest> requestCaptor = ArgumentCaptor.forClass(GetObjectRequest.class);

        // When
        underTest.getObject("test.jpg");

        // Then
        verify(s3Client).getObjectAsBytes(requestCaptor.capture());
        assertThat(requestCaptor.getValue().bucket()).isEqualTo(expectedBucket);
    }
}
