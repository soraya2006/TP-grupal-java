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
            throw new IllegalArgumentException("El alumno no puede ser nulo.");
        }
        while (idx < alumnos.size() && !existe) {
            if (alumnos.get(idx).getMatricula().equalsIgnoreCase(a.getMatricula())) {
                existe = true;
            }
            idx++;
        }
        if (existe) {
            throw new IllegalArgumentException("Ya existe un alumno con esa matrícula.");
        }
        alumnos.add(a);
    }

    public void agregarAsignatura(Asignatura a) {
        boolean existe = false;
        int idx = 0;
        if (a == null) {
            throw new IllegalArgumentException("La asignatura no puede ser nula.");
        }
        while (idx < asignaturas.size() && !existe) {
            if (asignaturas.get(idx).getCodigo().equalsIgnoreCase(a.getCodigo())) {
                existe = true;
            }
            idx++;
        }
        if (existe) {
            throw new IllegalArgumentException("Ya existe una asignatura con ese código.");
        }
        asignaturas.add(a);
    }

    public void agregarCurso(Curso c) {
        boolean existe = false;
        int idx = 0;
        if (c == null) {
            throw new IllegalArgumentException("El curso no puede ser nulo.");
        }
        while (idx < cursos.size() && !existe) {
            if (cursos.get(idx).getIdCurso().equalsIgnoreCase(c.getIdCurso())) {
                existe = true;
            }
            idx++;
        }
        if (existe) {
            throw new IllegalArgumentException("Ya existe un curso registrado con ese ID.");
        }
        cursos.add(c);
    }

    public void registrarAsistencia(Alumno alumno, Clase clase, Curso curso) {
        if (alumno == null || clase == null || curso == null) {
            throw new IllegalArgumentException("Ninguno de los parámetros puede ser nulo.");
        }
        boolean clasePerteneceAlCurso = false;
        int j = 0;
        List<Clase> clasesDelCurso = curso.getClasesDictadas();
        while (j < clasesDelCurso.size() && !clasePerteneceAlCurso) {
            if (clasesDelCurso.get(j).getId().equalsIgnoreCase(clase.getId())) {
                clasePerteneceAlCurso = true;
            }
            j++;
        }
        if (!clasePerteneceAlCurso) {
            throw new IllegalArgumentException("La clase especificada no pertenece a este curso.");
        }
        boolean encontrado = false;
        Inscripcion inscripcionActual;
        int i = 0;
        List<Inscripcion> inscripcionesCurso = curso.getInscripciones();
        while (i < inscripcionesCurso.size() && !encontrado) {
            inscripcionActual = inscripcionesCurso.get(i);
            if (inscripcionActual.getAlumno().equals(alumno)) {
                // Pasamos el control a Inscripcion, que ahora validará duplicados
                inscripcionActual.registrarAsistencia(clase, true);
                encontrado = true;
            }
            i++;
        }
        if (!encontrado) {
            throw new IllegalArgumentException("El alumno no está inscripto en este curso.");
        }
    }

    public List<RankingAsignatura> rankingPresentismo() {
        List<RankingAsignatura> ranking = new ArrayList<>();
        int totalAsistenciasPresentes,alumnosInscriptosEnCurso, totalClasesMaximasPosibles, clasesDictadasEnCurso;
        double porcentaje;
        for (Asignatura a : asignaturas) {
            totalAsistenciasPresentes = 0;
            totalClasesMaximasPosibles = 0;
            porcentaje = 0.0;
            for (Curso c : cursos) {
                if (c.getAsignatura().equals(a)) {
                    clasesDictadasEnCurso = c.getClasesDictadas().size();
                    alumnosInscriptosEnCurso = c.getInscripciones().size();
                    totalClasesMaximasPosibles += (clasesDictadasEnCurso * alumnosInscriptosEnCurso);
                    for (Inscripcion insc : c.getInscripciones()) {
                        totalAsistenciasPresentes += insc.cantidadPresentes();
                    }
                }
            }
            if (totalClasesMaximasPosibles > 0) {
                porcentaje = (totalAsistenciasPresentes * 100.0) / totalClasesMaximasPosibles;
            }
            ranking.add(new RankingAsignatura(a, porcentaje));
        }
        
        ranking.sort((x, y) -> Double.compare(y.getPorcentaje(), x.getPorcentaje()));    
        return ranking;
    }

    public void reporteAsignatura(Curso curso) {
        for (Inscripcion i : curso.getInscripciones()) {
            System.out.println("Alumno: " + i.getAlumno());
            System.out.println("Asistencias: " + i.cantidadPresentes());
            System.out.println("Porcentaje: " + i.calcularPorcentajeAsistencia() + "%");
            System.out.println("Modalidad: " + i.getModalidad());
            System.out.println("Condición: " + i.obtenerCondicion());
            System.out.println("----------------------------------------");
        }
    }

    public void alumnosLibres() {
        for (Curso c : cursos) {
            for (Inscripcion i : c.getInscripciones()) {
                if (i.obtenerCondicion() == CondicionAlumno.LIBRE) {
                    System.out.println(i.getAlumno() + " - " + c.getAsignatura().getNombre() + " (Curso: " + c.getIdCurso() + ")");
                }
            }
        }
    }

    public void alumnosLibres(int anio) {
        for (Curso c : cursos) {
            if (c.getAnioCalendario() == anio) {
                for (Inscripcion i : c.getInscripciones()) {
                    if (i.obtenerCondicion() == CondicionAlumno.LIBRE) {
                        System.out.println(i.getAlumno() + " - " + c.getAsignatura().getNombre() + " (Año: " + anio + ")");
                    }
                }
            }
        }
    }
}