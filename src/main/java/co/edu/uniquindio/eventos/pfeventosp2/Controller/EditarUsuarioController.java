package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.Usuario;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.UserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditarUsuarioController {

    @FXML private TextField idField, nombreField, correoField, telefonoField;
    @FXML private PasswordField passwordField;

    private Usuario usuarioEditable;

    public void setUsuarioData(Usuario usuario) {
        this.usuarioEditable = usuario;
        idField.setText(usuario.getId());
        nombreField.setText(usuario.getNombre());
        correoField.setText(usuario.getCorreo());
        telefonoField.setText(usuario.getTelefono());
        passwordField.setText(usuario.getPassword());
    }

    @FXML
    void onGuardar(ActionEvent event) {
        //Se actualizan los atributos del objeto en memoria
        usuarioEditable.setNombre(nombreField.getText());
        usuarioEditable.setCorreo(correoField.getText());
        usuarioEditable.setTelefono(telefonoField.getText());
        usuarioEditable.setPassword(passwordField.getText());

        //Guardar todo el archivo actualizado en el txt
        UserRepository.getInstance().updateArchive();

        mostrarAlerta("Éxito", "Cambios guardados correctamente.", Alert.AlertType.INFORMATION);
        cerrarVentana(event);
    }

    @FXML
    void onCancelar(ActionEvent event) { cerrarVentana(event); }

    private void cerrarVentana(ActionEvent event) {
        ((Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow()).close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}