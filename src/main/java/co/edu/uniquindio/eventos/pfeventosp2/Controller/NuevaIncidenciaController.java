package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.*;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.IncidenciaRepository;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class NuevaIncidenciaController {

    @FXML private TextField idField, idEntidadField;
    @FXML private ComboBox<TipoIncidencia> tipoCombo;
    @FXML private ComboBox<TipoEntidad> entidadCombo;
    @FXML private TextArea descArea;

    @FXML
    public void initialize() {

        //Bloquear el id de la nueva incidencia
        idField.setText(generarNuevoId());
        idField.setEditable(false);

        // Llenar los ComboBox con los valores de los Enums
        tipoCombo.setItems(FXCollections.observableArrayList(TipoIncidencia.values()));
        entidadCombo.setItems(FXCollections.observableArrayList(TipoEntidad.values()));
    }

    //Generar un nuevo id según el anterior en la lista
    private String generarNuevoId() {
        try {
            List<Incidencia> lista = IncidenciaRepository.getInstance().cargarIncidencias();
            if (lista.isEmpty()) return "INC-001";

            int maxId = 0;
            for (Incidencia inc : lista) {
                try {
                    // Quitamos "INC-" y convertimos a número
                    String numeroString = inc.getIdIncidencia().replaceAll("[^0-9]", "");
                    int numero = Integer.parseInt(numeroString);
                    if (numero > maxId) maxId = numero;
                } catch (Exception e) { /* Ignorar errores de parseo */ }
            }
            return String.format("INC-%03d", maxId + 1);
        } catch (IOException e) {
            return "INC-001";
        }
    }

    @FXML
    void onGuardar() {
        //Validar que no haya campos vacíos
        if (esCampoVacio(idField) || tipoCombo.getValue() == null ||
                entidadCombo.getValue() == null || esCampoVacio(idEntidadField) ||
                descArea.getText().isEmpty()) {

            mostrarAlerta("Error de validación", "Todos los campos son obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        //Crear la incidencia
        Incidencia nueva = new Incidencia(
                idField.getText(),
                tipoCombo.getValue(),
                descArea.getText(),
                entidadCombo.getValue(),
                idEntidadField.getText()
        );

        //Guardar en el repositorio
        try {
            IncidenciaRepository.getInstance().guardarIncidencia(nueva);
            mostrarAlerta("Éxito", "Incidencia registrada correctamente.", Alert.AlertType.INFORMATION);
            cerrarVentana();
        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo guardar la incidencia: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onCancelar() {
        cerrarVentana();
    }

    private boolean esCampoVacio(TextField field) {
        return field.getText() == null || field.getText().trim().isEmpty();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) idField.getScene().getWindow();
        stage.close();
    }

    //Método para mostrar alerta simple
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}