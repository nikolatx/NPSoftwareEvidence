package tnt.npse.controllers;

import tnt.npse.entities.License;
import tnt.npse.controllers.util.JsfUtil;
import tnt.npse.controllers.util.JsfUtil.PersistAction;
import tnt.npse.beans.LicenseFacade;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
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
import javax.inject.Inject;
import tnt.npse.entities.Customer;
import tnt.npse.entities.LicenseCustomer;
import tnt.npse.entities.Software;
import tnt.npse.entities.Status;
import tnt.npse.model.LicenseData;

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
        FacesContext context=FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        ec.getFlash().setKeepMessages(true);

        if (!getItems().stream().anyMatch(lic->
                (lic.getLicenseCode().equalsIgnoreCase(license.getLicenseCode())
                && lic.getSoftware().equals(license.getSoftware())))) {
            if (endUser!=null) {
                if (license.getSmaCode()!=null && !license.getSmaCode().isEmpty() && (license.getExpDate()==null || license.getExpDate().before(new Date()))) {
                    context.validationFailed();
                    JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("SellLicenseRequiredMessage_expDate"));
                } else {
                    selected=license;
                    //persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("LicenseCreated"));
                    softwareController.updateSoftwareData(software, false);
                    items=null;
                    items=getItems();
                    if (!JsfUtil.isValidationFailed()) {
                        JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle").getString("LicenseCreated"));
                        items = null;    // Invalidate list of items to trigger re-query.
                        softwareController.reload();
                    }
                }
            } else {
                context.validationFailed();
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("SellLicenseRequiredMessage_endUser"));
            }
        } else {
            context.validationFailed();
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("LicenseExists"));
        }
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

    public void update(LicenseData ld) {
        FacesContext context=FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        //ec.getFlash().setKeepMessages(true);
        
        License license = new License();
        items=getItems();
        license=items.stream().filter(lc->lc.getLicenseId().equals(ld.getLicenseId())).findFirst().orElse(null);
        license.setLicenseCode(ld.getLicenseCode());//
        license.setSmaCode(ld.getSmaCode());//
        license.setExpDate(ld.getExpDate());//
        
        //remove license from licenseSet of old license status
        Status oldStat=license.getStatusId();
        oldStat.getLicenseSet().remove(license);
        
        //find new status
        Status newStat=statusController.getItems().stream().filter(st->st.getName().equalsIgnoreCase(ld.getStatusName())).findFirst().orElse(null);
        
        //assign new status to the license
        license.setStatusId(newStat);
        //add license to the new status licenseSet
        newStat.getLicenseSet().add(license);
        
        //update old status licenseSet
        statusController.updateStatusData(oldStat, false);
        //update new status licenseSet
        statusController.updateStatusData(newStat, false);
        
        //remove license from old software's licenseSet
        Software oldSoft=license.getSoftware();
        oldSoft.getLicenseSet().remove(license);
        //find new software
        Software newSoft=softwareController.getItems().stream().filter(so->so.getName().equalsIgnoreCase(ld.getSoftwareName())).findFirst().orElse(null);
        //assign new software to license
        license.setSoftware(newSoft);
        //add license to new software's licenseSet
        newSoft.getLicenseSet().add(license);
        
        //update old and new software
        softwareController.updateSoftwareData(oldSoft, false);
        softwareController.updateSoftwareData(newSoft, false);
        
        Set<LicenseCustomer> lcSet=new HashSet<>();
        
        LicenseCustomer lc1=null;
        if (ld.getReseller()!=null) {
            lc1=new LicenseCustomer();
            lc1.setCustomer(ld.getReseller());
            lc1.setEndUser(false);
            lc1.setLicense(license);
            lcSet.add(lc1);
        }
        
        LicenseCustomer lc2=new LicenseCustomer();
        lc2.setCustomer(ld.getEndUser());
        lc2.setEndUser(true);
        lc2.setLicense(license);
        lcSet.add(lc2);
        
        selected=license;
        update(true);
        
    }
    
    public void update(License license, boolean messages) {
        selected=license;
        update(messages);
    }
    
    public void update(boolean messages) {
        if (messages)
            persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("LicenseUpdated"));
        else
            persist(PersistAction.UPDATE, "");
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
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

    public License getLicense(java.lang.Long id) {
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
