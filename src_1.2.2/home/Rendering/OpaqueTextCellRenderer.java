/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

import dimm.home.Main;
import dimm.home.UserMain;
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

    boolean alt_colors;

    public OpaqueTextCellRenderer(boolean opaque, boolean alt_colors)
    {
        setOpaque(opaque);
        setForeground(Main.ui.get_foreground());
        this.alt_colors = alt_colors;
        this.setLineWrap(true);
        if (Main.ui.has_rendered_panels())
        {
            this.setVerticalAlignment(TOP);
        }
        
    }
    public OpaqueTextCellRenderer(boolean opaque)
    {
        this(opaque, false );
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
            setText(UserMain.getString("-_invalid_-"));
        
        if (isSelected)
            setForeground(Main.ui.get_selected_color());
        else
            setForeground(Main.ui.get_foreground());

        if (alt_colors && (row & 1) != 0)
        {
            setOpaque(true);
            setBackground(Main.ui.get_nice_gray());
        }
        else
        {
            setOpaque(false);
        }

        return this;
    }
}
