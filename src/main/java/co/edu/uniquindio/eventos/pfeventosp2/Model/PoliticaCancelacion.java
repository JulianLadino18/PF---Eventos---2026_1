package co.edu.uniquindio.eventos.pfeventosp2.Model;

public interface PoliticaCancelacion {
    //método para devolver el porcentaje a reembolsar
    public double calcularPorcentajeReembolso();

    //método para obtener la descripción  de la política
    public String obtenerDescripcionPolitica();

}
