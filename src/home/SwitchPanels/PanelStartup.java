/*
 * PanelTasks.java
 *
 * Created on 13. M�rz 2008, 09:39
 */
package dimm.home.SwitchPanels;

import dimm.home.*;
import dimm.home.Rendering.BackgroundTitle;
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GhostButton;
import dimm.home.Rendering.SwitchSpringPanel;
import dimm.home.Utilities.SwingWorker;
import java.awt.Dimension;
import java.awt.Point;
import org.jdesktop.fuse.ResourceInjector;



class HTMLDlg extends GenericGlossyDlg
{

    UserMain main;
    HTMLDlg( UserMain parent)
    {
        super( parent, true, new HTMLViewPanel( "" ));
        main = parent;
        if (parent.isVisible())
            this.setLocation(parent.getLocationOnScreen().x + 30, parent.getLocationOnScreen().y + 30);
        else
            this.setLocationRelativeTo(null);

        this.setSize( 700, 600);
        ((HTMLViewPanel)pg_painter).set_cancel_text( null );

    }



   // String file;
    public void show_dlg( String uri)
    {
  //      file = _file;
        try
        {
            this.setLocation(main.getLocationOnScreen().x + 30, main.getLocationOnScreen().y + 30);
            this.setSize( 700, 600);
            ((HTMLViewPanel)pg_painter).show_browser( uri);
        }
        catch (Exception exc)
        {
            System.err.println("Error while reading HTML file: " + exc.getMessage() );
        }
    }


}


/**
 *
 * @author  Administrator
 */
public class PanelStartup extends SwitchSpringPanel
{
    

    /** Creates new form PanelTasks */
    public PanelStartup( UserMain m )
    {
        super(m, UserMain.PBC_ADMIN);
        ResourceInjector.get().inject(this);

        initComponents();


        PN_BUTTONS.add(new BackgroundTitle( UserMain.getString("Aufgaben" ) ), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 65, getWidth(), 60));
       // PN_BUTTONS.add(new BackgroundTitle( UserMain.getString("Werkzeuge" ) ), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 285, getWidth(), 60));
        
    }

    @Override
    public void setSize( Dimension d )
    {
        this.removeAll();
        
        super.setSize(d);
        

        initComponents();


        PN_BUTTONS.add(new BackgroundTitle( UserMain.getString("Aufgaben" ) ), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 65, getWidth(), 60));
        //PN_BUTTONS.add(new BackgroundTitle( UserMain.getString("Werkzeuge" ) ), new org.netbeans.lib.awtextra.AbsoluteConstraints(0, 285, getWidth(), 60));
    }


 
    boolean check_selected()
    {
        return true;
    }


    
            
    Point get_dlg_pos()
    {
        if (UserMain.self.is_touchscreen())
            return new Point( this.getLocationOnScreen().x + 10, this.getLocationOnScreen().y + 10 );
        else
            return new Point( this.getLocationOnScreen().x + 20, this.getLocationOnScreen().y + 20 );
    }
    



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PN_BUTTONS = new javax.swing.JPanel();
        BT_NETWORK = new GhostButton();
        PN_HEADER = new javax.swing.JPanel();

        setOpaque(false);

        PN_BUTTONS.setOpaque(false);
        PN_BUTTONS.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        BT_NETWORK.setFont(new java.awt.Font("Arial", 0, 14));
        BT_NETWORK.setForeground(new java.awt.Color(201, 201, 201));
        BT_NETWORK.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/tr_lupe.png"))); // NOI18N
        BT_NETWORK.setText(UserMain.Txt("Nach_Mail_stoebern")); // NOI18N
        BT_NETWORK.setBorderPainted(false);
        BT_NETWORK.setContentAreaFilled(false);
        BT_NETWORK.setFocusPainted(false);
        BT_NETWORK.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        BT_NETWORK.setMaximumSize(new java.awt.Dimension(101, 26));
        BT_NETWORK.setMinimumSize(new java.awt.Dimension(101, 26));
        BT_NETWORK.setPreferredSize(new java.awt.Dimension(101, 26));
        BT_NETWORK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_NETWORKActionPerformed(evt);
            }
        });
        PN_BUTTONS.add(BT_NETWORK, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 130, 170, 50));

        PN_HEADER.setOpaque(false);
        PN_HEADER.setLayout(new javax.swing.BoxLayout(PN_HEADER, javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(PN_BUTTONS, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
                    .addComponent(PN_HEADER, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(PN_BUTTONS, javax.swing.GroupLayout.DEFAULT_SIZE, 437, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PN_HEADER, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
   

    private void BT_NETWORKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_NETWORKActionPerformed
    {//GEN-HEADEREND:event_BT_NETWORKActionPerformed
        // TODO add your handling code here:
        
        if (check_selected())
        {
            HTMLDlg dlg = new HTMLDlg(main);

               dlg.show_dlg("http://www.google.de");
            
     
        }
         
      /*  SwingWorker sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
               UserMain.self.show_busy("Haloooooo");
                UserMain.self.show_busy_val(0.0);
                sleep(1000);
                UserMain.self.show_busy_val(10.0);
                sleep(1000);
                UserMain.self.show_busy_val(11.0);
                sleep(1000);
                UserMain.self.show_busy_val(51.0);
                sleep(1000);
                UserMain.self.show_busy_val(60.0);
                sleep(1000);
                UserMain.self.show_busy_val(70.0);
                sleep(1000);
                UserMain.self.show_busy_val(98.0);
                sleep(1000);
                UserMain.self.show_busy_val(99.0);
                sleep(1000);
                UserMain.self.show_busy_val(100.0);
                sleep(1000);

                UserMain.self.hide_busy();

                return null;
            }
        };
        sw.start();
*/
 
}//GEN-LAST:event_BT_NETWORKActionPerformed
 private static void sleep(int millis)
    {
        try
        {
            Thread.sleep(millis);
        }
        catch (InterruptedException interruptedException)
        {
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_NETWORK;
    private javax.swing.JPanel PN_BUTTONS;
    private javax.swing.JPanel PN_HEADER;
    // End of variables declaration//GEN-END:variables

    @Override
    public void activate_panel()
    {
        boolean system_level = true;
    }

    @Override
    public void deactivate_panel()
    {
        
    }
}
