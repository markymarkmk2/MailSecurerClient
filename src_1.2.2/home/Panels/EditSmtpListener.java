/*
 * EditChannelPanel.java
 *
 * Created on 25. M�rz 2008, 20:06
 */

package dimm.home.Panels;

import home.shared.SQL.SQLResult;
import dimm.home.Rendering.GlossButton;
import dimm.home.UserMain;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import home.shared.hibernate.DiskArchive;
import dimm.home.Models.DiskArchiveComboModel;
import dimm.home.Utilities.SwingWorker;
import home.shared.CS_Constants;
import home.shared.Utilities.Validator;
import home.shared.hibernate.SmtpServer;



/**
 
 @author  Administrator
 */
public class EditSmtpListener extends GenericEditPanel
{
    SmtpListenerOverview object_overview;
    SmtpListenerTableModel model;
    SmtpServer object;
    SmtpServer save_object;
    DiskArchiveComboModel dacm;
       
    
    /** Creates new form EditChannelPanel */
    public EditSmtpListener(int _row, SmtpListenerOverview _overview)
    {
        initComponents();     
        
        object_overview = _overview;
        model = object_overview.get_object_model();
        CB_VAULT.removeAllItems();
        


        SQLResult<DiskArchive> da_res = UserMain.sqc().get_da_result();

        // COMBO-MODEL DISK ARCHIVE
        dacm = new DiskArchiveComboModel(da_res );
                                
        row = _row;
        
        if (!model.is_new(row))
        {
            object = model.get_object(row);
            save_object = new SmtpServer( object );

           

            BT_DISABLED.setSelected(test_flag(CS_Constants.SL_DISABLED));
           
            if (test_flag( CS_Constants.SL_SSL))
                RB_SSL.setSelected(true);
            else if (test_flag( CS_Constants.SL_USE_TLS_IF_AVAIL))
                RB_TLS_IV_AVAIL.setSelected(true);
            else if (test_flag( CS_Constants.SL_USE_TLS_FORCE))
                RB_TLS_FORCE.setSelected(true);
            else
                RB_INSECURE.setSelected(true);



            int da_id = model.getSqlResult().getInt( row, "da_id");
            dacm.set_act_id(da_id);

            TXT_SERVER1.setText( object.getServer());
            TXT_PORT1.setText( object.getPort().toString());
            TXT_USER.setText(object.getUsername());
            TXTP_PWD.setText(object.getPassword());

            
        }
        else
        {
            object = new SmtpServer();
            object.setMandant(UserMain.sqc().get_act_mandant());

        }
        
      

        CB_VAULT.setModel(dacm);
    }
    
    
   
    /** This method is called from within the constructor to
     initialize the form.
     WARNING: Do NOT modify this code. The content of this method is
     always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        PN_ACTION = new javax.swing.JPanel();
        TXT_SERVER1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        BT_DISABLED = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        CB_VAULT = new javax.swing.JComboBox();
        TXT_PORT1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        TXT_USER = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        TXTP_PWD = new javax.swing.JPasswordField();
        jPanel1 = new javax.swing.JPanel();
        RB_INSECURE = new javax.swing.JRadioButton();
        RB_TLS_IV_AVAIL = new javax.swing.JRadioButton();
        RB_TLS_FORCE = new javax.swing.JRadioButton();
        RB_SSL = new javax.swing.JRadioButton();
        PN_BUTTONS = new javax.swing.JPanel();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();
        BT_HELP1 = new GlossButton();

        setDoubleBuffered(false);
        setOpaque(false);

        PN_ACTION.setDoubleBuffered(false);
        PN_ACTION.setOpaque(false);

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
        BT_DISABLED.setOpaque(false);

        jLabel3.setText(UserMain.getString("Speicherziel")); // NOI18N

        CB_VAULT.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

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

        jLabel5.setText(UserMain.getString("User")); // NOI18N

        TXT_USER.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_USERMouseClicked(evt);
            }
        });
        TXT_USER.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TXT_USERActionPerformed(evt);
            }
        });

        jLabel6.setText(UserMain.getString("Password")); // NOI18N

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(RB_TLS_IV_AVAIL)
                            .addComponent(RB_INSECURE, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                            .addComponent(RB_TLS_FORCE))
                        .addGap(63, 63, 63))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(RB_SSL)
                        .addContainerGap(124, Short.MAX_VALUE))))
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                            .addComponent(jLabel2)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(jLabel3))
                        .addGap(61, 61, 61)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CB_VAULT, 0, 196, Short.MAX_VALUE)
                            .addComponent(TXTP_PWD, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                            .addComponent(TXT_USER, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                            .addComponent(TXT_PORT1, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                            .addComponent(TXT_SERVER1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE))
                        .addGap(10, 10, 10))
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 281, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(BT_DISABLED, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        PN_ACTIONLayout.setVerticalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(TXT_SERVER1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(TXT_PORT1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TXT_USER, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(TXTP_PWD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(CB_VAULT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addComponent(BT_DISABLED)))
                .addContainerGap())
        );

        PN_BUTTONS.setDoubleBuffered(false);
        PN_BUTTONS.setOpaque(false);

        BT_OK.setText(UserMain.Txt("Okay")); // NOI18N
        BT_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKActionPerformed(evt);
            }
        });

        BT_ABORT.setText(UserMain.Txt("Abbruch")); // NOI18N
        BT_ABORT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ABORTActionPerformed(evt);
            }
        });

        BT_HELP1.setText(UserMain.Txt("?")); // NOI18N
        BT_HELP1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_HELP1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_BUTTONSLayout = new javax.swing.GroupLayout(PN_BUTTONS);
        PN_BUTTONS.setLayout(PN_BUTTONSLayout);
        PN_BUTTONSLayout.setHorizontalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap(301, Short.MAX_VALUE)
                .addComponent(BT_HELP1)
                .addGap(18, 18, 18)
                .addComponent(BT_ABORT)
                .addGap(18, 18, 18)
                .addComponent(BT_OK)
                .addContainerGap())
        );

        PN_BUTTONSLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ABORT, BT_OK});

        PN_BUTTONSLayout.setVerticalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OK)
                    .addComponent(BT_ABORT)
                    .addComponent(BT_HELP1))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PN_ACTION, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PN_BUTTONS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(PN_ACTION, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 29, Short.MAX_VALUE)
                .addComponent(PN_BUTTONS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKActionPerformed
    {//GEN-HEADEREND:event_BT_OKActionPerformed
        // TODO add your handling code here:
        
        ok_action(object, save_object);
       
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
            UserMain.self.show_vkeyboard( this.my_dlg, TXT_SERVER1, false);
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

    private void TXT_USERMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXT_USERMouseClicked
    {//GEN-HEADEREND:event_TXT_USERMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_TXT_USERMouseClicked

    private void TXT_USERActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TXT_USERActionPerformed
    {//GEN-HEADEREND:event_TXT_USERActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TXT_USERActionPerformed

    private void RB_INSECUREActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RB_INSECUREActionPerformed
    {//GEN-HEADEREND:event_RB_INSECUREActionPerformed
        // TODO add your handling code here:
        
        TXT_PORT1.setText( "" + EditAccountConnector.get_dflt_port( "smtp", false ) );
}//GEN-LAST:event_RB_INSECUREActionPerformed

    private void RB_TLS_IV_AVAILActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RB_TLS_IV_AVAILActionPerformed
    {//GEN-HEADEREND:event_RB_TLS_IV_AVAILActionPerformed
        // TODO add your handling code here:
        TXT_PORT1.setText( "" + EditAccountConnector.get_dflt_port( "smtp", false ) );
    }//GEN-LAST:event_RB_TLS_IV_AVAILActionPerformed

    private void RB_TLS_FORCEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RB_TLS_FORCEActionPerformed
    {//GEN-HEADEREND:event_RB_TLS_FORCEActionPerformed
        // TODO add your handling code here:
        TXT_PORT1.setText( "" + EditAccountConnector.get_dflt_port( "smtp", false ) );
    }//GEN-LAST:event_RB_TLS_FORCEActionPerformed

    private void RB_SSLActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RB_SSLActionPerformed
    {//GEN-HEADEREND:event_RB_SSLActionPerformed
        // TODO add your handling code here:
        TXT_PORT1.setText( "" + EditAccountConnector.get_dflt_port( "smtp", true ) );
    }//GEN-LAST:event_RB_SSLActionPerformed

    private void BT_HELP1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_HELP1ActionPerformed
    {//GEN-HEADEREND:event_BT_HELP1ActionPerformed
        // TODO add your handling code here:
        open_help(this.getClass().getSimpleName());
}//GEN-LAST:event_BT_HELP1ActionPerformed

    SwingWorker sw = null;
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JCheckBox BT_DISABLED;
    private javax.swing.JButton BT_HELP1;
    private javax.swing.JButton BT_OK;
    private javax.swing.JComboBox CB_VAULT;
    private javax.swing.JPanel PN_ACTION;
    private javax.swing.JPanel PN_BUTTONS;
    private javax.swing.JRadioButton RB_INSECURE;
    private javax.swing.JRadioButton RB_SSL;
    private javax.swing.JRadioButton RB_TLS_FORCE;
    private javax.swing.JRadioButton RB_TLS_IV_AVAIL;
    private javax.swing.JPasswordField TXTP_PWD;
    private javax.swing.JTextField TXT_PORT1;
    private javax.swing.JTextField TXT_SERVER1;
    private javax.swing.JTextField TXT_USER;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    int get_object_flags()
    {
        int flags = 0;
        if (object.getFlags() == null || object.getFlags().length() == 0)
            return 0;

        try
        {
            flags = Integer.parseInt(object.getFlags());
        }
        catch (NumberFormatException numberFormatException)
        {
            String object_name = object.getClass().getSimpleName();
            Logger.getLogger("").log(Level.SEVERE, "Invalid flag for " + object_name+ " " + numberFormatException );
        }

        return flags;
    }
    void set_object_flag(int flag)
    {
        int flags = get_object_flags();
        flags |= flag;
        object.setFlags(Integer.toString(flags));
    }
    void clr_object_flag(int flag)
    {
        int flags = get_object_flags();
        flags &= ~flag;
        object.setFlags(Integer.toString(flags));
    }

    boolean object_is_disabled()
    {
        int flags = 0;

        flags = get_object_flags();

        return ((flags & CS_Constants.SL_DISABLED) == CS_Constants.SL_DISABLED);
    }
    void set_object_disabled( boolean f)
    {
        int flags = get_object_flags();

        if (f)
            set_object_flag( CS_Constants.SL_DISABLED );
        else
            clr_object_flag( CS_Constants.SL_DISABLED );
    }
    final boolean test_flag( int test_flag )
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

    
    @Override
    protected boolean check_changed()
    {        
        if (model.is_new(row))
            return true;

        String server = object.getServer();
        if (server == null || TXT_SERVER1.getText().compareTo(server ) != 0)
            return true;

        int port = object.getPort();
        if (Integer.parseInt( TXT_PORT1.getText() ) != port)
            return true;

        String user = object.getUsername();
        if (user == null || TXT_USER.getText().compareTo(user ) != 0)
            return true;

        String pwd = object.getPassword();
        if (pwd == null || get_pwd().compareTo(pwd ) != 0)
            return true;


        if (BT_DISABLED.isSelected() != object_is_disabled())
            return true;

        long da_id = model.getSqlResult().getLong( row, "da_id");

        if ( CB_VAULT.getSelectedItem() != null)
        {
            if (dacm.get_act_id() != da_id)
                return true;
        }


        if (BT_DISABLED.isSelected() != test_flag(CS_Constants.IMF_DISABLED))
        {
            return true;
        }
        if (RB_SSL.isSelected() != test_flag(CS_Constants.IMF_USE_SSL))
        {
            return true;
        }
        if (RB_TLS_FORCE.isSelected() != test_flag(CS_Constants.IMF_USE_TLS_FORCE))
        {
            return true;
        }
        if (RB_TLS_IV_AVAIL.isSelected() != test_flag(CS_Constants.IMF_USE_TLS_IF_AVAIL))
        {
            return true;
        }

        
        return false;
    }

    String get_pwd()
    {
        char[] pwd = TXTP_PWD.getPassword();
        return new String(pwd);
    }
                        
    @Override
    protected boolean is_plausible()
    {

        if (!Validator.is_valid_name( TXT_USER.getText(), 80))
        {
            UserMain.errm_ok(UserMain.getString("Der_User_ist_nicht_okay"));
            return false;
        }
        if (get_pwd().length() == 0 || get_pwd().length() > 80)
        {
            UserMain.errm_ok(UserMain.getString("Das_Passwort_ist_nicht_okay"));
            return false;
        }


        if (!Validator.is_valid_port( TXT_PORT1.getText()))
        {
            UserMain.errm_ok(UserMain.getString("Port_ist_nicht_okay"));
            return false;
        }

        try
        {
            DiskArchive da = dacm.get_selected_da();
            String n = da.getName();
        }
        catch (Exception e)
        {
            UserMain.errm_ok(UserMain.getString("Speicherziel_ist_nicht_okay"));
            return false;
        }



        return true;
    }


    @Override
    protected void set_object_props()
    {
        String server1 = TXT_SERVER1.getText();
        int port1 = Integer.parseInt( TXT_PORT1.getText() );
        String user = TXT_USER.getText();
        String pwd = get_pwd();


        object.setServer(server1);
        object.setPort(port1);

        object.setFlags("0");
        set_flag( BT_DISABLED.isSelected(), CS_Constants.IMF_DISABLED);
        if (RB_TLS_IV_AVAIL.isSelected())
            set_flag(true, CS_Constants.IMF_USE_TLS_IF_AVAIL);
        if (RB_TLS_FORCE.isSelected())
            set_flag(true, CS_Constants.IMF_USE_TLS_FORCE);
        if (RB_SSL.isSelected())
            set_flag(true, CS_Constants.IMF_USE_SSL);

        object.setDiskArchive( dacm.get_selected_da());
        object.setUsername( user );
        object.setPassword(pwd);
        object.setType("");
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
