/*
 * NewJDialog.java
 *
 * Created on 20. M�rz 2008, 22:44
 */
package dimm.home.Panels;

import home.shared.SQL.SQLResult;
import dimm.home.Models.OverviewModel;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossPanel;
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
import home.shared.SQL.SQLArrayResult;


class BackupTableModel extends OverviewModel
{
    
    public BackupTableModel(UserMain _main, BackupOverview _dlg)
    {
        super( _main, _dlg );

        String[] _col_names = {"Id",UserMain.getString("Server"), UserMain.getString("Typ"), UserMain.getString("System"), UserMain.getString("Disabled"), UserMain.getString("Bearbeiten"), UserMain.getString("Löschen")};
        Class[] _col_classes = {String.class,  String.class,  String.class,  Boolean.class, Boolean.class, JButton.class, JButton.class};
        set_columns( _col_names, _col_classes );

    }

    @Override
    public String get_qry(long mandanten_id)
    {
        String qry = "select * from backup where mid=" + mandanten_id + " order by id";
        return qry;
    }

   

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (sqlResult == null)
            return null;

        Backup ba = (Backup)sqlResult.get(rowIndex);

        int flags = 0;
        try
        {
            flags = Integer.parseInt(ba.getFlags());
        }
        catch (Exception numberFormatException)
        {
        }
        switch (columnIndex)
        {
            case 0:
                return ba.getId(); // ID
            case 1:
                return ba.getAgentip() + ":" + ba.getAgentport().toString();
            case 2:
                boolean cycle = ((flags & CS_Constants.BACK_CYCLE) == CS_Constants.BACK_CYCLE);
                return cycle ? UserMain.Txt("Cycle") : UserMain.Txt("Schedule");
            case 3:
                return new Boolean((flags & CS_Constants.BACK_SYS) == CS_Constants.BACK_SYS);
            case 4:
                return new Boolean((flags & CS_Constants.BACK_DISABLED) == CS_Constants.BACK_DISABLED); // DISABLED
            default:
                return super.getValueAt(rowIndex, columnIndex);
        }
    }
     public Backup get_object( int index )
    {
        return (Backup) sqlResult.get(index);
    }

}
/**
 *
 * @author  mw
 */
public class BackupOverview extends SQLOverviewDialog implements PropertyChangeListener
{
    


    /** Creates new form NewJDialog */
    public BackupOverview(UserMain parent, boolean modal)
    {
        super(parent, "agentip", modal);
        initComponents();

        main = parent;

        TitlePanel titlePanel = new TitlePanel(this);
        PN_TITLE.add(titlePanel);
        titlePanel.installListeners();

        model = new BackupTableModel(main, this);
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

    BackupTableModel get_object_model()
    {
        return (BackupTableModel) model;
    }



    @Override
    public void set_table_header()
    {
        TableColumnModel cm = table.getTableHeader().getColumnModel();
        cm.getColumn(0).setMinWidth(40);
        cm.getColumn(0).setMaxWidth(40);
        cm.getColumn(1).setPreferredWidth(150);
        cm.getColumn(2).setPreferredWidth(60);
        cm.getColumn(3).setPreferredWidth(40);
        cm.getColumn(4).setPreferredWidth(40);

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

        SQLResult<Backup>  res = new SQLResult<Backup>( UserMain.sqc(), resa, new Backup().getClass());

        model.setSqlResult(res);
        table.tableChanged(new TableModelEvent(table.getModel()) );
        set_tabel_row_height();

        sql.close(cid);
    }



    @Override
    protected GlossDialogPanel get_edit_panel( int row )
    {
        return new EditBackup( row, this );
    }


   


    
}
