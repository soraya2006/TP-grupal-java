package universidad.ranking;


public class RankingAsignatura {

        private Asignatura asignatura;
        private double porcentaje;

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

