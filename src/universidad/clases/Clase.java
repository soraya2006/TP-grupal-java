package universidad.clases;

import java.io.Serializable;
import java.time.LocalDateTime;

public class Clase implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private LocalDateTime fechaHora;

    public Clase(String id, LocalDateTime fechaHora) {
        this.id = id;
        this.fechaHora = fechaHora;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDateTime getFechaHora() { return fechaHora; }
    public void setFechaHora(LocalDateTime fechaHora) { this.fechaHora = fechaHora; }

    @Override
    public String toString() {
        return "Clase ID: " + id + " - Fecha/Hora: " + fechaHora;
    }
}
