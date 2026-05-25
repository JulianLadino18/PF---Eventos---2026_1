package co.edu.uniquindio.eventos.pfeventosp2.Model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class UsuarioTest {

    // Verifica que un usuario pueda agregar correctamente un metodo de pago tipo billetera virtual
    @Test
    void debeAgregarMetodoNequi() {
        Usuario usuario = new Usuario(RoleUser.CLIENTE, "1", "Juan Perez", "juan@mail.com", "1234", "3001234567");

        usuario.agregarMetodoBilleteraVirtual("Nequi", "3001234567");

        assertEquals(1, usuario.getMetodosPago().size());
        assertTrue(usuario.getMetodosPago().contains("Nequi:3001234567"));
    }

    // Verifica que el sistema no permita registrar métodos de pago duplicados.
    @Test
    void noDebeDuplicarMetodoDePago() {
        Usuario usuario = new Usuario(RoleUser.CLIENTE, "1", "Juan Perez", "juan@mail.com", "1234", "3001234567");

        usuario.agregarMetodoTarjeta("123456789", "123", "12/28");
        usuario.agregarMetodoTarjeta("123456789", "123", "12/28");

        assertEquals(1, usuario.getMetodosPago().size());
    }

    // Verifica que un metodo de pago pueda eliminarse correctamente.
    @Test
    void debeEliminarMetodoDePago() {
        Usuario usuario = new Usuario(RoleUser.CLIENTE, "1", "Juan Perez", "juan@mail.com", "1234", "3001234567");
        usuario.agregarMetodoPSE("Bancolombia", "Ahorros", "100200300");

        usuario.eliminarMetodoPago("PSE:Bancolombia:Ahorros:100200300");

        assertTrue(usuario.getMetodosPago().isEmpty());
    }
}