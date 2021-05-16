/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.entities;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import tnt.npse.controllers.LicenseController;

/**
 *
 * @author NN
 */
@Named("dtLazyView")
@ViewScoped
public class LazyLicenseDTView implements Serializable {
    
    private LazyDataModel<License> lazyModel;
    private License selectedLic;
    private Customer reseller;
    private Customer endUser;
    
    //private List<License> selectedLicenses;
    
    @Inject
    private LicenseController licenseController;
    
    @PostConstruct
    public void init() {
        lazyModel = new LazyLicenseDataModel(licenseController.getItems());
        int a=1;
    }
    
    public void openNew() {
        this.selectedLic = new License();
    }
    
    
    
    
    
    public void onRowSelect(SelectEvent<License> event) {
        FacesMessage msg = new FacesMessage("License Selected", String.valueOf(event.getObject().getLicenseId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
        Set<Customer> resellers=selectedLic.getCustomerSet();
        reseller=resellers.stream().filter(e->e.getEndCustomer()==false).findFirst().get();
        endUser=resellers.stream().filter(e->e.getEndCustomer()==true).findFirst().get();
    }

    public LazyDataModel<License> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<License> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public License getSelectedLic() {
        return selectedLic;
    }

    public void setSelectedLic(License selectedLic) {
        this.selectedLic = selectedLic;
        Set<Customer> resellers=selectedLic.getCustomerSet();
        reseller=resellers.stream().filter(e->e.getEndCustomer()==false).findFirst().get();
        endUser=resellers.stream().filter(e->e.getEndCustomer()==true).findFirst().get();
    }

    public LicenseController getLicenseController() {
        return licenseController;
    }

    public void setLicenseController(LicenseController licenseController) {
        this.licenseController = licenseController;
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

    
}
