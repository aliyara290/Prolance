package com.dxc.attachmentservice.infrastructure.adapter.out.storage;

import com.dxc.attachmentservice.application.port.out.StoragePort;
import com.dxc.attachmentservice.domain.exception.StorageException;
import com.dxc.attachmentservice.infrastructure.config.AwsS3Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.io.InputStream;
import java.time.Duration;

@Component
@RequiredArgsConstructor
@Slf4j
public class S3StorageAdapter implements StoragePort {

    private final S3Client s3Client;
    private final S3Presigner s3Presigner;
    private final AwsS3Properties s3Properties;

    @Override
    public void upload(String objectKey, InputStream inputStream, long size, String contentType) {
        log.info("Uploading to S3: bucket={}, key={}, size={}", s3Properties.getBucketName(), objectKey, size);

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(objectKey)
                    .contentType(contentType)
                    .contentLength(size)
                    .build();

            s3Client.putObject(putRequest, RequestBody.fromInputStream(inputStream, size));

            log.info("Successfully uploaded to S3: key={}", objectKey);
        } catch (S3Exception e) {
            log.error("S3 upload failed: key={}, error={}", objectKey, e.getMessage(), e);
            throw new StorageException("Failed to upload file to S3: " + objectKey, e);
        }
    }

    @Override
    public void delete(String objectKey) {
        log.info("Deleting from S3: bucket={}, key={}", s3Properties.getBucketName(), objectKey);

        try {
            DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(objectKey)
                    .build();

            s3Client.deleteObject(deleteRequest);

            log.info("Successfully deleted from S3: key={}", objectKey);
        } catch (S3Exception e) {
            log.error("S3 deletion failed: key={}, error={}", objectKey, e.getMessage(), e);
            throw new StorageException("Failed to delete file from S3: " + objectKey, e);
        }
    }

    @Override
    public String generatePresignedUploadUrl(String objectKey, String contentType, Duration expiration) {
        log.info("Generating presigned upload URL: key={}, expiration={}", objectKey, expiration);

        try {
            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(s3Properties.getBucketName())
                    .key(objectKey)
                    .contentType(contentType)
                    .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                    .signatureDuration(expiration)
                    .putObjectRequest(putRequest)
                    .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (S3Exception e) {
            log.error("Failed to generate presigned upload URL: key={}, error={}", objectKey, e.getMessage(), e);
            throw new StorageException("Failed to generate presigned upload URL", e);
        }
    }

    @Override
    public String generatePresignedDownloadUrl(String objectKey, Duration expiration) {
        log.info("Generating presigned download URL: key={}, expiration={}", objectKey, expiration);

        try {
            GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                    .signatureDuration(expiration)
                    .getObjectRequest(req -> req
                            .bucket(s3Properties.getBucketName())
                            .key(objectKey))
                    .build();

            PresignedGetObjectRequest presignedRequest = s3Presigner.presignGetObject(presignRequest);
            return presignedRequest.url().toString();
        } catch (S3Exception e) {
            log.error("Failed to generate presigned download URL: key={}, error={}", objectKey, e.getMessage(), e);
            throw new StorageException("Failed to generate presigned download URL", e);
        }
    }
}
