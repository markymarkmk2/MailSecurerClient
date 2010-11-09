/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Panels.Diagnose;

import dimm.home.UserMain;
import home.shared.Utilities.ParseToken;
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
    final int DATA_COL = 3;

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

   /*         stb.append("EXIMA");
        stb.append(i);
        stb.append(":");
        stb.append(mbie.mandant.getId());
        stb.append(" EXISI");
        stb.append(i);
        stb.append(":");
        stb.append(mbie.size);
        stb.append(" EXIST");
        stb.append(i);
        stb.append(":");
        stb.append(mbie.get_status());
        stb.append(" EXITM");
        stb.append(i);
        stb.append(":");
        stb.append(mbie.total_msg);
        stb.append("\n");
*/
    @Override
    Object get_value( ArrayList<String> list, int column )
    {
        if (column == DATA_COL)
        {
            StringBuilder sb = new StringBuilder();
            ParseToken pt = new ParseToken(list.get(column));
            for (int i = 0; ;i++)
            {
                long ma_id = pt.GetLongValue("EXIMA" + i + ":");
                if (ma_id <= 0)
                    break;
                if (i > 0)
                    sb.append("; ");
                
                long size = pt.GetLongValue("EXISI" + i + ":");
                String status = pt.GetString("EXIST" + i + ":");
                String mb_per_s = pt.GetString("EXISP" + i + ":");
                long act_msg = pt.GetLongValue("EXIAM" + i + ":");
                long total_msg = pt.GetLongValue("EXITM" + i + ":");

                sb.append("Job ").append(Integer.toString(i + 1)).append(": ").append(status).append(" ").append(act_msg).append("/").append(total_msg).append(" (").append(mb_per_s).append("MB/s)");
            }


            return sb.toString();
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



    String[] col_names = {UserMain.getString("Name"), UserMain.getString("Ok"), UserMain.getString("Status"), UserMain.getString("Details")};
    Class[] col_classes = {String.class, Boolean.class, String.class, String.class, JButton.class,  };

    public DiagnoseWrkTableModel(StorageDiagnose _panel, ArrayList<ArrayList<String>> result_list)
    {
        panel = _panel;
        this.wrk_list = result_list;

        edit_bt = create_table_button(  "/dimm/home/images/web_edit.png", panel );
        sdf = new SimpleDateFormat("dd.MM.yyy");


        renderers = new ArrayList<WorkerRenderer>();
        renderers.add( new ExchangeImportRenderer() );

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

        switch( columnIndex)
        {
            case 0: return dse.get(0);
            case 1: return dse.get(1).charAt(0) == '1' ? true : false;
            case 2: return dse.get(2);
            case 3: return get_wrk_renderer( dse, columnIndex );
            case 4: return edit_bt;

        }
        return "";
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
        if (renderer == null)
        {
            if (columnIndex < list.size())
                return list.get(columnIndex);
            return "";
        }

        return renderer.get_value(list, columnIndex);
    }
}
