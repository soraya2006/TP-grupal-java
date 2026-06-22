package UI;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import io.CargadorXML;
import io.GeneradorXML;
import io.PersistenciaBinaria;

import universidad.Universidad;
import universidad.excepciones.*;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

/**
 * Punto de entrada del sistema de gestión universitaria.
 *
 * <p>Presenta una interfaz web interactiva modularizada que permite al usuario
 * cargar datos, registrar asistencias y generar reportes.</p>
 *
 * <p>Cumple las siguientes restricciones de programación estructurada:</p>
 * <ul>
 * <li>No se usa {@code break} ni {@code continue} en bucles.</li>
 * <li>No se usa {@code return} anticipado dentro de los flujos de control.</li>
 * <li>Toda entrada inválida se controla con estructuras condicionales y repetitivas.</li>
 * </ul>
 */
public class Main {
    private static Universidad universidad;
    private static final String RUTA_XML_DEFAULT = "src/io/universidad.xml";
    private static final String RUTA_BIN_DEFAULT = "src/io/universidad.dat";

    public static void main(String[] args) {
        try {
            universidad = PersistenciaBinaria.cargarEstado(RUTA_BIN_DEFAULT);
            if (universidad == null) {
                universidad = new Universidad();
            }

            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            // Handler para servir la página web principal de manera estructurada
            server.createContext("/", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    try (InputStream is = Main.class.getResourceAsStream("/UI/AppWeb/index.html")) {
                        byte[] responseBytes;
                        if (is == null) {
                            String errorMsg = "Error interno: No se encontró el archivo index.html en los recursos.";
                            responseBytes = errorMsg.getBytes(StandardCharsets.UTF_8);
                            exchange.sendResponseHeaders(404, responseBytes.length);
                        } else {
                            responseBytes = is.readAllBytes();
                            exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                            exchange.sendResponseHeaders(200, responseBytes.length);
                        }
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(responseBytes);
                        }
                    }
                }
            });

            // Handler para procesar las acciones delegando de manera cohesiva
            server.createContext("/accion", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                        Map<String, String> params = parsearPostParams(exchange.getRequestBody());
                        String operacion = params.get("operacion");
                        String resultado = "Operación no reconocida.";

                        if ("1".equals(operacion)) {
                            resultado = procesarCargaXML();
                        } else if ("2".equals(operacion)) {
                            resultado = procesarGuardarXML();
                        } else if ("3".equals(operacion)) {
                            resultado = procesarGuardarBinario();
                        } else if ("4".equals(operacion)) {
                            resultado = procesarCargarBinario();
                        } else if ("5".equals(operacion)) {
                            resultado = procesarRegistrarAsistencia(params);
                        } else if ("6".equals(operacion)) {
                            resultado = procesarRankingPresentismo();
                        } else if ("7".equals(operacion)) {
                            resultado = procesarReporteAsignatura(params);
                        } else if ("8".equals(operacion)) {
                            resultado = procesarAlumnosLibresTodas();
                        } else if ("9".equals(operacion)) {
                            resultado = procesarAlumnosLibresPorAnio(params);
                        }

                        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                        byte[] responseBytes = resultado.getBytes(StandardCharsets.UTF_8);
                        exchange.sendResponseHeaders(200, responseBytes.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(responseBytes);
                        }
                    }
                }
            });

            System.out.println("=============================================");
            System.out.println(" Servidor iniciado en http://localhost:8080 ");
            System.out.println("=============================================");
            server.start();

        } catch (IOException e) {
            System.err.println("Error al iniciar el servidor de UI: " + e.getMessage());
        }
    }

    // =========================================================================
    // MÉTODOS MODULARIZADOS DE ENTRADA/SALIDA Y PROCESAMIENTO (COHESIÓN)
    // =========================================================================

    private static String procesarCargaXML() {
        List<String> errores = CargadorXML.cargar(RUTA_XML_DEFAULT, universidad);
        String res = errores.isEmpty() ? "Carga XML completada sin errores."
                : "Cargado con advertencias: \n" + String.join("\n", errores);
        return res;
    }

    private static String procesarGuardarXML() {
        GeneradorXML.guardar(universidad, RUTA_XML_DEFAULT);
        return "Estado guardado con éxito en: " + RUTA_XML_DEFAULT;
    }

    private static String procesarGuardarBinario() {
        PersistenciaBinaria.guardarEstado(universidad, RUTA_BIN_DEFAULT);
        return "Estado binario guardado con éxito.";
    }

    private static String procesarCargarBinario() {
        String res;
        Universidad recuperada = PersistenciaBinaria.cargarEstado(RUTA_BIN_DEFAULT);
        if (recuperada != null) {
            System.out.println("Sistema cargado correctamente desde: " + RUTA_BIN_DEFAULT);
            universidad = recuperada;
            res = "Estado binario cargado con éxito.";
        } else {
            res = "Error al cargar el estado binario.";
        }
        return res;
    }

    private static String procesarRegistrarAsistencia(Map<String, String> params) {
        String res;
        try {
            String idCurso = params.get("idCurso");
            String idClase = params.get("idClase");
            String matricula = params.get("matricula");
            boolean presente = "s".equalsIgnoreCase(params.get("presente"));

            // Búsqueda controlada de Curso
            universidad.asignaturas.Curso cursoElegido = null;
            List<universidad.asignaturas.Curso> listaCursos = universidad.getCursos();
            int i = 0;
            while (i < listaCursos.size() && cursoElegido == null) {
                if (listaCursos.get(i).getIdCurso().equalsIgnoreCase(idCurso)) {
                    cursoElegido = listaCursos.get(i);
                }
                i++;
            }

            // Búsqueda controlada de Clase
            universidad.clases.Clase claseElegida = null;
            if (cursoElegido != null) {
                List<universidad.clases.Clase> listaClases = cursoElegido.getClasesDictadas();
                int j = 0;
                while (j < listaClases.size() && claseElegida == null) {
                    if (listaClases.get(j).getId().equalsIgnoreCase(idClase)) {
                        claseElegida = listaClases.get(j);
                    }
                    j++;
                }
            }

            // Búsqueda controlada de Alumno
            universidad.alumnos.Alumno alumnoElegido = null;
            java.util.Iterator<universidad.alumnos.Alumno> itAlumno = universidad.getAlumnos().iterator();
            while (itAlumno.hasNext() && alumnoElegido == null) {
                universidad.alumnos.Alumno a = itAlumno.next();
                if (a.getMatricula().equalsIgnoreCase(matricula)) {
                    alumnoElegido = a;
                }
            }

            if (cursoElegido == null) {
                res = "[!] No existe un curso con el ID ingresado.";
            } else if (claseElegida == null) {
                res = "[!] No existe una clase con ese ID en el curso.";
            } else if (alumnoElegido == null) {
                res = "[!] No existe ningún alumno con esa matrícula.";
            } else {
                universidad.registrarAsistencia(alumnoElegido, claseElegida, cursoElegido, presente);
                res = "Asistencia registrada: " + alumnoElegido + " -> " + (presente ? "PRESENTE" : "AUSENTE");
            }
        } catch (Exception e) {
            res = "[!] Error: " + e.getMessage();
        }
        return res;
    }

    private static String procesarRankingPresentismo() {
        String res;
        List<universidad.ranking.RankingAsignatura> ranking = universidad.rankingPresentismo();
        if (ranking.isEmpty()) {
            res = "No hay datos suficientes para generar el ranking.";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append(String.format("%-4s %-35s %-12s\n", "Pos.", "Asignatura", "Presentismo"));
            sb.append("─────────────────────────────────────────────────────────\n");
            int pos = 1;
            for (universidad.ranking.RankingAsignatura r : ranking) {
                sb.append(String.format("%3d.  %-35s %6.2f%%\n",
                        pos, r.getAsignatura().getNombre(), r.getPorcentaje()));
                pos++;
            }
            UI.GeneradorReportes.guardarRankingPresentismo(ranking);
            res = sb.toString();
        }
        return res;
    }

    private static String procesarReporteAsignatura(Map<String, String> params) {
        String res;
        String codigo = params.get("codigo");
        universidad.asignaturas.Asignatura encontrada = null;
        List<universidad.asignaturas.Asignatura> listaAsignaturas = universidad.getAsignaturas();
        int k = 0;
        while (k < listaAsignaturas.size() && encontrada == null) {
            if (listaAsignaturas.get(k).getCodigo().equalsIgnoreCase(codigo)) {
                encontrada = listaAsignaturas.get(k);
            }
            k++;
        }

        if (encontrada == null) {
            res = "[!] No existe ninguna asignatura con el código '" + codigo + "'.";
        } else {
            List<universidad.inscripciones.Inscripcion> inscripciones = universidad.obtenerInscripcionesPorAsignatura(encontrada);
            if (inscripciones.isEmpty()) {
                res = "No hay alumnos inscriptos en esta asignatura.";
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("REPORTE DE ASIGNATURA: ").append(encontrada.getNombre()).append("\n\n");
                for (universidad.inscripciones.Inscripcion insc : inscripciones) {
                    sb.append("Alumno: ").append(insc.getAlumno()).append("\n");
                    sb.append("Modalidad: ").append(insc.getModalidad()).append("\n");
                    sb.append(String.format("Porcentaje Asistencia: %.2f%%\n", insc.calcularPorcentajeAsistencia()));
                    sb.append("Condición: ").append(insc.obtenerCondicion()).append("\n");
                    sb.append("─────────────────────────────────────────\n");
                }
                UI.GeneradorReportes.guardarReporteAsignatura(encontrada, inscripciones);
                res = sb.toString();
            }
        }
        return res;
    }

    private static String procesarAlumnosLibresTodas() {
        String res;
        List<universidad.inscripciones.Inscripcion> libres = universidad.alumnosLibres();
        if (libres.isEmpty()) {
            res = "(No hay alumnos libres registrados)";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("ALUMNOS LIBRES - TODAS LAS ASIGNATURAS:\n\n");
            for (universidad.inscripciones.Inscripcion i : libres) {
                sb.append("• ").append(i.getAlumno())
                        .append(" en ").append(i.getCurso().getAsignatura().getNombre())
                        .append(" (Curso: ").append(i.getCurso().getIdCurso()).append(")\n");
            }
            UI.GeneradorReportes.guardarAlumnosLibresTodas(libres);
            res = sb.toString();
        }
        return res;
    }

    private static String procesarAlumnosLibresPorAnio(Map<String, String> params) {
        String res;
        try {
            int anio = Integer.parseInt(params.get("codigo"));
            List<universidad.inscripciones.Inscripcion> libres = universidad.alumnosLibres(anio);
            if (libres.isEmpty()) {
                res = "(No hay alumnos libres para el año de carrera " + anio + ")";
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("ALUMNOS LIBRES - AÑO DE CARRERA ").append(anio).append(":\n\n");
                for (universidad.inscripciones.Inscripcion i : libres) {
                    sb.append("• ").append(i.getAlumno())
                            .append(" en ").append(i.getCurso().getAsignatura().getNombre())
                            .append(" (Curso: ").append(i.getCurso().getIdCurso()).append(")\n");
                }
                UI.GeneradorReportes.guardarAlumnosLibresPorAnio(libres, anio);
                res = sb.toString();
            }
        } catch (NumberFormatException e) {
            res = "[!] Ingrese un año de carrera válido (1 a 5) en el cuadro de texto.";
        }
        return res;
    }

    private static Map<String, String> parsearPostParams(InputStream is) throws IOException {
        Map<String, String> result = new HashMap<>();
        String query = new String(is.readAllBytes(), StandardCharsets.UTF_8);
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            if (idx > 0) {
                String key = URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8);
                String value = URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8);
                result.put(key, value);
            }
        }
        return result;
    }
}