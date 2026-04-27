package com.dxc.crmservice.application.port.out;

import com.dxc.crmservice.domain.model.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface ActivityRepository {
    Activity save(Activity activity);
    Activity findById(UUID id, UUID tenantId);
    Activity update(Activity activity);
    void delete(UUID id, UUID tenantId);
    Page<Activity> findAll(UUID tenantId, Pageable pageable);
}
