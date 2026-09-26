package com.chhavi.payroll.dto;

import com.chhavi.payroll.entity.AuditAction;

import java.time.LocalDateTime;

public class PayrollAuditResponse {

    private Long id;
    private Long payrollId;
    private AuditAction action;
    private String username;
    private LocalDateTime timestamp;

    public PayrollAuditResponse() {
    }

    public PayrollAuditResponse(
            Long id,
            Long payrollId,
            AuditAction action,
            String username,
            LocalDateTime timestamp) {

        this.id = id;
        this.payrollId = payrollId;
        this.action = action;
        this.username = username;
        this.timestamp = timestamp;
    }

    public Long getId() {
        return id;
    }

    public Long getPayrollId() {
        return payrollId;
    }

    public AuditAction getAction() {
        return action;
    }

    public String getUsername() {
        return username;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}