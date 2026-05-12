package co.edu.uniquindio.eventos.pfeventosp2.Model;

//clase externa adapter
public class Pse {
    public boolean debitarCuentaBancaria(String banco, String tipoPersona, String documento, double total) {
        System.out.println("PSE: Conectando con " + banco + " para debitar $" + total + " al documento " + documento);
        return true;
    }
}
