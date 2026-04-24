package com.dxc.tenantservice.infrastructure.adapter.out.keycloak.config;

import feign.Contract;
import feign.codec.Encoder;
import feign.form.FormEncoder;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.cloud.openfeign.support.SpringEncoder;
import org.springframework.context.annotation.Bean;

public class KeycloakTokenClientConfig {

    @Bean
    public Encoder feignFormEncoder(ObjectFactory<HttpMessageConverters> messageConverters) {
        return new FormEncoder(new SpringEncoder(messageConverters));
    }

    @Bean
    public Contract feignContract() {
        return new feign.Contract.Default();
    }
}