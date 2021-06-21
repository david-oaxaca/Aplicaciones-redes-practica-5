/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pageget;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

/**
 *
 * @author tdwda
 */
public class Download_Pool implements Runnable{
    private Archivos Archivo_opts = new Archivos();
    private PageDownload descarga = new PageDownload();
    private ArrayList <String> alreadyDownloaded;
    File ruta;
    String URL;
    
    public Download_Pool(String URL, File ruta, ArrayList <String> alreadyD)
    {
        this.alreadyDownloaded = alreadyD;
        this.ruta = ruta;
        this.URL = URL;
    }
    
    
    public void run() 
    {
        try {
            descarga.init_MainDownload(URL, ruta, alreadyDownloaded);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("**** Termino hilo ****");
    }
    
}
