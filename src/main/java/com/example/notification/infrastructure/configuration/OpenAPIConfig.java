package com.example.notification.infrastructure.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAPIConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Notification Service API")
                        .version("1.0.0")
                        .description("Microserviço de notificações multicanal via RabbitMQ e REST")
                        .contact(new Contact()
                                .name("Notification Team")
                                .email("notification@example.com")));
    }
}
