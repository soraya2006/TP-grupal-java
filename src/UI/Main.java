package UI;

import io.CargadorXML;
import io.GeneradorXML;
import io.PersistenciaBinaria;

import universidad.Universidad;
import universidad.alumnos.Alumno;
import universidad.asignaturas.Asignatura;
import universidad.asignaturas.Curso;
import universidad.clases.Clase;
import universidad.ranking.RankingAsignatura;
import universidad.inscripciones.Inscripcion;
import universidad.excepciones.*;

import java.util.List;
import java.util.Scanner;

/**
 * Punto de entrada del sistema de gestión universitaria.
 *
 * <p>Presenta un menú interactivo de texto que permite al usuario
 * cargar datos, registrar asistencias y generar reportes.</p>
 *
 * <p>Cumple las siguientes restricciones de programación estructurada:</p>
 * <ul>
 * <li>No se usa {@code break} ni {@code continue} en bucles.</li>
 * <li>No se usa {@code return} anticipado dentro de métodos no-void.</li>
 * <li>Toda entrada inválida se controla con estructuras condicionales
 * y repetitivas.</li>
 * </ul>
 */
public class Main {
    private static final String RUTA_XML_DEFAULT    = "datos/universidad.xml";
    private static final String RUTA_BIN_DEFAULT    = "datos/universidad.dat";

    /**
     * Crea el sistema, intenta recuperar el estado guardado y presenta el menú.
     * * Nota: En versiones modernas de Java (Java 22+), el método main puede
     * prescindir del parámetro 'args' si no se utiliza y del modificador 'public'.
     */
    static void main() {

        Scanner sc = new Scanner(System.in);
        System.out.println("  SISTEMA DE GESTIÓN UNIVERSITARIA   ");
        System.out.println("======================================");
        System.out.println("Buscando estado guardado...");

        Universidad universidad = PersistenciaBinaria.cargarEstado(RUTA_BIN_DEFAULT);

        if (universidad == null) {
            universidad = new Universidad();
            System.out.println("Se inicia un sistema nuevo vacío.");
        }

        int opcion;

        do {
            mostrarMenu();
            opcion = leerEntero(sc, "Ingrese una opción: ");

            if (opcion == 1) {
                opcionCargarXML(sc, universidad);
            } else if (opcion == 2) {
                opcionGuardarXML(sc, universidad);
            } else if (opcion == 3) {
                opcionGuardarBinario(sc, universidad);
            } else if (opcion == 4) {
                universidad = opcionCargarBinario(sc, universidad);
            } else if (opcion == 5) {
                opcionRegistrarAsistencia(sc, universidad);
            } else if (opcion == 6) {
                opcionRankingPresentismo(universidad);
            } else if (opcion == 7) {
                opcionReporteAsignatura(sc, universidad);
            } else if (opcion == 8) {
                opcionAlumnosLibresTodas(universidad);
            } else if (opcion == 9) {
                opcionAlumnosLibresPorAnio(sc, universidad);
            } else if (opcion == 0) {
                System.out.println("\nCerrando el sistema...");
            } else {
                System.out.println("[!] Opción inválida. Ingrese un número entre 0 y 9.");
            }

        } while (opcion != 0);

        System.out.print("¿Desea guardar el estado antes de salir? (s/n): ");
        String respuesta = sc.nextLine().trim().toLowerCase();
        if (respuesta.equals("s")) {
            PersistenciaBinaria.guardarEstado(universidad, RUTA_BIN_DEFAULT);
            GeneradorXML.guardar(universidad, RUTA_XML_DEFAULT);
        }

        System.out.println("¡Hasta luego!");
        sc.close();
    }

    /**
     * Imprime el menú principal en consola.
     */
    private static void mostrarMenu() {
        System.out.println();
        System.out.println("             MENÚ PRINCIPAL           ");
        System.out.println("  [1] Cargar datos desde XML");
        System.out.println("  [2] Guardar estado en XML");
        System.out.println("  [3] Guardar estado (binario)");
        System.out.println("  [4] Cargar estado (binario)");
        System.out.println("  ──────────────────────────────────");
        System.out.println("  [5] Registrar asistencia manual");
        System.out.println("  ──────────────────────────────────");
        System.out.println("  [6] Ranking de asignaturas (presentismo)");
        System.out.println("  [7] Reporte detallado de una asignatura");
        System.out.println("  [8] Alumnos libres — todas las asignaturas");
        System.out.println("  [9] Alumnos libres — por año calendario");
        System.out.println("  ──────────────────────────────────");
        System.out.println("  [0] Salir");
    }

    /**
     * Solicita la ruta del XML y delega la carga a {@link CargadorXML}.
     * Si hay errores de validación, los muestra todos antes de continuar.
     *
     * @param sc          Scanner activo.
     * @param universidad Instancia donde se cargan los datos.
     */
    private static void opcionCargarXML(Scanner sc, Universidad universidad) {
        System.out.println("\n--- Cargar datos desde XML ---");
        System.out.print("Ruta del archivo XML [" + RUTA_XML_DEFAULT + "]: ");
        String ruta = sc.nextLine().trim();

        if (ruta.isEmpty()) {
            ruta = RUTA_XML_DEFAULT;
        }

        System.out.println("Cargando...");
        List<String> errores = CargadorXML.cargar(ruta, universidad);

        if (errores.isEmpty()) {
            System.out.println("Carga completada sin errores.");
        } else {
            System.out.println("\n[!] Se encontraron los siguientes problemas durante la carga:");
            int i = 0;
            while (i < errores.size()) {
                System.out.println("    " + errores.get(i));
                i++;
            }
            System.out.println("\nLos datos válidos fueron igualmente cargados.");
        }
    }

    /**
     * Solicita la ruta de destino y serializa el estado a XML.
     *
     * @param sc          Scanner activo.
     * @param universidad Instancia a serializar.
     */
    private static void opcionGuardarXML(Scanner sc, Universidad universidad) {
        System.out.println("\n--- Guardar estado en XML ---");
        System.out.print("Ruta de destino [" + RUTA_XML_DEFAULT + "]: ");
        String ruta = sc.nextLine().trim();

        if (ruta.isEmpty()) {
            ruta = RUTA_XML_DEFAULT;
        }

        GeneradorXML.guardar(universidad, ruta);
    }

    /**
     * Solicita la ruta de destino y serializa el estado en formato binario.
     *
     * @param sc          Scanner activo.
     * @param universidad Instancia a serializar.
     */
    private static void opcionGuardarBinario(Scanner sc, Universidad universidad) {
        System.out.println("\n--- Guardar estado (binario) ---");
        System.out.print("Ruta de destino [" + RUTA_BIN_DEFAULT + "]: ");
        String ruta = sc.nextLine().trim();

        if (ruta.isEmpty()) {
            ruta = RUTA_BIN_DEFAULT;
        }

        PersistenciaBinaria.guardarEstado(universidad, ruta);
    }

    /**
     * Carga el estado del sistema desde un archivo binario.
     * Si el archivo no existe o hay error, conserva el estado actual.
     *
     * @param sc          Scanner activo.
     * @param actual      Instancia actualmente en uso (se devuelve si falla la carga).
     * @return La universidad recuperada, o la instancia actual si falló.
     */
    private static Universidad opcionCargarBinario(Scanner sc, Universidad actual) {
        System.out.println("\n--- Cargar estado (binario) ---");
        System.out.print("Ruta del archivo [" + RUTA_BIN_DEFAULT + "]: ");
        String ruta = sc.nextLine().trim();

        if (ruta.isEmpty()) {
            ruta = RUTA_BIN_DEFAULT;
        }

        Universidad recuperada = PersistenciaBinaria.cargarEstado(ruta);

        Universidad resultado;
        if (recuperada != null) {
            resultado = recuperada;
        } else {
            System.out.println("[!] No se pudo cargar el archivo. Se conserva el estado actual.");
            resultado = actual;
        }

        return resultado;
    }

    /**
     * Guía al usuario para registrar la asistencia de un alumno a una clase.
     *
     * @param sc          Scanner activo.
     * @param universidad Instancia del sistema.
     */
    private static void opcionRegistrarAsistencia(Scanner sc, Universidad universidad) {
        System.out.println("\n--- Registrar asistencia manual ---");

        List<Curso> cursos = universidad.getCursos();
        if (cursos.isEmpty()) {
            System.out.println("[!] No hay cursos registrados en el sistema.");
        } else {
            System.out.println("\nCursos disponibles:");
            int i = 0;
            while (i < cursos.size()) {
                Curso c = cursos.get(i);
                System.out.println("  - " + c.getIdCurso()
                        + " | " + c.getAsignatura().getNombre()
                        + " (" + c.getAnioCalendario()
                        + " - C" + c.getCuatrimestreDictado() + ")");
                i++;
            }

            System.out.print("\nIngrese el ID del curso: ");
            String idCurso = sc.nextLine().trim();
            Curso cursoElegido = buscarCursoPorId(universidad, idCurso);

            if (cursoElegido == null) {
                System.out.println("[!] No existe un curso con el ID ingresado.");
            } else if (cursoElegido.getClasesDictadas().isEmpty()) {
                System.out.println("[!] El curso no tiene clases registradas.");
            } else if (cursoElegido.getInscripciones().isEmpty()) {
                System.out.println("[!] El curso no tiene alumnos inscriptos.");
            } else {
                System.out.println("\nClases del curso " + cursoElegido.getIdCurso() + ":");
                int j = 0;
                while (j < cursoElegido.getClasesDictadas().size()) {
                    Clase cl = cursoElegido.getClasesDictadas().get(j);
                    System.out.println("  - " + cl.getId() + " | " + cl.getFechaHora());
                    j++;
                }

                System.out.print("\nIngrese el ID de la clase: ");
                String idClase = sc.nextLine().trim();
                Clase claseElegida = buscarClasePorId(cursoElegido, idClase);

                if (claseElegida == null) {
                    System.out.println("[!] No existe una clase con ese ID en el curso.");
                } else {
                    System.out.println("\nAlumnos inscriptos en el curso:");
                    int k = 0;
                    while (k < cursoElegido.getInscripciones().size()) {
                        Alumno al = cursoElegido.getInscripciones().get(k).getAlumno();
                        System.out.println("  - " + al.getMatricula() + " | " + al);
                        k++;
                    }

                    System.out.print("\nIngrese la matrícula del alumno: ");
                    String matricula = sc.nextLine().trim();
                    Alumno alumnoElegido = buscarAlumnoPorMatricula(universidad, matricula);

                    if (alumnoElegido == null) {
                        System.out.println("[!] No existe ningún alumno con esa matrícula.");
                    } else {
                        System.out.print("¿El alumno estuvo presente? (s/n): ");
                        String respuesta = sc.nextLine().trim().toLowerCase();

                        boolean inputValido = respuesta.equals("s") || respuesta.equals("n");
                        if (!inputValido) {
                            System.out.println("[!] Respuesta inválida. Ingrese 's' para sí o 'n' para no.");
                        } else {
                            boolean presente = respuesta.equals("s");

                            try {
                                universidad.registrarAsistencia(alumnoElegido, claseElegida, cursoElegido, presente);
                                String estadoStr = presente ? "PRESENTE" : "AUSENTE";
                                System.out.println("Asistencia registrada: "
                                        + alumnoElegido + " → " + estadoStr);
                            } catch (AlumnoNoInscriptoException | AsistenciaYaRegistradaException | ClaseNoDictadaException e) {
                                System.out.println("[!] " + e.getMessage());
                            } catch (ParametroNuloException e) {
                                System.out.println("[!] Error interno: " + e.getMessage());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Muestra en pantalla y guarda en archivo el ranking de asignaturas
     * ordenado de mayor a menor por porcentaje de asistencia.
     *
     * @param universidad Instancia del sistema.
     */
    private static void opcionRankingPresentismo(Universidad universidad) {
        System.out.println("\n--- Ranking de asignaturas por presentismo ---\n");

        List<RankingAsignatura> ranking = universidad.rankingPresentismo();

        if (ranking.isEmpty()) {
            System.out.println("No hay datos suficientes para generar el ranking.");
        } else {
            System.out.println("  Pos.  Asignatura                          Presentismo");
            int pos = 1;
            for (RankingAsignatura r : ranking) {
                System.out.printf("  %3d.  %-38s %6.2f%%%n",
                        pos, r.getAsignatura().getNombre(), r.getPorcentaje());
                pos++;
            }

            GeneradorReportes.guardarRankingPresentismo(ranking);
        }
    }

    /**
     * Solicita el código de una asignatura, muestra el reporte detallado
     * por pantalla (respetando que Universidad no imprima) y lo guarda en archivo.
     *
     * @param sc          Scanner activo.
     * @param universidad Instancia del sistema.
     */
    private static void opcionReporteAsignatura(Scanner sc, Universidad universidad) {
        System.out.println("\n--- Reporte detallado de una asignatura ---");

        List<Asignatura> asignaturas = universidad.getAsignaturas();

        if (asignaturas.isEmpty()) {
            System.out.println("[!] No hay asignaturas registradas en el sistema.");
        } else {
            System.out.println("\nAsignaturas disponibles:");
            int i = 0;
            while (i < asignaturas.size()) {
                Asignatura a = asignaturas.get(i);
                System.out.println("  - [" + a.getCodigo() + "] " + a.getNombre()
                        + " (" + a.getTipo() + ")");
                i++;
            }

            System.out.print("\nIngrese el código de la asignatura: ");
            String codigo = sc.nextLine().trim();
            Asignatura encontrada = buscarAsignaturaPorCodigo(universidad, codigo);

            if (encontrada == null) {
                System.out.println("[!] No existe ninguna asignatura con el código '" + codigo + "'.");
            } else {
                System.out.println();
                List<Inscripcion> inscripciones = universidad.obtenerInscripcionesPorAsignatura(encontrada);

                if (inscripciones.isEmpty()) {
                    System.out.println("  No hay alumnos inscriptos en esta asignatura.");
                } else {
                    String idCursoActual = "";
                    int idx = 0;
                    while (idx < inscripciones.size()) {
                        Inscripcion insc = inscripciones.get(idx);
                        Curso c = insc.getCurso();

                        if (!c.getIdCurso().equals(idCursoActual)) {
                            idCursoActual = c.getIdCurso();
                            System.out.println("\n  Curso: " + c.getIdCurso()
                                    + " | Año: "          + c.getAnioCalendario()
                                    + " | Cuatrimestre: " + c.getCuatrimestreDictado());
                            System.out.println("  Clases dictadas: " + c.getClasesDictadas().size());
                        }

                        System.out.println("  Alumno:     " + insc.getAlumno());
                        System.out.println("  Modalidad:  " + insc.getModalidad());
                        System.out.printf ("  Presentes:  %d / %d%n",
                                insc.cantidadPresentes(),
                                c.getClasesDictadas().size());
                        System.out.printf ("  Porcentaje: %.2f%%%n",
                                insc.calcularPorcentajeAsistencia());
                        System.out.println("  Condición:  " + insc.obtenerCondicion());
                        idx++;
                    }
                }

                GeneradorReportes.guardarReporteAsignatura(encontrada, inscripciones);
            }
        }
    }

    /**
     * Lista por pantalla y guarda en archivo todos los alumnos con condición LIBRE
     * en cualquier asignatura.
     *
     * @param universidad Instancia del sistema.
     */
    private static void opcionAlumnosLibresTodas(Universidad universidad) {
        System.out.println("\n--- Alumnos libres — todas las asignaturas ---\n");

        List<Inscripcion> libres = universidad.alumnosLibres();

        if (libres.isEmpty()) {
            System.out.println("  (No hay alumnos libres registrados)");
        } else {
            int i = 0;
            while (i < libres.size()) {
                Inscripcion insc = libres.get(i);
                System.out.println("  " + insc.getAlumno()
                        + " — " + insc.getCurso().getAsignatura().getNombre()
                        + " (Curso: " + insc.getCurso().getIdCurso() + ")");
                i++;
            }
        }

        GeneradorReportes.guardarAlumnosLibresTodas(libres);
    }

    /**
     * Solicita un año calendario y lista los alumnos libres de ese año.
     * El resultado también se guarda en un archivo de texto.
     *
     * @param sc          Scanner activo.
     * @param universidad Instancia del sistema.
     */
    private static void opcionAlumnosLibresPorAnio(Scanner sc, Universidad universidad) {
        System.out.println("\n--- Alumnos libres — por año calendario ---");
        int anio = leerEntero(sc, "Ingrese el año calendario (ej: 2026): ");

        System.out.println();
        List<Inscripcion> libres = universidad.alumnosLibres(anio);

        if (libres.isEmpty()) {
            System.out.println("  (No hay alumnos libres para el año " + anio + ")");
        } else {
            int i = 0;
            while (i < libres.size()) {
                Inscripcion insc = libres.get(i);
                System.out.println("  " + insc.getAlumno()
                        + " — " + insc.getCurso().getAsignatura().getNombre()
                        + " (Curso: " + insc.getCurso().getIdCurso() + ")");
                i++;
            }
        }

        GeneradorReportes.guardarAlumnosLibresPorAnio(libres, anio);
    }

    /**
     * Busca un curso por su ID (sin distinción de mayúsculas/minúsculas).
     *
     * @param universidad Instancia del sistema.
     * @param idCurso     ID a buscar.
     * @return El curso encontrado, o {@code null} si no existe.
     */
    private static Curso buscarCursoPorId(Universidad universidad, String idCurso) {
        Curso resultado = null;
        List<Curso> cursos = universidad.getCursos();
        int i = 0;
        while (i < cursos.size() && resultado == null) {
            if (cursos.get(i).getIdCurso().equalsIgnoreCase(idCurso)) {
                resultado = cursos.get(i);
            }
            i++;
        }
        return resultado;
    }

    /**
     * Busca una clase dentro de un curso por su ID.
     *
     * @param curso   Curso donde buscar.
     * @param idClase ID de la clase.
     * @return La clase encontrada, o {@code null} si no existe.
     */
    private static Clase buscarClasePorId(Curso curso, String idClase) {
        Clase resultado = null;
        List<Clase> clases = curso.getClasesDictadas();
        int i = 0;
        while (i < clases.size() && resultado == null) {
            if (clases.get(i).getId().equalsIgnoreCase(idClase)) {
                resultado = clases.get(i);
            }
            i++;
        }
        return resultado;
    }

    /**
     * Busca un alumno por matrícula en la colección de alumnos del sistema.
     *
     * @param universidad Instancia del sistema.
     * @param matricula   Matrícula a buscar.
     * @return El alumno encontrado, o {@code null} si no existe.
     */
    private static Alumno buscarAlumnoPorMatricula(Universidad universidad, String matricula) {
        Alumno resultado = null;
        for (Alumno a : universidad.getAlumnos()) {
            if (resultado == null && a.getMatricula().equalsIgnoreCase(matricula)) {
                resultado = a;
            }
        }
        return resultado;
    }

    /**
     * Busca una asignatura por código (sin distinción de mayúsculas/minúsculas).
     *
     * @param universidad Instancia del sistema.
     * @param codigo      Código a buscar.
     * @return La asignatura encontrada, o {@code null} si no existe.
     */
    private static Asignatura buscarAsignaturaPorCodigo(Universidad universidad, String codigo) {
        Asignatura resultado = null;
        List<Asignatura> lista = universidad.getAsignaturas();
        int i = 0;
        while (i < lista.size() && resultado == null) {
            if (lista.get(i).getCodigo().equalsIgnoreCase(codigo)) {
                resultado = lista.get(i);
            }
            i++;

        }
        return resultado;
    }

    /**
     * Lee un número entero del usuario de forma segura.
     *
     * @param sc     Scanner activo.
     * @param prompt Texto a mostrar antes de leer.
     * @return El entero ingresado por el usuario.
     */
    private static int leerEntero(Scanner sc, String prompt) {
        System.out.print(prompt);
        boolean entradaValida = sc.hasNextInt();

        while (!entradaValida) {
            sc.nextLine();
            System.out.println("[!] Entrada inválida. Debe ingresar un número entero.");
            System.out.print(prompt);
            entradaValida = sc.hasNextInt();
        }

        int valor = sc.nextInt();
        sc.nextLine();
        return valor;
    }
}