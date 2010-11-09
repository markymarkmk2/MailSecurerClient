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

import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.ServerConnect.ConnectionID;
import dimm.home.ServerConnect.ResultSetID;
import dimm.home.ServerConnect.ServerCall;
import dimm.home.ServerConnect.StatementID;
import home.shared.CS_Constants;
import home.shared.SQL.SQLArrayResult;
import home.shared.hibernate.SmtpServer;


class SmtpListenerTableModel extends OverviewModel
{
    
    public SmtpListenerTableModel(UserMain _main, SmtpListenerOverview _dlg)
    {
        super( _main, _dlg );

        String[] _col_names = {"Id",UserMain.getString("Server"), UserMain.getString("Disabled"), UserMain.getString("Bearbeiten"), UserMain.getString("Löschen")};
        Class[] _col_classes = {String.class,  String.class,  Boolean.class, JButton.class, JButton.class};
        set_columns( _col_names, _col_classes );

    }

    @Override
    public String get_qry(long mandanten_id)
    {
        String qry = "select * from smtp_server where mid=" + mandanten_id + " order by id";
        return qry;
    }



    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (sqlResult == null)
            return null;

        SmtpServer smtp_server = (SmtpServer)sqlResult.get(rowIndex);

        switch (columnIndex)
        {
            case 0:
                return smtp_server.getId(); // ID
            case 1:
            {
                if (smtp_server.getServer().length()> 0)
                      return smtp_server.getServer() + ":" + smtp_server.getPort();
                return  "*:" + smtp_server.getPort();
            }
            case 2:
                int flags = sqlResult.getInt(rowIndex, "Flags");
                return (flags & CS_Constants.SL_DISABLED) == CS_Constants.SL_DISABLED; // DISABLED
            default:
                return super.getValueAt(rowIndex, columnIndex);
        }
    }
    public SmtpServer get_object( int index )
    {
        return (SmtpServer) sqlResult.get(index);
    }


}
/**
 *
 * @author  mw
 */
public class SmtpListenerOverview extends SQLOverviewDialog implements PropertyChangeListener
{
    


    /** Creates new form NewJDialog */
    public SmtpListenerOverview(UserMain parent, boolean modal)
    {
        super(parent, "server", modal);
        initComponents();

        main = parent;

        TitlePanel titlePanel = new TitlePanel(this);
        PN_TITLE.add(titlePanel);
        titlePanel.installListeners();

        model = new SmtpListenerTableModel(main, this);
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

    SmtpListenerTableModel get_object_model()
    {
        return (SmtpListenerTableModel) model;
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

        SQLResult<SmtpServer>  res = new SQLResult<SmtpServer>(UserMain.sqc(), resa, new SmtpServer().getClass());

        model.setSqlResult(res);
        table.tableChanged(new TableModelEvent(table.getModel()) );
        set_tabel_row_height();

        sql.close(cid);



    }



    @Override
    protected GlossDialogPanel get_edit_panel( int row )
    {
        return new EditSmtpListener( row, this );
    }




    
}
