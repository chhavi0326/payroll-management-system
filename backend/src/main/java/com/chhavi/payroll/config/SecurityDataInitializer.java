package com.chhavi.payroll.config;

import com.chhavi.payroll.entity.AppUser;
import com.chhavi.payroll.entity.Employee;
import com.chhavi.payroll.entity.Role;
import com.chhavi.payroll.repository.AppUserRepository;
import com.chhavi.payroll.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class SecurityDataInitializer implements CommandLineRunner {

    private final AppUserRepository appUserRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;

    private final String adminUsername;
    private final String adminPassword;

    private final String hrUsername;
    private final String hrPassword;

    private final String employeeUsername;
    private final String employeePassword;

    private final Long employeeId;

    public SecurityDataInitializer(
            AppUserRepository appUserRepository,
            EmployeeRepository employeeRepository,
            PasswordEncoder passwordEncoder,
            @Value("${app.security.seed-admin-username}") String adminUsername,
            @Value("${app.security.seed-admin-password}") String adminPassword,
            @Value("${app.security.seed-hr-username}") String hrUsername,
            @Value("${app.security.seed-hr-password}") String hrPassword,
            @Value("${app.security.seed-employee-username}") String employeeUsername,
            @Value("${app.security.seed-employee-password}") String employeePassword,
            @Value("${app.security.seed-employee-id}") Long employeeId) {

        this.appUserRepository = appUserRepository;
        this.employeeRepository = employeeRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
        this.hrUsername = hrUsername;
        this.hrPassword = hrPassword;
        this.employeeUsername = employeeUsername;
        this.employeePassword = employeePassword;
        this.employeeId = employeeId;
    }

    @Override
    public void run(String... args) {

        createAdminUser();
        createHrUser();
        createEmployeeUser();
    }

    private void createAdminUser() {

        if (appUserRepository.existsByUsername(adminUsername)) {
            return;
        }

        AppUser admin = new AppUser();

        admin.setUsername(adminUsername);
        admin.setPassword(
                passwordEncoder.encode(adminPassword)
        );
        admin.setRole(Role.ADMIN);

        appUserRepository.save(admin);
    }

    private void createHrUser() {

        if (appUserRepository.existsByUsername(hrUsername)) {
            return;
        }

        AppUser hr = new AppUser();

        hr.setUsername(hrUsername);
        hr.setPassword(
                passwordEncoder.encode(hrPassword)
        );
        hr.setRole(Role.HR);

        appUserRepository.save(hr);
    }

    private void createEmployeeUser() {

        if (appUserRepository.existsByUsername(employeeUsername)) {
            return;
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() ->
                        new IllegalStateException(
                                "Employee not found for security seed: "
                                        + employeeId
                        )
                );

        AppUser employeeUser = new AppUser();

        employeeUser.setUsername(employeeUsername);
        employeeUser.setPassword(
                passwordEncoder.encode(employeePassword)
        );
        employeeUser.setRole(Role.EMPLOYEE);
        employeeUser.setEmployee(employee);

        appUserRepository.save(employeeUser);
    }
}