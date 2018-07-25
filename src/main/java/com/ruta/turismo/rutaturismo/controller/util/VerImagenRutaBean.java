/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ruta.turismo.rutaturismo.controller.util;



import com.ruta.turismo.rutaturismo.Ruta;
import com.ruta.turismo.rutaturismo.session.RutaFacade;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.event.PhaseId;
import javax.inject.Inject;
import org.omnifaces.util.Faces;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author cuer
 */
@ManagedBean(name = "verImagenRutaBean")
@SessionScoped
public class VerImagenRutaBean implements Serializable{
    
    @Inject
    private RutaFacade rutaFacade;
    
    private StreamedContent imagen = new DefaultStreamedContent();;
 

    @PostConstruct
     public void init() {
        setImagen(null);
     }
   
    public void setImagen(StreamedContent imagen) {
        this.imagen = imagen;
    }
    
 
 
     public StreamedContent getImagen() throws Exception {
        
        String atrCodigo = Faces.getExternalContext().getRequestParameterMap().get("rutaCodigo");
        System.out.println("futacodigo: "+atrCodigo);
        try {
            imagen=null;
        if (Faces.getContext().getCurrentPhaseId() != PhaseId.RENDER_RESPONSE) {
           
          
            
                if (atrCodigo != null || !atrCodigo.isEmpty() ) {

 
                    try {
//                         System.out.println("Investigador: "+atrCodigo);
                        Ruta ruta = rutaFacade.find(Integer.valueOf(atrCodigo));
                     
                      imagen = new DefaultStreamedContent(new ByteArrayInputStream(ruta.getImgruta()), ".jpg", "imagen.jpg");
                      return imagen;
                    } catch (NullPointerException e) {
                        System.out.println("  000 "+e);
                         return new DefaultStreamedContent();
                    }
                     
                  
                }
            
            else {
                
              return new DefaultStreamedContent();
            }
           }
        } catch (NullPointerException e) {
          return new DefaultStreamedContent();
        }
        return new DefaultStreamedContent();
        
        
        
    }
    
    
}

	
	
