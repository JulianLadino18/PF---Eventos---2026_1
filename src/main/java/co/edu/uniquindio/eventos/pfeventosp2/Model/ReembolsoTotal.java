package co.edu.uniquindio.eventos.pfeventosp2.Model;

//patron strategy, estrategia de la política de cancelación
public class ReembolsoTotal implements PoliticaCancelacion{
    //se sobreescriben los métodos de la interface PoliticaCancelacion
    @Override
    //método para devolver el 100%
    public double calcularPorcentajeReembolso() {
        return 1.0;
    }
    @Override
    public String obtenerDescripcionPolitica() {
        return "Se devuelve el total del dinero.";
    }
}
