package universidad.excepciones;

public class ClaseNoDictadaException extends RuntimeException {
    public ClaseNoDictadaException() {
        super("Error: La clase solicitada no pertenece o no fue dictada en este curso.");
    }
    public ClaseNoDictadaException(String mensaje) {
        super(mensaje);
    }
}