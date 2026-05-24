package co.edu.uniquindio.eventos.pfeventosp2.Controller;
import co.edu.uniquindio.eventos.pfeventosp2.Model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ClientEntranceController {

        @FXML private Label lblEventName;
        @FXML private Label lblEventPlace;
        @FXML private VBox vboxZonas;
        @FXML private Label lblSubtotal;
        @FXML private Button btnContinuar;
        private ClientDashboardController dashboard;

        public void setDashboard(ClientDashboardController dashboard) {
            this.dashboard = dashboard;
        }


        private Evento eventoSeleccionado;
        private double totalCompra = 0.0;

        // Guarda qué Zona se eligió y cuántas entradas
        private Map<Zona, Integer> entradasSeleccionadas = new HashMap<>();

        //Pasa el evento que eligio el usuario
        public void inicializarEvento(Evento evento) {
            this.eventoSeleccionado = evento;

            lblEventName.setText(evento.getNombre());
            lblEventPlace.setText(evento.getRecinto().getNombre() + " - " + evento.getCiudad());

            cargarZonasDeCompra();
        }

        private void cargarZonasDeCompra() {
            vboxZonas.getChildren().clear();
            entradasSeleccionadas.clear();

            if (eventoSeleccionado.getRecinto() == null || eventoSeleccionado.getRecinto().getZonas() == null) {
                vboxZonas.getChildren().add(new Label("No hay zonas disponibles para este evento."));
                btnContinuar.setDisable(true);
                return;
            }

            // Recorremos las zonas y creamos un control para cada una
            for (Zona zona : eventoSeleccionado.getRecinto().getZonas()) {
                int disponibles = zona.consultarDisponibles();

                if (disponibles > 0) {
                    HBox filaZona = crearFilaZona(zona, disponibles);
                    vboxZonas.getChildren().add(filaZona);
                }
            }
            actualizarSubtotal();
        }

        private HBox crearFilaZona(Zona zona, int disponibles) {
            HBox hbox = new HBox(15);
            hbox.setAlignment(Pos.CENTER_LEFT);

            // Información de la zona
            Label lblInfo = new Label(zona.getNombre() + " - $" + zona.getPrecioBase() + " (Disp: " + disponibles + ")");
            lblInfo.setPrefWidth(280);

            // seleccionar la cantidad
            Spinner<Integer> spinnerCantidad = new Spinner<>();

            // Limita la compra al máximo disponible
            SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, disponibles, 0);
            spinnerCantidad.setValueFactory(valueFactory);
            spinnerCantidad.setPrefWidth(70);

            // Listener que se dispara cada vez que el usuario sube o baja la cantidad
            spinnerCantidad.valueProperty().addListener((obs, oldValue, newValue) -> {
                if (newValue > 0) {
                    entradasSeleccionadas.put(zona, newValue);
                } else {
                    entradasSeleccionadas.remove(zona); // Si vuelve a 0, se quita del carrito
                }
                actualizarSubtotal();
            });

            hbox.getChildren().addAll(lblInfo, spinnerCantidad);
            return hbox;
        }

        private void actualizarSubtotal() {
            totalCompra = 0.0;

            // Sumar el costo de cada zona seleccionada multiplicada por su cantidad
            for (Map.Entry<Zona, Integer> entry : entradasSeleccionadas.entrySet()) {
                totalCompra += entry.getKey().getPrecioBase() * entry.getValue();
            }

            lblSubtotal.setText("Subtotal: $" + String.format("%.2f", totalCompra));

            // El botón solo se habilita si el usuario ha seleccionado al menos 1 entrada
            btnContinuar.setDisable(totalCompra == 0);
        }

    @FXML
    private void onContinuarCompra() {
        if (entradasSeleccionadas.isEmpty()) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Atención");
            alerta.setHeaderText(null);
            alerta.setContentText("Debes seleccionar al menos una entrada para continuar.");
            alerta.showAndWait();
            return;
        }

        // construccion de compra con el builder
        Usuario usuarioActual = (Usuario) co.edu.uniquindio.eventos.pfeventosp2.HelloApplication.loggedUser;
        String idTemporal = "C-" + System.currentTimeMillis();
        Compra.CompraBuilder builder = new Compra.CompraBuilder(idTemporal, usuarioActual, eventoSeleccionado);

        for (Map.Entry<Zona, Integer> entry : entradasSeleccionadas.entrySet()) {
            Zona zonaSeleccionada = entry.getKey();
            int cantidadPedida = entry.getValue();

            for (int i = 0; i < cantidadPedida; i++) {
                String idBoletoTemporal = "TKT-" + System.currentTimeMillis() + "-" + i;
                Asiento asientoGenerico = null;
                Entrada boleto = EntradaFactory.crearEntrada(idBoletoTemporal, zonaSeleccionada, asientoGenerico);
                builder.agregarBoleto(boleto);
            }
        }

        Compra compraPendiente = builder.build();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pfeventosp2/ClienteEscenas/ClientPayment.fxml"));
            AnchorPane vistaPago = loader.load();

            // Extraemos el controlador del pago y le inyectamos nuestra compra
            ClientPaymentController paymentController = loader.getController();
            paymentController.inicializarPago(compraPendiente);

            // Conseguimos el contenedor central (#contenedorC) y reemplazamos el FXML
            AnchorPane contenedorC = (AnchorPane) lblEventName.getScene().lookup("#contenedorC");

            if (contenedorC != null) {
                contenedorC.getChildren().clear();
                contenedorC.getChildren().add(vistaPago);

                AnchorPane.setTopAnchor(vistaPago, 0.0);
                AnchorPane.setBottomAnchor(vistaPago, 0.0);
                AnchorPane.setLeftAnchor(vistaPago, 0.0);
                AnchorPane.setRightAnchor(vistaPago, 0.0);
            } else {
                System.out.println("Error: No se encontró el #contenedorC en la escena.");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar la vista de pagos.");
        }
    }
    @FXML
    private void onCancel() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pfeventosp2/ClienteEscenas/ClientEvents.fxml"));
            AnchorPane vistaEventos = loader.load();

            // Conseguimos el contenedor del padre directo
            AnchorPane contenedorC = (AnchorPane) lblEventName.getScene().lookup("#contenedorC");
            contenedorC.getChildren().clear();
            contenedorC.getChildren().add(vistaEventos);

            AnchorPane.setTopAnchor(vistaEventos, 0.0);
            AnchorPane.setBottomAnchor(vistaEventos, 0.0);
            AnchorPane.setLeftAnchor(vistaEventos, 0.0);
            AnchorPane.setRightAnchor(vistaEventos, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
