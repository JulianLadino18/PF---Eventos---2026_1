package co.edu.uniquindio.eventos.pfeventosp2.Model;

public class Asiento {
    //atributos
    private String idAsiento;
    private String fila;
    private int numero;
    //pueden ser los siguientes estados: "Disponible", "Reservado", "Vendido", "Bloqueado"
    private String estado;

    //constructor
    public Asiento(String idAsiento, String fila, int numero) {
        this.idAsiento = idAsiento;
        this.fila = fila;
        this.numero = numero;
        this.estado = "Disponible";
    }

    //método para cambiar el estado del asiento
    public void cambiarEstado(String nuevoEstado) {
        this.estado = nuevoEstado;
    }

    //getters
    public String getIdAsiento() {
        return idAsiento;
    }

    public String getFila() {
        return fila;
    }

    public int getNumero() {
        return numero;
    }

    public String getEstado() {
        return estado;
    }

    @Override
    public String toString() {
        return "Asiento{" +
                "fila='" + fila + '\'' +
                ", numero=" + numero +
                ", estado='" + estado + '\'' +
                '}';
    }
}
