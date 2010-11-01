/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GetMailAddressPanel.java
 *
 * Created on 13.11.2009, 13:21:07
 */

package dimm.home.Panels;

import dimm.home.Models.OverviewModel;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.Rendering.GlossTable;
import dimm.home.UserMain;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.table.TableColumnModel;


class UserListModel extends OverviewModel implements MouseListener
{

    ArrayList<String> list;
    boolean selectable;
    boolean[] sel_list;


    UserListModel( UserMain _main, ArrayList<String> list, boolean selectable )
    {
        super(_main, null );
        this.list = list;
        this.selectable = selectable;
        sel_list = new boolean[list.size()];


        if (selectable)
        {
            String[] _col_names =
            {
                "", UserMain.getString("Username"), UserMain.getString("Username"), UserMain.getString("Mail"), UserMain.getString("Bearbeiten"), UserMain.getString("Löschen")
            };
            Class[] _col_classes =
            {
                JCheckBox.class, String.class, String.class, JButton.class, JButton.class
            };
            set_columns(_col_names, _col_classes);
        }
        else
        {
            String[] _col_names =
            {
                UserMain.getString("Username"), UserMain.getString("Mail"), UserMain.getString("Bearbeiten"), UserMain.getString("Löschen")
            };
            Class[] _col_classes =
            {
                String.class, String.class, JButton.class, JButton.class
            };
            set_columns(_col_names, _col_classes);
        }

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

        if (selectable)
        {
            switch (columnIndex)
            {
                case 0:
                    return sel_list[rowIndex];
                case 1:
                    return get_user(list.get(rowIndex)); // ID
                case 2:
                    return get_mail(list.get(rowIndex));
                default:
                    return super.getValueAt(rowIndex, columnIndex);
            }
        }
        else
        {
            switch (columnIndex)
            {
                case 0:
                    return get_user(list.get(rowIndex)); // ID
                case 1:
                    return get_mail(list.get(rowIndex));
                default:
                    return super.getValueAt(rowIndex, columnIndex);
            }
        }
    }


    @Override
    public int getColumnCount()
    {
        return col_names.length - 2;
    }

    @Override
    public String get_qry( long station_id )
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public int getRowCount()
    {
        return list.size();
    }

    @Override
    public void mouseClicked( MouseEvent e )
    {
        GlossTable table = (GlossTable) e.getSource();
        int row = table.getSelectedRow();
        sel_list[row] = !sel_list[row];
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
        if (!selectable)
            return false;

        return sel_list[row];
    }

    void unselect_all()
    {
        for (int i = 0; i < sel_list.length; i++)
        {
            sel_list[i] = false;
        }
        fireTableDataChanged();
    }
    void select_all()
    {
        for (int i = 0; i < sel_list.length; i++)
        {
            sel_list[i] = false;
        }
        fireTableDataChanged();
    }

    ArrayList<String> get_list()
    {
        return list;
    }


}
/**
 *
 * @author mw
 */
public class UserListPanel extends GlossDialogPanel
{

    private boolean okay = false;
    GlossTable table;
    UserListModel model;
    boolean selectable;

    /** Creates new form GetMailAddressPanel */
    public UserListPanel( ArrayList<String> list)
    {
        this( list, false );
    }
    public UserListPanel( ArrayList<String> list, boolean selectable)
    {
        this.selectable = selectable;
        initComponents();

        model = new UserListModel( UserMain.self, list, selectable);
        table = new GlossTable();
        table.setModel(model);
        table.embed_to_scrollpanel(jScrollPane1);
        if (selectable)
            table.addMouseListener(model);



        TableColumnModel cm = table.getTableHeader().getColumnModel();
        cm.getColumn(0).setPreferredWidth(60);
        cm.getColumn(1).setPreferredWidth(150);
        model.set_table_header(cm);
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
        jScrollPane1 = new javax.swing.JScrollPane();
        BT_SEL_ALL = new javax.swing.JButton();
        BT_UNSEL_ALL = new javax.swing.JButton();

        BT_OKAY.setText(UserMain.Txt("Close")); // NOI18N
        BT_OKAY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKAYActionPerformed(evt);
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(BT_SEL_ALL)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(BT_UNSEL_ALL)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                        .addComponent(BT_OKAY)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_SEL_ALL, BT_UNSEL_ALL});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OKAY)
                    .addComponent(BT_SEL_ALL)
                    .addComponent(BT_UNSEL_ALL))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKAYActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKAYActionPerformed
    {//GEN-HEADEREND:event_BT_OKAYActionPerformed
        // TODO add your handling code here:
        setOkay(true);
        my_dlg.setVisible(false);

    }//GEN-LAST:event_BT_OKAYActionPerformed

    private void BT_SEL_ALLActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_SEL_ALLActionPerformed
    {//GEN-HEADEREND:event_BT_SEL_ALLActionPerformed
        // TODO add your handling code here:
        model.unselect_all();

    }//GEN-LAST:event_BT_SEL_ALLActionPerformed

    private void BT_UNSEL_ALLActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_UNSEL_ALLActionPerformed
    {//GEN-HEADEREND:event_BT_UNSEL_ALLActionPerformed
        // TODO add your handling code here:
        model.select_all();
    }//GEN-LAST:event_BT_UNSEL_ALLActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_OKAY;
    private javax.swing.JButton BT_SEL_ALL;
    private javax.swing.JButton BT_UNSEL_ALL;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

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
    public boolean is_selected( int row )
    {
        if (!selectable)
            return false;
        return model.is_selected( row );
    }


    @Override
    public JButton get_default_button()
    {
        return BT_OKAY;
    }

    public ArrayList<String> get_selected_users()
    {
        ArrayList<String> list = new ArrayList<String>();

        for (int i = 0; i < model.getRowCount(); i++)
        {
            if (model.is_selected(i))
            {
                list.add( model.get_list().get(i) );
            }
        }
        return list;
    }

}
