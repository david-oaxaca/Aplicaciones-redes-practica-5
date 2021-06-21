/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pageget;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.System.exit;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

/**
 *
 * @author tdwda
 */
public class PageGet {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws MalformedURLException, IOException {
        
        Scanner scanner = new Scanner(System.in);
        Archivos Archivo_opts = new Archivos();
        PageDownload descarga = new PageDownload();
        
        //PageGet "Uploads" directory creation
        
        File ruta = Archivo_opts.crearArchivo(new File("").getAbsolutePath(), "/Uploads");
        //String rutaAbsoluta = ruta.getAbsolutePath();
         String command = "";
        
        System.out.println("**********************************************************************");
        System.out.println("Forma de uso:");
        System.out.println("Escriba URL de la pagina que desea descargar ");
        System.out.println("Ejemplo: http://148.204.58.221/axel/aplicaciones");
        System.out.println("Escriba Salir para salir del programa");
        System.out.println("**********************************************************************");
        
        
        while(!command.equalsIgnoreCase("SALIR")){
            //System.out.print( rutaAbsoluta + "$ " );
            System.out.print("Escriba la pagina web que desea descargar: ");
            command = scanner.nextLine();
            if(!command.equalsIgnoreCase("SALIR")){
                descarga.init_MainDownload(command, ruta, new ArrayList());
                
            }else{
                System.out.println("\n**********************************************************************");
                exit(0);
            }
            
            System.out.println("\n**********************************************************************");
            //exit(0);
        }
        
    }
    
    
}
