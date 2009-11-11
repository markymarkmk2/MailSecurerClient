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
import home.shared.hibernate.AccountConnector;
import dimm.home.Models.DiskArchiveComboModel;
import dimm.home.ServerConnect.ServerCall;
import dimm.home.Utilities.SwingWorker;
import dimm.home.Utilities.Validator;
import home.shared.CS_Constants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.swing.JFileChooser;
import home.shared.AccountConnectorTypeEntry;

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
        for (int i = 0; i < CS_Constants.get_ac_list_count(); i++)
        {
            AccountConnectorTypeEntry mte = CS_Constants.get_ac(i);
            CB_TYPE.addItem(mte);
        }

        row = _row;

        if (!model.is_new(row))
        {
            try
            {
                object = model.get_object(row);

                String type = object.getType();
                for (int i = 0; i < CS_Constants.get_ac_list_count(); i++)
                {
                    AccountConnectorTypeEntry mte = CS_Constants.get_ac(i);
                    if (mte.getType().compareTo(type) == 0)
                    {
                        CB_TYPE.setSelectedIndex(i);
                        break;
                    }
                }

                TXT_SERVER1.setText(object.getIp());
                TXT_PORT1.setText(object.getPort().toString());
                TXT_USERNAME.setText(object.getUsername());
                TXTP_PWD.setText(object.getPwd());
                BT_DISABLED.setSelected(test_flag(CS_Constants.ACCT_DISABLED));

                if (test_flag( CS_Constants.ACCT_USE_SSL))
                    RB_SSL.setSelected(true);
                else if (test_flag( CS_Constants.ACCT_USE_TLS_IF_AVAIL))
                    RB_TLS_IV_AVAIL.setSelected(true);
                else if (test_flag( CS_Constants.ACCT_USE_TLS_FORCE))
                    RB_TLS_FORCE.setSelected(true);
                else
                    RB_INSECURE.setSelected(true);


                CB_CERTIFICATE.setSelected(test_flag(CS_Constants.ACCT_HAS_TLS_CERT));
                if (CB_CERTIFICATE.isSelected())
                {
                    BT_IMPORT_CERT.setVisible(true);
                }
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
            RB_INSECURE.setSelected(true);
            CB_CERTIFICATE.setSelected(false);
        }


    }

    int get_dflt_port( String type, boolean secure )
    {
        if (type.compareTo("smtp") == 0)
            return 25;
        if (type.compareTo("pop") == 0)
            return (secure ? 995 : 110);
        if (type.compareTo("imap") == 0)
            return (secure ? 993 : 143);
        if (type.compareTo("ldap") == 0)
            return (secure ? 636 : 389);

        return 0;
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
        jLabel1 = new javax.swing.JLabel();
        TXT_SERVER1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        BT_DISABLED = new javax.swing.JCheckBox();
        CB_TYPE = new javax.swing.JComboBox();
        TXT_PORT1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        LB_USER = new javax.swing.JLabel();
        TXTP_PWD = new javax.swing.JPasswordField();
        LB_PWD = new javax.swing.JLabel();
        TXT_USERNAME = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        RB_INSECURE = new javax.swing.JRadioButton();
        RB_TLS_IV_AVAIL = new javax.swing.JRadioButton();
        RB_TLS_FORCE = new javax.swing.JRadioButton();
        RB_SSL = new javax.swing.JRadioButton();
        CB_CERTIFICATE = new javax.swing.JCheckBox();
        BT_IMPORT_CERT = new javax.swing.JButton();
        PN_BUTTONS = new javax.swing.JPanel();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();
        BT_TEST = new GlossButton();
        BT_EDIT_USERS = new GlossButton();

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
        CB_TYPE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_TYPEActionPerformed(evt);
            }
        });

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
        LB_USER.setText(bundle.getString("User")); // NOI18N

        LB_PWD.setText(bundle.getString("Password")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(UserMain.Txt("Security"))); // NOI18N

        buttonGroup1.add(RB_INSECURE);
        RB_INSECURE.setText(UserMain.Txt("unsecure")); // NOI18N
        RB_INSECURE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RB_INSECUREActionPerformed(evt);
            }
        });

        buttonGroup1.add(RB_TLS_IV_AVAIL);
        RB_TLS_IV_AVAIL.setText(UserMain.Txt("TLS_if_available")); // NOI18N
        RB_TLS_IV_AVAIL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RB_TLS_IV_AVAILActionPerformed(evt);
            }
        });

        buttonGroup1.add(RB_TLS_FORCE);
        RB_TLS_FORCE.setText(UserMain.Txt("only_TLS")); // NOI18N
        RB_TLS_FORCE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RB_TLS_FORCEActionPerformed(evt);
            }
        });

        buttonGroup1.add(RB_SSL);
        RB_SSL.setText(UserMain.Txt("SSL")); // NOI18N
        RB_SSL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RB_SSLActionPerformed(evt);
            }
        });

        CB_CERTIFICATE.setText(UserMain.Txt("with_Certificate")); // NOI18N
        CB_CERTIFICATE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_CERTIFICATEActionPerformed(evt);
            }
        });

        BT_IMPORT_CERT.setText(UserMain.Txt("Import_certificate")); // NOI18N
        BT_IMPORT_CERT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_IMPORT_CERTActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(RB_TLS_IV_AVAIL)
                                .addComponent(RB_INSECURE, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                                .addComponent(RB_TLS_FORCE))
                            .addGap(63, 63, 63))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(RB_SSL)
                            .addContainerGap(124, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(CB_CERTIFICATE)
                            .addContainerGap(68, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(BT_IMPORT_CERT)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(RB_INSECURE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RB_TLS_IV_AVAIL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RB_TLS_FORCE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RB_SSL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CB_CERTIFICATE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BT_IMPORT_CERT)
                .addContainerGap())
        );

        javax.swing.GroupLayout PN_ACTIONLayout = new javax.swing.GroupLayout(PN_ACTION);
        PN_ACTION.setLayout(PN_ACTIONLayout);
        PN_ACTIONLayout.setHorizontalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addGap(85, 85, 85)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CB_TYPE, 0, 185, Short.MAX_VALUE)
                            .addComponent(TXT_PORT1, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                            .addComponent(TXT_SERVER1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)))
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(280, 280, 280))
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LB_USER)
                            .addComponent(LB_PWD))
                        .addGap(71, 71, 71)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TXTP_PWD, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)
                            .addComponent(TXT_USERNAME, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE)))
                    .addComponent(BT_DISABLED, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );
        PN_ACTIONLayout.setVerticalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(CB_TYPE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(TXT_SERVER1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(6, 6, 6)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(TXT_PORT1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(LB_USER)
                            .addComponent(TXT_USERNAME, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(LB_PWD)
                            .addComponent(TXTP_PWD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(BT_DISABLED))
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(19, Short.MAX_VALUE))
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

        BT_EDIT_USERS.setText(UserMain.Txt("Edit_users_and_addresses")); // NOI18N
        BT_EDIT_USERS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_EDIT_USERSActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_BUTTONSLayout = new javax.swing.GroupLayout(PN_BUTTONS);
        PN_BUTTONS.setLayout(PN_BUTTONSLayout);
        PN_BUTTONSLayout.setHorizontalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BT_TEST)
                .addGap(18, 18, 18)
                .addComponent(BT_EDIT_USERS)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 74, Short.MAX_VALUE)
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
                    .addComponent(BT_TEST, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_EDIT_USERS, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    SwingWorker sw;
    private void BT_TESTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_TESTActionPerformed
    {//GEN-HEADEREND:event_BT_TESTActionPerformed
        // TODO add your handling code here:

        AccountConnectorTypeEntry mte = (AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
        String type = mte.getType();

        int flags = 0;
        if (RB_SSL.isSelected())
            flags |= CS_Constants.ACCT_USE_SSL;
        if (RB_TLS_FORCE.isSelected())
            flags |= CS_Constants.ACCT_USE_TLS_FORCE;
        if (RB_TLS_IV_AVAIL.isSelected())
            flags |= CS_Constants.ACCT_USE_TLS_IF_AVAIL;

        
        final String cmd = "TestLogin CMD:test NM:'" + TXT_USERNAME.getText() + "' PW:'" + get_pwd() + "' HO:" +
                TXT_SERVER1.getText() + " PO:" + TXT_PORT1.getText() + " TY:" + type + " FL:" + flags;

        if (sw != null)
        {
            return;
        }

        sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                UserMain.self.show_busy(my_dlg, UserMain.Txt("Checking_login") + "...");
                String ret = UserMain.fcc().call_abstract_function(cmd, ServerCall.SHORT_CMD_TO);
                UserMain.self.hide_busy();
                if (ret.charAt(0) == '0')
                {
                    UserMain.info_ok(my_dlg, UserMain.Txt("Login_succeeded"));
                }
                else
                {
                    UserMain.errm_ok(my_dlg, UserMain.Txt("Login_failed") + ": " + ret.substring(3));
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

    private void CB_TYPEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_TYPEActionPerformed
    {//GEN-HEADEREND:event_CB_TYPEActionPerformed
        // TODO add your handling code here:
        AccountConnectorTypeEntry mte = (AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
        if (mte == null)
            return;
        String type = mte.getType();
        boolean user_visible = true;

        if (type.compareTo("smtp") == 0 || type.compareTo("pop") == 0 || type.compareTo("imap") == 0 )
        {
            user_visible = false;
        }
        // WE SHOW OUR USER DB ONLY IF NOT LDAP
        BT_EDIT_USERS.setVisible(!user_visible);
        
        TXT_USERNAME.setVisible(user_visible);
        TXTP_PWD.setVisible(user_visible);
        LB_USER.setVisible(user_visible);
        LB_PWD.setVisible(user_visible);

        if (my_dlg != null)
            my_dlg.pack();



    }//GEN-LAST:event_CB_TYPEActionPerformed

    private void CB_CERTIFICATEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_CERTIFICATEActionPerformed
    {//GEN-HEADEREND:event_CB_CERTIFICATEActionPerformed
        // TODO add your handling code here:
        BT_IMPORT_CERT.setVisible(CB_CERTIFICATE.isSelected());
    }//GEN-LAST:event_CB_CERTIFICATEActionPerformed

    private void RB_INSECUREActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RB_INSECUREActionPerformed
    {//GEN-HEADEREND:event_RB_INSECUREActionPerformed
        // TODO add your handling code here:
        AccountConnectorTypeEntry mte = (AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
        String type = mte.getType();
        TXT_PORT1.setText( "" + get_dflt_port( type, false ) );
    }//GEN-LAST:event_RB_INSECUREActionPerformed

    private void RB_TLS_IV_AVAILActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RB_TLS_IV_AVAILActionPerformed
    {//GEN-HEADEREND:event_RB_TLS_IV_AVAILActionPerformed
        // TODO add your handling code here:
        AccountConnectorTypeEntry mte = (AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
        String type = mte.getType();
        TXT_PORT1.setText( "" + get_dflt_port( type, false ) );

    }//GEN-LAST:event_RB_TLS_IV_AVAILActionPerformed

    private void RB_TLS_FORCEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RB_TLS_FORCEActionPerformed
    {//GEN-HEADEREND:event_RB_TLS_FORCEActionPerformed
        // TODO add your handling code here:
        AccountConnectorTypeEntry mte = (AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
        String type = mte.getType();
        TXT_PORT1.setText( "" + get_dflt_port( type, false ) );

    }//GEN-LAST:event_RB_TLS_FORCEActionPerformed

    private void RB_SSLActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RB_SSLActionPerformed
    {//GEN-HEADEREND:event_RB_SSLActionPerformed
        // TODO add your handling code here:
        AccountConnectorTypeEntry mte = (AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
        String type = mte.getType();
        TXT_PORT1.setText( "" + get_dflt_port( type, true ) );

    }//GEN-LAST:event_RB_SSLActionPerformed

    private void BT_EDIT_USERSActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_EDIT_USERSActionPerformed
    {//GEN-HEADEREND:event_BT_EDIT_USERSActionPerformed
        // TODO add your handling code here:
        boolean _is_new = is_new();

        // IF WE WANT TO INSERT DISKSPACE IN A NEW OBJECT , WE HAVE TO SAVE FIRST
        if (_is_new)
        {
            // IN CASE OF ERROR -> LEAVE, MESSAGE WAS ALREADY SHOWN
            if (!save_action(object) || model.getRowCount() <= 0)
                return;

            // NOW THE LAST OBJECT IN OVERVIEWLIST IS OUR NEW OBJECT, OBJECTS ARE ORDERED BY ID
            int size = model.getRowCount();
            AccountConnector new_object = model.get_object(size - 1);
            if (new_object.getUsername().compareTo(object.getUsername()) != 0)
            {
                return;
            }

            // SET INTERNAL VARS
            object = new_object;
            row = size - 1;
        }

        MailUserOverview dlg = new MailUserOverview(UserMain.self,  true);
        dlg.pack();

        dlg.setLocation(this.getLocationOnScreen().x + 30, this.getLocationOnScreen().y + 30);
        dlg.setTitle(UserMain.Txt("MailUser"));
        dlg.setVisible(true);


    }//GEN-LAST:event_BT_EDIT_USERSActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JCheckBox BT_DISABLED;
    private javax.swing.JButton BT_EDIT_USERS;
    private javax.swing.JButton BT_IMPORT_CERT;
    private javax.swing.JButton BT_OK;
    private javax.swing.JButton BT_TEST;
    private javax.swing.JCheckBox CB_CERTIFICATE;
    private javax.swing.JComboBox CB_TYPE;
    private javax.swing.JLabel LB_PWD;
    private javax.swing.JLabel LB_USER;
    private javax.swing.JPanel PN_ACTION;
    private javax.swing.JPanel PN_BUTTONS;
    private javax.swing.JRadioButton RB_INSECURE;
    private javax.swing.JRadioButton RB_SSL;
    private javax.swing.JRadioButton RB_TLS_FORCE;
    private javax.swing.JRadioButton RB_TLS_IV_AVAIL;
    private javax.swing.JPasswordField TXTP_PWD;
    private javax.swing.JTextField TXT_PORT1;
    private javax.swing.JTextField TXT_SERVER1;
    private javax.swing.JTextField TXT_USERNAME;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
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
/*
    boolean object_is_disabled()
    {
        int flags = 0;

        flags = get_object_flags();

        return ((flags & CS_Constants.ACCT_DISABLED) == CS_Constants.ACCT_DISABLED);
    }
*/
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


        if (BT_DISABLED.isSelected() != test_flag(CS_Constants.ACCT_DISABLED))
        {
            return true;
        }

        if (CB_CERTIFICATE.isSelected() != test_flag(CS_Constants.ACCT_HAS_TLS_CERT))
        {
            return true;
        }
        if (RB_SSL.isSelected() != test_flag(CS_Constants.ACCT_USE_SSL))
        {
            return true;
        }
        if (RB_TLS_FORCE.isSelected() != test_flag(CS_Constants.ACCT_USE_TLS_FORCE))
        {
            return true;
        }
        if (RB_TLS_IV_AVAIL.isSelected() != test_flag(CS_Constants.ACCT_USE_TLS_IF_AVAIL))
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

        AccountConnectorTypeEntry mte = (AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
        String typ = mte.getType();
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
        AccountConnectorTypeEntry mte;

        try
        {
            mte = (AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
            String name = mte.getName();
        }
        catch (Exception e)
        {
            UserMain.errm_ok(UserMain.getString("Typ_ist_nicht_okay"));
            return false;
        }

        
        String type = mte.getType();
        if (type.compareTo("ldap") == 0)
        {

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
        }
        if (CB_CERTIFICATE.isSelected())
        {
            if (RB_INSECURE.isSelected())
            {
                UserMain.errm_ok(UserMain.getString("Zertifikate_werden_nur_sicheren_Verbindungen_verwendet"));
                return false;
            }
        }


        return true;
    }

    @Override
    protected void set_object_props()
    {
        String server1 = TXT_SERVER1.getText();
        int port1 = Integer.parseInt(TXT_PORT1.getText());
        
        String name = TXT_USERNAME.getText();
        String pwd = get_pwd();
        String type = "";
        AccountConnectorTypeEntry mte = (AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
        type = mte.getType();

        object.setFlags(0);
        object.setIp(server1);
        object.setPort(new Integer(port1));
        
        set_flag( BT_DISABLED.isSelected(), CS_Constants.ACCT_DISABLED);
        if (RB_TLS_IV_AVAIL.isSelected())
            set_flag(true, CS_Constants.ACCT_USE_TLS_IF_AVAIL);
        if (RB_TLS_FORCE.isSelected())
            set_flag(true, CS_Constants.ACCT_USE_TLS_FORCE);
        if (RB_SSL.isSelected())
            set_flag(true, CS_Constants.ACCT_USE_SSL);

        
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
