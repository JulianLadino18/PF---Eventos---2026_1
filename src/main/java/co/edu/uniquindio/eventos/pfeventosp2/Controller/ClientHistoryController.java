package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.*;
import co.edu.uniquindio.eventos.pfeventosp2.HelloApplication;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.CompraRepository;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.UserRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ClientHistoryController {

    @FXML private TableView<Compra> tablaCompras;
    @FXML private TableColumn<Compra, String> colId, colEvento, colEstado;
    @FXML private TableColumn<Compra, String> colTotal;
    @FXML private TableColumn<Compra, String> colFecha;

    @FXML private TextField txtBuscarEvento;
    @FXML private DatePicker datePickerFiltro;
    @FXML private ComboBox<String> comboEstado;

    private ObservableList<Compra> listaCompleta;
    private FilteredList<Compra> listaFiltrada;
    private ClientDashboardController dashboard;

    @FXML
    public void initialize() {
        configurarColumnas();
        cargarDatos();
        configurarFiltros();
    }

    public void setDashboard(ClientDashboardController dashboard) {
        this.dashboard = dashboard;
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getIdCompra()));
        colEvento.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEvento().getNombre()));
        colFecha.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getFechaCreacion().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))));
        colEstado.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEstado().getClass().getSimpleName()));
        colTotal.setCellValueFactory(cellData -> {
            double total = cellData.getValue().getTotal();
            // convertir a String
            return new javafx.beans.property.SimpleStringProperty(String.format("$%.2f", total));
        });
    }

    private void cargarDatos() {
        Usuario user = (Usuario) HelloApplication.loggedUser;

        SistemaReservaFacade fachada = new SistemaReservaFacade();

        List<Compra> comprasUsuario = CompraRepository.getInstance().getListaCompras().stream()
                .filter(c -> c.getUsuario().getId().equals(user.getId()))
                .toList();

        listaCompleta = FXCollections.observableArrayList(comprasUsuario);
        listaFiltrada = new FilteredList<>(listaCompleta, p -> true);
        tablaCompras.setItems(listaFiltrada);

        comboEstado.getItems().addAll("EstadoCreada", "EstadoPagada", "EstadoCancelada");
    }

    private void configurarFiltros() {
        // Listener unificado para todos los filtros
        txtBuscarEvento.textProperty().addListener((obs, old, newVal) -> filtrar());
        datePickerFiltro.valueProperty().addListener((obs, old, newVal) -> filtrar());
        comboEstado.valueProperty().addListener((obs, old, newVal) -> filtrar());
    }

    private void filtrar() {
        listaFiltrada.setPredicate(compra -> {
            boolean coincideEvento = txtBuscarEvento.getText().isEmpty() ||
                    compra.getEvento().getNombre().toLowerCase().contains(txtBuscarEvento.getText().toLowerCase());

            boolean coincideFecha = datePickerFiltro.getValue() == null ||
                    compra.getFechaCreacion().toLocalDate().equals(datePickerFiltro.getValue());

            boolean coincideEstado = comboEstado.getValue() == null ||
                    compra.getEstado().getClass().getSimpleName().equals(comboEstado.getValue());

            return coincideEvento && coincideFecha && coincideEstado;
        });
    }

    @FXML
    private void onCancelarCompra() {
        Compra seleccionada = tablaCompras.getSelectionModel().getSelectedItem();

        if (seleccionada == null) {
            mostrarAlerta("Error", "Seleccione una compra de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        // Validación
        if (seleccionada.getEstado() instanceof EstadoCancelada) {
            mostrarAlerta("Info", "Esta compra ya está cancelada.", Alert.AlertType.INFORMATION);
            return;
        }

        double devolucion = seleccionada.getEvento().procesarReembolso(seleccionada.getTotal());

        // cambiar estado a estadoCancelada
        seleccionada.cancelar();

        // sobreescribir los datos
        try {
            CompraRepository.getInstance().actualizarArchivoCompras();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo actualizar el archivo de compras.", Alert.AlertType.ERROR);
            System.out.println(e.getMessage());
        }

        // Refrescar tabla visualmente
        tablaCompras.refresh();

        String devolucionFormateada = String.format("%,.2f", devolucion);

        mostrarAlerta("Procesado", "Compra cancelada.\n" +
                "Política: " + seleccionada.getEvento().getPolitica().obtenerDescripcionPolitica() + "\n" +
                "Total a devolver: $" + devolucionFormateada, Alert.AlertType.INFORMATION);
    }

    @FXML
    private void onExportarCSV() {
        // selector de archivos
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Historial como CSV");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos CSV (*.csv)", "*.csv"));
        fileChooser.setInitialFileName("historial_compras_" + ((Usuario)HelloApplication.loggedUser).getId() + ".csv");

        // Obtener la ventana actual para mostrar el diálogo encima
        File file = fileChooser.showSaveDialog(tablaCompras.getScene().getWindow());

        if (file != null) {
            // 2. Escribir el archivo forzando codificación UTF-8 para evitar problemas con tildes y ñ
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {

                pw.println("ID,Evento,Fecha,Estado,Total");

                //Recorrer lista filtrada actual
                for (Compra c : listaFiltrada) {
                    pw.println(
                            c.getIdCompra() + "," +
                                    c.getEvento().getNombre() + "," +
                                    c.getFechaCreacion().toString() + "," +
                                    c.getEstado().getClass().getSimpleName() + "," +
                                    String.format(java.util.Locale.US, "%.2f", c.getTotal())
                    );
                }

                mostrarAlerta("Éxito", "El historial se ha exportado correctamente a CSV.", Alert.AlertType.INFORMATION);

            } catch (IOException e) {
                mostrarAlerta("Error", "No se pudo exportar el archivo CSV: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onExportarPDF() {
        // selector de archivos
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Guardar Historial como PDF");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Archivos PDF (*.pdf)", "*.pdf"));
        fileChooser.setInitialFileName("historial_compras_" + ((Usuario)HelloApplication.loggedUser).getId() + ".pdf");

        // Obtener la ventana actual para mostrar el diálogo encima
        File file = fileChooser.showSaveDialog(tablaCompras.getScene().getWindow());

        if (file != null) {
            //Iniciar la creación del documento PDF
            Document documento = new Document();
            try {
                PdfWriter.getInstance(documento, new FileOutputStream(file));
                documento.open();

                // Título del PDF
                documento.add(new Paragraph("HISTORIAL DE COMPRAS - SISTEMA DE RESERVAS\n"));
                documento.add(new Paragraph("Cliente: " + HelloApplication.loggedUser.getNombre() + "\n"));
                documento.add(new Paragraph("Fecha de generación: " + java.time.LocalDate.now() + "\n\n"));

                // Crear una tabla en el PDF con 5 columnas
                PdfPTable tablaPdf = new PdfPTable(5);
                tablaPdf.setWidthPercentage(100); // Ocupar el ancho de la página

                // Encabezados de la tabla
                tablaPdf.addCell("ID Compra");
                tablaPdf.addCell("Evento");
                tablaPdf.addCell("Fecha");
                tablaPdf.addCell("Estado");
                tablaPdf.addCell("Total Pagar");

                // Recorrer la lista
                for (Compra c : listaFiltrada) {
                    tablaPdf.addCell(c.getIdCompra());
                    tablaPdf.addCell(c.getEvento().getNombre());
                    tablaPdf.addCell(c.getFechaCreacion().toString());
                    tablaPdf.addCell(c.getEstado().getClass().getSimpleName());
                    tablaPdf.addCell(String.format("$%.2f", c.getTotal()));
                }

                // Añadir la tabla al documento y cerrar
                documento.add(tablaPdf);
                documento.close();

                mostrarAlerta("Éxito", "El historial se ha exportado correctamente a PDF.", Alert.AlertType.INFORMATION);

            } catch (Exception e) {
                mostrarAlerta("Error", "No se pudo generar el archivo PDF: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void onLimpiarFiltros() {
        txtBuscarEvento.clear();
        datePickerFiltro.setValue(null);
        comboEstado.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String contenido, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}