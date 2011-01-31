/*
 * HTMLPanel.java
 *
 * Created on 10. April 2008, 20:47
 */

package dimm.home.Panels.MailView;

import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.UserMain;
import java.awt.EventQueue;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import org.lobobrowser.html.parser.*;
import org.lobobrowser.html.test.*;
import org.lobobrowser.html.gui.*;
//import org.w3c.css.sac.InputSource;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author  mw
 */
public class HTMLViewPanel extends GlossDialogPanel {
    
    boolean ok;
    SimpleHtmlRendererContext ctx;
    String page;
    //HtmlPanel panel;
    /** Creates new form HTMLPanel */
    public HTMLViewPanel(String _page) 
    {
        initComponents();
        page = _page;

   
        Logger.getLogger("org.lobobrowser").setLevel(Level.WARNING);

    }
    
     public void show_browser(String uri) throws MalformedURLException, IOException, SAXException
     {
         show_browser( (String)null, uri );
     }
    
    
     public void show_browser(String file, String uri) throws MalformedURLException, IOException, SAXException
     {
        // Open a connection on the URL we want to render first.
        //String uri = "http://lobobrowser.org/browser/home.jsp";
        InputStream in;
        
        if (file == null)
        {
            URL url = new URL(uri);
            URLConnection connection = url.openConnection();
            in = connection.getInputStream();
        }
        else
        {
            in = new FileInputStream( file ); 
        }
        show_browser(in, uri);
     }
     public void show_browser(InputStream in, String uri) throws MalformedURLException, IOException, SAXException
     {

        // A Reader should be created with the correct charset,
        // which may be obtained from the Content-Type header
        // of an HTTP response.
        Reader reader = new InputStreamReader(in);

        // InputSourceImpl constructor with URI recommended
        // so the renderer can resolve page component URLs.
        InputSource is = new InputSourceImpl(reader, uri);
        HtmlPanel htmlPanel = new HtmlPanel();
        /*UserAgentContext ucontext = new LocalUserAgentContext();
        HtmlRendererContext rendererContext = 
                new LocalHtmlRendererContext(htmlPanel, ucontext);
        */
        // Set a preferred width for the HtmlPanel,
        // which will allow getPreferredSize() to
        // be calculated according to block content.
        // We do this here to illustrate the 
        // feature, but is generally not
        // recommended for performance reasons.
        htmlPanel.setPreferredWidth(800);

        ctx = new SimpleHtmlRendererContext(htmlPanel, new SimpleUserAgentContext());

        // Note: This example does not perform incremental
        // rendering while loading the initial document.
        DocumentBuilderImpl builder = 
                new DocumentBuilderImpl(
                        ctx.getUserAgentContext(), 
                        ctx);

        Document document = builder.parse(is);
        in.close();

        // Set the document in the HtmlPanel. This method
        // schedules the document to be rendered in the
        // GUI thread.
        htmlPanel.setDocument(document, ctx);

        // Create a JFrame and add the HtmlPanel to it.
        /*final JFrame frame = new JFrame();
        frame.getContentPane().add(htmlPanel);*/
        this.PN_HTML.add(htmlPanel);

        // We pack the JFrame to demonstrate the
        // validity of HtmlPanel's preferred size.
        // Normally you would want to set a specific
        // JFrame size instead.

        // pack() should be called in the GUI dispatch
        // thread since the document is scheduled to
        // be rendered in that thread, and is required
        // for the preferred size determination.
        EventQueue.invokeLater(new Runnable() {
                public void run() {
                        my_dlg.pack();
                        my_dlg.setVisible(true);
                }
        });
    }
    
    public void set_ok_text( String txt )
    {
        BT_OK.setText(txt);
    }
    public void set_cancel_text( String txt )
    {
        if (txt == null)
            BT_ABORT.setVisible(false);
        else 
        {
            BT_ABORT.setVisible(true);
            BT_ABORT.setText(txt);
        }
    }
    public boolean get_ok()
    {
        return ok;
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PN_HTML = new javax.swing.JPanel();
        PN_BUTTONS = new javax.swing.JPanel();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();
        BT_WEB = new GlossButton();

        PN_HTML.setLayout(new javax.swing.BoxLayout(PN_HTML, javax.swing.BoxLayout.LINE_AXIS));

        PN_BUTTONS.setDoubleBuffered(false);
        PN_BUTTONS.setOpaque(false);

        BT_OK.setText(UserMain.Txt("OK")); // NOI18N
        BT_OK.setMaximumSize(new java.awt.Dimension(100, 43));
        BT_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKActionPerformed(evt);
            }
        });

        BT_ABORT.setText(UserMain.Txt("Abort")); // NOI18N
        BT_ABORT.setActionCommand(UserMain.Txt("Abort")); // NOI18N
        BT_ABORT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ABORTActionPerformed(evt);
            }
        });

        BT_WEB.setText(UserMain.Txt("Website")); // NOI18N
        BT_WEB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_WEBActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_BUTTONSLayout = new javax.swing.GroupLayout(PN_BUTTONS);
        PN_BUTTONS.setLayout(PN_BUTTONSLayout);
        PN_BUTTONSLayout.setHorizontalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BT_WEB, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 326, Short.MAX_VALUE)
                .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        PN_BUTTONSLayout.setVerticalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_WEB, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PN_BUTTONS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(PN_HTML, javax.swing.GroupLayout.DEFAULT_SIZE, 681, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(PN_HTML, javax.swing.GroupLayout.DEFAULT_SIZE, 504, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PN_BUTTONS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BT_OKActionPerformed
        // TODO add your handling code here:
        ok  = true;

        if (ok)
            this.setVisible(false);
    }//GEN-LAST:event_BT_OKActionPerformed

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BT_ABORTActionPerformed
        // TODO add your handling code here:
        ok  = false;
        this.setVisible(false);
    }//GEN-LAST:event_BT_ABORTActionPerformed

    private void BT_WEBActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_WEBActionPerformed
    {//GEN-HEADEREND:event_BT_WEBActionPerformed
        // TODO add your handling code here:
        
        
}//GEN-LAST:event_BT_WEBActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_OK;
    private javax.swing.JButton BT_WEB;
    private javax.swing.JPanel PN_BUTTONS;
    private javax.swing.JPanel PN_HTML;
    // End of variables declaration//GEN-END:variables

    @Override
    public JButton get_default_button()
    {
        return BT_OK;
    }
    
}
