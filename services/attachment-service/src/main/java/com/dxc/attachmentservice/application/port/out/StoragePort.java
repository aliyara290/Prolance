package com.dxc.attachmentservice.application.port.out;

import java.io.InputStream;
import java.time.Duration;

public interface StoragePort {

    void upload(String objectKey, InputStream inputStream, long size, String contentType);

    void delete(String objectKey);

    String generatePresignedUploadUrl(String objectKey, String contentType, Duration expiration);

    String generatePresignedDownloadUrl(String objectKey, Duration expiration);
}
