/*
 * SplashDlg.java
 *
 * Created on 13. Juni 2008, 10:13
 */

package dimm.home;

import dimm.home.Panels.PreferencesPanel;
import dimm.home.Rendering.GenericGlossyDlg;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.Timer;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

/**
 
 @author  Administrator
 */
public class SplashDlg extends javax.swing.JDialog implements ActionListener
{
    Timer timer;
    float incr;
    float act_alpha;

    @InjectedResource
    private BufferedImage glowPicture;
    @InjectedResource
    private BufferedImage noglowPicture;

    
    /** Creates new form SplashDlg */
    public SplashDlg(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        ResourceInjector.get().inject(this);

        initComponents();
        
        incr = 0.03f;
        act_alpha = 0.0f;
        timer = new Timer(50, this );
        
        PN_GLOW.setAlpha(act_alpha);
        Toolkit tk = Toolkit.getDefaultToolkit();
        int x = tk.getScreenSize().width;
        int y = tk.getScreenSize().height;
        
        this.setLocation(x/2 - 220, y/2 - 220);
        
        LB_VERSION.setText("V " + Main.version_str);
        //this.CB_CHECK_NEWS.setSelected(check_news);
    }
    
    @Override
    public void setVisible(boolean b)
    {
        super.setVisible(b);
        
        if (b)
            timer.start();
        else
            timer.stop();
    }
    public void set_text(String t )
    {
        LB_LOAD.setText(t);
    }
    
    /** This method is called from within the constructor to
     initialize the form.
     WARNING: Do NOT modify this code. The content of this method is
     always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        PN_NO_GLOW = new org.jdesktop.swingx.JXPanel();
        jButton1 = new javax.swing.JButton();
        PN_GLOW = new org.jdesktop.swingx.JXPanel();
        jButton2 = new javax.swing.JButton();
        jXPanel1 = new org.jdesktop.swingx.JXPanel();
        LB_LOAD = new javax.swing.JLabel();
        LB_VERSION = new javax.swing.JLabel();
        BT_PREFS = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setResizable(false);
        setUndecorated(true);

        PN_NO_GLOW.setOpaque(false);
        PN_NO_GLOW.setPaintBorderInsets(false);
        PN_NO_GLOW.setLayout(new javax.swing.BoxLayout(PN_NO_GLOW, javax.swing.BoxLayout.LINE_AXIS));

        jButton1.setIcon(new ImageIcon(noglowPicture));
        jButton1.setBorderPainted(false);
        jButton1.setContentAreaFilled(false);
        jButton1.setIconTextGap(0);
        jButton1.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton1.setMaximumSize(new java.awt.Dimension(440, 440));
        jButton1.setMinimumSize(new java.awt.Dimension(440, 440));
        jButton1.setPreferredSize(new java.awt.Dimension(440, 440));
        PN_NO_GLOW.add(jButton1);

        PN_NO_GLOW.setBounds(0, 0, 440, 440);
        jLayeredPane1.add(PN_NO_GLOW, javax.swing.JLayeredPane.DEFAULT_LAYER);

        PN_GLOW.setAlpha(0.0F);
        PN_GLOW.setOpaque(false);
        PN_GLOW.setPaintBorderInsets(false);
        PN_GLOW.setLayout(new javax.swing.BoxLayout(PN_GLOW, javax.swing.BoxLayout.LINE_AXIS));

        jButton2.setIcon(new ImageIcon(glowPicture));
        jButton2.setBorderPainted(false);
        jButton2.setIconTextGap(0);
        jButton2.setMargin(new java.awt.Insets(0, 0, 0, 0));
        jButton2.setMaximumSize(new java.awt.Dimension(440, 440));
        jButton2.setMinimumSize(new java.awt.Dimension(440, 440));
        jButton2.setOpaque(false);
        jButton2.setPreferredSize(new java.awt.Dimension(440, 440));
        PN_GLOW.add(jButton2);

        PN_GLOW.setBounds(0, 0, 440, 440);
        jLayeredPane1.add(PN_GLOW, javax.swing.JLayeredPane.PALETTE_LAYER);

        jXPanel1.setOpaque(false);
        jXPanel1.setPaintBorderInsets(false);

        LB_LOAD.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        LB_LOAD.setForeground(new java.awt.Color(210, 210, 210));
        LB_LOAD.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        LB_LOAD.setText("Loading..."); // NOI18N
        LB_LOAD.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);
        LB_LOAD.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        LB_LOAD.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        LB_VERSION.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        LB_VERSION.setForeground(new java.awt.Color(201, 201, 201));
        LB_VERSION.setText("V 1.2.3");
        LB_VERSION.setVerticalAlignment(javax.swing.SwingConstants.BOTTOM);

        BT_PREFS.setText(UserMain.Txt("Preferences")); // NOI18N
        BT_PREFS.setBorderPainted(false);
        BT_PREFS.setContentAreaFilled(false);
        BT_PREFS.setFocusPainted(false);
        BT_PREFS.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        BT_PREFS.setMargin(new java.awt.Insets(0, 0, 0, 0));
        BT_PREFS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_PREFSActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jXPanel1Layout = new javax.swing.GroupLayout(jXPanel1);
        jXPanel1.setLayout(jXPanel1Layout);
        jXPanel1Layout.setHorizontalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LB_VERSION, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(LB_LOAD, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 66, Short.MAX_VALUE)
                .addComponent(BT_PREFS)
                .addContainerGap())
        );
        jXPanel1Layout.setVerticalGroup(
            jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jXPanel1Layout.createSequentialGroup()
                .addContainerGap(397, Short.MAX_VALUE)
                .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel1Layout.createSequentialGroup()
                        .addComponent(LB_LOAD)
                        .addGap(30, 30, 30))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jXPanel1Layout.createSequentialGroup()
                        .addGroup(jXPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(LB_VERSION)
                            .addComponent(BT_PREFS))
                        .addContainerGap())))
        );

        jXPanel1.setBounds(0, 0, 440, 440);
        jLayeredPane1.add(jXPanel1, javax.swing.JLayeredPane.POPUP_LAYER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    boolean prefs_active = false;
    private void BT_PREFSActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_PREFSActionPerformed
    {//GEN-HEADEREND:event_BT_PREFSActionPerformed
        // TODO add your handling code here:
        PreferencesPanel pnl = new PreferencesPanel();
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
        dlg.setLocationRelativeTo(null);

        prefs_active = true;
        dlg.setVisible(true);

        if (pnl.isOkay())
        {
            Main.reinit_prefs();
        }
        prefs_active = false;
        
    }//GEN-LAST:event_BT_PREFSActionPerformed
    
  
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_PREFS;
    private javax.swing.JLabel LB_LOAD;
    private javax.swing.JLabel LB_VERSION;
    private org.jdesktop.swingx.JXPanel PN_GLOW;
    private org.jdesktop.swingx.JXPanel PN_NO_GLOW;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLayeredPane jLayeredPane1;
    private org.jdesktop.swingx.JXPanel jXPanel1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void actionPerformed(ActionEvent e)
    {
        act_alpha += incr;
        if (act_alpha >= 0.5f && incr > 0)
        {
            incr *= -1;
            act_alpha = 0.5f;
        }
        if (act_alpha <= 0.0f && incr < 0)
        {
            incr *= -1;
            act_alpha = 0.0f;
        }
        PN_GLOW.setAlpha(act_alpha);
      
    }

    boolean prefs_active()
    {
        return prefs_active;
    }
    
}
