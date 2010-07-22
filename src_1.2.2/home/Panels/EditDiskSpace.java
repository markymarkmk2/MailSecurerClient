/*
 * EditChannelPanel.java
 *
 * Created on 25. M�rz 2008, 20:06
 */

package dimm.home.Panels;

import dimm.home.Panels.FileSystem.GlossFileChooser;
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossButton;
import dimm.home.ServerConnect.RMXFileSystemView;
import dimm.home.UserMain;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import home.shared.hibernate.DiskArchive;
import home.shared.hibernate.DiskSpace;
import home.shared.Utilities.Validator;
import home.shared.CS_Constants;





class DS_mode_entry
{
    String txt;
    int flags;

    DS_mode_entry( String t, int f )
    {
        txt = t;
        flags = f;
    }
    @Override
    public String toString()
    {
        return txt;
    }
}
/**
 
 @author  Administrator
 */
public class EditDiskSpace extends GenericEditPanel
{
    DiskSpaceOverview object_overview;
    DiskSpaceTableModel model;
    DiskSpace object;
    DiskSpace save_object;
    
    
    /** Creates new form EditChannelPanel */
    public EditDiskSpace(DiskArchive da, int _row, DiskSpaceOverview _overview)
    {
        initComponents();     

        if (!UserMain.self.is_admin())
             BT_REINDEX.setVisible(false);

        object_overview = _overview;
        model = object_overview.get_object_model();
        
        CB_STATUS.removeAllItems();
        CB_MODE.removeAllItems();
        CB_CAP_DIM.removeAllItems();

        CB_CAP_DIM.addItem("MB");
        CB_CAP_DIM.addItem("GB");
        CB_CAP_DIM.addItem("TB");

        // FILL COMBOBOX TYPE
        for (int i = 0; i < object_overview.get_ds_entry_list().length; i++)
        {
            DiskSpaceOverview.DiskSpaceTypeEntry mte = object_overview.get_ds_entry_list()[i];
            CB_STATUS.addItem( mte );
        }
        // FILL COMBOBOX MODE
        CB_MODE.addItem( new DS_mode_entry( UserMain.Txt("Index_and_Data"), CS_Constants.DS_MODE_BOTH)  );
        CB_MODE.addItem( new DS_mode_entry( UserMain.Txt("Index_only"), CS_Constants.DS_MODE_INDEX)  );
        CB_MODE.addItem( new DS_mode_entry( UserMain.Txt("Data_only"), CS_Constants.DS_MODE_DATA )  );
                               
        row = _row;
        
        if (!model.is_new(row))
        {
            object = model.get_object(row);
            save_object = new DiskSpace( object );

            String status = object.getStatus();
            for (int i = 0; i < object_overview.get_ds_entry_list().length; i++)
            {
                DiskSpaceOverview.DiskSpaceTypeEntry mte = object_overview.get_ds_entry_list()[i];
                if (mte.type.compareTo(status)== 0)
                {
                    CB_STATUS.setSelectedIndex(i);
                    break;
                }
            }
            TXT_PATH.setText(object.getPath());
            TXT_CAP.setText( get_capa_val( object.getMaxCapacity() ));
            CB_CAP_DIM.setSelectedItem(get_capa_dim( object.getMaxCapacity() ) );

            BT_DISABLED.setSelected( object_is_disabled() );

            int mode = object_get_mode();
            if (mode == CS_Constants.DS_MODE_BOTH)
                CB_MODE.setSelectedIndex(0);
            else if (mode == CS_Constants.DS_MODE_INDEX)
                CB_MODE.setSelectedIndex(1);
            else if (mode == CS_Constants.DS_MODE_DATA)
                CB_MODE.setSelectedIndex(2);
        }
        else
        {
            object = new DiskSpace();
            CB_STATUS.setSelectedIndex(0);
            CB_MODE.setSelectedIndex(0);  // DATA + INDEX
            object.setDiskArchive(da);
            TXT_PATH.setText(UserMain.Txt("Neuer_Pfad") );
            TXT_CAP.setText( "100");
            CB_CAP_DIM.setSelectedItem("GB" );
            BT_DISABLED.setSelected( false );

        }
    }

    String get_capa_dim( String cap)
    {
        int idx = cap.indexOf(' ');
        if (idx > 0)
        {
            return cap.substring(idx + 1);
        }
        return "";
    }

    String get_capa_val( String cap)
    {
        if (cap == null)
            return "_";
        if ( cap.compareTo("-") == 0)
            return "-";

        String val;
        int idx = cap.indexOf(' ');
        if (idx > 0)
        {
            val = cap.substring(0, idx);
        }
        else
            val = cap;
        try
        {
            long l = Long.parseLong(val);
            return val;
        }
        catch (Exception exc)
        {
            return "invalid";
        }
    }

    String build_capa_str( String val, String dim )
    {
        return val + " " + dim;
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
        TXT_PATH = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        BT_DISABLED = new javax.swing.JCheckBox();
        TXT_CAP = new javax.swing.JTextField();
        CB_CAP_DIM = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        CB_STATUS = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        CB_MODE = new javax.swing.JComboBox();
        BT_PATHSELECT = new GlossButton();
        PN_BUTTONS = new javax.swing.JPanel();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();
        BT_REINDEX = new GlossButton();

        setDoubleBuffered(false);
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        PN_ACTION.setDoubleBuffered(false);
        PN_ACTION.setOpaque(false);

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

        jLabel2.setText(UserMain.getString("Path")); // NOI18N

        BT_DISABLED.setText(UserMain.getString("Gesperrt")); // NOI18N
        BT_DISABLED.setOpaque(false);

        CB_CAP_DIM.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "MB", "GB", "TB" }));

        jLabel3.setText(UserMain.getString("Kapazität")); // NOI18N

        jLabel4.setText(UserMain.getString("Status")); // NOI18N

        CB_STATUS.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setText(UserMain.getString("Mode")); // NOI18N

        CB_MODE.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        BT_PATHSELECT.setText("...");
        BT_PATHSELECT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_PATHSELECTActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_ACTIONLayout = new javax.swing.GroupLayout(PN_ACTION);
        PN_ACTION.setLayout(PN_ACTIONLayout);
        PN_ACTIONLayout.setHorizontalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BT_DISABLED, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                                .addComponent(TXT_PATH, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(BT_PATHSELECT))
                            .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(CB_STATUS, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(CB_MODE, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PN_ACTIONLayout.createSequentialGroup()
                                    .addComponent(TXT_CAP, javax.swing.GroupLayout.PREFERRED_SIZE, 88, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(CB_CAP_DIM, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addContainerGap(17, Short.MAX_VALUE))
        );
        PN_ACTIONLayout.setVerticalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(TXT_PATH, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_PATHSELECT))
                .addGap(6, 6, 6)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_CAP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(CB_CAP_DIM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CB_MODE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CB_STATUS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
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

        BT_OK.setText(UserMain.Txt("OK")); // NOI18N
        BT_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKActionPerformed(evt);
            }
        });

        BT_ABORT.setText(UserMain.Txt("Abort")); // NOI18N
        BT_ABORT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ABORTActionPerformed(evt);
            }
        });

        BT_REINDEX.setText(UserMain.Txt("ReIndex")); // NOI18N
        BT_REINDEX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_REINDEXActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_BUTTONSLayout = new javax.swing.GroupLayout(PN_BUTTONS);
        PN_BUTTONS.setLayout(PN_BUTTONSLayout);
        PN_BUTTONSLayout.setHorizontalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BT_REINDEX)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 242, Short.MAX_VALUE)
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
                    .addComponent(BT_REINDEX))
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

    private void BT_REINDEXActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_REINDEXActionPerformed
    {//GEN-HEADEREND:event_BT_REINDEXActionPerformed
        // TODO add your handling code here:
        if (BT_DISABLED.isSelected())
            return;

        int mode_flags = ((DS_mode_entry)CB_MODE.getSelectedItem()).flags;
        if (mode_flags == CS_Constants.DS_MODE_INDEX)
        {
            UserMain.errm_ok(UserMain.Txt("Sie_koennen_nur_DiskSpaces_mit_Daten_neu_indizieren"));
            return;
        }
        ReIndexPanel pnl = new ReIndexPanel(object.getDiskArchive().getId(), object.getId());
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
        dlg.set_next_location(my_dlg);
        dlg.setVisible(true);
    }//GEN-LAST:event_BT_REINDEXActionPerformed

    private void BT_PATHSELECTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_PATHSELECTActionPerformed
    {//GEN-HEADEREND:event_BT_PATHSELECTActionPerformed
        
        RMXFileSystemView fsv = new RMXFileSystemView(UserMain.fcc());
        GlossFileChooser gfc = new GlossFileChooser(fsv, object.getPath(), null, true);
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, gfc);
        dlg.setVisible(true);

        if ( gfc.get_act_file() == null)
        {
            return;
        }

        TXT_PATH.setText(gfc.get_act_file().getAbsolutePath());

    }//GEN-LAST:event_BT_PATHSELECTActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JCheckBox BT_DISABLED;
    private javax.swing.JButton BT_OK;
    private javax.swing.JButton BT_PATHSELECT;
    private javax.swing.JButton BT_REINDEX;
    private javax.swing.JComboBox CB_CAP_DIM;
    private javax.swing.JComboBox CB_MODE;
    private javax.swing.JComboBox CB_STATUS;
    private javax.swing.JPanel PN_ACTION;
    private javax.swing.JPanel PN_BUTTONS;
    private javax.swing.JTextField TXT_CAP;
    private javax.swing.JTextField TXT_PATH;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
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
    void set_object_flags(int flags)
    {
        object.setFlags(Integer.toString(flags));
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

        return ((flags & CS_Constants.DS_DISABLED) == CS_Constants.DS_DISABLED);
    }
    int object_get_mode()
    {
        int flags = 0;

        flags = get_object_flags();

        return (flags & CS_Constants.DS_MODE_BOTH);
    }
    void set_object_disabled( boolean f)
    {
        int flags = get_object_flags();

        if (f)
            set_object_flag( CS_Constants.DS_DISABLED );
        else
            clr_object_flag( CS_Constants.DS_DISABLED );
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

        DiskSpaceOverview.DiskSpaceTypeEntry dsty = (DiskSpaceOverview.DiskSpaceTypeEntry)CB_STATUS.getSelectedItem();
        String status = object.getStatus();
        if (dsty.type.compareTo(status) != 0)
            return true;

        String cap_txt = build_capa_str( TXT_CAP.getText(), CB_CAP_DIM.getSelectedItem().toString());
        if (object.getMaxCapacity().compareTo(cap_txt) != 0)
            return true;

        if (object_get_mode() != ((DS_mode_entry)CB_MODE.getSelectedItem()).flags)
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
        if (CB_STATUS.getSelectedItem() == null)
        {
            UserMain.errm_ok(UserMain.getString("Der_DiskSpace_Status_ist_nicht_okay"));
            return false;
        }
        try
        {
            long l = Long.parseLong(TXT_CAP.getText());
            String d = CB_CAP_DIM.getSelectedItem().toString();
        }
        catch (Exception exc)
        {
            UserMain.errm_ok(UserMain.getString("Die_Kapazität_ist_nicht_okay"));
            return false;
        }
                
        return true;
    }


    @Override
    protected void set_object_props()
    {
        String path = TXT_PATH.getText();
        boolean de = BT_DISABLED.isSelected();
        DiskSpaceOverview.DiskSpaceTypeEntry dsty = (DiskSpaceOverview.DiskSpaceTypeEntry)CB_STATUS.getSelectedItem();

        object.setPath(path);
        object.setStatus(dsty.type);
        object.setMaxCapacity( build_capa_str( TXT_CAP.getText(), CB_CAP_DIM.getSelectedItem().toString()) );
        int mode_flags = ((DS_mode_entry)CB_MODE.getSelectedItem()).flags;
        if (BT_DISABLED.isSelected())
            mode_flags |= CS_Constants.DS_DISABLED;

        set_object_flags(mode_flags);
        set_object_disabled( de );

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
