package com.letswork.crm.util;

public class InsufficientCreditsException extends Exception {
    public InsufficientCreditsException(String message) {
        super(message);
    }
}
