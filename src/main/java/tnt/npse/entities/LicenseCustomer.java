/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author NN
 */
@Entity
@Table(name = "license_customer")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "LicenseCustomer.findAll", query = "SELECT l FROM LicenseCustomer l"),
    @NamedQuery(name = "LicenseCustomer.findByLcId", query = "SELECT l FROM LicenseCustomer l WHERE l.lcId = :lcId"),
    @NamedQuery(name = "LicenseCustomer.findByEndUser", query = "SELECT l FROM LicenseCustomer l WHERE l.endUser = :endUser")})
public class LicenseCustomer implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "lc_id")
    private Long lcId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "end_user")
    private boolean endUser;
    @JoinColumn(name = "license_id", referencedColumnName = "license_id")
    @ManyToOne(optional = false)
    private License license;
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id")
    @ManyToOne(optional = true)
    private Customer customer;

    public LicenseCustomer() {
    }

    public LicenseCustomer(Long lcId) {
        this.lcId = lcId;
    }

    public LicenseCustomer(Long lcId, boolean endUser) {
        this.lcId = lcId;
        this.endUser = endUser;
    }

    public Long getLcId() {
        return lcId;
    }

    public void setLcId(Long lcId) {
        this.lcId = lcId;
    }

    public boolean getEndUser() {
        return endUser;
    }

    public void setEndUser(boolean endUser) {
        this.endUser = endUser;
    }

    public License getLicense() {
        return license;
    }

    public void setLicense(License license) {
        this.license = license;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (lcId != null ? lcId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LicenseCustomer)) {
            return false;
        }
        LicenseCustomer other = (LicenseCustomer) object;
        if ((this.lcId == null && other.lcId != null) || (this.lcId != null && !this.lcId.equals(other.lcId))) {
            return false;
        }
        if (this.lcId==null && other.lcId==null) {
            if ((this.customer!=null && other.customer==null)||(this.customer==null && other.customer!=null)) 
                return false;
            else if (this.customer!=other.customer || this.endUser!=other.endUser)
                return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "tnt.npsoftwareevidence.model.LicenseCustomer[ lcId=" + lcId + " ]";
    }
    
}
