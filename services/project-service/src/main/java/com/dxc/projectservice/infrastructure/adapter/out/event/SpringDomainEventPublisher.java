package com.dxc.projectservice.infrastructure.adapter.out.event;

import com.dxc.projectservice.application.port.out.DomainEventPublisher;
import com.dxc.projectservice.application.port.out.EventStorePort;
import com.dxc.projectservice.domain.model.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final EventStorePort eventStorePort;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent event) {
        log.debug("Persisting and publishing domain event: {}", event.eventId());
        eventStorePort.save(event);
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(Collection<DomainEvent> events) {
        if (events != null && !events.isEmpty()) {
            events.forEach(this::publish);
        }
    }
}
