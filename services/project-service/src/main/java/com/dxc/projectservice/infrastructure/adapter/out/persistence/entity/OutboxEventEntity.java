package com.dxc.projectservice.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OutboxEventEntity {

    @Id
    private UUID id;

    @Column(nullable = false, name = "aggregate_id")
    private UUID aggregateId;

    @Column(nullable = false, name = "aggregate_type")
    private String aggregateType;

    @Column(nullable = false, name = "event_type")
    private String type;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, name = "retry_count")
    private int retryCount;

    @Column(nullable = false, name = "occurred_on")
    private LocalDateTime occurredOn;

    @Column(nullable = false, name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(columnDefinition = "TEXT", name = "error_message")
    private String errorMessage;
}