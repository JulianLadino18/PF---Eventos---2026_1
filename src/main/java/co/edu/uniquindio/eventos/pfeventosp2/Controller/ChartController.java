package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.Compra;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Evento;
import co.edu.uniquindio.eventos.pfeventosp2.Model.PlataformaEventos;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Zona;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;

import javax.annotation.Nonnull;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ChartController {
    //Esta lista envolverá la lista maestra y aplicará los filtros de búsqueda
    private FilteredList<Evento> listaEventosFiltrada;
    //Lista observable que JavaFX usa para actualizar la vista automáticamente
    private ObservableList<Evento> listaEventosObservable;
    //Referencia al Singleton de datos
    private PlataformaEventos plataforma;

    @FXML
    private LineChart<String, Number> lineChartVenta;

    @FXML
    private PieChart pieCategorias;
    @FXML
    private PieChart pieOcupacionZonas;

    @FXML
    private BarChart<String, Number> barChartCiudades;

    @FXML
    private Label lblComprasRegistradas;

    @FXML
    private Label lblIngresosTotales;

    @FXML
    private ComboBox<Evento> comboEventos;

    private List<Compra> listaCompras = new ArrayList<>();


    @FXML
    public void initialize() {
        plataforma = PlataformaEventos.getInstancia();
        List<Evento> eventosDePersistencia = plataforma.getListaEventos();
        //Se convierte la lista normal a la lista observable que requiere JavaFX.
        listaEventosObservable = FXCollections.observableArrayList(eventosDePersistencia);
        this.listaCompras = plataforma.getListaCompras();

        comboEventos.setConverter(new javafx.util.StringConverter<Evento>() {
            @Override
            public String toString(Evento evento) {
                if (evento != null) {
                    return evento.getNombre() + " - (" + evento.getNombreRecinto() + ")";
                }
                return "";
            }
            @Override
            public Evento fromString(String string) {
                return null;
            }
        });

        comboEventos.setItems(listaEventosObservable);

        comboEventos.getSelectionModel().selectedItemProperty().addListener((observable, eventoAnterior, eventoSeleccionado) -> {
            if (eventoSeleccionado != null) {
                cargarPieChartOcupacionPorEvento(eventoSeleccionado);
            }
        });
        cargarMetricas();
    }

    private Map<String, Integer> obtenerEventosPorCategoria() {
        Map<String, Integer> categorias = new HashMap<>();
        for (Evento evento : listaEventosObservable) {
            String categoria = evento.getCategoria();
            categorias.put(categoria, categorias.getOrDefault(categoria, 0) + 1);
        }
        return categorias;
    }

    private void cargarPieChartCategorias() {
        Map<String, Integer> datos = obtenerEventosPorCategoria();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (Map.Entry<String, Integer> entry : datos.entrySet()) {
            pieChartData.add(new PieChart.Data(entry.getKey(), entry.getValue()));
        }
        pieCategorias.setData(pieChartData);
    }

    private Map<String, Integer> obtenerEventos() {
        Map<String, Integer> ciudades = new HashMap<>();
        for (Evento evento : listaEventosObservable) {
            String ciudad = evento.getNombre();
            ciudades.put(ciudad, ciudades.getOrDefault(ciudad, 0) + 1);
        }
        return ciudades;
    }

    private void cargarBarChartCiudades() {
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        Map<String, Integer> datos = obtenerEventos();
        for (Map.Entry<String, Integer> entry : datos.entrySet()) {serie.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }
        barChartCiudades.getData().add(serie);
    }

    private Map<LocalDate, Integer> obtenerEventosPorFecha() {
        Map<LocalDate, Integer> fechas = new TreeMap<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        for (Evento evento : listaEventosObservable) {
            String fechaString = evento.getFecha();
            if (fechaString == null || fechaString.trim().isEmpty()) {
                System.err.println("Advertencia: El evento '" + evento.getNombre() + "' no tiene una fecha válida asignada.");
                continue; // Salta este evento y continúa con el siguiente
            }
            try {
                LocalDate fecha = LocalDate.parse(fechaString.trim(), formatter);
                fechas.put(fecha, fechas.getOrDefault(fecha, 0) + 1);
            } catch (java.time.format.DateTimeParseException e) {
                // Si la fecha está mal formateada, te avisará en consola exactamente cuál es el problema
                System.err.println("Error de formato en el evento '" + evento.getNombre() + "'. " +
                        "Se esperaba 'dd/MM/yyyy' pero se encontró: '" + fechaString + "'");
                // No relanzamos la excepción para evitar que bloquee la carga del FXML
            }
        }

        return fechas;
    }
    private void cargarLineChartVenta() {
        lineChartVenta.getData().clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM");
        XYChart.Series<String, Number> serie = new XYChart.Series<>();
        Map<LocalDate, Integer> datos = obtenerEventosPorFecha();
        for (Map.Entry<LocalDate, Integer> entry : datos.entrySet()) {
            serie.getData().add(new XYChart.Data<>(entry.getKey().format(formatter), entry.getValue()));
        }
        lineChartVenta.getData().add(serie);
    }

    private double obtenerIngresosTotales(List<Compra> listaCompras) {
        double total = 0;
        for (Compra compra : this.listaCompras) {
            total += compra.getTotal();
        }
        return total;
    }

    private int obtenerTotalCompras(@Nonnull List<Compra> compras) {
        return compras.size();
    }

    /**
     * Carga y actualiza el PieChart con la ocupación actual de las zonas de un evento.
     * @param evento El evento seleccionado del cual se quiere ver la ocupación.
     */
    public void cargarPieChartOcupacionPorEvento(Evento evento) {

        if (evento == null || evento.getRecinto() == null) {
            System.err.println("Error: El evento o su recinto asociado es nulo.");
            return;
        }
        pieOcupacionZonas.getData().clear();
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        List<Zona> zonas = evento.getRecinto().getZonas();

        if (zonas == null || zonas.isEmpty()) {
            System.out.println("El recinto '" + evento.getNombreRecinto() + "' no tiene zonas registradas.");
            return;
        }
        for (Zona zona : zonas) {
            // CAMBIO: Usamos los asientos disponibles que calcula tu clase Zona
            int asientosDisponibles = zona.consultarDisponibles();

            // Si la zona tiene capacidad, la agregamos al gráfico
            if (asientosDisponibles > 0) {
                String etiqueta = zona.getNombre() + " (" + asientosDisponibles + " Libres)";
                pieChartData.add(new PieChart.Data(etiqueta, asientosDisponibles));
            }
        }

        pieOcupacionZonas.setData(pieChartData);
        pieOcupacionZonas.setTitle(evento.getNombre());
    }



    public void cargarMetricas(){
        int comprasTotales = obtenerTotalCompras(listaCompras);
        double ingresosTotales = obtenerIngresosTotales(listaCompras);
        lblComprasRegistradas.setText(String.valueOf(comprasTotales));
        lblIngresosTotales.setText(String.format("$ %,.2f", ingresosTotales));

        cargarPieChartCategorias();
        cargarBarChartCiudades();
        cargarLineChartVenta();
    }
}
