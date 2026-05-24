package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.Recinto;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Zona;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.RecintoRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

public class NuevaZonaController {

    @FXML private TextField asientosFilaField;
    @FXML private TextField capacidadField;
    @FXML private TextField idZonaField;
    @FXML private TextField nombreField;
    @FXML private TextField precioField;

    // Referencia al recinto al que le vamos a agregar esta zona
    private Recinto recintoPadre;

    public void setRecintoPadre(Recinto recinto) {
        this.recintoPadre = recinto;

        // Configurar campo ID como no editable y autogenerar
        idZonaField.setEditable(false);
        idZonaField.setStyle("-fx-background-color: #e9ecef;");
        idZonaField.setText(generarIdZona());
    }

    private String generarIdZona() {
        int maxId = 0;
        // Revisamos las zonas de ESTE recinto para generar el ID
        for (Zona z : recintoPadre.getZonas()) {
            try {
                String numStr = z.getIdZona().replaceAll("[^0-9]", "");
                int num = Integer.parseInt(numStr);
                if (num > maxId) maxId = num;
            } catch (Exception e) {}
        }
        return String.format("Z%03d", maxId + 1);
    }

    @FXML
    void onGuardar(ActionEvent event) {
        // 1. Validar campos vacíos
        if (nombreField.getText().isEmpty() || capacidadField.getText().isEmpty() ||
                asientosFilaField.getText().isEmpty() || precioField.getText().isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        try {
            // 2. Extraer datos numéricos y validar formatos
            int capacidad = Integer.parseInt(capacidadField.getText());
            int asientosFila = Integer.parseInt(asientosFilaField.getText());
            double precio = Double.parseDouble(precioField.getText());

            // 3. LA REGLA DE ORO: Validar capacidad entre 10 y 20
            if (capacidad < 10 || capacidad > 20) {
                mostrarAlerta("Límite de Asientos", "Por requerimientos, la capacidad debe estar entre 10 y 20 asientos.", Alert.AlertType.WARNING);
                return;
            }

            // 4. Validar coherencia de los asientos por fila
            if (asientosFila > capacidad || asientosFila <= 0) {
                mostrarAlerta("Error", "Los asientos por fila deben ser mayores a 0 y no pueden exceder la capacidad total.", Alert.AlertType.WARNING);
                return;
            }

            // 5. Crear la nueva Zona (Al crearla, tu clase generará los asientos automáticamente)
            Zona nuevaZona = new Zona(idZonaField.getText(), nombreField.getText(), capacidad, precio, asientosFila);

            // 6. Añadir al Recinto en memoria
            recintoPadre.agregarZona(nuevaZona);

            // 7. Guardar en el TXT (Llamando al repositorio)
            RecintoRepository.getInstance().guardarNuevaZona(recintoPadre.getIdRecinto(), nuevaZona);

            mostrarAlerta("Éxito", "La zona ha sido agregada correctamente.", Alert.AlertType.INFORMATION);
            cerrarVentana(event);

        } catch (NumberFormatException e) {
            mostrarAlerta("Error de Formato", "Capacidad, Asientos por Fila y Precio deben ser valores numéricos.", Alert.AlertType.ERROR);
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error: " + e.getMessage(), Alert.AlertType.ERROR);
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

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}