package co.edu.uniquindio.eventos.pfeventosp2.Model;

//patrón decorator - para los servicios adicionales
public abstract class EntradaDecorator extends Entrada{
    protected Entrada entradaInterna;

    public EntradaDecorator(Entrada entradaInterna) {
        //aquí se le pasan los datos de la entrada original al constructor padre
        super(entradaInterna.getIdEntrada(), entradaInterna.getZona(), entradaInterna.getAsiento());
        this.entradaInterna = entradaInterna;
    }
}
