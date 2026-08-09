package com.dxc.taskservice.application.port.out;

import com.dxc.taskservice.domain.model.event.DomainEvent;
import java.util.Collection;

public interface DomainEventPublisher {
    void publish(DomainEvent event);
    void publish(Collection<DomainEvent> events);
}
