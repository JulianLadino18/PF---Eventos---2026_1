package co.edu.uniquindio.eventos.pfeventosp2.Model;

//clase externa adapter
public class Tarjetas {
    public boolean cobrarTarjeta(String numTarjeta, String cvv, String fechaExpiracion, double valor) {
        //la siguiente parte es para que se vean los ultimos digitos de la tarjeta
        String ultimosDigitos = numTarjeta.substring(numTarjeta.length() - 4);
        System.out.println("Visa/Master: Cobrando $" + valor + " a la tarjeta terminada en " + ultimosDigitos);
        return true;
    }
}
