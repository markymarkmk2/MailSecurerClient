/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MailViewPanel.java
 *
 * Created on 15.07.2009, 16:14:22
 */

package dimm.home.Panels.MailView;

import dimm.home.Main;
import dimm.home.Preferences;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.UserMain;
import java.awt.Component;
import java.awt.EventQueue;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.swing.JButton;
import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.parser.*;
import org.lobobrowser.html.test.SimpleHtmlRendererContext;
import org.lobobrowser.html.test.SimpleUserAgentContext;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 *
 * @author mw
 */
public class MailPreviewPanel extends GlossDialogPanel
{
    String search_id;
    ColumbaFSRenderer renderer;

    /** Creates new form MailViewPanel */
    public MailPreviewPanel(String body, boolean is_html)
    {
        initComponents();

        if (is_html)
        {
            if (Main.get_prefs().get_boolean_prop(Preferences.HTML_HQ_RENDERER))
            {
                renderer = new ColumbaFSRenderer();
                add_view_panel( renderer );
                renderer.view(body);
            }
            else
            {
                try
                {
                    create_browser_panel(new ByteArrayInputStream(body.getBytes("UTF-8")), "??");
                }
                catch (IOException iOException)
                {
                }
                catch (SAXException sAXException)
                {
                }
            }
        }
        else
        {
            TXT_PANE.setText(body);
            TXT_PANE.setCaretPosition(0);
        }
    }

    void create_browser_panel( InputStream in, String uri) throws IOException, SAXException
    {
        SimpleHtmlRendererContext ctx;
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
        //this.PN_VIEW.add(htmlPanel);
        add_view_panel( htmlPanel );

        // We pack the JFrame to demonstrate the
        // validity of HtmlPanel's preferred size.
        // Normally you would want to set a specific
        // JFrame size instead.

        // pack() should be called in the GUI dispatch
        // thread since the document is scheduled to
        // be rendered in that thread, and is required
        // for the preferred size determination.
        EventQueue.invokeLater(new Runnable() {
            @Override
                public void run() {
                        my_dlg.pack();
                        my_dlg.setVisible(true);
                }
        });

    }

    void add_view_panel( Component pane )
    {

        PN_VIEW.remove(SC_TXT_PANE);

        javax.swing.GroupLayout PN_VIEWLayout = new javax.swing.GroupLayout(PN_VIEW);
        PN_VIEW.setLayout(PN_VIEWLayout);
        PN_VIEWLayout.setHorizontalGroup(
            PN_VIEWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pane, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE)
        );
        PN_VIEWLayout.setVerticalGroup(
            PN_VIEWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pane, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
        );

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PN_VIEW = new javax.swing.JPanel();
        SC_TXT_PANE = new javax.swing.JScrollPane();
        TXT_PANE = new javax.swing.JTextPane();
        BT_CLOSE = new javax.swing.JButton();
        BT_EXPORT = new javax.swing.JButton();

        TXT_PANE.setEditable(false);
        SC_TXT_PANE.setViewportView(TXT_PANE);

        javax.swing.GroupLayout PN_VIEWLayout = new javax.swing.GroupLayout(PN_VIEW);
        PN_VIEW.setLayout(PN_VIEWLayout);
        PN_VIEWLayout.setHorizontalGroup(
            PN_VIEWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SC_TXT_PANE, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE)
        );
        PN_VIEWLayout.setVerticalGroup(
            PN_VIEWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SC_TXT_PANE, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE)
        );

        BT_CLOSE.setText(UserMain.getString("Schliessen")); // NOI18N
        BT_CLOSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_CLOSEActionPerformed(evt);
            }
        });

        BT_EXPORT.setText(UserMain.getString("Export_Mail")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PN_VIEW, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BT_EXPORT)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 445, Short.MAX_VALUE)
                        .addComponent(BT_CLOSE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PN_VIEW, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_CLOSE)
                    .addComponent(BT_EXPORT))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_CLOSEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_CLOSEActionPerformed
    {//GEN-HEADEREND:event_BT_CLOSEActionPerformed
        // TODO add your handling code here:
        setVisible(false);
    }//GEN-LAST:event_BT_CLOSEActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_CLOSE;
    private javax.swing.JButton BT_EXPORT;
    private javax.swing.JPanel PN_VIEW;
    private javax.swing.JScrollPane SC_TXT_PANE;
    private javax.swing.JTextPane TXT_PANE;
    // End of variables declaration//GEN-END:variables


    @Override
    public JButton get_default_button()
    {
        return BT_CLOSE;
    }

}
