package com.dxc.projectservice.application.port.out;

import com.dxc.projectservice.domain.model.event.DomainEvent;
import java.util.Collection;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
    void publish(Collection<DomainEvent> events);
}
