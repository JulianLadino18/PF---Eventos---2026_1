package co.edu.uniquindio.eventos.pfeventosp2.Model;

public class Admin extends Persona{
    //constructor
    public Admin(String id, String nombre, String correo, String password) {
        super(id, nombre, correo, password);
    }


    @Override
    public String toString() {
        return "Admin{" +
                super.toString() + "}";
    }
}
