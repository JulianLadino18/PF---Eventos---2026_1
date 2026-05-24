package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.RoleUser;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Usuario;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.UserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class SingUpClientController {

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPassword;

    @FXML
    private TextField txtPhone;

    private UserRepository userRepository;

    @FXML
    public void initialize() {
        userRepository = UserRepository.getInstance();
    }

    /**
     * Maneja el evento de Registrarse
     */
    @FXML
    private void onGoClientRegister(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        String email = txtEmail.getText().trim();
        String password = txtPassword.getText().trim();
        String phone = txtPhone.getText().trim();

        //Crear el nuevo usuario
        Usuario nuevoCliente = new Usuario(RoleUser.CLIENTE, id, name, email, password, phone);

        //Registrar el usuario nuevo
        userRepository.addPersona(nuevoCliente);

        // Cargar la interfaz que le toca
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pfeventosp2/Login.fxml"));
            Parent root = loader.load();

            LoginController controller = loader.getController();

            //Mostrar la nueva ventana
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Iniciar Sesion");
            stage.show();

            //Cerrar el hermoso register
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar el Login.", Alert.AlertType.ERROR);
        }
    }

    //Valida que los campos esten completos
    private boolean validarCampos() {
        if (txtId.getText().trim().isEmpty()) {
            mostrarAlerta("Error de validación", "La Cédula es obligatoria", Alert.AlertType.WARNING);
            return false;
        }
        if (txtName.getText().trim().isEmpty()) {
            mostrarAlerta("Error de validación", "El Nombre es obligatorio", Alert.AlertType.WARNING);
            return false;
        }
        if (txtEmail.getText().trim().isEmpty()) {
            mostrarAlerta("Error de validación", "El Correo es obligatorio", Alert.AlertType.WARNING);
            return false;
        }
        if (txtPassword.getText().trim().isEmpty()) {
            mostrarAlerta("Error de validación", "La Contraseña es obligatoria", Alert.AlertType.WARNING);
            return false;
        }
        if (txtPhone.getText().trim().isEmpty()) {
            mostrarAlerta("Error de validación", "El Telefono es obligatorio", Alert.AlertType.WARNING);
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
