package dimm.home.Rendering;

import dimm.home.Main;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;

public class GlossTable extends JTable
{

 
    public void embed_to_scrollpanel(JScrollPane SCP_TABLE)
    {        
        SCP_TABLE.setViewportView(this);
        SCP_TABLE.getViewport().setOpaque(false);
        
        Component coh = getTableHeader().getParent();
        if (coh instanceof JViewport)
        {
            JViewport vp = (JViewport) coh;
            vp.setOpaque(false);
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
        
        setDefaultRenderer(JButton.class, new ButtonCellRenderer());
        setDefaultRenderer(String.class, new OpaqueTextCellRenderer(false));
        setDefaultRenderer(Integer.class, new OpaqueTextCellRenderer(false));
        setDefaultRenderer(Long.class, new OpaqueTextCellRenderer(false));
        setDefaultRenderer(Boolean.class, new BoolButtonCellRenderer(
                "dimm/home/images/web_check.png", 
                "dimm/home/images/ok_empty.png"));
   
        getTableHeader().setDefaultRenderer(new HeaderCellRenderer());
        setOpaque(false);
        setRowHeight(20);


        setEnabled(true);
        
        setGridColor(Main.ui.get_nice_gray());
        setShowGrid(true);
        setRowMargin(4);
        setShowVerticalLines(false);
        getTableHeader().setOpaque(true);
        getTableHeader().setBackground(Color.BLACK);        
        
    }
}
