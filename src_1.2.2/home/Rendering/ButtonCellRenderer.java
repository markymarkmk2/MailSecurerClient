/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

import dimm.home.Main;
import java.awt.Component;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author mw
 */
public class ButtonCellRenderer implements TableCellRenderer
{
    boolean alt_colors;
    ButtonCellRenderer( boolean a )
    {
        alt_colors = a;
    }


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        if (value instanceof JButton)
        {
            JButton b = (JButton) value;

            if (alt_colors && (row & 1) != 0)
            {
                b.setOpaque(true);
                b.setBackground(GlossTable.get_highlight_rowcolor());
            }
            else
            {
                b.setOpaque(false);
            }

            return b;
        }
        return null;
    }
}
