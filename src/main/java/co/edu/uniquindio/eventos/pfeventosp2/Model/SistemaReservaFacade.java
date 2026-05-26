package co.edu.uniquindio.eventos.pfeventosp2.Model;

import co.edu.uniquindio.eventos.pfeventosp2.Repository.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SistemaReservaFacade {
    //atributos con los repositorios
    private UserRepository userRepository;
    private EventoRepository eventoRepository;
    private RecintoRepository recintoRepository;
    private EntradaRepository entradaRepository;
    private CompraRepository compraRepository;
    private IncidenciaRepository incidenciaRepository;

    //atributos con listas
    private List<Evento> eventosDisponibles;
    private List<Recinto> recintosDisponibles;
    private List<Entrada> entradasDisponibles;
    private List<Compra> comprasRealizadas;

    // ==========================================
    // 3. CONSTRUCTOR (Inicialización en Cascada)
    // ==========================================
    public SistemaReservaFacade() {
        //esto es para instanciar los repositorios
        this.userRepository = UserRepository.getInstance();
        this.eventoRepository = EventoRepository.getInstance();
        this.recintoRepository = RecintoRepository.getInstance();
        this.entradaRepository = EntradaRepository.getInstance();
        this.compraRepository = CompraRepository.getInstance();
        this.incidenciaRepository = IncidenciaRepository.getInstance();

        //aquí se cargan los datos en las listas
        this.eventosDisponibles = new ArrayList<>();
        this.recintosDisponibles = new ArrayList<>();
        this.entradasDisponibles = new ArrayList<>();
        this.comprasRealizadas = new ArrayList<>();
        cargarBaseDeDatos();
    }

    private void cargarBaseDeDatos() {
        try {
            //cargar recintos para los eventos
            this.recintosDisponibles = recintoRepository.cargarRecintos();

            //cargar eventos para las compras
            this.eventosDisponibles = eventoRepository.cargarEventos(recintosDisponibles);

            //aquí se recopilan las zonas de los recintos para cargar las  entradas
            List<Zona> todasLasZonas = new ArrayList<>();
            for (Recinto r : recintosDisponibles) {
                if (r.getZonas() != null) {
                    todasLasZonas.addAll(r.getZonas());
                }
            }
            this.entradasDisponibles = entradaRepository.cargarEntradas(todasLasZonas);

            //aquí se cargan las compras
            List<Usuario> clientes = obtenerSoloUsuarios();
            this.comprasRealizadas = compraRepository.cargarCompras(clientes, eventosDisponibles, entradasDisponibles);

        } catch (IOException e) {
            System.out.println("Error al sincronizar la base de datos TXT: " + e.getMessage());
        }
    }


    //Gestión de Usuarios
    public Persona iniciarSesion(String correo, String password) {
        return userRepository.login(correo, password);
    }

    public void registrarUsuario(Persona nuevaPersona) {
        userRepository.addPersona(nuevaPersona);
    }

    //Consultas
    public List<Evento> obtenerEventos() {
        return this.eventosDisponibles;
    }

    public List<Recinto> obtenerRecintos() {
        return this.recintosDisponibles;
    }

    public List<Compra> obtenerComprasRealizadas() {
        return this.comprasRealizadas;
    }

    //Métodos para procesar el pago
    //el método sirve para validar el pago y guarda la info en el repositorio
    // Dentro de SistemaReservaFacade.java

    public boolean realizarCompra(Compra nuevaCompra, Usuario comprador, String metodoPagoTexto) {
        IPagoAdapter adaptador = comprador.obtenerAdapterDesdeString(metodoPagoTexto);
        if (adaptador == null) return false;

        Pago transaccion = new Pago(nuevaCompra.getTotal(), adaptador);
        boolean pagoExitoso = transaccion.ejecutarPago();

        // Si el pago es exitoso
        if (pagoExitoso) {
            try {
                //  Actualiza el estado de la compra
                nuevaCompra.setEstado(new EstadoPagada());

                for (Entrada boleto : nuevaCompra.getItemsCompra()) {
                    entradaRepository.guardarEntrada(boleto);
                    this.entradasDisponibles.add(boleto);
                }

                // Guarda la compra en el txt (ahora sí encontrará los boletos al recargar)
                compraRepository.guardarCompra(nuevaCompra);

                //  Actualiza en las listas
                this.comprasRealizadas.add(nuevaCompra);

                return true;
            } catch (IOException e) {
                System.out.println("Error al guardar los datos de la compra: " + e.getMessage());
                return false;
            }
        }

        return false;
    }


    private List<Usuario> obtenerSoloUsuarios() {
        List<Usuario> listaUsuarios = new ArrayList<>();
        for (Persona p : userRepository.getPersonas()) {
            if (p instanceof Usuario) {
                listaUsuarios.add((Usuario) p);
            }
        }
        return listaUsuarios;
    }
}
