/*
 * NewJDialog.java
 *
 * Created on 20. März 2008, 22:44
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


class DiskSpaceTableModel extends OverviewModel
{
    
    public DiskSpaceTableModel(UserMain _main, DiskSpaceOverview _dlg)
    {
        super( _main, _dlg );

        String[] _col_names = {"Id",UserMain.getString("Name"), UserMain.getString("Capacity"), UserMain.getString("Mode"), UserMain.getString("Status"), UserMain.getString("Disabled"), UserMain.getString("Bearbeiten"), UserMain.getString("Löschen")};
        Class[] _col_classes = {String.class,  String.class,  String.class,  String.class,  String.class,  Boolean.class, JButton.class, JButton.class};
        set_columns( _col_names, _col_classes );

    }

    @Override
    public String get_qry(long da_id)
    {
        String qry = "select * from disk_space where da_id=" + da_id + " order by id";
        return qry;
    }

    String get_type_str( String type)
    {
        DiskSpaceOverview mo_dlg = (DiskSpaceOverview)dlg;

        for (int i = 0; i < mo_dlg.get_ds_entry_list().length; i++)
        {
            DiskSpaceOverview.DiskSpaceTypeEntry mte = mo_dlg.get_ds_entry_list()[i];
            if (mte.type.compareTo(type)== 0)
            {
                return mte.name;
            }
        }
        return "unknown";
    }
    String get_mode_str( int flags )
    {

        String ret = "";

        if ((flags & CS_Constants.DS_MODE_BOTH) == CS_Constants.DS_MODE_BOTH)
        {
            ret = "Data & Index";
        }
        else if ((flags & CS_Constants.DS_MODE_DATA) == CS_Constants.DS_MODE_DATA)
        {
            ret = "Data";
        }
        else if ((flags & CS_Constants.DS_MODE_INDEX) == CS_Constants.DS_MODE_INDEX)
        {
            ret = "Index";
        }

        return ret;
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (sqlResult == null)
            return null;

        DiskSpace ds = (DiskSpace)sqlResult.get(rowIndex);

        switch (columnIndex)
        {
            case 0:
                return ds.getId(); // ID
            case 1:
                return ds.getPath();
            case 2:
                return ds.getMaxCapacity();
            case 3:
                return get_mode_str( sqlResult.getInt(rowIndex, "Flags") );
            case 4:
                return get_type_str( ds.getStatus());
            case 5:
                int flags = sqlResult.getInt(rowIndex, "Flags");
                return new Boolean((flags & CS_Constants.DS_DISABLED) == CS_Constants.DS_DISABLED); // DISABLED
            default:
                return super.getValueAt(rowIndex, columnIndex);
        }
    }
    public DiskSpace get_object( int index )
    {
        return (DiskSpace) sqlResult.get(index);
    }


}
/**
 *
 * @author  mw
 */
public class DiskSpaceOverview extends SQLOverviewDialog
{


    DiskArchive da;
    public static final int DISABLED =   0x01;

    public class DiskSpaceTypeEntry
    {
        String type;
        String name;

        DiskSpaceTypeEntry( String t, String n)
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

    DiskSpaceTypeEntry[] ds_entry_list =
    {
        new DiskSpaceTypeEntry(CS_Constants.DS_EMPTY, UserMain.Txt("Empty") ),
        new DiskSpaceTypeEntry(CS_Constants.DS_DATA, UserMain.Txt("In use") ),
        new DiskSpaceTypeEntry(CS_Constants.DS_FULL, UserMain.Txt("Full") ),
        new DiskSpaceTypeEntry(CS_Constants.DS_ERROR, UserMain.Txt("Error") ),
        new DiskSpaceTypeEntry(CS_Constants.DS_OFFLINE, UserMain.Txt("Offline") )
    };
    public DiskSpaceTypeEntry[] get_ds_entry_list()
    {
        return ds_entry_list;
    }


    /** Creates new form NewJDialog */
    public DiskSpaceOverview(UserMain parent, DiskArchive _da, boolean modal)
    {
        super(parent, "path", modal);
        da = _da;

        initComponents();

        main = parent;

        TitlePanel titlePanel = new TitlePanel(this);
        PN_TITLE.add(titlePanel);
        titlePanel.installListeners();

        model = new DiskSpaceTableModel(main, this);
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

    DiskSpaceTableModel get_object_model()
    {
        return (DiskSpaceTableModel) model;
    }



    @Override
    public void set_table_header()
    {
        TableColumnModel cm = table.getTableHeader().getColumnModel();
        cm.getColumn(0).setMinWidth(40);
        cm.getColumn(0).setMaxWidth(40);
        cm.getColumn(1).setPreferredWidth(100);
        cm.getColumn(3).setPreferredWidth(80);
        cm.getColumn(4).setPreferredWidth(60);
        cm.getColumn(5).setPreferredWidth(40);

        model.set_table_header(cm);
    }


    @Override
    public void gather_sql_result()
    {
        gather_sql_result( da.getId() );
    }

    @Override
    public void gather_sql_result(long da_id)
    {
        ServerCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open();

        if (!check_valid_cid( sql, cid ))
            return;

         StatementID sid = sql.createStatement(cid);

        String qry =  model.get_qry( da_id );

        ResultSetID rid = sql.executeQuery(sid, qry);
        SQLArrayResult resa = sql.get_sql_array_result(rid);

        SQLResult<DiskSpace>  res = new SQLResult<DiskSpace>(UserMain.sqc(), resa, new DiskSpace().getClass());

        model.setSqlResult(res);
        table.tableChanged(new TableModelEvent(table.getModel()) );
        set_tabel_row_height();

        sql.close(cid);



    }



    

    @Override
    protected GlossDialogPanel get_edit_panel( int row )
    {
        return new EditDiskSpace( da, row, this );
    }



    


}
