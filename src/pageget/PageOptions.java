package pageget;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author David Oaxaca
 * @author David Madrigal
 * 
 */
public class PageOptions {

    private URL pagina;
    private URLConnection Pag_conexion;
    
    public PageOptions(URL PagURL){
        this.pagina = PagURL;
    }
    
    public PageOptions(String PagURL) throws MalformedURLException{
        this.pagina = new URL(PagURL);
    }
    
    public void crearConexion() throws IOException{
        this.Pag_conexion = this.pagina.openConnection();
    }
    
    public int getLength(){
        return this.Pag_conexion.getContentLength();
    }
    
    public String getType(){
        return this.Pag_conexion.getContentType();
    }

    public URL getPagina() {
        return pagina;
    }

    public void setPagina(URL pagina) {
        this.pagina = pagina;
    }

    public URLConnection getPag_conexion() {
        return Pag_conexion;
    }

    public void setPag_conexion(URLConnection Pag_conexion) {
        this.Pag_conexion = Pag_conexion;
    }
    
    
    
    
}
