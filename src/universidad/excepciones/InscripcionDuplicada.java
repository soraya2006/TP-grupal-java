package universidad.excepciones;

public class InscripcionDuplicada extends RuntimeException {
    public InscripcionDuplicada(){
        super("El alumno ya se encuentra inscripto en este curso.");
    }
    public InscripcionDuplicada(String message) {
      super(message);
    }
}
