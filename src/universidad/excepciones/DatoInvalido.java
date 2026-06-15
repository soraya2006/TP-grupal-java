package universidad.excepciones;

public class DatoInvalido extends RuntimeException {
    public DatoInvalido(){
        super("El dato ingresado es invalido");
    }
    public DatoInvalido(String message) {
      super(message);
    }
}
