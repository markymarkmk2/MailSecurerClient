/*
 * LoginPanel.java
 *
 * Created on 22. Mai 2008, 15:05
 */

package dimm.home.Panels;

import home.shared.SQL.SQLResult;
import dimm.home.Main;
import dimm.home.Preferences;
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.ServerConnect.CommContainer;
import dimm.home.ServerConnect.FunctionCallConnect;
import dimm.home.ServerConnect.SQLConnect;
import dimm.home.ServerConnect.StationEntry;
import dimm.home.ServerConnect.UDP_Communicator;
import dimm.home.UserMain;
import home.shared.Utilities.ParseToken;
import dimm.home.Utilities.SwingWorker;
import home.shared.CS_Constants.USERMODE;
import home.shared.SQL.OptCBEntry;
import home.shared.SQL.UserSSOEntry;
import home.shared.hibernate.Mandant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataListener;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

// Preferences

class MailSecurerComboModel implements ComboBoxModel
{
    ArrayList<StationEntry> st_list;
    int act_st_idx;


    public MailSecurerComboModel( ArrayList<StationEntry> st_list )
    {
        this.st_list = st_list;
        if (st_list.isEmpty())
            act_st_idx = -1;
        else
            act_st_idx = 0;

    }


    @Override
    public void setSelectedItem( Object anItem )
    {
        for (int i = 0; i < st_list.size(); i++)
        {
            if (st_list.get(i).toString().compareTo(anItem.toString()) == 0)
            {
                act_st_idx = i;
                break;
            }
        }
    }

   @Override
    public Object getSelectedItem()
    {
       if (st_list.isEmpty())
           return null;
        StationEntry da = st_list.get(act_st_idx);
        if (da != null)
            return da.toString();
        return null;
    }

   @Override
    public int getSize()
    {
        return st_list.size();
    }

   @Override
    public Object getElementAt( int index )
    {
        return st_list.get(index).toString();
    }

   @Override
    public void addListDataListener( ListDataListener l )
    {
    }

   @Override
    public void removeListDataListener( ListDataListener l )
    {
    }
   StationEntry get_act_station()
   {
        if (st_list.isEmpty())
           return null;
       return st_list.get(act_st_idx);
   }
}
class MandantComboModel implements ComboBoxModel
{
    ArrayList<Mandant> ma_list;
    int act_ma_idx;


    public MandantComboModel( ArrayList<Mandant> st_list )
    {
        this.ma_list = st_list;
    }


    @Override
    public void setSelectedItem( Object anItem )
    {

        if (anItem instanceof String)
        {
            for (int i = 0; i < ma_list.size(); i++)
            {
                if (ma_list.get(i).getName().compareTo(anItem.toString()) == 0)
                {
                    act_ma_idx = i;
                    break;
                }
            }
        }
        if (anItem instanceof Mandant)
        {
            for (int i = 0; i < ma_list.size(); i++)
            {
                if (ma_list.get(i) == anItem)
                {
                    act_ma_idx = i;
                    break;
                }
            }
        }
    }

   @Override
    public Object getSelectedItem()
    {
       if (ma_list.isEmpty())
           return null;

        Mandant da = ma_list.get(act_ma_idx);
        if (da != null)
            return da.getName();
        return null;
    }

   @Override
    public int getSize()
    {
        return ma_list.size();
    }

   @Override
    public Object getElementAt( int index )
    {
       if (ma_list.isEmpty())
           return null;
       
        return ma_list.get(index).getName();
    }

   @Override
    public void addListDataListener( ListDataListener l )
    {
    }

   @Override
    public void removeListDataListener( ListDataListener l )
    {
    }

   Mandant get_act_mandant()
   {
       if (ma_list.isEmpty())
           return null;
       return ma_list.get(act_ma_idx);
   }
}
/**
 
 @author  Administrator
 */
public class LoginPanel extends GlossDialogPanel implements CommContainer
{
    UserMain main;
    int login_retries;
    private boolean in_init;

    @InjectedResource
    private String gui_default_station = "";
   

    /** Creates new form LoginPanel */
    public LoginPanel(UserMain _main)
    {
        in_init = true;
        ResourceInjector.get().inject(this);

        initComponents();
        
        BT_SSL.setSelected(Main.get_bool_prop(Preferences.SERVER_SSL, true));


        CB_USER.removeAllItems();
        CB_USER.addItem(UserMain.getString("Anwender") );
        CB_USER.addItem(UserMain.getString("Verwaltung") );
        CB_USER.addItem(UserMain.getString("Verwaltung_alle_Mandanten") );
        if (Main.enable_admin)
            CB_USER.addItem(UserMain.getString("System") );
        main = _main;
        login_retries = 0;
        

        
        in_init = false;


        PF_PWD.requestFocus();
        main.set_titel("");

            SwingUtilities.invokeLater( new Runnable()
                 {
                @Override
                 public void run()
                     {
                     PF_PWD.requestFocus();
                     set_lists_bg();
                 }
             });

    }

  


    void set_lists_bg()
    {
        UserMain.self.show_busy(my_dlg, UserMain.Txt("Scanning") + "...");
        SwingWorker local_sw = new SwingWorker() {

            @Override
            public Object construct()
            {
                ArrayList<StationEntry> st_list = build_st_list();

                MailSecurerComboModel server_model = new MailSecurerComboModel(st_list);
                CB_SERVER.setModel(server_model);
                if (st_list.size() > 0)
                {
                    server_model.setSelectedItem(0);

                    ArrayList<Mandant> ma_list = build_mandant_list( st_list.get(0) );
                    MandantComboModel mandant_model = new MandantComboModel(ma_list);
                    CB_MANDANT.setModel(mandant_model);
                }
                UserMain.self.hide_busy();

                return null;
            }
        };
        local_sw.start();

    }
    
    boolean use_mallorca_proxy()
    {
        return true;
    }

    SwingWorker sw;
    boolean do_login()
    {
        Mandant ma = null;
        StationEntry st = ((MailSecurerComboModel)CB_SERVER.getModel()).get_act_station();

        if (st != null)
        {
            if (CB_MANDANT.isVisible())
            {
                if (CB_MANDANT.getModel().getSize() == 0)
                {
                    UserMain.errm_ok(UserMain.getString("This_MailSecurer_has_not_any_valid_companies_yet"));
                    return false;
                }
                ma = ((MandantComboModel)CB_MANDANT.getModel()).get_act_mandant();
            }
        }

        
        if (st == null || (ma == null && CB_MANDANT.isVisible()))
        {
            UserMain.errm_ok(UserMain.getString("Please_select_Server_and_a_company_first"));
            return false;            
        }

        int ma_id = -1;

        if (ma != null)
            ma_id = ma.getId();


        // DEFAULT PORT IS BASE PORT + 1 + MA_ID
        int port = st.get_port() + ma_id + 1;
        String ip = st.get_ip();

        // ALLOW MODIFIED PORT AND IP
        FunctionCallConnect fcc = new FunctionCallConnect(st.get_ip(), st.get_port(), BT_SSL.isSelected());
        String answer =  fcc.call_abstract_function("GETSETOPTION CMD:GETTCP MA:" + ma_id );
        if (answer != null && answer.length() > 3 && answer.charAt(0) == '0')
        {
            ParseToken pt = new ParseToken(answer.substring(3));
            port = (int)pt.GetLongValue("PO:");
            ip = pt.GetString("IP:");
        }
        fcc.close();


        UserMain.set_comm_params( ma_id, ip, port, BT_SSL.isSelected() );


        boolean ret = _do_login(ma_id);
        
        if (!ret)
        {
            login_retries++;
            if (login_retries == 5)
            {
                UserMain.errm_ok(UserMain.getString("Halloo??_Konzentration_bitte!"));
            }
            if (login_retries >= 8)
            {
                UserMain.errm_ok(UserMain.getString("Na,_ich_glaub_das_wird_nichts_mehr!"));                                                          
            }
        }
        else
        {
            
            login_retries = 0;            
        }
        return ret;
    }
    
    boolean _do_login( int ma_id)
    {
        boolean ret = false;
        
        String pwd = new String(PF_PWD.getPassword());
        
        String user_type = CB_USER.getSelectedItem().toString();
        String user = this.TXT_USER.getText();
                     
        SQLConnect sql = UserMain.sqc();

        main.reset_act_userdata();
        sql.set_mandant_id(-1);

        // USER
        if (user_type.compareTo(UserMain.getString("Anwender"))== 0 )
        {
            ret = try_user_login( ma_id, user, pwd );            
        }
        // ADMIN
        if (user_type.compareTo(UserMain.getString("Verwaltung"))== 0 )
        {
            ret = try_admin_login( ma_id, user, pwd );
        }

        // MULTI ADMIN
        if (user_type.compareTo(UserMain.getString("System"))== 0 || user_type.compareTo(UserMain.getString("Verwaltung_alle_Mandanten"))== 0)
        {
            ret = try_sysadmin_login( user, pwd );
        }
            
        return ret;
    }
    
   

    @Override
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag)
        {
        }            
    }

    void rebuild_mandant_list()
    {
        MailSecurerComboModel model = (MailSecurerComboModel)CB_SERVER.getModel();
        if (model.st_list.size() > 0)
        {
            int idx = CB_SERVER.getSelectedIndex();
            ArrayList<Mandant> ma_list = build_mandant_list( model.st_list.get(idx) );
            MandantComboModel mandant_model = new MandantComboModel(ma_list);
            CB_MANDANT.setModel(mandant_model);
        }
    }

    
    /** This method is called from within the constructor to
     initialize the form.
     WARNING: Do NOT modify this code. The content of this method is
     always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        CB_USER = new javax.swing.JComboBox();
        LB_PWD = new javax.swing.JLabel();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();
        BT_CHANGE_PWD = new GlossButton();
        LB_USER = new javax.swing.JLabel();
        TXT_USER = new javax.swing.JTextField();
        PF_PWD = new javax.swing.JPasswordField();
        jLabel2 = new javax.swing.JLabel();
        CB_SERVER = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        CB_MANDANT = new javax.swing.JComboBox();
        BT_SSL = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();

        jLabel1.setText(UserMain.Txt("Benutzer")); // NOI18N

        CB_USER.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Verwaltung eine Box", "Verwaltung alle Boxen", "System" }));
        CB_USER.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_USERActionPerformed(evt);
            }
        });

        LB_PWD.setText(UserMain.Txt("Passwort")); // NOI18N

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

        BT_CHANGE_PWD.setText(UserMain.Txt("Neues_Passwort")); // NOI18N
        BT_CHANGE_PWD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_CHANGE_PWDActionPerformed(evt);
            }
        });

        LB_USER.setText(UserMain.getString("Loginname")); // NOI18N

        TXT_USER.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_USERMouseClicked(evt);
            }
        });

        PF_PWD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PF_PWDMouseClicked(evt);
            }
        });

        jLabel2.setText(UserMain.Txt("Server")); // NOI18N

        CB_SERVER.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_SERVERActionPerformed(evt);
            }
        });

        jLabel4.setText(UserMain.Txt("Mandant")); // NOI18N

        CB_MANDANT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_MANDANTActionPerformed(evt);
            }
        });

        BT_SSL.setText(" ");
        BT_SSL.setOpaque(false);
        BT_SSL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_SSLActionPerformed(evt);
            }
        });

        jLabel3.setText(UserMain.Txt("SSL")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LB_USER)
                    .addComponent(LB_PWD)
                    .addComponent(jLabel1)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BT_SSL)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 171, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BT_ABORT)
                            .addComponent(BT_OK, javax.swing.GroupLayout.Alignment.TRAILING)))
                    .addComponent(CB_MANDANT, 0, 265, Short.MAX_VALUE)
                    .addComponent(CB_SERVER, 0, 265, Short.MAX_VALUE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(PF_PWD, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(TXT_USER, javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(CB_USER, javax.swing.GroupLayout.Alignment.LEADING, 0, 164, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(BT_CHANGE_PWD)
                .addGap(232, 232, 232))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ABORT, BT_OK});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(CB_SERVER, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(CB_MANDANT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(CB_USER, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_USER, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LB_USER))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PF_PWD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LB_PWD))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_SSL)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(BT_CHANGE_PWD)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BT_ABORT)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BT_OK)))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void CB_USERActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_USERActionPerformed
    {//GEN-HEADEREND:event_CB_USERActionPerformed
        // TODO add your handling code here:
        if (CB_USER.getSelectedIndex() == 0) // ADMIN
        {
            BT_CHANGE_PWD.setVisible(true);
            TXT_USER.setVisible(true);
            LB_USER.setVisible(true);
             CB_MANDANT.setVisible(true);
             TXT_USER.requestFocus();
        }
        if (CB_USER.getSelectedIndex() == 1) // MULTIADMIN
        {
            BT_CHANGE_PWD.setVisible(true);
            TXT_USER.setVisible(true);
            LB_USER.setVisible(true);
            CB_MANDANT.setVisible(true);
            TXT_USER.requestFocus();
        }
        if (CB_USER.getSelectedIndex() == 2) // SYSADMIN
        {
            CB_MANDANT.setVisible(false);
            BT_CHANGE_PWD.setVisible(false);
            TXT_USER.setVisible(true);
            LB_USER.setVisible(true);
            TXT_USER.requestFocus();
        }
            
}//GEN-LAST:event_CB_USERActionPerformed

    private void BT_OKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKActionPerformed
    {//GEN-HEADEREND:event_BT_OKActionPerformed
        // TODO add your handling code here:
        StationEntry st = ((MailSecurerComboModel)CB_SERVER.getModel()).get_act_station();
        if (st == null)
        {
                        UserMain.errm_ok(UserMain.getString("Please_select_Server_and_a_company_first"));
                        return;
        }
        String pwd = new String(PF_PWD.getPassword());
        if (pwd.length() == 0)
        {
            UserMain.errm_ok(UserMain.getString("Bitte_geben_Sie_ein_Passwort_ein"));
            return;
        }
               
        if (sw != null)
            return;

        UserMain.self.show_busy(my_dlg, UserMain.Txt("Connecting") + "...");

        sw = new SwingWorker() 
        {

            @Override
            public Object construct()
            {
                
                boolean logged_in = do_login();
                
                if (logged_in)
                {
                    setVisible(false);
                    boolean dirty = false;

                    if (BT_SSL.isSelected() != Main.get_bool_prop(Preferences.SERVER_SSL, true))
                    {
                        dirty = true;
                    }
                    if (dirty)
                    {
                        Main.get_prefs().set_prop(Preferences.SERVER_SSL, BT_SSL.isSelected() ? "1" : "0");                
                        Main.get_prefs().store_props();
                    }
                }
                if (login_retries >= 8)
                {
                    setVisible(false);                   
                }
                sw = null;
                UserMain.self.hide_busy();
                return null;
            }
        } ;
        sw.start();

                   
    }//GEN-LAST:event_BT_OKActionPerformed

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ABORTActionPerformed
    {//GEN-HEADEREND:event_BT_ABORTActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);   
    }//GEN-LAST:event_BT_ABORTActionPerformed

    private void BT_CHANGE_PWDActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_CHANGE_PWDActionPerformed
    {//GEN-HEADEREND:event_BT_CHANGE_PWDActionPerformed
        // TODO add your handling code here:
        if (!do_login())
            return;
        CheckPwdPanel pnl = new CheckPwdPanel(main, /*strong*/false);
        GenericGlossyDlg dlg = new GenericGlossyDlg(null, true, pnl);
        dlg.setSize(500, 200);

        dlg.setLocation(this.getLocationOnScreen().x + 200, this.getLocationOnScreen().y + 50);
        dlg.setTitle(UserMain.getString("Passwort_ändern"));
        dlg.setVisible(true);   
            
        String pwd = new String(PF_PWD.getPassword());

        if (!pnl.isOkay())
            return;

        String new_pwd = pnl.get_pwd();

        if (new_pwd.length() == 0)
        {
            UserMain.errm_ok(UserMain.getString("Das_neue_Benutzerpasswort_konnte_nicht_gespeichert_werden"));     
            return;
        }
        
        SQLConnect sql = UserMain.sqc();
        if ( main.getUserLevel() == USERMODE.UL_ADMIN )
        {    
            
            String qry;
            
            int id = sql.get_act_mandant_id();
            
            // b_user USER
            qry = "update mandant set password='" + new_pwd + "' where id=" + id + " and password='" + pwd + "'";
            
            if (sql.sql_lazy_update( qry) != 1)
            {
                UserMain.errm_ok(UserMain.getString("Das_neue_Benutzerpasswort_konnte_nicht_gespeichert_werden"));                
            }
            else
            {
                UserMain.info_ok(UserMain.getString("Das_neue_Benutzerpasswort_wurde_gespeichert"));                
            }
        }        
    }//GEN-LAST:event_BT_CHANGE_PWDActionPerformed

    private void TXT_USERMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXT_USERMouseClicked
    {//GEN-HEADEREND:event_TXT_USERMouseClicked
        // TODO add your handling code here:
        if (UserMain.self.is_touchscreen())
        {
            UserMain.self.show_vkeyboard( this.my_dlg, TXT_USER, false);
        }
        
    }//GEN-LAST:event_TXT_USERMouseClicked

    private void PF_PWDMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_PF_PWDMouseClicked
    {//GEN-HEADEREND:event_PF_PWDMouseClicked
        // TODO add your handling code here:
        if (UserMain.self.is_touchscreen())
        {
            UserMain.self.show_vkeyboard( this.my_dlg, PF_PWD, false);
        }

    }//GEN-LAST:event_PF_PWDMouseClicked

    private void CB_SERVERActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_SERVERActionPerformed
    {//GEN-HEADEREND:event_CB_SERVERActionPerformed
        // TODO add your handling code here:
        rebuild_mandant_list();
        TXT_USER.requestFocus();
        
    }//GEN-LAST:event_CB_SERVERActionPerformed

    private void CB_MANDANTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_MANDANTActionPerformed
    {//GEN-HEADEREND:event_CB_MANDANTActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_CB_MANDANTActionPerformed

    private void BT_SSLActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_SSLActionPerformed
    {//GEN-HEADEREND:event_BT_SSLActionPerformed
        // TODO add your handling code here:
        rebuild_mandant_list();
    }//GEN-LAST:event_BT_SSLActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_CHANGE_PWD;
    private javax.swing.JButton BT_OK;
    private javax.swing.JCheckBox BT_SSL;
    private javax.swing.JComboBox CB_MANDANT;
    private javax.swing.JComboBox CB_SERVER;
    private javax.swing.JComboBox CB_USER;
    private javax.swing.JLabel LB_PWD;
    private javax.swing.JLabel LB_USER;
    private javax.swing.JPasswordField PF_PWD;
    private javax.swing.JTextField TXT_USER;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    // End of variables declaration//GEN-END:variables

    @Override
    public JButton get_default_button()
    {
        return BT_OK;
    }
    
 
    
    boolean try_admin_login( int m_id, String nname, String pwd )
    {
        
        FunctionCallConnect fcc = UserMain.fcc();

        String ret = fcc.call_abstract_function("auth_user CMD:admin MA:" + m_id + " NM:'" + nname + "' PW:'" + pwd + "'", FunctionCallConnect.MEDIUM_TIMEOUT );

        if (ret == null)
        {
            UserMain.errm_ok(UserMain.getString("Die_Authentifizierung_ist_fehlgeschlagen"));
            return false;
        }
        int idx = ret.indexOf(':');
        if (idx == -1 || idx == ret.length() - 1)
        {
            UserMain.errm_ok(UserMain.getString("Fehler_beim_Authentifizieren: ") + ret);
            return false;
        }
        int code = Integer.parseInt(ret.substring(0, idx) );

        if (code != 0)
        {
            UserMain.errm_ok(UserMain.getString("Die_Authentifizierung_war_nicht_erfolgreich") + ret.substring(idx) );
            return false;
        }
        ParseToken pt = new ParseToken(ret);

        String sso_token = pt.GetString("SSO:");
        UserSSOEntry sso_entry = null;
        ret = fcc.call_abstract_function("auth_user CMD:getsso SSO:" + sso_token, FunctionCallConnect.MEDIUM_TIMEOUT );
        if (ret != null && ret.charAt(0) == '0')
        {
            pt = new ParseToken( ret.substring(3) );
            Object o = pt.GetCompressedObject("CSSO:");
            if (o instanceof UserSSOEntry)
            {
                sso_entry = (UserSSOEntry)o;
            }
        }


        main.setUserLevel( USERMODE.UL_ADMIN );

        Mandant ma = UserMain.sqc().get_mandant(m_id);
        main.set_titel( ma.getName() + " <" + nname + "> (" + UserMain.Txt("Admin") + ")" );

        main.set_act_userdata( nname, pwd, null, sso_token, sso_entry );

        UserMain.sqc().set_mandant_id(m_id);

        
        SwingUtilities.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                main.switch_to_panel(UserMain.PBC_ADMIN);
            }
        });


        return true;
    }
    boolean try_user_login( int m_id, String nname, String pwd )
    {
        FunctionCallConnect fcc = UserMain.fcc();

        String ret = fcc.call_abstract_function("auth_user CMD:login MA:" + m_id + " NM:'" + nname + "' PW:'" + pwd + "'", FunctionCallConnect.MEDIUM_TIMEOUT );

        if (ret == null)
        {
            UserMain.errm_ok(UserMain.getString("Die_Authentifizierung_ist_fehlgeschlagen"));
            return false;
        }
        int idx = ret.indexOf(':');
        if (idx == -1 || idx == ret.length() - 1)
        {
            UserMain.errm_ok(UserMain.getString("Fehler_beim_Authentifizieren: ") + ret);
            return false;
        }
        int code = Integer.parseInt(ret.substring(0, idx) );

        if (code != 0)
        {
            UserMain.errm_ok(UserMain.getString("Die_Authentifizierung_war_nicht_erfolgreich: ") + ret.substring(idx) );
            return false;
        }
        ParseToken pt = new ParseToken(ret);
        String mail_list = pt.GetString("MA:");
        String[] mail_array = mail_list.split(",");
        ArrayList<String> mail_aliases = new ArrayList<String>();
        mail_aliases.addAll(Arrays.asList(mail_array));

        String sso_token = pt.GetString("SSO:");
        UserSSOEntry sso_entry = null;
        ret = fcc.call_abstract_function("auth_user CMD:getsso SSO:" + sso_token, FunctionCallConnect.MEDIUM_TIMEOUT );
        if (ret != null && ret.charAt(0) == '0')
        {
            pt = new ParseToken( ret.substring(3) );
            sso_entry = pt.GetObject("CSSO:", UserSSOEntry.class);
        }
        Mandant ma = UserMain.sqc().get_mandant(m_id);
        boolean is_4_eyes = sso_entry.role_has_option(OptCBEntry._4EYES);

        if (sso_entry != null && sso_entry.is_admin())
        {
            main.setUserLevel( USERMODE.UL_ADMIN );
            main.set_titel( ma.getName() + " <" + nname + "> (" + UserMain.Txt("Admin") + ")"  + (is_4_eyes ? " " + UserMain.Txt("4_Eyes") : ""));
        }
        else
        {
            main.setUserLevel( USERMODE.UL_USER );
            main.set_titel( ma.getName() + " <" + nname + "> (" + UserMain.Txt("User") + ")"   + (is_4_eyes ? " " + UserMain.Txt("4_Eyes") : ""));
        }
        main.set_act_userdata( nname, pwd, mail_aliases, sso_token, sso_entry );

        SQLConnect sql = UserMain.sqc();
        sql.set_mandant_id(m_id);

        SwingUtilities.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                if (main.getUserLevel() == USERMODE.UL_ADMIN )
                    main.switch_to_panel(UserMain.PBC_ADMIN);
                else
                    main.switch_to_panel(UserMain.PBC_SEARCH);
            }
        });
        return true;
    }

    boolean try_sysadmin_login( String nname, String pwd )
    {
        FunctionCallConnect fcc = UserMain.fcc();

        String ret = fcc.call_abstract_function("auth_user CMD:sysadmin NM:'" + nname + "' PW:'" + pwd + "'", FunctionCallConnect.MEDIUM_TIMEOUT );

        if (ret == null)
        {
            UserMain.errm_ok(UserMain.getString("Die_Authentifizierung_ist_fehlgeschlagen"));
            return false;
        }
        int idx = ret.indexOf(':');
        if (idx == -1)
        {
            UserMain.errm_ok(UserMain.getString("Fehler_beim_Authentifizieren: ") + ret);
            return false;
        }
        int code = Integer.parseInt(ret.substring(0, idx) );

        if (code != 0)
        {
            UserMain.errm_ok(UserMain.getString("Die_Authentifizierung_war_nicht_erfolgreich: ") + ret.substring(idx) );
            return false;
        }

        main.setUserLevel( USERMODE.UL_SYSADMIN );
        main.set_titel( "<" + nname + "> (" + UserMain.Txt("SysAdmin") + ")" );

        SwingUtilities.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                main.switch_to_panel(UserMain.PBC_SYSTEM);
            }
        });

        return true;
    }

    
    ArrayList<StationEntry> build_st_list()
    {
        ArrayList<StationEntry> st_list = new ArrayList<StationEntry>();

        String default_station = Main.get_prop(Preferences.DEFAULT_STATION, gui_default_station);
        if (default_station != null && default_station.length() > 0)
        {
            try
            {
                ParseToken pt = new ParseToken(default_station);
                //String version = pt.GetString("VER:");
                //long station = pt.GetLongValue("STATION:");
                long station = 1;
                String version = "";
                String name = pt.GetString("NAME:");
                String ip = pt.GetString("IP:");
                int port = (int) pt.GetLongValue("PO:");
                boolean only_this = pt.GetBoolean("OT:");

                StationEntry st = new StationEntry(name, station, version, ip, port);
                st_list.add(st);
                
                // NO SCANNING
                if (only_this)
                {
                    CB_SERVER.setEnabled(false);
                    
                    UserMain.self.hide_busy();
                    return st_list;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
                UserMain.errm_ok(my_dlg, UserMain.Txt("Error_parsing_station_entry_from_preferences"));
            }
        }


        try
        {
            UDP_Communicator comm = new UDP_Communicator(this);

            String answer_remote = comm.udp_send("HELLO", /*scan*/ true, /*local*/null, /*retries*/ 2, null, 200);
            if (answer_remote == null)
                answer_remote = comm.udp_send("HELLO", /*scan*/ true, /*local*/null, /*retries*/ 2, null, 1000);

            if (answer_remote == null || answer_remote.length() == 0)
                answer_remote = comm.udp_send("HELLO", /*scan*/ true, /*local*/"localhost", /*retries*/ 2, null, 500);

            StringTokenizer str = new StringTokenizer(answer_remote, "\n");

            while (str.hasMoreElements())
            {
                String line = str.nextToken().trim();
                if (line.length() == 0)
                    continue;

                try
                {
                    ParseToken pt = new ParseToken(line);
                    String version = pt.GetString("VER:");
                    long station = pt.GetLongValue("STATION:");
                    String name = pt.GetString("NAME:");
                    String ip = pt.GetString("IP:");
                    int port = (int) pt.GetLongValue("PO:");

                    StationEntry st = new StationEntry(name, station, version, ip, port);

                    boolean found = false;
                    for (int i = 0; i < st_list.size(); i++)
                    {
                        StationEntry stationEntry = st_list.get(i);
                        if (stationEntry.toString().compareTo( st.toString() ) == 0)
                        {
                            found = true;
                            break;
                        }
                    }
                    if (!found)
                        st_list.add(st);
                }
                catch (Exception e)
                {
                    System.out.println("Error reading Station entry " + line + ": " +  e.getMessage());
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        UserMain.self.hide_busy();

        if (st_list.isEmpty())
        {
            UserMain.errm_ok(my_dlg, UserMain.Txt("No_Stations_could_be_found"));
        }
        return st_list;

    }

    ArrayList<Mandant> build_mandant_list(StationEntry st)
    {
        ArrayList<Mandant> ma_list = new ArrayList<Mandant>();

        int port = st.get_port();
        if (port <= 0)
            port = Main.get_port();


        SQLConnect sqc = new SQLConnect(st.get_ip(), port, BT_SSL.isSelected() );


        String default_station = Main.get_prop(Preferences.DEFAULT_STATION, gui_default_station);
        ParseToken pt = new ParseToken(default_station);
        long fix_mandant = pt.GetLongValue("FM:");
        String fix_mandant_name = pt.GetString("FN:");



        SQLResult<Mandant> ma_res = sqc.init_mandant_list();
        if (ma_res != null)
        {
            for (int i = 0; i < ma_res.size(); i++)
            {
                Mandant mandant = ma_res.get(i);

                // CHECK IF WE WANT FIX ID?
                if (fix_mandant > 0 && mandant.getId() != fix_mandant)
                    continue;

                // CHECK IF WE WANT FIX OR NAME?
                if (fix_mandant_name != null && fix_mandant_name.length() > 0)
                {
                    if (fix_mandant_name.compareTo(mandant.getName()) != 0)
                        continue;
                }

                ma_list.add(mandant);

                // IF WE HAVE FOUND AT LEAST ONE MANDANT WE CAN LOCK COMBO
                if (fix_mandant > 0 || (fix_mandant_name != null && fix_mandant_name.length() > 0))
                {
                    CB_MANDANT.setEnabled(false);
                }
            }
        }
        return ma_list;
    }



    @Override
    public StationEntry get_selected_box()
    {
        MailSecurerComboModel model = (MailSecurerComboModel)CB_SERVER.getModel();
        if (model.st_list.size() > 0)
            return model.st_list.get(model.act_st_idx);
        return null;
    }

    @Override
    public void set_status( String st )
    {
        //UserMain.self.show_busy(my_dlg, st);
    }

    @Override
    public boolean do_scan_local()
    {
        return false;
    }

   

   
    
    
}
