/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tnt.npse.beans;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import tnt.npse.entities.Settings;

/**
 *
 * @author NN
 */
@Stateless
public class SettingsFacade extends AbstractFacade<Settings> {

    @PersistenceContext(unitName = "npsoftwareevidence")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SettingsFacade() {
        super(Settings.class);
    }
    
}
