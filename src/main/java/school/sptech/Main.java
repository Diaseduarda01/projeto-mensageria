package school.sptech;

import school.sptech.repository.NotificacaoRepository;
import school.sptech.service.NotificacaoService;

import java.sql.Connection;
import java.sql.DriverManager;

public class Main {
    public static void main(String[] args) {

        String url = "jdbc:mysql://localhost:3306/flyon";
        String usuario = "root";
        String senha = "dudadias";

        try (Connection conexao = DriverManager.getConnection(url, usuario, senha)) {
            NotificacaoRepository repo = new NotificacaoRepository(conexao);
            NotificacaoService notificacaoService = new NotificacaoService(repo);
            notificacaoService.processarNotificacoes();
        } catch (Exception e) {
            System.out.println("Erro ao processar notificações: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
