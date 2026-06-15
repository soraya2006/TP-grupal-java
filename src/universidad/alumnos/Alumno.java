package universidad.alumnos;

import java.io.Serializable;
import java.time.LocalDate;
import java.io.Serial;
import java.util.Objects;
import universidad.excepciones.*;

public class Alumno implements Comparable<Alumno>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String matricula;
    private String apellido;
    private String nombre;
    private LocalDate fechaNacimiento;

    public Alumno(String matricula, String apellido, String nombre, LocalDate fechaNacimiento) {
        if (matricula == null || matricula.isBlank()) {
            throw new ParametroNuloException("La matrícula no puede estar vacía");
        }
        if (apellido == null || apellido.isBlank()) {
            throw new ParametroNuloException("El apellido no puede estar vacío");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new ParametroNuloException("El nombre no puede estar vacío");
        }
        if (fechaNacimiento == null) {
            throw new ParametroNuloException("La fecha de nacimiento no puede ser nula");
        }   
        this.matricula = matricula;
        this.apellido = apellido;
        this.nombre = nombre;
        this.fechaNacimiento = fechaNacimiento;
    }
    public String getMatricula() { 
        return matricula; 
    }
    public void setMatricula(String matricula) { 
        if (matricula == null || matricula.isBlank()) {
            throw new ParametroNuloException("La matrícula no puede estar vacía");
        }
        this.matricula = matricula; 
    }
    public String getApellido() { 
        return apellido; 
    }
    public void setApellido(String apellido) { 
        if (apellido == null || apellido.isBlank()) {
            throw new ParametroNuloException("El apellido no puede estar vacío");
        }
        this.apellido = apellido; 
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
    public LocalDate getFechaNacimiento() { 
        return fechaNacimiento; 
    }
    public void setFechaNacimiento(LocalDate fechaNacimiento) { 
        if (fechaNacimiento == null) {
            throw new ParametroNuloException("La fecha de nacimiento no puede ser nula");
        }
        this.fechaNacimiento = fechaNacimiento; 
    }
    @Override
    public int compareTo(Alumno otro) {
        int compApellido = this.apellido.compareToIgnoreCase(otro.apellido);
        if (compApellido != 0) {
            return compApellido;
        }
        return this.nombre.compareToIgnoreCase(otro.nombre);
    }
    @Override
    public boolean equals(Object o) {
        if (this == o){
            return true;
        } else if (o == null || getClass() != o.getClass()) {
            return false;
        } else{
            Alumno alumno = (Alumno) o;
            if (this.matricula == null || alumno.matricula == null) {
                return false;
            } else {
                return this.matricula.equalsIgnoreCase(alumno.matricula);
            }
        }
    }
    @Override
    public String toString() {
        return apellido + ", " + nombre + " (Matrícula: " + matricula + ")";
    }
    @Override
    public int hashCode() {
        return Objects.hash(matricula != null ? matricula.toLowerCase() : null);
    }
}