package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.HelloApplication;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Usuario;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class ClientProfileController {

    @FXML
    private TextField txtId;
    @FXML
    private TextField txtName;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtPassword;

    @FXML
    private Button btnEdit;
    @FXML
    private Button btnSave;
    @FXML
    private Button btnCancel;

    @FXML
    private void initialize(){
        cargaDatos();
        cambiarEstadoEdicion(false);
    }

    private void cargaDatos(){
        Usuario cliente = (Usuario) HelloApplication.loggedUser;
        txtName.setText(cliente.getNombre());
        txtPhone.setText(cliente.getTelefono());
        txtEmail.setText(cliente.getCorreo());
        txtPassword.setText(cliente.getPassword());
        txtId.setText(cliente.getId());
    }

    // metodo que activa y desactiva la edicion de los campos
    private void cambiarEstadoEdicion(boolean cambio) {
        // La cédula no se edita
        txtId.setEditable(false);

        txtName.setEditable(cambio);
        txtPhone.setEditable(cambio);
        txtEmail.setEditable(cambio);
        txtPassword.setEditable(cambio);

        // visibilidad de botones
        btnEdit.setVisible(!cambio);
        btnSave.setVisible(cambio);
        btnCancel.setVisible(cambio);
    }

    @FXML
    private void onEdit() {
        cambiarEstadoEdicion(true);
    }

    @FXML
    private void onCancel() {
        // cargar los datos viejos
        cargaDatos();
        cambiarEstadoEdicion(false);
    }

    @FXML
    private void onSave() {
        // Validar campos
        if (!validarCampos()) {
            return;
        }
        // cambiar datos
        Usuario cliente = (Usuario) HelloApplication.loggedUser;
        cliente.setNombre(txtName.getText().trim());
        cliente.setTelefono(txtPhone.getText().trim());
        cliente.setCorreo(txtEmail.getText().trim());
        cliente.setPassword(txtPassword.getText().trim());

        // guardar datos nuevos
        UserRepository.getInstance().updateArchive();

        mostrarAlerta("Éxito", "Perfil actualizado correctamente.", Alert.AlertType.INFORMATION);

        //bloquea todo otra vez
        cambiarEstadoEdicion(false);
    }

    private boolean validarCampos() {
        if (txtName.getText().trim().isEmpty()) {
            mostrarAlerta("Error de validación", "El nombre es obligatorio", Alert.AlertType.WARNING);
            return false;
        }
        if (txtPhone.getText().trim().isEmpty()) {
            mostrarAlerta("Error de validación", "El telefono es obligatorio", Alert.AlertType.WARNING);
            return false;
        }
        if (txtEmail.getText().trim().isEmpty()) {
            mostrarAlerta("Error de validación", "El correo es obligatorio", Alert.AlertType.WARNING);
            return false;
        }
        if (txtPassword.getText().trim().isEmpty()) {
            mostrarAlerta("Error de validación", "La Contraseña es obligatoria", Alert.AlertType.WARNING);
            return false;
        }
        return true;
    }

    /**
     * Muestra una alerta al usuario
     */
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
