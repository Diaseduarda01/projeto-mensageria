package com.example.notification.application.service;

import com.example.notification.domain.enums.NotificationChannel;
import com.example.notification.domain.exceptions.UnsupportedChannelException;
import com.example.notification.domain.strategy.NotificationStrategy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class NotificationStrategyResolverTest {

    @Test
    @DisplayName("Deve retornar a strategy correta para o canal informado")
    void shouldResolveCorrectStrategy() {
        NotificationStrategy emailStrategy = mock(NotificationStrategy.class);
        when(emailStrategy.supports(NotificationChannel.EMAIL)).thenReturn(true);

        var resolver = new NotificationStrategyResolver(List.of(emailStrategy));

        NotificationStrategy resolved = resolver.resolve(NotificationChannel.EMAIL);

        assertThat(resolved).isSameAs(emailStrategy);
    }

    @Test
    @DisplayName("Deve lançar UnsupportedChannelException quando não houver strategy para o canal")
    void shouldThrowWhenNoStrategyFound() {
        var resolver = new NotificationStrategyResolver(List.of());

        assertThatThrownBy(() -> resolver.resolve(NotificationChannel.EMAIL))
                .isInstanceOf(UnsupportedChannelException.class)
                .hasMessageContaining("EMAIL");
    }

    @Test
    @DisplayName("Deve ignorar strategies que não suportam o canal solicitado")
    void shouldIgnoreNonMatchingStrategies() {
        NotificationStrategy smsStrategy = mock(NotificationStrategy.class);
        when(smsStrategy.supports(NotificationChannel.EMAIL)).thenReturn(false);
        when(smsStrategy.supports(NotificationChannel.SMS)).thenReturn(true);

        NotificationStrategy emailStrategy = mock(NotificationStrategy.class);
        when(emailStrategy.supports(NotificationChannel.EMAIL)).thenReturn(true);

        var resolver = new NotificationStrategyResolver(List.of(smsStrategy, emailStrategy));

        assertThat(resolver.resolve(NotificationChannel.EMAIL)).isSameAs(emailStrategy);
        assertThat(resolver.resolve(NotificationChannel.SMS)).isSameAs(smsStrategy);
    }
}
