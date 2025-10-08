package com.LetsWork.CRM.util;

public class InsufficientCreditsException extends Exception {
    public InsufficientCreditsException(String message) {
        super(message);
    }
}
