/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dimm.home.Panels;

import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.Rendering.HelpCaller;
import dimm.home.ServerConnect.ConnectionID;
import dimm.home.ServerConnect.ServerCall;
import dimm.home.ServerConnect.StatementID;
import dimm.home.UserMain;
import dimm.home.Utilities.CmdExecutor;
import dimm.home.native_libs.NativeLoader;

/**
 *
 * @author mw
 */
public abstract class GenericEditPanel extends GlossDialogPanel implements HelpCaller
{

    protected int row;
    private static boolean needs_init;

    protected abstract void set_object_props();

    protected abstract boolean check_changed();

    protected abstract boolean is_plausible();

    protected abstract boolean is_new();

    public static boolean needs_init()
    {
        return needs_init;
    }

    public static void set_needs_init( boolean needs_init )
    {
        GenericEditPanel.needs_init = needs_init;
    }

    protected boolean update_db( Object object, Object old_object )
    {
        set_object_props();

        String object_name = object.getClass().getSimpleName();




        boolean okay = false;
        ServerCall sql = null;
        ConnectionID cid = null;
        StatementID sta = null;
        try
        {
            sql = UserMain.sqc().get_sqc();
            cid = sql.open();
            sta = sql.createStatement(cid);

            okay = sql.Update(sta, object, old_object);

        }
        catch (Exception e)
        {
            UserMain.errm_ok(my_dlg, UserMain.Txt("Exception") + " " + object_name + " " + e.getLocalizedMessage());
        }
        finally
        {
            if (sta != null)
            {
                sql.close(sta);
            }
            if (cid != null)
            {
                sql.close(cid);
            }
        }
        if (!okay)
        {
            UserMain.errm_ok(my_dlg, UserMain.Txt("Cannot_update") + " " + object_name + " " + sql.get_last_err());
            return false;
        }
        set_needs_init(true);

        return true;
    }

    protected boolean insert_db( Object object )
    {
        set_object_props();

        String object_name = object.getClass().getSimpleName();

        ServerCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open();
        StatementID sta = sql.createStatement(cid);

        boolean okay = sql.Insert(sta, object);

        sql.close(sta);
        sql.close(cid);

        if (!okay)
        {
            UserMain.errm_ok(my_dlg, UserMain.Txt("Cannot_insert") + " " + object_name + " " + sql.get_last_err());
            return false;
        }
        set_needs_init(true);

        return true;

    }

    protected void ok_action( Object object, Object old_object )
    {
        boolean ok = save_action(object, old_object);

        if (ok)
        {
            this.setVisible(false);
        }

    }

    protected boolean save_action( Object object, Object old_object )
    {
        boolean ok = true;

        if (!is_plausible())
        {
            return false;
        }

        if (is_new())
        {
            if (!UserMain.self.is_admin())
            {
                UserMain.errm_ok(my_dlg, UserMain.Txt("Sie_duerfen_keine_Änderungen_vornehmen"));
                return false;
            }

            ok = insert_db(object);
            this.firePropertyChange("REBUILD", null, null);
        }
        else
        {
            if (check_changed())
            {
                if (!UserMain.self.is_admin())
                {
                    UserMain.errm_ok(my_dlg, UserMain.Txt("Sie_duerfen_keine_Änderungen_vornehmen"));
                    return false;
                }
                if (UserMain.info_ok_cancel(UserMain.WANT_DB_CHANGE_TXT, this.getLocationOnScreen()))
                {
                    ok = update_db(object, old_object);
                    this.firePropertyChange("REBUILD", null, null);
                }
            }
        }
        if (ok)
        {
            UserMain.sqc().rebuild_result_array(object.getClass());
        }

        return ok;

    }

    protected void abort_action()
    {
        this.setVisible(false);
    }

    @Override
    public void open_help( String class_name )
    {
        UserMain.open_help_panel( class_name );

    }

   
}
