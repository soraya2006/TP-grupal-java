package universidad;

import universidad.alumnos.Alumno;
import universidad.alumnos.CondicionAlumno;
import universidad.ranking.RankingAsignatura;
import universidad.asignaturas.Asignatura;
import universidad.asignaturas.Curso;
import universidad.clases.Clase;
import universidad.inscripciones.Inscripcion;

import java.util.ArrayList;
import java.util.List;

public class Universidad {
    private List<Alumno> alumnos;
    private List<Asignatura> asignaturas;
    private List<Curso> cursos;

    public Universidad() {
        alumnos = new ArrayList<>();
        asignaturas = new ArrayList<>();
        cursos = new ArrayList<>();
    }
    public void agregarAlumno(Alumno a) {
        boolean existe = false;
        int idx = 0;
        if (a == null) {
            throw new IllegalArgumentException("El alumno a agregar no puede ser nulo.");
        }
        while (idx < alumnos.size() && !existe) {
            if (alumnos.get(idx).getMatricula().equalsIgnoreCase(a.getMatricula())) {
                existe = true;
            }
            idx++;
        }
        if (existe) {
            throw new IllegalArgumentException("Ya existe un alumno registrado con esa matrícula.");
        }
        alumnos.add(a);
    }
 // -- empezar a corregir dsde aca
    public void agregarAsignatura(Asignatura a) {
        boolean existe = false;
        int idx = 0;
        if (a == null) {
            throw new IllegalArgumentException("La asignatura a agregar no puede ser nula.");
        }
        while (idx < asignaturas.size() && !existe) {
            if (asignaturas.get(idx).getCodigo().equalsIgnoreCase(a.getCodigo())) {
                existe = true;
            }
            idx++;
        }
        if (existe) {
            throw new IllegalArgumentException("Ya existe una asignatura registrada con ese código.");
        }
        asignaturas.add(a);
    }
    public void agregarClase(Clase c) {
        clases.add(c);
    }
    public void agregarInscripcion(Inscripcion i) {
        inscripciones.add(i);
    }
    public void registrarAsistencia(Alumno alumno, Clase clase, Asignatura asignatura) {
        boolean encontrado = false;
        int i = 0;
        Inscripcion inscripcionActual;
        while (i < inscripciones.size() && !encontrado) {
            inscripcionActual = inscripciones.get(i);
            
            if (inscripcionActual.getAlumno().equals(alumno) && inscripcionActual.getAsignatura().equals(asignatura)) {
                inscripcionActual.registrarAsistencia(clase, true);
                encontrado = true; 
            }
            i++;
        }
        if (!encontrado) {
            throw new IllegalArgumentException("El alumno no está inscripto en la asignatura");
        }
    }

    public List<RankingAsignatura> rankingPresentismo(){
        int totalAsistencias;
        int totalClasesMaximas;
        double porcentaje;
        List<RankingAsignatura> ranking = new ArrayList<>();
        for (Asignatura a : asignaturas) {
            totalAsistencias = 0;
            totalClasesMaximas = 0;
            porcentaje = 0.0;
            for (Inscripcion i : inscripciones) {
                if (i.getAsignatura().equals(a)) {
                    totalAsistencias += i.cantidadPresentes();
                    totalClasesMaximas += i.getAsistencias().size(); 
                }
            }
            if (totalClasesMaximas > 0) {
                porcentaje = (totalAsistencias * 100.0) / totalClasesMaximas;
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
        int anioAsignatura;
        Asignatura a
        for (Inscripcion i : inscripciones) {
            a = i.getAsignatura();
            anioAsignatura = (a.getCuatrimestre() + 1) / 2;
            if (anioAsignatura == anio) {
                if (i.obtenerCondicion() == CondicionAlumno.LIBRE) {
                    System.out.println(i.getAlumno() + " - " + a.getNombre() + " (Año: " + anioAsignatura + ")");
                }
            }
        }
    }
}