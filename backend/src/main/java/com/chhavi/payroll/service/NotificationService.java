package com.chhavi.payroll.service;

import com.chhavi.payroll.dto.NotificationResponse;
import com.chhavi.payroll.entity.AppUser;
import com.chhavi.payroll.entity.Employee;
import com.chhavi.payroll.entity.Notification;
import com.chhavi.payroll.entity.NotificationType;
import com.chhavi.payroll.repository.AppUserRepository;
import com.chhavi.payroll.repository.NotificationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final AppUserRepository appUserRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            AppUserRepository appUserRepository) {

        this.notificationRepository = notificationRepository;
        this.appUserRepository = appUserRepository;
    }

    @Transactional
    public NotificationResponse createNotification(
            Employee employee,
            NotificationType type,
            String message) {

        Notification notification = new Notification();

        notification.setEmployee(employee);
        notification.setType(type);
        notification.setMessage(message);
        notification.setReadStatus(false);
        notification.setCreatedAt(LocalDateTime.now());

        Notification savedNotification =
                notificationRepository.save(notification);

        return mapToResponse(savedNotification);
    }

    public List<NotificationResponse> getEmployeeNotifications(
            Long employeeId) {

        return notificationRepository
                .findByEmployeeIdOrderByCreatedAtDesc(employeeId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<NotificationResponse> getUnreadNotifications(
            Long employeeId) {

        return notificationRepository
                .findByEmployeeIdAndReadStatusOrderByCreatedAtDesc(
                        employeeId,
                        false
                )
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public long getUnreadCount(Long employeeId) {

        return notificationRepository
                .countByEmployeeIdAndReadStatus(
                        employeeId,
                        false
                );
    }

    @Transactional
    public NotificationResponse markAsRead(
            Long notificationId) {

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "Notification not found with id: "
                                                + notificationId
                                )
                        );

        notification.setReadStatus(true);

        Notification updatedNotification =
                notificationRepository.save(notification);

        return mapToResponse(updatedNotification);
    }

    public Long getCurrentEmployeeId() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        AppUser user =
                appUserRepository.findByUsername(
                        authentication.getName()
                ).orElseThrow(() ->
                        new IllegalStateException(
                                "Authenticated user not found"
                        )
                );

        if (user.getEmployee() == null) {
            throw new IllegalStateException(
                    "Authenticated user is not linked to an employee"
            );
        }

        return user.getEmployee().getId();
    }

    private NotificationResponse mapToResponse(
            Notification notification) {

        return new NotificationResponse(
                notification.getId(),
                notification.getEmployee().getId(),
                notification.getType(),
                notification.getMessage(),
                notification.isReadStatus(),
                notification.getCreatedAt()
        );
    }
}