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


class ImapFetcherTableModel extends OverviewModel
{
    
    public ImapFetcherTableModel(UserMain _main, ImapFetcherOverview _dlg)
    {
        super( _main, _dlg );

        String[] _col_names = {"Id",UserMain.getString("Typ"), UserMain.getString("Server"), UserMain.getString("Disabled"), UserMain.getString("Bearbeiten"), UserMain.getString("Löschen")};
        Class[] _col_classes = {String.class,  String.class,  String.class,  Boolean.class, JButton.class, JButton.class};
        set_columns( _col_names, _col_classes );

    }

    @Override
    public String get_qry(long mandanten_id)
    {
        String qry = "select * from imap_fetcher where mid=" + mandanten_id + " order by id";
        return qry;
    }

    String get_type_str( String type)
    {
        ImapFetcherOverview mo_dlg = (ImapFetcherOverview)dlg;

        for (int i = 0; i < mo_dlg.get_mt_entry_list().length; i++)
        {
            ImapFetcherOverview.ImapFetcherTypeEntry mte = mo_dlg.get_mt_entry_list()[i];
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

        ImapFetcher imapfetcher = new ImapFetcher();
        imapfetcher = (ImapFetcher)sqlResult.get(rowIndex);

        switch (columnIndex)
        {
            case 0:
                return imapfetcher.getId(); // ID
            case 1:
                return get_type_str( imapfetcher.getType());
            case 2:
                return imapfetcher.getServer() + ":" + imapfetcher.getPort();
            case 3:
                int flags = sqlResult.getInt(rowIndex, "Flags");
                return new Boolean((flags & CS_Constants.IMF_DISABLED) == CS_Constants.IMF_DISABLED); // DISABLED
            default:
                return super.getValueAt(rowIndex, columnIndex);
        }
    }
    public ImapFetcher get_object( int index )
    {
        return (ImapFetcher) sqlResult.get(index);
    }


}
/**
 *
 * @author  mw
 */
public class ImapFetcherOverview extends SQLOverviewDialog implements PropertyChangeListener
{
    
    

    public class ImapFetcherTypeEntry
    {
        String type;
        String name;

        ImapFetcherTypeEntry( String t, String n)
        {
            type = t;
            name = n;
        }

        @Override
        public String toString()
        {
            return name;
        }

        String getProtocol()
        {
            return "imap";
        }
    }

    ImapFetcherTypeEntry[] mt_entry_list =
    {
        new ImapFetcherTypeEntry(CS_Constants.IFETCHER_TYPE_IMAP,"IMAP"),
        new ImapFetcherTypeEntry(CS_Constants.IFETCHER_TYPE_ENVELOPE,"Exchange Envelope")
    };
    public ImapFetcherTypeEntry[] get_mt_entry_list()
    {
        return mt_entry_list;
    }


    /** Creates new form NewJDialog */
    public ImapFetcherOverview(UserMain parent, boolean modal)
    {
        super(parent, "server", modal);
        initComponents();

        main = parent;

        TitlePanel titlePanel = new TitlePanel(this);
        PN_TITLE.add(titlePanel);
        titlePanel.installListeners();

        model = new ImapFetcherTableModel(main, this);
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

    ImapFetcherTableModel get_object_model()
    {
        return (ImapFetcherTableModel) model;
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

        SQLResult<ImapFetcher>  res = new SQLResult<ImapFetcher>(UserMain.sqc(), resa, new ImapFetcher().getClass());

        model.setSqlResult(res);
        table.tableChanged(new TableModelEvent(table.getModel()) );
        set_tabel_row_height();

        sql.close(cid);



    }



    @Override
    protected GlossDialogPanel get_edit_panel( int row )
    {
        return new EditImapFetcher( row, this );
    }




    
}
