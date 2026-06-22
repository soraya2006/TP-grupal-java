package universidad.asignaturas;
/**
 * Asignatura optativa y promocional.
 * Requiere 50% de asistencia para habilitar y 60% para promocionar.
 */
public class AsignaturaOptativa extends Asignatura {

    /**
     * @param codigo       Código único de la asignatura.
     * @param nombre       Nombre de la asignatura.
     * @param cuatrimestre Cuatrimestre en que se dicta (1 a 10).
     */
    public AsignaturaOptativa(String codigo, String nombre, int cuatrimestre, boolean promocional) {
        super(codigo, nombre, cuatrimestre, promocional);
    }
    /**
     * @return 50.0 — porcentaje mínimo para habilitar.
     */
    @Override
    public String getTipo() {
        return "Optativa";
    }
    @Override
    public String getCodigoTipo() { return "OPTATIVA"; }
    @Override
    public double porcentajeHabilitacion() {
        return 50.0;
    }
    /**
     * @return 60.0 — porcentaje mínimo para promocionar.
     */
    @Override
    public double porcentajePromocion() {
        return 60.0;
    }
}
