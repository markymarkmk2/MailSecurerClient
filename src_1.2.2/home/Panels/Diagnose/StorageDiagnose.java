/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * StorageDiagnose.java
 *
 * Created on 11.02.2010, 10:36:26
 */

package dimm.home.Panels.Diagnose;

import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.Rendering.GlossTable;
import dimm.home.UserMain;
import dimm.home.Utilities.DateStr;
import dimm.home.Utilities.SwingWorker;
import home.shared.CS_Constants;
import home.shared.SQL.SQLResult;
import home.shared.Utilities.ParseToken;
import home.shared.Utilities.SizeStr;
import home.shared.hibernate.DiskArchive;
import home.shared.hibernate.DiskSpace;
import home.shared.hibernate.Mandant;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.RowSorter;
import javax.swing.Timer;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;

class SMandantEntry
{
    public static final int ME_SYSTEM = -1;
    public static final int ME_ALL = -2;

    Mandant m;
    int id;

    public SMandantEntry( Mandant m )
    {
        this.m = m;
        id = m.getId();
    }
    public SMandantEntry( int _id )
    {
        this.m = null;
        id = _id;
    }

    public int getId()
    {
        return id;
    }



    @Override
    public String toString()
    {
        if (m != null)
            return m.getName();

        if (id == ME_SYSTEM)
            return UserMain.Txt("System");

        return UserMain.Txt("All");
    }

}



class DS_StatusEntry
{
    DiskSpace ds;
    long docs;
    long capacity;
    long free_space;
    long total_space;
    long last_mod;

    public DS_StatusEntry( DiskArchive da, String line)
    {
        // "TP:DS ID:" + ds.getId() + " PA:\"" + ds.getPath() + "\" + ST:" + ds.getStatus() + " CNT:" + docs + " CAP:" + dsi.getCapacity() + "FS:" + free_space + " TS:" + total_space + " LM:" + last_mod + "\n";

        ParseToken pt = new ParseToken(line);

        SQLResult<DiskSpace> ds_result = UserMain.sqc().get_ds_result();

        int ds_id = (int)pt.GetLongValue("ID:");
        for (int i = 0; i < ds_result.getRows(); i++)
        {
            DiskSpace _ds = ds_result.get(i);
            if (_ds.getId() == ds_id)
            {
                ds = _ds;
                break;
            }
        }
        docs = pt.GetLongValue("CNT:");
        capacity = pt.GetLongValue("CAP:");
        free_space = pt.GetLongValue("FS:");
        total_space = pt.GetLongValue("TS:");
        last_mod = pt.GetLongValue("LM:");
    }
    String get_da_name()
    {
        return ds.getDiskArchive().getName();
    }
    boolean is_offline()
    {
        if ((Integer.parseInt(ds.getFlags()) & CS_Constants.DS_DISABLED) == CS_Constants.DS_DISABLED)
            return true;

        if ((Integer.parseInt(ds.getDiskArchive().getFlags()) & CS_Constants.DA_DISABLED) == CS_Constants.DS_DISABLED)
            return true;

        return false;
    }
}

class DiagnoseDSTableModel extends AbstractTableModel
{
    StorageDiagnose panel;

    JButton edit_bt;
    SimpleDateFormat sdf;
    ArrayList<DS_StatusEntry> dse_list;



    String[] col_names = {UserMain.getString("DiskArchive"), UserMain.getString("Path"), UserMain.getString("Docs"), UserMain.getString("Used"), UserMain.getString("MaxCapacity"), UserMain.getString("PartitionFree"), UserMain.getString("PartitionUsed"), UserMain.getString("LastUsed")};
    Class[] col_classes = {String.class, String.class, String.class, String.class, String.class, String.class, String.class, String.class  };

    public DiagnoseDSTableModel(StorageDiagnose _panel, ArrayList<DS_StatusEntry> dse_list)
    {
        panel = _panel;
        this.dse_list = dse_list;
        
        edit_bt = create_table_button(  "/dimm/home/images/web_edit.png", panel );
        sdf = new SimpleDateFormat("dd.MM.yyy");

    }

    void set_dse_list( ArrayList<DS_StatusEntry> dse_list )
    {
        this.dse_list = dse_list;
        this.fireTableDataChanged();
    }


    public JButton create_table_button(String rsrc, StorageDiagnose panel)
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
        return dse_list.size();
    }

    @Override
    public int getColumnCount()
    {
        return col_names.length;
    }



    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        DS_StatusEntry dse = dse_list.get(rowIndex);


        switch (columnIndex)
        {
            case 0:
                return dse.get_da_name();
            case 1:
                return dse.ds.getPath();
            case 2:
                return new SizeStr( dse.docs );
            case 3:
                return new SizeStr( dse.capacity );
            case 4:
                return dse.ds.getMaxCapacity();
            case 5:
                return new SizeStr( dse.free_space );
            case 6:
                return new SizeStr( dse.total_space );
            case 7:
                return new DateStr( dse.last_mod / 1000);
            
            

            default:
                return "???";
        }
    }


    public int get_edit_column()
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
        }
    }
}


/**
 *
 * @author mw
 */
public class StorageDiagnose extends GlossDialogPanel implements MouseListener, ActionListener
{
    int ma_id;
    GlossTable table;
    ImageIcon ok_icn;
    ImageIcon nok_icn;
    ImageIcon empty_icn;
    DiagnoseDSTableModel model;

    ArrayList<DS_StatusEntry> dse_list;
    Timer timer;


    /** Creates new form StorageDiagnose */
    public StorageDiagnose( int ma_id)
    {
        this.ma_id = ma_id;
        dse_list = new ArrayList<DS_StatusEntry>();

        initComponents();

        SQLResult<Mandant> mr = UserMain.sqc().get_mandant_result();

        CB_MANDANT.removeAllItems();
        if (ma_id < 0)
        {
            CB_MANDANT.addItem( new SMandantEntry(SMandantEntry.ME_ALL));
        }
        CB_MANDANT.addItem( new SMandantEntry(SMandantEntry.ME_SYSTEM));

        for (int i = 0; i < mr.size(); i++)
        {
            Mandant mandant = mr.get(i);
            if (ma_id >= 0)
            {
                if (mandant.getId() != ma_id)
                    continue;
            }
            CB_MANDANT.addItem( new SMandantEntry(mandant));
        }
        table = new GlossTable();

        model = new DiagnoseDSTableModel(this, dse_list);
        table.setModel(model);
        RowSorter sorter = new TableRowSorter(model);
        table.setRowSorter(sorter);
        table.addMouseListener(this);

        // REGISTER TABLE TO SCROLLPANEL
        table.embed_to_scrollpanel( SCP_TABLE );

        timer = new Timer(5000, this);
        actionPerformed(null);
        timer.start();


    }

    void read_status()
    {
        dse_list.clear();

        SQLResult<Mandant> mr = UserMain.sqc().get_mandant_result();
        for (int i = 0; i < mr.size(); i++)
        {
            Mandant mandant = mr.get(i);
            if (ma_id >= 0)
            {
                if (mandant.getId() != ma_id)
                    continue;
            }
            SQLResult<DiskArchive> da_set = UserMain.sqc().get_da_result();

            for (int j = 0; j < da_set.getRows(); j++)
            {
                DiskArchive da = da_set.get(j);
                if (ma_id >= 0)
                {
                    if (da.getMandant().getId() != ma_id)
                        continue;
                }

                read_status( da );
            }
        }
    }


    boolean read_status( DiskArchive da )
    {         

        String ret = UserMain.fcc().call_abstract_function("ListVaultData CMD:status MA:" + ma_id + " DA:" + da.getId() );
        if (ret == null)
        {
            UserMain.errm_ok(UserMain.Txt("Cannot_read_status_of_diskarchive") +" " + da.getName());
            return false;
        }
        if (ret.charAt(0) != '0')
        {
            UserMain.errm_ok(UserMain.Txt("Error_reading_status_of_diskarchive") + " " + da.getName() + ": " + ret);
            return false;
        }

        ret = ParseToken.DeCompressObject(ret.substring(3)).toString();

        StringTokenizer str = new StringTokenizer(ret, "\n\r");
        while (str.hasMoreTokens())
        {
            String line = str.nextToken();
            DS_StatusEntry dse = new DS_StatusEntry(da, line);
            if (dse.ds != null)
                dse_list.add(dse);
            else
                System.out.println("Missing DS: " + line);
        }
        return true;



    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel4 = new javax.swing.JLabel();
        CB_MANDANT = new javax.swing.JComboBox();
        PN_DISKSPACES = new javax.swing.JPanel();
        SCP_TABLE = new javax.swing.JScrollPane();
        BT_OKAY = new GlossButton();

        jLabel4.setText(UserMain.Txt("Mandant")); // NOI18N

        CB_MANDANT.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        PN_DISKSPACES.setBorder(javax.swing.BorderFactory.createTitledBorder(UserMain.Txt("DiskSpaces"))); // NOI18N

        javax.swing.GroupLayout PN_DISKSPACESLayout = new javax.swing.GroupLayout(PN_DISKSPACES);
        PN_DISKSPACES.setLayout(PN_DISKSPACESLayout);
        PN_DISKSPACESLayout.setHorizontalGroup(
            PN_DISKSPACESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_TABLE, javax.swing.GroupLayout.DEFAULT_SIZE, 685, Short.MAX_VALUE)
        );
        PN_DISKSPACESLayout.setVerticalGroup(
            PN_DISKSPACESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_TABLE, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 225, Short.MAX_VALUE)
        );

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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(PN_DISKSPACES, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(CB_MANDANT, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(609, Short.MAX_VALUE)
                        .addComponent(BT_OKAY, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(CB_MANDANT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(PN_DISKSPACES, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addComponent(BT_OKAY)
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
    private javax.swing.JComboBox CB_MANDANT;
    private javax.swing.JPanel PN_DISKSPACES;
    private javax.swing.JScrollPane SCP_TABLE;
    private javax.swing.JLabel jLabel4;
    // End of variables declaration//GEN-END:variables

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
                //open_view_dlg( row );
            }
        }

        // DBLCLICK OPENS EDIT TOO
        if (e.getClickCount() == 2)
        {
             //open_view_dlg( row );
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

    SwingWorker sw;
    @Override
    public void actionPerformed( ActionEvent e )
    {
        if (sw != null && !sw.finished())
        {
            return;
        }
        sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                try
                {
                    read_status();
                }
                catch (Exception e)
                {
                }
                return null;
            }
        };
        sw.start();
    }

    @Override
    public void deactivate()
    {
        timer.stop();
    }




}
