package co.edu.uniquindio.eventos.pfeventosp2.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//patrón Builder
public class Compra {
    //atributos
    private String idCompra;
    private Usuario usuario;
    private Evento evento;
    private LocalDateTime fechaCreacion;
    private double total;
    private EstadoCompra estado;

    //listas de items y servicios adicionales
    //falta crear la clase Boleto
    private List<Entrada> itemsCompra;
    private List<String> serviciosAdicionales;

    //constructor
    private Compra(CompraBuilder builder) {
        this.idCompra = builder.idCompra;
        this.usuario = builder.usuario;
        this.evento = builder.evento;
        this.fechaCreacion = builder.fechaCreacion;
        this.itemsCompra = builder.itemsCompra;
        this.serviciosAdicionales = builder.serviciosAdicionales;
        this.total = builder.total;
        this.estado = new EstadoCreada();
    }

    //getters y setters

    public String getIdCompra() {
        return idCompra;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public Evento getEvento() {
        return evento;
    }

    public LocalDateTime getFechaCreacion() {
        return fechaCreacion;
    }

    public EstadoCompra getEstado() {
        return estado;
    }

    public void setEstado(EstadoCompra estado) {
        this.estado = estado;
    }

    public List<String> getServiciosAdicionales() {
        return serviciosAdicionales;
    }

    public double getTotal() {
        return total;
    }

    //método para pagar la entrada
    public void pagar() {
        estado.pagar(this);
    }

    //método para cancelar la entrada
    public void cancelar() {
        estado.cancelar(this);
    }

    public List<Entrada> getItemsCompra() {
        return itemsCompra;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    //métodos para modificar la compra solo si está creada
    public void agregarBoleto(Entrada boleto) {
        estado.agregarBoleto(this, boleto);
    }

    public void quitarBoleto(Entrada boleto) {
        estado.quitarBoleto(this, boleto);
    }

    //patrón BUILDER
    public static class CompraBuilder {
        private String idCompra;
        private Usuario usuario;
        private Evento evento;
        private LocalDateTime fechaCreacion;
        private double total = 0.0;
        //falta crear la clase boleto
        private List<Entrada> itemsCompra = new ArrayList<>();
        private List<String> serviciosAdicionales = new ArrayList<>();

        public CompraBuilder(String idCompra, Usuario usuario, Evento evento) {
            this.idCompra = idCompra;
            this.usuario = usuario;
            this.evento = evento;
            this.fechaCreacion = LocalDateTime.now();
        }

        public CompraBuilder agregarBoleto(Entrada boleto) {
            this.itemsCompra.add(boleto);
            this.total += boleto.getPrecioFinal();
            return this;
        }

        public CompraBuilder agregarServicio(String servicio, double precioServicio) {
            this.serviciosAdicionales.add(servicio);
            this.total += precioServicio;
            return this;
        }

        // Método final que construye el objeto
        public Compra build() {
            if (this.itemsCompra.isEmpty()) {
                System.out.println("Una compra debe tener al menos un boleto.");
            }
            return new Compra(this);
        }
    }
}
