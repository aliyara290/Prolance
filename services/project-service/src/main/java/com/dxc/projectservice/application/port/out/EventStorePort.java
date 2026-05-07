package com.dxc.projectservice.application.port.out;

import com.dxc.projectservice.domain.model.event.DomainEvent;

public interface EventStorePort {
    void save(DomainEvent event);
}
