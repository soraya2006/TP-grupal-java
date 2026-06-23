package universidad.alumnos;

import java.io.Serializable;
import java.time.LocalDate;
import java.io.Serial;
import java.util.Objects;
import universidad.excepciones.*;


public class Alumno implements Comparable<Alumno>, Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String matricula;
    private final String apellido;
    private final String nombre;
    private final LocalDate fechaNacimiento;
    /**
     * Registra un nuevo alumno.
     * @param matricula El DNI o número de legajo único del alumno.
     * @param nombre    El nombre de pila.
     * @param apellido  El apellido.
     * @param fechaNacimiento Fecha de nacimiento del alumno
     */
    public Alumno(String matricula, String apellido, String nombre, LocalDate fechaNacimiento) {
        if (matricula == null || matricula.isBlank()) {
            throw new ParametroNuloException("La matrícula no puede estar vacía");
        } else if (apellido == null || apellido.isBlank()) {
            throw new ParametroNuloException("El apellido no puede estar vacío");
        } else if (nombre == null || nombre.isBlank()) {
            throw new ParametroNuloException("El nombre no puede estar vacío");
        } else if (fechaNacimiento == null) {
            throw new ParametroNuloException("La fecha de nacimiento no puede ser nula");
        }else {
            this.matricula = matricula;
            this.apellido = apellido;
            this.nombre = nombre;
            this.fechaNacimiento = fechaNacimiento;
        }
    }
    public String getMatricula() { 
        return matricula; 
    }
    public String getApellido() { 
        return apellido; 
    }
    public String getNombre() { 
        return nombre; 
    }
    public LocalDate getFechaNacimiento() { 
        return fechaNacimiento; 
    }

    @Override
    public int compareTo(Alumno otro) {
        int comp = this.apellido.compareToIgnoreCase(otro.apellido);
        if (comp == 0) { // si es el mismo apellido comparamos x nombre
            comp = this.nombre.compareToIgnoreCase(otro.nombre);
        }
        if (comp == 0) { // si tienen mismo nombre y apellido comparamos por matricula
            comp = this.matricula.compareToIgnoreCase(otro.matricula);
        }

        return comp;
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
        return getApellido() + ", " + getNombre() + " (Matrícula: " + getMatricula() + ")";
    }
    @Override
    public int hashCode() {
        return Objects.hash(matricula != null ? matricula.toLowerCase() : null);
    }
}