/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Models;

import home.shared.SQL.SQLResult;
import dimm.home.Rendering.SQLOverviewDialog;
import dimm.home.UserMain;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;

/**
 *
 * @author Administrator
 */
public abstract class OverviewModel extends AbstractTableModel
{
    protected SQLOverviewDialog dlg;
    protected UserMain main;
    JButton tonne_bt;
    JButton edit_bt;
    protected String[] col_names = null;
    protected Class[] col_classes = null;

    protected SQLResult sqlResult;

    static final boolean first_col_visible = true;
    

    public OverviewModel(UserMain _main, SQLOverviewDialog _dlg)
    {
        main = _main;
        dlg = _dlg;
        
        tonne_bt = create_table_button( "/dimm/home/images/web_delete.png", dlg );
        edit_bt = create_table_button(  "/dimm/home/images/web_edit.png", dlg );
              
        
    }
    
    public JButton create_table_button(String rsrc, SQLOverviewDialog dlg)
    {
        ImageIcon icn = new ImageIcon(this.getClass().getResource(rsrc));
        JButton bt = new JButton(icn);
        bt.addMouseListener(dlg);
        bt.setBorderPainted(false);
        bt.setOpaque(false);
        bt.setMargin(new Insets(0, 0, 0, 0));
        bt.setContentAreaFilled(false);
        
        return bt;        
    }
    public void set_columns( String[] names, Class[] classes)
    {
        col_names = names;
        col_classes = classes;
    }
            
    
    public abstract String get_qry(long station_id);

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
        if (sqlResult != null)
            return sqlResult.getRows();
        
        return 0;
    }

    @Override
    public int getColumnCount()
    {           
        // EDIT IST 2.LAST ROW!!!!
        if (!UserMain.self.is_user() && !UserMain.self.is_admin())
            return col_names.length - 2;

        // EDIT IST 2.LAST ROW!!!!
        if (!UserMain.self.is_admin())
            return col_names.length - 1;
        
        
        return col_names.length;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (sqlResult == null)
            return null;
        
        if (columnIndex == (col_names.length - 2))
           return edit_bt;
        if (columnIndex == (col_names.length - 1))
           return tonne_bt;
        
        return null;
    }

    public SQLResult getSqlResult()
    {
        return sqlResult;
    }

    public void setSqlResult(SQLResult sqlResult)
    {
        this.sqlResult = sqlResult;
    }

    public boolean is_new(int row)
    {
        // THESE ARE THE CONDITIONS FOR THE DECISION "NEW RECORD"
        return (getSqlResult() == null || getSqlResult().getRows() <= row || row == -1);
    }
    public int get_row_by_id( int id)
    {
        return sqlResult.get_row_by_id(id);
    }
    
    // SETS THE WIDTH OF THE LAST TWO ICON-COLUMNS
    public void set_table_header( TableColumnModel cm )
    {
        if (!first_col_visible)
        {
            cm.getColumn( 0 ).setMinWidth(00);
            cm.getColumn( 0 ).setMaxWidth(00);
            cm.getColumn( 0 ).setPreferredWidth(00);
        }
        if (getColumnCount() > get_edit_column())
        {
            cm.getColumn( get_edit_column() ).setMinWidth(60);
            cm.getColumn( get_edit_column() ).setMaxWidth(60);
            
            if (getColumnCount() > get_del_column() )
            {
                cm.getColumn( get_del_column() ).setMinWidth(50);
                cm.getColumn( get_del_column() ).setMaxWidth(50);        
            }
        }
    }
    public int get_edit_column()
    {
        return col_names.length - 2;
    }
    public int get_del_column()
    {
        return col_names.length - 1;
    }
  
}
