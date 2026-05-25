package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.*;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.CompraRepository;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.EntradaRepository;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class GestionarComprasController {

    //atributos del fxml
    @FXML private TableView<Compra> tvCompras;
    @FXML private TableColumn<Compra, String> colIdCompra, colCliente, colEvento, colFecha, colEstado;
    @FXML private TableColumn<Compra, Double> colTotal;
    @FXML private TableView<Entrada> tvEntradas;
    @FXML private TableColumn<Entrada, String> colIdEntrada, colZona, colAsiento;
    @FXML private TableColumn<Entrada, Double> colPrecio;

    //Botones para reasignar un asiento, cancelar una compra o simular el reembolso de una compra cancelada
    @FXML private Button btnReasignar, btnCancelar, btnReembolso;

    private ObservableList<Compra> listaComprasObservable;
    private ObservableList<Entrada> listaEntradasObservable;
    private Compra compraSeleccionada;

    @FXML
    public void initialize() {
        // 1. Configurar columnas de Compras (CON PROTECCIÓN CONTRA NULOS)
        colIdCompra.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getIdCompra()));

        colCliente.setCellValueFactory(cell -> {
            Usuario u = cell.getValue().getUsuario();
            // Si el usuario es nulo, mostramos "Usuario no encontrado" en vez de romper la tabla
            return new SimpleStringProperty(u != null ? u.getNombre() : "Usuario no encontrado");
        });

        colEvento.setCellValueFactory(cell -> {
            Evento e = cell.getValue().getEvento();
            return new SimpleStringProperty(e != null ? e.getNombre() : "Evento no encontrado");
        });

        colFecha.setCellValueFactory(cell -> {
            return new SimpleStringProperty(cell.getValue().getFechaCreacion() != null
                    ? cell.getValue().getFechaCreacion().toLocalDate().toString()
                    : "Sin fecha");
        });

        colEstado.setCellValueFactory(cell -> {
            EstadoCompra estado = cell.getValue().getEstado();
            return new SimpleStringProperty(estado != null
                    ? estado.getClass().getSimpleName().replace("Estado", "")
                    : "Desconocido");
        });

        colTotal.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getTotal()));

        // 2. Configurar columnas de Entradas (También con protección)
        colIdEntrada.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getIdEntrada()));
        colZona.setCellValueFactory(cell -> {
            Zona z = cell.getValue().getZona();
            return new SimpleStringProperty(z != null ? z.getNombre() : "Sin Zona");
        });
        colAsiento.setCellValueFactory(cell -> {
            Asiento a = cell.getValue().getAsiento();
            return new SimpleStringProperty(a != null ? a.getIdAsiento() : "General/N/A");
        });
        colPrecio.setCellValueFactory(cell -> new SimpleObjectProperty<>(cell.getValue().getPrecioFinal()));

        // 3. Cargar datos
        cargarDatos();

        // 4. Listeners de selección
        tvCompras.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                seleccionarCompra(newSelection);
            }
        });

        tvEntradas.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null && compraSeleccionada != null) {
                boolean puedeReasignar = newSelection.getAsiento() != null &&
                        compraSeleccionada.getEstado() instanceof EstadoPagada;
                btnReasignar.setDisable(!puedeReasignar);
            }
        });
    }

    private void cargarDatos() {
        //Forzar la instanciación del singleton para forzar su constructor
        //También lee todos los TXT y llama al método cargarCompras() internamente
        PlataformaEventos.getInstancia();

        //Como el repositorio se llenó, se obtiene la lista
        List<Compra> todasLasCompras = CompraRepository.getInstance().getListaCompras();

        listaComprasObservable = FXCollections.observableArrayList(todasLasCompras);
        tvCompras.setItems(listaComprasObservable);
    }

    private void seleccionarCompra(Compra compra) {
        this.compraSeleccionada = compra;

        //Actualizar tabla inferior
        listaEntradasObservable = FXCollections.observableArrayList(compra.getItemsCompra());
        tvEntradas.setItems(listaEntradasObservable);

        //Habilitar/Deshabilitar botones según el Patrón State
        boolean estaCancelada = compra.getEstado() instanceof EstadoCancelada;
        boolean estaPagada = compra.getEstado() instanceof EstadoPagada;

        btnCancelar.setDisable(estaCancelada);
        btnReembolso.setDisable(!estaCancelada); //El reembolso es para compras canceladas
        btnReasignar.setDisable(true); //Se habilita al tocar una entrada
    }

    @FXML
    void onCancelarCompra(ActionEvent event) {
        //Mostrar confirmación
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Cancelar Compra");
        confirm.setContentText("¿Estás seguro de cancelar esta compra? Se liberarán los asientos.");
        if (confirm.showAndWait().get() != ButtonType.OK) return;

        //Usar el Patrón State para cancelar
        //Esto cambia su estado interno a EstadoCancelada
        compraSeleccionada.cancelar();

        //Guardar cambios en el archivo
        guardarCambiosEnPersistencia();

        //Refrescar interfaz
        tvCompras.refresh();
        seleccionarCompra(compraSeleccionada);
        mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "La compra ha sido cancelada.");
    }

    @FXML
    void onSimularReembolso(ActionEvent event) {
        //Uso del Patrón Strategy para calcular la devolución
        Evento evento = compraSeleccionada.getEvento();
        double totalPagado = compraSeleccionada.getTotal();

        //Delegar el cálculo a la política (ReembolsoTotal o SinReembolso)
        double montoADevolver = evento.procesarReembolso(totalPagado);
        String politicaDesc = evento.getPolitica().obtenerDescripcionPolitica();

        //Registrar/Simular reembolso, esto solo va a funcionar para las entradas canceladas, ya que no tiene sentido reembolsar una entrada pagada o apartada
        String mensaje = "Política del Evento: " + politicaDesc + "\n"
                + "Total Pagado: $" + totalPagado + "\n"
                + "Monto a Reembolsar: $" + montoADevolver + "\n\n";
        mostrarAlerta(Alert.AlertType.INFORMATION, "Simulación de Reembolso", mensaje);
    }

    @FXML
    void onReasignarAsiento(ActionEvent event) {
        Entrada entradaSel = tvEntradas.getSelectionModel().getSelectedItem();
        Zona zona = entradaSel.getZona();

        //Buscar asientos disponibles en la misma zona
        List<String> asientosDisponibles = new ArrayList<>();
        for (Asiento a : zona.getAsientos()) {
            //Un asiento está disponible si no está VENDIDO, RESERVADO ni BLOQUEADO
            if (a.getEstado().equalsIgnoreCase("DISPONIBLE")) {
                asientosDisponibles.add(a.getIdAsiento());
            }
        }

        if (asientosDisponibles.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Sin Asientos", "No hay asientos disponibles en esta zona para reasignar.");
            return;
        }

        //Mostrar cuadro de diálogo (choice dialog) para elegir el nuevo asiento
        ChoiceDialog<String> dialog = new ChoiceDialog<>(asientosDisponibles.get(0), asientosDisponibles);
        dialog.setTitle("Reasignar Asiento");
        dialog.setHeaderText("Asiento Actual: " + entradaSel.getAsiento().getIdAsiento());
        dialog.setContentText("Seleccione el nuevo asiento:");

        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String nuevoIdAsiento = result.get();

            //Buscar el objeto asiento nuevo
            Asiento nuevoAsientoObj = null;
            for (Asiento a : zona.getAsientos()) {
                if (a.getIdAsiento().equals(nuevoIdAsiento)) {
                    nuevoAsientoObj = a;
                    break;
                }
            }

            //Hacer el cambio en memoria
            if (nuevoAsientoObj != null) {
                //Liberar el viejo
                entradaSel.getAsiento().setEstado("DISPONIBLE");
                // Ocupar el nuevo
                nuevoAsientoObj.setEstado("VENDIDO");
                entradaSel.setAsiento(nuevoAsientoObj);

                try {
                    //Guardar las compras
                    CompraRepository.getInstance().actualizarArchivoCompras();
                   EntradaRepository.getInstance().actualizarArchivoEntradas();

                } catch (Exception e) {
                    System.err.println("Error al guardar la reasignación: " + e.getMessage());
                }

                tvEntradas.refresh();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Asiento reasignado a " + nuevoIdAsiento);
            }
        }
    }

    private void guardarCambiosEnPersistencia() {
        try {
            CompraRepository.getInstance().actualizarArchivoCompras();
        } catch (Exception e) {
            System.err.println("Error al guardar las compras: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}