package com.example.notification.domain.entity;

import com.example.notification.domain.enums.NotificationChannel;

import java.time.LocalDateTime;

public record Notification(
        String recipient,
        String subject,
        String message,
        LocalDateTime sendAt,
        NotificationChannel channel
) {}
