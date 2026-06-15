package universidad.excepciones;

public class DatoInvalidoException extends RuntimeException {
    public DatoInvalidoException(){
        super("El dato ingresado es invalido");
    }
    public DatoInvalidoException(String message) {
      super(message);
    }
}
