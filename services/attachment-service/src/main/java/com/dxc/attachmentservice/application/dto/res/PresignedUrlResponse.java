package com.dxc.attachmentservice.application.dto.res;

import java.time.Instant;

public record PresignedUrlResponse(
    String url,
    String objectKey,
    Instant expiresAt
) {}
