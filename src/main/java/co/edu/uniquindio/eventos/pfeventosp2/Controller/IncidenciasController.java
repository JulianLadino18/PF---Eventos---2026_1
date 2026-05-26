package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.Incidencia;
import co.edu.uniquindio.eventos.pfeventosp2.Model.TipoIncidencia;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.IncidenciaRepository;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class IncidenciasController {

    @FXML
    private Button btnEliminar;
    @FXML
    private TableView<Incidencia> tblIncidencias;
    @FXML
    private TableColumn<Incidencia, String> idC, tipoC, entidadC, descC;
    @FXML
    private TableColumn<Incidencia, LocalDateTime> fechaC;
    @FXML
    private ComboBox<TipoIncidencia> tipoFiltro;
    @FXML
    private DatePicker fechaFiltro;

    private ObservableList<Incidencia> listaObservable;
    private FilteredList<Incidencia> listaFiltrada;

    @FXML
    public void initialize() {
        //Vincular columnas
        idC.setCellValueFactory(new PropertyValueFactory<>("idIncidencia"));
        tipoC.setCellValueFactory(new PropertyValueFactory<>("tipo"));
        entidadC.setCellValueFactory(new PropertyValueFactory<>("idEntidadAfectada"));
        descC.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        //Formato de Fecha en la tabla
        fechaC.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        fechaC.setCellFactory(column -> new TableCell<Incidencia, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.format(formatter));
            }
        });

        //Cargar datos
        cargarDatos();

        //Configurar filtros
        tipoFiltro.setItems(FXCollections.observableArrayList(TipoIncidencia.values()));

        //Listener para filtrar al instante
        tipoFiltro.setOnAction(e -> aplicarFiltros());
        fechaFiltro.setOnAction(e -> aplicarFiltros());
    }

    private void cargarDatos() {
        try {
            List<Incidencia> lista = IncidenciaRepository.getInstance().cargarIncidencias();
            listaObservable = FXCollections.observableArrayList(lista);
            listaFiltrada = new FilteredList<>(listaObservable, p -> true);
            tblIncidencias.setItems(listaFiltrada);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Lógica de filtros
    private void aplicarFiltros() {
        TipoIncidencia tipoSel = tipoFiltro.getValue();
        var fechaSel = fechaFiltro.getValue();

        listaFiltrada.setPredicate(inc -> {
            boolean coincideTipo = (tipoSel == null || inc.getTipo() == tipoSel);
            boolean coincideFecha = (fechaSel == null || inc.getFecha().toLocalDate().isEqual(fechaSel));
            return coincideTipo && coincideFecha;
        });
    }

    @FXML
    void onLimpiarFiltros() {
        tipoFiltro.getSelectionModel().clearSelection();
        fechaFiltro.setValue(null);
        listaFiltrada.setPredicate(p -> true);
    }

    @FXML
    void onNuevaIncidencia(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/eventos/pfeventosp2/AdminEscenas/NuevaIncidencia.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tblIncidencias.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            //Volver a leer el repositorio y actualizar despues de cerrar la ventana
            List<Incidencia> listaActualizada = IncidenciaRepository.getInstance().cargarIncidencias();
            listaObservable.setAll(listaActualizada);

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo actualizar la tabla.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onVerDetalle(ActionEvent event) {
        Incidencia seleccionado = tblIncidencias.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Atención", "Selecciona una incidencia de la tabla.", Alert.AlertType.WARNING);
            return;
        }

        //Construir el mensaje
        String mensajeDetalle = "ID: " + seleccionado.getIdIncidencia() + "\n" +
                "Tipo: " + seleccionado.getTipo() + "\n" +
                "Fecha: " + seleccionado.getFecha().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) + "\n" +
                "Entidad Afectada: " + seleccionado.getTipoEntidadAfectada() + " (" + seleccionado.getIdEntidadAfectada() + ")\n\n" +
                "Descripción:\n" + seleccionado.getDescripcion();

        //Crear la alerta, se hizo sin el mostrar mensaje para poder volver más grande la alerta
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalle de Incidencia");
        alert.setHeaderText(null);
        alert.setContentText(mensajeDetalle);
        alert.getDialogPane().setPrefWidth(800);
        alert.showAndWait();
    }

    @FXML
    void onEliminarIncidencia(ActionEvent event) {
        Incidencia seleccionado = tblIncidencias.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Por favor, selecciona una incidencia para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        //Confirmación de seguridad
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar Eliminación");
        confirmacion.setHeaderText("¿Estás seguro de eliminar esta incidencia?");
        confirmacion.setContentText("ID: " + seleccionado.getIdIncidencia());

        if (confirmacion.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                //Eliminar del archivo
                IncidenciaRepository.getInstance().eliminarIncidenciaEnArchivo(seleccionado.getIdIncidencia());

                //Refrescar la tabla
                cargarDatos();

                mostrarAlerta("Éxito", "Incidencia eliminada correctamente.", Alert.AlertType.INFORMATION);
            } catch (IOException e) {
                mostrarAlerta("Error", "No se pudo eliminar la incidencia: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }
    //Método para mostrar alertas
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}