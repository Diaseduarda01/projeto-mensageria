package com.example.notification.infrastructure.messaging;

import com.example.notification.application.dto.NotificationRequestDTO;
import com.example.notification.application.usecase.ProcessNotificationUseCase;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQConsumer {

    private final ProcessNotificationUseCase processNotificationUseCase;
    private final ObjectMapper objectMapper;

    @RabbitListener(queues = "${notification.queue.name}")
    public void consume(String message) {
        log.info("Mensagem recebida da fila: {}", message);
        try {
            NotificationRequestDTO dto = objectMapper.readValue(message, NotificationRequestDTO.class);
            processNotificationUseCase.execute(dto);
        } catch (JsonProcessingException e) {
            log.error("Falha ao desserializar mensagem da fila: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Erro ao processar notificação: {}", e.getMessage());
        }
    }
}
