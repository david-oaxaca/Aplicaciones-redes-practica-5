/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pageget;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.jsoup.HttpStatusException;
import org.jsoup.nodes.TextNode;

/**
 *
 * @author tdwda
 */
public class PageDownload extends Thread{

    private PageOptions pageGet_opts;
    private Archivos Archivo_opts;
    private ArrayList <String> alreadyDownloaded;
    private volatile ArrayList<String> carpetasDowloaded = new ArrayList();
    private String [] blacklist = {"facebook", "twitter", "google", "wikipedia"};
    protected ExecutorService pool = Executors.newFixedThreadPool(5);
    
    public PageDownload() {
        this.Archivo_opts = new Archivos();
    }
    
    
    public void init_MainDownload(String URL, File ruta, ArrayList <String> alreadyD) throws MalformedURLException, IOException{
        ArrayList <String> page_links;
        ArrayList <String> page_media;
        ArrayList <String> page_imports;
        ArrayList <String> redirecciones_paginas = new ArrayList();
        String aux_ruta;
        String aux_URL;
        
        this.alreadyDownloaded = alreadyD;
        
        this.pageGet_opts = new PageOptions(URL);
        this.pageGet_opts.crearConexion();
        this.carpetasDowloaded.add(URL);

        String page_URL =this.pageGet_opts.getPagina().toString();
        
        page_links = validateBlackList(getLinksHTML(page_URL, "a", "href"));
        page_media = getLinksHTML(page_URL, "img", "src");
        page_imports = getLinksHTML(page_URL, "link", "href");
        
        String nueva_URL = init_Downloads(ruta.getAbsolutePath(), URL);
        //System.out.println(nueva_URL);
        
        this.alreadyDownloaded.add(nueva_URL);

        ArrayList <String> nuevaMedia = new ArrayList();
        for (int i = 0; i < page_media.size(); i++) {

            String archivo_descargado = init_Downloads(ruta.getAbsolutePath(), page_media.get(i));
            nuevaMedia.add(archivo_descargado);

            this.alreadyDownloaded.add(getFileName(archivo_descargado));
        }

        ArrayList <String> nuevosLinks = new ArrayList();
        for (int i = 0; i < page_links.size(); i++) {

            String archivo_descargado = init_Downloads(ruta.getAbsolutePath(), page_links.get(i));
            nuevosLinks.add(archivo_descargado);


            if(archivo_descargado.contains(".html") && !this.alreadyDownloaded.contains(archivo_descargado)){
                 redirecciones_paginas.add(page_links.get(i));
                //init_MainDownload(page_links.get(i), ruta, this.alreadyDownloaded);
                //this.alreadyDownloaded.add(page_links.get(i));
                //this.pool.execute(new Download_Pool(page_links.get(i), ruta, this.alreadyDownloaded));
            }

            this.alreadyDownloaded.add(getFileName(archivo_descargado));

        }

        if(!nuevosLinks.isEmpty()){
            replaceLink(nueva_URL, nuevosLinks, "a", "href");
        }
        if(!nuevaMedia.isEmpty()){
            replaceLink(nueva_URL, nuevaMedia, "img", "src");
        }
            
        for (String redirecciones_pagina : redirecciones_paginas) {
            if(!this.alreadyDownloaded.contains(redirecciones_pagina)){
                ArrayList <String> no_necesarios = new ArrayList();
                no_necesarios.addAll(this.alreadyDownloaded);
                no_necesarios.addAll(redirecciones_paginas);
                this.pool.execute(new Download_Pool(redirecciones_pagina, ruta, no_necesarios));
            }
        }
        
    }
   
    /**
    * @brief: El método descarga el archivo de una ruta especifica si no se encuentra
    *         en la lista de alreadyDownloaded
    *
    * @param URL            URL del elemento a descargar 
    * @param ruta           Ruta donde estara el elemento descargado
    * 
    * @return  Regresa la ruta del elemento que se descargo o la ruta  
    *          del elemento si ya se habia descargado
    */
    
    public String init_Downloads(String ruta, String URL) throws IOException{
        PageOptions URL_opts = new PageOptions(URL);
        URL_opts.crearConexion();
        
        String pag_ruta = URL_opts.getPagina().getFile();
        File archivoN;
        File pag_descarga = this.Archivo_opts.crearArchivo(ruta, pag_ruta);
        
        if(getFileName(pag_ruta).contains(".")){
            archivoN = new File(pag_descarga.getAbsolutePath());
        }else if(getFileName(pag_ruta).contains("?")){
            String pag_ruta_D = pag_descarga.getAbsolutePath();
            String requestPath = pag_ruta_D.substring(0, pag_ruta_D.length() - getFileName(pag_ruta).length());
            archivoN = new File(requestPath + "/index.html");
        }else{
            archivoN = new File(pag_descarga.getAbsolutePath() + "/index.html");
        }
        
        if(!this.alreadyDownloaded.contains(getFileName(archivoN.getAbsolutePath()))){
            try{
                InputStream in = URL_opts.getPag_conexion().getInputStream(); 
                System.out.println("Ruta de la pagina descargada: ");

                System.out.println(archivoN.getAbsolutePath());
                Path path = Paths.get(archivoN.getAbsolutePath());

                Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
            }catch(IOException e){
                System.out.println("ERROR 403 \t Pagina no accesible");
                
            }
            
        }
        
        
        return archivoN.getAbsolutePath();
    }
    
    
    /**
    * @brief: El método se encarga de reemplazar todos los valores de un atributo del tipo de elemento 
    *       especificado de un archivo HTML 
    *
    * @param archivo        Ruta del archivo HTML 
    * @param linksNuevo     ArrayList con todos los valores que reemplazaran a los anteriores 
    * @param elem           Elemento de HTML. Ejemplo: img  
    * @param attribute      Atributo de el elemnto de HTML. Ejemplo: El atributo src de img  
    * 
    */
    
    public void replaceLink(String archivo, ArrayList <String> linksNuevo, String elem, String attribute) 
    throws IOException{
        
        Document doc = Jsoup.parse(new File(archivo), "ISO-8859-1");
        Elements links = doc.select(elem + "[" + attribute + "]");
        PrintWriter pw = new PrintWriter(archivo);
        
        for (int i = 0; i < links.size(); i++) {
            Element link = links.get(i);
            link.attr(attribute, linksNuevo.get(i));   
            
        }
        pw.write(doc.html());
        pw.flush();
        pw.close();
    }
    
    /**
    * @brief: El método se encarga de obtener todos los valores de los elementos con 
    *       el atributo especificado de un archivo HTML en una URL
    *
    * @param URL            URL del archivo HTML 
    * @param elem           Elemento de HTML. Ejemplo: img  
    * @param attribute      Atributo de el elemnto de HTML. Ejemplo: El atributo src de img
    * 
    * @return  ArrayList con todos los valores de los atributos del elemento especificado
    */
    
    public ArrayList <String> getLinksHTML(String URL, String elem, String attribute) throws IOException{
        Document doc; 
        ArrayList <String> references = new ArrayList();
        
        doc = Jsoup.connect(URL).get();
        Elements links = doc.select(elem + "[" + attribute + "]");

        links.forEach(link -> {
            //System.out.println(link.attr("abs:href"));
            references.add(link.attr("abs:" + attribute));
        }); 
        
        return references;
    }
    
    /**
    * @brief: El método obtiene el nombre del archivo de una URL especificadad
    *         Por ejemplo: El unkoun.gif de \\icons\\unknown.gif 
    *
    * @param URL   URL de la cual se requiere el nombre del archivo 
    * 
    * @return  Nombre del archivo de una URL especificada
    */
    
    public String getFileName(String URL){
        String [] fileTokens = URL.split("/");
        int len = fileTokens.length;
        if(len > 0){
            return fileTokens[len - 1];
        }else{
            return URL;
        }
        
    }
    
    /**
    * @brief: El método elimina los enlaces de una lista que contenga un sitio en la lista negra ya sea
    *         por su tamaño o por otra razon.
    *
    * @param links   ArrayList con los enlaces que validara
    * 
    * @return  Un arraylist sin enlaces dentro de la lista negra
    */
    
    public ArrayList <String> validateBlackList(ArrayList <String> links){
        for (int i = 0; i < links.size()-2; i++) {
            for (String blacklist_elem : this.blacklist) {
                if (links.get(i).contains(blacklist_elem)) {
                    links.remove(i);
                }
            }
        }
        
        return links;
    }
}

