package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class EditarEventoController{

    @FXML
    private Button cancelarButton;

    @FXML
    private ComboBox<String> categoriaComboB;

    @FXML
    private TextField ciudadField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField descripbionField;

    @FXML
    private Button guardarButton;

    @FXML
    private ComboBox<String> horaComboB;

    @FXML
    private ComboBox<String> minutoComboB;

    @FXML
    private TextField nombreField;

    @FXML
    private ComboBox<String> politicaComboB;

    @FXML
    private ComboBox<String> recintoComboB;

    @FXML
    private Text textId; //Aquí el ID es obligatorio

    private Evento eventoEditable;
    // Formateador estándar para las fechas
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    //Este método se ejecuta automáticamente
    @FXML
    public void initialize() {
        //Llenar Horas (00-23)
        for (int i = 0; i < 24; i++) {
            horaComboB.getItems().add(String.format("%02d", i));
        }
        //Llenar Minutos (en intervalos de 15, por ejemplo)
        for (int i = 0; i < 60; i += 15) {
            minutoComboB.getItems().add(String.format("%02d", i));
        }

        // Ejemplo de datos para los otros combos
        categoriaComboB.getItems().addAll("Concierto", "Teatro", "Cultura", "Deporte", "Conferencia", "Festival", "Taller");
        politicaComboB.getItems().addAll("Reembolso Total", "Sin Reembolso");
        recintoComboB.getItems().clear(); // Limpiar por seguridad

        //Llamamos al Singleton que ya cargó los recintos desde el TXT
        List<Recinto> recintosDisponibles = PlataformaEventos.getInstancia().getListaRecintos();

        //Verificamos que la lista no esté vacía
        if (recintosDisponibles != null && !recintosDisponibles.isEmpty()) {
            for (Recinto r : recintosDisponibles) {
                //Agregamos solo el nombre al ComboBox visual
                recintoComboB.getItems().add(r.getNombre());
            }
        } else {
            //Mensaje de advertencia visual si el TXT de recintos está vacío
            recintoComboB.getItems().add("No hay recintos registrados");
        }
    }

    /**
     * --- ESTE ES EL MÉTODO CLAVE QUE SOLICITAS ---
     * Toma un objeto Evento seleccionado en la otra escena y llena la interfaz.
     * @param seleccionado El evento cuyos datos queremos editar.
     */
    public void setEventoData(Evento seleccionado) {
        if (seleccionado == null) return;
        this.eventoEditable = seleccionado;
        // 1. Campos de Texto y Texto Fijo
        textId.setText(seleccionado.getIdEvento()); // Muestra el ID en la zona indicada
        nombreField.setText(seleccionado.getNombre());
        ciudadField.setText(seleccionado.getCiudad());
        descripbionField.setText(seleccionado.getDescripcion());

        // Debemos parsear el String. Usaremos el formato dd-MM-yyyy
        if (seleccionado.getFecha() != null && !seleccionado.getFecha().isEmpty()) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            LocalDate fechaParsed = LocalDate.parse(seleccionado.getFecha(), formatter);
            datePicker.setValue(fechaParsed);
        }

        categoriaComboB.setValue(seleccionado.getCategoria());

        // Política de Cancelación
        PoliticaCancelacion politica = seleccionado.getPolitica();
        if (politica instanceof ReembolsoTotal) {
            politicaComboB.setValue("Reembolso Total");
        } else if (politica instanceof SinReembolso) {
            politicaComboB.setValue("Sin Reembolso");
        }

        // Recinto (Obtener el nombrecite: [cite: 3])
        Recinto recinto = seleccionado.getRecinto();
        if (recinto != null) {
            recintoComboB.setValue(recinto.getNombre());
        }

        //getMinuto() y formato de hora
        //El modelo no tiene getMinuto() y guarda la hora como String
        //Debemos parsear ese String para llenar los dos combos
        String horaStringModel = seleccionado.getHora();
        if (horaStringModel != null && horaStringModel.contains(":")) {
            String[] partesTiempo = horaStringModel.split(":");
            if (partesTiempo.length == 2) {
                horaComboB.setValue(partesTiempo[0]); //ej: Setea "20"
                minutoComboB.setValue(partesTiempo[1]); //ej: Setea "00"
            }
        }
    }

    @FXML
    void onCancelar(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    @FXML
    void onGuardar(ActionEvent event) {
        //Validación básica de campos obligatorios
        if (nombreField.getText().isEmpty() || ciudadField.getText().isEmpty() ||
                datePicker.getValue() == null || horaComboB.getValue() == null ||
                minutoComboB.getValue() == null || recintoComboB.getValue() == null ||
                politicaComboB.getValue() == null) {

            mostrarAlerta("Error de Validación", "Por favor llene todos los campos obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        try {
            //Actualizar el objeto en memoria usando setters
            eventoEditable.setNombre(nombreField.getText());
            eventoEditable.setCategoria(categoriaComboB.getValue());
            eventoEditable.setCiudad(ciudadField.getText());
            eventoEditable.setDescripcion(descripbionField.getText());
            eventoEditable.setFecha(datePicker.getValue().format(formatter));
            eventoEditable.setHora(horaComboB.getValue() + ":" + minutoComboB.getValue());

            // Buscar Objetos Reales para Política y Recinto
            String politicaSel = politicaComboB.getValue();
            if (politicaSel.equals("Reembolso Total")) {
                eventoEditable.setPolitica(new ReembolsoTotal());
            } else {
                eventoEditable.setPolitica(new SinReembolso());
            }
            // Recinto (Buscar objeto real por nombre)cite: [cite: 2, 3]
            List<Recinto> recintos = PlataformaEventos.getInstancia().getListaRecintos();
            Recinto recintoSeleccionado = null;
            for(Recinto r : recintos) {
                if(r.getNombre().equals(recintoComboB.getValue())) {
                    recintoSeleccionado = r;
                    break;
                }
            }
            eventoEditable.setRecinto(recintoSeleccionado);

            PlataformaEventos.getInstancia().actualizarEventoEnPersistencia(eventoEditable);
            mostrarAlerta("Éxito", "El evento ha sido actualizado correctamente.", Alert.AlertType.INFORMATION);
            // Al cerrarse, showAndWait() en el controlador principal continuará y ejecutará tblEventos.refresh()
            cerrarVentana(event);

        } catch (IOException e) {
            mostrarAlerta("Error de Guardado", "No se pudieron guardar los cambios en el archivo: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    // Métodos auxiliares
    private void cerrarVentana(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipoAlert) {
        Alert alerta = new Alert(tipoAlert);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}