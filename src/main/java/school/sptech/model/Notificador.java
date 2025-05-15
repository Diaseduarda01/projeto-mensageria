package school.sptech.model;

public abstract class Notificador {
    protected String destino;

    public Notificador(String destino) {
        this.destino = destino;
    }

    public abstract void notificar(String mensagem);
}
