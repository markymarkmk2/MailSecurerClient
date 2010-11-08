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
import dimm.home.Utilities.SwingWorker;
import home.shared.SQL.SQLResult;
import home.shared.Utilities.ParseToken;
import home.shared.hibernate.DiskArchive;
import home.shared.hibernate.Mandant;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.StringTokenizer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.RowSorter;
import javax.swing.Timer;
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


/**
 *
 * @author mw
 */
public class StorageDiagnose extends GlossDialogPanel implements MouseListener, ActionListener
{
    int ma_id;
    GlossTable ds_table;
    GlossTable wrk_table;
    ImageIcon ok_icn;
    ImageIcon nok_icn;
    ImageIcon empty_icn;
    DiagnoseDSTableModel ds_model;
    DiagnoseWrkTableModel wrk_model;

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
        ds_table = new GlossTable();

        ds_model = new DiagnoseDSTableModel(this, dse_list);
        ds_table.setModel(ds_model);
        RowSorter sorter = new TableRowSorter(ds_model);
        ds_table.setRowSorter(sorter);
        ds_table.addMouseListener(this);

        // REGISTER TABLE TO SCROLLPANEL
        ds_table.embed_to_scrollpanel( SCP_TABLE );

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


    static String last_ret = null;
    
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

        ret = UserMain.fcc().call_abstract_function("GETWORKERSTATUS MA:" + ma_id );
        if (ret != null && ret.charAt(0) == '0')
        {
            if (last_ret == null || last_ret.compareTo(ret) != 0)
            {
                last_ret = ret;
                Object o = ParseToken.DeCompressObject(ret.substring(3)).toString();
                if (o instanceof ArrayList)
                {
                    ArrayList<ArrayList<String>> result_list = (ArrayList<ArrayList<String>>) o;
                    wrk_model.set_result_list(result_list);
                }
            }
        }

        

        String lic_txt = "";
        ret = UserMain.fcc().call_abstract_function("LicenseConfig CMD:CHECK PRD:MailSecurer" );
        if (ret != null && ret.charAt(0) == '0')
        {
            ParseToken pt = new ParseToken(ret.substring(3));
            boolean licensed = pt.GetBoolean("LS:");
            if (!licensed)
            {
                lic_txt = UserMain.Txt("unlicensed");
            }
            else if (pt.GetString("MU:").length() > 0)
            {
                lic_txt = "  " + pt.GetString("UU:") + "/" + pt.GetString("MU:");
            }
        }
        TXT_LIC.setText(lic_txt);

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
        PN_WOKER = new javax.swing.JPanel();
        SCP_WRK_TABLE = new javax.swing.JScrollPane();
        BT_OKAY = new GlossButton();
        TXT_LIC = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        PN_DISKSPACES = new javax.swing.JPanel();
        SCP_TABLE = new javax.swing.JScrollPane();

        jLabel4.setText(UserMain.Txt("Mandant")); // NOI18N

        CB_MANDANT.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        PN_WOKER.setBorder(javax.swing.BorderFactory.createTitledBorder(UserMain.Txt("Worker"))); // NOI18N

        javax.swing.GroupLayout PN_WOKERLayout = new javax.swing.GroupLayout(PN_WOKER);
        PN_WOKER.setLayout(PN_WOKERLayout);
        PN_WOKERLayout.setHorizontalGroup(
            PN_WOKERLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_WRK_TABLE, javax.swing.GroupLayout.DEFAULT_SIZE, 709, Short.MAX_VALUE)
        );
        PN_WOKERLayout.setVerticalGroup(
            PN_WOKERLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_WRK_TABLE, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 169, Short.MAX_VALUE)
        );

        BT_OKAY.setText(UserMain.Txt("Close")); // NOI18N
        BT_OKAY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKAYActionPerformed(evt);
            }
        });

        TXT_LIC.setEditable(false);
        TXT_LIC.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        TXT_LIC.setText("1000/1000");

        jLabel5.setText(UserMain.Txt("Licensestatus")); // NOI18N

        PN_DISKSPACES.setBorder(javax.swing.BorderFactory.createTitledBorder(UserMain.Txt("DiskSpaces"))); // NOI18N

        javax.swing.GroupLayout PN_DISKSPACESLayout = new javax.swing.GroupLayout(PN_DISKSPACES);
        PN_DISKSPACES.setLayout(PN_DISKSPACESLayout);
        PN_DISKSPACESLayout.setHorizontalGroup(
            PN_DISKSPACESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_TABLE, javax.swing.GroupLayout.DEFAULT_SIZE, 699, Short.MAX_VALUE)
        );
        PN_DISKSPACESLayout.setVerticalGroup(
            PN_DISKSPACESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_TABLE, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 126, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PN_WOKER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(CB_MANDANT, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(18, 18, 18)
                                .addComponent(TXT_LIC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 472, Short.MAX_VALUE)
                                .addComponent(BT_OKAY, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(PN_DISKSPACES, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(CB_MANDANT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(PN_DISKSPACES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PN_WOKER, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OKAY)
                    .addComponent(jLabel5)
                    .addComponent(TXT_LIC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
    private javax.swing.JPanel PN_WOKER;
    private javax.swing.JScrollPane SCP_TABLE;
    private javax.swing.JScrollPane SCP_WRK_TABLE;
    private javax.swing.JTextField TXT_LIC;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    // End of variables declaration//GEN-END:variables

     @Override
    public JButton get_default_button()
    {
        return BT_OKAY;
    }

   @Override
    public void mouseClicked(MouseEvent e)
    {
        Component c = ds_table.getComponentAt(e.getPoint());
        int row = ds_table.rowAtPoint(e.getPoint());
        int col = ds_table.columnAtPoint(e.getPoint());

        if (e.getClickCount() == 1)
        {
            if (col == ds_model.get_edit_column())
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
