/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.viewbeans;

import java.io.Serializable;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.SerializationUtils;
import tnt.npse.controllers.CustomerController;
import tnt.npse.controllers.LicenseController;
import tnt.npse.controllers.PersonController;
import tnt.npse.controllers.SoftwareController;
import tnt.npse.controllers.StatusController;
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
    
    private List<Customer> customers = new ArrayList<>();
    private Customer selReseller;
    private Customer selEndUser;
    
    private List<License> licenses;
    private License selLicense;
    
    private List<Person> contacts = new ArrayList<>();
    private Person selContact;
    
    private List<Person> econtacts = new ArrayList<>();
    private Person selEContact;
    
    private String softwareName;
    private Date expDate;
    
    private Customer newCompany;
    private Person newContact;
    
    private boolean addEndUser;
    
    @Inject
    private SoftwareController softwareController;
    @Inject
    private CustomerController customerController;
    @Inject
    private LicenseController licenseController;
    @Inject
    private PersonController personController;
    @Inject
    private StatusController statusController;
    
    
    
    @PostConstruct
    public void init() {
        allsoftware=softwareController.getItems();
        licenses=licenseController.getItems();
        customerController.getItems().stream().forEach(e->customers.add(e));
        customers.forEach(endU->contacts.addAll(endU.getPersonSet()));
        customers.forEach(endU->econtacts.addAll(endU.getPersonSet()));
        newCompany=new Customer();
        newContact=new Person();
        selLicense=new License();
        selSoftware=new Software();
        Calendar cal=Calendar.getInstance();
        cal.add(Calendar.YEAR, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        expDate=cal.getTime();
    }
    
    
    public void resellerSelected() {
        //if reset option in dropdown is selected
        if (selReseller==null) {
            selContact=null;
            contacts=null;
            contacts=personController.getItems();
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
            contacts=null;
            contacts=personController.getItems();
            selReseller=null;
            return;
        }
        //select reseller data based on selected person
        selReseller=(Customer)selContact.getCustomerId();
        //fill a list of reseller contact persons
        contacts=new ArrayList<>();
        selReseller.getPersonSet().stream().forEach(pers->contacts.add(pers));
    }
    
    public void endUserSelected() {
        //if reset option in dropdown is selected
        if (selEndUser==null) {
            customers=null;
            customers=customerController.getItems();
            econtacts=null;
            econtacts=personController.getItems();
            selEContact=null;
            return;
        }
        //fill end user contact list
        econtacts=null;
        econtacts=new ArrayList<>();
        selEndUser.getPersonSet().stream().forEach(pers->econtacts.add(pers));
        //set first contact from end user list
        selEContact=econtacts.stream().findFirst().orElse(null);
    }
    
    public void eContactSelected() {
        //if reset option in dropdown is selected
        if (selEContact==null) {
            customers=new ArrayList<>();
            customers=customerController.getItems();
            econtacts=new ArrayList<>();
            econtacts=personController.getItems();
            selEndUser=null;
            return;
        }
        econtacts=null;
        //fill list of contact persons
        econtacts=new ArrayList<>();
        selEndUser=(Customer)selEContact.getCustomerId();
        selEndUser.getPersonSet().stream().forEach(pers->econtacts.add(pers));
    }
    
    
    //creates a new software with a given name
    public void createSoftware() {
        softwareController.create(softwareName);
        allsoftware=softwareController.getItems();
        selSoftware=allsoftware.stream().filter(e->e.getName().equalsIgnoreCase(softwareName)).findFirst().orElse(null);
    }
    
    public void addingEndUser(boolean endUser) {
        addEndUser=endUser;
    }
    
    //adds a new company to the database
    public void addNewCompany() {
        int number=customers.size();
        customerController.create(newCompany, newContact);
        customers=customerController.getItems();
        //if new Company was added
        if (customers.size()>number) {
            newCompany=customers.stream().filter(e->(e.getName().equalsIgnoreCase(newCompany.getName())&&
                    e.getStreet().equalsIgnoreCase(newCompany.getStreet()) &&
                    e.getCity().equalsIgnoreCase(newCompany.getCity())
                    )).findFirst().orElse(null);
            
            Customer savedCompany = (Customer) SerializationUtils.clone(newCompany);

            contacts.add(savedCompany.getPersonSet().stream().findFirst().orElse(null));
            econtacts.add(savedCompany.getPersonSet().stream().findFirst().orElse(null));

            newContact=savedCompany.getPersonSet().stream().findFirst().orElse(null);
            
            if (addEndUser==false) {
                selReseller=customers.stream().filter(e->e.getCustomerId().equals(newCompany.getCustomerId())).findFirst().orElse(null);
                selContact=selReseller.getPersonSet().stream().filter(c->c.getPersonId().equals(newContact.getPersonId())).findFirst().orElse(null);
            }
                
            if (addEndUser==true) {
                selEndUser=customers.stream().filter(e->e.getCustomerId().equals(newCompany.getCustomerId())).findFirst().orElse(null);
                selEContact=selEndUser.getPersonSet().stream().filter(c->c.getPersonId().equals(newContact.getPersonId())).findFirst().orElse(null);
            }
            
            newCompany=null;
            newCompany=new Customer();
            newContact=null;
            newContact=new Person();
        }
    }
    
    public void sellLicense() {
        
        selLicense.setExpDate(expDate);
        
        Status active=statusController.getItems().stream().filter(e->e.getName().equalsIgnoreCase("active")).findFirst().orElse(null);
        Status notActivated=statusController.getItems().stream().filter(e->e.getName().equalsIgnoreCase("not activated")).findFirst().orElse(null);
        if (selLicense.getSmaCode()!=null && !selLicense.getSmaCode().isEmpty()) {
            selLicense.setStatusId(active);
        } else {
            selLicense.setStatusId(notActivated);
        }
        
        Set<LicenseCustomer> lcSet=new HashSet<>();
        LicenseCustomer lcReseller=null;
        if (selReseller!=null) {
            lcReseller=new LicenseCustomer();
            lcReseller.setEndUser(false);
            lcReseller.setCustomer(selReseller);
            lcReseller.setLicense(selLicense);
            lcSet.add(lcReseller);
        }
        
        selSoftware=softwareController.getItems().stream().filter(sf->sf.getSoftwareId().equals(selSoftware.getSoftwareId())).findFirst().orElse(null);
        
        LicenseCustomer lcEndUser=null;
        if (selEndUser!=null) {
            lcEndUser=new LicenseCustomer();
            lcEndUser.setEndUser(true);
            lcEndUser.setCustomer(selEndUser);
            lcEndUser.setLicense(selLicense);
            lcSet.add(lcEndUser);
        }
        
        selLicense.setLicenseCustomerSet(lcSet);
        
        selLicense.setSoftware(selSoftware);
        
        selSoftware.getLicenseSet().add(selLicense);
        licenseController.sellLicense(selSoftware, selLicense, selReseller, selEndUser);
       
    }
    
    
    //update of existing license
    public void createSMA() {
        
        /*
        if (smaCode!=null && !smaCode.isEmpty()) {
            //smaDisabled=true;
            Calendar cal=Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.YEAR, 1);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            //expDate=cal.getTime();
        }
        */
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

    public boolean isAddEndUser() {
        return addEndUser;
    }

    public void setAddEndUser(boolean addEndUser) {
        this.addEndUser = addEndUser;
    }

    
  

    
    
    

}
