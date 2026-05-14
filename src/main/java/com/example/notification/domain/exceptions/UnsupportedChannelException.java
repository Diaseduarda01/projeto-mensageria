package com.example.notification.domain.exceptions;

import com.example.notification.domain.enums.NotificationChannel;

public class UnsupportedChannelException extends NotificationException {

    public UnsupportedChannelException(NotificationChannel channel) {
        super("Nenhuma strategy encontrada para o canal: " + channel);
    }
}
