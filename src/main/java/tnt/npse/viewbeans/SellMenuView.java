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
    
    @Inject
    private SoftwareController softwareController;
    @Inject
    private CustomerController customerController;
    @Inject
    private LicenseController licenseController;
    
    
    @PostConstruct
    public void init() {
        softwareController.getItems().stream().forEach(e->allsoftware.add(e));
        customerController.getItems().stream().filter(e->e.getEndCustomer()==false).forEach(e->resellers.add(e));
        customerController.getItems().stream().filter(e->e.getEndCustomer()==true).forEach(e->endUsers.add(e));
        
    }
    
    //add items to SelectOneMenu for licenses
    public void softwareSelected() {
        licenses=(List<License>) licenseController.getItems()
                .stream()
                .filter(e->e.getSoftwareId()
                .getName()
                .equalsIgnoreCase(selSoftware.getName())).collect(Collectors.toList());
    }
    
    public void licenseSelected() {
        smaCode = selLicense.getSmaCode();
    }
    
    public void resellerSelected() {
        //popuni meni za osobe za kontakt
        contacts=new ArrayList<>();
        selReseller.getPersonSet().stream().forEach(contacts::add);
        int a=1;
    }
    
    public void contactSelected() {
        
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

    

}
