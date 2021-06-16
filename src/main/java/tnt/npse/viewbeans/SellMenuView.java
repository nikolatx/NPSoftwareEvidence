/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.viewbeans;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.SerializationUtils;
import tnt.npse.controllers.CustomerController;
import tnt.npse.controllers.LicenseController;
import tnt.npse.controllers.LicenseCustomerController;
import tnt.npse.controllers.PersonController;
import tnt.npse.controllers.SoftwareController;
import tnt.npse.controllers.StatusController;
import tnt.npse.controllers.util.JsfUtil;
import tnt.npse.entities.Customer;
import tnt.npse.entities.License;
import tnt.npse.entities.LicenseCustomer;
import tnt.npse.entities.Person;
import tnt.npse.entities.Software;
import tnt.npse.entities.Status;



/**
 *
 * @author NN
 */
@Named
@ViewScoped
public class SellMenuView implements Serializable {
    
    private List<Software> allsoftware = new ArrayList<>();
    private Software selSoftware;
    
    //private List<Customer> resellers = new ArrayList<>();
    private Customer selReseller;
    
    private List<Customer> customers = new ArrayList<>();
    
    //private List<Customer> endUsers = new ArrayList<>();
    private Customer selEndUser;
    
    private List<License> licenses;
    private License selLicense;
    
    private String smaCode;
    private List<Person> contacts = new ArrayList<>();
    private Person selContact;
    
    private List<Person> econtacts = new ArrayList<>();
    private Person selEContact;
    
    private License emptyLicense=new License();
    
    private String softwareName;
    /*private String licenseCode;*/
    private Date expDate;

    private boolean licInputDisabled;
    private boolean smaButtonDisabled;
    private boolean expDateButtonDisabled;
    
    private Customer newCompany;
    private Person newContact;
    
    @Inject
    private SoftwareController softwareController;
    @Inject
    private CustomerController customerController;
    @Inject
    private LicenseController licenseController;
    @Inject
    private LicenseCustomerController licenseCustomerController;
    @Inject
    private PersonController personController;
    @Inject
    private StatusController statusController;
    
    
    
    @PostConstruct
    public void init() {
        allsoftware=softwareController.getItems();
        licenses=licenseController.getItems();
        
        //customerController.getItems().stream().forEach(e->resellers.add(e));
        //customerController.getItems().stream().forEach(e->endUsers.add(e));
        customerController.getItems().stream().forEach(e->customers.add(e));
        //endUsers=customerController.getItems(); 
        
        //resellers.forEach(res->contacts.addAll(res.getPersonSet()));
        //endUsers.forEach(endU->econtacts.addAll(endU.getPersonSet()));
        
        customers.forEach(endU->contacts.addAll(endU.getPersonSet()));
        customers.forEach(endU->econtacts.addAll(endU.getPersonSet()));
        newCompany=new Customer();
        newContact=new Person();
        selLicense=new License();
        selSoftware=new Software();
        licInputDisabled=true;
        smaButtonDisabled=true;
        expDateButtonDisabled=true;
        
        Calendar cal=Calendar.getInstance();
        
        cal.add(Calendar.YEAR, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        expDate=cal.getTime();
    }
    
    
    
    
    
    
    
    private void resetAllData() {
        /*
        selLicense=null;
        selReseller=null;
        selEndUser=null;
        selContact=null;
        selEContact=null;
        contacts=new ArrayList<>();
        econtacts=new ArrayList<>();
        resellers=new ArrayList<>();
        endUsers=new ArrayList<>();
        licenses=null;
        licenses=new ArrayList<>();
        allsoftware=new ArrayList<>();
        smaCode="";
        init();
        */
    }
    
        
    
    
    public void resellerSelected() {
        //if reset option in dropdown is selected
        if (selReseller==null) {
            resetAllData();
            return;
        }
        //fill contact persons for selected reseller
        contacts=new ArrayList<>();
        selReseller.getPersonSet().stream().forEach(contacts::add);
        //select first contact from reseller list
        selContact=contacts.stream().findFirst().orElse(null);
    }
    
    public void contactSelected() {
        //if reset option in dropdown is selected
        if (selContact==null) {
            resetAllData();
            return;
        }
        //select reseller data based on selected person
        selReseller=(Customer)selContact.getCustomerId();
        //fill a list of reseller contact persons
        contacts=new ArrayList<>();
        selReseller.getPersonSet().stream().forEach(pers->contacts.add(pers));
    }
    
    
    //ako se odabere Select ne uzlazi ovde zbog required!!! jer radi validaciju pre submita
    
    
    public void endUserSelected() {
        //if reset option in dropdown is selected
        if (selEndUser==null) {
            resetAllData();
            return;
        }
        //fill end user contact list
        econtacts=null;
        selEContact=null;
        //econtacts=new ArrayList<>();
        econtacts=new ArrayList<>();
        
        selEndUser.getPersonSet().stream().forEach(pers->econtacts.add(pers));
        //set first contact from end user list
        selEContact=(Person)selEndUser.getPersonSet().stream().findFirst().orElse(null);
    }
    
    public void eContactSelected() {
        //if reset option in dropdown is selected
        if (selEContact==null) {
            resetAllData();
            return;
        }
        selEndUser=null;
        //econtacts=null;
        contacts=null;
        //fill list of contact persons
        //econtacts=new ArrayList<>();
        contacts=new ArrayList<>();
        //selEndUser.getPersonSet().stream().forEach(pers->econtacts.add(pers));
        selEndUser.getPersonSet().stream().forEach(pers->contacts.add(pers));
        //select end user
        selEndUser=(Customer)selEContact.getCustomerId();
    }
    
    
    
    public void createSoftware() {
        FacesContext context=FacesContext.getCurrentInstance();
        ExternalContext ec = context.getExternalContext();
        ec.getFlash().setKeepMessages(true);
        if (softwareName==null || softwareName.isEmpty())
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("CreateSoftwareRequiredMessage_name"));
        else if (allsoftware.stream().anyMatch(sof->sof.getName().equalsIgnoreCase(softwareName))) {
            context.validationFailed();
            JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle").getString("SoftwareExists"));
        }
        else {
            allsoftware.add(new Software(softwareName));
            selSoftware=allsoftware.stream().filter(e->e.getName().equalsIgnoreCase(softwareName)).findFirst().orElse(null);
        }
    }
    
    
    
    public void softwareSelected() {
        
       
    }
    
    public void createLicense() {
        
        
    }
    
    public void createSMA() {
        if (smaCode!=null && !smaCode.isEmpty()) {
            //smaDisabled=true;
            Calendar cal=Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.YEAR, 1);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            //expDate=cal.getTime();
        }
    }
    
    public void extendSMA() {
        /*Calendar cal=Calendar.getInstance();
        if (expDate.before(new Date())) 
            cal.setTime(new Date());
        else
            cal.setTime(expDate);
        cal.add(Calendar.YEAR, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        expDate=cal.getTime();
*/
    }
    
    public void addNewCompany() {
        
        //Customer newComp=new Customer();
        //ne moze da se radi sa newCompany vec mora da se pravi lokalna promenljiva koja se unistava posle upisa
        //User deepCopy = (User) SerializationUtils.clone(pm);
        
        
        
        
        customerController.create(newCompany, newContact);
        Customer savedCompany = (Customer) SerializationUtils.clone(newCompany);
        //endUsers.add(newCompany);
        //resellers.add(newCompany);
        customers.add(savedCompany);
        
        contacts.add(savedCompany.getPersonSet().stream().findFirst().orElse(null));
        //econtacts.add(newCompany.getPersonSet().stream().findFirst().orElse(null));
        
        if (selReseller==null)
            selReseller=customers.stream().filter(e->e.getCustomerId().equals(newCompany.getCustomerId())).findFirst().orElse(null);
        //selReseller=resellers.stream().filter(e->e.getCustomerId().equals(newCompany.getCustomerId())).findFirst().orElse(null);
        
        if (selEndUser==null)
            selEndUser=customers.stream().filter(e->e.getCustomerId().equals(newCompany.getCustomerId())).findFirst().orElse(null);
        
       selContact=selReseller.getPersonSet().stream().filter(c->c.getPersonId().equals(newContact.getPersonId())).findFirst().orElse(null);
       selEContact=selEndUser.getPersonSet().stream().filter(c->c.getPersonId().equals(newContact.getPersonId())).findFirst().orElse(null);
       newCompany=null;
       newCompany=new Customer();
       newContact=null;
       newContact=new Person();
    }
    
    public void sellLicense() {
        int a=1;
        a=5;
        
        Status active=statusController.getItems().stream().filter(e->e.getName().equalsIgnoreCase("active")).findFirst().orElse(null);
        Status notActivated=statusController.getItems().stream().filter(e->e.getName().equalsIgnoreCase("not activated")).findFirst().orElse(null);
        if (selLicense.getSmaCode()!=null && !selLicense.getSmaCode().isEmpty()) {
            selLicense.setStatusId(active);
            //selLicense.setSmaCode(smaCode);
            selLicense.setExpDate(expDate);
        } else
            selLicense.setStatusId(notActivated);
        
        LicenseCustomer lcReseller=null;
        if (selReseller!=null) {
            lcReseller=new LicenseCustomer();
            lcReseller.setEndUser(false);
            lcReseller.setCustomer(selReseller);
            lcReseller.setLicense(selLicense);
            
        }
        LicenseCustomer lcEndUser=new LicenseCustomer();
        lcEndUser.setEndUser(true);
        lcEndUser.setCustomer(selEndUser);
        lcEndUser.setLicense(selLicense);
        Set<LicenseCustomer> lcSet=new HashSet<>();
        lcSet.add(lcReseller);
        lcSet.add(lcEndUser);
        selLicense.setLicenseCustomerSet(lcSet);
        selLicense.setSoftwareId(selSoftware);
        //selSoftware.getLicenseSet().add(selLicense);
        licenseController.sellLicense(selSoftware, selLicense, selReseller, selEndUser);
    }
    
    public List<Software> getAllsoftware() {
        return allsoftware;
    }

    public void setAllsoftware(List<Software> allsoftware) {
        this.allsoftware = allsoftware;
    }

    public Software getSelSoftware() {
        return selSoftware;
    }

    public void setSelSoftware(Software selSoftware) {
        this.selSoftware = selSoftware;
    }

    public SoftwareController getSoftwareController() {
        return softwareController;
    }

    public void setSoftwareController(SoftwareController softwareController) {
        this.softwareController = softwareController;
    }

    public CustomerController getCustomerController() {
        return customerController;
    }

    public void setCustomerController(CustomerController customerController) {
        this.customerController = customerController;
    }

    public LicenseController getLicenseController() {
        return licenseController;
    }

    public void setLicenseController(LicenseController licenseController) {
        this.licenseController = licenseController;
    }
    
    public Customer getSelReseller() {
        return selReseller;
    }

    public void setSelReseller(Customer selReseller) {
        this.selReseller = selReseller;
    }

    public Customer getSelEndUser() {
        return selEndUser;
    }

    public void setSelEndUser(Customer selEndUser) {
        this.selEndUser = selEndUser;
    }

    public List<License> getLicenses() {
        return licenses;
    }

    public void setLicenses(List<License> licenses) {
        this.licenses = licenses;
    }

    public License getSelLicense() {
        return selLicense;
    }

    public void setSelLicense(License selLicense) {
        this.selLicense = selLicense;
    }

    public String getSmaCode() {
        return smaCode;
    }

    public void setSmaCode(String smaCode) {
        this.smaCode = smaCode;
    }

    public List<Person> getContacts() {
        return contacts;
    }

    public void setContacts(List<Person> contacts) {
        this.contacts = contacts;
    }

    public Person getSelContact() {
        return selContact;
    }

    public void setSelContact(Person selContact) {
        this.selContact = selContact;
    }

    public License getEmptyLicense() {
        return emptyLicense;
    }

    public void setEmptyLicense(License emptyLicense) {
        this.emptyLicense = emptyLicense;
    }

    public Person getSelEContact() {
        return selEContact;
    }

    public void setSelEContact(Person selEContact) {
        this.selEContact = selEContact;
    }

    public String getSoftwareName() {
        return softwareName;
    }

    public void setSoftwareName(String softwareName) {
        this.softwareName = softwareName;
    }

    public Customer getNewCompany() {
        return newCompany;
    }

    public void setNewCompany(Customer newCompany) {
        this.newCompany = newCompany;
    }

    public Person getNewContact() {
        return newContact;
    }

    public void setNewContact(Person newContact) {
        this.newContact = newContact;
    }

    public boolean isLicInputDisabled() {
        return licInputDisabled;
    }

    public void setLicInputDisabled(boolean licInputDisabled) {
        this.licInputDisabled = licInputDisabled;
    }

    public boolean isSmaButtonDisabled() {
        return smaButtonDisabled;
    }

    public void setSmaButtonDisabled(boolean SmaButtonDisabled) {
        this.smaButtonDisabled = SmaButtonDisabled;
    }

    public boolean isExpDateButtonDisabled() {
        return expDateButtonDisabled;
    }

    public void setExpDateButtonDisabled(boolean expDateButtonDisabled) {
        this.expDateButtonDisabled = expDateButtonDisabled;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public List<Person> getEcontacts() {
        return econtacts;
    }

    public void setEcontacts(List<Person> econtacts) {
        this.econtacts = econtacts;
    }

  
  

    
    
    

}
