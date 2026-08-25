package com.chhavi.payroll.exception;

public class InvalidPayrollStateException extends RuntimeException {

    public InvalidPayrollStateException(String message) {
        super(message);
    }
}