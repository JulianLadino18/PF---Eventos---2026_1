package co.edu.uniquindio.eventos.pfeventosp2.Model;

//clase externa patron adapter
public class DaviPlata {
    public boolean cobrarBilleteraDigital(String numero, double valor) {
        System.out.println("DaviPlata: Cobrando $" + valor + " al número " + numero);
        return true;
    }
}
