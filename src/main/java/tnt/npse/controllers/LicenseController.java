package tnt.npse.controllers;

import tnt.npse.entities.License;
import tnt.npse.controllers.util.JsfUtil;
import tnt.npse.controllers.util.JsfUtil.PersistAction;
import tnt.npse.beans.LicenseFacade;

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
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import tnt.npse.entities.Customer;
import tnt.npse.entities.Software;
import tnt.npse.entities.Status;

@Named("licenseController")
@RequestScoped
public class LicenseController implements Serializable {

    @EJB
    private tnt.npse.beans.LicenseFacade ejbFacade;
    private List<License> items = null;
    private License selected;

    @Inject
    private StatusController statusController;
    @Inject
    private SoftwareController softwareController;
    
    public LicenseController() {
    }

    @PostConstruct
    public void init() {
        getItems();
    }
    
    public void sellLicense(Software software, License license, Customer reseller, Customer endUser) {
        int a=1;
        a=2;
        selected=license;
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
    
    
    
    public License create(String licenseCode, String softwareName) {
        FacesContext context=FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        ec.getFlash().setKeepMessages(true);
        License lic=null;
        
        ///selected umesto lic!!!!!!
        
        
        if (licenseCode!=null && !licenseCode.isEmpty()) {
            lic=items.stream().filter(e->e.getLicenseCode().equalsIgnoreCase(licenseCode)&&e.getSoftwareId().getName().equalsIgnoreCase(softwareName)).findFirst().orElse(null);
            if (lic==null) {
                lic=new License();
                lic.setLicenseCode(licenseCode);
                lic.setSmaCode("");
                lic.setExpDate(null);
                lic.setLicenseCustomerSet(null);
                Status status=statusController.getItems().stream().filter(st->st.getName().equalsIgnoreCase("not activated")).findFirst().orElse(null);
                lic.setStatusId(status);

                Software soft=null;
                soft=softwareController.getItems().stream().filter(so->so.getName().equalsIgnoreCase(softwareName)).findFirst().orElse(null);
                lic.setSoftwareId(soft);
                selected=lic;
                persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("LicenseCreated"));
                items=null;
                items=getItems();
                lic=items.stream().filter(li->li.getLicenseCode().equalsIgnoreCase(licenseCode)).findFirst().orElse(null);
                selected=lic;
                int a=1;
            } else {
                JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("LicenseExists"));
                lic=null;
            }
        }
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
        return lic;
    }
    
    
    public License getSelected() {
        return selected;
    }

    public void setSelected(License selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private LicenseFacade getFacade() {
        return ejbFacade;
    }

    public License prepareCreate() {
        selected = new License();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("LicenseCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("LicenseUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("LicenseDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<License> getItems() {
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
                    getFacade().create(selected);
                } else if (persistAction != PersistAction.DELETE) {
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

    public License getLicense(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<License> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<License> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = License.class)
    public static class LicenseControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            LicenseController controller = (LicenseController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "licenseController");
            return controller.getLicense(getKey(value));
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
            if (object instanceof License) {
                License o = (License) object;
                return getStringKey(o.getLicenseId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), License.class.getName()});
                return null;
            }
        }

    }

}
