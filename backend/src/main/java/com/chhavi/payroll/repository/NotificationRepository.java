package com.chhavi.payroll.repository;

import com.chhavi.payroll.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByEmployeeIdOrderByCreatedAtDesc(
            Long employeeId
    );

    List<Notification> findByEmployeeIdAndReadStatusOrderByCreatedAtDesc(
            Long employeeId,
            boolean readStatus
    );

    long countByEmployeeIdAndReadStatus(
            Long employeeId,
            boolean readStatus
    );
}