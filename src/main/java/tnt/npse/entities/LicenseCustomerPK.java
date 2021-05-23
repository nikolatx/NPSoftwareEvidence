/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

/**
 *
 * @author NN
 */
@Embeddable
public class LicenseCustomerPK implements Serializable {

    @Basic(optional = false)
    @NotNull
    @Column(name = "license_id")
    private int licenseId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "customer_id")
    private int customerId;

    public LicenseCustomerPK() {
    }

    public LicenseCustomerPK(int licenseId, int customerId) {
        this.licenseId = licenseId;
        this.customerId = customerId;
    }

    public int getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(int licenseId) {
        this.licenseId = licenseId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (int) licenseId;
        hash += (int) customerId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof LicenseCustomerPK)) {
            return false;
        }
        LicenseCustomerPK other = (LicenseCustomerPK) object;
        if (this.licenseId != other.licenseId) {
            return false;
        }
        if (this.customerId != other.customerId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "tnt.npsoftwareevidence.model.LicenseCustomerPK[ licenseId=" + licenseId + ", customerId=" + customerId + " ]";
    }
    
}
