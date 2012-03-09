/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Panels.MailView;

import dimm.home.Panels.LogicFilter;
import dimm.home.Rendering.GlossTable;
import dimm.home.UserMain;
import home.shared.CS_Constants;
import home.shared.Utilities.ParseToken;
import home.shared.filter.ExprEntry;
import home.shared.filter.ExprEntry.TYPE;
import home.shared.filter.GroupEntry;
import home.shared.filter.LogicEntry;
import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.AbstractTableModel;



class SimpleSearchEntryModel extends GroupEntry
{
    SimpleSearchEntryModel(ArrayList<LogicEntry> list)
    {
        if (list != null)
            children = list;
    }

    void set_new_filter( ArrayList<LogicEntry> al )
    {
        children = al;
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

/**
 *
 * @author Administrator
 */
public class SimpleSearchTableModel extends AbstractTableModel implements MouseListener
{
    static int DELETE_COL = 3;

    MailViewPanel pnl;

    TYPE valType = TYPE.STRING;

    static SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy  HH:mm");
    
    JButton ic_delete;

    SimpleSearchEntryModel model;

    public void setValType( TYPE valType )
    {
        this.valType = valType;
    }

    public TYPE getValType()
    {
        return valType;
    }



    SimpleSearchTableModel(MailViewPanel _pnl,  SimpleSearchEntryModel _model)
    {
        super();

        pnl = _pnl;
        model = _model;

        //sdf = new SimpleDateFormat("dd.MM.yyyy  HH:mm");

        ic_delete = GlossTable.create_table_button("/dimm/home/images/web_delete.png");
    }

    public String get_compressed_xml_list_data()
    {
        ArrayList<LogicEntry> al = new ArrayList<LogicEntry>();

        if (pnl.get_quick_search().length() > 0)
        {
            /*
            ExprEntry ee = new ExprEntry(null, CS_Constants.VFLD_ALL, pnl.get_quick_search(), OPERATION.CONTAINS, TYPE.STRING, false, false);
            // ALL ARE ADDED AS "AND"
            al.add(ee);*/
            String[] fields = CS_Constants.VFLD_ALL.split(",");
            String val = pnl.get_quick_search();

            String[] vals;
            // ADD ONLY VALID ENTRIES
            if (val.charAt(0) == '\"')
            {
                vals = val.split("\"");
            }
            else
            {
                vals = val.split(" ");
            }
            for (int v = 0; v < vals.length; v++)
            {
                if (vals[v].length() == 0)
                    continue;

                GroupEntry ge = new GroupEntry(al, false, false);
                for (int i = 0; i < fields.length; i++)
                {
                    String field = fields[i];
                    // ALL ARE ADDED AS "OR"
                    ExprEntry ee = new ExprEntry( ge.getChildren(), field, vals[v], ExprEntry.OPERATION.CONTAINS, ExprEntry.TYPE.STRING, /*neg*/false, /*is_or*/true );
                    ge.getChildren().add(ee);
                }
                al.add(ge);
            }
        }

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
                if (ee.getType() == TYPE.TIMESTAMP)
                {
                    Date d = null;
                    try
                    {
                        Long l = Long.parseLong(ee.getValue(), 16);

                        d = new Date(l);//SpinnerCellEditor.fmt.parse(ee.getValue());
                    }
                    catch (Exception parseException)
                    {
                        d = new Date();//SpinnerCellEditor.fmt.parse(ee.getValue());
                    }
                    return SimpleSearchTableModel.sdf.format(d);
                }
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
//                if (ee.getType() == TYPE.TIMESTAMP)
//                {
//                    if (aValue instanceof Date)
//                    {
//                         ee.setValue( Long.toString(((Date)aValue).getTime(), 16) );
//                    }
//                    else if(aValue instanceof Long)
//                    {
//                         ee.setValue( Long.toString( (Long)aValue, 16) );
//                    }
//                    else
//                    {
//                        Long l = Long.parseLong(aValue.toString(), 16);
//                        Date d = new Date(l);
//                        ee.setValue(  sdf.format(d) );
//                        ee.setValue(aValue.toString());
//                    }
//                }
//                else
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

    void set_filter( String filter )
    {
        Object o = ParseToken.DeCompressObject(filter);
        if (o instanceof ArrayList)
        {
            ArrayList<LogicEntry> al = (ArrayList<LogicEntry>) o;
            model.set_new_filter( al );

            fireTableDataChanged();

        }

    }
}
class SimpleSearchEditor extends  DefaultCellEditor
{
    public SimpleSearchEditor( JComboBox cb )
    {
        super( cb );
    }
}




class ValueCellEditor extends DefaultCellEditor
{

    SimpleSearchTableModel model;
    JTextField textField;
    JSpinner spinner;
    EditorDelegate textDelegate;
    EditorDelegate spinnerDelegate;

    void updateEditor()
    {
        editorComponent = (model.valType == TYPE.TIMESTAMP) ? spinner : textField;
    }
 /**
     * The protected <code>EditorDelegate</code> class.
     */
    protected class SpinnerDelegate extends EditorDelegate implements ChangeListener
    {

       /**
        * When an item's state changes, editing is ended.
        * @param e the action event
        * @see #stopCellEditing
        */
        @Override
        public void itemStateChanged(ItemEvent e) {
	    ValueCellEditor.this.stopCellEditing();
	}

        @Override
        public void stateChanged( ChangeEvent e )
        {
            //SpinnerCellEditor.this.stopCellEditing();
        }
    }
    public ValueCellEditor(SimpleSearchTableModel model, JTextField textField, final JSpinner spinner)
    {
        super( textField );
        this.model = model;
        this.textField = textField;
        this.spinner = spinner;

        textDelegate = delegate;


        //editorComponent = spinner;
	this.clickCountToStart = 2;
        spinnerDelegate = new SpinnerDelegate()
        {
            @Override
            public void setValue(Object value)
            {

                Date d = null;
                try
                {
                    Long l = Long.parseLong(value.toString(), 16);
                    d = new Date( l );
                }
                catch (Exception parseException)
                {
                    d = new Date();
                }
		spinner.setValue(d);
            }

            @Override
	    public Object getCellEditorValue()
            {
                Object o = spinner.getValue();
                if (o instanceof Date)
                {
                    Long l = new Long(((Date)o).getTime() );
                    return Long.toString(l, 16 );
                }
		return spinner.getValue();
	    }
        };
	spinner.addChangeListener((SpinnerDelegate)spinnerDelegate);
    }



    @Override
    public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int row, int column )
    {
        LogicEntry le = model.model.getChildren().get(row);

        if (le instanceof ExprEntry)
        {
            ExprEntry ex = (ExprEntry)le;
            if (ex.getType() == TYPE.TIMESTAMP)
            {
                delegate = spinnerDelegate;
                editorComponent = spinner;
                try
                {
                    Date d = SimpleSearchTableModel.sdf.parse(value.toString());
                    value = Long.toString(d.getTime(), 16);
                }
                catch (ParseException parseException)
                {
                }
            }
            else
            {
                delegate = textDelegate;
                editorComponent = textField;
            }
        }
        return super.getTableCellEditorComponent(table, value, isSelected, row, column);
    }

}