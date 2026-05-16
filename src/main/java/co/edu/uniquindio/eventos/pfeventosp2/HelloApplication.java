package co.edu.uniquindio.eventos.pfeventosp2;

import co.edu.uniquindio.eventos.pfeventosp2.Model.Persona;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    public static Persona loggedUser;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("/pfeventosp2/Login.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 370, 500);
        stage.setTitle("Login");
        stage.setScene(scene);
        stage.show();
    }
}
