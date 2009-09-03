/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

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

    public BoolButtonCellRenderer(String icn_ok, String icn_nok)
    {
        ImageIcon ok_icn = new ImageIcon(this.getClass().getResource(icn_ok));
        ImageIcon nok_icn = new ImageIcon(this.getClass().getResource(icn_nok));
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

    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column)
    {
        if (value instanceof Boolean)
        {
            Boolean b = (Boolean) value;
            return (b.booleanValue()) ? ok_btn : nok_btn;
        }
        return null;
    }
}

