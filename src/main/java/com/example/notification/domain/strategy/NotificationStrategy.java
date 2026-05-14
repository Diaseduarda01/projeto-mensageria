package com.example.notification.domain.strategy;

import com.example.notification.domain.entity.Notification;
import com.example.notification.domain.enums.NotificationChannel;

public interface NotificationStrategy {

    void send(Notification notification);

    boolean supports(NotificationChannel channel);
}
