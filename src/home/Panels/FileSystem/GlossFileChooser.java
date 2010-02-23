/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * GlossFileChooser.java
 *
 * Created on 02.02.2010, 21:06:23
 */

package dimm.home.Panels.FileSystem;

import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.Rendering.GlossTable;
import dimm.home.Rendering.SingleTextEditPanel;
import dimm.home.ServerConnect.RMXFileSystemView;
import dimm.home.UserMain;
import dimm.home.Utilities.DateStr;
import home.shared.Utilities.SizeStr;
import home.shared.SQL.RMXFile;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumnModel;





class FSFileEntry
{
    RMXFile root;

    public FSFileEntry( RMXFile root )
    {
        this.root = root;
    }

    @Override
    public String toString()
    {
        return root.getAbsolutePath();
    }

    public RMXFile getFile()
    {
        return root;
    }    
}

class FSRootEntry extends FSFileEntry
{
    public FSRootEntry( RMXFile root )
    {
        super( root );
    }
    public RMXFile getRoot()
    {
        return super.getFile();
    }
    @Override
    public String toString()
    {
        String name = root.getName();

        if (name == null || name.length() == 0)
        {
            name = root.getAbsolutePath();
        }
        return name;
    }
}

class FileSystemTableModel extends AbstractTableModel
{
    ArrayList<RMXFile> fileList;
    protected String[] col_names = {"",""};
    protected String[] detailed_col_names = {"","", "", ""};
    protected Class[] col_classes = { JButton.class, String.class };
    protected Class[] detailed_col_classes = { JButton.class, String.class, String.class, String.class };
    JButton directoryIcon;
    JButton fileIcon;
     boolean detailed_view;
    

    public FileSystemTableModel(  boolean detailed_view)
    {
        this.detailed_view = detailed_view;
        fileIcon = GlossTable.create_table_button(null);
        ImageIcon file_icn = new ImageIcon(this.getClass().getResource("/dimm/home/images/fileicon.png"));
        fileIcon.setIcon( file_icn );
        directoryIcon = GlossTable.create_table_button(null);
        directoryIcon.setIcon( UIManager.getIcon("FileView.directoryIcon"));
        fileList = new ArrayList<RMXFile>();
    }



    public void setFileList( RMXFile[] file_arr )
    {
        fileList.clear();
        for (int i = 0; i < file_arr.length; i++)
        {
            fileList.add( file_arr[i] );
        }
        fireTableDataChanged();
    }

    public ArrayList<RMXFile> getFileList()
    {
        return fileList;
    }
    

    @Override
    public int getRowCount()
    {
        return fileList.size() + 1;
    }

    @Override
    public int getColumnCount()
    {
        if (detailed_view)
            return detailed_col_classes.length;
        
        return col_classes.length;
    }

    @Override
    public Object getValueAt( int rowIndex, int columnIndex )
    {
        if (rowIndex == 0)
        {
            if (columnIndex == 0)
                return directoryIcon;
            if (columnIndex == 1)
                return "..";
            return "";
        }
        rowIndex--;

        RMXFile f = fileList.get(rowIndex);

        switch( columnIndex)
        {
            case 0: return f.isDirectory() ? directoryIcon : fileIcon;
            case 1: return f.getName();
            case 2: return new SizeStr(f.length());
            case 3: return new DateStr(f.lastModified());
        }

        return "";
    }
    
    RMXFile get_file( int row )
    {
        if (row == 0)
            return null;
        row--;
        
        return fileList.get( row );
    }

    @Override
    public Class<?> getColumnClass( int columnIndex )
    {
        if (detailed_view)
            return detailed_col_classes[columnIndex];
        return col_classes[columnIndex];
    }

    @Override
    public String getColumnName( int column )
    {
        if (detailed_view)
            return detailed_col_names[column];
        return col_names[column];
    }


}
/**
 *
 * @author mw
 */
public class GlossFileChooser extends GlossDialogPanel implements MouseListener
{
    GlossTable table;
    FileSystemTableModel model;
    RMXFileSystemView fsv;
    String start_path;
    boolean only_dirs;

    RMXFile act_fs_root;
    RMXFile act_dir;
    RMXFile act_file;

    Comparator file_sort;
    boolean sort_reverse;
    boolean detailed_view;
    int is_in_init = 0;



    /** Creates new form GlossFileChooser */
    public GlossFileChooser(RMXFileSystemView fsv, String start_path, String[] filter, boolean only_dirs )
    {
        this.start_path = start_path;
        this.fsv = fsv;
        this.only_dirs = only_dirs;


        initComponents();

        BT_UP.setIcon( UIManager.getIcon("FileChooser.upFolderIcon" ) );
        BT_NEW.setIcon( UIManager.getIcon("FileChooser.newFolderIcon" ) );
        BTT_DETAIL.setIcon( UIManager.getIcon("FileChooser.detailsViewIcon") );

        CB_TYPE.removeAllItems();
        if (filter != null)
        {
            for (int i = 0; i < filter.length; i++)
            {
                String string = filter[i];
                CB_TYPE.addItem(string);
            }
        }
        CB_TYPE.addItem("*.*");
        CB_TYPE.setSelectedIndex(0);
 

        model = new FileSystemTableModel(detailed_view);
        table = new GlossTable();

        table.setModel(model);
        set_table_header();
        table.addMouseListener(this);

        // REGISTER TABLE TO SCROLLPANEL
        table.embed_to_scrollpanel( SCP_LIST );

        file_sort = new Comparator<RMXFile>()
        {

            @Override
            public int compare( RMXFile o1, RMXFile o2 )
            {
                int ret = o1.getName().compareTo(o2.getName());
                if (sort_reverse)
                    ret *= -1;
                return ret;
            }
        };
    }

    void set_table_header()
    {
        TableColumnModel cm = table.getTableHeader().getColumnModel();
        cm.getColumn(0).setMinWidth(30);
        cm.getColumn(0).setMaxWidth(30);
        cm.getColumn(1).setPreferredWidth(120);
        if (detailed_view)
        {
            cm.getColumn(2).setPreferredWidth(50);
            cm.getColumn(3).setPreferredWidth(50);
        }
    }
    String get_act_filter()
    {
        return CB_TYPE.getSelectedItem().toString();
    }
    void filter_array( RMXFile[] arr )
    {
        String filter = get_act_filter();
        if (filter.equals("*.*"))
            return;

        for (int i = 0; i < arr.length; i++)
        {
            RMXFile rMXFile = arr[i];
            if (rMXFile.isDirectory())
                continue;

            // REMOVE IF NO MATCH
            if (!rMXFile.getName().matches(filter))
                arr[i] = null;
        }
    }

    @Override
    public void activate()
    {
        super.activate();
        fill_file_list();
    }


    
    private void fill_file_list()
    {
        is_in_init=1;
        RMXFile[] roots = fsv.getRoots();

        CB_FSROOT.removeAllItems();

        
        for (int i = 0; i < roots.length; i++)
        {
            CB_FSROOT.addItem(new FSRootEntry( roots[i]));
        }
        

        if (start_path == null || start_path.length() == 0)
        {
            if (roots.length > 0)                
            {
                start_path = roots[0].getAbsolutePath();
            }
            else
            {
                start_path = "";
            }
        }
        RMXFile act_start = fsv.createFileObject(start_path);
        if (act_start == null)
            act_start = roots[0];

        if (act_start.isDirectory())
        {
            set_act_dir( act_start );
            set_act_file( act_dir );
            set_act_fs_root( fsv.getRoot(act_dir) );
        }
        else
        {
            set_act_dir( fsv.getParentDirectory(act_start) );
            set_act_file( act_start );
            set_act_fs_root( fsv.getRoot(act_dir) );
        }
        RMXFile[] file_list = fsv.getFiles(act_dir, only_dirs);
        Arrays.sort( file_list, file_sort);
        filter_array(file_list);

        model.setFileList(file_list);
        is_in_init--;

    }

    boolean set_act_fs_root( RMXFile root )
    {
        boolean ret = false;
        is_in_init++;

        act_fs_root = root;

        int cnt = CB_FSROOT.getItemCount();
        if (act_fs_root != null)
        {
            for (int i = 0; i < cnt; i++)
            {
                FSFileEntry entry = (FSFileEntry)CB_FSROOT.getItemAt(i);
                if (entry.getFile().getAbsolutePath().equals(act_fs_root.getAbsolutePath()))
                {
                    CB_FSROOT.setSelectedIndex(i);
                    ret = true;
                }
            }
        }
        is_in_init--;

        return ret;
    }
    void set_act_dir( RMXFile f )
    {
        is_in_init++;

        act_dir = f;

        // IF WE ARE NOT A ROOT, THEN ADD TO LIST
        if (!set_act_fs_root( f ))
        {
            if (CB_FSROOT.getItemAt(0) instanceof FSRootEntry)
            {
                CB_FSROOT.insertItemAt(new FSFileEntry(act_dir), 0);
            }
            if (CB_FSROOT.getItemAt(0) instanceof FSFileEntry)
            {
                CB_FSROOT.removeItemAt(0);
                CB_FSROOT.insertItemAt(new FSFileEntry(act_dir), 0);

            }
            SwingUtilities.invokeLater( new Runnable() {

                @Override
                public void run()
                {
                    CB_FSROOT.setSelectedItem(CB_FSROOT.getItemAt(0));
                }
            });
        }
        
        is_in_init--;
    }

    void set_act_file( RMXFile f )
    {
        is_in_init++;

        act_file = f;
        if (act_file == null)
            act_file = act_dir;

        TXT_PATH.setText(act_file.getAbsolutePath());


        is_in_init--;

    }

    void set_new_dir( RMXFile dir )
    {
        set_act_dir( dir );
        set_act_file( dir );
        RMXFile[] file_list = fsv.getFiles(act_dir, only_dirs);
        Arrays.sort( file_list, file_sort);
        filter_array(file_list);
        
        model.setFileList(file_list);        
    }
    void up()
    {
        act_dir = fsv.getParentDirectory(act_dir);
        set_new_dir( act_dir );
    }
    public RMXFile get_act_dir()
    {
        return act_dir;
    }
    public RMXFile get_act_file()
    {
        return act_file;
    }



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        CB_FSROOT = new javax.swing.JComboBox();
        BT_UP = new javax.swing.JButton();
        BT_NEW = new javax.swing.JButton();
        BTT_DETAIL = new javax.swing.JToggleButton();
        SCP_LIST = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        TXT_PATH = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        CB_TYPE = new javax.swing.JComboBox();
        jPanel3 = new javax.swing.JPanel();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();

        jPanel1.setOpaque(false);

        jLabel2.setText(UserMain.getString("Search_in")); // NOI18N

        CB_FSROOT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_FSROOTActionPerformed(evt);
            }
        });
        CB_FSROOT.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                CB_FSROOTPropertyChange(evt);
            }
        });

        BT_UP.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/web_not_selected.png"))); // NOI18N
        BT_UP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_UPActionPerformed(evt);
            }
        });

        BT_NEW.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/web_check.png"))); // NOI18N
        BT_NEW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_NEWActionPerformed(evt);
            }
        });

        BTT_DETAIL.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/ic_attachment.png"))); // NOI18N
        BTT_DETAIL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BTT_DETAILActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CB_FSROOT, 0, 288, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BT_UP, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BT_NEW, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BTT_DETAIL, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(CB_FSROOT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(BT_UP)
                        .addComponent(BT_NEW)
                        .addComponent(jLabel2))
                    .addComponent(BTT_DETAIL))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setOpaque(false);

        jLabel1.setText(UserMain.getString("Filename")); // NOI18N

        TXT_PATH.setEditable(false);

        jLabel3.setText(UserMain.getString("Filetype")); // NOI18N

        CB_TYPE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_TYPEActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TXT_PATH, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
                    .addComponent(CB_TYPE, 0, 402, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(TXT_PATH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(CB_TYPE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setOpaque(false);

        BT_OK.setText(UserMain.getString("Select")); // NOI18N
        BT_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKActionPerformed(evt);
            }
        });

        BT_ABORT.setText(UserMain.getString("Abort")); // NOI18N
        BT_ABORT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ABORTActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(210, Short.MAX_VALUE)
                .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OK)
                    .addComponent(BT_ABORT))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(SCP_LIST, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 468, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SCP_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKActionPerformed
    {//GEN-HEADEREND:event_BT_OKActionPerformed
        // TODO add your handling code here:
        my_dlg.setVisible(false);
    }//GEN-LAST:event_BT_OKActionPerformed

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ABORTActionPerformed
    {//GEN-HEADEREND:event_BT_ABORTActionPerformed
        // TODO add your handling code here:
        act_dir = null;
        act_file = null;
        my_dlg.setVisible(false);
    }//GEN-LAST:event_BT_ABORTActionPerformed

    private void CB_FSROOTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_FSROOTActionPerformed
    {//GEN-HEADEREND:event_CB_FSROOTActionPerformed
        // TODO add your handling code here:
        if (is_in_init > 0)
            return;

        FSFileEntry entry = (FSFileEntry)CB_FSROOT.getSelectedItem();
        if (entry != null)
        {
            set_new_dir(entry.getFile());
        }

    }//GEN-LAST:event_CB_FSROOTActionPerformed

    private void CB_TYPEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_TYPEActionPerformed
    {//GEN-HEADEREND:event_CB_TYPEActionPerformed
        // TODO add your handling code here:
        // RESCAN
        if (is_in_init > 0)
            return;

        if (act_dir != null)
            set_new_dir( act_dir );

    }//GEN-LAST:event_CB_TYPEActionPerformed

    private void BT_UPActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_UPActionPerformed
    {//GEN-HEADEREND:event_BT_UPActionPerformed
        // TODO add your handling code here:
        up();
    }//GEN-LAST:event_BT_UPActionPerformed

    private void BT_NEWActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_NEWActionPerformed
    {//GEN-HEADEREND:event_BT_NEWActionPerformed
        // TODO add your handling code here:
        SingleTextEditPanel pnl = new SingleTextEditPanel( UserMain.Txt("New_name") );
        pnl.setText(UserMain.Txt("New_folder"));
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
        dlg.set_next_location(BT_NEW );
        dlg.setVisible(true);
        if (pnl.isOkay())
        {
            RMXFile new_dir = fsv.createFileObject(act_dir, pnl.getText());
            if (new_dir != null)
            {
                try
                {
                    new_dir = fsv.createNewFolder(new_dir);
                }
                catch (IOException iOException)
                {
                    new_dir = null;
                }
            }
            if (new_dir == null)
            {
                UserMain.errm_ok(my_dlg, UserMain.Txt("Cannot_create_directory") + "<" + pnl.getText() + ">" );
            }
            else
            {
                // REFRESH
                set_new_dir( act_dir );
            }
        }
    }//GEN-LAST:event_BT_NEWActionPerformed

    private void BTT_DETAILActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BTT_DETAILActionPerformed
    {//GEN-HEADEREND:event_BTT_DETAILActionPerformed
        // TODO add your handling code here:
        detailed_view = BTT_DETAIL.isSelected();

        model = new FileSystemTableModel(detailed_view);
        table.setModel(model);
        set_table_header();
    }//GEN-LAST:event_BTT_DETAILActionPerformed

    private void CB_FSROOTPropertyChange(java.beans.PropertyChangeEvent evt)//GEN-FIRST:event_CB_FSROOTPropertyChange
    {//GEN-HEADEREND:event_CB_FSROOTPropertyChange
        // TODO add your handling code here:
       if (is_in_init > 0)
            return;

        FSFileEntry entry = (FSFileEntry)CB_FSROOT.getSelectedItem();
        if (entry != null)
        {
            set_new_dir(entry.getFile());
        }
    }//GEN-LAST:event_CB_FSROOTPropertyChange


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JToggleButton BTT_DETAIL;
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_NEW;
    private javax.swing.JButton BT_OK;
    private javax.swing.JButton BT_UP;
    private javax.swing.JComboBox CB_FSROOT;
    private javax.swing.JComboBox CB_TYPE;
    private javax.swing.JScrollPane SCP_LIST;
    private javax.swing.JTextField TXT_PATH;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mouseClicked( MouseEvent e )
    {
        if (e.getClickCount() == 2)
        {
            int row = table.getSelectedRow();
            if (row == 0)
            {
                up();
            }
            else
            {
                RMXFile f = model.get_file(row);
                if (f.isDirectory())
                {
                    set_new_dir(f);
                }
            }
        }
        if (e.getClickCount() == 1)
        {
            int row = table.getSelectedRow();
            set_act_file( model.get_file(row) );
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
        return BT_OK;
    }



}
