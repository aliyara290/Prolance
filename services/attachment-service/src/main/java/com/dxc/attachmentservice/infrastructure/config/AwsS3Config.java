package com.dxc.attachmentservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
public class AwsS3Config {

    @Bean
    public S3Client s3Client() {
        return S3Client.create();
    }

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.create();
    }
}
