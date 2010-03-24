/*
 * GlossErrDialog.java
 *
 * Created on 25. M�rz 2008, 00:09
 */
package dimm.home.Rendering;

import dimm.home.Main;
import dimm.home.UserMain;
import java.awt.Color;
import javax.swing.ImageIcon;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

/**
 *
 * @author  mw
 */
public class GlossErrDialog extends javax.swing.JDialog
{
    public static final int MODE_WARN = 1;
    public static final int MODE_ERROR = 2;
    public static final int MODE_INFO = 3;
    public static final int MODE_BUSY = 4;
    public static final int MODE_BUSY_ABORTABLE = 5;

  
 

    private boolean okay = false;
    private boolean aborted = false;
    Radar radar;
    
    /** Creates new form GlossErrDialog */
    public GlossErrDialog(java.awt.Frame parent)
    {
        super(parent, true);

        try
        {
            ResourceInjector.get().inject(this);
        }
        catch (Exception e)
        {
            e = null;
        }
        initComponents();

        radar = new Radar();
        set_defaults();
    }
    public GlossErrDialog(java.awt.Dialog parent)
    {
        super(parent, true);
        initComponents();

        radar = new Radar();
//        radar = new Radar();
        this.getRootPane().setDefaultButton(BT_OK);
        set_defaults();
    }

    public boolean isAborted()
    {
        return aborted;
    }
    public void start_radar()
    {
        radar.start();
    }
    public void stop_radar()
    {
        radar.stop();
    }

 
    
    private void set_defaults()
    {
        TitlePanel titlePanel = new TitlePanel(this);
        PN_TITEL.add(titlePanel);
        titlePanel.installListeners();
        pack();
        //this.SCP_TEXT.getViewport().setOpaque(false);
        this.TXT_ERR.setForeground(Main.ui.get_nice_white());
        this.LBX_ICON.setIcon( new ImageIcon(this.getClass().getResource("/dimm/home/images/errmok_error.png")));
    }
    public void set_mode(int m)
    {
        aborted = false;
        PN_ICON.remove(LBX_ICON);
        switch(m)
        {
            case MODE_ERROR:
            {
                setTitle(UserMain.getString("Fehler") );
                this.LBX_ICON.setIcon( new ImageIcon(this.getClass().getResource("/dimm/home/images/errmok_error.png")));                
                PN_ICON.add(LBX_ICON);
                break;
            }
            case MODE_WARN:
            {
                setTitle(UserMain.getString("Achtung") );
                this.LBX_ICON.setIcon( new ImageIcon(this.getClass().getResource("/dimm/home/images/achtung_medium.png")));                
                PN_ICON.add(LBX_ICON);
                break;
            }
            case MODE_INFO:
            {
                setTitle(UserMain.getString("Hinweis") );
                this.LBX_ICON.setIcon( new ImageIcon(this.getClass().getResource("/dimm/home/images/achtung_medium.png")));                
                PN_ICON.add(LBX_ICON);
                break;
            }
            case MODE_BUSY:
            {
                setTitle("Busy..." );
                this.LBX_ICON.setIcon( new ImageIcon(this.getClass().getResource("/dimm/home/images/busy_medium_small.png")));     
                this.BT_ABORT.setVisible(false);
                this.BT_OK.setVisible(false);
                this.setModal(false);
                PN_ICON.add(radar);
                break;
            }
            case MODE_BUSY_ABORTABLE:
            {
                setTitle("Busy..." );
                this.LBX_ICON.setIcon( new ImageIcon(this.getClass().getResource("/dimm/home/images/busy_medium_small.png")));     
                this.BT_ABORT.setVisible(true);
                this.BT_OK.setVisible(false);
                this.setModal(false);
                PN_ICON.add(radar);
                break;
            }
            default:
            {
                setTitle(UserMain.getString("Achtung") );
                this.LBX_ICON.setIcon( new ImageIcon(this.getClass().getResource("/dimm/home/images/achtung_medium.png")));                
                PN_ICON.add(LBX_ICON);
                break;
            }
        }
        pack();
    }
 
    public void setText(String txt)
    {
        this.TXT_ERR.setText(txt);
        pack();
    }

    public void setCancel(boolean f)
    {
        this.BT_ABORT.setVisible(f);
    }
/*
    public static void errm_ok(String titel, String text)
    {
        final GlossErrDialog dlg = new GlossErrDialog((Frame)null);

        dlg.setTitle(text);
        dlg.setText(text);
        dlg.setCancel(false);

        SwingUtilities.invokeLater(new Runnable()
        {

            public void run()
            {
                dlg.setLocation(320, 200);
                dlg.setVisible(true);
            }
            });


    }*/

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        jXPanel1 = new GlossPanel();
        PN_TITEL = new javax.swing.JPanel();
        PN_ERR = new javax.swing.JPanel();
        TXT_ERR = new javax.swing.JTextArea();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();
        PN_ICON = new javax.swing.JPanel();
        LBX_ICON = new org.jdesktop.swingx.JXLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);
        setResizable(false);
        setUndecorated(true);

        jXPanel1.setLayout(new java.awt.GridBagLayout());

        PN_TITEL.setLayout(new javax.swing.BoxLayout(PN_TITEL, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        jXPanel1.add(PN_TITEL, gridBagConstraints);

        PN_ERR.setOpaque(false);

        TXT_ERR.setColumns(20);
        TXT_ERR.setEditable(false);
        TXT_ERR.setFont(new java.awt.Font("Arial", 0, 12));
        TXT_ERR.setLineWrap(true);
        TXT_ERR.setRows(5);
        TXT_ERR.setText("??"); // NOI18N
        TXT_ERR.setWrapStyleWord(true);
        TXT_ERR.setBorder(null);
        TXT_ERR.setOpaque(false);

        BT_OK.setText(UserMain.Txt("Okay")); // NOI18N
        BT_OK.setBorder(null);
        BT_OK.setBorderPainted(false);
        BT_OK.setContentAreaFilled(false);
        BT_OK.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                BT_OKActionPerformed(evt);
            }
        });

        BT_ABORT.setText(UserMain.Txt("Abbruch")); // NOI18N
        BT_ABORT.setBorder(null);
        BT_ABORT.setBorderPainted(false);
        BT_ABORT.setContentAreaFilled(false);
        BT_ABORT.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                BT_ABORTActionPerformed(evt);
            }
        });

        PN_ICON.setOpaque(false);
        PN_ICON.setLayout(new javax.swing.BoxLayout(PN_ICON, javax.swing.BoxLayout.LINE_AXIS));

        LBX_ICON.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/radarframe.png"))); // NOI18N
        PN_ICON.add(LBX_ICON);

        javax.swing.GroupLayout PN_ERRLayout = new javax.swing.GroupLayout(PN_ERR);
        PN_ERR.setLayout(PN_ERRLayout);
        PN_ERRLayout.setHorizontalGroup(
            PN_ERRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ERRLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ERRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PN_ERRLayout.createSequentialGroup()
                        .addComponent(PN_ICON, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(TXT_ERR))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_ERRLayout.createSequentialGroup()
                        .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        PN_ERRLayout.setVerticalGroup(
            PN_ERRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ERRLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ERRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PN_ICON, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TXT_ERR, javax.swing.GroupLayout.DEFAULT_SIZE, 110, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PN_ERRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        jXPanel1.add(PN_ERR, gridBagConstraints);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jXPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jXPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 163, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    private void BT_OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BT_OKActionPerformed
    // TODO add your handling code here:
        setOkay(true);
        this.setVisible(false);
    }//GEN-LAST:event_BT_OKActionPerformed

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BT_ABORTActionPerformed
    // TODO add your handling code here:
        setOkay(false);
        aborted = true;
        this.setVisible(false);
        
    }//GEN-LAST:event_BT_ABORTActionPerformed
    /**
     * @param args the command line arguments
     */
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_OK;
    private org.jdesktop.swingx.JXLabel LBX_ICON;
    private javax.swing.JPanel PN_ERR;
    private javax.swing.JPanel PN_ICON;
    private javax.swing.JPanel PN_TITEL;
    private javax.swing.JTextArea TXT_ERR;
    private org.jdesktop.swingx.JXPanel jXPanel1;
    // End of variables declaration//GEN-END:variables

    public boolean isOkay()
    {
        return okay;
    }

    public void setOkay(boolean okay)
    {
        this.okay = okay;
    }

    public void set_radar_percent( double percent )
    {
        if (radar != null)
            radar.set_percent(percent);
    }
}
