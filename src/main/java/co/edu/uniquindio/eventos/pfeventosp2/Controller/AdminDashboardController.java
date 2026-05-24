package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class AdminDashboardController {

    @FXML
    private Button adminsButton;

    @FXML
    private Button cerrarSesionButton;

    @FXML
    private AnchorPane contenido;

    @FXML
    private Button dashboardButton;

    @FXML
    private Button eventosButton;

    @FXML
    private Button incidenciasButton;

    @FXML
    private VBox menu;

    @FXML
    private Button perfilButton;

    @FXML
    private Button recintosButton;

    @FXML
    private Button usuariosButton;

    @FXML
    void onAdmins(ActionEvent event) {
        cargarEscena("/co/edu/uniquindio/eventos/pfeventosp2/AdminEscenas/GestionarAdmins.fxml");
    }

    @FXML
    void onCerrarSesion(ActionEvent event) {

    }

    @FXML
    void onDashboard(ActionEvent event) {

    }

    @FXML
    void onEventos(ActionEvent event) {
        cargarEscena("/co/edu/uniquindio/eventos/pfeventosp2/AdminEscenas/Eventos.fxml");
    }

    @FXML
    void onIncidencias(ActionEvent event) {
        cargarEscena("/co/edu/uniquindio/eventos/pfeventosp2/AdminEscenas/Incidencias.fxml");
    }

    @FXML
    void onPerfil(ActionEvent event) {

    }

    @FXML
    void onRecintos(ActionEvent event) {
        cargarEscena("/co/edu/uniquindio/eventos/pfeventosp2/AdminEscenas/Recintos.fxml");
    }

    @FXML
    void onUsuarios(ActionEvent event) {
        cargarEscena("/co/edu/uniquindio/eventos/pfeventosp2/AdminEscenas/Usuarios.fxml");
    }


    //Método para cargar las escenas
    private void cargarEscena(String fxmlPath) {
        try {
            //Cargar el FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Node nuevaEscena = loader.load();

            //Limpiar el contenido actual y poner el nuevo
            contenido.getChildren().setAll(nuevaEscena);

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error al cargar la escena: " + fxmlPath);
        }
    }

}