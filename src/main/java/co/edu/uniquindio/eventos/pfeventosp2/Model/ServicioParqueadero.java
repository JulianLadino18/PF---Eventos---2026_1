package co.edu.uniquindio.eventos.pfeventosp2.Model;

//servicio adicional dentro del decorator
public class ServicioParqueadero extends EntradaDecorator{
    private double costoParqueadero;

    //constructor
    public ServicioParqueadero(Entrada entradaInterna, double costoParqueadero) {
        super(entradaInterna);
        this.costoParqueadero = costoParqueadero;
    }

    @Override
    public double getPrecioFinal() {
        return entradaInterna.getPrecioFinal() + costoParqueadero;
    }

    @Override
    public String getDescripcion() {
        return entradaInterna.getDescripcion() + " + Parqueadero ($" + costoParqueadero + ")";
    }
}
