package com.example.notification.infrastructure.strategy;

import com.example.notification.domain.entity.Notification;
import com.example.notification.domain.enums.NotificationChannel;
import com.example.notification.infrastructure.email.EmailSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class EmailNotificationStrategyTest {

    @Mock
    private EmailSender emailSender;

    @InjectMocks
    private EmailNotificationStrategy strategy;

    @Test
    @DisplayName("Deve suportar o canal EMAIL")
    void shouldSupportEmailChannel() {
        assertThat(strategy.supports(NotificationChannel.EMAIL)).isTrue();
    }

    @Test
    @DisplayName("Não deve suportar canais diferentes de EMAIL")
    void shouldNotSupportOtherChannels() {
        assertThat(strategy.supports(NotificationChannel.SMS)).isFalse();
        assertThat(strategy.supports(NotificationChannel.PUSH)).isFalse();
        assertThat(strategy.supports(NotificationChannel.WHATSAPP)).isFalse();
    }

    @Test
    @DisplayName("Deve chamar o EmailSender com os dados corretos")
    void shouldDelegateToEmailSender() {
        var notification = new Notification(
                "user@email.com",
                "Seu agendamento foi confirmado",
                LocalDateTime.now(),
                NotificationChannel.EMAIL
        );

        strategy.send(notification);

        verify(emailSender).send(
                eq("user@email.com"),
                any(String.class),
                eq("Seu agendamento foi confirmado")
        );
    }
}
