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
import java.beans.PropertyChangeListener;
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
import home.shared.CS_Constants.USERMODE;
import home.shared.SQL.SQLArrayResult;


class MandantTableModel extends OverviewModel
{
    
    public MandantTableModel(UserMain _main, MandantOverview _dlg)
    {
        super( _main, _dlg );

        String[] _col_names = {"Id",UserMain.getString("Name"), /*UserMain.getString("Lizenz"),*/ UserMain.getString("IMAP"), UserMain.getString("Disabled"), UserMain.getString("Bearbeiten"), UserMain.getString("Löschen")};
        Class[] _col_classes = {String.class,  String.class,  /*String.class, */ Boolean.class,  Boolean.class, JButton.class, JButton.class};
        set_columns( _col_names, _col_classes );

    }

    @Override
    public String get_qry(long l)
    {
        String qry = "select * from mandant order by id";
        return qry;
    }

 /*   String get_license_str( String type)
    {
        MandantOverview mo_dlg = (MandantOverview)dlg;

        for (int i = 0; i < mo_dlg.get_ml_entry_list().length; i++)
        {
            MandantOverview.MandantLicenseEntry mle = mo_dlg.get_ml_entry_list()[i];
            if (mle.type.indexOf(type) >= 0)
            {
                return mle.name;
            }
        }
        return "unknown";
    }

*/
    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (sqlResult == null)
            return null;

        Mandant mandant = new Mandant();
        mandant = (Mandant)sqlResult.get(rowIndex);

        switch (columnIndex)
        {
            case 0:
                return mandant.getId(); // ID
            case 1:
                return mandant.getName();
            /*case 2:
                return get_license_str( mandant.getLicense());*/
            case 2:
                return new Boolean(mandant.getImap_port() != 0);
            case 3:
                int flags = sqlResult.getInt(rowIndex, "Flags");
                return new Boolean((flags & CS_Constants.MA_DISABLED) == CS_Constants.MA_DISABLED); // DISABLED
            default:
                return super.getValueAt(rowIndex, columnIndex);
        }
    }
    @Override
    public int getColumnCount()
    {

        // EDIT IST 2.LAST ROW!!!!
        if (UserMain.self.getUserLevel() != USERMODE.UL_SYSADMIN)
            return col_names.length - 2;


        return col_names.length;
    }
    public Mandant get_object( int index )
    {
        return (Mandant) sqlResult.get(index);
    }


}
/**
 *
 * @author  mw
 */
public class MandantOverview extends SQLOverviewDialog implements PropertyChangeListener
{
    

    public class MandantLicenseEntry
    {
        String type;
        String name;

        MandantLicenseEntry( String t, String n)
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





    /** Creates new form NewJDialog */
    public MandantOverview(UserMain parent, boolean modal)
    {
        super(parent, "name", modal);
        initComponents();

        main = parent;

        TitlePanel titlePanel = new TitlePanel(this);
        PN_TITLE.add(titlePanel);
        titlePanel.installListeners();

        model = new MandantTableModel(main, this);
        table = new GlossTable();

        table.setModel(model);
        table.addMouseListener(this);

        // REGISTER TABLE TO SCROLLPANEL
        table.embed_to_scrollpanel( SCP_TABLE );

        if (UserMain.self.getUserLevel() != USERMODE.UL_SYSADMIN)
            this.BT_NEW.setVisible(false);

        pack();

        create_sql_worker();
    }

    MandantTableModel get_object_model()
    {
        return (MandantTableModel) model;
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
        gather_sql_result( -1 );
    }

    @Override
    public void gather_sql_result(long not_needed_id)
    {
        ServerCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open();

        if (!check_valid_cid( sql, cid ))
            return;

         StatementID sid = sql.createStatement(cid);

        String qry =  model.get_qry( not_needed_id );

        ResultSetID rid = sql.executeQuery(sid, qry);
        SQLArrayResult resa = sql.get_sql_array_result(rid);

        SQLResult<Mandant>  res = new SQLResult<Mandant>(UserMain.sqc(), resa, new Mandant().getClass());

        model.setSqlResult(res);
        table.tableChanged(new TableModelEvent(table.getModel()) );
        set_tabel_row_height();

        sql.close(cid);



    }

    @Override
    protected boolean del_object( int row )
    {
        Mandant m = get_object_model().get_object( row );

        ServerCall sql = UserMain.sqc().get_sqc();
        boolean okay = sql.DeleteObject( m);
        if (okay)
            set_needs_init( true );

        return okay;

    }




    @Override
    protected GlossDialogPanel get_edit_panel( int row )
    {
        return new EditMandant( row, this );
    }



    
}
