package co.edu.uniquindio.eventos.pfeventosp2.Model;

//servicio adicional dentro del decorator
public class ServicioSouvenir extends EntradaDecorator{
    private double costo;
    //aquí pueden ir cosas como "Camiseta" o "Póster firmado"
    private String itemSouvenir;

    //constructor
    public ServicioSouvenir(Entrada entradaInterna, double costo, String itemSouvenir) {
        super(entradaInterna);
        this.costo = costo;
        this.itemSouvenir = itemSouvenir;
    }

    @Override
    public double getPrecioFinal() {
        return entradaInterna.getPrecioFinal() + costo;
    }

    @Override
    public String getDescripcion() {
        return entradaInterna.getDescripcion() + " + Souvenir Oficial (" + itemSouvenir + ") Precio: $" + costo;
    }
}
