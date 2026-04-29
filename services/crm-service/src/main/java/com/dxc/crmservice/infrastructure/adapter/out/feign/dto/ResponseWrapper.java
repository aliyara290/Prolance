package com.dxc.crmservice.infrastructure.adapter.out.feign.dto;

import lombok.Builder;

@Builder
public record ResponseWrapper<T>(T data) {
}
