package universidad.excepciones;

public class ClaseDuplicadaException extends RuntimeException {
    public ClaseDuplicadaException() {
        super("La clase con ese ID ya está registrada en este curso.");
    }
    public ClaseDuplicadaException(String message) {
        super(message);
    }
}
