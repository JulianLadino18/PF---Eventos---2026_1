package co.edu.uniquindio.eventos.pfeventosp2.Model;

import java.util.ArrayList;
import java.util.List;

public class Recinto {
    private String idRecinto;
    private String nombre;
    private String direccion;
    private String ciudad;
    //este atributo es para las zonas asociadas al recinto
    private List<Zona> zonas;

    //constructor
    public Recinto(String idRecinto, String nombre, String direccion, String ciudad) {
        this.idRecinto = idRecinto;
        this.nombre = nombre;
        this.direccion = direccion;
        this.ciudad = ciudad;
        this.zonas = new ArrayList<>();
    }

    //getters y setters
    public String getIdRecinto() {
        return idRecinto;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getCiudad() {
        return ciudad;
    }

    public List<Zona> getZonas() {
        return zonas;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setCiudad(String ciudad) {
        this.ciudad = ciudad;
    }

    //método para agregar zona
    public void agregarZona(Zona zona) {
        this.zonas.add(zona);
    }

    //método para eliminar una zona
    public void eliminarZona(Zona zona) {
        this.zonas.remove(zona);
    }

    //método para consultar la capacidad total sumando todas las zonas
    public int getCapacidadTotal() {
        int total = 0;
        for (Zona zona : zonas) {
            total += zona.getCapacidad();
        }
        return total;
    }
}
