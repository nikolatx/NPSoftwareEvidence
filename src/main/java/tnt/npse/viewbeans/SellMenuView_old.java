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
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import tnt.npse.controllers.CustomerController;
import tnt.npse.controllers.LicenseController;
import tnt.npse.controllers.LicenseCustomerController;
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
public class SellMenuView_old implements Serializable {
    
    private List<Software> allsoftware = new ArrayList<>();
    private Software selSoftware;
    
    private List<Customer> resellers = new ArrayList<>();
    private Customer selReseller;
    
    private List<Customer> endUsers = new ArrayList<>();
    private Customer selEndUser;
    
    private List<License> licenses;
    private License selLicense;
    
    private String smaCode;
    private List<Person> contacts = new ArrayList<>();
    private Person selContact;
    
    private List<Person> econtacts = new ArrayList<>();
    private Person selEContact;
    
    private License emptyLicense=new License();
    
    private boolean smaDisabled;
    private boolean licDisabled;
    private boolean extendDisabled;
    
    private String softwareName;
    private String licenseCode;
    private Date expDate;
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
        extendDisabled=true;
        newCompany=new Customer();
        newContact=new Person();
    }
    
    
    
    
    //add items to SelectOneMenu for licenses
    public void softwareSelected() {
        if (selSoftware==null) {
            resetAllData();
            licDisabled=true;
            return;
        }
        licDisabled=false;
        licenses=null;
        licenses=(List<License>) licenseController.getItems()
            .stream()
            .filter(e->e.getSoftware()
            .getName()
            .equalsIgnoreCase(selSoftware.getName())).collect(Collectors.toList());
/*
        if (licenses!=null && licenses.size()==1) {
            //there is only one license
            selLicense=licenses.get(0);
            //only one smaCode
            //smaCode=selLicense.getSmaCode();
            licenseSelected();
        } 
        else {
            selLicense=null;
            selReseller=null;
            selEndUser=null;
            selContact=null;
            selEContact=null;
            smaCode="";
            expDate=null;
        }
*/
    }
    
    private void resetAllData() {
        selLicense=null;
        selReseller=null;
        selEndUser=null;
        selContact=null;
        selEContact=null;
        licenses=null;
        contacts=new ArrayList<>();
        econtacts=new ArrayList<>();
        resellers=new ArrayList<>();
        endUsers=new ArrayList<>();
        licenses=new ArrayList<>();
        allsoftware=new ArrayList<>();
        smaCode="";
        expDate=null;
        init();
    }
    
    public void licenseSelected() {
        //selektuj smacode i kompanije i osobe, ali bez blokiranja ostalih kompanija, osobe blokiraj
        if (selLicense==null) {
            resetAllData();
            return;
        }
        smaCode = selLicense.getSmaCode();
        expDate = selLicense.getExpDate();
        selSoftware=selLicense.getSoftware();
        //if smaCode already exists, set input box to readonly
        smaDisabled = smaCode != null && !smaCode.isEmpty();
        extendDisabled = smaCode==null || smaCode.isEmpty();
        
        LicenseCustomer lc=selLicense.getLicenseCustomerSet().stream().filter(lic->lic.getEndUser()==false).findFirst().orElse(null);
        if (lc!=null) {
            selReseller=lc.getCustomer();
            selContact=selReseller.getPersonSet().stream().findFirst().orElse(null);
        } else {
            selReseller=null;
            selContact=null;
        }
        lc=selLicense.getLicenseCustomerSet().stream().filter(lic->lic.getEndUser()==true).findFirst().orElse(null);
        if (lc!=null) {
            selEndUser=lc.getCustomer();
            selEContact=selEndUser.getPersonSet().stream().findFirst().orElse(null);
        } else {
            selEndUser=null;
            selEContact=null;
        }
    }
    
    public void check1() {
        int a=1;
        a=a+1;
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
        econtacts=null;
        //fill list of contact persons
        econtacts=new ArrayList<>();
        selEndUser.getPersonSet().stream().forEach(pers->econtacts.add(pers));
        //select end user
        selEndUser=(Customer)selEContact.getCustomerId();
    }
    
    
    
    public void createSoftware() {
        
        //Software soft=softwareController.create(softwareName);
        Software soft = new Software();
        soft.setName(softwareName);
        allsoftware.add(soft);
        selSoftware=soft;
        //softwareSelected();
    }
    
    public void createLicense() {
        //License lic=licenseController.create(licenseCode, selSoftware.getName());
        License lic = new License();
        lic.setLicenseCode(selLicense.getLicenseCode());   //(licenseCode);
        lic.setSmaCode("");
        lic.setExpDate(null);
        lic.setSoftware(selSoftware);
        lic.setStatusId(null);
        licenses.add(lic);
        selLicense=lic;
        //licenseSelected();
    }
    
    public void createSMA() {
        if (smaCode!=null && !smaCode.isEmpty()) {
            smaDisabled=true;
            Calendar cal=Calendar.getInstance();
            cal.setTime(new Date());
            cal.add(Calendar.YEAR, 1);
            cal.add(Calendar.DAY_OF_MONTH, -1);
            expDate=cal.getTime();
        }
    }
    
    public void extendSMA() {
        Calendar cal=Calendar.getInstance();
        if (expDate.before(new Date())) 
            cal.setTime(new Date());
        else
            cal.setTime(expDate);
        cal.add(Calendar.YEAR, 1);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        expDate=cal.getTime();
    }
    
    public void addNewCompany() {
        
        customerController.create(newCompany, newContact);
        resellers.add(newCompany);
        selReseller=newCompany;
        contacts.add(newContact);
        selContact=newContact;
    }
    
    public void sellLicense() {
        int a=1;
        a=5;
        
        Status active=statusController.getItems().stream().filter(e->e.getName().equalsIgnoreCase("active")).findFirst().orElse(null);
        Status notActivated=statusController.getItems().stream().filter(e->e.getName().equalsIgnoreCase("not activated")).findFirst().orElse(null);
        if (smaCode!=null && !smaCode.isEmpty()) {
            selLicense.setStatusId(active);
            selLicense.setSmaCode(smaCode);
        } else
            selLicense.setStatusId(notActivated);
        if (expDate!=null)
            selLicense.setExpDate(expDate);
        LicenseCustomer lcReseller=null;
        if (selReseller!=null) {
            lcReseller=new LicenseCustomer();
            lcReseller.setEndUser(false);
            lcReseller.setCustomer(selReseller);
            lcReseller.setLicense(selLicense);
        }
        LicenseCustomer lcEndUser=new LicenseCustomer();
        lcEndUser.setEndUser(true);
        selEndUser.setCustomerId(2);
        lcEndUser.setCustomer(selEndUser);
        lcEndUser.setLicense(selLicense);
        Set<LicenseCustomer> lcSet=new HashSet<>();
        lcSet.add(lcReseller);
        lcSet.add(lcEndUser);
        selLicense.setLicenseCustomerSet(lcSet);
        selSoftware.getLicenseSet().add(selLicense);
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

    public String getSoftwareName() {
        return softwareName;
    }

    public void setSoftwareName(String softwareName) {
        this.softwareName = softwareName;
    }

    public String getLicenseCode() {
        return licenseCode;
    }

    public void setLicenseCode(String licenseCode) {
        this.licenseCode = licenseCode;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public boolean isExtendDisabled() {
        return extendDisabled;
    }

    public void setExtendDisabled(boolean extendDisabled) {
        this.extendDisabled = extendDisabled;
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

    
    
    

}
