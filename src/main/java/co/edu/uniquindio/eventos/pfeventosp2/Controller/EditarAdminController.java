package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.Admin;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.UserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditarAdminController {

    @FXML private TextField idField, nombreField, correoField;
    @FXML private PasswordField passwordField;
    private Admin adminEditable;

    public void setAdminData(Admin admin) {
        this.adminEditable = admin;
        idField.setText(admin.getId());
        nombreField.setText(admin.getNombre());
        correoField.setText(admin.getCorreo());
        passwordField.setText(admin.getPassword()); //Se muestra la contraseña actual
    }

    @FXML
    void onGuardar(ActionEvent event) {
        //Validación de campos obligatorios
        if (nombreField.getText().isEmpty() || correoField.getText().isEmpty()) {
            mostrarAlerta("Error de Validación", "Por favor llene todos los campos obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        try {
            //Actualizar el objeto en memoria
            adminEditable.setNombre(nombreField.getText());
            adminEditable.setCorreo(correoField.getText());

            //Llamar a la persistencia para sobrescribir el archivo
            UserRepository.getInstance().updateArchive();

            //Notificar y cerrar
            mostrarAlerta("Éxito", "El administrador ha sido actualizado correctamente.", Alert.AlertType.INFORMATION);
            cerrarVentana(event);

        } catch (Exception e) {
            mostrarAlerta("Error de Guardado", "No se pudieron guardar los cambios: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML void onCancelar(ActionEvent event) {
        cerrarVentana(event);
    }
    private void cerrarVentana(ActionEvent event) {
        ((Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow()).close();
    }
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipoAlert) {
        Alert alerta = new Alert(tipoAlert);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}