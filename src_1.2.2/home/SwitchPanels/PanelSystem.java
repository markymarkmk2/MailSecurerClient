/*
 * PanelTasks.java
 *
 * Created on 13. M�rz 2008, 09:39
 */
package dimm.home.SwitchPanels;

import dimm.home.*;
import dimm.home.Panels.CertificatePanel;
import dimm.home.Panels.LicensePanel;
import dimm.home.Panels.MandantOverview;
import dimm.home.Panels.SetAdminLoginPanel;
import dimm.home.Rendering.FlatBackgroundTitle;
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
public class PanelSystem extends SwitchSpringPanel
{

    /** Creates new form PanelTasks */
    public PanelSystem( UserMain m )
    {
        super(m, UserMain.PBC_ADMIN);
        ResourceInjector.get().inject(this);

        initComponents();

        add_titles();

    }
    void add_titles()
    {
        PN_BUTTONS.add(new FlatBackgroundTitle( UserMain.getString("Mandanten" ) ), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 35, getWidth(), 60));
        PN_BUTTONS.add(new FlatBackgroundTitle( UserMain.getString("Werkzeuge" ) ), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 155, getWidth(), 60));
    }

    @Override
    public void setSize( Dimension d )
    {
        this.removeAll();
        
        super.setSize(d);
        

        initComponents();

        add_titles();
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
        BT_SET_ADMIN_LOGIN = new GhostButton();
        BT_MANDANTEN = new GhostButton();
        BT_LICENSE = new GhostButton();
        BT_ZERTIFIKATE = new GhostButton();
        PN_HEADER = new javax.swing.JPanel();

        setOpaque(false);

        PN_BUTTONS.setOpaque(false);
        PN_BUTTONS.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BT_SET_ADMIN_LOGIN.setFont(new java.awt.Font("Arial", 0, 14));
        BT_SET_ADMIN_LOGIN.setForeground(new java.awt.Color(201, 201, 201));
        BT_SET_ADMIN_LOGIN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/login-register.png"))); // NOI18N
        BT_SET_ADMIN_LOGIN.setText(UserMain.Txt("Sysadmin")); // NOI18N
        BT_SET_ADMIN_LOGIN.setToolTipText(UserMain.getString("Set_Master_User_login_and_password")); // NOI18N
        BT_SET_ADMIN_LOGIN.setBorderPainted(false);
        BT_SET_ADMIN_LOGIN.setContentAreaFilled(false);
        BT_SET_ADMIN_LOGIN.setFocusPainted(false);
        BT_SET_ADMIN_LOGIN.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_SET_ADMIN_LOGIN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_SET_ADMIN_LOGINActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_SET_ADMIN_LOGIN, new org.netbeans.lib.awtextra.AbsoluteConstraints(240, 200, 190, 50));

        BT_MANDANTEN.setFont(new java.awt.Font("Arial", 0, 14));
        BT_MANDANTEN.setForeground(new java.awt.Color(201, 201, 201));
        BT_MANDANTEN.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/rollen.png"))); // NOI18N
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
        PN_BUTTONS.add(BT_MANDANTEN, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 80, 150, 50));

        BT_LICENSE.setFont(new java.awt.Font("Arial", 0, 14));
        BT_LICENSE.setForeground(new java.awt.Color(201, 201, 201));
        BT_LICENSE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/disk-jockey-32x32.png"))); // NOI18N
        BT_LICENSE.setText(UserMain.Txt("Lizenzen")); // NOI18N
        BT_LICENSE.setToolTipText(UserMain.getString("Create_or_replace_your_licenses")); // NOI18N
        BT_LICENSE.setBorderPainted(false);
        BT_LICENSE.setContentAreaFilled(false);
        BT_LICENSE.setFocusPainted(false);
        BT_LICENSE.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_LICENSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_LICENSEActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_LICENSE, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 200, 190, 50));

        BT_ZERTIFIKATE.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BT_ZERTIFIKATE.setForeground(new java.awt.Color(201, 201, 201));
        BT_ZERTIFIKATE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/imap.png"))); // NOI18N
        BT_ZERTIFIKATE.setText(UserMain.Txt("Certificats")); // NOI18N
        BT_ZERTIFIKATE.setToolTipText(UserMain.getString("Create_or_replace_your_certificates")); // NOI18N
        BT_ZERTIFIKATE.setBorderPainted(false);
        BT_ZERTIFIKATE.setContentAreaFilled(false);
        BT_ZERTIFIKATE.setFocusPainted(false);
        BT_ZERTIFIKATE.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_ZERTIFIKATE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ZERTIFIKATEActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_ZERTIFIKATE, new org.netbeans.lib.awtextra.AbsoluteConstraints(410, 200, 190, 50));

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
   

    
    private void BT_SET_ADMIN_LOGINActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_SET_ADMIN_LOGINActionPerformed
    {//GEN-HEADEREND:event_BT_SET_ADMIN_LOGINActionPerformed
        // TODO add your handling code here:
       if (check_selected())
        {
            TimingTargetAdapter tt = make_spring_button_dlg( new SetAdminLoginPanel(),  get_dlg_pos(),  UserMain.getString("Sysadmin_login") );
            spring_button_action(evt.getSource(), tt);
        }
        
}//GEN-LAST:event_BT_SET_ADMIN_LOGINActionPerformed

    private void BT_MANDANTENActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_MANDANTENActionPerformed
    {//GEN-HEADEREND:event_BT_MANDANTENActionPerformed
        // TODO add your handling code here:
       if (check_selected())
        {
            TimingTargetAdapter tt = make_spring_button_dlg( new MandantOverview(main, true),  get_dlg_pos(),  UserMain.getString("Mandanten") );
            spring_button_action(evt.getSource(), tt);
        }

}//GEN-LAST:event_BT_MANDANTENActionPerformed

    private void BT_LICENSEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_LICENSEActionPerformed
    {//GEN-HEADEREND:event_BT_LICENSEActionPerformed
        // TODO add your handling code here:
       if (check_selected())
        {
            TimingTargetAdapter tt = make_spring_button_dlg( new LicensePanel(),  get_dlg_pos(),  UserMain.getString("Licenses") );
            spring_button_action(evt.getSource(), tt);
        }

    }//GEN-LAST:event_BT_LICENSEActionPerformed

    private void BT_ZERTIFIKATEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ZERTIFIKATEActionPerformed
    {//GEN-HEADEREND:event_BT_ZERTIFIKATEActionPerformed
        // TODO add your handling code here:
       if (check_selected())
        {
            TimingTargetAdapter tt = make_spring_button_dlg( new CertificatePanel(),  get_dlg_pos(),  UserMain.getString("Certificates") );
            spring_button_action(evt.getSource(), tt);
        }

    }//GEN-LAST:event_BT_ZERTIFIKATEActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_LICENSE;
    private javax.swing.JButton BT_MANDANTEN;
    private javax.swing.JButton BT_SET_ADMIN_LOGIN;
    private javax.swing.JButton BT_ZERTIFIKATE;
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
