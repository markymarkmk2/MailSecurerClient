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

import com.thoughtworks.xstream.XStream;
import dimm.home.Panels.RoleFilter;
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.Rendering.GlossTable;
import dimm.home.ServerConnect.FunctionCallConnect;
import dimm.home.ServerConnect.InStreamID;
import dimm.home.ServerConnect.ServerInputStream;
import dimm.home.UserMain;
import dimm.home.Utilities.ParseToken;
import dimm.home.Utilities.SizeStr;
import dimm.home.Utilities.SwingWorker;
import home.shared.CS_Constants;
import home.shared.mail.RFCMimeMail;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.ListSelectionModel;
import javax.swing.table.AbstractTableModel;


class MailPreviewDlg extends GenericGlossyDlg
{

    UserMain main;
    MailPreviewDlg( UserMain parent, RFCMimeMail mail)
    {
        super( parent, true, new MailPreviewPanel(mail));
        main = parent;
        if (parent.isVisible())
            this.setLocation(parent.getLocationOnScreen().x + 30, parent.getLocationOnScreen().y + 30);
        else
            this.setLocationRelativeTo(null);

        this.setSize( 700, 600);
    }
}


class MBoxFilterOutputStream extends OutputStream
{
    byte[] FROM = {0,'F', 'r', 'o', 'm', ' ' };
    OutputStream os;

    public MBoxFilterOutputStream( OutputStream os)
    {
        this.os = os;
    }

    @Override
    public void write( byte[] b ) throws IOException
    {
        this.write(b, 0, b.length);
    }

    boolean detect_mode = false;
    int detect_cnt = 0;
    @Override
    public void write( byte[] b, int off, int len ) throws IOException
    {
        for( int i = off; i < len && detect_mode; i++)
        {
            this.write(b[i]);
            off++;
        }
        
        int act_idx = 0;
        for (act_idx = off; act_idx < len; act_idx++)
        {
            // DETECT NL
            if (b[act_idx] == '\n' || b[act_idx] == '\r')
            {
                // SAVE IT
                FROM[0] = b[act_idx];
                break;
            }
        }
        // WRITE CLEAN STUFF
        os.write(b, off, act_idx - off);

        // WRITE REST IF DETECTED TO SINGLE BYTE FUNC
        if (act_idx < len)
        {
            detect_mode = true;
            detect_cnt = 0;
            for( int i = act_idx; i < len; i++)
                this.write(b[i]);
        }

    }

    @Override
    public void write( int b ) throws IOException
    {
        if (!detect_mode)
        {
            // DETECT NL
            if (b == '\n' || b == '\r')
            {
                // SAVE IT
                FROM[0] = (byte)b;
                detect_mode = true;
                detect_cnt = 0;
            }
        }
        // WE ARE IN PROGRESS OF DETECTION
        if (b == FROM[detect_cnt])
        {
            if (detect_cnt == FROM.length - 1)
            {
                os.write(FROM[0]);
                os.write((byte)'>');
                os.write(FROM, 1, FROM.length - 1);
                detect_mode = false;
                detect_cnt = 0;
                return;
            }
            detect_cnt++;
        }
        else
        {
            // OKAY; REST OF FAILED DETECTION TO STREAM ( Fro, F, "From" )
            if (detect_mode)
            {
                os.write(FROM, 0, detect_cnt);
                detect_mode = false;
                detect_cnt = 0;
            }
            os.write(b);
        }
    }

    @Override
    public void flush() throws IOException
    {
        super.flush();
    }

    @Override
    public void close() throws IOException
    {

        flush();
        os.close();
        super.close();
    }

    void write_direct( String string ) throws IOException
    {
        os.write(string.getBytes());
    }



}
class FieldComboEntry
{
    String field;

    public FieldComboEntry( String field )
    {
        this.field = field;
    }

    @Override
    public String toString()
    {
        return UserMain.Txt(field);
    }

    public String getField()
    {
        return field;
    }
    
}
class MailTableModel extends AbstractTableModel
{
    static int SUBJECT_COL = 2;
    
    MailViewPanel pnl;
    ArrayList<ArrayList<String>> result_array;
    ArrayList<String> field_list;

    SimpleDateFormat sdf;
    JButton ic_attachment;
    JButton ic_no_attachment;

    MailTableModel(MailViewPanel _pnl,  ArrayList<ArrayList<String>> ret_arr)
    {
        super();

        pnl = _pnl;
        result_array = ret_arr;

        sdf = new SimpleDateFormat("dd.MM.yyyy  HH:mm");

        ic_attachment = create_table_button("/dimm/home/images/ic_attachment.png");
        ic_no_attachment = create_table_button(null);

        field_list = new ArrayList<String>();

        field_list.add(CS_Constants.FLD_DATE);
        field_list.add(CS_Constants.FLD_HAS_ATTACHMENT);
        field_list.add(CS_Constants.FLD_SUBJECT);
        field_list.add(CS_Constants.FLD_SIZE);
    }

    @Override
    public boolean isCellEditable( int row, int column )
    {
        return false;
    }

    ArrayList<String> get_field_list()
    {

        return field_list;
    }

    @Override
    public String getColumnName(int column)
    {
        switch( column )
        {
            case 0: return UserMain.Txt("Date");
            case 2: return UserMain.Txt("Subject");
            case 3: return UserMain.Txt("Size");
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        if (columnIndex == 1)
            return JButton.class;

        return String.class;
    }

    @Override
    public int getRowCount()
    {
        if (result_array == null)
            return 0;
        return result_array.size();
    }

    @Override
    public int getColumnCount()
    {
        if (field_list == null)
            return 0;
        return field_list.size();
    }

    public JButton create_table_button(String rsrc)
    {
        JButton bt;
        if (rsrc != null)
        {
            ImageIcon icn = new ImageIcon(this.getClass().getResource(rsrc));
            bt = new JButton(icn);
        }
        else
        {
            bt = new JButton("");
        }
        //bt.addMouseListener(dlg);
        bt.setBorderPainted(false);
        bt.setOpaque(false);
        bt.setMargin(new Insets(0, 0, 0, 0));
        bt.setContentAreaFilled(false);

        return bt;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        String val = result_array.get(rowIndex).get(columnIndex);
        if ( columnIndex == 0)
        {
            long time = Long.parseLong(val, 16);
            Date d = new Date(time);
            return sdf.format(d);
        }
        else if (columnIndex == 1)
        {
            if (val != null && val.length() > 0 && val.charAt(0) == '1')
                return ic_attachment;

            return ic_no_attachment;
        }
        else if (columnIndex == 3)
        {
            long size = Long.parseLong(val, 16);
            SizeStr str = new SizeStr(size);
            return str.toString();
        }
        return val;
    }
}
/**
 *
 * @author mw
 */
public class MailViewPanel extends GlossDialogPanel implements MouseListener
{
    String search_id;
    GlossTable table;
    MailTableModel model;

    /** Creates new form MailViewPanel */
    public MailViewPanel()
    {
        initComponents();

        table = new GlossTable();
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        model = new MailTableModel(this, null);
        table.setModel(model);
        table.addMouseListener(this);

        // REGISTER TABLE TO SCROLLPANEL
        table.embed_to_scrollpanel( SCP_TABLE );

        CB_FIELD.removeAllItems();
        CB_FIELD.addItem( new FieldComboEntry(CS_Constants.FLD_BODY));
        CB_FIELD.addItem( new FieldComboEntry(CS_Constants.FLD_ATTACHMENT));
        CB_FIELD.addItem( new FieldComboEntry(CS_Constants.FLD_SUBJECT));
        

        CB_FIELD.setSelectedIndex(0);
       
    }

    int get_entries()
    {
        int entries = 5;
        try
        {
            int idx = CB_ENTRIES.getSelectedIndex();
            if (idx == -1)
            {
                idx = 0;
            }
            entries = Integer.parseInt(CB_ENTRIES.getItemAt(idx).toString());
        }
        catch (NumberFormatException numberFormatException)
        {
        }

        return entries;
    }
    void search_mail()
    {


        String mail =  TXT_MAIL.getText();
        String search_val = TXT_SEARCH.getText();
        FieldComboEntry fld_entry= (FieldComboEntry)CB_FIELD.getSelectedItem();
        String field_name = fld_entry.getField();

        int entries = get_entries();
        int mandant = UserMain.self.get_act_mandant().getId();

        String cmd = "SearchMail CMD:open MA:" + mandant + " EM:'" + mail + "' FL:'" + field_name + "' VL:'" + search_val + "' CNT:'" + entries + "' ";
 

        fill_model_with_search( cmd );
    }

    void fill_model_with_search( String cmd )
    {
        FunctionCallConnect fcc = UserMain.fcc();
        int mandant = UserMain.self.get_act_mandant().getId();
 
        if (search_id != null)
        {
            // CLOSE EXISTING CALL;

            fcc.call_abstract_function("SearchMail CMD:close MA:" + mandant + " ID:" + search_id);
        }

        // OPEN SEARCH CALL
        String open_ret = fcc.call_abstract_function( cmd, FunctionCallConnect.MEDIUM_TIMEOUT);
        if (open_ret.charAt(0) != '0')
        {
            UserMain.errm_ok(my_dlg, "SearchMail open gave " + open_ret );
            return;
        }
        String[] l = open_ret.split(" ");

        search_id = l[1];
        ArrayList<String>field_list = model.get_field_list();


        cmd =  "SearchMail CMD:get MA:" + mandant + " ID:" + search_id + " ROW:-1 FLL:'";
        for ( int i = 0; i < field_list.size(); i++ )
        {
            if (i > 0)
                cmd += ",";
            cmd += field_list.get(i);
        }
        cmd += "'";

        String search_get_ret = fcc.call_abstract_function( cmd);

        if (search_get_ret.charAt(0) != '0')
        {
            UserMain.errm_ok(my_dlg, "SearchMail get gave " + search_get_ret );
            return;
        }

        XStream xstream = new XStream();
        Object o = xstream.fromXML(search_get_ret.substring(3));

        if (o instanceof ArrayList)
        {
            ArrayList<ArrayList<String>> ret_arr = (ArrayList<ArrayList<String>>)o;

            model = new MailTableModel( this, ret_arr );
            table.setModel(model);

            table.getColumnModel().getColumn(0).setMinWidth(80);
            table.getColumnModel().getColumn(0).setPreferredWidth(120);
            table.getColumnModel().getColumn(0).setMaxWidth(180);
            table.getColumnModel().getColumn(1).setMinWidth(20);
            table.getColumnModel().getColumn(1).setMaxWidth(20);
            table.getColumnModel().getColumn(2).setPreferredWidth(180);
            table.getColumnModel().getColumn(3).setMinWidth(30);
            table.getColumnModel().getColumn(3).setMaxWidth(50);
        }
    }

    SwingWorker sw;
    void open_mail( final int row )
    {
        if (sw != null)
            return;

        sw = new SwingWorker() {

            @Override
            public Object construct()
            {
                UserMain.self.show_busy(my_dlg, UserMain.Txt("Loading_mail") + "...");

                File tmp_file = run_download_mail(row, null);

                UserMain.self.hide_busy();

                if (tmp_file != null)
                {
                    run_open_mail( row, tmp_file );
                    tmp_file.delete();
                }

                sw = null;
                return null;
            }
        };

        sw.start();
    }

    void export_mail( final File f, final int[] rows, final String format )
    {
        if (sw != null)
            return;

        sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                UserMain.self.show_busy(my_dlg, UserMain.Txt("Exporting_mail") + "...");

                if (format.toLowerCase().startsWith("eml"))
                {
                    run_export_mail(f, rows);
                }
                if (format.toLowerCase().startsWith("m"))
                {
                    run_export_mbox(f, rows);
                }

                UserMain.self.hide_busy();

                sw = null;
                return null;
            }
        };

        sw.start();
    }

    private static final String forbidden_sj_chars = ":<>*?\\/'\"|$`´\t\r\n";
    private String clean_fname( String name )
    {

        // FIRST 80 CHARS, NO CONTROLCODES, NO SPECIAL CHARS
        StringBuffer sb = new StringBuffer();

        char last_char = 0;
        for (int i = 0; i < name.length(); i++)
        {
            char ch = name.charAt(i);
            if (!Character.isISOControl(ch) && forbidden_sj_chars.indexOf(ch) == -1)
            {
                sb.append(ch);
                last_char = ch;
            }
            else
            {
                if (last_char != ' ')
                    sb.append(' ');
                last_char = ' ';
            }

            if (i >= 79)
                break;
        }
        return sb.toString();
    }


    void run_export_mail( File dir, int[] rowi )
    {
        int last_percent = -1;
        UserMain.self.show_busy_val(0);
        for (int i = 0; i < rowi.length; i++)
        {
            int percent = i * 100 / rowi.length;
            if (percent != last_percent)
            {
                last_percent = percent;
                UserMain.self.show_busy_val(percent);
            }

            int row = rowi[i];
            String subject = table.getModel().getValueAt(row, MailTableModel.SUBJECT_COL).toString();
            subject = clean_fname(subject);
            File f = new File(dir, subject + ".eml");
            int idx = 1;
            while (f.exists() && idx < 100000)
            {
                f = new File(dir, subject + "_" + idx + ".eml");
                idx++;
            }
            run_download_mail(row, f.getAbsolutePath());
        }
    }



    void run_export_mbox( File dir, int[] rowi )
    {
        int last_percent = -1;
        UserMain.self.show_busy_val(0);
        MBoxFilterOutputStream mbfos = null;
        SimpleDateFormat sdf = new SimpleDateFormat("E M HH:mm:ss y");
        Date d = new Date();
        try
        {
            String subject = "export";
            File f = new File(dir, subject + ".mbx");
            int idx = 1;
            while (f.exists() && idx < 100000)
            {
                f = new File(dir, subject + "_" + idx + ".mbx");
                idx++;
            }
            mbfos = new MBoxFilterOutputStream(new BufferedOutputStream(new FileOutputStream(f)));
            for (int i = 0; i < rowi.length; i++)
            {
                int percent = i * 100 / rowi.length;
                if (percent != last_percent)
                {
                    last_percent = percent;
                    UserMain.self.show_busy_val(percent);
                }

                int row = rowi[i];
                d.setTime(System.currentTimeMillis());
                String timestamp = sdf.format( d );
                mbfos.write_direct("From MailSecurer " + timestamp + "\n" );
                run_download_mbox(row, mbfos);
                mbfos.write_direct("\n" );
            }
        }
        catch (Exception ex)
        {
            UserMain.errm_ok(my_dlg, UserMain.Txt("Fehler_beim_Schreiben_der_MBox-Daten") + ":\n" + ex.getMessage());
        }
        finally
        {
            try
            {
                mbfos.close();
            }
            catch (IOException ex)
            {

            }
        }
    }
    void run_download_mbox( int row, MBoxFilterOutputStream mbfos )
    {
        ServerInputStream sis = null;
        
        try
        {


            FunctionCallConnect fcc = UserMain.fcc();
            String ret = fcc.call_abstract_function("SearchMail CMD:open_mail ID:" + search_id + " ROW:" + row);
            if (ret.charAt(0) != '0')
            {
                UserMain.errm_ok(my_dlg, "SearchMail open_mail gave " + ret);
            }
            String[] l = ret.split(" ");
            String instream_id = l[1];
            ParseToken pt = new ParseToken(l[2]);
            long len = pt.GetLong("LEN:");

            InStreamID id = new InStreamID(instream_id, len);

            sis = new ServerInputStream(fcc.get_sqc(), id);
            sis.read(mbfos);


        }
        catch (Exception iOException)
        {
            iOException.printStackTrace();
            UserMain.errm_ok(my_dlg, "Fehler beim Abholen der Mail: " + iOException.getMessage() );
        }
        finally
        {
            try
            {
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


    File run_download_mail( int row, String file_name )
    {
        
        ServerInputStream sis = null;
        BufferedOutputStream baos = null;
        File tmp_file = null;

        try
        {
            if (file_name == null)
                tmp_file = File.createTempFile("dlml", ".tmp", new File("."));
            else
                tmp_file = new File(file_name);

            FileOutputStream fos = new FileOutputStream( tmp_file );
            baos = new BufferedOutputStream(fos);


            FunctionCallConnect fcc = UserMain.fcc();
            String ret = fcc.call_abstract_function("SearchMail CMD:open_mail ID:" + search_id + " ROW:" + row);
            if (ret.charAt(0) != '0')
            {
                UserMain.errm_ok(my_dlg, "SearchMail open_mail gave " + ret);
                return null;
            }
            String[] l = ret.split(" ");
            String instream_id = l[1];
            ParseToken pt = new ParseToken(l[2]);
            long len = pt.GetLong("LEN:");

            InStreamID id = new InStreamID(instream_id, len);

            sis = new ServerInputStream(fcc.get_sqc(), id);
            sis.read(baos);

            baos.close();

            return tmp_file;
        }
        catch (Exception iOException)
        {
            iOException.printStackTrace();
            UserMain.errm_ok(my_dlg, "Fehler beim Abholen der Mail: " + iOException.getMessage() );
        }
        finally
        {
            try
            {
                if (sis != null)
                {
                    sis.close();
                }
                if (baos != null)
                {
                    baos.close();
                }
            }
            catch (IOException iOException)
            {
            }
            
        }
        return null;
    }
    void run_open_mail( int row, File file )
    {
        String subject = table.getModel().getValueAt(row, MailTableModel.SUBJECT_COL).toString();
        BufferedInputStream bais = null;

        try
        {

            FileInputStream fis = new FileInputStream( file );
            bais = new BufferedInputStream(fis);
          
            RFCMimeMail mmsg;

            mmsg = new RFCMimeMail();
            
            mmsg.parse(bais);


            MailPreviewDlg dlg = new MailPreviewDlg(UserMain.self, mmsg);
            bais.close();

            UserMain.self.hide_busy();

            dlg.setModal(false);
            dlg.setTitle(subject);
            dlg.setLocation( my_dlg.getLocation().x + 20,my_dlg.getLocation().y + 20);
            dlg.setVisible(true);

        }
        catch (Exception iOException)
        {
            iOException.printStackTrace();
            UserMain.errm_ok(my_dlg, "Fehler beim Abholen der Mail: " + iOException.getMessage() );
        }
        finally
        {
            try
            {
                if (bais != null)
                {
                    bais.close();
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

        jLabel1 = new javax.swing.JLabel();
        TXT_SEARCH = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        SCP_TABLE = new javax.swing.JScrollPane();
        BT_CLOSE = new GlossButton();
        BT_EXPORT = new GlossButton();
        TXT_MAIL = new javax.swing.JTextField();
        CB_FIELD = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        CB_ENTRIES = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        BT_RESTORE = new GlossButton();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TXTA_FILTER = new javax.swing.JTextArea();
        BT_TOGGLE_SELECTION = new GlossButton();

        jLabel1.setText(UserMain.getString("Suche")); // NOI18N

        TXT_SEARCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TXT_SEARCHActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/tr_browse.png"))); // NOI18N
        jButton1.setIconTextGap(0);
        jButton1.setInheritsPopupMenu(true);
        jButton1.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_TABLE, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 732, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SCP_TABLE, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE))
        );

        BT_CLOSE.setText(UserMain.getString("Schliessen")); // NOI18N
        BT_CLOSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_CLOSEActionPerformed(evt);
            }
        });

        BT_EXPORT.setText(UserMain.getString("Export_Mail")); // NOI18N
        BT_EXPORT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_EXPORTActionPerformed(evt);
            }
        });

        CB_FIELD.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText(UserMain.getString("Filter")); // NOI18N

        jLabel3.setText("Mailadresse");

        CB_ENTRIES.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10", "100", "1000" }));
        CB_ENTRIES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_ENTRIESActionPerformed(evt);
            }
        });

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dimm/home/MA_Properties"); // NOI18N
        jLabel4.setText(bundle.getString("Entries")); // NOI18N

        BT_RESTORE.setText(UserMain.Txt("Restore_Mail")); // NOI18N
        BT_RESTORE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_RESTOREActionPerformed(evt);
            }
        });

        jLabel5.setText("Filter");

        TXTA_FILTER.setColumns(20);
        TXTA_FILTER.setEditable(false);
        TXTA_FILTER.setRows(5);
        TXTA_FILTER.setTabSize(4);
        TXTA_FILTER.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXTA_FILTERMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TXTA_FILTER);

        BT_TOGGLE_SELECTION.setText(UserMain.Txt("Select_onoff")); // NOI18N
        BT_TOGGLE_SELECTION.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_TOGGLE_SELECTIONActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BT_EXPORT)
                        .addGap(10, 10, 10)
                        .addComponent(BT_RESTORE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 481, Short.MAX_VALUE)
                        .addComponent(BT_CLOSE)
                        .addGap(10, 10, 10))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(32, 32, 32)
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 346, Short.MAX_VALUE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(TXT_MAIL, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(32, 32, 32)
                                .addComponent(CB_ENTRIES, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(TXT_SEARCH, javax.swing.GroupLayout.DEFAULT_SIZE, 83, Short.MAX_VALUE)
                        .addGap(44, 44, 44)
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(CB_FIELD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton1)
                        .addContainerGap())
                    .addComponent(BT_TOGGLE_SELECTION)))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(TXT_SEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(CB_FIELD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel1)
                                .addComponent(jLabel2)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(TXT_MAIL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(CB_ENTRIES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jButton1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BT_TOGGLE_SELECTION)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_CLOSE)
                    .addComponent(BT_EXPORT)
                    .addComponent(BT_RESTORE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton1ActionPerformed
    {//GEN-HEADEREND:event_jButton1ActionPerformed
        // TODO add your handling code here:
        search_mail();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void BT_CLOSEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_CLOSEActionPerformed
    {//GEN-HEADEREND:event_BT_CLOSEActionPerformed
        // TODO add your handling code here:
        FunctionCallConnect fcc = UserMain.fcc();

        if (search_id != null)
        {
            int mandant = UserMain.self.get_act_mandant().getId();
            fcc.call_abstract_function("SearchMail CMD:close MA:" + mandant + " ID:" + search_id);
        }

        setVisible(false);
    }//GEN-LAST:event_BT_CLOSEActionPerformed

    private void TXT_SEARCHActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TXT_SEARCHActionPerformed
    {//GEN-HEADEREND:event_TXT_SEARCHActionPerformed
        // TODO add your handling code here:
        search_mail();

    }//GEN-LAST:event_TXT_SEARCHActionPerformed


    private void BT_EXPORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_EXPORTActionPerformed
    {//GEN-HEADEREND:event_BT_EXPORTActionPerformed
        // TODO add your handling code here:
        // CHOOSE CERTFILE
        MailExportPanel pnl = new MailExportPanel(  );
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
        dlg.set_next_location( my_dlg );

        dlg.setVisible(true);

        if (!pnl.isOkay())
            return;

     

        File dir = pnl.get_dir();
        int[] rowi = table.getSelectedRows();

        export_mail( dir, rowi, pnl.get_format() );

    }//GEN-LAST:event_BT_EXPORTActionPerformed

    private void BT_RESTOREActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_RESTOREActionPerformed
    {//GEN-HEADEREND:event_BT_RESTOREActionPerformed
        // TODO add your handling code here:
        int[] rowi = table.getSelectedRows();
        GetMailAddressPanel pnl = new GetMailAddressPanel( UserMain.self.get_act_mailaliases() );
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
        dlg.set_next_location( my_dlg );

        dlg.setVisible(true);

        if (!pnl.isOkay())
            return;

        String mail = pnl.get_mail();

        FunctionCallConnect fcc = UserMain.fcc();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < rowi.length; i++)
        {
            if (i > 0)
                sb.append( ",");
            sb.append( rowi[i] );
        }

        UserMain.self.show_busy(my_dlg, UserMain.Txt("Sende_Mail...") );

        String ret = fcc.call_abstract_function("SearchMail CMD:send_mail ID:" + search_id + " TO:" + mail + " ROWLIST:" + sb.toString(), 30);

        UserMain.self.hide_busy();


        if (ret.charAt(0) != '0')
        {
            UserMain.errm_ok(my_dlg, "SearchMail send_mail " + ret);
            return;
        }
    }//GEN-LAST:event_BT_RESTOREActionPerformed

    static String last_filter;
    private void TXTA_FILTERMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXTA_FILTERMouseClicked
    {//GEN-HEADEREND:event_TXTA_FILTERMouseClicked
        // TODO add your handling code here:
        try
        {
            ArrayList<String> var_names = new ArrayList<String>();
            var_names.add(CS_Constants.FLD_FROM);
            var_names.add(CS_Constants.FLD_TO);
            var_names.add(CS_Constants.FLD_CC);
            var_names.add(CS_Constants.FLD_BCC);
            var_names.add(CS_Constants.FLD_SUBJECT);
            var_names.add(CS_Constants.FLD_BODY);
            var_names.add(CS_Constants.FLD_DATE);
            var_names.add(CS_Constants.FLD_ATTACHMENT);
            var_names.add(CS_Constants.FLD_ATTACHMENT_NAME);
            var_names.add(CS_Constants.FLD_SIZE);
            var_names.add(CS_Constants.FLD_HEADERVAR_NAME);
            var_names.add(CS_Constants.FLD_HEADERVAR_VALUE);
            var_names.add(CS_Constants.FLD_META_ADDRESS);
           
            boolean compressed = true;
            RoleFilter rf = new RoleFilter(var_names, last_filter, compressed );

            GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, rf);
            dlg.setVisible(true);

            if (rf.isOkay())
            {
                 last_filter = rf.get_compressed_xml_list_data(compressed);

                 String nice_txt = RoleFilter.get_nice_filter_text( last_filter, compressed );
                 TXTA_FILTER.setText(nice_txt);
                 TXTA_FILTER.setCaretPosition(0);
                 
                 do_filter_search();
            }
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }


    }//GEN-LAST:event_TXTA_FILTERMouseClicked

    private void BT_TOGGLE_SELECTIONActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_TOGGLE_SELECTIONActionPerformed
    {//GEN-HEADEREND:event_BT_TOGGLE_SELECTIONActionPerformed
        // TODO add your handling code here:
        if (table.getSelectedRowCount() == 0)
            table.selectAll();
        else
            table.clearSelection();
    }//GEN-LAST:event_BT_TOGGLE_SELECTIONActionPerformed

    private void CB_ENTRIESActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_ENTRIESActionPerformed
    {//GEN-HEADEREND:event_CB_ENTRIESActionPerformed
        // TODO add your handling code here:
        do_filter_search();
    }//GEN-LAST:event_CB_ENTRIESActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_CLOSE;
    private javax.swing.JButton BT_EXPORT;
    private javax.swing.JButton BT_RESTORE;
    private javax.swing.JButton BT_TOGGLE_SELECTION;
    private javax.swing.JComboBox CB_ENTRIES;
    private javax.swing.JComboBox CB_FIELD;
    private javax.swing.JScrollPane SCP_TABLE;
    private javax.swing.JTextArea TXTA_FILTER;
    private javax.swing.JTextField TXT_MAIL;
    private javax.swing.JTextField TXT_SEARCH;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables




    @Override
    public void mouseClicked( MouseEvent e )
    {
        if (e.getClickCount() == 2)
        {
            if (e.getSource() == table)
            {
                int row = table.rowAtPoint(e.getPoint());
                
                open_mail( row );
            }
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

    @Override
    public JButton get_default_button()
    {
        return BT_CLOSE;
    }
    public static void main( String[] args )
    {
        try
        {
            String test = "BlahBla From blah blah\nFro m blah\nFrom sdfksjdf\nFr";
            String test2 = "om From blah blah\nFro m blah\nFrom sdfksjdf\n";
            ByteArrayOutputStream byos = new ByteArrayOutputStream();
            MBoxFilterOutputStream mbfos = new MBoxFilterOutputStream(byos);
            mbfos.write(test.getBytes());
            mbfos.write(test2.getBytes());
            String res = byos.toString();
            System.out.println("In : " + test + test2);
            System.out.println("Out: " + res);
        }
        catch (IOException iOException)
        {
        }

    }

    private void do_filter_search()
    {
        if (last_filter == null)
            return;

        int mandant = UserMain.self.get_act_mandant().getId();
        String user = UserMain.self.get_act_username();
        String pwd = UserMain.self.get_act_pwd();

         int entries = get_entries();

         String cmd = "SearchMail CMD:open_filter MA:" + mandant + " US:'" + user + "' PW:'" + pwd + "' UL:" +
                    UserMain.self.getUserLevel() + " FL:'" + last_filter + "' CNT:'" + entries + "' ";

         fill_model_with_search(cmd);


    }

}
