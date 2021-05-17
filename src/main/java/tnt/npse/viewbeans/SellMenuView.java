/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.viewbeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import tnt.npse.controllers.CustomerController;
import tnt.npse.controllers.LicenseController;
import tnt.npse.controllers.SoftwareController;
import tnt.npse.entities.Customer;
import tnt.npse.entities.License;
import tnt.npse.entities.Person;
import tnt.npse.entities.Software;



/**
 *
 * @author NN
 */
@Named
@ViewScoped
public class SellMenuView implements Serializable {
    
    private List<Software> allsoftware = new ArrayList<>();
    private Software selSoftware;
    
    private List<Customer> resellers = new ArrayList<>();
    private Customer selReseller;
    
    private List<Customer> endUsers = new ArrayList<>();
    private Customer selEndUser;
    
    private List<License> licenses = new ArrayList<>();
    private License selLicense;
    
    private String smaCode;
    private List<Person> contacts = new ArrayList<>();
    private Person selContact;
    
    private List<Person> econtacts = new ArrayList<>();
    private Person selEContact;
    
    private License emptyLicense=new License();
    
    @Inject
    private SoftwareController softwareController;
    @Inject
    private CustomerController customerController;
    @Inject
    private LicenseController licenseController;
    
    
    @PostConstruct
    public void init() {
        softwareController.getItems().stream().forEach(e->allsoftware.add(e));
        licenses=new ArrayList<>();
        allsoftware.stream().forEach(soft->soft.getLicenseSet().stream().forEach(lic->licenses.add(lic)));
        customerController.getItems().stream().filter(e->e.getEndCustomer()==false).forEach(e->resellers.add(e));
        customerController.getItems().stream().filter(e->e.getEndCustomer()==true).forEach(e->endUsers.add(e));
        resellers.stream().forEach(res->res.getPersonSet().stream().forEach(per->contacts.add(per)));
        endUsers.stream().forEach(res->res.getPersonSet().stream().forEach(per->econtacts.add(per)));
        
        
        
    }
    
    //add items to SelectOneMenu for licenses
    public void softwareSelected() {
        if (selSoftware==null) 
        {
            resetAllData();
            return;
        }
        //load licenses (sma if 1 license), resellers, endusers, contacts for both resellers and endusers
        licenses=(List<License>) licenseController.getItems()
                .stream()
                .filter(e->e.getSoftwareId()
                .getName()
                .equalsIgnoreCase(selSoftware.getName())).collect(Collectors.toList());
        if (licenses!=null && licenses.size()==1) {
            //there is only one license
            selLicense=licenses.get(0);
            //only one smaCode
            smaCode=selLicense.getSmaCode();
            resellers=new ArrayList<>();
            endUsers=new ArrayList<>();
            licenses.stream().forEach(lic->lic.getCustomerSet().stream().filter(e->e.getEndCustomer()==false).forEach(cust->resellers.add(cust)));
            licenses.stream().forEach(lic->lic.getCustomerSet().stream().filter(e->e.getEndCustomer()==true).forEach(cust->endUsers.add(cust)));
            
            //exact Reseller
            selReseller=selLicense.getCustomerSet().stream().filter(e->e.getEndCustomer()==false).findFirst().orElse(null);
            //exact EndUser
            selEndUser=selLicense.getCustomerSet().stream().filter(e->e.getEndCustomer()==true).findFirst().orElse(null);
            //first contact of reseller
            contacts=new ArrayList<>();
            econtacts=new ArrayList<>();
            resellers.stream().forEach(res->res.getPersonSet().stream().forEach(pers->contacts.add(pers)));
            endUsers.stream().forEach(res->res.getPersonSet().stream().forEach(pers->econtacts.add(pers)));
            
            selContact=selReseller.getPersonSet().stream().findFirst().orElse(null);
            //first contact of enduser
            selEContact=selEndUser.getPersonSet().stream().findFirst().orElse(null);
        } else if (licenses.size()>1) {
            //limit selection options for each menu
            resellers=new ArrayList<>();
            endUsers=new ArrayList<>();
            contacts=new ArrayList<>();
            econtacts=new ArrayList<>();
            
            licenses.stream().forEach(lic->lic.getCustomerSet()
                    .stream().filter(cust->cust.getEndCustomer()==false).forEach(e->resellers.add(e)));
            licenses.stream().forEach(lic->lic.getCustomerSet()
                    .stream().filter(cust->cust.getEndCustomer()==true).forEach(e->endUsers.add(e)));
            
            resellers.stream().forEach(cust->getContacts().stream().forEach(pers->contacts.add(pers)));
            endUsers.stream().forEach(cust->getContacts().stream().forEach(pers->econtacts.add(pers)));
        }
        else {
            resetAllData();
        }
    }
    
    private void resetAllData() {
        selLicense=null;
        selReseller=null;
        selEndUser=null;
        selContact=null;
        selEContact=null;
        contacts=new ArrayList<>();
        econtacts=new ArrayList<>();
        resellers=new ArrayList<>();
        endUsers=new ArrayList<>();
        licenses=new ArrayList<>();
        allsoftware=new ArrayList<>();
        smaCode="";
        init();
    }
    
    public void licenseSelected() {
        if (selLicense==null) {
            resetAllData();
            return;
        }
        smaCode = selLicense.getSmaCode();
        contacts=new ArrayList<>();
        selReseller = selLicense.getCustomerSet().stream().filter(e->e.getEndCustomer()==false).findFirst().orElse(null);
        if (selReseller.getPersonSet()!=null && selReseller.getPersonSet().size()>0) {
            selReseller.getPersonSet().stream().forEach(e->contacts.add(e));
            selContact=contacts.get(0);
        }
        
        
        int a=1;
    }
    
    public void resellerSelected() {
        if (selReseller==null) {
            resetAllData();
            return;
        }
        //popuni meni za osobe za kontakt
        contacts=new ArrayList<>();
        selReseller.getPersonSet().stream().forEach(contacts::add);
        if (contacts!=null)
            selContact=contacts.get(0);
        else
            selContact=null;
        int a=1;
    }
    
    public void contactSelected() {
        if (selContact==null) {
            resetAllData();
            return;
        }
        selReseller=(Customer)selContact.getCustomerId();
        licenses=new ArrayList<>();
        selReseller.getLicenseSet().stream().forEach(licenses::add);
        allsoftware=new ArrayList<>();
        licenses.stream().forEach(lic->allsoftware.add(lic.getSoftwareId()));
    }
    
    public void endUserSelected() {
        
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

    public List<Customer> getResellers() {
        return resellers;
    }

    public LicenseController getLicenseController() {
        return licenseController;
    }

    public void setLicenseController(LicenseController licenseController) {
        this.licenseController = licenseController;
    }
    
    public void setResellers(List<Customer> resellers) {
        this.resellers = resellers;
    }

    public Customer getSelReseller() {
        return selReseller;
    }

    public void setSelReseller(Customer selReseller) {
        this.selReseller = selReseller;
    }

    public List<Customer> getEndUsers() {
        return endUsers;
    }

    public void setEndUsers(List<Customer> endUsers) {
        this.endUsers = endUsers;
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

    public List<Person> getEcontacts() {
        return econtacts;
    }

    public void setEcontacts(List<Person> econtacts) {
        this.econtacts = econtacts;
    }

    public Person getSelEContact() {
        return selEContact;
    }

    public void setSelEContact(Person selEContact) {
        this.selEContact = selEContact;
    }

    

}
