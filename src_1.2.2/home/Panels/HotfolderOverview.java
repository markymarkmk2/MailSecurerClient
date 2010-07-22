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


class HotfolderTableModel extends OverviewModel
{


    public HotfolderTableModel(UserMain _main, HotfolderOverview dlg)
    {
        super( _main, dlg );

        String[] _col_names = {"ID",UserMain.getString("Path"), UserMain.getString("EMail"), UserMain.getString("Disabled"), UserMain.getString("Bearbeiten"), UserMain.getString("Löschen")};
        Class[] _col_classes = {String.class,  String.class,  String.class,  Boolean.class, JButton.class, JButton.class};
        set_columns( _col_names, _col_classes );

    }

    @Override
    public String get_qry(long mandanten_id)
    {
        String qry = "select * from Hotfolder where mid=" + mandanten_id + " order by id";
        return qry;
    }



    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (sqlResult == null)
            return null;

        Hotfolder hf = new Hotfolder();
        hf = (Hotfolder)sqlResult.get(rowIndex);

        switch (columnIndex)
        {
            case 0:
                return hf.getId(); // ID
            case 1:
                return hf.getPath(); // NAME
            case 2:
                return hf.getUsermailadress(); // LOGINNAME
            case 3:
                int flags = sqlResult.getInt(rowIndex, "Flags");
                return new Boolean((flags & CS_Constants.HF_FLAG_DISABLED) == CS_Constants.HF_FLAG_DISABLED); // DISABLED
            default:
                return super.getValueAt(rowIndex, columnIndex);
        }
    }
    public Hotfolder get_object( int index )
    {
        return (Hotfolder) sqlResult.get(index);
    }


}
/**
 *
 * @author  mw
 */
public class HotfolderOverview extends SQLOverviewDialog implements PropertyChangeListener
{


    


    /** Creates new form NewJDialog */
    public HotfolderOverview(UserMain parent, boolean modal)
    {
        super(parent, "path", modal);
        initComponents();

        main = parent;

        TitlePanel titlePanel = new TitlePanel(this);
        PN_TITLE.add(titlePanel);
        titlePanel.installListeners();

        model = new HotfolderTableModel(main, this);
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

    HotfolderTableModel get_object_model()
    {
        return (HotfolderTableModel) model;
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

        SQLResult<Hotfolder>  res = new SQLResult<Hotfolder>(UserMain.sqc(), resa, new Hotfolder().getClass());

        model.setSqlResult(res);
        table.tableChanged(new TableModelEvent(table.getModel()) );
        set_tabel_row_height();

        sql.close(cid);



    }



    @Override
    protected GlossDialogPanel get_edit_panel( int row )
    {
        return new EditHotfolder( row, this );
    }

   
}
