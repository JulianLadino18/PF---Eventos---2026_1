package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.Recinto;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Zona;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.RecintoRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class EditarZonaController {

    @FXML private TextField asientosFilaField;
    @FXML private TextField capacidadField;
    @FXML private TextField idZonaField;
    @FXML private TextField nombreField;
    @FXML private TextField precioField;

    private Recinto recintoPadre;
    private Zona zonaEditable;

    //Recibe los datos al hacer clic en la tarjeta visual
    public void setZonaData(Recinto recinto, Zona zona) {
        this.recintoPadre = recinto;
        this.zonaEditable = zona;

        idZonaField.setText(zona.getIdZona());
        idZonaField.setEditable(false);
        idZonaField.setStyle("-fx-background-color: #e9ecef;");

        nombreField.setText(zona.getNombre());
        capacidadField.setText(String.valueOf(zona.getCapacidad()));
        asientosFilaField.setText(String.valueOf(zona.getAsientosPorFila()));
        precioField.setText(String.valueOf(zona.getPrecioBase()));
    }

    @FXML
    void onGuardar(ActionEvent event) {
        try {
            int capacidad = Integer.parseInt(capacidadField.getText());
            int asientosFila = Integer.parseInt(asientosFilaField.getText());
            double precio = Double.parseDouble(precioField.getText());

            // Validar límites
            if (capacidad < 10 || capacidad > 20) {
                mostrarAlerta("Límite", "La capacidad debe estar entre 10 y 20.", Alert.AlertType.WARNING);
                return;
            }

            // Actualizar objeto en memoria
            zonaEditable.setNombre(nombreField.getText());
            zonaEditable.setPrecioBase(precio);

            // Si la capacidad o filas cambiaron, actualizamos (esto regenera los asientos)
            if (zonaEditable.getCapacidad() != capacidad) zonaEditable.setCapacidad(capacidad);
            if (zonaEditable.getAsientosPorFila() != asientosFila) zonaEditable.setAsientosPorFila(asientosFila);

            // Actualizar en TXT
            RecintoRepository.getInstance().actualizarZonaEnArchivo(recintoPadre.getIdRecinto(), zonaEditable);

            mostrarAlerta("Éxito", "Zona actualizada correctamente.", Alert.AlertType.INFORMATION);
            cerrarVentana(event);

        } catch (Exception e) {
            mostrarAlerta("Error", "Revisa que los campos numéricos sean correctos.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    void onEliminar(ActionEvent event) {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar");
        alerta.setContentText("¿Eliminar la zona '" + zonaEditable.getNombre() + "'?");

        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Eliminar de la memoria del Recinto
                recintoPadre.eliminarZona(zonaEditable);

                // Eliminar del TXT
                RecintoRepository.getInstance().eliminarZonaEnArchivo(zonaEditable.getIdZona());

                mostrarAlerta("Éxito", "Zona eliminada.", Alert.AlertType.INFORMATION);
                cerrarVentana(event);

            } catch (IOException e) {
                mostrarAlerta("Error", "No se pudo eliminar: " + e.getMessage(), Alert.AlertType.ERROR);
            }
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
        alerta.setTitle(titulo); alerta.setHeaderText(null); alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}