package co.edu.uniquindio.eventos.pfeventosp2.Model;

public interface IPagoAdapter {
    //método para saber si la transacción fue acepatada o rechazada
    public boolean procesarPago(double monto);
}
