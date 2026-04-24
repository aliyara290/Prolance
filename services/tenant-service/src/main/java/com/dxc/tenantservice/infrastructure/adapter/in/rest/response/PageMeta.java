package com.dxc.tenantservice.infrastructure.adapter.in.rest.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PageMeta {
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
}