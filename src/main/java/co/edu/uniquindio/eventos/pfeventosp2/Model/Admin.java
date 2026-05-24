package co.edu.uniquindio.eventos.pfeventosp2.Model;

public class Admin extends Persona{
    //constructor
    public Admin(RoleUser role,String id, String nombre, String correo, String password) {
        super(role,id, nombre, correo, password);
    }


    @Override
    public String toString() {
        return "Admin{" +
                super.toString() + "}";
    }
}
