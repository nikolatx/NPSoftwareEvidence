/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import tnt.npse.entities.Customer;

/**
 *
 * @author NN
 */
public class LicenseData implements Serializable {
    
    private Integer licenseId;
    private String licenseCode;
    private String smaCode;
    private Date expDate;
    private String softwareName;
    private String statusName;
    private Customer reseller;
    private Customer endUser;

    public LicenseData() {
    }

    public Integer getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(Integer licenseId) {
        this.licenseId = licenseId;
    }
    
    public String getLicenseCode() {
        return licenseCode;
    }

    public void setLicenseCode(String licenseCode) {
        this.licenseCode = licenseCode;
    }

    public String getSmaCode() {
        return smaCode;
    }

    public void setSmaCode(String smaCode) {
        this.smaCode = smaCode;
    }

    public Date getExpDate() {
        return expDate;
    }

    public void setExpDate(Date expDate) {
        this.expDate = expDate;
    }

    public String getSoftwareName() {
        return softwareName;
    }

    public void setSoftwareName(String softwareName) {
        this.softwareName = softwareName;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(this.licenseId);
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
        final LicenseData other = (LicenseData) obj;
        if ((this.licenseId==null)?(other.licenseId!=null):!this.licenseId.equals(other.licenseId))
            return false;
        //(!Objects.equals(this.licenseId, other.licenseId)) 
        return true;
    }
    
}
