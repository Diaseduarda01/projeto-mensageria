package com.example.notification.integration;

import com.example.notification.application.dto.NotificationRequestDTO;
import com.example.notification.domain.enums.NotificationChannel;
import com.example.notification.infrastructure.email.EmailSender;
import com.example.notification.infrastructure.whatsapp.EvolutionApiSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@SpringBootTest
@Testcontainers
class NotificationIntegrationTest {

    @Container
    static RabbitMQContainer rabbitMQContainer =
            new RabbitMQContainer("rabbitmq:3.13-management");

    @DynamicPropertySource
    static void configureRabbitMQ(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", rabbitMQContainer::getAmqpPort);
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EmailSender emailSender;

    @MockBean
    private EvolutionApiSender evolutionApiSender;

    @Value("${notification.queue.exchange}")
    private String exchange;

    @Value("${notification.queue.routing-key}")
    private String routingKey;

    @Test
    @DisplayName("Deve processar mensagem da fila e acionar o EmailSender")
    void shouldConsumeMessageAndSendEmail() throws Exception {
        var dto = NotificationRequestDTO.builder()
                .recipient("integration@test.com")
                .message("Mensagem de teste de integração")
                .sendAt(LocalDateTime.now().plusHours(1))
                .channel(NotificationChannel.EMAIL)
                .build();

        String json = objectMapper.writeValueAsString(dto);
        Message message = MessageBuilder
                .withBody(json.getBytes(StandardCharsets.UTF_8))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .build();

        rabbitTemplate.send(exchange, routingKey, message);

        // Aguarda até 5 segundos pelo processamento assíncrono
        verify(emailSender, timeout(5000)).send(
                eq("integration@test.com"),
                any(String.class),
                eq("Mensagem de teste de integração")
        );
    }

    @Test
    @DisplayName("Deve processar mensagem da fila e acionar o EvolutionApiSender")
    void shouldConsumeMessageAndSendWhatsApp() throws Exception {
        var dto = NotificationRequestDTO.builder()
                .recipient("5511999999999")
                .message("Seu agendamento foi confirmado via WhatsApp")
                .sendAt(LocalDateTime.now().plusHours(1))
                .channel(NotificationChannel.WHATSAPP)
                .build();

        String json = objectMapper.writeValueAsString(dto);
        Message message = MessageBuilder
                .withBody(json.getBytes(StandardCharsets.UTF_8))
                .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                .build();

        rabbitTemplate.send(exchange, routingKey, message);

        verify(evolutionApiSender, timeout(5000)).send(
                eq("5511999999999"),
                eq("Seu agendamento foi confirmado via WhatsApp")
        );
    }
}
