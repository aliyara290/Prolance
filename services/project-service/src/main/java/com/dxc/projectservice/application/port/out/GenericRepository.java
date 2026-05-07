package com.dxc.projectservice.application.port.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface GenericRepository<T> {
    T save(T entity);
    Optional<T> findById(UUID id, UUID tenantId);
    void delete(UUID id, UUID tenantId);
    Page<T> findAll(UUID tenantId, Pageable pageable);
    boolean existsById(UUID id, UUID tenantId);
}
