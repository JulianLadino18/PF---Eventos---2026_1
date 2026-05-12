package co.edu.uniquindio.eventos.pfeventosp2.Model;

public class PseAdapter implements IPagoAdapter{
    //atributos
    private Pse apiPse;
    private String banco;
    private String tipoPersona; //aquí puede ser natural o juridica
    private String documento;

    public PseAdapter(String banco, String tipoPersona, String documento) {
        //aquí se instancia la clase externa
        this.apiPse = new Pse();
        this.banco = banco;
        this.tipoPersona = tipoPersona;
        this.documento = documento;
    }

    @Override
    public boolean procesarPago(double monto) {
        return apiPse.debitarCuentaBancaria(this.banco, this.tipoPersona, this.documento, monto);
    }
}
