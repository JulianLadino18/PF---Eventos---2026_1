package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.*;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.UserRepository;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;
import java.util.stream.Collectors;

public class GestionarAdminsController {

    @FXML private TableView<Admin> tblAdmins;
    @FXML private TableColumn<Admin, String> idC, nombreC, correoC, passC;

    @FXML
    public void initialize() {
        idC.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreC.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        correoC.setCellValueFactory(new PropertyValueFactory<>("correo"));

        cargarAdmins();
    }

    private void cargarAdmins() {
        var listaAdmins = UserRepository.getInstance().getPersonas().stream()
                .filter(p -> p instanceof Admin)
                .map(p -> (Admin) p)
                .collect(Collectors.toList());
        tblAdmins.setItems(FXCollections.observableArrayList(listaAdmins));
    }

    @FXML
    void onNuevo() {
        try {
            //Cargamos el archivo FXML de la nueva ventana
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pfeventosp2/AdminEscenas/NuevoAdmin.fxml"));
            Parent root = loader.load();

            //Creamos el Stage (la ventana nueva)
            Stage stage = new Stage();
            stage.setTitle("Registrar Nuevo Administrador");
            stage.initModality(Modality.WINDOW_MODAL);

            //Indicamos que esta ventana depende de la principal
            stage.initOwner(tblAdmins.getScene().getWindow());

            stage.setScene(new Scene(root));

            //Esperamos a que el usuario cierre la ventana de registro
            stage.showAndWait();

            //Refrescar tabla cuando se cierrte la ventana
            cargarAdmins();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de registro.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onEditar() {
        //Obtener el seleccionado
        Admin seleccionado = tblAdmins.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un administrador para editar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            //Cargar la vista de edición
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pfeventosp2/AdminEscenas/EditarAdmin.fxml"));
            Parent root = loader.load();

            //Pasar los datos al controlador de edición ANTES de mostrarlo
            EditarAdminController controller = loader.getController();
            controller.setAdminData(seleccionado);

            //Mostrar la ventana
            Stage stage = new Stage();
            stage.setTitle("Editar Administrador");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tblAdmins.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            //Refrescar la tabla al cerrar la ventana
            cargarAdmins();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir la ventana de edición.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onEliminar(ActionEvent event) {
        //Obtener el admin seleccionado en la tabla
        Admin seleccionado = tblAdmins.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Atención", "Por favor, selecciona un administrador para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        //Confirmación de seguridad
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar Eliminación");
        alerta.setHeaderText("¿Estás seguro de eliminar a " + seleccionado.getNombre() + "?");
        alerta.setContentText("Esta acción es permanente y no se puede deshacer.");

        if (alerta.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {

            //Eliminar de la lista en memoria
            UserRepository.getInstance().getPersonas().remove(seleccionado);

            //Actualizar el archivo txt
            UserRepository.getInstance().updateArchive();

            //Refrescar la tabla para mostrar los cambios
            cargarAdmins();

            mostrarAlerta("Éxito", "Administrador eliminado correctamente.", Alert.AlertType.INFORMATION);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
