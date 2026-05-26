package co.edu.uniquindio.eventos.pfeventosp2.Repository;

import co.edu.uniquindio.eventos.pfeventosp2.Model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EntradaRepository {
    private static final String rutaEntradas = "src/main/java/co/edu/uniquindio/eventos/pfeventosp2/Txt/entradas.txt";
    private List<Entrada> listaEntradas = new ArrayList<>();
    private static EntradaRepository instancia;

    //constructor privado para el patrón singleton
    private EntradaRepository() {
        this.listaEntradas = new ArrayList<>();
    }

    public static EntradaRepository getInstance() {
        if (instancia == null) {
            instancia = new EntradaRepository();
        }
        return instancia;
    }

    public void guardarEntrada(Entrada entrada) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaEntradas, true))) {
            //
            String idAsiento;
            if (entrada.getAsiento() != null) {
                //si es tipo VIP, o platea
                idAsiento = entrada.getAsiento().getIdAsiento();
            } else {
                //no tiene un asiento asignado porque es zona general
                idAsiento = "NULL";
            }
            String linea = entrada.getIdEntrada() + "@@@" +
                    entrada.getZona().getIdZona() + "@@@" +
                    idAsiento + "@@@" +
                    entrada.getEstado() + "@@@" +
                    entrada.getDescripcion();
            pw.println(linea);
            listaEntradas.add(entrada);
        }
    }

    public List<Entrada> cargarEntradas(List<Zona> zonasDisponibles) throws IOException {
        listaEntradas.clear();
        File file = new File(rutaEntradas);
        if (!file.exists()) return listaEntradas;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("@@@");

                Zona zona = buscarZona(datos[1], zonasDisponibles);
                Asiento asiento = null;
                if (!datos[2].equals("NULL")) {
                    asiento = buscarAsientoEnZona(datos[2], zona);
                }

                //se crea la base
                Entrada entrada = EntradaFactory.crearEntrada(datos[0], zona, asiento);

                //aquí se cargan los servicios que estarán en el espacio 4, los servicios están separados por "|"
                //ejemplo: "Parqueadero:20000|Souvenir:45000:Camiseta"
                String[] servicios = datos[4].split("\\|");

                for (String s : servicios) {
                    //los detalles de los servicios se separan por ":"
                    String[] info = s.split(":");
                    String nombreServicio = info[0];

                    if (nombreServicio.equals("Parqueadero")) {
                        double precio = Double.parseDouble(info[1]);
                        entrada = new ServicioParqueadero(entrada, precio);
                    }
                    else if (nombreServicio.equals("Alimentacion")) {
                        double precio = Double.parseDouble(info[1]);
                        String detalle = info[2];
                        entrada = new ServicioAlimentacion(entrada, precio, detalle);
                    }
                    else if (nombreServicio.equals("AccesoRapido")) {
                        double precio = Double.parseDouble(info[1]);
                        entrada = new ServicioAccesoRapido(entrada, precio);
                    }
                    else if (nombreServicio.equals("Souvenir")) {
                        double precio = Double.parseDouble(info[1]);
                        String detalle = info[2];
                        entrada = new ServicioSouvenir(entrada, precio, detalle);
                    }
                }
                listaEntradas.add(entrada);
            }
        }
        return listaEntradas;
    }

    private Zona buscarZona(String id, List<Zona> zonas) {
        for (Zona z : zonas){
            if (z.getIdZona().equals(id)) return z;
        }
        return null;
    }

    private Asiento buscarAsientoEnZona(String id, Zona zona) {
        if (zona == null || zona.getAsientos() == null) return null;
        for (Asiento a : zona.getAsientos()){
            if (a.getIdAsiento().equals(id)) return a;
        }
        return null;
    }
}
