/*
 * PanelTasks.java
 *
 * Created on 13. M�rz 2008, 09:39
 */
package dimm.home;

import dimm.home.Rendering.BackgroundTitle;
import dimm.home.Rendering.GhostButton;
import dimm.home.Rendering.SwitchSpringPanel;
import dimm.home.Rendering.TimingTargetAdapter;
import dimm.home.Utilities.CmdExecutor;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;



/**
 *
 * @author  Administrator
 */
public class PanelVerwaltung extends SwitchSpringPanel
{

    /** Creates new form PanelTasks */
    public PanelVerwaltung( UserMain m )
    {
        super(m, UserMain.PBC_ADMIN);
        ResourceInjector.get().inject(this);

        initComponents();


        PN_BUTTONS.add(new BackgroundTitle( UserMain.getString("Einstellungen" ) ), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 65, getWidth(), 60));
        PN_BUTTONS.add(new BackgroundTitle( UserMain.getString("Werkzeuge" ) ), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 285, getWidth(), 60));
        
    }

    @Override
    public void setSize( Dimension d )
    {
        this.removeAll();
        
        super.setSize(d);
        

        initComponents();


        PN_BUTTONS.add(new BackgroundTitle( UserMain.getString("Einstellungen" ) ), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 65, getWidth(), 60));
        PN_BUTTONS.add(new BackgroundTitle( UserMain.getString("Werkzeuge" ) ), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 285, getWidth(), 60));
    }


 
    boolean check_selected()
    {
        return true;
    }

    
    public void set_selected_station( int i, int _channels)
    {
         
    }


    
            
    Point get_dlg_pos()
    {
        if (UserMain.self.is_touchscreen())
            return new Point( this.getLocationOnScreen().x + 10, this.getLocationOnScreen().y + 10 );
        else
            return new Point( this.getLocationOnScreen().x + 20, this.getLocationOnScreen().y + 20 );
    }
    
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PN_BUTTONS = new javax.swing.JPanel();
        BT_NETWORK = new GhostButton();
        BT_LOG = new GhostButton();
        BT_FILETRANSFER = new GhostButton();
        BT_UPDATE = new GhostButton();
        BT_AUDIOPARAM = new GhostButton();
        BT_REBOOT = new GhostButton();
        BT_TERMINAL = new GhostButton();
        BT_LOWLEVELPARAMS = new GhostButton();
        BT_STATIONID = new GhostButton();
        BT_CALTS = new GhostButton();
        BT_SETTINGS = new GhostButton();
        PN_HEADER = new javax.swing.JPanel();

        setOpaque(false);

        PN_BUTTONS.setOpaque(false);
        PN_BUTTONS.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BT_NETWORK.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BT_NETWORK.setForeground(new java.awt.Color(201, 201, 201));
        BT_NETWORK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/tr_einstellungen.png"))); // NOI18N
        BT_NETWORK.setText(UserMain.Txt("Netzwerk")); // NOI18N
        BT_NETWORK.setBorderPainted(false);
        BT_NETWORK.setContentAreaFilled(false);
        BT_NETWORK.setFocusPainted(false);
        BT_NETWORK.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_NETWORK.setMaximumSize(new java.awt.Dimension(101, 26));
        BT_NETWORK.setMinimumSize(new java.awt.Dimension(101, 26));
        BT_NETWORK.setPreferredSize(new java.awt.Dimension(101, 26));
        BT_NETWORK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_NETWORKActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_NETWORK, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 130, -1, 50));

        BT_LOG.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BT_LOG.setForeground(new java.awt.Color(201, 201, 201));
        BT_LOG.setText("null");
        BT_LOG.setBorderPainted(false);
        BT_LOG.setContentAreaFilled(false);
        BT_LOG.setFocusPainted(false);
        BT_LOG.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_LOG.setMaximumSize(new java.awt.Dimension(133, 35));
        BT_LOG.setMinimumSize(new java.awt.Dimension(133, 35));
        BT_LOG.setPreferredSize(new java.awt.Dimension(133, 35));
        BT_LOG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_LOGActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_LOG, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 355, -1, 50));

        BT_FILETRANSFER.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BT_FILETRANSFER.setForeground(new java.awt.Color(201, 201, 201));
        BT_FILETRANSFER.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/disk-jockey-32x32.png"))); // NOI18N
        BT_FILETRANSFER.setText(UserMain.Txt("Dateitransfer")); // NOI18N
        BT_FILETRANSFER.setBorderPainted(false);
        BT_FILETRANSFER.setContentAreaFilled(false);
        BT_FILETRANSFER.setFocusPainted(false);
        BT_FILETRANSFER.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_FILETRANSFER.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_FILETRANSFERActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_FILETRANSFER, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 355, -1, 50));

        BT_UPDATE.setFont(new java.awt.Font("Arial", 0, 14));
        BT_UPDATE.setForeground(new java.awt.Color(201, 201, 201));
        BT_UPDATE.setText("null");
        BT_UPDATE.setBorderPainted(false);
        BT_UPDATE.setContentAreaFilled(false);
        BT_UPDATE.setFocusPainted(false);
        BT_UPDATE.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_UPDATE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_UPDATEActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_UPDATE, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 355, -1, 50));

        BT_AUDIOPARAM.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BT_AUDIOPARAM.setForeground(new java.awt.Color(201, 201, 201));
        BT_AUDIOPARAM.setText("null");
        BT_AUDIOPARAM.setBorderPainted(false);
        BT_AUDIOPARAM.setContentAreaFilled(false);
        BT_AUDIOPARAM.setFocusPainted(false);
        BT_AUDIOPARAM.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_AUDIOPARAM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_AUDIOPARAMActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_AUDIOPARAM, new org.netbeans.lib.awtextra.AbsoluteConstraints(200, 130, -1, 50));

        BT_REBOOT.setFont(new java.awt.Font("Arial", 0, 14));
        BT_REBOOT.setForeground(new java.awt.Color(201, 201, 201));
        BT_REBOOT.setText("null");
        BT_REBOOT.setBorderPainted(false);
        BT_REBOOT.setContentAreaFilled(false);
        BT_REBOOT.setFocusPainted(false);
        BT_REBOOT.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_REBOOT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_REBOOTActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_REBOOT, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 355, 110, 50));

        BT_TERMINAL.setFont(new java.awt.Font("Arial", 0, 14));
        BT_TERMINAL.setForeground(new java.awt.Color(201, 201, 201));
        BT_TERMINAL.setText("null");
        BT_TERMINAL.setBorderPainted(false);
        BT_TERMINAL.setContentAreaFilled(false);
        BT_TERMINAL.setFocusPainted(false);
        BT_TERMINAL.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_TERMINAL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_TERMINALActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_TERMINAL, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 355, -1, 50));

        BT_LOWLEVELPARAMS.setFont(new java.awt.Font("Arial", 0, 14));
        BT_LOWLEVELPARAMS.setForeground(new java.awt.Color(201, 201, 201));
        BT_LOWLEVELPARAMS.setText("null");
        BT_LOWLEVELPARAMS.setBorderPainted(false);
        BT_LOWLEVELPARAMS.setContentAreaFilled(false);
        BT_LOWLEVELPARAMS.setFocusPainted(false);
        BT_LOWLEVELPARAMS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_LOWLEVELPARAMS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_LOWLEVELPARAMSActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_LOWLEVELPARAMS, new org.netbeans.lib.awtextra.AbsoluteConstraints(330, 130, -1, 50));

        BT_STATIONID.setFont(new java.awt.Font("Arial", 0, 14));
        BT_STATIONID.setForeground(new java.awt.Color(201, 201, 201));
        BT_STATIONID.setText("null");
        BT_STATIONID.setBorderPainted(false);
        BT_STATIONID.setContentAreaFilled(false);
        BT_STATIONID.setFocusPainted(false);
        BT_STATIONID.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_STATIONID.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_STATIONIDActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_STATIONID, new org.netbeans.lib.awtextra.AbsoluteConstraints(480, 130, -1, 50));

        BT_CALTS.setFont(new java.awt.Font("Arial", 0, 14));
        BT_CALTS.setForeground(new java.awt.Color(201, 201, 201));
        BT_CALTS.setText("null");
        BT_CALTS.setBorderPainted(false);
        BT_CALTS.setContentAreaFilled(false);
        BT_CALTS.setFocusPainted(false);
        BT_CALTS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_CALTS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_CALTSActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_CALTS, new org.netbeans.lib.awtextra.AbsoluteConstraints(620, 130, -1, 50));

        BT_SETTINGS.setBackground(new java.awt.Color(201, 201, 201));
        BT_SETTINGS.setFont(new java.awt.Font("Arial", 0, 14));
        BT_SETTINGS.setForeground(new java.awt.Color(201, 201, 201));
        BT_SETTINGS.setText("null");
        BT_SETTINGS.setBorderPainted(false);
        BT_SETTINGS.setContentAreaFilled(false);
        BT_SETTINGS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_SETTINGS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_SETTINGSActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_SETTINGS, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 180, 140, 50));

        PN_HEADER.setOpaque(false);
        PN_HEADER.setLayout(new javax.swing.BoxLayout(PN_HEADER, javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(PN_BUTTONS, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                    .addComponent(PN_HEADER, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(PN_BUTTONS, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PN_HEADER, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
   

    private void BT_NETWORKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_NETWORKActionPerformed
    {//GEN-HEADEREND:event_BT_NETWORKActionPerformed
        // TODO add your handling code here:
        if (check_selected())
        {
        }        
}//GEN-LAST:event_BT_NETWORKActionPerformed

    private void BT_LOGActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_LOGActionPerformed
    {//GEN-HEADEREND:event_BT_LOGActionPerformed
        // TODO add your handling code here:
        if (check_selected())
        {
            TimingTargetAdapter tt = make_spring_button_dlg( new LogPanel(main),  get_dlg_pos(), UserMain.getString("Log-Dateien") );

            spring_button_action(evt.getSource(), tt);
        }
}//GEN-LAST:event_BT_LOGActionPerformed

    private void BT_FILETRANSFERActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_FILETRANSFERActionPerformed
    {//GEN-HEADEREND:event_BT_FILETRANSFERActionPerformed
        // TODO add your handling code here:
        if (check_selected())
        {
        }
        

}//GEN-LAST:event_BT_FILETRANSFERActionPerformed

    private void BT_UPDATEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_UPDATEActionPerformed
    {//GEN-HEADEREND:event_BT_UPDATEActionPerformed
        // TODO add your handling code here:
        if (check_selected())
        {
            TimingTargetAdapter tt = make_spring_button_dlg( new UpdatePanel(main),  get_dlg_pos(), UserMain.getString("Software-Update") );

            spring_button_action(evt.getSource(), tt);
        }
        
}//GEN-LAST:event_BT_UPDATEActionPerformed

    private void BT_AUDIOPARAMActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_AUDIOPARAMActionPerformed
    {//GEN-HEADEREND:event_BT_AUDIOPARAMActionPerformed
        // TODO add your handling code here:
        if (check_selected())
        {
        }        
}//GEN-LAST:event_BT_AUDIOPARAMActionPerformed

    private void BT_REBOOTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_REBOOTActionPerformed
    {//GEN-HEADEREND:event_BT_REBOOTActionPerformed
        // TODO add your handling code here:
        if (check_selected())
        {
/*            if (main.info_ok_cancel(UserMain.getString("Wollen_Sie_wirklich_die_Box_neu_booten?") ))
                main.get_comm().send_cmd("REBOOT" );
  */      }
 
        
}//GEN-LAST:event_BT_REBOOTActionPerformed

    private void BT_TERMINALActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_TERMINALActionPerformed
    {//GEN-HEADEREND:event_BT_TERMINALActionPerformed
        // TODO add your handling code here:
        if (check_selected())
        {
        }        
}//GEN-LAST:event_BT_TERMINALActionPerformed

    private void BT_LOWLEVELPARAMSActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_LOWLEVELPARAMSActionPerformed
    {//GEN-HEADEREND:event_BT_LOWLEVELPARAMSActionPerformed
        // TODO add your handling code here:
        if (check_selected())
        {
        }
        
}//GEN-LAST:event_BT_LOWLEVELPARAMSActionPerformed

    private void BT_STATIONIDActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_STATIONIDActionPerformed
    {//GEN-HEADEREND:event_BT_STATIONIDActionPerformed
        // TODO add your handling code here:
        if (check_selected())
        {
        }        
}//GEN-LAST:event_BT_STATIONIDActionPerformed

    private void BT_CALTSActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_CALTSActionPerformed
    {//GEN-HEADEREND:event_BT_CALTSActionPerformed
        // TODO add your handling code here:
        String[] cmd = {"/opt/SonicRemote/eGalax/TouchKit/TKCal/TKCal", "/dev/tkpanel0"};
        
        CmdExecutor exe = new CmdExecutor( cmd );
        exe.exec();
                
        
}//GEN-LAST:event_BT_CALTSActionPerformed

    private void BT_SETTINGSActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_SETTINGSActionPerformed
    {//GEN-HEADEREND:event_BT_SETTINGSActionPerformed
        if (check_selected())
        {
        }
    }//GEN-LAST:event_BT_SETTINGSActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_AUDIOPARAM;
    private javax.swing.JButton BT_CALTS;
    private javax.swing.JButton BT_FILETRANSFER;
    private javax.swing.JButton BT_LOG;
    private javax.swing.JButton BT_LOWLEVELPARAMS;
    private javax.swing.JButton BT_NETWORK;
    private javax.swing.JButton BT_REBOOT;
    private javax.swing.JButton BT_SETTINGS;
    private javax.swing.JButton BT_STATIONID;
    private javax.swing.JButton BT_TERMINAL;
    private javax.swing.JButton BT_UPDATE;
    private javax.swing.JPanel PN_BUTTONS;
    private javax.swing.JPanel PN_HEADER;
    // End of variables declaration//GEN-END:variables

    @Override
    public void activate_panel()
    {
        boolean system_level = true;
    }

    @Override
    public void deactivate_panel()
    {
        
    }
}
