
package universidad.sistema;

import universidad.alumnos.Alumno;
import universidad.asignaturas.Asignatura;
import universidad.clases.Clase;

import java.util.ArrayList;
import java.util.List;

public class Universidad {

    private List<Alumno> alumnos;
    private List<Asignatura> asignaturas;
    private List<Clase> clases;
    private List<Inscripcion> inscripciones;

    public Universidad() {
        alumnos = new ArrayList<>();
        asignaturas = new ArrayList<>();
        clases = new ArrayList<>();
        inscripciones = new ArrayList<>();
    }

    public void agregarAlumno(Alumno a) {
        alumnos.add(a);
    }

    public void agregarAsignatura(Asignatura a) {
        asignaturas.add(a);
    }

    public void agregarClase(Clase c) {
        clases.add(c);
    }

    public void agregarInscripcion(Inscripcion i) {
        inscripciones.add(i);
    }

    public void registrarAsistencia(Alumno alumno, Clase clase) {

        for(Inscripcion i : inscripciones) {

            if(i.getAlumno().equals(alumno)
                    && i.getAsignatura().equals(clase.getAsignatura())) {

                i.registrarAsistencia(clase.getId());
                return;
            }
        }

        throw new IllegalArgumentException(
                "El alumno no está inscripto en la asignatura");
    }

    public List<RankingAsignatura> rankingPresentismo(){

        List<RankingAsignatura> ranking = new ArrayList<>();

        for(Asignatura a : asignaturas){

            int totalAsistencias = 0;
            int totalClases = 0;
            int totalInscriptos = 0;

            for(Clase c : clases){

                if(c.getAsignatura().equals(a))
                    totalClases++;
            }

            for(Inscripcion i : inscripciones){

                if(i.getAsignatura().equals(a)){

                    totalInscriptos++;

                    totalAsistencias +=
                            i.getCantidadAsistencias();
                }
            }

            double porcentaje = 0;

            if(totalClases > 0 && totalInscriptos > 0){

                porcentaje =
                        (totalAsistencias * 100.0) /
                                (totalClases * totalInscriptos);
            }

            ranking.add(
                    new RankingAsignatura(a, porcentaje)
            );
        }

        ranking.sort((x,y) ->
                Double.compare(
                        y.getPorcentaje(),
                        x.getPorcentaje()
                )
        );

        return ranking;
    }

    public void reporteAsignatura(Asignatura asignatura){

        int totalClases = 0;

        for(Clase c : clases){

            if(c.getAsignatura().equals(asignatura))
                totalClases++;
        }

        for(Inscripcion i : inscripciones){

            if(i.getAsignatura().equals(asignatura)){

                double porcentaje =
                        i.calcularPorcentaje(totalClases);

                System.out.println(
                        i.getAlumno()
                );

                System.out.println(
                        "Asistencias: " +
                                i.getCantidadAsistencias()
                );

                System.out.println(
                        "Porcentaje: " +
                                porcentaje
                );

                System.out.println(
                        "Modalidad: " +
                                i.getModalidad()
                );

                System.out.println(
                        "Condicion: " +
                                i.obtenerCondicion(totalClases)
                );

                System.out.println("----------------");
            }
        }
    }

    public void alumnosLibres(){

        for(Inscripcion i : inscripciones){

            Asignatura a =
                    i.getAsignatura();

            int totalClases = 0;

            for(Clase c : clases){

                if(c.getAsignatura().equals(a))
                    totalClases++;
            }

            if(i.obtenerCondicion(totalClases)
                    == CondicionAlumno.LIBRE){

                System.out.println(
                        i.getAlumno() +
                                " - " +
                                a.getNombre()
                );
            }
        }
    }

    public void alumnosLibres(int anio){

        for(Inscripcion i : inscripciones){

            Asignatura a =
                    i.getAsignatura();

            int anioAsignatura =
                    (a.getCuatrimestre() + 1) / 2;

            if(anioAsignatura != anio)
                continue;

            int totalClases = 0;

            for(Clase c : clases){

                if(c.getAsignatura().equals(a))
                    totalClases++;
            }

            if(i.obtenerCondicion(totalClases)
                    == CondicionAlumno.LIBRE){

                System.out.println(
                        i.getAlumno() +
                                " - " +
                                a.getNombre()
                );
            }
        }
    }
}
