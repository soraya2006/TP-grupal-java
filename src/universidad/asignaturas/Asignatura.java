package universidad.asignaturas;

import java.io.Serializable;
import java.io.Serial;
import universidad.excepciones.*;
/**
 * Representa una materia que se dicta en la facultad.
 */
public class Asignatura implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String codigo;
    private String nombre;
    private int cuatrimestre;
    private boolean promocional;
    private CategoriaAsignatura categoria;

    /**
     * Crea una nueva materia con sus reglas de aprobación.
     * * @param codigo       El código único de la materia.
     * @param nombre       El nombre completo de la materia.
     * @param cuatrimestre En qué cuatrimestre se da (del 1 al 10).
     * @param promocional  Verdadero si la materia se puede promocionar, falso si no.
     * @param categoria    El tipo de materia (Obligatoria, Optativa, etc.).
     */
    public Asignatura(String codigo, String nombre, int cuatrimestre, boolean promocional, CategoriaAsignatura categoria) {
        if (cuatrimestre < 1 || cuatrimestre > 10) {
            throw new DatoInvalidoException("Cuatrimestre inválido");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new ParametroNuloException("El código de la asignatura no puede estar vacío");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new ParametroNuloException("El nombre de la asignatura no puede estar vacío");
        }
        if (categoria == null) {
            throw new ParametroNuloException("La categoría de la asignatura no puede ser nula");
        }
        
        this.codigo = codigo;
        this.nombre = nombre;
        this.cuatrimestre = cuatrimestre;
        this.promocional = promocional;
        this.categoria = categoria;
    }
    public String getCodigo() {
        return codigo; 
    }
    public void setCodigo(String codigo) { 
        if (codigo == null || codigo.isBlank()) {
            throw new ParametroNuloException("El código no puede estar vacío");
        }
        this.codigo = codigo; 
    }
    public String getNombre() { 
        return nombre; 
    }
    public void setNombre(String nombre) { 
        if (nombre == null || nombre.isBlank()) {
            throw new ParametroNuloException("El nombre no puede estar vacío");
        }
        this.nombre = nombre; 
    }
    public int getCuatrimestre() { 
        return cuatrimestre; 
    }
    public void setCuatrimestre(int cuatrimestre) { 
        if (cuatrimestre < 1 || cuatrimestre > 10) {
            throw new DatoInvalidoException("Cuatrimestre inválido");
        }
        this.cuatrimestre = cuatrimestre; 
    }
    public boolean isPromocional() { 
        return promocional; 
    }
    public void setPromocional(boolean promocional) { 
        this.promocional = promocional; 
    }
    public CategoriaAsignatura getCategoria() { 
        return categoria; 
    }
    public void setCategoria(CategoriaAsignatura categoria) { 
        if (categoria == null) {
            throw new ParametroNuloException("La categoría no puede ser nula");
        }
        this.categoria = categoria; 
    }
    @Override
    public String toString() {
        return "[" + codigo + "] " + nombre + " - " + categoria;
    }
    public double porcentajeHabilitacion() {
        return switch (this.getCategoria()) {
            case OBLIGATORIA -> 60.0;
            case OPTATIVA -> 50.0;
            case PASANTIA, TESIS -> 75.0;
        };
    }

    /**
     * Calcula qué porcentaje de asistencia hace falta para quedar regular (habilitar).
     * * @return El porcentaje mínimo requerido (ejemplo: 60.0).
     */
    public double porcentajePromocion() {
        return switch (this.getCategoria()) {
            case OBLIGATORIA -> 80.0;
            case OPTATIVA -> 60.0;
            default -> -1.0; //el caso default cubre las otras categoría
        };
    }
}