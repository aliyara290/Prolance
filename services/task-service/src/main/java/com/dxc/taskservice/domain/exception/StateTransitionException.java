package com.dxc.taskservice.domain.exception;

public class StateTransitionException extends DomainException {
    private static final long serialVersionUID = 1L;

    public StateTransitionException(String message) {
        super(message);
    }
}
