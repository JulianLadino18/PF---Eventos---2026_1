package co.edu.uniquindio.eventos.pfeventosp2.Repository;

import co.edu.uniquindio.eventos.pfeventosp2.Model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class EventoRepository {
    //atributos
    private static final String rutaEventos = "src/main/java/co/edu/uniquindio/eventos/pfeventosp2/Txt/eventos.txt";
    private static EventoRepository instancia;

    //constructor privado para el patrón singleton
    private EventoRepository() {
    }

    public static EventoRepository getInstance() {
        if (instancia == null) {
            instancia = new EventoRepository();
        }
        return instancia;
    }

    //método para guardar los eventos
    public void guardarEvento(Evento evento) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaEventos, true))) {
            //aquí también se guarda el ID del recinto y el nombre de la clase de la política
            //se usan los separadores "@@@" por si en el nombre del evento de usan ","
            String linea = evento.getIdEvento() + "@@@" +
                    evento.getNombre() + "@@@" +
                    evento.getCategoria() + "@@@" +
                    evento.getDescripcion() + "@@@" +
                    evento.getCiudad() + "@@@" +
                    evento.getFecha() + "@@@" +
                    evento.getHora() + "@@@" +
                    evento.getEstado() + "@@@" +
                    evento.getRecinto().getIdRecinto() + "@@@" +  // Solo el ID del recinto
                    evento.getPolitica().getClass().getSimpleName(); // El nombre de la estrategia
            pw.println(linea);
        }
    }

    //método para cargar los eventos del txt
    public List<Evento> cargarEventos(List<Recinto> recintosDisponibles) throws IOException {
        List<Evento> lista = new ArrayList<>();
        File file = new File(rutaEventos);
        if (!file.exists()) return lista;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue; // Ignora líneas vacías

                String[] datos = linea.split("@@@");

                // Buscar el objeto Recinto completo usando el ID guardado en la posición 8
                Recinto r = buscarRecinto(datos[8], recintosDisponibles);

                // Reconstruir estrategia de cancelación usando el nombre de la clase en la posición 9
                PoliticaCancelacion p = crearPolitica(datos[9]);

                // instanciar el objeto con todos los datos que dio el admin
                Evento e = new Evento(datos[0], datos[1], datos[2], datos[3], datos[4], datos[5], datos[6], p, r);
                e.cambiarEstado(datos[7]);

                lista.add(e);
            }
        }
        return lista;
    }

    private PoliticaCancelacion crearPolitica(String nombre) {
        if (nombre.equals("ReembolsoTotal")) return new ReembolsoTotal();
        return new SinReembolso(); // Por defecto
    }

    private Recinto buscarRecinto(String id, List<Recinto> recintos) {
        //se recorren todos los recintos disponibles para devolver los que coinciden con el ID del txt, si no se encuentra se devuelve null
        for (Recinto r : recintos) {
            if (r.getIdRecinto().equals(id)) {
                return r;
            }
        }
        return null;
    }
}
