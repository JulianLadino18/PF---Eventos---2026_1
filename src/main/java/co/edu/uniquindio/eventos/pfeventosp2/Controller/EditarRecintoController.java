package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.PlataformaEventos;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Recinto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class EditarRecintoController {

    @FXML private TextField ciudadField;
    @FXML private TextField direccionField;
    @FXML private TextField nombreField;
    @FXML private Text textId;

    //Guardará la referencia del recinto que estamos editando
    private Recinto recintoEditable;

    //Llena la interfaz y guarda la referencia en memoria.
    public void setRecintoData(Recinto seleccionado) {
        if (seleccionado == null) return;
        this.recintoEditable = seleccionado;
        textId.setText(seleccionado.getIdRecinto());
        nombreField.setText(seleccionado.getNombre());
        direccionField.setText(seleccionado.getDireccion());
        ciudadField.setText(seleccionado.getCiudad());
    }

    @FXML
    void onGuardar(ActionEvent event) {
        if (nombreField.getText().isEmpty() || direccionField.getText().isEmpty() || ciudadField.getText().isEmpty()) {
            mostrarAlerta("Error de Validación", "Por favor llene todos los campos obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        try {
            //Actualizar el objeto en memoria
            recintoEditable.setNombre(nombreField.getText());
            recintoEditable.setDireccion(direccionField.getText());
            recintoEditable.setCiudad(ciudadField.getText());

            //Llamar a la persistencia para que sobrescriba el archivo
            PlataformaEventos.getInstancia().actualizarRecintoEnPersistencia(recintoEditable);

            //Notificar y cerrar
            mostrarAlerta("Éxito", "El recinto ha sido actualizado correctamente.", Alert.AlertType.INFORMATION);
            cerrarVentana(event);

        } catch (IOException e) {
            mostrarAlerta("Error de Guardado", "No se pudieron guardar los cambios en el archivo: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    void onCancelar(ActionEvent event) {
        cerrarVentana(event);
    }

    private void cerrarVentana(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipoAlert) {
        Alert alerta = new Alert(tipoAlert);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}