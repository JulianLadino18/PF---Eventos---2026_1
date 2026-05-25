package co.edu.uniquindio.eventos.pfeventosp2.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CompraTest {

    // Verifica que una compra pueda construirse correctamente mediante el patrón Builder con entradas y servicios adicionales.
    @Test
    void debeConstruirCompraConDatosBasicos() {

        Usuario usuario = new Usuario(
                RoleUser.CLIENTE,
                "1",
                "Juan Perez",
                "juan@mail.com",
                "1234",
                "3001234567"
        );

        Recinto recinto = new Recinto(
                "R1",
                "Arena",
                "Calle 1",
                "Bogota"
        );

        PoliticaCancelacion politica = new PoliticaCancelacion() {
            @Override
            public double calcularPorcentajeReembolso() {
                return 0.5;
            }

            @Override
            public String obtenerDescripcionPolitica() {
                return "Reembolso parcial";
            }
        };

        Evento evento = new Evento(
                "E1",
                "Concierto",
                "Musica",
                "Descripcion",
                "Bogota",
                "2026-06-01",
                "20:00",
                politica,
                recinto
        );

        Zona zona = new Zona("Z1", "VIP", 50, 200000, 10);

        Entrada entrada = new EntradaEstandar("ENT1", zona, null);

        Compra compra = new Compra.CompraBuilder("C1", usuario, evento)
                .agregarBoleto(entrada)
                .agregarServicio("Seguro", 10000)
                .build();

        assertEquals("C1", compra.getIdCompra());
        assertEquals(1, compra.getItemsCompra().size());
        assertEquals(210000, compra.getTotal(), 0.01);
    }
}