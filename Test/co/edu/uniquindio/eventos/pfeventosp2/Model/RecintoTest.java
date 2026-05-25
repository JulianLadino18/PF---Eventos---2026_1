package co.edu.uniquindio.eventos.pfeventosp2.Model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RecintoTest {

    // Verifica que las zonas puedan agregarse correctamente al recinto y que la capacidad total se calcule adecuadamente.
    @Test
    void debeAgregarZonaYCalcularCapacidadTotal() {
        Recinto recinto = new Recinto("R1", "Movistar Arena", "Calle 1", "Bogota");
        Zona vip = new Zona("Z1", "VIP", 50, 200000, 10);
        Zona general = new Zona("Z2", "General", 100, 80000, 10);

        recinto.agregarZona(vip);
        recinto.agregarZona(general);

        assertEquals(2, recinto.getZonas().size());
        assertEquals(150, recinto.getCapacidadTotal());
    }

    // Verifica que una zona pueda eliminarse correctamente del recinto
    @Test
    void debeEliminarZona() {
        Recinto recinto = new Recinto("R1", "Movistar Arena", "Calle 1", "Bogota");
        Zona vip = new Zona("Z1", "VIP", 50, 200000, 10);

        recinto.agregarZona(vip);
        recinto.eliminarZona(vip);

        assertTrue(recinto.getZonas().isEmpty());
    }
}