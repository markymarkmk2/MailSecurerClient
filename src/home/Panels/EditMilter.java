/*
 * EditChannelPanel.java
 *
 * Created on 25. M�rz 2008, 20:06
 */

package dimm.home.Panels;

import dimm.home.Rendering.GlossButton;
import dimm.home.UserMain;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import dimm.general.SQL.*;
import dimm.general.hibernate.DiskArchive;
import dimm.general.hibernate.Milter;
import dimm.home.Models.DiskArchiveComboModel;



class MilterTypeEntry
{
    String type;
    String name;

    MilterTypeEntry( String t, String n)
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
public class EditMilter extends GenericEditPanel
{
    MilterOverview object_overview;
    MilterTableModel model;
    Milter object;
    DiskArchiveComboModel dacm;
    
    
    /** Creates new form EditChannelPanel */
    public EditMilter(int _row, MilterOverview _overview)
    {
        initComponents();     
        
        object_overview = _overview;
        model = object_overview.get_object_model();
        CB_VAULT.removeAllItems();
        CB_TYPE.removeAllItems();

        // FILL COMBOBOX TYPE
        for (int i = 0; i < object_overview.get_mt_entry_list().length; i++)
        {
            MilterOverview.MilterTypeEntry mte = object_overview.get_mt_entry_list()[i];
            CB_TYPE.addItem( mte );
        }


        SQLResult<DiskArchive> da_res = UserMain.sqc().get_da_result();

        // COMBO-MODEL DISK ARCHIVE
        dacm = new DiskArchiveComboModel(da_res );
                                
        row = _row;
        
        if (!model.is_new(row))
        {
            object = model.get_object(row);

            String type = object.getType();
            for (int i = 0; i < object_overview.get_mt_entry_list().length; i++)
            {
                MilterOverview.MilterTypeEntry mte = object_overview.get_mt_entry_list()[i];
                if (mte.type.compareTo(type)== 0)
                {
                    CB_TYPE.setSelectedIndex(i);
                    break;
                }
            }


            int da_id = model.getSqlResult().getInt( row, "da_id");
            dacm.set_act_id(da_id);
            
        }
        else
        {
            object = new Milter();
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
        java.awt.GridBagConstraints gridBagConstraints;

        PN_ACTION = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        TXT_SERVER1 = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        BT_DISABLED = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        CB_VAULT = new javax.swing.JComboBox();
        CB_TYPE = new javax.swing.JComboBox();
        TXT_PORT1 = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        TXT_SERVER2 = new javax.swing.JTextField();
        TXT_PORT2 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        PN_BUTTONS = new javax.swing.JPanel();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();

        setDoubleBuffered(false);
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        PN_ACTION.setDoubleBuffered(false);
        PN_ACTION.setOpaque(false);

        jLabel1.setText(UserMain.Txt("Type")); // NOI18N

        TXT_SERVER1.setText(UserMain.Txt("Neuer_Pfad")); // NOI18N
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

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dimm/home/SR_Properties"); // NOI18N
        jLabel2.setText(bundle.getString("Server")); // NOI18N

        BT_DISABLED.setText(bundle.getString("Gesperrt")); // NOI18N
        BT_DISABLED.setOpaque(false);

        jLabel3.setText(bundle.getString("Speicherziel")); // NOI18N

        CB_VAULT.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        CB_TYPE.setEditable(true);
        CB_TYPE.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        TXT_PORT1.setText(UserMain.Txt("Neuer_Pfad")); // NOI18N
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

        jLabel4.setText(bundle.getString("Server")); // NOI18N

        jLabel5.setText(bundle.getString("Server")); // NOI18N

        TXT_SERVER2.setText(UserMain.Txt("Neuer_Pfad")); // NOI18N
        TXT_SERVER2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_SERVER2MouseClicked(evt);
            }
        });
        TXT_SERVER2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TXT_SERVER2ActionPerformed(evt);
            }
        });

        TXT_PORT2.setText(UserMain.Txt("Neuer_Pfad")); // NOI18N
        TXT_PORT2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_PORT2MouseClicked(evt);
            }
        });
        TXT_PORT2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TXT_PORT2ActionPerformed(evt);
            }
        });

        jLabel6.setText(bundle.getString("Server")); // NOI18N

        javax.swing.GroupLayout PN_ACTIONLayout = new javax.swing.GroupLayout(PN_ACTION);
        PN_ACTION.setLayout(PN_ACTIONLayout);
        PN_ACTIONLayout.setHorizontalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BT_DISABLED, javax.swing.GroupLayout.DEFAULT_SIZE, 398, Short.MAX_VALUE)
                            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel2))
                                .addGap(69, 69, 69)
                                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(CB_VAULT, javax.swing.GroupLayout.Alignment.LEADING, 0, 291, Short.MAX_VALUE)
                                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(CB_TYPE, 0, 283, Short.MAX_VALUE)
                                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PN_ACTIONLayout.createSequentialGroup()
                                                .addComponent(TXT_SERVER1, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                                                .addGap(56, 56, 56)
                                                .addComponent(jLabel4)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                                .addComponent(TXT_PORT1, javax.swing.GroupLayout.DEFAULT_SIZE, 55, Short.MAX_VALUE)))
                                        .addGap(8, 8, 8)))))
                        .addContainerGap())
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(75, 75, 75)
                        .addComponent(TXT_SERVER2, javax.swing.GroupLayout.DEFAULT_SIZE, 142, Short.MAX_VALUE)
                        .addGap(56, 56, 56)
                        .addComponent(jLabel6)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(TXT_PORT2, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18))))
        );

        PN_ACTIONLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {TXT_PORT1, TXT_PORT2});

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
                    .addComponent(TXT_SERVER2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(TXT_PORT2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addGap(13, 13, 13)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(CB_VAULT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 37, Short.MAX_VALUE)
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
                .addContainerGap(230, Short.MAX_VALUE)
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

    private void TXT_SERVER2MouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXT_SERVER2MouseClicked
    {//GEN-HEADEREND:event_TXT_SERVER2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_TXT_SERVER2MouseClicked

    private void TXT_SERVER2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TXT_SERVER2ActionPerformed
    {//GEN-HEADEREND:event_TXT_SERVER2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TXT_SERVER2ActionPerformed

    private void TXT_PORT2MouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXT_PORT2MouseClicked
    {//GEN-HEADEREND:event_TXT_PORT2MouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_TXT_PORT2MouseClicked

    private void TXT_PORT2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TXT_PORT2ActionPerformed
    {//GEN-HEADEREND:event_TXT_PORT2ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TXT_PORT2ActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JCheckBox BT_DISABLED;
    private javax.swing.JButton BT_OK;
    private javax.swing.JComboBox CB_TYPE;
    private javax.swing.JComboBox CB_VAULT;
    private javax.swing.JPanel PN_ACTION;
    private javax.swing.JPanel PN_BUTTONS;
    private javax.swing.JTextField TXT_PORT1;
    private javax.swing.JTextField TXT_PORT2;
    private javax.swing.JTextField TXT_SERVER1;
    private javax.swing.JTextField TXT_SERVER2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
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

        return ((flags & MilterOverview.DISABLED) == MilterOverview.DISABLED);
    }
    void set_object_disabled( boolean f)
    {
        int flags = get_object_flags();

        if (f)
            set_object_flag( MilterOverview.DISABLED );
        else
            clr_object_flag( MilterOverview.DISABLED );
    }

    
    protected boolean check_changed()
    {        
        if (model.is_new(row))
            return true;

        String server = object.getInServer();
        if (server != null && TXT_SERVER1.getText().compareTo(server ) != 0)
            return true;

        int port = object.getInPort();
        if (Integer.parseInt( TXT_PORT1.getText() ) != port)
            return true;

        if (TXT_SERVER2.isVisible())
        {
            server = object.getInServer();
            if (server != null && TXT_SERVER2.getText().compareTo(server ) != 0)
                return true;

            port = object.getInPort();
            if (Integer.parseInt( TXT_PORT2.getText() ) != port)
                return true;
        }

        if (BT_DISABLED.isSelected() != object_is_disabled())
            return true;

        long da_id = model.getSqlResult().getLong( row, "da_id");

        if ( CB_VAULT.getSelectedItem() != null)
        {
            if (dacm.get_act_id() != da_id)
                return true;
        }
        
        return false;
    }
                        
    protected boolean is_plausible()
    {
        if (TXT_SERVER1.getText().length() == 0 || TXT_SERVER1.getText().length() > 256)
        {
            UserMain.errm_ok(UserMain.getString("Der_Pfad_ist_nicht_okay"));
            return false;
        }
        if (TXT_SERVER2.isVisible())
        {
            if (TXT_SERVER2.getText().length() == 0 || TXT_SERVER2.getText().length() > 256)
            {
                UserMain.errm_ok(UserMain.getString("Der_Pfad_ist_nicht_okay"));
                return false;
            }
        }
        try
        {
            int port = Integer.parseInt( TXT_PORT1.getText() );
            if (TXT_SERVER2.isVisible())
            {
                port = Integer.parseInt( TXT_PORT2.getText() );
            }
        }
        catch (Exception exc)
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


    protected void set_object_props()
    {
        String server1 = TXT_SERVER1.getText();
        int port1 = Integer.parseInt( TXT_PORT1.getText() );
        String server2 = object.getOutServer();
        int port2 = object.getOutPort();
        if (TXT_SERVER2.isVisible())
        {
            server2 = TXT_SERVER2.getText();
            port2 = Integer.parseInt( TXT_PORT2.getText() );
        }
        boolean de = BT_DISABLED.isSelected();

        object.setInServer(server1);
        object.setOutServer(server2);
        object.setInPort(port1);
        object.setOutPort(port2);
        set_object_disabled( de );
        object.setDiskArchive( dacm.get_selected_da());
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
