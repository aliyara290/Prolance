package com.dxc.attachmentservice.domain.exception;

public class InvalidAttachmentException extends RuntimeException {
    public InvalidAttachmentException(String message) {
        super(message);
    }
}
