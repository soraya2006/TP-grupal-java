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

        @Override
        public String toString() {
            return asignatura.getNombre() +
                    " -> " + porcentaje + "%";
        }
}

