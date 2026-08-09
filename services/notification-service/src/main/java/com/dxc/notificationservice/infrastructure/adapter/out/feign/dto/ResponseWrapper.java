package com.dxc.notificationservice.infrastructure.adapter.out.feign.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseWrapper<T> {
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;
}
