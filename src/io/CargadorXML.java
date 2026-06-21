package io;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import java.io.File;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.*;

import universidad.Universidad;
import universidad.alumnos.Alumno;
import universidad.asignaturas.*;
import universidad.clases.Clase;
import universidad.inscripciones.Inscripcion;
import universidad.excepciones.*;

/**
 * Cargador de datos desde un archivo XML hacia un objeto {@link Universidad}.
 *
 * <p>La estructura esperada del XML es la siguiente:</p>
 * <pre>
 * &lt;universidad&gt;
 *   &lt;alumnos&gt;
 *     &lt;alumno matricula="A001" apellido="Perez" nombre="Juan" fechaNacimiento="2003-05-20"/&gt;
 *   &lt;/alumnos&gt;
 *   &lt;asignaturas&gt;
 *     &lt;asignatura tipo="OBLIGATORIA" codigo="MAT101" cuatrimestre="1" promocional="true"&gt;
 *       &lt;nombre&gt;Matematica I&lt;/nombre&gt;
 *     &lt;/asignatura&gt;
 *     &lt;!-- tipo puede ser: OBLIGATORIA | OPTATIVA | PASANTIA --&gt;
 *   &lt;/asignaturas&gt;
 *   &lt;cursos&gt;
 *     &lt;curso idCurso="COM-01" codigoAsignatura="MAT101"
 *            anioCalendario="2026" cuatrimestreDictado="1"&gt;
 *       &lt;clases&gt;
 *         &lt;clase id="CL-001" fechaHora="2026-03-04T18:00:00"/&gt;
 *       &lt;/clases&gt;
 *       &lt;inscripciones&gt;
 *         &lt;inscripcion matriculaAlumno="A001" modalidad="REGULAR"&gt;
 *           &lt;asistencias&gt;
 *             &lt;asistencia idClase="CL-001" presente="true"/&gt;
 *           &lt;/asistencias&gt;
 *         &lt;/inscripcion&gt;
 *       &lt;/inscripciones&gt;
 *     &lt;/curso&gt;
 *   &lt;/cursos&gt;
 * &lt;/universidad&gt;
 * </pre>
 *
 * <p>El método {@link #cargar(String, Universidad)} devuelve una lista de mensajes
 * de error. Los elementos con errores se omiten; el resto se carga normalmente.</p>
 */
public class CargadorXML {
    /**
     * Parsea el archivo XML indicado y carga los datos en la universidad.
     * Los errores encontrados se acumulan y se devuelven al final.
     *
     * @param rutaArchivo Ruta del archivo XML a leer.
     * @param universidad Instancia de {@link Universidad} donde se cargarán los datos.
     * @return Lista de mensajes de error (vacía si todo fue correcto).
     */
    public static List<String> cargar(String rutaArchivo, Universidad universidad) {
        List<String> errores = new ArrayList<>();
        Map<String, Asignatura> mapaAsignaturas = new HashMap<>();
        Map<String, Alumno> mapaAlumnos = new HashMap<>();

        Document doc = parsearXML(rutaArchivo, errores);
        if (doc == null) {
            return errores;
        }

        Element raiz = doc.getDocumentElement();
        if (!raiz.getTagName().equals("universidad")) {
            errores.add("[FATAL] El elemento raíz debe ser <universidad>, se encontró: <" + raiz.getTagName() + ">");
            return errores;
        }
        cargarAlumnos(raiz, universidad, mapaAlumnos, errores);
        cargarAsignaturas(raiz, universidad, mapaAsignaturas, errores);
        cargarCursos(raiz, universidad, mapaAsignaturas, mapaAlumnos, errores);

        return errores;
    }
    /**
     * Abre y parsea el archivo XML. Si hay un error lo agrega a la lista y retorna null.
     */
    private static Document parsearXML(String ruta, List<String> errores) {
        try {
            File archivo = new File(ruta);
            if (!archivo.exists()) {
                errores.add("[FATAL] El archivo no existe: " + ruta);
                return null;
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            return builder.parse(archivo);
        } catch (Exception e) {
            errores.add("[FATAL] Error al parsear el XML: " + e.getMessage());
            return null;
        }
    }
    /**
     * Carga todos los elementos {@code <alumno>} dentro de {@code <alumnos>}.
     * Cada alumno se agrega al objeto universidad y al mapa auxiliar por matrícula.
     */
    private static void cargarAlumnos(Element raiz,
                                      Universidad universidad,
                                      Map<String, Alumno> mapaAlumnos,
                                      List<String> errores) {
        NodeList seccionAlumnos = raiz.getElementsByTagName("alumnos");
        if (seccionAlumnos.getLength() == 0) {
            errores.add("[ADVERTENCIA] No se encontró la sección <alumnos> en el XML.");
            return;
        }

        NodeList nodos = ((Element) seccionAlumnos.item(0)).getElementsByTagName("alumno");

        for (int i = 0; i < nodos.getLength(); i++) {
            Element el = (Element) nodos.item(i);
            String contexto = "<alumno> #" + (i + 1);
            String matricula     = atributo(el, "matricula");
            String apellido      = atributo(el, "apellido");
            String nombre        = atributo(el, "nombre");
            String fechaStr      = atributo(el, "fechaNacimiento");

            if (matricula.isEmpty()) {
                errores.add("[ERROR] " + contexto + ": falta el atributo 'matricula'. Elemento omitido.");
                continue;
            }
            contexto = "<alumno matricula='" + matricula + "'>";

            if (apellido.isEmpty()) {
                errores.add("[ERROR] " + contexto + ": falta el atributo 'apellido'. Elemento omitido.");
                continue;
            }
            if (nombre.isEmpty()) {
                errores.add("[ERROR] " + contexto + ": falta el atributo 'nombre'. Elemento omitido.");
                continue;
            }
            if (fechaStr.isEmpty()) {
                errores.add("[ERROR] " + contexto + ": falta el atributo 'fechaNacimiento'. Elemento omitido.");
                continue;
            }

            LocalDate fechaNacimiento;
            try {
                fechaNacimiento = LocalDate.parse(fechaStr);
            } catch (DateTimeParseException e) {
                errores.add("[ERROR] " + contexto + ": 'fechaNacimiento' no tiene formato válido (esperado: YYYY-MM-DD). Elemento omitido.");
                continue;
            }

            try {
                Alumno alumno = new Alumno(matricula, apellido, nombre, fechaNacimiento);
                universidad.agregarAlumno(alumno);
                mapaAlumnos.put(matricula.toLowerCase(), alumno);
            } catch (AlumnoDuplicadoException e) {
                errores.add("[ERROR] " + contexto + ": ya existe un alumno con esa matrícula. Elemento omitido.");
            } catch (ParametroNuloException | DatoInvalidoException e) {
                errores.add("[ERROR] " + contexto + ": " + e.getMessage() + ". Elemento omitido.");
            }
        }
    }
    /**
     * Carga todos los elementos {@code <asignatura>} dentro de {@code <asignaturas>}.
     * El atributo {@code tipo} determina qué subclase concreta instanciar (polimorfismo).
     */
    private static void cargarAsignaturas(Element raiz,
                                          Universidad universidad,
                                          Map<String, Asignatura> mapaAsignaturas,
                                          List<String> errores) {
        NodeList seccion = raiz.getElementsByTagName("asignaturas");
        if (seccion.getLength() == 0) {
            errores.add("[ADVERTENCIA] No se encontró la sección <asignaturas> en el XML.");
            return;
        }

        NodeList nodos = ((Element) seccion.item(0)).getElementsByTagName("asignatura");

        for (int i = 0; i < nodos.getLength(); i++) {
            Element el = (Element) nodos.item(i);
            String contexto = "<asignatura> #" + (i + 1);
            String tipo        = atributo(el, "tipo").toUpperCase();
            String codigo      = atributo(el, "codigo");
            String cuatriStr   = atributo(el, "cuatrimestre");
            String promoStr    = atributo(el, "promocional");
            String nombre      = textoHijo(el, "nombre");

            if (tipo.isEmpty()) {
                errores.add("[ERROR] " + contexto + ": falta el atributo 'tipo'. Elemento omitido.");
                continue;
            }
            if (codigo.isEmpty()) {
                errores.add("[ERROR] " + contexto + ": falta el atributo 'codigo'. Elemento omitido.");
                continue;
            }
            contexto = "<asignatura codigo='" + codigo + "'>";
            if (nombre.isEmpty()) {
                errores.add("[ERROR] " + contexto + ": falta el elemento hijo <nombre>. Elemento omitido.");
                continue;
            }
            if (cuatriStr.isEmpty()) {
                errores.add("[ERROR] " + contexto + ": falta el atributo 'cuatrimestre'. Elemento omitido.");
                continue;
            }
            int cuatrimestre;
            try {
                cuatrimestre = Integer.parseInt(cuatriStr);
            } catch (NumberFormatException e) {
                errores.add("[ERROR] " + contexto + ": 'cuatrimestre' no es un número entero válido ('" + cuatriStr + "'). Elemento omitido.");
                continue;
            }
            boolean promocional = false;
            if (!tipo.equals("PASANTIA")) {
                if (promoStr.isEmpty()) {
                    errores.add("[ADVERTENCIA] " + contexto + ": falta el atributo 'promocional'. Se asume 'false'.");
                } else if (promoStr.equalsIgnoreCase("true")) {
                    promocional = true;
                } else if (!promoStr.equalsIgnoreCase("false")) {
                    errores.add("[ADVERTENCIA] " + contexto + ": valor inesperado para 'promocional' ('" + promoStr + "'). Se asume 'false'.");
                }
            }
            Asignatura asignatura;
            try {
                switch (tipo) {
                    case "OBLIGATORIA":
                        asignatura = new AsignaturaObligatoria(codigo, nombre, cuatrimestre, promocional);
                        break;
                    case "OPTATIVA":
                        asignatura = new AsignaturaOptativa(codigo, nombre, cuatrimestre, promocional);
                        break;
                    case "PASANTIA":
                        // AsignaturaPasantiaTesis NO recibe 'promocional': siempre false en su constructor
                        asignatura = new AsignaturaPasantiaTesis(codigo, nombre, cuatrimestre);
                        break;
                    default:
                        errores.add("[ERROR] " + contexto + ": tipo desconocido '" + tipo + "'. "
                                + "Valores válidos: OBLIGATORIA, OPTATIVA, PASANTIA. Elemento omitido.");
                        continue;
                }

                universidad.agregarAsignatura(asignatura);
                mapaAsignaturas.put(codigo.toLowerCase(), asignatura);

            } catch (AsignaturaDuplicadaException e) {
                errores.add("[ERROR] " + contexto + ": ya existe una asignatura con ese código. Elemento omitido.");
            } catch (ParametroNuloException | DatoInvalidoException e) {
                errores.add("[ERROR] " + contexto + ": " + e.getMessage() + ". Elemento omitido.");
            }
        }
    }
    /**
     * Carga todos los {@code <curso>}, incluyendo sus {@code <clases>} e {@code <inscripciones>}.
     */
    private static void cargarCursos(Element raiz, Universidad universidad, Map<String, Asignatura> mapaAsignaturas, Map<String, Alumno> mapaAlumnos, List<String> errores) {
        NodeList seccion = raiz.getElementsByTagName("cursos");
        if (seccion.getLength() == 0) {
            errores.add("[ADVERTENCIA] No se encontró la sección <cursos> en el XML.");
            return;
        }
        NodeList nodos = ((Element) seccion.item(0)).getChildNodes();

        int numeroCurso = 0;
        for (int i = 0; i < nodos.getLength(); i++) {
            if (nodos.item(i).getNodeType() != Node.ELEMENT_NODE) continue;
            Element el = (Element) nodos.item(i);
            if (!el.getTagName().equals("curso")) continue;
            numeroCurso++;String idCurso = atributo(el, "idCurso");
            String codigoAsignatura = atributo(el, "codigoAsignatura");
            String anioStr = atributo(el, "anioCalendario");
            String cuatriStr = atributo(el, "cuatrimestreDictado");
            String contexto = "<curso> #" + numeroCurso;
            if (idCurso.isEmpty()) {
                errores.add("[ERROR] " + contexto + ": falta el atributo 'idCurso'. Elemento omitido.");
                continue;
            }
            contexto = "<curso idCurso='" + idCurso + "'>";

            if (codigoAsignatura.isEmpty()) {
                errores.add("[ERROR] " + contexto + ": falta 'codigoAsignatura'. Elemento omitido.");
                continue;
            }
            if (anioStr.isEmpty()) {
                errores.add("[ERROR] " + contexto + ": falta 'anioCalendario'. Elemento omitido.");
                continue;
            }
            if (cuatriStr.isEmpty()) {
                errores.add("[ERROR] " + contexto + ": falta 'cuatrimestreDictado'. Elemento omitido.");
                continue;
            }
            int anioCalendario, cuatrimestreDictado;
            try {
                anioCalendario = Integer.parseInt(anioStr);
            } catch (NumberFormatException e) {
                errores.add("[ERROR] " + contexto + ": 'anioCalendario' no es un entero válido ('" + anioStr + "'). Elemento omitido.");
                continue;
            }
            try {
                cuatrimestreDictado = Integer.parseInt(cuatriStr);
            } catch (NumberFormatException e) {
                errores.add("[ERROR] " + contexto + ": 'cuatrimestreDictado' no es un entero válido ('" + cuatriStr + "'). Elemento omitido.");
                continue;
            }
            Asignatura asignatura = mapaAsignaturas.get(codigoAsignatura.toLowerCase());
            if (asignatura == null) {
                errores.add("[ERROR] " + contexto + ": no existe ninguna asignatura con código '"
                        + codigoAsignatura + "'. Elemento omitido.");
                continue;
            }
            Curso curso;
            try {
                curso = new Curso(idCurso, asignatura, anioCalendario, cuatrimestreDictado);
            } catch (CursoDuplicadoException e) {
                errores.add("[ERROR] " + contexto + ": ya existe un curso con ese ID. Elemento omitido.");
                continue;
            } catch (ParametroNuloException | DatoInvalidoException e) {
                errores.add("[ERROR] " + contexto + ": " + e.getMessage() + ". Elemento omitido.");
                continue;
            }
            Map<String, Clase> mapaClases = new HashMap<>();
            cargarClasesDeCurso(el, curso, mapaClases, contexto, errores);
            cargarInscripcionesDeCurso(el, curso, mapaAlumnos, mapaClases, contexto, errores);
            try {
                universidad.agregarCurso(curso);
            } catch (CursoDuplicadoException e) {
                errores.add("[ERROR] " + contexto + ": ya existe un curso con ese ID. Elemento omitido.");
            } catch (ParametroNuloException e) {
                errores.add("[ERROR] " + contexto + ": " + e.getMessage() + ". Elemento omitido.");
            }
        }
    }

    /**
     * Carga las {@code <clase>} dentro del nodo {@code <clases>} de un curso.
     * Popula el mapa {@code mapaClases} para uso posterior en las asistencias.
     */
    private static void cargarClasesDeCurso(Element elCurso, Curso curso, Map<String, Clase> mapaClases, String contexto, List<String> errores) {
        NodeList seccion = elCurso.getElementsByTagName("clases");
        if (seccion.getLength() == 0) {
            return;
        }
        NodeList nodos = ((Element) seccion.item(0)).getChildNodes();
        int numeroClase = 0;

        for (int i = 0; i < nodos.getLength(); i++) {
            if (nodos.item(i).getNodeType() != Node.ELEMENT_NODE) continue;
            Element el = (Element) nodos.item(i);
            if (!el.getTagName().equals("clase")) continue;
            numeroClase++;

            String idClase = atributo(el, "id");
            String fechaHoraStr = atributo(el, "fechaHora");
            String ctxClase = contexto + " > <clase> #" + numeroClase;

            if (idClase.isEmpty()) {
                errores.add("[ERROR] " + ctxClase + ": falta el atributo 'id'. Elemento omitido.");
                continue;
            }
            ctxClase = contexto + " > <clase id='" + idClase + "'>";

            if (fechaHoraStr.isEmpty()) {
                errores.add("[ERROR] " + ctxClase + ": falta el atributo 'fechaHora'. Elemento omitido.");
                continue;
            }
            LocalDateTime fechaHora;
            try {
                fechaHora = LocalDateTime.parse(fechaHoraStr);
            } catch (DateTimeParseException e) {
                errores.add("[ERROR] " + ctxClase + ": 'fechaHora' no tiene formato válido "
                        + "(esperado: YYYY-MM-DDTHH:MM:SS). Elemento omitido.");
                continue;
            }

            try {
                Clase clase = new Clase(idClase, fechaHora);
                curso.agregarClase(clase);
                mapaClases.put(idClase.toLowerCase(), clase);
            } catch (ClaseDuplicadaException e) {
                errores.add("[ERROR] " + ctxClase + ": ya existe una clase con ese ID en este curso. Elemento omitido.");
            } catch (ParametroNuloException e) {
                errores.add("[ERROR] " + ctxClase + ": " + e.getMessage() + ". Elemento omitido.");
            }
        }
    }
    /**
     * Carga las {@code <inscripcion>} (y sus {@code <asistencia>}) dentro de un curso.
     */
    private static void cargarInscripcionesDeCurso(Element elCurso, Curso curso, Map<String, Alumno> mapaAlumnos, Map<String, Clase> mapaClases, String contexto, List<String> errores) {
        NodeList seccion = elCurso.getElementsByTagName("inscripciones");
        if (seccion.getLength() == 0) {
            return;
        }
        NodeList nodos = ((Element) seccion.item(0)).getChildNodes();
        int numeroInsc = 0;
        for (int i = 0; i < nodos.getLength(); i++) {
            if (nodos.item(i).getNodeType() != Node.ELEMENT_NODE) continue;
            Element el = (Element) nodos.item(i);
            if (!el.getTagName().equals("inscripcion")) continue;
            numeroInsc++;
            String matricula    = atributo(el, "matriculaAlumno");
            String modalidadStr = atributo(el, "modalidad").toUpperCase();
            String ctxInsc      = contexto + " > <inscripcion> #" + numeroInsc;
            if (matricula.isEmpty()) {
                errores.add("[ERROR] " + ctxInsc + ": falta el atributo 'matriculaAlumno'. Elemento omitido.");
                continue;
            }
            ctxInsc = contexto + " > <inscripcion alumno='" + matricula + "'>";

            if (modalidadStr.isEmpty()) {
                errores.add("[ERROR] " + ctxInsc + ": falta el atributo 'modalidad'. Elemento omitido.");
                continue;
            }
            Alumno alumno = mapaAlumnos.get(matricula.toLowerCase());
            if (alumno == null) {
                errores.add("[ERROR] " + ctxInsc + ": no existe ningún alumno con matrícula '"
                        + matricula + "'. Elemento omitido.");
                continue;
            }
            universidad.asignaturas.ModalidadCursada modalidad;
            try {
                modalidad = universidad.asignaturas.ModalidadCursada.valueOf(modalidadStr);
            } catch (IllegalArgumentException e) {
                errores.add("[ERROR] " + ctxInsc + ": modalidad desconocida '" + modalidadStr + "'. "
                        + "Valores válidos: REGULAR, CONDICIONAL, OYENTE. Elemento omitido.");
                continue;
            }
            Inscripcion inscripcion;
            try {
                inscripcion = new Inscripcion(alumno, curso, modalidad);
                curso.agregarInscripcion(inscripcion);
            } catch (AlumnoDuplicadoException e) {
                errores.add("[ERROR] " + ctxInsc + ": el alumno ya está inscripto en este curso. Elemento omitido.");
                continue;
            } catch (ParametroNuloException | InscripcionDuplicada e) {
                errores.add("[ERROR] " + ctxInsc + ": " + e.getMessage() + ". Elemento omitido.");
                continue;
            }
            cargarAsistenciasDeInscripcion(el, inscripcion, mapaClases, ctxInsc, errores);
        }
    }
    /**
     * Carga los registros de {@code <asistencia>} de una inscripción.
     * Cada asistencia referencia una clase por ID (debe existir en el curso).
     */
    private static void cargarAsistenciasDeInscripcion(Element elInscripcion, Inscripcion inscripcion, Map<String, Clase> mapaClases, String contexto, List<String> errores) {
        NodeList seccion = elInscripcion.getElementsByTagName("asistencias");
        if (seccion.getLength() == 0) {
            return;
        }
        NodeList nodos = ((Element) seccion.item(0)).getChildNodes();
        int numeroAsist = 0;

        for (int i = 0; i < nodos.getLength(); i++) {
            if (nodos.item(i).getNodeType() != Node.ELEMENT_NODE) continue;
            Element el = (Element) nodos.item(i);
            if (!el.getTagName().equals("asistencia")) continue;
            numeroAsist++;

            String idClase    = atributo(el, "idClase");
            String presenteStr = atributo(el, "presente").toLowerCase();
            String ctxAsist   = contexto + " > <asistencia> #" + numeroAsist;
            if (idClase.isEmpty()) {
                errores.add("[ERROR] " + ctxAsist + ": falta el atributo 'idClase'. Elemento omitido.");
                continue;
            }
            ctxAsist = contexto + " > <asistencia idClase='" + idClase + "'>";

            if (presenteStr.isEmpty()) {
                errores.add("[ERROR] " + ctxAsist + ": falta el atributo 'presente'. Elemento omitido.");
                continue;
            }
            if (!presenteStr.equals("true") && !presenteStr.equals("false")) {
                errores.add("[ERROR] " + ctxAsist + ": 'presente' debe ser 'true' o 'false' (se encontró '"
                        + presenteStr + "'). Elemento omitido.");
                continue;
            }
            Clase clase = mapaClases.get(idClase.toLowerCase());
            if (clase == null) {
                errores.add("[ERROR] " + ctxAsist + ": no existe ninguna clase con id '"
                        + idClase + "' en este curso. Elemento omitido.");
                continue;
            }

            boolean presente = presenteStr.equals("true");

            try {
                inscripcion.registrarAsistencia(clase, presente);
            } catch (AsistenciaYaRegistradaException e) {
                errores.add("[ERROR] " + ctxAsist + ": ya existe una asistencia registrada para esta clase. Elemento omitido.");
            } catch (ParametroNuloException e) {
                errores.add("[ERROR] " + ctxAsist + ": " + e.getMessage() + ". Elemento omitido.");
            }
        }
    }
    /**
     * Lee un atributo de un elemento. Retorna cadena vacía si no existe o está en blanco.
     */
    private static String atributo(Element el, String nombre) {
        if (!el.hasAttribute(nombre)) return "";
        return el.getAttribute(nombre).trim();
    }

    /**
     * Lee el contenido de texto del primer elemento hijo con el tag dado.
     * Retorna cadena vacía si no existe o está en blanco.
     */
    @SuppressWarnings("SameParameterValue")
    private static String textoHijo(Element el, String tagHijo) {
        NodeList lista = el.getElementsByTagName(tagHijo);
        if (lista.getLength() == 0) return "";
        String texto = lista.item(0).getTextContent();
        return (texto == null) ? "" : texto.trim();
    }
}
