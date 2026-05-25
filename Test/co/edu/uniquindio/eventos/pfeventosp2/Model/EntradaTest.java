package co.edu.uniquindio.eventos.pfeventosp2.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EntradaTest {

    // Verifica que una entrada pueda cambiar correctamente su estado a anulada
    @Test
    void debeCambiarEstadoAAnulada() {
        Zona zona = new Zona("Z1", "VIP", 50, 200000, 10);
        Entrada entrada = new EntradaEstandar("ENT1", zona, null);

        entrada.anularEntrada();

        assertEquals(EstadoEntrada.ANULADA, entrada.getEstado());
    }
}