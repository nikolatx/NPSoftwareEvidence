package tnt.npse.controllers;

import tnt.npse.entities.Settings;
import tnt.npse.controllers.util.JsfUtil;
import tnt.npse.controllers.util.JsfUtil.PersistAction;
import tnt.npse.beans.SettingsFacade;

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

@Named("settingsController")
@RequestScoped
public class SettingsController implements Serializable {

    @EJB
    private tnt.npse.beans.SettingsFacade ejbFacade;
    private List<Settings> items = null;
    private Settings selected;

    public SettingsController() {
    }

    public Settings getSelected() {
        return selected;
    }

    public void setSelected(Settings selected) {
        this.selected = selected;
    }

    private SettingsFacade getFacade() {
        return ejbFacade;
    }

    public Settings prepareCreate() {
        selected = new Settings();
        return selected;
    }
    
    public void create(String name) {
        FacesContext context=FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        items=getItems();


/*
        long count=items.stream().filter(st->st.getName().equalsIgnoreCase(name)).count();
        if (count==0) {
            Settings stat=new Settings();
            stat.setName(name);
            selected=stat;
            create();
            selected=items.stream().filter(e->e.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        } else {
            context.validationFailed();
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("EditSettingsExists"));
        }
*/
    }
    
    public void create(Settings settings) {
        selected=settings;
        create();
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("SettingsCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
            items=getItems();
        }
    }

    public void update(Settings selectedSett) {
        FacesContext context=FacesContext.getCurrentInstance();
        items=null;
        items=getItems();
        if (selectedSett!=null) {
/*            
            long count=items.stream().filter(s->s.getName().equalsIgnoreCase(selectedSett.getName())).count();
            if (count==0) {
                selected=selectedSett;
                persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("SettingsUpdated"));
            } else {
                context.validationFailed();
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("SettingsExists"));
            }
*/
        }
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
    
    
    //updates fields of settings which name is not changed (licenseSet)
    public void updateSettingsData(Settings selectedSett, boolean messages) {
        selected=selectedSett;
        if (messages)
            persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("SettingsUpdated"));
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
        items=getItems();
/*
        String oldName=selectedLic.getSettingsName();
        selected=items.stream().filter(s->s.getName().equalsIgnoreCase(oldName)).findFirst().orElse(null);
        if (selected!=null) {
            long count=items.stream().filter(s->s.getName().equalsIgnoreCase(newName)).count();
            if (count==0) {
                selected.setName(newName);
                persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("SettingsUpdated"));
                selectedLic.setSettingsName(newName);
            } else {
                context.validationFailed();
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("SettingsExists"));
            }
        }
*/
    }
    
    
    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("SettingsUpdated"));
    }

    public void destroy(Settings settings) {
        selected=settings;
        destroy();
    }
    
    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("SettingsDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Settings> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
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

    public Settings getSettings(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Settings> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Settings> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Settings.class)
    public static class SettingsControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SettingsController controller = (SettingsController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "statusController");
            return controller.getSettings(getKey(value));
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
            if (object instanceof Settings) {
                Settings o = (Settings) object;
                return getStringKey(o.getSettingId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Settings.class.getName()});
                return null;
            }
        }

    }

}
