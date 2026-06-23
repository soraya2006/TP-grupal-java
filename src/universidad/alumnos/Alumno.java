package universidad.alumnos;

import java.io.Serializable;
import java.time.LocalDate;
import java.io.Serial;
import java.util.Objects;
import universidad.excepciones.*;

/**
 * Representa a un alumno dentro del sistema universitario.
 *
 * <p>Cada alumno se identifica de forma única por su matrícula. La clase implementa
 * {@link Comparable} para permitir la ordenación por apellido, nombre y matrícula,
 * y {@link Serializable} para soportar persistencia binaria.</p>
 */
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
    /**
     * Compara este alumno con otro para determinar su orden natural.
     *
     * <p>El criterio de ordenación es, en orden de prioridad:
     * <ol>
     *   <li>Apellido (sin distinción de mayúsculas).</li>
     *   <li>Nombre (sin distinción de mayúsculas), en caso de apellidos iguales.</li>
     *   <li>Matrícula (sin distinción de mayúsculas), en caso de apellido y nombre iguales.</li>
     * </ol>
     *
     * @param otro el alumno con el que se compara.
     * @return un valor negativo, cero o positivo según si este alumno es menor, igual o mayor que {@code otro}.
     */
    @Override
    public int compareTo(Alumno otro) {
        int comp = this.apellido.compareToIgnoreCase(otro.apellido);
        if (comp == 0) {
            comp = this.nombre.compareToIgnoreCase(otro.nombre);
        }
        if (comp == 0) {
            comp = this.matricula.compareToIgnoreCase(otro.matricula);
        }

        return comp;
    }
    /**
     * Indica si este alumno es igual a otro objeto.
     *
     * <p>Dos alumnos se consideran iguales si sus matrículas coinciden
     * sin distinción de mayúsculas y minúsculas.</p>
     *
     * @param o el objeto a comparar.
     * @return {@code true} si ambos alumnos tienen la misma matrícula; {@code false} en caso contrario.
     */
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
    /**
     * Devuelve una representación textual del alumno con apellido, nombre y matrícula.
     * @return cadena con el formato {@code "Apellido, Nombre (Matrícula: XXX)"}.
     */
    @Override
    public String toString() {
        return getApellido() + ", " + getNombre() + " (Matrícula: " + getMatricula() + ")";
    }
    /**
     * Devuelve el código hash del alumno, basado en su matrícula en minúsculas.
     *
     * <p>Es consistente con {@link #equals(Object)}: dos alumnos iguales
     * siempre producen el mismo hash.</p>
     *
     * @return el código hash.
     */
    @Override
    public int hashCode() {
        return Objects.hash(matricula != null ? matricula.toLowerCase() : null);
    }
}