package com.gov.rw.erp.exception;

public class PayrollAlreadyExistsException extends RuntimeException {
    public PayrollAlreadyExistsException(String message) {
        super(message);
    }
}
