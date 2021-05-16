/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.util;

import java.util.Comparator;
import org.primefaces.model.SortOrder;
import tnt.npse.entities.License;

/**
 *
 * @author NN
 */

public class LazyLicenseSorter implements Comparator<License> {

    private String sortField;
    private SortOrder sortOrder;

    public LazyLicenseSorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }
    
    @Override
    public int compare(License license1, License license2) {
        try {
            Object value1 = license1.getClass().getField(sortField).get(license1);
            Object value2 = license2.getClass().getField(sortField).get(license2);
            int value = ((Comparable)value1).compareTo(value2);
            return SortOrder.ASCENDING.equals(sortOrder) ? value : -1 * value;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
    
}
