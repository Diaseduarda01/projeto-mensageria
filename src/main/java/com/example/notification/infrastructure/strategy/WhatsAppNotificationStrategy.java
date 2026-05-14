package com.example.notification.infrastructure.strategy;

import com.example.notification.domain.entity.Notification;
import com.example.notification.domain.enums.NotificationChannel;
import com.example.notification.domain.strategy.NotificationStrategy;
import com.example.notification.infrastructure.whatsapp.EvolutionApiSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WhatsAppNotificationStrategy implements NotificationStrategy {

    private final EvolutionApiSender evolutionApiSender;

    @Override
    public void send(Notification notification) {
        log.debug("Enviando notificação via WHATSAPP para {}", notification.recipient());
        evolutionApiSender.send(notification.recipient(), notification.message());
    }

    @Override
    public boolean supports(NotificationChannel channel) {
        return NotificationChannel.WHATSAPP.equals(channel);
    }
}
