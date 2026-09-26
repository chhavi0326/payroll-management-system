package com.chhavi.payroll.security;

import com.chhavi.payroll.entity.AppUser;
import com.chhavi.payroll.entity.Role;
import com.chhavi.payroll.entity.Payroll;
import com.chhavi.payroll.repository.AppUserRepository;
import com.chhavi.payroll.repository.PayrollRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class PayrollSecurityService {

    private final AppUserRepository appUserRepository;
    private final PayrollRepository payrollRepository;

    public PayrollSecurityService(
            AppUserRepository appUserRepository,
            PayrollRepository payrollRepository) {

        this.appUserRepository = appUserRepository;
        this.payrollRepository = payrollRepository;
    }

    public boolean canAccessEmployeePayroll(
            Authentication authentication,
            Long employeeId) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            return false;
        }

        if (isAdminOrHr(authentication)) {
            return true;
        }

        AppUser user =
                appUserRepository.findByUsername(
                        authentication.getName()
                ).orElse(null);

        if (user == null ||
                user.getRole() != Role.EMPLOYEE ||
                user.getEmployee() == null) {
            return false;
        }

        return user.getEmployee()
                .getId()
                .equals(employeeId);
    }

    public boolean canAccessPayroll(
            Authentication authentication,
            Long payrollId) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            return false;
        }

        if (isAdminOrHr(authentication)) {
            return true;
        }

        AppUser user =
                appUserRepository.findByUsername(
                        authentication.getName()
                ).orElse(null);

        if (user == null ||
                user.getRole() != Role.EMPLOYEE ||
                user.getEmployee() == null) {
            return false;
        }

        Payroll payroll =
                payrollRepository.findById(payrollId)
                        .orElse(null);

        if (payroll == null ||
                payroll.getEmployee() == null) {
            return false;
        }

        return payroll.getEmployee()
                .getId()
                .equals(user.getEmployee().getId());
    }

    private boolean isAdminOrHr(
            Authentication authentication) {

        return authentication.getAuthorities()
                .stream()
                .anyMatch(authority ->
                        authority.getAuthority().equals("ROLE_ADMIN")
                                || authority.getAuthority().equals("ROLE_HR"));
    }
}