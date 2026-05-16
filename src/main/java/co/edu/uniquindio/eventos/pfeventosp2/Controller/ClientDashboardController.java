package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.Persona;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

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
}
