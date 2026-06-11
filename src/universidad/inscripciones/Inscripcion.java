package universidad.inscripciones;

import java.io.Serializable;
import java.util.ArrayList; 
import universidad.alumnos.Alumno;
import universidad.alumnos.CondicionAlumno; 
import universidad.asignaturas.Asignatura;
import universidad.asignaturas.ModalidadCursada;
import universidad.asistencias.Asistencia;
import universidad.clases.Clase;

public class Inscripcion implements Serializable {
    private static final long serialVersionUID = 1L;

    private Alumno alumno;
    private Asignatura asignatura;
    private ModalidadCursada modalidad;
    private ArrayList<Asistencia> asistencias;

    public Inscripcion(Alumno alumno, Asignatura asignatura, ModalidadCursada modalidad) {
        this.alumno = alumno;
        this.asignatura = asignatura;
        this.modalidad = modalidad;
        this.asistencias = new ArrayList<>(); 
    }

    public void registrarAsistencia(Clase c, boolean presente) {
        this.asistencias.add(new Asistencia(c, presente));
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
        if (asistencias.size() != 0) {
            porcentaje = (cantidadPresentes() * 100.0) / asistencias.size();
        }
        return porcentaje;
    }

    public CondicionAlumno obtenerCondicion() {
        CondicionAlumno condicionFinal = CondicionAlumno.LIBRE;
        double asistencia = calcularPorcentajeAsistencia(), habilita = 0.0, promociona = 0.0;
        if (modalidad == ModalidadCursada.OYENTE) {
            condicionFinal = CondicionAlumno.LIBRE; 
        } else {
            habilita = asignatura.porcentajeHabilitacion();
            promociona = asignatura.porcentajePromocion();
            if (modalidad == ModalidadCursada.CONDICIONAL) {
                habilita += 20.0; 
                if (promociona != -1.0) {
                    promociona += 20.0;
                }
            }   
            if (asignatura.isPromocional() && promociona != -1.0 && asistencia >= promociona) {
                condicionFinal = CondicionAlumno.PROMOCIONA;
            } else if (asistencia >= habilita) {
                condicionFinal = CondicionAlumno.HABILITA;
            } else {
                condicionFinal = CondicionAlumno.LIBRE;
            }
        }
        return condicionFinal;
    }
    public Alumno getAlumno() { return alumno; }
    public Asignatura getAsignatura() { return asignatura; }
    public ModalidadCursada getModalidad() { return modalidad; }
    public ArrayList<Asistencia> getAsistencias() { return asistencias; }
}