package com.example.notification.application.usecase;

import com.example.notification.application.dto.NotificationRequestDTO;
import com.example.notification.application.service.NotificationStrategyResolver;
import com.example.notification.domain.entity.Notification;
import com.example.notification.domain.exceptions.NotificationException;
import com.example.notification.domain.strategy.NotificationStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessNotificationUseCase {

    private final NotificationStrategyResolver strategyResolver;

    public void execute(NotificationRequestDTO dto) {
        validate(dto);

        String subject = (dto.getSubject() != null && !dto.getSubject().isBlank())
                ? dto.getSubject()
                : "Notificação";

        Notification notification = new Notification(
                dto.getRecipient(),
                subject,
                dto.getMessage(),
                dto.getSendAt(),
                dto.getChannel()
        );

        log.info("Processando notificação para {} via {}", notification.recipient(), notification.channel());

        NotificationStrategy strategy = strategyResolver.resolve(notification.channel());
        strategy.send(notification);

        log.info("Notificação enviada com sucesso para {}", notification.recipient());
    }

    private void validate(NotificationRequestDTO dto) {
        if (dto.getRecipient() == null || dto.getRecipient().isBlank()) {
            throw new NotificationException("O destinatário não pode ser vazio");
        }
        if (dto.getMessage() == null || dto.getMessage().isBlank()) {
            throw new NotificationException("A mensagem não pode ser vazia");
        }
        if (dto.getChannel() == null) {
            throw new NotificationException("O canal de notificação é obrigatório");
        }
    }
}
