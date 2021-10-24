/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.viewbeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import tnt.npse.controllers.SettingsController;
import tnt.npse.controllers.StatusController;
import tnt.npse.entities.Settings;
import tnt.npse.entities.Status;

/**
 *
 * @author NN
 */
@Named("settingsView")
@ViewScoped
public class SettingsView implements Serializable {
   
    List<Status> statuses=new ArrayList<>();
    private Status statWithSMA;
    private Status statWithoutSMA;
    private Status statDeleted;
    private boolean showDeleted;
    
    
    @Inject
    private StatusController statusController;
    @Inject
    private SettingsController settingsController;
    
    @PostConstruct
    public void init() {
        statuses=statusController.getItems();
        List<Settings> settings=settingsController.getItems();
        Settings setting=new Settings();
        if (settings==null || settings.isEmpty()) {
            statusController.createDefaultStatuses();
            Status active=statusController.getItems().stream().filter(e->e.getName().equalsIgnoreCase("active")).findFirst().orElse(null);
            Status notActivated=statusController.getItems().stream().filter(e->e.getName().equalsIgnoreCase("not activated")).findFirst().orElse(null);
            Status deleted=statusController.getItems().stream().filter(e->e.getName().equalsIgnoreCase("deleted")).findFirst().orElse(null);
            setting.setStatWithSMA(active);
            setting.setStatWithoutSMA(notActivated);
            setting.setStatDeleted(deleted);
            setting.setShowDeleted(true);
            settingsController.create(setting);
            settings=settingsController.getItems();
        }
        setting=settings.get(0);
        statWithSMA=setting.getStatWithSMA();
        statWithoutSMA=setting.getStatWithoutSMA();
        statDeleted=setting.getStatDeleted();
        showDeleted=setting.isShowDeleted();

        int a=1;
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<Status> statuses) {
        this.statuses = statuses;
    }

    public Status getStatWithSMA() {
        return statWithSMA;
    }

    public void setStatWithSMA(Status statWithSMA) {
        this.statWithSMA = statWithSMA;
    }

    public Status getStatWithoutSMA() {
        return statWithoutSMA;
    }

    public void setStatWithoutSMA(Status statWithoutSMA) {
        this.statWithoutSMA = statWithoutSMA;
    }

    public boolean isShowDeleted() {
        return showDeleted;
    }

    public void setShowDeleted(boolean showDeleted) {
        this.showDeleted = showDeleted;
    }

    public Status getStatDeleted() {
        return statDeleted;
    }

    public void setStatDeleted(Status statDeleted) {
        this.statDeleted = statDeleted;
    }
    
    
    
    
    
    
    
}
