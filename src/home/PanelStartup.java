/*
 * PanelTasks.java
 *
 * Created on 13. M�rz 2008, 09:39
 */
package dimm.home;

import dimm.home.Rendering.BackgroundTitle;
import dimm.home.Rendering.GhostButton;
import dimm.home.Rendering.SwitchSpringPanel;
import java.awt.Dimension;
import java.awt.Point;
import org.jdesktop.fuse.ResourceInjector;



/**
 *
 * @author  Administrator
 */
public class PanelStartup extends SwitchSpringPanel
{
    

    /** Creates new form PanelTasks */
    public PanelStartup( UserMain m )
    {
        super(m, UserMain.PBC_ADMIN);
        ResourceInjector.get().inject(this);

        initComponents();


        PN_BUTTONS.add(new BackgroundTitle( UserMain.getString("Aufgaben" ) ), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 65, getWidth(), 60));
       // PN_BUTTONS.add(new BackgroundTitle( UserMain.getString("Werkzeuge" ) ), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 285, getWidth(), 60));
        
    }

    @Override
    public void setSize( Dimension d )
    {
        this.removeAll();
        
        super.setSize(d);
        

        initComponents();


        PN_BUTTONS.add(new BackgroundTitle( UserMain.getString("Aufgaben" ) ), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 65, getWidth(), 60));
        //PN_BUTTONS.add(new BackgroundTitle( UserMain.getString("Werkzeuge" ) ), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 285, getWidth(), 60));
    }


 
    boolean check_selected()
    {
        return true;
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
        PN_HEADER = new javax.swing.JPanel();

        setOpaque(false);

        PN_BUTTONS.setOpaque(false);
        PN_BUTTONS.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BT_NETWORK.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BT_NETWORK.setForeground(new java.awt.Color(201, 201, 201));
        BT_NETWORK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/tr_lupe.png"))); // NOI18N
        BT_NETWORK.setText(UserMain.Txt("Nach_Mail_st�bern")); // NOI18N
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
        PN_BUTTONS.add(BT_NETWORK, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 130, 170, 50));

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

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
}