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
import home.shared.SQL.SQLArrayResult;


class RoleTableModel extends OverviewModel
{


    public RoleTableModel(UserMain _main, RoleOverview dlg)
    {
        super( _main, dlg );

        String[] _col_names = {"ID",UserMain.getString("Name"), UserMain.getString("Filter"), UserMain.getString("Disabled"), UserMain.getString("Bearbeiten"), UserMain.getString("Löschen")};
        Class[] _col_classes = {String.class,  String.class,  String.class,  Boolean.class, JButton.class, JButton.class};
        set_columns( _col_names, _col_classes );

    }

    @Override
    public String get_qry(long mandanten_id)
    {
        String qry = "select * from role where mid=" + mandanten_id + " order by name";
        return qry;
    }




    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (sqlResult == null)
            return null;

        Role role = new Role();
        role = (Role)sqlResult.get(rowIndex);

        switch (columnIndex)
        {
            case 0:
                return role.getId(); // ID
            case 1:
                return role.getName(); // NAME
            case 2:
            {
                RoleOverview rdlg = (RoleOverview)dlg;                
                return rdlg.get_account_match_descr( role.getAccountmatch() ); // ACCOUNTFILTER
            }
            case 3:
                int flags = sqlResult.getInt(rowIndex, "Flags");
                return (flags & CS_Constants.ROLE_DISABLED) == CS_Constants.ROLE_DISABLED; // DISABLED
            default:
                return super.getValueAt(rowIndex, columnIndex);
        }
    }
    public Role get_object( int index )
    {
        return (Role) sqlResult.get(index);
    }

}
/**
 *
 * @author  mw
 */
public class RoleOverview extends SQLOverviewDialog implements PropertyChangeListener
{

    /** Creates new form NewJDialog */
    public RoleOverview(UserMain parent, boolean modal)
    {
        super(parent, "name", modal);
        initComponents();

        main = parent;

        TitlePanel titlePanel = new TitlePanel(this);
        PN_TITLE.add(titlePanel);
        titlePanel.installListeners();

        model = new RoleTableModel(main, this);
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

    RoleTableModel get_object_model()
    {
        return (RoleTableModel) model;
    }

    // DECODE DB-STRING TO HUMAN READABLE 
    String get_account_match_descr( String acm )
    {
        return LogicFilter.get_nice_filter_text( acm);
    }


    @Override
    public void set_table_header()
    {
        TableColumnModel cm = table.getTableHeader().getColumnModel();
        cm.getColumn(0).setMinWidth(40);
        cm.getColumn(0).setMaxWidth(40);
        cm.getColumn(1).setPreferredWidth(80);
        cm.getColumn(2).setPreferredWidth(150);

        // BT_DISABLED
        cm.getColumn(3).setMinWidth(60);
        cm.getColumn(3).setMaxWidth(60);

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

        SQLResult<Role>  res = new SQLResult<Role>(UserMain.sqc(), resa, new Role().getClass());

        model.setSqlResult(res);



        table.tableChanged(new TableModelEvent(table.getModel()) );
        set_tabel_row_height();

        sql.close(cid);



    }



    @Override
    protected GlossDialogPanel get_edit_panel( int row )
    {
        return new EditRole( row, this );
    }



   
}
