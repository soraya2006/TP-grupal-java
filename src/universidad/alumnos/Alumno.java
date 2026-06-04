package universidad.alumnos;

import java.io.Serializable;
import java.time.LocalDate;

public class Alumno implements Comparable<Alumno>, Serializable {
    private static final long serialVersionUID = 1L;
    
    private String matricula;
    private String apellido;
    private String nombre;
    private LocalDate fechaNacimiento;

    public Alumno(String matricula, String apellido, String nombre, LocalDate fechaNacimiento) {
        this.matricula = matricula;
        this.apellido = apellido;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getMatricula() { return matricula; }
    public void setMatricula(String matricula) { this.matricula = matricula; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    
    @Override
    public int compareTo(Alumno otro) {
        int compApellido = this.apellido.compareToIgnoreCase(otro.apellido);
        if (compApellido != 0) {
            return compApellido;
        }
        return this.nombre.compareToIgnoreCase(otro.nombre);
    }

    @Override
    public String toString() {
        return apellido + ", " + nombre + " (Matrícula: " + matricula + ")";
    }
}