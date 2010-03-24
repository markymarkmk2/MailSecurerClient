/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

import dimm.home.Main;
import dimm.home.UserMain;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author mw
 */

class HeaderLabel extends JLabel
{
    private int col;
    
    @Override
    protected void paintComponent(Graphics g)
    {
        //super.paintComponent(g);
        if (getCol() > 0)
            g.drawString( getText(), 5, getHeight() -8);
        else
            g.drawString( getText(), 3, getHeight() -8);
        Color old_color = g.getColor();
        g.setColor(Main.ui.get_appl_base_color());
        g.drawLine(0, getHeight(), getWidth(), getHeight());
        
        if (getCol() > 0)
        {            
            g.setColor(Main.ui.get_nice_gray());
            g.drawLine(0, 6, 0, getHeight() -6);
        }
        
        g.setColor(old_color);
    }

    public int getCol()
    {
        return col;
    }

    public void setCol(int col)
    {
        this.col = col;
    }
    
}
public class HeaderCellRenderer implements TableCellRenderer
{

    HeaderLabel label;
    boolean alt_colors;

    public HeaderCellRenderer( boolean alt_colors)
    {
        label = new HeaderLabel();
        label.setForeground(Main.ui.get_appl_base_color());
        label.setOpaque(false);
        label.setHorizontalAlignment(JLabel.LEFT);
        this.alt_colors = alt_colors;        
    }
    
    public HeaderCellRenderer()
    {
        this(false);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        label.setText(value.toString());
        label.setCol(column);

        if (alt_colors && (row & 1) != 0)
        {
            label.setOpaque(true);
            label.setBackground(Main.ui.get_nice_gray());
        }
        else
        {
            label.setOpaque(false);
        }

        return label;
    }
}

  
