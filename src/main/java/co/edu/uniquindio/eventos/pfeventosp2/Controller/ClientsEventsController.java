package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.Evento;
import co.edu.uniquindio.eventos.pfeventosp2.Model.PlataformaEventos;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Recinto;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Zona;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ClientsEventsController {

    @FXML
    private TextField txtCity;
    @FXML private ComboBox<String> comboCategory;
    @FXML private DatePicker txtDate;
    @FXML private TextField txtPrice;

    private FilteredList<Evento> listaFiltrada;

    @FXML private Label lblPrincipal;
    @FXML private Label lblCategory;
    @FXML private Label lblState;
    @FXML private Label lblDate;
    @FXML private Label lblPlace;
    @FXML private Label lblDescription;
    @FXML private Label lblPolitics;
    @FXML private VBox vBZonas;
    @FXML
    private TableView<Evento> EventsTable;

    @FXML
    private TableColumn<Evento, String> colName;
    @FXML
    private TableColumn<Evento, String> colCategory;
    @FXML
    private TableColumn<Evento, String> colState;

    private ClientDashboardController dashboard;

    public void setDashboard(ClientDashboardController dashboard) {
        this.dashboard = dashboard;
    }

    @FXML
    public void initialize() {
        //Vincula columnas con los métodos de la clase Evento
        // Reemplaza los PropertyValueFactory por expresiones Lambda directas:
        colName.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
        colCategory.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCategoria()));
        colState.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getEstado()));

        //Cargar los datos y aplicar la lista filtrada
        ObservableList<Evento> listaOriginal = FXCollections.observableArrayList(PlataformaEventos.getInstancia().getListaEventos());
        listaFiltrada = new FilteredList<>(listaOriginal, p -> true);

        // poner los datos en el tableview
        EventsTable.setItems(listaFiltrada);

        comboCategory.getItems().addAll("Concierto", "Teatro", "Conferencia");

        // listeners
        txtCity.textProperty().addListener((obs, old, newValue) -> aplicarFiltros());
        comboCategory.valueProperty().addListener((obs, old, newValue) -> aplicarFiltros());
        txtDate.valueProperty().addListener((obs, old, newValue) -> aplicarFiltros());
        txtPrice.textProperty().addListener((obs, old, newValue) -> aplicarFiltros());

        EventsTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarDetallesEvento(newSelection);
            }
        });
    }

    @FXML
    private void onClear() {
        //Limpiar los campos de texto
        txtCity.clear();
        txtPrice.clear();

        //pone el comboBox a ninguno seleccionado
        comboCategory.setValue(null);

        //Limpia el datePicker
        txtDate.setValue(null);
    }

    private void aplicarFiltros() {
        listaFiltrada.setPredicate(evento -> {

            // filtra por ciudad
            if (txtCity.getText() != null && !txtCity.getText().isEmpty()) {
                String ciudadFiltro = txtCity.getText().toLowerCase().trim();
                if (!evento.getRecinto().getCiudad().toLowerCase().contains(ciudadFiltro)) {
                    return false;
                }
            }

            //Filtro por Categoría
            if (comboCategory.getValue() != null) {
                if (!evento.getCategoria().equalsIgnoreCase(comboCategory.getValue())) {
                    return false;
                }
            }

            // Filtro por Fecha
            if (txtDate.getValue() != null) {
                String fechaFiltro = txtDate.getValue().toString();

                if (!evento.getFecha().equals(fechaFiltro)) {
                    return false;
                }
            }



            // Filtro por precio máximo
            if (txtPrice.getText() != null && !txtPrice.getText().isEmpty()) {
                try {
                    double precioMax = Double.parseDouble(txtPrice.getText().trim());
                    boolean tieneZonaLibre = false;

                    // Verificar que el evento tenga recinto y zona
                    if (evento.getRecinto() != null && evento.getRecinto().getZonas() != null) {
                        for (Zona zona : evento.getRecinto().getZonas()) {

                            // Comprobar precio y puestos
                            if (zona.getPrecioBase() <= precioMax && zona.consultarDisponibles() > 0) {
                                tieneZonaLibre = true;
                                break;
                            }
                        }
                    }

                    //
                    if (!tieneZonaLibre) {
                        return false;
                    }

                } catch (NumberFormatException e) {
                }
            }

            return true;
        });
    }

    @FXML
    private void onBuy() {
        Evento eventoElegido = EventsTable.getSelectionModel().getSelectedItem();

        // Validacion
        if (eventoElegido == null) {
            if (this.dashboard != null) {
                this.dashboard.mostrarAlerta("Selección Requerida", "Por favor, selecciona un evento de la lista para continuar.", Alert.AlertType.WARNING);
            }
            return;
        }

        // usar metodo del dashboard
        if (this.dashboard != null) {
            FXMLLoader loader = this.dashboard.cambiarVistaCentral("/pfeventosp2/ClienteEscenas/ClientEntrances.fxml");

            //Pasar el evento a la pantalla de las entradas
            if (loader != null) {
                ClientEntranceController entradasController = loader.getController();
                entradasController.inicializarEvento(eventoElegido);
            }
        }
    }

    /**
     * Toma el evento seleccionado y pinta sus datos en el panel de detalles
     */
    private void cargarDetallesEvento(Evento evento) {
        if (evento == null) return;

        lblPrincipal.setText(evento.getNombre());
        lblCategory.setText(evento.getCategoria());
        lblState.setText(evento.getEstado());
        lblDescription.setText(evento.getDescripcion());

        //Juntando fecha y hora
        lblDate.setText(evento.getFecha() + " a las " + evento.getHora());
        if (evento.getPolitica() != null) {
            lblPolitics.setText(evento.getPolitica().getClass().getSimpleName());
        } else {
            lblPolitics.setText("No hay políticas");
        }

        //Información del Recinto asociado
        Recinto recinto = evento.getRecinto();
        if (recinto != null) {
            // juntando nombre y ciudad del recinto
            String lugar = recinto.getNombre() + " (" + recinto.getDireccion() + ") - " + evento.getCiudad();
            lblPlace.setText(lugar);

            //Carga de Zonas desde el Recinto
            vBZonas.getChildren().clear(); // Limpia zonas del evento anterior

            //Dado que un evento tiene diversas zonas con diferentes precios, se agrega el label directamente de aca (no cabe en el tableview xd)
            if (recinto.getZonas() != null && !recinto.getZonas().isEmpty()) {
                for (Zona zona : recinto.getZonas()) {
                    // Se crea Label con el nombre, el precio y los asientos disponibles
                    Label lblZona = new Label(zona.getNombre() + " - Precio: $" + zona.getPrecioBase() +
                            " (Disponibles: " + zona.consultarDisponibles() + "/" + zona.getCapacidad() + ")");
                    lblZona.setStyle("-fx-font-size: 13px; -fx-text-fill: #333333;");

                    // Añadir el label al Vbox
                    vBZonas.getChildren().add(lblZona);
                }
            } else {
                vBZonas.getChildren().add(new Label("Este recinto no tiene zonas registradas."));
            }

        } else {
            lblPlace.setText("Recinto no asignado");
            vBZonas.getChildren().clear();
            vBZonas.getChildren().add(new Label("No hay zonas disponibles."));
        }
    }

}
