package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.Persona;
import co.edu.uniquindio.eventos.pfeventosp2.Model.PlataformaEventos;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Usuario;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.UserRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UsuariosController {

    @FXML private TextField buscarField;
    @FXML private TableView<Usuario> tblUsuarios; //Tipo Usuario, no Persona
    @FXML private TableColumn<Usuario, String> idC;
    @FXML private TableColumn<Usuario, String> nombreC;
    @FXML private TableColumn<Usuario, String> correoC;
    @FXML private TableColumn<Usuario, String> telefonoC;
    @FXML private TableColumn<Usuario, Double> gastosC;

    private ObservableList<Usuario> listaUsuariosObservable;
    private FilteredList<Usuario> listaUsuariosFiltrada;

    @FXML
    public void initialize() {
        //Vincular columnas con los atributos de Usuario/Persona
        PlataformaEventos.getInstancia().vincularComprasAUsuarios();
        idC.setCellValueFactory(new PropertyValueFactory<>("id"));
        nombreC.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        correoC.setCellValueFactory(new PropertyValueFactory<>("correo"));
        telefonoC.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        gastosC.setCellValueFactory(new PropertyValueFactory<>("totalGastado"));

        //Cargar datos y filtrar solo los de tipo Usuario
        List<Persona> todasLasPersonas = UserRepository.getInstance().getPersonas();
        List<Usuario> soloClientes = new ArrayList<>();

        for (Persona p : todasLasPersonas) {
            if (p instanceof Usuario) {
                soloClientes.add((Usuario) p);
            }
        }

        //Configurar listas observables para el buscador
        listaUsuariosObservable = FXCollections.observableArrayList(soloClientes);
        listaUsuariosFiltrada = new FilteredList<>(listaUsuariosObservable, p -> true);
        tblUsuarios.setItems(listaUsuariosFiltrada);

        //Configurar el Buscador (Filtra por nombre o correo)
        buscarField.textProperty().addListener((observable, oldValue, newValue) -> {
            listaUsuariosFiltrada.setPredicate(usuario -> {
                if (newValue == null || newValue.isEmpty()) return true;

                String filtro = newValue.toLowerCase();
                if (usuario.getNombre().toLowerCase().contains(filtro)) return true;
                if (usuario.getCorreo().toLowerCase().contains(filtro)) return true;

                return false;
            });
        });
    }

    @FXML
    void onNuevo(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/co/edu/uniquindio/eventos/pfeventosp2/AdminEscenas/NuevoUsuario.fxml"));
            javafx.scene.Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Registrar Nuevo Usuario");
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stage.initOwner(tblUsuarios.getScene().getWindow());
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();

            // Refrescar la tabla al cerrar
            listaUsuariosObservable.setAll(obtenerClientesActualizados());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Método auxiliar para obtener lista fresca
    private List<Usuario> obtenerClientesActualizados() {
        List<Usuario> lista = new ArrayList<>();
        for (Persona p : UserRepository.getInstance().getPersonas()) {
            if (p instanceof Usuario) lista.add((Usuario) p);
        }
        return lista;
    }

    @FXML
    void onEditar(ActionEvent event) {
        Usuario seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Selecciona un usuario para editar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/co/edu/uniquindio/eventos/pfeventosp2/AdminEscenas/EditarUsuario.fxml"));
            javafx.scene.Parent root = loader.load();

            EditarUsuarioController controller = loader.getController();
            controller.setUsuarioData(seleccionado);

            Stage stage = new Stage();
            stage.setTitle("Editar Usuario");
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stage.initOwner(tblUsuarios.getScene().getWindow());
            stage.setScene(new javafx.scene.Scene(root));
            stage.showAndWait();

            //Refrescar tabla tras cerrar
            tblUsuarios.refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onEliminar(ActionEvent event) {
        Usuario seleccionado = tblUsuarios.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Atención", "Por favor, selecciona un usuario para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar Eliminación");
        alerta.setHeaderText("¡Atención! Acción permanente.");
        alerta.setContentText("¿Estás seguro de que deseas eliminar a " + seleccionado.getNombre() + "?\nSe perderá su historial de compras.");

        Optional<ButtonType> resultado = alerta.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            //Remover de la lista general del repositorio
            UserRepository.getInstance().getPersonas().remove(seleccionado);

            //Actualizar el txt
            UserRepository.getInstance().updateArchive();

            //Remover de la tabla visual
            listaUsuariosObservable.remove(seleccionado);

            mostrarAlerta("Éxito", "El usuario ha sido eliminado del sistema.", Alert.AlertType.INFORMATION);
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