/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

import dimm.home.Models.OverviewModel;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.TableCellRenderer;


/**
 *
 * @author media
 */
public abstract class SQLDialog extends JDialog  implements MouseListener
{
    protected SwingWorker sql_worker;
    
    protected OverviewModel model;

    public abstract void set_table_header();
        
    public abstract void gather_sql_result();
    public abstract void gather_sql_result(long station_id);
                
    public abstract void set_tabel_row_height();
    
    public SQLDialog( JFrame parent, boolean modal)
    {
        super( parent, modal);
    }
    
 @Override
    public void setVisible( boolean b )
    {
        if (b)
        {
            if (sql_worker != null)
            {
                try
                {
                    if ( !sql_worker.isDone() )
                    {
                        sql_worker.get();
                    }
                    sql_worker = null;
                } catch ( Exception ex )
                {
                }
            }
        }
        super.setVisible(b);
    }
 
    public OverviewModel get_model()
    {
        return model; 
    }
    
    protected SwingWorker create_sql_worker()
    {
        sql_worker = new SwingWorker()
        {

            @Override
            protected Object doInBackground() throws Exception
            {
                 set_table_header();
        
                 gather_sql_result();
                
                 set_tabel_row_height();   
                 
                 return null;
            }        
        };
        sql_worker.execute(); 
        return sql_worker;
    }
    public void wait_for_sql_worker()
    {
        try
        {
            if ( sql_worker != null )
            {
                sql_worker.get();
            }
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
    }
    
    public int getPreferredRowHeight(JTable table, int rowIndex, int margin) 
    {
        // Get the current default height for all rows
        int height = table.getRowHeight();
    
        // Determine highest cell in the row
        for (int c=0; c<table.getColumnCount(); c++) 
        {
            TableCellRenderer renderer = table.getCellRenderer(rowIndex, c);
            Component comp = table.prepareRenderer(renderer, rowIndex, c);
            int h = comp.getPreferredSize().height + 2*margin;
            
            height = Math.max(height, h);
        }
        return height;
    }
        
    public void packRows(JTable table, int margin) 
    {
        for (int r=0; r<table.getRowCount(); r++) 
        {
            // Get the preferred height
            int h = getPreferredRowHeight(table, r, margin);
    
            // Now set the row height using the preferred height
            
            table.setRowHeight(r, h);
            //System.out.println("Rowheight row " + r + " = " + h);
            
        }
    }           
    
      
    
    abstract public void mouseClicked(MouseEvent e);

    public void mousePressed(MouseEvent e)
    {
    }

    public void mouseReleased(MouseEvent e)
    {
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }    
}
