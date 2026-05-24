package co.edu.uniquindio.eventos.pfeventosp2.Model;

import co.edu.uniquindio.eventos.pfeventosp2.Repository.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PlataformaEventos {
    //atributos
    //patrón singleton
    private static PlataformaEventos instancia;
    private Persona usuarioAutenticado;

    //listas
    private List<Persona> listaPersonas;
    private List<Evento> listaEventos;
    private List<Recinto> listaRecintos;

    //repositorios
    private EventoRepository eventoRepo;
    private RecintoRepository recintoRepo;


    //constructor
    private PlataformaEventos() {
        //aquí se inicializan los repositorios
        this.eventoRepo = EventoRepository.getInstance();
        this.recintoRepo = RecintoRepository.getInstance();

        //aquí se cargan los usuarios y los admins desde el repositorio
        this.listaPersonas = UserRepository.getInstance().getPersonas();

        //aquí se cargan los recintos y los eventos
        try {
            this.listaRecintos = recintoRepo.cargarRecintos();
            //se necesita la lista recintos para cargar los eventos, ya que se necesita el ID del recinto
            this.listaEventos = eventoRepo.cargarEventos(listaRecintos);

            //Cargar las compras para vincularlas a los usuarios
            //Necesitamos todas las listas para cargar las compras correctamente
            List<Usuario> usuarios = new ArrayList<>();
            for(Persona p : listaPersonas) {
                if(p instanceof Usuario) usuarios.add((Usuario) p);
            }

            //Extraer todas las zonas de todos los recintos
            List<Zona> todasLasZonas = new ArrayList<>();
            for (Recinto r : listaRecintos) {
                todasLasZonas.addAll(r.getZonas());
            }

            List<Entrada> todasLasEntradas = EntradaRepository.getInstance().cargarEntradas(todasLasZonas);
            System.out.println("DEBUG: Se cargaron " + todasLasEntradas.size() + " entradas desde el archivo.");
            for(Entrada e : todasLasEntradas) {
                System.out.println("   Entrada cargada: " + e.getIdEntrada());
            }
            //Cargamos las compras desde el txt pasando las listas y se vincula al usuario
            CompraRepository.getInstance().cargarCompras(usuarios, listaEventos, todasLasEntradas);
            vincularComprasAUsuarios();
        } catch (IOException e) {
            System.out.println("Error al cargar datos: " + e.getMessage());
            this.listaRecintos = new ArrayList<>();
            this.listaEventos = new ArrayList<>();
        }
    }

    //patron singleton
    public static PlataformaEventos getInstancia() {
        if (instancia == null) {
            instancia = new PlataformaEventos();
        }
        return instancia;
    }

    //getters
    public Persona getUsuarioAutenticado() {
        return usuarioAutenticado;
    }

    public List<Evento> getListaEventos() {
        return listaEventos;
    }

    public List<Recinto> getListaRecintos() {
        return listaRecintos;
    }

    public boolean esAdminLogueado() {
        return usuarioAutenticado instanceof Admin;
    }

    //método para el inicio de sesión usando el repositorio de los usuarios
    public boolean iniciarSesion(String correo, String contrasena) {
        Persona p = UserRepository.getInstance().login(correo, contrasena);
        if (p != null) {
            this.usuarioAutenticado = p;
            return true;
        }
        return false;
    }

    //método para cerrar sesion, por lo que el usuario autenticado se vuelve  null
    public void cerrarSesion() {
        this.usuarioAutenticado = null;
    }

    //método para agregar datos y guardar datos en el txt de los eventos
    public void registrarEvento(Evento evento) throws IOException {
        listaEventos.add(evento);
        eventoRepo.guardarEvento(evento);
    }

    //método para agregar datos y guardar datos en el txt de los recintos
    public void registrarRecinto(Recinto recinto) throws IOException {
        listaRecintos.add(recinto);
        recintoRepo.guardarRecinto(recinto);
    }

    //Método de lógica que actualiza el estado
    public void actualizarEstadoEventoEnPersistencia(Evento eventoSeleccionado, String nuevoEstado) throws IOException {
        eventoSeleccionado.cambiarEstado(nuevoEstado);
        //Llamar al repositorio para guardar el cambio en el TXT
        EventoRepository.getInstance().actualizarEvento(eventoSeleccionado);
    }

    //Método de lógica que actualiza el Evento
    public void actualizarEventoEnPersistencia(Evento eventoSeleccionado) throws IOException {
        //Llamar al repositorio para guardar el cambio en el TXT
        EventoRepository.getInstance().actualizarEvento(eventoSeleccionado);
    }

    public void eliminarEvento(Evento evento) throws IOException {
        //Eliminar de la lista en memoria
        listaEventos.remove(evento);
        //Eliminar del archivo de texto
        EventoRepository.getInstance().eliminarEventoEnArchivo(evento.getIdEvento());
    }

    //Método para actualizar un recinto
    public void actualizarRecintoEnPersistencia(Recinto recintoSeleccionado) throws IOException {
        //Llama al repositorio para guardar el cambio en el txt de recintos
        RecintoRepository.getInstance().actualizarRecintoEnArchivo(recintoSeleccionado);
    }

    // Método para eliminar un recinto completamente
    public void eliminarRecinto(Recinto recinto) throws IOException {
        //Eliminar de la lista en memoria
        listaRecintos.remove(recinto);
        //Eliminar de los archivos TXT
        RecintoRepository.getInstance().eliminarRecintoEnArchivo(recinto.getIdRecinto());
    }

    //Método para vincular las compras al usuario y poder obtener el total gastado
    public void vincularComprasAUsuarios() {
        List<Compra> todasLasCompras = CompraRepository.getInstance().getListaCompras();

        //Limpiamos el historial por si acaso ya estaban vinculadas
        for (Persona p : listaPersonas) {
            if (p instanceof Usuario) {
                ((Usuario) p).getHistorialCompras().clear();
            }
        }

        //Vinculamos cada compra al usuario correcto
        for (Compra c : todasLasCompras) {
            Usuario u = c.getUsuario();
            if (u != null) {
                u.agregarCompra(c);
            }
        }
    }

    public void bloquearAsiento(Evento evento, Zona zona, Asiento asiento) throws IOException {
        //Cambiamos el estado en el objeto en memoria
        asiento.setEstado("BLOQUEADO");
        //Guardar en el archivo de bloqueos
        BloqueoRepository.getInstance().guardarBloqueo(asiento.getIdAsiento());
    }
}
