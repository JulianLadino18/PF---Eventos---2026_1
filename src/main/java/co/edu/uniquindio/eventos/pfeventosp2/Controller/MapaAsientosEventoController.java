package co.edu.uniquindio.eventos.pfeventosp2.Controller;

import co.edu.uniquindio.eventos.pfeventosp2.Model.*;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.BloqueoRepository;
import co.edu.uniquindio.eventos.pfeventosp2.Repository.CompraRepository;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.List;

public class MapaAsientosEventoController {

    @FXML private Text tituloEventoText, subtituloRecintoText;
    @FXML private VBox mapaContenedor;

    private Evento eventoActual;

    public void setEventoData(Evento evento) {
        this.eventoActual = evento;
        sincronizarEstadosAsientos();
        tituloEventoText.setText("Disponibilidad: " + evento.getNombre());
        subtituloRecintoText.setText("Recinto: " + evento.getRecinto().getNombre() + " - Haz clic en un asiento para detalles.");
        dibujarMapaVisual();
    }

    //Método para sincronizar los estados de los asientos al mapa mediante el id
    private void sincronizarEstadosAsientos() {
        //Se ponen todos los asientos disponibles por defecto
        for (Zona z : eventoActual.getRecinto().getZonas()) {
            if (z.getAsientos() != null) {
                for (Asiento a : z.getAsientos()) {
                    a.setEstado("DISPONIBLE");
                }
            }
        }

        //Cargar los bloqueos
        try {
            List<String> idsBloqueados = BloqueoRepository.getInstance().cargarBloqueos();
            for (Zona z : eventoActual.getRecinto().getZonas()) {
                if (z.getAsientos() != null) {
                    for (Asiento a : z.getAsientos()) {
                        if (idsBloqueados.contains(a.getIdAsiento())) {
                            a.setEstado("BLOQUEADO");
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Revisar todas las compras de la plataforma
        for (Compra c : CompraRepository.getInstance().getListaCompras()) {
            // Si la compra es de este evento específico
            if (c.getEvento().getIdEvento().equals(eventoActual.getIdEvento())) {
                //Si la compra fue cancelada, los asientos siguen disponibles
                if (c.getEstado() instanceof EstadoCancelada) {
                    continue;
                }

                //Si está Pagada = VENDIDO (Rojo), si está Creada = RESERVADO (Naranja)
                String estadoOcupacion = (c.getEstado() instanceof EstadoPagada) ? "VENDIDO" : "RESERVADO";

                //Extraer las entradas de la compra
                for (Entrada e : c.getItemsCompra()) {
                    if (e.getAsiento() != null) {
                        String idAsientoComprado = e.getAsiento().getIdAsiento();

                        //Buscar ese id exacto dentro de las zonas del mapa visual y actualizarlo
                        for (Zona zonaMapa : eventoActual.getRecinto().getZonas()) {
                            if (zonaMapa.getAsientos() != null) {
                                for (Asiento asientoVisual : zonaMapa.getAsientos()) {
                                    if (asientoVisual.getIdAsiento().equals(idAsientoComprado)) {
                                        // Aquí se actualiza el asiento
                                        asientoVisual.setEstado(estadoOcupacion);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void dibujarMapaVisual() {
        mapaContenedor.getChildren().clear();

        if (eventoActual.getRecinto() == null || eventoActual.getRecinto().getZonas().isEmpty()) {
            mapaContenedor.getChildren().add(new Text("Este recinto no tiene zonas configuradas."));
            return;
        }

        // --- Dibujar el escenario ---
        Label escenario = new Label("ESCENARIO");
        escenario.setPrefSize(500, 40);
        escenario.setAlignment(Pos.CENTER);
        escenario.setStyle("-fx-background-color: #d1d5db; -fx-border-color: #9ca3af; -fx-border-width: 2; -fx-font-weight: bold; -fx-font-size: 16px; -fx-background-radius: 5;");
        mapaContenedor.getChildren().add(escenario);

        HBox contenedorZonasH = new HBox();
        contenedorZonasH.setSpacing(20);
        contenedorZonasH.setAlignment(Pos.TOP_CENTER);

        // --- Dibujar Zonas y Asientos ---
        for (Zona zona : eventoActual.getRecinto().getZonas()) {
            VBox zonaBox = new VBox();
            zonaBox.setSpacing(10);
            zonaBox.setAlignment(Pos.TOP_CENTER);
            zonaBox.setStyle("-fx-border-color: #4d5e9e; -fx-border-radius: 5; -fx-padding: 10; -fx-background-color: #f8f9fa;");

            Label lblZona = new Label(zona.getNombre() + " ($" + zona.getPrecioBase() + ")");
            lblZona.setStyle("-fx-font-weight: bold; -fx-text-fill: #4c3984;");

            GridPane gridAsientos = new GridPane();
            gridAsientos.setHgap(5);
            gridAsientos.setVgap(5);
            gridAsientos.setAlignment(Pos.CENTER);

            int columna = 0;
            int filaGrid = 0;
            int maxColumnas = zona.getAsientosPorFila();

            for (Asiento asiento : zona.getAsientos()) {
                String[] partes = asiento.getIdAsiento().split("-");
                String textoSilla = (partes.length >= 3) ? partes[1] + partes[2] : "S";

                Button btnAsiento = new Button(textoSilla);
                btnAsiento.setPrefSize(35, 35);

                // ASIGNAR COLOR SEGÚN ESTADO
                btnAsiento.setStyle(obtenerEstiloPorEstado(asiento.getEstado()));

                // AL HACER CLIC, ABRIR DETALLE
                btnAsiento.setOnAction(e -> abrirDetalleAsiento(zona, asiento));

                gridAsientos.add(btnAsiento, columna, filaGrid);

                columna++;
                if (columna >= maxColumnas) {
                    columna = 0;
                    filaGrid++;
                }
            }
            zonaBox.getChildren().addAll(lblZona, gridAsientos);
            contenedorZonasH.getChildren().add(zonaBox);
        }
        mapaContenedor.getChildren().add(contenedorZonasH);
    }

    private String obtenerEstiloPorEstado(String estado) {
        String baseStyle = "-fx-text-fill: white; -fx-font-size: 10px; -fx-padding: 0; -fx-cursor: hand; -fx-background-radius: 3; -fx-background-color: ";
        if (estado == null) return baseStyle + "#4caf50;"; // Verde por defecto

        switch (estado.toUpperCase()) {
            case "VENDIDO": return baseStyle + "#f44336;"; // Rojo
            case "RESERVADO": return baseStyle + "#ff9800;"; // Naranja
            case "BLOQUEADO": return baseStyle + "#9e9e9e;"; // Gris
            case "DISPONIBLE":
            default: return baseStyle + "#4caf50;"; // Verde
        }
    }

    private void abrirDetalleAsiento(Zona zona, Asiento asiento) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(getClass().getResource("/pfeventosp2/AdminEscenas/DetalleAsiento.fxml"));
            javafx.scene.Parent root = loader.load();

            DetalleAsientoController controller = loader.getController();
            controller.setDatos(eventoActual, zona, asiento);

            Stage stage = new Stage();
            stage.setTitle("Detalle de Asiento");
            stage.initModality(javafx.stage.Modality.WINDOW_MODAL);
            stage.initOwner(mapaContenedor.getScene().getWindow());
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Refrescar colores al volver por si se bloqueó o cambió estado
            dibujarMapaVisual();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML void onVolver(ActionEvent event) {
        ((Stage) ((Node) event.getSource()).getScene().getWindow()).close();
    }
}