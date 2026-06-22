package UI;

import universidad.asignaturas.Asignatura;
import universidad.asignaturas.Curso;
import universidad.asistencias.Asistencia;
import universidad.inscripciones.Inscripcion;
import universidad.ranking.RankingAsignatura;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Genera reportes del sistema en archivos de texto plano.
 *
 * <p>Esta clase pertenece a la capa de presentación. Recibe datos ya
 * calculados por la capa de dominio ({@link universidad.Universidad}) y los
 * escribe en archivos. No realiza ningún cálculo de negocio.</p>
 *
 * <p>Si el archivo no puede crearse se informa el error por consola
 * sin interrumpir la ejecución del programa.</p>
 */
public class GeneradorReportes {

    /** Carpeta donde se guardan todos los reportes. Se crea si no existe. */
    private static final String CARPETA = "reportes/";

    /**
     * Escribe en archivo el ranking de asignaturas ordenado por presentismo.
     *
     * @param ranking Lista ya ordenada de mayor a menor porcentaje,
     *                producida por {@code Universidad.rankingPresentismo()}.
     */
    public static void guardarRankingPresentismo(List<RankingAsignatura> ranking) {
        String ruta = CARPETA + "ranking_presentismo.txt";
        PrintWriter pw = abrirArchivo(ruta);

        if (pw != null) {
            pw.println("   RANKING DE ASIGNATURAS POR PRESENTISMO");

            if (ranking.isEmpty()) {
                pw.println("  (Sin datos)");
            } else {
                int posicion = 1;
                for (RankingAsignatura r : ranking) {
                    pw.printf("  %2d. %-38s %6.2f%%%n",
                            posicion,
                            r.getAsignatura().getNombre(),
                            r.getPorcentaje());
                    posicion++;
                }
            }

            pw.println("========================================");
            cerrarArchivo(pw, ruta);
        }
    }

    /**
     * Escribe en archivo el reporte detallado de alumnos de una asignatura.
     *
     * <p>Recibe la lista de inscripciones producida por
     * {@code Universidad.obtenerInscripcionesPorAsignatura()}.</p>
     *
     * @param asignatura   La asignatura cuyo reporte se genera.
     * @param inscripciones Lista de inscripciones de esa asignatura.
     */
    public static void guardarReporteAsignatura(Asignatura asignatura,
                                                List<Inscripcion> inscripciones) {
        String nombreArchivo = "reporte_"
                + asignatura.getCodigo().replaceAll("[^a-zA-Z0-9]", "_")
                + ".txt";
        String ruta = CARPETA + nombreArchivo;
        PrintWriter pw = abrirArchivo(ruta);

        if (pw != null) {
            pw.println("========================================");
            pw.println("  REPORTE DE ASIGNATURA: " + asignatura.getNombre());
            pw.println("  Código: " + asignatura.getCodigo()
                    + " | Tipo: " + asignatura.getTipo());
            pw.println("========================================");
            if (inscripciones.isEmpty()) {
                pw.println("  No hay alumnos inscriptos en esta asignatura.");
            } else {
                String idCursoActual = "";
                for (Inscripcion i : inscripciones) {
                    Curso c = i.getCurso();

                    if (!c.getIdCurso().equals(idCursoActual)) {
                        idCursoActual = c.getIdCurso();
                        pw.println();
                        pw.println("  Curso: " + c.getIdCurso()
                                + " | Año: "          + c.getAnioCalendario()
                                + " | Cuatrimestre: " + c.getCuatrimestreDictado());
                        pw.println("  Clases dictadas: " + c.getClasesDictadas().size());
                    }

                    pw.println("  Alumno:     " + i.getAlumno());
                    pw.println("  Modalidad:  " + i.getModalidad());
                    pw.printf ("  Presentes:  %d / %d%n",
                            i.cantidadPresentes(),
                            i.getCurso().getClasesDictadas().size());
                    pw.printf ("  Porcentaje: %.2f%%%n",
                            i.calcularPorcentajeAsistencia());
                    pw.println("  Condición:  " + i.obtenerCondicion());
                    pw.println("  Asistencias detalladas:");

                    for (Asistencia a : i.getAsistencias()) {
                        pw.println("    - " + a);
                    }
                }
            }

            cerrarArchivo(pw, ruta);
        }
    }

    /**
     * Escribe en archivo el listado de alumnos libres de todas las asignaturas.
     *
     * <p>Recibe la lista producida por {@code Universidad.obtenerAlumnosLibres()}.</p>
     *
     * @param libres Lista de inscripciones con condición LIBRE.
     */
    public static void guardarAlumnosLibresTodas(List<Inscripcion> libres) {
        String ruta = CARPETA + "alumnos_libres_todas.txt";
        PrintWriter pw = abrirArchivo(ruta);

        if (pw != null) {
            pw.println("   ALUMNOS LIBRES — TODAS LAS ASIGNATURAS");

            if (libres.isEmpty()) {
                pw.println("  (No hay alumnos libres registrados)");
            } else {
                for (Inscripcion i : libres) {
                    pw.println("  " + i.getAlumno()
                            + " — " + i.getCurso().getAsignatura().getNombre()
                            + " (Curso: " + i.getCurso().getIdCurso() + ")");
                }
            }

            cerrarArchivo(pw, ruta);
        }
    }

    /**
     * Escribe en archivo el listado de alumnos libres de un año calendario.
     *
     * <p>Recibe la lista producida por {@code Universidad.obtenerAlumnosLibres(int)}.</p>
     *
     * @param libres         Lista de inscripciones LIBRE del año indicado.
     * @param anioCarrera El año al que corresponden los datos.
     */
    public static void guardarAlumnosLibresPorAnio(List<Inscripcion> libres, int anioCarrera) {
        String ruta = CARPETA + "alumnos_libres_anio_" + anioCarrera + ".txt";
        PrintWriter pw = abrirArchivo(ruta);

        if (pw != null) {
            pw.println("   ALUMNOS LIBRES — AÑO DE CARRERA: " + anioCarrera);

            if (libres.isEmpty()) {
                pw.println("  (No hay alumnos libres para el año " + anioCarrera + ")");
            } else {
                for (Inscripcion i : libres) {
                    pw.println("  " + i.getAlumno()
                            + " — " + i.getCurso().getAsignatura().getNombre()
                            + " (Curso: " + i.getCurso().getIdCurso() + ")");
                }
            }

            cerrarArchivo(pw, ruta);
        }
    }

    /**
     * Abre (o crea) un archivo para escritura.
     * Si ocurre algún error retorna {@code null} e informa por consola.
     *
     * @param ruta Ruta completa del archivo.
     * @return Un {@link PrintWriter} listo para escribir, o {@code null} si falló.
     */
    private static PrintWriter abrirArchivo(String ruta) {
        PrintWriter pw = null;
        boolean errorAbierto = false;

        try {
            new File(CARPETA).mkdirs();
            pw = new PrintWriter(new FileWriter(ruta, false));
        } catch (IOException e) {
            System.err.println("[Reporte] No se pudo crear el archivo: "
                    + ruta + " — " + e.getMessage());
            errorAbierto = true;
        }

        PrintWriter resultado = null;
        if (!errorAbierto) {
            resultado = pw;
        }

        return resultado;
    }

    /**
     * Cierra el {@link PrintWriter} e informa la ruta donde quedó guardado el reporte.
     *
     * @param pw   El writer a cerrar.
     * @param ruta La ruta del archivo (para el mensaje de confirmación).
     */
    private static void cerrarArchivo(PrintWriter pw, String ruta) {
        pw.flush();
        pw.close();
        System.out.println("[Reporte] Guardado en: " + ruta);
    }
}
