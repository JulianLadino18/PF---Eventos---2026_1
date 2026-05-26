package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.*;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.UserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.util.List;

public class NuevoUsuarioController {

    @FXML private TextField idField, nombreField, correoField, telefonoField, passwordField;

    @FXML
    public void initialize() {
        //Bloquear el campo ID
        idField.setEditable(false);

        // Autogenerar y colocar el ID
        idField.setText(generarNuevoId());
    }

    //Calcular el próximo ID disponible
    private String generarNuevoId() {
        //Obtenemos la lista de todas las personas (Admins + Clientes)
        var todasLasPersonas = UserRepository.getInstance().getPersonas();

        int maxId = 0;

        for (Persona p : todasLasPersonas) {
            // Solo nos interesan los que son Admin (Empiezan con AD)
            if (p instanceof Usuario) {
                try {
                    //Extraemos los números del id quitando la D
                    String numeroString = p.getId().replaceAll("[^0-9]", "");
                    int numero = Integer.parseInt(numeroString);
                    if (numero > maxId) {
                        maxId = numero;
                    }
                } catch (NumberFormatException ex) {
                    //Ignorar si el formato no es el esperado
                }
            }
        }

        // Si no hay admins, empezamos en 1001 como en tu ejemplo
        if (maxId == 0) return "CL2001";

        // Generamos el siguiente sumando 1 al máximo encontrado
        return String.format("CL%04d", maxId + 1);
    }

    @FXML
    void onGuardar(ActionEvent event) {
        if (idField.getText().isEmpty() || nombreField.getText().isEmpty() || correoField.getText().isEmpty() || passwordField.getText().isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.", Alert.AlertType.ERROR);
            return;
        }

        //Crear nuevo objeto Usuario
        Usuario nuevoUsuario = new Usuario(
                RoleUser.CLIENTE,
                idField.getText(),
                nombreField.getText(),
                correoField.getText(),
                passwordField.getText(),
                telefonoField.getText()
        );

        //Guardar en el repositorio
        UserRepository.getInstance().addPersona(nuevoUsuario);

        mostrarAlerta("Éxito", "Usuario registrado correctamente.", Alert.AlertType.INFORMATION);
        cerrarVentana(event);
    }

    @FXML
    void onCancelar(ActionEvent event) {
        cerrarVentana(event);
    }

    private void cerrarVentana(ActionEvent event) {
        ((Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow()).close();
    }

    private void mostrarAlerta(String titulo, String mensaje, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}