package co.edu.uniquindio.eventos.pfeventosp2.Model;

//adaptador para nequi del patorn adapter
public class NequiAdapter implements IPagoAdapter{
    //atributos
    private Nequi apiNequi;
    private String numeroCelular;

    public NequiAdapter(String numeroCelular) {
        //aquí se instancia la clase externa
        this.apiNequi = new Nequi();
        this.numeroCelular = numeroCelular;
    }

    @Override
    public boolean procesarPago(double monto) {
        return apiNequi.hacerTransferencia(this.numeroCelular, monto);
    }
}
