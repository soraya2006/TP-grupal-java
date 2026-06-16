package universidad;

import universidad.alumnos.Alumno;
import universidad.alumnos.CondicionAlumno;
import universidad.ranking.RankingAsignatura;
import universidad.asistencias.Asistencia;
import universidad.asignaturas.Asignatura;
import universidad.asignaturas.Curso;
import universidad.clases.Clase;
import universidad.inscripciones.Inscripcion;
import universidad.excepciones.*;
import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
//JAVADOCS
/**
 * Clase principal que administra todo el sistema de la universidad.
 * Guarda las listas de alumnos, asignaturas y cursos.
 */
public class Universidad implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final Set<Alumno> alumnos;
    private final List<Asignatura> asignaturas;
    private final List<Curso> cursos;

    public Universidad() {
        alumnos = new TreeSet<>();
        asignaturas = new ArrayList<>();
        cursos = new ArrayList<>();
    }
    /**
     * Agrega un nuevo alumno al sistema.
     * @param a El objeto Alumno que se va a guardar.
     * @throws DatoInvalidoException Si el alumno es nulo.
     * @throws AlumnoDuplicadoException Si ya existe un alumno con esa matrícula.
     */
    public void agregarAlumno(Alumno a) { // agrega al alumno de forma ordenada
        if (a == null) {
            throw new ParametroNuloException("El alumno a agregar no puede ser nulo.");
        }
        for (Alumno existente : alumnos) {
            if (existente.getMatricula().equalsIgnoreCase(a.getMatricula())) {
                throw new AlumnoDuplicadoException();
            }
        }
        alumnos.add(a);
    }
    /**
     * Guarda una nueva materia en el sistema.
     * @param a La asignatura que se quiere agregar.
     */
    public void agregarAsignatura(Asignatura a) {
        boolean existe = false;
        int idx = 0;
        if (a == null) {
            throw new ParametroNuloException("La asignatura a agregar no puede ser nula.");
        }
        while (idx < asignaturas.size() && !existe) {
            if (asignaturas.get(idx).getCodigo().equalsIgnoreCase(a.getCodigo())) {
                existe = true;
            }
            idx++;
        }
        if (existe) {
            throw new AsignaturaDuplicadaException();
        }
        asignaturas.add(a);
    }
    /**
     * Abre un nuevo curso o comisión en el sistema.
     * @param c El curso a guardar.
     */
    public void agregarCurso(Curso c) {
        boolean existe = false;
        int idx = 0;
        if (c == null) {
            throw new ParametroNuloException("El curso a agregar no puede ser nulo.");
        }
        while (idx < cursos.size() && !existe) {
            if (cursos.get(idx).getIdCurso().equalsIgnoreCase(c.getIdCurso())) {
                existe = true;
            }
            idx++;
        }
        if (existe) {
            throw new CursoDuplicadoException();
        }
        cursos.add(c);
    }
    /**
     * Registra la asistencia (o inasistencia) de un alumno en un día de clases específico.
     * @param alumno   El estudiante al que se le toma asistencia.
     * @param clase    El día y horario de la clase.
     * @param curso    La comisión donde se dictó la clase.
     * @param presente Verdadero si vino, falso si faltó.
     */
    public void registrarAsistencia(Alumno alumno, Clase clase, Curso curso, boolean presente) {
        if (alumno == null || clase == null || curso == null) {
            throw new ParametroNuloException();
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
            throw new ClaseNoDictadaException("La clase especificada no pertenece a este curso.");
        }
        boolean encontrado = false;
        Inscripcion inscripcionActual;
        int i = 0;
        List<Inscripcion> inscripcionesCurso = curso.getInscripciones();
        while (i < inscripcionesCurso.size() && !encontrado) {
            inscripcionActual = inscripcionesCurso.get(i);
            if (inscripcionActual.getAlumno().equals(alumno)) {
                inscripcionActual.registrarAsistencia(clase, presente); // corregi esto, entonces pasamos la variable "presente" en ujar de un "true" fijo
                encontrado = true;
            }
            i++;
        }
        if (!encontrado) {
            throw new AlumnoNoInscriptoException("El alumno no está inscripto en este curso.");
        }
    }
    /**
     * Arma un listado con las materias ordenadas desde la que tiene mejor asistencia
     * hasta la que tiene peor asistencia.
     * @return Una lista ordenada con el ranking de presentismo.
     */
    public List<RankingAsignatura> rankingPresentismo() {
        List<RankingAsignatura> ranking;
        int totalAsistenciasPresentes, alumnosInscriptosEnCurso, totalClasesMaximasPosibles, clasesDictadasEnCurso;
        double porcentaje;
        ranking = new ArrayList<>();
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
    /**
     * Imprime en la consola un informe detallado de todos los alumnos de un curso.
     * @param curso El curso del que se quiere ver el reporte.
     */
    public void reporteAsignatura(Asignatura asignatura) {
        if (asignatura == null) {
            throw new ParametroNuloException("La asignatura no puede ser nula.");
        }

        boolean tieneCursos = false;

        for (Curso c : cursos) {
            if (c.getAsignatura().equals(asignatura)) {
                tieneCursos = true;

                System.out.println("========================================");
                System.out.println("Curso: " + c.getIdCurso()
                        + " | Año: " + c.getAnioCalendario()
                        + " | Cuatrimestre: " + c.getCuatrimestreDictado());
                System.out.println("Clases dictadas: " + c.getClasesDictadas().size());
                System.out.println("========================================");

                for (Inscripcion i : c.getInscripciones()) {
                    System.out.println("Alumno:     " + i.getAlumno());
                    System.out.println("Modalidad:  " + i.getModalidad());
                    System.out.println("Presentes:  " + i.cantidadPresentes()
                            + " / " + c.getClasesDictadas().size());
                    System.out.println("Porcentaje: " + i.calcularPorcentajeAsistencia() + "%");
                    System.out.println("Condición:  " + i.obtenerCondicion());
                    System.out.println("Asistencias detalladas:");
                    for (Asistencia a : i.getAsistencias()) {
                        System.out.println("  " + a);
                    }
                    System.out.println("----------------------------------------");
                }
            }
        }

        if (!tieneCursos) {
            System.out.println("No hay cursos registrados para la asignatura: " + asignatura.getNombre());
        }
    }
    /**
     * Muestra en pantalla todos los alumnos que quedaron libres en un año específico.
     * @param anio El año que se quiere consultar (ejemplo: 2026).
     */
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