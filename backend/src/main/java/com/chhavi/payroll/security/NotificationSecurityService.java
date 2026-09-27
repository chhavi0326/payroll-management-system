package com.chhavi.payroll.security;

import com.chhavi.payroll.entity.AppUser;
import com.chhavi.payroll.entity.Notification;
import com.chhavi.payroll.entity.Role;
import com.chhavi.payroll.repository.AppUserRepository;
import com.chhavi.payroll.repository.NotificationRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class NotificationSecurityService {

    private final AppUserRepository appUserRepository;
    private final NotificationRepository notificationRepository;

    public NotificationSecurityService(
            AppUserRepository appUserRepository,
            NotificationRepository notificationRepository) {

        this.appUserRepository = appUserRepository;
        this.notificationRepository = notificationRepository;
    }

    public boolean isCurrentEmployee(
            Authentication authentication) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            return false;
        }

        AppUser user =
                appUserRepository.findByUsername(
                        authentication.getName()
                ).orElse(null);

        return user != null
                && user.getRole() == Role.EMPLOYEE
                && user.getEmployee() != null;
    }

    public boolean canAccessNotification(
            Authentication authentication,
            Long notificationId) {

        if (authentication == null ||
                !authentication.isAuthenticated()) {
            return false;
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

        Notification notification =
                notificationRepository.findById(notificationId)
                        .orElse(null);

        if (notification == null ||
                notification.getEmployee() == null) {
            return false;
        }

        return notification.getEmployee()
                .getId()
                .equals(user.getEmployee().getId());
    }
}