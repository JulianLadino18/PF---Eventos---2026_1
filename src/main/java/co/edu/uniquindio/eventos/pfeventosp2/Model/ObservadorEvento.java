package co.edu.uniquindio.eventos.pfeventosp2.Model;

public interface ObservadorEvento {
    //Método que se llamará cuando el evento cambie de estado
    public void notificarCambio(String nombreEvento, String nuevoEstado);
}
