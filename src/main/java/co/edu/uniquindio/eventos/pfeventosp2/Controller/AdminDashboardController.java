package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.HelloApplication;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Persona;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminDashboardController {
    private Persona loggedUser;
    @FXML
    private Text nombreText;

    public void setUser(Persona user) {
        this.loggedUser = user;
        //Actualizar texto de nombre admin por el nombre del admin loggeado
        if (nombreText != null) {
            nombreText.setText("Bienvenido, " + loggedUser.getNombre());
        }
    }

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Button btnPerfil;

    @FXML
    private AnchorPane contenido;

    @FXML
    private VBox menu;

    @FXML
    void onCerrarSesion(ActionEvent event) {
        try {
            //Al presionar cerrar sesión se tienen que borrar los datos de referencia al usuario loggeado
            this.loggedUser = null; //se borra la referencia de este controlador
            HelloApplication.loggedUser = null; //Se borra la variable global

            //Se vuelve a  cargar la ventana del login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pfeventosp2/Login.fxml"));
            Parent root = loader.load();

            //Mostrar la nueva ventana del login
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Inicio de Sesión");
            stage.setResizable(false);
            stage.show();

            //Cerrar la ventana actual que es ele dashboard
            Stage ventanaActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            ventanaActual.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al intentar volver a la pantalla de Login.");
        }
    }


    @FXML
    void onPerfil(ActionEvent event) {
        try {
            //Cargar la vista del perfil
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pfeventosp2/AdminEscenas/Perfil.fxml"));
            Node nuevaEscena = loader.load();

            //Extraer el controlador del perfil
            PerfilAdminController perfilController = loader.getController();

            //Pasar el usuario que acabamos de recibir en el Dashboard al controler de perfil
            perfilController.setDatosAdmin(this.loggedUser);

            //Mostramos la ventana dentro del AnchorPane de contenido
            contenido.getChildren().setAll(nuevaEscena);

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error al cargar la vista de Perfil.");
        }
    }

}
