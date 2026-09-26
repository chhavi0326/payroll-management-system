package com.chhavi.payroll.controller;

import com.chhavi.payroll.dto.PayrollAuditResponse;
import com.chhavi.payroll.service.PayrollAuditService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payroll")
public class PayrollAuditController {

    private final PayrollAuditService payrollAuditService;

    public PayrollAuditController(
            PayrollAuditService payrollAuditService) {

        this.payrollAuditService = payrollAuditService;
    }

    // Audit history for a specific payroll
    @GetMapping("/{payrollId}/audit")
    @PreAuthorize(
            "@payrollSecurityService.canAccessPayroll(authentication, #payrollId)"
    )
    public ResponseEntity<List<PayrollAuditResponse>> getPayrollAuditHistory(
            @PathVariable Long payrollId) {

        return ResponseEntity.ok(
                payrollAuditService.getPayrollAuditHistory(payrollId)
        );
    }

    // Audit history performed by a specific user
    // Restricted to ADMIN and HR
    @GetMapping("/audit/user/{username}")
    @PreAuthorize("hasAnyRole('ADMIN', 'HR')")
    public ResponseEntity<List<PayrollAuditResponse>> getUserAuditHistory(
            @PathVariable String username) {

        return ResponseEntity.ok(
                payrollAuditService.getUserAuditHistory(username)
        );
    }
}