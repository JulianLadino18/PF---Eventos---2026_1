package co.edu.uniquindio.eventos.pfeventosp2.Repository;

import co.edu.uniquindio.eventos.pfeventosp2.Model.Recinto;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Zona;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class RecintoRepository {
    //rutas
    private static final String rutaRecintos = "src/main/java/co/edu/uniquindio/eventos/pfeventosp2/Txt/recintos.txt";
    //como las zonas pertenecen a recintos, entonces este repositorio es el encargado de guardar y cargar las zonas
    private static final String rutaZonas = "src/main/java/co/edu/uniquindio/eventos/pfeventosp2/Txt/zonas.txt";
    private static RecintoRepository instancia;

    //constructor privado para el patrón singleton
    private RecintoRepository() {
        // No hay listas que inicializar en esta clase
    }

    public static RecintoRepository getInstance() {
        if (instancia == null) {
            instancia = new RecintoRepository();
        }
        return instancia;
    }

    //método para guardar un recinto, se usan @@@ porque si el nombre de un evento tiene "," se romperá la lógica
    public void guardarRecinto(Recinto recinto) throws IOException {
        //aquí se guarda el recinto
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaRecintos, true))) {
            pw.println(recinto.getIdRecinto() + "@@@" + recinto.getNombre() + "@@@" +
                    recinto.getDireccion() + "@@@" + recinto.getCiudad());
        }
        //aquí se guardan las zonas del recinto
        guardarZonas(recinto.getIdRecinto(), recinto.getZonas());
    }

    //método para cargar todos los recintos y asignarles las zonas correspondientes
    public List<Recinto> cargarRecintos() throws IOException {
        List<Recinto> listaRecintos = new ArrayList<>();
        File archivoRecintos = new File(rutaRecintos);

        if (!archivoRecintos.exists()) return listaRecintos;

        //aquí se leen los recintos
        try (BufferedReader br = new BufferedReader(new FileReader(archivoRecintos))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("@@@");
                if (datos.length >= 4) {
                    Recinto recinto = new Recinto(
                            datos[0], //idRecinto
                            datos[1], //nombre
                            datos[2], //direccion
                            datos[3]  //ciudad
                    );
                    listaRecintos.add(recinto);
                }
            }
        }
        //aquí se cargan y se asignan las zonas a sus respectivos recintos
        cargarYAsignarZonas(listaRecintos);
        return listaRecintos;
    }

    //método para leer el archivo de las zonas para añadirlas a la lista de zonas del recinto padre
    private void cargarYAsignarZonas(List<Recinto> recintos) throws IOException {
        File archivoZonas = new File(rutaZonas);
        if (!archivoZonas.exists()) return;
        //aquí se leen las zonas
        try (BufferedReader br = new BufferedReader(new FileReader(archivoZonas))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("@@@");
                if (datos.length >= 5) {
                    String idRecintoPadre = datos[0];
                    //aquí se crea el objeto Zona
                    Zona zona = new Zona(
                            datos[1], //idZona
                            datos[2], //nombre
                            Integer.parseInt(datos[3]), //capacidad
                            Double.parseDouble(datos[4]), //precioBase
                            Integer.parseInt(datos[5])  //asientosPorFila
                    );
                    //aquí se busca el recinto y se le asigna una zona
                    for (Recinto r : recintos) {
                        if (r.getIdRecinto().equals(idRecintoPadre)) {
                            r.agregarZona(zona);
                            break;
                        }
                    }
                }
            }
        }
    }


    //método para guardar las zonas
    private void guardarZonas(String idRecinto, List<Zona> zonas) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaZonas, true))) {
            for (Zona zona : zonas) {
                pw.println(idRecinto + "@@@" + zona.getIdZona() + "@@@" +
                        zona.getNombre() + "@@@" + zona.getCapacidad() + "@@@" +
                        zona.getPrecioBase() + "@@@" + zona.getAsientosPorFila());
            }
        }
    }
}
