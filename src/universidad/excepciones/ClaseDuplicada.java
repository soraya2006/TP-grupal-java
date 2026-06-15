package universidad.excepciones;

public class ClaseDuplicada extends RuntimeException {
    public ClaseDuplicada() {
        super("La clase con ese ID ya está registrada en este curso.");
    }
    public ClaseDuplicada(String message) {
        super(message);
    }
}
