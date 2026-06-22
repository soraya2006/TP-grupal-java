package universidad.asignaturas;
/**
 * Representa las distintas modalidades en las que un alumno puede cursar una asignatura.
 * * <p>Las modalidades influyen en el seguimiento de la asistencia y los requisitos
 * académicos del estudiante dentro del curso.</p>
 * */
public enum ModalidadCursada {
    /**
     * Modalidad estándar de cursada. El alumno debe cumplir con los porcentajes de
     * asistencia mínimos requeridos para habilitar o promocionar la asignatura.
     */
    REGULAR,
    /**
     * Modalidad condicional. El alumno cursa de manera provisional supeditado a
     * resoluciones administrativas o correlativas pendientes.
     */
    CONDICIONAL,
    /**
     * Modalidad de oyente. El alumno asiste a las clases para presenciar las explicaciones,
     * pero no cumple con instancias de evaluación formal obligatorias.
     */
    OYENTE
}