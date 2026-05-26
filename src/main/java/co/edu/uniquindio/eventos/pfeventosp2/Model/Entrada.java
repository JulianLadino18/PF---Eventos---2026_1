package co.edu.uniquindio.eventos.pfeventosp2.Model;

//patrón decorator - esta es la clase abstracta(base)
public abstract class Entrada {
    //atributos
    protected String idEntrada;
    protected Zona zona;
    //este atributo es null si está en zona general
    protected Asiento asiento;
    protected EstadoEntrada estado;

    //constructor
    public Entrada(String idEntrada, Zona zona, Asiento asiento) {
        this.idEntrada = idEntrada;
        this.zona = zona;
        this.asiento = asiento;
        this.estado = EstadoEntrada.ACTIVA;
    }

    //getters
    public String getIdEntrada() {
        return idEntrada;
    }
    public Zona getZona() {
        return zona;
    }
    public Asiento getAsiento() {
        return asiento;
    }
    public EstadoEntrada getEstado() {
        return estado;
    }

    public void setAsiento(Asiento asiento) {
        this.asiento = asiento;
    }

    //métodos abstractos que el decorator va a modificar
    public abstract double getPrecioFinal();
    public abstract String getDescripcion();

    //cambiar el estado de la entrada a "anulada"
    public void anularEntrada() {
        this.estado = EstadoEntrada.ANULADA;
    }
}
