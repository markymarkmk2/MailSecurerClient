/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GetMailAddressPanel.java
 *
 * Created on 13.11.2009, 13:21:07
 */

package dimm.home.Panels;

import dimm.home.Main;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.Rendering.GlossTable;
import dimm.home.ServerConnect.ServerCall;
import dimm.home.UserMain;
import home.shared.Utilities.SizeStr;
import home.shared.Utilities.ParseToken;
import home.shared.hibernate.DiskArchive;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.Timer;



/**
 *
 * @author mw
 */
public class ReIndexPanel extends GlossDialogPanel implements ActionListener
{

    private boolean okay = false;
    GlossTable table;
    int da_idx;
    int ds_idx;
    Timer timer;
    
    ImageIcon ok_icn;
    ImageIcon nok_icn;
    ImageIcon empty_icn;

    enum STATUS
    {
        NOT_STARTED,
        ACTIVE,
        PAUSED,
        ABORTED,
        FINISHED
    }

    STATUS status;
    STATUS last_status;

    String last_status_ret = "";
    int last_da = -1;


    /** Creates new form GetMailAddressPanel */
    public ReIndexPanel( int da_idx, int ds_idx)
    {
        initComponents();

        if (Main.ui.has_rendered_panels())
        {
            PB_PERCENT.setBackground(Color.BLACK);
            PB_PERCENT.setForeground( new Color(0,51,153));
        }

        String  icn_ok = "/dimm/home/images/web_check.png";
        String  icn_empty = "/dimm/home/images/ok_empty.png";
        String  icn_warn = "/dimm/home/images/web_delete.png";

        ok_icn = new ImageIcon(this.getClass().getResource(icn_ok));
        nok_icn = new ImageIcon(this.getClass().getResource(icn_warn));
        empty_icn = new ImageIcon(this.getClass().getResource(icn_empty));

        status = STATUS.NOT_STARTED;
        last_status = status;


        this.da_idx = da_idx;
        this.ds_idx = ds_idx;

        read_status();
        

        timer = new Timer(500, this);
        timer.start();
    }
    public ReIndexPanel( int da_idx)
    {
        this( da_idx, -1 );
    }

    void set_icon( JButton btn, ImageIcon icn )
    {
        btn.setIcon(icn);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setBorderPainted(false);
    }

    void read_status()
    {
         String cmd = "reindex CMD:check MA:" + UserMain.self.get_act_mandant().getId();

         String ret = UserMain.fcc().call_abstract_function(cmd, ServerCall.SHORT_CMD_TO);
         if (ret == null)
             return;

         if (ret.compareTo(last_status_ret) == 0)
             return;

         last_status_ret = ret;
         
         if (ret.charAt(0) == '0')
         {
             String line = ret.substring(3);
             ParseToken pt = new ParseToken(line);

             if (pt.GetBoolean("AB:"))
             {
                 BT_STARTSTOP.setText(UserMain.Txt("Start"));
                 status = STATUS.ABORTED;
             }
             else if (pt.GetBoolean("PA:"))
             {
                 BT_STARTSTOP.setText(UserMain.Txt("Resume"));
                 status = STATUS.PAUSED;
             }
             else if (pt.GetBoolean("BS:"))
             {
                 BT_STARTSTOP.setText(UserMain.Txt("Pause"));
                 status = STATUS.ACTIVE;
             }
             else
             {
                 if ((int)pt.GetLongValue("PC:") >= 99)
                 {
                     status = STATUS.FINISHED;
                     BT_STARTSTOP.setText(UserMain.Txt("Start"));
                 }
             }

//             if (status != last_status)
             {
                 set_icon( BT_ACTIVE, pt.GetBoolean("BS:") ? ok_icn : empty_icn);
                 set_icon( BT_RABORT, pt.GetBoolean("AB:") ? nok_icn : empty_icn);
                 set_icon( BT_RPAUSE, pt.GetBoolean("PA:") ? ok_icn : empty_icn);
                 last_status = status;
             }
             if (status != STATUS.NOT_STARTED)
             {

                 PB_PERCENT.setValue( (int)pt.GetLongValue("PC:"));

                 TXT_TCNT.setText( pt.GetString("TCNT:"));
                 TXT_ACNT.setText( pt.GetString("ACNT:"));
                 TXT_TRCNT.setText( pt.GetString("TRCNT:"));
                 TXT_ARCNT.setText( pt.GetString("ARCNT:"));


                 TXT_TSIZ.setText( SizeStr.format( pt.GetString("TSIZ:")) );
                 TXT_ASIZ.setText( SizeStr.format( pt.GetString("ASIZ:")) );
                 TXT_TRSIZ.setText( SizeStr.format( pt.GetString("TRSIZ:")) );
                 TXT_ARSIZ.setText( SizeStr.format( pt.GetString("ARSIZ:")) );

                 TXT_DS.setText( pt.GetString("RPA:"));

                 int act_da = (int)pt.GetLongValue("RDA:");
                 if (act_da != last_da)
                 {
                     last_da = act_da;
                     DiskArchive da = UserMain.sqc().get_disk_archive(act_da);
                     if (da != null)
                        TXT_DA.setText( da.getName() );
                 }


                 TXT_STATUS.setText( pt.GetString("MSG:"));
             }

         }
         else
         {
             TXT_STATUS.setText( ret);
         }
    }

    void handle_button()
    {
        String ret = null;
        
        if (status == STATUS.ACTIVE)
        {
            String cmd = "reindex CMD:pause MA:" + UserMain.self.get_act_mandant().getId();
            ret = UserMain.fcc().call_abstract_function(cmd, ServerCall.SHORT_CMD_TO);
            BT_STARTSTOP.setText(UserMain.Txt("Resume") );
        }
        if (status == STATUS.PAUSED)
        {
            String cmd = "reindex CMD:resume MA:" + UserMain.self.get_act_mandant().getId();
            ret = UserMain.fcc().call_abstract_function(cmd, ServerCall.SHORT_CMD_TO);
            BT_STARTSTOP.setText(UserMain.Txt("Pause") );
        }
        if (status == STATUS.NOT_STARTED || status == STATUS.ABORTED || status == STATUS.FINISHED )
        {
            status = STATUS.ACTIVE;
            BT_STARTSTOP.setText(UserMain.Txt("Pause"));
            String cmd = "reindex CMD:start MA:" + UserMain.self.get_act_mandant().getId() + " DA:" + da_idx;
            if (ds_idx == -1)
                cmd += " TY:one_da";
            else
                cmd += " DS:" + ds_idx + " TY:one_ds";
            ret = UserMain.fcc().call_abstract_function(cmd, ServerCall.SHORT_CMD_TO);
        }

       
        if (ret == null || ret.length() == 0 || ret.charAt(0) != '0')
        {
            UserMain.errm_ok(my_dlg, UserMain.Txt("Error_while_calling_command") + " " + ret);
        }
        
        my_dlg.pack();
    }

    void abort()
    {
        if (status != STATUS.ABORTED)
        {
            String cmd = "reindex CMD:abort MA:" + UserMain.self.get_act_mandant().getId();
            UserMain.fcc().call_abstract_function(cmd, ServerCall.SHORT_CMD_TO);
        }
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BT_OKAY = new GlossButton();
        jPanel1 = new javax.swing.JPanel();
        PB_PERCENT = new javax.swing.JProgressBar();
        TXT_DA = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        BT_STARTSTOP = new GlossButton();
        TXT_ACNT = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        TXT_ASIZ = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        TXT_ARCNT = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        TXT_ARSIZ = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        TXT_TCNT = new javax.swing.JTextField();
        TXT_TSIZ = new javax.swing.JTextField();
        TXT_TRCNT = new javax.swing.JTextField();
        TXT_TRSIZ = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        TXT_DS = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        TXT_STATUS = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        BT_ACTIVE = new javax.swing.JButton();
        BT_RABORT = new javax.swing.JButton();
        BT_RPAUSE = new javax.swing.JButton();
        BT_ABORT = new GlossButton();

        BT_OKAY.setText(UserMain.Txt("Close")); // NOI18N
        BT_OKAY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKAYActionPerformed(evt);
            }
        });

        PB_PERCENT.setStringPainted(true);

        TXT_DA.setEditable(false);
        TXT_DA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TXT_DAActionPerformed(evt);
            }
        });

        jLabel1.setText(UserMain.Txt("Dateien")); // NOI18N

        BT_STARTSTOP.setText(UserMain.Txt("Start")); // NOI18N
        BT_STARTSTOP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_STARTSTOPActionPerformed(evt);
            }
        });

        TXT_ACNT.setEditable(false);
        TXT_ACNT.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel2.setText(UserMain.Txt("In_Bearbeitung")); // NOI18N

        TXT_ASIZ.setEditable(false);
        TXT_ASIZ.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel3.setText(UserMain.Txt("Datenmenge")); // NOI18N

        TXT_ARCNT.setEditable(false);
        TXT_ARCNT.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel4.setText(UserMain.Txt("Dateien")); // NOI18N

        TXT_ARSIZ.setEditable(false);
        TXT_ARSIZ.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel5.setText(UserMain.Txt("Datenmenge")); // NOI18N

        jLabel6.setText(UserMain.Txt("Total")); // NOI18N

        TXT_TCNT.setEditable(false);
        TXT_TCNT.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        TXT_TSIZ.setEditable(false);
        TXT_TSIZ.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        TXT_TRCNT.setEditable(false);
        TXT_TRCNT.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        TXT_TRSIZ.setEditable(false);
        TXT_TRSIZ.setHorizontalAlignment(javax.swing.JTextField.RIGHT);

        jLabel7.setText(UserMain.Txt("Path")); // NOI18N

        TXT_DS.setEditable(false);

        jLabel8.setText(UserMain.Txt("Status")); // NOI18N

        TXT_STATUS.setEditable(false);

        jLabel9.setText(UserMain.Txt("Archiv")); // NOI18N

        BT_ACTIVE.setText(UserMain.getString("Active")); // NOI18N
        BT_ACTIVE.setBorder(null);
        BT_ACTIVE.setBorderPainted(false);

        BT_RABORT.setText(UserMain.getString("Abort")); // NOI18N
        BT_RABORT.setBorder(null);
        BT_RABORT.setBorderPainted(false);

        BT_RPAUSE.setText(UserMain.getString("Pause")); // NOI18N
        BT_RPAUSE.setBorder(null);
        BT_RPAUSE.setBorderPainted(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3)
                    .addComponent(jLabel7)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(BT_RABORT)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(TXT_ARSIZ, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                    .addComponent(TXT_ARCNT, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                    .addComponent(TXT_ASIZ, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)
                                    .addComponent(TXT_ACNT, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(TXT_TSIZ, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                                    .addComponent(TXT_TCNT, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                                    .addComponent(TXT_TRSIZ, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)
                                    .addComponent(TXT_TRCNT, javax.swing.GroupLayout.DEFAULT_SIZE, 175, Short.MAX_VALUE)))
                            .addComponent(TXT_DS, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                            .addComponent(TXT_STATUS, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE))
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(TXT_DA, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(PB_PERCENT, javax.swing.GroupLayout.PREFERRED_SIZE, 217, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BT_ACTIVE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(BT_RPAUSE)
                            .addComponent(BT_STARTSTOP, javax.swing.GroupLayout.DEFAULT_SIZE, 108, Short.MAX_VALUE))
                        .addContainerGap())))
        );

        jPanel1Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ACTIVE, BT_RABORT, BT_RPAUSE});

        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_DA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGap(19, 19, 19)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(TXT_ACNT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TXT_TCNT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(TXT_ASIZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TXT_TSIZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(TXT_DS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(TXT_ARCNT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(TXT_ARSIZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(TXT_TRCNT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TXT_TRSIZ, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_STATUS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_ACTIVE)
                    .addComponent(BT_RPAUSE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BT_RABORT)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(PB_PERCENT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_STARTSTOP))
                .addContainerGap())
        );

        BT_ABORT.setText(UserMain.Txt("Abort")); // NOI18N
        BT_ABORT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ABORTActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(BT_ABORT)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 287, Short.MAX_VALUE)
                        .addComponent(BT_OKAY))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ABORT, BT_OKAY});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OKAY)
                    .addComponent(BT_ABORT))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKAYActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKAYActionPerformed
    {//GEN-HEADEREND:event_BT_OKAYActionPerformed
        // TODO add your handling code here:
        timer.stop();
        setOkay(true);
        my_dlg.setVisible(false);

    }//GEN-LAST:event_BT_OKAYActionPerformed

    private void TXT_DAActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TXT_DAActionPerformed
    {//GEN-HEADEREND:event_TXT_DAActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TXT_DAActionPerformed

    private void BT_STARTSTOPActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_STARTSTOPActionPerformed
    {//GEN-HEADEREND:event_BT_STARTSTOPActionPerformed
        // TODO add your handling code here:
        handle_button();
    }//GEN-LAST:event_BT_STARTSTOPActionPerformed

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ABORTActionPerformed
    {//GEN-HEADEREND:event_BT_ABORTActionPerformed
        // TODO add your handling code here:
        abort();
    }//GEN-LAST:event_BT_ABORTActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_ACTIVE;
    private javax.swing.JButton BT_OKAY;
    private javax.swing.JButton BT_RABORT;
    private javax.swing.JButton BT_RPAUSE;
    private javax.swing.JButton BT_STARTSTOP;
    private javax.swing.JProgressBar PB_PERCENT;
    private javax.swing.JTextField TXT_ACNT;
    private javax.swing.JTextField TXT_ARCNT;
    private javax.swing.JTextField TXT_ARSIZ;
    private javax.swing.JTextField TXT_ASIZ;
    private javax.swing.JTextField TXT_DA;
    private javax.swing.JTextField TXT_DS;
    private javax.swing.JTextField TXT_STATUS;
    private javax.swing.JTextField TXT_TCNT;
    private javax.swing.JTextField TXT_TRCNT;
    private javax.swing.JTextField TXT_TRSIZ;
    private javax.swing.JTextField TXT_TSIZ;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
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

    @Override
    public void actionPerformed( ActionEvent e )
    {
        if (status == STATUS.NOT_STARTED)
            return;

        timer.stop();

        try
        {
            read_status();
        }
        catch (Exception exc)
        {
            Main.err_log_warn("Caught exception in read_starus: " + exc.getMessage());
            exc.printStackTrace();
        }
        timer.start();
    }

    @Override
    public void deactivate()
    {
        timer.stop();
    }

}
