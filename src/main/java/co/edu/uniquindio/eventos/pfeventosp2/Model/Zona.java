package co.edu.uniquindio.eventos.pfeventosp2.Model;

import java.util.ArrayList;
import java.util.List;

public class Zona {
    //atributos
    private String idZona;
    private String nombre;
    private int capacidad;
    private double precioBase;
    private int asientosPorFila;
    private List<Asiento> asientos;

    //constructor
    public Zona(String idZona, String nombre, int capacidad, double precioBase, int asientosPorFila) {
        this.idZona = idZona;
        this.nombre = nombre;
        this.capacidad = capacidad;
        this.precioBase = precioBase;
        this.asientosPorFila = asientosPorFila;
        this.asientos = new ArrayList<>();
        generarAsientos();
    }

    //getters y setters
    public String getIdZona() {
        return idZona;
    }

    public String getNombre() {
        return nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public double getPrecioBase() {
        return precioBase;
    }

    public int getAsientosPorFila() {
        return asientosPorFila;
    }

    public List<Asiento> getAsientos() {
        return asientos;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }

    //Si cambian la capacidad o los asientos por fila, se regeneran los asientos
    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
        this.asientos.clear(); //Se limpian los viejos
        generarAsientos();     //Se genera la nueva cuadrícula
    }

    public void setAsientosPorFila(int asientosPorFila) {
        this.asientosPorFila = asientosPorFila;
        this.asientos.clear();
        generarAsientos();
    }


    //método para administrar asientos por zona
    public void agregarAsiento(Asiento asiento) {
        if (asientos.size() < capacidad) {
            this.asientos.add(asiento);
        } else {
            System.out.println("No se pueden agregar más asientos, capacidad máxima alcanzada.");
        }
    }

    //método para generar los asientos por zona
    private void generarAsientos() {
        char letraFila = 'A';
        int numeroEnFila = 1;

        for (int i = 1; i <= capacidad; i++) {
            String idAsiento = this.nombre.substring(0, Math.min(3, this.nombre.length())).toUpperCase() + "-" + letraFila + "-" + numeroEnFila;
            asientos.add(new Asiento(idAsiento, String.valueOf(letraFila), numeroEnFila));
            numeroEnFila++;
            if (numeroEnFila > asientosPorFila) {
                numeroEnFila = 1;
                letraFila++;
            }
        }

    }
    //metodo para consultar la ocupación por zona
    public int consultarOcupacion() {
        int ocupados = 0;
        for (Asiento asiento : asientos) {
            if (asiento.getEstado().equals("Vendido") || asiento.getEstado().equals("Reservado")) {
                ocupados++;
            }
        }
        return ocupados;
    }

    //método para consultar asientos disponibles por zona
    public int consultarDisponibles() {
        return capacidad - consultarOcupacion();
    }

}
