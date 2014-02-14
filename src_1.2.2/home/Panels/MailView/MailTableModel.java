/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Panels.MailView;

import dimm.home.Main;
import dimm.home.Rendering.GlossTable;
import dimm.home.UserMain;
import home.shared.CS_Constants;
import home.shared.Utilities.SizeStr;
import home.shared.hibernate.Role;
import java.awt.Component;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import javax.mail.internet.MimeUtility;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author Administrator
 */
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
            label.setBackground(UserMain.get_nice_gray());
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
    static final int OPEN_ATTACH_COL = 4;
    static final int SUBJECT_COL = 5;
    static final int SIZE_COL = 6;
    static final int _4EYES_COL = 7;
    static final int UID_COL = 8;
    static final int BCC_COL = 9;
    static final int SHOW_COLUMNS = 8;


    MailViewPanel pnl;
    ArrayList<ArrayList<String>> result_array;
    int max_elems;
    ArrayList<String> field_list;

    JButton ic_attachment;
    JButton ic_view_attachment;
    JButton ic_no_attachment;

    String _4eyes_protected;

    MailTableModel(MailViewPanel _pnl,  ArrayList<ArrayList<String>> ret_arr, int max_elems)
    {
        super();

        pnl = _pnl;
        result_array = ret_arr;
        this.max_elems = max_elems;


        ic_attachment = GlossTable.create_table_button("/dimm/home/images/ic_attachment.png");
        ic_view_attachment = GlossTable.create_table_button("/dimm/home/images/ic_view_attachment.png");
        ic_no_attachment = GlossTable.create_table_button(null);

        field_list = new ArrayList<String>();

        field_list.add(CS_Constants.FLD_DATE);
        field_list.add(CS_Constants.FLD_FROM);
        field_list.add(CS_Constants.FLD_TO);
        field_list.add(CS_Constants.FLD_HAS_ATTACHMENT);
        field_list.add(CS_Constants.FLD_SUBJECT);
        field_list.add(CS_Constants.FLD_SIZE);
        field_list.add(CS_Constants.VFLD_4EYES);
        field_list.add(CS_Constants.FLD_UID_NAME);
        field_list.add(CS_Constants.FLD_BCC);

        _4eyes_protected = UserMain.Txt("Protected_by_4-eyes_principle");

    }
    int getFieldIndex( String fname )
    {
        for (int i = 0; i < field_list.size(); i++)
        {
           String n = field_list.get(i);
           if (n.equals(fname))
               return i;
        }
        return -1;
    }
    int getFieldIndex( int col )
    {
        switch( col )
        {
            case DATE_COL: return 0;
            case FROM_COL: return 1;
            case TO_COL: return 2;
            case ATTACH_COL: return 3;
            case OPEN_ATTACH_COL: return 3;
            case SUBJECT_COL: return 4;
            case SIZE_COL: return 5;
            case _4EYES_COL: return 6;
            case UID_COL: return 7;
            case BCC_COL: return 8;
        }
        return -1;
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
        if (columnIndex == OPEN_ATTACH_COL)
            return JButton.class;

        return String.class;
    }

    @Override
    public int getRowCount()
    {
        if (result_array == null)
            return 0;
        return max_elems;
    }

    @Override
    public int getColumnCount()
    {
        if (field_list == null)
            return 0;

        // DONT SHOW UID_ID
        return SHOW_COLUMNS;
    }

    Role get_4_eyes_model( int rowIndex )
    {
        Role role = null;
        String _4eyes_val = result_array.get(rowIndex).get(getFieldIndex(CS_Constants.VFLD_4EYES));
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

    boolean forbidden_4_eyes_auth( int rowIndex )
    {
        String _4eyes_val = result_array.get(rowIndex).get(_4EYES_COL);

        if (_4eyes_val != null && _4eyes_val.length() > 0 && Character.isDigit(_4eyes_val.charAt(0)) )
        {
            if  (UserMain.sqc() == null)
                return true;
        }
        return false;
    }



    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        while (rowIndex >= result_array.size())
        {
            pnl.reload_result( result_array );
        }

        String val = null;
        int fieldIdx = getFieldIndex(columnIndex);
        if (fieldIdx >= 0)
        {
            val = result_array.get(rowIndex).get(fieldIdx);
        }

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
        else if (columnIndex == OPEN_ATTACH_COL)
        {
            if (val != null && val.length() > 0 && val.charAt(0) == '1')
                return ic_view_attachment;

            return ic_no_attachment;
        }
        else if (columnIndex == SIZE_COL)
        {
            long size = Long.parseLong(val, 16);
            return new Long(size);
        }
        try
        {
            val = MimeUtility.decodeText(val);
            val = decode_undetected_utf8( val );
        }
        catch (Exception ex)
        {
        }

        return val;
    }

    String get_uid( int row )
    {
        return result_array.get(row).get(getFieldIndex(CS_Constants.FLD_UID_NAME));
    }
    String get_bcc( int row )
    {
        return result_array.get(row).get(getFieldIndex(CS_Constants.FLD_BCC));
    }

    private String decode_undetected_utf8( String val )
    {
        // TRY TO DETECT UNDECODED 2-BYTE UTF-8
        if (val.indexOf('Ã') == -1)
           return val;

        try
        {
            byte[] arr = val.getBytes("iso-8859-1");
            String ret = new String(arr, "utf-8");
            return ret;
        }
        catch (UnsupportedEncodingException unsupportedEncodingException)
        {
        }
        return val;
    }



}
