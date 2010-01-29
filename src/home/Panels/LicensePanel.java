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

import com.thoughtworks.xstream.XStream;
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.Rendering.GlossTable;
import dimm.home.ServerConnect.FunctionCallConnect;
import dimm.home.UserMain;
import home.shared.Utilities.ParseToken;
import home.shared.Utilities.ZipUtilities;
import home.shared.license.DemoLicenseTicket;
import home.shared.license.HWIDLicenseTicket;
import home.shared.license.LicenseTicket;
import home.shared.license.ValidTicketContainer;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Insets;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;




class LicenseFileFilter implements FilenameFilter
{

    @Override
    public boolean accept( File dir, String name )
    {
        return name.endsWith(".xml");
    }
}

class LicenseTableModel extends AbstractTableModel
{
    LicensePanel panel;
    ArrayList<ValidTicketContainer> ticket_list;
    JButton tonne_bt;
    JButton edit_bt;
    SimpleDateFormat sdf;


    String[] col_names = {UserMain.getString("Product"), UserMain.getString("Type"), UserMain.getString("Units"), UserMain.getString("Modulecode"), UserMain.getString("Valid"), UserMain.getString("Bearbeiten"), UserMain.getString("Löschen")};
    Class[] col_classes = {String.class,  String.class,  String.class,  String.class,  Boolean.class, JButton.class, JButton.class};

    public LicenseTableModel(LicensePanel _panel, ArrayList<ValidTicketContainer> _ticket_list)
    {
        panel = _panel;
        ticket_list = _ticket_list;
        tonne_bt = create_table_button( "/dimm/home/images/web_delete.png", panel );
        edit_bt = create_table_button(  "/dimm/home/images/web_edit.png", panel );
        sdf = new SimpleDateFormat("dd.MM.yyy");

    }

    public JButton create_table_button(String rsrc, LicensePanel panel)
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
        if (ticket_list == null)
            return 0;
        return ticket_list.size();
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



    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (ticket_list == null)
            return "";

        ValidTicketContainer tck = ticket_list.get(rowIndex);
        LicenseTicket ticket = tck.getTicket();

        switch (columnIndex)
        {
            case 0:
                return ticket.getProduct();
            case 1:
                return get_type_str( ticket );
            case 2:
                return Integer.toString( ticket.getUnits() );
            case 3:
                return Integer.toHexString( ticket.getUnits() );
            case 4:
                return new Boolean( tck.isValid() );
            case 5:
               return edit_bt;
            case 6:
                return tonne_bt;

            default:
                return "???";
        }
    }

    private Object get_type_str( LicenseTicket ticket )
    {
        if (ticket instanceof DemoLicenseTicket)
        {
            DemoLicenseTicket dlt = (DemoLicenseTicket) ticket;
            return UserMain.Txt("until") + " " + dlt.get_text();
        }
        if (ticket instanceof HWIDLicenseTicket)
        {
            return UserMain.Txt("Hardware");
        }

        return "Unknown license type " + ticket.getClass().getName();
    }
   
    public int get_edit_column()
    {
        return col_names.length - 2;
    }
    public int get_del_column()
    {
        return col_names.length - 1;
    }

    public ArrayList<ValidTicketContainer> getTicket_list()
    {
        return ticket_list;
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

}
/**
 *
 * @author mw
 */
public class LicensePanel extends GlossDialogPanel implements MouseListener,  PropertyChangeListener
{
    GlossTable table;
    
    ImageIcon ok_icn;
    ImageIcon nok_icn;
    ImageIcon empty_icn;
    LicenseTableModel model;



    /** Creates new form GetMailAddressPanel */
    public LicensePanel( )
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

        read_license_list();

        TXT_HWID.setText( read_hwid() );
      
    }


    public void set_table_header()
    {
        TableColumnModel cm = table.getTableHeader().getColumnModel();
        cm.getColumn(0).setPreferredWidth(100);
        cm.getColumn(1).setPreferredWidth(100);
        cm.getColumn(2).setPreferredWidth(60);
        cm.getColumn(3).setPreferredWidth(60);
        cm.getColumn(4).setMaxWidth(60);
        cm.getColumn(4).setMinWidth(60);

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
        BT_NEW_LIC = new GlossButton();
        jLabel1 = new javax.swing.JLabel();
        TXT_HWID = new javax.swing.JTextField();

        BT_OKAY.setText(UserMain.Txt("Close")); // NOI18N
        BT_OKAY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKAYActionPerformed(evt);
            }
        });

        BT_NEW_LIC.setText(UserMain.getString("Add_License")); // NOI18N
        BT_NEW_LIC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_NEW_LICActionPerformed(evt);
            }
        });

        jLabel1.setText(UserMain.getString("Hardware-ID")); // NOI18N

        TXT_HWID.setEditable(false);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(SCP_TABLE, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 521, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(TXT_HWID, javax.swing.GroupLayout.PREFERRED_SIZE, 83, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 153, Short.MAX_VALUE)
                        .addComponent(BT_NEW_LIC)
                        .addGap(18, 18, 18)
                        .addComponent(BT_OKAY, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(SCP_TABLE, javax.swing.GroupLayout.DEFAULT_SIZE, 123, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OKAY, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(TXT_HWID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_NEW_LIC))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKAYActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKAYActionPerformed
    {//GEN-HEADEREND:event_BT_OKAYActionPerformed
        // TODO add your handling code here:
        
        my_dlg.setVisible(false);

    }//GEN-LAST:event_BT_OKAYActionPerformed

    static File last_dir;
    static File last_file;

    LicenseTicket get_lic_from_user()
    {
                // TODO add your handling code here:
        FileDialog fd = new FileDialog(my_dlg);
        fd.setMode(FileDialog.LOAD);

        fd.setLocation(my_dlg.getLocationOnScreen().x + 20, my_dlg.getLocationOnScreen().y + 20 );


        if (last_dir != null)
        {
            fd.setDirectory(last_dir.getAbsolutePath());
        }
        if (last_file != null)
        {
            fd.setFile(last_file.getName());
        }
        else
        {
            fd.setFile("MailSecurer_license.xml");
        }
        fd.setFilenameFilter( new LicenseFileFilter() );
   

        fd.setVisible(true);


        String f_name = fd.getFile();
        if (f_name == null)
            return null;

        last_file = new File(fd.getDirectory(), f_name );

        last_dir = last_file.getParentFile();

        LicenseTicket ticket = read_license( last_file );
        return ticket;
    }

    void ask_for_update( LicenseTicket ticket )
    {
        if (UserMain.errm_ok_cancel(my_dlg, UserMain.Txt("Wollen_Sie_dieses_Lizenticket_übernehmen")))
        {
            FunctionCallConnect fcc = UserMain.fcc();

            XStream xs = new XStream();
            String ticket_str = xs.toXML(ticket);
            try
            {
                ticket_str = ZipUtilities.compress(ticket_str);
            }
            catch (Exception exc)
            {
                UserMain.errm_ok( my_dlg, UserMain.Txt("Lizenz_wurde_nicht_übernommen") + ": " + exc.getLocalizedMessage());
                return;
            }

            String ret = fcc.call_abstract_function("LicenseConfig CMD:SET PRD:" + ticket.getProduct() + "TK:" + ticket_str, FunctionCallConnect.SHORT_TIMEOUT );

             if (ret != null && fcc.get_last_err_code() == 0)
             {
                if (ret.charAt(0) == '0')
                {
                    UserMain.errm_ok( my_dlg, UserMain.Txt("Lizenz_wurde_übernommen"));
                    propertyChange(null);
                    return;
                }
             }
             UserMain.errm_ok( my_dlg, UserMain.Txt("Lizenz_wurde_nicht_übernommen"));
        }
    }

    private void BT_NEW_LICActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_NEW_LICActionPerformed
    {//GEN-HEADEREND:event_BT_NEW_LICActionPerformed
        // TODO add your handling code here:

        LicenseTicket ticket = get_lic_from_user();
        if (ticket == null)
            return;

        open_view_dlg( ticket );

        ask_for_update( ticket );
       

    }//GEN-LAST:event_BT_NEW_LICActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_NEW_LIC;
    private javax.swing.JButton BT_OKAY;
    private javax.swing.JScrollPane SCP_TABLE;
    private javax.swing.JTextField TXT_HWID;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables


    LicenseTicket read_license( File lic_path )
    {
        if (lic_path.exists())
        {
            FileInputStream fis = null;
            XStream xs = new XStream();
            try
            {
                fis = new FileInputStream(lic_path);
                Object o = xs.fromXML(fis);
                if (o instanceof LicenseTicket)
                {
                    LicenseTicket t = (LicenseTicket)o;
                    return t;
                }
            }
            catch (FileNotFoundException fileNotFoundException)
            {
            }
            finally
            {
                if (fis != null)
                {
                    try
                    {
                        fis.close();
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
                    boolean okay = del_object( row );

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
        read_license_list();

        table.tableChanged(new TableModelEvent(table.getModel()) );
        

        this.repaint();
    }

    private void open_view_dlg( int row )
    {
        ValidTicketContainer tck = model.getTicket_list().get(row);
        open_view_dlg( tck.getTicket() );
    }
    private void open_view_dlg( LicenseTicket ticket )
    {
        LicenseViewPanel pnl = new LicenseViewPanel( this, ticket );
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
        dlg.set_next_location(my_dlg);
        dlg.setVisible(true);
    }
    
    private String read_hwid( )
    {
        FunctionCallConnect fcc = UserMain.fcc();
        String ret = fcc.call_abstract_function("LicenseConfig CMD:HWID", FunctionCallConnect.SHORT_TIMEOUT );

         if (ret != null && fcc.get_last_err_code() == 0)
         {
            if (ret.charAt(0) == '0')
            {
                ParseToken pt = new ParseToken(ret.substring(3));
                String hwid = pt.GetString("HWID:");
                return hwid;
            }
         }
        return "invalid";
    }

    private boolean del_object( int row )
    {
        ValidTicketContainer tck = model.getTicket_list().get(row);

        FunctionCallConnect fcc = UserMain.fcc();
        String ret = fcc.call_abstract_function("LicenseConfig CMD:DEL PRD:" + tck.getTicket().getProduct(), FunctionCallConnect.SHORT_TIMEOUT );

         if (ret != null && fcc.get_last_err_code() == 0)
         {
            if (ret.charAt(0) == '0')
            {
                return true;
            }
         }
        return false;

    }

    void read_license_list()
    {
        FunctionCallConnect fcc = UserMain.fcc();


        String ret = fcc.call_abstract_function("LicenseConfig CMD:GET" , FunctionCallConnect.SHORT_TIMEOUT );

        model = new LicenseTableModel( this, null);

        if (ret != null && fcc.get_last_err_code() == 0)
        {
            if (ret.charAt(0) == '0')
            {
                ParseToken pt = new ParseToken(ret.substring(3));
                String lic = pt.GetString("TK:");
                lic = ZipUtilities.uncompress(lic);

                XStream xs = new XStream();
                Object o = xs.fromXML(lic);
                if (o instanceof ArrayList)
                {
                    ArrayList<ValidTicketContainer> ticket_list = (ArrayList<ValidTicketContainer>)o;
                    model = new LicenseTableModel( this, ticket_list );
                    table.setModel(model);
                    set_table_header();
                    set_tabel_row_height();
                }
            }
        }
    }
}
