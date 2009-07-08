/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

import dimm.home.Models.OverviewModel;
import dimm.home.UserMain;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;


/**
 *
 * @author media
 */
public abstract class SQLOverviewDialog extends JDialog  implements MouseListener,  PropertyChangeListener
{
    protected SwingWorker sql_worker;
    
    protected OverviewModel model;

    protected UserMain main;
    protected GlossTable table;
    protected GlossPanel pg_painter;


    public abstract void set_table_header();
        
    public abstract void gather_sql_result();
    public abstract void gather_sql_result(long station_id);
                
    public void set_tabel_row_height()
    {
        packRows( table, table.getRowMargin() );
        table.repaint();
    }

    
    public SQLOverviewDialog( JFrame parent, boolean modal)
    {
        super( parent, modal);
    }

    @Override
    public void setSize(Dimension d)
    {
        pg_painter.setSize(d);
        set_tabel_row_height();
        super.setSize(d);
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

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        gather_sql_result();
        table.tableChanged(new TableModelEvent(table.getModel()) );
        set_tabel_row_height();

        this.repaint();
    }

}
