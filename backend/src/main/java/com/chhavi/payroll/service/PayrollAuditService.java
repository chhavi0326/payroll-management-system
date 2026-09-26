package com.chhavi.payroll.service;

import com.chhavi.payroll.dto.PayrollAuditResponse;
import com.chhavi.payroll.entity.AuditAction;
import com.chhavi.payroll.entity.PayrollAudit;
import com.chhavi.payroll.repository.PayrollAuditRepository;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PayrollAuditService {

    private final PayrollAuditRepository payrollAuditRepository;

    public PayrollAuditService(
            PayrollAuditRepository payrollAuditRepository) {

        this.payrollAuditRepository = payrollAuditRepository;
    }

    public void recordAudit(
            Long payrollId,
            AuditAction action) {

        PayrollAudit audit = new PayrollAudit();

        audit.setPayrollId(payrollId);
        audit.setAction(action);
        audit.setUsername(getCurrentUsername());
        audit.setTimestamp(LocalDateTime.now());

        payrollAuditRepository.save(audit);
    }

    public List<PayrollAuditResponse> getPayrollAuditHistory(
            Long payrollId) {

        return payrollAuditRepository
                .findByPayrollIdOrderByTimestampAsc(payrollId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<PayrollAuditResponse> getUserAuditHistory(
            String username) {

        return payrollAuditRepository
                .findByUsernameOrderByTimestampDesc(username)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private String getCurrentUsername() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        if (authentication == null ||
                !authentication.isAuthenticated() ||
                authentication instanceof AnonymousAuthenticationToken) {

            return "SYSTEM";
        }

        return authentication.getName();
    }

    private PayrollAuditResponse mapToResponse(
            PayrollAudit audit) {

        return new PayrollAuditResponse(
                audit.getId(),
                audit.getPayrollId(),
                audit.getAction(),
                audit.getUsername(),
                audit.getTimestamp()
        );
    }
}