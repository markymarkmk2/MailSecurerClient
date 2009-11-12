/*
 * EditChannelPanel.java
 *
 * Created on 25. M�rz 2008, 20:06
 */
package dimm.home.Panels;

import dimm.general.SQL.SQLResult;
import dimm.home.Models.OverviewModel;
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossTable;
import dimm.home.Rendering.SQLOverviewDialog;
import dimm.home.Rendering.SingleMailEditPanel;
import dimm.home.ServerConnect.ConnectionID;
import dimm.home.ServerConnect.ResultSetID;
import dimm.home.ServerConnect.ServerCall;
import dimm.home.ServerConnect.StatementID;
import dimm.home.UserMain;
import javax.swing.JButton;
import dimm.home.Utilities.SwingWorker;
import dimm.home.Utilities.Validator;
import home.shared.CS_Constants;
import home.shared.SQL.SQLArrayResult;
import home.shared.hibernate.MailAddress;
import home.shared.hibernate.MailHeaderVariable;
import java.io.File;
import home.shared.hibernate.MailUser;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumnModel;

class EMailModel extends OverviewModel
{

    ArrayList<MailAddress> mhv_list;
    String link_table;

    EMailModel( UserMain _main, SQLOverviewDialog _dlg, String link_table )
    {
        super(_main, _dlg);
        this.link_table = link_table;

        String[] _col_names =
        {
            "Id", UserMain.getString("Mail"), UserMain.getString("Bearbeiten"), UserMain.getString("Löschen")
        };
        Class[] _col_classes =
        {
            String.class, String.class, JButton.class, JButton.class
        };
        set_columns(_col_names, _col_classes);

    }

    @Override
    public Object getValueAt( int rowIndex, int columnIndex )
    {
        if (sqlResult == null)
        {
            return null;
        }

        MailAddress email;
        email = (MailAddress) sqlResult.get(rowIndex);

        switch (columnIndex)
        {
            case 0:
                return email.getId(); // ID
            case 1:
                return email.getEmail();
            default:
                return super.getValueAt(rowIndex, columnIndex);
        }
    }

    @Override
    public int getColumnCount()
    {
        return col_names.length;
    }

    public MailAddress get_object( int index )
    {
        return (MailAddress) sqlResult.get(index);
    }

    @Override
    public String get_qry( long station_id )
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    String get_link_table()
    {
        return link_table;
    }
}

/**

@author  Administrator
 */
public class EditMailUser extends GenericEditPanel implements PropertyChangeListener, MouseListener
{

    MailUserOverview object_overview;
    MailUserOverviewTableModel model;
    MailUser object;
    ArrayList<MailAddress> mail_add_list;
    ArrayList<MailAddress> mail_view_list;
    GlossTable add_table;
    GlossTable view_table;
    EMailModel add_model;
    EMailModel view_model;

    /** Creates new form EditChannelPanel */
    public EditMailUser( int _row, MailUserOverview _overview )
    {
        initComponents();

        object_overview = _overview;
        model = object_overview.get_object_model();

        row = _row;

        mail_add_list = new ArrayList<MailAddress>();
        mail_view_list = new ArrayList<MailAddress>();

        if (!model.is_new(row))
        {
            try
            {
                object = model.get_object(row);


                TXT_EMAIL.setText(object.getEmail());

                TXT_USERNAME.setText(object.getUsername());
                TXTP_PWD.setText(object.getPwd());
                BT_DISABLED.setSelected(test_flag(CS_Constants.ACCT_DISABLED));


            }
            catch (Exception exc)
            {
                UserMain.errm_ok(UserMain.getString("Fehler_beim_Lesen_der_Datenbankdaten"));
            }
        }
        else
        {
            object = new MailUser();
            object.setMandant(UserMain.sqc().get_act_mandant());
            TXT_EMAIL.setText("");
        }
        
        // DISABLE PASSWORD IF WE HAVE A REMOTE USERACCOUNT (IMAP/SMTP/POP), PWD IS STORED THERE
        if (object_overview.getAcct() != null)
        {
            String type = object_overview.getAcct().getType();
            if (type.compareTo("dbs") != 0)
            {
                TXTP_PWD.setVisible(false);
                LB_PWD.setVisible(false);
            }
        }

        add_table = new GlossTable();
        add_table.addMouseListener(this);
        view_table = new GlossTable();
        view_table.addMouseListener(this);

        build_header_list(object);

    }

    EMailModel build_header_list( String link_table, GlossTable table, JScrollPane scp )
    {
        int mu_id = object.getId();
        String qry = "select ma.* from mail_address ma," + link_table + " where ma.id=ma_id and mu_id=" + mu_id;
        ServerCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open();
        StatementID sid = sql.createStatement(cid);


        ResultSetID rid = sql.executeQuery(sid, qry);
        SQLArrayResult resa = sql.get_sql_array_result(rid);
        SQLResult<MailHeaderVariable> add_res = new SQLResult<MailHeaderVariable>(resa, new MailAddress().getClass());


        EMailModel mail_model = new EMailModel(UserMain.self, object_overview, link_table);
        table.setModel(mail_model);
        table.embed_to_scrollpanel(scp);
        mail_model.setSqlResult(add_res);

        TableColumnModel cm = table.getTableHeader().getColumnModel();
        cm.getColumn(0).setMinWidth(40);
        cm.getColumn(0).setMaxWidth(40);
        cm.getColumn(1).setPreferredWidth(150);
        mail_model.set_table_header(cm);

        return mail_model;
    }

    private void build_header_list( MailUser m )
    {
        add_model = build_header_list("mu_add_link", add_table, SCP_ADD_MAIL);

        view_model = build_header_list("mu_view_link", view_table, SCP_VIEW_MAIL);
    }

    /** This method is called from within the constructor to
    initialize the form.
    WARNING: Do NOT modify this code. The content of this method is
    always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        PN_ACTION = new javax.swing.JPanel();
        TXT_EMAIL = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        BT_DISABLED = new javax.swing.JCheckBox();
        LB_USER = new javax.swing.JLabel();
        TXTP_PWD = new javax.swing.JPasswordField();
        LB_PWD = new javax.swing.JLabel();
        TXT_USERNAME = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        SCP_ADD_MAIL = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        SCP_VIEW_MAIL = new javax.swing.JScrollPane();
        BT_NEW_ADD_MAIL = new javax.swing.JButton();
        BT_ADD_VIEW_MAIL = new javax.swing.JButton();
        PN_BUTTONS = new javax.swing.JPanel();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();

        setDoubleBuffered(false);
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        PN_ACTION.setDoubleBuffered(false);
        PN_ACTION.setOpaque(false);

        TXT_EMAIL.setText(UserMain.Txt("Neuer_Server")); // NOI18N
        TXT_EMAIL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_EMAILMouseClicked(evt);
            }
        });
        TXT_EMAIL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TXT_EMAILActionPerformed(evt);
            }
        });

        jLabel2.setText(UserMain.getString("Server")); // NOI18N

        BT_DISABLED.setText(UserMain.getString("Gesperrt")); // NOI18N
        BT_DISABLED.setFocusable(false);
        BT_DISABLED.setOpaque(false);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dimm/home/MA_Properties"); // NOI18N
        LB_USER.setText(bundle.getString("User")); // NOI18N

        LB_PWD.setText(bundle.getString("Password")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(UserMain.Txt("Aliases"))); // NOI18N
        jPanel1.setPreferredSize(new java.awt.Dimension(254, 118));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_ADD_MAIL, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_ADD_MAIL, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(UserMain.Txt("Allowed_Viewers"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_VIEW_MAIL, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_VIEW_MAIL, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
        );

        BT_NEW_ADD_MAIL.setText(UserMain.Txt("New")); // NOI18N
        BT_NEW_ADD_MAIL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_NEW_ADD_MAILActionPerformed(evt);
            }
        });

        BT_ADD_VIEW_MAIL.setText(UserMain.Txt("New")); // NOI18N
        BT_ADD_VIEW_MAIL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ADD_VIEW_MAILActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_ACTIONLayout = new javax.swing.GroupLayout(PN_ACTION);
        PN_ACTION.setLayout(PN_ACTIONLayout);
        PN_ACTIONLayout.setHorizontalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_ACTIONLayout.createSequentialGroup()
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LB_USER)
                            .addComponent(LB_PWD)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TXT_EMAIL, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                            .addComponent(TXT_USERNAME, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE)
                            .addComponent(TXTP_PWD, javax.swing.GroupLayout.DEFAULT_SIZE, 403, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(BT_DISABLED, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(76, 76, 76))
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(BT_NEW_ADD_MAIL)
                            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(BT_ADD_VIEW_MAIL))
                        .addContainerGap())))
        );
        PN_ACTIONLayout.setVerticalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(TXT_EMAIL, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_DISABLED))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LB_USER)
                    .addComponent(TXT_USERNAME, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(LB_PWD)
                    .addComponent(TXTP_PWD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_NEW_ADD_MAIL)
                    .addComponent(BT_ADD_VIEW_MAIL))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(PN_ACTION, gridBagConstraints);

        PN_BUTTONS.setDoubleBuffered(false);
        PN_BUTTONS.setOpaque(false);

        BT_OK.setText(UserMain.getString("Okay")); // NOI18N
        BT_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKActionPerformed(evt);
            }
        });

        BT_ABORT.setText(UserMain.getString("Abbruch")); // NOI18N
        BT_ABORT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ABORTActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_BUTTONSLayout = new javax.swing.GroupLayout(PN_BUTTONS);
        PN_BUTTONS.setLayout(PN_BUTTONSLayout);
        PN_BUTTONSLayout.setHorizontalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap(484, Short.MAX_VALUE)
                .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        PN_BUTTONSLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ABORT, BT_OK});

        PN_BUTTONSLayout.setVerticalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(PN_BUTTONS, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKActionPerformed
    {//GEN-HEADEREND:event_BT_OKActionPerformed
        // TODO add your handling code here:

        ok_action(object);

    }//GEN-LAST:event_BT_OKActionPerformed

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ABORTActionPerformed
    {//GEN-HEADEREND:event_BT_ABORTActionPerformed
        // TODO add your handling code here:
        abort_action();
    }//GEN-LAST:event_BT_ABORTActionPerformed

    private void TXT_EMAILMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXT_EMAILMouseClicked
    {//GEN-HEADEREND:event_TXT_EMAILMouseClicked
        // TODO add your handling code here:
        if (UserMain.self.is_touchscreen())
        {
            UserMain.self.show_vkeyboard(this.my_dlg, TXT_EMAIL, false);
        }
}//GEN-LAST:event_TXT_EMAILMouseClicked

    private void TXT_EMAILActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TXT_EMAILActionPerformed
    {//GEN-HEADEREND:event_TXT_EMAILActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_TXT_EMAILActionPerformed
    SwingWorker sw;
    private void BT_NEW_ADD_MAILActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_NEW_ADD_MAILActionPerformed
    {//GEN-HEADEREND:event_BT_NEW_ADD_MAILActionPerformed
        // TODO add your handling code here:
        new_mail_object(add_table);
    }//GEN-LAST:event_BT_NEW_ADD_MAILActionPerformed

    private void BT_ADD_VIEW_MAILActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ADD_VIEW_MAILActionPerformed
    {//GEN-HEADEREND:event_BT_ADD_VIEW_MAILActionPerformed
        // TODO add your handling code here:
        new_mail_object(view_table);

    }//GEN-LAST:event_BT_ADD_VIEW_MAILActionPerformed
    static File last_dir;
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_ADD_VIEW_MAIL;
    private javax.swing.JCheckBox BT_DISABLED;
    private javax.swing.JButton BT_NEW_ADD_MAIL;
    private javax.swing.JButton BT_OK;
    private javax.swing.JLabel LB_PWD;
    private javax.swing.JLabel LB_USER;
    private javax.swing.JPanel PN_ACTION;
    private javax.swing.JPanel PN_BUTTONS;
    private javax.swing.JScrollPane SCP_ADD_MAIL;
    private javax.swing.JScrollPane SCP_VIEW_MAIL;
    private javax.swing.JPasswordField TXTP_PWD;
    private javax.swing.JTextField TXT_EMAIL;
    private javax.swing.JTextField TXT_USERNAME;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables

    int get_object_flags()
    {
        return object.getFlags();

    }

    void set_object_flag( int flag )
    {
        int flags = get_object_flags();
        flags |= flag;
        object.setFlags(flags);
    }

    void clr_object_flag( int flag )
    {
        int flags = get_object_flags();
        flags &= ~flag;
        object.setFlags(flags);
    }

    boolean test_flag( int test_flag )
    {
        int flags = get_object_flags();
        return ((flags & test_flag) == test_flag);
    }

    void set_flag( boolean state, int flag )
    {
        if (state)
        {
            set_object_flag(flag);
        }
        else
        {
            clr_object_flag(flag);
        }
    }

    String get_pwd()
    {
        char[] pwd = TXTP_PWD.getPassword();
        return new String(pwd);
    }

    @Override
    protected boolean check_changed()
    {
        if (model.is_new(row))
        {
            return true;
        }

        String email = object.getEmail();
        if (email != null && TXT_EMAIL.getText().compareTo(email) != 0)
        {
            return true;
        }

        if (BT_DISABLED.isSelected() != test_flag(CS_Constants.ACCT_DISABLED))
        {
            return true;
        }

        String user = object.getUsername();
        if (user != null && TXT_USERNAME.getText().compareTo(user) != 0)
        {
            return true;
        }

        String pwd = object.getPwd();
        if (pwd != null && get_pwd().compareTo(pwd) != 0)
        {
            return true;
        }

        return false;
    }

    @Override
    protected boolean is_plausible()
    {
        if (!Validator.is_valid_email(TXT_EMAIL.getText()))
        {
            UserMain.errm_ok(UserMain.getString("Die_Mailadresse_ist_nicht_okay"));
            return false;
        }

        if (!Validator.is_valid_name(TXT_USERNAME.getText(), 80))
        {
            UserMain.errm_ok(UserMain.getString("Der_User_ist_nicht_okay"));
            return false;
        }

        if (TXTP_PWD.isVisible())
        {
            if (get_pwd().length() == 0 || get_pwd().length() > 80)
            {
                UserMain.errm_ok(UserMain.getString("Das_Passwort_ist_nicht_okay"));
                return false;
            }
        }
        return true;
    }

    @Override
    protected void set_object_props()
    {
        String email = TXT_EMAIL.getText();
        String name = TXT_USERNAME.getText();
        String pwd = get_pwd();

        object.setEmail(email);
        object.setUsername(name);
        object.setPwd(pwd);
        object.setFlags(0);
        set_flag(BT_DISABLED.isSelected(), CS_Constants.ACCT_DISABLED);
    }

    @Override
    public JButton get_default_button()
    {
        return BT_OK;
    }

    @Override
    protected boolean is_new()
    {
        return model.is_new(row);
    }

    void edit_mail_row( int row, EMailModel model )
    {
        String name = model.get_object(row).getEmail();
        SingleMailEditPanel pnl = new SingleMailEditPanel(UserMain.Txt("Edit Email"));
        pnl.setText( name);
        pnl.setLabel(UserMain.Txt("EMail"));
        GenericGlossyDlg dlg = new GenericGlossyDlg(null, true, pnl);

        pnl.addPropertyChangeListener("REBUILD", this);

        dlg.set_next_location(this);
        dlg.setVisible(true);
        if (pnl.isOkay())
        {
            model.get_object(row).setEmail(pnl.getText());

            ServerCall sql = UserMain.sqc().get_sqc();
            ConnectionID cid = sql.open();
            StatementID sta = sql.createStatement(cid);
            sql.Update(sta, model.get_object(row));

            propertyChange(new PropertyChangeEvent(this, "REBUILD", null, null));
        }
    }

    boolean del_mail_object( int row, EMailModel mail_model )
    {
        MailAddress mail = mail_model.get_object(row);
        String link_table = mail_model.get_link_table();

        ServerCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open();
        StatementID sta = sql.createStatement(cid);

        int mu_id = object.getId();
        int ma_id = mail.getId();

        boolean okay = true;

        String ins_stmt = "delete from " + link_table + " where mu_id=" + mu_id + " and ma_id=" + ma_id;
        int rows = sql.executeUpdate(sta, ins_stmt);
        if (rows != 1)
            okay = false;

        int remaining_entries = sql.GetFirstSqlFieldInt(cid, "select count(*) from " + link_table + " where ma_id=" + ma_id, 0);

        if (okay && remaining_entries == 0)
        {
            okay = sql.Delete(sta, mail);
        }

        sql.close(sta);
        sql.close(cid);

        if (!okay)
        {
            String object_name = mail.getClass().getSimpleName();
            UserMain.errm_ok(my_dlg, UserMain.Txt("Cannot_delete") + " " + object_name + " " + sql.get_last_err());
        }

        propertyChange(new PropertyChangeEvent(this, "REBUILD", null, null));

        return okay;
    }

    void new_mail_object( GlossTable table )
    {
        EMailModel mail_model = (EMailModel) table.getModel();

        boolean was_new = model.is_new(row);

        if (was_new)
        {
            boolean ok = save_action(object);
            if (!ok)
            {
                return;
            }
            // REBUILD MODEL AND SET VARS
            object_overview.gather_sql_result();
            model = object_overview.get_object_model();
            row = model.get_row_by_id(object.getId());
        }
        int mu_id = object.getId();

        ServerCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open();
        StatementID sta = sql.createStatement(cid);
        String link_table = mail_model.link_table;


        MailAddress madr = new MailAddress(0, object.getMandant(), "", 0);
        boolean okay = true;
        if (!sql.Insert(sta, madr))
            okay = false;

        if (okay)
        {
            int ma_id = madr.getId();
            String ins_stmt = "insert into " + link_table + " ( mu_id, ma_id ) values (" + mu_id + "," + ma_id + ")";
            int rows = sql.executeUpdate(sta, ins_stmt);
        }


        sql.close(sta);
        sql.close(cid);

        build_header_list(object);

        // RELOAD MODEL, IT HAS BENN CREATED NEW!
        mail_model = (EMailModel) table.getModel();

        int mail_row = mail_model.getRowCount() - 1;
        if (mail_row >= 0)
        {
            edit_mail_row(mail_row, mail_model);
        }
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
        build_header_list(object);
    }

    @Override
    public void mouseClicked( MouseEvent e )
    {
        if (e.getSource() instanceof GlossTable)
        {
            GlossTable table = (GlossTable) e.getSource();
            Component c = table.getComponentAt(e.getPoint());
            int hrow = table.rowAtPoint(e.getPoint());
            int col = table.columnAtPoint(e.getPoint());

            EMailModel mail_model = (EMailModel) table.getModel();
            MailAddress mail_addr = mail_model.get_object(hrow);

            if (col == mail_model.get_edit_column())
            {
                edit_mail_row(hrow, mail_model);
            }

            if (col == mail_model.get_del_column())
            {

                String txt = UserMain.getString("Wollen_Sie_wirklich_diesen_Eintrag_loeschen");
                txt += ": <" + mail_addr.getEmail() + ">";

                if (UserMain.errm_ok_cancel(txt + "?"))
                {
                    boolean okay = del_mail_object(hrow, mail_model);

                    propertyChange(new PropertyChangeEvent(this, "REBUILD", null, null));
                }
            }
        }
    }

    @Override
    public void mousePressed( MouseEvent e )
    {
    }

    @Override
    public void mouseReleased( MouseEvent e )
    {
    }

    @Override
    public void mouseEntered( MouseEvent e )
    {
    }

    @Override
    public void mouseExited( MouseEvent e )
    {
    }
}
