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
import dimm.home.Rendering.GlossTable;
import dimm.home.UserMain;
import home.shared.CS_Constants;
import home.shared.mail.RFCMimeMail;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;
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
class MailHeaderModel extends AbstractTableModel
{

    RFCMimeMail msg;
    Address[] from;
    Address[] to;

    public MailHeaderModel( RFCMimeMail msg )
    {
        this.msg = msg;
        MimeMessage mime_msg = msg.getMsg();
        try
        {
            from = mime_msg.getFrom();
            to = mime_msg.getAllRecipients();
        }
        catch (MessagingException messagingException)
        {
        }
    }

    @Override
    public Object getValueAt( int row, int column )
    {
        if (column == 0)
        {
            if (row < from.length)
                return "From:";
            else
                return "To:";
        }
        else
        {
            if (row < from.length)
                return from[row].toString();
            else
                return to[row - from.length].toString();
        }
    }

    @Override
    public boolean isCellEditable( int row, int column )
    {
        return false;
    }

    @Override
    public int getRowCount()
    {
        if (from == null)
            return 0;
        if (to == null)
            return 0;

        return from.length + to.length;
    }

    @Override
    public int getColumnCount()
    {
        return 2;
    }
    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        return String.class;
    }

}
class MailAttachmentModel extends AbstractTableModel
{

    RFCMimeMail msg;
  
    public MailAttachmentModel( RFCMimeMail msg )
    {
        this.msg = msg;
        
     }

    @Override
    public Object getValueAt( int row, int column )
    {
        if (column == 0)
        {
            Part attachment = msg.get_attachment(row);
            String name = null;
            try
            {
                name = attachment.getFileName();
                if (name == null)
                {
                    name = attachment.getDescription();
                }
            }
            catch (MessagingException messagingException)
            {
            }
            return name;
        }
        return null;
    }

    @Override
    public boolean isCellEditable( int row, int column )
    {
        return false;
    }

    @Override
    public int getRowCount()
    {
        return msg.get_attachment_cnt();
    }

    @Override
    public int getColumnCount()
    {
        return 1;
    }
    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        return String.class;
    }

}


public class MailPreviewPanel extends GlossDialogPanel implements MouseListener
{

    String search_id;
    Component last_renderer_component;
    RFCMimeMail msg;
    String html_txt;
    String plain_txt;
    GlossTable tb_header;
    GlossTable tb_att;

    /** Creates new form MailViewPanel */
    public MailPreviewPanel( RFCMimeMail msg )
    {
        this.msg = msg;

        initComponents();

        last_renderer_component = SCP_TXTA;

        html_txt = msg.get_html_content();
        plain_txt = msg.get_text_content();


        if (html_txt != null)
        {
            if (Main.get_prefs().get_boolean_prop(Preferences.HTML_HQ_RENDERER))
            {
                CB_HQ.setSelected(true);  // CALLS CALLBACK AND SETS BROWSER
            }
            else
            {
                Component renderer = create_lobobrowser_renderer();
                add_view_panel(renderer);
            }
        }
        else
        {
            CB_HQ.setVisible(false);
            TXTA_MAIL.setText(plain_txt);
            TXTA_MAIL.setCaretPosition(0);
        }

        set_table_models();

        
    }

    void set_table_models()
    {
        MailHeaderModel header_model = new MailHeaderModel(msg);

        tb_header = new GlossTable();

        // REGISTER TABLE TO SCROLLPANEL
        tb_header.embed_to_scrollpanel( SCP_HEADER );
        tb_header.setModel(header_model);
        tb_header.setTableHeader(null);
        tb_header.getColumnModel().getColumn(0).setMinWidth(40);
        tb_header.getColumnModel().getColumn(0).setMaxWidth(60);


        MailAttachmentModel attachment_model = new MailAttachmentModel(msg);
        tb_att = new GlossTable();
        tb_att.setModel(attachment_model);
        tb_att.setTableHeader(null);
        tb_att.addMouseListener(this);
        tb_att.embed_to_scrollpanel( SCP_ATTACHMENT );
    }

    Component create_columba_renderer()
    {
        try
        {
            ColumbaFSRenderer renderer = new ColumbaFSRenderer();
            renderer.view(html_txt);
            return renderer;
        }
        catch (Exception e)
        {
        }
        return null;
    }

    Component create_text_renderer()
    {
        TXTA_MAIL.setText(plain_txt);
        TXTA_MAIL.setCaretPosition(0);
        return TXTA_MAIL;
    }

    Component create_lobobrowser_renderer()
    {
        HtmlPanel htmlPanel = null;
        Reader reader = null;
        InputStream in = null;
        try
        {
            in = new ByteArrayInputStream(html_txt.getBytes("UTF-8"));
            String uri = "??";
            SimpleHtmlRendererContext ctx;

            // A Reader should be created with the correct charset,
            // which may be obtained from the Content-Type header
            // of an HTTP response.
            reader = new InputStreamReader(in);

            // InputSourceImpl constructor with URI recommended
            // so the renderer can resolve page component URLs.
            InputSource is = new InputSourceImpl(reader, uri);
            htmlPanel = new HtmlPanel();

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

            Document document = null;
            document = builder.parse(is);
            in.close();

            // Set the document in the HtmlPanel. This method
            // schedules the document to be rendered in the
            // GUI thread.
            htmlPanel.setDocument(document, ctx);


            // We pack the JFrame to demonstrate the
            // validity of HtmlPanel's preferred size.
            // Normally you would want to set a specific
            // JFrame size instead.

            // pack() should be called in the GUI dispatch
            // thread since the document is scheduled to
            // be rendered in that thread, and is required
            // for the preferred size determination.
            if (my_dlg != null)
            {
                EventQueue.invokeLater(new Runnable()
                {

                    @Override
                    public void run()
                    {
                        my_dlg.pack();
                    }
                });
            }
            return htmlPanel;

        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        finally
        {
            try
            {
                if (in != null)
                {
                    in.close();
                }
                if (reader != null)
                {
                    reader.close();
                }
            }
            catch (IOException iOException)
            {
            }
        }
        return null;
    }

    void add_view_panel( Component new_renderer_pane )
    {
        PN_VIEW.remove(last_renderer_component);


        // FALLBACK TO TEXT IF HTML CANNOT RENDER
        if (new_renderer_pane == null)
        {
            new_renderer_pane = create_text_renderer();
        }

        javax.swing.GroupLayout PN_VIEWLayout = new javax.swing.GroupLayout(PN_VIEW);
        PN_VIEW.setLayout(PN_VIEWLayout);
        PN_VIEWLayout.setHorizontalGroup(
                PN_VIEWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(new_renderer_pane, javax.swing.GroupLayout.DEFAULT_SIZE, 593, Short.MAX_VALUE));
        PN_VIEWLayout.setVerticalGroup(
                PN_VIEWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(new_renderer_pane, javax.swing.GroupLayout.DEFAULT_SIZE, 410, Short.MAX_VALUE));


        last_renderer_component = new_renderer_pane;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BT_CLOSE = new javax.swing.JButton();
        CB_HQ = new javax.swing.JCheckBox();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        SCP_HEADER = new javax.swing.JScrollPane();
        SCP_ATTACHMENT = new javax.swing.JScrollPane();
        PN_VIEW = new javax.swing.JPanel();
        SCP_TXTA = new javax.swing.JScrollPane();
        TXTA_MAIL = new javax.swing.JTextArea();

        BT_CLOSE.setText(UserMain.getString("Schliessen")); // NOI18N
        BT_CLOSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_CLOSEActionPerformed(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dimm/home/MA_Properties"); // NOI18N
        CB_HQ.setText(bundle.getString("High-Quality")); // NOI18N
        CB_HQ.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_HQActionPerformed(evt);
            }
        });

        jSplitPane1.setDividerLocation(100);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jSplitPane2.setDividerLocation(400);
        jSplitPane2.setLeftComponent(SCP_HEADER);
        jSplitPane2.setRightComponent(SCP_ATTACHMENT);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 99, Short.MAX_VALUE)
        );

        jSplitPane1.setTopComponent(jPanel1);

        TXTA_MAIL.setColumns(20);
        TXTA_MAIL.setRows(5);
        SCP_TXTA.setViewportView(TXTA_MAIL);

        javax.swing.GroupLayout PN_VIEWLayout = new javax.swing.GroupLayout(PN_VIEW);
        PN_VIEW.setLayout(PN_VIEWLayout);
        PN_VIEWLayout.setHorizontalGroup(
            PN_VIEWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_TXTA, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
        );
        PN_VIEWLayout.setVerticalGroup(
            PN_VIEWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_TXTA, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
        );

        jSplitPane1.setBottomComponent(PN_VIEW);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 632, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(CB_HQ)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 440, Short.MAX_VALUE)
                        .addComponent(BT_CLOSE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_CLOSE)
                    .addComponent(CB_HQ))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_CLOSEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_CLOSEActionPerformed
    {//GEN-HEADEREND:event_BT_CLOSEActionPerformed
        // TODO add your handling code here:
        my_dlg.setVisible(false);
    }//GEN-LAST:event_BT_CLOSEActionPerformed

    private void CB_HQActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_HQActionPerformed
    {//GEN-HEADEREND:event_CB_HQActionPerformed
        // TODO add your handling code here:
        if (CB_HQ.isSelected())
        {
            Component renderer = create_columba_renderer();
            add_view_panel(renderer);
        }
        else
        {
            Component renderer = create_lobobrowser_renderer();
            add_view_panel(renderer);
        }
    }//GEN-LAST:event_CB_HQActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_CLOSE;
    private javax.swing.JCheckBox CB_HQ;
    private javax.swing.JPanel PN_VIEW;
    private javax.swing.JScrollPane SCP_ATTACHMENT;
    private javax.swing.JScrollPane SCP_HEADER;
    private javax.swing.JScrollPane SCP_TXTA;
    private javax.swing.JTextArea TXTA_MAIL;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    // End of variables declaration//GEN-END:variables

    @Override
    public JButton get_default_button()
    {
        return BT_CLOSE;
    }

    @Override
    public void mouseClicked( MouseEvent e )
    {
        if (e.getClickCount() == 2 && e.getSource() == tb_att)
        {
            int row = tb_att.rowAtPoint(e.getPoint());

            store_attachment( row );


        }

    }

    @Override
    public void mousePressed( MouseEvent e )
    {

    }

    @Override
    public void mouseReleased( MouseEvent e )
    {

    }

    @Override
    public void mouseEntered( MouseEvent e )
    {

    }

    @Override
    public void mouseExited( MouseEvent e )
    {

    }

    static File last_dir = null;
    private void store_attachment( int row )
    {
        Part p = msg.get_attachment(row);

        FileDialog fd = new FileDialog(my_dlg);
        fd.setMode(FileDialog.SAVE);

        fd.setLocation(my_dlg.getLocationOnScreen().x + 20, my_dlg.getLocationOnScreen().y + 20 );


        if (last_dir != null)
        {
            fd.setDirectory(last_dir.getAbsolutePath());
        }
        try
        {
            fd.setFile(p.getFileName());
        }
        catch (MessagingException messagingException)
        {
            fd.setFile("Attachment.att");
        }


        fd.setVisible(true);

        String f_name = fd.getFile();
        if (f_name == null)
            return;

        File trg_file = new File(fd.getDirectory(), f_name );

        last_dir = trg_file.getParentFile();

        if (trg_file.exists())
        {
            if (!UserMain.errm_ok_cancel(my_dlg, UserMain.Txt("Do_you_want_to_overwrite_this_file")))
                return;
        }

        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try
        {
            InputStream is = p.getInputStream();
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream( new FileOutputStream(trg_file) );

            byte [] buffer = new byte[CS_Constants.STREAM_BUFFER_LEN];
            while (true)
            {
                int rlen = bis.read(buffer);
                if (rlen == -1)
                    break;
                
                bos.write(buffer, 0, rlen);
            }
        }
        catch (Exception messagingException)
        {
            messagingException.printStackTrace();
            UserMain.errm_ok(my_dlg, UserMain.Txt("Could_not_save_attachment") + ": " + messagingException.getMessage());
        }
        finally
        {
            if (bis != null)
            {
                try
                {
                    bis.close();
                }
                catch (IOException iOException)
                {
                }
            }

            if (bos != null)
            {
                try
                {
                    bos.close();
                }
                catch (IOException iOException)
                {
                }
            }
        }
    }
}