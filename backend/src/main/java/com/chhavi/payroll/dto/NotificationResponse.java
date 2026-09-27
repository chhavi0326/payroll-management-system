package com.chhavi.payroll.dto;

import com.chhavi.payroll.entity.NotificationType;

import java.time.LocalDateTime;

public class NotificationResponse {

    private Long id;
    private Long employeeId;
    private NotificationType type;
    private String message;
    private boolean readStatus;
    private LocalDateTime createdAt;

    public NotificationResponse() {
    }

    public NotificationResponse(
            Long id,
            Long employeeId,
            NotificationType type,
            String message,
            boolean readStatus,
            LocalDateTime createdAt) {

        this.id = id;
        this.employeeId = employeeId;
        this.type = type;
        this.message = message;
        this.readStatus = readStatus;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public NotificationType getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }

    public boolean isReadStatus() {
        return readStatus;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}