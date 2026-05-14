package com.example.notification.application.usecase;

import com.example.notification.application.dto.NotificationRequestDTO;
import com.example.notification.application.service.NotificationStrategyResolver;
import com.example.notification.domain.entity.Notification;
import com.example.notification.domain.enums.NotificationChannel;
import com.example.notification.domain.exceptions.NotificationException;
import com.example.notification.domain.strategy.NotificationStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProcessNotificationUseCaseTest {

    @Mock
    private NotificationStrategyResolver strategyResolver;

    @Mock
    private NotificationStrategy strategy;

    @InjectMocks
    private ProcessNotificationUseCase useCase;

    @Test
    @DisplayName("Deve selecionar a strategy correta e enviar a notificação")
    void shouldResolveStrategyAndSendNotification() {
        var dto = buildDto("user@email.com", "Mensagem de teste", NotificationChannel.EMAIL);
        when(strategyResolver.resolve(NotificationChannel.EMAIL)).thenReturn(strategy);

        useCase.execute(dto);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(strategy).send(captor.capture());

        Notification sent = captor.getValue();
        assertThat(sent.recipient()).isEqualTo("user@email.com");
        assertThat(sent.message()).isEqualTo("Mensagem de teste");
        assertThat(sent.channel()).isEqualTo(NotificationChannel.EMAIL);
    }

    @Test
    @DisplayName("Deve lançar exceção quando o destinatário estiver em branco")
    void shouldThrowWhenRecipientIsBlank() {
        var dto = buildDto("", "Mensagem", NotificationChannel.EMAIL);

        assertThatThrownBy(() -> useCase.execute(dto))
                .isInstanceOf(NotificationException.class)
                .hasMessageContaining("destinatário");

        verify(strategy, never()).send(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando a mensagem estiver em branco")
    void shouldThrowWhenMessageIsBlank() {
        var dto = buildDto("user@email.com", "  ", NotificationChannel.EMAIL);

        assertThatThrownBy(() -> useCase.execute(dto))
                .isInstanceOf(NotificationException.class)
                .hasMessageContaining("mensagem");

        verify(strategy, never()).send(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o canal for nulo")
    void shouldThrowWhenChannelIsNull() {
        var dto = buildDto("user@email.com", "Mensagem", null);

        assertThatThrownBy(() -> useCase.execute(dto))
                .isInstanceOf(NotificationException.class)
                .hasMessageContaining("canal");

        verify(strategy, never()).send(any());
    }

    private NotificationRequestDTO buildDto(String recipient, String message, NotificationChannel channel) {
        return NotificationRequestDTO.builder()
                .recipient(recipient)
                .message(message)
                .sendAt(LocalDateTime.now().plusHours(1))
                .channel(channel)
                .build();
    }
}
