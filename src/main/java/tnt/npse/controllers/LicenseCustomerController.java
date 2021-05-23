package tnt.npse.controllers;

import tnt.npse.entities.LicenseCustomer;
import tnt.npse.controllers.util.JsfUtil;
import tnt.npse.controllers.util.JsfUtil.PersistAction;
import tnt.npse.beans.LicenseCustomerFacade;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;

@Named("licenseCustomerController")
@SessionScoped
public class LicenseCustomerController implements Serializable {

    @EJB
    private tnt.npse.beans.LicenseCustomerFacade ejbFacade;
    private List<LicenseCustomer> items = null;
    private LicenseCustomer selected;

    public LicenseCustomerController() {
    }

    public LicenseCustomer getSelected() {
        return selected;
    }

    public void setSelected(LicenseCustomer selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
        selected.getLicenseCustomerPK().setLicenseId(selected.getLicense().getLicenseId());
        selected.getLicenseCustomerPK().setCustomerId(selected.getCustomer().getCustomerId());
    }

    protected void initializeEmbeddableKey() {
        selected.setLicenseCustomerPK(new tnt.npse.entities.LicenseCustomerPK());
    }

    private LicenseCustomerFacade getFacade() {
        return ejbFacade;
    }

    public LicenseCustomer prepareCreate() {
        selected = new LicenseCustomer();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("LicenseCustomerCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("LicenseCustomerUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("LicenseCustomerDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<LicenseCustomer> getItems() {
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

    public LicenseCustomer getLicenseCustomer(tnt.npse.entities.LicenseCustomerPK id) {
        return getFacade().find(id);
    }

    public List<LicenseCustomer> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<LicenseCustomer> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = LicenseCustomer.class)
    public static class LicenseCustomerControllerConverter implements Converter {

        private static final String SEPARATOR = "#";
        private static final String SEPARATOR_ESCAPED = "\\#";

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            LicenseCustomerController controller = (LicenseCustomerController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "licenseCustomerController");
            return controller.getLicenseCustomer(getKey(value));
        }

        tnt.npse.entities.LicenseCustomerPK getKey(String value) {
            tnt.npse.entities.LicenseCustomerPK key;
            String values[] = value.split(SEPARATOR_ESCAPED);
            key = new tnt.npse.entities.LicenseCustomerPK();
            key.setLicenseId(Integer.parseInt(values[0]));
            key.setCustomerId(Integer.parseInt(values[1]));
            return key;
        }

        String getStringKey(tnt.npse.entities.LicenseCustomerPK value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value.getLicenseId());
            sb.append(SEPARATOR);
            sb.append(value.getCustomerId());
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof LicenseCustomer) {
                LicenseCustomer o = (LicenseCustomer) object;
                return getStringKey(o.getLicenseCustomerPK());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), LicenseCustomer.class.getName()});
                return null;
            }
        }

    }

}
