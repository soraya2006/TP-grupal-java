package io;

import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;

import universidad.Universidad;
import universidad.alumnos.Alumno;
import universidad.asignaturas.*;
import universidad.asistencias.Asistencia;
import universidad.clases.Clase;
import universidad.inscripciones.Inscripcion;

/**
 * Serializa el estado completo de un objeto {@link Universidad} a un archivo XML.
 *
 * <p>El formato generado es exactamente el mismo que espera {@link CargadorXML},
 * por lo que el par Guardar/Cargar funciona de forma simétrica.</p>
 *
 * Uso:
 * <pre>
 *   GeneradorXML.guardar(universidad, "datos/universidad.xml");
 * </pre>
 */
public class GeneradorXML {

    /**
     * Escribe el estado completo de la universidad en un archivo XML.
     *
     * @param universidad Instancia a serializar.
     * @param rutaArchivo Ruta del archivo de destino (se crea o sobreescribe).
     */
    public static void guardar(Universidad universidad, String rutaArchivo) {
        try {
            // crear documento vacío
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();

            Element raiz = doc.createElement("universidad");
            doc.appendChild(raiz);

            raiz.appendChild(construirAlumnos(doc, universidad));
            raiz.appendChild(construirAsignaturas(doc, universidad));
            raiz.appendChild(construirCursos(doc, universidad));

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            File archivo = new File(rutaArchivo); // crea directorio si no existe
            if (archivo.getParentFile() != null) {
                archivo.getParentFile().mkdirs();
            }

            transformer.transform(new DOMSource(doc), new StreamResult(archivo));
            System.out.println("[GeneradorXML] Estado guardado correctamente en: " + rutaArchivo);

        } catch (Exception e) {
            System.err.println("[GeneradorXML] Error al guardar el XML: " + e.getMessage());
        }
    }

    // constructores
    private static Element construirAlumnos(Document doc, Universidad universidad) {
        Element seccion = doc.createElement("alumnos");
        for (Alumno a : universidad.getAlumnos()) {
            Element el = doc.createElement("alumno");
            el.setAttribute("matricula",        a.getMatricula());
            el.setAttribute("apellido",         a.getApellido());
            el.setAttribute("nombre",           a.getNombre());
            el.setAttribute("fechaNacimiento",  a.getFechaNacimiento().toString());
            seccion.appendChild(el);
        }
        return seccion;
    }

    private static Element construirAsignaturas(Document doc, Universidad universidad) {
        Element seccion = doc.createElement("asignaturas");
        for (Asignatura a : universidad.getAsignaturas()) {
            String tipo = a.getCodigoTipo();
            Element el = doc.createElement("asignatura");
            el.setAttribute("tipo",          tipo);
            el.setAttribute("codigo",        a.getCodigo());
            el.setAttribute("cuatrimestre",  String.valueOf(a.getCuatrimestre()));
            if (!tipo.equals("PASANTIA")) {
                el.setAttribute("promocional", String.valueOf(a.isPromocional()));
            }
            Element nombre = doc.createElement("nombre");
            nombre.setTextContent(a.getNombre());
            el.appendChild(nombre);

            seccion.appendChild(el);
        }
        return seccion;
    }

    private static Element construirCursos(Document doc, Universidad universidad) {
        Element seccion = doc.createElement("cursos");
        for (Curso c : universidad.getCursos()) {
            Element elCurso = doc.createElement("curso");
            elCurso.setAttribute("idCurso",              c.getIdCurso());
            elCurso.setAttribute("codigoAsignatura",     c.getAsignatura().getCodigo());
            elCurso.setAttribute("anioCalendario",       String.valueOf(c.getAnioCalendario()));
            elCurso.setAttribute("cuatrimestreDictado",  String.valueOf(c.getCuatrimestreDictado()));
            //Clases
            Element elClases = doc.createElement("clases");
            for (Clase clase : c.getClasesDictadas()) {
                Element elClase = doc.createElement("clase");
                elClase.setAttribute("id",        clase.getId());
                elClase.setAttribute("fechaHora", clase.getFechaHora().toString());
                elClases.appendChild(elClase);
            }
            elCurso.appendChild(elClases);
            //Inscripciones
            Element elInscripciones = doc.createElement("inscripciones");
            for (Inscripcion insc : c.getInscripciones()) {
                Element elInsc = doc.createElement("inscripcion");
                elInsc.setAttribute("matriculaAlumno", insc.getAlumno().getMatricula());
                elInsc.setAttribute("modalidad",       insc.getModalidad().name());
                //Asistencias
                Element elAsistencias = doc.createElement("asistencias");
                for (Asistencia asist : insc.getAsistencias()) {
                    Element elAsist = doc.createElement("asistencia");
                    elAsist.setAttribute("idClase",  asist.getClase().getId());
                    elAsist.setAttribute("presente", String.valueOf(asist.isPresente()));
                    elAsistencias.appendChild(elAsist);
                }
                elInsc.appendChild(elAsistencias);
                elInscripciones.appendChild(elInsc);
            }
            elCurso.appendChild(elInscripciones);

            seccion.appendChild(elCurso);
        }
        return seccion;
    }
}
