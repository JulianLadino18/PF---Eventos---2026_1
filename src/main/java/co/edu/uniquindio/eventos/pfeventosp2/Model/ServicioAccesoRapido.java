package co.edu.uniquindio.eventos.pfeventosp2.Model;

//servicio adicional dentro del decorator
public class ServicioAccesoRapido extends EntradaDecorator{
    private double costo;

    //constructor
    public ServicioAccesoRapido(Entrada entradaInterna, double costo) {
        super(entradaInterna);
        this.costo = costo;
    }

    //getters
    @Override
    public double getPrecioFinal() {
        return entradaInterna.getPrecioFinal() + costo;
    }

    @Override
    public String getDescripcion() {
        return entradaInterna.getDescripcion() + " + Fast Pass (Fila preferencial) Precio: $" + costo;
    }
}
