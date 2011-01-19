/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LogPanel.java
 *
 * Created on 19.01.2010, 20:12:55
 */

package dimm.home.Panels.Diagnose;

import dimm.home.Rendering.CustomScrollPane;
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.Rendering.GlossTable;
import dimm.home.ServerConnect.FunctionCallConnect;
import dimm.home.ServerConnect.InStreamID;
import dimm.home.ServerConnect.ServerInputStream;
import dimm.home.UserMain;
import dimm.home.Utilities.SwingWorker;
import home.shared.Utilities.LogConfigEntry;
import home.shared.Utilities.LogListener;
import home.shared.Utilities.ParseToken;
import java.awt.FileDialog;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.RowSorter;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author mw
 */

class LogLine
{
    String date;
    String time;
    String mode;
    String source;
    String msg;
    long offset;

    LogLine( long _offset, String line)
    {
        offset = _offset;
        parse( line );
    }

    void parse( String line )
    {
        date = "";
        time = "";
        mode = "";
        source = "";
        msg = "";
        
        try
        {
            int idx = line.indexOf(' ');
            date = line.substring(0, idx);
            int last_idx = idx + 1;
            idx = line.indexOf(": ", last_idx);
            time = line.substring(last_idx, idx);
            last_idx = idx + 1;
            idx = line.indexOf(':', last_idx);
            mode = line.substring(last_idx, idx).trim();
            last_idx = idx + 1;
            idx = line.indexOf(':', last_idx);
            source = line.substring(last_idx, idx).trim();
            last_idx = idx + 1;
            msg = line.substring(last_idx).trim();
        }
        catch (Exception e)
        {
            date = "";
            time = "";
            mode = "";
            source = "";
            msg = line;
        }
    }

    @Override
    public String toString()
    {
        return date + " " + time + " " + mode + " " + source + " " + msg;
    }
}
class SyncLogLine extends LogLine
{
    SyncLogLine( long _offset, String line)
    {
        super( _offset, line );
    }
//Wed May 02 10:47:17 2007 debug  : Finishing sync job 8

    @Override
    void parse( String line )
    {
        date = "";
        time = "";
        mode = "";
        source = "";
        msg = "";

        try
        {
            int idx = line.indexOf(' ');
            date = line.substring(0, idx);
            int last_idx = idx + 1;
            idx = line.indexOf(" ", last_idx);
            date += " " + line.substring(last_idx, idx);
            last_idx = idx + 1;
            idx = line.indexOf(" ", last_idx);
            date += " " + line.substring(last_idx, idx);
            last_idx = idx + 1;
            idx = line.indexOf(" ", last_idx);
            time = line.substring(last_idx, idx);
            last_idx = idx + 1;
            idx = line.indexOf(" ", last_idx);
            date += " " + line.substring(last_idx, idx);
            last_idx = idx + 1;
            idx = line.indexOf(" ", last_idx);
            date += " " + line.substring(last_idx, idx);
            last_idx = idx + 1;

            idx = line.indexOf(": ", last_idx);
            mode = line.substring(last_idx, idx).trim();
            last_idx = idx + 1;
            source = "Sync";
            msg = line.substring(last_idx);
        }
        catch (Exception e)
        {
            date = "";
            time = "";
            mode = "";
            source = "";
            msg = line;
        }
    }

}
class LogModel extends AbstractTableModel
{
    ArrayList<LogLine> line_list;
    LogPanel panel;


    String[] col_names = {UserMain.getString("Date"), UserMain.getString("Time"), UserMain.getString("Level"), UserMain.getString("Mode"), UserMain.getString("Message")};
    Class[] col_classes = {String.class, String.class, String.class,  String.class,  String.class};


    public LogModel( ArrayList<LogLine> line_list, LogPanel panel )
    {
        this.line_list = line_list;
        this.panel = panel;
    }

    @Override
    public String getColumnName(int column)
    {
        return col_names[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        return col_classes[columnIndex];
    }


    @Override
    public int getRowCount()
    {
        return line_list.size();
    }

    @Override
    public int getColumnCount()
    {
        return 5;
    }

    @Override
    public Object getValueAt( int rowIndex, int columnIndex )
    {
        if (rowIndex * 100 / getRowCount() > 90)
            panel.lazy_load();

        if (rowIndex >=getRowCount())
            return "";

        LogLine line = line_list.get(rowIndex);

        switch (columnIndex)
        {
            case 0: return line.date;
            case 1: return line.time;
            case 2: return line.mode;
            case 3: return line.source;
            case 4: return line.msg;
        }
        return "";
    }

}

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

public class LogPanel extends GlossDialogPanel  implements MouseListener, ActionListener
{
    //long offset;
    boolean end_was_reached;


    //static final long LINES_PER_CALL = 1000;
    static final int BYTE_PER_CALL = 10240;
    GlossTable log_table;


    CustomScrollPane custom_scroll_pane;

    private Timer timer;
    String fixed_log_type;
    boolean logdump_active = false;

    ArrayList<LogConfigEntry> config_list;
    private long log_file_size;
    
    private long last_fetched_offset;

    LogModel model;


    public LogPanel()
    {
        fixed_log_type = null;

        this.end_was_reached = false;
       // this.offset = 0;


        initComponents();
        
        do_inits();
    }

    @Override
    public void setDlg( GenericGlossyDlg dlg )
    {
        super.setDlg(dlg);
        my_dlg.setModal(false);
    }


    public void set_log_type( String log_type)
    {
        fixed_log_type = log_type;
    }

    public void add_log_entry( String name, String cmd)
    {
        CB_LOG_SOURCE.addItem( new LogEntry(name, cmd));
    }



    boolean inside_init = false;

    private void do_inits()
    {
        inside_init = true;

        log_table = new GlossTable();

        log_table.embed_to_scrollpanel(this.jScrollPane1);
        
        config_list = read_config_list();


        CB_LOG_SOURCE.removeAllItems();

        for (int i = 0; i < config_list.size(); i++)
        {
            LogConfigEntry tck = config_list.get(i);
            add_log_entry( UserMain.Txt( tck.typ ), tck.typ );
        }
       
        add_log_entry("BackupServer",  LogListener.SYNC );

        inside_init = false;

        fetch_first_block();

        timer = new Timer(2000, this);
        timer.start();
    }

    private ArrayList<LogConfigEntry> read_config_list()
    {
        FunctionCallConnect fcc = UserMain.fcc();

        String ret = fcc.call_abstract_function("show_log CMD:get_config" , FunctionCallConnect.SHORT_TIMEOUT );        

        if (ret != null && fcc.get_last_err_code() == 0)
        {
            if (ret.charAt(0) == '0')
            {
                ParseToken pt = new ParseToken(ret.substring(3));
                Object o = pt.GetCompressedObject("CFG:");
                if (o instanceof ArrayList)
                {
                     return (ArrayList<LogConfigEntry>)o;
                }
            }
        }
        return null;
    }

    String get_log_type()
    {
        if (fixed_log_type != null)
            return fixed_log_type;

        if ( CB_LOG_SOURCE.getItemCount() == 0)
            return null;

        String log_type = "L4J";
        if (CB_LOG_SOURCE != null && CB_LOG_SOURCE.getSelectedItem() != null)
        {
            log_type = ((LogEntry)CB_LOG_SOURCE.getSelectedItem()).get_cmd();
        }
        else 
        {
            log_type = ((LogEntry) CB_LOG_SOURCE.getItemAt(0)).get_cmd();
        }

        return log_type;
    }

    boolean read_status()
    {
        String log_type = get_log_type();

        if ( log_type == null)
            return false;

        FunctionCallConnect fcc = UserMain.fcc();

        String ret = fcc.call_abstract_function("show_log CMD:read_status LG:" + log_type, FunctionCallConnect.MEDIUM_TIMEOUT );

        if (ret == null)
            return false;
        if (ret.charAt(0) != '0')
            return false;

        ParseToken pt = new ParseToken(ret.substring(3));

        log_file_size = pt.GetLongValue("SI:");
       

        return true;
    }

    String read_next_block( long offset, int size)
    {
        if (end_was_reached)
            return null;

        String log_type = get_log_type();

        if ( log_type == null)
            return null;

        FunctionCallConnect fcc = UserMain.fcc();


        String ret = fcc.call_abstract_function("show_log CMD:read_block LG:" + log_type + " SI:" + size + " OF:" + offset , FunctionCallConnect.MEDIUM_TIMEOUT );


        if (ret == null )
        {
            return null;
           }
        if (ret.charAt(0) != '0')
            {
            return null;
            }
        ParseToken pt = new ParseToken(ret.substring(3));
        log_file_size = pt.GetLongValue("SI:");
        

        String xml = pt.GetString("SB:");
        Object o = ParseToken.DeCompressObject(xml);
        if (o instanceof String)
        {
            String sb = (String) o;
            return sb;
        }
        return null;
    }
    
    // REVERSE THE LINES OF THE LOG
    ArrayList<LogLine> parse_line_list( long offset, String data, boolean is_sync)
    {
        ArrayList<LogLine> line_list = new ArrayList<LogLine>();

        if (data == null)
            return line_list;

        
        int last_line_start = 0;
        for( int i = 0; i < data.length(); i++)
        {
            char ch = data.charAt(i);
            if (ch == '\n')
            {
                if (i - last_line_start > 0)
                {
                    String s = data.substring(last_line_start, i);
                    LogLine line = null;
                    if (!is_sync)
                        line = new LogLine(offset + last_line_start, s);
                    else
                        line = new SyncLogLine(offset + last_line_start, s);
                    line_list.add(0, line);
                }
                last_line_start = i + 1;
            }
        }
        if (last_line_start < data.length())
        {
            String s = data.substring(last_line_start);
            LogLine line = null;
            if (!is_sync)
                line = new LogLine(offset + last_line_start, s);
            else
                line = new SyncLogLine(offset + last_line_start, s);

            line_list.add(0, line);
        }

        return line_list;
    }
    boolean is_sync()
    {
        return get_log_type().compareTo( LogListener.SYNC) == 0;
    }


    boolean fetch_first_block()
    {
        if (inside_init)
            return false;
        
        ArrayList<LogLine> line_list = new ArrayList<LogLine>();
        end_was_reached = false;

        if (read_status())
        {


            long loffset = log_file_size - BYTE_PER_CALL;
            if (loffset < 0)
                loffset = 0;
            String data = read_next_block( loffset, BYTE_PER_CALL );

            

            line_list = parse_line_list( loffset, data, is_sync() );
        }


        //RowSorter sorter = new TableRowSorter(model);

        model = new LogModel(line_list, this);
        log_table.setModel(model);
        //log_table.setRowSorter(sorter);

        log_table.setShowGrid(false);
        log_table.setSelectionMode(DefaultListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        
        log_table.getColumnModel().getColumn(0).setMaxWidth(70);
        log_table.getColumnModel().getColumn(1).setMaxWidth(70);
        log_table.getColumnModel().getColumn(2).setMaxWidth(60);
        log_table.getColumnModel().getColumn(3).setMaxWidth(60);
        log_table.getColumnModel().getColumn(4).setPreferredWidth(180);
        
        last_fetched_offset =  log_file_size;

        return true;
    }

    boolean check_update_log()
    {
        long last_log_file_size = log_file_size;
        if (!read_status())
            return false;

        return (last_log_file_size != log_file_size);
    }


    boolean in_lazy_load = false;
    boolean lazy_load()
    {
        if (in_lazy_load)
            return false;

        if (end_was_reached)
            return true;

        in_lazy_load = true;

        try
        {
            int byte_to_fetch = BYTE_PER_CALL;
            long loffset = model.line_list.get(model.getRowCount() - 1).offset  - byte_to_fetch;
            if (loffset < 0)
            {
                loffset = 0;
                byte_to_fetch = (int) model.line_list.get(model.getRowCount() - 1).offset;
            }

            System.out.println("Fetching " + byte_to_fetch + " byte from " + loffset);
            String data = read_next_block(loffset, byte_to_fetch);


            // PARSE TO LINELIST
            ArrayList<LogLine> line_list = parse_line_list( loffset, data, is_sync());

            merge_to_log_list(line_list);

            if (loffset == 0)
            {
                this.end_was_reached = true;
            }
        }
        catch (Exception e)
        {
        }

        in_lazy_load = false;

        return true;

    }

    void merge_to_log_list(ArrayList<LogLine> line_list )
    {
        // AND MERGE THE LAST ENTRY TO THE FIRST ENTRY OF THIS BLOCK
        ArrayList<LogLine> model_line_list = model.line_list;
        LogLine last_line = model_line_list.get( model_line_list.size() - 1);
        LogLine first_line = model_line_list.get( 0 );
        LogLine new_first_line =  line_list.get(0);
        LogLine new_last_line = line_list.get( line_list.size() - 1);

        // IS THE NEW LOGDATA NEWER THAN THE EXISTING DATA
        if (new_last_line.offset >= first_line.offset)
        {
            if (first_line.offset + first_line.toString().length() == new_last_line.offset)
            {
                String line = first_line.toString() + new_last_line.toString();
                first_line.parse( line );                
            }
            if (new_last_line.offset == first_line.offset)
                line_list.remove(new_last_line);

            // INSART AT BEGINNING
            for (int i = line_list.size() - 1; i >= 0; i--)
            {
                LogLine logLine = line_list.get(i);
                model_line_list.add(0, logLine);
            }
        }
        // IS THE NEW LOGDATA OLDER THAN THE EXISTING DATA
        else if(new_first_line.offset <= last_line.offset)
        {
            if (new_first_line.offset + new_first_line.toString().length() == last_line.offset)
            {
                String line = new_first_line.toString() + last_line.toString();
                last_line.parse( line );
                
            }
            if (new_first_line.offset == last_line.offset)
                line_list.remove(new_first_line);

            // APPEND TO LIST
            for (int i = 0; i <  line_list.size(); i++)
            {
                LogLine logLine = line_list.get(i);
                model_line_list.add( logLine);
            }
        }
        else
        {
            // MERGE INTO FIRST FOUND 
            for (int i = 0; i < model_line_list.size(); i++)
            {
                LogLine logLine = model_line_list.get(i);
                if (logLine.offset < new_first_line.offset)
                {
                    for (int l = line_list.size() - 1; l >= 0; l--)
                    {
                        LogLine new_ll = line_list.get(l);
                        model_line_list.add( i, new_ll);
                    }
                    break;
                }

            }
        }

        model.fireTableDataChanged();

    }
    boolean update_log()
    {
        long missing_length = log_file_size - last_fetched_offset;

        // TOO MUCH DATA MISSING, WE START FROM SCRATCH
        if (missing_length > BYTE_PER_CALL)
        {
            return fetch_first_block();
        }

        // READ NEW APPENDED DATA
        String data = read_next_block( last_fetched_offset, (int)missing_length );

        long loffset = last_fetched_offset;

        // PARSE TO LINELIST
        ArrayList<LogLine> line_list = parse_line_list( loffset, data, is_sync() );

        merge_to_log_list( line_list );

        return true;
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
        jScrollPane1 = new javax.swing.JScrollPane();
        BT_OK = new GlossButton();
        jLabel1 = new javax.swing.JLabel();
        BT_DUMP = new GlossButton();
        BT_FILTER = new GlossButton();
        BT_HELP1 = new GlossButton();

        CB_LOG_SOURCE.setMaximumRowCount(20);
        CB_LOG_SOURCE.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "default" }));
        CB_LOG_SOURCE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_LOG_SOURCEActionPerformed(evt);
            }
        });
        CB_LOG_SOURCE.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                CB_LOG_SOURCEPropertyChange(evt);
            }
        });

        LOG_PANEL.setBackground(new java.awt.Color(0, 0, 0));
        LOG_PANEL.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        LOG_PANEL.setMinimumSize(new java.awt.Dimension(600, 200));
        LOG_PANEL.setPreferredSize(new java.awt.Dimension(600, 200));

        jScrollPane1.setOpaque(false);

        javax.swing.GroupLayout LOG_PANELLayout = new javax.swing.GroupLayout(LOG_PANEL);
        LOG_PANEL.setLayout(LOG_PANELLayout);
        LOG_PANELLayout.setHorizontalGroup(
            LOG_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 637, Short.MAX_VALUE)
        );
        LOG_PANELLayout.setVerticalGroup(
            LOG_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE)
        );

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

        BT_FILTER.setText(UserMain.Txt("Filter")); // NOI18N
        BT_FILTER.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_FILTERActionPerformed(evt);
            }
        });

        BT_HELP1.setText(UserMain.Txt("?")); // NOI18N
        BT_HELP1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_HELP1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(LOG_PANEL, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 641, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(CB_LOG_SOURCE, javax.swing.GroupLayout.PREFERRED_SIZE, 199, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_DUMP)
                        .addGap(18, 18, 18)
                        .addComponent(BT_FILTER)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 145, Short.MAX_VALUE)
                        .addComponent(BT_HELP1)
                        .addGap(18, 18, 18)
                        .addComponent(BT_OK)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(LOG_PANEL, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OK)
                    .addComponent(jLabel1)
                    .addComponent(CB_LOG_SOURCE, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_DUMP)
                    .addComponent(BT_FILTER)
                    .addComponent(BT_HELP1))
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

    private void CB_LOG_SOURCEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_LOG_SOURCEActionPerformed
    {//GEN-HEADEREND:event_CB_LOG_SOURCEActionPerformed
        // TODO add your handling code here:
                // SET NEW TEXT

        fetch_first_block();


    }//GEN-LAST:event_CB_LOG_SOURCEActionPerformed

    private void BT_FILTERActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_FILTERActionPerformed
    {//GEN-HEADEREND:event_BT_FILTERActionPerformed
        // TODO add your handling code here:
        LogFilterPanel pnl = new LogFilterPanel();
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
        dlg.set_next_location(my_dlg);
        dlg.setVisible(true);


    }//GEN-LAST:event_BT_FILTERActionPerformed

    private void CB_LOG_SOURCEPropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_CB_LOG_SOURCEPropertyChange
    {//GEN-HEADEREND:event_CB_LOG_SOURCEPropertyChange
        // TODO add your handling code here:
        fetch_first_block();
    }//GEN-LAST:event_CB_LOG_SOURCEPropertyChange

    private void BT_HELP1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_HELP1ActionPerformed
    {//GEN-HEADEREND:event_BT_HELP1ActionPerformed
        // TODO add your handling code here:
        UserMain.open_help_panel(this.getClass().getSimpleName());
}//GEN-LAST:event_BT_HELP1ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_DUMP;
    private javax.swing.JButton BT_FILTER;
    private javax.swing.JButton BT_HELP;
    private javax.swing.JButton BT_HELP1;
    private javax.swing.JButton BT_OK;
    private javax.swing.JComboBox CB_LOG_SOURCE;
    private javax.swing.JPanel LOG_PANEL;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
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
            if (mouseEvent.getComponent() == log_table)
            {
                JPopupMenu pop = new JPopupMenu();
               /* pop.add( new AbstractAction("Copy")
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

                pop.show(mouseEvent.getComponent(), mouseEvent.getX(), mouseEvent.getY() );*/
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

        if (check_update_log())
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
