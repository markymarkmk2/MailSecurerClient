/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MultiCheckboxPanel.java
 *
 * Created on 01.11.2010, 09:32:10
 */
package dimm.home.Rendering;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;




class CheckboxListModel extends AbstractTableModel implements MouseListener
{

    String[] name_list;
    boolean[] val_array;

    protected String[] col_names = null;
    protected Class[] col_classes = null;


    CheckboxListModel(String[] name_list, boolean[] val_array, String titel )
    {
        this.name_list = name_list;
        this.val_array = val_array;


        col_names = new String[]  { "", titel };
        col_classes = new Class[] { JCheckBox.class, String.class };
    }
    String get_user(String entry)
    {
        int idx = entry.indexOf(" <");
        if (idx > 0)
        {
            return entry.substring(0, idx);
        }
        return entry;
    }
    String get_mail(String entry)
    {
        int idx1 = entry.indexOf("<");
        int idx2 = entry.indexOf(">");
        if (idx1 > 0 && idx2 > idx1)
        {
            return entry.substring(idx1 + 1, idx2);
        }
        return entry;
    }

    @Override
    public Object getValueAt( int rowIndex, int columnIndex )
    {
        switch (columnIndex)
        {
            case 0:
                return val_array[rowIndex];
            case 1:
                return name_list[rowIndex]; // ID

        }
        return "?";
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
    public int getColumnCount()
    {
        return col_names.length;
    }



    @Override
    public int getRowCount()
    {
        return name_list.length;
    }

    @Override
    public void mouseClicked( MouseEvent e )
    {
        GlossTable table = (GlossTable) e.getSource();
        int row = table.getSelectedRow();
        val_array[row] = !val_array[row];
        this.fireTableCellUpdated(row, 0);
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

    boolean is_selected( int row )
    {
        return val_array[row];
    }

    void unselect_all()
    {
        for (int i = 0; i < val_array.length; i++)
        {
            val_array[i] = false;
        }
        fireTableDataChanged();
    }
    void select_all()
    {
        for (int i = 0; i < val_array.length; i++)
        {
            val_array[i] = false;
        }
        fireTableDataChanged();
    }

    String[] get_name_array()
    {
        return name_list;
    }
    boolean[] get_val_array()
    {
        return val_array;
    }
}
class CheckBoxCellRenderer implements TableCellRenderer
{
    JCheckBox cb;

    public CheckBoxCellRenderer()
    {
        cb = new JCheckBox();
    }


    @Override
    public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column )
    {
        if (value instanceof Boolean)
        {
            cb.setSelected( ((Boolean)value).booleanValue() );
        }
        cb.setSelected( false);
        return cb;
    }
}
/**
 *
 * @author mw
 */
public class MultiCheckboxPanel extends GlossDialogPanel implements MouseListener
{

    private boolean okay;
    DefaultListModel list_model;
    
  /*  GlossTable table;
    CheckboxListModel model;
*/

    /** Creates new form MultiCheckboxPanel */
    public MultiCheckboxPanel(String[] name_list, boolean[] val_array, String titel)
    {

        if (val_array.length != name_list.length)
        {
            throw new RuntimeException("Wrong list / array len in MultiCheckboxPanel" );
        }
        initComponents();
        
        //model = new CheckboxListModel( name_list, val_array, titel);
        /*table = new GlossTable();
        table.setModel(model);
        table.embed_to_scrollpanel(SCP_LIST);
        table.addMouseListener(model);
        table.setDefaultRenderer(JCheckBox.class, new CheckBoxListCellRenderer());
         * */
        list_model = new DefaultListModel();
        JL_LIST.setModel(list_model);

        for (int i = 0; i < val_array.length; i++)
        {
            boolean b = val_array[i];
            String s = name_list[i];
            JCheckBox cb = new JCheckBox(s, b);
            cb.setSize(50, 20);
            list_model.addElement(cb);
        }
        JL_LIST.setCellRenderer( new CheckBoxListCellRenderer() );
        JL_LIST.addMouseListener(this);



   /*     TableColumnModel cm = table.getTableHeader().getColumnModel();
        cm.getColumn(0).setPreferredWidth(40);
        cm.getColumn(1).setPreferredWidth(150);       */
    }
    public void show_buttons( boolean b )
    {
        PN_BUTTONS.setVisible(b);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PN_BUTTONS = new javax.swing.JPanel();
        BT_ABORT = new javax.swing.JButton();
        BT_OK = new javax.swing.JButton();
        BT_SEL_ALL = new javax.swing.JButton();
        BT_UNSEL_ALL = new javax.swing.JButton();
        SCP_LIST = new javax.swing.JScrollPane();
        JL_LIST = new javax.swing.JList();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dimm/home/MA_Properties"); // NOI18N
        BT_ABORT.setText(bundle.getString("Abort")); // NOI18N
        BT_ABORT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ABORTActionPerformed(evt);
            }
        });

        BT_OK.setText(bundle.getString("Okay")); // NOI18N
        BT_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKActionPerformed(evt);
            }
        });

        BT_SEL_ALL.setText("+");
        BT_SEL_ALL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_SEL_ALLActionPerformed(evt);
            }
        });

        BT_UNSEL_ALL.setText("-");
        BT_UNSEL_ALL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_UNSEL_ALLActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_BUTTONSLayout = new javax.swing.GroupLayout(PN_BUTTONS);
        PN_BUTTONS.setLayout(PN_BUTTONSLayout);
        PN_BUTTONSLayout.setHorizontalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addComponent(BT_SEL_ALL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BT_UNSEL_ALL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 162, Short.MAX_VALUE)
                .addComponent(BT_ABORT)
                .addGap(18, 18, 18)
                .addComponent(BT_OK)
                .addContainerGap())
        );

        PN_BUTTONSLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ABORT, BT_OK});

        PN_BUTTONSLayout.setVerticalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_BUTTONSLayout.createSequentialGroup()
                .addGroup(PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OK)
                    .addComponent(BT_ABORT)
                    .addComponent(BT_SEL_ALL)
                    .addComponent(BT_UNSEL_ALL))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        JL_LIST.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        SCP_LIST.setViewportView(JL_LIST);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addComponent(PN_BUTTONS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(SCP_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                .addGap(16, 16, 16)
                .addComponent(PN_BUTTONS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ABORTActionPerformed
    {//GEN-HEADEREND:event_BT_ABORTActionPerformed
        okay = false;
        my_dlg.setVisible(false);

        // TODO add your handling code here:
}//GEN-LAST:event_BT_ABORTActionPerformed

    private void BT_OKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKActionPerformed
    {//GEN-HEADEREND:event_BT_OKActionPerformed
        // TODO add your handling code here:
        okay = true;
        my_dlg.setVisible(false);
}//GEN-LAST:event_BT_OKActionPerformed

    private void BT_SEL_ALLActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_SEL_ALLActionPerformed
    {//GEN-HEADEREND:event_BT_SEL_ALLActionPerformed
        // TODO add your handling code here:
        for (int i = 0; i < list_model.size(); i++)
        {
            ((JCheckBox)list_model.get(i)).setSelected(true);
        }
        JL_LIST.repaint();
    }//GEN-LAST:event_BT_SEL_ALLActionPerformed

    private void BT_UNSEL_ALLActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_UNSEL_ALLActionPerformed
    {//GEN-HEADEREND:event_BT_UNSEL_ALLActionPerformed
        // TODO add your handling code here:
        // TODO add your handling code here:
        for (int i = 0; i < list_model.size(); i++)
        {
            ((JCheckBox)list_model.get(i)).setSelected(false);
        }
        JL_LIST.repaint();

}//GEN-LAST:event_BT_UNSEL_ALLActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_OK;
    private javax.swing.JButton BT_SEL_ALL;
    private javax.swing.JButton BT_UNSEL_ALL;
    private javax.swing.JList JL_LIST;
    private javax.swing.JPanel PN_BUTTONS;
    private javax.swing.JScrollPane SCP_LIST;
    // End of variables declaration//GEN-END:variables

    @Override
    public JButton get_default_button()
    {
        return BT_OK;
    }

    /**
     * @return the okay
     */
    public boolean isOkay()
    {
        return okay;
    }

    /**
     * @param okay the okay to set
     */
    public void setOkay( boolean okay )
    {
        this.okay = okay;
    }

    public boolean[] get_val_array()
    {
         boolean[] val_array = new boolean[list_model.size()];

         for (int i = 0; i < val_array.length; i++)
        {
            val_array[i] = ((JCheckBox)list_model.get(i)).isSelected();
        }
        return val_array;
    }

    @Override
    public void mouseClicked( MouseEvent e )
    {
    }

    @Override
    public void mousePressed( MouseEvent e )
    {
        JCheckBox cb = (JCheckBox)JL_LIST.getSelectedValue();
        cb.setSelected( !cb.isSelected());
        cb.repaint();
        JL_LIST.repaint();

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

}
