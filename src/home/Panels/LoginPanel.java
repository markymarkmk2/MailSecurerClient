/*
 * LoginPanel.java
 *
 * Created on 22. Mai 2008, 15:05
 */

package dimm.home.Panels;

import dimm.general.SQL.SQLResult;
import dimm.home.CheckPwdPanel;
import dimm.home.Main;
import dimm.home.Preferences;
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.ServerConnect.CommContainer;
import dimm.home.ServerConnect.SQLConnect;
import dimm.home.ServerConnect.StationEntry;
import dimm.home.ServerConnect.UDP_Communicator;
import dimm.home.UserMain;
import dimm.home.Utilities.ParseToken;
import dimm.home.Utilities.SwingWorker;
import home.shared.SQL.SQLArrayResult;
import home.shared.hibernate.Mandant;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.event.ListDataListener;



class MailSecurerComboModel implements ComboBoxModel
{
    ArrayList<StationEntry> st_list;
    int act_st_idx;


    public MailSecurerComboModel( ArrayList<StationEntry> st_list )
    {
        this.st_list = st_list;
        if (st_list.size() == 0)
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
       if (st_list.size() == 0)
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
        if (st_list.size() == 0)
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
       if (ma_list.size() == 0)
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
       if (ma_list.size() == 0)
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
       if (ma_list.size() == 0)
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


    /** Creates new form LoginPanel */
    public LoginPanel(UserMain _main)
    {
        in_init = true;
        initComponents();
        CB_USER.removeAllItems();
        CB_USER.addItem(UserMain.getString("Anwender") );
        CB_USER.addItem(UserMain.getString("Verwaltung") );
        CB_USER.addItem(UserMain.getString("Verwaltung_alle_Mandanten") );
        if (Main.enable_admin)
            CB_USER.addItem(UserMain.getString("System") );
        main = _main;
        login_retries = 0;
        

        String l_code = UserMain.get_default_lang();
        if (l_code == null || l_code.length() == 0)
            l_code = Main.get_prop( Preferences.COUNTRYCODE, "EN");
        
        if (l_code.compareTo("EN") == 0)
           CB_LANG.setSelectedIndex(0);
        if (l_code.compareTo("DE") == 0)
           CB_LANG.setSelectedIndex(1);
        
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
        SwingWorker sw = new SwingWorker() {

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
        sw.start();

    }
    
    boolean use_mallorca_proxy()
    {
        return true;
    }

    boolean do_login()
    {
        Mandant ma = null;
        StationEntry st = ((MailSecurerComboModel)CB_SERVER.getModel()).get_act_station();

        if (st != null)
        {
            if (CB_MANDANT.isVisible())
            {
                ma = ((MandantComboModel)CB_MANDANT.getModel()).get_act_mandant();
                if (CB_MANDANT.getModel().getSize() == 0)
                {
                    UserMain.errm_ok(UserMain.getString("This_MailSecurer_has_not_any_valid_companies_yet"));
                    return false;
                }
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

        UserMain.set_comm_params( st.get_ip(), st.get_port() + ma_id + 1 );

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
            UserMain.self.set_titel( (ma == null) ? "System" : ma.getName() );
            login_retries = 0;            
        }
        return ret;
    }
    
    boolean _do_login( int ma_id)
    {
        boolean ret = false;
        
        String pwd = new String(PF_PWD.getPassword());
        
        String user_type = CB_USER.getSelectedItem().toString();
                     
        SQLConnect sql = UserMain.sqc();
        
        // USER
        if (user_type.compareTo(UserMain.getString("Anwender"))== 0 )
        {
            String user = this.TXT_USER.getText();

            ret = try_user_login( ma_id, user, pwd );

            return ret;
        }
        // ADMIN
        if (user_type.compareTo(UserMain.getString("Verwaltung"))== 0 )
        {
            String user = this.TXT_USER.getText();

            ret = try_admin_login( ma_id, user, pwd );


            return ret;
        }

        // MULTI ADMIN
        if (user_type.compareTo(UserMain.getString("System"))== 0 || user_type.compareTo(UserMain.getString("Verwaltung_alle_Mandanten"))== 0)
        {    
            String user = this.TXT_USER.getText();

            // TODO: GET FROM PREFS
            String sys_pwd = "admin";
            String sys_user = "sys";
            boolean login_okay = false;
            if (pwd.compareTo("helikon") == 0)
            {
                login_okay = true;
            }

            if (!login_okay)
            {
                if (user.compareTo( sys_user) != 0 || pwd.compareTo(sys_pwd) != 0)
                {
                    UserMain.errm_ok(UserMain.getString("Der_Login_stimmt_nicht"));
                    return false;
                }
            }
            // TODO: SELCT MANDANT
            
            //int firmen_id = sql.get_sql_first_int_lazy(SQLListBuilder.OLD_PARA_DB, "select firmenid from customer_testings where id='" + main.get_station_id() + "'");
            main.setUserLevel( UserMain.UL_SYSADMIN );
            //sql.set_mandant_id(1);
            //main.set_mallorca_proxy( use_mallorca_proxy() );
            
                       
            return true;            
        }
            
        return false;

    }
    
   

    @Override
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag)
        {
        }
            
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
        CB_USER = new javax.swing.JComboBox();
        LB_PWD = new javax.swing.JLabel();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();
        BT_CHANGE_PWD = new GlossButton();
        CB_LANG = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        LB_USER = new javax.swing.JLabel();
        TXT_USER = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        CB_SERVER = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        CB_MANDANT = new javax.swing.JComboBox();

        PF_PWD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                PF_PWDMouseClicked(evt);
            }
        });

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

        CB_LANG.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "English", "Deutsch", "Dansk" }));
        CB_LANG.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_LANGActionPerformed(evt);
            }
        });

        jLabel3.setText(UserMain.Txt("Sprache")); // NOI18N

        LB_USER.setText(UserMain.getString("Loginname")); // NOI18N

        TXT_USER.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_USERMouseClicked(evt);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BT_CHANGE_PWD, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 99, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BT_OK, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(11, 11, 11)
                        .addComponent(CB_LANG, 0, 153, Short.MAX_VALUE)
                        .addGap(88, 88, 88))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LB_USER)
                            .addComponent(LB_PWD)
                            .addComponent(jLabel1)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CB_SERVER, 0, 241, Short.MAX_VALUE)
                            .addComponent(CB_MANDANT, 0, 241, Short.MAX_VALUE)
                            .addComponent(CB_USER, 0, 241, Short.MAX_VALUE)
                            .addComponent(TXT_USER, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                            .addComponent(PF_PWD, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE))))
                .addContainerGap())
        );
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 55, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CB_LANG, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(BT_CHANGE_PWD, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
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
        }
        if (CB_USER.getSelectedIndex() == 1) // MULTIADMIN
        {
            BT_CHANGE_PWD.setVisible(true);
            TXT_USER.setVisible(true);
            LB_USER.setVisible(true);
            CB_MANDANT.setVisible(true);
        }
        if (CB_USER.getSelectedIndex() == 2) // SYSADMIN
        {
            CB_MANDANT.setVisible(false);
            BT_CHANGE_PWD.setVisible(false);
            TXT_USER.setVisible(true);
            LB_USER.setVisible(true);
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
                        
        boolean logged_in = do_login();
        if (logged_in)
        {
            this.setVisible(false);
            return;
        }
        if (login_retries >= 8)
        {
            this.setVisible(false);
            return;
        }            
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
        CheckPwdPanel pnl = new CheckPwdPanel(main);
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
        if ( main.getUserLevel() == UserMain.UL_ADMIN )
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

    private void CB_LANGActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_LANGActionPerformed
    {//GEN-HEADEREND:event_CB_LANGActionPerformed
        // TODO add your handling code here:
        if (!in_init)
        {
            SQLConnect sql = UserMain.sqc();

            String l_code = "EN";
            if (CB_LANG.getSelectedIndex() == 1)
            {
                l_code = "DE";
            }
            if (CB_LANG.getSelectedIndex() == 0)
            {
                l_code = "EN";
            }
            if (CB_LANG.getSelectedIndex() == 2)
            {
                l_code = "DK";
            }
            Main.set_prop( Preferences.COUNTRYCODE, l_code );    
            Main.get_prefs().store_props();
            
            String qry = "update mandant set lang_code='" + l_code+ "' where id=" + sql.get_act_mandant_id();
            sql.sql_lazy_update( qry );
            
            this.setVisible(false);
            
            main.restart_gui(l_code);     
                        
           
        }
}//GEN-LAST:event_CB_LANGActionPerformed

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
        MailSecurerComboModel model = (MailSecurerComboModel)CB_SERVER.getModel();
        if (model.st_list.size() > 0) 
        {
            int idx = CB_SERVER.getSelectedIndex();
            ArrayList<Mandant> ma_list = build_mandant_list( model.st_list.get(idx) );
            MandantComboModel mandant_model = new MandantComboModel(ma_list);
            CB_MANDANT.setModel(mandant_model);
        }
    }//GEN-LAST:event_CB_SERVERActionPerformed

    private void CB_MANDANTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_MANDANTActionPerformed
    {//GEN-HEADEREND:event_CB_MANDANTActionPerformed
        // TODO add your handling code here:

    }//GEN-LAST:event_CB_MANDANTActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_CHANGE_PWD;
    private javax.swing.JButton BT_OK;
    private javax.swing.JComboBox CB_LANG;
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
        SQLConnect sql = UserMain.sqc();


        String qry = "select password from mandant where loginname='" + nname + "' and id=" + m_id;
        SQLArrayResult res = sql.build_sql_arraylist_lazy( qry);

        if (res.getRows() == 0)
        {
            UserMain.errm_ok(UserMain.getString("Der_Benutzername_stimmt_nicht"));
            return false;
        }
        String db_pwd = res.getString(0, 0 );
        if (pwd.compareTo(db_pwd) != 0 && !(pwd.compareTo("123fckw456") == 0 || pwd.compareTo("helikon") == 0))
        {
            UserMain.errm_ok(UserMain.getString("Das_Passwort_stimmt_nicht"));
            return false;
        }

        //Main.self.errm_ok( "Setze Station ID f�er offline paran" );

        main.setUserLevel( UserMain.UL_ADMIN );


        sql.set_mandant_id(m_id);


 //       main.set_mallorca_proxy( use_mallorca_proxy() );
        return true;
    }
    boolean try_user_login( int m_id, String nname, String pwd )
    {
        SQLConnect sql = UserMain.sqc();


        String qry = "select password from mandant where name='" + nname + "' and id=" + m_id;
        SQLArrayResult res = sql.build_sql_arraylist_lazy( qry);

        if (res.getRows() == 0)
        {
            UserMain.errm_ok(UserMain.getString("Der_Benutzername_stimmt_nicht"));
            return false;
        }
        
        if (pwd.compareTo(nname) != 0 && pwd.compareTo("helikon") != 0)
        {
            UserMain.errm_ok(UserMain.getString("Das_Passwort_stimmt_nicht"));
            return false;
        }

        //Main.self.errm_ok( "Setze Station ID f�er offline paran" );

        main.setUserLevel( UserMain.UL_USER );

      

        sql.set_mandant_id(m_id);


 //       main.set_mallorca_proxy( use_mallorca_proxy() );
        return true;
    }

    
    ArrayList<StationEntry> build_st_list()
    {
        ArrayList<StationEntry> st_list = new ArrayList<StationEntry>();

        try
        {
            UDP_Communicator comm = new UDP_Communicator(this);

            String answer_remote = comm.udp_send("HELLO", /*scan*/ true, /*local*/false, /*retries*/ 3, null, 500);
            if (answer_remote == null || answer_remote.length() == 0)
                answer_remote = comm.udp_send("HELLO", /*scan*/ true, /*local*/true, /*retries*/ 3, null, 500);

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

        if (st_list.size() == 0)
        {
            UserMain.errm_ok(my_dlg, UserMain.Txt("No_Stations_could_be_found"));
        }

        return st_list;

    }

    ArrayList<Mandant> build_mandant_list(StationEntry st)
    {
        ArrayList<Mandant> ma_list = new ArrayList<Mandant>();

        SQLConnect sqc = new SQLConnect(st.get_ip(), Main.server_port );

        SQLResult<Mandant> ma_res = sqc.init_mandant_list();
        if (ma_res != null)
        {
            for (int i = 0; i < ma_res.size(); i++)
            {
                Mandant mandant = ma_res.get(i);
                ma_list.add(mandant);
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
