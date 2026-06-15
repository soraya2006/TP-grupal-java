package universidad.excepciones;
public class AlumnoDuplicadoException extends RuntimeException {
    public AlumnoDuplicadoException() {
        super("Error: Ya existe un alumno registrado con esa matrícula.");
    }
    public AlumnoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}