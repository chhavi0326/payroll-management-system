package com.chhavi.payroll.security;

import com.chhavi.payroll.entity.AppUser;
import com.chhavi.payroll.entity.Role;
import com.chhavi.payroll.exception.EmployeeNotFoundException;
import com.chhavi.payroll.repository.AppUserRepository;
import com.chhavi.payroll.repository.EmployeeRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserService(
            AppUserRepository appUserRepository,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder) {

        this.appUserRepository = appUserRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public AppUser createUser(
            String username,
            String password,
            Role role,
            Long employeeId) {

        if (appUserRepository.existsByUsername(username)) {
            throw new IllegalArgumentException(
                    "Username already exists: " + username
            );
        }

        AppUser user = new AppUser();

        user.setUsername(username);
        user.setPassword(
                passwordEncoder.encode(password)
        );
        user.setRole(role);

        if (employeeId != null) {
            user.setEmployee(
                    employeeRepository.findById(employeeId)
                            .orElseThrow(() ->
                                    new EmployeeNotFoundException(
                                            "Employee not found with id: "
                                                    + employeeId
                                    )
                            )
            );
        }

        return appUserRepository.save(user);
    }
}