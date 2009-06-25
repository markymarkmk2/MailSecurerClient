/*
 * IPRealPanel.java
 *
 * Created on 9. Juni 2008, 15:07
 */

package dimm.home;

import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.Utilities.CmdExecutor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import javax.swing.JButton;

/**
 
 @author  Administrator
 */
public class IPRealPanel extends GlossDialogPanel
{
    int eth_nr;
    
    /** Creates new form IPRealPanel */
    public IPRealPanel(int _eth_nr)
    {
        initComponents();
        eth_nr = _eth_nr;
        
                
    }
    
    public void load_vals()
    {
        StringBuffer sb = new StringBuffer();
        
       
        
        // WIN
        sb.setLength(0);
        String[] str = {"ipconfig"};
        CmdExecutor cmd = new CmdExecutor( str );        
        if (cmd.exec() == 0)
        {
            sb.append("IP_Config:\n");
            sb.append(cmd.get_out_text());
        }
        String[] str2 = {"route","print"};
        cmd = new CmdExecutor( str2 );        
        if (cmd.exec() == 0)
        {
            sb.append("\n\nRouting:\n");
            sb.append(cmd.get_out_text());
        }
            
        TXTA_LOCAL.setText(sb.toString());
        
                                          
    }
    
    /** This method is called from within the constructor to
     initialize the form.
     WARNING: Do NOT modify this code. The content of this method is
     always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jScrollPane1 = new javax.swing.JScrollPane();
        TXTA_LOCAL = new javax.swing.JTextArea();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        BT_OK = new GlossButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        TXTA_SB = new javax.swing.JTextArea();
        BT_CLIP = new GlossButton();

        jScrollPane1.setOpaque(false);

        TXTA_LOCAL.setColumns(20);
        TXTA_LOCAL.setEditable(false);
        TXTA_LOCAL.setForeground(new java.awt.Color(201, 201, 201));
        TXTA_LOCAL.setRows(5);
        TXTA_LOCAL.setOpaque(false);
        jScrollPane1.setViewportView(TXTA_LOCAL);

        jLabel1.setText(UserMain.Txt("Dieser_Rechner")); // NOI18N

        jLabel2.setText(UserMain.Txt("SonicBox")); // NOI18N

        BT_OK.setText(UserMain.Txt("CLOSE_DIALOG")); // NOI18N
        BT_OK.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                BT_OKActionPerformed(evt);
            }
        });

        jScrollPane2.setOpaque(false);

        TXTA_SB.setColumns(20);
        TXTA_SB.setEditable(false);
        TXTA_SB.setForeground(new java.awt.Color(201, 201, 201));
        TXTA_SB.setRows(5);
        TXTA_SB.setOpaque(false);
        jScrollPane2.setViewportView(TXTA_SB);

        BT_CLIP.setText(UserMain.Txt("In_die_Zwischenable")); // NOI18N
        BT_CLIP.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                BT_CLIPActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 535, Short.MAX_VALUE)
                    .addComponent(jLabel2)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(BT_CLIP, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 297, Short.MAX_VALUE)
                        .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 171, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_CLIP, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKActionPerformed
    {//GEN-HEADEREND:event_BT_OKActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_BT_OKActionPerformed

    private void BT_CLIPActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_CLIPActionPerformed
    {//GEN-HEADEREND:event_BT_CLIPActionPerformed
        // TODO add your handling code here:
        StringBuffer sb2 = new StringBuffer();
        
        sb2.append("SonicBox:\n");        
        sb2.append(TXTA_SB.getText());
        sb2.append("\n\nSonicRemote:\n");
        sb2.append(TXTA_LOCAL.getText());
        
        Clipboard systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable t = new StringSelection(sb2.toString()) ;
        systemClipboard.setContents(t, null);
        
}//GEN-LAST:event_BT_CLIPActionPerformed
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_CLIP;
    private javax.swing.JButton BT_OK;
    private javax.swing.JTextArea TXTA_LOCAL;
    private javax.swing.JTextArea TXTA_SB;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

    @Override
    public JButton get_default_button()
    {
        return BT_OK;
    }
    
}