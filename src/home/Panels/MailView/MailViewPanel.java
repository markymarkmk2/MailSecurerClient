/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MailViewPanel.java
 *
 * Created on 15.07.2009, 16:14:22
 */

package dimm.home.Panels.MailView;

import com.thoughtworks.xstream.XStream;
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.Rendering.GlossTable;
import dimm.home.ServerConnect.FunctionCallConnect;
import dimm.home.ServerConnect.InStreamID;
import dimm.home.ServerConnect.ServerInputStream;
import dimm.home.UserMain;
import dimm.home.Utilities.ParseToken;
import dimm.home.Utilities.SizeStr;
import dimm.home.Utilities.SwingWorker;
import home.shared.CS_Constants;
import home.shared.mail.RFCMimeMail;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.table.AbstractTableModel;

class MailPreviewDlg extends GenericGlossyDlg
{

    UserMain main;
    MailPreviewDlg( UserMain parent, RFCMimeMail mail)
    {
        super( parent, true, new MailPreviewPanel(mail));
        main = parent;
        if (parent.isVisible())
            this.setLocation(parent.getLocationOnScreen().x + 30, parent.getLocationOnScreen().y + 30);
        else
            this.setLocationRelativeTo(null);

        this.setSize( 700, 600);
    }
}







class FieldComboEntry
{
    String field;

    public FieldComboEntry( String field )
    {
        this.field = field;
    }

    @Override
    public String toString()
    {
        return UserMain.Txt(field);
    }

    public String getField()
    {
        return field;
    }
    
}
class MailTableModel extends AbstractTableModel
{
    static int SUBJECT_COL = 2;
    
    MailViewPanel pnl;
    ArrayList<ArrayList<String>> result_array;
    ArrayList<String> field_list;

    SimpleDateFormat sdf;
    JButton ic_attachment;
    JButton ic_no_attachment;

    MailTableModel(MailViewPanel _pnl, ArrayList<String> field_list, ArrayList<ArrayList<String>> ret_arr)
    {
        super();

        pnl = _pnl;
        result_array = ret_arr;
        this.field_list = field_list;

        sdf = new SimpleDateFormat("dd.MM.yyyy  HH:mm");

        ic_attachment = create_table_button("/dimm/home/images/ic_attachment.png");
        ic_no_attachment = create_table_button(null);

    }

    @Override
    public boolean isCellEditable( int row, int column )
    {
        return false;
    }



    @Override
    public String getColumnName(int column)
    {
        switch( column )
        {
            case 0: return UserMain.Txt("Date");
            case 2: return UserMain.Txt("Subject");
            case 3: return UserMain.Txt("Size");
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        if (columnIndex == 1)
            return JButton.class;

        return String.class;
    }

    @Override
    public int getRowCount()
    {
        if (result_array == null)
            return 0;
        return result_array.size();
    }

    @Override
    public int getColumnCount()
    {
        if (field_list == null)
            return 0;
        return field_list.size();
    }

    public JButton create_table_button(String rsrc)
    {
        JButton bt;
        if (rsrc != null)
        {
            ImageIcon icn = new ImageIcon(this.getClass().getResource(rsrc));
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

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        String val = result_array.get(rowIndex).get(columnIndex);
        if ( columnIndex == 0)
        {
            long time = Long.parseLong(val, 16);
            Date d = new Date(time);
            return sdf.format(d);
        }
        else if (columnIndex == 1)
        {
            if (val != null && val.length() > 0 && val.charAt(0) == '1')
                return ic_attachment;

            return ic_no_attachment;
        }
        else if (columnIndex == 3)
        {
            long size = Long.parseLong(val, 16);
            SizeStr str = new SizeStr(size);
            return str.toString();
        }
        return val;
    }
}
/**
 *
 * @author mw
 */
public class MailViewPanel extends GlossDialogPanel implements MouseListener
{
    String search_id;
    GlossTable table;
    MailTableModel model;

    /** Creates new form MailViewPanel */
    public MailViewPanel()
    {
        initComponents();

        table = new GlossTable();

        model = new MailTableModel(this, null, null);
        table.setModel(model);
        table.addMouseListener(this);

        // REGISTER TABLE TO SCROLLPANEL
        table.embed_to_scrollpanel( SCP_TABLE );

        CB_FIELD.removeAllItems();
        CB_FIELD.addItem( new FieldComboEntry(CS_Constants.FLD_BODY));
        CB_FIELD.addItem( new FieldComboEntry(CS_Constants.FLD_ATTACHMENT));
        CB_FIELD.addItem( new FieldComboEntry(CS_Constants.FLD_SUBJECT));
        

        CB_FIELD.setSelectedIndex(0);
       
    }

    void search_mail()
    {
        // TODO add your handling code here:
        FunctionCallConnect fcc = UserMain.fcc();

        if (search_id != null)
        {
                    // CLOSE CALL;
            fcc.call_abstract_function("SearchMail CMD:close MA:1 ID:" + search_id, 5000);
        }


        String mail =  TXT_MAIL.getText();
        String search_val = TXT_SEARCH.getText();

        int entries = 5;

        FieldComboEntry fld_entry= (FieldComboEntry)CB_FIELD.getSelectedItem();
        String field_name = fld_entry.getField();

        try
        {
            int idx = CB_ENTRIES.getSelectedIndex();
            if (idx == -1)
            {
                idx = 0;

            }
            entries = Integer.parseInt(CB_ENTRIES.getSelectedItem().toString());
        }
        catch (NumberFormatException numberFormatException)
        {
        }

        String open_ret = fcc.call_abstract_function("SearchMail CMD:open MA:1 EM:'" + mail + "' FL:'" + field_name + "' VL:'" + search_val + "' CNT:'" + entries + "' ", 5000);
        if (open_ret.charAt(0) != '0')
        {
            UserMain.errm_ok(my_dlg, "SearchMail open gave " + open_ret );
            return;
        }
        String[] l = open_ret.split(" ");

        search_id = l[1];
        ArrayList<String>field_list = new ArrayList<String>();


        field_list.add(CS_Constants.FLD_DATE);
        field_list.add(CS_Constants.FLD_HAS_ATTACHMENT);
        field_list.add(CS_Constants.FLD_SUBJECT);
        field_list.add(CS_Constants.FLD_SIZE);


        String cmd =  "SearchMail CMD:get MA:1 ID:" + search_id + " ROW:-1 FLL:'";
        for ( int i = 0; i < field_list.size(); i++ )
        {
            if (i > 0)
                cmd += ",";
            cmd += field_list.get(i);
        }
        cmd += "'";


        String search_get_ret = fcc.call_abstract_function( cmd, 5000);




        if (search_get_ret.charAt(0) != '0')
        {
            UserMain.errm_ok(my_dlg, "SearchMail get gave " + search_get_ret );
            return;
        }



        XStream xstream = new XStream();
        Object o = xstream.fromXML(search_get_ret.substring(3));

        if (o instanceof ArrayList)
        {
            ArrayList<ArrayList<String>> ret_arr = (ArrayList<ArrayList<String>>)o;

            model = new MailTableModel( this, field_list, ret_arr );
            table.setModel(model);

            table.getColumnModel().getColumn(0).setMinWidth(80);
            table.getColumnModel().getColumn(0).setPreferredWidth(120);
            table.getColumnModel().getColumn(0).setMaxWidth(180);
            table.getColumnModel().getColumn(1).setMinWidth(20);
            table.getColumnModel().getColumn(1).setMaxWidth(20);
            table.getColumnModel().getColumn(2).setPreferredWidth(180);
            table.getColumnModel().getColumn(3).setMinWidth(30);
            table.getColumnModel().getColumn(3).setMaxWidth(50);
        }

    }

    SwingWorker sw;
    void open_mail( final int row )
    {
        if (sw != null)
            return;

        sw = new SwingWorker() {

            @Override
            public Object construct()
            {
                run_open_mail(row);
                sw = null;
                return null;
            }
        };

        sw.start();


    }

    void run_open_mail( int row )
    {
        String subject = table.getModel().getValueAt(row, MailTableModel.SUBJECT_COL).toString();
        ServerInputStream sis = null;
        BufferedOutputStream baos = null;
        BufferedInputStream bais = null;
        File tmp_file = null;

        UserMain.self.show_busy(my_dlg, UserMain.Txt("Loading_mail") + "...");
        try
        {
            tmp_file = File.createTempFile("dlml", ".tmp", new File("."));
            FileOutputStream fos = new FileOutputStream( tmp_file );
            baos = new BufferedOutputStream(fos);


            FunctionCallConnect fcc = UserMain.fcc();
            String ret = fcc.call_abstract_function("SearchMail CMD:open_mail ID:" + search_id + " ROW:" + row, 5);
            if (ret.charAt(0) != '0')
            {
                UserMain.errm_ok(my_dlg, "SearchMail open_mail gave " + ret);
                return;
            }
            String[] l = ret.split(" ");
            String instream_id = l[1];
            ParseToken pt = new ParseToken(l[2]);
            long len = pt.GetLong("LEN:");

            InStreamID id = new InStreamID(instream_id, len);

            sis = new ServerInputStream(fcc.get_sqc(), id);
            sis.read(baos);

            baos.close();


            FileInputStream fis = new FileInputStream( tmp_file );
            bais = new BufferedInputStream(fis);
          
            RFCMimeMail mmsg;

            mmsg = new RFCMimeMail();
            
            mmsg.parse(bais);


            MailPreviewDlg dlg = new MailPreviewDlg(UserMain.self, mmsg);
            bais.close();

            UserMain.self.hide_busy();

            dlg.setModal(false);
            dlg.setTitle(subject);
            dlg.setLocation( my_dlg.getLocation().x + 20,my_dlg.getLocation().y + 20);
            dlg.setVisible(true);

        }
        catch (Exception iOException)
        {
            iOException.printStackTrace();
            UserMain.errm_ok(my_dlg, "Fehler beim Abholen der Mail: " + iOException.getMessage() );
        }
        finally
        {
            try
            {
                if (sis != null)
                {
                    sis.close();
                }
                if (baos != null)
                {
                    baos.close();
                }
                if (bais != null)
                {
                    bais.close();
                }
                if (tmp_file != null)
                    tmp_file.delete();
            }
            catch (IOException iOException)
            {
            }
            UserMain.self.hide_busy();
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

        jLabel1 = new javax.swing.JLabel();
        TXT_SEARCH = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        SCP_TABLE = new javax.swing.JScrollPane();
        BT_CLOSE = new javax.swing.JButton();
        BT_EXPORT = new javax.swing.JButton();
        TXT_MAIL = new javax.swing.JTextField();
        CB_FIELD = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        CB_ENTRIES = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();

        jLabel1.setText(UserMain.getString("Suche")); // NOI18N

        TXT_SEARCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TXT_SEARCHActionPerformed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/tr_browse.png"))); // NOI18N
        jButton1.setIconTextGap(0);
        jButton1.setInheritsPopupMenu(true);
        jButton1.setMargin(new java.awt.Insets(1, 1, 1, 1));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_TABLE, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 590, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_TABLE, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE)
        );

        BT_CLOSE.setText(UserMain.getString("Schliessen")); // NOI18N
        BT_CLOSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_CLOSEActionPerformed(evt);
            }
        });

        BT_EXPORT.setText(UserMain.getString("Export_Mail")); // NOI18N

        CB_FIELD.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText(UserMain.getString("Filter")); // NOI18N

        jLabel3.setText("Mailadresse");

        CB_ENTRIES.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "10", "100", "1000" }));

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dimm/home/MA_Properties"); // NOI18N
        jLabel4.setText(bundle.getString("Entries")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(BT_EXPORT)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 382, Short.MAX_VALUE)
                                .addComponent(BT_CLOSE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(TXT_MAIL, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                                        .addGap(49, 49, 49))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(CB_ENTRIES, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(CB_FIELD, javax.swing.GroupLayout.Alignment.LEADING, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(TXT_SEARCH, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jButton1)))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(19, 19, 19)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TXT_SEARCH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TXT_MAIL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(CB_FIELD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(CB_ENTRIES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel4)))
                .addGap(24, 24, 24)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_CLOSE)
                    .addComponent(BT_EXPORT))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_jButton1ActionPerformed
    {//GEN-HEADEREND:event_jButton1ActionPerformed
        // TODO add your handling code here:
        search_mail();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void BT_CLOSEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_CLOSEActionPerformed
    {//GEN-HEADEREND:event_BT_CLOSEActionPerformed
        // TODO add your handling code here:
        FunctionCallConnect fcc = UserMain.fcc();

        if (search_id != null)
        {
            String ret = fcc.call_abstract_function("SearchMail CMD:close MA:1 ID:" + search_id, 5000);
        }

        setVisible(false);
    }//GEN-LAST:event_BT_CLOSEActionPerformed

    private void TXT_SEARCHActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TXT_SEARCHActionPerformed
    {//GEN-HEADEREND:event_TXT_SEARCHActionPerformed
        // TODO add your handling code here:
        search_mail();

    }//GEN-LAST:event_TXT_SEARCHActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_CLOSE;
    private javax.swing.JButton BT_EXPORT;
    private javax.swing.JComboBox CB_ENTRIES;
    private javax.swing.JComboBox CB_FIELD;
    private javax.swing.JScrollPane SCP_TABLE;
    private javax.swing.JTextField TXT_MAIL;
    private javax.swing.JTextField TXT_SEARCH;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables




    @Override
    public void mouseClicked( MouseEvent e )
    {
        if (e.getClickCount() == 2)
        {
            if (e.getSource() == table)
            {
                int row = table.rowAtPoint(e.getPoint());
                
                open_mail( row );
            }
        }
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

    @Override
    public JButton get_default_button()
    {
        return BT_CLOSE;
    }

}
