package co.edu.uniquindio.eventos.pfeventosp2.Model;

//patrón State indica qué se puede hacer en cada estadp
public interface EstadoCompra {
    void pagar(Compra compra);
    void cancelar(Compra compra);
    void agregarBoleto(Compra compra, Entrada boleto);
    void quitarBoleto(Compra compra, Entrada boleto);
}
