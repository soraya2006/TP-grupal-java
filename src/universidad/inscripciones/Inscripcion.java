package universidad.inscripciones;

import java.io.Serializable;
import java.util.ArrayList;
import java.io.Serial;
import universidad.alumnos.Alumno;
import universidad.alumnos.CondicionAlumno; 
import universidad.asignaturas.Asignatura;
import universidad.asignaturas.ModalidadCursada;
import universidad.asistencias.Asistencia;
import universidad.clases.Clase;
import universidad.asignaturas.Curso;
import universidad.excepciones.*;
/**
 * Es la ficha personal de un alumno dentro de un curso.
 * Acá se anota su modalidad (regular, oyente) y se guarda su lista de presentes y ausentes.
 */
public class Inscripcion implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    // son final porque, como se asignan una sola vez en el constructor y nunca cambia, protege la seguridad y blabla
    private final Alumno alumno;
    private final Curso curso;
    private final ModalidadCursada modalidad;
    private final ArrayList<Asistencia> asistencias;
    /**
     * Anota a un alumno en un curso.
     * @param alumno    El estudiante que se quiere anotar.
     * @param curso     El curso al que va a asistir.
     * @param modalidad Si cursa de forma regular, condicional u oyente.
     */
    public Inscripcion(Alumno alumno, Curso curso, ModalidadCursada modalidad) {
        if (alumno == null || curso == null || modalidad == null) {
            throw new ParametroNuloException();
        }else {
            this.alumno = alumno;
            this.curso = curso; // Guardamos la referencia al curso
            this.modalidad = modalidad;
            this.asistencias = new ArrayList<>();
        }
    }
    /**
     * Marca si el alumno vino o faltó a una clase.
     * @param c        La clase que se dictó.
     * @param presente Verdadero si vino, falso si faltó.
     */
    public void registrarAsistencia(Clase c, boolean presente) {
        if (c == null) {
            throw new ParametroNuloException("La clase a registrar no puede ser nula.");
        } else {
            boolean asistenciaDuplicada = false;
            int i = 0;
            while (i < asistencias.size() && !asistenciaDuplicada) {
                if (asistencias.get(i).getClase().getId().equalsIgnoreCase(c.getId())) {
                    asistenciaDuplicada = true;
                }
                i++;
            }
            if (asistenciaDuplicada) {
                throw new AsistenciaYaRegistradaException();

            } else {
                this.asistencias.add(new Asistencia(c, presente));
            }
        }
    }

    public int cantidadPresentes() {
        int cont = 0;
        for (Asistencia a : asistencias) {
            if (a.isPresente()) {
                cont++;
            }
        }
        return cont;
    }

    public double calcularPorcentajeAsistencia() {
        double porcentaje = 0.0;
        if (!curso.getClasesDictadas().isEmpty()) {
            porcentaje = (cantidadPresentes() * 100.0) / curso.getClasesDictadas().size();
        }
        return porcentaje;
    }
    /**
     * Revisa cuántas veces vino el alumno y las reglas de la materia para saber si aprobó.
     * @return El estado final del alumno (Libre, Habilita o Promociona).
     */
    public CondicionAlumno obtenerCondicion() {
        if (modalidad == ModalidadCursada.OYENTE) {
            return CondicionAlumno.LIBRE;
        } else{
            double asistencia = calcularPorcentajeAsistencia();
            double habilita = curso.getAsignatura().porcentajeHabilitacion();
            double promociona = curso.getAsignatura().porcentajePromocion();
            if (modalidad == ModalidadCursada.CONDICIONAL) {
                habilita += 20.0;
                if (promociona != -1.0) {
                    promociona += 20.0;
                }
            }
            boolean puedePromocionar = curso.getAsignatura().isPromocional() && promociona != -1.0;
            if (puedePromocionar && asistencia >= promociona) {
                return CondicionAlumno.PROMOCIONA;
            } else if (asistencia >= habilita) {
                return CondicionAlumno.HABILITA;
            } else {
                return CondicionAlumno.LIBRE;
            }
        }
    }
    public Alumno getAlumno() { return alumno; }
    public ModalidadCursada getModalidad() { return modalidad; }
    public Asignatura getAsignatura() { return curso.getAsignatura(); }
    public Curso getCurso() { return curso; }
    public ArrayList<Asistencia> getAsistencias() { return asistencias; }
}