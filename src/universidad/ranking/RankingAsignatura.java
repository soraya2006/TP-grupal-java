package universidad.ranking;
import universidad.asignaturas.Asignatura;

public class RankingAsignatura {

    private final Asignatura asignatura;
    private final double porcentaje;

    public RankingAsignatura(Asignatura asignatura, double porcentaje) {
        this.asignatura = asignatura;
        this.porcentaje = porcentaje;
    }

    public double getPorcentaje() {
            return porcentaje;
        }
    /**
     * Retorna la asignatura asociada a este elemento del ranking.
     * @return La asignatura.
     */
    public Asignatura getAsignatura() {
        return asignatura;
    }

    @Override
    public String toString() {
        return asignatura.getNombre() + " -> " + porcentaje + "%";
    }
}

