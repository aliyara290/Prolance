package com.dxc.notificationservice.infrastructure.adapter.out.persistence.jpa;

import com.dxc.notificationservice.infrastructure.adapter.out.persistence.entity.NotificationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface NotificationRepositoryJpa extends JpaRepository<NotificationEntity, UUID> {
    
    Optional<NotificationEntity> findByIdAndTenantId(UUID id, UUID tenantId);
    
    @Query("""
            SELECT n FROM NotificationEntity n 
            WHERE n.tenantId = :tenantId 
            AND n.recipientId = :recipientId 
            AND (:type IS NULL OR n.type = :type) 
            AND (:readStatus IS NULL OR n.readStatus = :readStatus) 
            AND (:category IS NULL OR n.category = :category) 
            AND n.archivedAt IS NULL
            """)
    Page<NotificationEntity> findByFilters(
            @Param("tenantId") UUID tenantId, 
            @Param("recipientId") UUID recipientId, 
            @Param("type") String type, 
            @Param("readStatus") String readStatus, 
            @Param("category") String category, 
            Pageable pageable
    );
    
    @Query("SELECT COUNT(n) FROM NotificationEntity n WHERE n.tenantId = :tenantId AND n.recipientId = :recipientId AND n.readStatus = 'UNREAD' AND n.archivedAt IS NULL")
    long countUnread(@Param("tenantId") UUID tenantId, @Param("recipientId") UUID recipientId);
    
    @Modifying
    @Query("UPDATE NotificationEntity n SET n.readStatus = 'READ', n.readAt = CURRENT_TIMESTAMP WHERE n.tenantId = :tenantId AND n.recipientId = :recipientId AND n.readStatus = 'UNREAD'")
    void markAllAsRead(@Param("tenantId") UUID tenantId, @Param("recipientId") UUID recipientId);
}
