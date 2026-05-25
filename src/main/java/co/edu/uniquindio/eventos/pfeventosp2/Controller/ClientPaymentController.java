package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.util.List;

public class ClientPaymentController {

    @FXML private Label lblResumen;
    @FXML private Label lblTotalPagar;
    @FXML private ComboBox<String> comboMetodoPago;

    @FXML private VBox vboxCamposDinamicos;
    @FXML private TextField txtCampo1, txtCampo2, txtCampo3;
    @FXML private CheckBox chkParqueadero, chkAlimentacion, chkFastPass, chkSouvenir;

    private Compra compraPendiente;
    private SistemaReservaFacade fachada;

    private double subtotalBase = 0.0;
    private boolean decoradoresYaAplicados = false; // Evita cobrar doble si un pago falla la primera vez

    @FXML
    public void initialize() {
        fachada = new SistemaReservaFacade();
        comboMetodoPago.getItems().addAll("Nequi", "DaviPlata", "PSE", "Tarjeta");

        comboMetodoPago.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> {
            if (newV != null) configurarCamposDinamicos(newV);
        });

        // Listeners que recalculan el precio cada vez que el usuario marca o desmarca un servicio
        chkParqueadero.selectedProperty().addListener((obs, oldV, newV) -> calcularTotalDinamico());
        chkAlimentacion.selectedProperty().addListener((obs, oldV, newV) -> calcularTotalDinamico());
        chkFastPass.selectedProperty().addListener((obs, oldV, newV) -> calcularTotalDinamico());
        chkSouvenir.selectedProperty().addListener((obs, oldV, newV) -> calcularTotalDinamico());
    }

    public void inicializarPago(Compra compra) {
        this.compraPendiente = compra;
        this.subtotalBase = compra.getTotal();

        lblResumen.setText("Evento: " + compra.getEvento().getNombre() + "\nEntradas seleccionadas: " + compra.getItemsCompra().size());
        lblTotalPagar.setText("$" + subtotalBase);
    }

    private void calcularTotalDinamico() {
        double nuevoTotal = subtotalBase;
        int cantidadEntradas = compraPendiente.getItemsCompra().size();

        if (chkParqueadero.isSelected()) nuevoTotal += 20000 * cantidadEntradas;
        if (chkAlimentacion.isSelected()) nuevoTotal += 35000 * cantidadEntradas;
        if (chkFastPass.isSelected()) nuevoTotal += 50000 * cantidadEntradas;
        if (chkSouvenir.isSelected()) nuevoTotal += 15000 * cantidadEntradas;

        lblTotalPagar.setText("$" + nuevoTotal);
    }

    /**
     * Aplica el patrón Decorator
     */
    private void envolverEntradasConDecoradores() {
        if (decoradoresYaAplicados) return; // Protección anti-cobros dobles

        List<Entrada> boletos = compraPendiente.getItemsCompra();
        double totalDecorado = 0.0;

        // Recorrer entradas y decorar
        for (int i = 0; i < boletos.size(); i++) {
            Entrada entradaFisica = boletos.get(i);

            if (chkParqueadero.isSelected())
                entradaFisica = new ServicioParqueadero(entradaFisica, 20000);

            if (chkAlimentacion.isSelected())
                entradaFisica = new ServicioAlimentacion(entradaFisica, 35000, "Combo Clásico");

            if (chkFastPass.isSelected())
                entradaFisica = new ServicioAccesoRapido(entradaFisica, 50000);

            if (chkSouvenir.isSelected())
                entradaFisica = new ServicioSouvenir(entradaFisica, 15000, "Camiseta del Evento");

            // Reemplazar entrada cmun por la decorada
            boletos.set(i, entradaFisica);
            totalDecorado += entradaFisica.getPrecioFinal(); // sumar nuevo precio
        }

        // Actualizar la compra
        compraPendiente.setTotal(totalDecorado);
        decoradoresYaAplicados = true;

        if (chkParqueadero.isSelected()) compraPendiente.getServiciosAdicionales().add("Parqueadero");
        if (chkAlimentacion.isSelected()) compraPendiente.getServiciosAdicionales().add("Alimentacion");
        if (chkFastPass.isSelected()) compraPendiente.getServiciosAdicionales().add("AccesoRapido");
        if (chkSouvenir.isSelected()) compraPendiente.getServiciosAdicionales().add("Souvenir");
    }

    @FXML
    private void onPagar() {
        String metodoSeleccionado = comboMetodoPago.getValue();

        // Validaciones
        if (metodoSeleccionado == null) {
            mostrarAlerta("Dato Requerido", "Por favor seleccione un método de pago.", Alert.AlertType.WARNING);
            return;
        }

        if (txtCampo1.isVisible() && txtCampo1.getText().isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Por favor llene los datos de pago.", Alert.AlertType.WARNING);
            return;
        }

        envolverEntradasConDecoradores();

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

        // Facade y State
        boolean exito = fachada.realizarCompra(compraPendiente, comprador, stringFormateadoParaAdapter);

        if (exito) {
            mostrarAlerta("Pago Exitoso", "El pago ha sido aprobado. ¡Disfruta tu evento!", Alert.AlertType.INFORMATION);
            volverAInicio();
        } else {
            mostrarAlerta("Transacción Declinada", "No se pudo procesar el pago. Intente de nuevo.", Alert.AlertType.ERROR);
            // si falla, se usa para no hacer un doble cobro
            decoradoresYaAplicados = false;
        }
    }


    private void configurarCamposDinamicos(String metodo) {
        vboxCamposDinamicos.setVisible(true);
        vboxCamposDinamicos.setManaged(true);

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
    private void onCancelar() {
        compraPendiente.cancelar();
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
            AnchorPane contenedorC = (AnchorPane) lblResumen.getScene().lookup("#contenedorC");
            if (contenedorC != null) {
                contenedorC.getChildren().clear();
                contenedorC.getChildren().add(vistaEventos);
                AnchorPane.setTopAnchor(vistaEventos, 0.0);
                AnchorPane.setBottomAnchor(vistaEventos, 0.0);
                AnchorPane.setLeftAnchor(vistaEventos, 0.0);
                AnchorPane.setRightAnchor(vistaEventos, 0.0);
            }
        } catch (IOException e) { e.printStackTrace(); }
    }
}