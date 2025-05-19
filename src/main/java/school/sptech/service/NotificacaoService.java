package school.sptech.service;

import school.sptech.controller.SlackController;
import school.sptech.model.Cliente;
import school.sptech.model.Notificador;
import school.sptech.repository.NotificacaoRepository;

import java.sql.SQLException;

public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;


    public NotificacaoService(NotificacaoRepository notificacaoRepository) {
        this.notificacaoRepository = notificacaoRepository;
    }

    public void processarNotificacoes() throws SQLException {
        var clientes = notificacaoRepository.buscarClientes();

        for (Cliente cliente : clientes) {
            if (cliente.deveNotificarAgora()) {
                // AGREGADO dinamicamente com os dados do banco
                Notificador notificador = new SlackController(cliente.getWebhookUrl());

                String mensagem = "📢 Olá, seus dados foram carregados.";
                notificador.notificar(mensagem);

                notificacaoRepository.atualizarUltimaNotificacao(cliente.getId());
            }
        }
    }
}



