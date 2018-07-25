package com.ruta.turismo.rutaturismo.controller;

import com.ruta.turismo.rutaturismo.Ruta;
import com.ruta.turismo.rutaturismo.Trayecto;
import com.ruta.turismo.rutaturismo.controller.util.JsfUtil;
import com.ruta.turismo.rutaturismo.controller.util.JsfUtil.PersistAction;
import com.ruta.turismo.rutaturismo.session.TrayectoFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.event.UnselectEvent;
import org.primefaces.event.map.MarkerDragEvent;
import org.primefaces.event.map.OverlaySelectEvent;
import org.primefaces.model.DualListModel;
import org.primefaces.model.map.Circle;
import org.primefaces.model.map.DefaultMapModel;
import org.primefaces.model.map.LatLng;
import org.primefaces.model.map.MapModel;
import org.primefaces.model.map.Marker;
import org.primefaces.model.map.Polyline;

@Named("trayectoController")
@ViewScoped
public class TrayectoController implements Serializable {

    
    @EJB
    private com.ruta.turismo.rutaturismo.session.TrayectoFacade ejbFacade;
    private List<Trayecto> items = null;
    private List<Trayecto> trayectosruta = null;
     private List<Trayecto> mistrayectos = null;
    private Trayecto selected;
    private Trayecto selectedimg;
    @Inject
    private RutaController rutaController;
   
    
    private DualListModel<Ruta> rutasdual;

    private MapModel emptyModel;
    private Marker marker;
    
     private MapModel miemptyModel;
    private Marker mimarker;

    private double lat;

    private double lng;
    private List<Ruta> rutastarget;
    private Integer orden=0;

    private HashSet stateSet=null ;
    public TrayectoController() {
    }

    public Trayecto getSelected() {
        return selected;
    }

    public void setSelected(Trayecto selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    public RutaController getRutaController() {
        return rutaController;
    }

    public void setRutaController(RutaController rutaController) {
        this.rutaController = rutaController;
    }

    private TrayectoFacade getFacade() {
        return ejbFacade;
    }

    public void addMarker() {
        lat = selected.getLatitud();
        lng = selected.getLongitud();
        LatLng coord1=new LatLng(lat, lng);
       marker = new Marker(coord1, selected.getDescripcion());

        emptyModel.addOverlay(marker);
        Polyline polyline = new Polyline();
        polyline.getPaths().add(coord1);
        polyline.setStrokeWeight(4);
        polyline.setStrokeColor("#FF9900");
        polyline.setStrokeOpacity(0.5);

                emptyModel.addOverlay(polyline);

        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Marker Added", "Lat:" + lat + ", Lng:" + lng));
    }
    public void onMarkerSelect(OverlaySelectEvent event) {
        marker = (Marker) event.getOverlay();
        selectedimg=(Trayecto)marker.getData();
    }
     public void onMiMarkerSelect(OverlaySelectEvent event) {
        mimarker = (Marker) event.getOverlay();
    }
     public Trayecto prepareCreate() {

        cargapuntos();
        selected = new Trayecto();
//        selected.setIdRuta(rutaController.getSelected());
//        selected.setTipotrayecto("D");// tipo de proyecto creados por administraodr
       

        return selected;
    }
    
    public void cargapuntos() {
        emptyModel = new DefaultMapModel();
        rutastarget=rutaController.getRutasdual().getTarget();
        System.out.println(" -------- "+rutastarget);
        for(Ruta ruta:rutastarget)
        {
            
        
        trayectosruta = ejbFacade.trayectos(ruta);
        Polyline polyline = new Polyline();
        if (trayectosruta != null) {
            int inicio=0;
            int fin=trayectosruta.size();
            int cont=0;
            for (Trayecto t : trayectosruta) {
                LatLng coord1=null;
                
                coord1 = new LatLng(t.getLatitud(), t.getLongitud());
                 //Circle
       
 
        
                if(inicio==cont)
                    {
                        System.out.println(fin+" INICIO "+cont); 
                         Circle circle1 = new Circle(coord1, 50);
                        circle1.setStrokeColor("#0000FF");
                        circle1.setFillColor("#0000FF");
                        circle1.setFillOpacity(0.5);
                        
//                        emptyModel.addOverlay(new Marker(coord1, "DESCRIPCIÓN: "+t.getDescripcion()));
                        emptyModel.addOverlay(new Marker(coord1, "DESCRIPCIÓN: "+t.getDescripcion()+" - DIRECCIÓN: "+t.getDireccion()+" - COSTO:"+t.getCostovisita(),t,"https://maps.google.com/mapfiles/ms/micons/blue-dot.png"));
                          emptyModel.addOverlay(circle1);
                    }
                if (cont>0 && cont< fin-1)
                    {
                         System.out.println(fin+" MEDIO "+cont); 
//                         emptyModel.addOverlay(new Marker(coord1, "DESCRIPCIÓN: "+t.getDescripcion()));
                        emptyModel.addOverlay(new Marker(coord1, "DESCRIPCIÓN: "+t.getDescripcion()+" - DIRECCIÓN: "+t.getDireccion()+" - COSTO:"+t.getCostovisita(),t));   
                    }
                 if(fin-1==cont)
                    {
                         System.out.println(fin+" FIN "+cont); 
                         Circle circle2 = new Circle(coord1, 50);
                        circle2.setStrokeColor("#ffcc00");
                        circle2.setFillColor("#ffcc00");
                        circle2.setStrokeOpacity(0.5);
                         circle2.setFillOpacity(0.5);
                        


                        emptyModel.addOverlay(circle2);
//                        emptyModel.addOverlay(new Marker(coord1, "DESCRIPCIÓN: "+t.getDescripcion()));
                       emptyModel.addOverlay(new Marker(coord1, "DESCRIPCIÓN: "+t.getDescripcion()+" - DIRECCIÓN: "+t.getDireccion()+" - COSTO:"+t.getCostovisita(),t,"https://maps.google.com/mapfiles/ms/micons/yellow-dot.png")); 
                    }
                 
                polyline.getPaths().add(coord1);
                polyline.setStrokeWeight(4);
                if(ruta.getColor()!=null)
                {
                    if(ruta.getColor().length()>0)
                    {
                        polyline.setStrokeColor("#"+ruta.getColor());
                    }
                    else{
                        polyline.setStrokeColor("#FF9900");
                    }
                    
                }
                else
                {
                     polyline.setStrokeColor("#FF9900");
                }
                
                polyline.setStrokeOpacity(0.5);

                emptyModel.addOverlay(polyline);
                 
                cont++;
                       
            }
            for(Marker premarker : emptyModel.getMarkers()) {
                 premarker.setDraggable(false);
             }
        }
        }
        

    }
    public void cargaMispuntos() {
         miemptyModel = new DefaultMapModel();
       
           
        Polyline polyline = new Polyline();
        if (mistrayectos != null) {
            int inicio=0;
            int fin=mistrayectos.size();
            int cont=0;
            for (Trayecto t : mistrayectos) {
                LatLng coord1=null;
                
                coord1 = new LatLng(t.getLatitud(), t.getLongitud());
                 //Circle
       
 
        
                if(inicio==cont)
                    {
                        System.out.println(fin+" INICIO "+cont); 
                         Circle circle1 = new Circle(coord1, 50);
                        circle1.setStrokeColor("#0000FF");
                        circle1.setFillColor("#0000FF");
                        circle1.setFillOpacity(0.5);
                        
//                        emptyModel.addOverlay(new Marker(coord1, "DESCRIPCIÓN: "+t.getDescripcion()));
                        miemptyModel.addOverlay(new Marker(coord1, "DESCRIPCIÓN: "+t.getDescripcion()+" - DIRECCIÓN: "+t.getDireccion()+" - COSTO:"+t.getCostovisita(),t,"https://maps.google.com/mapfiles/ms/micons/blue-dot.png"));
                          miemptyModel.addOverlay(circle1);
                    }
                if (cont>0 && cont< fin-1)
                    {
                         System.out.println(fin+" MEDIO "+cont); 
//                         emptyModel.addOverlay(new Marker(coord1, "DESCRIPCIÓN: "+t.getDescripcion()));
                        miemptyModel.addOverlay(new Marker(coord1, "DESCRIPCIÓN: "+t.getDescripcion()+" - DIRECCIÓN: "+t.getDireccion()+" - COSTO:"+t.getCostovisita(),t));   
                    }
                 if(fin-1==cont)
                    {
                         System.out.println(fin+" FIN "+cont); 
                         Circle circle2 = new Circle(coord1, 50);
                        circle2.setStrokeColor("#ffcc00");
                        circle2.setFillColor("#ffcc00");
                        circle2.setStrokeOpacity(0.5);
                         circle2.setFillOpacity(0.5);
                        


                        miemptyModel.addOverlay(circle2);
//                        emptyModel.addOverlay(new Marker(coord1, "DESCRIPCIÓN: "+t.getDescripcion()));
                       miemptyModel.addOverlay(new Marker(coord1, "DESCRIPCIÓN: "+t.getDescripcion()+" - DIRECCIÓN: "+t.getDireccion()+" - COSTO:"+t.getCostovisita(),t,"https://maps.google.com/mapfiles/ms/micons/yellow-dot.png")); 
                    }
                 
                polyline.getPaths().add(coord1);
                polyline.setStrokeWeight(4);
                polyline.setStrokeColor("#FF9900");
                               
                polyline.setStrokeOpacity(0.5);

                miemptyModel.addOverlay(polyline);
                 
                cont++;
                       
            }
           
        }
        
        

    }
    
    
    public void limpia()
    {
            mistrayectos=null;
            stateSet = null;
            orden=0;
    }
    public void mistrayectosadd()
    {
       

//        Trayecto mitrayecto=(Trayecto)marker.getData();
         Trayecto mitrayecto=selectedimg;
    
     
        if(mistrayectos==null)
        {
            mistrayectos=new ArrayList<>();
            stateSet = new HashSet();
        }
       
        if(stateSet.contains(mitrayecto))
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "Ya esta agregado este trayeto, por favor seleccione otro", "Lat:" + marker.getLatlng().getLat() + ", Lng:" + marker.getLatlng().getLng()));
        }
        else
        {
             stateSet.add(mitrayecto);
           orden++;
           mitrayecto.setOrden(orden);
             mistrayectos.add(mitrayecto);
             
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Agregado mi trayecto", "Lat:" + marker.getLatlng().getLat() + ", Lng:" + marker.getLatlng().getLng()));
             
        }
       
        System.out.println(" +++  "+mistrayectos);
        
    }
    public void mistrayectosremove()
    {
       

        Trayecto mitrayecto=(Trayecto)marker.getData();
     
        if(mistrayectos==null)
        {
            mistrayectos=new ArrayList<>();
            stateSet = new HashSet();
        }
    
         if(stateSet.contains(mitrayecto))
        {
             stateSet.remove(mitrayecto);
             mistrayectos.remove(mitrayecto);
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Se ha eliminado trayecto", "Lat:" + marker.getLatlng().getLat() + ", Lng:" + marker.getLatlng().getLng()));
             
            
        }
        else
        {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_WARN, "No esta agregado este trayeto..", "Lat:" + marker.getLatlng().getLat() + ", Lng:" + marker.getLatlng().getLng()));
        }
        
            
       
       
        System.out.println(" +++  "+mistrayectos);
        
    }
     public void onMarkerDrag(MarkerDragEvent event) {
        marker = event.getMarker();
          
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Marker Dragged", "Lat:" + marker.getLatlng().getLat() + ", Lng:" + marker.getLatlng().getLng()));
    }
    
    public void onTransfer(TransferEvent event) {
        StringBuilder builder = new StringBuilder();
        for(Object item : event.getItems()) {
            builder.append(((Ruta) item)).append("<br />");
        }
         
        FacesMessage msg = new FacesMessage();
        msg.setSeverity(FacesMessage.SEVERITY_INFO);
        msg.setSummary("Ruta Seleccionada");
        msg.setDetail(builder.toString());
         
        FacesContext.getCurrentInstance().addMessage(null, msg);
    } 
 
    public void onSelect(SelectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Ruta Seleccioanda", event.getObject().toString()));
    }
     
    public void onUnselect(UnselectEvent event) {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "Ruta no seleccionanda", event.getObject().toString()));
    }
     
    public void onReorder() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "List Reordered", null));
    } 
    public Integer incremento()
    {
      return  ejbFacade.incremento();
    }
 

  

    public Trayecto selectednull() {
        selected = null;

        return selected;
    }

    public void create() {
        addMarker();
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("TrayectoCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("TrayectoUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("TrayectoDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Trayecto> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {

            setEmbeddableKeys();
            try {
                if (persistAction == PersistAction.CREATE) {
                    Integer incremento = getFacade().incremento();
                    selected.setIdTrayecto(incremento);
                    getFacade().create(selected);
                } else if (persistAction == PersistAction.UPDATE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Trayecto getTrayecto(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Trayecto> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Trayecto> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public MapModel getEmptyModel() {
        return emptyModel;
    }

    public void setEmptyModel(MapModel emptyModel) {
        this.emptyModel = emptyModel;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public List<Ruta> getRutastarget() {
        return rutastarget;
    }

    public void setRutastarget(List<Ruta> rutastarget) {
        this.rutastarget = rutastarget;
    }
    

    public List<Trayecto> getTrayectosruta() {
        return trayectosruta;
    }

    public void setTrayectosruta(List<Trayecto> trayectosruta) {
        this.trayectosruta = trayectosruta;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public List<Trayecto> getMistrayectos() {
        return mistrayectos;
    }

    public void setMistrayectos(List<Trayecto> mistrayectos) {
        this.mistrayectos = mistrayectos;
    }

    public MapModel getMiemptyModel() {
        return miemptyModel;
    }

    public void setMiemptyModel(MapModel miemptyModel) {
        this.miemptyModel = miemptyModel;
    }

    public Marker getMimarker() {
        return mimarker;
    }

    public void setMimarker(Marker mimarker) {
        this.mimarker = mimarker;
    }

    public Trayecto getSelectedimg() {
        return selectedimg;
    }

    public void setSelectedimg(Trayecto selectedimg) {
        this.selectedimg = selectedimg;
    }
    
    
    

    @FacesConverter(forClass = Trayecto.class)
    public static class TrayectoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            TrayectoController controller = (TrayectoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "trayectoController");
            return controller.getTrayecto(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Trayecto) {
                Trayecto o = (Trayecto) object;
                return getStringKey(o.getIdTrayecto());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Trayecto.class.getName()});
                return null;
            }
        }

    }

}
