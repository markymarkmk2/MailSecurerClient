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

import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.UserMain;
import home.shared.license.DemoLicenseTicket;
import home.shared.license.HWIDLicenseTicket;
import home.shared.license.LicenseTicket;
import javax.swing.JButton;


public class LicenseViewPanel extends GlossDialogPanel
{

    LicensePanel panel;
    String init_ticket;
    LicenseTicket ticket;

    /** Creates new form GetMailAddressPanel */
    public LicenseViewPanel( LicensePanel _panel, LicenseTicket _ticket)
    {
        panel = _panel;
        ticket = _ticket;
        
        initComponents();

        init_ticket = ticket.toString();

        set_gui( ticket );
    }

    void set_gui( LicenseTicket ticket )
    {
        TXT_PRODUCT.setText(ticket.getProduct());
        TXT_SERIAL.setText("" + ticket.getSerial());
        TXT_UNITS.setText("" + ticket.getUnits());
        TXT_KEY.setText(ticket.getKey());
        if (ticket instanceof DemoLicenseTicket)
        {
            DemoLicenseTicket dlt = (DemoLicenseTicket)ticket;
            TXT_TYPEDATA.setText(dlt.get_text());
            LB_TYPETXT.setText(UserMain.Txt("Expires"));
        }
        if (ticket instanceof HWIDLicenseTicket)
        {
            HWIDLicenseTicket dlt = (HWIDLicenseTicket)ticket;
            TXT_TYPEDATA.setText(dlt.getHwid());
            LB_TYPETXT.setText(UserMain.Txt("HW-ID"));
        }
        
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ticket.get_modules_text().size(); i++)
        {
            if (i > 0)
                sb.append("\n");
            sb.append( UserMain.Txt( ticket.get_modules_text().get(i) ) );
        }
        TXTA_MODULES.setText( sb.toString() );

        if (my_dlg != null)
            my_dlg.pack();
        
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
        jLabel1 = new javax.swing.JLabel();
        TXT_PRODUCT = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        TXT_SERIAL = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        TXT_UNITS = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TXTA_MODULES = new javax.swing.JTextArea();
        LB_TYPETXT = new javax.swing.JLabel();
        TXT_TYPEDATA = new javax.swing.JTextField();
        LB_TYPETXT1 = new javax.swing.JLabel();
        TXT_KEY = new javax.swing.JTextField();
        BT_REPLACE = new GlossButton();

        BT_OKAY.setText(UserMain.Txt("Close")); // NOI18N
        BT_OKAY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKAYActionPerformed(evt);
            }
        });

        jLabel1.setText(UserMain.getString("Product")); // NOI18N

        TXT_PRODUCT.setEditable(false);

        jLabel2.setText(UserMain.getString("Serial")); // NOI18N

        TXT_SERIAL.setEditable(false);

        jLabel3.setText(UserMain.getString("Units")); // NOI18N

        TXT_UNITS.setEditable(false);

        jLabel4.setText(UserMain.getString("Modules")); // NOI18N

        TXTA_MODULES.setColumns(20);
        TXTA_MODULES.setEditable(false);
        TXTA_MODULES.setRows(3);
        jScrollPane1.setViewportView(TXTA_MODULES);

        LB_TYPETXT.setText("AAA");

        TXT_TYPEDATA.setEditable(false);

        LB_TYPETXT1.setText(UserMain.getString("Key")); // NOI18N

        TXT_KEY.setEditable(false);

        BT_REPLACE.setText(UserMain.getString("Replace")); // NOI18N
        BT_REPLACE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_REPLACEActionPerformed(evt);
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel1)
                            .addComponent(LB_TYPETXT1)
                            .addComponent(LB_TYPETXT)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                            .addComponent(TXT_KEY, javax.swing.GroupLayout.DEFAULT_SIZE, 315, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(TXT_TYPEDATA, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(TXT_UNITS, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(TXT_SERIAL, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(TXT_PRODUCT, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 107, Short.MAX_VALUE))))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(BT_REPLACE)
                        .addGap(181, 181, 181)
                        .addComponent(BT_OKAY, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_PRODUCT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addGap(11, 11, 11)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_SERIAL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_UNITS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_TYPEDATA, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LB_TYPETXT))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 62, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_KEY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LB_TYPETXT1))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_REPLACE)
                    .addComponent(BT_OKAY, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKAYActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKAYActionPerformed
    {//GEN-HEADEREND:event_BT_OKAYActionPerformed
        // TODO add your handling code here:
        if (ticket != null && !init_ticket.equals( ticket.toString() ))
        {
            panel.ask_for_update(ticket);
        }
        
        my_dlg.setVisible(false);

    }//GEN-LAST:event_BT_OKAYActionPerformed

    private void BT_REPLACEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_REPLACEActionPerformed
    {//GEN-HEADEREND:event_BT_REPLACEActionPerformed
        // TODO add your handling code here:
        ticket = panel.get_lic_from_user();
        if (ticket != null)
        {
            set_gui(ticket);
        }
    }//GEN-LAST:event_BT_REPLACEActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_OKAY;
    private javax.swing.JButton BT_REPLACE;
    private javax.swing.JLabel LB_TYPETXT;
    private javax.swing.JLabel LB_TYPETXT1;
    private javax.swing.JTextArea TXTA_MODULES;
    private javax.swing.JTextField TXT_KEY;
    private javax.swing.JTextField TXT_PRODUCT;
    private javax.swing.JTextField TXT_SERIAL;
    private javax.swing.JTextField TXT_TYPEDATA;
    private javax.swing.JTextField TXT_UNITS;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables




    @Override
    public JButton get_default_button()
    {
        return BT_OKAY;
    }

}