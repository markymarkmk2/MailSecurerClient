/*
 * EditChannelPanel.java
 *
 * Created on 25. M�rz 2008, 20:06
 */
package dimm.home.Panels;

import com.thoughtworks.xstream.XStream;
import dimm.home.Rendering.GlossButton;
import dimm.home.UserMain;
import javax.swing.JButton;
import dimm.general.SQL.*;
import home.shared.hibernate.DiskArchive;
import home.shared.hibernate.AccountConnector;
import dimm.home.Models.DiskArchiveComboModel;
import dimm.home.ServerConnect.ServerCall;
import dimm.home.Utilities.SwingWorker;
import dimm.home.Utilities.Validator;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.swing.JFileChooser;

/**

@author  Administrator
 */
public class EditAccountConnector extends GenericEditPanel
{

    AccountConnectorOverview object_overview;
    AccountConnectorTableModel model;
    AccountConnector object;
    DiskArchiveComboModel dacm;
    private static final int DFLT_LDAP_PORT = 389;
    private static final int DFLT_LDAP_SSL_PORT = 636;

    /** Creates new form EditChannelPanel */
    public EditAccountConnector( int _row, AccountConnectorOverview _overview )
    {
        initComponents();

        object_overview = _overview;
        model = object_overview.get_object_model();
        CB_TYPE.removeAllItems();

        // FILL COMBOBOX TYPE
        for (int i = 0; i < object_overview.get_ac_entry_list().length; i++)
        {
            AccountConnectorOverview.AccountConnectorTypeEntry mte = object_overview.get_ac_entry_list()[i];
            CB_TYPE.addItem(mte);
        }


        SQLResult<DiskArchive> da_res = UserMain.sqc().get_da_result();



        row = _row;

        if (!model.is_new(row))
        {
            try
            {
                object = model.get_object(row);

                String type = object.getType();
                for (int i = 0; i < object_overview.get_ac_entry_list().length; i++)
                {
                    AccountConnectorOverview.AccountConnectorTypeEntry mte = object_overview.get_ac_entry_list()[i];
                    if (mte.type.compareTo(type) == 0)
                    {
                        CB_TYPE.setSelectedIndex(i);
                        break;
                    }
                }



                TXT_SERVER1.setText(object.getIp());
                TXT_PORT1.setText(object.getPort().toString());
                TXT_USERNAME.setText(object.getUsername());
                TXTP_PWD.setText(object.getPwd());
                CB_SSL.setSelected(object_has_ssl());
            }
            catch (Exception exc)
            {
                UserMain.errm_ok(UserMain.getString("Fehler_beim_Lesen_der_Datenbankdaten"));
            }


        }
        else
        {
            object = new AccountConnector();
            object.setMandant(UserMain.sqc().get_act_mandant());
            TXT_SERVER1.setText("127.0.0.1");
            TXT_PORT1.setText(Integer.toString(DFLT_LDAP_PORT));

        }



    }

    /** This method is called from within the constructor to
    initialize the form.
    WARNING: Do NOT modify this code. The content of this method is
    always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        PN_ACTION = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        TXT_SERVER1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        BT_DISABLED = new javax.swing.JCheckBox();
        CB_TYPE = new javax.swing.JComboBox();
        TXT_PORT1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        TXTP_PWD = new javax.swing.JPasswordField();
        jLabel5 = new javax.swing.JLabel();
        CB_SSL = new javax.swing.JCheckBox();
        TXT_USERNAME = new javax.swing.JTextField();
        BT_IMPORT_CERT = new javax.swing.JButton();
        PN_BUTTONS = new javax.swing.JPanel();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();
        BT_TEST = new javax.swing.JButton();

        setDoubleBuffered(false);
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        PN_ACTION.setDoubleBuffered(false);
        PN_ACTION.setOpaque(false);

        jLabel1.setText(UserMain.getString("Typ")); // NOI18N

        TXT_SERVER1.setText(UserMain.Txt("Neuer_Server")); // NOI18N
        TXT_SERVER1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_SERVER1MouseClicked(evt);
            }
        });
        TXT_SERVER1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TXT_SERVER1ActionPerformed(evt);
            }
        });

        jLabel2.setText(UserMain.getString("Server")); // NOI18N

        BT_DISABLED.setText(UserMain.getString("Gesperrt")); // NOI18N
        BT_DISABLED.setFocusable(false);
        BT_DISABLED.setOpaque(false);

        CB_TYPE.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        TXT_PORT1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_PORT1MouseClicked(evt);
            }
        });
        TXT_PORT1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TXT_PORT1ActionPerformed(evt);
            }
        });

        jLabel4.setText(UserMain.getString("Port")); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dimm/home/MA_Properties"); // NOI18N
        jLabel3.setText(bundle.getString("User")); // NOI18N

        jLabel5.setText(bundle.getString("Password")); // NOI18N

        CB_SSL.setText("SSL");
        CB_SSL.setFocusable(false);
        CB_SSL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_SSLActionPerformed(evt);
            }
        });

        BT_IMPORT_CERT.setText(bundle.getString("Import_certificate")); // NOI18N
        BT_IMPORT_CERT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_IMPORT_CERTActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_ACTIONLayout = new javax.swing.GroupLayout(PN_ACTION);
        PN_ACTION.setLayout(PN_ACTIONLayout);
        PN_ACTIONLayout.setHorizontalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addComponent(BT_DISABLED, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BT_IMPORT_CERT))
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))
                        .addGap(71, 71, 71)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(CB_TYPE, 0, 332, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PN_ACTIONLayout.createSequentialGroup()
                                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                                        .addComponent(TXT_SERVER1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_ACTIONLayout.createSequentialGroup()
                                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(TXTP_PWD, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                                            .addComponent(TXT_USERNAME, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE))
                                        .addGap(56, 56, 56)))
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(CB_SSL, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE)
                                    .addComponent(TXT_PORT1, javax.swing.GroupLayout.DEFAULT_SIZE, 76, Short.MAX_VALUE))))))
                .addGap(10, 10, 10))
        );
        PN_ACTIONLayout.setVerticalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(CB_TYPE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_SERVER1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(TXT_PORT1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(TXT_USERNAME, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CB_SSL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jLabel5)
                            .addComponent(TXTP_PWD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                        .addComponent(BT_DISABLED))
                    .addComponent(BT_IMPORT_CERT))
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

        BT_TEST.setText(bundle.getString("Test")); // NOI18N
        BT_TEST.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_TESTActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_BUTTONSLayout = new javax.swing.GroupLayout(PN_BUTTONS);
        PN_BUTTONS.setLayout(PN_BUTTONSLayout);
        PN_BUTTONSLayout.setHorizontalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BT_TEST)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 191, Short.MAX_VALUE)
                .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        PN_BUTTONSLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ABORT, BT_OK, BT_TEST});

        PN_BUTTONSLayout.setVerticalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_TEST))
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

    private void TXT_SERVER1MouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXT_SERVER1MouseClicked
    {//GEN-HEADEREND:event_TXT_SERVER1MouseClicked
        // TODO add your handling code here:
        if (UserMain.self.is_touchscreen())
        {
            UserMain.self.show_vkeyboard(this.my_dlg, TXT_SERVER1, false);
        }
}//GEN-LAST:event_TXT_SERVER1MouseClicked

    private void TXT_SERVER1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TXT_SERVER1ActionPerformed
    {//GEN-HEADEREND:event_TXT_SERVER1ActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_TXT_SERVER1ActionPerformed

    private void TXT_PORT1MouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXT_PORT1MouseClicked
    {//GEN-HEADEREND:event_TXT_PORT1MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_TXT_PORT1MouseClicked

    private void TXT_PORT1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TXT_PORT1ActionPerformed
    {//GEN-HEADEREND:event_TXT_PORT1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TXT_PORT1ActionPerformed

    private void CB_SSLActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_SSLActionPerformed
    {//GEN-HEADEREND:event_CB_SSLActionPerformed
        // TODO add your handling code here:
        try
        {
            if (CB_SSL.isSelected())
            {
                int port = Integer.parseInt(TXT_PORT1.getText());
                if (port == DFLT_LDAP_PORT)
                {
                    TXT_PORT1.setText(Integer.toString(DFLT_LDAP_SSL_PORT));

                }
            }
            else
            {
                int port = Integer.parseInt(TXT_PORT1.getText());
                if (port == DFLT_LDAP_SSL_PORT)
                {
                    TXT_PORT1.setText(Integer.toString(DFLT_LDAP_PORT));

                }
            }
        }
        catch (NumberFormatException numberFormatException)
        {
        }

    }//GEN-LAST:event_CB_SSLActionPerformed
    SwingWorker sw;
    private void BT_TESTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_TESTActionPerformed
    {//GEN-HEADEREND:event_BT_TESTActionPerformed
        // TODO add your handling code here:
        final String cmd = "TestLoginLDAP CMD:test NM:'" + TXT_USERNAME.getText() + "' PW:'" + get_pwd() + "' HO:" +
                TXT_SERVER1.getText() + " PO:" + TXT_PORT1.getText() + " SSL:" + (CB_SSL.isSelected() ? "1" : "0");

        if (sw != null)
        {
            return;
        }

        sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                UserMain.self.show_busy(my_dlg, UserMain.Txt("Checking_LDAP_login") + "...");
                String ret = UserMain.fcc().call_abstract_function(cmd, ServerCall.SHORT_CMD_TO);
                UserMain.self.hide_busy();
                if (ret.charAt(0) == '0')
                {
                    UserMain.info_ok(my_dlg, UserMain.Txt("LDAP_login_succeeded"));
                }
                else
                {
                    UserMain.errm_ok(my_dlg, UserMain.Txt("LDAP_login_failed") + ": " + ret.substring(3));
                }
                sw = null;
                return null;
            }
        };

        sw.start();

    }//GEN-LAST:event_BT_TESTActionPerformed

    static File last_dir;
    private void BT_IMPORT_CERTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_IMPORT_CERTActionPerformed
    {//GEN-HEADEREND:event_BT_IMPORT_CERTActionPerformed
        // TODO add your handling code here:

        // CHOOSE CERTFILE
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setCurrentDirectory(last_dir);
        if (JFileChooser.APPROVE_OPTION != chooser.showDialog(my_dlg, UserMain.Txt("Select_certificate")))
        {
            return;
        }
        File dir = chooser.getSelectedFile();
        last_dir = dir;

        // READ TO CERTBUFF
        ByteBuffer bb = ByteBuffer.allocate( (int)dir.length());
        try
        {
            FileInputStream fr = new FileInputStream(dir);
            
            fr.read(bb.array());
            fr.close();
        }
        catch (IOException iOException)
        {
            UserMain.errm_ok(my_dlg, UserMain.Txt("Error_while_reading_certificate") + ": " + iOException.getMessage() );
            return;
        }
        // BUILD IMPORT_COMMAND


        XStream xs = new XStream();
        String cert_xml = xs.toXML(bb);
        
        final String cmd = "upload_certificate MA:" + object.getMandant().getId() + " TY:cacert + CERT:" + cert_xml;

        if (sw != null)
        {
            return;
        }

        // AND SHOVE IT RIGHT OUT
        sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                UserMain.self.show_busy(my_dlg, UserMain.Txt("Importing_certificate") + "...");
                String ret = UserMain.fcc().call_abstract_function(cmd, ServerCall.SHORT_CMD_TO);
                UserMain.self.hide_busy();
                if (ret.charAt(0) == '0')
                {
                    UserMain.info_ok(my_dlg, UserMain.Txt("Certificate_was_imported_successful"));
                }
                else
                {
                    UserMain.errm_ok(my_dlg, UserMain.Txt("Importing_certificate_failed") + ": " + ret.substring(3));
                }
                sw = null;
                return null;
            }
        };

        sw.start();


    }//GEN-LAST:event_BT_IMPORT_CERTActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JCheckBox BT_DISABLED;
    private javax.swing.JButton BT_IMPORT_CERT;
    private javax.swing.JButton BT_OK;
    private javax.swing.JButton BT_TEST;
    private javax.swing.JCheckBox CB_SSL;
    private javax.swing.JComboBox CB_TYPE;
    private javax.swing.JPanel PN_ACTION;
    private javax.swing.JPanel PN_BUTTONS;
    private javax.swing.JPasswordField TXTP_PWD;
    private javax.swing.JTextField TXT_PORT1;
    private javax.swing.JTextField TXT_SERVER1;
    private javax.swing.JTextField TXT_USERNAME;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
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

    boolean object_is_disabled()
    {
        int flags = 0;

        flags = get_object_flags();

        return ((flags & AccountConnectorOverview.DISABLED) == AccountConnectorOverview.DISABLED);
    }

    boolean object_has_ssl()
    {
        int flags = 0;

        flags = get_object_flags();

        return ((flags & AccountConnectorOverview.USE_SSL) == AccountConnectorOverview.USE_SSL);
    }

    void set_object_disabled( boolean f )
    {
        if (f)
        {
            set_object_flag(AccountConnectorOverview.DISABLED);
        }
        else
        {
            clr_object_flag(AccountConnectorOverview.DISABLED);
        }
    }

    void set_object_ssl( boolean f )
    {
        if (f)
        {
            set_object_flag(AccountConnectorOverview.USE_SSL);
        }
        else
        {
            clr_object_flag(AccountConnectorOverview.USE_SSL);
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

        String server = object.getIp();
        if (server != null && TXT_SERVER1.getText().compareTo(server) != 0)
        {
            return true;
        }

        if (object.getPort() == null)
        {
            return true;
        }

        int port = object.getPort();
        if (Integer.parseInt(TXT_PORT1.getText()) != port)
        {
            return true;
        }


        if (BT_DISABLED.isSelected() != object_is_disabled())
        {
            return true;
        }

        if (CB_SSL.isSelected() != object_has_ssl())
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

        AccountConnectorOverview.AccountConnectorTypeEntry mte = (AccountConnectorOverview.AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
        String typ = mte.type;
        if (object.getType() != null && object.getType().compareTo(typ) != 0)
        {
            return true;
        }

        return false;
    }

    @Override
    protected boolean is_plausible()
    {
        if (!Validator.is_valid_name(TXT_SERVER1.getText(), 255))
        {
            UserMain.errm_ok(UserMain.getString("Der_Server_ist_nicht_okay"));
            return false;
        }
        if (!Validator.is_valid_port(TXT_PORT1.getText()))
        {
            UserMain.errm_ok(UserMain.getString("Port_ist_nicht_okay"));
            return false;
        }

        if (!Validator.is_valid_name(TXT_USERNAME.getText(), 80))
        {
            UserMain.errm_ok(UserMain.getString("Der_User_ist_nicht_okay"));
            return false;
        }
        if (get_pwd().length() == 0 || get_pwd().length() > 80)
        {
            UserMain.errm_ok(UserMain.getString("Das_Passwort_ist_nicht_okay"));
            return false;
        }


        try
        {
            AccountConnectorOverview.AccountConnectorTypeEntry mte = (AccountConnectorOverview.AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
            String name = mte.name;
        }
        catch (Exception e)
        {
            UserMain.errm_ok(UserMain.getString("Typ_ist_nicht_okay"));
            return false;
        }

        return true;
    }

    @Override
    protected void set_object_props()
    {
        String server1 = TXT_SERVER1.getText();
        int port1 = Integer.parseInt(TXT_PORT1.getText());
        boolean de = BT_DISABLED.isSelected();
        boolean ssl = CB_SSL.isSelected();
        String name = TXT_USERNAME.getText();
        String pwd = get_pwd();
        String type = "";
        AccountConnectorOverview.AccountConnectorTypeEntry mte = (AccountConnectorOverview.AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
        type = mte.type;

        object.setIp(server1);
        object.setPort(new Integer(port1));
        set_object_disabled(de);
        set_object_ssl(ssl);
        object.setType(type);
        object.setUsername(name);
        object.setPwd(pwd);
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
}