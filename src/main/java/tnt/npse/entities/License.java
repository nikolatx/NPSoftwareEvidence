/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.entities;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author NN
 */
@Entity
@Table(name = "license")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "License.findAll", query = "SELECT l FROM License l"),
    @NamedQuery(name = "License.findByLicenseId", query = "SELECT l FROM License l WHERE l.licenseId = :licenseId"),
    @NamedQuery(name = "License.findByLicenseCode", query = "SELECT l FROM License l WHERE l.licenseCode = :licenseCode"),
    @NamedQuery(name = "License.findBySmaCode", query = "SELECT l FROM License l WHERE l.smaCode = :smaCode"),
    @NamedQuery(name = "License.findByExpDate", query = "SELECT l FROM License l WHERE l.expDate = :expDate")})
public class License implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "license_id")
    private Long licenseId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "licenseCode")
    private String licenseCode;
    @Basic(optional = true)
    @Size(max = 255)
    @Column(name = "smaCode")
    private String smaCode;
    @Basic(optional = true)
    @Column(name = "expDate")
    @Temporal(TemporalType.DATE)
    private Date expDate;
    
    @JoinColumn(name = "software_id", referencedColumnName = "software_id")
    @ManyToOne(cascade = CascadeType.PERSIST, optional = false, fetch = FetchType.EAGER)
    private Software software;
    
    @JoinColumn(name = "status_id", referencedColumnName = "status_id")
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private Status statusId;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "license", fetch = FetchType.EAGER)
    private Set<LicenseCustomer> licenseCustomerSet=new HashSet<>();

    
    public License() {
    }

    public License(Long licenseId) {
        this.licenseId = licenseId;
    }

    public License(Long licenseId, String licenseCode, String smaCode, Date expDate) {
        this.licenseId = licenseId;
        this.licenseCode = licenseCode;
        this.smaCode = smaCode;
        this.expDate = expDate;
    }

    public Long getLicenseId() {
        return licenseId;
    }

    public void setLicenseId(Long licenseId) {
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

    public Software getSoftware() {
        return software;
    }
    
    @XmlTransient
    public Set<LicenseCustomer> getLicenseCustomerSet() {
        return licenseCustomerSet;
    }

    public void setLicenseCustomerSet(Set<LicenseCustomer> licenseCustomerSet) {
        this.licenseCustomerSet = licenseCustomerSet;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public Status getStatusId() {
        return statusId;
    }

    public void setStatusId(Status statusId) {
        this.statusId = statusId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (licenseId != null ? licenseId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof License)) {
            return false;
        }
        License other = (License) object;
        if ((this.licenseId == null && other.licenseId != null) || (this.licenseId != null && !this.licenseId.equals(other.licenseId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "tnt.npse.entities.License[ licenseId=" + licenseId + " ]";
    }
    
}
