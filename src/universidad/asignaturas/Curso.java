package universidad.asignaturas;
import java.io.Serializable;
import java.io.Serial;
import java.util.ArrayList;
import java.util.List;
import universidad.clases.Clase;
import universidad.inscripciones.Inscripcion;
import universidad.excepciones.*;

public class Curso implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private final String idCurso;
    private final Asignatura asignatura;
    private final int anioCalendario;
    private final int cuatrimestreDictado;
    private final List<Clase> clasesDictadas;
    private final List<Inscripcion> inscripciones;

    public Curso(String idCurso, Asignatura asignatura, int anioCalendario, int cuatrimestreDictado) {
        if (idCurso == null || idCurso.isBlank()) {
            throw new ParametroNuloException("El ID del curso no puede estar vacío.");
        }
        if (asignatura == null) {
            throw new ParametroNuloException("La asignatura asociada no puede ser nula.");
        }
        if (cuatrimestreDictado < 1 || cuatrimestreDictado > 2) {
            throw new DatoInvalidoException("Cuatrimestre invalido. El cuatrimestre de dictado debe ser 1 o 2.");
        }
        this.idCurso = idCurso;
        this.asignatura = asignatura;
        this.anioCalendario = anioCalendario;
        this.cuatrimestreDictado = cuatrimestreDictado;
        this.clasesDictadas = new ArrayList<>();
        this.inscripciones = new ArrayList<>();
    }

    public String getIdCurso() {
        return idCurso;
    }

    public Asignatura getAsignatura() {
        return asignatura;
    }

    public int getAnioCalendario() {
        return anioCalendario;
    }

    public int getCuatrimestreDictado() {
        return cuatrimestreDictado;
    }

    public List<Clase> getClasesDictadas() {
        return clasesDictadas;
    }

    public List<Inscripcion> getInscripciones() {
        return inscripciones;
    }

    public void agregarClase(Clase c) {
        boolean existe = false;
        int i = 0;
        if (c == null) {
            throw new ParametroNuloException("La clase no puede ser nula.");
        }
        while (i < clasesDictadas.size() && !existe) {
            if (clasesDictadas.get(i).getId().equalsIgnoreCase(c.getId())) {
                existe = true;
            }
            i++;
        }
        if (existe) {
            throw new ClaseDuplicadaException();
        }
        clasesDictadas.add(c);
    }

    public void agregarInscripcion(Inscripcion insc) {
        boolean existe = false;
        int i = 0;
        if (insc == null) {
            throw new ParametroNuloException("La inscripción no puede ser nula.");
        }
        if (insc.getCurso() != this) {
            throw new InscripcionDuplicada();
        }

        while (i < inscripciones.size() && !existe) {
            if (inscripciones.get(i).getAlumno().equals(insc.getAlumno())) {
                existe = true;
            }
            i++;
        }
        if (existe) {
            throw new AlumnoDuplicadoException();
        }
        inscripciones.add(insc);
    }
}