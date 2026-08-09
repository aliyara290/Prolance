package com.dxc.attachmentservice.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI attachmentServiceOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Prolance Attachment Service API")
                        .description("Centralized attachment management service for the Prolance SaaS platform. "
                                + "Handles file uploads, downloads, and pre-signed URL generation via Amazon S3.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("DXC Prolance Team")
                        )
                );
    }
}
