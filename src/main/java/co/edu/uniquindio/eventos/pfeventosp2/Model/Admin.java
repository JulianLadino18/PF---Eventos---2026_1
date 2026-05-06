package co.edu.uniquindio.eventos.pfeventosp2.Model;

public class Admin extends Persona{
    public Admin(String id, String nombre, String correo, String password) {
        super(id, nombre, correo, password);
    }

    //falta crear la clase evento
    /*public void gestionarEvento(Evento evento, String nuevoEstado) {
        evento.setEstado(nuevoEstado);
        //(Publicar, Pausar, Cancelar)
    }

    //falta crear la clase asiento :p
    public void cambiarEstadoAsiento(Asiento asiento, String nuevoEstado) {
        asiento.setEstado(nuevoEstado);
        //(Bloquear o habilitar asientos)
    }

    public void registrarIncidencia(String descripcion, String tipo) {
        //se va a crear un registro en el IncidenciaRepository
    }*/

    @Override
    public String toString() {
        return "Admin{" +
                super.toString() + "}";
    }
}
