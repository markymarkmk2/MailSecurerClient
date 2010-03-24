/*
 * NewJDialog.java
 *
 * Created on 20. Mrz 2008, 22:44
 */
package dimm.home.Rendering;

import dimm.home.UserMain;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import javax.swing.JDialog;



/**
 *
 * @author  mw
 */
public class GenericGlossyDlg extends JDialog
{

    UserMain main;
    protected GlossDialogPanel pg_painter;
    GlossDialogPanel child;
      
  
    /** Creates new form NewJDialog */
    public GenericGlossyDlg(UserMain parent, boolean modal, GlossDialogPanel _child)
    {
        super(UserMain.self, modal);
        initComponents();
        this.setLocation(300, 200);
        child = _child;

        child.setDlg(this);
        this.getRootPane().setDefaultButton(child.get_default_button());

        pg_painter = child;
        main = parent;

        TitlePanel titlePanel = new TitlePanel(this);
        PN_TITLE.add(titlePanel);
        titlePanel.installListeners();
        PN_PANELS.add( child);
        
        pack();        
    }
    public void setChildPanel( GlossDialogPanel _child )
    {
        child = _child;
        child.setDlg(this);
        PN_PANELS.removeAll();
        PN_PANELS.add( child);
        
        pack();  
    }
    public GlossDialogPanel get_panel()
    {
        return child;
    }
    

    @Override
    public void setVisible(boolean b)
    {
        if (b)
            child.activate();
        else
            child.deactivate();
        
        super.setVisible(b);

    }

    @Override
    public void setSize(Dimension d)
    {
        pg_painter.setSize(d);
       
        super.setSize(d);        
    }
    public void set_next_location( Component comp )
    {
        Point loc = comp.getLocation();
        if (comp.isVisible())
            loc = comp.getLocationOnScreen();
        
        if (UserMain.self.is_touchscreen())
            setLocation(loc.x + 10, loc.y + 10 );
        else        
            setLocation(loc.x + 30, loc.y + 30 );
        
    }
  

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        PN_MAIN = new GlossPanel();
        PN_TITLE = new javax.swing.JPanel();
        PN_PANELS = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter()
        {
            public void windowClosing(java.awt.event.WindowEvent evt)
            {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        PN_MAIN.setBackground(new java.awt.Color(51, 51, 51));
        PN_MAIN.setLayout(new java.awt.GridBagLayout());

        PN_TITLE.setOpaque(false);
        PN_TITLE.setLayout(new javax.swing.BoxLayout(PN_TITLE, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        PN_MAIN.add(PN_TITLE, gridBagConstraints);

        PN_PANELS.setBackground(new java.awt.Color(51, 51, 51));
        PN_PANELS.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PN_PANELS.setForeground(new java.awt.Color(255, 255, 255));
        PN_PANELS.setOpaque(false);
        PN_PANELS.setLayout(new javax.swing.BoxLayout(PN_PANELS, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        PN_MAIN.add(PN_PANELS, gridBagConstraints);

        getContentPane().add(PN_MAIN);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosing(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosing
    {//GEN-HEADEREND:event_formWindowClosing
        // TODO add your handling code here:
        child.setVisible(false);
    }//GEN-LAST:event_formWindowClosing

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.jdesktop.swingx.JXPanel PN_MAIN;
    private javax.swing.JPanel PN_PANELS;
    private javax.swing.JPanel PN_TITLE;
    // End of variables declaration//GEN-END:variables
}
