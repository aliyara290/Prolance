package com.dxc.attachmentservice.infrastructure.adapter.out.storage;

import com.dxc.attachmentservice.domain.exception.StorageException;
import com.dxc.attachmentservice.infrastructure.config.AwsS3Properties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3StorageAdapterTest {

    @Mock
    private S3Client s3Client;

    @Mock
    private S3Presigner s3Presigner;

    @Mock
    private AwsS3Properties s3Properties;

    private S3StorageAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new S3StorageAdapter(s3Client, s3Presigner, s3Properties);
        lenient().when(s3Properties.getBucketName()).thenReturn("test-bucket");
    }

    @Test
    @DisplayName("Should upload file to S3")
    void upload_shouldCallS3PutObject() {
        InputStream inputStream = new ByteArrayInputStream("content".getBytes());

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenReturn(PutObjectResponse.builder().build());

        adapter.upload("task/id/file.pdf", inputStream, 7, "application/pdf");

        verify(s3Client).putObject(any(PutObjectRequest.class), any(RequestBody.class));
    }

    @Test
    @DisplayName("Should throw StorageException on upload failure")
    void upload_shouldThrowOnS3Error() {
        InputStream inputStream = new ByteArrayInputStream("content".getBytes());

        when(s3Client.putObject(any(PutObjectRequest.class), any(RequestBody.class)))
                .thenThrow(S3Exception.builder().message("Access Denied").build());

        assertThatThrownBy(() -> adapter.upload("key", inputStream, 7, "application/pdf"))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("Failed to upload");
    }

    @Test
    @DisplayName("Should delete file from S3")
    void delete_shouldCallS3DeleteObject() {
        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenReturn(DeleteObjectResponse.builder().build());

        adapter.delete("task/id/file.pdf");

        verify(s3Client).deleteObject(any(DeleteObjectRequest.class));
    }

    @Test
    @DisplayName("Should throw StorageException on delete failure")
    void delete_shouldThrowOnS3Error() {
        when(s3Client.deleteObject(any(DeleteObjectRequest.class)))
                .thenThrow(S3Exception.builder().message("Not Found").build());

        assertThatThrownBy(() -> adapter.delete("key"))
                .isInstanceOf(StorageException.class)
                .hasMessageContaining("Failed to delete");
    }

    @Test
    @DisplayName("Should generate presigned upload URL")
    void generatePresignedUploadUrl_shouldReturnUrl() throws Exception {
        PresignedPutObjectRequest presignedRequest = mock(PresignedPutObjectRequest.class);
        when(presignedRequest.url()).thenReturn(URI.create("https://s3.amazonaws.com/upload").toURL());
        when(s3Presigner.presignPutObject(any(PutObjectPresignRequest.class))).thenReturn(presignedRequest);

        String result = adapter.generatePresignedUploadUrl("key", "application/pdf", Duration.ofMinutes(15));

        assertThat(result).isEqualTo("https://s3.amazonaws.com/upload");
    }

    @Test
    @DisplayName("Should generate presigned download URL")
    void generatePresignedDownloadUrl_shouldReturnUrl() throws Exception {
        PresignedGetObjectRequest presignedRequest = mock(PresignedGetObjectRequest.class);
        when(presignedRequest.url()).thenReturn(URI.create("https://s3.amazonaws.com/download").toURL());
        when(s3Presigner.presignGetObject(any(GetObjectPresignRequest.class))).thenReturn(presignedRequest);

        String result = adapter.generatePresignedDownloadUrl("key", Duration.ofMinutes(15));

        assertThat(result).isEqualTo("https://s3.amazonaws.com/download");
    }
}
