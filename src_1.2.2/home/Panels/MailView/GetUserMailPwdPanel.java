/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GetMailAddressPanel.java
 *
 * Created on 13.11.2009, 13:21:07
 */

package dimm.home.Panels.MailView;

import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.UserMain;
import home.shared.Utilities.Validator;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.text.JTextComponent;

/**
 *
 * @author mw
 */
public class GetUserMailPwdPanel extends GlossDialogPanel
{

    private boolean okay = false;

    /** Creates new form GetMailAddressPanel */
    public GetUserMailPwdPanel()
    {
        initComponents();
    }

    public void enable_mail_list( boolean f, ArrayList<String> mail_list )
    {
        CB_MAILLIST.setVisible(f);
        LB_MAIL.setVisible(f);
        CB_MAILLIST.removeAllItems();

        if (mail_list != null)
        {
            for (int i = 0; i < mail_list.size(); i++)
            {
                CB_MAILLIST.addItem( mail_list.get(i) );
            }
        }
    }
    public void enable_user( boolean f, String def )
    {
        TXT_USER.setVisible(f);
        TXT_USER.setText(def);
    }
    public void enable_pwd( boolean f, String def )
    {
        TXTP_PWD.setVisible(f);
        TXTP_PWD.setText(def);
    }

    public String get_mail()
    {
        if (CB_MAILLIST.getSelectedItem() != null)
            return CB_MAILLIST.getSelectedItem().toString();

        JTextComponent tc = (JTextComponent) CB_MAILLIST.getEditor().getEditorComponent();

        return tc.getText();
    }
    public String get_user()
    {
        return TXT_USER.getText();
    }
    public String get_pwd()
    {
        char[] arr = TXTP_PWD.getPassword();
        return new String( arr );
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LB_MAIL = new javax.swing.JLabel();
        CB_MAILLIST = new javax.swing.JComboBox();
        BT_OKAY = new GlossButton();
        BT_ABORT = new GlossButton();
        LB_USER = new javax.swing.JLabel();
        TXT_USER = new javax.swing.JTextField();
        LB_PWD = new javax.swing.JLabel();
        TXTP_PWD = new javax.swing.JPasswordField();

        LB_MAIL.setText(UserMain.Txt("EMailaddress")); // NOI18N

        CB_MAILLIST.setEditable(true);

        BT_OKAY.setText(UserMain.Txt("OK")); // NOI18N
        BT_OKAY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKAYActionPerformed(evt);
            }
        });

        BT_ABORT.setText(UserMain.Txt("Abort")); // NOI18N
        BT_ABORT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ABORTActionPerformed(evt);
            }
        });

        LB_USER.setText(UserMain.Txt("Username")); // NOI18N

        LB_PWD.setText(UserMain.Txt("Password")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LB_USER)
                            .addComponent(LB_PWD)
                            .addComponent(LB_MAIL))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CB_MAILLIST, 0, 250, Short.MAX_VALUE)
                            .addComponent(TXT_USER, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                            .addComponent(TXTP_PWD, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_OKAY, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ABORT, BT_OKAY});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LB_USER)
                    .addComponent(TXT_USER, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LB_PWD)
                    .addComponent(TXTP_PWD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LB_MAIL)
                    .addComponent(CB_MAILLIST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OKAY, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ABORTActionPerformed
    {//GEN-HEADEREND:event_BT_ABORTActionPerformed
        // TODO add your handling code here:
        setOkay(false);
        my_dlg.setVisible(false);
    }//GEN-LAST:event_BT_ABORTActionPerformed

    private void BT_OKAYActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKAYActionPerformed
    {//GEN-HEADEREND:event_BT_OKAYActionPerformed
        // TODO add your handling code here:
        String m = get_mail();
        if ( CB_MAILLIST.isVisible() && !Validator.is_valid_email(get_mail()))
        {
            UserMain.errm_ok(UserMain.getString("Die_Mailadresse_ist_nicht_okay"));
            return;
        }
        if ( TXT_USER.isVisible() && !Validator.is_valid_name(get_user(), 80))
        {
            UserMain.errm_ok(UserMain.getString("Der_Benutzer_ist_nicht_okay"));
            return;
        }
        if ( TXTP_PWD.isVisible() && !Validator.is_valid_name(get_pwd(), 80))
        {
            UserMain.errm_ok(UserMain.getString("Das_Passwort_ist_nicht_okay"));
            return;
        }
        setOkay(true);
        my_dlg.setVisible(false);

    }//GEN-LAST:event_BT_OKAYActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_OKAY;
    private javax.swing.JComboBox CB_MAILLIST;
    private javax.swing.JLabel LB_MAIL;
    private javax.swing.JLabel LB_PWD;
    private javax.swing.JLabel LB_USER;
    private javax.swing.JPasswordField TXTP_PWD;
    private javax.swing.JTextField TXT_USER;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the okay
     */
    public boolean isOkay()
    {
        return okay;
    }

    /**
     * @param okay the okay to set
     */
    public void setOkay( boolean okay )
    {
        this.okay = okay;
    }

    @Override
    public JButton get_default_button()
    {
        return BT_OKAY;
    }

}
