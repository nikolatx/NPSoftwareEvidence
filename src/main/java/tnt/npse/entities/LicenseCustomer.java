/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
    @NamedQuery(name = "LicenseCustomer.findByLicenseId", query = "SELECT l FROM LicenseCustomer l WHERE l.licenseCustomerPK.licenseId = :licenseId"),
    @NamedQuery(name = "LicenseCustomer.findByCustomerId", query = "SELECT l FROM LicenseCustomer l WHERE l.licenseCustomerPK.customerId = :customerId"),
    @NamedQuery(name = "LicenseCustomer.findByEndUser", query = "SELECT l FROM LicenseCustomer l WHERE l.endUser = :endUser")})
public class LicenseCustomer implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected LicenseCustomerPK licenseCustomerPK;
    @Basic(optional = false)
    @NotNull
    @Column(name = "end_user")
    private boolean endUser;
    @JoinColumn(name = "license_id", referencedColumnName = "license_id", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private License license;
    @JoinColumn(name = "customer_id", referencedColumnName = "customer_id", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Customer customer;

    public LicenseCustomer() {
    }

    public LicenseCustomer(LicenseCustomerPK licenseCustomerPK) {
        this.licenseCustomerPK = licenseCustomerPK;
    }

    public LicenseCustomer(LicenseCustomerPK licenseCustomerPK, boolean endUser) {
        this.licenseCustomerPK = licenseCustomerPK;
        this.endUser = endUser;
    }

    public LicenseCustomer(int licenseId, int customerId) {
        this.licenseCustomerPK = new LicenseCustomerPK(licenseId, customerId);
    }

    public LicenseCustomerPK getLicenseCustomerPK() {
        return licenseCustomerPK;
    }

    public void setLicenseCustomerPK(LicenseCustomerPK licenseCustomerPK) {
        this.licenseCustomerPK = licenseCustomerPK;
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
        hash += (licenseCustomerPK != null ? licenseCustomerPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LicenseCustomer)) {
            return false;
        }
        LicenseCustomer other = (LicenseCustomer) object;
        if ((this.licenseCustomerPK == null && other.licenseCustomerPK != null) || (this.licenseCustomerPK != null && !this.licenseCustomerPK.equals(other.licenseCustomerPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "tnt.npsoftwareevidence.model.LicenseCustomer[ licenseCustomerPK=" + licenseCustomerPK + " ]";
    }
    
}
