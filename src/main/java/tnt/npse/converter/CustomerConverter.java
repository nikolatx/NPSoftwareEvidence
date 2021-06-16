/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.converter;

import java.util.HashMap;
import java.util.Map;
import javax.el.ValueExpression;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import tnt.npse.entities.Customer;
import tnt.npse.viewbeans.SellMenuView;

/**
 *
 * @author NN
 */

@FacesConverter(value="tnt.npse.converter.LicenseConverter")
public class CustomerConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value==null || value.trim().isEmpty()) 
           return null;
        
        ValueExpression vex=context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{sellMenuView}", SellMenuView.class);
        SellMenuView sellMenuView=(SellMenuView)vex.getValue(context.getELContext());
        return sellMenuView.getCustomers().stream().filter(e->e.getName().equalsIgnoreCase(value));
    }

    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        if (o==null || o.equals("")) 
            return "";
        return String.valueOf(((Customer) o).getName());
    }
    
}
