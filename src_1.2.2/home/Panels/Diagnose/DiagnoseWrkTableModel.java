/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Panels.Diagnose;

import dimm.home.UserMain;
import java.awt.Insets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author mw
 */

abstract class WorkerRenderer
{
    String name;

    public WorkerRenderer( String name )
    {
        this.name = name;
    }


    abstract Object get_value( ArrayList<String> list, int column );
}

class ExchangeImportRenderer extends WorkerRenderer
{

    public ExchangeImportRenderer()
    {
        super( "ExchangeImportServer" );
    }

    @Override
    Object get_value( ArrayList<String> list, int column )
    {
        if (column < list.size())
            return list.get(column);

        switch (column)
        {
            case 0: return list.get(0);
        }

        return "";
    }
}

public final class DiagnoseWrkTableModel extends AbstractTableModel
{
    StorageDiagnose panel;

    JButton edit_bt;
    SimpleDateFormat sdf;
    ArrayList<ArrayList<String>> wrk_list;
    ArrayList<WorkerRenderer> renderers;



    String[] col_names = {UserMain.getString("Name"), UserMain.getString("Ok"), UserMain.getString("Status"), UserMain.getString("Used"), UserMain.getString("MaxCapacity"), UserMain.getString("PartitionFree"), UserMain.getString("PartitionUsed"), UserMain.getString("LastUsed")};
    Class[] col_classes = {String.class, Boolean.class, String.class, String.class, String.class, String.class, String.class, String.class  };

    public DiagnoseWrkTableModel(StorageDiagnose _panel, ArrayList<ArrayList<String>> result_list)
    {
        panel = _panel;
        this.wrk_list = result_list;

        edit_bt = create_table_button(  "/dimm/home/images/web_edit.png", panel );
        sdf = new SimpleDateFormat("dd.MM.yyy");

    }

    WorkerRenderer get_renderer( String r )
    {
        for (int i = 0; i < renderers.size(); i++)
        {
            WorkerRenderer renderer = renderers.get(i);
            if (renderer.name.compareTo(r) == 0)
                return renderer;
        }
        return null;
    }

    void set_result_list( ArrayList<ArrayList<String>> dse_list )
    {
        this.wrk_list = dse_list;
        this.fireTableDataChanged();
    }


    public JButton create_table_button(String rsrc, StorageDiagnose panel)
    {
        ImageIcon icn = new ImageIcon(this.getClass().getResource(rsrc));
        JButton bt = new JButton(icn);
        bt.addMouseListener(panel);
        bt.setBorderPainted(false);
        bt.setOpaque(false);
        bt.setMargin(new Insets(0, 0, 0, 0));
        bt.setContentAreaFilled(false);

        return bt;
    }

    @Override
    public boolean isCellEditable( int rowIndex, int columnIndex )
    {
        return (columnIndex == 0) ? true : false;
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
        return wrk_list.size();
    }

    @Override
    public int getColumnCount()
    {
        return col_names.length;
    }



    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        ArrayList<String> dse = wrk_list.get(rowIndex);

        Object o = get_wrk_renderer( dse, columnIndex );

        return o;
    }

    public int get_edit_column()
    {
        return col_names.length - 1;
    }

    // SETS THE WIDTH OF THE LAST TWO ICON-COLUMNS
    public void set_table_header( TableColumnModel cm )
    {
        if (getColumnCount() > get_edit_column())
        {
            cm.getColumn( get_edit_column() ).setMinWidth(60);
            cm.getColumn( get_edit_column() ).setMaxWidth(60);
        }
    }

    private Object get_wrk_renderer( ArrayList<String> list, int columnIndex )
    {
        String worker = list.get(0);
        WorkerRenderer renderer = get_renderer( worker );

        return renderer.get_value(list, columnIndex);
    }
}
