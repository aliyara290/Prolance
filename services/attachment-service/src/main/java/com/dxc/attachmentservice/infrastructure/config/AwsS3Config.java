package com.dxc.attachmentservice.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3ClientBuilder;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.S3Presigner.Builder;

@Configuration
public class AwsS3Config {

    @Bean
    public S3Client s3Client(AwsS3Properties awsS3Properties) {
        S3ClientBuilder builder = S3Client.builder().crossRegionAccessEnabled(true);
        if (StringUtils.hasText(awsS3Properties.getRegion())) {
            builder.region(Region.of(awsS3Properties.getRegion()));
        }
        builder.credentialsProvider(getCredentialsProvider(awsS3Properties));
        return builder.build();
    }

    @Bean
    public S3Presigner s3Presigner(AwsS3Properties awsS3Properties) {
        Builder builder = S3Presigner.builder();
        if (StringUtils.hasText(awsS3Properties.getRegion())) {
            builder.region(Region.of(awsS3Properties.getRegion()));
        }
        builder.credentialsProvider(getCredentialsProvider(awsS3Properties));
        return builder.build();
    }

    private AwsCredentialsProvider getCredentialsProvider(AwsS3Properties properties) {
        if (StringUtils.hasText(properties.getAccessKey()) && StringUtils.hasText(properties.getSecretKey())) {
            return StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())
            );
        }
        return DefaultCredentialsProvider.create();
    }
}
