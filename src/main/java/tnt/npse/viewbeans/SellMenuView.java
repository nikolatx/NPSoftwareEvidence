/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.viewbeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import tnt.npse.controllers.CustomerController;
import tnt.npse.controllers.LicenseController;
import tnt.npse.controllers.LicenseCustomerController;
import tnt.npse.controllers.SoftwareController;
import tnt.npse.entities.Customer;
import tnt.npse.entities.License;
import tnt.npse.entities.LicenseCustomer;
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
    
    private boolean smaDisabled;
    private boolean licDisabled;
    
    
    @Inject
    private SoftwareController softwareController;
    @Inject
    private CustomerController customerController;
    @Inject
    private LicenseController licenseController;
    @Inject
    private LicenseCustomerController licenseCustomerController;
    
    
    
    @PostConstruct
    public void init() {
        softwareController.getItems().stream().forEach(e->allsoftware.add(e));
        licenses=new ArrayList<>();
        allsoftware.stream().forEach(soft->soft.getLicenseSet().stream().forEach(lic->licenses.add(lic)));
        
        List<Customer> cList = new ArrayList<>();
        
        resellers=customerController.getItems(); 
        endUsers=customerController.getItems(); 
        
        /*
        resellers=(licenseCustomerController.getItems().stream()
                .filter(lc->lc.getEndUser()==false))
                .map(e->e.getCustomer())
                .distinct()
                .collect(Collectors.toList());  

        endUsers=(licenseCustomerController.getItems().stream()
                .filter(lc->lc.getEndUser()==true))
                .map(e->e.getCustomer())
                .distinct()
                .collect(Collectors.toList());  
        */        

        resellers.forEach(res->contacts.addAll(res.getPersonSet()));
        endUsers.forEach(endU->econtacts.addAll(endU.getPersonSet()));
        smaDisabled=true;
        licDisabled=true;
    }
    
    public void addSoftware(Software soft) {
        if (soft!=null) 
            allsoftware.add(soft);
    }
    
    
    //add items to SelectOneMenu for licenses
    public void softwareSelected() {
        if (selSoftware==null) {
            resetAllData();
            licDisabled=true;
            return;
        }
        licDisabled=false;
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
            //smaCode=selLicense.getSmaCode();
            licenseSelected();
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
        //selektuj smacode i kompanije i osobe, ali bez blokiranja ostalih kompanija, osobe blokiraj
        if (selLicense==null) {
            resetAllData();
            return;
        }
        smaCode = selLicense.getSmaCode();
        //if smaCode already exists, set input box to readonly
        smaDisabled = smaCode != null && !smaCode.isEmpty();
        
        selReseller=selLicense.getLicenseCustomerSet().stream().filter(lic->lic.getEndUser()==false).findFirst().orElse(null).getCustomer();
        selContact=selReseller.getPersonSet().stream().findFirst().orElse(null);
        selEndUser=selLicense.getLicenseCustomerSet().stream().filter(lic->lic.getEndUser()==true).findFirst().orElse(null).getCustomer();
        selEContact=selEndUser.getPersonSet().stream().findFirst().orElse(null);
        int a=0;
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
    
    public void endUserSelected() {
        //if reset option in dropdown is selected
        if (selEndUser==null) {
            resetAllData();
            return;
        }
        //set first contact from end user list
        selEContact=(Person)selEndUser.getPersonSet().stream().findFirst().orElse(null);
        //fill end user contact list
        econtacts=new ArrayList<>();
        selEndUser.getPersonSet().stream().forEach(pers->econtacts.add(pers));
    }
    
    public void eContactSelected() {
        //if reset option in dropdown is selected
        if (selEContact==null) {
            resetAllData();
            return;
        }
        //select end user
        selEndUser=(Customer)selEContact.getCustomerId();
        //fill list of contact persons
        econtacts=new ArrayList<>();
        selEndUser.getPersonSet().stream().forEach(pers->econtacts.add(pers));
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

    public boolean isSmaDisabled() {
        return smaDisabled;
    }

    public void setSmaDisabled(boolean smaDisabled) {
        this.smaDisabled = smaDisabled;
    }

    public boolean isLicDisabled() {
        return licDisabled;
    }

    public void setLicDisabled(boolean licDisabled) {
        this.licDisabled = licDisabled;
    }

    

}
