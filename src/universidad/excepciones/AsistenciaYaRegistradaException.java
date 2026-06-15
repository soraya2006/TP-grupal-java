package universidad.excepciones;

public class AsistenciaYaRegistradaException extends RuntimeException {
  public AsistenciaYaRegistradaException() {
    super("Ya se encuentra registrada la asistencia del alumno para esta clase.");
  }
  public AsistenciaYaRegistradaException(String mensaje) { // por si quieren modificar algo
    super(mensaje);
  }
}
