package universidad.excepciones;
public class CursoDuplicadoException extends RuntimeException {
    public CursoDuplicadoException() {
        super("Error: Ya existe un curso registrado con ese ID.");
    }
    public CursoDuplicadoException(String mensaje) {
        super(mensaje);
    }
}
