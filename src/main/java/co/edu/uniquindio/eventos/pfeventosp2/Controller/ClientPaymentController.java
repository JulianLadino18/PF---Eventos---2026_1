package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class ClientPaymentController {

    @FXML private Label lblResumen;
    @FXML private Label lblTotalPagar;
    @FXML private ComboBox<String> comboMetodoPago;

    // Contenedor y campos dinámicos
    @FXML private VBox vboxCamposDinamicos;
    @FXML private TextField txtCampo1;
    @FXML private TextField txtCampo2;
    @FXML private TextField txtCampo3;

    private Compra compraPendiente;
    private SistemaReservaFacade fachada;

    @FXML
    public void initialize() {
        fachada = new SistemaReservaFacade();
        comboMetodoPago.getItems().addAll("Nequi", "DaviPlata", "PSE", "Tarjeta");

        // 🚨 Listener Mágico: Detecta cuando el usuario cambia la opción del ComboBox
        comboMetodoPago.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                configurarCamposDinamicos(newValue);
            }
        });
    }

    public void inicializarPago(Compra compra) {
        this.compraPendiente = compra;
        lblResumen.setText("Evento: " + compra.getEvento().getNombre() + "\nEntradas seleccionadas: " + compra.getItemsCompra().size());
        lblTotalPagar.setText("$" + compra.getTotal());
    }

    /**
     * Activa y nombra los textfields según el método de pago
     */
    private void configurarCamposDinamicos(String metodo) {
        // Hacemos visible el contenedor principal
        vboxCamposDinamicos.setVisible(true);
        vboxCamposDinamicos.setManaged(true);

        // Apagamos y limpiamos todos los campos primero
        txtCampo1.setVisible(false); txtCampo1.setManaged(false); txtCampo1.clear();
        txtCampo2.setVisible(false); txtCampo2.setManaged(false); txtCampo2.clear();
        txtCampo3.setVisible(false); txtCampo3.setManaged(false); txtCampo3.clear();

        switch (metodo) {
            case "Nequi":
            case "DaviPlata":
                txtCampo1.setPromptText("Número de Celular registrado");
                txtCampo1.setVisible(true); txtCampo1.setManaged(true);
                break;
            case "PSE":
                txtCampo1.setPromptText("Nombre del Banco (Ej: Bancolombia)");
                txtCampo1.setVisible(true); txtCampo1.setManaged(true);
                txtCampo2.setPromptText("Tipo de Persona (Natural/Jurídica)");
                txtCampo2.setVisible(true); txtCampo2.setManaged(true);
                txtCampo3.setPromptText("Número de Documento");
                txtCampo3.setVisible(true); txtCampo3.setManaged(true);
                break;
            case "Tarjeta":
                txtCampo1.setPromptText("Número de Tarjeta (16 dígitos)");
                txtCampo1.setVisible(true); txtCampo1.setManaged(true);
                txtCampo2.setPromptText("Código CVV (3 dígitos)");
                txtCampo2.setVisible(true); txtCampo2.setManaged(true);
                txtCampo3.setPromptText("Fecha Expiración (MM/AA)");
                txtCampo3.setVisible(true); txtCampo3.setManaged(true);
                break;
        }
    }

    @FXML
    private void onPagar() {
        String metodoSeleccionado = comboMetodoPago.getValue();

        if (metodoSeleccionado == null) {
            mostrarAlerta("Dato Requerido", "Por favor seleccione un método de pago de la lista.", Alert.AlertType.WARNING);
            return;
        }

        // Validación
        if (txtCampo1.isVisible() && txtCampo1.getText().isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Por favor llene los datos de pago.", Alert.AlertType.WARNING);
            return;
        }

        Usuario comprador = compraPendiente.getUsuario();

        String stringFormateadoParaAdapter = "";

        switch (metodoSeleccionado) {
            case "Nequi":
            case "DaviPlata":
                stringFormateadoParaAdapter = metodoSeleccionado + ":" + txtCampo1.getText();
                break;
            case "PSE":
                stringFormateadoParaAdapter = "PSE:" + txtCampo1.getText() + ":" + txtCampo2.getText() + ":" + txtCampo3.getText();
                break;
            case "Tarjeta":
                stringFormateadoParaAdapter = "Tarjeta:" + txtCampo1.getText() + ":" + txtCampo2.getText() + ":" + txtCampo3.getText();
                break;
        }


        boolean exito = fachada.realizarCompra(compraPendiente, comprador, stringFormateadoParaAdapter);

        if (exito) {

            mostrarAlerta("Pago Exitoso", "El pago ha sido aprobado. ¡Disfruta tu evento!", Alert.AlertType.INFORMATION);
            volverAInicio();
        } else {
            mostrarAlerta("Transacción Declinada", "No se pudo procesar el pago. Intente de nuevo.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onCancelar() {
        compraPendiente.cancelar(); // Patrón State
        mostrarAlerta("Compra Cancelada", "El proceso ha sido cancelado. No se generaron cargos.", Alert.AlertType.INFORMATION);
        volverAInicio();
    }


    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }

    private void volverAInicio() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pfeventosp2/ClienteEscenas/ClientEvents.fxml"));
            AnchorPane vistaEventos = loader.load();

            // Reemplazamos la vista actual en el contenedor principal
            AnchorPane contenedorC = (AnchorPane) lblResumen.getScene().lookup("#contenedorC");
            if (contenedorC != null) {
                contenedorC.getChildren().clear();
                contenedorC.getChildren().add(vistaEventos);

                AnchorPane.setTopAnchor(vistaEventos, 0.0);
                AnchorPane.setBottomAnchor(vistaEventos, 0.0);
                AnchorPane.setLeftAnchor(vistaEventos, 0.0);
                AnchorPane.setRightAnchor(vistaEventos, 0.0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}