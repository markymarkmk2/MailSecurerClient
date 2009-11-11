/*
 * EditChannelPanel.java
 *
 * Created on 25. M�rz 2008, 20:06
 */

package dimm.home.Panels;

import dimm.general.SQL.SQLResult;
import dimm.home.Models.AccountConnectorComboModel;
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossButton;
import dimm.home.ServerConnect.ConnectionID;
import dimm.home.ServerConnect.ResultSetID;
import dimm.home.ServerConnect.ServerCall;
import dimm.home.ServerConnect.StatementID;
import dimm.home.UserMain;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.JButton;
import home.shared.hibernate.Role;
import dimm.home.Utilities.Validator;
import home.shared.CS_Constants;
import home.shared.SQL.OptCBEntry;
import home.shared.SQL.SQLArrayResult;
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
    
    String object_name;

    AccountConnectorComboModel accm;
    
    SQLResult<RoleOption>  option_res;
    String role_filter_save;

    
    /** Creates new form EditChannelPanel */
    public EditRole(int _row, RoleOverview _overview)
    {
        initComponents();     
        
        vbox_overview = _overview;
        model = vbox_overview.get_object_model();

        create_option_buttons();

        SQLResult<AccountConnector> da_res = UserMain.sqc().get_account_result();

        // COMBO-MODEL DISK ARCHIVE
        accm = new AccountConnectorComboModel(da_res );
                                
        row = _row;
        
        if (!model.is_new(row))
        {
            object = model.get_object(row);


            TXT_NAME.setText(object.getName());
            TXT_ACCOUNTMATCH.setText( vbox_overview.get_account_match_descr(object.getAccountmatch()));
            String opts = object.getOpts();
            read_opts_buttons( object.getId() );
            BT_DISABLED.setSelected( object_is_disabled() );
            int ac_id = model.getSqlResult().getInt( row, "ac_id");
            accm.set_act_id(ac_id);

            role_filter_save = object.getAccountmatch();

            set_filter_preview( RoleFilter.get_nice_filter_text( role_filter_save ) );
        }
        else
        {
            object = new Role();
            object.setMandant(UserMain.sqc().get_act_mandant());
            role_filter_save = "";
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
        BT_EDIT_MATCH = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        CB_ACCOUNT = new javax.swing.JComboBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        TXT_ACCOUNTMATCH = new javax.swing.JTextArea();
        PN_BUTTONS = new javax.swing.JPanel();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();

        setDoubleBuffered(false);
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        PN_ACTION.setDoubleBuffered(false);
        PN_ACTION.setOpaque(false);

        jLabel1.setText(UserMain.Txt("Name")); // NOI18N

        TXT_NAME.setText(UserMain.Txt("Neuer_Pfad")); // NOI18N
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
                .addContainerGap(380, Short.MAX_VALUE))
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
                .addContainerGap(26, Short.MAX_VALUE))
        );

        BT_EDIT_MATCH.setText("...");
        BT_EDIT_MATCH.setMargin(new java.awt.Insets(2, 5, 2, 5));
        BT_EDIT_MATCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_EDIT_MATCHActionPerformed(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dimm/home/MA_Properties"); // NOI18N
        jLabel3.setText(bundle.getString("Realm")); // NOI18N

        CB_ACCOUNT.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        TXT_ACCOUNTMATCH.setColumns(20);
        TXT_ACCOUNTMATCH.setEditable(false);
        TXT_ACCOUNTMATCH.setRows(2);
        TXT_ACCOUNTMATCH.setTabSize(4);
        jScrollPane1.setViewportView(TXT_ACCOUNTMATCH);

        javax.swing.GroupLayout PN_ACTIONLayout = new javax.swing.GroupLayout(PN_ACTION);
        PN_ACTION.setLayout(PN_ACTIONLayout);
        PN_ACTIONLayout.setHorizontalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TXT_NAME, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CB_ACCOUNT, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BT_EDIT_MATCH)
                .addContainerGap())
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(BT_DISABLED, javax.swing.GroupLayout.DEFAULT_SIZE, 479, Short.MAX_VALUE)
                .addContainerGap(11, Short.MAX_VALUE))
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PN_OPTS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(11, 11, 11))
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
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(BT_EDIT_MATCH)))
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 49, Short.MAX_VALUE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PN_OPTS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BT_DISABLED)
                .addContainerGap())
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

        javax.swing.GroupLayout PN_BUTTONSLayout = new javax.swing.GroupLayout(PN_BUTTONS);
        PN_BUTTONS.setLayout(PN_BUTTONSLayout);
        PN_BUTTONSLayout.setHorizontalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap(312, Short.MAX_VALUE)
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

    private void BT_EDIT_MATCHActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_EDIT_MATCHActionPerformed
    {//GEN-HEADEREND:event_BT_EDIT_MATCHActionPerformed
        // TODO add your handling code here:
       
        try
        {
            ArrayList<String> var_names = new ArrayList<String>();
            var_names.add("Username");
            var_names.add("Email");
            var_names.add("Domain");
            RoleFilter rf = new RoleFilter(var_names, object.getAccountmatch());

            GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, rf);
            dlg.setVisible(true);

            if (rf.isOkay())
            {
                 String role_filter_xml = rf.get_compressed_xml_list_data();
                 object.setAccountmatch(role_filter_xml);
                 set_filter_preview( RoleFilter.get_nice_filter_text( role_filter_xml ) );
            }
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }


    }//GEN-LAST:event_BT_EDIT_MATCHActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JCheckBox BT_DISABLED;
    private javax.swing.JButton BT_EDIT_MATCH;
    private javax.swing.JButton BT_OK;
    private javax.swing.JComboBox CB_ACCOUNT;
    private javax.swing.JCheckBox CP_OPT1;
    private javax.swing.JCheckBox CP_OPT2;
    private javax.swing.JCheckBox CP_OPT3;
    private javax.swing.JCheckBox CP_OPT4;
    private javax.swing.JCheckBox CP_OPT5;
    private javax.swing.JCheckBox CP_OPT6;
    private javax.swing.JPanel PN_ACTION;
    private javax.swing.JPanel PN_BUTTONS;
    private javax.swing.JPanel PN_OPTS;
    private javax.swing.JTextArea TXT_ACCOUNTMATCH;
    private javax.swing.JTextField TXT_NAME;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
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
        int flags = get_object_flags();

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

      
        String ac = object.getAccountmatch();

        if (ac == null || TXT_ACCOUNTMATCH.getText().compareTo(ac) != 0)
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
        if (object.getAccountmatch().length() == 0)
        {
            UserMain.errm_ok(UserMain.getString("Benutzerfilter_ist_nicht_okay"));
            return false;
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
        set_object_disabled( de );
    }
    public String get_option_qry(long role_id)
    {
        String qry = "select * from role_option where ro_id=" + role_id + " order by id";
        return qry;
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

        option_res = new SQLResult<RoleOption>(resa, new RoleOption().getClass());

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
    protected boolean update_db(Object object)
    {
        boolean ret = super.update_db(object);
        if (ret)
        {
            ret = write_opts_buttons( this.object.getId() );
        }
        return ret;

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
    }
    
   
}
