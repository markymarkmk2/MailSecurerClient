/*
 * PanelTasks.java
 *
 * Created on 13. M�rz 2008, 09:39
 */
package dimm.home;

import dimm.home.Panels.HotfolderOverview;
import dimm.home.Panels.MilterOverview;
import dimm.home.Rendering.BackgroundTitle;
import dimm.home.Rendering.GhostButton;
import dimm.home.Rendering.SwitchSpringPanel;
import dimm.home.Rendering.TimingTargetAdapter;
import java.awt.Dimension;
import java.awt.Point;
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
        BT_FILETRANSFER = new GhostButton();
        BT_AUDIOPARAM = new GhostButton();
        BT_LOWLEVELPARAMS = new GhostButton();
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

        BT_FILETRANSFER.setFont(new java.awt.Font("Arial", 0, 14));
        BT_FILETRANSFER.setForeground(new java.awt.Color(201, 201, 201));
        BT_FILETRANSFER.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/turntable_33x48.png"))); // NOI18N
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
        PN_BUTTONS.add(BT_FILETRANSFER, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 350, -1, 50));

        BT_AUDIOPARAM.setFont(new java.awt.Font("Arial", 0, 14));
        BT_AUDIOPARAM.setForeground(new java.awt.Color(201, 201, 201));
        BT_AUDIOPARAM.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/tr_schedule.png"))); // NOI18N
        BT_AUDIOPARAM.setText(UserMain.Txt("IMAP")); // NOI18N
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

        BT_LOWLEVELPARAMS.setFont(new java.awt.Font("Arial", 0, 14));
        BT_LOWLEVELPARAMS.setForeground(new java.awt.Color(201, 201, 201));
        BT_LOWLEVELPARAMS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/disk-jockey-32x32.png"))); // NOI18N
        BT_LOWLEVELPARAMS.setText(UserMain.Txt("Preferences")); // NOI18N
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
            TimingTargetAdapter tt = make_spring_button_dlg( new HotfolderOverview(main, true),  get_dlg_pos(),  UserMain.getString("Hotfolder") );
            spring_button_action(evt.getSource(), tt);
        }        
}//GEN-LAST:event_BT_NETWORKActionPerformed

    
    private void BT_FILETRANSFERActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_FILETRANSFERActionPerformed
    {//GEN-HEADEREND:event_BT_FILETRANSFERActionPerformed
        // TODO add your handling code here:
        if (check_selected())
        {
            TimingTargetAdapter tt = make_spring_button_dlg( new MilterOverview(main, true),  get_dlg_pos(),  UserMain.getString("Milter") );
            spring_button_action(evt.getSource(), tt);
            /*
            UserMain.self.show_busy("Kuckst Du hier, machst Du stop wenn Du willst", true);

            SwingWorker sw = new SwingWorker()
            {

                @Override
                public Object construct()
                {
                    boolean aborted = false;
                   try
                    {
                       int i = 50;
                       while (i-- > 0)
                       {
                            Thread.sleep(100);
                            if (UserMain.self.is_busy_aborted())
                            {
                                aborted = true;
                                UserMain.self.show_busy("Wiedergesehen...");
                                break;
                            }
                       }
                    }
                    catch (InterruptedException interruptedException)
                    {
                    }
                   if (aborted)
                   {
                       try
                       {
                           Thread.sleep(1000);
                       }
                       catch (InterruptedException interruptedException)
                       {
                       }
                   }


                    UserMain.self.hide_busy();
                    return null;

                }
            };

            sw.start();
         }
             * */
        }

}//GEN-LAST:event_BT_FILETRANSFERActionPerformed

    private void BT_AUDIOPARAMActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_AUDIOPARAMActionPerformed
    {//GEN-HEADEREND:event_BT_AUDIOPARAMActionPerformed
        // TODO add your handling code here:
        if (check_selected())
        {
            UserMain.info_ok("Juchhuuuu!!!");
        }        
}//GEN-LAST:event_BT_AUDIOPARAMActionPerformed

    private void BT_LOWLEVELPARAMSActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_LOWLEVELPARAMSActionPerformed
    {//GEN-HEADEREND:event_BT_LOWLEVELPARAMSActionPerformed
        // TODO add your handling code here:
        if (check_selected())
        {
            UserMain.errm_ok_cancel("Scheissseeee!!!");
        }
        
}//GEN-LAST:event_BT_LOWLEVELPARAMSActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_AUDIOPARAM;
    private javax.swing.JButton BT_FILETRANSFER;
    private javax.swing.JButton BT_LOWLEVELPARAMS;
    private javax.swing.JButton BT_NETWORK;
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

    /*
     * 
     Copying 175 files to J:\Develop\Java\JMailArchiv\Client\build\classes
Copied 32 empty directories to 14 empty directories under J:\Develop\Java\JMailArchiv\Client\build\classes
compile:
Created dir: J:\Develop\Java\JMailArchiv\Client\dist
Building jar: J:\Develop\Java\JMailArchiv\Client\dist\JMailClient.jar
jnlp:
jar:
BUILD SUCCESSFUL (total time: 2 seconds)
*
     * */
}
