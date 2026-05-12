package co.edu.uniquindio.eventos.pfeventosp2.Model;

public class EstadoCancelada implements EstadoCompra{
    @Override
    public void agregarBoleto(Compra compra, Entrada boleto) {
        System.out.println("Error: La compra está cancelada. No se pueden añadir elementos.");
    }

    @Override
    public void quitarBoleto(Compra compra, Entrada boleto) {
        System.out.println("Error: La compra está cancelada.");
    }

    @Override
    public void pagar(Compra compra) {
        System.out.println("Error: No se puede pagar una compra cancelada.");
    }

    @Override
    public void cancelar(Compra compra) {
        System.out.println("La compra ya se encuentra en estado cancelado.");
    }
}
