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
import dimm.home.Panels.LogicFilter;
import dimm.home.Panels.Login4EyesPanel;
import dimm.home.Preferences;
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.Rendering.GlossTable;
import dimm.home.ServerConnect.FunctionCallConnect;
import dimm.home.ServerConnect.InStreamID;
import dimm.home.ServerConnect.ServerInputStream;
import dimm.home.UserMain;
import dimm.home.Utilities.CXStream;
import dimm.home.Utilities.CmdExecutor;
import home.shared.Utilities.ParseToken;
import dimm.home.Utilities.SwingWorker;
import dimm.home.native_libs.NativeLoader;
import home.shared.CS_Constants;
import home.shared.SQL.OptCBEntry;
import home.shared.filter.ExprEntry;
import home.shared.filter.ExprEntry.OPERATION;
import home.shared.filter.ExprEntry.TYPE;
import home.shared.filter.VarTypeEntry;
import home.shared.hibernate.Role;
import home.shared.mail.EncodedMailInputStream;
import home.shared.mail.EncodedMailOutputStream;
import home.shared.mail.RFCMailAddress;
import home.shared.mail.RFCMimeMail;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.MessagingException;
import javax.mail.Part;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerDateModel;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableRowSorter;

class MailPreviewDlg extends GenericGlossyDlg
{

    UserMain main;

    MailPreviewDlg( UserMain parent, RFCMimeMail mail, String uid )
    {
        super(parent, true, new MailPreviewPanel(mail, uid));
        main = parent;

        this.set_next_location(parent);

        this.setSize(700, 600);
    }

    MailPreviewDlg( UserMain parent, MailPreviewPanel panel )
    {
        super(parent, true, panel);
        main = parent;

        this.set_next_location(parent);

        this.setSize(700, 600);
    }
}

class MailTableRowSorter extends TableRowSorter<MailTableModel>
{

    public MailTableRowSorter( MailTableModel m )
    {
        super(m);
    }
    LongComparator lc = new LongComparator();

    @Override
    public Comparator<?> getComparator( int column )
    {
        if (column == MailTableModel.DATE_COL || column == MailTableModel.SIZE_COL)
        {
            return lc;
        }
        return super.getComparator(column);
    }
}

/**
 *
 * @author mw
 */
public class MailViewPanel extends GlossDialogPanel implements MouseListener, CellEditorListener
{

    String search_id;
    GlossTable table;
    MailTableModel model;
    GlossTable simple_search_list;
    private static final int SIMPLE_SEARCH = 0;
    private static final int COMPLEX_SEARCH = 1;
    int search_mode = SIMPLE_SEARCH;
    GlossTable simple_search_table;
    SimpleSearchTableModel simple_search_tablemodel;
    TableCellEditor simple_condition_editor;
    TableCellEditor simple_val_editor;
    MailTableRowSorter sorter;
    public static final double DFLT_DIV_POS = 0.35;
    private static int SIMPLE_TAB_COL_CONDITION = 0;
    private static int SIMPLE_TAB_COL_NEG = 1;
    private static int SIMPLE_TAB_COL_VALUE = 2;
    private static int SIMPLE_TAB_COL_DELETE = 3;
    public static final int MAX_FETCH_SIZE = 5000;
    static String last_filter;
    int div_location;

    /** Creates new form MailViewPanel */
    public MailViewPanel()
    {
        initComponents();

        table = new GlossTable(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.addMouseListener(this);
        // REGISTER TABLE TO SCROLLPANEL
        table.embed_to_scrollpanel(SCP_TABLE);

        model = new MailTableModel(this, null, 0);
        table.setModel(model);
        sorter = new MailTableRowSorter(model);
        table.setRowSorter(sorter);
        table.setShowGrid(false);

        table.getColumnModel().getColumn(MailTableModel.DATE_COL).setCellRenderer(new UnixTimeCellRenderer(true));
        table.getColumnModel().getColumn(MailTableModel.SIZE_COL).setCellRenderer(new SizeStrCellRenderer(true));




        simple_search_table = new GlossTable(true);
        simple_search_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        simple_search_table.embed_to_scrollpanel(SCP_LIST);

        simple_search_tablemodel = new SimpleSearchTableModel(this, new SimpleSearchEntryModel(null));
        if (last_filter != null)
        {
            simple_search_tablemodel.set_filter(last_filter);
        }

        simple_search_table.setModel(simple_search_tablemodel);
        simple_search_table.addMouseListener(simple_search_tablemodel);

        JComboBox CB_CONDITION = new JComboBox();
        CB_CONDITION.removeAllItems();
        CB_CONDITION.addItem(new ConditionCBEntry(new ExprEntry(null, CS_Constants.FLD_FROM, "", OPERATION.CONTAINS, TYPE.STRING, false, false)));
        CB_CONDITION.addItem(new ConditionCBEntry(new ExprEntry(null, CS_Constants.FLD_TO, "", OPERATION.CONTAINS, TYPE.STRING, false, false)));
        CB_CONDITION.addItem(new ConditionCBEntry(new ExprEntry(null, CS_Constants.FLD_SUBJECT, "", OPERATION.CONTAINS, TYPE.STRING, false, false)));
        CB_CONDITION.addItem(new ConditionCBEntry(new ExprEntry(null, CS_Constants.FLD_SUBJECT, "", OPERATION.CONTAINS_SUBSTR, TYPE.STRING, false, false)));
        CB_CONDITION.addItem(new ConditionCBEntry(new ExprEntry(null, CS_Constants.FLD_DATE, "", OPERATION.NUM_LT, TYPE.TIMESTAMP, false, false)));
        CB_CONDITION.addItem(new ConditionCBEntry(new ExprEntry(null, CS_Constants.FLD_DATE, "", OPERATION.NUM_GT, TYPE.TIMESTAMP, false, false)));
        CB_CONDITION.addItem(new ConditionCBEntry(new ExprEntry(null, CS_Constants.VFLD_MAIL, "", OPERATION.CONTAINS, TYPE.STRING, false, false)));
        CB_CONDITION.addItem(new ConditionCBEntry(new ExprEntry(null, CS_Constants.VFLD_MAIL, "", OPERATION.CONTAINS_SUBSTR, TYPE.STRING, false, false)));
        CB_CONDITION.addItem(new ConditionCBEntry(new ExprEntry(null, CS_Constants.VFLD_TXT, "", OPERATION.CONTAINS, TYPE.STRING, false, false)));
        CB_CONDITION.addItem(new ConditionCBEntry(new ExprEntry(null, CS_Constants.VFLD_TXT, "", OPERATION.CONTAINS_SUBSTR, TYPE.STRING, false, false)));
        CB_CONDITION.addItem(new ConditionCBEntry(new ExprEntry(null, CS_Constants.FLD_ATTACHMENT_NAME, "", OPERATION.CONTAINS, TYPE.STRING, false, false)));
        CB_CONDITION.addItem(new ConditionCBEntry(new ExprEntry(null, CS_Constants.VFLD_ALL, "", OPERATION.CONTAINS, TYPE.STRING, false, false)));
        CB_CONDITION.addItem(new ConditionCBEntry(new ExprEntry(null, CS_Constants.VFLD_ALL, "", OPERATION.CONTAINS_SUBSTR, TYPE.STRING, false, false)));

        JComboBox CB_NEG = new JComboBox();
        CB_NEG.addItem(new NegCBEntry(false));
        CB_NEG.addItem(new NegCBEntry(true));

        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_NEG).setMinWidth(30);
        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_NEG).setMaxWidth(30);
        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_DELETE).setMinWidth(30);
        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_DELETE).setMaxWidth(30);
        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_CONDITION).setCellEditor(new DefaultCellEditor(CB_CONDITION));
        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_NEG).setCellEditor(new DefaultCellEditor(CB_NEG));

        JTextField TXT_VAL = new JTextField();
        /*    final DefaultCellEditor txt_editor = new DefaultCellEditor( TXT_VAL);
        txt_editor.setClickCountToStart(1);
        txt_editor.addCellEditorListener(this);*/

        JSpinner Date_VAL = new JSpinner(new SpinnerDateModel());
        javax.swing.JSpinner.DateEditor de = new javax.swing.JSpinner.DateEditor(
                Date_VAL, "dd.MM.yyyy HH:mm");

        de.getTextField().setHorizontalAlignment(JTextField.LEFT);
        Date_VAL.setEditor(de);

        final DefaultCellEditor value_editor = new ValueCellEditor(simple_search_tablemodel, TXT_VAL, Date_VAL);
        value_editor.setClickCountToStart(1);
        value_editor.addCellEditorListener(this);

        ActionListener okAction = new ActionListener()
        {

            @Override
            public void actionPerformed( ActionEvent e )
            {
                simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_VALUE).getCellEditor().stopCellEditing();
                last_filter = simple_search_tablemodel.get_compressed_xml_list_data();
                TXTA_FILTER.setText(LogicFilter.get_nice_filter_text(last_filter));
                TXTA_FILTER.setCaretPosition(0);

                do_filter_search();
            }
        };
        TXT_VAL.addActionListener(okAction);
        de.getTextField().addActionListener(okAction);

        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_VALUE).setCellEditor(value_editor/*txt_editor */);

        TBP_SEARCH.setSelectedIndex(SIMPLE_SEARCH);

        CB_VIEW_CONTENT.setSelected(true);

        div_location = SPL_VIEW.getDividerLocation();

        if (!CB_VIEW_CONTENT.isSelected())
        {
            SPL_VIEW.setDividerLocation(1.0);
        }


        if (new File("unlimited_entries.txt").exists())
        {
            CB_ENTRIES.addItem("10000");
            CB_ENTRIES.addItem("100000");
            CB_ENTRIES.addItem("1000000");
        }

        TXT_QUICKSEARCH.requestFocusInWindow();

        // SCP_LIST.setVisible(false);

    }

    @Override
    public void setVisible( boolean aFlag )
    {
        super.setVisible(aFlag);
        if (aFlag)
        {
            TXT_QUICKSEARCH.requestFocusInWindow();
        }
    }

    @Override
    public void setBounds( int x, int y, int width, int height )
    {

        super.setBounds(x, y, width, height);
        if (CB_VIEW_CONTENT.isVisible() && CB_VIEW_CONTENT.getHeight() > 0)
        {
            if (!CB_VIEW_CONTENT.isSelected())
            {
                SPL_VIEW.setDividerLocation(1.0);
            }
            else
            {
                SPL_VIEW.setDividerLocation(DFLT_DIV_POS);
            }
        }

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
    /*  void search_mail()
    {


    String mail =  TXT_MAIL.getText();
    String search_val = TXT_SEARCH.getText();
    FieldComboEntry fld_entry= (FieldComboEntry)CB_FIELD.getSelectedItem();
    String field_name = fld_entry.getField();

    int entries = get_entries();
    int mandant = UserMain.self.get_act_mandant_id();

    String cmd = "SearchMail CMD:open MA:" + mandant + " EM:'" + mail + "' FL:'" + field_name + "' VL:'" + search_val + "' CNT:'" + entries + "' ";


    fill_model_with_search( cmd );
    }*/

    void fill_model_with_search( String cmd, int cnt )
    {
        FunctionCallConnect fcc = UserMain.fcc();
        int mandant = UserMain.self.get_act_mandant_id();

        if (search_id != null)
        {
            // CLOSE EXISTING CALL;

            fcc.call_abstract_function("SearchMail CMD:close MA:" + mandant + " ID:" + search_id);
        }

        // OPEN SEARCH CALL
        String open_ret = fcc.call_abstract_function(cmd, FunctionCallConnect.LONG_TIMEOUT);
        if (open_ret.charAt(0) != '0')
        {
            UserMain.errm_ok(my_dlg, "SearchMail open gave " + open_ret);
            return;
        }
        String[] l = open_ret.split(" ");

        search_id = l[1];
        ArrayList<String> field_list = model.get_field_list();

        ParseToken pt = new ParseToken(open_ret);

        cnt = (int) pt.GetLongValue("N:");

        int fetch_size = cnt;
        if (cnt > MAX_FETCH_SIZE)
        {
            fetch_size = MAX_FETCH_SIZE;
        }

        if (cnt > 1000)
        {
            cmd = "SearchMail CMD:get MA:" + mandant + " ID:" + search_id + " ROW:0 ROWS:" + fetch_size + " FLL:'";
        }
        else
        {
            cmd = "SearchMail CMD:get MA:" + mandant + " ID:" + search_id + " ROW:-1 FLL:'";
        }

        for (int i = 0; i < field_list.size(); i++)
        {
            if (i > 0)
            {
                cmd += ",";
            }
            cmd += field_list.get(i);
        }
        cmd += "'";

        String search_get_ret = fcc.call_abstract_function(cmd);

        if (search_get_ret.charAt(0) != '0')
        {
            UserMain.errm_ok(my_dlg, "SearchMail get gave " + search_get_ret);
            return;
        }

        CXStream xstream = new CXStream();
        Object o = xstream.fromXML(search_get_ret.substring(3));

        if (o instanceof ArrayList)
        {
            ArrayList<ArrayList<String>> ret_arr = (ArrayList<ArrayList<String>>) o;

            model = new MailTableModel(this, ret_arr, cnt);
            sorter = new MailTableRowSorter(model);
            table.setRowSorter(sorter);
            table.setModel(model);
            table.getColumnModel().getColumn(MailTableModel.DATE_COL).setCellRenderer(new UnixTimeCellRenderer(true));
            table.getColumnModel().getColumn(MailTableModel.SIZE_COL).setCellRenderer(new SizeStrCellRenderer(true));

            model.fireTableDataChanged();

            table.getColumnModel().getColumn(MailTableModel.DATE_COL).setMinWidth(80);
            table.getColumnModel().getColumn(MailTableModel.DATE_COL).setPreferredWidth(120);
            table.getColumnModel().getColumn(MailTableModel.DATE_COL).setMaxWidth(180);
            table.getColumnModel().getColumn(MailTableModel.FROM_COL).setPreferredWidth(60);
            table.getColumnModel().getColumn(MailTableModel.TO_COL).setPreferredWidth(60);

            table.getColumnModel().getColumn(MailTableModel.ATTACH_COL).setMinWidth(20);
            table.getColumnModel().getColumn(MailTableModel.ATTACH_COL).setMaxWidth(20);
            table.getColumnModel().getColumn(MailTableModel.OPEN_ATTACH_COL).setMinWidth(20);
            table.getColumnModel().getColumn(MailTableModel.OPEN_ATTACH_COL).setMaxWidth(20);
            table.getColumnModel().getColumn(MailTableModel.SUBJECT_COL).setPreferredWidth(180);
            table.getColumnModel().getColumn(MailTableModel.SIZE_COL).setMinWidth(30);
            table.getColumnModel().getColumn(MailTableModel.SIZE_COL).setMaxWidth(50);
            table.getColumnModel().getColumn(MailTableModel._4EYES_COL).setMinWidth(0);
            table.getColumnModel().getColumn(MailTableModel._4EYES_COL).setMaxWidth(0);
        }
    }
    SwingWorker sw;

    void open_mail( final int row )
    {
        if (!UserMain.self.check_for_role_option(my_dlg, OptCBEntry.READ))
        {
            return;
        }

        // CHECK FOR 4 EYES
        Role role = model.get_4_eyes_model(row);
        if (role != null)
        {
            if (!check_4eyes_login(role))
            {
                return;
            }
        }

        if (sw != null)
        {
            return;
        }

        sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                UserMain.self.show_busy(my_dlg, UserMain.Txt("Loading_mail") + "...");

                File tmp_file = run_download_mail(row, null, true);

                sw = null;

                //UserMain.self.hide_busy();

                if (tmp_file != null)
                {
                    run_open_mail(row, tmp_file, true);
                    if (!Main.get_bool_prop(Preferences.CACHE_MAILFILES, false))
                    {
                        tmp_file.delete();
                    }
                }


                return null;
            }
        };

        sw.start();
    }
    SwingWorker view_sw;
    final ArrayList<Integer> preview_list = new ArrayList<Integer>();

    void preview_mail( final int row )
    {
        if (!UserMain.self.check_for_role_option(my_dlg, OptCBEntry.READ))
        {
            return;
        }
        // CHECK FOR 4 EYES
        Role role = model.get_4_eyes_model(row);
        if (role != null)
        {
            if (!check_4eyes_login(role))
            {
                return;
            }
        }


        if (view_sw != null)
        {
            synchronized (preview_list)
            {
                preview_list.add(new Integer(row));
                return;
            }
        }

        view_sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                int work_row = row;
                while (true)
                {

                    File tmp_file = run_download_mail(work_row, null, true);


                    //UserMain.self.hide_busy();

                    if (tmp_file != null)
                    {
                        run_preview_mail(row, tmp_file, true);

                        if (!Main.get_bool_prop(Preferences.CACHE_MAILFILES, false))
                        {
                            tmp_file.delete();
                        }
                    }
                    synchronized (preview_list)
                    {
                        if (preview_list.isEmpty())
                        {
                            view_sw = null;
                            break;
                        }
                        work_row = preview_list.get(preview_list.size() - 1);
                        preview_list.clear();
                    }
                }

                return null;
            }
        };

        view_sw.start();
    }

    void raw_view_mail( final int row )
    {
        if (!UserMain.self.check_for_role_option(my_dlg, OptCBEntry.READ))
        {
            return;
        }
        // CHECK FOR 4 EYES
        Role role = model.get_4_eyes_model(row);
        if (role != null)
        {
            if (!check_4eyes_login(role))
            {
                return;
            }
        }


        if (sw != null)
        {
            return;
        }

        sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                UserMain.self.show_busy(my_dlg, UserMain.Txt("Loading_mail") + "...");

                File tmp_file = run_download_mail(row, null, true);

                sw = null;

                UserMain.self.hide_busy();

                if (tmp_file != null && tmp_file.exists())
                {
                    GlossDialogPanel pnl = new GlossDialogPanel()
                    {

                        @Override
                        public JButton get_default_button()
                        {
                            return null;
                        }
                    };
                    JTextArea txta = new JTextArea(132, 80);
                    pnl.setLayout(new BoxLayout(pnl, 1));
                    JScrollPane jsp = new JScrollPane(txta);
                    pnl.add(jsp);

                    GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
                    byte[] buff = new byte[(int) tmp_file.length()];
                    try
                    {
                        InputStream fis = new FileInputStream(tmp_file);
                        fis = new EncodedMailInputStream(fis);
                        fis.read(buff);
                        fis.close();
                    }
                    catch (IOException iOException)
                    {
                    }
                    String txt = new String(buff);
                    txta.setText(txt);

                    if (!Main.get_bool_prop(Preferences.CACHE_MAILFILES, false))
                    {
                        tmp_file.delete();
                    }

                    dlg.setSize(400, 400);
                    dlg.setVisible(true);
                }


                return null;
            }
        };

        sw.start();
    }

    void export_mail( final File f, final int[] rows, final String format )
    {
        if (!UserMain.self.check_for_role_option(my_dlg, OptCBEntry.READ))
        {
            return;
        }


        if (sw != null)
        {
            return;
        }

        sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                UserMain.self.show_busy(my_dlg, UserMain.Txt("Exporting_mail") + "...");

                if (format.toLowerCase().startsWith("client"))
                {
                    run_export_mail(null, rows, true);
                }
                if (format.toLowerCase().startsWith("eml"))
                {
                    run_export_mail(f, rows, false);
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
        StringBuilder sb = new StringBuilder();

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
                {
                    sb.append(' ');
                }
                last_char = ' ';
            }

            if (i >= 79)
            {
                break;
            }
        }
        return sb.toString();
    }

    void run_export_mail( File dir, int[] rowi, boolean open_in_client )
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
            row = sorter.convertRowIndexToModel(row);

            // CHECK FOR 4 EYES
            Role role = model.get_4_eyes_model(row);
            if (role != null)
            {
                if (!check_4eyes_login(role))
                {
                    continue;
                }
            }

            String subject = table.getModel().getValueAt(row, MailTableModel.SUBJECT_COL).toString();
            subject = clean_fname(subject);
            File f = null;
            if (dir == null)
            {
                try
                {
                    f = File.createTempFile("mstemp", ".eml", new File(Main.get_tmp_path()));
                    f.deleteOnExit();
                }
                catch (IOException ex)
                {
                    Logger.getLogger(MailViewPanel.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            else
            {
                f = new File(dir, subject + ".eml");
                int idx = 1;
                while (f.exists() && idx < 100000)
                {
                    f = new File(dir, subject + "_" + idx + ".eml");
                    idx++;
                }
            }
            run_download_mail(row, f.getAbsolutePath(), false);
            if (open_in_client)
            {
                UserMain.self.hide_busy();

                String[] cmd = null;
                if (NativeLoader.is_win())
                {
                    cmd = new String[3];
                    cmd[0] = "cmd";
                    cmd[1] = "/c";
                    cmd[2] = f.getAbsolutePath();
                }
                if (NativeLoader.is_osx())
                {
                    cmd = new String[2];
                    cmd[0] = "open";
                    cmd[1] = f.getAbsolutePath();
                }
                if (cmd != null)
                {
                    CmdExecutor exe = new CmdExecutor(cmd);
                    exe.set_no_debug(false);
                    exe.start(); //exec();
                }
            }
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
                row = sorter.convertRowIndexToModel(row);

                // CHECK FOR 4 EYES
                Role role = model.get_4_eyes_model(row);
                if (role != null)
                {
                    if (!check_4eyes_login(role))
                    {
                        continue;
                    }
                }


                d.setTime(System.currentTimeMillis());
                String timestamp = sdf.format(d);
                mbfos.write_direct("From MailSecurer " + timestamp + "\n");
                run_download_mbox(row, mbfos);
                mbfos.write_direct("\n");
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
            UserMain.errm_ok(my_dlg, "Fehler beim Abholen der Mail: " + iOException.getMessage());
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

    File run_download_mail( int row, final String file_name, boolean encoded )
    {

        ServerInputStream sis = null;
        OutputStream baos = null;
        File tmp_file = null;
        boolean error_occured = false;

        try
        {
            if (file_name == null)
            {
                if (Main.get_bool_prop(Preferences.CACHE_MAILFILES, false))
                {
                    String uid = model.get_uid(row);
                    tmp_file = new File(Main.get_cache_path(), uid + ".tmp");
                    if (tmp_file.exists())
                    {
                        return tmp_file;
                    }

                    tmp_file.deleteOnExit();
                }
                else
                {
                    // TempFile may not be writable, write it to USER DIR
                    tmp_file = File.createTempFile("dlml", ".tmp", new File(Main.get_tmp_path()));
                    tmp_file.deleteOnExit();
                }
            }
            else
            {
                tmp_file = new File(file_name);
            }

            FileOutputStream fos = new FileOutputStream(tmp_file);
            baos = new BufferedOutputStream(fos);
            if (encoded)
            {
                baos = new EncodedMailOutputStream(baos);
            }


            FunctionCallConnect fcc = UserMain.fcc();
            String ret = fcc.call_abstract_function("SearchMail CMD:open_mail ID:" + search_id + " ROW:" + row);
            if (ret.charAt(0) != '0')
            {
                UserMain.errm_ok(my_dlg, "SearchMail open_mail gave " + ret);
                error_occured = true;

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
            baos = null;

            sis.close();
            sis = null;


            return tmp_file;
        }
        catch (Exception iOException)
        {
            iOException.printStackTrace();
            UserMain.errm_ok(my_dlg, "Fehler beim Abholen der Mail: " + iOException.getMessage());
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
                UserMain.errm_ok(my_dlg, "Fehler beim Schließen der Mail: " + iOException.getMessage());
            }

            // CLEAR CACHED FILES ON ERROR
            if (error_occured && file_name == null)
            {
                tmp_file.delete();
            }
        }
        return null;
    }

    void run_preview_mail( int row, File file, boolean encoded )
    {
        InputStream bais = null;

        try
        {
            FileInputStream fis = new FileInputStream(file);
            bais = new BufferedInputStream(fis);
            if (encoded)
            {
                bais = new EncodedMailInputStream(bais);
            }

            // CREATE AND PARSE MAIL
            RFCMimeMail mmsg = new RFCMimeMail();
            mmsg.parse(bais);

            // HANDLE BCC VISIBILITY
            if (model.get_bcc(row) != null)
            {
                // ADD BCC IF WE ARE ADMIN OR IF WE ARE  BCC OURSELF OR IF WE ARE SENDER
                String bcc = model.get_bcc(row);
                boolean show_bcc = false;

                if (UserMain.self.user_has_role_option(OptCBEntry.ADMIN))
                {
                    show_bcc = true;
                }


                if (mmsg.is_in_email(UserMain.self.get_act_mailaliases(), RFCMailAddress.ADR_TYPE.FROM))
                {
                    show_bcc = true;
                }

                if (mmsg.is_in_email(UserMain.self.get_act_mailaliases(), RFCMailAddress.ADR_TYPE.BCC))
                {
                    show_bcc = true;
                }

                if (show_bcc)
                {
                    mmsg.getEmail_list().add(new RFCMailAddress(bcc, RFCMailAddress.ADR_TYPE.BCC));
                }
            }

            // CREATE AND ADD PANEL
            MailPreviewPanel panel = new MailPreviewPanel(mmsg, model.get_uid(row));
            panel.setDlg(my_dlg);
            JComponent pnl = panel.get_SPL_MAIL();

            SCP_PREVIEW.setViewportView(pnl);
            PN_PREVIEW.repaint();

        }
        catch (Exception iOException)
        {
            iOException.printStackTrace();
            UserMain.errm_ok(my_dlg, "Fehler beim Abholen der Mail: " + iOException.getMessage());
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

    void run_open_mail( int row, File file, boolean encoded )
    {
        String _subject = "Unknown";
        String uuid = "???";
        if (row >= 0)
        {
            _subject = table.getModel().getValueAt(row, MailTableModel.SUBJECT_COL).toString();
            uuid = model.get_uid(row);
        }

        final String subject = _subject;


        InputStream bais = null;

        try
        {
            FileInputStream fis = new FileInputStream(file);
            bais = new BufferedInputStream(fis);
            if (encoded)
            {
                bais = new EncodedMailInputStream(bais);
            }



            final RFCMimeMail mmsg = new RFCMimeMail();
            mmsg.parse(bais);
            bais.close();
            bais = null;

            final MailPreviewPanel panel = new MailPreviewPanel(mmsg, uuid);

            SwingUtilities.invokeLater(new Runnable()
            {

                @Override
                public void run()
                {
                    UserMain.self.hide_busy();
                    MailPreviewDlg dlg = new MailPreviewDlg(UserMain.self, panel);


                    dlg.setModal(false);
                    dlg.setTitle(subject);
                    dlg.setLocation(my_dlg.getLocation().x + 20, my_dlg.getLocation().y + 20);
                    dlg.setVisible(true);
                }
            });



        }
        catch (Exception iOException)
        {
            iOException.printStackTrace();
            UserMain.errm_ok(my_dlg, "Fehler beim Abholen der Mail: " + iOException.getMessage());
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

        TBP_SEARCH = new javax.swing.JTabbedPane();
        PN_SIMPLE = new javax.swing.JPanel();
        TXT_QUICKSEARCH = new javax.swing.JTextField();
        BT_ADD = new GlossButton();
        BT_DEL = new GlossButton();
        SCP_LIST = new javax.swing.JScrollPane();
        BT_SIMPLESEARCH = new GlossButton();
        BT_FILTERS = new GlossButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        CB_ENTRIES = new javax.swing.JComboBox();
        BT_HELP1 = new GlossButton();
        PN_COMPLEX = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TXTA_FILTER = new javax.swing.JTextArea();
        BT_CLOSE = new GlossButton();
        BT_EXPORT = new GlossButton();
        BT_RESTORE = new GlossButton();
        BT_TOGGLE_SELECTION = new GlossButton();
        BT_VIEW_CONTENT = new javax.swing.JButton();
        BT_OPEN_EML = new javax.swing.JButton();
        CB_VIEW_CONTENT = new javax.swing.JCheckBox();
        SPL_VIEW = new javax.swing.JSplitPane();
        PN_PREVIEW = new javax.swing.JPanel();
        SCP_PREVIEW = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        SCP_TABLE = new javax.swing.JScrollPane();
        BT_OPEN_IN_MAIL = new GlossButton();

        PN_SIMPLE.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                PN_SIMPLEFocusGained(evt);
            }
        });

        TXT_QUICKSEARCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TXT_QUICKSEARCHActionPerformed(evt);
            }
        });

        BT_ADD.setText(" + ");
        BT_ADD.setMargin(new java.awt.Insets(2, 1, 2, 1));
        BT_ADD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ADDActionPerformed(evt);
            }
        });

        BT_DEL.setText(" - ");
        BT_DEL.setMargin(new java.awt.Insets(2, 1, 2, 1));
        BT_DEL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_DELActionPerformed(evt);
            }
        });

        SCP_LIST.setMinimumSize(new java.awt.Dimension(24, 70));
        SCP_LIST.setPreferredSize(new java.awt.Dimension(4, 100));

        BT_SIMPLESEARCH.setText(UserMain.getString("Search")); // NOI18N
        BT_SIMPLESEARCH.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        BT_SIMPLESEARCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_SIMPLESEARCHActionPerformed(evt);
            }
        });

        BT_FILTERS.setText(UserMain.getString("Filters")); // NOI18N
        BT_FILTERS.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        BT_FILTERS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_FILTERSActionPerformed(evt);
            }
        });

        jLabel1.setText(UserMain.Txt("Quicksearch")); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dimm/home/MA_Properties"); // NOI18N
        jLabel4.setText(bundle.getString("Entries")); // NOI18N

        CB_ENTRIES.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10", "100", "1000" }));
        CB_ENTRIES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_ENTRIESActionPerformed(evt);
            }
        });

        BT_HELP1.setText(UserMain.Txt("?")); // NOI18N
        BT_HELP1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_HELP1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_SIMPLELayout = new javax.swing.GroupLayout(PN_SIMPLE);
        PN_SIMPLE.setLayout(PN_SIMPLELayout);
        PN_SIMPLELayout.setHorizontalGroup(
            PN_SIMPLELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_SIMPLELayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BT_ADD)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BT_DEL)
                .addGap(18, 18, 18)
                .addComponent(BT_FILTERS)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addComponent(TXT_QUICKSEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BT_HELP1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 87, Short.MAX_VALUE)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CB_ENTRIES, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BT_SIMPLESEARCH)
                .addContainerGap())
            .addComponent(SCP_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, 784, Short.MAX_VALUE)
        );

        PN_SIMPLELayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ADD, BT_DEL});

        PN_SIMPLELayout.setVerticalGroup(
            PN_SIMPLELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_SIMPLELayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_SIMPLELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_ADD)
                    .addComponent(BT_DEL)
                    .addComponent(BT_SIMPLESEARCH)
                    .addComponent(BT_FILTERS)
                    .addComponent(jLabel1)
                    .addComponent(TXT_QUICKSEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CB_ENTRIES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(BT_HELP1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SCP_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        TBP_SEARCH.addTab(UserMain.getString("Simple_Search"), PN_SIMPLE); // NOI18N

        PN_COMPLEX.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                PN_COMPLEXFocusGained(evt);
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

        javax.swing.GroupLayout PN_COMPLEXLayout = new javax.swing.GroupLayout(PN_COMPLEX);
        PN_COMPLEX.setLayout(PN_COMPLEXLayout);
        PN_COMPLEXLayout.setHorizontalGroup(
            PN_COMPLEXLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_COMPLEXLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel5)
                .addGap(32, 32, 32)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 708, Short.MAX_VALUE)
                .addContainerGap())
        );
        PN_COMPLEXLayout.setVerticalGroup(
            PN_COMPLEXLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_COMPLEXLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_COMPLEXLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 118, Short.MAX_VALUE)
                    .addComponent(jLabel5))
                .addContainerGap())
        );

        TBP_SEARCH.addTab(UserMain.getString("Complex_Search"), PN_COMPLEX); // NOI18N

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

        BT_RESTORE.setText(UserMain.Txt("Restore_Mail")); // NOI18N
        BT_RESTORE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_RESTOREActionPerformed(evt);
            }
        });

        BT_TOGGLE_SELECTION.setText(UserMain.Txt("Select_onoff")); // NOI18N
        BT_TOGGLE_SELECTION.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_TOGGLE_SELECTIONActionPerformed(evt);
            }
        });

        BT_VIEW_CONTENT.setText("        ");
        BT_VIEW_CONTENT.setBorderPainted(false);
        BT_VIEW_CONTENT.setContentAreaFilled(false);
        BT_VIEW_CONTENT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_VIEW_CONTENTActionPerformed(evt);
            }
        });

        BT_OPEN_EML.setText("        ");
        BT_OPEN_EML.setBorderPainted(false);
        BT_OPEN_EML.setContentAreaFilled(false);
        BT_OPEN_EML.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OPEN_EMLActionPerformed(evt);
            }
        });

        CB_VIEW_CONTENT.setText(UserMain.Txt("View_Content")); // NOI18N
        CB_VIEW_CONTENT.setOpaque(false);
        CB_VIEW_CONTENT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_VIEW_CONTENTActionPerformed(evt);
            }
        });

        SPL_VIEW.setDividerLocation(120);
        SPL_VIEW.setDividerSize(2);
        SPL_VIEW.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        SPL_VIEW.setPreferredSize(new java.awt.Dimension(763, 352));

        javax.swing.GroupLayout PN_PREVIEWLayout = new javax.swing.GroupLayout(PN_PREVIEW);
        PN_PREVIEW.setLayout(PN_PREVIEWLayout);
        PN_PREVIEWLayout.setHorizontalGroup(
            PN_PREVIEWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_PREVIEW, javax.swing.GroupLayout.DEFAULT_SIZE, 787, Short.MAX_VALUE)
        );
        PN_PREVIEWLayout.setVerticalGroup(
            PN_PREVIEWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_PREVIEW, javax.swing.GroupLayout.DEFAULT_SIZE, 207, Short.MAX_VALUE)
        );

        SPL_VIEW.setRightComponent(PN_PREVIEW);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_TABLE, javax.swing.GroupLayout.DEFAULT_SIZE, 787, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_TABLE, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE)
        );

        SPL_VIEW.setLeftComponent(jPanel1);

        BT_OPEN_IN_MAIL.setText(UserMain.Txt("OpenInMail")); // NOI18N
        BT_OPEN_IN_MAIL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OPEN_IN_MAILActionPerformed(evt);
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
                        .addComponent(BT_TOGGLE_SELECTION)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_EXPORT)
                        .addGap(10, 10, 10)
                        .addComponent(BT_RESTORE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_OPEN_IN_MAIL)
                        .addGap(10, 10, 10)
                        .addComponent(BT_OPEN_EML)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BT_VIEW_CONTENT)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                        .addComponent(CB_VIEW_CONTENT)
                        .addGap(18, 18, 18)
                        .addComponent(BT_CLOSE)
                        .addGap(10, 10, 10))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(TBP_SEARCH, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 789, Short.MAX_VALUE)
                            .addComponent(SPL_VIEW, javax.swing.GroupLayout.DEFAULT_SIZE, 789, Short.MAX_VALUE))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(TBP_SEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SPL_VIEW, javax.swing.GroupLayout.DEFAULT_SIZE, 330, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_CLOSE)
                    .addComponent(BT_TOGGLE_SELECTION)
                    .addComponent(BT_EXPORT)
                    .addComponent(BT_RESTORE)
                    .addComponent(BT_OPEN_IN_MAIL)
                    .addComponent(BT_VIEW_CONTENT)
                    .addComponent(CB_VIEW_CONTENT)
                    .addComponent(BT_OPEN_EML))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_CLOSEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_CLOSEActionPerformed
    {//GEN-HEADEREND:event_BT_CLOSEActionPerformed
        // TODO add your handling code here:
        FunctionCallConnect fcc = UserMain.fcc();

        if (search_id != null)
        {
            int mandant = UserMain.self.get_act_mandant_id();
            fcc.call_abstract_function("SearchMail CMD:close MA:" + mandant + " ID:" + search_id);
        }

        setVisible(false);

        UserMain.close_search();
    }//GEN-LAST:event_BT_CLOSEActionPerformed

    private void BT_EXPORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_EXPORTActionPerformed
    {//GEN-HEADEREND:event_BT_EXPORTActionPerformed
        // TODO add your handling code here:
        // CHOOSE CERTFILE

        if (!UserMain.self.check_for_role_option(my_dlg, OptCBEntry.EXPORT))
        {
            return;
        }


        MailExportPanel pnl = new MailExportPanel();
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
        dlg.set_next_location(my_dlg);

        dlg.setVisible(true);

        if (!pnl.isOkay())
        {
            return;
        }



        File dir = pnl.get_dir();
        int[] rowi = table.getSelectedRows();

        export_mail(dir, rowi, pnl.get_format());

    }//GEN-LAST:event_BT_EXPORTActionPerformed

    private void BT_RESTOREActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_RESTOREActionPerformed
    {//GEN-HEADEREND:event_BT_RESTOREActionPerformed
        // TODO add your handling code here:

        if (!UserMain.self.check_for_role_option(my_dlg, OptCBEntry.RESTORE))
        {
            return;
        }



        // GET SELECTED ROWS
        int[] rowi = table.getSelectedRows();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < rowi.length; i++)
        {
            int row = sorter.convertRowIndexToModel(rowi[i]);

            // CHECK FOR 4 EYES
            Role role = model.get_4_eyes_model(row);
            if (role != null)
            {
                if (!check_4eyes_login(role))
                {
                    break;
                }
            }

            if (i > 0)
            {
                sb.append(",");
            }
            sb.append(row);
        }
        if (sb.length() == 0)
        {
            return;
        }

        // GET FROM- AND TO MAILADDRESSES
        GetMailAddressPanel pnl = new GetMailAddressPanel(UserMain.self.get_act_mailaliases());
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
        dlg.set_next_location(my_dlg);

        dlg.setVisible(true);

        if (!pnl.isOkay())
        {
            return;
        }


        String to_mail = pnl.get_to_mail();


        UserMain.self.show_busy(my_dlg, UserMain.Txt("Sende_Mail..."));

        FunctionCallConnect fcc = UserMain.fcc();
        String ret = fcc.call_abstract_function("SearchMail CMD:send_mail ID:" + search_id + " TO:" + to_mail + " ROWLIST:" + sb.toString(), FunctionCallConnect.LONG_TIMEOUT);

        UserMain.self.hide_busy();


        if (ret.charAt(0) != '0')
        {
            UserMain.errm_ok(my_dlg, "SearchMail send_mail " + ret);
            return;
        }
    }//GEN-LAST:event_BT_RESTOREActionPerformed

    private void TXTA_FILTERMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXTA_FILTERMouseClicked
    {//GEN-HEADEREND:event_TXTA_FILTERMouseClicked
        // TODO add your handling code here:
        try
        {
            ArrayList<VarTypeEntry> var_names = new ArrayList<VarTypeEntry>();
            var_names.add(new VarTypeEntry(CS_Constants.FLD_FROM, ExprEntry.TYPE.STRING));
            var_names.add(new VarTypeEntry(CS_Constants.FLD_TO, ExprEntry.TYPE.STRING));
            var_names.add(new VarTypeEntry(CS_Constants.FLD_CC, ExprEntry.TYPE.STRING));
            var_names.add(new VarTypeEntry(CS_Constants.FLD_BCC, ExprEntry.TYPE.STRING));
            var_names.add(new VarTypeEntry(CS_Constants.FLD_SUBJECT, ExprEntry.TYPE.STRING));
            var_names.add(new VarTypeEntry(CS_Constants.FLD_BODY, ExprEntry.TYPE.STRING));
            var_names.add(new VarTypeEntry(CS_Constants.FLD_DATE, ExprEntry.TYPE.STRING));
            var_names.add(new VarTypeEntry(CS_Constants.FLD_ATTACHMENT, ExprEntry.TYPE.STRING));
            var_names.add(new VarTypeEntry(CS_Constants.FLD_ATTACHMENT_NAME, ExprEntry.TYPE.STRING));
            var_names.add(new VarTypeEntry(CS_Constants.FLD_SIZE, ExprEntry.TYPE.STRING));
            var_names.add(new VarTypeEntry(CS_Constants.FLD_HEADERVAR_NAME, ExprEntry.TYPE.STRING));
            var_names.add(new VarTypeEntry(CS_Constants.FLD_HEADERVAR_VALUE, ExprEntry.TYPE.STRING));

            //var_names.add(CS_Constants.FLD_META_ADDRESS);


            LogicFilter rf = new LogicFilter(var_names, last_filter);

            GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, rf);
            dlg.setVisible(true);

            if (rf.isOkay())
            {
                last_filter = rf.get_compressed_xml_list_data();

                String nice_txt = LogicFilter.get_nice_filter_text(last_filter);
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
        {
            table.selectAll();
        }
        else
        {
            table.clearSelection();
        }
    }//GEN-LAST:event_BT_TOGGLE_SELECTIONActionPerformed

    private void CB_ENTRIESActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_ENTRIESActionPerformed
    {//GEN-HEADEREND:event_CB_ENTRIESActionPerformed
        // TODO add your handling code here:
        do_filter_search();
    }//GEN-LAST:event_CB_ENTRIESActionPerformed

    private void PN_SIMPLEFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_PN_SIMPLEFocusGained
    {//GEN-HEADEREND:event_PN_SIMPLEFocusGained
        // TODO add your handling code here:
        search_mode = SIMPLE_SEARCH;
    }//GEN-LAST:event_PN_SIMPLEFocusGained

    private void PN_COMPLEXFocusGained(java.awt.event.FocusEvent evt)//GEN-FIRST:event_PN_COMPLEXFocusGained
    {//GEN-HEADEREND:event_PN_COMPLEXFocusGained
        // TODO add your handling code here:
        search_mode = COMPLEX_SEARCH;

    }//GEN-LAST:event_PN_COMPLEXFocusGained

    private void BT_ADDActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ADDActionPerformed
    {//GEN-HEADEREND:event_BT_ADDActionPerformed
        // TODO add your handling code here:
        // DEFAULT: ALL CONTAINS WORD
       /* if (!SCP_LIST.isVisible())
        {
        SCP_LIST.setVisible(true);
        my_dlg.pack();
        }*/
        simple_search_tablemodel.model.getChildren().add(new ExprEntry(simple_search_tablemodel.model.getChildren(), CS_Constants.FLD_FROM, "", ExprEntry.OPERATION.CONTAINS, ExprEntry.TYPE.STRING, false, false));
        simple_search_tablemodel.fireTableDataChanged();

    }//GEN-LAST:event_BT_ADDActionPerformed

    private void BT_DELActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_DELActionPerformed
    {//GEN-HEADEREND:event_BT_DELActionPerformed
        // TODO add your handling code here:
        int row = simple_search_table.getSelectedRow();
        if (row >= 0)
        {
            simple_search_tablemodel.model.getChildren().remove(row);
            simple_search_tablemodel.fireTableDataChanged();
        }
    }//GEN-LAST:event_BT_DELActionPerformed

    private void BT_SIMPLESEARCHActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_SIMPLESEARCHActionPerformed
    {//GEN-HEADEREND:event_BT_SIMPLESEARCHActionPerformed
        // TODO add your handling code here:
        boolean b1 = simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_VALUE).getCellEditor().stopCellEditing();
        boolean b2 = BT_SIMPLESEARCH.requestFocusInWindow();
        last_filter = simple_search_tablemodel.get_compressed_xml_list_data();
        TXTA_FILTER.setText(LogicFilter.get_nice_filter_text(last_filter));
        TXTA_FILTER.setCaretPosition(0);

        do_filter_search();

    }//GEN-LAST:event_BT_SIMPLESEARCHActionPerformed

    private void BT_VIEW_CONTENTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_VIEW_CONTENTActionPerformed
    {//GEN-HEADEREND:event_BT_VIEW_CONTENTActionPerformed
        // TODO add your handling code here:
        int row = table.getSelectedRow();
        row = sorter.convertRowIndexToModel(row);
        raw_view_mail(row);

    }//GEN-LAST:event_BT_VIEW_CONTENTActionPerformed
    static File last_dir;
    static File last_file;

    private void BT_OPEN_EMLActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OPEN_EMLActionPerformed
    {//GEN-HEADEREND:event_BT_OPEN_EMLActionPerformed
        // TODO add your handling code here:

        FileDialog fd = new FileDialog(my_dlg);
        fd.setMode(FileDialog.LOAD);

        fd.setLocation(my_dlg.getLocationOnScreen().x + 20, my_dlg.getLocationOnScreen().y + 20);


        if (last_dir != null)
        {
            fd.setDirectory(last_dir.getAbsolutePath());
        }
        if (last_file != null)
        {
            fd.setFile(last_file.getName());
        }

        fd.setVisible(true);

        String f_name = fd.getFile();
        if (f_name == null)
        {
            return;
        }

        last_file = new File(fd.getDirectory(), f_name);

        last_dir = last_file.getParentFile();

        run_open_mail(-1, last_file, false);
    }//GEN-LAST:event_BT_OPEN_EMLActionPerformed

    private void CB_VIEW_CONTENTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_VIEW_CONTENTActionPerformed
    {//GEN-HEADEREND:event_CB_VIEW_CONTENTActionPerformed
        // TODO add your handling code here:
        if (CB_VIEW_CONTENT.isSelected())
        {
            SPL_VIEW.setDividerLocation(DFLT_DIV_POS);
            int row = table.getSelectedRow();
            if (row >= 0)
            {
                row = sorter.convertRowIndexToModel(row);
                preview_mail(row);
            }
        }
        else
        {
            SPL_VIEW.setDividerLocation(1.0);
        }

        if (my_dlg != null)
        {
            my_dlg.pack();
        }
    }//GEN-LAST:event_CB_VIEW_CONTENTActionPerformed

    private void BT_OPEN_IN_MAILActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OPEN_IN_MAILActionPerformed
    {//GEN-HEADEREND:event_BT_OPEN_IN_MAILActionPerformed
        // TODO add your handling code here:
        int[] rowi = table.getSelectedRows();

        export_mail(null, rowi, "client");

    }//GEN-LAST:event_BT_OPEN_IN_MAILActionPerformed

    private void BT_FILTERSActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_FILTERSActionPerformed
    {//GEN-HEADEREND:event_BT_FILTERSActionPerformed
        // TODO add your handling code here:
        ViewFilterSaveDlg pnl = new ViewFilterSaveDlg(this, last_filter);
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
        dlg.set_next_location(BT_FILTERS);
        dlg.setVisible(true);
    }//GEN-LAST:event_BT_FILTERSActionPerformed

    private void TXT_QUICKSEARCHActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TXT_QUICKSEARCHActionPerformed
    {//GEN-HEADEREND:event_TXT_QUICKSEARCHActionPerformed
        // TODO add your handling code here:
        BT_SIMPLESEARCHActionPerformed(evt);
    }//GEN-LAST:event_TXT_QUICKSEARCHActionPerformed

    private void BT_HELP1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_HELP1ActionPerformed
    {//GEN-HEADEREND:event_BT_HELP1ActionPerformed
        // TODO add your handling code here:
        UserMain.open_help_panel(this.getClass().getSimpleName());
}//GEN-LAST:event_BT_HELP1ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ADD;
    private javax.swing.JButton BT_CLOSE;
    private javax.swing.JButton BT_DEL;
    private javax.swing.JButton BT_EXPORT;
    private javax.swing.JButton BT_FILTERS;
    private javax.swing.JButton BT_HELP1;
    private javax.swing.JButton BT_OPEN_EML;
    private javax.swing.JButton BT_OPEN_IN_MAIL;
    private javax.swing.JButton BT_RESTORE;
    private javax.swing.JButton BT_SIMPLESEARCH;
    private javax.swing.JButton BT_TOGGLE_SELECTION;
    private javax.swing.JButton BT_VIEW_CONTENT;
    private javax.swing.JComboBox CB_ENTRIES;
    private javax.swing.JCheckBox CB_VIEW_CONTENT;
    private javax.swing.JPanel PN_COMPLEX;
    private javax.swing.JPanel PN_PREVIEW;
    private javax.swing.JPanel PN_SIMPLE;
    private javax.swing.JScrollPane SCP_LIST;
    private javax.swing.JScrollPane SCP_PREVIEW;
    private javax.swing.JScrollPane SCP_TABLE;
    private javax.swing.JSplitPane SPL_VIEW;
    private javax.swing.JTabbedPane TBP_SEARCH;
    private javax.swing.JTextArea TXTA_FILTER;
    private javax.swing.JTextField TXT_QUICKSEARCH;
    private javax.swing.JLabel jLabel1;
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
                if (row == -1)
                    return;
                row = sorter.convertRowIndexToModel(row);

                open_mail(row);
            }

        }
        else if (e.getClickCount() == 1)
        {

            if (e.getSource() == table)
            {
                int row = table.rowAtPoint(e.getPoint());
                if (row == -1)
                    return;

                row = sorter.convertRowIndexToModel(row);
                int col = table.columnAtPoint(e.getPoint());

                if (col == MailTableModel.OPEN_ATTACH_COL)
                {
                    open_attachments(row);
                }
                else
                {
                    if (CB_VIEW_CONTENT.isSelected())
                    {
                        preview_mail(row);
                    }
                }
            }
        }
    }

    RFCMimeMail parseMailFile( File file, boolean encoded )
    {
        InputStream bais = null;

        try
        {
            FileInputStream fis = new FileInputStream(file);
            bais = new BufferedInputStream(fis);
            if (encoded)
            {
                bais = new EncodedMailInputStream(bais);
            }

            // CREATE AND PARSE MAIL
            RFCMimeMail mmsg = new RFCMimeMail();
            mmsg.parse(bais);
            return mmsg;
        }
        catch (Exception exc)
        {
        }
        finally
        {
            if (bais != null)
            {
                try
                {
                    bais.close();
                }
                catch (IOException iOException)
                {
                }
            }

        }
        return null;
    }

    void open_attachment( Part p, final File trg_file, boolean background )
    {

        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        try
        {
            InputStream is = p.getInputStream();
            bis = new BufferedInputStream(is);
            bos = new BufferedOutputStream(new FileOutputStream(trg_file));

            byte[] buffer = new byte[CS_Constants.STREAM_BUFFER_LEN];
            while (true)
            {
                int rlen = bis.read(buffer);
                if (rlen == -1)
                {
                    break;
                }

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

       
        Runnable r = new Runnable() {

            @Override
            public void run()
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
        };
        Thread thr = new Thread(r);
        thr.start();
        if (!background)
        {
            try
            {
                thr.join();
            }
            catch (InterruptedException interruptedException)
            {
            }
        }
    }

    void open_attachments( final int row )
    {
        if (!UserMain.self.check_for_role_option(my_dlg, OptCBEntry.READ))
        {
            return;
        }

        // CHECK FOR 4 EYES
        Role role = model.get_4_eyes_model(row);
        if (role != null)
        {
            if (!check_4eyes_login(role))
            {
                return;
            }
        }

        if (sw != null)
        {
            return;
        }

        sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                UserMain.self.show_busy(my_dlg, UserMain.Txt("Loading_mail") + "...");

                File tmp_file = run_download_mail(row, null, true);

                RFCMimeMail mmsg = parseMailFile(tmp_file, true);

                if (mmsg == null)
                {
                    if (!Main.get_bool_prop(Preferences.CACHE_MAILFILES, false))
                    {
                        tmp_file.delete();
                    }
                    return null;
                }

                sw = null;

                UserMain.self.hide_busy();


                String extension = ".tmp";
                int attCnt = mmsg.get_attachment_cnt();

                for (int a = 0; a < attCnt; a++)
                {
                    Part p = mmsg.get_attachment(a);

                    try
                    {
                        String fileName = p.getFileName();
                        // SKIP ATTACHMENTS w/o FILE
                        if (fileName == null)
                            continue;

                        int ext_idx = fileName.lastIndexOf(".");
                        if (ext_idx > 0)
                        {
                            extension = p.getFileName().substring(ext_idx);
                        }
                    }
                    catch (Exception messagingException)
                    {
                        continue;
                    }

                    String uuid = model.get_uid(row);

                    File trg_file = MailPreviewPanel.create_temp_file(uuid,  a, extension);
                    trg_file.deleteOnExit();

                    open_attachment(p, trg_file, true);

                }

                if (!Main.get_bool_prop(Preferences.CACHE_MAILFILES, false))
                {
                    tmp_file.delete();
                }
                return null;
            }
        };

        sw.start();
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
        {
            return;
        }

        int mandant = UserMain.self.get_act_mandant_id();
        String user = UserMain.self.get_act_username();
        String pwd = UserMain.self.get_act_pwd();

        final int entries = get_entries();

        final String cmd = "SearchMail CMD:open_filter MA:" + mandant + " US:'" + user + "' PW:'" + pwd + "' UL:"
                + UserMain.self.getUserLevel() + " FL:'" + last_filter + "' CNT:'" + entries + "' ";

        if (sw != null)
        {
            return;
        }

        sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                UserMain.self.show_busy(my_dlg, UserMain.Txt("Searching") + "...");

                fill_model_with_search(cmd, entries);

                UserMain.self.hide_busy();

                sw = null;



                return null;
            }
        };

        sw.start();




    }

    @Override
    public void editingStopped( ChangeEvent e )
    {
        //this.BT_SIMPLESEARCHActionPerformed(null);
    }

    @Override
    public void editingCanceled( ChangeEvent e )
    {
    }
    protected Role _4e_role = null;

    boolean is4eyes_logged_in( Role role )
    {
        if (_4e_role == null)
        {
            return false;
        }

        if (role != null && _4e_role != null && role.getId() != _4e_role.getId())
        {
            return false;
        }

        return true;
    }

    boolean check_4eyes_login( Role role )
    {

        if (is4eyes_logged_in(role))
        {
            return true;
        }


        if (Login4EyesPanel.check_login(role))
        {
            _4e_role = role;
        }
        return is4eyes_logged_in(role);
    }

    void reload_result( ArrayList<ArrayList<String>> result_array )
    {
        int start_id = result_array.size();

        FunctionCallConnect fcc = UserMain.fcc();
        int mandant = UserMain.self.get_act_mandant_id();

        ArrayList<String> field_list = model.get_field_list();

        String cmd = "SearchMail CMD:get MA:" + mandant + " ID:" + search_id + " ROW:" + start_id + " ROWS:" + MAX_FETCH_SIZE + " FLL:'";
        for (int i = 0; i < field_list.size(); i++)
        {
            if (i > 0)
            {
                cmd += ",";
            }
            cmd += field_list.get(i);
        }
        cmd += "'";

        String search_get_ret = fcc.call_abstract_function(cmd);

        if (search_get_ret.charAt(0) != '0')
        {
            UserMain.errm_ok(my_dlg, "SearchMail get gave " + search_get_ret);
            return;
        }

        CXStream xstream = new CXStream();
        Object o = xstream.fromXML(search_get_ret.substring(3));

        if (o instanceof ArrayList)
        {
            ArrayList<ArrayList<String>> ret_arr = (ArrayList<ArrayList<String>>) o;
            result_array.addAll(ret_arr);
        }
    }

    void set_filter( String filter )
    {
        last_filter = filter;
        simple_search_tablemodel.set_filter(filter);

        TXTA_FILTER.setText(LogicFilter.get_nice_filter_text(last_filter));
        TXTA_FILTER.setCaretPosition(0);
        TXT_QUICKSEARCH.setText("");

        do_filter_search();

    }

    String get_quick_search()
    {
        return TXT_QUICKSEARCH.getText();
    }
}
