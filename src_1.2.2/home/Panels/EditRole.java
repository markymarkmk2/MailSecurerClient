/*
 * EditChannelPanel.java
 *
 * Created on 25. M�rz 2008, 20:06
 */

package dimm.home.Panels;

import com.thoughtworks.xstream.XStream;
import home.shared.SQL.SQLResult;
import dimm.home.Models.AccountConnectorComboModel;
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossButton;
import dimm.home.ServerConnect.ConnectionID;
import dimm.home.ServerConnect.ResultSetID;
import dimm.home.ServerConnect.ServerCall;
import dimm.home.ServerConnect.StatementID;
import dimm.home.UserMain;
import dimm.home.Utilities.SwingWorker;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.JButton;
import home.shared.hibernate.Role;
import home.shared.Utilities.Validator;
import home.shared.CS_Constants;
import home.shared.SQL.OptCBEntry;
import home.shared.SQL.SQLArrayResult;
import home.shared.Utilities.ParseToken;
import home.shared.Utilities.ZipUtilities;
import home.shared.filter.ExprEntry;
import home.shared.filter.VarTypeEntry;
import home.shared.hibernate.AccountConnector;
import home.shared.hibernate.RoleOption;
import java.util.ArrayList;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JCheckBox;



/**
 
 @author  Administrator
 */
public class EditRole extends GenericEditPanel
{
    RoleOverview vbox_overview;
    RoleTableModel model;
    Role object;
    Role save_object;
    
    String object_name;

    AccountConnectorComboModel accm;
    
    SQLResult<RoleOption>  option_res;
    String role_filter_save;
    boolean was_4eyes;

    
    /** Creates new form EditChannelPanel */
    public EditRole(int _row, RoleOverview _overview)
    {
        initComponents();     
        
        vbox_overview = _overview;
        model = vbox_overview.get_object_model();

        create_option_buttons();

        SQLResult<AccountConnector> da_res = UserMain.sqc().get_account_result();

        // COMBO-MODEL 
        accm = new AccountConnectorComboModel(da_res );
                                
        row = _row;

        PNL_4EYES.setVisible(false);
        
        if (!model.is_new(row))
        {
            object = model.get_object(row);
            save_object = new Role( object );

            TXT_NAME.setText(object.getName());
            String acm_text = vbox_overview.get_account_match_descr(object.getAccountmatch());
            
            TXT_ACCOUNTMATCH.setText( acm_text );

            
            read_opts_buttons( object.getId() );
            TXT_4EYES_USER.setText(object.getUser4eyes());
            TXTP_4EYES_PWD.setText(object.getPwd4eyes());
            
            if (BT_4EYES.isSelected())
            {
                was_4eyes = true;
                PNL_4EYES.setVisible(true);
            }
            BT_DISABLED.setSelected( object_is_disabled() );
            int ac_id = model.getSqlResult().getInt( row, "ac_id");
            accm.set_act_id(ac_id);

            role_filter_save = object.getAccountmatch();

            set_filter_preview( LogicFilter.get_nice_filter_text( role_filter_save ) );
        }
        else
        {
            object = new Role();
            object.setMandant(UserMain.sqc().get_act_mandant());
            role_filter_save = "";
            set_object_flag(CS_Constants.ROLE_ACM_COMPRESSED);
        }

        CB_ACCOUNT.setModel(accm);
        object_name = object.getClass().getSimpleName();

    }

    void create_option_buttons()
    {
        javax.swing.GroupLayout PN_OPTSLayout = new javax.swing.GroupLayout(PN_OPTS);
        PN_OPTS.setLayout(PN_OPTSLayout);

        ParallelGroup parallel_group = PN_OPTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
        SequentialGroup seq_group = PN_OPTSLayout.createSequentialGroup();

        for (int i = 0; i < OptCBEntry.opt_list.length; i++)
        {
            OptCBEntry optCBEntry = OptCBEntry.opt_list[i];

            // 4-EYES IS ALREADY FIXED INCLUDED (BECAUSE OF UER/PWD)
            if (optCBEntry.getTxt().compareTo(OptCBEntry._4EYES) == 0)
            {
                optCBEntry.setCb(BT_4EYES);
                continue;
            }
            
            JCheckBox cb = new javax.swing.JCheckBox();

            optCBEntry.setTxt(UserMain.Txt(optCBEntry.getToken()));

            cb.setText(optCBEntry.getTxt());
            optCBEntry.setCb(cb);

            parallel_group.addComponent(cb);
            seq_group.addComponent(cb);
            if (i +1 == OptCBEntry.opt_list.length)
                seq_group.addContainerGap(26, Short.MAX_VALUE);
            else
                seq_group.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);

        }

        PN_OPTSLayout.setHorizontalGroup(
            PN_OPTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_OPTSLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup( parallel_group )
                .addContainerGap(380, Short.MAX_VALUE))
        );
        PN_OPTSLayout.setVerticalGroup(
            PN_OPTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup( seq_group )
        );

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
        TXT_NAME = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        BT_DISABLED = new javax.swing.JCheckBox();
        PN_OPTS = new javax.swing.JPanel();
        CP_OPT1 = new javax.swing.JCheckBox();
        CP_OPT2 = new javax.swing.JCheckBox();
        CP_OPT3 = new javax.swing.JCheckBox();
        CP_OPT4 = new javax.swing.JCheckBox();
        CP_OPT5 = new javax.swing.JCheckBox();
        CP_OPT6 = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        CB_ACCOUNT = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        TXT_ACCOUNTMATCH = new javax.swing.JTextArea();
        BT_4EYES = new javax.swing.JCheckBox();
        PNL_4EYES = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        TXT_4EYES_USER = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        TXTP_4EYES_PWD = new javax.swing.JPasswordField();
        PN_BUTTONS = new javax.swing.JPanel();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();
        BT_MATCH_USERS = new GlossButton();

        setDoubleBuffered(false);
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        PN_ACTION.setDoubleBuffered(false);
        PN_ACTION.setOpaque(false);

        jLabel1.setText(UserMain.Txt("Name")); // NOI18N

        TXT_NAME.setText(UserMain.Txt("Neuer_Rolle")); // NOI18N
        TXT_NAME.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_NAMEMouseClicked(evt);
            }
        });
        TXT_NAME.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TXT_NAMEActionPerformed(evt);
            }
        });

        jLabel2.setText(UserMain.getString("Mailkonto")); // NOI18N

        BT_DISABLED.setText(UserMain.getString("Gesperrt")); // NOI18N
        BT_DISABLED.setOpaque(false);

        PN_OPTS.setBorder(javax.swing.BorderFactory.createTitledBorder(UserMain.getString("Options"))); // NOI18N

        CP_OPT1.setText("jCheckBox1");

        CP_OPT2.setText("jCheckBox1");

        CP_OPT3.setText("jCheckBox1");

        CP_OPT4.setText("jCheckBox1");

        CP_OPT5.setText("jCheckBox1");

        CP_OPT6.setText("jCheckBox1");

        javax.swing.GroupLayout PN_OPTSLayout = new javax.swing.GroupLayout(PN_OPTS);
        PN_OPTS.setLayout(PN_OPTSLayout);
        PN_OPTSLayout.setHorizontalGroup(
            PN_OPTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_OPTSLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_OPTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CP_OPT1)
                    .addComponent(CP_OPT2)
                    .addComponent(CP_OPT3)
                    .addComponent(CP_OPT4)
                    .addComponent(CP_OPT5)
                    .addComponent(CP_OPT6))
                .addContainerGap(344, Short.MAX_VALUE))
        );
        PN_OPTSLayout.setVerticalGroup(
            PN_OPTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_OPTSLayout.createSequentialGroup()
                .addComponent(CP_OPT1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CP_OPT2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CP_OPT3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CP_OPT4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CP_OPT5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CP_OPT6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dimm/home/MA_Properties"); // NOI18N
        jLabel3.setText(bundle.getString("Realm")); // NOI18N

        TXT_ACCOUNTMATCH.setColumns(20);
        TXT_ACCOUNTMATCH.setEditable(false);
        TXT_ACCOUNTMATCH.setRows(2);
        TXT_ACCOUNTMATCH.setTabSize(4);
        TXT_ACCOUNTMATCH.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_ACCOUNTMATCHMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TXT_ACCOUNTMATCH);

        BT_4EYES.setText(UserMain.Txt("4EYES")); // NOI18N
        BT_4EYES.setOpaque(false);
        BT_4EYES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_4EYESActionPerformed(evt);
            }
        });

        PNL_4EYES.setOpaque(false);

        jLabel4.setText(UserMain.Txt("User")); // NOI18N

        jLabel5.setText(UserMain.Txt("Password")); // NOI18N

        TXTP_4EYES_PWD.setEditable(false);
        TXTP_4EYES_PWD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXTP_4EYES_PWDMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout PNL_4EYESLayout = new javax.swing.GroupLayout(PNL_4EYES);
        PNL_4EYES.setLayout(PNL_4EYESLayout);
        PNL_4EYESLayout.setHorizontalGroup(
            PNL_4EYESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PNL_4EYESLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PNL_4EYESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4))
                .addGap(48, 48, 48)
                .addGroup(PNL_4EYESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(TXTP_4EYES_PWD)
                    .addComponent(TXT_4EYES_USER, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)))
        );
        PNL_4EYESLayout.setVerticalGroup(
            PNL_4EYESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PNL_4EYESLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PNL_4EYESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(TXT_4EYES_USER, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PNL_4EYESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(TXTP_4EYES_PWD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout PN_ACTIONLayout = new javax.swing.GroupLayout(PN_ACTION);
        PN_ACTION.setLayout(PN_ACTIONLayout);
        PN_ACTIONLayout.setHorizontalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_ACTIONLayout.createSequentialGroup()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PN_ACTIONLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(TXT_NAME, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(CB_ACCOUNT, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 155, Short.MAX_VALUE))
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PN_ACTIONLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PN_OPTS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(BT_DISABLED, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 443, Short.MAX_VALUE)
                            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                                .addComponent(BT_4EYES)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(PNL_4EYES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap())
        );
        PN_ACTIONLayout.setVerticalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(TXT_NAME, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(CB_ACCOUNT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel2))
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addComponent(PN_OPTS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BT_4EYES)
                    .addComponent(PNL_4EYES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BT_DISABLED)
                .addContainerGap(49, Short.MAX_VALUE))
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(PN_ACTION, gridBagConstraints);

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

        BT_MATCH_USERS.setText(UserMain.Txt("Show_matchig_users")); // NOI18N
        BT_MATCH_USERS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_MATCH_USERSActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_BUTTONSLayout = new javax.swing.GroupLayout(PN_BUTTONS);
        PN_BUTTONS.setLayout(PN_BUTTONSLayout);
        PN_BUTTONSLayout.setHorizontalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BT_MATCH_USERS)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        PN_BUTTONSLayout.setVerticalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_MATCH_USERS))
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
        ok_action(object, save_object);
    }//GEN-LAST:event_BT_OKActionPerformed

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ABORTActionPerformed
    {//GEN-HEADEREND:event_BT_ABORTActionPerformed
        // TODO add your handling code here:
        abort_action();
    }//GEN-LAST:event_BT_ABORTActionPerformed

    private void TXT_NAMEMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXT_NAMEMouseClicked
    {//GEN-HEADEREND:event_TXT_NAMEMouseClicked
        // TODO add your handling code here:
        if (UserMain.self.is_touchscreen())
        {
            UserMain.self.show_vkeyboard( this.my_dlg, TXT_NAME, false);
        }
}//GEN-LAST:event_TXT_NAMEMouseClicked

    private void TXT_NAMEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TXT_NAMEActionPerformed
    {//GEN-HEADEREND:event_TXT_NAMEActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_TXT_NAMEActionPerformed

    SwingWorker sw = null;
    private void BT_MATCH_USERSActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_MATCH_USERSActionPerformed
    {//GEN-HEADEREND:event_BT_MATCH_USERSActionPerformed
        // TODO add your handling code here:

        String role_filter_compressed = object.getAccountmatch();
        if (!is_plausible())
            return;
        

        final String cmd = "ListUsers CMD:match_filter MA:" + UserMain.self.get_act_mandant().getId() + " AC:" + accm.get_act_id() + " FLC:'" + role_filter_compressed + "'";

        if (sw != null)
        {
            return;
        }

        sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                UserMain.self.show_busy(my_dlg, UserMain.Txt("Checking_userlist") + "...");
                String ret = UserMain.fcc().call_abstract_function(cmd, ServerCall.SHORT_CMD_TO);
                UserMain.self.hide_busy();
                
                if (ret != null && ret.charAt(0) == '0')
                {
                    Object o = ParseToken.DeCompressObject(ret.substring(3));
                    if (o instanceof ArrayList<?>)
                    {
                        ArrayList<String> list = (ArrayList<String>)o;
                        UserListPanel pnl = new UserListPanel(list);
                        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
                        dlg.set_next_location(my_dlg);
                        dlg.setVisible(true);
                    }
                }
                else
                {
                    UserMain.errm_ok(my_dlg, UserMain.Txt("Check_user_failed") + ": " + ((ret != null) ?  ret.substring(3): ""));
                }
                sw = null;
                return null;
            }
        };

        sw.start();


    }//GEN-LAST:event_BT_MATCH_USERSActionPerformed

    private void TXT_ACCOUNTMATCHMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXT_ACCOUNTMATCHMouseClicked
    {//GEN-HEADEREND:event_TXT_ACCOUNTMATCHMouseClicked
        // TODO add your handling code here:
        edit_user_filter();
    }//GEN-LAST:event_TXT_ACCOUNTMATCHMouseClicked

    private void BT_4EYESActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_4EYESActionPerformed
    {//GEN-HEADEREND:event_BT_4EYESActionPerformed
        // TODO add your handling code here:
        PNL_4EYES.setVisible(BT_4EYES.isSelected());
        if (BT_4EYES.isSelected())
        {
            TXTP_4EYES_PWD.setText("");
            TXT_4EYES_USER.setText("");
        }
    }//GEN-LAST:event_BT_4EYESActionPerformed

    private void TXTP_4EYES_PWDMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXTP_4EYES_PWDMouseClicked
    {//GEN-HEADEREND:event_TXTP_4EYES_PWDMouseClicked
        // TODO add your handling code here:
        CheckPwdPanel pnl = new CheckPwdPanel(UserMain.self, /*trong*/true);
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);        

        dlg.setLocation(my_dlg.get_next_location());
        dlg.setTitle(UserMain.getString("4Augen-Passwort_setzen"));
        dlg.setVisible(true);

        if (pnl.isOkay())
        {
            TXTP_4EYES_PWD.setText(pnl.get_pwd());
        }
    }//GEN-LAST:event_TXTP_4EYES_PWDMouseClicked
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox BT_4EYES;
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JCheckBox BT_DISABLED;
    private javax.swing.JButton BT_MATCH_USERS;
    private javax.swing.JButton BT_OK;
    private javax.swing.JComboBox CB_ACCOUNT;
    private javax.swing.JCheckBox CP_OPT1;
    private javax.swing.JCheckBox CP_OPT2;
    private javax.swing.JCheckBox CP_OPT3;
    private javax.swing.JCheckBox CP_OPT4;
    private javax.swing.JCheckBox CP_OPT5;
    private javax.swing.JCheckBox CP_OPT6;
    private javax.swing.JPanel PNL_4EYES;
    private javax.swing.JPanel PN_ACTION;
    private javax.swing.JPanel PN_BUTTONS;
    private javax.swing.JPanel PN_OPTS;
    private javax.swing.JPasswordField TXTP_4EYES_PWD;
    private javax.swing.JTextField TXT_4EYES_USER;
    private javax.swing.JTextArea TXT_ACCOUNTMATCH;
    private javax.swing.JTextField TXT_NAME;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    int get_object_flags()
    {
        return get_object_flags(object);
    }
    int get_object_flags(Role r)
    {
        int flags = 0;
        if (r.getFlags() == null || r.getFlags().length() == 0)
            return 0;

        try
        {
            flags = Integer.parseInt(r.getFlags());
        }
        catch (NumberFormatException numberFormatException)
        {
            Logger.getLogger("").log(Level.SEVERE, "Invalid flag for " + object_name + " " + numberFormatException );
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

        return ((flags & CS_Constants.ROLE_DISABLED) == CS_Constants.ROLE_DISABLED);
    }
    void set_object_disabled( boolean f)
    {
        if (f)
            set_object_flag( CS_Constants.ROLE_DISABLED);
        else
            clr_object_flag( CS_Constants.ROLE_DISABLED);
    }

    
    @Override
    protected boolean check_changed()
    {        
        if (model.is_new(row))
            return true;

        String name = object.getName();
        if (name == null || TXT_NAME.getText().compareTo(name ) != 0)
            return true;   
        
        if (BT_DISABLED.isSelected() != object_is_disabled())
            return true;

      
        long ac_id = model.getSqlResult().getLong( row, "ac_id");

        if ( CB_ACCOUNT.getSelectedItem() != null)
        {
            if (accm.get_act_id() != ac_id)
                return true;
        }

        if (check_opts_buttons_changed())
            return true;

        if (object.getAccountmatch().compareTo(role_filter_save) != 0)
            return true;

        if (BT_4EYES.isSelected())
        {
            if (object.getUser4eyes() == null)
                return true;

            if (object.getUser4eyes().compareTo( TXT_4EYES_USER.getText()) != 0)
                return true;

            if (object.getPwd4eyes() == null)
                return true;

            String pwd = new String( TXTP_4EYES_PWD.getPassword() );
            if (object.getPwd4eyes().compareTo( pwd) != 0)
                return true;
        }

        return false;
    }
                        
    @Override
    protected boolean is_plausible()
    {
        if (!Validator.is_valid_path( TXT_NAME.getText(), 255))
        {
            UserMain.errm_ok(UserMain.getString("Der_Pfad_ist_nicht_okay"));
            return false;
        }
        try
        {
            AccountConnector da = accm.get_selected_ac();
            String n = accm.getName(da);
        }
        catch (Exception e)
        {
            UserMain.errm_ok(UserMain.getString("Realm_ist_nicht_okay"));
            return false;
        }
        if (object.getAccountmatch() == null || object.getAccountmatch().length() == 0)
        {
            UserMain.errm_ok(UserMain.getString("Benutzerfilter_ist_nicht_okay"));
            return false;
        }
        if (BT_4EYES.isSelected())
        {
            if (!Validator.is_valid_name(TXT_4EYES_USER.getText(), 80))
            {
                UserMain.errm_ok(UserMain.getString("Der_4-Augen_Username_ist_nicht_okay"));
                return false;
            }
            String pwd = new String( TXTP_4EYES_PWD.getPassword() );
            if (!Validator.is_valid_name(pwd, 80))
            {
                UserMain.errm_ok(UserMain.getString("Das_4-Augen_Passwort_ist_nicht_okay"));
                return false;
            }
        }

                
        return true;
    }

    

    @Override
    protected void set_object_props()
    {
        String opts = "";
        String name = TXT_NAME.getText();        
        boolean de = BT_DISABLED.isSelected();


        //String ac = TXT_ACCOUNTMATCH.getText();

        object.setName(name);
        object.setOpts(opts);
        
        object.setLicense( new Integer(0));
        object.setAccountConnector( accm.get_selected_ac());

        object.setUser4eyes(TXT_4EYES_USER.getText());
        String pwd = new String( TXTP_4EYES_PWD.getPassword() );
        object.setPwd4eyes(pwd);

        set_object_disabled( de );
    }
    public String get_option_qry(long role_id)
    {
        String qry = "select * from role_option where ro_id=" + role_id + " order by id";
        return qry;
    }

    @Override
    public boolean  update_db( Object o, Object so)
    {
        if (was_4eyes)
        {
            // CHECK WITH THE OLD OBJECT
            if (!Login4EyesPanel.check_login(save_object))
            {
                return false;
            }
        }
        boolean ret = super.update_db(o, so);
        if (ret)
        {
            ret = write_opts_buttons( this.object.getId() );
        }
        return ret;
    }
    
    
        
    @Override
    protected boolean is_new()
    {
        return model.is_new(row);
    }
   

    @Override
    public JButton get_default_button()
    {
        return BT_OK;
    }

    private void read_opts_buttons( int id)
    {
        ServerCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open();
        StatementID sid = sql.createStatement(cid);


        String qry = get_option_qry( id );
        ResultSetID rid = sql.executeQuery(sid, qry);

        SQLArrayResult resa = sql.get_sql_array_result(rid);

        option_res = new SQLResult<RoleOption>(UserMain.sqc(), resa, new RoleOption().getClass());

        for (int i = 0; i < OptCBEntry.opt_list.length; i++)
        {
            OptCBEntry optCBEntry = OptCBEntry.opt_list[i];

            RoleOption  ro = get_option_for_token( optCBEntry.getToken() );

            if (ro != null)
            {
                optCBEntry.getCb().setSelected(true);
            }
            else
            {
                optCBEntry.getCb().setSelected(false);
            }
        }

        sql.close(cid);

    }
    private boolean  write_opts_buttons( int id)
    {
        ServerCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open();
        StatementID sid = sql.createStatement(cid);

        boolean ret = true;


        for (int i = 0; i < OptCBEntry.opt_list.length; i++)
        {
            OptCBEntry optCBEntry = OptCBEntry.opt_list[i];

            RoleOption  ro = get_option_for_token( optCBEntry.getToken() );

            if (ro != null && !optCBEntry.getCb().isSelected())
            {
                sql.Delete(sid, ro);
            }
            if (ro == null && optCBEntry.getCb().isSelected())
            {
                ro = new RoleOption( 0, object, optCBEntry.getToken(), 0);
                if (!sql.Insert(sid, ro))
                    ret = false;
            }
        }

        sql.close(cid);

        return ret;

    }

    private RoleOption get_option_for_token(String token)
    {
        // NEW ?
        if (option_res == null)
            return null;

        for (int j = 0; j < option_res.size(); j++)
        {
            try
            {
                RoleOption roleOption = option_res.get(j);
                if (roleOption.getToken().compareTo(token) == 0)
                {
                    return roleOption;
                }
            }
            catch (Exception e)
            {
                System.out.println("Exception in get_option_for_token(): " + e.getMessage());
            }
        }
        return null;
    }

    private boolean check_opts_buttons_changed()
    {
        boolean changed = false;

        for (int i = 0; i < OptCBEntry.opt_list.length; i++)
        {
            OptCBEntry optCBEntry = OptCBEntry.opt_list[i];

            RoleOption  ro = get_option_for_token( optCBEntry.getToken() );
            if (ro != null && !optCBEntry.getCb().isSelected())
            {
                changed = true;
                break;
            }
            if (ro == null && optCBEntry.getCb().isSelected())
            {
                changed = true;
                break;
            }
        }
        return changed;
    }

    @Override
    protected boolean insert_db(Object object)
    {
        boolean ret = super.insert_db(object);
        if (ret)
        {
            ret = write_opts_buttons( this.object.getId() );
        }
        return ret;

    }

    private void set_filter_preview( String _nice_filter_text )
    {                   
        TXT_ACCOUNTMATCH.setText(_nice_filter_text);
        TXT_ACCOUNTMATCH.setCaretPosition(0);

    }

    private void edit_user_filter()
    {
        try
        {
            ArrayList<VarTypeEntry> var_names = new ArrayList<VarTypeEntry>();
            var_names.add(new VarTypeEntry("Username", ExprEntry.TYPE.STRING) );
            var_names.add(new VarTypeEntry("Email", ExprEntry.TYPE.STRING) );
            var_names.add(new VarTypeEntry("Domain", ExprEntry.TYPE.STRING) );
            var_names.add(new VarTypeEntry("Group", ExprEntry.TYPE.STRING) );
            
            
            LogicFilter rf = new LogicFilter(var_names, object.getAccountmatch());

            GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, rf);
            dlg.setVisible(true);

            if (rf.isOkay())
            {
                 String role_filter_xml = rf.get_compressed_xml_list_data();
                 object.setAccountmatch(role_filter_xml);
                 set_filter_preview( LogicFilter.get_nice_filter_text( role_filter_xml) );
            }
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
    }
    
   
}