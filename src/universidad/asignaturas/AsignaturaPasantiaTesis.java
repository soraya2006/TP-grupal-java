package universidad.asignaturas;

/**
 * Asignatura de tipo pasantía (práctica laboral) o tesis, no promocional.
 * Requiere 75% de asistencia para habilitar.
 */
public class AsignaturaPasantiaTesis extends Asignatura {
    /**
     * @param codigo       Código único de la asignatura.
     * @param nombre       Nombre de la asignatura.
     * @param cuatrimestre Cuatrimestre en que se dicta (1 a 10).
     */
    public AsignaturaPasantiaTesis(String codigo, String nombre, int cuatrimestre) {
        super(codigo, nombre, cuatrimestre, false); // Pasamos 'false' de forma fija
    }
    /**
     * @return 75.0 — porcentaje mínimo para habilitar.
     */
    @Override
    public String getTipo() {
        return "Pasantía / Tesis";
    }
    @Override
    public double porcentajeHabilitacion() {
        return 75.0;
    }
    /**
     * @return -1.0 — Asignatura no promocionable.
     */
    @Override
    public double porcentajePromocion() {
        return -1.0;
    }
}