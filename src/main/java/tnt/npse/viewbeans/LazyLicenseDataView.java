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
import javax.transaction.Transactional;
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
        if (statusController.getItems()!=null) 
            statusController.getItems().stream().forEach(s->allStatuses.add(s.getName()));
        allSoftware=new ArrayList<>();
        if (softwareController.getItems()!=null) 
            softwareController.getItems().stream().forEach(s->allSoftware.add(s.getName()));
        customers=customerController.getItems();
        person=new Person();
    }
    
    public void onRowSelect(SelectEvent<LicenseData> event) {
        FacesMessage msg = new FacesMessage("License Selected", String.valueOf(event.getObject().getLicenseId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
        reseller=selectedLic.getReseller();
        endUser=selectedLic.getEndUser();
        originalLicense = (LicenseData) SerializationUtils.clone(selectedLic);
        person=new Person();
    }
    
    @Transactional
    public void changeLicenseData() {
        
        long id=originalLicense.getLicenseId();
        FacesContext context=FacesContext.getCurrentInstance();

        //VALIDATION
        //check if license code for choosen software already exists in database
        LicenseData ld=lazyModel.getWrappedData().stream().filter(el->(
                el.getLicenseCode().equalsIgnoreCase(selectedLic.getLicenseCode()) &&
                el.getSoftwareName().equalsIgnoreCase(selectedLic.getSoftwareName())
            )).findFirst().orElse(null);
        
        if (ld!=null && !Objects.equals(ld.getLicenseId(), selectedLic.getLicenseId())) {
            //LicenseData lic=lazyModel.getWrappedData().stream().filter(e->e.getLicenseId().equals(id)).findFirst().orElse(null);
            selectedLic=originalLicense;
            context.validationFailed();
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("LicenseExists"));
            return;
        }
        //if smaCode is given, but expDate is not set
        if (selectedLic.getSmaCode()!=null && !selectedLic.getSmaCode().isEmpty() && selectedLic.getExpDate()==null) {
            context.validationFailed();
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("EditLicenseTitle_dateRequired"));
            return;
        }
        
        //fetch license by licenseId from database
        License license=licenseController.getLicense(selectedLic.getLicenseId());
        
        
        //find licenseCustomer record for given license and reseller
        LicenseCustomer resellerLC=new LicenseCustomer();
        resellerLC=null;
        resellerLC = lcController.getItems().stream().filter(lc->(
                lc.getLicense().getLicenseId().equals(license.getLicenseId()) 
                    && lc.getEndUser()==false)).findFirst().orElse(null);
        
        //find licenseCustomer record for given license and end user
        LicenseCustomer endUserLC=lcController.getItems().stream().filter(e->(
                e.getLicense().getLicenseId().equals(license.getLicenseId())  
                    && e.getEndUser()==true)).findFirst().orElse(null);
       
        resellerLC.setCustomer(reseller);
        endUserLC.setCustomer(endUser);
        
        //delete records with license from oldReseller and oldEndUser objects
        Customer oldReseller=selectedLic.getReseller();
        if (oldReseller!=null)
            oldReseller.getLicenseCustomerSet().removeIf(e->e.getLicense().getLicenseId().equals(license.getLicenseId()));
        Customer oldEndUser=selectedLic.getEndUser();
        oldEndUser.getLicenseCustomerSet().removeIf(e->e.getLicense().equals(license));
        
        //add new licenseCustomer records to new reseller and endUser objects
        if (reseller!=null) 
            reseller.getLicenseCustomerSet().add(resellerLC);

        endUser.getLicenseCustomerSet().add(endUserLC);

        //clean lc set of license, and add new reseller and end user lc set
        license.getLicenseCustomerSet().removeAll(license.getLicenseCustomerSet());
        license.getLicenseCustomerSet().add(resellerLC);
        license.getLicenseCustomerSet().add(endUserLC);
        
        //update selectedLicense object with new reseller and endUser
        selectedLic.setReseller(reseller);
        selectedLic.setEndUser(endUser);
        
        //update database
        
        customerController.update(oldReseller, false);
        customerController.update(oldEndUser, false);
        customerController.update(reseller, false);
        customerController.update(endUser, false);

        lcController.update(resellerLC, false);  //ako se izbaci, dodaje novi zapis bez updatea postojeceg zapisa
        lcController.update(endUserLC, false); //4->null radi dobro kada je na kraju, a null->4 na pocetku
        
        
        //set other license data
        license.setLicenseCode(selectedLic.getLicenseCode());
        license.setSmaCode(selectedLic.getSmaCode());
        license.setExpDate(selectedLic.getExpDate());
        
        licenseController.update(license, false);
        
        //UPDATE OF SOFTWARE AND STATUS
        
        //remove license from licenseSet of old license status
        Status oldStat=license.getStatusId();
        oldStat.getLicenseSet().remove(license);
        
        //find new status
        Status newStat=statusController.getItems().stream().filter(st->
                st.getName().equalsIgnoreCase(selectedLic.getStatusName())).findFirst().orElse(null);
        
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
        Software newSoft=softwareController.getItems().stream().filter(so->
                so.getName().equalsIgnoreCase(selectedLic.getSoftwareName())).findFirst().orElse(null);
        //assign new software to license
        license.setSoftware(newSoft);
        //add license to new software's licenseSet
        newSoft.getLicenseSet().add(license);
        
        //update old and new software
        softwareController.updateSoftwareData(oldSoft, false);
        softwareController.updateSoftwareData(newSoft, false);
        
        licenseController.update(license, true);
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
        if (reseller!=null) {
            long id=reseller.getCustomerId();
            FacesContext context=FacesContext.getCurrentInstance();

            Customer custCheck=customers.stream().filter(e->(
                    e.getName().equalsIgnoreCase(reseller.getName()) && 
                    e.getStreet().equalsIgnoreCase(reseller.getStreet()) &&
                    e.getCity().equalsIgnoreCase(reseller.getCity()) && 
                    !e.getCustomerId().equals(reseller.getCustomerId())
                    )).findFirst().orElse(null);
            if (custCheck==null)
                customerController.update(reseller, true);
            else {
                Customer original=customers.stream().filter(e->e.getCustomerId().equals(id)).findFirst().orElse(null);
                reseller.setName(original.getName());
                reseller.setStreet(original.getStreet());
                reseller.setNumber(original.getNumber());
                reseller.setCity(original.getCity());
                reseller.setCountry(original.getCountry());
                context.validationFailed();
                JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("CustomerExists"));
            }
        }
    }
    
    //called from pencil icon below Company data panelgrid
    public void editEndUser() {
        long id=endUser.getCustomerId();
        FacesContext context=FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        
        Customer custCheck=customers.stream().filter(e->(
                e.getName().equalsIgnoreCase(endUser.getName()) && 
                e.getStreet().equalsIgnoreCase(endUser.getStreet()) &&
                e.getCity().equalsIgnoreCase(endUser.getCity()) && 
                !e.getCustomerId().equals(endUser.getCustomerId())
                )).findFirst().orElse(null);
        if (custCheck==null)
            customerController.update(endUser, true);
        else {
            Customer original=customers.stream().filter(e->e.getCustomerId().equals(id)).findFirst().orElse(null);
            endUser.setName(original.getName());
            endUser.setStreet(original.getStreet());
            endUser.setNumber(original.getNumber());
            endUser.setCity(original.getCity());
            endUser.setCountry(original.getCountry());
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
