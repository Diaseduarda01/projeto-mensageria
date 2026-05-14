package com.example.notification.interfaces.rest;

import com.example.notification.application.dto.NotificationRequestDTO;
import com.example.notification.application.usecase.ProcessNotificationUseCase;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Tag(name = "Notificações", description = "Endpoints para envio manual de notificações")
public class NotificationController {

    private final ProcessNotificationUseCase processNotificationUseCase;

    @PostMapping
    @Operation(
            summary = "Enviar notificação",
            description = "Envia uma notificação manualmente pelo canal especificado"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Notificação enviada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos na requisição"),
            @ApiResponse(responseCode = "500", description = "Erro interno ao enviar notificação")
    })
    public ResponseEntity<Void> send(@RequestBody @Valid NotificationRequestDTO dto) {
        processNotificationUseCase.execute(dto);
        return ResponseEntity.ok().build();
    }
}
