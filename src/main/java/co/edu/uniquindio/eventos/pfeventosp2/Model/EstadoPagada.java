package co.edu.uniquindio.eventos.pfeventosp2.Model;
//patrón State
public class EstadoPagada implements EstadoCompra{
    @Override
    public void pagar(Compra compra) {
        //este método no se puede realizar ya que la compra ya fue pagada
        System.out.println("Error: Esta compra ya fue pagada.");
    }

    @Override
    public void cancelar(Compra compra) {
        //si se cancela un pago se tiene que reembolsar
        System.out.println("Iniciando proceso de reembolso según políticas del evento...");
        // Aquí llamas a la política de cancelación del evento
    }

    @Override
    public void agregarBoleto(Compra compra, Entrada boleto) {
        //como la compra ya está pagada, no se pueden añadir boletos
        System.out.println("No se pueden agregar boletos a una compra que ya fue pagada.");
    }

    @Override
    public void removerBoleto(Compra compra, Entrada boleto) {
        //como la compra ya está pagada, no se pueden quitar boletos
        System.out.println("No se pueden quitar boletos de una compra que ya fue pagada.");
    }
}
