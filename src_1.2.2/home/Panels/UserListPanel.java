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
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.table.TableColumnModel;


class UserListModel extends OverviewModel
{

    ArrayList<String> list;


    UserListModel( UserMain _main, ArrayList<String> list )
    {
        super(_main, null );
        this.list = list;

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
                return get_user(list.get(rowIndex)); // ID
            case 1:
                return get_mail(list.get(rowIndex));
            default:
                return super.getValueAt(rowIndex, columnIndex);
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


}
/**
 *
 * @author mw
 */
public class UserListPanel extends GlossDialogPanel
{

    private boolean okay = false;
    GlossTable table;

    /** Creates new form GetMailAddressPanel */
    public UserListPanel( ArrayList<String> list)
    {
        initComponents();

        UserListModel model = new UserListModel( UserMain.self, list);
        table = new GlossTable();
        table.setModel(model);
        table.embed_to_scrollpanel(jScrollPane1);


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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
                    .addComponent(BT_OKAY, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BT_OKAY)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKAYActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKAYActionPerformed
    {//GEN-HEADEREND:event_BT_OKAYActionPerformed
        // TODO add your handling code here:
        setOkay(true);
        my_dlg.setVisible(false);

    }//GEN-LAST:event_BT_OKAYActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_OKAY;
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

    @Override
    public JButton get_default_button()
    {
        return BT_OKAY;
    }

}
