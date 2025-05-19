package school.sptech.model;

import java.time.LocalDateTime;

public class Cliente {
    private int id;
    private int idUsuario;
    private int status;
    private int frequenciaMinutos;
    private String canalDestinatario;
    private String webhookUrl;
    private LocalDateTime ultimaNotificacao;

    public Cliente() {
    }

    public Cliente(int id, int idUsuario, int status, int frequenciaMinutos,
                   String canalDestinatario, String webhookUrl, LocalDateTime ultimaNotificacao) {
        this.id = id;
        this.idUsuario = idUsuario;
        this.status = status;
        this.frequenciaMinutos = frequenciaMinutos;
        this.canalDestinatario = canalDestinatario;
        this.webhookUrl = webhookUrl;
        this.ultimaNotificacao = ultimaNotificacao;
    }

    public boolean deveNotificarAgora() {
        if (ultimaNotificacao == null) return true;
        LocalDateTime agora = LocalDateTime.now();
        return java.time.Duration.between(ultimaNotificacao, agora).toMinutes() >= frequenciaMinutos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getFrequenciaMinutos() {
        return frequenciaMinutos;
    }

    public void setFrequenciaMinutos(int frequenciaMinutos) {
        this.frequenciaMinutos = frequenciaMinutos;
    }

    public String getCanalDestinatario() {
        return canalDestinatario;
    }

    public void setCanalDestinatario(String canalDestinatario) {
        this.canalDestinatario = canalDestinatario;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public LocalDateTime getUltimaNotificacao() {
        return ultimaNotificacao;
    }

    public void setUltimaNotificacao(LocalDateTime ultimaNotificacao) {
        this.ultimaNotificacao = ultimaNotificacao;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", idUsuario=" + idUsuario +
                ", status=" + status +
                ", frequenciaMinutos=" + frequenciaMinutos +
                ", canalDestinatario='" + canalDestinatario + '\'' +
                ", webhookUrl='" + webhookUrl + '\'' +
                ", ultimaNotificacao=" + ultimaNotificacao +
                '}';
    }
}