package com.chhavi.payroll.controller;

import com.chhavi.payroll.dto.NotificationResponse;
import com.chhavi.payroll.security.NotificationSecurityService;
import com.chhavi.payroll.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService) {

        this.notificationService = notificationService;
    }

    // Get all notifications for the logged-in employee
    @GetMapping
    @PreAuthorize(
            "@notificationSecurityService.isCurrentEmployee(authentication)"
    )
    public ResponseEntity<List<NotificationResponse>>
    getNotifications() {

        Long employeeId =
                notificationService
                        .getCurrentEmployeeId();

        return ResponseEntity.ok(
                notificationService.getEmployeeNotifications(
                        employeeId
                )
        );
    }

    // Get unread notifications
    @GetMapping("/unread")
    @PreAuthorize(
            "@notificationSecurityService.isCurrentEmployee(authentication)"
    )
    public ResponseEntity<List<NotificationResponse>>
    getUnreadNotifications() {

        Long employeeId =
                notificationService
                        .getCurrentEmployeeId();

        return ResponseEntity.ok(
                notificationService.getUnreadNotifications(
                        employeeId
                )
        );
    }

    // Get unread notification count
    @GetMapping("/unread/count")
    @PreAuthorize(
            "@notificationSecurityService.isCurrentEmployee(authentication)"
    )
    public ResponseEntity<Long> getUnreadCount() {

        Long employeeId =
                notificationService
                        .getCurrentEmployeeId();

        return ResponseEntity.ok(
                notificationService.getUnreadCount(
                        employeeId
                )
        );
    }

    // Mark notification as read
    @PutMapping("/{id}/read")
    @PreAuthorize(
            "@notificationSecurityService.canAccessNotification(authentication, #id)"
    )
    public ResponseEntity<NotificationResponse> markAsRead(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                notificationService.markAsRead(id)
        );
    }
}