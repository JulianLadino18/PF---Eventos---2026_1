package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.Persona;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientDashboardController {
    @FXML
    private Button btnRegisterClient;
    @FXML
    private Button btnDepositsWithdrawals;
    @FXML
    private Button btnCheckBalance;
    @FXML
    private Button btnTransfers;
    @FXML
    private Button btnGenerateReport;
    @FXML
    private Button btnEmployeeManagement;
    @FXML
    private Button btnSecurityAuthentication;
    @FXML
    private AnchorPane contenido;

    private Persona loggedUser;

    public void setUser(Persona user) {
        this.loggedUser = user;
    }

    @FXML
    private AnchorPane contenedorC;

    @FXML
    private void onGoEvents() {
        FXMLLoader loader = cambiarVistaCentral("/pfeventosp2/ClienteEscenas/ClientEvents.fxml");

        if (loader != null) {
            ClientsEventsController envController = loader.getController();
            envController.setDashboard(this);
        }
    }

    @FXML
    private void onGoHistory() {
        FXMLLoader loader = cambiarVistaCentral("/pfeventosp2/ClienteEscenas/ClientHistory.fxml");

        if (loader != null) {
            ClientHistoryController hisController = loader.getController();
            hisController.setDashboard(this);
        }
    }

    @FXML
    private void onGoProfile() {
        cambiarVistaCentral("/pfeventosp2/ClienteEscenas/ClientProfile.fxml");
    }


    //Cierra el dashboard actual y abre el login
    @FXML
    private void onCloseDashboard(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pfeventosp2/Login.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Iniciar Sesión");
            stage.show();

            Stage dashboardStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            dashboardStage.close();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo regresar al Login.", Alert.AlertType.ERROR);
        }
    }

    //Metodo que usan todos las escenas para cambiar las vistas

    public FXMLLoader cambiarVistaCentral(String rutaFxml) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFxml));
            AnchorPane nuevaVista = loader.load();

            contenedorC.getChildren().clear();
            contenedorC.getChildren().add(nuevaVista);

            AnchorPane.setTopAnchor(nuevaVista, 0.0);
            AnchorPane.setBottomAnchor(nuevaVista, 0.0);
            AnchorPane.setLeftAnchor(nuevaVista, 0.0);
            AnchorPane.setRightAnchor(nuevaVista, 0.0);

            return loader;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Muestra una alerta al usuario
     */
    public void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

}
