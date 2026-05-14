package com.example.notification.infrastructure.strategy;

import com.example.notification.domain.entity.Notification;
import com.example.notification.domain.enums.NotificationChannel;
import com.example.notification.infrastructure.whatsapp.EvolutionApiSender;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WhatsAppNotificationStrategyTest {

    @Mock
    private EvolutionApiSender evolutionApiSender;

    @InjectMocks
    private WhatsAppNotificationStrategy strategy;

    @Test
    @DisplayName("Deve suportar o canal WHATSAPP")
    void shouldSupportWhatsAppChannel() {
        assertThat(strategy.supports(NotificationChannel.WHATSAPP)).isTrue();
    }

    @Test
    @DisplayName("Não deve suportar canais diferentes de WHATSAPP")
    void shouldNotSupportOtherChannels() {
        assertThat(strategy.supports(NotificationChannel.EMAIL)).isFalse();
        assertThat(strategy.supports(NotificationChannel.SMS)).isFalse();
        assertThat(strategy.supports(NotificationChannel.PUSH)).isFalse();
    }

    @Test
    @DisplayName("Deve chamar o EvolutionApiSender com os dados corretos")
    void shouldDelegateToEvolutionApiSender() {
        var notification = new Notification(
                "5511999999999",
                "Seu agendamento foi confirmado",
                LocalDateTime.now(),
                NotificationChannel.WHATSAPP
        );

        strategy.send(notification);

        verify(evolutionApiSender).send("5511999999999", "Seu agendamento foi confirmado");
    }
}
