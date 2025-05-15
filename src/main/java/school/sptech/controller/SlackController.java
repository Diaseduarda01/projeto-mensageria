package school.sptech.controller;

import school.sptech.model.Notificador;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class SlackController extends Notificador{

    public SlackController(String destino) {
        super(destino);
    }

    @Override
    public void notificar(String mensagem) {
        try {
            URL url = new URL(destino);
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("POST");
            conexao.setDoOutput(true);
            conexao.setRequestProperty("Content-Type", "application/json");

            // Slack espera o campo "text" no corpo JSON
            String payload = "{\"text\":\"" + mensagem + "\"}";

            byte[] out = payload.getBytes(StandardCharsets.UTF_8);

            OutputStream stream = conexao.getOutputStream();
            stream.write(out);

            int resposta = conexao.getResponseCode();
            if (resposta != 200) {
                System.out.println("Erro ao enviar notificação Slack. Código: " + resposta);
            }

            stream.close();
            conexao.disconnect();

        } catch (Exception e) {
            System.out.println("Erro ao notificar Slack: " + e.getMessage());
        }
    }
}
