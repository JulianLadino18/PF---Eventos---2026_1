module co.edu.uniquindio.eventos.pfeventosp2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens co.edu.uniquindio.eventos.pfeventosp2 to javafx.fxml;
    exports co.edu.uniquindio.eventos.pfeventosp2;

    opens co.edu.uniquindio.eventos.pfeventosp2.Controller to javafx.fxml;
    exports co.edu.uniquindio.eventos.pfeventosp2.Controller;
    opens co.edu.uniquindio.eventos.pfeventosp2.Model to javafx.base;
}