package com.dxc.projectservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.projectservice.infrastructure.adapter.out.persistence.entity.OutboxEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OutboxEventRepository extends JpaRepository<OutboxEventEntity, UUID> {
    List<OutboxEventEntity> findByAggregateIdOrderByOccurredOnDesc(UUID aggregateId);
    List<OutboxEventEntity> findByStatusOrderByOccurredOnAsc(String status);
}
