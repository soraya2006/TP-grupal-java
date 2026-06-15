package universidad.excepciones;
public class AsignaturaDuplicadaException extends RuntimeException {
    public AsignaturaDuplicadaException() { // excepcion x defecto
        super("Error: Ya existe una asignatura registrada con ese código.");
    }
    public AsignaturaDuplicadaException(String mensaje) { // por si queres editar el mensaje
        super(mensaje);
    }
}