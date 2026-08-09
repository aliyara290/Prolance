package com.dxc.projectservice.infrastructure.adapter.out.event.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.Map;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class MemberAddedIntegrationEvent extends IntegrationEvent {
    private Map<String, Object> payload;
}
