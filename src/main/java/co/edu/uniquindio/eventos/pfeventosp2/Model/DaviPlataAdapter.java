package co.edu.uniquindio.eventos.pfeventosp2.Model;

public class DaviPlataAdapter implements IPagoAdapter {
    //atributos
    private DaviPlata apiDaviPlata;
    private String numeroCelular;

    public DaviPlataAdapter(String numeroCelular) {
        //se instancia la clase externa
        this.apiDaviPlata = new DaviPlata();
        this.numeroCelular = numeroCelular;
    }

    @Override
    public boolean procesarPago(double monto) {
        return apiDaviPlata.cobrarBilleteraDigital(this.numeroCelular, monto);
    }
}
