package dimm.home.Rendering;

import dimm.home.Main;
import dimm.home.UserMain;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;

public class GlossTable extends JTable
{

    public static Color get_highlight_rowcolor()
    {
        return Main.ui.get_nice_gray();
    }
 
    public void embed_to_scrollpanel(JScrollPane SCP_TABLE)
    {        
        SCP_TABLE.setViewportView(this);
        SCP_TABLE.getViewport().setOpaque(false);

        if (getTableHeader() != null)
        {
            Component coh = getTableHeader().getParent();
            if (coh instanceof JViewport)
            {
                JViewport vp = (JViewport) coh;
                vp.setOpaque(false);
            }
        }
    }
    
    @Override
    public boolean getScrollableTracksViewportWidth()
    {
        if (getParent() instanceof JViewport)
        {
            return ((JViewport) getParent()).getWidth() > getPreferredSize().width;
        }
        return false;
    }

    @Override
    public boolean getScrollableTracksViewportHeight()
    {
        if (getParent() instanceof JViewport)
        {
            return ((JViewport) getParent()).getHeight() > getPreferredSize().height;
        }
        return false;
    }
    public GlossTable()
    {
        this(false);
    }

    public GlossTable(boolean alt_colors)
    {
        super();
       

        setShowGrid(false);
        setShowVerticalLines(false);
        setShowHorizontalLines(false);
        //setCellSelectionEnabled(false);
        setColumnSelectionAllowed(false);
        setRowSelectionAllowed(true);
        
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JTableHeader thd = this.getTableHeader();
        thd.setPreferredSize(new Dimension(thd.getPreferredSize().width, 25));
        
        setDefaultRenderer(JButton.class, new ButtonCellRenderer(alt_colors));
        setDefaultRenderer(String.class, new OpaqueTextCellRenderer(false, alt_colors));
        setDefaultRenderer(Integer.class, new OpaqueTextCellRenderer(false, alt_colors));
        setDefaultRenderer(Long.class, new OpaqueTextCellRenderer(false, alt_colors));
        setDefaultRenderer(Boolean.class, new BoolButtonCellRenderer(alt_colors,
                "/dimm/home/images/web_check.png",
                "/dimm/home/images/ok_empty.png"));
   
        getTableHeader().setDefaultRenderer(new HeaderCellRenderer(alt_colors));
        setOpaque(false);
        setRowHeight(20);


        setEnabled(true);
        
        setGridColor(Main.ui.get_nice_gray());
        setShowGrid(true);
        setRowMargin(4);
        setShowVerticalLines(false);
        getTableHeader().setOpaque(true);
        getTableHeader().setBackground(UserMain.self.getTableHeaderBackground());
        
    }



    
    public static JButton create_table_button(String rsrc)
    {
        JButton bt;
        if (rsrc != null)
        {
            ImageIcon icn = new ImageIcon(UserMain.self.getClass().getResource(rsrc));
            bt = new JButton(icn);
        }
        else
        {
            bt = new JButton("");
        }
        //bt.addMouseListener(dlg);
        bt.setBorderPainted(false);
        bt.setOpaque(false);
        bt.setMargin(new Insets(0, 0, 0, 0));
        bt.setContentAreaFilled(false);

        return bt;
    }
}
