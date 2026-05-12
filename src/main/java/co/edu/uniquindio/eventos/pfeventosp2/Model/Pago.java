package co.edu.uniquindio.eventos.pfeventosp2.Model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Pago {
    //atributos
    private String idPago;
    private double montoTotal;
    private LocalDateTime fechaPago;
    private IPagoAdapter metodoDePago; // Aquí está la magia del patrón Adapter

    //constructor
    public Pago(double montoTotal, IPagoAdapter metodoDePago) {
        //esto surve para generar un id único para la transacción
        this.idPago = UUID.randomUUID().toString();
        this.montoTotal = montoTotal;
        this.fechaPago = LocalDateTime.now();
        this.metodoDePago = metodoDePago;
    }

    //método principal que ejecuta el cobro
    public boolean ejecutarPago() {
        if (metodoDePago == null) {
            System.out.println("Error: No se ha definido un método de pago.");
            return false;
        }
        //aquí el adaptador sabrá a qué API externa llamar
        return metodoDePago.procesarPago(this.montoTotal);
    }

    //getters
    public String getIdPago() {
        return idPago;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public IPagoAdapter getMetodoDePago() {
        return metodoDePago;
    }
}
