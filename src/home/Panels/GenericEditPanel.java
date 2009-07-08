/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Panels;

import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.ServerConnect.ConnectionID;
import dimm.home.ServerConnect.SQLCall;
import dimm.home.ServerConnect.StatementID;
import dimm.home.UserMain;

/**
 *
 * @author mw
 */
public abstract class GenericEditPanel extends GlossDialogPanel
{
    protected int row;

    protected abstract void set_object_props();
    protected abstract boolean check_changed();
    protected abstract boolean is_plausible();
    protected abstract boolean is_new();

    protected boolean update_db(Object object)
    {
        set_object_props();

        String object_name = object.getClass().getSimpleName();


        SQLCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open();
        StatementID sta = sql.createStatement(cid);

        boolean okay = sql.Update( sta, object );

        sql.close(sta);
        sql.close(cid);

        if (!okay)
        {
            UserMain.errm_ok(my_dlg, UserMain.Txt("Cannot_update") + " " + object_name + " " +sql.get_last_err());
            return false;
        }

        return true;
    }

    protected boolean insert_db(Object object)
    {
        set_object_props();

        String object_name = object.getClass().getSimpleName();

        SQLCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open();
        StatementID sta = sql.createStatement(cid);

        boolean okay = sql.Insert( sta, object );

        sql.close(sta);
        sql.close(cid);

        if (!okay)
        {
            UserMain.errm_ok(my_dlg, UserMain.Txt("Cannot_insert") + " " + object_name + " " + sql.get_last_err());
            return false;
        }

        return true;

    }

    protected void ok_action(Object object)
    {
        boolean ok = true;

        if (!is_plausible())
            return;

        if (is_new())
        {
            ok = insert_db(object);
            this.firePropertyChange("REBUILD", null, null);
        }
        else
        {
            if (check_changed())
            {
                if (UserMain.info_ok_cancel(UserMain.WANT_DB_CHANGE_TXT, this.getLocationOnScreen()))
                {
                    ok = update_db(object);
                    this.firePropertyChange("REBUILD", null, null);
                }
            }
        }
        if (ok)
            this.setVisible(false);

    }

    protected void abort_action()
    {
        this.setVisible(false);
    }

}
