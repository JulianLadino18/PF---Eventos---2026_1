package co.edu.uniquindio.eventos.pfeventosp2.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EventoTest {

    // Verifica que el estado del evento cambie correctamente a cancelado
    @Test
    void debeCambiarEstadoYNotificarSiSeCancela() {
        Recinto recinto = new Recinto("R1", "Arena", "Calle 1", "Bogota");
        PoliticaCancelacion politica = new PoliticaCancelacion() {
            @Override
            public double calcularPorcentajeReembolso() {
                return 0.5;
            }

            @Override
            public String obtenerDescripcionPolitica() {
                return "Reembolso del 50%";
            }
        };

        Evento evento = new Evento("E1", "Concierto", "Musica", "Descripcion", "Bogota",
                "2026-06-01", "20:00", politica, recinto);

        evento.cambiarEstado("CANCELADO");

        assertEquals("CANCELADO", evento.getEstado());
    }

    // Verifica que el reembolso se calcule correctamente según la política de cancelación definida.
    @Test
    void debeCalcularReembolsoSegunPolitica() {
        Recinto recinto = new Recinto("R1", "Arena", "Calle 1", "Bogota");
        PoliticaCancelacion politica = new PoliticaCancelacion() {
            @Override
            public double calcularPorcentajeReembolso() {
                return 0.75;
            }

            @Override
            public String obtenerDescripcionPolitica() {
                return "Reembolso del 75%";
            }
        };

        Evento evento = new Evento("E1", "Concierto", "Musica", "Descripcion", "Bogota",
                "2026-06-01", "20:00", politica, recinto);

        double reembolso = evento.procesarReembolso(100000);

        assertEquals(75000, reembolso, 0.01);
    }
}