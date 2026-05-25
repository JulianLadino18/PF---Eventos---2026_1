package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.*;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.CompraRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ClientEntranceController {

    @FXML private Label lblEventName;
    @FXML private Label lblEventPlace;
    @FXML private VBox vboxMapa;
    @FXML private VBox vboxCarrito;
    @FXML private Label lblSubtotal;
    @FXML private Button btnContinuar;

    private Evento eventoSeleccionado;

    // guardar lo que clickea el usuario
    private List<Entrada> carritoEntradas = new ArrayList<>();

    public void inicializarEvento(Evento evento) {
        this.eventoSeleccionado = evento;
        lblEventName.setText(evento.getNombre());
        lblEventPlace.setText(evento.getRecinto().getNombre() + " - " + evento.getCiudad());

        sincronizarEstadosAsientos();

        Mapa();
    }

    // Logica copiada del admin pa saber que esta vendido

    private void sincronizarEstadosAsientos() {
        // Por defecto todos disponibles
        for (Zona z : eventoSeleccionado.getRecinto().getZonas()) {
            if (z.getAsientos() != null) {
                for (Asiento a : z.getAsientos()) {
                    a.cambiarEstado("DISPONIBLE");
                }
            }
        }


        // Revisar las compras
        for (Compra c : CompraRepository.getInstance().getListaCompras()) {
            if (c.getEvento().getIdEvento().equals(eventoSeleccionado.getIdEvento()) && !(c.getEstado() instanceof EstadoCancelada)) {
                String estadoOcupacion = (c.getEstado() instanceof EstadoPagada) ? "VENDIDO" : "RESERVADO";
                for (Entrada e : c.getItemsCompra()) {
                    if (e.getAsiento() != null) {
                        for (Zona zonaMapa : eventoSeleccionado.getRecinto().getZonas()) {
                            if (zonaMapa.getAsientos() != null) {
                                for (Asiento asientoVisual : zonaMapa.getAsientos()) {
                                    if (asientoVisual.getIdAsiento().equals(e.getAsiento().getIdAsiento())) {
                                        asientoVisual.cambiarEstado(estadoOcupacion);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //mapa
    public void Mapa() {
        vboxMapa.getChildren().clear();

        if (eventoSeleccionado.getRecinto() == null || eventoSeleccionado.getRecinto().getZonas().isEmpty()) {
            vboxMapa.getChildren().add(new Label("Este evento no tiene un mapa de asientos configurado."));
            return;
        }

        // --- Escenario ---
        Label escenario = new Label("ESCENARIO");
        escenario.setPrefSize(400, 40);
        escenario.setAlignment(Pos.CENTER);
        escenario.setStyle("-fx-background-color: #d1d5db; -fx-border-color: #9ca3af; -fx-border-width: 2; -fx-font-weight: bold;");
        vboxMapa.getChildren().add(escenario);

        HBox contenedorZonasH = new HBox(20);
        contenedorZonasH.setAlignment(Pos.TOP_CENTER);

        for (Zona zona : eventoSeleccionado.getRecinto().getZonas()) {
            VBox zonaBox = new VBox(10);
            zonaBox.setAlignment(Pos.TOP_CENTER);
            zonaBox.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-padding: 10;");

            Label lblZona = new Label(zona.getNombre() + " ($" + zona.getPrecioBase() + ")");
            lblZona.setStyle("-fx-font-weight: bold;");

            GridPane gridAsientos = new GridPane();
            gridAsientos.setHgap(5); gridAsientos.setVgap(5);
            gridAsientos.setAlignment(Pos.CENTER);

            int columna = 0, filaGrid = 0;
            int maxColumnas = zona.getAsientosPorFila();

            for (Asiento asiento : zona.getAsientos()) {
                String[] partes = asiento.getIdAsiento().split("-");
                String textoSilla = (partes.length >= 3) ? partes[1] + partes[2] : "S";

                Button btnAsiento = new Button(textoSilla);
                btnAsiento.setPrefSize(35, 35);

                // Color inicial
                boton(btnAsiento, asiento.getEstado());

                // Acción de click
                btnAsiento.setOnAction(e -> clickAsiento(btnAsiento, zona, asiento));

                gridAsientos.add(btnAsiento, columna, filaGrid);

                columna++;
                if (columna >= maxColumnas) {
                    columna = 0; filaGrid++;
                }
            }
            zonaBox.getChildren().addAll(lblZona, gridAsientos);
            contenedorZonasH.getChildren().add(zonaBox);
        }
        vboxMapa.getChildren().add(contenedorZonasH);
    }

    private void boton(Button btn, String estado) {
        String base = "-fx-text-fill: white; -fx-font-size: 10px; -fx-cursor: hand; -fx-background-radius: 3; -fx-background-color: ";
        if (estado.equals("DISPONIBLE")) btn.setStyle(base + "#4caf50;"); // Verde
        else if (estado.equals("SELECCIONADO")) btn.setStyle(base + "#0009ff;"); // Azul Cliente
        else btn.setStyle(base + "#9e9e9e; -fx-cursor: default;"); // Gris (Vendido/Bloqueado)
    }

    // seleccionar para añadir al carro de compras
    private void clickAsiento(Button btn, Zona zona, Asiento asiento) {
        // Si no está disponible ni seleccionado, no hace nada (está vendido)
        if (!asiento.getEstado().equals("DISPONIBLE") && !asiento.getEstado().equals("SELECCIONADO")) {
            return;
        }

        if (asiento.getEstado().equals("DISPONIBLE")) {
            // lo selecciona
            asiento.cambiarEstado("SELECCIONADO");
            boton(btn, "SELECCIONADO");

            // fabricar entrada y meter en el carro
            String idTemporal = "TKT-" + System.currentTimeMillis();
            Entrada nuevaEntrada = EntradaFactory.crearEntrada(idTemporal, zona, asiento);
            carritoEntradas.add(nuevaEntrada);

        } else if (asiento.getEstado().equals("SELECCIONADO")) {
            // ya no lo selecciona
            asiento.cambiarEstado("DISPONIBLE");
            boton(btn, "DISPONIBLE");

            // se saca del carrito
            carritoEntradas.removeIf(e -> e.getAsiento().getIdAsiento().equals(asiento.getIdAsiento()));
        }

        actualizarCarro();
    }

    private void actualizarCarro() {
        vboxCarrito.getChildren().clear();
        double total = 0;

        for (Entrada e : carritoEntradas) {
            Label lblItem = new Label("• " + e.getZona().getNombre() + " (" + e.getAsiento().getIdAsiento() + ") - $" + e.getZona().getPrecioBase());
            lblItem.setStyle("-fx-font-size: 12px;");
            vboxCarrito.getChildren().add(lblItem);
            total += e.getZona().getPrecioBase();
        }

        lblSubtotal.setText("$" + total);
        btnContinuar.setDisable(carritoEntradas.isEmpty());
    }

    // contruir la compra ( builder )
    @FXML
    private void onContinuarCompra() {
        Usuario usuarioActual = (Usuario) co.edu.uniquindio.eventos.pfeventosp2.HelloApplication.loggedUser;
        String idTemporal = "C-" + System.currentTimeMillis();

        Compra.CompraBuilder builder = new Compra.CompraBuilder(idTemporal, usuarioActual, eventoSeleccionado);
        for (Entrada boleto : carritoEntradas) {
            builder.agregarBoleto(boleto);
        }
        Compra compraPendiente = builder.build();

        // Cambio de pantalla
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pfeventosp2/ClienteEscenas/ClientPayment.fxml"));
            AnchorPane vistaPago = loader.load();
            ClientPaymentController paymentController = loader.getController();
            paymentController.inicializarPago(compraPendiente);

            AnchorPane contenedorC = (AnchorPane) lblEventName.getScene().lookup("#contenedorC");
            contenedorC.getChildren().clear();
            contenedorC.getChildren().add(vistaPago);
            AnchorPane.setTopAnchor(vistaPago, 0.0); AnchorPane.setBottomAnchor(vistaPago, 0.0);
            AnchorPane.setLeftAnchor(vistaPago, 0.0); AnchorPane.setRightAnchor(vistaPago, 0.0);

        } catch (IOException e) { e.printStackTrace(); }
    }

    @FXML
    private void onCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pfeventosp2/ClienteEscenas/ClientEvents.fxml"));
            AnchorPane vistaEventos = loader.load();
            AnchorPane contenedorC = (AnchorPane) lblEventName.getScene().lookup("#contenedorC");
            contenedorC.getChildren().clear();
            contenedorC.getChildren().add(vistaEventos);
            AnchorPane.setTopAnchor(vistaEventos, 0.0); AnchorPane.setBottomAnchor(vistaEventos, 0.0);
            AnchorPane.setLeftAnchor(vistaEventos, 0.0); AnchorPane.setRightAnchor(vistaEventos, 0.0);
        } catch (IOException e) { e.printStackTrace(); }
    }
}