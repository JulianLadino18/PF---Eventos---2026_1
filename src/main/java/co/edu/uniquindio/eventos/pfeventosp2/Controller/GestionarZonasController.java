package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.Asiento;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Recinto;
import co.edu.uniquindio.eventos.pfeventosp2.Model.Zona;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GestionarZonasController {

    @FXML private VBox mapaContenedor;
    @FXML private Text tituloRecintoText;

    private Recinto recintoActual;

    public void setRecintoData(Recinto recinto) {
        this.recintoActual = recinto;
        tituloRecintoText.setText("Mapa de Asientos: " + recinto.getNombre());
        dibujarZonasVisuales();
    }

    public void dibujarZonasVisuales() {
        mapaContenedor.getChildren().clear();

        if (recintoActual.getZonas().isEmpty()) {
            Text mensajeVacio = new Text("No hay zonas registradas. ¡Agrega la primera!");
            mensajeVacio.setStyle("-fx-fill: #888888; -fx-font-size: 16px;");
            mapaContenedor.getChildren().add(mensajeVacio);
            return;
        }

        //---Dibujar el escenario---
        Label escenario = new Label("ESCENARIO");
        escenario.setPrefSize(500, 40);
        escenario.setAlignment(Pos.CENTER);
        escenario.setStyle("-fx-background-color: #d1d5db; -fx-border-color: #9ca3af; -fx-border-width: 2; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 5; -fx-border-radius: 5;");
        mapaContenedor.getChildren().add(escenario);

        //Contenedor horizontal  para las zonas
        //Aquí se pondrán las zonas lado a lado (Zona 1 | Zona 2 | Zona 3)
        HBox contenedorZonasH = new HBox();
        contenedorZonasH.setSpacing(20);
        contenedorZonasH.setAlignment(Pos.TOP_CENTER);

        //Dibujar cada zona y sus asientos
        for (Zona zona : recintoActual.getZonas()) {
            //Columna principal de la zona
            VBox zonaBox = new VBox();
            zonaBox.setSpacing(10);
            zonaBox.setAlignment(Pos.TOP_CENTER);
            zonaBox.setStyle("-fx-border-color: #4d5e9e; -fx-border-radius: 5; -fx-padding: 10; -fx-background-color: #f8f9fa;");

            //Botón superior (Nombre de la zona) que al darle clic abre la ventana de Editar
            Button btnEditarZona = new Button(zona.getNombre() + "\n$" + zona.getPrecioBase());
            btnEditarZona.setStyle("-fx-background-color: transparent; -fx-font-weight: bold; -fx-text-fill: #4c3984; -fx-cursor: hand; -fx-font-size: 14px; -fx-text-alignment: center;");
            btnEditarZona.setOnAction(e -> abrirEdicionZona(zona));

            //Cuadrícula para los asientos
            GridPane gridAsientos = new GridPane();
            gridAsientos.setHgap(5); // Espacio horizontal entre sillas
            gridAsientos.setVgap(5); // Espacio vertical entre sillas
            gridAsientos.setAlignment(Pos.CENTER);

            int columna = 0;
            int filaGrid = 0;
            int maxColumnas = zona.getAsientosPorFila();

            //Dibujar cada asiento
            for (Asiento asiento : zona.getAsientos()) {

                //Extraer "A1", "B2" del ID
                String[] partes = asiento.getIdAsiento().split("-");
                String textoSilla = (partes.length == 3) ? partes[1] + partes[2] : "S";

                Button btnAsiento = new Button(textoSilla);
                btnAsiento.setPrefSize(35, 35); // Tamaño del bloquecito

                //Le damos color verde
                btnAsiento.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 0; -fx-cursor: hand; -fx-background-radius: 3;");

                gridAsientos.add(btnAsiento, columna, filaGrid);

                //Controlar el salto de línea según los asientos por fila
                columna++;
                if (columna >= maxColumnas) {
                    columna = 0;
                    filaGrid++;
                }
            }

            //Unir todo: Meter el título y la cuadrícula en la caja de la Zona
            zonaBox.getChildren().addAll(btnEditarZona, gridAsientos);

            //Meter la caja de la Zona en el contenedor horizontal principal
            contenedorZonasH.getChildren().add(zonaBox);
        }

        //Agregar las zonas debajo del escenario
        mapaContenedor.getChildren().add(contenedorZonasH);
    }

    private void abrirEdicionZona(Zona zonaSeleccionada) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/co/edu/uniquindio/eventos/pfeventosp2/AdminEscenas/EditarZona.fxml"));
            javafx.scene.Parent root = loader.load();

            EditarZonaController controller = loader.getController();
            controller.setZonaData(recintoActual, zonaSeleccionada);

            Stage stageModal = new Stage();
            stageModal.setTitle("Editar Zona");
            stageModal.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stageModal.initOwner(mapaContenedor.getScene().getWindow());
            stageModal.setScene(new javafx.scene.Scene(root));

            stageModal.showAndWait();

            //Al cerrar, redibujamos el mapa por si cambiaron la cantidad de asientos
            dibujarZonasVisuales();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onNuevaZona(ActionEvent event) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/co/edu/uniquindio/eventos/pfeventosp2/AdminEscenas/NuevaZona.fxml"));
            javafx.scene.Parent root = loader.load();

            NuevaZonaController controller = loader.getController();
            controller.setRecintoPadre(recintoActual);

            Stage stageModal = new Stage();
            stageModal.setTitle("Agregar Nueva Zona");
            stageModal.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stageModal.initOwner(mapaContenedor.getScene().getWindow());
            stageModal.setScene(new javafx.scene.Scene(root));

            stageModal.showAndWait();

            dibujarZonasVisuales();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void onVolver(ActionEvent event) {
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.close();
    }
}