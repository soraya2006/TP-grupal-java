package universidad.asignaturas;

import java.io.Serializable;

public class Asignatura implements Serializable {
    private static final long serialVersionUID = 1L;

    private String codigo;
    private String nombre;
    private int cuatrimestre;
    private boolean promocional;
    private CategoriaAsignatura categoria; 

    public Asignatura(String codigo, String nombre, int cuatrimestre, boolean promocional, CategoriaAsignatura categoria) {
        if (cuatrimestre < 1 || cuatrimestre > 10) {
            throw new IllegalArgumentException("Cuatrimestre inválido");
        }
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El código de la asignatura no puede estar vacío");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre de la asignatura no puede estar vacío");
        }
        if (categoria == null) {
            throw new IllegalArgumentException("La categoría de la asignatura no puede ser nula");
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
            throw new IllegalArgumentException("El código no puede estar vacío");
        }
        this.codigo = codigo; 
    }
    public String getNombre() { 
        return nombre; 
    }
    public void setNombre(String nombre) { 
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío");
        }
        this.nombre = nombre; 
    }
    public int getCuatrimestre() { 
        return cuatrimestre; 
    }
    public void setCuatrimestre(int cuatrimestre) { 
        if (cuatrimestre < 1 || cuatrimestre > 10) {
            throw new IllegalArgumentException("Cuatrimestre inválido");
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
            throw new IllegalArgumentException("La categoría no puede ser nula");
        }
        this.categoria = categoria; 
    }
    @Override
    public String toString() {
        return "[" + codigo + "] " + nombre + " - " + categoria;
    }
    public double porcentajeHabilitacion() {
        double porcentajeRequerido = 0.0;

        switch (this.getCategoria()) {
            case OBLIGATORIA:
                porcentajeRequerido = 60.0;
                break;
            case OPTATIVA:
                porcentajeRequerido = 50.0;
                break;
            case PASANTIA:
            case TESIS:
                porcentajeRequerido = 75.0;
                break;
            default:
                porcentajeRequerido = 0.0;
                break;
        }
        return porcentajeRequerido;
    }

    public double porcentajePromocion() {
        double porcentajeRequerido = -1.0;

        switch (this.getCategoria()) {
            case OBLIGATORIA:
                porcentajeRequerido = 80.0;
                break;
            case OPTATIVA:
                porcentajeRequerido = 60.0;
                break;
            case PASANTIA:
            case TESIS:
                porcentajeRequerido = -1.0;
                break;
            default:
                porcentajeRequerido = -1.0;
                break;
        }
        return porcentajeRequerido;
    }
}