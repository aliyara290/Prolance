package com.dxc.tenantservice.application.saga;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;

@Component
@Slf4j
@Scope("prototype")
public class RegistrationSaga {

    private final Deque<CompensationAction> compensations = new ArrayDeque<>();

    public void register(CompensationAction compensation) {
        compensations.push(compensation);
    }

    public void rollback() {
        compensations.forEach(action -> {
            try {
                action.compensate();
            } catch (Exception e) {
                log.error("Compensation failed, manual cleanup required", e);
            }
        });
    }
}