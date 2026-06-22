package universidad.asignaturas;

import java.io.Serializable;
import java.io.Serial;
import universidad.excepciones.*;
/**
 * Representa una materia que se dicta en la facultad.
 */
public abstract class Asignatura implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String codigo;
    private final String nombre;
    private final int cuatrimestre;
    private final boolean promocional;
    /**
     * Crea una nueva materia con sus reglas de aprobación.
     * @param codigo       El código único de la materia.
     * @param nombre       El nombre completo de la materia.
     * @param cuatrimestre En qué cuatrimestre se da (del 1 al 10).
     * @param promocional  Verdadero si la materia se puede promocionar, falso si no.
     */
    public Asignatura(String codigo, String nombre, int cuatrimestre, boolean promocional) {
        if (cuatrimestre < 1 || cuatrimestre > 10) {
            throw new DatoInvalidoException("Cuatrimestre inválido");
        } else if (codigo == null || codigo.isBlank()) {
            throw new ParametroNuloException("El código de la asignatura no puede estar vacío");
        } else if (nombre == null || nombre.isBlank()) {
            throw new ParametroNuloException("El nombre de la asignatura no puede estar vacío");
        }
        this.codigo = codigo;
        this.nombre = nombre;
        this.cuatrimestre = cuatrimestre;
        this.promocional = promocional;
    }
    public String getCodigo() {
        return codigo;
    }
    public String getNombre() {
        return nombre;
    }
    public int getCuatrimestre() {
        return cuatrimestre;
    }
    public boolean isPromocional() {
        return promocional;
    }
    @Override
    public String toString() {
        return "[" + codigo + "] " + nombre + " - " + getTipo();
    }
    public abstract String getTipo();
    public abstract double porcentajeHabilitacion(); // abstracta
    public abstract double porcentajePromocion(); // abstracta
}