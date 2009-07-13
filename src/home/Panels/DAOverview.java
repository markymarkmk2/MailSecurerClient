/*
 * NewJDialog.java
 *
 * Created on 20. M�rz 2008, 22:44
 */
package dimm.home.Panels;

import dimm.home.Models.OverviewModel;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossPanel;
import dimm.home.Rendering.GlossTable;
import dimm.home.Rendering.TitlePanel;
import dimm.home.Rendering.SQLOverviewDialog;
import dimm.home.ServerConnect.SQLCall;
import dimm.home.UserMain;
import javax.swing.JButton;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumnModel;

import dimm.general.SQL.*;
import dimm.general.hibernate.*;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.ServerConnect.ConnectionID;
import dimm.home.ServerConnect.ResultSetID;
import dimm.home.ServerConnect.StatementID;


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
                return new Boolean((flags & DAOverview.DISABLED) == DAOverview.DISABLED); // DISABLED
            default:
                return super.getValueAt(rowIndex, columnIndex);
        }
    }
    @Override
    public int getColumnCount()
    {

        // EDIT IST 2.LAST ROW!!!!
        if (UserMain.self.getUserLevel() < UserMain.UL_ADMIN)
            return col_names.length - 2;

        // DELETE IST LAST ROW!!!!
        if (UserMain.self.getUserLevel() < UserMain.UL_MULTIADMIN)
            return col_names.length - 1;

        return col_names.length;
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

        if (UserMain.self.getUserLevel() < UserMain.UL_MULTIADMIN)
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
        SQLCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open();
        StatementID sid = sql.createStatement(cid);

        String qry =  model.get_qry( firmen_id );

        ResultSetID rid = sql.executeQuery(sid, qry);
        SQLArrayResult resa = sql.get_sql_array_result(rid);

        SQLResult<DiskArchive>  res = new SQLResult<DiskArchive>(resa, new DiskArchive().getClass());

        model.setSqlResult(res);
        table.tableChanged(new TableModelEvent(table.getModel()) );
        set_tabel_row_height();

        sql.close(cid);



    }




    protected GlossDialogPanel get_edit_panel( int row )
    {
        return new EditDA( row, this );
    }



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        PN_MAIN = new GlossPanel();
        PN_TITLE = new javax.swing.JPanel();
        PN_TABLE = new javax.swing.JPanel();
        SCP_TABLE = new javax.swing.JScrollPane();
        PN_BUTTONS = new javax.swing.JPanel();
        BT_NEW = new GlossButton();
        BT_QUIT = new GlossButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        setUndecorated(true);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        PN_MAIN.setBackground(new java.awt.Color(51, 51, 51));
        PN_MAIN.setLayout(new java.awt.GridBagLayout());

        PN_TITLE.setOpaque(false);
        PN_TITLE.setLayout(new javax.swing.BoxLayout(PN_TITLE, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        PN_MAIN.add(PN_TITLE, gridBagConstraints);

        PN_TABLE.setBackground(new java.awt.Color(51, 51, 51));
        PN_TABLE.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PN_TABLE.setForeground(new java.awt.Color(255, 255, 255));
        PN_TABLE.setOpaque(false);
        PN_TABLE.setLayout(new javax.swing.BoxLayout(PN_TABLE, javax.swing.BoxLayout.LINE_AXIS));

        SCP_TABLE.setBackground(new java.awt.Color(51, 51, 51));
        SCP_TABLE.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        SCP_TABLE.setOpaque(false);
        SCP_TABLE.setPreferredSize(new java.awt.Dimension(500, 300));
        PN_TABLE.add(SCP_TABLE);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        PN_MAIN.add(PN_TABLE, gridBagConstraints);

        PN_BUTTONS.setOpaque(false);

        BT_NEW.setForeground(new java.awt.Color(204, 204, 204));
        BT_NEW.setText(UserMain.Txt("Neues_DiskArchive_hinzufuegen")); // NOI18N
        BT_NEW.setActionCommand("        ");
        BT_NEW.setBorder(null);
        BT_NEW.setContentAreaFilled(false);
        BT_NEW.setMargin(new java.awt.Insets(2, 20, 2, 20));
        BT_NEW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_NEWActionPerformed(evt);
            }
        });

        BT_QUIT.setForeground(new java.awt.Color(204, 204, 204));
        BT_QUIT.setText(UserMain.Txt("CLOSE_DIALOG")); // NOI18N
        BT_QUIT.setBorder(BT_NEW.getBorder());
        BT_QUIT.setContentAreaFilled(false);
        BT_QUIT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_QUITActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_BUTTONSLayout = new javax.swing.GroupLayout(PN_BUTTONS);
        PN_BUTTONS.setLayout(PN_BUTTONSLayout);
        PN_BUTTONSLayout.setHorizontalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap(200, Short.MAX_VALUE)
                .addComponent(BT_NEW, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BT_QUIT, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        PN_BUTTONSLayout.setVerticalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_QUIT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_NEW, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        PN_MAIN.add(PN_BUTTONS, gridBagConstraints);

        getContentPane().add(PN_MAIN);

        pack();
    }// </editor-fold>

    private void BT_NEWActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:

                new_edit_dlg();

}

    private void BT_QUITActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        this.setVisible(false);
    }

    boolean check_del_constaints(int row)
    {
        ConnectionID cid = null;
        boolean ret = true;
        DiskArchive da = (DiskArchive)model.getSqlResult().get(row);
        int id = da.getId();

        SQLCall sql = UserMain.sqc().get_sqc();

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
        if (!check_del_constaints(row))
            return false;

        boolean ok = super.del_object(row);

        // IF SOMETHING HAS BEEN DELETED, WE REBUILD OUR GLOBAL DA-LIST
        if (ok)
        {
            UserMain.sqc().rebuild_da_array(UserMain.sqc().get_act_mandant().getId());
        }
        return ok;

    }



    




    // Variables declaration - do not modify
    private javax.swing.JButton BT_NEW;
    private javax.swing.JButton BT_QUIT;
    private javax.swing.JPanel PN_BUTTONS;
    private org.jdesktop.swingx.JXPanel PN_MAIN;
    private javax.swing.JPanel PN_TABLE;
    private javax.swing.JPanel PN_TITLE;
    private javax.swing.JScrollPane SCP_TABLE;
    // End of variables declaration


    

}
