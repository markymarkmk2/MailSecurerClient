/*
 * NewJDialog.java
 *
 * Created on 20. M�rz 2008, 22:44
 */
package dimm.home.Panels;

import home.shared.SQL.SQLResult;
import dimm.home.Models.OverviewModel;
import dimm.home.Rendering.GlossTable;
import dimm.home.Rendering.TitlePanel;
import dimm.home.Rendering.SQLOverviewDialog;
import dimm.home.UserMain;
import javax.swing.JButton;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumnModel;

import home.shared.hibernate.*;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.ServerConnect.ConnectionID;
import dimm.home.ServerConnect.ResultSetID;
import dimm.home.ServerConnect.ServerCall;
import dimm.home.ServerConnect.StatementID;
import home.shared.CS_Constants;
import home.shared.SQL.SQLArrayResult;


class DATableModel extends OverviewModel
{
    
    public DATableModel(UserMain _main, DAOverview _dlg)
    {
        super( _main, _dlg );

        String[] _col_names = {"Id",UserMain.getString("Name"), UserMain.getString("Disabled"), UserMain.getString("Bearbeiten"), UserMain.getString("Löschen")};
        Class[] _col_classes = {String.class,  String.class,  Boolean.class, JButton.class, JButton.class};
        set_columns( _col_names, _col_classes );

    }

    @Override
    public String get_qry(long mandanten_id)
    {
        String qry = "select * from disk_archive where mid=" + mandanten_id + " order by id";
        return qry;
    }

    String get_type_str( String type)
    {
        DAOverview mo_dlg = (DAOverview)dlg;

        for (int i = 0; i < mo_dlg.get_da_entry_list().length; i++)
        {
            DAOverview.DATypeEntry mte = mo_dlg.get_da_entry_list()[i];
            if (mte.type.compareTo(type)== 0)
            {
                return mte.name;
            }
        }
        return "unknown";
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (sqlResult == null)
            return null;

        DiskArchive da = new DiskArchive();
        da = (DiskArchive)sqlResult.get(rowIndex);

        switch (columnIndex)
        {
            case 0:
                return da.getId(); // ID
            case 1:
                return da.getName();
            case 2:
                int flags = sqlResult.getInt(rowIndex, "Flags");
                return new Boolean((flags & CS_Constants.DA_DISABLED) == CS_Constants.DA_DISABLED); // DISABLED
            default:
                return super.getValueAt(rowIndex, columnIndex);
        }
    }
    public DiskArchive get_object( int index )
    {
        return (DiskArchive) sqlResult.get(index);
    }


}
/**
 *
 * @author  mw
 */
public class DAOverview extends SQLOverviewDialog
{

 public static final int DISABLED =   0x01;

    public class DATypeEntry
    {
        String type;
        String name;

        DATypeEntry( String t, String n)
        {
            type = t;
            name = n;
        }

        @Override
        public String toString()
        {
            return name;
        }
    }

    DATypeEntry[] da_entry_list =
    {
        new DATypeEntry("diskarchive","DiskArchive"),
    };
    public DATypeEntry[] get_da_entry_list()
    {
        return da_entry_list;
    }


    /** Creates new form NewJDialog */
    public DAOverview(UserMain parent, boolean modal)
    {
        super(parent, "name", modal);
        initComponents();

        main = parent;

        TitlePanel titlePanel = new TitlePanel(this);
        PN_TITLE.add(titlePanel);
        titlePanel.installListeners();

        model = new DATableModel(main, this);
        table = new GlossTable();

        table.setModel(model);
        table.addMouseListener(this);

        // REGISTER TABLE TO SCROLLPANEL
        table.embed_to_scrollpanel( SCP_TABLE );

        if (!UserMain.self.is_admin())
            this.BT_NEW.setVisible(false);

        pack();

        create_sql_worker();
    }

    DATableModel get_object_model()
    {
        return (DATableModel) model;
    }



    @Override
    public void set_table_header()
    {
        TableColumnModel cm = table.getTableHeader().getColumnModel();
        cm.getColumn(0).setMinWidth(40);
        cm.getColumn(0).setMaxWidth(40);
        cm.getColumn(1).setPreferredWidth(150);

        model.set_table_header(cm);
    }


    @Override
    public void gather_sql_result()
    {
        gather_sql_result( main.get_firmen_id() );
    }

    @Override
    public void gather_sql_result(long firmen_id)
    {
        ServerCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open();
        
        if (!check_valid_cid( sql, cid ))
            return;

        StatementID sid = sql.createStatement(cid);

        String qry =  model.get_qry( firmen_id );

        ResultSetID rid = sql.executeQuery(sid, qry);
        SQLArrayResult resa = sql.get_sql_array_result(rid);

        SQLResult<DiskArchive>  res = new SQLResult<DiskArchive>(UserMain.sqc(), resa, new DiskArchive().getClass());

        model.setSqlResult(res);
        table.tableChanged(new TableModelEvent(table.getModel()) );
        set_tabel_row_height();

        sql.close(cid);

    }




    @Override
    protected GlossDialogPanel get_edit_panel( int row )
    {
        return new EditDA( row, this );
    }



    boolean check_del_constraints(int row)
    {
        ConnectionID cid = null;
        boolean ret = true;
        DiskArchive da = (DiskArchive)model.getSqlResult().get(row);
        int id = da.getId();

        ServerCall sql = UserMain.sqc().get_sqc();

        try
        {
            cid = sql.open();

            String name = sql.GetFirstSqlField( cid, "select path from disk_space where da_id=" + id, 0);
            if (name != null)
            {
                UserMain.errm_ok(UserMain.Txt("Dieses_Diskarchiv_verwaltet noch DiskSpace: " + name));
                ret = false;
            }
            name = sql.GetFirstSqlField( cid, "select path from hotfolder where da_id=" + id, 0);
            if (name != null)
            {
                UserMain.errm_ok(UserMain.Txt("Dieses_Diskarchiv_wird_noch_von_einem_Hotfolder_verwendet: " + name));
                ret = false;
            }

            name = sql.GetFirstSqlField( cid, "select server from imap_fetcher where da_id=" + id, 0);
            if (name != null)
            {
                UserMain.errm_ok(UserMain.Txt("Dieses_Diskarchiv_wird_noch_von_einem_IMAP-Connect_verwendet: " + name));
                ret = false;
            }

            name = sql.GetFirstSqlField( cid, "select local_server from proxy where da_id=" + id, 0);
            if (name != null)
            {
                UserMain.errm_ok(UserMain.Txt("Dieses_Diskarchiv_wird_noch_von_einem_Proxy-Connect_verwendet: " + name));
                ret = false;
            }

            name = sql.GetFirstSqlField( cid, "select in_server from milter where da_id=" + id, 0);
            if (name != null)
            {
                UserMain.errm_ok(UserMain.Txt("Dieses_Diskarchiv_wird_noch_von_einem_SMTP-Connect_verwendet: " + name));
                ret = false;
            }
        }
        catch (Exception e)
        {
            ret = false;
        }
        finally
        {
            sql.close(cid);
        }

       return ret;

    }
    @Override
    protected boolean del_object( int row )
    {
        if (!check_del_constraints(row))
            return false;

        boolean ok = super.del_object(row);

        // IF SOMETHING HAS BEEN DELETED, WE REBUILD OUR GLOBAL DA-LIST
        if (ok)
        {
            UserMain.sqc().rebuild_da_array();
        }
        return ok;

    }

}
