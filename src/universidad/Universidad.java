
package universidad;

import universidad.alumnos.Alumno;
import universidad.alumnos.CondicionAlumno;
import universidad.ranking.RankingAsignatura;
import universidad.asignaturas.Asignatura;
import universidad.clases.Clase;
import universidad.inscripciones.Inscripcion;

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

    public void registrarAsistencia(Alumno alumno, Clase clase, Asignatura asignatura) {

        for(Inscripcion i : inscripciones) {

            if(i.getAlumno().equals(alumno)
                    && i.getAsignatura().equals(asignatura)) {
                i.registrarAsistencia(clase, true);
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

            for(Inscripcion i : inscripciones){
                if(i.getAsignatura().equals(a)){
                    totalInscriptos++;
                    totalAsistencias += i.cantidadPresentes();
                    totalClases += i.getAsistencias().size();
                }
            }

            double porcentaje = 0.0;

            if(totalClases > 0 && totalInscriptos > 0){
                porcentaje = (totalAsistencias * 100.0) /totalClases;
            }

            ranking.add(new RankingAsignatura(a, porcentaje));
        }
    ranking.sort((x, y) -> Double.compare(y.getPorcentaje(), x.getPorcentaje()));    
        return ranking;
    }

    public void reporteAsignatura(Asignatura asignatura) {
        for (Inscripcion i : inscripciones) {
            if (i.getAsignatura().equals(asignatura)) {
                System.out.println("Alumno: " + i.getAlumno());
                System.out.println("Asistencias: " + i.cantidadPresentes());
                System.out.println("Porcentaje: " + i.calcularPorcentajeAsistencia() + "%");
                System.out.println("Modalidad: " + i.getModalidad());
                System.out.println("Condición: " + i.obtenerCondicion());
                System.out.println("----------------------------------------");
            }
        }
    }

    public void alumnosLibres() {
        for (Inscripcion i : inscripciones) {
            if (i.obtenerCondicion() == CondicionAlumno.LIBRE) {
                System.out.println(i.getAlumno() + " - " + i.getAsignatura().getNombre());
            }
        }
    }

    public void alumnosLibres(int anio) {
        for (Inscripcion i : inscripciones) {
            Asignatura a = i.getAsignatura();
            int anioAsignatura = (a.getCuatrimestre() + 1) / 2;

            if (anioAsignatura == anio) {
                if (i.obtenerCondicion() == CondicionAlumno.LIBRE) {
                    System.out.println(i.getAlumno() + " - " + a.getNombre() + " (Año: " + anioAsignatura + ")");
                }
            }
        }
    }
}
