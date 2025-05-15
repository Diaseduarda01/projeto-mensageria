package school.sptech.model;

import java.time.LocalDateTime;

public class Cliente {
    public int id;
    public String nomeCliente;
    public String webhookUrl;
    public String canal;
    public java.time.LocalDateTime ultimaNotificacao;
    public int intervaloMinutos;

    public Cliente() {
    }

    public Cliente(int id, String nomeCliente, String webhookUrl, String canal, LocalDateTime ultimaNotificacao, int intervaloMinutos) {
        this.id = id;
        this.nomeCliente = nomeCliente;
        this.webhookUrl = webhookUrl;
        this.canal = canal;
        this.ultimaNotificacao = ultimaNotificacao;
        this.intervaloMinutos = intervaloMinutos;
    }

    public boolean deveNotificarAgora() {
        if (ultimaNotificacao == null) return true;
        java.time.LocalDateTime agora = java.time.LocalDateTime.now();
        java.time.Duration duracao = java.time.Duration.between(ultimaNotificacao, agora);
        return duracao.toMinutes() >= intervaloMinutos;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeCliente() {
        return nomeCliente;
    }

    public void setNomeCliente(String nomeCliente) {
        this.nomeCliente = nomeCliente;
    }

    public String getWebhookUrl() {
        return webhookUrl;
    }

    public void setWebhookUrl(String webhookUrl) {
        this.webhookUrl = webhookUrl;
    }

    public String getCanal() {
        return canal;
    }

    public void setCanal(String canal) {
        this.canal = canal;
    }

    public LocalDateTime getUltimaNotificacao() {
        return ultimaNotificacao;
    }

    public void setUltimaNotificacao(LocalDateTime ultimaNotificacao) {
        this.ultimaNotificacao = ultimaNotificacao;
    }

    public int getIntervaloMinutos() {
        return intervaloMinutos;
    }

    public void setIntervaloMinutos(int intervaloMinutos) {
        this.intervaloMinutos = intervaloMinutos;
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "id=" + id +
                ", nomeCliente='" + nomeCliente + '\'' +
                ", webhookUrl='" + webhookUrl + '\'' +
                ", canal='" + canal + '\'' +
                ", ultimaNotificacao=" + ultimaNotificacao +
                ", intervaloMinutos=" + intervaloMinutos +
                '}';
    }
}
