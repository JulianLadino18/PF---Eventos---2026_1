package co.edu.uniquindio.eventos.pfeventosp2.Model;

import java.time.LocalDateTime;

public class Incidencia {
    //atributos
    private String idIncidencia;
    private TipoIncidencia tipo;
    private String descripcion;
    private LocalDateTime fecha;
    private TipoEntidad tipoEntidadAfectada;
    private String idEntidadAfectada;

    //constructor
    public Incidencia(String idIncidencia, TipoIncidencia tipo, String descripcion, TipoEntidad tipoEntidadAfectada, String idEntidadAfectada) {
        this.idIncidencia = idIncidencia;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = LocalDateTime.now();
        this.tipoEntidadAfectada = tipoEntidadAfectada;
        this.idEntidadAfectada = idEntidadAfectada;
    }

    //constructor para el repositorio, ya que con el otro constructor, cada vez que se lea el archivo el repositorio le pondrá la fecha "ahora mismo" en lugar de la fecha en la que ocurrió el error
    public Incidencia(String idIncidencia, TipoIncidencia tipo, String descripcion, LocalDateTime fecha, TipoEntidad tipoEntidadAfectada, String idEntidadAfectada) {
        this.idIncidencia = idIncidencia;
        this.tipo = tipo;
        this.descripcion = descripcion;
        this.fecha = fecha; // Asigna la fecha que viene del archivo
        this.tipoEntidadAfectada = tipoEntidadAfectada;
        this.idEntidadAfectada = idEntidadAfectada;
    }

    //getters
    public String getIdIncidencia() {
        return idIncidencia;
    }

    public TipoIncidencia getTipo() {
        return tipo;
    }
    public String getDescripcion() {
        return descripcion;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public TipoEntidad getTipoEntidadAfectada() {
        return tipoEntidadAfectada;
    }

    public String getIdEntidadAfectada() {
        return idEntidadAfectada;
    }

    @Override
    public String toString() {
        return "Incidencia{" +
                "idIncidencia='" + idIncidencia + '\'' +
                ", tipo=" + tipo +
                ", descripcion='" + descripcion + '\'' +
                ", fecha=" + fecha +
                ", tipoEntidadAfectada=" + tipoEntidadAfectada +
                ", idEntidadAfectada='" + idEntidadAfectada + '\'' +
                '}';
    }
}
