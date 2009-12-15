/*
 * PanelTasks.java
 *
 * Created on 13. M�rz 2008, 09:39
 */
package dimm.home.SwitchPanels;

import dimm.home.*;
import dimm.home.Panels.MandantOverview;
import dimm.home.Rendering.BackgroundTitle;
import dimm.home.Rendering.GhostButton;
import dimm.home.Rendering.SwitchSpringPanel;
import dimm.home.Rendering.TimingTargetAdapter;
import dimm.home.Utilities.SwingWorker;
import java.awt.Dimension;
import java.awt.Point;
import org.jdesktop.fuse.ResourceInjector;



/**
 *
 * @author  Administrator
 */
public class PanelSystem extends SwitchSpringPanel
{

    /** Creates new form PanelTasks */
    public PanelSystem( UserMain m )
    {
        super(m, UserMain.PBC_ADMIN);
        ResourceInjector.get().inject(this);

        initComponents();


        PN_BUTTONS.add(new BackgroundTitle( UserMain.getString("Mandanten" ) ), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 65, getWidth(), 60));
        PN_BUTTONS.add(new BackgroundTitle( UserMain.getString("Werkzeuge" ) ), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 285, getWidth(), 60));
        
    }

    @Override
    public void setSize( Dimension d )
    {
        this.removeAll();
        
        super.setSize(d);
        

        initComponents();


        PN_BUTTONS.add(new BackgroundTitle( UserMain.getString("Mandanten" ) ), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 65, getWidth(), 60));
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
        BT_FREE1 = new GhostButton();
        BT_FREE2 = new GhostButton();
        BT_FREE3 = new GhostButton();
        BT_MANDANTEN = new GhostButton();
        PN_HEADER = new javax.swing.JPanel();

        setOpaque(false);

        PN_BUTTONS.setOpaque(false);
        PN_BUTTONS.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BT_FREE1.setFont(new java.awt.Font("Arial", 0, 14));
        BT_FREE1.setForeground(new java.awt.Color(201, 201, 201));
        BT_FREE1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/turntable_33x48.png"))); // NOI18N
        BT_FREE1.setText(UserMain.Txt("???")); // NOI18N
        BT_FREE1.setToolTipText("");
        BT_FREE1.setBorderPainted(false);
        BT_FREE1.setContentAreaFilled(false);
        BT_FREE1.setFocusPainted(false);
        BT_FREE1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_FREE1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_FREE1ActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_FREE1, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 350, 190, 50));

        BT_FREE2.setFont(new java.awt.Font("Arial", 0, 14));
        BT_FREE2.setForeground(new java.awt.Color(201, 201, 201));
        BT_FREE2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/tr_schedule.png"))); // NOI18N
        BT_FREE2.setText(UserMain.Txt("???")); // NOI18N
        BT_FREE2.setToolTipText("");
        BT_FREE2.setBorderPainted(false);
        BT_FREE2.setContentAreaFilled(false);
        BT_FREE2.setFocusPainted(false);
        BT_FREE2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_FREE2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_FREE2ActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_FREE2, new org.netbeans.lib.awtextra.AbsoluteConstraints(260, 350, 170, 50));

        BT_FREE3.setFont(new java.awt.Font("Arial", 0, 14));
        BT_FREE3.setForeground(new java.awt.Color(201, 201, 201));
        BT_FREE3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/disk-jockey-32x32.png"))); // NOI18N
        BT_FREE3.setText(UserMain.Txt("???")); // NOI18N
        BT_FREE3.setToolTipText("");
        BT_FREE3.setBorderPainted(false);
        BT_FREE3.setContentAreaFilled(false);
        BT_FREE3.setFocusPainted(false);
        BT_FREE3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_FREE3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_FREE3ActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_FREE3, new org.netbeans.lib.awtextra.AbsoluteConstraints(440, 350, 190, 50));

        BT_MANDANTEN.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BT_MANDANTEN.setForeground(new java.awt.Color(201, 201, 201));
        BT_MANDANTEN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/tr_einstellungen.png"))); // NOI18N
        BT_MANDANTEN.setText(UserMain.Txt("Mandanten")); // NOI18N
        BT_MANDANTEN.setToolTipText(UserMain.Txt("Long_Companies")); // NOI18N
        BT_MANDANTEN.setBorderPainted(false);
        BT_MANDANTEN.setContentAreaFilled(false);
        BT_MANDANTEN.setFocusPainted(false);
        BT_MANDANTEN.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_MANDANTEN.setMaximumSize(new java.awt.Dimension(101, 26));
        BT_MANDANTEN.setMinimumSize(new java.awt.Dimension(101, 26));
        BT_MANDANTEN.setPreferredSize(new java.awt.Dimension(101, 26));
        BT_MANDANTEN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_MANDANTENActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_MANDANTEN, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 130, 160, 50));

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
   

    
    private void BT_FREE1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_FREE1ActionPerformed
    {//GEN-HEADEREND:event_BT_FREE1ActionPerformed
        // TODO add your handling code here:
        if (check_selected())
        {
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
        

}//GEN-LAST:event_BT_FREE1ActionPerformed

    private void BT_FREE2ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_FREE2ActionPerformed
    {//GEN-HEADEREND:event_BT_FREE2ActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_BT_FREE2ActionPerformed

    private void BT_FREE3ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_FREE3ActionPerformed
    {//GEN-HEADEREND:event_BT_FREE3ActionPerformed
        // TODO add your handling code here:
        
}//GEN-LAST:event_BT_FREE3ActionPerformed

    private void BT_MANDANTENActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_MANDANTENActionPerformed
    {//GEN-HEADEREND:event_BT_MANDANTENActionPerformed
        // TODO add your handling code here:
       if (check_selected())
        {
            TimingTargetAdapter tt = make_spring_button_dlg( new MandantOverview(main, true),  get_dlg_pos(),  UserMain.getString("Mandanten") );
            spring_button_action(evt.getSource(), tt);
        }

}//GEN-LAST:event_BT_MANDANTENActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_FREE1;
    private javax.swing.JButton BT_FREE2;
    private javax.swing.JButton BT_FREE3;
    private javax.swing.JButton BT_MANDANTEN;
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
