/*
 * LoginPanel.java
 *
 * Created on 22. Mai 2008, 15:05
 */

package dimm.home.Panels;

import dimm.home.*;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import home.shared.Utilities.Validator;
import javax.swing.JButton;
import javax.swing.JDialog;

/**
 
 @author  Administrator
 */
public class CheckPwdPanel extends GlossDialogPanel
{
    UserMain main;
    private boolean  okay;
    
    /** Creates new form LoginPanel */
    public CheckPwdPanel(UserMain _main, boolean strong)
    {
        initComponents();
        main = _main;
        okay = false;
        
        CB_WEAK_PWD.setSelected(strong);
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
        PF_PWD1 = new javax.swing.JPasswordField();
        CB_WEAK_PWD = new javax.swing.JCheckBox();

        PF_PWD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PF_PWDMouseClicked(evt);
            }
        });

        jLabel1.setText(UserMain.Txt("Passwort")); // NOI18N

        jLabel2.setText(UserMain.Txt("Wiederholen")); // NOI18N

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

        PF_PWD1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PF_PWD1MouseClicked(evt);
            }
        });

        CB_WEAK_PWD.setText(UserMain.getString("Allow_weak_password")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CB_WEAK_PWD)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(PF_PWD, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE)
                            .addComponent(PF_PWD1, javax.swing.GroupLayout.DEFAULT_SIZE, 245, Short.MAX_VALUE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(PF_PWD1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(PF_PWD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(CB_WEAK_PWD)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 22, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKActionPerformed
    {//GEN-HEADEREND:event_BT_OKActionPerformed
        // TODO add your handling code here:
        String pwd = new String(PF_PWD.getPassword());
        String pwd1 = new String(PF_PWD1.getPassword());

        boolean strong = CB_WEAK_PWD.isSelected();
        if (!check_passwords( my_dlg, pwd, pwd1, strong))
            return;
        
        setOkay(true);
        this.setVisible(false);
        
    }//GEN-LAST:event_BT_OKActionPerformed

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BT_ABORTActionPerformed
        // TODO add your handling code here:
        setOkay(false);                                        
    
        this.setVisible(false);        
    }//GEN-LAST:event_BT_ABORTActionPerformed

    private void PF_PWD1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_PF_PWD1MouseClicked
        // TODO add your handling code here:
        if (UserMain.self.is_touchscreen())
        {
            UserMain.self.show_vkeyboard( this.my_dlg, PF_PWD1, false);
        }
        
    }//GEN-LAST:event_PF_PWD1MouseClicked

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
    private javax.swing.JCheckBox CB_WEAK_PWD;
    private javax.swing.JPasswordField PF_PWD;
    private javax.swing.JPasswordField PF_PWD1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    // End of variables declaration//GEN-END:variables


    public static boolean check_passwords( JDialog dlg, String pwd, String pwd1, boolean strong)
    {

        if (pwd.length() == 0)
        {
            UserMain.errm_ok(UserMain.getString("Bitte_geben_Sie_ein_Passwort_ein"));
            return false;
        }


        if (pwd.compareTo(pwd1) != 0)
        {
            UserMain.errm_ok(dlg, UserMain.getString("Die_Passworte_stimmen_nicht_ueberein,_bitte_noch_einmal_versuchen"));
            return false;
        }

        if (!strong)
        {
            if (pwd.length() < 4)
            {
                UserMain.errm_ok(dlg, UserMain.getString("Sorry,_das_Passwort_muss_mindestens_4_Zeichen_lang_sein"));
                return false;
            }
        }
        else
        {
            StringBuffer sb = new StringBuffer();
            if (!Validator.is_valid_strong_pwd(pwd, sb ))
            {
                StringBuffer msg = new StringBuffer(UserMain.getString("Das_Passwort_ist_ungültig" + ":\n") );

                if (sb.toString().contains(Validator.PWD_TOO_SHORT))
                    msg.append(UserMain.getString(Validator.PWD_TOO_SHORT) + "\n");
                if (sb.toString().contains(Validator.PWD_NO_DIGITS))
                    msg.append(UserMain.getString(Validator.PWD_NO_DIGITS) + "\n");
                if (sb.toString().contains(Validator.PWD_NO_LETTERS))
                    msg.append(UserMain.getString(Validator.PWD_NO_LETTERS) + "\n");
                if (sb.toString().contains(Validator.PWD_NO_SPECIALS))
                    msg.append(UserMain.getString(Validator.PWD_NO_SPECIALS) + " \"" + Validator.PWD_SPECIAL_CHARS + "\"");

                UserMain.errm_ok(dlg, msg.toString());
                return false;
            }
        }
        return true;
    }

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