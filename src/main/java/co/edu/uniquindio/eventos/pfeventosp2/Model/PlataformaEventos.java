package co.edu.uniquindio.eventos.pfeventosp2.Model;

import co.edu.uniquindio.eventos.pfeventosp2.Repository.EventoRepository;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.RecintoRepository;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.UserRepository;

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
        this.eventoRepo = new EventoRepository();
        this.recintoRepo = new RecintoRepository();

        //aquí se cargan los usuarios y los admins desde el repositorio
        this.listaPersonas = UserRepository.getInstance().getPersonas();

        //aquí se cargan los recintos y los eventos
        try {
            this.listaRecintos = recintoRepo.cargarRecintos();
            //se necesita la lista recintos para cargar los eventos, ya que se necesita el ID del recinto
            this.listaEventos = eventoRepo.cargarEventos(listaRecintos);
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
}
