/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LogPanel.java
 *
 * Created on 19.01.2010, 20:12:55
 */

package dimm.home.Panels;

import dimm.home.Rendering.CustomScrollPane;
import dimm.home.Rendering.CustomScrollPaneDialog;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.ServerConnect.FunctionCallConnect;
import dimm.home.ServerConnect.InStreamID;
import dimm.home.ServerConnect.ServerInputStream;
import dimm.home.UserMain;
import dimm.home.Utilities.SwingWorker;
import home.shared.Utilities.ParseToken;
import java.awt.Color;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.Timer;

/**
 *
 * @author mw
 */

class LogEntry
{
    String name;
    String cmd;

    LogEntry( String name, String cmd )
    {
        this.name = name;
        this.cmd = cmd;
    }
    @Override
    public String toString()
    {
        return name;
    }
    public String get_cmd()
    {
        return cmd;
    }
}

class TightTextArea extends JTextArea
{
    int h;
    public TightTextArea(int _h)
    {
        super();
        h = _h;
    }
    @Override
    protected int getRowHeight()
    {
        return h;
    }
}

public class LogPanel extends GlossDialogPanel  implements MouseListener, ActionListener, CustomScrollPaneDialog
{
    long offset;
    boolean end_was_reached;

    static final long LINES_PER_CALL = 1000;
    JTextArea LOG_TEXT;


    CustomScrollPane custom_scroll_pane;

    private Timer timer;
    String fixed_log_type;
    boolean logdump_active = false;


    public LogPanel()
    {
        fixed_log_type = null;


        this.end_was_reached = false;
        this.offset = 0;

        initComponents();


        CB_LOG_SOURCE.removeAllItems();

        do_inits();
    }

    public void set_log_type( String log_type)
    {
        fixed_log_type = log_type;
    }

    public void add_log_entry( String name, String cmd)
    {
        CB_LOG_SOURCE.addItem( new LogEntry(name, cmd));
    }



    private void do_inits()
    {
        LOG_TEXT = new TightTextArea/*JTextArea*/(7);
        LOG_TEXT.setEditable(false);
        LOG_TEXT.setTabSize(4);
        LOG_TEXT.setAutoscrolls(false);
        LOG_TEXT.setFont( new Font("Courier", Font.PLAIN, 11 ) );
        LOG_TEXT.setForeground( new Color( 255, 128, 0));  // AMBER
        LOG_TEXT.setBackground(Color.black);

        custom_scroll_pane = new CustomScrollPane( this, LOG_TEXT );
        LOG_PANEL.add(custom_scroll_pane);


        reset_text_pos();

        set_text(read_next_block());

        LOG_TEXT.addMouseListener(this);        

        timer = new Timer(2000, this);
        timer.start();
    }

    public void CB_LOG_SOURCEActionPerformed(java.awt.event.ActionEvent evt)
    {
        // SET NEW TEXT
        reset_text_pos();

        set_text( read_next_block() );

        custom_scroll_pane.reset_scroll();
    }


    void reset_text_pos()
    {
        offset = 0;
        end_was_reached = false;
        LOG_TEXT.setText(null);
    }
    boolean inside_add_text = false;
    @Override
    public void add_text()
    {
        if (inside_add_text)
            return;

        inside_add_text = true;
        append_text(read_next_block());
        inside_add_text = false;
    }

    void append_text( String text )
    {
        if (text == null)
            return;

        LOG_TEXT.append(text );

        custom_scroll_pane.doLayout();
    }
    void set_text( String text )
    {
        LOG_TEXT.setText(text );

    }

    String get_log_type()
    {
        if (fixed_log_type != null)
            return fixed_log_type;

        String log_type = "L4J";
        if (CB_LOG_SOURCE != null && CB_LOG_SOURCE.getSelectedItem() != null)
        {
            log_type = ((LogEntry)CB_LOG_SOURCE.getSelectedItem()).get_cmd();
        }



        return log_type;
    }

    String read_next_block()
    {
        if (end_was_reached)
            return null;

        String log_type = get_log_type();

        if ( log_type == null)
            return null;

        FunctionCallConnect fcc = UserMain.fcc();


        String ret = fcc.call_abstract_function("show_log CMD:read LG:" + log_type + " LI:" + LINES_PER_CALL + " OF:" + offset , FunctionCallConnect.MEDIUM_TIMEOUT );


        if (ret != null )
        {
            if (ret.charAt(0) == '0')
            {
                ret = ret.substring(3);
                offset += ret.length() + 1;
                return ret;
            }
            if (ret.startsWith("42:"))
                            end_was_reached = true;
         /*   else
            {
                //LOG_TEXT.setText(ret);
            }*/

        }
        return null;
    }

    String read_nlines_block( long lines, long loffset)
    {
        if (end_was_reached)
            return null;
        String log_type = get_log_type();

        if ( log_type == null)
            return null;

        FunctionCallConnect fcc = UserMain.fcc();


        String ret = fcc.call_abstract_function("show_log CMD:read LG:" + log_type + " LI:" + lines + " OF:" + loffset , FunctionCallConnect.MEDIUM_TIMEOUT );

        if (ret != null)
        {
            if (ret.charAt(0) == '0')
            {
                ret = ret.substring(3);
                offset += ret.length() + 1;
                return ret;
            }
            else
            {
                if (ret.startsWith("42:"))
                            end_was_reached = true;
/*                else
                    LOG_TEXT.setText(ret);*/
            }

        }
        else
        {
            offset = 0;
        }
        return null;
    }

    int update_log()
    {
        
        boolean found_last_top = false;
        int insert_index = 0;
        int line_len = LOG_TEXT.getText().indexOf('\n');
        if (line_len < 0)
            line_len = LOG_TEXT.getText().length();

        String last_top = LOG_TEXT.getText().substring(0, line_len ).trim();


        if (line_len == 0)
            return 0;

        long get_line_cnt = 5;
        while(!found_last_top)
        {
            // UPPER LIMIT
            if (get_line_cnt > LINES_PER_CALL)
                break; 

            // GET LINES UNTIL WE REACH LAST TOP
            String new_top = read_nlines_block( get_line_cnt, 0);
            if (new_top == null || new_top.length() == 0)
                break;

            // IS LAST LINE IDENTICAL TO THIS LINE?
            java.util.StringTokenizer stok = new java.util.StringTokenizer( new_top, "\n\r" );
            if ( stok.hasMoreElements() )
            {
                if (last_top.compareTo( stok.nextToken() ) == 0)
                    return 0;
            }

            get_line_cnt += 250;
            

            stok = new java.util.StringTokenizer( new_top, "\n\r" );

            ArrayList<String> new_lines = new ArrayList<String>();

            while( stok.hasMoreElements() )
            {
                String tk = stok.nextToken().trim();
                new_lines.add(tk);
                if (last_top.compareTo( tk ) == 0)
                {

                    found_last_top = true;
                    for (int i = 0; i < new_lines.size(); i++)
                    {
                        String string = new_lines.get(i);
                        LOG_TEXT.insert( string, insert_index );
                        insert_index += string.length();
                        LOG_TEXT.insert( "\n", insert_index );
                        insert_index ++;
                    }

                    break;
                }
            }
        }
        if (found_last_top)
            return 0;

        return 1;
    }
    void get_log_dump( final File file )
    {
        if (logdump_active)
            return;
        logdump_active = true;


        SwingWorker log_sw = new SwingWorker() {

            @Override
            public Object construct()
            {
                UserMain.self.show_busy(my_dlg, UserMain.Txt("Creating_Logdump..."));

                run_get_log_dump( file );

                UserMain.self.hide_busy();

                logdump_active = false;

                return null;
            }

            @Override
            public boolean finished()
            {
                logdump_active = false;
                return super.finished();
            }

        };

        log_sw.start();
    }

    void run_get_log_dump( File file )
    {

        BufferedOutputStream baos = null;
        ServerInputStream sis = null;


        try
        {
            FileOutputStream fos = new FileOutputStream(file);
            baos = new BufferedOutputStream(fos);


            FunctionCallConnect fcc = UserMain.fcc();
            String ret = fcc.call_abstract_function("dump_log MA:" + UserMain.sqc().get_act_mandant_id(), FunctionCallConnect.LONG_TIMEOUT);
            if (ret == null || ret.charAt(0) != '0')
            {
                UserMain.errm_ok(my_dlg, "DumpMail gave error " + ret);
                return;
            }
            UserMain.self.show_busy(my_dlg, UserMain.Txt("Downloading_Logdump..."));

            String[] l = ret.split(" ");
            String instream_id = l[1];
            ParseToken pt = new ParseToken(l[2]);
            long len = pt.GetLong("LEN:");

            InStreamID id = new InStreamID(instream_id, len);

            sis = new ServerInputStream(fcc.get_sqc(), id);
            sis.read(baos);

            baos.close();
            baos = null;

            sis.close();
            sis = null;
        }
        catch (IOException iOException)
        {
            UserMain.errm_ok(my_dlg, "Error while reading DumpMail: " + iOException.getLocalizedMessage());
        }
        finally
        {
            try
            {
                if (baos != null)
                {
                    baos.close();

                }
                if (sis != null)
                {
                    sis.close();

                }
            }
            catch (IOException iOException)
            {
            }
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

        CB_LOG_SOURCE = new javax.swing.JComboBox();
        LOG_PANEL = new javax.swing.JPanel();
        BT_OK = new GlossButton();
        jLabel1 = new javax.swing.JLabel();
        BT_DUMP = new GlossButton();

        CB_LOG_SOURCE.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "default" }));

        LOG_PANEL.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        LOG_PANEL.setMinimumSize(new java.awt.Dimension(600, 200));
        LOG_PANEL.setPreferredSize(new java.awt.Dimension(600, 200));
        LOG_PANEL.setLayout(new java.awt.GridLayout(1, 0));

        BT_OK.setText(UserMain.getString("OK")); // NOI18N
        BT_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKActionPerformed(evt);
            }
        });

        jLabel1.setText("Log");

        BT_DUMP.setText(UserMain.getString("Log_Dump")); // NOI18N
        BT_DUMP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_DUMPActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(LOG_PANEL, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(CB_LOG_SOURCE, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_DUMP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 247, Short.MAX_VALUE)
                        .addComponent(BT_OK)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LOG_PANEL, javax.swing.GroupLayout.DEFAULT_SIZE, 295, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OK)
                    .addComponent(jLabel1)
                    .addComponent(CB_LOG_SOURCE, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_DUMP))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKActionPerformed
    {//GEN-HEADEREND:event_BT_OKActionPerformed

        setVisible(false);
}//GEN-LAST:event_BT_OKActionPerformed

    static File last_dir;
    static File last_file;

    private void BT_DUMPActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_DUMPActionPerformed
    {//GEN-HEADEREND:event_BT_DUMPActionPerformed
        // TODO add your handling code here:
        FileDialog fd = new FileDialog(my_dlg);
        fd.setMode(FileDialog.SAVE);

        fd.setLocation(my_dlg.getLocationOnScreen().x + 20, my_dlg.getLocationOnScreen().y + 20 );


        if (last_dir != null)
        {
            fd.setDirectory(last_dir.getAbsolutePath());
        }
        if (last_file != null)
        {
            fd.setFile(last_file.getName());
        }
        else
        {
            fd.setFile("logdump.zip");
        }

        fd.setVisible(true);

        String f_name = fd.getFile();
        if (f_name == null)
            return;

        last_file = new File(fd.getDirectory(), f_name );

        last_dir = last_file.getParentFile();

        get_log_dump( last_file );

    }//GEN-LAST:event_BT_DUMPActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_DUMP;
    private javax.swing.JButton BT_OK;
    private javax.swing.JComboBox CB_LOG_SOURCE;
    private javax.swing.JPanel LOG_PANEL;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables

    @Override
    public JButton get_default_button()
    {
        return BT_OK;
    }

    /** Closes the dialog */
    @Override
    public void mouseExited(java.awt.event.MouseEvent mouseEvent)
    {
    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent mouseEvent)
    {
    }

    @Override
    public void mousePressed(java.awt.event.MouseEvent mouseEvent)
    {
    }

    @Override
    public void mouseClicked(java.awt.event.MouseEvent mouseEvent)
    {
        if ((mouseEvent.getModifiers() & MouseEvent.BUTTON3_MASK) == MouseEvent.BUTTON3_MASK)
        {
            if (mouseEvent.getComponent() == LOG_TEXT)
            {
                JPopupMenu pop = new JPopupMenu();
                pop.add( new AbstractAction("Copy")
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();

                        StringSelection contents = new StringSelection(LOG_TEXT.getSelectedText());
                        cb.setContents(contents, null);
                    }
                });
                pop.add( new JSeparator() );

                pop.add( new AbstractAction("Select All")
                {
                    @Override
                    public void actionPerformed(ActionEvent e)
                    {
                        LOG_TEXT.selectAll();
                    }
                });

                pop.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY() );
            }
        }

    }

    @Override
    public void mouseEntered(java.awt.event.MouseEvent mouseEvent)
    {
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent)
    {
        if (logdump_active)
            return;

        timer.stop();
        update_log();
        timer.restart();
    }

    @Override
    public void deactivate()
    {
        super.deactivate();
        timer.stop();
    }





}
