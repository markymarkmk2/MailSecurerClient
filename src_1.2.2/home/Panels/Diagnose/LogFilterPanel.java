/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GetMailAddressPanel.java
 *
 * Created on 13.11.2009, 13:21:07
 */

package dimm.home.Panels.Diagnose;

import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.Rendering.GlossTable;
import dimm.home.ServerConnect.FunctionCallConnect;
import dimm.home.UserMain;
import home.shared.Utilities.LogConfigEntry;
import home.shared.Utilities.LogListener;
import home.shared.Utilities.ParseToken;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;



class LogFilterTableModel extends AbstractTableModel
{
    LogFilterPanel panel;
    ArrayList<LogConfigEntry> entry_list;
    SimpleDateFormat sdf;


    String[] col_names = {UserMain.getString("Logtype"), UserMain.getString("Visible"), UserMain.getString("LogLevel")};
    Class[] col_classes = {String.class,  JButton.class, String.class};

    public LogFilterTableModel(LogFilterPanel _panel, ArrayList<LogConfigEntry> _entry_list)
    {
        panel = _panel;
        entry_list = _entry_list;
        sdf = new SimpleDateFormat("dd.MM.yyy");

    }

   

    @Override
    public String getColumnName(int column)
    {
        return col_names[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        return col_classes[columnIndex];
    }

    @Override
    public int getRowCount()
    {
        if (entry_list == null)
            return 0;
        return entry_list.size();
    }

    @Override
    public int getColumnCount()
    {
        return col_names.length;
    }



    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (entry_list == null)
            return "";

        LogConfigEntry tck = entry_list.get(rowIndex);
        

        switch (columnIndex)
        {
            case 0: return UserMain.Txt( tck.typ );
            case 1: return panel.is_log_enabled( columnIndex );
            case 2: return LvlCBEntry.get_lvl_name( tck.level );

            default:
                return "???";
        }
    }

    @Override
    public void setValueAt( Object aValue, int rowIndex, int columnIndex )
    {
        LogConfigEntry tck = entry_list.get(rowIndex);

        if (columnIndex == 1 && aValue instanceof JButton)
        {
            JButton sel = (JButton)aValue;
            panel.set_log_enabled( columnIndex, sel.isSelected() );
        }
        
    }

    @Override
    public boolean isCellEditable( int row, int column )
    {
        if (column == 1)
            return true;
        return false;

    }
   
    public ArrayList<LogConfigEntry> get_entry_list()
    {
        return entry_list;
    }
}

/**
 *
 * @author mw
 */
public class LogFilterPanel extends GlossDialogPanel implements MouseListener,  PropertyChangeListener
{
    GlossTable table;
    
    ImageIcon ok_icn;
    ImageIcon nok_icn;
    ImageIcon empty_icn;
    LogFilterTableModel model;

    static ArrayList<Boolean> filter_entry_list = new ArrayList<Boolean>();


    /** Creates new form GetMailAddressPanel */
    public LogFilterPanel( )
    {
        initComponents();
        
        table = new GlossTable();        
        table.addMouseListener(this);

        // REGISTER TABLE TO SCROLLPANEL
        table.embed_to_scrollpanel( SCP_TABLE );
        
        JComboBox CB_LVL = new JComboBox();
        CB_LVL.addItem( new LvlCBEntry(LogListener.LVL_ERR));
        CB_LVL.addItem( new LvlCBEntry(LogListener.LVL_WARN));
        CB_LVL.addItem( new LvlCBEntry(LogListener.LVL_DEBUG));
        CB_LVL.addItem( new LvlCBEntry(LogListener.LVL_VERBOSE));


        read_config_list();
        table.getColumnModel().getColumn(1).setCellEditor( new DefaultCellEditor(  CB_LVL ) );


        if (filter_entry_list.size() != model.entry_list.size())
        {
            filter_entry_list.clear();
            for (int i = 0; i < model.entry_list.size(); i++)
            {
                filter_entry_list.add( new Boolean(true));
            }
        }
    }

    public static ArrayList<Boolean> get_filter_entry_list()
    {
        return filter_entry_list;
    }

    public void set_table_header()
    {
        TableColumnModel cm = table.getTableHeader().getColumnModel();
        cm.getColumn(0).setPreferredWidth(100);
        cm.getColumn(1).setPreferredWidth(100);            
    }


    public int getPreferredRowHeight(JTable table, int rowIndex, int margin)
    {
        // Get the current default height for all rows
        int height = table.getRowHeight();

        // Determine highest cell in the row
        for (int c=0; c<table.getColumnCount(); c++)
        {
            TableCellRenderer renderer = table.getCellRenderer(rowIndex, c);
            Component comp = table.prepareRenderer(renderer, rowIndex, c);
            int h = comp.getPreferredSize().height + 2*margin;

            height = Math.max(height, h);
        }
        return height;
    }

    public void packRows(JTable table, int margin)
    {
        for (int r=0; r<table.getRowCount(); r++)
        {
            // Get the preferred height
            int h = getPreferredRowHeight(table, r, margin);

            // Now set the row height using the preferred height

            table.setRowHeight(r, h);
            //System.out.println("Rowheight row " + r + " = " + h);

        }
    }
   

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BT_OKAY = new GlossButton();
        SCP_TABLE = new javax.swing.JScrollPane();

        BT_OKAY.setText(UserMain.Txt("Close")); // NOI18N
        BT_OKAY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKAYActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SCP_TABLE, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
                    .addComponent(BT_OKAY, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SCP_TABLE, javax.swing.GroupLayout.DEFAULT_SIZE, 516, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(BT_OKAY, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKAYActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKAYActionPerformed
    {//GEN-HEADEREND:event_BT_OKAYActionPerformed
        // TODO add your handling code here:
        my_dlg.setVisible(false);

    }//GEN-LAST:event_BT_OKAYActionPerformed

  
   


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_OKAY;
    private javax.swing.JScrollPane SCP_TABLE;
    // End of variables declaration//GEN-END:variables


  



    @Override
    public JButton get_default_button()
    {
        return BT_OKAY;
    }

   @Override
    public void mouseClicked(MouseEvent e)
    {

    }
    @Override
    public void mousePressed( MouseEvent e )
    {
    }

    @Override
    public void mouseReleased( MouseEvent e )
    {
    }

    @Override
    public void mouseEntered( MouseEvent e )
    {
    }

    @Override
    public void mouseExited( MouseEvent e )
    {
    }
    public void set_tabel_row_height()
    {
        packRows( table, table.getRowMargin() );
        table.repaint();
    }

  
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        read_config_list();

        table.tableChanged(new TableModelEvent(table.getModel()) );
        

        this.repaint();
    }
    
   

  
   

    private void read_config_list()
    {
        FunctionCallConnect fcc = UserMain.fcc();


        String ret = fcc.call_abstract_function("show_log CMD:get_config" , FunctionCallConnect.SHORT_TIMEOUT );

        model = new LogFilterTableModel( this, null);

        if (ret != null && fcc.get_last_err_code() == 0)
        {
            if (ret.charAt(0) == '0')
            {
                ParseToken pt = new ParseToken(ret.substring(3));
                Object o = pt.GetCompressedObject("CFG:");
                if (o instanceof ArrayList)
                {
                    ArrayList<LogConfigEntry> entry_list = (ArrayList<LogConfigEntry>)o;
                    model = new LogFilterTableModel( this, entry_list );
                    table.setModel(model);
                    set_table_header();
                    set_tabel_row_height();
                }
            }
        }
    }


   

    boolean is_log_enabled( int row )
    {
        return filter_entry_list.get(row);
    }

    void set_log_enabled( int row, boolean  v )
    {
        filter_entry_list.set(row, new Boolean( v) );
    }
}
