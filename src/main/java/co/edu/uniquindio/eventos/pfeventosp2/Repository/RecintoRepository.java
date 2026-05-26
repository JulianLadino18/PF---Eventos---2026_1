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

    //Método para actualizar  el txt de los recintos si se agrega o se elimina un recinto
    public void actualizarRecintoEnArchivo(Recinto recintoActualizado) throws IOException {
        File file = new File(rutaRecintos);
        if (!file.exists()) return;
        List<String> lineasModificadas = new ArrayList<>();
        boolean encontrado = false;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("@@@");
                if (datos.length > 0 && datos[0].equals(recintoActualizado.getIdRecinto())) {
                    encontrado = true;
                    //Reconstruir la línea con los datos nuevos
                    String lineaNueva = recintoActualizado.getIdRecinto() + "@@@" +
                            recintoActualizado.getNombre() + "@@@" +
                            recintoActualizado.getDireccion() + "@@@" +
                            recintoActualizado.getCiudad();
                    lineasModificadas.add(lineaNueva);
                } else {
                    lineasModificadas.add(linea); //Conversevar los intactos
                }
            }
        }

        if (!encontrado) {
            throw new IOException("No se encontró el recinto con ID: " + recintoActualizado.getIdRecinto());
        }

        //Sobrescribir el archivo
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, false))) {
            for (String l : lineasModificadas) {
                pw.println(l);
            }
        }
    }

    //Método para eliminar un recinto y sus zonas asociadas de los archivos txt
    public void eliminarRecintoEnArchivo(String idRecinto) throws IOException {
        //Eliminar de recintos.txt
        File fileRecintos = new File(rutaRecintos);
        if (fileRecintos.exists()) {
            List<String> recintosRestantes = new ArrayList<>();
            boolean encontrado = false;

            try (BufferedReader br = new BufferedReader(new FileReader(fileRecintos))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    if (linea.trim().isEmpty()) continue;
                    String[] datos = linea.split("@@@");

                    if (datos.length > 0 && datos[0].equals(idRecinto)) {
                        encontrado = true; //Encuentra el recinto y lo salta
                    } else {
                        recintosRestantes.add(linea); //Se conservan los demás
                    }
                }
            }

            if (encontrado) {
                try (PrintWriter pw = new PrintWriter(new FileWriter(fileRecintos, false))) {
                    for (String l : recintosRestantes) {
                        pw.println(l);
                    }
                }
            }
        }

        //Eliminar de zonas.txt
        File fileZonas = new File(rutaZonas);
        if (fileZonas.exists()) {
            List<String> zonasRestantes = new ArrayList<>();

            try (BufferedReader br = new BufferedReader(new FileReader(fileZonas))) {
                String linea;
                while ((linea = br.readLine()) != null) {
                    if (linea.trim().isEmpty()) continue;
                    String[] datos = linea.split("@@@");

                    //En zonas.txt, el ID del recinto padre es el primer dato
                    if (datos.length > 0 && !datos[0].equals(idRecinto)) {
                        zonasRestantes.add(linea); //Solo conservamos las zonas que no sean de este recinto
                    }
                }
            }

            //Sobreescribir el archivo de zonas
            try (PrintWriter pw = new PrintWriter(new FileWriter(fileZonas, false))) {
                for (String l : zonasRestantes) {
                    pw.println(l);
                }
            }
        }
    }

    // Método para actualizar una zona en zonas.txt
    public void actualizarZonaEnArchivo(String idRecintoPadre, Zona zonaActualizada) throws IOException {
        File file = new File(rutaZonas);
        if (!file.exists()) return;

        List<String> lineasModificadas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("@@@");
                //Buscamos por el id de la Zona
                if (datos.length > 1 && datos[1].equals(zonaActualizada.getIdZona())) {
                    //Reconstruimos con los nuevos datos
                    String lineaNueva = idRecintoPadre + "@@@" + zonaActualizada.getIdZona() + "@@@" +
                            zonaActualizada.getNombre() + "@@@" + zonaActualizada.getCapacidad() + "@@@" +
                            zonaActualizada.getPrecioBase() + "@@@" + zonaActualizada.getAsientosPorFila();
                    lineasModificadas.add(lineaNueva);
                } else {
                    lineasModificadas.add(linea); //Conservar intactas
                }
            }
        }
        //Sobrescribir archivo
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, false))) {
            for (String l : lineasModificadas) { pw.println(l); }
        }
    }

    //Método para eliminar una zona específica de zonas.txt
    public void eliminarZonaEnArchivo(String idZona) throws IOException {
        File file = new File(rutaZonas);
        if (!file.exists()) return;

        List<String> lineasRestantes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("@@@");
                //Si el id no coincide, se guarda. Si coincide, se ignora para borrarla
                if (datos.length > 1 && !datos[1].equals(idZona)) {
                    lineasRestantes.add(linea);
                }
            }
        }
        //Sobrescribir archivo
        try (PrintWriter pw = new PrintWriter(new FileWriter(file, false))) {
            for (String l : lineasRestantes) { pw.println(l); }
        }
    }

    //Método para añadir una nueva zona en el txt
    public void guardarNuevaZona(String idRecintoPadre, Zona zona) throws IOException {
        try (java.io.PrintWriter pw = new java.io.PrintWriter(new java.io.FileWriter(rutaZonas, true))) {
            pw.println(idRecintoPadre + "@@@" + zona.getIdZona() + "@@@" +
                    zona.getNombre() + "@@@" + zona.getCapacidad() + "@@@" +
                    zona.getPrecioBase() + "@@@" + zona.getAsientosPorFila());
        }
    }
}
