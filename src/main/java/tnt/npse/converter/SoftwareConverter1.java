/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.converter;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.el.ValueExpression;
import javax.enterprise.context.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import tnt.npse.entities.Software;
import tnt.npse.viewbeans.SellMenuView;

/**
 *
 * @author NN
 */
@ManagedBean
@ViewScoped
@FacesConverter(value="SoftwareConverter1")
public class SoftwareConverter1 implements Converter, Serializable {

    private Map<String, Object> map;
    
    @PostConstruct
    public void init(){
        map=new HashMap<>();
    }
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        if (value==null || value.trim().isEmpty())
            return null;
        
        /*ValueExpression vex=context.getApplication().getExpressionFactory()
                .createValueExpression(context.getELContext(), "#{sellMenuView}", SellMenuView.class);
        SellMenuView sellMenuView=(SellMenuView)vex.getValue(context.getELContext());
        return sellMenuView.getAllsoftware().stream().filter(e->e.getName().equalsIgnoreCase(value));
        */
        return map.get(value);
    }
    


    @Override
    public String getAsString(FacesContext fc, UIComponent uic, Object o) {
        String selectItemValue=String.valueOf(o.hashCode());
        map.put(selectItemValue, o);
        return selectItemValue;
        
        /*
        if (o == null || o.equals("")) 
            return "";
        String name=String.valueOf(((Software)o).getName());
        System.out.println(name);
        return name;
        */
    }

}