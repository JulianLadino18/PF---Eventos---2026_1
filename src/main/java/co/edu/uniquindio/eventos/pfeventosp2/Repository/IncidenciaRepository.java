package co.edu.uniquindio.eventos.pfeventosp2.Repository;

import co.edu.uniquindio.eventos.pfeventosp2.Model.Incidencia;
import co.edu.uniquindio.eventos.pfeventosp2.Model.TipoEntidad;
import co.edu.uniquindio.eventos.pfeventosp2.Model.TipoIncidencia;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class IncidenciaRepository {
    private static final String rutaIncidencias = "src/main/java/co/edu/uniquindio/eventos/pfeventosp2/Txt/incidencias.txt";
    private static IncidenciaRepository instancia;

    //constructor para el patrón singleton
    private IncidenciaRepository() {
        // No hay listas que inicializar en esta clase
    }

    public static IncidenciaRepository getInstance() {
        if (instancia == null) {
            instancia = new IncidenciaRepository();
        }
        return instancia;
    }

    public void guardarIncidencia(Incidencia incidencia) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaIncidencias, true))) {
            // Guardamos todos los atributos separados por @@@
            String linea = incidencia.getIdIncidencia() + "@@@" +
                    incidencia.getTipo() + "@@@" +
                    incidencia.getDescripcion() + "@@@" +
                    incidencia.getFecha().toString() + "@@@" +
                    incidencia.getTipoEntidadAfectada() + "@@@" +
                    incidencia.getIdEntidadAfectada();
            pw.println(linea);
        }
    }

    //método para cargar todas las incidencias del txt reconstruyendo la fecha original
    public List<Incidencia> cargarIncidencias() throws IOException {
        List<Incidencia> lista = new ArrayList<>();
        File file = new File(rutaIncidencias);
        if (!file.exists()) {
            return lista;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("@@@");
                //se pasa la fecha guardada a string
                LocalDateTime fechaOriginal = LocalDateTime.parse(datos[3]);
                //aqupi se usa el segundo constructor para inyectar la fecha guardada y que no se use LocalDateTime.now()
                Incidencia inc = new Incidencia(
                        datos[0],                           //id
                        TipoIncidencia.valueOf(datos[1]),   //tipo (Enum)
                        datos[2],                           //descripción
                        fechaOriginal,                      //fecha del txt
                        TipoEntidad.valueOf(datos[4]),      //entidad afectada (Enum)
                        datos[5]                            //id entidad
                );
                lista.add(inc);
            }
        }
        return lista;
    }

   //método para consultar incidencias por rango de fechas
    public List<Incidencia> consultarPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) throws IOException {
        List<Incidencia> todas = cargarIncidencias();
        List<Incidencia> filtradas = new ArrayList<>();

        for (Incidencia inc : todas) {
            LocalDateTime fecha = inc.getFecha();
            // Verificamos si la fecha está dentro del rango (inclusive)
            if ((fecha.isAfter(fechaInicio) || fecha.isEqual(fechaInicio)) &&
                    (fecha.isBefore(fechaFin) || fecha.isEqual(fechaFin))) {
                filtradas.add(inc);
            }
        }
        return filtradas;
    }
}
