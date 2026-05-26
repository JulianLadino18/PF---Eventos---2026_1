package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.PlataformaEventos;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Recinto;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class RecintosController {

    // --- Componentes FXML ---
    @FXML
    private TextField buscarField;

    @FXML
    private TableView<Recinto> tblRecintos;

    @FXML
    private TableColumn<Recinto, String> idC;

    @FXML
    private TableColumn<Recinto, String> nombreC;

    @FXML
    private TableColumn<Recinto, String> direccionC;

    @FXML
    private TableColumn<Recinto, String> ciudadC;

    @FXML
    private TableColumn<Recinto, Integer> capacidadC;

    @FXML
    private Button nuevoButton;

    @FXML
    private Button editarButton;

    @FXML
    private Button eliminarButton;

    @FXML
    private Button gestionarZonasButton;

    //Listas para el manejo de datos y búsqueda
    private ObservableList<Recinto> listaRecintosObservable;
    private FilteredList<Recinto> listaRecintosFiltrada;

    //Este método se ejecuta automáticamente al iniciar
    @FXML
    public void initialize() {
        //Configurar las columnas de la tabla vinculándolas a los atributos de la clase Recinto
        idC.setCellValueFactory(new PropertyValueFactory<>("idRecinto"));
        nombreC.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        direccionC.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        ciudadC.setCellValueFactory(new PropertyValueFactory<>("ciudad"));

        //JavaFX buscará automáticamente el método "getCapacidadTotal()" en la clase Recinto
        capacidadC.setCellValueFactory(new PropertyValueFactory<>("capacidadTotal"));

        //Cargar los datos desde el Singleton
        List<Recinto> recintosDePersistencia = PlataformaEventos.getInstancia().getListaRecintos();

        //Convertir a ObservableList y luego envolver en FilteredList para el buscador
        listaRecintosObservable = FXCollections.observableArrayList(recintosDePersistencia);
        listaRecintosFiltrada = new FilteredList<>(listaRecintosObservable, p -> true);

        //Asignar la lista filtrada a la tabla
        tblRecintos.setItems(listaRecintosFiltrada);

        //Configurar el Buscador (filtra por nombre o ciudad)
        buscarField.textProperty().addListener((observable, oldValue, newValue) -> {
            listaRecintosFiltrada.setPredicate(recinto -> {
                // Si el buscador está vacío, mostrar todos
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                //Convertir el texto a minúsculas para que la búsqueda no sea sensible a mayúsculas
                String filtroLowerCase = newValue.toLowerCase();

                //Buscar coincidencias en el nombre
                if (recinto.getNombre().toLowerCase().contains(filtroLowerCase)) {
                    return true;
                }

                //Buscar coincidencias en la ciudad
                if (recinto.getCiudad().toLowerCase().contains(filtroLowerCase)) {
                    return true;
                }

                //Si no coincide con nada, no lo mostramos
                return false;
            });
        });
    }

    //MÉTODOS
    //Método para el botón nuevo, envía a NuevoEvento
    @FXML
    void onNuevo(ActionEvent event) {
        try {
            //Cargar la vista
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/co/edu/uniquindio/eventos/pfeventosp2/AdminEscenas/NuevoRecinto.fxml"));
            Parent root = loader.load();

            //Configurar la ventana modal
            Stage stageModal = new Stage();
            stageModal.setTitle("Crear Nuevo Recinto");
            stageModal.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stageModal.initOwner(tblRecintos.getScene().getWindow());

            //Mostrar y esperar a que se cierre
            stageModal.setScene(new javafx.scene.Scene(root));
            stageModal.showAndWait();

            //Al cerrarse la ventana, pedimos la lista fresca del Singleton
            listaRecintosObservable.setAll(PlataformaEventos.getInstancia().getListaRecintos());

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la ventana de nuevo recinto: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    void onEditar(ActionEvent event) {
        Recinto seleccionado = tblRecintos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Atención", "Por favor, selecciona un recinto de la tabla para editar.", Alert.AlertType.WARNING);
            return;
        }

        try {
            //Cargar FXML
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/co/edu/uniquindio/eventos/pfeventosp2/AdminEscenas/EditarRecinto.fxml"));
            javafx.scene.Parent root = loader.load();

            //Pasar los datos al controller de editar recinto
            EditarRecintoController controllerEdicion = loader.getController();
            controllerEdicion.setRecintoData(seleccionado);

            //Configurar Ventana Modal
            Stage stageModal = new Stage();
            stageModal.setTitle("Editar Recinto: " + seleccionado.getNombre());
            stageModal.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stageModal.initOwner(tblRecintos.getScene().getWindow());

            //Mostrar y esperar a que el usuario termine
            stageModal.setScene(new javafx.scene.Scene(root));
            stageModal.showAndWait();

            //Refrescar tabla
            tblRecintos.refresh();

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la ventana de edición. " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    void onEliminar(ActionEvent event) {
        //Obtener el recinto seleccionado de la tabla
        Recinto seleccionado = tblRecintos.getSelectionModel().getSelectedItem();

        //Validar que se haya seleccionado algo
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Por favor, selecciona un recinto de la tabla para eliminar.", Alert.AlertType.WARNING);
            return;
        }

        //Crear alerta de confirmación
        Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        alertaConfirmacion.setTitle("Confirmar Eliminación");
        alertaConfirmacion.setHeaderText("¡Atención! Acción irreversible.");
        alertaConfirmacion.setContentText("¿Estás seguro de que deseas eliminar permanentemente el recinto '" + seleccionado.getNombre() + "'? \n\nEsto también eliminará TODAS las zonas asociadas a él.");

        //Mostrar alerta y esperar respuesta
        java.util.Optional<ButtonType> resultado = alertaConfirmacion.showAndWait();

        //Si el usuario acepta
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                //Llamar a la lógica central
                PlataformaEventos.getInstancia().eliminarRecinto(seleccionado);

                //Remover visualmente de la tabla
                listaRecintosObservable.remove(seleccionado);

                //Mostrar mensaje de éxito
                mostrarAlerta("Éxito", "El recinto y sus zonas han sido eliminados correctamente.", Alert.AlertType.INFORMATION);

            } catch (IOException e) {
                mostrarAlerta("Error", "No se pudo eliminar el recinto de los archivos: " + e.getMessage(), Alert.AlertType.ERROR);
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onGestionarZonas(ActionEvent event) {
        Recinto seleccionado = tblRecintos.getSelectionModel().getSelectedItem();

        if (seleccionado == null) {
            mostrarAlerta("Atención", "Por favor, selecciona un recinto de la tabla para gestionar sus zonas.", Alert.AlertType.WARNING);
            return;
        }

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/co/edu/uniquindio/eventos/pfeventosp2/AdminEscenas/GestionarZonas.fxml"));
            javafx.scene.Parent root = loader.load();

            //Pasamos el recinto seleccionado al nuevo controlador
            GestionarZonasController controller = loader.getController();
            controller.setRecintoData(seleccionado);

            Stage stageModal = new Stage();
            stageModal.setTitle("Zonas del Recinto");
            stageModal.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stageModal.initOwner(tblRecintos.getScene().getWindow());
            stageModal.setScene(new javafx.scene.Scene(root));

            stageModal.showAndWait();

            //Refrescamos la tabla por si se agregaron zonas
            tblRecintos.refresh();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir la ventana de zonas: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Método para alertas
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}