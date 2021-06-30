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
import tnt.npse.controllers.StatusController;
import tnt.npse.controllers.util.JsfUtil;
import tnt.npse.entities.Status;
import tnt.npse.model.LazyStatusDataModel;

/**
 *
 * @author NN
 */
@Named("lazyStatusDataView")
@ViewScoped
public class LazyStatusDataView implements Serializable {
    
    private LazyDataModel<Status> lazyModel;
    private Status selectedStat;
    private Status newStat;
    private Status originalStat;
    private String statusName;
    
    @Inject
    private StatusController statusController;
    
    @PostConstruct
    public void init() {
        lazyModel = new LazyStatusDataModel(statusController.getItems());
        lazyModel.setRowIndex(0);
    }
    
    public void onRowSelect(SelectEvent<Status> event) {
        FacesMessage msg = new FacesMessage("Status Selected", String.valueOf(event.getObject().getStatusId()));
        FacesContext.getCurrentInstance().addMessage(null, msg);
        originalStat = (Status) SerializationUtils.clone(selectedStat);
        newStat=new Status();
    }
    
    public void prepareStatus() {
        newStat=new Status();
    }
    
    //adds new company
    public void addStatus() {
        statusController.create(newStat.getName());
        if (!JsfUtil.isValidationFailed()) 
            lazyModel=new LazyStatusDataModel(statusController.getItems());
    }
    
    public void editStatus() {
        statusController.update(selectedStat);
        if (!JsfUtil.isValidationFailed()) {
            lazyModel=new LazyStatusDataModel(statusController.getItems());
        } else
            selectedStat.setName(originalStat.getName());
    }
    
    public void deleteStatus() {
        statusController.destroy(selectedStat);
        if (!JsfUtil.isValidationFailed()) {
            lazyModel.getWrappedData().remove(selectedStat);
            List<Status> stats=lazyModel.getWrappedData();
            lazyModel=new LazyStatusDataModel(stats);
            selectedStat=null;
        }
    }

    public LazyDataModel<Status> getLazyModel() {
        return lazyModel;
    }

    public void setLazyModel(LazyDataModel<Status> lazyModel) {
        this.lazyModel = lazyModel;
    }

    public Status getSelectedStat() {
        return selectedStat;
    }

    public void setSelectedStat(Status selectedStat) {
        this.selectedStat = selectedStat;
        originalStat=(Status) SerializationUtils.clone(selectedStat);
    }

    public Status getNewStat() {
        return newStat;
    }

    public void setNewStat(Status newStat) {
        this.newStat = newStat;
    }

    public Status getOriginalStat() {
        return originalStat;
    }

    public void setOriginalStat(Status originalStat) {
        this.originalStat = originalStat;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }
    
}
