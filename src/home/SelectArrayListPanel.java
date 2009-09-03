/*
 * SelectChannelPanel.java
 *
 * Created on 6. Mai 2008, 16:47
 */
package dimm.home;

import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import java.util.ArrayList;
import javax.swing.JButton;

/**
 *
 * @author  media
 */
public class SelectArrayListPanel extends GlossDialogPanel
{

    private boolean okay;

    /** Creates new form SelectChannelPanel */
    public SelectArrayListPanel(ArrayList list)
    {
        initComponents();
        okay = false;
        
        CB_CHANNEL.removeAllItems();
        for (int i = 0; i < list.size(); i++)
        {
            CB_CHANNEL.addItem(list.get(i));
        }        
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jLabel1 = new javax.swing.JLabel();
        CB_CHANNEL = new javax.swing.JComboBox();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();

        jLabel1.setText("Kanal");

        CB_CHANNEL.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        BT_OK.setText("Ok");
        BT_OK.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                BT_OKActionPerformed(evt);
            }
        });

        BT_ABORT.setText("Abbruch");
        BT_ABORT.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                BT_ABORTActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(CB_CHANNEL, 0, 210, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ABORT, BT_OK});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(CB_CHANNEL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BT_OKActionPerformed
        // TODO add your handling code here:
        okay = true;//GEN-LAST:event_BT_OKActionPerformed
        my_dlg.setVisible( false );

    }

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BT_ABORTActionPerformed
        // TODO add your handling code here:
        okay = false;
        my_dlg.setVisible( false );
    }//GEN-LAST:event_BT_ABORTActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_OK;
    private javax.swing.JComboBox CB_CHANNEL;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
    @Override
    public JButton get_default_button()
    {
        return BT_OK;
    }

    public boolean isOkay()
    {
        return okay;
    }
    public int get_selected_idx()
    {
        return CB_CHANNEL.getSelectedIndex();
    }
}
