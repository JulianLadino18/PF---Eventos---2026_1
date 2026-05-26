package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.HelloApplication;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Persona;
import co.edu.uniquindio.eventos.pfeventosp2.Model.RoleUser;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.UserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class LoginController {

    @FXML
    private TextField txtMail;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnIngresar;

    private UserRepository userRepository;

    @FXML
    private AnchorPane rootPane;


    @FXML
    public void initialize() {
        userRepository = UserRepository.getInstance();
    }


    /**
     * Maneja el evento de inciar sesion
     */
    @FXML
    private void onGoClient(ActionEvent event) {
        if (!validarCampos()) {
            return;
        }

        String email = txtMail.getText().trim();
        String password = txtPassword.getText().trim();

        //Buscar el usuario
        Persona user = userRepository.login(email, password);

        if (user == null) {
            mostrarAlerta("Error", "Usuario o contraseña incorrectos", Alert.AlertType.ERROR);
            return;
        }

        //Guardar sesión global
        HelloApplication.loggedUser = user;

        String fxmlPath;
        RoleUser rol = user.getRole();

        switch (rol) {
            case ADMIN:
                fxmlPath = "/pfeventosp2/AdminEscenas/DashboardAdmin.fxml";
                break;

            case CLIENTE:
                fxmlPath = "/pfeventosp2/ClienteEscenas/DashboardClient.fxml";
                break;

            default:
                mostrarAlerta("Error", "Rol no definido para este usuario", Alert.AlertType.ERROR);
                return;
        }

        // Cargar la interfaz que le toca
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (rol == RoleUser.ADMIN) {
                AdminDashboardController controller = loader.getController();
                controller.setUser(user);
            } else {
                ClientDashboardController controller = loader.getController();
                controller.setUser(user);
            }

            //Mostrar la nueva ventana
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Dashboard de - " + rol);
            stage.show();

            //Cerrar el hermoso login
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar el Dashboard solicitado.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void onGoClientRegister(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pfeventosp2/ClienteEscenas/ClientRegistrer.fxml"));
            Parent root = loader.load();

            SingUpClientController controller = loader.getController();

            //Mostrar la nueva ventana
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Registrarme");
            stage.show();

            //Cerrar el hermoso login
            ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar el Register.", Alert.AlertType.ERROR);
        }

    }

    /**
     * Valida que los campos esten completos
     */
    private boolean validarCampos() {
        if (txtMail.getText().trim().isEmpty()) {
            mostrarAlerta("Error de validación", "El Correo es obligatorio", Alert.AlertType.WARNING);
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
