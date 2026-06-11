package universidad.asignaturas;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import universidad.asignaturas.Asignatura;
import universidad.clases.Clase;
import universidad.inscripciones.Inscripcion;
public class Curso implements Serializable {
    private static final long serialVersionUID = 1L;
    private String idCurso; 
    private Asignatura asignatura;
    private int anioCalendario; 
    private int cuatrimestreDictado; 
    private List<Clase> clasesDictadas;
    private List<Inscripcion> inscripciones;
    public Curso(String idCurso, Asignatura asignatura, int anioCalendario, int cuatrimestreDictado) {
        if (idCurso == null || idCurso.isBlank()) {
            throw new IllegalArgumentException("El ID del curso no puede estar vacío.");
        }
        if (asignatura == null) {
            throw new IllegalArgumentException("La asignatura asociada no puede ser nula.");
        }
        if (cuatrimestreDictado < 1 || cuatrimestreDictado > 2) {
            throw new IllegalArgumentException("El cuatrimestre de dictado debe ser 1 o 2.");
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
    public void agregarClase(Clase c){
        boolean existe = false;
        int i = 0;
        if (c == null){
            throw new IllegalArgumentException("La clase no puede ser nula.");
        }
        while (i < clasesDictadas.size() && !existe) {
            if (clasesDictadas.get(i).getId().equalsIgnoreCase(c.getId())) {
                existe = true;
            }
            i++;
        }
        if (existe) {
            throw new IllegalArgumentException("La clase con ese ID ya está registrada en este curso.");
        }
        clasesDictadas.add(c);
    }
    public void agregarInscripcion(Inscripcion insc){
        boolean existe = false;
        int i = 0;
        if (insc == null){
            throw new IllegalArgumentException("La inscripción no puede ser nula.");
        }
        while(i < inscripciones.size() && !existe){
            if(inscripciones.get(i).getAlumno().equals(insc.getAlumno())){
                existe = true;
            }
            i++
        }
        if(existe){
            throw new IllegalArgumentException("El alumno ya se encuentra inscripto en este curso.");
        }
        inscripciones.add(insc);
    }
}
