package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NuevoEventoController {
    // --- Componentes FXML ---
    @FXML private Button cancelarButton;
    @FXML
    private Button crearButton;

    @FXML private TextField idField; // El campo del ID
    @FXML private TextField nombreField;
    @FXML private ComboBox<String> categoriaComboB;
    @FXML private TextField descripcionField; // Nombre corregido
    @FXML private TextField ciudadField;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> horaComboB;
    @FXML private ComboBox<String> minutoComboB;
    @FXML private ComboBox<String> politicaComboB;
    @FXML private ComboBox<String> recintoComboB;

    // Formateador estándar para la fecha
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @FXML
    public void initialize() {
        //Bloquear el campo ID para que el usuario no lo edite
        idField.setEditable(false);

        //Autogenerar y mostrar el ID
        idField.setText(generarNuevoId());

        //Llenar Horas (00-23) y Minutos (00, 15, 30, 45)
        for (int i = 0; i < 24; i++) horaComboB.getItems().add(String.format("%02d", i));
        for (int i = 0; i < 60; i += 15) minutoComboB.getItems().add(String.format("%02d", i));

        //Llenar datos fijos de los combos
        categoriaComboB.getItems().addAll("Concierto", "Teatro", "Cultura", "Deporte", "Conferencia", "Festival", "Taller");
        politicaComboB.getItems().addAll("Reembolso Total", "Sin Reembolso");

        //Cargar Recintos dinámicamente desde el Singleton
        try {
            List<Recinto> recintosGlobales = PlataformaEventos.getInstancia().getListaRecintos();
            if (recintosGlobales != null && !recintosGlobales.isEmpty()) {
                for (Recinto r : recintosGlobales) {
                    recintoComboB.getItems().add(r.getNombre());
                }
            } else {
                recintoComboB.getItems().add("No hay recintos registrados");
            }
        } catch (Exception e) {
            System.err.println("Error al cargar recintos en el combo: " + e.getMessage());
        }
    }

    //Calcular el próixmo ID disponible
    private String generarNuevoId() {
        List<Evento> eventosActuales = PlataformaEventos.getInstancia().getListaEventos();
        if (eventosActuales == null || eventosActuales.isEmpty()) {
            return "E001"; //Si no hay eventos, este es el primero
        }

        int maxId = 0;
        for (Evento e : eventosActuales) {
            try {
                //Extraer solo los números del ID (quita la 'E')
                String numeroString = e.getIdEvento().replaceAll("[^0-9]", "");
                int numero = Integer.parseInt(numeroString);
                if (numero > maxId) {
                    maxId = numero;
                }
            } catch (NumberFormatException ex) {
                //Ignorar si por alguna razón un ID está mal formateado en el TXT
            }
        }

        //Sumar 1 al máximo encontrado y formatear a 3 dígitos (ej: 005)
        return String.format("E%03d", maxId + 1);
    }

    @FXML
    void onCrear(ActionEvent event) {
        //Validación de campos obligatorios
        if (nombreField.getText().isEmpty() || ciudadField.getText().isEmpty() ||
                datePicker.getValue() == null || horaComboB.getValue() == null ||
                minutoComboB.getValue() == null || recintoComboB.getValue() == null ||
                politicaComboB.getValue() == null) {

            mostrarAlerta("Error de Validación", "Por favor llene todos los campos obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        try {
            //Extraer datos de la UI
            String idNuevo = idField.getText(); // Usamos el ID autogenerado
            String nombre = nombreField.getText();
            String categoria = categoriaComboB.getValue();
            String descripcion = descripcionField.getText();
            String ciudad = ciudadField.getText();
            String fecha = datePicker.getValue().format(formatter);
            String hora = horaComboB.getValue() + ":" + minutoComboB.getValue();

            //Instanciar Objeto Política
            PoliticaCancelacion politica;
            if (politicaComboB.getValue().equals("Reembolso Total")) {
                politica = new ReembolsoTotal();
            } else {
                politica = new SinReembolso();
            }

            //Buscar Objeto Recinto Real
            List<Recinto> recintos = PlataformaEventos.getInstancia().getListaRecintos();
            Recinto recintoSeleccionado = null;
            for (Recinto r : recintos) {
                if (r.getNombre().equals(recintoComboB.getValue())) {
                    recintoSeleccionado = r;
                    break;
                }
            }

            if (recintoSeleccionado == null) {
                mostrarAlerta("Error", "Debe seleccionar un recinto válido.", Alert.AlertType.ERROR);
                return;
            }

            //Instanciar el nuevo Evento
            Evento nuevoEvento = new Evento(
                    idNuevo, nombre, categoria, descripcion, ciudad,
                    fecha, hora, politica, recintoSeleccionado
            );

            //Registrar en el Singleton (Memoria + TXT)
            PlataformaEventos.getInstancia().registrarEvento(nuevoEvento);

            //Notificar y cerrar
            mostrarAlerta("Éxito", "El evento " + idNuevo + " ha sido creado correctamente.", Alert.AlertType.INFORMATION);
            cerrarVentana(event);

        } catch (IOException e) {
            mostrarAlerta("Error de Persistencia", "No se pudo guardar el evento en el archivo: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    @FXML
    void onCancelar(ActionEvent event) {
        cerrarVentana(event);
    }

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
