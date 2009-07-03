/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Models;

import dimm.general.SQL.SQLResult;
import dimm.general.hibernate.DiskArchive;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataListener;

/**
 *
 * @author mw
 */
public class DiskArchiveComboModel implements ComboBoxModel
{
    SQLResult<DiskArchive> da_res;
    int act_da_id;

    public int get_act_id()
    {
        return act_da_id;
    }
    public void set_act_id(int i)
    {
        act_da_id = i;
    }
    public DiskArchive get_selected_da()
    {
        return da_res.get_obj_by_id(act_da_id);
    }

    public DiskArchiveComboModel( SQLResult<DiskArchive> da_res_ )
    {
        da_res = da_res_;
        act_da_id = -1;
    }

    public void setSelectedItem( Object anItem )
    {
        act_da_id = -1;
        if (anItem instanceof DiskArchive)
        {
            act_da_id = ((DiskArchive)anItem).getId();
        }
        if (anItem instanceof String)
        {
            for (int i = 0; i < da_res.size(); i++)
            {
                DiskArchive diskArchive = da_res.get(i);
                if (da_res.get(i).getName().compareTo((String)anItem) == 0)
                {
                    act_da_id = da_res.get(i).getId();
                    break;
                }
            }
        }
    }

    public Object getSelectedItem()
    {
        DiskArchive da = get_selected_da();
        if (da != null)
            return da.getName();
        return null;
    }

    public int getSize()
    {
        return da_res.size();
    }

    public Object getElementAt( int index )
    {
        return da_res.get(index).getName();
    }

    public void addListDataListener( ListDataListener l )
    {
    }

    public void removeListDataListener( ListDataListener l )
    {
    }
}