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
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.Rendering.GlossTable;
import dimm.home.UserMain;
import dimm.home.Utilities.CmdExecutor;
import dimm.home.native_libs.NativeLoader;
import home.shared.CS_Constants;
import home.shared.mail.RFCMailAddress;
import home.shared.mail.RFCMimeMail;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.CharArrayReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.mail.internet.MimeUtility;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.table.AbstractTableModel;
import org.lobobrowser.html.gui.HtmlPanel;
import org.lobobrowser.html.parser.*;
import org.lobobrowser.html.test.SimpleHtmlRendererContext;
import org.lobobrowser.html.test.SimpleUserAgentContext;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;


/**
 *
 * @author mw
 */
class MailHeaderModel extends AbstractTableModel
{

    RFCMimeMail msg;
    //String bcc;
    /*ArrayList<String> from;
    ArrayList<String> to;*/

    public MailHeaderModel( RFCMimeMail msg )
    {
        this.msg = msg;
/*
        from = new ArrayList<String>();
        to = new ArrayList<String>();
        MimeMessage mime_msg = msg.getMsg();
        for (int i = 0; i < msg.getEmail_list().size(); i++)
        {
            if (msg.getEmail_list().get(i).is_from())
                from.add( msg.getEmail_list().get(i).get_mail() );
            else
                to.add( msg.getEmail_list().get(i).get_mail() );
        }*/
    }

    @Override
    public Object getValueAt( int row, int column )
    {
        RFCMailAddress mail = msg.getEmail_list().get(row);
        if (column == 0)
        {

            if (mail.getAdr_type() == RFCMailAddress.ADR_TYPE.FROM)
                return "From:";
            if (mail.getAdr_type() == RFCMailAddress.ADR_TYPE.TO)
                return "To:";
            if (mail.getAdr_type() == RFCMailAddress.ADR_TYPE.CC)
                return "Cc:";
            if (mail.getAdr_type() == RFCMailAddress.ADR_TYPE.BCC)
                return "Bcc:";

            return "?:";
        }
        else
        {
            String adr = mail.get_mail();

            String ret = adr;
            try
            {
                ret = MimeUtility.decodeText(adr);
            }
            catch (Exception ex)
            {
            }
            return ret;
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
        return msg.getEmail_list().size();
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
                if (name != null)
                    name = MimeUtility.decodeText(name);

            }
            catch (Exception messagingException)
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
    RFCMimeMail msg;
    String html_txt;
    String plain_txt;
    GlossTable tb_header;
    GlossTable tb_att;
    boolean test_flying_saucer = true;
    String uid;

    /** Creates new form MailViewPanel */
    public MailPreviewPanel( RFCMimeMail msg, String uid )
    {
        this.msg = msg;
        this.uid = uid;

        initComponents();



        html_txt = msg.get_html_content();                
        plain_txt = msg.get_text_content();


        if (html_txt != null)
        {
            Component r = create_html_renderer();
            set_renderer( r );
        }
        else
        {
            Component r = create_text_renderer();
            set_renderer( r );
        }

        set_table_models();        
    }
    
    final Component create_html_renderer()
    {
        
        Component renderer = null;
        Part html_part = msg.get_html_part();
       
            String charset = null;
            if (html_part != null)
            {
                charset = get_charset(html_part);
            }
            if (charset == null)
                charset = "UTF-8";

            renderer = create_lobobrowser_renderer(html_txt, charset);
       
        return renderer;
    }

    final void set_renderer( Component renderer )
    {
        add_view_panel(renderer);
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
    }

     static public String get_charset( Part p )
    {
        if (p == null)
            return null;

        String mt;
        try
        {
            mt = p.getContentType();
        }
        catch (MessagingException ex)
        {
            return null;
        }
        int atr_idx = mt.indexOf(';');
        if (atr_idx == -1)
            atr_idx = mt.indexOf('\n');
        if (atr_idx == -1)
            return "";


        String attr = mt.substring(atr_idx) + 1;

        String delim = "[/;\"=\n\r\t ]";
        StringTokenizer st = new StringTokenizer(attr, delim);
        String name = st.nextToken();
        try
        {
            if (name.compareToIgnoreCase("charset") == 0)
            {
                String eq = st.nextToken("\"\n\r");
                String val = eq;
                if (st.hasMoreTokens())
                    val = st.nextToken("\"\n\r");

                return javax.mail.internet.MimeUtility.javaCharset(val);
            }
        }
        catch (Exception e)
        {
            System.out.println("Invalid Charset: " + mt);
        }
        return "UTF-8";
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
  

    final Component create_text_renderer()
    {
        JTextArea TXTA_MAIL = new JTextArea();
        TXTA_MAIL.setText(plain_txt);
        TXTA_MAIL.setCaretPosition(0);
        return TXTA_MAIL;
    }

    Component create_lobobrowser_renderer(String html_txt, String charset)
    {
        HtmlPanel htmlPanel = null;
        Reader reader = null;

        //UserMain.errm_ok(my_dlg, html_txt);
        //InputStream in = null;
        try
        {
            Charset cset = null;
            try
            {
                cset = Charset.forName(charset);
            }
            catch (Exception e)
            {
                cset = Charset.defaultCharset();
            }
            //in = new ByteArrayInputStream(html_txt.getBytes(cset));
            String uri = "??";
            SimpleHtmlRendererContext ctx;

            // A Reader should be created with the correct charset,
            // which may be obtained from the Content-Type header
            // of an HTTP response.
            //reader = new InputStreamReader(in);
            reader = new CharArrayReader(html_txt.toCharArray());
         
            // InputSourceImpl constructor with URI recommended
            // so the renderer can resolve page component URLs.
            InputSource is = new InputSourceImpl(reader, uri);
            is.setEncoding(cset.name());
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
            Logger.getLogger("org.lobobrowser").setLevel(Level.SEVERE);

            // Note: This example does not perform incremental
            // rendering while loading the initial document.
            DocumentBuilderImpl builder =
                    new DocumentBuilderImpl(
                    ctx.getUserAgentContext(),
                    ctx);

            Document document = null;
            document = builder.parse(is);
            //in.close();

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
                /*if (in != null)
                {
                    in.close();
                }*/
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
        PN_VIEW.removeAll();

        // FALLBACK TO TEXT IF HTML CANNOT RENDER
        if (new_renderer_pane == null)
        {
            PN_VIEW.add(SCP_PREVIEW);
            new_renderer_pane = create_text_renderer();
            SCP_PREVIEW.setViewportView(new_renderer_pane);
            SCP_PREVIEW.getViewport().setOpaque(false);
        }
        else
        {
        javax.swing.GroupLayout PN_VIEWLayout = new javax.swing.GroupLayout(PN_VIEW);
        PN_VIEW.setLayout(PN_VIEWLayout);
        PN_VIEWLayout.setHorizontalGroup(
            PN_VIEWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(new_renderer_pane, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
        );
        PN_VIEWLayout.setVerticalGroup(
            PN_VIEWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(new_renderer_pane, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
        );
            PN_VIEW.add(new_renderer_pane);
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

        BT_CLOSE = new GlossButton();
        SPL_MAIL = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        SCP_HEADER = new javax.swing.JScrollPane();
        SCP_ATTACHMENT = new javax.swing.JScrollPane();
        PN_VIEW = new javax.swing.JPanel();
        SCP_PREVIEW = new javax.swing.JScrollPane();

        BT_CLOSE.setText(UserMain.getString("Schliessen")); // NOI18N
        BT_CLOSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_CLOSEActionPerformed(evt);
            }
        });

        SPL_MAIL.setDividerLocation(70);
        SPL_MAIL.setDividerSize(2);
        SPL_MAIL.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jSplitPane2.setDividerLocation(400);
        jSplitPane2.setDividerSize(3);
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
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 69, Short.MAX_VALUE)
        );

        SPL_MAIL.setTopComponent(jPanel1);

        javax.swing.GroupLayout PN_VIEWLayout = new javax.swing.GroupLayout(PN_VIEW);
        PN_VIEW.setLayout(PN_VIEWLayout);
        PN_VIEWLayout.setHorizontalGroup(
            PN_VIEWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_PREVIEW, javax.swing.GroupLayout.DEFAULT_SIZE, 630, Short.MAX_VALUE)
        );
        PN_VIEWLayout.setVerticalGroup(
            PN_VIEWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_PREVIEW, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE)
        );

        SPL_MAIL.setBottomComponent(PN_VIEW);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(SPL_MAIL, javax.swing.GroupLayout.DEFAULT_SIZE, 632, Short.MAX_VALUE)
                    .addComponent(BT_CLOSE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SPL_MAIL, javax.swing.GroupLayout.DEFAULT_SIZE, 354, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BT_CLOSE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_CLOSEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_CLOSEActionPerformed
    {//GEN-HEADEREND:event_BT_CLOSEActionPerformed
        // TODO add your handling code here:
        my_dlg.setVisible(false);
    }//GEN-LAST:event_BT_CLOSEActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_CLOSE;
    private javax.swing.JPanel PN_VIEW;
    private javax.swing.JScrollPane SCP_ATTACHMENT;
    private javax.swing.JScrollPane SCP_HEADER;
    private javax.swing.JScrollPane SCP_PREVIEW;
    private javax.swing.JSplitPane SPL_MAIL;
    private javax.swing.JPanel jPanel1;
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

    static File create_temp_file(String uid, int row, String extension)
    {
        File tmp_file = null;
        try
        {
            if (Main.get_bool_prop(Preferences.CACHE_MAILFILES, false))
            {
                tmp_file = new File(Main.get_cache_path(), uid + "_" + row + extension);
                if (tmp_file.exists())
                {
                    return tmp_file;


                }
                tmp_file.deleteOnExit();
            }
            else
            {
                tmp_file = File.createTempFile("dlml", extension, new File("."));
                tmp_file.deleteOnExit();
            }
        }
        catch (IOException iOException)
        {
        }
        return tmp_file;
    }

    static File last_dir = null;
    private void store_attachment( int row )
    {
        MailOpenStorePanel pnl = new MailOpenStorePanel();
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
        if (getDlg() != null)
            dlg.setLocation( getDlg().get_next_location() );
        else
            dlg.setLocation( tb_att.getLocationOnScreen().x + 30, tb_att.getLocationOnScreen().y + 30 + 30*row );

        boolean wants_open = false;

        Part p = msg.get_attachment(row);

        if (NativeLoader.is_win() || NativeLoader.is_osx())
        {
            try
            {
                if (p.getFileName() != null && p.getFileName().length() > 0)
                {
                    dlg.setVisible(true);

                    if (!pnl.isOkay())
                    {
                        return;
                    }
                    wants_open = pnl.wants_open();
                }
            }
            catch (MessagingException messagingException)
            {
            }
        }

        File trg_file = null;
        if (!wants_open)
        {

            FileDialog fd = new FileDialog(my_dlg);
            fd.setMode(FileDialog.SAVE);

            fd.setLocation(my_dlg.getLocationOnScreen().x + 20, my_dlg.getLocationOnScreen().y + 20 );

            String attachment_name = "Attachment.att";
            try
            {
                attachment_name = p.getFileName();
            }
            catch (MessagingException messagingException)
            {
            }

            if (last_dir != null)
            {
                fd.setDirectory(last_dir.getAbsolutePath());
            }
        
            fd.setFile(attachment_name);
          


            fd.setVisible(true);

            String f_name = fd.getFile();
            if (f_name == null)
                return;

            f_name = correct_mac_suffix_bug( f_name, attachment_name );

            trg_file = new File(fd.getDirectory(), f_name );
            
            if (trg_file.exists())
            {
                if (!UserMain.errm_ok_cancel(my_dlg, UserMain.Txt("Do_you_want_to_overwrite_this_file")))
                    return;
            }
        }
        else
        {
            String extension = ".tmp";

            try
            {
                int ext_idx = p.getFileName().lastIndexOf(".");
                if (ext_idx > 0)
                {
                    extension = p.getFileName().substring(ext_idx);
                }
            }
            catch (MessagingException messagingException)
            {
            }
            trg_file =  create_temp_file( uid, row, extension );
            trg_file.deleteOnExit();
        }


        last_dir = trg_file.getParentFile();


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
        if (wants_open)
        {
            String[] cmd = null;
            if (NativeLoader.is_win())
            {
                cmd = new String[3];
                cmd[0] = "cmd";
                cmd[1] = "/c";
                cmd[2] = trg_file.getAbsolutePath();
            }
            if (NativeLoader.is_osx())
            {
                cmd = new String[4];
                cmd[0] = "open";
                cmd[1] = "-a";
                cmd[2] = "Preview";
                cmd[3] = trg_file.getAbsolutePath();
            }
            if (cmd != null)
            {
                CmdExecutor exe = new CmdExecutor(cmd);
                exe.set_no_debug(false);
                exe.exec();
            }

        }
    }
    JComponent get_SPL_MAIL()
    {
        return SPL_MAIL;
    }

    // MAC FILESELECTER DIALOG DELETES SUFFIX IF "HIDE SUFFIXES" IS SELECTED
    private String correct_mac_suffix_bug( String f_name, String attachment_name )
    {
        if (attachment_name.lastIndexOf(".") >= 0  && (f_name.lastIndexOf('.') != attachment_name.lastIndexOf(".")))
        {
            int suffidx = attachment_name.lastIndexOf('.');
            f_name = f_name + attachment_name.substring(suffidx);
        }
        return f_name;
    }
}
