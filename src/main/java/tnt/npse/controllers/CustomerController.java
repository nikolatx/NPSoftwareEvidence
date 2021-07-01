package tnt.npse.controllers;

import tnt.npse.entities.Customer;
import tnt.npse.controllers.util.JsfUtil;
import tnt.npse.controllers.util.JsfUtil.PersistAction;
import tnt.npse.beans.CustomerFacade;

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
import tnt.npse.entities.Person;

@Named("customerController")
@SessionScoped
public class CustomerController implements Serializable {

    @EJB
    private tnt.npse.beans.CustomerFacade ejbFacade;
    private List<Customer> items = null;
    private Customer selected;

    
    public CustomerController() {
    }

    public Customer getSelected() {
        return selected;
    }

    public void setSelected(Customer selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private CustomerFacade getFacade() {
        return ejbFacade;
    }

    public Customer prepareCreate() {
        selected = new Customer();
        initializeEmbeddableKey();
        return selected;
    }

    
    //creates a new customer with one contact (person)
    public void create(Customer customer, Person contact) {
        FacesContext context=FacesContext.getCurrentInstance();
        items=getItems();
        Customer cust=items.stream().filter(cu->
                cu.getName().equalsIgnoreCase(customer.getName()) &&
                cu.getStreet().equalsIgnoreCase(customer.getStreet()) &&
                cu.getCity().equalsIgnoreCase(customer.getCity())).findFirst().orElse(null);
        if (cust==null) {
            contact.setCustomerId(customer);
            customer.getPersonSet().add(contact);
            selected=customer;
            persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("CustomerCreated"));
            
            if (!JsfUtil.isValidationFailed()) {
                items = null;    // Invalidate list of items to trigger re-query.
            }
        } else {
            context.validationFailed();
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("NewCompanyExists"));
        }
    }
    
    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("CustomerCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }
    
    public void refresh() {
        items=null;
        getItems();
    }
    
    public void update(Customer cust, boolean messages) {
        selected=cust;
        update(messages);
    }
    
    public void update(boolean messages) {
        if (messages)
            persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("CustomerUpdated"));
        else
            persist(PersistAction.UPDATE, "");
    }

    public void destroy(Customer customer) {
        FacesContext context=FacesContext.getCurrentInstance();
        if (customer.getLicenseCustomerSet().isEmpty()) {
            selected=customer;
            destroy();
        } else {
            context.validationFailed();
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("DeleteCompanyNotOrphan"));
        }
    }
    
    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("CustomerDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Customer> getItems() {
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

    public Customer getCustomer(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<Customer> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Customer> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Customer.class)
    public static class CustomerControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CustomerController controller = (CustomerController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "customerController");
            return controller.getCustomer(getKey(value));
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
            if (object instanceof Customer) {
                Customer o = (Customer) object;
                return getStringKey(o.getCustomerId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Customer.class.getName()});
                return null;
            }
        }

    }

}
