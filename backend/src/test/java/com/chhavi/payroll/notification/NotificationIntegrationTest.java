package com.chhavi.payroll.notification;

import com.chhavi.payroll.dto.NotificationResponse;
import com.chhavi.payroll.entity.Employee;
import com.chhavi.payroll.entity.Notification;
import com.chhavi.payroll.entity.NotificationType;
import com.chhavi.payroll.repository.EmployeeRepository;
import com.chhavi.payroll.repository.NotificationRepository;
import com.chhavi.payroll.security.NotificationSecurityService;
import com.chhavi.payroll.service.NotificationService;
import com.chhavi.payroll.service.PayrollService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
        "app.security.seed-admin-username=admin",
        "app.security.seed-admin-password=Admin@123",
        "app.security.seed-hr-username=hr",
        "app.security.seed-hr-password=Hr@123",
        "app.security.seed-employee-username=priya",
        "app.security.seed-employee-password=Priya@123",
        "app.security.seed-employee-id=1",
        "app.security.jwt-secret=replace-this-with-a-long-random-development-secret-key-1234567890",
        "app.security.jwt-expiration-ms=3600000"
})
@Transactional
class NotificationIntegrationTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private PayrollService payrollService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private NotificationSecurityService notificationSecurityService;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldCreateAndMarkNotificationAsRead() {

        Employee employee = employeeRepository.findById(1L)
                .orElseThrow(() ->
                        new AssertionError("Employee with id 1 was not found"));

        long unreadBefore =
                notificationService.getUnreadCount(employee.getId());

        NotificationResponse created =
                notificationService.createNotification(
                        employee,
                        NotificationType.PAYROLL_PROCESSED,
                        "Test payroll notification " + UUID.randomUUID()
                );

        assertNotNull(created.getId());
        assertEquals(employee.getId(), created.getEmployeeId());
        assertEquals(NotificationType.PAYROLL_PROCESSED, created.getType());
        assertFalse(created.isReadStatus());

        long unreadAfterCreate =
                notificationService.getUnreadCount(employee.getId());

        assertEquals(unreadBefore + 1, unreadAfterCreate);

        NotificationResponse updated =
                notificationService.markAsRead(created.getId());

        assertTrue(updated.isReadStatus());

        long unreadAfterRead =
                notificationService.getUnreadCount(employee.getId());

        assertEquals(unreadBefore, unreadAfterRead);
    }

    @Test
    void shouldCreateProcessedNotificationWhenPayrollIsProcessed() {

        Employee employee = employeeRepository.findById(1L)
                .orElseThrow(() ->
                        new AssertionError("Employee with id 1 was not found"));

        String payPeriod = "TEST-PROCESS-" + UUID.randomUUID();

        var payroll =
                payrollService.createPayroll(
                        employee.getId(),
                        new BigDecimal("50000"),
                        new BigDecimal("5000"),
                        new BigDecimal("1000"),
                        payPeriod
                );

        payrollService.processPayroll(payroll.getId());

        List<Notification> notifications =
                notificationRepository.findByEmployeeIdOrderByCreatedAtDesc(
                        employee.getId()
                );

        assertTrue(
                notifications.stream()
                        .anyMatch(notification ->
                                notification.getType() == NotificationType.PAYROLL_PROCESSED
                                        && notification.getMessage().contains(payPeriod))
        );
    }

    @Test
    void shouldCreatePaidNotificationWhenPayrollIsPaid() {

        Employee employee = employeeRepository.findById(1L)
                .orElseThrow(() ->
                        new AssertionError("Employee with id 1 was not found"));

        String payPeriod = "TEST-PAID-" + UUID.randomUUID();

        var payroll =
                payrollService.createPayroll(
                        employee.getId(),
                        new BigDecimal("60000"),
                        new BigDecimal("6000"),
                        new BigDecimal("1500"),
                        payPeriod
                );

        payrollService.processPayroll(payroll.getId());
        payrollService.payPayroll(payroll.getId());

        List<Notification> notifications =
                notificationRepository.findByEmployeeIdOrderByCreatedAtDesc(
                        employee.getId()
                );

        assertTrue(
                notifications.stream()
                        .anyMatch(notification ->
                                notification.getType() == NotificationType.PAYROLL_PAID
                                        && notification.getMessage().contains(payPeriod))
        );
    }

    @Test
    void shouldAllowEmployeeToAccessOwnNotification() {

        Employee employee = employeeRepository.findById(1L)
                .orElseThrow(() ->
                        new AssertionError("Employee with id 1 was not found"));

        Notification notification = new Notification();
        notification.setEmployee(employee);
        notification.setType(NotificationType.PAYROLL_PROCESSED);
        notification.setMessage("Security test notification");
        notification.setReadStatus(false);
        notification.setCreatedAt(LocalDateTime.now());

        notification = notificationRepository.save(notification);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "priya",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_EMPLOYEE"))
                )
        );

        assertTrue(
                notificationSecurityService.isCurrentEmployee(
                        SecurityContextHolder.getContext().getAuthentication()
                )
        );

        assertTrue(
                notificationSecurityService.canAccessNotification(
                        SecurityContextHolder.getContext().getAuthentication(),
                        notification.getId()
                )
        );
    }

    @Test
    void shouldRejectAdminFromEmployeeNotificationAccess() {

        Employee employee = employeeRepository.findById(1L)
                .orElseThrow(() ->
                        new AssertionError("Employee with id 1 was not found"));

        Notification notification = new Notification();
        notification.setEmployee(employee);
        notification.setType(NotificationType.PAYROLL_PROCESSED);
        notification.setMessage("Admin security test");
        notification.setReadStatus(false);
        notification.setCreatedAt(LocalDateTime.now());

        notification = notificationRepository.save(notification);

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );

        assertFalse(
                notificationSecurityService.isCurrentEmployee(
                        SecurityContextHolder.getContext().getAuthentication()
                )
        );

        assertFalse(
                notificationSecurityService.canAccessNotification(
                        SecurityContextHolder.getContext().getAuthentication(),
                        notification.getId()
                )
        );
    }
}