/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.entities;

import java.io.Serializable;
import java.util.Set;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author NN
 */
@Entity
@Table(name = "software")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Software.findAll", query = "SELECT s FROM Software s"),
    @NamedQuery(name = "Software.findBySoftwareId", query = "SELECT s FROM Software s WHERE s.softwareId = :softwareId"),
    @NamedQuery(name = "Software.findByName", query = "SELECT s FROM Software s WHERE s.name = :name")})
public class Software implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "software_id")
    private Integer softwareId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "name")
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "softwareId", fetch = FetchType.EAGER)
    private Set<License> licenseSet;

    public Software() {
    }

    public Software(String name) {
        this.name = name;
    }

    public Software(Integer softwareId) {
        this.softwareId = softwareId;
    }

    public Software(Integer softwareId, String name) {
        this.softwareId = softwareId;
        this.name = name;
    }

    public Integer getSoftwareId() {
        return softwareId;
    }

    public void setSoftwareId(Integer softwareId) {
        this.softwareId = softwareId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlTransient
    public Set<License> getLicenseSet() {
        return licenseSet;
    }

    public void setLicenseSet(Set<License> licenseSet) {
        this.licenseSet = licenseSet;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (softwareId != null ? softwareId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Software)) {
            return false;
        }
        Software other = (Software) object;
        if ((this.softwareId == null && other.softwareId != null) || (this.softwareId != null && !this.softwareId.equals(other.softwareId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "tnt.npse.entities.Software[ softwareId=" + softwareId + " ]";
    }
    
}
