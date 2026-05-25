package co.edu.uniquindio.eventos.pfeventosp2.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

//Este repositorio es para guardar los asientos que se encuentran bloqueados ya que no se tiene un txt para todos los asientos
public class BloqueoRepository {
    private static final String rutaBloqueos = "src/main/java/co/edu/uniquindio/eventos/pfeventosp2/Txt/bloqueos.txt";
    private static BloqueoRepository instancia;

    //constructor
    private BloqueoRepository() {
    }

    //Singleton
    public static BloqueoRepository getInstance() {
        if (instancia == null) instancia = new BloqueoRepository();
        return instancia;
    }

    public void guardarBloqueo(String idAsiento) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaBloqueos, true))) {
            pw.println(idAsiento);
        }
    }

    public List<String> cargarBloqueos() throws IOException {
        List<String> bloqueados = new ArrayList<>();
        File file = new File(rutaBloqueos);
        if (!file.exists()) return bloqueados;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                bloqueados.add(linea.trim());
            }
        }
        return bloqueados;
    }

    //Método para eliminar el bloqueo de un asiento
    public void eliminarBloqueo(String idAsiento) throws IOException {
        List<String> bloqueos = cargarBloqueos();
        bloqueos.remove(idAsiento);
        //Reescribir el archivo
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaBloqueos))) {
            for (String id : bloqueos) {
                writer.write(id);
                writer.newLine();
            }
        }
    }
}