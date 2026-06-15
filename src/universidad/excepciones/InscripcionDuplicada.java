package universidad.excepciones;

public class InscripcionDuplicada extends RuntimeException {
    public InscripcionDuplicada(){
      super("Esta inscripción fue instanciada para otro curso.");
    }
    public InscripcionDuplicada(String message) {
      super(message);
    }
}
