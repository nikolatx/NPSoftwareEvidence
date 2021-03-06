package tnt.npse.controllers;

import tnt.npse.entities.Software;
import tnt.npse.controllers.util.JsfUtil;
import tnt.npse.controllers.util.JsfUtil.PersistAction;
import tnt.npse.beans.SoftwareFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import tnt.npse.entities.License;
import tnt.npse.model.LicenseData;

@Named("softwareController")
@RequestScoped
public class SoftwareController implements Serializable {

    @EJB
    private tnt.npse.beans.SoftwareFacade ejbFacade;
    
    private List<Software> items = null;
    private Software selected;
    private License license;
    
    
    public SoftwareController() {
    }

    @PostConstruct
    public void init() {
        getItems();
    }
    public Software getSelected() {
        return selected;
    }

    public void setSelected(Software selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private SoftwareFacade getFacade() {
        return ejbFacade;
    }

    public Software prepareCreate() {
        initializeEmbeddableKey();
        return selected;
    }

    //creates a new software with a given name
    public Software create(String name) {
        FacesContext context=FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        ec.getFlash().setKeepMessages(true);
        selected=null;
        items=getItems();
        
        if (name==null || name.isEmpty())
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("CreateSoftwareRequiredMessage_name"));
        else if (items.stream().anyMatch(sof->sof.getName().equalsIgnoreCase(name))) {
            context.validationFailed();
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("SoftwareExists"));
        }
        else {
            selected=new Software(name);
            persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("SoftwareCreated"));
            if (!JsfUtil.isValidationFailed()) {
                items = null;    // Invalidate list of items to trigger re-query.
                items=getItems();
            }
            selected=items.stream().filter(e->e.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
        }
        return selected;
    }
    
    /*
    public void create() {
        
        if (selected==null) {
            items=getItems();
            Software soft=items.stream().filter(e->e.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
            if (soft==null) {
                selected=new Software();
                selected.setName(name);
                persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("SoftwareCreated"));
                items=null;
                items=getItems();
                selected=items.stream().filter(e->e.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
                int a=1;
            } else {
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("SoftwareExists"));
                selected=null;
            }
        }
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        FacesContext context=FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        ec.getFlash().setKeepMessages(true);
    }
    */
    
    public void reload() {
        items=null;
        getItems();
    }
    
    public void update(Software selectedSoft) {
        FacesContext context=FacesContext.getCurrentInstance();
        items=getItems();
        if (selectedSoft!=null) {
            long count=items.stream().filter(s->s.getName().equalsIgnoreCase(selectedSoft.getName())).count();
            if (count==0) {
                selected=selectedSoft;
                persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("SoftwareUpdated"));
            } else {
                context.validationFailed();
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("SoftwareExists"));
            }
        }
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
    
    //updates fields of software which name is not changed (licenseSet)
    public void updateSoftwareData(Software selectedSoft, boolean messages) {
        selected=selectedSoft;
        if (messages)
            persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("SoftwareUpdated"));
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
        ec.getFlash().setKeepMessages(true);
        items=getItems();
        String oldName=selectedLic.getSoftwareName();
        selected=items.stream().filter(s->s.getName().equalsIgnoreCase(oldName)).findFirst().orElse(null);
        if (selected!=null) {
            long count=items.stream().filter(s->s.getName().equalsIgnoreCase(newName)).count();
            if (count==0) {
                selected.setName(newName);
                persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("SoftwareUpdated"));
                selectedLic.setSoftwareName(newName);
            } else {
                context.validationFailed();
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("SoftwareExists"));
            }
        }
    }
    
    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("SoftwareUpdated"));
    }

    public void destroy(Software software) {
        selected=software;
        destroy();
    }
    
    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("SoftwareDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Software> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    /*
    public void sellLicense(Software selSoftware, License selLicense, Customer selReseller, Customer selEndUser) {
        int a=1;
        a=2;
        selected=selSoftware;
        license=selLicense;
        FacesContext context=FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        ec.getFlash().setKeepMessages(true);
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("LicenseCreated"));
        items=null;
        items=getItems();
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
*/
    
    
    private void persist(PersistAction persistAction, String successMessage) {
        FacesContext context=FacesContext.getCurrentInstance();
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
                context.validationFailed();
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
                context.validationFailed();
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public Software getSoftware(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Software> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Software> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    

    @FacesConverter(forClass = Software.class)
    public static class SoftwareControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            SoftwareController controller = (SoftwareController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "softwareController");
            return controller.getSoftware(getKey(value));
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
            if (object instanceof Software) {
                Software o = (Software) object;
                return getStringKey(o.getSoftwareId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Software.class.getName()});
                return null;
            }
        }

    }

    /*
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }*/

    
    
}
