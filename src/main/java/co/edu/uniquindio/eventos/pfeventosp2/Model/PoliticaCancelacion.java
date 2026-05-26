package co.edu.uniquindio.eventos.pfeventosp2.Model;
//Patrón Strategy
//sus hijos con: ReembolsoTotal y SinReembolso
public interface PoliticaCancelacion {
    //método para devolver el porcentaje a reembolsar
    public double calcularPorcentajeReembolso();

    //método para obtener la descripción  de la política
    public String obtenerDescripcionPolitica();

}
