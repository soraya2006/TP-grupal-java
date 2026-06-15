package universidad.excepciones;

public class ParametroNuloException extends RuntimeException {
    public ParametroNuloException() { // fijo x defecto
        super("Error: Ninguno de los parámetros obligatorios puede ser nulo.");
    }
    public ParametroNuloException(String mensaje) { // por si quieren modificar algo
        super(mensaje);
    }
}
