/*
 * LoginPanel.java
 *
 * Created on 22. Mai 2008, 15:05
 */

package dimm.home.Panels;

import dimm.home.*;
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import home.shared.hibernate.Role;
import javax.swing.JButton;

/**
 
 @author  Administrator
 */
public class Login4EyesPanel extends GlossDialogPanel
{

    public static boolean check_login(Role r)
    {
        Login4EyesPanel pnl = new Login4EyesPanel(r);

        GenericGlossyDlg dlg = new GenericGlossyDlg(null, true, pnl);

        dlg.setLocation(UserMain.self.getLocationOnScreen().x + 200, UserMain.self.getLocationOnScreen().y + 50);
        dlg.setTitle(UserMain.getString("4_eyes_login_for_role") + " " + r.getName());
        dlg.setVisible(true);

        return pnl.isOkay();
    }
    
    private boolean  okay;
    Role role;
    
    /** Creates new form LoginPanel */
    public Login4EyesPanel(Role r)
    {
        initComponents();
        okay = false;
        role = r;
        
    }
    
    /** This method is called from within the constructor to
     initialize the form.
     WARNING: Do NOT modify this code. The content of this method is
     always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PF_PWD = new javax.swing.JPasswordField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();
        TXT_USER = new javax.swing.JTextField();

        PF_PWD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PF_PWDMouseClicked(evt);
            }
        });

        jLabel1.setText(UserMain.Txt("Username")); // NOI18N

        jLabel2.setText(UserMain.Txt("Password")); // NOI18N

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

        TXT_USER.setText(UserMain.Txt("Login4EyesPanel.TXT_USER.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PF_PWD, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)
                            .addComponent(TXT_USER, javax.swing.GroupLayout.DEFAULT_SIZE, 218, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 110, Short.MAX_VALUE)
                        .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(TXT_USER, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(PF_PWD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 32, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKActionPerformed
    {//GEN-HEADEREND:event_BT_OKActionPerformed
        // TODO add your handling code here:
        String user = TXT_USER.getText();
        String pwd = new String(PF_PWD.getPassword());

        if (role.getUser4eyes() != null && user.compareTo(role.getUser4eyes()) != 0)
        {
            UserMain.errm_ok(UserMain.getString("Der_4-Augen_Username_ist_nicht_okay"));
            return;
        }


        if (role.getPwd4eyes() != null && pwd.compareTo(role.getPwd4eyes()) != 0)
        {
            UserMain.errm_ok(UserMain.getString("Das_4-Augen_Passwort_ist_nicht_okay"));
            return;
        }

        setOkay(true);
        this.setVisible(false);
        
    }//GEN-LAST:event_BT_OKActionPerformed

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BT_ABORTActionPerformed
        // TODO add your handling code here:
        setOkay(false);                                        
    
        this.setVisible(false);        
    }//GEN-LAST:event_BT_ABORTActionPerformed

    private void PF_PWDMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PF_PWDMouseClicked
        // TODO add your handling code here:
        if (UserMain.self.is_touchscreen())
        {
            UserMain.self.show_vkeyboard( this.my_dlg, PF_PWD, false);
        }        
    }//GEN-LAST:event_PF_PWDMouseClicked
    
    
  
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_OK;
    private javax.swing.JPasswordField PF_PWD;
    private javax.swing.JTextField TXT_USER;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables


    @Override
    public JButton get_default_button()
    {
        return BT_OK;
    }

    public boolean isOkay()
    {
        return okay;
    }

    public void setOkay(boolean okay)
    {
        this.okay = okay;
    }
    public String get_pwd()
    {
        return new String( PF_PWD.getPassword() );
    }
    
}