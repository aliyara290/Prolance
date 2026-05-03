package com.dxc.tenantservice.infrastructure.adapter.in.rest.response;

import lombok.Builder;

@Builder
public record PageMeta(
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean hasNext,
        boolean hasPrevious
) {}