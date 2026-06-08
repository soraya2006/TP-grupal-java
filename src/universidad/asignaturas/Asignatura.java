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
        if(cuatrimestre < 1 || cuatrimestre > 10)
            throw new IllegalArgumentException("Cuatrimestre inválido");
        this.codigo = codigo;
        this.nombre = nombre;
        this.cuatrimestre = cuatrimestre;
        this.promocional = promocional;
        this.categoria = categoria;
    }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCuatrimestre() { return cuatrimestre; }
    public void setCuatrimestre(int cuatrimestre) { this.cuatrimestre = cuatrimestre; }

    public boolean isPromocional() { return promocional; }
    public void setPromocional(boolean promocional) { this.promocional = promocional; }

    public CategoriaAsignatura getCategoria() { return categoria; }
    public void setCategoria(CategoriaAsignatura categoria) { this.categoria = categoria; }

    @Override
    public String toString() {
        return "[" + codigo + "] " + nombre + " - " + categoria;
    }
}
