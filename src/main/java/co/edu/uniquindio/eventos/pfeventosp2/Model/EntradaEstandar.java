package co.edu.uniquindio.eventos.pfeventosp2.Model;

//patrón decorator
public class EntradaEstandar extends Entrada{
    //constructor
    public EntradaEstandar(String idEntrada, Zona zona, Asiento asiento) {
        super(idEntrada, zona, asiento);
    }

    //getters
    @Override
    public double getPrecioFinal() {
        return zona.getPrecioBase();
    }

    @Override
    public String getDescripcion() {
        return "Entrada para zona: " + zona.getNombre();
    }
}
