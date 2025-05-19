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
        String sql = "SELECT id, id_usuario, status, frequencia_minutos, canal_destinatario, webhook_url FROM notificacao_config WHERE status = 1";

        try (PreparedStatement stmt = conexao.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setId(rs.getInt("id"));
                cliente.setIdUsuario(rs.getInt("id_usuario"));
                cliente.setStatus(rs.getInt("status"));
                cliente.setFrequenciaMinutos(rs.getInt("frequencia_minutos"));
                cliente.setCanalDestinatario(rs.getString("canal_destinatario"));
                cliente.setWebhookUrl(rs.getString("webhook_url"));
                lista.add(cliente);
            }
        }

        return lista;
    }

    public void atualizarUltimaNotificacao(int idCliente) throws SQLException {
        String sql = "UPDATE notificacao_config SET ultima_notificacao = CURRENT_TIMESTAMP WHERE id = ?";
        try (PreparedStatement stmt = conexao.prepareStatement(sql)) {
            stmt.setInt(1, idCliente);
            stmt.executeUpdate();
        }
    }

}
