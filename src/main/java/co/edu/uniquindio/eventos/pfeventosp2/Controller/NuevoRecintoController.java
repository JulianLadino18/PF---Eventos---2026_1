package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.PlataformaEventos;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Recinto;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class NuevoRecintoController {

    @FXML private Button cancelarButton;
    @FXML private Button crearButton;
    @FXML private TextField ciudadField;
    @FXML private TextField direccionField;
    @FXML private TextField idField;
    @FXML private TextField nombreField;

    @FXML
    public void initialize() {
        //Bloquear el campo ID
        idField.setEditable(false);

        // Autogenerar y colocar el ID
        idField.setText(generarNuevoId());
    }

    //Calcular el próximo ID disponible
    private String generarNuevoId() {
        List<Recinto> recintosActuales = PlataformaEventos.getInstancia().getListaRecintos();

        if (recintosActuales == null || recintosActuales.isEmpty()) {
            return "R001"; //Primer recinto
        }

        int maxId = 0;
        for (Recinto r : recintosActuales) {
            try {
                //Extraemos los números del ID quitando la 'R'
                String numeroString = r.getIdRecinto().replaceAll("[^0-9]", "");
                int numero = Integer.parseInt(numeroString);
                if (numero > maxId) {
                    maxId = numero;
                }
            } catch (NumberFormatException ex) {
                //Ignorar formatos incorrectos
            }
        }

        return String.format("R%03d", maxId + 1);
    }

    @FXML
    void onCrear(ActionEvent event) {
        //Validar campos vacíos
        if (nombreField.getText().isEmpty() || direccionField.getText().isEmpty() || ciudadField.getText().isEmpty()) {
            mostrarAlerta("Error de Validación", "Por favor llene todos los campos obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        try {
            //Extraer los datos
            String id = idField.getText();
            String nombre = nombreField.getText();
            String direccion = direccionField.getText();
            String ciudad = ciudadField.getText();

            //Crear el nuevo objeto Recinto
            Recinto nuevoRecinto = new Recinto(id, nombre, direccion, ciudad);

            //Registrar a través del singleton
            PlataformaEventos.getInstancia().registrarRecinto(nuevoRecinto);

            //Notificar éxito y cerrar ventana
            mostrarAlerta("Éxito", "El recinto " + nombre + " ha sido creado correctamente.", Alert.AlertType.INFORMATION);
            cerrarVentana(event);

        } catch (IOException e) {
            mostrarAlerta("Error de Persistencia", "No se pudo guardar el recinto en el archivo: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error inesperado: " + e.getMessage(), Alert.AlertType.ERROR);
            e.printStackTrace();
        }
    }

    //Método para cerrar lal ventana sin hacer ningún cambio
    @FXML
    void onCancelar(ActionEvent event) {
        cerrarVentana(event);
    }

    //Métodos auxiliares
    private void cerrarVentana(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }

    //Método para mostrar la alerta
    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}