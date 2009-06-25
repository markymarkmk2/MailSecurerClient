/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

import dimm.home.Main;
import java.awt.Component;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import org.jdesktop.swingx.JXLabel;

/**
 *
 * @author mw
 */
public class OpaqueTextCellRenderer extends JXLabel implements TableCellRenderer
{
   

    public OpaqueTextCellRenderer(boolean opaque)
    {
        setOpaque(opaque);
        setForeground(Main.ui.get_nice_white());
        this.setLineWrap(true);
        this.setVerticalAlignment(TOP);       
    }


    @Override
    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        if (value instanceof Integer || value instanceof Long )
            this.setHorizontalAlignment(CENTER);
        else
            this.setHorizontalAlignment(LEFT);
        
        if (value != null)
            setText(value.toString());
        else
            setText(java.util.ResourceBundle.getBundle("dimm/home/SR_Properties").getString("-_invalid_-"));
        
        if (isSelected)
            setForeground(Main.ui.get_appl_selected_color());
        else
            setForeground(Main.ui.get_nice_white());

        return this;
    }
}
