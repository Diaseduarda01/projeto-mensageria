package com.example.notification.infrastructure.strategy;

import com.example.notification.domain.entity.Notification;
import com.example.notification.domain.enums.NotificationChannel;
import com.example.notification.domain.strategy.NotificationStrategy;
import com.example.notification.infrastructure.email.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailNotificationStrategy implements NotificationStrategy {

    private final EmailSender emailSender;

    @Override
    public void send(Notification notification) {
        log.debug("Enviando notificação via EMAIL para {}", notification.recipient());
        emailSender.send(
                notification.recipient(),
                notification.subject(),
                notification.message()
        );
    }

    @Override
    public boolean supports(NotificationChannel channel) {
        return NotificationChannel.EMAIL.equals(channel);
    }
}
