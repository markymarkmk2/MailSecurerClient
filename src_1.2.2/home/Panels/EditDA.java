/*
 * EditChannelPanel.java
 *
 * Created on 25. M�rz 2008, 20:06
 */

package dimm.home.Panels;

import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossButton;
import dimm.home.UserMain;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import home.shared.hibernate.DiskArchive;
import home.shared.Utilities.Validator;



class DATypeEntry
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
/**
 
 @author  Administrator
 */
public class EditDA extends GenericEditPanel
{
    DAOverview object_overview;
    DATableModel model;
    DiskArchive object;
    DiskArchive save_object;
    String object_name;
    
    
    /** Creates new form EditChannelPanel */
    public EditDA(int _row, DAOverview _overview)
    {
        initComponents();     

        if (!UserMain.self.is_admin())
             BT_REINDEX.setVisible(false);


        object_overview = _overview;
        model = object_overview.get_object_model();
        
        CB_TYPE.removeAllItems();

        // FILL COMBOBOX TYPE
        for (int i = 0; i < object_overview.get_da_entry_list().length; i++)
        {
            DAOverview.DATypeEntry mte = object_overview.get_da_entry_list()[i];
            CB_TYPE.addItem( mte );
        }
                               
        row = _row;
        
        if (!model.is_new(row))
        {
            object = model.get_object(row);
            save_object = new DiskArchive( object );

            TXT_NAME.setText(object.getName());

//            String type = object.getType();
            // HACK, WE ONLY HAVE ON TYPE
            String type = object_overview.get_da_entry_list()[0].type;
            for (int i = 0; i < object_overview.get_da_entry_list().length; i++)
            {
                DAOverview.DATypeEntry mte = object_overview.get_da_entry_list()[i];
                if (mte.type.compareTo(type)== 0)
                {
                    CB_TYPE.setSelectedIndex(i);
                    break;
                }
            }

            BT_DISABLED.setSelected(  object_is_disabled() );
        }
        else
        {
            object = new DiskArchive();
            CB_TYPE.setSelectedIndex(0);
            object.setMandant(UserMain.sqc().get_act_mandant());
            TXT_NAME.setText( UserMain.Txt("New_Diskarchive"));
        }
        
        object_name = object.getClass().getSimpleName();

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
        CB_TYPE = new javax.swing.JComboBox();
        BT_DISKSPACES = new GlossButton();
        PN_BUTTONS = new javax.swing.JPanel();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();
        BT_REINDEX = new GlossButton();

        setDoubleBuffered(false);
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        PN_ACTION.setDoubleBuffered(false);
        PN_ACTION.setOpaque(false);

        jLabel1.setText(UserMain.getString("Type")); // NOI18N

        TXT_NAME.setText(UserMain.getString("Neuer_Name")); // NOI18N
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

        jLabel2.setText(UserMain.getString("Name")); // NOI18N

        BT_DISABLED.setText(UserMain.getString("Gesperrt")); // NOI18N
        BT_DISABLED.setOpaque(false);

        CB_TYPE.setEditable(true);
        CB_TYPE.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        BT_DISKSPACES.setText(UserMain.Txt("DiskSpaces")); // NOI18N
        BT_DISKSPACES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_DISKSPACESActionPerformed(evt);
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
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addGap(22, 22, 22)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TXT_NAME, javax.swing.GroupLayout.DEFAULT_SIZE, 339, Short.MAX_VALUE)
                            .addComponent(CB_TYPE, 0, 339, Short.MAX_VALUE))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_ACTIONLayout.createSequentialGroup()
                        .addComponent(BT_DISABLED)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 244, Short.MAX_VALUE)
                        .addComponent(BT_DISKSPACES)
                        .addContainerGap())))
        );
        PN_ACTIONLayout.setVerticalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_NAME, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(CB_TYPE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_DISKSPACES)
                    .addComponent(BT_DISABLED))
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 143, Short.MAX_VALUE)
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

    private void BT_DISKSPACESActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_DISKSPACESActionPerformed
    {//GEN-HEADEREND:event_BT_DISKSPACESActionPerformed
        // TODO add your handling code here:
        boolean _is_new = is_new();
        
        // IF WE WANT TO INSERT DISKSPACE IN A NEW OBJECT , WE HAVE TO SAVE FIRST
        if (_is_new)
        {
            // IN CASE OF ERROR -> LEAVE, MESSAGE WAS ALREADY SHOWN
            if (!save_action(object, save_object) || model.getRowCount() <= 0)
                return;

            // NOW THE LAST OBJECT IN OVERVIEWLIST IS OUR NEW OBJECT, OBJECTS ARE ORDERED BY ID
            int size = model.getRowCount();
            DiskArchive new_object = model.get_object(size - 1);
            if (new_object.getName().compareTo(object.getName()) != 0)
            {
                return;
            }

            // SET INTERNAL VARS
            object = new_object;
            row = size - 1;            
        }

        DiskSpaceOverview dlg = new DiskSpaceOverview(UserMain.self, object, true);
        dlg.pack();

        dlg.setLocation(this.getLocationOnScreen().x + 30, this.getLocationOnScreen().y + 30);
        dlg.setTitle(UserMain.Txt("DiskSpaces"));
        dlg.setVisible(true);
    }//GEN-LAST:event_BT_DISKSPACESActionPerformed

    private void BT_REINDEXActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_REINDEXActionPerformed
    {//GEN-HEADEREND:event_BT_REINDEXActionPerformed
        // TODO add your handling code here:
        if (BT_DISABLED.isSelected())
            return;


        ReIndexPanel pnl = new ReIndexPanel(object.getId());
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
        dlg.set_next_location(my_dlg);
        dlg.setVisible(true);

    }//GEN-LAST:event_BT_REINDEXActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JCheckBox BT_DISABLED;
    private javax.swing.JButton BT_DISKSPACES;
    private javax.swing.JButton BT_OK;
    private javax.swing.JButton BT_REINDEX;
    private javax.swing.JComboBox CB_TYPE;
    private javax.swing.JPanel PN_ACTION;
    private javax.swing.JPanel PN_BUTTONS;
    private javax.swing.JTextField TXT_NAME;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
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

        return ((flags & DAOverview.DISABLED) == DAOverview.DISABLED);
    }
    void set_object_disabled( boolean f)
    {
        int flags = get_object_flags();

        if (f)
            set_object_flag( DAOverview.DISABLED );
        else
            clr_object_flag( DAOverview.DISABLED );
    }

    
    @Override
    protected boolean check_changed()
    {        
        if (model.is_new(row))
            return true;

        String name = object.getName();
        if (name != null && TXT_NAME.getText().compareTo(name ) != 0)
            return true;


        if (BT_DISABLED.isSelected() != object_is_disabled())
            return true;

        
        return false;
    }
                        
    @Override
    protected boolean is_plausible()
    {
        if (!Validator.is_valid_name( TXT_NAME.getText(), 255))
        {
            UserMain.errm_ok(UserMain.getString("Der_Pfad_ist_nicht_okay"));
            return false;
        }
                
        return true;
    }


    @Override
    protected void set_object_props()
    {
        String name = TXT_NAME.getText();
        boolean de = BT_DISABLED.isSelected();

        object.setName(name);
        set_object_disabled( de );
    }

    @Override
    protected boolean save_action( Object o, Object so )
    {
        boolean ok = super.save_action(o, so);

        // IF SOMETHING HAS BEEN SAVED, WE REBUILD OUR GLOBAL DA-LIST
        if (ok)
        {
            UserMain.sqc().rebuild_da_array();
        }
        return ok;
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
