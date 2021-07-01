/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import tnt.npse.util.LazyLicenseDataSorter;
import tnt.npse.entities.License;
import tnt.npse.entities.LicenseCustomer;


/**
 *
 * @author NN
 */
public class LazyLicenseDataModel extends LazyDataModel<LicenseData> {

    private List<LicenseData> licenses=new ArrayList<>();

    //od liste licenci pravi se lista LicenseData
    public LazyLicenseDataModel(List<License> licenses1) {
        licenses1.forEach(lic->{
            LicenseData ld=new LicenseData();
            ld.setLicenseId(lic.getLicenseId());
            ld.setLicenseCode(lic.getLicenseCode());
            ld.setSmaCode(lic.getSmaCode());
            ld.setExpDate(lic.getExpDate());
            ld.setSoftwareName(lic.getSoftware().getName());
            ld.setStatusName(lic.getStatusId().getName());
            
            LicenseCustomer lc=new LicenseCustomer();
            lc=null;
            if (lic.getLicenseCustomerSet()!=null)
                lc=lic.getLicenseCustomerSet().stream().filter(f->f.getEndUser()==false).findFirst().orElse(null);
            if (lc!=null && lc.getCustomer()!=null)
                ld.setReseller(lc.getCustomer());
            else
                ld.setReseller(null);
            if (lic.getLicenseCustomerSet()!=null)
                lc=lic.getLicenseCustomerSet().stream().filter(f->f.getEndUser()==true).findFirst().orElse(null);
            if (lc!=null && lc.getCustomer()!=null)
                ld.setEndUser(lc.getCustomer());
            else
                ld.setEndUser(null);
            
            this.licenses.add(ld);
        });
    }

    @Override
    public LicenseData getRowData(String rowKey) {
        for(LicenseData license : licenses) {
            if (license.getLicenseId() == Long.parseLong(rowKey))
                return license;
        }
        return null;
    }

    @Override
    public String getRowKey(LicenseData license) {
        return String.valueOf(license.getLicenseId());
    }

    @Override
    public List<LicenseData> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        
        List<LicenseData> data = new ArrayList<>();
        
        //u listu data se ubacuju knjige iz pocetne liste svih knjiga koje zadovoljavaju filtere
        for (LicenseData k : licenses) {
            boolean match = false, notEmpty=false;
            
            //ukoliko su zadati kriterijumi filtriranja radi se filtriranje; kriterijumi su zadati mapom filterBy u kojoj
            //key ima vrednost naziva polja po kom se filtrira, a value ima vrednost koja je uneta
            if (filterBy != null) {
                for (Iterator<String> it = filterBy.keySet().iterator(); it.hasNext();) {
                    try {
                        //uzima se jedan po jedan od unetih kriterijuma za filtriranje
                        String filterProperty = it.next();  //naziv polja
                        Field field = k.getClass().getDeclaredField(filterProperty);
                        //mora da se omoguci pristup polju
                        field.setAccessible(true);
                        String fieldValue=String.valueOf(field.get(k)); //vrednost polja
                        String filterValueStr=(String)filterBy.get(filterProperty).getFilterValue();    //uneta vrednost filtra
                        
                        //ako je uneta neka vrednost za filtar
                        if(filterValueStr != null) {
                            //oznacava se da je uneto pojam po kom se filtrira
                            notEmpty=true;
                            if (fieldValue.toLowerCase().contains(filterValueStr)) {
                                //ako vrednost polja sadrzi unetu vrednost po kojoj se filtrira
                                match = true;
                                break;
                            }
                        }
                    } catch(Exception e) {
                        match = false;
                    }
                }
            }
            //ako vrednost polja sadrzi vrednost po kojoj se filtrira, ili nije unet ni jedan kriterijum za filtriranje
            //knjiga se ubacuje u listu podataka za prikaz
            if (match || notEmpty==false) {
                data.add(k);
            }
        }
        
        //sort
        if (sortBy != null && !sortBy.isEmpty()) {
            for (SortMeta meta : sortBy.values()) {
                Collections.sort(data, new LazyLicenseDataSorter(meta.getSortField(), meta.getSortOrder()));
            }
        }
        
        //rowCount
        int dataSize = data.size();
        this.setRowCount(dataSize);
 
        //paginate
        if (dataSize > pageSize) {
            try {
                return data.subList(first, first + pageSize);
            }
            catch (IndexOutOfBoundsException e) {
                return data.subList(first, first + (dataSize % pageSize));
            }
        }
        else {
            return data;
        }
    }

    public List<LicenseData> getLicenses() {
        return licenses;
    }

    public void setLicenses(List<LicenseData> licenses) {
        this.licenses = licenses;
    }
    
}
