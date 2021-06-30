/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.util;

import java.lang.reflect.Field;
import java.util.Comparator;
import org.primefaces.model.SortOrder;
import tnt.npse.entities.Customer;

/**
 *
 * @author NN
 */
public class LazyCustomerDataSorter  implements Comparator<Customer> {

    private String sortField;
    private SortOrder sortOrder;

    public LazyCustomerDataSorter(String sortField, SortOrder sortOrder) {
        this.sortField = sortField;
        this.sortOrder = sortOrder;
    }
    
    @Override
    public int compare(Customer customer1, Customer customer2) {
        try {
            Field field1=customer1.getClass().getDeclaredField(sortField);
            field1.setAccessible(true);
            Object value1=field1.get(customer1);
            Field field2=customer2.getClass().getDeclaredField(sortField);
            field2.setAccessible(true);

            Object value2=field2.get(customer2);
            
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
    
    
    
    
}
