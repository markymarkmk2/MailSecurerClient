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
public class CheckCellRenderer implements TableCellRenderer
{

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        if (value instanceof JButton)
        {
            return (JButton) value;
        }

        return null;
    }
}
