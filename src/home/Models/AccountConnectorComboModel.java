/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Models;

import home.shared.SQL.SQLResult;
import home.shared.hibernate.AccountConnector;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;
import home.shared.hibernate.DiskArchive;
/**
 *
 * @author mw
 */
public class AccountConnectorComboModel implements ComboBoxModel
{
    SQLResult<AccountConnector> ac_res;
    int act_da_id;

    public int get_act_id()
    {
        return act_da_id;
    }
    public void set_act_id(int i)
    {
        act_da_id = i;
    }
    public AccountConnector get_selected_ac()
    {
        return ac_res.get_obj_by_id(act_da_id);
    }

    public AccountConnectorComboModel( SQLResult<AccountConnector> da_res_ )
    {
        ac_res = da_res_;
        act_da_id = -1;
    }
    public String getName(AccountConnector ac)
    {
        return ac.getType() + "://" + ac.getIp();
    }


    @Override
    public void setSelectedItem( Object anItem )
    {
        act_da_id = -1;
        if (anItem instanceof DiskArchive)
        {
            act_da_id = ((DiskArchive)anItem).getId();
        }
        if (anItem instanceof String)
        {
            for (int i = 0; i < ac_res.size(); i++)
            {
                
                if (getName( ac_res.get(i)).compareTo((String)anItem) == 0)
                {
                    act_da_id = ac_res.get(i).getId();
                    break;
                }
            }
        }
    }

    @Override
    public Object getSelectedItem()
    {
        AccountConnector ac = get_selected_ac();
        if (ac != null)
            return getName(ac);
        return null;
    }

    @Override
    public int getSize()
    {
        return ac_res.size();
    }

    @Override
    public Object getElementAt( int index )
    {
        return getName( ac_res.get(index) );
    }

    @Override
    public void addListDataListener( ListDataListener l )
    {
    }

    @Override
    public void removeListDataListener( ListDataListener l )
    {
    }
}