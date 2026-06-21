package io;
import universidad.Universidad;
import java.io.*;
/**
 * Gestiona la persistencia binaria del estado completo del sistema.
 *
 * <p>Utiliza serialización estándar de Java para guardar y recuperar
 * el objeto {@link Universidad} desde un archivo binario (.dat).</p>
 *
 * <p>El par {@link #guardarEstado} / {@link #cargarEstado} funciona
 * de forma simétrica: lo que uno escribe, el otro lo recupera.</p>
 */
public class PersistenciaBinaria {
    /**
     * Serializa el estado completo del sistema en un archivo binario.
     *
     * <p>Si el archivo no existe, se crea automáticamente.
     * Si ya existe, se sobreescribe.</p>
     *
     * @param universidad  Instancia a serializar. No debe ser {@code null}.
     * @param rutaArchivo  Ruta del archivo de destino (ej: {@code "datos/universidad.dat"}).
     */
    public static void guardarEstado(Universidad universidad, String rutaArchivo) {
        try (FileOutputStream fout = new FileOutputStream(rutaArchivo);
             ObjectOutputStream outStream = new ObjectOutputStream(fout)) {
            outStream.writeObject(universidad);
            System.out.println("Sistema guardado correctamente en: " + rutaArchivo);

        } catch (IOException e) {
            System.err.println("Error al guardar el estado: " + e.getMessage());
        }
    }
    /**
     * Recupera el estado del sistema desde un archivo binario previamente guardado.
     *
     * <p>Si el archivo no existe o ocurre un error durante la lectura,
     * se informa por consola y se retorna {@code null}.</p>
     *
     * @param rutaArchivo  Ruta del archivo a leer (ej: {@code "datos/universidad.dat"}).
     * @return La instancia de {@link Universidad} recuperada,
     *         o {@code null} si el archivo no existe o no pudo leerse.
     */
    public static Universidad cargarEstado(String rutaArchivo) {
        Universidad universidadRecuperada = null;
        try (FileInputStream fin = new FileInputStream(rutaArchivo);
             ObjectInputStream inStream = new ObjectInputStream(fin)) {
            universidadRecuperada = (Universidad) inStream.readObject();
            System.out.println("Sistema cargado correctamente desde: " + rutaArchivo);

        } catch (FileNotFoundException e) {
            System.out.println("Archivo de guardado no encontrado. Se iniciará un sistema nuevo.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error crítico al cargar el estado: " + e.getMessage());
        }
        return universidadRecuperada;
    }
}
