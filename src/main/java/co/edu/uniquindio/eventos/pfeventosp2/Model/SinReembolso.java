package co.edu.uniquindio.eventos.pfeventosp2.Model;

//patron strategy, estrategia de la política de cancelación
public class SinReembolso implements PoliticaCancelacion{
    //se sobreescriben los métodos de la interface PoliticaCancelacion
    @Override
    //método para devolver el 0%
    public double calcularPorcentajeReembolso() {
        return 0.0;
    }
    @Override
    public String obtenerDescripcionPolitica() {
        return "No se pudo devolver el dinero, no se admiten devoluciones.";
    }
}
