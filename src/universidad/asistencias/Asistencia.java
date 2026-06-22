package universidad.asistencias;

import java.io.Serial;
import java.io.Serializable;
import universidad.clases.Clase;
import universidad.excepciones.*;
/**
 * Guarda el registro de un solo día, diciendo si el alumno vino o faltó a una clase.
 */
public class Asistencia implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Clase clase;
    private final boolean presente;

    public Asistencia(Clase clase, boolean presente) {
        if (clase == null) {
            throw new ParametroNuloException("La clase asociada a la asistencia no puede ser nula");
        } else {
            this.clase = clase;
            this.presente = presente;
        }
    }
    public Clase getClase() { 
        return clase; 
    }
    public boolean isPresente() { 
        return presente; 
    }
    @Override
    public String toString() {
        String estado = "AUSENTE";
        if (presente) {
            estado = "PRESENTE";
        } 
        return clase.toString() + " -> Estado: " + estado;
    }
}