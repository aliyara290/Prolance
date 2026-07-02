package com.dxc.taskservice.domain.exception;

public class ValidationException extends DomainException {
    private static final long serialVersionUID = 1L;

    public ValidationException(String message) {
        super(message);
    }
}
