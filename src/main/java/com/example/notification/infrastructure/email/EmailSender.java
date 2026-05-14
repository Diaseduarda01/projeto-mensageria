package com.example.notification.infrastructure.email;

import com.example.notification.domain.exceptions.NotificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EmailSender {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    public void send(String to, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);
            log.info("E-mail enviado para {}", to);
        } catch (Exception e) {
            log.error("Falha ao enviar e-mail para {}: {}", to, e.getMessage());
            throw new NotificationException("Falha ao enviar e-mail para: " + to, e);
        }
    }
}
