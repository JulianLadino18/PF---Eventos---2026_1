package co.edu.uniquindio.eventos.pfeventosp2.Model;


//clase externa adapter
public class Nequi {
    public boolean hacerTransferencia(String celular, double cantidad) {
        System.out.println("Nequi: Procesando transferencia de $" + cantidad + " desde el número " + celular);
        //se asegura que el pago fue exitoso
        return true;
    }
}
