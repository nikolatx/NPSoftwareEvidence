/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import tnt.npse.controllers.LicenseController;

/**
 *
 * @author NN
 */
@Named("dtBasicView")
@ViewScoped
public class BasicView implements Serializable {
    
    private List<License> licenses = new ArrayList<>();
    
    private License selectedLicense;
    
    
    @Inject
    private LicenseController licenseController;
 
    @PostConstruct
    public void init() {
        licenses = licenseController.getItems();
    }

    public List<License> getLicenses() {
        return licenses;
    }

    public void setLicenses(List<License> licenses) {
        this.licenses = licenses;
    }

    public License getSelectedLicense() {
        return selectedLicense;
    }

    public void setSelectedLicense(License selectedLicense) {
        this.selectedLicense = selectedLicense;
    }

    public LicenseController getLicenseController() {
        return licenseController;
    }

    public void setLicenseController(LicenseController licenseController) {
        this.licenseController = licenseController;
    }
    
}
