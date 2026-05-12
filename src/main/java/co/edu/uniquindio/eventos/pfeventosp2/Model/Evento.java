package co.edu.uniquindio.eventos.pfeventosp2.Model;

import java.util.ArrayList;
import java.util.List;

public class Evento {
    //atributos
    private String idEvento;
    private String nombre;
    private String categoria;
    private String descripcion;
    private String ciudad;
    private String fecha;
    private String hora;
    private String estado; //pueden ser: Borrador, Publicado, Pausado, Cancelado, Finalizado

    //patrón strategy
    private PoliticaCancelacion politica;

    //patrón observer(los usuarios a notificar)
    private List<ObservadorEvento> clientesInscritos;

    //atributo para asociar un recinto a un evento
    private Recinto recinto;

    //constructor
    public Evento(String idEvento, String nombre, String categoria, String descripcion, String ciudad, String fecha, String hora, PoliticaCancelacion politica, Recinto recinto) {
        this.idEvento = idEvento;
        this.nombre = nombre;
        this.categoria = categoria;
        this.descripcion = descripcion;
        this.ciudad = ciudad;
        this.fecha = fecha;
        this.hora = hora;
        this.estado = "BORRADOR";
        this.politica = politica;
        this.recinto = recinto;
        this.clientesInscritos = new ArrayList<>();
    }


    //getters y setters
    public String getIdEvento() {
        return idEvento;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCategoria() {
        return categoria;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    public String getEstado() {
        return estado;
    }

    public PoliticaCancelacion getPolitica() {
        return politica;
    }

    public Recinto getRecinto() {
        return recinto;
    }

    public void setPolitica(PoliticaCancelacion nuevaPolitica) {
        this.politica = nuevaPolitica;
    }


    //métodos del patrón observer
    public void agregarObservador(ObservadorEvento cliente) {
        clientesInscritos.add(cliente);
    }

    public void eliminarObservador(ObservadorEvento cliente) {
        clientesInscritos.remove(cliente);
    }

    private void notificarClientes() {
        for (ObservadorEvento cliente : clientesInscritos) {
            cliente.notificarCambio(this.nombre, this.estado);
        }
    }

    public void cambiarEstado(String nuevoEstado) {
        this.estado = nuevoEstado;
        // Si el evento se cancela o pausa, se notificará a todos los usuarios automáticamente
        if (nuevoEstado.equals("CANCELADO") || nuevoEstado.equals("PAUSADO")) {
            notificarClientes();
        }
    }

    //métodos del patrón strategy
    public double procesarReembolso(double valorPagado) {
        return valorPagado * politica.calcularPorcentajeReembolso();
    }

    //consultar la disponibilidad del evento por zonas y asientos
    public void mostrarDisponibilidad() {
        System.out.println("--- Disponibilidad para el evento: " + nombre + " ---");
        System.out.println("Lugar: " + recinto.getNombre() + " (" + recinto.getDireccion() + ")");
        for (Zona zona : recinto.getZonas()) {
            System.out.println("Zona: " + zona.getNombre() +
                    " | Disponibles: " + zona.consultarDisponibles() +
                    " / " + zona.getCapacidad());
        }
    }

}
