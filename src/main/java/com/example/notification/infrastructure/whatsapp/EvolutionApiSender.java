package com.example.notification.infrastructure.whatsapp;

import com.example.notification.domain.exceptions.NotificationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Slf4j
@Component
public class EvolutionApiSender {

    private final RestClient restClient;
    private final String instance;

    public EvolutionApiSender(
            RestClient.Builder builder,
            @Value("${evolution.api.base-url}") String baseUrl,
            @Value("${evolution.api.key}") String apiKey,
            @Value("${evolution.api.instance}") String instance
    ) {
        this.restClient = builder
                .baseUrl(baseUrl)
                .defaultHeader("apikey", apiKey)
                .build();
        this.instance = instance;
    }

    public void send(String phoneNumber, String message) {
        try {
            restClient.post()
                    .uri("/message/sendText/{instance}", instance)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(EvolutionMessageRequest.of(phoneNumber, message))
                    .retrieve()
                    .toBodilessEntity();

            log.info("WhatsApp enviado para {}", phoneNumber);
        } catch (Exception e) {
            log.error("Falha ao enviar WhatsApp para {}: {}", phoneNumber, e.getMessage());
            throw new NotificationException("Falha ao enviar WhatsApp para: " + phoneNumber, e);
        }
    }
}
