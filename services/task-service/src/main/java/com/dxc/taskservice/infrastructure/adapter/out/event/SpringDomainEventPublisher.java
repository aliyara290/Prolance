package com.dxc.taskservice.infrastructure.adapter.out.event;

import com.dxc.taskservice.application.port.out.DomainEventPublisher;
import com.dxc.taskservice.domain.model.event.DomainEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(DomainEvent event) {
        log.debug("Publishing domain event: {}", event.eventId());
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(Collection<DomainEvent> events) {
        if (events != null && !events.isEmpty()) {
            events.forEach(this::publish);
        }
    }
}
