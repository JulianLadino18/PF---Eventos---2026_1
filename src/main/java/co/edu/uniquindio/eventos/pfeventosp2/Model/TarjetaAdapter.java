package co.edu.uniquindio.eventos.pfeventosp2.Model;

//adaptador para tarjetas del patron adapter
public class TarjetaAdapter implements IPagoAdapter{
    //atributos
    private Tarjetas apiTarjetas;
    private String numeroTarjeta;
    private String cvv;
    private String fechaExpiracion;

    public TarjetaAdapter(String numeroTarjeta, String cvv, String fechaExpiracion) {
        //aquí se instancia la clase externa
        this.apiTarjetas = new Tarjetas();
        this.numeroTarjeta = numeroTarjeta;
        this.cvv = cvv;
        this.fechaExpiracion = fechaExpiracion;
    }

    @Override
    public boolean procesarPago(double monto) {
        return apiTarjetas.cobrarTarjeta(this.numeroTarjeta, this.cvv, this.fechaExpiracion, monto);
    }
}
