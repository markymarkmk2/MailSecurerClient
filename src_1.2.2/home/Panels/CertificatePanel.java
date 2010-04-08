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

import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.Rendering.GlossTable;
import dimm.home.Rendering.SingleTextAreaPanel;
import dimm.home.ServerConnect.FunctionCallConnect;
import dimm.home.UserMain;
import home.shared.Utilities.ParseToken;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.EventObject;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;




class SpinnerTableRenderer implements TableCellRenderer
{
    @Override
    public Component getTableCellRendererComponent( JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column )
    {
        if (value instanceof JSpinner)
        {
            JSpinner sp = (JSpinner)value;
            if (isSelected)
            {
                sp.setForeground(table.getSelectionForeground());
                sp.setBackground(table.getSelectionBackground());
            }
            return sp;
        }
        return null;
    }
}

class SpinnerTableEditor implements TableCellEditor
{

    JTable table;
    ArrayList<CellEditorListener> listeners;

    public SpinnerTableEditor( JTable table )
    {

        this.table = table;
        listeners = new ArrayList<CellEditorListener>();
    }


    @Override
    public Component getTableCellEditorComponent( JTable table, Object value, boolean isSelected, int row, int column )
    {
        if (value instanceof JSpinner)
        {
            JSpinner sp = (JSpinner)value;
            return sp;
        }
        return null;
    }

    @Override
    public Object getCellEditorValue()
    {
        return null;
    }

    @Override
    public boolean isCellEditable( EventObject anEvent )
    {
        return true;
    }

    @Override
    public boolean shouldSelectCell( EventObject anEvent )
    {
        return true;
    }

    @Override
    public boolean stopCellEditing()
    {
        for (int i = 0; i < listeners.size(); i++)
        {
            CellEditorListener cl = listeners.get(i);
            cl.editingStopped( new ChangeEvent(table));

        }
        table.tableChanged( new TableModelEvent(table.getModel() ));
        return true;
    }

    @Override
    public void cancelCellEditing()
    {
        for (int i = 0; i < listeners.size(); i++)
        {
            CellEditorListener cl = listeners.get(i);
            cl.editingCanceled( new ChangeEvent(table));

        }

    }

    @Override
    public void addCellEditorListener( CellEditorListener l )
    {
        listeners.add(l);
    }

    @Override
    public void removeCellEditorListener( CellEditorListener l )
    {
        listeners.remove(l);
    }
}

class CertTableModel extends AbstractTableModel
{
    CertificatePanel panel;
    ArrayList<X509Certificate[]> cert_list;
    JButton tonne_bt;
    JButton edit_bt;
    SimpleDateFormat sdf;
    ArrayList<JSpinner> level_list;


    String[] col_names = {UserMain.getString("Level"), UserMain.getString("Issuer"), UserMain.getString("Valid"), UserMain.getString("Valid_from"), UserMain.getString("Valid_till"), UserMain.getString("Bearbeiten"), UserMain.getString("Löschen")};
    Class[] col_classes = {JSpinner.class, String.class,  Boolean.class,  String.class, String.class,  JButton.class, JButton.class};

    public CertTableModel(CertificatePanel _panel, ArrayList<X509Certificate[]> _ticket_list)
    {
        panel = _panel;
        tonne_bt = create_table_button( "/dimm/home/images/web_delete.png", panel );
        edit_bt = create_table_button(  "/dimm/home/images/web_edit.png", panel );
        sdf = new SimpleDateFormat("dd.MM.yyy");
        set_cert_list( _ticket_list );
    }


    public JButton create_table_button(String rsrc, CertificatePanel panel)
    {
        ImageIcon icn = new ImageIcon(this.getClass().getResource(rsrc));
        JButton bt = new JButton(icn);
        bt.addMouseListener(panel);
        bt.setBorderPainted(false);
        bt.setOpaque(false);
        bt.setMargin(new Insets(0, 0, 0, 0));
        bt.setContentAreaFilled(false);

        return bt;
    }

    @Override
    public boolean isCellEditable( int rowIndex, int columnIndex )
    {
        return (columnIndex == 0) ? true : false;
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
        if (cert_list == null)
            return 0;
        return cert_list.size();
    }

    @Override
    public int getColumnCount()
    {
        // EDIT IST 2.LAST ROW!!!!
        if (!UserMain.self.is_user() && !UserMain.self.is_admin())
            return col_names.length - 2;

        // EDIT IST 2.LAST ROW!!!!
        if (!UserMain.self.is_admin())
            return col_names.length - 1;


        return col_names.length;
    }

    ArrayList<X509Certificate[]> get_cert_list()
    {
        return cert_list;
    }


    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (cert_list == null)
            return "";

        X509Certificate[] carr = cert_list.get(rowIndex);
        if (carr.length == 0)
            return null;

        int level = 0;
        Object l = level_list.get(rowIndex).getValue();
        if (l instanceof Number)
        {
            level = ((Number)l).intValue() - 1;
        }
        X509Certificate cert = null;
        if (level >= 0 && level < carr.length)
            cert = carr[level];

        if (cert == null)
            return null;


        switch (columnIndex)
        {
            case 0:                            
                return level_list.get(rowIndex);
            case 1:
                return cert.getIssuerDN().getName();
            case 2:
                try
                {
                    cert.checkValidity();
                    return new Boolean(true);
                }
                catch( Exception exc)
                {
                    return new Boolean(false);
                }

            case 3:
                return sdf.format( cert.getNotBefore() );
            case 4:
                return sdf.format( cert.getNotAfter() );
            case 5:
               return edit_bt;
            case 6:
                return tonne_bt;

            default:
                return "???";
        }
    }

   
    public int get_edit_column()
    {
        return col_names.length - 2;
    }
    public int get_del_column()
    {
        return col_names.length - 1;
    }



    // SETS THE WIDTH OF THE LAST TWO ICON-COLUMNS
    public void set_table_header( TableColumnModel cm )
    {
        if (getColumnCount() > get_edit_column())
        {
            cm.getColumn( get_edit_column() ).setMinWidth(60);
            cm.getColumn( get_edit_column() ).setMaxWidth(60);

            if (getColumnCount() > get_del_column() )
            {
                cm.getColumn( get_del_column() ).setMinWidth(60);
                cm.getColumn( get_del_column() ).setMaxWidth(60);
            }
        }
    }

    void set_cert_list( ArrayList<X509Certificate[]> _cert_list )
    {
        cert_list = _cert_list;
        level_list = new ArrayList<JSpinner>();
        for (int i = 0; i < cert_list.size(); i++)
        {
            SpinnerNumberModel spm = new SpinnerNumberModel();
            spm.setMinimum( new Integer(1) );
            spm.setMaximum( new Integer(cert_list.get(i).length) );

            JSpinner sp= new JSpinner(spm);
            sp.setValue( new Integer(cert_list.get(i).length)  );
            spm.addChangeListener(panel);
            level_list.add(sp);
        }
    }

}
/**
 *
 * @author mw
 */
public class CertificatePanel extends GlossDialogPanel implements MouseListener,  PropertyChangeListener, ChangeListener
{
    GlossTable table;
    
    ImageIcon ok_icn;
    ImageIcon nok_icn;
    ImageIcon empty_icn;
    CertTableModel model;



    /** Creates new form GetMailAddressPanel */
    public CertificatePanel( )
    {
        initComponents();
        String  icn_ok = "/dimm/home/images/web_check.png";
        String  icn_empty = "/dimm/home/images/ok_empty.png";
        String  icn_warn = "/dimm/home/images/web_delete.png";

        ok_icn = new ImageIcon(this.getClass().getResource(icn_ok));
        nok_icn = new ImageIcon(this.getClass().getResource(icn_warn));
        empty_icn = new ImageIcon(this.getClass().getResource(icn_empty));

        table = new GlossTable();        
        table.addMouseListener(this);

        // REGISTER TABLE TO SCROLLPANEL
        table.embed_to_scrollpanel( SCP_TABLE );
        table.setDefaultRenderer(JSpinner.class, new SpinnerTableRenderer());
        table.setDefaultEditor(JSpinner.class, new SpinnerTableEditor(table));        

        read_certificates();
      
    }


    void read_certificates()
    {
        FunctionCallConnect fcc = UserMain.fcc();
        String ret = fcc.call_abstract_function("certificate CMD:list", FunctionCallConnect.SHORT_TIMEOUT );

        if (ret != null && fcc.get_last_err_code() == 0)
        {
            if (ret.charAt(0) == '0')
            {
                ParseToken pt = new ParseToken(ret);
                ArrayList o = pt.GetObject("CL:", ArrayList.class);
                if ( o != null)
                {
                    ArrayList<X509Certificate[]> cert_list = (ArrayList<X509Certificate[]>)o;
                    model = new CertTableModel(this, cert_list);
                    table.setModel(model);
                    set_table_header();

                    propertyChange(null);
                    return;
                }
            }
        }
        ArrayList<X509Certificate[]> cert_list = new ArrayList<X509Certificate[]>();
        model = new CertTableModel(this, cert_list);
        table.setModel(model);
        set_table_header();

    }

    public void set_table_header()
    {
        TableColumnModel cm = table.getTableHeader().getColumnModel();
        cm.getColumn(0).setMinWidth(40);
        cm.getColumn(0).setMaxWidth(40);
        cm.getColumn(1).setPreferredWidth(140);
        cm.getColumn(2).setMaxWidth(50);
        cm.getColumn(2).setMinWidth(50);
        cm.getColumn(3).setPreferredWidth(60);
        cm.getColumn(4).setPreferredWidth(60);

        model.set_table_header(cm);
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

    void set_icon( JButton btn, ImageIcon icn )
    {
        btn.setIcon(icn);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);

        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setBorderPainted(false);
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
        BT_CSR = new GlossButton();
        BT_IMPORT = new GlossButton();
        BT_IMPORT_CA = new GlossButton();
        BT_NEW_KEY = new GlossButton();

        BT_OKAY.setText(UserMain.Txt("Close")); // NOI18N
        BT_OKAY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKAYActionPerformed(evt);
            }
        });

        BT_CSR.setText(UserMain.getString("Create_Signing_Request")); // NOI18N
        BT_CSR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_CSRActionPerformed(evt);
            }
        });

        BT_IMPORT.setText(UserMain.getString("Import_certificate")); // NOI18N
        BT_IMPORT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_IMPORTActionPerformed(evt);
            }
        });

        BT_IMPORT_CA.setText(UserMain.getString("Import_root_certificate")); // NOI18N
        BT_IMPORT_CA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_IMPORT_CAActionPerformed(evt);
            }
        });

        BT_NEW_KEY.setText(UserMain.getString("Create_New_Key")); // NOI18N
        BT_NEW_KEY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_NEW_KEYActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SCP_TABLE, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 652, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(BT_IMPORT_CA)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 429, Short.MAX_VALUE)
                        .addComponent(BT_OKAY, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(BT_NEW_KEY)
                    .addComponent(BT_CSR)
                    .addComponent(BT_IMPORT))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_CSR, BT_IMPORT, BT_IMPORT_CA, BT_NEW_KEY});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SCP_TABLE, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BT_NEW_KEY)
                .addGap(4, 4, 4)
                .addComponent(BT_CSR)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BT_IMPORT)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BT_OKAY, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_IMPORT_CA))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKAYActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKAYActionPerformed
    {//GEN-HEADEREND:event_BT_OKAYActionPerformed
        // TODO add your handling code here:
        
        my_dlg.setVisible(false);

    }//GEN-LAST:event_BT_OKAYActionPerformed

  

    private void BT_CSRActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_CSRActionPerformed
    {//GEN-HEADEREND:event_BT_CSRActionPerformed
        // TODO add your handling code here:
        FunctionCallConnect fcc = UserMain.fcc();
        String ret = fcc.call_abstract_function("certificate CMD:create_csr AL:mailsecurer KS:mailsecurer", FunctionCallConnect.SHORT_TIMEOUT );
        if (ret != null && ret.length() > 0 && ret.charAt(0) == '0')
        {
            ParseToken pt = new ParseToken(ret.substring(3) );
            String csr = pt.GetCompressedObject("CSR:").toString();

            
            JFileChooser jch = new JFileChooser();
            jch.setSelectedFile(new File("MailSecurer.csr"));
            jch.setLocation( my_dlg.get_next_location() );
            
            if (jch.showSaveDialog(my_dlg) == JFileChooser.APPROVE_OPTION)
            {
                try
                {
                    File f = jch.getSelectedFile();
                    FileWriter tw = new FileWriter(f);
                    tw.write(csr);
                    tw.close();
                }
                catch (IOException iOException)
                {
                    UserMain.errm_ok(my_dlg, UserMain.Txt("Cannot_save_CSR_to_file") + ": " + iOException.getMessage());
                }
            }

            SingleTextAreaPanel stap = new SingleTextAreaPanel(false);
            stap.setText(csr);
            GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, stap);
            dlg.set_next_location( my_dlg );
            dlg.setVisible(true);
        }
    }//GEN-LAST:event_BT_CSRActionPerformed

    private void BT_IMPORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_IMPORTActionPerformed
    {//GEN-HEADEREND:event_BT_IMPORTActionPerformed
        // TODO add your handling code here:
        byte[] data = select_ca_data();
        if (data != null)
        {

            ByteBuffer bb = ByteBuffer.wrap(data);
            String cert = ParseToken.BuildCompressedObjectString( bb );
            // DATA IS VALID NOW
            FunctionCallConnect fcc = UserMain.fcc();
            String ret = fcc.call_abstract_function("certificate CMD:import AL:mailsecurer KS:mailsecurer CERT:\"" + cert + "\"", FunctionCallConnect.MEDIUM_TIMEOUT );
            if (ret != null && ret.length() > 0 && ret.charAt(0) == '0')
            {
                UserMain.info_ok(my_dlg, UserMain.Txt("Certificate_was_imported_successful" ));
            }
            else
            {
                UserMain.errm_ok(my_dlg, UserMain.Txt("Certificate_could_not_be_imported") + ": " + ret);
            }
            read_certificates();
            propertyChange( new PropertyChangeEvent(this, "REBUILD", null, null ) );

        }
    }//GEN-LAST:event_BT_IMPORTActionPerformed

    private void BT_IMPORT_CAActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_IMPORT_CAActionPerformed
    {//GEN-HEADEREND:event_BT_IMPORT_CAActionPerformed
        // TODO add your handling code here:
        // TODO add your handling code here:
        byte[] data = select_ca_data();
        if (data != null)
        {

            ByteBuffer bb = ByteBuffer.wrap(data);
            String cert = ParseToken.BuildCompressedObjectString( bb );
            // BUILD A FAKE ALIAS, MUST BE UNIQUE
            String alias = Long.toString(System.currentTimeMillis() / 1000);
            // DATA IS VALID NOW
            FunctionCallConnect fcc = UserMain.fcc();
            String ret = fcc.call_abstract_function("certificate CMD:import TC:1 AL:" + alias + " KS:mailsecurer CERT:\"" + cert + "\"", FunctionCallConnect.MEDIUM_TIMEOUT );
            if (ret != null && ret.length() > 0 && ret.charAt(0) == '0')
            {
                UserMain.info_ok(my_dlg, UserMain.Txt("Certificate_was_imported_successful" ));
            }
            else
            {
                UserMain.errm_ok(my_dlg, UserMain.Txt("Certificate_could_not_be_imported") + ": " + ret);
            }
            read_certificates();
            propertyChange( new PropertyChangeEvent(this, "REBUILD", null, null ) );

        }
    }//GEN-LAST:event_BT_IMPORT_CAActionPerformed

    private void BT_NEW_KEYActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_NEW_KEYActionPerformed
    {//GEN-HEADEREND:event_BT_NEW_KEYActionPerformed
        // TODO add your handling code here:
        NewCertificatePanel pnl = new NewCertificatePanel();
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
        dlg.set_next_location( my_dlg );
        dlg.setVisible(true);
        if (pnl.isOk())
        {
            FunctionCallConnect fcc = UserMain.fcc();
            String cmd = "certificate CMD:create AL:" + pnl.get_alias() +
                    " CN:\"" + pnl.get_CN() + "\"" +
                    " O_:\"" + pnl.get_O() + "\"" +
                    " L_:\"" + pnl.get_L() + "\"" +
                    " S_:\"" + pnl.get_S() + "\"" +
                    " OU:\"" + pnl.get_OU() + "\"" +
                    " C_:\"" + pnl.get_C() + "\"" +
                    " KL:" + pnl.get_keylength() + 
                    " KS:mailsecurer";

            String ret = fcc.call_abstract_function(cmd, FunctionCallConnect.MEDIUM_TIMEOUT );
            if (ret != null && ret.length() > 0 && ret.charAt(0) == '0')
            {
                UserMain.info_ok(my_dlg, UserMain.Txt("Certificate_was_created_successful" ));
            }
            else
            {
                UserMain.errm_ok(my_dlg, UserMain.Txt("Certificate_could_not_be_created") + ": " + ret);
            }

            read_certificates();
            propertyChange( new PropertyChangeEvent(this, "REBUILD", null, null ) );

        }
    }//GEN-LAST:event_BT_NEW_KEYActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_CSR;
    private javax.swing.JButton BT_IMPORT;
    private javax.swing.JButton BT_IMPORT_CA;
    private javax.swing.JButton BT_NEW_KEY;
    private javax.swing.JButton BT_OKAY;
    private javax.swing.JScrollPane SCP_TABLE;
    // End of variables declaration//GEN-END:variables


    byte[] select_ca_data()
    {
        JFileChooser jch = new JFileChooser();
        jch.setLocation( my_dlg.get_next_location() );

        if (jch.showOpenDialog(my_dlg) == JFileChooser.APPROVE_OPTION)
        {
            byte[] data = null;
            FileInputStream tr = null;
            try
            {
                File f = jch.getSelectedFile();
                tr = new FileInputStream(f);
                data = new byte[(int)f.length()];
                tr.read(data);

                return data;
            }
            catch (IOException iOException)
            {
                UserMain.errm_ok(my_dlg, UserMain.Txt("Cannot_load_certificate") + ": " + iOException.getMessage());
                return null;
            }
            finally
            {
                if (tr != null)
                {
                    try
                    {
                        tr.close();
                    }
                    catch (IOException iOException)
                    {
                    }
                }
            }
        }
        return null;
    }
 


    @Override
    public JButton get_default_button()
    {
        return BT_OKAY;
    }

   @Override
    public void mouseClicked(MouseEvent e)
    {
        Component c = table.getComponentAt(e.getPoint());
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());

        if (e.getClickCount() == 1)
        {
            if (col == model.get_edit_column())
            {
                open_view_dlg( row );
            }

            if (col == model.get_del_column())
            {
                String txt = UserMain.getString("Wollen_Sie_wirklich_diesen_Eintrag_loeschen");

                if (UserMain.errm_ok_cancel( txt + "?" ))
                {
                    boolean okay = del_certificate( row );

                    propertyChange( new PropertyChangeEvent(this, "REBUILD", null, null ) );
                }
            }
        }

        // DBLCLICK OPENS EDIT TOO
        if (e.getClickCount() == 2)
        {
             open_view_dlg( row );
        }

        //System.out.println("Row " + row + "Col " + col);
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

        table.tableChanged(new TableModelEvent(table.getModel()) );
        

        this.repaint();
    }

    private void open_view_dlg( int row )
    {
        X509Certificate[] cert = model.get_cert_list().get(row);
        open_view_dlg( cert[0] );
    }
    private void open_view_dlg( X509Certificate ticket )
    {
/*        CertificateViewPanel pnl = new CertificateViewPanel( this, ticket );
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
        dlg.set_next_location(my_dlg);
        dlg.setVisible(true);*/
    }

    private boolean del_certificate( int row )
    {
        X509Certificate[] cert = model.get_cert_list().get(row);

        String xml = ParseToken.BuildCompressedObjectString(cert);

        FunctionCallConnect fcc = UserMain.fcc();
        String ret = fcc.call_abstract_function("certificate CMD:delete_cert CERT:\"" + xml + "\"", FunctionCallConnect.SHORT_TIMEOUT );

        read_certificates();

        if (ret != null && fcc.get_last_err_code() == 0)
        {
            if (ret.charAt(0) == '0')
            {
                propertyChange(null);
                return true;
            }
        }
        UserMain.errm_ok(my_dlg, UserMain.Txt("Cannot_delete_certificate") + ": " + ret);
        return false;
    }

    @Override
    public void stateChanged( ChangeEvent e )
    {
        table.tableChanged(new TableModelEvent(table.getModel()) );
    }
}
