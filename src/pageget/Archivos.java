/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pageget;

import java.io.File;

/**
 *
 * @author tdwda
 */
public class Archivos {
    
    /**
    * El método se encarga de verificar la direccion absoluta dada para la creación
    * de un archivo, en caso de que no existe el directorio raiz lo creara, de manera recursiva
    * hasta que dicha ruta proporcionada exista
    *
    * @param  carpeta   Directorio del nuevo archivo que se creara
    */
    private void verificarPath(File carpeta) {
        if(!carpeta.getParentFile().exists()){
            verificarPath(carpeta.getParentFile());
        }
        
        if(!carpeta.exists()) {
            carpeta.mkdir();
        }
    }

    
    /**
    * El método se encarga de crear un archivo con el parametro dado y devolverlos
    *
    * @param ruta           ruta donde se creara el nuevo archivo
    * @param  nombre        nombre del nuevo archvio que se creara
    * @return  carpeta con el File creado
    */
    public File crearArchivo(String ruta, String nombre) {
        String ruta_nueva = ruta +  nombre;
        File carpeta = new File(ruta_nueva);
        
        //Verificamos que previamente exista la ruta del servidor
        verificarPath(carpeta);
        return carpeta;
    }
}
