package school.sptech.repository;

import school.sptech.model.Cliente;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificacaoRepository {
    private final Connection conexao;

    public NotificacaoRepository(Connection conexao) {
        this.conexao = conexao;
    }

    public List<Cliente> buscarClientes() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT id, nome_cliente, webhook_url, canal, ultima_notificacao, intervalo_minutos FROM notificacoes";

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.id = rs.getInt("id");
                cliente.nomeCliente = rs.getString("nome_cliente");
                cliente.webhookUrl = rs.getString("webhook_url");
                cliente.canal = rs.getString("canal");
                Timestamp ultima = rs.getTimestamp("ultima_notificacao");
                cliente.ultimaNotificacao = (ultima != null) ? ultima.toLocalDateTime() : null;
                cliente.intervaloMinutos = rs.getInt("intervalo_minutos");
                lista.add(cliente);
            }
        }

        return lista;
    }

    public void atualizarUltimaNotificacao(int idCliente) throws SQLException {
        String sql = "UPDATE notificacoes SET ultima_notificacao = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.executeUpdate();
        }
    }
}
