/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Panels.Diagnose;

import dimm.home.UserMain;
import dimm.home.Utilities.DateStr;
import home.shared.CS_Constants;
import home.shared.SQL.SQLResult;
import home.shared.Utilities.ParseToken;
import home.shared.Utilities.SizeStr;
import home.shared.hibernate.DiskArchive;
import home.shared.hibernate.DiskSpace;
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

class DS_StatusEntry
{
    DiskSpace ds;
    long docs;
    long capacity;
    long free_space;
    long total_space;
    long last_mod;

    public DS_StatusEntry( DiskArchive da, String line)
    {
        // "TP:DS ID:" + ds.getId() + " PA:\"" + ds.getPath() + "\" + ST:" + ds.getStatus() + " CNT:" + docs + " CAP:" + dsi.getCapacity() + "FS:" + free_space + " TS:" + total_space + " LM:" + last_mod + "\n";

        ParseToken pt = new ParseToken(line);

        SQLResult<DiskSpace> ds_result = UserMain.sqc().get_ds_result();

        int ds_id = (int)pt.GetLongValue("ID:");
        for (int i = 0; i < ds_result.getRows(); i++)
        {
            DiskSpace _ds = ds_result.get(i);
            if (_ds.getId() == ds_id)
            {
                ds = _ds;
                break;
            }
        }
        docs = pt.GetLongValue("CNT:");
        capacity = pt.GetLongValue("CAP:");
        free_space = pt.GetLongValue("FS:");
        total_space = pt.GetLongValue("TS:");
        last_mod = pt.GetLongValue("LM:");
    }
    String get_da_name()
    {
        return ds.getDiskArchive().getName();
    }
    boolean is_offline()
    {
        if ((Integer.parseInt(ds.getFlags()) & CS_Constants.DS_DISABLED) == CS_Constants.DS_DISABLED)
            return true;

        if ((Integer.parseInt(ds.getDiskArchive().getFlags()) & CS_Constants.DA_DISABLED) == CS_Constants.DS_DISABLED)
            return true;

        return false;
    }
}


class DiagnoseDSTableModel extends AbstractTableModel
{
    StorageDiagnose panel;

    JButton edit_bt;
    SimpleDateFormat sdf;
    ArrayList<DS_StatusEntry> dse_list;



    String[] col_names = {UserMain.getString("DiskArchive"), UserMain.getString("Path"), UserMain.getString("Docs"), UserMain.getString("Used"), UserMain.getString("MaxCapacity"), UserMain.getString("PartitionFree"), UserMain.getString("PartitionSize"), UserMain.getString("LastUsed")};
    Class[] col_classes = {String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class  };

    DiagnoseDSTableModel(StorageDiagnose _panel, ArrayList<DS_StatusEntry> dse_list)
    {
        panel = _panel;
        this.dse_list = dse_list;

        edit_bt = create_table_button(  "/dimm/home/images/web_edit.png", panel );
        sdf = new SimpleDateFormat("dd.MM.yyy");

    }

    void set_dse_list( ArrayList<DS_StatusEntry> dse_list )
    {
        this.dse_list = dse_list;
        this.fireTableDataChanged();
    }


    public final JButton create_table_button(String rsrc, StorageDiagnose panel)
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
        return dse_list.size();
    }

    @Override
    public int getColumnCount()
    {
        return col_names.length;
    }



    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        DS_StatusEntry dse = dse_list.get(rowIndex);


        switch (columnIndex)
        {
            case 0:
                return dse.get_da_name();
            case 1:
                return dse.ds.getPath();
            case 2:
                return new SizeStr( dse.docs );
            case 3:
                return new SizeStr( dse.capacity );
            case 4:
                return dse.ds.getMaxCapacity();
            case 5:
                return new SizeStr( dse.free_space );
            case 6:
                return new SizeStr( dse.total_space );
            case 7:
                return new DateStr( dse.last_mod / 1000);



            default:
                return "???";
        }
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
}
