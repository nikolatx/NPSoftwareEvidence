/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.viewbeans;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.SerializationUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import tnt.npse.controllers.CustomerController;
import tnt.npse.controllers.PersonController;
import tnt.npse.controllers.util.JsfUtil;
import tnt.npse.entities.Customer;
import tnt.npse.entities.Person;
import tnt.npse.model.LazyCompanyDataModel;

/**
 *
 * @author NN
 */
@Named("lazyCompanyDataView")
@ViewScoped
public class LazyCompanyDataView implements Serializable {
    
    private LazyDataModel<Customer> lazyModel;
    private Customer selectedComp;
    private Customer newCompany;
    private Customer originalCompany;
    private Person selectedPers;
    private Person newPers;
    private Person originalPers;
    private boolean rowSelected;

    public boolean isRowSelected() {
        return rowSelected;
    }

    public void setRowSelected(boolean rowSelected) {
        this.rowSelected = rowSelected;
    }
    
    @Inject
    private CustomerController customerController;
    @Inject 
    private PersonController personController;
    
    @PostConstruct
    public void init() {
        selectedComp=new Customer();
        
        newCompany=new Customer();
        rowSelected=false;
        lazyModel = new LazyCompanyDataModel(customerController.getItems());
        //lazyModel.setRowIndex(0);
        
    }
    
    public void onRowSelect(SelectEvent<Customer> event) {
        FacesMessage msg = new FacesMessage("License Selected", String.valueOf(event.getObject().getCustomerId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
        originalCompany = (Customer) SerializationUtils.clone(selectedComp);
        selectedPers=new Person();
        rowSelected=true;
    }
    
    public void prepareCompany() {
        newCompany=new Customer();
        newPers=new Person();
    }
    
    //adds new company
    public void addNewCompany() {
        customerController.create(newCompany, newPers);
        lazyModel=new LazyCompanyDataModel(customerController.getItems());
    }
    
    public void editCompany() {
        FacesContext context=FacesContext.getCurrentInstance();
        Customer custCheck=lazyModel.getWrappedData().stream().filter(e->(
                e.getName().equalsIgnoreCase(selectedComp.getName()) && 
                e.getStreet().equalsIgnoreCase(selectedComp.getStreet()) &&
                e.getCity().equalsIgnoreCase(selectedComp.getCity()) && 
                !e.getCustomerId().equals(selectedComp.getCustomerId())
                )).findFirst().orElse(null);
        if (custCheck==null)
            customerController.update(selectedComp, true);
        else {
            selectedComp.setName(originalCompany.getName());
            selectedComp.setStreet(originalCompany.getStreet());
            selectedComp.setNumber(originalCompany.getNumber());
            selectedComp.setCity(originalCompany.getCity());
            selectedComp.setCountry(originalCompany.getCountry());
            context.validationFailed();
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("CustomerExists"));
        }
    }
    
    public void deleteCompany() {
        customerController.destroy(selectedComp);
        if (!JsfUtil.isValidationFailed()) {
            lazyModel.getWrappedData().remove(selectedComp);
            List<Customer> custs=lazyModel.getWrappedData();
            lazyModel=new LazyCompanyDataModel(custs);
            selectedComp=null;
        }
    }
    
    
    
    
    public void prepareContact() {
        selectedPers=new Person();
    }
    
    public void addContact() {
        personController.create(selectedPers, selectedComp);
    }
    
    public void editContact() {
        personController.update(selectedPers, selectedComp, originalPers);
    }
    
    public void deleteContact() {
        personController.delete(selectedPers);
        if (!JsfUtil.isValidationFailed()) {
            selectedComp.setPersonSet(
                    selectedComp.getPersonSet().stream()
                    .filter(e->!e.getPersonId().equals(selectedPers.getPersonId())).collect(Collectors.toSet()));
            selectedPers=null;
        }
    }
    
    
    


    public LazyDataModel<Customer> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Customer> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public Customer getSelectedComp() {
        return selectedComp;
    }

    public void setSelectedComp(Customer selectedComp) {
        this.selectedComp = selectedComp;
        originalCompany = (Customer) SerializationUtils.clone(selectedComp);
    }

    public Customer getNewCompany() {
        return newCompany;
    }

    public void setNewCompany(Customer newCompany) {
        this.newCompany = newCompany;
    }

    public Customer getOriginalCompany() {
        return originalCompany;
    }

    public void setOriginalCompany(Customer originalCompany) {
        this.originalCompany = originalCompany;
    }
   
    public Person getSelectedPers() {
        return selectedPers;
    }

    public void setSelectedPers(Person selectedPers) {
        this.selectedPers = selectedPers;
        originalPers=(Person) SerializationUtils.clone(selectedPers);
    }

    public Person getNewPers() {
        return newPers;
    }

    public void setNewPers(Person newPers) {
        this.newPers = newPers;
    }

    public Person getOriginalPers() {
        return originalPers;
    }

    public void setOriginalPers(Person originalPers) {
        this.originalPers = originalPers;
    }
    
    
}
