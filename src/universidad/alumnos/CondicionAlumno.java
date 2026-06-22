package universidad.alumnos;
/**
 * Representa la condición académica final de un alumno en una asignatura.
 * * <p>Esta condición se calcula dinámicamente en base al porcentaje de asistencia acumulado
 * por el alumno, la modalidad de su cursada y la categoría de la asignatura correspondiente.</p>

 */
public enum CondicionAlumno {
    /**
     * El alumno no alcanzó el mínimo de asistencia requerido para habilitar la materia
     * y pierde la regularidad de la cursada.
     */
    LIBRE,
    /**
     * El alumno cumple con la asistencia mínima para mantener la regularidad y queda en
     * condiciones de rendir el examen final o habilitar la materia, pero sin alcanzar la promoción.
     */
    HABILITA,
    /**
     * El alumno superó el umbral exigente de asistencia requerido y, de cumplir las condiciones,
     * puede acreditar la materia de forma directa sin examen final (siempre que sea promocional).
     */
    PROMOCIONA
}

