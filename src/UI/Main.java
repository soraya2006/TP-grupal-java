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
    private static final String RUTA_XML_DEFAULT = "src/io/universidad.xml";
    private static final String RUTA_BIN_DEFAULT = "src/io/universidad.dat";

    /**
     * Crea el sistema, intenta recuperar el estado guardado y presenta el menú.
     * * Nota: En versiones modernas de Java (Java 22+), el método main puede
     * prescindir del parámetro 'args' si no se utiliza y del modificador 'public'.
     */
    public static void main(String[] args) {
        try {
            universidad = PersistenciaBinaria.cargarEstado(RUTA_BIN_DEFAULT);
            if (universidad == null) {
                universidad = new Universidad();
            }
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
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

            server.createContext("/accion", new HttpHandler() {
                @Override
                public void handle(HttpExchange exchange) throws IOException {
                    if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                        Map<String, String> params = parsearPostParams(exchange.getRequestBody());
                        String operacion = params.get("operacion");
                        String resultado = "Operación no reconocida.";

                        if ("1".equals(operacion)) {
                            resultado = procesarOperacion1();
                        } else if ("2".equals(operacion)) {
                            resultado = procesarOperacion2();
                        } else if ("3".equals(operacion)) {
                            resultado = procesarOperacion3();
                        } else if ("4".equals(operacion)) {
                            resultado = procesarOperacion4();
                        } else if ("5".equals(operacion)) {
                            resultado = procesarOperacion5(params);
                        } else if ("6".equals(operacion)) {
                            resultado = procesarOperacion6();
                        } else if ("7".equals(operacion)) {
                            resultado = procesarOperacion7(params);
                        } else if ("8".equals(operacion)) {
                            resultado = procesarOperacion8();
                        } else if ("9".equals(operacion)) {
                            resultado = procesarOperacion9(params);
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

    private static String procesarOperacion1() {
        List<String> errores = CargadorXML.cargar(RUTA_XML_DEFAULT, universidad);
        String res = errores.isEmpty() ? "Carga XML completada sin errores."
                : "Cargado con advertencias: \n" + String.join("\n", errores);
        return res;
    }

    private static String procesarOperacion2() {
        GeneradorXML.guardar(universidad, RUTA_XML_DEFAULT);
        String res = "Estado guardado con éxito en: " + RUTA_XML_DEFAULT;
        return res;
    }

    private static String procesarOperacion3() {
        PersistenciaBinaria.guardarEstado(universidad, RUTA_BIN_DEFAULT);
        String res = "Estado binario guardado con éxito.";
        return res;
    }

    private static String procesarOperacion4() {
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

    private static String procesarOperacion5(Map<String, String> params) {
        String res;
        try {
            String idCurso = params.get("idCurso");
            String idClase = params.get("idClase");
            String matricula = params.get("matricula");
            boolean presente = "s".equalsIgnoreCase(params.get("presente"));

            universidad.asignaturas.Curso cursoElegido = null;
            List<universidad.asignaturas.Curso> cursos = universidad.getCursos();
            int i = 0;
            while (i < cursos.size() && cursoElegido == null) {
                if (cursos.get(i).getIdCurso().equalsIgnoreCase(idCurso)) {
                    cursoElegido = cursos.get(i);
                }
                i++;
            }

            universidad.clases.Clase claseElegida = null;
            if (cursoElegido != null) {
                List<universidad.clases.Clase> clases = cursoElegido.getClasesDictadas();
                int j = 0;
                while (j < clases.size() && claseElegida == null) {
                    if (clases.get(j).getId().equalsIgnoreCase(idClase)) {
                        claseElegida = clases.get(j);
                    }
                    j++;
                }
            }

            if (cursoElegido == null) {
                res = "[!] No existe un curso con el ID ingresado.";
            } else if (claseElegida == null) {
                res = "[!] No existe una clase con ese ID en el curso.";
            } else {
                universidad.alumnos.Alumno alumnoElegido = null;
                java.util.Iterator<universidad.alumnos.Alumno> itAlumnos = universidad.getAlumnos().iterator();
                while (itAlumnos.hasNext() && alumnoElegido == null) {
                    universidad.alumnos.Alumno a = itAlumnos.next();
                    if (a.getMatricula().equalsIgnoreCase(matricula)) {
                        alumnoElegido = a;
                    }
                }
                if (alumnoElegido == null) {
                    res = "[!] No existe ningún alumno con esa matrícula.";
                } else {
                    universidad.registrarAsistencia(alumnoElegido, claseElegida, cursoElegido, presente);
                    res = "Asistencia registrada: " + alumnoElegido + " -> " + (presente ? "PRESENTE" : "AUSENTE");
                }
            }
        } catch (Exception e) {
            res = "[!] Error: " + e.getMessage();
        }
        return res;
    }

    private static String procesarOperacion6() {
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

    private static String procesarOperacion7(Map<String, String> params) {
        String res;
        String codigo = params.get("codigo");
        universidad.asignaturas.Asignatura encontrada = null;
        List<universidad.asignaturas.Asignatura> asignaturas = universidad.getAsignaturas();
        int i = 0;
        while (i < asignaturas.size() && encontrada == null) {
            if (asignaturas.get(i).getCodigo().equalsIgnoreCase(codigo)) {
                encontrada = asignaturas.get(i);
            }
            i++;
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

    private static String procesarOperacion8() {
        String res;
        List<universidad.inscripciones.Inscripcion> libres = universidad.alumnosLibres();
        if (libres.isEmpty()) {
            res = "(No hay alumnos libres registrados)";
        } else {
            StringBuilder sb = new StringBuilder();
            sb.append("ALUMNOS LIBRES - TODAS LAS ASIGNATURAS:\n\n");
            for (universidad.inscripciones.Inscripcion i : libres) {
                sb.append("Alumno: ").append(i.getAlumno()).append("\n");
                sb.append("Asignatura: ").append(i.getCurso().getAsignatura().getNombre()).append("\n");
                sb.append("Curso: ").append(i.getCurso().getIdCurso()).append("\n");
                sb.append("─────────────────────────────────────────\n");
            }
            UI.GeneradorReportes.guardarAlumnosLibresTodas(libres);
            res = sb.toString();
        }
        return res;
    }

    private static String procesarOperacion9(Map<String, String> params) {
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
                    sb.append("Alumno: ").append(i.getAlumno()).append("\n");
                    sb.append("Asignatura: ").append(i.getCurso().getAsignatura().getNombre()).append("\n");
                    sb.append("Curso: ").append(i.getCurso().getIdCurso()).append("\n");
                    sb.append("─────────────────────────────────────────\n");
                }
                UI.GeneradorReportes.guardarAlumnosLibresPorAnio(libres, anio);
                res = sb.toString();
            }
        } catch (NumberFormatException e) {
            res = "[!] Ingrese un año de carrera válido (1 a 5) en el cuadro de texto.";
        } catch (universidad.excepciones.DatoInvalidoException e){
            res = "[!] Error de validación: " + e.getMessage();
        }
        return res;
    }

    /**
     * Procesa y parsea el cuerpo de una solicitud HTTP POST codificada en formato
     * application/x-www-form-urlencoded, convirtiéndola en un mapa de clave-valor.
     * * @param is El flujo de entrada conteniendo los bytes del cuerpo del POST.
     * @return Un mapa {@link Map} con los parámetros y sus respectivos valores decodificados.
     * @throws IOException Si ocurre un error de lectura en el flujo de datos.
     */
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