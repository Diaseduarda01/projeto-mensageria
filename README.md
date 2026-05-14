# Notification Service

Microserviço de notificações multicanal construído com **Java 21**, **Spring Boot** e **RabbitMQ**, seguindo **Clean Architecture** e **Strategy Pattern**. Projetado para ser extensível, testável e pronto para produção.

---

## Sumário

- [Visão Geral](#visão-geral)
- [Stack](#stack)
- [Arquitetura](#arquitetura)
- [Fluxo da Aplicação](#fluxo-da-aplicação)
- [Estrutura de Pacotes](#estrutura-de-pacotes)
- [Design Pattern — Strategy](#design-pattern--strategy)
- [Como Executar](#como-executar)
- [API REST](#api-rest)
- [Formato da Mensagem (Fila)](#formato-da-mensagem-fila)
- [Testes](#testes)
- [Como Adicionar um Novo Canal](#como-adicionar-um-novo-canal)
- [Variáveis de Ambiente](#variáveis-de-ambiente)

---

## Visão Geral

O **Notification Service** é responsável por processar eventos de notificação vindos de uma fila RabbitMQ e encaminhá-los para o canal correto (e-mail, SMS, push etc.). A primeira implementação entrega notificações por **e-mail**.

O serviço também expõe uma **API REST** para envio manual de notificações, útil para testes e integrações diretas.

---

## Stack

| Tecnologia | Finalidade |
|---|---|
| Java 21 | Linguagem principal (records, sealed classes) |
| Spring Boot 3.3 | Framework principal |
| Spring AMQP | Integração com RabbitMQ |
| Spring Mail | Envio de e-mails via SMTP |
| Springdoc OpenAPI | Documentação automática (Swagger UI) |
| Lombok | Redução de boilerplate |
| JUnit 5 + Mockito | Testes unitários |
| Testcontainers | Testes de integração com RabbitMQ real |
| Docker + Compose | Empacotamento e orquestração |

---

## Arquitetura

O projeto segue **Clean Architecture**, separando o código em camadas com dependências que apontam sempre para dentro (em direção ao domínio):

```
┌─────────────────────────────────────────────┐
│               interfaces (REST)             │  ← entrada HTTP
├─────────────────────────────────────────────┤
│              infrastructure                 │  ← RabbitMQ, e-mail, config
├─────────────────────────────────────────────┤
│               application                  │  ← use cases, DTOs, resolvers
├─────────────────────────────────────────────┤
│                 domain                      │  ← entidades, enums, interfaces
└─────────────────────────────────────────────┘
```

**Regra de dependência:** camadas externas conhecem as internas, nunca o contrário. O domínio não importa nada de Spring.

---

## Fluxo da Aplicação

```
[Producer externo]
       │
       │  JSON → notification.queue
       ▼
 RabbitMQConsumer          ← infrastructure/messaging
       │
       │  deserializa → NotificationRequestDTO
       ▼
 ProcessNotificationUseCase ← application/usecase
       │
       │  valida dados
       │  cria Notification (domain entity)
       ▼
 NotificationStrategyResolver ← application/service
       │
       │  encontra a strategy pelo canal
       ▼
 EmailNotificationStrategy  ← infrastructure/strategy
       │
       ▼
   EmailSender → SMTP
```

O mesmo fluxo acontece quando a notificação chega via **API REST** (`POST /notifications`), pulando apenas o consumer.

---

## Estrutura de Pacotes

```
src/main/java/com/example/notification/
│
├── NotificationServiceApplication.java
│
├── domain/
│   ├── entity/
│   │   └── Notification.java              ← record imutável
│   ├── enums/
│   │   └── NotificationChannel.java       ← EMAIL, SMS, PUSH, WHATSAPP
│   ├── strategy/
│   │   └── NotificationStrategy.java      ← interface do padrão Strategy
│   └── exceptions/
│       ├── NotificationException.java
│       └── UnsupportedChannelException.java
│
├── application/
│   ├── dto/
│   │   └── NotificationRequestDTO.java    ← payload de entrada (validado)
│   ├── service/
│   │   └── NotificationStrategyResolver.java
│   └── usecase/
│       └── ProcessNotificationUseCase.java
│
├── infrastructure/
│   ├── email/
│   │   └── EmailSender.java               ← wrapper do JavaMailSender
│   ├── strategy/
│   │   └── EmailNotificationStrategy.java ← implementação EMAIL
│   ├── messaging/
│   │   └── RabbitMQConsumer.java          ← @RabbitListener
│   └── configuration/
│       ├── RabbitMQConfig.java
│       └── OpenAPIConfig.java
│
└── interfaces/
    └── rest/
        ├── NotificationController.java
        └── GlobalExceptionHandler.java
```

---

## Design Pattern — Strategy

O **Strategy Pattern** permite adicionar novos canais de notificação sem modificar nenhum código existente (Open/Closed Principle).

### Interface (domínio)

```java
public interface NotificationStrategy {
    void send(Notification notification);
    boolean supports(NotificationChannel channel);
}
```

### Implementações (infraestrutura)

```java
// Já implementado
EmailNotificationStrategy   → suporta NotificationChannel.EMAIL

// Prontos para implementar
SmsNotificationStrategy     → suporta NotificationChannel.SMS
PushNotificationStrategy    → suporta NotificationChannel.PUSH
WhatsAppNotificationStrategy → suporta NotificationChannel.WHATSAPP
```

### Resolver (aplicação)

O `NotificationStrategyResolver` recebe automaticamente todas as implementações de `NotificationStrategy` via injeção de dependência e seleciona a correta em tempo de execução:

```java
// Spring injeta todas as implementações disponíveis
private final List<NotificationStrategy> strategies;

public NotificationStrategy resolve(NotificationChannel channel) {
    return strategies.stream()
            .filter(s -> s.supports(channel))
            .findFirst()
            .orElseThrow(() -> new UnsupportedChannelException(channel));
}
```

---

## Como Executar

### Pré-requisitos

- Docker e Docker Compose instalados

### 1. Subir com Docker Compose

```bash
# Clonar o repositório
git clone <url-do-repo>
cd notification-service

# Configurar credenciais de e-mail (opcional para testar via Swagger)
export MAIL_USERNAME=seu@email.com
export MAIL_PASSWORD=sua-senha-de-app

# Subir os serviços
docker compose up --build
```

Serviços disponíveis:

| Serviço | URL |
|---|---|
| API REST | http://localhost:8080 |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| RabbitMQ Management | http://localhost:15672 (guest/guest) |

### 2. Executar localmente (sem Docker)

Necessário RabbitMQ rodando localmente ou via container:

```bash
# Subir apenas o RabbitMQ
docker run -d --name rabbitmq \
  -p 5672:5672 -p 15672:15672 \
  rabbitmq:3.13-management

# Rodar a aplicação
./mvnw spring-boot:run
```

---

## API REST

### `POST /notifications`

Envia uma notificação manualmente.

**Request:**
```json
{
  "recipient": "user@email.com",
  "message": "Seu agendamento foi confirmado",
  "sendAt": "2026-05-10T10:00:00",
  "channel": "EMAIL"
}
```

**Respostas:**

| Código | Descrição |
|---|---|
| `200` | Notificação enviada com sucesso |
| `400` | Dados inválidos ou canal não suportado |
| `500` | Falha ao enviar (ex: erro de SMTP) |

A documentação interativa completa está disponível em `/swagger-ui.html`.

---

## Formato da Mensagem (Fila)

Mensagens publicadas na fila `notification.queue` devem seguir o formato JSON abaixo:

```json
{
  "recipient": "user@email.com",
  "message": "Seu agendamento foi confirmado",
  "sendAt": "2026-05-10T10:00:00",
  "channel": "EMAIL"
}
```

| Campo | Tipo | Descrição |
|---|---|---|
| `recipient` | String | E-mail ou identificador do destinatário |
| `message` | String | Conteúdo da notificação |
| `sendAt` | LocalDateTime | Data e hora do envio (`yyyy-MM-ddTHH:mm:ss`) |
| `channel` | Enum | Canal: `EMAIL`, `SMS`, `PUSH`, `WHATSAPP` |

O consumer é agnóstico a headers do Spring AMQP (`__TypeId__`), aceitando JSON puro de qualquer producer.

---

## Testes

### Testes Unitários

Cobrem as regras de negócio isoladas de frameworks e infraestrutura:

```bash
mvn test -Dtest="ProcessNotificationUseCaseTest,\
EmailNotificationStrategyTest,\
NotificationStrategyResolverTest"
```

| Classe de Teste | O que verifica |
|---|---|
| `ProcessNotificationUseCaseTest` | Seleção da strategy, validações de recipient/message/channel |
| `EmailNotificationStrategyTest` | Método `supports()`, delegação ao `EmailSender` com parâmetros corretos |
| `NotificationStrategyResolverTest` | Resolução correta, exceção para canal sem strategy |

### Teste de Integração

Sobe um container RabbitMQ real via Testcontainers e valida o fluxo completo:

```bash
mvn test -Dtest="NotificationIntegrationTest"
```

O teste publica uma mensagem JSON na fila e verifica (com timeout de 5 segundos) que o `EmailSender` foi chamado com os parâmetros corretos — sem enviar e-mail de verdade, usando `@MockBean`.

### Todos os testes

```bash
mvn test
```

---

## Como Adicionar um Novo Canal

Para adicionar **WhatsApp**, por exemplo, basta criar **um único arquivo**:

```java
// src/main/java/com/example/notification/infrastructure/strategy/WhatsAppNotificationStrategy.java

@Slf4j
@Component
@RequiredArgsConstructor
public class WhatsAppNotificationStrategy implements NotificationStrategy {

    private final WhatsAppClient whatsAppClient; // seu cliente HTTP

    @Override
    public void send(Notification notification) {
        log.info("Enviando WhatsApp para {}", notification.recipient());
        whatsAppClient.send(notification.recipient(), notification.message());
    }

    @Override
    public boolean supports(NotificationChannel channel) {
        return NotificationChannel.WHATSAPP.equals(channel);
    }
}
```

O Spring detecta automaticamente o novo `@Component` e o `NotificationStrategyResolver` passa a oferecer suporte ao canal `WHATSAPP` sem nenhuma outra alteração.

---

## Variáveis de Ambiente

| Variável | Padrão | Descrição |
|---|---|---|
| `RABBITMQ_HOST` | `localhost` | Host do RabbitMQ |
| `RABBITMQ_PORT` | `5672` | Porta AMQP |
| `RABBITMQ_USER` | `guest` | Usuário RabbitMQ |
| `RABBITMQ_PASS` | `guest` | Senha RabbitMQ |
| `MAIL_HOST` | `smtp.gmail.com` | Servidor SMTP |
| `MAIL_PORT` | `587` | Porta SMTP |
| `MAIL_USERNAME` | — | Usuário do e-mail remetente |
| `MAIL_PASSWORD` | — | Senha de app do e-mail |

> **Dica Gmail:** gere uma *Senha de App* em Conta Google → Segurança → Verificação em duas etapas → Senhas de app.
