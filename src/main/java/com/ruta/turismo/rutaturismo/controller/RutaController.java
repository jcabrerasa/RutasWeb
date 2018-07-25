package com.ruta.turismo.rutaturismo.controller;

import com.ruta.turismo.rutaturismo.Ruta;
import com.ruta.turismo.rutaturismo.controller.util.JsfUtil;
import com.ruta.turismo.rutaturismo.controller.util.JsfUtil.PersistAction;
import com.ruta.turismo.rutaturismo.session.RutaFacade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJBException;
import javax.inject.Named;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import org.omnifaces.util.Messages;
import org.primefaces.event.TabChangeEvent;
import org.primefaces.event.TabCloseEvent;
import org.primefaces.model.DualListModel;

@Named("rutaController")
@ViewScoped
public class RutaController implements Serializable {

    @Inject
    private RutaFacade ejbFacade;
    private List<Ruta> items = null;
     private List<Ruta> itemsTarget = null;
    private Ruta selected;
     private DualListModel<Ruta> rutasdual;
     private Long idimg;
     

    public RutaController() {
    }
    @PostConstruct
    public void init()
    {
    nuevoModel();
           
    }
    public void nuevoModel()
    {
        setSelected(null);
        items=null;
        itemsTarget=null;
         itemsTarget = new ArrayList<Ruta>();
            rutasdual  = new DualListModel<>( getItems(), itemsTarget);
    }

    public Ruta getSelected() {
        return selected;
    }

    public void setSelected(Ruta selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private RutaFacade getFacade() {
        return ejbFacade;
    }

    public Ruta prepareCreate() {
        selected = new Ruta();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("RutaCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("RutaUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("RutaDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Ruta> getItems() {
        if (items == null) {
            items = getFacade().findRustasActivas();
           
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                 if (persistAction == PersistAction.CREATE) {
                     Integer incremento= getFacade().incremento();
                     selected.setIdRuta(incremento);
                     getFacade().create(selected);
                 }
                 else if (persistAction == PersistAction.UPDATE) {
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

    public Ruta getRuta(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Ruta> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Ruta> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }
    public void onTabChange(TabChangeEvent event) {
        FacesMessage msg = new FacesMessage("Cambio de Tab", "Activación Tab: " + event.getTab().getTitle());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }
         
    public void onTabClose(TabCloseEvent event) {
        FacesMessage msg = new FacesMessage("Tab Cerrado", "Cerrado tab: " + event.getTab().getTitle());
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public List<Ruta> getItemsTarget() {
        return itemsTarget;
    }

    public void setItemsTarget(List<Ruta> itemsTarget) {
        this.itemsTarget = itemsTarget;
    }

    public DualListModel<Ruta> getRutasdual() {
        return rutasdual;
    }

    public void setRutasdual(DualListModel<Ruta> rutasdual) {
        this.rutasdual = rutasdual;
    }

    public Long getIdimg() {
        return idimg;
    }

    public void setIdimg(Long idimg) {
        this.idimg = idimg;
    }
    
    

  @FacesConverter("rutaControllerConverter")
    public  class RutaControllerConverter implements Converter {
        
        @Override
	public String getAsString(FacesContext context, UIComponent component, Object modelValue) {
		Integer id = (modelValue != null) ? ((Ruta) modelValue).getIdRuta() : null;
		return (id != null) ? String.valueOf(id) : "";
	}

	@Override
	public Object getAsObject(FacesContext context, UIComponent component, String submittedValue) {
		if (submittedValue == null || submittedValue.isEmpty()) {
			return null;
		}

		if (!submittedValue.matches("[0-9]+")) {
			throw new ConverterException(Messages.createError("Invalid ID."));
		}

		Integer id = Integer.valueOf(submittedValue);

		if (id != 42) {
			throw new ConverterException(Messages.createError("Unknown ID."));
		}

		return new Ruta(id);
	}

//        @Override
//        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
//            if (value == null || value.length() == 0) {
//                return null;
//            }
//            RutaController controller = (RutaController) facesContext.getApplication().getELResolver().
//                    getValue(facesContext.getELContext(), null, "rutaController");
//            return controller.getRuta(getKey(value));
//        }
//
//        java.lang.Integer getKey(String value) {
//            java.lang.Integer key;
//            key = Integer.valueOf(value);
//            return key;
//        }
//
//        String getStringKey(java.lang.Integer value) {
//            StringBuilder sb = new StringBuilder();
//            sb.append(value);
//            return sb.toString();
//        }
//
//        @Override
//        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
//            if (object == null) {
//                return null;
//            }
//            if (object instanceof Ruta) {
//                Ruta o = (Ruta) object;
//                return getStringKey(o.getIdRuta());
//            } else {
//                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Ruta.class.getName()});
//                return null;
//            }
//        }

    }

}
