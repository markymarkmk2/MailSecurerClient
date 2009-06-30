/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Models;

import dimm.home.Rendering.SQLDialog;
import dimm.home.ServerConnect.SQLResult;
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
    
    protected UserMain main;
    JButton tonne_bt;
    JButton edit_bt;
    protected String[] col_names = null;
    protected Class[] col_classes = null;

    protected SQLResult sqlResult;
    

    public OverviewModel(UserMain _main, SQLDialog dlg)
    {
        main = _main;
        
        tonne_bt = create_table_button( "/dimm/home/images/web_delete.png", dlg );
        edit_bt = create_table_button(  "/dimm/home/images/web_edit.png", dlg );
              
        
    }
    
    public JButton create_table_button(String rsrc, SQLDialog dlg)
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
            return sqlResult.size();
        
        return 0;
    }

    @Override
    public int getColumnCount()
    {           
        // EDIT IST 2.LAST ROW!!!!
        if (UserMain.self.getUserLevel() < UserMain.UL_ADMIN)
            return col_names.length - 2;
        
        // DELETE IST LAST ROW!!!!
        if (UserMain.self.getUserLevel() < UserMain.UL_SYSADMIN)
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
        return (getSqlResult() == null || getSqlResult().size() <= row || row == -1);
    }
    
    // SETS THE WIDTH OF THE LAST TWO ICON-COLUMNS
    public void set_table_header( TableColumnModel cm )
    {
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
