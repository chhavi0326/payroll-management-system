package com.chhavi.payroll.security;

import com.chhavi.payroll.entity.AppUser;
import com.chhavi.payroll.entity.Role;
import com.chhavi.payroll.repository.AppUserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class PayrollSecurityService {

    private final AppUserRepository appUserRepository;

    public PayrollSecurityService(
            AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public boolean canAccessEmployeePayroll(
            Authentication authentication,
            Long employeeId) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            return false;
        }

        boolean isAdminOrHr =
                authentication.getAuthorities()
                        .stream()
                        .anyMatch(authority ->
                                authority.getAuthority().equals("ROLE_ADMIN")
                                        || authority.getAuthority().equals("ROLE_HR"));

        if (isAdminOrHr) {
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
}