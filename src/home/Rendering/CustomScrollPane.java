/*
 * CustomScrollPane.java
 *
 * Created on 30. November 2006, 15:43
 */

package dimm.home.Rendering;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

/**
 *
 * @author  Rolf
 */


public class CustomScrollPane extends JPanel
{
    protected JScrollBar m_vertSB;
    protected JScrollBar m_horzSB;
    protected CustomViewport m_viewport;
    protected JComponent m_comp;

    CustomScrollPaneDialog dialog;

    public CustomScrollPane(CustomScrollPaneDialog _dialog, JComponent comp)
    {
        setLayout(null);
        dialog = _dialog;

        m_viewport = new CustomViewport();
        m_viewport.setLayout(null);
        m_viewport.setBackground(java.awt.Color.white);

        add(m_viewport);
        m_comp = comp;
        m_viewport.add(m_comp);

        m_vertSB = new JScrollBar(
        JScrollBar.VERTICAL, 0, 0, 0, 0);
        m_vertSB.setUnitIncrement(5);
        add(m_vertSB);

        m_horzSB = new JScrollBar(
        JScrollBar.HORIZONTAL, 0, 0, 0, 0);
        m_horzSB.setUnitIncrement(5);
        add(m_horzSB);

        AdjustmentListener lst = new AdjustmentListener()
        {
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
                if (e.getSource() == m_vertSB)
                {
                    int m = m_vertSB.getMaximum();
                    int v = e.getValue();
                    Dimension d_component = m_comp.getSize();
                    Dimension d_vert_sb = m_vertSB.getSize();

                    v += m_vertSB.getVisibleAmount();
                    //d_vert_sb.height;
                    //System.out.println("V: " + v + " M: " + m + " Dm: " + d_component.height + " Dh: " + d_vert_sb.height);

                    if ( m > 0 && (v*100)/m > 90)
                    {
                        //System.out.println("V: " + v + " M: " + m + " Dh: " + d_vert_sb.height);
                        //System.out.println("Adding text");
                        dialog.add_text();
                    }
                }
                m_viewport.doLayout();
            }
        };
        m_vertSB.addAdjustmentListener(lst);
        m_horzSB.addAdjustmentListener(lst);
    }

    public void reset_scroll()
    {
        m_vertSB.setValue(0);
        m_horzSB.setValue(0);
        doLayout();
    }

    public void doLayout()
    {
        Dimension d_frame = getSize();

        Dimension d_component = m_comp.getPreferredSize();
        Dimension d_vert_sb = m_vertSB.getPreferredSize();
        Dimension d_hori_sb = m_horzSB.getPreferredSize();

        int w_frame = Math.max(d_frame.width - d_vert_sb.width-1, 0);
        int h_frame = Math.max(d_frame.height - d_hori_sb.height-1, 0);

        m_viewport.setBounds(0, 0, w_frame, h_frame);
        m_vertSB.setBounds(w_frame+1, 0, d_vert_sb.width, h_frame);
        m_horzSB.setBounds(0, h_frame+1, w_frame, d_hori_sb.height);

        d_vert_sb = m_vertSB.getSize();
        d_hori_sb = m_horzSB.getSize();

        int xs = d_component.width;
        int extent_x = w_frame;
        m_horzSB.setMaximum(xs + extent_x);
        m_horzSB.setBlockIncrement(extent_x);
        m_horzSB.setVisibleAmount(extent_x);
        m_horzSB.setEnabled(xs > 0);

        int ys = Math.max(d_component.height, 0);
        int extent_y = h_frame;
        m_vertSB.setMaximum(ys + extent_y);           
        m_vertSB.setBlockIncrement(extent_y);
        m_vertSB.setVisibleAmount(extent_y);            
        m_vertSB.setEnabled(ys > 0);
    }

    public Dimension getPreferredSize()
    {
        Dimension d_component = m_comp.getPreferredSize();
        Dimension d_vert_sb = m_vertSB.getPreferredSize();
        Dimension d_hori_sb = m_horzSB.getPreferredSize();
        Dimension d = new Dimension(d_component.width+d_vert_sb.width,
        d_component.height+d_hori_sb.height);
        return d;
    }

    class CustomViewport extends JPanel
    {
        public void doLayout()
        {
            Dimension d_component = m_comp.getPreferredSize();
            int x = m_horzSB.getValue();
            int y = m_vertSB.getValue();
            m_comp.setBounds(-x, -y, d_component.width, d_component.height);
        }
    }
}

