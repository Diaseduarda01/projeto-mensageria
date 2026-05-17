package com.example.notification.application.dto;

import com.example.notification.domain.enums.NotificationChannel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Payload de envio de notificação")
public class NotificationRequestDTO {

    @NotBlank(message = "O destinatário é obrigatório")
    @Schema(description = "E-mail ou identificador do destinatário", example = "user@email.com")
    private String recipient;

    @Schema(description = "Assunto (usado em EMAIL, ignorado nos demais canais)", example = "Confirme seu e-mail")
    private String subject;

    @NotBlank(message = "A mensagem é obrigatória")
    @Schema(description = "Conteúdo da notificação", example = "Seu agendamento foi confirmado")
    private String message;

    @NotNull(message = "A data de envio é obrigatória")
    @Schema(description = "Data e hora agendada para o envio", example = "2026-05-10T10:00:00")
    private LocalDateTime sendAt;

    @NotNull(message = "O canal é obrigatório")
    @Schema(description = "Canal de notificação", example = "EMAIL")
    private NotificationChannel channel;
}
