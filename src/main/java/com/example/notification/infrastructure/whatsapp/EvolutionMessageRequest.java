package com.example.notification.infrastructure.whatsapp;

public record EvolutionMessageRequest(
        String number,
        Options options,
        TextMessage textMessage
) {
    public record Options(int delay) {}
    public record TextMessage(String text) {}

    public static EvolutionMessageRequest of(String phoneNumber, String text) {
        return new EvolutionMessageRequest(
                phoneNumber,
                new Options(1200),
                new TextMessage(text)
        );
    }
}
