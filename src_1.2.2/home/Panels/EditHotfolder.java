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
import home.shared.hibernate.Hotfolder;
import dimm.home.Models.DiskArchiveComboModel;
import dimm.home.Panels.FileSystem.GlossFileChooser;
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.ServerConnect.RMXFileSystemView;
import home.shared.Utilities.Validator;
import home.shared.CS_Constants;



/**
 
 @author  Administrator
 */
public class EditHotfolder extends GenericEditPanel
{
    HotfolderOverview vbox_overview;
    HotfolderTableModel model;
    Hotfolder object;
    Hotfolder save_object;
    DiskArchiveComboModel dacm;
    String object_name;
    
    
    /** Creates new form EditChannelPanel */
    public EditHotfolder(int _row, HotfolderOverview _overview)
    {
        initComponents();     
        
        vbox_overview = _overview;
        model = vbox_overview.get_object_model();
        CB_VAULT.removeAllItems();
        CB_MAILACCOUNT.removeAllItems();

        SQLResult<DiskArchive> da_res = UserMain.sqc().get_da_result();

        dacm = new DiskArchiveComboModel(da_res );

                                
        row = _row;
        
        if (!model.is_new(row))
        {
            object = model.get_object(row);
            save_object = new Hotfolder( object );


            TXT_PATH.setText(object.getPath());
            CB_MAILACCOUNT.addItem( object.getUsermailadress() );
            BT_DISABLED.setSelected( object_is_disabled() );

            int da_id = model.getSqlResult().getInt( row, "da_id");
            dacm.set_act_id(da_id);
            
        }
        else
        {
            object = new Hotfolder();
            object.setMandant(UserMain.sqc().get_act_mandant());
        }

        object_name = object.getClass().getSimpleName();

        CB_VAULT.setModel(dacm);
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
        TXT_PATH = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        BT_DISABLED = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        CB_VAULT = new javax.swing.JComboBox();
        CB_MAILACCOUNT = new javax.swing.JComboBox();
        BT_SELECT_PATH = new javax.swing.JButton();
        PN_BUTTONS = new javax.swing.JPanel();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();
        BT_HELP1 = new GlossButton();

        setDoubleBuffered(false);
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        PN_ACTION.setDoubleBuffered(false);
        PN_ACTION.setOpaque(false);

        jLabel1.setText(UserMain.Txt("Pfad")); // NOI18N

        TXT_PATH.setText(UserMain.Txt("Neuer_Pfad")); // NOI18N
        TXT_PATH.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_PATHMouseClicked(evt);
            }
        });
        TXT_PATH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TXT_PATHActionPerformed(evt);
            }
        });

        jLabel2.setText(UserMain.getString("Mailkonto")); // NOI18N

        BT_DISABLED.setText(UserMain.getString("Gesperrt")); // NOI18N
        BT_DISABLED.setOpaque(false);

        jLabel3.setText(UserMain.getString("Speicherziel")); // NOI18N

        jLabel5.setText("(eMail)");

        CB_VAULT.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        CB_MAILACCOUNT.setEditable(true);
        CB_MAILACCOUNT.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        BT_SELECT_PATH.setText("...");
        BT_SELECT_PATH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_SELECT_PATHActionPerformed(evt);
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
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                                .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(10, 10, 10)
                                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(CB_MAILACCOUNT, 0, 236, Short.MAX_VALUE)
                                    .addComponent(CB_VAULT, javax.swing.GroupLayout.Alignment.LEADING, 0, 236, Short.MAX_VALUE)))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_ACTIONLayout.createSequentialGroup()
                                .addComponent(TXT_PATH, javax.swing.GroupLayout.DEFAULT_SIZE, 223, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(BT_SELECT_PATH))))
                    .addComponent(BT_DISABLED))
                .addContainerGap())
        );
        PN_ACTIONLayout.setVerticalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(TXT_PATH, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_SELECT_PATH))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CB_MAILACCOUNT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CB_VAULT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 39, Short.MAX_VALUE)
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
                .addContainerGap(129, Short.MAX_VALUE)
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

    private void TXT_PATHMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXT_PATHMouseClicked
    {//GEN-HEADEREND:event_TXT_PATHMouseClicked
        // TODO add your handling code here:
        if (UserMain.self.is_touchscreen())
        {
            UserMain.self.show_vkeyboard( this.my_dlg, TXT_PATH, false);
        }
}//GEN-LAST:event_TXT_PATHMouseClicked

    private void TXT_PATHActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TXT_PATHActionPerformed
    {//GEN-HEADEREND:event_TXT_PATHActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_TXT_PATHActionPerformed

    private void BT_SELECT_PATHActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_SELECT_PATHActionPerformed
    {//GEN-HEADEREND:event_BT_SELECT_PATHActionPerformed
        // TODO add your handling code here:
        RMXFileSystemView fsv = new RMXFileSystemView(UserMain.fcc());
        GlossFileChooser gfc = new GlossFileChooser(fsv, object.getPath(), null, true);
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, gfc);
        dlg.setVisible(true);


        if (gfc.get_act_file() != null)
        {
            TXT_PATH.setText(gfc.get_act_file().getAbsolutePath());
        }

    }//GEN-LAST:event_BT_SELECT_PATHActionPerformed

    private void BT_HELP1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_HELP1ActionPerformed
    {//GEN-HEADEREND:event_BT_HELP1ActionPerformed
        // TODO add your handling code here:
        open_help(this.getClass().getSimpleName());
}//GEN-LAST:event_BT_HELP1ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JCheckBox BT_DISABLED;
    private javax.swing.JButton BT_HELP1;
    private javax.swing.JButton BT_OK;
    private javax.swing.JButton BT_SELECT_PATH;
    private javax.swing.JComboBox CB_MAILACCOUNT;
    private javax.swing.JComboBox CB_VAULT;
    private javax.swing.JPanel PN_ACTION;
    private javax.swing.JPanel PN_BUTTONS;
    private javax.swing.JTextField TXT_PATH;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
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

        return ((flags & CS_Constants.HF_FLAG_DISABLED) == CS_Constants.HF_FLAG_DISABLED);
    }
    void set_object_disabled( boolean f)
    {
        int flags = get_object_flags();

        if (f)
            set_object_flag( CS_Constants.HF_FLAG_DISABLED );
        else
            clr_object_flag( CS_Constants.HF_FLAG_DISABLED );
    }

    
    @Override
    protected boolean check_changed()
    {        
        if (model.is_new(row))
            return true;

        String path = object.getPath();
        if (path != null && TXT_PATH.getText().compareTo(path ) != 0)
            return true;   
        
        if (BT_DISABLED.isSelected() != object_is_disabled())
            return true;

        long da_id = model.getSqlResult().getLong( row, "da_id");

        if ( CB_VAULT.getSelectedItem() != null)
        {
            if (dacm.get_act_id() != da_id)
                return true;
        }

        String email = get_email_cb_txt();
        if (object.getUsermailadress().compareTo(email) != 0)
            return true;
        
        return false;
    }
                        
    @Override
    protected boolean is_plausible()
    {
        if (!Validator.is_valid_path( TXT_PATH.getText(), 255))
        {
            UserMain.errm_ok(UserMain.getString("Der_Pfad_ist_nicht_okay"));
            return false;
        }
        try
        {
            String email = get_email_cb_txt();

            if (!Validator.is_valid_email(email))
            {
                UserMain.errm_ok(UserMain.getString("Email_ist_nicht_okay"));
                return false;
            }
        }
        catch (Exception exc)
        {
            UserMain.errm_ok(UserMain.getString("Email_ist_nicht_okay"));
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

    
    String get_email_cb_txt()
    {
        String email = null;
        
        if (CB_MAILACCOUNT.getEditor().getItem() != null)
            email = CB_MAILACCOUNT.getEditor().getItem().toString();
        else if (CB_MAILACCOUNT.getSelectedItem() != null)
            email = CB_MAILACCOUNT.getSelectedItem().toString();

        return email;
    }

    @Override
    protected void set_object_props()
    {
        String path = TXT_PATH.getText();
        String email = get_email_cb_txt();
        boolean de = BT_DISABLED.isSelected();

        object.setPath(path);
        object.setUsermailadress(email);
        set_object_disabled( de );
        object.setDiskArchive( dacm.get_selected_da());
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
    
}
