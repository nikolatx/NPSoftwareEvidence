/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.viewbeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.SerializationUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import tnt.npse.controllers.CustomerController;
import tnt.npse.controllers.LicenseController;
import tnt.npse.controllers.LicenseCustomerController;
import tnt.npse.controllers.PersonController;
import tnt.npse.controllers.SoftwareController;
import tnt.npse.controllers.StatusController;
import tnt.npse.controllers.util.JsfUtil;
import tnt.npse.entities.Customer;
import tnt.npse.model.LazyLicenseDataModel;
import tnt.npse.entities.License;
import tnt.npse.entities.LicenseCustomer;
import tnt.npse.entities.Person;
import tnt.npse.entities.Software;
import tnt.npse.entities.Status;
import tnt.npse.model.LicenseData;

/**
 *
 * @author NN
 */
@Named("lazyLicenseDataView")
@ViewScoped
public class LazyLicenseDataView implements Serializable {
    
    private LazyDataModel<LicenseData> lazyModel;
    private LicenseData selectedLic;
    private Customer reseller;
    private Customer endUser;
    private List<String> allStatuses=new ArrayList<>();
    private List<String> allSoftware=new ArrayList<>();
    private String softwareName;
    private String oldLicenseCode;
    private String statusName;
    private Person person;
    private List<Customer> customers;
    private LicenseData originalLicense;
    private Customer newCustomer;
    private Person newContact;
    private String updateRes="";
    private Person originalPerson;
    
    @Inject
    private LicenseController licenseController;
    @Inject
    private StatusController statusController;
    @Inject
    private SoftwareController softwareController;
    @Inject
    private CustomerController customerController;
    @Inject 
    private PersonController personController;
    @Inject 
    private LicenseCustomerController lcController;
    
    @PostConstruct
    public void init() {
        List<License> lics=licenseController.getItems();
        lazyModel = new LazyLicenseDataModel(lics);
        allStatuses=new ArrayList<>();
        statusController.getItems().stream().forEach(s->allStatuses.add(s.getName()));
        allSoftware=new ArrayList<>();
        softwareController.getItems().stream().forEach(s->allSoftware.add(s.getName()));
        customers=customerController.getItems();
        person=new Person();
        lazyModel.setRowIndex(0);
    }
    
    public void onRowSelect(SelectEvent<LicenseData> event) {
        FacesMessage msg = new FacesMessage("License Selected", String.valueOf(event.getObject().getLicenseId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
        reseller=selectedLic.getReseller();
        endUser=selectedLic.getEndUser();
        originalLicense = (LicenseData) SerializationUtils.clone(selectedLic);
        person=new Person();
    }
    
    
    public void changeLicenseData() {
        int id=originalLicense.getLicenseId();
        FacesContext context=FacesContext.getCurrentInstance();
        //ExternalContext ec = context.getExternalContext();

        LicenseData ld=lazyModel.getWrappedData().stream().filter(el->(
                el.getLicenseCode().equalsIgnoreCase(selectedLic.getLicenseCode()) &&
                el.getSoftwareName().equalsIgnoreCase(selectedLic.getSoftwareName())
            )).findFirst().orElse(null);
        
        if (ld!=null && !Objects.equals(ld.getLicenseId(), selectedLic.getLicenseId())) {
            LicenseData lic=lazyModel.getWrappedData().stream().filter(e->e.getLicenseId().equals(id)).findFirst().orElse(null);
            selectedLic=originalLicense; //.setLicenseCode(lic.getLicenseCode());
            context.validationFailed();
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("LicenseExists"));
            return;
        }
        if (selectedLic.getSmaCode()!=null && selectedLic.getExpDate()==null) {
            context.validationFailed();
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("EditLicenseTitle_dateRequired"));
            return;
        }
        //fetch oriinal license
        License license=licenseController.getLicense(selectedLic.getLicenseId());
        
        //find licenseCustomer record for given license and reseller
        LicenseCustomer resellerLC=lcController.getItems().stream().filter(e->(
                e.getLicense().equals(license) && e.getCustomer().equals(selectedLic.getReseller())
                )).findFirst().orElse(null);
        //find licenseCustomer record for given license and end user
        LicenseCustomer endUserLC=lcController.getItems().stream().filter(e->(
                e.getLicense().equals(license) && e.getCustomer().equals(selectedLic.getEndUser())
                )).findFirst().orElse(null);
        //if reseller is not selected set appropriate fields
        if (resellerLC==null) {
            resellerLC=new LicenseCustomer();
            resellerLC.setEndUser(false);
            resellerLC.setLicense(license);
        }
        //update licensecustomer record with new reseller and endUser
        resellerLC.setCustomer(reseller);
        endUserLC.setCustomer(endUser);
        
        
        //remove old reseller and endUser from orm
        selectedLic.getReseller().getLicenseCustomerSet().removeAll(selectedLic.getReseller().getLicenseCustomerSet());
        selectedLic.getEndUser().getLicenseCustomerSet().removeAll(selectedLic.getEndUser().getLicenseCustomerSet());
        customerController.update(selectedLic.getReseller());
        customerController.update(selectedLic.getEndUser());
        
        //delete all licensecustomer recoreds for reseller, fot this license
        reseller.getLicenseCustomerSet().removeIf(e->e.getLicense().equals(license));
        //add new record to reseller's lc set
        reseller.getLicenseCustomerSet().add(resellerLC);
        //do the same for end user
        endUser.getLicenseCustomerSet().removeIf(e->e.getLicense().equals(license));
        endUser.getLicenseCustomerSet().add(endUserLC);
        
        //clean lc set of license, and add reseller and end user lc set
        license.getLicenseCustomerSet().removeAll(license.getLicenseCustomerSet());
        license.getLicenseCustomerSet().add(resellerLC);
        license.getLicenseCustomerSet().add(endUserLC);
        
        
        
        
        selectedLic.setReseller(reseller);
        selectedLic.setEndUser(endUser);
       
        
        customerController.update(reseller);
        customerController.update(endUser);
        lcController.update(resellerLC);
        lcController.update(endUserLC);
        
        
        license.setLicenseCode(selectedLic.getLicenseCode());
        license.setSmaCode(selectedLic.getSmaCode());
        license.setExpDate(selectedLic.getExpDate());
        
        //remove license from licenseSet of old license status
        Status oldStat=license.getStatusId();
        oldStat.getLicenseSet().remove(license);
        
        //find new status
        Status newStat=statusController.getItems().stream().filter(st->st.getName().equalsIgnoreCase(selectedLic.getStatusName())).findFirst().orElse(null);
        
        //assign new status to the license
        license.setStatusId(newStat);
        //add license to the new status licenseSet
        newStat.getLicenseSet().add(license);
        
        //update old status licenseSet
        statusController.updateStatusData(oldStat);
        //update new status licenseSet
        statusController.updateStatusData(newStat);
        
        //remove license from old software's licenseSet
        Software oldSoft=license.getSoftware();
        oldSoft.getLicenseSet().remove(license);
        //find new software
        Software newSoft=softwareController.getItems().stream().filter(so->so.getName().equalsIgnoreCase(selectedLic.getSoftwareName())).findFirst().orElse(null);
        //assign new software to license
        license.setSoftware(newSoft);
        //add license to new software's licenseSet
        newSoft.getLicenseSet().add(license);
        
        //update old and new software
        softwareController.updateSoftwareData(oldSoft);
        softwareController.updateSoftwareData(newSoft);
        
        licenseController.update(license);
        customerController.refresh();
    }
    
    //creates new software - clicking on + icon in edit license dialog
    public void createSoftware() {
        softwareController.create(softwareName);
        allSoftware=softwareController.getItems().stream().map(e->e.getName()).collect(Collectors.toList());
    }
    
    //renames software - clicking on pencil icon in edit license dialog
    public void renameSoftware() {
        softwareController.update(selectedLic, softwareName);
        allSoftware=softwareController.getItems().stream().map(e->e.getName()).collect(Collectors.toList());
    }
    
    //selects software name to display in edit software name dialog
    public void selectSoftware() {
        softwareName=selectedLic.getSoftwareName();
    }
    
    //creates new status - plus icon in edit license dialog
    public void createStatus() {
        statusController.create(statusName);
        allStatuses=statusController.getItems().stream().map(st->st.getName()).collect(Collectors.toList());
    }
    
    //selects status name to display in edit status name dialog
    public void selectStatus() {
        statusName=selectedLic.getStatusName();
    }
    
    //renames status - clicking on pencil icon in edit license dialog
    public void renameStatus() {
        statusController.update(selectedLic, statusName);
        allStatuses=statusController.getItems().stream().map(e->e.getName()).collect(Collectors.toList());
    }
    
    //called from pencil icon below Company data panelgrid
    public void editReseller() {
        int id=reseller.getCustomerId();
        FacesContext context=FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        
        Customer custCheck=customers.stream().filter(e->(
                e.getName().equalsIgnoreCase(reseller.getName()) && 
                e.getStreet().equalsIgnoreCase(reseller.getStreet()) &&
                e.getCity().equalsIgnoreCase(reseller.getCity()) && 
                !e.getCustomerId().equals(reseller.getCustomerId())
                )).findFirst().orElse(null);
        if (custCheck==null)
            customerController.update(reseller);
        else {
            Customer original=customers.stream().filter(e->e.getCustomerId().equals(id)).findFirst().orElse(null);
            reseller.setName(original.getName());
            reseller.setStreet(original.getStreet());
            reseller.setNumber(original.getNumber());
            reseller.setCity(original.getCity());
            reseller.setCountry(original.getCountry());
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("CustomerExists"));
        }
    }
    
    //called from pencil icon below Company data panelgrid
    public void editEndUser() {
        int id=reseller.getCustomerId();
        FacesContext context=FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        
        Customer custCheck=customers.stream().filter(e->(
                e.getName().equalsIgnoreCase(reseller.getName()) && 
                e.getStreet().equalsIgnoreCase(reseller.getStreet()) &&
                e.getCity().equalsIgnoreCase(reseller.getCity()) && 
                !e.getCustomerId().equals(reseller.getCustomerId())
                )).findFirst().orElse(null);
        if (custCheck==null)
            customerController.update(reseller);
        else {
            Customer original=customers.stream().filter(e->e.getCustomerId().equals(id)).findFirst().orElse(null);
            reseller.setName(original.getName());
            reseller.setStreet(original.getStreet());
            reseller.setNumber(original.getNumber());
            reseller.setCity(original.getCity());
            reseller.setCountry(original.getCountry());
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("CustomerExists"));
        }
    }
    
    //when edit license button is clicked - newCustomer and person should be initialized
    public void onEditLicense(LicenseData ld) {
        //reseller=selectedLic.getReseller();
        //endUser=selectedLic.getEndUser();
        selectedLic=ld;
        originalLicense = (LicenseData) SerializationUtils.clone(selectedLic);
        newCustomer=new Customer();
        person=new Person();
    }
    
    //adds new company
    public void addNewCustomer() {
        customerController.create(newCustomer, person);
        customers=customerController.getItems();
    }
    
    public void prepareContact() {
        person=new Person();
        updateRes="";
    }
    
    public void addResellerContact() {
        personController.create(person, selectedLic.getReseller());
    }
    
    public void editResellerContact() {
        personController.update(person, selectedLic.getReseller(), originalPerson);
    }
    
    public void deleteResellerContact() {
        personController.delete(person);
        selectedLic.getReseller().setPersonSet(
                selectedLic.getReseller().getPersonSet().stream().filter(e->!e.getPersonId().equals(person.getPersonId())).collect(Collectors.toSet())
        );
    }
    
    public void addEndUserContact() {
        personController.create(person, selectedLic.getEndUser());
    }
    
    public void editEndUserContact() {
        personController.update(person, selectedLic.getEndUser(), originalPerson);
    }
    
    public void deleteEndUserContact() {
        personController.delete(person);
        selectedLic.getEndUser().setPersonSet(
                selectedLic.getEndUser().getPersonSet().stream().filter(e->!e.getPersonId().equals(person.getPersonId())).collect(Collectors.toSet())
        );
    }
    
    public LazyDataModel<LicenseData> getLazyModel() {
        return lazyModel;
    }

    public LicenseData getSelectedLic() {
        return selectedLic;
    }

    public void setSelectedLic(LicenseData selectedLic) {
        this.selectedLic = selectedLic;
        if (selectedLic!=null) {
            reseller=selectedLic.getReseller();
            endUser=selectedLic.getEndUser();
        }
    }

    public Customer getReseller() {
        return reseller;
    }

    public void setReseller(Customer reseller) {
        this.reseller = reseller;
    }

    public Customer getEndUser() {
        return endUser;
    }

    public void setEndUser(Customer endUser) {
        this.endUser = endUser;
    }

    public List<String> getAllStatuses() {
        return allStatuses;
    }

    public void setAllStatuses(List<String> allStatuses) {
        this.allStatuses = allStatuses;
    }

    public List<String> getAllSoftware() {
        return allSoftware;
    }

    public void setAllSoftware(List<String> allSoftware) {
        this.allSoftware = allSoftware;
    }

    public String getSoftwareName() {
        return softwareName;
    }

    public void setSoftwareName(String softwareName) {
        this.softwareName = softwareName;
    }

    public String getOldLicenseCode() {
        return oldLicenseCode;
    }

    public void setOldLicenseCode(String oldLicenseCode) {
        this.oldLicenseCode = oldLicenseCode;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
        originalPerson=(Person) SerializationUtils.clone(person);
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public Customer getNewCustomer() {
        return newCustomer;
    }

    public void setNewCustomer(Customer newCustomer) {
        this.newCustomer = newCustomer;
    }

    public Person getNewContact() {
        return newContact;
    }

    public void setNewContact(Person newContact) {
        this.newContact = newContact;
    }

    public String getUpdateRes() {
        return updateRes;
    }

    public void setUpdateRes(String updateRes) {
        this.updateRes = updateRes;
    }
    
}
