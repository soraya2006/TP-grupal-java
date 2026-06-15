package universidad.excepciones;

public class AlumnoNoInscriptoException extends RuntimeException {
    public AlumnoNoInscriptoException() { // x defecto
        super("Error: El alumno no se encuentra inscripto en esta asignatura.");
    }
    public AlumnoNoInscriptoException(String mensaje) { // x si queres editar
        super(mensaje);
    }
}