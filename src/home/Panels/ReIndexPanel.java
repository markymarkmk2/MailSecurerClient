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

import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.Rendering.GlossTable;
import dimm.home.ServerConnect.ServerCall;
import dimm.home.UserMain;
import dimm.home.Utilities.ParseToken;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    /** Creates new form GetMailAddressPanel */
    public ReIndexPanel( int da_idx, int ds_idx)
    {
        initComponents();
        this.da_idx = da_idx;
        this.ds_idx = ds_idx;
        timer = new Timer(5000, this);
        timer.start();

    }
    public ReIndexPanel( int da_idx)
    {
        this( da_idx, -1 );
    }
/*
 * StringBuffer sb = new StringBuffer();
        sb.append( "BS:");
        sb.append( isBusy() ? "1":"0");
        sb.append( " PA:");
        sb.append( pause ? "1":"0");
        sb.append( " AB:");
        sb.append( abort ? "1":"0");
        sb.append( " TCNT:" );
        sb.append( getTotal_cnt() );
        sb.append( " TSIZ:" );
        sb.append( getTotal_size() );
        sb.append( " ACNT:" );
        sb.append( getAct_cnt() );
        sb.append( " ASIZ:" );
        sb.append( getAct_size() );
        if (act_re_idx >= 0)
        {
            ReIndexDSHEntry reIndexDSHEntry = reindex_list.get(act_re_idx);
            sb.append( " RPA:\"" );
            sb.append( reIndexDSHEntry.getData_dsh().getDs().getPath() );
            sb.append( "\"");
            sb.append( " RDS:" );
            sb.append( reIndexDSHEntry.getData_dsh().getDs().getId() );
            sb.append( " TRCNT:" );
            sb.append( getTotal_cnt() );
            sb.append( " TRSIZ:" );
            sb.append( getTotal_size() );
            sb.append( " ARCNT:" );
            sb.append( getAct_cnt() );
            sb.append( " ARSIZ:" );
            sb.append( getAct_size() );
        }
        sb.append( " MSG:\"" );
        sb.append( getLast_msg() );
        sb.append( "\"");*/
    String last_status_ret = "";
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

             TBT_ACTIVE.setSelected( pt.GetBoolean("BS:"));
             TBT_ABORT.setSelected( pt.GetBoolean("AB:"));
             TBT_PAUSE.setSelected( pt.GetBoolean("PA:"));
             PB_PERCENT.setValue( (int)pt.GetLongValue("PC:"));
             
             TXT_TCNT.setText( pt.GetString("TCNT:"));
             TXT_ACNT.setText( pt.GetString("ACNT:"));
             TXT_TRCNT.setText( pt.GetString("TRCNT:"));
             TXT_ARCNT.setText( pt.GetString("ARCNT:"));

             TXT_TSIZ.setText( pt.GetString("TSIZ:"));
             TXT_ASIZ.setText( pt.GetString("ASIZ:"));
             TXT_TRSIZ.setText( pt.GetString("TRSIZ:"));
             TXT_ARSIZ.setText( pt.GetString("ARSIZ:"));

             TXT_DS.setText( pt.GetString("RPA:"));

             
             TXT_STATUS.setText( pt.GetString("MSG:"));

             if (!TBT_ABORT.isSelected() && TBT_ACTIVE.isSelected())
             {
                 if ( TBT_PAUSE.isSelected())
                 {
                     BT_STARTSTOP.setText(UserMain.Txt("Resume"));
                 }
                 else
                 {
                     BT_STARTSTOP.setText(UserMain.Txt("Pause"));
                 }
             }
         }
         else
         {
             TXT_STATUS.setText( ret);
         }
    }
    void start()
    {
        String ret = null;
        
        if (TBT_ACTIVE.isSelected())
        {
            if (TBT_ABORT.isSelected())
            {
                return;
            }
            if (TBT_PAUSE.isSelected())
            {
                String cmd = "reindex CMD:resume MA:" + UserMain.self.get_act_mandant().getId();
                ret = UserMain.fcc().call_abstract_function(cmd, ServerCall.SHORT_CMD_TO);
            }
            else
            {
                String cmd = "reindex CMD:pause MA:" + UserMain.self.get_act_mandant().getId();
                ret = UserMain.fcc().call_abstract_function(cmd, ServerCall.SHORT_CMD_TO);
            }
        }
        else
        {
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
    }
    void abort()
    {
        if (TBT_ACTIVE.isSelected())
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
        TBT_ACTIVE = new javax.swing.JToggleButton();
        TBT_ABORT = new javax.swing.JToggleButton();
        jLabel9 = new javax.swing.JLabel();
        TBT_PAUSE = new javax.swing.JToggleButton();
        BT_ABORT = new GlossButton();

        BT_OKAY.setText(UserMain.Txt("Close")); // NOI18N
        BT_OKAY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKAYActionPerformed(evt);
            }
        });

        PB_PERCENT.setStringPainted(true);

        TXT_DA.setEditable(false);
        TXT_DA.setText("jTextField1");
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
        TXT_DS.setText("jTextField1");

        jLabel8.setText(UserMain.Txt("Status")); // NOI18N

        TXT_STATUS.setEditable(false);
        TXT_STATUS.setText("jTextField1");

        TBT_ACTIVE.setText(UserMain.Txt("Active")); // NOI18N
        TBT_ACTIVE.setBorder(null);
        TBT_ACTIVE.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        TBT_ACTIVE.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        TBT_ACTIVE.setIconTextGap(10);
        TBT_ACTIVE.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/ok_green.png"))); // NOI18N

        TBT_ABORT.setText(UserMain.Txt("Abort")); // NOI18N
        TBT_ABORT.setBorder(null);
        TBT_ABORT.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        TBT_ABORT.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        TBT_ABORT.setIconTextGap(10);
        TBT_ABORT.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/ok.png"))); // NOI18N

        jLabel9.setText(UserMain.Txt("Archiv")); // NOI18N

        TBT_PAUSE.setText(UserMain.Txt("Pause")); // NOI18N
        TBT_PAUSE.setBorder(null);
        TBT_PAUSE.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        TBT_PAUSE.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        TBT_PAUSE.setIconTextGap(10);
        TBT_PAUSE.setPressedIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/ok.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, 36, Short.MAX_VALUE)
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
                        .addComponent(TXT_DA, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TXT_DS, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(TXT_ARSIZ, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                                    .addComponent(TXT_ARCNT, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                                    .addComponent(TXT_ASIZ, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                                    .addComponent(TXT_ACNT, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(TXT_TSIZ, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                                    .addComponent(TXT_TCNT, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                                    .addComponent(TXT_TRSIZ, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)
                                    .addComponent(TXT_TRCNT, javax.swing.GroupLayout.DEFAULT_SIZE, 157, Short.MAX_VALUE)))
                            .addComponent(TXT_STATUS, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(PB_PERCENT, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                                .addGap(18, 18, 18))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createSequentialGroup()
                                        .addComponent(TBT_ACTIVE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 2, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(TBT_ABORT))
                                .addGap(208, 208, 208)))
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TBT_PAUSE)
                            .addComponent(BT_STARTSTOP))
                        .addGap(10, 10, 10))))
        );
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
                    .addComponent(TBT_ACTIVE)
                    .addComponent(TBT_PAUSE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TBT_ABORT)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BT_STARTSTOP)
                    .addComponent(PB_PERCENT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 213, Short.MAX_VALUE)
                        .addComponent(BT_OKAY, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OKAY, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_ABORT))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKAYActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKAYActionPerformed
    {//GEN-HEADEREND:event_BT_OKAYActionPerformed
        // TODO add your handling code here:
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
        start();
    }//GEN-LAST:event_BT_STARTSTOPActionPerformed

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ABORTActionPerformed
    {//GEN-HEADEREND:event_BT_ABORTActionPerformed
        // TODO add your handling code here:
        abort();
    }//GEN-LAST:event_BT_ABORTActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_OKAY;
    private javax.swing.JButton BT_STARTSTOP;
    private javax.swing.JProgressBar PB_PERCENT;
    private javax.swing.JToggleButton TBT_ABORT;
    private javax.swing.JToggleButton TBT_ACTIVE;
    private javax.swing.JToggleButton TBT_PAUSE;
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
        read_status();
    }

}
