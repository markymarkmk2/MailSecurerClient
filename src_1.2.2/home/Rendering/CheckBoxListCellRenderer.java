/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

import dimm.home.Main;
import dimm.home.UserMain;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Administrator
 */
public class CheckBoxListCellRenderer extends JButton implements ListCellRenderer
{
    protected ImageIcon pressed;
    protected ImageIcon not_pressed;
    JCheckBox cb;
    
    
    public CheckBoxListCellRenderer()
    {
        pressed = new ImageIcon(this.getClass().getResource("/dimm/home/images/ok.png") );
        not_pressed = new ImageIcon(this.getClass().getResource("/dimm/home/images/ok_empty.png") );
        
        this.setIcon(new ImageIcon(this.getClass().getResource("/dimm/home/images/ok_empty.png")));
        this.setPressedIcon(new ImageIcon(this.getClass().getResource("/dimm/home/images/ok.png")));
        this.setIconTextGap(10);
        this.setOpaque(false);
        this.setForeground(Main.ui.get_foreground());
        this.setContentAreaFilled(false);
        this.setBorderPainted(false);
        this.setHorizontalAlignment(LEFT);
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
    {
        cb = (JCheckBox)value;
        this.setSelected(cb.isSelected());
        if (cb.isSelected())       
            this.setIcon(pressed);
        else
            this.setIcon(not_pressed);
        
        this.setText(cb.getText());
            
        return this;
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        if (!(cb instanceof KatButton))
        {
            super.paintComponent(g);
            return;
        }
        KatButton kb = (KatButton)cb;
        
        int txt_start = 30;
        
        ImageIcon ic = (ImageIcon)getIcon();
        g.drawImage(ic.getImage(), 3, 3, null);
        
        String txt = kb.get_kat_txt();

        if (kb.is_usertracklist())
            g.setColor(Main.ui.get_brownisch());
        
        g.drawString(txt, txt_start, getHeight() - 2);
        
        
                
    }
    
}
