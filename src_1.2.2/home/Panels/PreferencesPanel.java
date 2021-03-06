/*
 * LoginPanel.java
 *
 * Created on 22. Mai 2008, 15:05
 */

package dimm.home.Panels;

import dimm.home.Main;
import dimm.home.Preferences;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.Rendering.UI_Generic;
import dimm.home.UserMain;
import home.shared.Utilities.ParseToken;
import java.util.ArrayList;
import javax.swing.JButton;

// Preferences

/**
 
 @author  Administrator
 */
public class PreferencesPanel extends GlossDialogPanel
{
    boolean okay;
    int last_ui;

    public boolean isOkay()
    {
        return okay;
    }
    
    /** Creates new form LoginPanel */
    public PreferencesPanel()
    {
        initComponents();
        CB_CHECK_NEW.setSelected( Main.get_long_prop(Preferences.CHECK_NEWS) > 0);
        CB_CACHE_MAILS.setSelected( Main.get_long_prop(Preferences.CACHE_MAILFILES) > 0);



        COMBO_UI.removeAllItems();
        ArrayList<String> ui_names = UI_Generic.get_ui_names();
        for (int i = 0; i < ui_names.size(); i++)
        {
            String string = ui_names.get(i);
            COMBO_UI.addItem(string);
        }

        last_ui = (int)Main.get_long_prop(Preferences.UI, 0l);
        if (last_ui < COMBO_UI.getItemCount())
            COMBO_UI.setSelectedIndex( last_ui );
        else
            COMBO_UI.setSelectedIndex( 0 );

        String ds = Main.get_prop( Preferences.DEFAULT_STATION );
        if (ds != null && ds.length() > 0)
        {
            ParseToken pt = new ParseToken(ds);
            String ip = pt.GetString("IP:");
            long po = pt.GetLongValue("PO:");
            boolean only_this = pt.GetBoolean("OT:");
            TXT_SERVER_IP.setText(ip);
            if (po > 0)
                TXT_SERVER_PORT.setText(Long.toString(po) );
            CB_NO_SCANNING.setSelected(only_this);
        }

        String l_code = Main.get_prop( Preferences.COUNTRYCODE, "DE");

        if (l_code.compareTo("DE") == 0)
           COMBO_LANG.setSelectedIndex(0);
        if (l_code.compareTo("EN") == 0)
           COMBO_LANG.setSelectedIndex(1);        
    }

    void set()
    {
        String l_code = "DE";
        if (COMBO_LANG.getSelectedIndex() == 1)
        {
            l_code = "EN";
        }
        if (COMBO_LANG.getSelectedIndex() == 0)
        {
            l_code = "DE";
        }
        Main.set_prop( Preferences.COUNTRYCODE, l_code );
        int selected_ui =  COMBO_UI.getSelectedIndex();

        Main.set_long_prop(Preferences.UI, selected_ui);
        Main.set_long_prop(Preferences.CHECK_NEWS, CB_CHECK_NEW.isSelected() ? 1 : 0);

        Main.set_long_prop(Preferences.CACHE_MAILFILES,  CB_CACHE_MAILS.isSelected() ? 1 : 0);



        String ds = "";
        if (TXT_SERVER_IP.getText().length() > 0)
        {
            ds = "NAME:MailSecurer IP:" + TXT_SERVER_IP.getText() + " PO:" + TXT_SERVER_PORT.getText();
            ds += " OT:" + (CB_NO_SCANNING.isSelected() ? "1":"0");
        }
        Main.set_prop( Preferences.DEFAULT_STATION, ds );

        Main.get_prefs().store_props();

        if (selected_ui != last_ui)
        {
            UserMain.errm_ok(UserMain.Txt("Please_restart_the_Client"));
            System.exit(0);
        }

        Main.ui = UI_Generic.create_ui( (int)Main.get_long_prop(Preferences.UI, 0l) );
        Main.ui.set_ui(false);


        if (UserMain.self != null)
        {            
            UserMain.self.restart_gui(l_code);            
        }
        else
        {
            UserMain.init_text_interface(l_code);
        }

    }

    
    /** This method is called from within the constructor to
     initialize the form.
     WARNING: Do NOT modify this code. The content of this method is
     always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();
        jLabel1 = new javax.swing.JLabel();
        COMBO_UI = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        COMBO_LANG = new javax.swing.JComboBox();
        CB_CHECK_NEW = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        TXT_SERVER_IP = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        TXT_SERVER_PORT = new javax.swing.JTextField();
        CB_NO_SCANNING = new javax.swing.JCheckBox();
        CB_CACHE_MAILS = new javax.swing.JCheckBox();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();

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

        jLabel1.setText(UserMain.Txt("Look_n_Feel")); // NOI18N

        jLabel2.setText(UserMain.Txt("Language")); // NOI18N

        COMBO_LANG.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Deutsch", "English" }));

        CB_CHECK_NEW.setText("  ");
        CB_CHECK_NEW.setOpaque(false);

        jLabel3.setText(UserMain.Txt("CheckNews")); // NOI18N

        jLabel4.setText(UserMain.Txt("Server-IP")); // NOI18N

        jLabel5.setText(UserMain.Txt("Server-Port")); // NOI18N

        CB_NO_SCANNING.setText(UserMain.Txt("No_Scan_for_Servers")); // NOI18N
        CB_NO_SCANNING.setOpaque(false);

        CB_CACHE_MAILS.setText("  ");
        CB_CACHE_MAILS.setOpaque(false);

        jLabel6.setText(UserMain.Txt("Cache_Mails")); // NOI18N

        jLabel7.setText(UserMain.Txt("No_Scan_for_Servers")); // NOI18N

        jLabel8.setText("(default 8050)");

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
                            .addComponent(jLabel1)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel7)
                            .addComponent(jLabel6)
                            .addComponent(jLabel3))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGap(20, 20, 20)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(COMBO_UI, 0, 243, Short.MAX_VALUE)
                                    .addComponent(COMBO_LANG, 0, 243, Short.MAX_VALUE)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(TXT_SERVER_PORT, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel8))
                                    .addComponent(TXT_SERVER_IP, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(CB_CACHE_MAILS)
                                    .addComponent(CB_NO_SCANNING)
                                    .addComponent(CB_CHECK_NEW))
                                .addGap(214, 214, 214))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(BT_ABORT)
                        .addGap(18, 18, 18)
                        .addComponent(BT_OK)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ABORT, BT_OK});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(COMBO_LANG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(COMBO_UI, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(43, 43, 43)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(TXT_SERVER_IP, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(TXT_SERVER_PORT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel7)
                    .addComponent(CB_NO_SCANNING))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CB_CACHE_MAILS)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CB_CHECK_NEW)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OK)
                    .addComponent(BT_ABORT))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKActionPerformed
    {//GEN-HEADEREND:event_BT_OKActionPerformed
        // TODO add your handling code here:
        set();
        okay = true;

        this.setVisible(false);
    }//GEN-LAST:event_BT_OKActionPerformed

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ABORTActionPerformed
    {//GEN-HEADEREND:event_BT_ABORTActionPerformed
        // TODO add your handling code here:
        okay = false;
        this.setVisible(false);   
    }//GEN-LAST:event_BT_ABORTActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_OK;
    private javax.swing.JCheckBox CB_CACHE_MAILS;
    private javax.swing.JCheckBox CB_CHECK_NEW;
    private javax.swing.JCheckBox CB_NO_SCANNING;
    private javax.swing.JComboBox COMBO_LANG;
    private javax.swing.JComboBox COMBO_UI;
    private javax.swing.JTextField TXT_SERVER_IP;
    private javax.swing.JTextField TXT_SERVER_PORT;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    // End of variables declaration//GEN-END:variables

    @Override
    public JButton get_default_button()
    {
        return BT_OK;
    }
    
   

   
    
    
}
