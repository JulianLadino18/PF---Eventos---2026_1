package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.*;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.BloqueoRepository;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.CompraRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class DetalleAsientoController {

    @FXML
    private Label lblId, lblZona, lblEstado, lblPrecio;
    @FXML
    private VBox boxComprador;
    @FXML
    private Label lblNombreUsuario, lblIdUsuario;
    @FXML
    private Button btnBloquear, btnDesbloquear;
    private Evento eventoSeleccionado;
    private Zona zonaSeleccionada;
    private Asiento asientoSeleccionado;

    //Esto es para configurar la visibilidad inicial de los botones
    @FXML
    public void initialize() {
        //Inicialmente se ocultan todos
        btnBloquear.setVisible(false);
        btnDesbloquear.setVisible(false);
    }

    public void setDatos(Evento evento, Zona zona, Asiento asiento) {
        this.eventoSeleccionado = evento;
        this.zonaSeleccionada = zona;
        this.asientoSeleccionado = asiento;
        lblId.setText(asiento.getIdAsiento());
        lblZona.setText(zona.getNombre());
        lblEstado.setText(asiento.getEstado());
        lblPrecio.setText("$" + zona.getPrecioBase());

        //Si la silla no está disponible, se busca quién la tiene
        if (asiento.getEstado().equalsIgnoreCase("VENDIDO") || asiento.getEstado().equalsIgnoreCase("RESERVADO")) {
            buscarYMostrarComprador(evento, asiento);
        } else {
            boxComprador.setVisible(false);
        }

        //Lógica de visibilidad de botones
        if (asiento.getEstado().equalsIgnoreCase("DISPONIBLE")) {
            btnBloquear.setVisible(true);
        } else if (asiento.getEstado().equalsIgnoreCase("BLOQUEADO")) {
            btnDesbloquear.setVisible(true);
        }
    }

    private void buscarYMostrarComprador(Evento evento, Asiento asiento) {
        Compra compraEncontrada = null;

        //Buscar la compra
        for (Compra c : CompraRepository.getInstance().getListaCompras()) {
            if (c.getEvento().getIdEvento().equals(evento.getIdEvento())) {
                for (Entrada entrada : c.getItemsCompra()) {
                    if (entrada.getAsiento() != null &&
                            entrada.getAsiento().getIdAsiento().equals(asiento.getIdAsiento())) {
                        compraEncontrada = c;
                        break;
                    }
                }
            }
            if (compraEncontrada != null) break;
        }

        //Mostrar resultados
        if (compraEncontrada != null) {
            if (compraEncontrada.getUsuario() != null) {
                lblNombreUsuario.setText("Nombre: " + compraEncontrada.getUsuario().getNombre());
                lblIdUsuario.setText("ID: " + compraEncontrada.getUsuario().getId());
            } else {
                lblNombreUsuario.setText("Error: El usuario asociado a la compra es NULL.");
                lblIdUsuario.setText("ID Compra: " + compraEncontrada.getIdCompra());
            }
        } else {
            lblNombreUsuario.setText("Error: No se encontró la compra asociada.");
            lblIdUsuario.setText("");
        }
        boxComprador.setVisible(true);
    }

    @FXML
    void onBloquear(ActionEvent event) {
        try {
            //Llamar a la plataforma para que haga el trabajo completo
            PlataformaEventos.getInstancia().bloquearAsiento(eventoSeleccionado, zonaSeleccionada, asientoSeleccionado);
            lblEstado.setText("BLOQUEADO");
            btnBloquear.setVisible(false); //Ocultar el botón para evitar bloqueos dobles

            //Cerrar la ventana
            Stage stage = (Stage) btnBloquear.getScene().getWindow();
            stage.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error fatal al guardar el bloqueo: " + e.getMessage());
        }
    }

    @FXML
    void onCerrar(ActionEvent event) {
        ((Stage) lblId.getScene().getWindow()).close();
    }

    @FXML
    void onDesbloquear(ActionEvent event) {
        try {
            PlataformaEventos.getInstancia().desbloquearAsiento(zonaSeleccionada, asientoSeleccionado);
            //Cambiar texto a disponible
            lblEstado.setText("DISPONIBLE");
            //cambiar visibilidad de los botones
            btnDesbloquear.setVisible(false);
            btnBloquear.setVisible(true);

            //Cuando se desbloquee el asiento se cierra la ventana
            Stage stage = (Stage) btnDesbloquear.getScene().getWindow();
            stage.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al desbloquear: " + e.getMessage());
        }
    }
}