package universidad.asistencias;

import java.io.Serial;
import java.io.Serializable;
import universidad.clases.Clase;
import universidad.excepciones.*;

public class Asistencia implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Clase clase;
    private boolean presente;

    public Asistencia(Clase clase, boolean presente) {
        if (clase == null) {
            throw new ParametroNuloException("La clase asociada a la asistencia no puede ser nula");
        }
        this.clase = clase;
        this.presente = presente;
    }
    public Clase getClase() { 
        return clase; 
    }
    public void setClase(Clase clase) { 
        if (clase == null) {
            throw new ParametroNuloException("La clase asociada a la asistencia no puede ser nula");
        }
        this.clase = clase; 
    }
    public boolean isPresente() { 
        return presente; 
    }
    public void setPresente(boolean presente) { 
        this.presente = presente; 
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