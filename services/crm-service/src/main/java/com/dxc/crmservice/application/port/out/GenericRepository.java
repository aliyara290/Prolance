package com.dxc.crmservice.application.port.out;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface GenericRepository<T> {
    T save(T object);
    T findById(UUID id, UUID tenantId);
    T update(T object);
    void delete(UUID id, UUID tenantId);
    Page<T> findAll(UUID tenantId, Pageable pageable);
}
