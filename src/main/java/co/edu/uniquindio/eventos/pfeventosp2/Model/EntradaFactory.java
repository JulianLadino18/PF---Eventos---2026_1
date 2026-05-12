package co.edu.uniquindio.eventos.pfeventosp2.Model;

import java.util.Map;

//patrón factory method
public class EntradaFactory {
    public static Entrada crearEntrada(String idEntrada, Zona zona, Asiento asiento) {
        //crea la entrada base, los servicios adicionales se agregan de manera externa
        return new EntradaEstandar(idEntrada, zona, asiento);
    }
}
