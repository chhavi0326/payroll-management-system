package com.chhavi.payroll.exception;

public class DuplicatePayrollException extends RuntimeException {

    public DuplicatePayrollException(String message) {
        super(message);
    }
}