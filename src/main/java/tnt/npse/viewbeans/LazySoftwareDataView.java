/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.viewbeans;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.commons.lang3.SerializationUtils;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.LazyDataModel;
import tnt.npse.controllers.SoftwareController;
import tnt.npse.controllers.util.JsfUtil;
import tnt.npse.entities.Software;
import tnt.npse.model.LazySoftwareDataModel;

/**
 *
 * @author NN
 */
@Named("lazySoftwareDataView")
@ViewScoped
public class LazySoftwareDataView implements Serializable {
    
    private LazyDataModel<Software> lazyModel;
    private Software selectedSoft;
    private Software newSoft;
    private Software originalSoft;
    
    @Inject
    private SoftwareController softwareController;
    
    @PostConstruct
    public void init() {
        lazyModel = new LazySoftwareDataModel(softwareController.getItems());
        lazyModel.setRowIndex(0);
    }
    
    public void onRowSelect(SelectEvent<Software> event) {
        FacesMessage msg = new FacesMessage("Software Selected", String.valueOf(event.getObject().getSoftwareId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
        originalSoft = (Software) SerializationUtils.clone(selectedSoft);
        newSoft=new Software();
    }
    
    public void prepareSoftware() {
        newSoft=new Software();
    }
    
    //adds new company
    public void addSoftware() {
        softwareController.create(newSoft.getName());
        if (!JsfUtil.isValidationFailed()) 
            lazyModel=new LazySoftwareDataModel(softwareController.getItems());
    }
    
    public void editSoftware() {
        softwareController.update(selectedSoft);
        if (!JsfUtil.isValidationFailed()) {
            lazyModel=new LazySoftwareDataModel(softwareController.getItems());
        } else
            selectedSoft.setName(originalSoft.getName());
    }
    
    public void deleteSoftware() {
        softwareController.destroy(selectedSoft);
        if (!JsfUtil.isValidationFailed()) {
            lazyModel.getWrappedData().remove(selectedSoft);
            List<Software> softs=lazyModel.getWrappedData();
            lazyModel=new LazySoftwareDataModel(softs);
            selectedSoft=null;
        }
    }

    public LazyDataModel<Software> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Software> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public Software getSelectedSoft() {
        return selectedSoft;
    }

    public void setSelectedSoft(Software selectedSoft) {
        this.selectedSoft = selectedSoft;
        originalSoft=(Software) SerializationUtils.clone(selectedSoft);
    }

    public Software getNewSoft() {
        return newSoft;
    }

    public void setNewSoft(Software newSoft) {
        this.newSoft = newSoft;
    }

    public Software getOriginalSoft() {
        return originalSoft;
    }

    public void setOriginalSoft(Software originalSoft) {
        this.originalSoft = originalSoft;
    }
    
}
