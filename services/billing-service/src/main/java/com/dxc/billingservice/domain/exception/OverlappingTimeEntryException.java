package com.dxc.billingservice.domain.exception;

public class OverlappingTimeEntryException extends BusinessRuleException {
    public OverlappingTimeEntryException(String message) {
        super(message);
    }
}
