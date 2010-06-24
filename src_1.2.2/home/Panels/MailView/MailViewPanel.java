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
import home.shared.Utilities.SizeStr;
import dimm.home.Utilities.SwingWorker;
import dimm.home.native_libs.NativeLoader;
import home.shared.CS_Constants;
import home.shared.SQL.OptCBEntry;
import home.shared.filter.ExprEntry;
import home.shared.filter.ExprEntry.OPERATION;
import home.shared.filter.ExprEntry.TYPE;
import home.shared.filter.GroupEntry;
import home.shared.filter.LogicEntry;
import home.shared.filter.VarTypeEntry;
import home.shared.hibernate.Role;
import home.shared.mail.RFCMimeMail;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import java.util.Comparator;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BoxLayout;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;


class MailPreviewDlg extends GenericGlossyDlg
{
    UserMain main;
    MailPreviewDlg( UserMain parent, RFCMimeMail mail)
    {
        super( parent, true, new MailPreviewPanel(mail));
        main = parent;

        this.set_next_location(parent);

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

class LongComparator implements Comparator<Long>
{

    @Override
    public int compare( Long o1, Long o2 )
    {
        long l1 = o1.longValue();
        long l2 = o2.longValue();
        if (l1 > l2)
            return -1;
        if (l2 > l1)
            return 1;
        return 0;
    }
}
class UnixTimeCellRenderer implements TableCellRenderer
{
    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy  HH:mm");
    Date d = new Date(0);
    JLabel label = new JLabel();

    boolean alt_colors;

    public UnixTimeCellRenderer( boolean alt_colors )
    {
        this.alt_colors = alt_colors;
    }


    @Override
    public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column )
    {
        label.setText( value.toString());
        if (alt_colors && (row & 1) != 0)
        {
            label.setOpaque(true);
            label.setBackground(Main.ui.get_nice_gray());
        }
        else
        {
            label.setOpaque(false);
        }
        if (value instanceof Long)
        {
            Long l = (Long)value;
            d.setTime(l.longValue());
            label.setText( sdf.format(d) );
            return label;
        }

        return label;
    }
}

class SizeStrCellRenderer implements TableCellRenderer
{
    JLabel label = new JLabel();
    boolean alt_colors;

    public SizeStrCellRenderer( boolean alt_colors )
    {
        this.alt_colors = alt_colors;
    }

    @Override
    public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column )
    {
        if (alt_colors && (row & 1) != 0)
        {
            label.setOpaque(true);
            label.setBackground(Main.ui.get_nice_gray());
        }
        else
        {
            label.setOpaque(false);
        }

        if (value instanceof Long)
        {
            Long l = (Long)value;
            label.setText( SizeStr.format(l.doubleValue()) );
            return label;
        }
        label.setText( value.toString());


        return label;

    }
}
class MailTableModel extends AbstractTableModel
{
    static final int DATE_COL = 0;
    static final int FROM_COL = 1;
    static final int TO_COL = 2;
    static final int ATTACH_COL = 3;
    static final int SUBJECT_COL = 4;
    static final int SIZE_COL = 5;
    static final int _4EYES_COL = 6;

    MailViewPanel pnl;
    ArrayList<ArrayList<String>> result_array;
    ArrayList<String> field_list;

    JButton ic_attachment;
    JButton ic_no_attachment;

    String _4eyes_protected;

    MailTableModel(MailViewPanel _pnl,  ArrayList<ArrayList<String>> ret_arr)
    {
        super();

        pnl = _pnl;
        result_array = ret_arr;


        ic_attachment = GlossTable.create_table_button("/dimm/home/images/ic_attachment.png");
        ic_no_attachment = GlossTable.create_table_button(null);

        field_list = new ArrayList<String>();

        field_list.add(CS_Constants.FLD_DATE);
        field_list.add(CS_Constants.FLD_FROM);
        field_list.add(CS_Constants.FLD_TO);
        field_list.add(CS_Constants.FLD_HAS_ATTACHMENT);
        field_list.add(CS_Constants.FLD_SUBJECT);
        field_list.add(CS_Constants.FLD_SIZE);
        field_list.add(CS_Constants.VFLD_4EYES);

        _4eyes_protected = UserMain.Txt("Protected_by_4-eyes_principle");

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
            case DATE_COL: return UserMain.Txt("Date");
            case FROM_COL: return UserMain.Txt("From");
            case TO_COL: return UserMain.Txt("To");
            case SUBJECT_COL: return UserMain.Txt("Subject");
            case SIZE_COL: return UserMain.Txt("Size");
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        if (columnIndex == ATTACH_COL || columnIndex == _4EYES_COL)
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

    Role get_4_eyes_model( int rowIndex )
    {
        Role role = null;
        String _4eyes_val = result_array.get(rowIndex).get(_4EYES_COL);
        boolean is_4eyes = false;

        if (_4eyes_val != null && _4eyes_val.length() > 0 && Character.isDigit(_4eyes_val.charAt(0)) )
        {
            is_4eyes = true;
            try
            {
                role = UserMain.sqc().get_role(Integer.parseInt(_4eyes_val));
            }
            catch (Exception numberFormatException)
            {
                System.out.println("Invalid role col val: " + _4eyes_val);
            }
        }
        return role;
    }

    

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        String val = result_array.get(rowIndex).get(columnIndex);

        Role role = get_4_eyes_model( rowIndex );

        if (columnIndex == SUBJECT_COL && role != null && !pnl.is4eyes_logged_in(role))
        {
            return _4eyes_protected;
        }

        if ( columnIndex == 0)
        {
            long time = Long.parseLong(val, 16);
            return new Long(time);
        }
        else if (columnIndex == ATTACH_COL)
        {
            if (val != null && val.length() > 0 && val.charAt(0) == '1')
                return ic_attachment;

            return ic_no_attachment;
        }            
        else if (columnIndex == SIZE_COL)
        {            
            long size = Long.parseLong(val, 16);
            return new Long(size);
        }
        return val;
    }
}



class SimpleSearchEntryModel extends GroupEntry
{
    SimpleSearchEntryModel(ArrayList<LogicEntry> list)
    {
        if (list != null)
            children = list;
    }    
}
class ConditionCBEntry
{
    ExprEntry entry;

    public ConditionCBEntry( ExprEntry entry )
    {
        this.entry = entry;
    }

    static String get_nice_txt( ExprEntry e )
    {
        return (e.isNeg() ? UserMain.Txt("not") + " " : "") + UserMain.Txt(e.getName()) + " " + LogicFilter.get_op_nice_txt( e.getOperation(), e.getType());
    }
    @Override
    public String toString()
    {
        return get_nice_txt(entry);
    }
}
class NegCBEntry
{
    boolean neg;

    public NegCBEntry( boolean _neg )
    {
        this.neg = _neg;
    }

    public boolean isNeg()
    {
        return neg;
    }

    static String get_nice_txt( boolean n )
    {
        return n ? UserMain.Txt("not") + " " : "";
    }
    @Override
    public String toString()
    {
        return get_nice_txt(neg);
    }
}
class SimpleSearchTableModel extends AbstractTableModel implements MouseListener
{
    static int DELETE_COL = 3;

    MailViewPanel pnl;
    

    SimpleDateFormat sdf;
    JButton ic_delete;

    SimpleSearchEntryModel model;

    SimpleSearchTableModel(MailViewPanel _pnl,  SimpleSearchEntryModel _model)
    {
        super();

        pnl = _pnl;
        model = _model;

        sdf = new SimpleDateFormat("dd.MM.yyyy  HH:mm");

        ic_delete = GlossTable.create_table_button("/dimm/home/images/web_delete.png");     
    }

    public String get_compressed_xml_list_data()
    {
        String xml = null;
        CXStream xstr = new CXStream();

        ArrayList<LogicEntry> al = new ArrayList<LogicEntry>();

        // ADD ONLY VALID ENTRIES
        for (int i = 0; i < model.getChildren().size(); i++)
        {
            LogicEntry logicEntry = model.getChildren().get(i);
            if (logicEntry instanceof ExprEntry)
            {
                ExprEntry e = (ExprEntry)logicEntry;
                if (e.getValue().length() > 0)
                {
                    ExprEntry ee = new ExprEntry( al, e.getName(), e.getValue(), e.getOperation(), e.getType(), e.isNeg(), e.isPrevious_is_or() );
                    // ALL ARE ADDED AS "AND"
                    al.add(ee);
                }
            }
        }

        String compressed_list_str = ParseToken.BuildCompressedObjectString(al);
        return compressed_list_str;
    }


    @Override
    public boolean isCellEditable( int row, int column )
    {
        if (column < DELETE_COL)
            return true;
        return false;
        
    }

    @Override
    public String getColumnName(int column)
    {
        switch( column )
        {
            case 0: return UserMain.Txt("Condition");
            case 1: return "";
            case 2: return UserMain.Txt("Value");
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        if (columnIndex == DELETE_COL)
            return JButton.class;
        
        return String.class;
    }

    @Override
    public int getRowCount()
    {
        if (model == null)
            return 0;
        return model.getChildren().size();
    }

    @Override
    public int getColumnCount()
    {
        return 4;
    }

    


    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        LogicEntry le = model.getChildren().get(rowIndex);
        if (le instanceof ExprEntry)
        {
            ExprEntry ee = (ExprEntry) le;
            if (columnIndex == 0)
            {
                String txt = ConditionCBEntry.get_nice_txt( ee );
                return txt;
            }
            if (columnIndex == 1)
            {
                String txt = NegCBEntry.get_nice_txt( ee.isNeg() );
                return txt;
            }
            if (columnIndex == 2)
            {
                String txt = ee.getValue();
                return txt;
            }
            if (columnIndex == DELETE_COL)
            {
                return ic_delete;
            }
        }
        return "";
    }

    @Override
    public void setValueAt( Object aValue, int rowIndex, int columnIndex )
    {
        LogicEntry le = model.getChildren().get(rowIndex);
        if (le instanceof ExprEntry)
        {
            ExprEntry ee = (ExprEntry) le;
            if (columnIndex == 0 && aValue instanceof ConditionCBEntry)
            {
                ConditionCBEntry ccbe = (ConditionCBEntry)aValue;
                ee.setOperation( ccbe.entry.getOperation() );
                ee.setType( ccbe.entry.getType());
                ee.setName( ccbe.entry.getName());
                ee.setNeg( ccbe.entry.isNeg());
                ee.setPrevious_is_or( ccbe.entry.isPrevious_is_or() );
            }
            if (columnIndex == 1&& aValue instanceof NegCBEntry)
            {
                NegCBEntry ncb = (NegCBEntry)aValue;
                ee.setNeg( ncb.isNeg() );
            }
            if (columnIndex == 2)
            {
                ee.setValue(aValue.toString());
            }
        }
    }


    @Override
    public void mouseClicked( MouseEvent e )
    {
        if (e.getClickCount() == 1 && e.getSource() instanceof JTable)
        {
            JTable tb = (JTable)e.getSource();
            int row = tb.rowAtPoint(e.getPoint());
            int col = tb.columnAtPoint(e.getPoint());
            if (col == DELETE_COL && row >= 0 && row < model.getChildren().size())
            {
                model.getChildren().remove(row);
                fireTableDataChanged();
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
}
class SimpleSearchEditor extends  DefaultCellEditor
{
    public SimpleSearchEditor( JComboBox cb )
    {
        super( cb );
    }
}


class MailTableRowSorter extends TableRowSorter<MailTableModel>
{

    public MailTableRowSorter(MailTableModel m)
    {
        super(m);
    }

    LongComparator lc = new LongComparator();

    @Override
    public Comparator<?> getComparator( int column )
    {
        if (column == MailTableModel.DATE_COL || column == MailTableModel.SIZE_COL)
            return lc;
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

    public static final double DFLT_DIV_POS = 0.3;
    private static int SIMPLE_TAB_COL_CONDITION = 0;
    private static int SIMPLE_TAB_COL_NEG = 1;
    private static int SIMPLE_TAB_COL_VALUE = 2;
    private static int SIMPLE_TAB_COL_DELETE = 3;

    /** Creates new form MailViewPanel */
    public MailViewPanel()
    {
        initComponents();

        table = new GlossTable(true);
        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        table.addMouseListener(this);
        // REGISTER TABLE TO SCROLLPANEL
        table.embed_to_scrollpanel( SCP_TABLE );

        model = new MailTableModel(this, null);
        table.setModel(model);
        sorter = new MailTableRowSorter(model);
        table.setRowSorter(sorter);
        table.setShowGrid(false);
        
        table.getColumnModel().getColumn(MailTableModel.DATE_COL).setCellRenderer( new UnixTimeCellRenderer(true) );
        table.getColumnModel().getColumn(MailTableModel.SIZE_COL).setCellRenderer( new SizeStrCellRenderer(true) );

       


        simple_search_table = new GlossTable(true);
        simple_search_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        simple_search_table.embed_to_scrollpanel( SCP_LIST );

        simple_search_tablemodel = new SimpleSearchTableModel(this, new SimpleSearchEntryModel(null));
        simple_search_table.setModel(simple_search_tablemodel);
        simple_search_table.addMouseListener(simple_search_tablemodel);

        JComboBox CB_CONDITION = new JComboBox();
        CB_CONDITION.removeAllItems();
        /*CB_CONDITION.addItem( new ConditionCBEntry( new ExprEntry(null, CS_Constants.FLD_FROM, "", OPERATION.CONTAINS, TYPE.STRING, false, false)) );
        CB_CONDITION.addItem( new ConditionCBEntry( new ExprEntry(null, CS_Constants.FLD_TO, "", OPERATION.CONTAINS, TYPE.STRING, false, false)) );
        CB_CONDITION.addItem( new ConditionCBEntry( new ExprEntry(null, CS_Constants.FLD_SUBJECT, "", OPERATION.CONTAINS, TYPE.STRING, false, false)) );
        CB_CONDITION.addItem( new ConditionCBEntry( new ExprEntry(null, CS_Constants.FLD_BODY, "", OPERATION.CONTAINS, TYPE.STRING, false, false)) );
        CB_CONDITION.addItem( new ConditionCBEntry( new ExprEntry(null, CS_Constants.FLD_ATTACHMENT, "", OPERATION.CONTAINS, TYPE.STRING, false, false)) );*/
        CB_CONDITION.addItem( new ConditionCBEntry( new ExprEntry(null, CS_Constants.FLD_FROM, "", OPERATION.CONTAINS, TYPE.STRING, false, false)) );
        CB_CONDITION.addItem( new ConditionCBEntry( new ExprEntry(null, CS_Constants.VFLD_ALL, "", OPERATION.CONTAINS, TYPE.STRING, false, false)) );
        CB_CONDITION.addItem( new ConditionCBEntry( new ExprEntry(null, CS_Constants.VFLD_ALL, "", OPERATION.CONTAINS_SUBSTR, TYPE.STRING, false, false)) );
        CB_CONDITION.addItem( new ConditionCBEntry( new ExprEntry(null, CS_Constants.FLD_SUBJECT, "", OPERATION.CONTAINS, TYPE.STRING, false, false)) );
        CB_CONDITION.addItem( new ConditionCBEntry( new ExprEntry(null, CS_Constants.FLD_SUBJECT, "", OPERATION.CONTAINS_SUBSTR, TYPE.STRING, false, false)) );
        CB_CONDITION.addItem( new ConditionCBEntry( new ExprEntry(null, CS_Constants.VFLD_MAIL, "", OPERATION.CONTAINS, TYPE.STRING, false, false)) );
        CB_CONDITION.addItem( new ConditionCBEntry( new ExprEntry(null, CS_Constants.VFLD_MAIL, "", OPERATION.CONTAINS_SUBSTR, TYPE.STRING, false, false)) );
        CB_CONDITION.addItem( new ConditionCBEntry( new ExprEntry(null, CS_Constants.VFLD_TXT, "", OPERATION.CONTAINS, TYPE.STRING, false, false)) );
        CB_CONDITION.addItem( new ConditionCBEntry( new ExprEntry(null, CS_Constants.VFLD_TXT, "", OPERATION.CONTAINS_SUBSTR, TYPE.STRING, false, false)) );
        CB_CONDITION.addItem( new ConditionCBEntry( new ExprEntry(null, CS_Constants.FLD_ATTACHMENT_NAME, "", OPERATION.CONTAINS, TYPE.STRING, false, false)) );

        JComboBox CB_NEG = new JComboBox();
        CB_NEG.addItem( new NegCBEntry(false));
        CB_NEG.addItem( new NegCBEntry(true));

        JTextField TXT_VAL = new JTextField();
        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_NEG).setMinWidth(30);
        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_NEG).setMaxWidth(30);
        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_DELETE).setMinWidth(30);
        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_DELETE).setMaxWidth(30);
        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_CONDITION).setCellEditor( new DefaultCellEditor(  CB_CONDITION ) );
        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_NEG).setCellEditor( new DefaultCellEditor(  CB_NEG ) );
        DefaultCellEditor txt_editor = new DefaultCellEditor( TXT_VAL);
        txt_editor.setClickCountToStart(1);
        txt_editor.addCellEditorListener(this);
        TXT_VAL.addActionListener( new ActionListener()
        {

            @Override
            public void actionPerformed( ActionEvent e )
            {
                simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_VALUE).getCellEditor().stopCellEditing();
                last_filter = simple_search_tablemodel.get_compressed_xml_list_data();
                do_filter_search();
            }
        });

        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_VALUE).setCellEditor( txt_editor  );
       
        TBP_SEARCH.setSelectedIndex(SIMPLE_SEARCH);

        CB_VIEW_CONTENT.setSelected(true);


        if (CB_VIEW_CONTENT.isSelected())
        {
            SPL_VIEW.setDividerLocation(200);
        }
        else
        {
            SPL_VIEW.setDividerLocation(500);
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

    void fill_model_with_search( String cmd )
    {
        FunctionCallConnect fcc = UserMain.fcc();
        int mandant = UserMain.self.get_act_mandant_id();
 
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

        CXStream xstream = new CXStream();
        Object o = xstream.fromXML(search_get_ret.substring(3));

        if (o instanceof ArrayList)
        {
            ArrayList<ArrayList<String>> ret_arr = (ArrayList<ArrayList<String>>)o;

            model = new MailTableModel(this, ret_arr);
            sorter = new MailTableRowSorter(model);
            table.setRowSorter(sorter);
            table.setModel(model);
            table.getColumnModel().getColumn(MailTableModel.DATE_COL).setCellRenderer( new UnixTimeCellRenderer(true) );
            table.getColumnModel().getColumn(MailTableModel.SIZE_COL).setCellRenderer( new SizeStrCellRenderer(true) );

            model.fireTableDataChanged();

            table.getColumnModel().getColumn(MailTableModel.DATE_COL).setMinWidth(80);
            table.getColumnModel().getColumn(MailTableModel.DATE_COL).setPreferredWidth(120);
            table.getColumnModel().getColumn(MailTableModel.DATE_COL).setMaxWidth(180);
            table.getColumnModel().getColumn(MailTableModel.FROM_COL).setPreferredWidth(60);
            table.getColumnModel().getColumn(MailTableModel.TO_COL).setPreferredWidth(60);

            table.getColumnModel().getColumn(MailTableModel.ATTACH_COL).setMinWidth(20);
            table.getColumnModel().getColumn(MailTableModel.ATTACH_COL).setMaxWidth(20);
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
        if (!UserMain.self.check_for_role_option( my_dlg, OptCBEntry.READ))
        {
            return;
        }

        // CHECK FOR 4 EYES
        Role role = model.get_4_eyes_model( row );
        if (role != null)
        {
            if (!check_4eyes_login(role))
                return;
        }
        
        if (sw != null)
            return;

        sw = new SwingWorker() {

            @Override
            public Object construct()
            {
                UserMain.self.show_busy(my_dlg, UserMain.Txt("Loading_mail") + "...");

                File tmp_file = run_download_mail(row, null);

                sw = null;

                //UserMain.self.hide_busy();

                if (tmp_file != null)
                {
                    run_open_mail( row, tmp_file );
                    tmp_file.delete();
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
        if (!UserMain.self.check_for_role_option( my_dlg, OptCBEntry.READ))
        {
            return;
        }
        // CHECK FOR 4 EYES
        Role role = model.get_4_eyes_model( row );
        if (role != null)
        {
            if (!check_4eyes_login(role))
                return;
        }


        if (view_sw != null)
        {
            synchronized( preview_list)
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

                    File tmp_file = run_download_mail(work_row, null);


                    //UserMain.self.hide_busy();

                    if (tmp_file != null)
                    {
                        run_preview_mail( row, tmp_file );
                        tmp_file.delete();
                    }
                    synchronized( preview_list )
                    {
                        if (preview_list.size() == 0)
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
        if (!UserMain.self.check_for_role_option( my_dlg, OptCBEntry.READ))
        {
            return;
        }
        // CHECK FOR 4 EYES
        Role role = model.get_4_eyes_model( row );
        if (role != null)
        {
            if (!check_4eyes_login(role))
                return;
        }


        if (sw != null)
            return;

        sw = new SwingWorker() {

            @Override
            public Object construct()
            {
                UserMain.self.show_busy(my_dlg, UserMain.Txt("Loading_mail") + "...");

                File tmp_file = run_download_mail(row, null);

                sw = null;

                UserMain.self.hide_busy();

                if (tmp_file != null)
                {
                    GlossDialogPanel pnl = new GlossDialogPanel()
                    {
                        @Override
                        public JButton get_default_button()
                        {
                            return null;
                        }
                    };
                    JTextArea txta  = new JTextArea(132, 80);
                    pnl.setLayout( new BoxLayout(pnl, 1));
                    JScrollPane jsp = new JScrollPane(txta);
                    pnl.add( jsp );
                    
                    GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
                    byte[] buff = new byte[ (int)tmp_file.length() ];
                    try
                    {
                        FileInputStream fis = new FileInputStream(tmp_file);
                        fis.read(buff);
                        fis.close();
                    }
                    catch (IOException iOException)
                    {
                    }
                    String txt = new String(buff);
                    txta.setText(txt);                    
                    tmp_file.delete();
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
         if (sw != null)
            return;

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


    void run_export_mail( File dir, int[] rowi, boolean  open_in_client )
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
            Role role = model.get_4_eyes_model( row );
            if (role != null)
            {
                if (!check_4eyes_login(role))
                    continue;
            }

            String subject = table.getModel().getValueAt(row, MailTableModel.SUBJECT_COL).toString();
            subject = clean_fname(subject);
            File f = null;
            if (dir == null)
            {
                try
                {
                    f = File.createTempFile("mstemp", ".eml");
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
            run_download_mail(row, f.getAbsolutePath());
            if (open_in_client)
            {
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
                    exe.exec();
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
                Role role = model.get_4_eyes_model( row );
                if (role != null)
                {
                    if (!check_4eyes_login(role))
                        continue;
                }


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
            baos = null;

            sis.close();
            sis = null;


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
                UserMain.errm_ok(my_dlg, "Fehler beim Schließen der Mail: " + iOException.getMessage() );
            }
            
        }
        return null;
    }


    void run_preview_mail( int row, File file )
    {
        BufferedInputStream bais = null;

        try
        {
            FileInputStream fis = new FileInputStream( file );
            bais = new BufferedInputStream(fis);

            // CREATE AND PARSE MAIL
            RFCMimeMail mmsg = new RFCMimeMail();
            mmsg.parse(bais);

            // CREATE AND ADD PANEL
            MailPreviewPanel panel = new MailPreviewPanel(mmsg);
            JComponent pnl = panel.get_SPL_MAIL();
            
            PN_PREVIEW.removeAll();
            javax.swing.GroupLayout PN_PREVIEWLayout = new javax.swing.GroupLayout(PN_PREVIEW);
            PN_PREVIEW.setLayout(PN_PREVIEWLayout);
            PN_PREVIEWLayout.setHorizontalGroup(
                    PN_PREVIEWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(pnl, javax.swing.GroupLayout.DEFAULT_SIZE, 765, Short.MAX_VALUE));
            PN_PREVIEWLayout.setVerticalGroup(
                    PN_PREVIEWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(pnl, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE));

            PN_PREVIEW.repaint();

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

    
    void run_open_mail( int row, File file )
    {
        String subject = "Unknown";
        if ( row >= 0)
            subject = table.getModel().getValueAt(row, MailTableModel.SUBJECT_COL).toString();
        
        BufferedInputStream bais = null;

        try
        {
            FileInputStream fis = new FileInputStream( file );
            bais = new BufferedInputStream(fis);
          
            RFCMimeMail mmsg = new RFCMimeMail();           
            mmsg.parse(bais);

            MailPreviewDlg dlg = new MailPreviewDlg(UserMain.self, mmsg);
            bais.close();
            bais = null;

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

        BT_CLOSE = new GlossButton();
        BT_EXPORT = new GlossButton();
        CB_ENTRIES = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        BT_RESTORE = new GlossButton();
        BT_TOGGLE_SELECTION = new GlossButton();
        TBP_SEARCH = new javax.swing.JTabbedPane();
        PN_SIMPLE = new javax.swing.JPanel();
        BT_ADD = new GlossButton();
        BT_DEL = new GlossButton();
        SCP_LIST = new javax.swing.JScrollPane();
        BT_SIMPLESEARCH = new GlossButton();
        PN_COMPLEX = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TXTA_FILTER = new javax.swing.JTextArea();
        BT_VIEW_CONTENT = new javax.swing.JButton();
        BT_OPEN_EML = new javax.swing.JButton();
        CB_VIEW_CONTENT = new javax.swing.JCheckBox();
        SPL_VIEW = new javax.swing.JSplitPane();
        PN_PREVIEW = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        SCP_TABLE = new javax.swing.JScrollPane();
        BT_OPEN_IN_MAIL = new GlossButton();

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

        CB_ENTRIES.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10", "100", "1000" }));
        CB_ENTRIES.setSelectedIndex(0);
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

        BT_TOGGLE_SELECTION.setText(UserMain.Txt("Select_onoff")); // NOI18N
        BT_TOGGLE_SELECTION.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_TOGGLE_SELECTIONActionPerformed(evt);
            }
        });

        PN_SIMPLE.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                PN_SIMPLEFocusGained(evt);
            }
        });

        BT_ADD.setText("+");
        BT_ADD.setMargin(new java.awt.Insets(2, 1, 2, 1));
        BT_ADD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ADDActionPerformed(evt);
            }
        });

        BT_DEL.setText("-");
        BT_DEL.setMargin(new java.awt.Insets(2, 1, 2, 1));
        BT_DEL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_DELActionPerformed(evt);
            }
        });

        BT_SIMPLESEARCH.setText(UserMain.getString("Search")); // NOI18N
        BT_SIMPLESEARCH.setHorizontalTextPosition(javax.swing.SwingConstants.LEADING);
        BT_SIMPLESEARCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_SIMPLESEARCHActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_SIMPLELayout = new javax.swing.GroupLayout(PN_SIMPLE);
        PN_SIMPLE.setLayout(PN_SIMPLELayout);
        PN_SIMPLELayout.setHorizontalGroup(
            PN_SIMPLELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_SIMPLELayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BT_ADD, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BT_DEL, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 629, Short.MAX_VALUE)
                .addComponent(BT_SIMPLESEARCH)
                .addContainerGap())
            .addComponent(SCP_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, 772, Short.MAX_VALUE)
        );

        PN_SIMPLELayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ADD, BT_DEL});

        PN_SIMPLELayout.setVerticalGroup(
            PN_SIMPLELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_SIMPLELayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_SIMPLELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_ADD, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_DEL, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_SIMPLESEARCH))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SCP_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, 81, Short.MAX_VALUE))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 696, Short.MAX_VALUE)
                .addContainerGap())
        );
        PN_COMPLEXLayout.setVerticalGroup(
            PN_COMPLEXLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_COMPLEXLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_COMPLEXLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                    .addComponent(jLabel5))
                .addContainerGap())
        );

        TBP_SEARCH.addTab(UserMain.getString("Complex_Search"), PN_COMPLEX); // NOI18N

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

        SPL_VIEW.setDividerSize(3);
        SPL_VIEW.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        javax.swing.GroupLayout PN_PREVIEWLayout = new javax.swing.GroupLayout(PN_PREVIEW);
        PN_PREVIEW.setLayout(PN_PREVIEWLayout);
        PN_PREVIEWLayout.setHorizontalGroup(
            PN_PREVIEWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 765, Short.MAX_VALUE)
        );
        PN_PREVIEWLayout.setVerticalGroup(
            PN_PREVIEWLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
        );

        SPL_VIEW.setRightComponent(PN_PREVIEW);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_TABLE, javax.swing.GroupLayout.DEFAULT_SIZE, 765, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_TABLE, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
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
                        .addComponent(SPL_VIEW, javax.swing.GroupLayout.DEFAULT_SIZE, 767, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(32, 32, 32)
                        .addComponent(CB_ENTRIES, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 532, Short.MAX_VALUE)
                        .addComponent(CB_VIEW_CONTENT)
                        .addContainerGap())
                    .addComponent(TBP_SEARCH, javax.swing.GroupLayout.DEFAULT_SIZE, 777, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BT_TOGGLE_SELECTION)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_EXPORT)
                        .addGap(10, 10, 10)
                        .addComponent(BT_RESTORE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_OPEN_IN_MAIL)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_VIEW_CONTENT)
                        .addGap(18, 18, 18)
                        .addComponent(BT_OPEN_EML)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 130, Short.MAX_VALUE)
                        .addComponent(BT_CLOSE)
                        .addGap(10, 10, 10))))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(TBP_SEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, 148, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(CB_ENTRIES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CB_VIEW_CONTENT))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SPL_VIEW, javax.swing.GroupLayout.DEFAULT_SIZE, 418, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_CLOSE)
                    .addComponent(BT_TOGGLE_SELECTION)
                    .addComponent(BT_EXPORT)
                    .addComponent(BT_RESTORE)
                    .addComponent(BT_OPEN_IN_MAIL)
                    .addComponent(BT_VIEW_CONTENT)
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

        if (!UserMain.self.check_for_role_option( my_dlg, OptCBEntry.EXPORT))
        {
            return;
        }


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

        if (!UserMain.self.check_for_role_option( my_dlg, OptCBEntry.RESTORE))
        {
            return;
        }

        // GET SELECTED ROWS
        int[] rowi = table.getSelectedRows();

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < rowi.length; i++)
        {
            int row = row = sorter.convertRowIndexToModel(rowi[i]);

            // CHECK FOR 4 EYES
            Role role = model.get_4_eyes_model( row );
            if (role != null)
            {
                if (!check_4eyes_login(role))
                    break;
            }

            if (i > 0)
                sb.append( ",");
            sb.append( row );
        }
        if (sb.length() == 0)
            return;

        // GET MAILADDRESS
        GetMailAddressPanel pnl = new GetMailAddressPanel( UserMain.self.get_act_mailaliases() );
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
        dlg.set_next_location( my_dlg );

        dlg.setVisible(true);

        if (!pnl.isOkay())
            return;

        String mail = pnl.get_mail();

        UserMain.self.show_busy(my_dlg, UserMain.Txt("Sende_Mail...") );

        FunctionCallConnect fcc = UserMain.fcc();
        String ret = fcc.call_abstract_function("SearchMail CMD:send_mail ID:" + search_id + " TO:" + mail + " ROWLIST:" + sb.toString(), FunctionCallConnect.LONG_TIMEOUT);

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
            ArrayList<VarTypeEntry> var_names = new ArrayList<VarTypeEntry>();
            var_names.add( new VarTypeEntry( CS_Constants.FLD_FROM, ExprEntry.TYPE.STRING) );
            var_names.add( new VarTypeEntry( CS_Constants.FLD_TO, ExprEntry.TYPE.STRING) );
            var_names.add( new VarTypeEntry( CS_Constants.FLD_CC, ExprEntry.TYPE.STRING) );
            var_names.add( new VarTypeEntry( CS_Constants.FLD_BCC, ExprEntry.TYPE.STRING) );
            var_names.add( new VarTypeEntry( CS_Constants.FLD_SUBJECT, ExprEntry.TYPE.STRING) );
            var_names.add( new VarTypeEntry( CS_Constants.FLD_BODY, ExprEntry.TYPE.STRING) );
            var_names.add( new VarTypeEntry( CS_Constants.FLD_DATE, ExprEntry.TYPE.STRING) );
            var_names.add( new VarTypeEntry( CS_Constants.FLD_ATTACHMENT, ExprEntry.TYPE.STRING) );
            var_names.add( new VarTypeEntry( CS_Constants.FLD_ATTACHMENT_NAME, ExprEntry.TYPE.STRING) );
            var_names.add( new VarTypeEntry( CS_Constants.FLD_SIZE, ExprEntry.TYPE.STRING) );
            var_names.add( new VarTypeEntry( CS_Constants.FLD_HEADERVAR_NAME, ExprEntry.TYPE.STRING) );
            var_names.add( new VarTypeEntry( CS_Constants.FLD_HEADERVAR_VALUE, ExprEntry.TYPE.STRING) );
            
            //var_names.add(CS_Constants.FLD_META_ADDRESS);
            
           
            LogicFilter rf = new LogicFilter(var_names, last_filter );

            GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, rf);
            dlg.setVisible(true);

            if (rf.isOkay())
            {
                 last_filter = rf.get_compressed_xml_list_data();

                 String nice_txt = LogicFilter.get_nice_filter_text( last_filter );
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
        simple_search_tablemodel.model.getChildren().add( new ExprEntry(simple_search_tablemodel.model.getChildren(), CS_Constants.FLD_FROM, "", ExprEntry.OPERATION.CONTAINS, ExprEntry.TYPE.STRING, false, false));
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
        
        do_filter_search();

    }//GEN-LAST:event_BT_SIMPLESEARCHActionPerformed

    private void BT_VIEW_CONTENTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_VIEW_CONTENTActionPerformed
    {//GEN-HEADEREND:event_BT_VIEW_CONTENTActionPerformed
        // TODO add your handling code here:
        int row = table.getSelectedRow();
        row = sorter.convertRowIndexToModel(row);
        raw_view_mail( row );

    }//GEN-LAST:event_BT_VIEW_CONTENTActionPerformed

    static File last_dir;
    static File last_file;

    private void BT_OPEN_EMLActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OPEN_EMLActionPerformed
    {//GEN-HEADEREND:event_BT_OPEN_EMLActionPerformed
        // TODO add your handling code here:

        FileDialog fd = new FileDialog(my_dlg);
        fd.setMode(FileDialog.LOAD);

        fd.setLocation(my_dlg.getLocationOnScreen().x + 20, my_dlg.getLocationOnScreen().y + 20 );


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
            return;

        last_file = new File(fd.getDirectory(), f_name );

        last_dir = last_file.getParentFile();

        run_open_mail( -1, last_file );
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
                preview_mail( row );
            }
        }
        else
        {
            SPL_VIEW.setDividerLocation(1.0);
        }

        if (my_dlg != null)
            my_dlg.pack();
    }//GEN-LAST:event_CB_VIEW_CONTENTActionPerformed

    private void BT_OPEN_IN_MAILActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OPEN_IN_MAILActionPerformed
    {//GEN-HEADEREND:event_BT_OPEN_IN_MAILActionPerformed
        // TODO add your handling code here:
        int[] rowi = table.getSelectedRows();

        export_mail( null, rowi, "client" );

    }//GEN-LAST:event_BT_OPEN_IN_MAILActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ADD;
    private javax.swing.JButton BT_CLOSE;
    private javax.swing.JButton BT_DEL;
    private javax.swing.JButton BT_EXPORT;
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
    private javax.swing.JScrollPane SCP_TABLE;
    private javax.swing.JSplitPane SPL_VIEW;
    private javax.swing.JTabbedPane TBP_SEARCH;
    private javax.swing.JTextArea TXTA_FILTER;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables




    @Override
    public void mouseClicked( MouseEvent e )
    {
        if (e.getClickCount() == 2)
        {
            if (e.getSource() == table)
            {
                int row = table.rowAtPoint(e.getPoint());
                row = sorter.convertRowIndexToModel(row);
                
                open_mail( row );
            }
        }
        else if (e.getClickCount() == 1)
        {
            
            if (e.getSource() == table)
            {
                int row = table.rowAtPoint(e.getPoint());
                row = sorter.convertRowIndexToModel(row);

                if (CB_VIEW_CONTENT.isSelected())
                    preview_mail( row );
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

        int mandant = UserMain.self.get_act_mandant_id();
        String user = UserMain.self.get_act_username();
        String pwd = UserMain.self.get_act_pwd();

         int entries = get_entries();

         String cmd = "SearchMail CMD:open_filter MA:" + mandant + " US:'" + user + "' PW:'" + pwd + "' UL:" +
                    UserMain.self.getUserLevel() + " FL:'" + last_filter + "' CNT:'" + entries + "' ";

         fill_model_with_search(cmd);


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
    
    boolean is4eyes_logged_in(Role role)
    {
        if (_4e_role == null)
            return false;

        if (role != null && _4e_role != null && role.getId() != _4e_role.getId())
            return false;

        return true;
    }
    boolean check_4eyes_login(Role role)
    {

        if (is4eyes_logged_in(role))
            return true;


        if (Login4EyesPanel.check_login(role))
        {
            _4e_role = role;
        }
        return is4eyes_logged_in(role);
    }

}
