/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dimm.home.Rendering;

import dimm.home.Main;
import java.awt.Component;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

/**
 *
 * @author mw
 */
public class BoolButtonCellRenderer implements TableCellRenderer
{

    JButton ok_btn;
    JButton nok_btn;
    boolean alt_colors;

    public BoolButtonCellRenderer( boolean alt_colors, String icn_ok, String icn_nok )
    {
        ImageIcon ok_icn = new ImageIcon(this.getClass().getResource(icn_ok));
        ImageIcon nok_icn = new ImageIcon(this.getClass().getResource(icn_nok));
        this.alt_colors = alt_colors;

        ok_btn = new JButton(ok_icn);
        nok_btn = new JButton(nok_icn);
        nok_btn.setContentAreaFilled(false);
        ok_btn.setContentAreaFilled(false);
        ok_btn.setOpaque(false);
        nok_btn.setOpaque(false);
        ok_btn.setMargin(new Insets(0, 0, 0, 0));
        ok_btn.setBorderPainted(false);
        nok_btn.setMargin(new Insets(0, 0, 0, 0));
        nok_btn.setBorderPainted(false);


    }

    public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column )
    {
        if (value instanceof Boolean)
        {
            Boolean b = (Boolean) value;
            JButton label = (b.booleanValue()) ? ok_btn : nok_btn;

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
        return null;
    }
}

