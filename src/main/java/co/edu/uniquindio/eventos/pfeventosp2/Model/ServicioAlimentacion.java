package co.edu.uniquindio.eventos.pfeventosp2.Model;

//servicio adicional dentro del decorator
public class ServicioAlimentacion extends EntradaDecorator{
    private double costo;
    //aquí pueden ir detalles como "hamburguesa" o "gaseosa"
    private String detalleCombo;

    //constructor
    public ServicioAlimentacion(Entrada entradaInterna, double costo, String detalleCombo) {
        super(entradaInterna);
        this.costo = costo;
        this.detalleCombo = detalleCombo;
    }

    //getters
    @Override
    public double getPrecioFinal() {
        return entradaInterna.getPrecioFinal() + costo;
    }

    @Override
    public String getDescripcion() {
        return entradaInterna.getDescripcion() + " + Combo Alimentación (" + detalleCombo + ") Precio: $" + costo;
    }
}
