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
        String qry = "select * from role where mid=" + mandanten_id + " order by id";
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
                int flags = sqlResult.getInt(rowIndex, "Flags");
                boolean compressed = (flags & CS_Constants.ROLE_ACM_COMPRESSED) == CS_Constants.ROLE_ACM_COMPRESSED;
                return rdlg.get_account_match_descr( role.getAccountmatch(), compressed ); // ACCOUNTFILTER
            }
            case 3:
                int flags = sqlResult.getInt(rowIndex, "Flags");
                return new Boolean((flags & CS_Constants.ROLE_DISABLED) == CS_Constants.ROLE_DISABLED); // DISABLED
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
    String get_account_match_descr( String acm, boolean compressed )
    {
        return RoleFilter.get_nice_filter_text( acm, compressed );
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
        BT_NEW.setText(UserMain.Txt("Neuen_Role_hinzufuegen")); // NOI18N
        BT_NEW.setActionCommand("        ");
        BT_NEW.setBorder(null);
        BT_NEW.setContentAreaFilled(false);
        BT_NEW.setMargin(new java.awt.Insets(2, 20, 2, 20));
        BT_NEW.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_NEWActionPerformed(evt);
            }
        });

        BT_QUIT.setForeground(new java.awt.Color(204, 204, 204));
        BT_QUIT.setText(UserMain.Txt("CLOSE_DIALOG")); // NOI18N
        BT_QUIT.setBorder(BT_NEW.getBorder());
        BT_QUIT.setContentAreaFilled(false);
        BT_QUIT.addActionListener(new java.awt.event.ActionListener() {
            @Override
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
