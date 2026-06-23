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
import java.util.*;

/**
 * Clase principal que administra todo el sistema de la universidad.
 * Guarda las listas de alumnos, asignaturas y cursos.
 *
 * <p><b>Importante:</b> esta clase pertenece exclusivamente a la capa de
 * dominio. Ningún método imprime ni lee datos de ningún dispositivo de
 * entrada/salida. Toda la presentación queda a cargo de la capa de
 * interfaz de usuario (Main, GeneradorReportes, etc.).</p>
 */
public class Universidad implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Set<Alumno> alumnos;
    private final List<Asignatura> asignaturas;
    private final List<Curso> cursos;

    public Universidad() {
        alumnos     = new TreeSet<>();
        asignaturas = new ArrayList<>();
        cursos      = new ArrayList<>();
    }
    /**
     * Agrega un nuevo alumno al sistema en orden alfabético.
     *
     * @param a El alumno a agregar.
     * @throws ParametroNuloException   Si el alumno es nulo.
     * @throws AlumnoDuplicadoException Si ya existe un alumno con esa matrícula.
     */
    public void agregarAlumno(Alumno a) {
        if (a == null) {
            throw new ParametroNuloException("El alumno a agregar no puede ser nulo.");
        }

        boolean duplicado = false;
        for (Alumno existente : alumnos) {
            if (!duplicado && existente.getMatricula().equalsIgnoreCase(a.getMatricula())) {
                duplicado = true;
            }
        }

        if (duplicado) {
            throw new AlumnoDuplicadoException();
        }

        alumnos.add(a);
    }

    /**
     * Agrega una nueva asignatura al sistema.
     *
     * @param a La asignatura a agregar.
     * @throws ParametroNuloException      Si la asignatura es nula.
     * @throws AsignaturaDuplicadaException Si ya existe una asignatura con ese código.
     */
    public void agregarAsignatura(Asignatura a) {
        if (a == null) {
            throw new ParametroNuloException("La asignatura a agregar no puede ser nula.");
        }

        boolean duplicada = false;
        int idx = 0;
        while (idx < asignaturas.size() && !duplicada) {
            if (asignaturas.get(idx).getCodigo().equalsIgnoreCase(a.getCodigo())) {
                duplicada = true;
            }
            idx++;
        }
        if (duplicada) {
            throw new AsignaturaDuplicadaException();
        }
        asignaturas.add(a);
    }

    /**
     * Agrega un nuevo curso al sistema.
     *
     * @param c El curso a agregar.
     * @throws ParametroNuloException  Si el curso es nulo.
     * @throws CursoDuplicadoException Si ya existe un curso con ese ID.
     */
    public void agregarCurso(Curso c) {
        if (c == null) {
            throw new ParametroNuloException("El curso a agregar no puede ser nulo.");
        }

        boolean duplicado = false;
        int idx = 0;
        while (idx < cursos.size() && !duplicado) {
            if (cursos.get(idx).getIdCurso().equalsIgnoreCase(c.getIdCurso())) {
                duplicado = true;
            }
            idx++;
        }

        if (duplicado) {
            throw new CursoDuplicadoException();
        }

        cursos.add(c);
    }

    /**
     * Registra la asistencia (o inasistencia) de un alumno a una clase.
     *
     * @param alumno   El alumno al que se le registra la asistencia.
     * @param clase    La clase a la que asistió o faltó.
     * @param curso    El curso al que pertenece la clase.
     * @param presente {@code true} si asistió, {@code false} si faltó.
     * @throws ParametroNuloException       Si alguno de los parámetros es nulo.
     * @throws ClaseNoDictadaException      Si la clase no pertenece al curso.
     * @throws AlumnoNoInscriptoException   Si el alumno no está inscripto en el curso.
     */
    public void registrarAsistencia(Alumno alumno, Clase clase, Curso curso, boolean presente) {
        if (alumno == null || clase == null || curso == null) {
            throw new ParametroNuloException();
        } else {
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
            } else {
                boolean encontrado = false;
                int i = 0;
                List<Inscripcion> inscripcionesCurso = curso.getInscripciones();
                while (i < inscripcionesCurso.size() && !encontrado) {
                    Inscripcion inscripcionActual = inscripcionesCurso.get(i);
                    if (inscripcionActual.getAlumno().equals(alumno)) {
                        inscripcionActual.registrarAsistencia(clase, presente);
                        encontrado = true;
                    }
                    i++;
                }
                if (!encontrado) {
                    throw new AlumnoNoInscriptoException("El alumno no está inscripto en este curso.");
                }
            }
        }
    }

    /**
     * Calcula el ranking de asignaturas ordenado de mayor a menor
     * porcentaje de presentismo.
     *
     * @return Lista ordenada de {@link RankingAsignatura}.
     */
    public List<RankingAsignatura> rankingPresentismo() {
        List<RankingAsignatura> ranking = new ArrayList<>();

        for (Asignatura a : asignaturas) {
            int totalPresentes              = 0;
            int totalClasesMaximasPosibles  = 0;

            for (Curso c : cursos) {
                if (c.getAsignatura().equals(a)) {
                    int clasesDictadas        = c.getClasesDictadas().size();
                    int alumnosInscriptos     = c.getInscripciones().size();
                    totalClasesMaximasPosibles += clasesDictadas * alumnosInscriptos;

                    for (Inscripcion insc : c.getInscripciones()) {
                        totalPresentes += insc.cantidadPresentes();
                    }
                }
            }

            double porcentaje = 0.0;
            if (totalClasesMaximasPosibles > 0) {
                porcentaje = (totalPresentes * 100.0) / totalClasesMaximasPosibles;
            }

            ranking.add(new RankingAsignatura(a, porcentaje));
        }

        ranking.sort((x, y) -> Double.compare(y.getPorcentaje(), x.getPorcentaje()));
        return ranking;
    }

    /**
     * Devuelve todas las inscripciones activas en los cursos de una asignatura.
     *
     * <p>La capa de presentación recibe esta lista y decide cómo mostrarla
     * (consola, archivo, ventana gráfica, etc.).</p>
     *
     * <p>Cada {@link Inscripcion} contiene: el alumno, la modalidad, la lista
     * de asistencias y el curso al que pertenece, por lo que la interfaz tiene
     * acceso a todos los datos necesarios para construir el reporte.</p>
     *
     * @param asignatura La asignatura a consultar.
     * @return Lista de inscripciones encontradas. Lista vacía si no hay cursos
     *         o si la asignatura no tiene alumnos inscriptos.
     * @throws ParametroNuloException Si la asignatura es nula.
     */
    public List<Inscripcion> obtenerInscripcionesPorAsignatura(Asignatura asignatura) {
        if (asignatura == null) {
            throw new ParametroNuloException("La asignatura no puede ser nula.");
        } else {
            List<Inscripcion> resultado = new ArrayList<>();
            for (Curso c : cursos) {
                if (c.getAsignatura().equals(asignatura)) {
                    for (Inscripcion i : c.getInscripciones()) {
                        resultado.add(i);
                    }
                }
            }
            return resultado;
        }

    }

    /**
     * Devuelve todas las inscripciones cuya condición final es {@link CondicionAlumno#LIBRE},
     * sin importar la asignatura ni el año.
     *
     * @return Lista de inscripciones con condición LIBRE.
     *         Lista vacía si no hay ninguna.
     */
    public List<Inscripcion> alumnosLibres() {
        List<Inscripcion> resultado = new ArrayList<>();

        for (Curso c : cursos) {
            for (Inscripcion i : c.getInscripciones()) {
                if (i.obtenerCondicion() == CondicionAlumno.LIBRE) {
                    resultado.add(i);
                }
            }
        }
        return resultado;
    }

    /**
     * Devuelve las inscripciones con condición LIBRE correspondientes a
     * asignaturas del año académico indicado.
     *
     * El año académico se calcula a partir del cuatrimestre de la asignatura:
     * cuatrimestres 1-2 → año 1, cuatrimestres 3-4 → año 2, etc.
     * Fórmula: (cuatrimestre - 1) / 2 + 1
     *
     * @param anioCarrera El año académico a filtrar (1 a 5).
     * @return Lista de inscripciones LIBRE para ese año. Lista vacía si no hay ninguna.
     */
    public List<Inscripcion> alumnosLibres(int anioCarrera) {
        if (anioCarrera < 1 || anioCarrera > 5) {
            throw new DatoInvalidoException("El año de carrera debe estar comprendido entre 1 y 5.");
        } else {
            List<Inscripcion> resultado = new ArrayList<>();
            for (Curso c : cursos) {
                int cuatrimestre = c.getAsignatura().getCuatrimestre();
                int anioAcademico = (cuatrimestre - 1) / 2 + 1;
                if (anioAcademico == anioCarrera) {
                    for (Inscripcion i : c.getInscripciones()) {
                        if (i.obtenerCondicion() == CondicionAlumno.LIBRE) {
                            resultado.add(i);
                        }
                    }
                }
            }
            return resultado;
        }
    }
    /**
     * Retorna el conjunto de alumnos ordenado alfabéticamente.
     *
     * @return Vista no modificable del conjunto de alumnos.
     */
    public Set<Alumno> getAlumnos() {
        return Collections.unmodifiableSet(alumnos);
    }

    /**
     * Retorna la lista de asignaturas registradas.
     *
     * @return Vista no modificable de la lista de asignaturas.
     */
    public List<Asignatura> getAsignaturas() {
        return Collections.unmodifiableList(asignaturas);
    }

    /**
     * Retorna la lista de cursos registrados.
     *
     * @return Vista no modificable de la lista de cursos.
     */
    public List<Curso> getCursos() {
        return Collections.unmodifiableList(cursos);
    }
}