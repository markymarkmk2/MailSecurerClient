/*
 * PanelTasks.java
 *
 * Created on 13. M�rz 2008, 09:39
 */
package dimm.home.SwitchPanels;

import dimm.home.Panels.AuditPanel;
import dimm.home.Panels.Diagnose.StorageDiagnose;
import dimm.home.Panels.LogPanel;
import dimm.home.Panels.MailImport.PanelImportMailbox;
import dimm.home.Rendering.FlatBackgroundTitle;
import dimm.home.Rendering.GhostButton;
import dimm.home.Rendering.SwitchSpringPanel;
import dimm.home.Rendering.TimingTargetAdapter;
import dimm.home.UserMain;
import java.awt.Dimension;
import java.awt.Point;

import jrdesktop.utilities.JRConnectEvent;
import jrdesktop.utilities.JRConnectEventListener;
import org.jdesktop.fuse.ResourceInjector;



/**
 *
 * @author  Administrator
 */
public class PanelTools extends SwitchSpringPanel implements JRConnectEventListener
{

    /** Creates new form PanelTasks */
    public PanelTools( UserMain m )
    {
        super(m, UserMain.PBC_ADMIN);
        ResourceInjector.get().inject(this);

        initComponents();



        add_titles();

    }
    void add_titles()
    {
        PN_BUTTONS.add(new FlatBackgroundTitle( UserMain.getString("Tools" ) ), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 35, getWidth(), 60));
        PN_BUTTONS.add(new FlatBackgroundTitle( UserMain.getString("Import" ) ), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 155, getWidth(), 60));
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
        BT_IMPORT_MBOX = new GhostButton();
        BT_INIT = new GhostButton();
        BT_STATUS = new GhostButton();
        BT_LOG = new GhostButton();
        BT_AUDITLOG = new GhostButton();
        BT_DIAGNOSE = new GhostButton();
        PN_HEADER = new javax.swing.JPanel();

        setOpaque(false);

        PN_BUTTONS.setOpaque(false);
        PN_BUTTONS.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BT_IMPORT_MBOX.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BT_IMPORT_MBOX.setForeground(new java.awt.Color(201, 201, 201));
        BT_IMPORT_MBOX.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/import.png"))); // NOI18N
        BT_IMPORT_MBOX.setText(UserMain.Txt("Import")); // NOI18N
        BT_IMPORT_MBOX.setToolTipText(UserMain.Txt("Import_Mailboxen")); // NOI18N
        BT_IMPORT_MBOX.setBorderPainted(false);
        BT_IMPORT_MBOX.setContentAreaFilled(false);
        BT_IMPORT_MBOX.setFocusPainted(false);
        BT_IMPORT_MBOX.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_IMPORT_MBOX.setMaximumSize(new java.awt.Dimension(101, 26));
        BT_IMPORT_MBOX.setMinimumSize(new java.awt.Dimension(101, 26));
        BT_IMPORT_MBOX.setPreferredSize(new java.awt.Dimension(101, 26));
        BT_IMPORT_MBOX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_IMPORT_MBOXActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_IMPORT_MBOX, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 200, 170, 50));

        BT_INIT.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BT_INIT.setForeground(new java.awt.Color(201, 201, 201));
        BT_INIT.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/diagnose.png"))); // NOI18N
        BT_INIT.setText(UserMain.Txt("Init")); // NOI18N
        BT_INIT.setToolTipText(UserMain.Txt("Long_Init")); // NOI18N
        BT_INIT.setBorderPainted(false);
        BT_INIT.setContentAreaFilled(false);
        BT_INIT.setFocusPainted(false);
        BT_INIT.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_INIT.setMaximumSize(new java.awt.Dimension(101, 26));
        BT_INIT.setMinimumSize(new java.awt.Dimension(101, 26));
        BT_INIT.setPreferredSize(new java.awt.Dimension(101, 26));
        BT_INIT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_INITActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_INIT, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 80, 170, 50));

        BT_STATUS.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BT_STATUS.setForeground(new java.awt.Color(201, 201, 201));
        BT_STATUS.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/disk-jockey-32x32.png"))); // NOI18N
        BT_STATUS.setText(UserMain.Txt("RemoteSupport")); // NOI18N
        BT_STATUS.setToolTipText(UserMain.Txt("Long_Remote")); // NOI18N
        BT_STATUS.setBorderPainted(false);
        BT_STATUS.setContentAreaFilled(false);
        BT_STATUS.setFocusPainted(false);
        BT_STATUS.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_STATUS.setMaximumSize(new java.awt.Dimension(101, 26));
        BT_STATUS.setMinimumSize(new java.awt.Dimension(101, 26));
        BT_STATUS.setPreferredSize(new java.awt.Dimension(101, 26));
        BT_STATUS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_STATUSActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_STATUS, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 80, 170, 50));

        BT_LOG.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BT_LOG.setForeground(new java.awt.Color(201, 201, 201));
        BT_LOG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/diagnose.png"))); // NOI18N
        BT_LOG.setText(UserMain.Txt("Log")); // NOI18N
        BT_LOG.setToolTipText(UserMain.Txt("Long_Diag")); // NOI18N
        BT_LOG.setBorderPainted(false);
        BT_LOG.setContentAreaFilled(false);
        BT_LOG.setFocusPainted(false);
        BT_LOG.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_LOG.setMaximumSize(new java.awt.Dimension(101, 26));
        BT_LOG.setMinimumSize(new java.awt.Dimension(101, 26));
        BT_LOG.setPreferredSize(new java.awt.Dimension(101, 26));
        BT_LOG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_LOGActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_LOG, new org.netbeans.lib.awtextra.AbsoluteConstraints(400, 80, 170, 50));

        BT_AUDITLOG.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BT_AUDITLOG.setForeground(new java.awt.Color(201, 201, 201));
        BT_AUDITLOG.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/diagnose.png"))); // NOI18N
        BT_AUDITLOG.setText(UserMain.Txt("AuditLog")); // NOI18N
        BT_AUDITLOG.setToolTipText(UserMain.Txt("Long_AuditLog")); // NOI18N
        BT_AUDITLOG.setBorderPainted(false);
        BT_AUDITLOG.setContentAreaFilled(false);
        BT_AUDITLOG.setFocusPainted(false);
        BT_AUDITLOG.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_AUDITLOG.setMaximumSize(new java.awt.Dimension(101, 26));
        BT_AUDITLOG.setMinimumSize(new java.awt.Dimension(101, 26));
        BT_AUDITLOG.setPreferredSize(new java.awt.Dimension(101, 26));
        BT_AUDITLOG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_AUDITLOGActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_AUDITLOG, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 80, 170, 50));

        BT_DIAGNOSE.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        BT_DIAGNOSE.setForeground(new java.awt.Color(201, 201, 201));
        BT_DIAGNOSE.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/status.png"))); // NOI18N
        BT_DIAGNOSE.setText(UserMain.Txt("Diagnose")); // NOI18N
        BT_DIAGNOSE.setToolTipText(UserMain.Txt("Long_Diag")); // NOI18N
        BT_DIAGNOSE.setBorderPainted(false);
        BT_DIAGNOSE.setContentAreaFilled(false);
        BT_DIAGNOSE.setFocusPainted(false);
        BT_DIAGNOSE.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_DIAGNOSE.setMaximumSize(new java.awt.Dimension(101, 26));
        BT_DIAGNOSE.setMinimumSize(new java.awt.Dimension(101, 26));
        BT_DIAGNOSE.setPreferredSize(new java.awt.Dimension(101, 26));
        BT_DIAGNOSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_DIAGNOSEActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_DIAGNOSE, new org.netbeans.lib.awtextra.AbsoluteConstraints(230, 200, 170, 50));

        PN_HEADER.setOpaque(false);
        PN_HEADER.setLayout(new javax.swing.BoxLayout(PN_HEADER, javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(PN_BUTTONS, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE)
                    .addComponent(PN_HEADER, javax.swing.GroupLayout.DEFAULT_SIZE, 800, Short.MAX_VALUE))
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
   

    
    private void BT_IMPORT_MBOXActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_IMPORT_MBOXActionPerformed
    {//GEN-HEADEREND:event_BT_IMPORT_MBOXActionPerformed
        // TODO add your handling code here:
       if (check_selected())
        {
            TimingTargetAdapter tt = make_spring_button_dlg( new PanelImportMailbox(),  get_dlg_pos(),  UserMain.getString("MailboxImport") );
            spring_button_action(evt.getSource(), tt);
        }

    }//GEN-LAST:event_BT_IMPORT_MBOXActionPerformed

    private void BT_INITActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_INITActionPerformed
    {//GEN-HEADEREND:event_BT_INITActionPerformed

        if (UserMain.errm_ok_cancel(null, UserMain.Txt("Do_you_want_to_restart_this_company?")))
        {
            UserMain.self.initialize_act_mandant();
        }
}//GEN-LAST:event_BT_INITActionPerformed

    private void BT_LOGActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_LOGActionPerformed
    {//GEN-HEADEREND:event_BT_LOGActionPerformed
        // TODO add your handling code here:
        LogPanel log_panel = new LogPanel();

        TimingTargetAdapter tt = make_spring_button_dlg( log_panel,  get_dlg_pos(),  UserMain.getString("Log") );
        spring_button_action(evt.getSource(), tt);


    }//GEN-LAST:event_BT_LOGActionPerformed

    private void BT_STATUSActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_STATUSActionPerformed
    {//GEN-HEADEREND:event_BT_STATUSActionPerformed
        // TODO add your handling code here:

        if (!UserMain.is_jrd_server_running())
        {
            UserMain.start_jrd_server(this);
        }
        else
        {
            UserMain.stop_jrd_server(this);
        }
        BT_STATUS.repaint();

    }//GEN-LAST:event_BT_STATUSActionPerformed

    private void BT_AUDITLOGActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_AUDITLOGActionPerformed
    {//GEN-HEADEREND:event_BT_AUDITLOGActionPerformed
        // TODO add your handling code here:

        TimingTargetAdapter tt = make_spring_button_dlg( new AuditPanel( UserMain.self.get_act_mandant_id() ),  get_dlg_pos(),  UserMain.getString("AuditLog") );
        spring_button_action(evt.getSource(), tt);

    }//GEN-LAST:event_BT_AUDITLOGActionPerformed

    private void BT_DIAGNOSEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_DIAGNOSEActionPerformed
    {//GEN-HEADEREND:event_BT_DIAGNOSEActionPerformed
        // TODO add your handling code here:
        TimingTargetAdapter tt = make_spring_button_dlg( new StorageDiagnose( UserMain.self.get_act_mandant_id() ),  get_dlg_pos(),  UserMain.getString("StorageDiagnose") );
        spring_button_action(evt.getSource(), tt);

    }//GEN-LAST:event_BT_DIAGNOSEActionPerformed
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_AUDITLOG;
    private javax.swing.JButton BT_DIAGNOSE;
    private javax.swing.JButton BT_IMPORT_MBOX;
    private javax.swing.JButton BT_INIT;
    private javax.swing.JButton BT_LOG;
    private javax.swing.JButton BT_STATUS;
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


    @Override
    public void connect_event( JRConnectEvent ext )
    {
        if (ext.getEvent() == JRConnectEvent.EVENT.CONNECTED)
        {
             BT_STATUS.setText(UserMain.Txt("CloseRemoteSupport"));
             BT_STATUS.setToolTipText(UserMain.Txt("CloseRemoteSupport"));
        }
        else
        {
             BT_STATUS.setText(UserMain.Txt("RemoteSupport"));
             BT_STATUS.setToolTipText(UserMain.Txt("Long_Remote"));
        }
    }

   
}