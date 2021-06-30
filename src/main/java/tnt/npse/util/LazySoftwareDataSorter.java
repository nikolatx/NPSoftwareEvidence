/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.util;

import java.lang.reflect.Field;
import java.util.Comparator;
import org.primefaces.model.SortOrder;
import tnt.npse.entities.Software;

/**
 *
 * @author NN
 */
public class LazySoftwareDataSorter implements Comparator<Software> {

    private String sortField;
    private SortOrder sortOrder;

    public LazySoftwareDataSorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }
    
    @Override
    public int compare(Software software1, Software software2) {
        try {
            Field field1=software1.getClass().getDeclaredField(sortField);
            field1.setAccessible(true);
            Object value1=field1.get(software1);
            Field field2=software2.getClass().getDeclaredField(sortField);
            field2.setAccessible(true);
            Object value2=field2.get(software2);
            int value=0;
            if (value1==null ^ value2==null)
                value=1;
            else if (value1==null && value2==null)
                value=0;
            else
                value = ((Comparable)value1).compareTo(value2);
            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    
    
    
}
