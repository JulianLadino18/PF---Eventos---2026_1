package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.Evento;
import co.edu.uniquindio.eventos.pfeventosp2.Model.PlataformaEventos;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import javafx.event.ActionEvent;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class EventosController {

    //--- Componentes FXML ---
    @FXML
    private Button aplicarButton;

    @FXML
    private TextField buscarField;

    //ComboBox en String para los estados
    @FXML
    private ComboBox<String> cambiarEstadoComboB;

    //ComboBox para filtrar por categoría
    @FXML
    private ComboBox<String> filtrarComboB;

    //--- Definición de la Tabla y Columnas con Tipos ---
    //La tabla contendrá objetos de tipo Evento
    @FXML
    private TableView<Evento> tblEventos;

    //Cada columna es para un tipo de dato específico de Evento
    @FXML
    private TableColumn<Evento, String> idC;
    @FXML
    private TableColumn<Evento, String> nombreC;
    @FXML
    private TableColumn<Evento, String> categoriaC;
    @FXML
    private TableColumn<Evento, String> descC;
    @FXML
    private TableColumn<Evento, String> estadoC;
    @FXML
    private TableColumn<Evento, String> fechaC;
    @FXML
    private TableColumn<Evento, String> horaC;
    @FXML
    private TableColumn<Evento, String> ciudadC;

    //Para el Recinto, se usa el nombre del Recinto correspondiente
    @FXML
    private TableColumn<Evento, String> recintoC;

    //Esta columna de la tabla tendrá un símbolo, la disponibilidad y el mapa del recinto se verá en otra escena
    @FXML
    private TableColumn<Evento, String> disponibilidadC;

    @FXML
    private Button editarButton;
    @FXML
    private Button eliminarButton;
    @FXML
    private Button mapaComboB;
    @FXML
    private Button nuevoButton;


    //Esta lista envolverá la lista maestra y aplicará los filtros de búsqueda
    private FilteredList<Evento> listaEventosFiltrada;
    //Lista observable que JavaFX usa para actualizar la vista automáticamente
    private ObservableList<Evento> listaEventosObservable;
    //Referencia al Singleton de datos
    private PlataformaEventos plataforma;

    //Este método se ejecuta automáticamente después de que se carga el archivo FXML.
    //Aquí se configura la tabla y cargamos los datos iniciales.
    @FXML
    public void initialize() {
        //Obtener la instancia del Singleton que ya cargó los datos del TXT
        plataforma = PlataformaEventos.getInstancia();

        //Configurar cómo se extraen los datos de la clase Evento para las columnas simples.
        idC.setCellValueFactory(new PropertyValueFactory<>("idEvento"));
        nombreC.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        categoriaC.setCellValueFactory(new PropertyValueFactory<>("categoria"));
        descC.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        estadoC.setCellValueFactory(new PropertyValueFactory<>("estado"));
        fechaC.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        horaC.setCellValueFactory(new PropertyValueFactory<>("hora"));
        ciudadC.setCellValueFactory(new PropertyValueFactory<>("ciudad"));

        //Configurar la columna Recinto buscando el qetter getNombreRecinto en la clase Evento
        recintoC.setCellValueFactory(new PropertyValueFactory<Evento, String>("nombreRecinto"));

        //Cargar los datos desde el Singleton.
        List<Evento> eventosDePersistencia = plataforma.getListaEventos();

        //Se convierte la lista normal a la lista observable que requiere JavaFX.
        listaEventosObservable = FXCollections.observableArrayList(eventosDePersistencia);

        //Ahora creamos el FilteredList envolviendo la lista observable ya inicializada.
        listaEventosFiltrada = new FilteredList<>(listaEventosObservable, p -> true);

        //Vinculamos ÚNICAMENTE la FilteredList a la tabla.
        tblEventos.setItems(listaEventosFiltrada);

        //Lógica para que el buscador de evento funcione
        buscarField.textProperty().addListener((observable, oldValue, newValue) -> {
            listaEventosFiltrada.setPredicate(evento -> {
                // Regla A: Si el campo de búsqueda está vacío, mostrar TODO
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                //Filtrar por nombre
                if (evento.getNombre().toLowerCase().contains(lowerCaseFilter)) {
                    return true; //Coincidencia encontrada solo en el nombre
                }
                // ----------------------------------------

                //Si no coincide con el nombre, no mostrar
                return false;
            });
        });

        //Configurar elementos adicionales de los comboBox
        cambiarEstadoComboB.setItems(FXCollections.observableArrayList(
                "Borrador", "Publicado", "Pausado", "Cancelado", "Finalizado"
        ));
        filtrarComboB.setItems(FXCollections.observableArrayList(
                "Todas", "Concierto", "Teatro", "Cultura", "Deporte", "Conferencia", "Festival", "Taller"
        ));

        cambiarEstadoComboB.setDisable(true);
        aplicarButton.setDisable(true);

        //Añadimos un listener a la tabla para detectar selecciones
        tblEventos.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            //'newValue' es el evento seleccionado, o null si se deselecciona
            if (newValue != null) {
                // Si hay selección, habilitamos los controles y seteamos el estado actual
                cambiarEstadoComboB.setDisable(false);
                aplicarButton.setDisable(false);
                cambiarEstadoComboB.setValue(newValue.getEstado()); //Muestra el estado actual
            } else {
                //Si no hay selección, deshabilitamos todo
                cambiarEstadoComboB.setDisable(true);
                aplicarButton.setDisable(true);
                cambiarEstadoComboB.setValue(null);
            }
        });

        // Listener para la barra de búsqueda (cuando cambia el texto)cite: [cite: 2]
        buscarField.textProperty().addListener((observable, oldValue, newValue) -> {
            aplicarFiltrosCombinados();
        });

        // Listener para el ComboBox de categoría (cuando cambia la selección)cite: [cite: 1, 2]
        filtrarComboB.valueProperty().addListener((observable, oldValue, newValue) -> {
            aplicarFiltrosCombinados();
        });
    }

    //Método centralizado que evalúa tanto el texto de búsqueda como la categoría seleccionada
    //En esta parte se usó IA para la construcción de los filtros :)
    private void aplicarFiltrosCombinados() {
        //Obtenemos los valores actuales de ambos filtros [cite: 2]
        String textoBusqueda = buscarField.getText();
        String categoriaSeleccionada = filtrarComboB.getValue();

        //Actualizamos la 'regla' (predicado) de la lista filtradacite: [cite: 2]
        listaEventosFiltrada.setPredicate(evento -> {

            // --- REGLA 1: Búsqueda por Texto (Nombre) ---
            //Si el campo de búsqueda está vacío, la Regla 1 se cumple (true)cite: [cite: 1]
            boolean cumpleTexto = true;
            if (textoBusqueda != null && !textoBusqueda.isEmpty()) {
                String lowerCaseFilter = textoBusqueda.toLowerCase();
                //Si el nombre contiene el texto, se cumple la Reglacite: [cite: 2]
                if (!evento.getNombre().toLowerCase().contains(lowerCaseFilter)) {
                    cumpleTexto = false; // No cumple Regla 1
                }
            }

            //--- REGLA 2: Filtrado por Categoría ---
            //Si no hay categoría o es "Todas", la Regla 2 se cumple (true)cite: [cite: 1, 2]
            boolean cumpleCategoria = true;
            if (categoriaSeleccionada != null && !categoriaSeleccionada.equals("Todas") && !categoriaSeleccionada.isEmpty()) {
                //Si la categoría del evento NO coincide con la seleccionada, no cumple Regla 2
                if (!evento.getCategoria().equals(categoriaSeleccionada)) {
                    cumpleCategoria = false; //No cumple Regla 2
                }
            }

            // --- RESULTADO: Retornar true solo si CUMPLE AMBAS REGLAS ---cite: [cite: 1, 2]
            return cumpleTexto && cumpleCategoria;
        });
    }

    //método para aplicar el  estado seleccionado en el comboBox
    @FXML
    void onAplicarEstado(ActionEvent event) {
        //Obtener el evento seleccionado en la tabla y el nuevo estado del combo box
        Evento seleccionado = tblEventos.getSelectionModel().getSelectedItem();
        String nuevoEstado = cambiarEstadoComboB.getValue();

        if (seleccionado != null && nuevoEstado != null) {
            try {
                //Llama a la lógica (Singleton) que a su vez llama al repositorio
                PlataformaEventos.getInstancia().actualizarEstadoEventoEnPersistencia(seleccionado, nuevoEstado);
                // Refrescar la tabla para ver el cambio visualmente
                tblEventos.refresh();
                // Mostrar mensaje de éxito
                mostrarAlerta("Éxito", "El estado del evento ha sido actualizado correctamente.");
            } catch (IOException e) {
                mostrarAlerta("Error", "No se pudo actualizar el estado en el archivo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    //Método para editar un evento seleccionado
    @FXML
    void onEditar(ActionEvent event) {
        //Obtener el ítem seleccionado actualmente en la tabla
        Evento seleccionado = tblEventos.getSelectionModel().getSelectedItem();

        //Verificar que algo esté seleccionadocite
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Por favor, selecciona un evento de la tabla para editar.");
            return;
        }

        //Lógica para abrir la nueva ventana modal de edición
        try {
            //Cargar el archivo FXML de la escena de Edición
            // AJUSTAR RUTA SI ES NECESARIO.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pfeventosp2/AdminEscenas/EditarEvento.fxml"));
            Parent root = loader.load(); //Carga la escena y genera el controlador
            //Obtener la instancia del controlador de Edición automáticamente
            EditarEventoController controllerEdicion = loader.getController();

            //PASAR LOS DATOS
            controllerEdicion.setEventoData(seleccionado);

            //Configurar y mostrar la nueva ventana
            Stage stageModal = new Stage();
            stageModal.setTitle("Editar Evento: " + seleccionado.getNombre()); //Título de la ventana
            stageModal.initModality(Modality.WINDOW_MODAL); // Bloquea interacción con la ventana principal
            // Establece la ventana actual (donde está la tabla) como "dueña" de la modal
            stageModal.initOwner(tblEventos.getScene().getWindow());

            //Setear la escena cargada en la ventana
            Scene scene = new Scene(root);
            stageModal.setScene(scene);
            //Mostrar la ventana y esperar a que el usuario la cierre antes de continuar
            stageModal.showAndWait();
            tblEventos.refresh(); //Actualiza la vista de los ítems existentes cite: [cite: 3]
        } catch (IOException e) {
            // Manejo de error si no se encuentra el archivo FXML
            mostrarAlerta("Error", "No se pudo cargar la ventana de edición. " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Método para acceder a la escena para crear un nuevo evento
    @FXML
    void onNuevoEvento(ActionEvent event) {
        try {
            //Cargar el archivo FXML de la escena de Nuevo Evento
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pfeventosp2/AdminEscenas/NuevoEvento.fxml"));
            Parent root = loader.load();

            //Configurar la nueva ventana (Stage) como Modal
            Stage stageModal = new Stage();
            stageModal.setTitle("Crear Nuevo Evento");
            stageModal.initModality(Modality.WINDOW_MODAL); // Impide interactuar con la tabla mientras está abierta
            stageModal.initOwner(tblEventos.getScene().getWindow());

            //Setear la escena y mostrarla
            Scene scene = new Scene(root);
            stageModal.setScene(scene);

            //showAndWait() pausa la ejecución de este método hasta que se cierre la ventana
            stageModal.showAndWait();

            // Actualizamos la lista observable con los datos frescos del Singleton:
            listaEventosObservable.setAll(plataforma.getListaEventos());

        } catch (IOException e) {
            mostrarAlerta("Error", "No se pudo cargar la ventana de nuevo evento. " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void onEliminarEvento(ActionEvent event) {
        //Obtener el evento seleccionado
        Evento seleccionado = tblEventos.getSelectionModel().getSelectedItem();

        //Validar que haya una selección
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Por favor, selecciona un evento de la tabla para eliminar.");
            return;
        }

        //Crear una alerta de confirmación
        Alert alertaConfirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        alertaConfirmacion.setTitle("Confirmar Eliminación");
        alertaConfirmacion.setHeaderText(null);
        alertaConfirmacion.setContentText("¿Estás seguro de que deseas eliminar permanentemente el evento: " + seleccionado.getNombre() + "?");

        //Mostrar alerta y esperar la respuesta del usuario
        Optional<ButtonType> resultado = alertaConfirmacion.showAndWait();

        //Si el usuario hace clic en aceptar
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                //Llamar a la lógica del Singleton
                plataforma.eliminarEvento(seleccionado);

                //Remover el evento de la lista observable para que desaparezca de la tabla al instante
                listaEventosObservable.remove(seleccionado);

                //Notificar éxito
                mostrarAlerta("Éxito", "El evento ha sido eliminado correctamente.");

            } catch (IOException e) {
                //Manejar error si el archivo está bloqueado o falla
                mostrarAlerta("Error", "No se pudo eliminar el evento del archivo: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void onMapa(ActionEvent event) {
        //Obtener el evento seleccionado de la tabla
        Evento seleccionado = tblEventos.getSelectionModel().getSelectedItem();

        //Validar que el administrador haya seleccionado un evento
        if (seleccionado == null) {
            mostrarAlerta("Atención", "Por favor, selecciona un evento de la tabla para ver su disponibilidad.");
            return;
        }

        //Validar que el evento tenga un recinto asignado
        if (seleccionado.getRecinto() == null) {
            mostrarAlerta("Atención", "Este evento aún no tiene un recinto asignado.");
            return;
        }

        try {
            //Cargar la vista del mapa de asientos que creamos hace un momento
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/pfeventosp2/AdminEscenas/MapaAsientosEvento.fxml"));
            Parent root = loader.load();

            //Obtener el controlador del mapa y pasarle el evento seleccionado
            MapaAsientosEventoController controller = loader.getController();
            controller.setEventoData(seleccionado);

            //Configurar y mostrar la nueva ventana
            Stage stage = new Stage();
            stage.setTitle("Mapa de Disponibilidad");
            stage.initModality(Modality.WINDOW_MODAL);
            stage.initOwner(tblEventos.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            //Refrescar la tabla cuando se cierre la ventana
            tblEventos.refresh();

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo abrir el mapa de asientos: " + e.getMessage());
        }
    }

    //Método para mostrar alertas simples al usuario
    private void mostrarAlerta(String titulo, String mensaje) {
        //Definir el tipo de alerta por defecto como información
        Alert.AlertType tipoAlerta = Alert.AlertType.INFORMATION;

        //Si el título es "Error", cambiar el tipo a ERROR para que muestre el icono rojo
        if (titulo != null && titulo.equalsIgnoreCase("Error")) {
            tipoAlerta = Alert.AlertType.ERROR;
        }

        //Crear la alerta con el tipo definido
        Alert alerta = new Alert(tipoAlerta);
        alerta.setTitle(titulo); //Título de la ventana
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje); //El mensaje principal

        //Mostrar la alerta y esperar a que el usuario la cierre (showAndWait)cite: [cite: 1, 2]
        alerta.showAndWait();
    }

}
