/*
 * PanelTasks.java
 *
 * Created on 13. M�rz 2008, 09:39
 */
package dimm.home;

import dimm.home.Panels.DAOverview;
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
        BT_DISKARCHIVES = new GhostButton();
        BT_HOTFOLDER = new GhostButton();
        BT_MILTER = new GhostButton();
        BT_PROXY = new GhostButton();
        PN_HEADER = new javax.swing.JPanel();

        setOpaque(false);

        PN_BUTTONS.setOpaque(false);
        PN_BUTTONS.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BT_DISKARCHIVES.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BT_DISKARCHIVES.setForeground(new java.awt.Color(201, 201, 201));
        BT_DISKARCHIVES.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/tr_einstellungen.png"))); // NOI18N
        BT_DISKARCHIVES.setText(UserMain.Txt("Archive")); // NOI18N
        BT_DISKARCHIVES.setBorderPainted(false);
        BT_DISKARCHIVES.setContentAreaFilled(false);
        BT_DISKARCHIVES.setFocusPainted(false);
        BT_DISKARCHIVES.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_DISKARCHIVES.setMaximumSize(new java.awt.Dimension(101, 26));
        BT_DISKARCHIVES.setMinimumSize(new java.awt.Dimension(101, 26));
        BT_DISKARCHIVES.setPreferredSize(new java.awt.Dimension(101, 26));
        BT_DISKARCHIVES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_DISKARCHIVESActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_DISKARCHIVES, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 130, -1, 50));

        BT_HOTFOLDER.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BT_HOTFOLDER.setForeground(new java.awt.Color(201, 201, 201));
        BT_HOTFOLDER.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/turntable_33x48.png"))); // NOI18N
        BT_HOTFOLDER.setText(UserMain.Txt("Hotfolder")); // NOI18N
        BT_HOTFOLDER.setBorderPainted(false);
        BT_HOTFOLDER.setContentAreaFilled(false);
        BT_HOTFOLDER.setFocusPainted(false);
        BT_HOTFOLDER.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_HOTFOLDER.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_HOTFOLDERActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_HOTFOLDER, new org.netbeans.lib.awtextra.AbsoluteConstraints(220, 130, -1, 50));

        BT_MILTER.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BT_MILTER.setForeground(new java.awt.Color(201, 201, 201));
        BT_MILTER.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/tr_schedule.png"))); // NOI18N
        BT_MILTER.setText(UserMain.Txt("Milter")); // NOI18N
        BT_MILTER.setBorderPainted(false);
        BT_MILTER.setContentAreaFilled(false);
        BT_MILTER.setFocusPainted(false);
        BT_MILTER.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_MILTER.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_MILTERActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_MILTER, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 130, -1, 50));

        BT_PROXY.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BT_PROXY.setForeground(new java.awt.Color(201, 201, 201));
        BT_PROXY.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/disk-jockey-32x32.png"))); // NOI18N
        BT_PROXY.setText(UserMain.Txt("Mail_Proxy")); // NOI18N
        BT_PROXY.setBorderPainted(false);
        BT_PROXY.setContentAreaFilled(false);
        BT_PROXY.setFocusPainted(false);
        BT_PROXY.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_PROXY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_PROXYActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_PROXY, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 130, -1, 50));

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
   

    private void BT_DISKARCHIVESActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_DISKARCHIVESActionPerformed
    {//GEN-HEADEREND:event_BT_DISKARCHIVESActionPerformed
        // TODO add your handling code here:
        if (check_selected())
        {
            TimingTargetAdapter tt = make_spring_button_dlg( new DAOverview(main, true),  get_dlg_pos(),  UserMain.getString("Archive") );
            spring_button_action(evt.getSource(), tt);
        }        
}//GEN-LAST:event_BT_DISKARCHIVESActionPerformed

    
    private void BT_HOTFOLDERActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_HOTFOLDERActionPerformed
    {//GEN-HEADEREND:event_BT_HOTFOLDERActionPerformed
        // TODO add your handling code here:
        if (check_selected())
        {
            TimingTargetAdapter tt = make_spring_button_dlg( new HotfolderOverview(main, true),  get_dlg_pos(),  UserMain.getString("Hotfolder") );
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

}//GEN-LAST:event_BT_HOTFOLDERActionPerformed

    private void BT_MILTERActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_MILTERActionPerformed
    {//GEN-HEADEREND:event_BT_MILTERActionPerformed
        // TODO add your handling code here:
        if (check_selected())
        {
            TimingTargetAdapter tt = make_spring_button_dlg( new MilterOverview(main, true),  get_dlg_pos(),  UserMain.getString("SMTP-Connect") );
            spring_button_action(evt.getSource(), tt);
        }
}//GEN-LAST:event_BT_MILTERActionPerformed

    private void BT_PROXYActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_PROXYActionPerformed
    {//GEN-HEADEREND:event_BT_PROXYActionPerformed
        // TODO add your handling code here:
        if (check_selected())
        {
            TimingTargetAdapter tt = make_spring_button_dlg( new ProxyOverview(main, true),  get_dlg_pos(),  UserMain.getString("Mail_Proxy") );
            spring_button_action(evt.getSource(), tt);
        }
        
}//GEN-LAST:event_BT_PROXYActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_DISKARCHIVES;
    private javax.swing.JButton BT_HOTFOLDER;
    private javax.swing.JButton BT_MILTER;
    private javax.swing.JButton BT_PROXY;
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
