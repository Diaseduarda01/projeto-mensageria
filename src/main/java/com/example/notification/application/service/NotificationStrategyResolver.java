package com.example.notification.application.service;

import com.example.notification.domain.enums.NotificationChannel;
import com.example.notification.domain.exceptions.UnsupportedChannelException;
import com.example.notification.domain.strategy.NotificationStrategy;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationStrategyResolver {

    private final List<NotificationStrategy> strategies;

    public NotificationStrategy resolve(NotificationChannel channel) {
        return strategies.stream()
                .filter(strategy -> strategy.supports(channel))
                .findFirst()
                .orElseThrow(() -> new UnsupportedChannelException(channel));
    }
}
