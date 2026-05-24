package co.edu.uniquindio.eventos.pfeventosp2.Model;

import java.util.ArrayList;
import java.util.List;

public class Usuario extends Persona{
    //atributos
    private String telefono;
    private ArrayList<String> metodosPago;
    private List<Compra> historialCompras;

    //constructor
    public Usuario(RoleUser role,String id, String nombre, String correo, String password, String telefono) {
        super(role ,id, nombre, correo, password);
        this.telefono = telefono;
        this.metodosPago = new ArrayList<>();
        this.historialCompras = new ArrayList<>();
    }

    //getters y setters
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    public ArrayList<String> getMetodosPago() { return metodosPago; }
    public void setMetodosPago(ArrayList<String> metodosPago) {
        this.metodosPago = metodosPago;
    }


    //método para gestionar métodos de pago
    //agregar billetera virtual
    public void agregarMetodoBilleteraVirtual(String tipo, String celular) {
        //tipo puede ser "equi" o "DaviPlata"
        String metodoFormateado = tipo + ":" + celular;
        if (!metodosPago.contains(metodoFormateado)) {
            metodosPago.add(metodoFormateado);
        }
    }

    //agregar tarjeta
    public void agregarMetodoTarjeta(String numero, String cvv, String fechaExpiracion) {
        String metodoFormateado = "Tarjeta:" + numero + ":" + cvv + ":" + fechaExpiracion;
        if (!metodosPago.contains(metodoFormateado)) {
            metodosPago.add(metodoFormateado);
        }
    }

    //agregar PSE
    public void agregarMetodoPSE(String banco, String tipo, String documento) {
        String metodoFormateado = "PSE:" + banco + ":" + tipo + ":" + documento;
        if (!metodosPago.contains(metodoFormateado)) {
            metodosPago.add(metodoFormateado);
        }
    }

    //método para eliminar métodos de pago
    public void eliminarMetodoPago(String metodo) {
        metodosPago.remove(metodo);
    }

    //este método sirve para tomar un string guardado y lo convierte en el adapter
    public IPagoAdapter obtenerAdapterDesdeString(String metodoGuardado) {
        String[] partes = metodoGuardado.split(":");
        String tipo = partes[0];
        switch (tipo) {
            case "Nequi":
                return new NequiAdapter(partes[1]);
            case "DaviPlata":
                return new DaviPlataAdapter(partes[1]);
            case "Tarjeta":
                return new TarjetaAdapter(partes[1], partes[2], partes[3]);
            case "PSE":
                return new PseAdapter(partes[1], partes[2], partes[3]);
            default:
                return null;
        }
    }

    public void agregarCompra(Compra compra) {
        this.historialCompras.add(compra);
    }

    // Asegúrate de que el getter exista
    public List<Compra> getHistorialCompras() {
        return historialCompras;
    }

    //Método para calcular el total gastado por el usuario
    public double getTotalGastado() {
        double total = 0.0;
        if (historialCompras != null) {
            for (Compra compra : historialCompras) {
                total += compra.getTotal();
            }
        }
        return total;
    }

    @Override
    public String toString() {
        return "Usuario{" + super.toString() +
                "telefono='" + telefono + '\'' +
                ", metodosPago=" + metodosPago +
                '}';
    }

}
