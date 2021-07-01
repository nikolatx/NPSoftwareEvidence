package tnt.npse.controllers;

import tnt.npse.entities.Status;
import tnt.npse.controllers.util.JsfUtil;
import tnt.npse.controllers.util.JsfUtil.PersistAction;
import tnt.npse.beans.StatusFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import tnt.npse.model.LicenseData;

@Named("statusController")
@RequestScoped
public class StatusController implements Serializable {

    @EJB
    private tnt.npse.beans.StatusFacade ejbFacade;
    private List<Status> items = null;
    private Status selected;

    public StatusController() {
    }

    public Status getSelected() {
        return selected;
    }

    public void setSelected(Status selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private StatusFacade getFacade() {
        return ejbFacade;
    }

    public Status prepareCreate() {
        selected = new Status();
        initializeEmbeddableKey();
        return selected;
    }
    
    public void create(String name) {
        FacesContext context=FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
//        ec.getFlash().setKeepMessages(true);
        items=getItems();
        long count=items.stream().filter(st->st.getName().equalsIgnoreCase(name)).count();
        if (count==0) {
            Status stat=new Status();
            stat.setName(name);
            selected=stat;
            create();
            selected=items.stream().filter(e->e.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        } else {
            context.validationFailed();
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("EditStatusExists"));
        }
    }
    

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("StatusCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
            items=getItems();
        }
    }

    public void update(Status selectedStat) {
        FacesContext context=FacesContext.getCurrentInstance();
        items=null;
        items=getItems();
        if (selectedStat!=null) {
            long count=items.stream().filter(s->s.getName().equalsIgnoreCase(selectedStat.getName())).count();
            if (count==0) {
                selected=selectedStat;
                persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("StatusUpdated"));
            } else {
                context.validationFailed();
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("StatusExists"));
            }
        }
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
    
    
    //updates fields of status which name is not changed (licenseSet)
    public void updateStatusData(Status selectedStat, boolean messages) {
        selected=selectedStat;
        if (messages)
            persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("StatusUpdated"));
        else
            persist(PersistAction.UPDATE, "");
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    
    public void update(LicenseData selectedLic, String newName) {
        FacesContext context=FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
//        ec.getFlash().setKeepMessages(true);
        items=getItems();
        String oldName=selectedLic.getStatusName();
        selected=items.stream().filter(s->s.getName().equalsIgnoreCase(oldName)).findFirst().orElse(null);
        if (selected!=null) {
            long count=items.stream().filter(s->s.getName().equalsIgnoreCase(newName)).count();
            if (count==0) {
                selected.setName(newName);
                persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("StatusUpdated"));
                selectedLic.setStatusName(newName);
            } else {
                context.validationFailed();
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("StatusExists"));
            }
        }
    }
    
    
    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("StatusUpdated"));
    }

    public void destroy(Status status) {
        selected=status;
        destroy();
    }
    
    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("StatusDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Status> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                if (!successMessage.isEmpty())
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

    public Status getStatus(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Status> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Status> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Status.class)
    public static class StatusControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            StatusController controller = (StatusController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "statusController");
            return controller.getStatus(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Status) {
                Status o = (Status) object;
                return getStringKey(o.getStatusId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Status.class.getName()});
                return null;
            }
        }

    }

}
