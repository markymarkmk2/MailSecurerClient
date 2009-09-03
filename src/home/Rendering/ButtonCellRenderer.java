/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

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

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        if (value instanceof JButton)
        {
            JButton b = (JButton) value;
            return b;
        }
        return null;
    }
}
