package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.Admin;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Persona;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.UserRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class PerfilAdminController {

    //Elementos FXML
    @FXML private TextField txtId;
    @FXML private TextField txtNombre;
    @FXML private TextField txtCorreo;

    @FXML private PasswordField txtContrasenaActual;
    @FXML private PasswordField txtNuevaContrasena;
    @FXML private PasswordField txtConfirmarContrasena;

    //Variable del usuario
    private Persona adminLogueado;


    //Este método sirve para que el dashboard pase el admin que se loggeo
    public void setDatosAdmin(Persona usuario) {
        //se ponen los datos del uduario recibido
        this.adminLogueado = usuario;

        //Para llenar los datos del fxml al recibirlos
        if (adminLogueado != null) {
            //Aquí se meuestran los datos del admin
            txtId.setText(adminLogueado.getId());
            txtNombre.setText(adminLogueado.getNombre());
            txtCorreo.setText(adminLogueado.getCorreo());
        }
    }

    //Método para actualizar el nombre y el correo del admin
    @FXML
    void onActualizarDatos(ActionEvent event) {
        //Validar que no estén vacíos
        if (txtNombre.getText().trim().isEmpty() || txtCorreo.getText().trim().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "El nombre y el correo son obligatorios.");
            return;
        }

        //Actualizar el objeto en la memoria
        adminLogueado.setNombre(txtNombre.getText().trim());
        adminLogueado.setCorreo(txtCorreo.getText().trim());

        //Guardar en el archivo TXT a través del repositorio
        try {
            //Este método debe tomar la persona y sobrescribir su línea en users.txt
            UserRepository.getInstance().updateArchive();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Tus datos personales han sido actualizados.");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar la información en la base de datos.");
        }
    }

    //Método para cambiar la contraseña del admin, pero se tiene que verificar introduciendo la contraseña antigua
    @FXML
    void onCambiarContrasena(ActionEvent event) {
        String actual = txtContrasenaActual.getText();
        //Se valida la contraseña nueva dos veces
        String nueva = txtNuevaContrasena.getText();
        String confirmacion = txtConfirmarContrasena.getText();

        //Validar que no haya campos vacíos
        if (actual.isEmpty() || nueva.isEmpty() || confirmacion.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Vacíos", "Debes llenar todos los campos de seguridad.");
            return;
        }

        //Validar que la contraseña actual sea correcta
        if (!adminLogueado.getPassword().equals(actual)) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Seguridad", "La contraseña actual ingresada es incorrecta.");
            return;
        }

        //Validar que las contraseñas nuevas coincidan
        if (!nueva.equals(confirmacion)) {
            mostrarAlerta(Alert.AlertType.ERROR, "No coinciden", "La nueva contraseña y la confirmación no son iguales.");
            return;
        }
        // 5. Aplicar cambios y guardar
        try {
            adminLogueado.setPassword(nueva);
            UserRepository.getInstance().updateArchive();

            //Limpiar los campos para que no queden visibles
            txtContrasenaActual.clear();
            txtNuevaContrasena.clear();
            txtConfirmarContrasena.clear();

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Tu contraseña se ha cambiado correctamente.");
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ocurrió un error al intentar cambiar la contraseña.");
        }
    }

    //Método para mostrar ventanas emergentes
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}