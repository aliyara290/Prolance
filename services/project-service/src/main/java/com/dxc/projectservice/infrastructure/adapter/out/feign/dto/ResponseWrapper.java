package com.dxc.projectservice.infrastructure.adapter.out.feign.dto;

import lombok.Builder;

@Builder
public record ResponseWrapper<T>(T data) {
}
