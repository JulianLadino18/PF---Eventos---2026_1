package co.edu.uniquindio.eventos.pfeventosp2.Model;

import java.util.ArrayList;

public class Usuario extends Persona{
    private String telefono;
    private ArrayList<String> metodosPago;

    public Usuario(String id, String nombre, String correo, String password, String telefono) {
        super(id, nombre, correo, password);
        this.telefono = telefono;
        this.metodosPago = new ArrayList<>();
    }

    public String getTelefono() { return telefono; }
    public ArrayList<String> getMetodosPago() { return metodosPago; }

    public void setMetodosPago(ArrayList<String> metodosPago) {
        this.metodosPago = metodosPago;
    }

    @Override
    public String toString() {
        return "Usuario{" + super.toString() +
                "telefono='" + telefono + '\'' +
                ", metodosPago=" + metodosPago +
                '}';
    }
}
