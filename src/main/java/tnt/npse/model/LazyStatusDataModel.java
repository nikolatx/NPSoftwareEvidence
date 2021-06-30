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
import tnt.npse.entities.Status;
import tnt.npse.util.LazyStatusDataSorter;


/**
 *
 * @author NN
 */
public class LazyStatusDataModel extends LazyDataModel<Status> {

    private List<Status> allStatuses=new ArrayList<>();

    public LazyStatusDataModel(List<Status> allStatuses) {
        this.allStatuses=allStatuses;
    }

    @Override
    public Status getRowData(String rowKey) {
        for(Status status : allStatuses) {
            if (status.getStatusId() == Long.parseLong(rowKey))
                return status;
        }
        return null;
    }

    @Override
    public String getRowKey(Status status) {
        return String.valueOf(status.getStatusId());
    }

    @Override
    public List<Status> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
        
        List<Status> data = new ArrayList<>();
        
        //u listu data se ubacuju knjige iz pocetne liste svih knjiga koje zadovoljavaju filtere
        for (Status k : allStatuses) {
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
                Collections.sort(data, new LazyStatusDataSorter(meta.getSortField(), meta.getSortOrder()));
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

    public List<Status> getAllStatuses() {
        return allStatuses;
    }

    public void setAllStatuses(List<Status> allStatuses) {
        this.allStatuses = allStatuses;
    }

    
}
