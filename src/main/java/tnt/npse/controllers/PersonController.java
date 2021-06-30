package tnt.npse.controllers;

import tnt.npse.entities.Person;
import tnt.npse.controllers.util.JsfUtil;
import tnt.npse.controllers.util.JsfUtil.PersistAction;
import tnt.npse.beans.PersonFacade;

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
import javax.inject.Inject;
import tnt.npse.entities.Customer;

@Named("personController")
@RequestScoped
public class PersonController implements Serializable {

    @EJB
    private tnt.npse.beans.PersonFacade ejbFacade;
    private List<Person> items = null;
    private Person selected;

    @Inject 
    private CustomerController customerController;
    @Inject
    private LicenseController licenseController;
    
    public PersonController() {
    }

    public Person getSelected() {
        return selected;
    }

    public void setSelected(Person selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private PersonFacade getFacade() {
        return ejbFacade;
    }

    public Person prepareCreate() {
        selected = new Person();
        initializeEmbeddableKey();
        return selected;
    }
    
    public void create(Person person, Customer company) {
        FacesContext context=FacesContext.getCurrentInstance();
        items=getItems();
        Person person1=person;
        Person perCheck=items.stream().filter(e-> (
                e.getFirstName().equalsIgnoreCase(person1.getFirstName()) &&
                e.getLastName().equalsIgnoreCase(person1.getLastName()) &&
                e.getCustomerId().getCustomerId().equals(company.getCustomerId())
                )).findFirst().orElse(null);
        
        if (perCheck==null) {
            person.setCustomerId(company);
            
            selected=person;
            persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("PersonCreated"));
            
            if (!JsfUtil.isValidationFailed()) {
                items = null;    // Invalidate list of items to trigger re-query.
            }
            getItems();
            
            person=items.stream().filter(e->(
                    e.getFirstName().equalsIgnoreCase(person1.getFirstName()) &&
                    e.getLastName().equalsIgnoreCase(person1.getLastName()) &&
                    e.getEmail().equalsIgnoreCase(person1.getEmail()) &&
                            e.getPhone().equalsIgnoreCase(person1.getPhone())
                    )).findFirst().orElse(null);
            
            company.getPersonSet().add(person);
            customerController.update(company);
        } else {
            context.validationFailed();
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("ContactExists"));
        }
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("PersonCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update(Person person, Customer company, Person originalPerson) {
        FacesContext context=FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        items=getItems();
        Person perCheck=items.stream().filter(e-> (
                e.getFirstName().equalsIgnoreCase(person.getFirstName()) &&
                e.getLastName().equalsIgnoreCase(person.getLastName()) &&
                e.getCustomerId().getCustomerId().equals(company.getCustomerId()) &&
                !e.getPersonId().equals(person.getPersonId())
                )).findFirst().orElse(null);
        
        if (perCheck==null) {
            selected=person;
            update();
            if (!JsfUtil.isValidationFailed()) {
                items = null;    // Invalidate list of items to trigger re-query.
            }
        } else {
            context.validationFailed();
            person.setFirstName(originalPerson.getFirstName());
            person.setLastName(originalPerson.getLastName());
            person.setPhone(originalPerson.getPhone());
            person.setEmail(originalPerson.getEmail());
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("ContactExists"));
        }
    }
    
    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("PersonUpdated"));
    }

    public void delete(Person person) {
        selected=person;
        destroy();
    }
    
    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("PersonDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Person> getItems() {
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
                    getFacade().refresh();
                } else {
                    getFacade().remove(selected);
                    getFacade().refresh();
                    items=null;
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

    public Person getPerson(java.lang.Integer id) {
        return getFacade().find(id);
    }

    public List<Person> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Person> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Person.class)
    public static class PersonControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PersonController controller = (PersonController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "personController");
            return controller.getPerson(getKey(value));
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
            if (object instanceof Person) {
                Person o = (Person) object;
                return getStringKey(o.getPersonId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Person.class.getName()});
                return null;
            }
        }

    }

}
