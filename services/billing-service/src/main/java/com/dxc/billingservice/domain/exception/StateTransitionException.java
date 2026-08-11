package com.dxc.billingservice.domain.exception;

public class StateTransitionException extends RuntimeException {
    public StateTransitionException(String message) {
        super(message);
    }
}
