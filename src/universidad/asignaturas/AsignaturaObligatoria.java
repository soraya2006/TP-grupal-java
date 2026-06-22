package universidad.asignaturas;

/**
 * Asignatura obligatoria y promocional.
 * Requiere 60% de asistencia para habilitar y 80% para promocionar.
 */
public class AsignaturaObligatoria extends Asignatura {

    /**
     * @param codigo       Código único de la asignatura.
     * @param nombre       Nombre de la asignatura.
     * @param cuatrimestre Cuatrimestre en que se dicta (1 a 10).
     */
    public AsignaturaObligatoria(String codigo, String nombre, int cuatrimestre, boolean promocional) {
        super(codigo, nombre, cuatrimestre, promocional);
    }

    /**
     * @return 60.0 — porcentaje mínimo para habilitar.
     */
    @Override
    public double porcentajeHabilitacion() {
        return 60.0;
    }
    @Override
    public String getTipo() {
        return "Obligatoria";
    }
    @Override
    public String getCodigoTipo() { return "OBLIGATORIA"; }
    /**
     * @return 80.0 — porcentaje mínimo para promocionar.
     */
    @Override
    public double porcentajePromocion() {
        return 80.0;
    }
}
