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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

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
    private static Universidad universidad;
    private static final String RUTA_XML_DEFAULT = "datos/universidad.xml";
    private static final String RUTA_BIN_DEFAULT = "datos/universidad.dat";

    /**
     * Crea el sistema, intenta recuperar el estado guardado y presenta el menú.
     * * Nota: En versiones modernas de Java (Java 22+), el método main puede
     * prescindir del parámetro 'args' si no se utiliza y del modificador 'public'.
     */
    static void main(String[] args) {
        try {
            // Intentar recuperar el estado previo al iniciar
            universidad = PersistenciaBinaria.cargarEstado(RUTA_BIN_DEFAULT);
            if (universidad == null) {
                universidad = new Universidad();
            }

            // Crear el servidor HTTP en el puerto 8080
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

            // Handler para servir la página web principal
            server.createContext("/", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    try (InputStream is = Main.class.getResourceAsStream("/UI/AppWeb/index.html")) {
                        if (is == null) {
                            String errorMsg = "Error interno: No se encontró el archivo index.html en los recursos.";
                            byte[] responseBytes = errorMsg.getBytes(StandardCharsets.UTF_8);
                            exchange.sendResponseHeaders(404, responseBytes.length);
                            try (OutputStream os = exchange.getResponseBody()) {
                                os.write(responseBytes);
                            }
                            return;
                        }

                        byte[] response = is.readAllBytes();
                        exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                        exchange.sendResponseHeaders(200, response.length);
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response);
                        }
                    }
                }
            });

            // Handler para procesar las acciones del menú
            server.createContext("/accion", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                        Map<String, String> params = parsearPostParams(exchange.getRequestBody());
                        String operacion = params.get("operacion");
                        String resultado = "Operación no reconocida.";

                        if ("1".equals(operacion)) {
                            List<String> errores = CargadorXML.cargar(RUTA_XML_DEFAULT, universidad);
                            resultado = errores.isEmpty() ? "Carga XML completada sin errores."
                                    : "Cargado con advertencias: \n" + String.join("\n", errores);
                        } else if ("2".equals(operacion)) {
                            GeneradorXML.guardar(universidad, RUTA_XML_DEFAULT);
                            resultado = "Estado guardado con éxito en: " + RUTA_XML_DEFAULT;
                        } else if ("3".equals(operacion)) {
                            PersistenciaBinaria.guardarEstado(universidad, RUTA_BIN_DEFAULT);
                            resultado = "Estado binario guardado con éxito.";
                        } else if ("4".equals(operacion)) {
                            Universidad recuperada = PersistenciaBinaria.cargarEstado(RUTA_BIN_DEFAULT);
                            if (recuperada != null) {
                                System.out.println("Sistema cargado correctamente desde: " + RUTA_BIN_DEFAULT);
                                universidad = recuperada;
                                resultado = "Estado binario cargado con éxito.";
                            } else {
                                resultado = "Error al cargar el estado binario.";
                            }
                        } else if ("5".equals(operacion)) {
                            // Opción 5: Registrar asistencia manual
                            try {
                                String idCurso = params.get("idCurso");
                                String idClase = params.get("idClase");
                                String matricula = params.get("matricula");
                                boolean presente = "s".equalsIgnoreCase(params.get("presente"));

                                universidad.asignaturas.Curso cursoElegido = null;
                                for (universidad.asignaturas.Curso c : universidad.getCursos()) {
                                    if (c.getIdCurso().equalsIgnoreCase(idCurso)) cursoElegido = c;
                                }

                                universidad.clases.Clase claseElegida = null;
                                if (cursoElegido != null) {
                                    for (universidad.clases.Clase cl : cursoElegido.getClasesDictadas()) {
                                        if (cl.getId().equalsIgnoreCase(idClase)) claseElegida = cl;
                                    }
                                }

                                universidad.alumnos.Alumno alumnoElegido = null;
                                for (universidad.alumnos.Alumno a : universidad.getAlumnos()) {
                                    if (a.getMatricula().equalsIgnoreCase(matricula)) alumnoElegido = a;
                                }

                                if (cursoElegido == null) {
                                    resultado = "[!] No existe un curso con el ID ingresado.";
                                } else if (claseElegida == null) {
                                    resultado = "[!] No existe una clase con ese ID en el curso.";
                                } else if (alumnoElegido == null) {
                                    resultado = "[!] No existe ningún alumno con esa matrícula.";
                                } else {
                                    universidad.registrarAsistencia(alumnoElegido, claseElegida, cursoElegido, presente);
                                    resultado = "Asistencia registrada: " + alumnoElegido + " -> " + (presente ? "PRESENTE" : "AUSENTE");
                                }
                            } catch (Exception e) {
                                resultado = "[!] Error: " + e.getMessage();
                            }
                        } else if ("6".equals(operacion)) {
                            // Opción 6: Ranking de presentismo
                            List<universidad.ranking.RankingAsignatura> ranking = universidad.rankingPresentismo();
                            if (ranking.isEmpty()) {
                                resultado = "No hay datos suficientes para generar el ranking.";
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
                                resultado = sb.toString();
                            }
                        } else if ("7".equals(operacion)) {
                            // Opción 7: Reporte de asignatura
                            String codigo = params.get("codigo");
                            universidad.asignaturas.Asignatura encontrada = null;
                            for (universidad.asignaturas.Asignatura a : universidad.getAsignaturas()) {
                                if (a.getCodigo().equalsIgnoreCase(codigo)) encontrada = a;
                            }

                            if (encontrada == null) {
                                resultado = "[!] No existe ninguna asignatura con el código '" + codigo + "'.";
                            } else {
                                List<universidad.inscripciones.Inscripcion> inscripciones = universidad.obtenerInscripcionesPorAsignatura(encontrada);
                                if (inscripciones.isEmpty()) {
                                    resultado = "No hay alumnos inscriptos en esta asignatura.";
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
                                    resultado = sb.toString();
                                }
                            }
                        } else if ("8".equals(operacion)) {
                            // Opción 8: Alumnos libres todas las asignaturas
                            List<universidad.inscripciones.Inscripcion> libres = universidad.alumnosLibres();
                            if (libres.isEmpty()) {
                                resultado = "(No hay alumnos libres registrados)";
                            } else {
                                StringBuilder sb = new StringBuilder();
                                sb.append("ALUMNOS LIBRES - TODAS LAS ASIGNATURAS:\n\n");
                                for (universidad.inscripciones.Inscripcion i : libres) {
                                    sb.append("• ").append(i.getAlumno())
                                            .append(" en ").append(i.getCurso().getAsignatura().getNombre())
                                            .append(" (Curso: ").append(i.getCurso().getIdCurso()).append(")\n");
                                }
                                UI.GeneradorReportes.guardarAlumnosLibresTodas(libres);
                                resultado = sb.toString();
                            }
                        } else if ("9".equals(operacion)) {
                            // Opción 9: Alumnos libres por año calendario
                            try {
                                int anio = Integer.parseInt(params.get("codigo")); // Reutiliza el parámetro de texto de la UI
                                List<universidad.inscripciones.Inscripcion> libres = universidad.alumnosLibres(anio);
                                if (libres.isEmpty()) {
                                    resultado = "(No hay alumnos libres para el año " + anio + ")";
                                } else {
                                    StringBuilder sb = new StringBuilder();
                                    sb.append("ALUMNOS LIBRES - AÑO ").append(anio).append(":\n\n");
                                    for (universidad.inscripciones.Inscripcion i : libres) {
                                        sb.append("• ").append(i.getAlumno())
                                                .append(" en ").append(i.getCurso().getAsignatura().getNombre())
                                                .append(" (Curso: ").append(i.getCurso().getIdCurso()).append(")\n");
                                    }
                                    UI.GeneradorReportes.guardarAlumnosLibresPorAnio(libres, anio);
                                    resultado = sb.toString();
                                }
                            } catch (NumberFormatException e) {
                                resultado = "[!] Ingrese un año válido en el cuadro de texto.";
                            }
                        }

                        // Responder al navegador
                        exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
                        byte[] responseBytes = resultado.getBytes(StandardCharsets.UTF_8);
                        exchange.sendResponseHeaders(200, responseBytes.length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(responseBytes);
                        os.close();
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