package io;
import universidad.Universidad;
import java.io.*;
public class PersistenciaBinaria {
    // metodo q guardar todo el estado del sistema en un archivo binario
    public static void guardarEstado(Universidad universidad, String rutaArchivo) {
        try (FileOutputStream fout = new FileOutputStream(rutaArchivo);
             ObjectOutputStream outStream = new ObjectOutputStream(fout)) {
            outStream.writeObject(universidad);
            System.out.println("Sistema guardado correctamente en: " + rutaArchivo);

        } catch (IOException e) {
            System.err.println("Error al guardar el estado: " + e.getMessage());
        }
    }
    // metodo para cargar el estado del sistema desde el archivo binario
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
