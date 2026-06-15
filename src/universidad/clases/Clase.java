package universidad.clases;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.io.Serial;
import universidad.excepciones.*;

public class Clase implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String id;
    private LocalDateTime fechaHora;

    public Clase(String id, LocalDateTime fechaHora) {
        if (id == null || id.isBlank()) {
            throw new ParametroNuloException("El ID de la clase no puede estar vacío");
        }
        if (fechaHora == null) {
            throw new ParametroNuloException("La fecha y hora de la clase no pueden ser nulas");
        }
        this.id = id;
        this.fechaHora = fechaHora;
    }

    public String getId() { 
        return id; 
    }

    public void setId(String id) {
        if (id == null || id.isBlank()) {
            throw new ParametroNuloException("El ID de la clase no puede estar vacío");
        }
        this.id = id;
    }

    public LocalDateTime getFechaHora() { 
        return fechaHora; 
    }

    public void setFechaHora(LocalDateTime fechaHora) { 
        this.fechaHora = fechaHora; 
    }

    @Override
    public String toString() {
        return "Clase ID: " + id + " - Fecha/Hora: " + fechaHora;
    }
}