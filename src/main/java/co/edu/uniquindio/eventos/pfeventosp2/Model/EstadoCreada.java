package co.edu.uniquindio.eventos.pfeventosp2.Model;

import java.io.Serializable;
//patrón State
public class EstadoCreada implements EstadoCompra{
    @Override
    public void pagar(Compra compra) {
        System.out.println("Procesando pago..");
        //el método siguiente permite pasar al siguiente estado
        compra.setEstado(new EstadoPagada());
        System.out.println("Pago Exitoso");
    }

    @Override
    public void cancelar(Compra compra) {
        System.out.println("Compra cancelada sin cobros.");
        //el método siguiente permite pasar al siguiente estado
        compra.setEstado(new EstadoCancelada());
    }

    @Override
    public void agregarBoleto(Compra compra, Entrada boleto) {
        compra.getItemsCompra().add(boleto);
        //después de añadir el boleto también se tiene que aumentar el precio final de la compra
        double nuevoTotal = compra.getTotal() + boleto.getPrecio();
        compra.setTotal(nuevoTotal);
        System.out.println("Boleto agregado. Precio final: $" + nuevoTotal);
    }

    @Override
    public void quitarBoleto(Compra compra, Entrada boleto) {
        //primero se tiene que verificar si el boleto está dentro de la compra, y si está se elimina
        if (compra.getItemsCompra().remove(boleto)) {
            //depsués de quitar la boleta se le tiene que restar el precio al precio final de la compra
            double nuevoTotal = compra.getTotal() - boleto.getPrecio();
            compra.setTotal(nuevoTotal);
            System.out.println("Boleto eliminado. Nuevo total: $" + nuevoTotal);
        } else {
            System.out.println("El boleto no se encontró en esta compra.");
        }
    }
}
