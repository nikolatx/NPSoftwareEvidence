/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.entities;

import java.io.Serializable;
import java.util.Objects;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author NN
 */
@Entity
@Table(name = "settings")
@XmlRootElement
public class Settings implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "setting_id")
    private Long settingId;
    
    @OneToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="stat_with_sma", referencedColumnName = "status_id")
    private Status statWithSMA;
    
    @OneToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="stat_without_sma", referencedColumnName = "status_id")
    private Status statWithoutSMA;
    
    @OneToOne(fetch=FetchType.EAGER)
    @JoinColumn(name="stat_deleted", referencedColumnName = "status_id")
    private Status statDeleted;
    
    @Basic(optional = false)
    @NotNull
    @Column(name = "show_deleted")
    private boolean showDeleted;

    public Settings() {
    }

    public Long getSettingId() {
        return settingId;
    }

    public void setSettingId(Long settingId) {
        this.settingId = settingId;
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
    
    
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.settingId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Settings other = (Settings) obj;
        if (this.showDeleted != other.showDeleted) {
            return false;
        }
        if (!Objects.equals(this.settingId, other.settingId)) {
            return false;
        }
        if (!Objects.equals(this.statWithSMA, other.statWithSMA)) {
            return false;
        }
        if (!Objects.equals(this.statWithoutSMA, other.statWithoutSMA)) {
            return false;
        }
        return true;
    }

    
    
}
