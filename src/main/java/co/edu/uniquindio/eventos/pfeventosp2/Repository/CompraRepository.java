package co.edu.uniquindio.eventos.pfeventosp2.Repository;

import co.edu.uniquindio.eventos.pfeventosp2.Model.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CompraRepository {
    //ruta del txt
    private static final String rutaCompras = "src/main/java/co/edu/uniquindio/eventos/pfeventosp2/Txt/compras.txt";
    private List<Compra> listaCompras = new ArrayList<>();
    private static CompraRepository instancia;

    //constructor privado para el patrón singleton
    private CompraRepository() {
        this.listaCompras = new ArrayList<>();
    }

    public static CompraRepository getInstance() {
        if (instancia == null) {
            instancia = new CompraRepository();
        }
        return instancia;
    }

    public void guardarCompra(Compra compra) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaCompras, true))) {
            //aqui se convierte la lista de entradas en un string separados por comas
            List<String> idsEntradas = new ArrayList<>();
            for (Entrada entrada : compra.getItemsCompra()) {
                idsEntradas.add(entrada.getIdEntrada());
            }
            String entradasString = idsEntradas.isEmpty() ? "Vacio" : String.join(",", idsEntradas);

            //aquí se convierten los servicios adicionales en un string
            String serviciosString = compra.getServiciosAdicionales().isEmpty() ? "Ninguno" : String.join(",", compra.getServiciosAdicionales());

            //se construye la linea con los separadores @@@
            String linea = compra.getIdCompra() + "@@@" +
                    compra.getUsuario().getId() + "@@@" +       // O .getIdentificacion() (según como esté en tu clase Usuario)
                    compra.getEvento().getIdEvento() + "@@@" +
                    compra.getFechaCreacion().toString() + "@@@" +
                    compra.getTotal() + "@@@" +
                    compra.getEstado().getClass().getSimpleName() + "@@@" + // Guarda "EstadoCreada" o "EstadoPagada"
                    entradasString + "@@@" +
                    serviciosString;
            pw.println(linea);
            listaCompras.add(compra); // Agregamos a la lista en memoria
        }
    }

    public List<Compra> cargarCompras(List<Usuario> usuariosDisponibles, List<Evento> eventosDisponibles, List<Entrada> entradasDisponibles) throws IOException {
        listaCompras.clear();
        File file = new File(rutaCompras);
        if (!file.exists()) return listaCompras;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] datos = linea.split("@@@");
                //aquí se construyen los objetos base
                Usuario u = buscarUsuario(datos[1], usuariosDisponibles);
                Evento e = buscarEvento(datos[2], eventosDisponibles);

                //aquí se usa el patron builder para construir la compra
                Compra.CompraBuilder builder = new Compra.CompraBuilder(datos[0], u, e);

                //aquí se agregan las entradas
                if (!datos[6].equals("Vacio")) {
                    String[] idsEntradas = datos[6].split(",");
                    for (String id : idsEntradas) {
                        Entrada entradaEncontrada = buscarEntrada(id, entradasDisponibles);
                        if (entradaEncontrada != null) {
                            builder.agregarBoleto(entradaEncontrada);
                        }
                    }
                }

                //aquí se reconstruyen los servicios
                if (!datos[7].equals("Ninguno")) {
                    String[] servicios = datos[7].split(",");
                    for (String servicio : servicios) {
                        builder.agregarServicio(servicio, 0.0);
                    }
                }

                //aquí se construye el objeto final
                Compra compraReconstruida = builder.build();
                compraReconstruida.setTotal(Double.parseDouble(datos[4]));
                //aquí se reconstruye el estado
                compraReconstruida.setEstado(reconstruirEstado(datos[5]));

                listaCompras.add(compraReconstruida);
            }
        }
        return listaCompras;
        /*listaCompras.clear();
        File archivo = new File(rutaCompras);

        System.out.println("--- CARGANDO COMPRAS DESDE TXT ---");
        if (!archivo.exists()) {
            System.out.println("No se encontró el archivo: " + archivo.getAbsolutePath());
            return listaCompras;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            int numLinea = 1;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty()) continue; // Salta líneas vacías

                try {
                    String[] datos = linea.split("@@@");
                    String idCompra = datos[0];
                    String idUsuario = datos[1];
                    String idEvento = datos[2];
                    // datos[3] es la fecha
                    double total = Double.parseDouble(datos[4]);
                    String estadoString = datos[5];
                    String entradasString = datos[6];
                    String serviciosString = datos[7];

                    // 1. Buscar Usuario y Evento
                    Usuario usuario = buscarUsuario(idUsuario, usuariosDisponibles);
                    Evento evento = buscarEvento(idEvento, eventosDisponibles);

                    if (usuario == null || evento == null) {
                        System.out.println("Línea " + numLinea + " ignorada: Usuario o Evento no encontrados en memoria.");
                        continue;
                    }

                    // 2. Construir la compra usando el Builder
                    Compra.CompraBuilder builder = new Compra.CompraBuilder(idCompra, usuario, evento);
                    Compra nuevaCompra = builder.build();
                    nuevaCompra.setTotal(total); // Forzamos el total guardado

                    // 3. Restaurar Entradas
                    if (!entradasString.equals("Vacio")) {
                        String[] idsEntradas = entradasString.split(",");
                        for (String idEnt : idsEntradas) {
                            Entrada ent = buscarEntrada(idEnt, entradasDisponibles);
                            if (ent != null) {
                                nuevaCompra.getItemsCompra().add(ent);
                            }
                        }
                    }

                    // 4. Restaurar Servicios
                    if (!serviciosString.equals("Ninguno")) {
                        String[] servicios = serviciosString.split(",");
                        for (String serv : servicios) {
                            nuevaCompra.getServiciosAdicionales().add(serv);
                        }
                    }

                    // 5. Restaurar Estado
                    if (estadoString.equals("EstadoPagada")) {
                        nuevaCompra.setEstado(new EstadoPagada());
                    } else if (estadoString.equals("EstadoCancelada")) {
                        nuevaCompra.setEstado(new EstadoCancelada());
                    } else {
                        nuevaCompra.setEstado(new EstadoCreada());
                    }

                    // 6. Agregar a la lista
                    listaCompras.add(nuevaCompra);
                    System.out.println("Compra " + idCompra + " cargada exitosamente.");

                } catch (Exception e) {
                    System.out.println("Error procesando la línea " + numLinea + ": " + e.getMessage());
                }
                numLinea++;
            }
        }
        System.out.println("Total de compras cargadas en memoria: " + listaCompras.size());
        return listaCompras;*/
    }

  //método para consultar las entradas por el id de una compra
    public List<Entrada> consultarEntradasPorIdCompra(String idCompra) {
        for (Compra c : listaCompras) {
            if (c.getIdCompra().equals(idCompra)) {
                return c.getItemsCompra();
            }
        }
        return new ArrayList<>();
    }

    //método para consultar las entradas por el evento elegido
    public List<Entrada> consultarEntradasPorEvento(String idEvento) {
        List<Entrada> entradasPorEvento = new ArrayList<>();
        for (Compra c : listaCompras) {
            if (c.getEvento().getIdEvento().equals(idEvento)) {
                entradasPorEvento.addAll(c.getItemsCompra());
            }
        }
        return entradasPorEvento;
    }

    //aquí se obtienen todas las compras
    public List<Compra> getListaCompras() {
        return listaCompras;
    }



    private Usuario buscarUsuario(String id, List<Usuario> usuarios) {
        for (Usuario u : usuarios) {
            //Comparamos si el ID es exacto o si el ID del usuario termina con el ID buscado
            //Esto soluciona que "CL2001" coincida con "2001"
            if (u.getId().equals(id) || u.getId().endsWith(id)) {
                return u;
            }
        }
        System.out.println("ERROR: No se encontró usuario con ID: " + id);
        return null;
    }

    private Evento buscarEvento(String id, List<Evento> eventos) {
        for (Evento e : eventos) {
            if (e.getIdEvento().equals(id)) return e;
        }
        return null;
    }

    private Entrada buscarEntrada(String id, List<Entrada> entradas) {
        for (Entrada ent : entradas) {
            if (ent.getIdEntrada().equals(id)) return ent;
        }
        return null;
    }

    private EstadoCompra reconstruirEstado(String nombreEstado) {
        if (nombreEstado.equals("EstadoPagada")) return new EstadoPagada();
        if (nombreEstado.equals("EstadoCancelada")) return new EstadoCancelada();
        return new EstadoCreada(); // Por defecto
    }

    public void actualizarArchivoCompras() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(rutaCompras))) {
            for (Compra c : listaCompras) {
                StringBuilder sb = new StringBuilder();

                //Construir la línea: ID@@@ID_Usuario@@@ID_Evento@@@Fecha@@@Total@@@Estado
                sb.append(c.getIdCompra()).append("@@@")
                        .append(c.getUsuario().getId()).append("@@@")
                        .append(c.getEvento().getIdEvento()).append("@@@")
                        .append(c.getFechaCreacion().toString()).append("@@@")
                        .append(c.getTotal()).append("@@@")
                        .append(c.getEstado().getClass().getSimpleName());

                //Agregar entradas
                sb.append("@@@");
                if (!c.getItemsCompra().isEmpty()) {
                    List<String> idEntradas = new ArrayList<>();
                    for (Entrada e : c.getItemsCompra()) {
                        idEntradas.add(e.getIdEntrada());
                    }
                    sb.append(String.join(",", idEntradas));
                } else {
                    sb.append("Vacio");
                }

                //Agregar servicios
                sb.append("@@@");
                if (c.getServiciosAdicionales() != null && !c.getServiciosAdicionales().isEmpty()) {
                    sb.append(String.join(",", c.getServiciosAdicionales()));
                } else {
                    sb.append("Ninguno");
                }

                writer.write(sb.toString());
                writer.newLine();
            }
        }
    }
}
