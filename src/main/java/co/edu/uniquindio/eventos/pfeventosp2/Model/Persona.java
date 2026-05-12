package co.edu.uniquindio.eventos.pfeventosp2.Model;

public abstract class Persona {
    //atributos
    protected String id;
    protected String nombre;
    protected String correo;
    protected String password;

    //constructor
    public Persona(String id, String nombre, String correo, String password) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.password = password;
    }

    //getters y setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    //método para autenticar usuario
    public boolean autenticar(String correo, String password) {
        return this.correo.equals(correo) && this.password.equals(password);
    }

    @Override
    public String toString() {
        return "id='" + id + '\'' +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
