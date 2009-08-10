/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MailViewPanel.java
 *
 * Created on 15.07.2009, 16:14:22
 */

package dimm.home.Panels;

import dimm.home.Main;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.UserMain;
import dimm.home.Utilities.ExtFilenameFilter;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Enumeration;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;


class MboxFilenameFilter implements FilenameFilter
{
    String extension;
    boolean recursive;

    MboxFilenameFilter()
    {
        extension = ".msf";
        recursive = true;
    }
    @Override
    public boolean accept( File dir, String name )
    {
        if (name.endsWith(extension))
            return true;

        if (recursive)
        {
            File fdir = new File( dir, name );
            if (fdir.isDirectory())
            {
                // SBD ARE HANDLED INTERNALLY
                if (!fdir.getName().endsWith(".sbd"))
                    return true;
            }
        }


        return false;
    }
}

class MboxTreeNode implements TreeNode
{
    MboxTreeNode parent;
    File node;
    MboxFilenameFilter filter;
    MboxTreeNode[] child_list;
    boolean is_selected;
    String[] default_sel_offnames = {"Junk", "Spam", "Trash", "Drafts", "Templates"};

    MboxTreeNode( MboxTreeNode p, File n )
    {
        parent = p;
        filter = new MboxFilenameFilter();
        node = n;
        is_selected = true;
        
        for (int i = 0; i < default_sel_offnames.length; i++)
        {
            String no_sel = default_sel_offnames[i];
            if (node.getName().indexOf(no_sel) != -1)
                is_selected = false;            
        }
    }
    MboxTreeNode( File r )
    {
        parent = null;
        filter = new MboxFilenameFilter();
        node = r;
        is_selected = true;  // THE WHOLE TREE 
    }

    @Override
    public TreeNode getChildAt( int childIndex )
    {
        check_children();

        if (childIndex < child_list.length)
            return child_list[childIndex];

        return null;
    }
    File get_file()
    {
        return node;
    }
    boolean get_is_selected()
    {
        return is_selected;
    }

    @Override
    public int getChildCount()
    {
        check_children();

        return child_list.length;
    }

    @Override
    public TreeNode getParent()
    {
        return parent;
    }

    @Override
    public int getIndex( TreeNode _n )
    {
        MboxTreeNode n = (MboxTreeNode)_n;
        check_children();

        for (int i = 0; i < child_list.length; i++)
        {
            File file = child_list[i].get_file();
            if (file.equals( n.get_file() ))
            {
                return i;
            }
        }
        return -1;
    }
    void check_children()
    {
        if (child_list == null)
        {
            File[] _flist;

            if (node.isDirectory())
                _flist = node.listFiles(filter);
            else
            {
                // WE ARE MSF
                String fp = node.getAbsolutePath();
                // LOOK IN SBD FOLDER FOR SUB-DIRS
                File sbd_dir = new File( fp.substring(0, fp.length() - 4) + ".sbd" );
                if (sbd_dir.exists())
                    _flist = sbd_dir.listFiles(filter);
                else
                    _flist = new File[0];
            }
            
            child_list = new MboxTreeNode[_flist.length];
            for (int i = 0; i < _flist.length; i++)
            {
                File file = _flist[i];
                child_list[i] = new MboxTreeNode( this, file );
            }
        }
    }

    @Override
    public boolean getAllowsChildren()
    {
        return true;
    }

    @Override
    public boolean isLeaf()
    {
        check_children();

        if (child_list.length == 0)
            return true;//node.isFile();

        return false;

    }

    @Override
    public Enumeration children()

    {
        Enumeration en = new Enumeration()
        {
            int idx = 0;

            @Override
            public boolean hasMoreElements()
            {
                return (idx < getChildCount());
            }

            @Override
            public Object nextElement()
            {
                return getChildAt(idx++);
            }
        };

        return en;
    }
    String get_mbox_name()
    {
        if (node.isDirectory())
            return node.getName();

        return node.getName().substring(0, node.getName().length() - 4);

    }

    void toggle_selected(MboxTreeModel model)
    {
        is_selected = !is_selected;
        model.nodeChanged(this);

        check_children();

        for (int i = 0; i < child_list.length; i++)
        {
            MboxTreeNode mboxTreeNode = child_list[i];
            mboxTreeNode.toggle_selected(model);
        }
    }
}
class MboxTreeModel extends DefaultTreeModel
{
    MboxTreeModel( TreeNode n )
    {
        super(n);
    }

}


class MboxTreeCellRenderer implements TreeCellRenderer
{

    JCheckBox jcb;
    JLabel jlb;

    MboxTreeCellRenderer()
    {
        jcb = new JCheckBox();
        jlb = new JLabel();
        jlb.setForeground(Main.ui.get_nice_white() );
        jlb.setBackground(Main.ui.get_appl_dgray() );
    }
    
    @Override
    public Component getTreeCellRendererComponent( JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus )
    {
        if (value instanceof MboxTreeNode)
        {
            MboxTreeNode node = (MboxTreeNode) value;
            jcb.setText(node.get_mbox_name());
            jcb.setSelected( node.get_is_selected());
            return jcb;

        }

        jlb.setText("?");
        return jlb;
    }
}
/**
 *
 * @author mw
 */
public class PanelImportMailbox extends GlossDialogPanel implements MouseListener
{

    MboxTreeModel model;


    private void set_next_state()
    {
        switch(state)
        {
            case IMP_SEL_TYPE:
            {
                TP_PANE.setEnabledAt(0, false);
                TP_PANE.setEnabledAt(1, true);
                TP_PANE.setSelectedIndex(1);
                //PN_SELECT.setVisible(false);
                //PN_THUNDERBIRD.setVisible(true);

                state = IMPORT_STATE.IMP_SEL_MBOXES;
                BT_BACK.setVisible(true);
                break;
            }
            case IMP_SEL_MBOXES:
            {
                TP_PANE.setEnabledAt(1, false);
                TP_PANE.setEnabledAt(2, true);
                TP_PANE.setSelectedIndex(2);
//                PN_THUNDERBIRD.setVisible(false);
//                PN_CONFIRM.setVisible(true);
                handle_check();
                
                state = IMPORT_STATE.IMP_CONFIRM;
                break;
            }
            case IMP_CONFIRM:
            {
                TP_PANE.setEnabledAt(2, false);
                TP_PANE.setEnabledAt(3, true);
                TP_PANE.setSelectedIndex(3);
//                PN_CONFIRM.setVisible(false);
//                PN_STATUS.setVisible(true);
                
                state = IMPORT_STATE.IMP_STATUS;
                BT_NEXT.setText(UserMain.Txt("Close"));
                BT_BACK.setVisible(false);
                handle_import();
                break;
            }
            case IMP_STATUS:
            {
                setVisible(false);
            }
        }
        repaint();
    }

    private void set_prev_state()
    {
        switch(state)
        {
            case IMP_SEL_MBOXES:
            {
                TP_PANE.setEnabledAt(1, false);
                TP_PANE.setEnabledAt(0, true);
                TP_PANE.setSelectedIndex(0);
//                PN_THUNDERBIRD.setVisible(false);
//                PN_SELECT.setVisible(true);
                state = IMPORT_STATE.IMP_SEL_TYPE;
                BT_BACK.setVisible(false);
                break;
            }
            case IMP_CONFIRM:
            {
                TP_PANE.setEnabledAt(2, false);
                TP_PANE.setEnabledAt(1, true);
                TP_PANE.setSelectedIndex(1);
//                PN_CONFIRM.setVisible(false);
//                PN_THUNDERBIRD.setVisible(true);
                state = IMPORT_STATE.IMP_SEL_MBOXES;
            }
        }
    }

    private void handle_import()
    {
        UserMain.errm_ok("Importing...");
    }

    private void handle_check()
    {
        UserMain.errm_ok("Checking...");
    }


    enum IMPORT_STATE
    {
        IMP_SEL_TYPE,
        IMP_SEL_MBOXES,
        IMP_CONFIRM,
        IMP_STATUS
    };

    IMPORT_STATE state;

    /** Creates new form MailViewPanel */
    public PanelImportMailbox()
    {
        initComponents();

        MboxTreeNode node = new MboxTreeNode( new File( "Z:\\Mail_lokales_Konto\\Thunderbird\\Profiles\\p27621i7.default\\Mail") );
        model = new MboxTreeModel( node );
        JT_DIR.setModel(model);
        JT_DIR.setCellRenderer( new MboxTreeCellRenderer() );
        JT_DIR.setForeground(Main.ui.get_nice_white() );
        JT_DIR.setBackground(Main.ui.get_appl_dgray() );

        /*PN_SELECT.setVisible(true);
        PN_THUNDERBIRD.setVisible(false);
        PN_CONFIRM.setVisible(false);
        PN_STATUS.setVisible(false);*/
        TP_PANE.setEnabledAt(0, true);
        TP_PANE.setEnabledAt(1, false);
        TP_PANE.setEnabledAt(2, false);
        TP_PANE.setEnabledAt(3, false);

        BT_BACK.setVisible(false);

        state = IMPORT_STATE.IMP_SEL_TYPE;

        
        MouseListener ml = new MouseAdapter()
        {

            @Override
            public void mousePressed( MouseEvent e )
            {
                int selRow = JT_DIR.getRowForLocation(e.getX(), e.getY());
                TreePath selPath = JT_DIR.getPathForLocation(e.getX(), e.getY());
                if (selRow != -1)
                {
                    if (e.getClickCount() == 1)
                    {
                        mySingleClick(selRow, selPath);
                    }
                    else if (e.getClickCount() == 2)
                    {
                        myDoubleClick(selRow, selPath);
                    }
                }
            }
        };
        JT_DIR.addMouseListener(ml);



    }

    void mySingleClick(int selRow, TreePath selPath)
    {
        MboxTreeNode n = (MboxTreeNode)selPath.getLastPathComponent();
        n.toggle_selected(model);
            
        
    }
    void myDoubleClick(int selRow, TreePath selPath)
    {
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        BT_NEXT = new GlossButton();
        BT_BACK = new GlossButton();
        BT_ABORT = new GlossButton();
        TP_PANE = new javax.swing.JTabbedPane();
        PN_SELECT = new javax.swing.JPanel();
        RD_FIREFOX = new javax.swing.JRadioButton();
        RD_OUTLOOK = new javax.swing.JRadioButton();
        RD_FREE = new javax.swing.JRadioButton();
        PN_THUNDERBIRD = new javax.swing.JPanel();
        LB_PATH = new javax.swing.JLabel();
        TXT_PATH = new javax.swing.JTextField();
        BT_SET_PATH = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        JT_DIR = new javax.swing.JTree();
        jLabel1 = new javax.swing.JLabel();
        PN_CONFIRM = new javax.swing.JPanel();
        PN_STATUS = new javax.swing.JPanel();

        BT_NEXT.setText(UserMain.getString("Next")); // NOI18N
        BT_NEXT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_NEXTActionPerformed(evt);
            }
        });

        BT_BACK.setText(UserMain.getString("Back")); // NOI18N
        BT_BACK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_BACKActionPerformed(evt);
            }
        });

        BT_ABORT.setText(UserMain.getString("Abort")); // NOI18N
        BT_ABORT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ABORTActionPerformed(evt);
            }
        });

        RD_FIREFOX.setText("jRadioButton1");
        RD_FIREFOX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RD_FIREFOXActionPerformed(evt);
            }
        });

        RD_OUTLOOK.setText("jRadioButton2");

        RD_FREE.setText("jRadioButton3");

        javax.swing.GroupLayout PN_SELECTLayout = new javax.swing.GroupLayout(PN_SELECT);
        PN_SELECT.setLayout(PN_SELECTLayout);
        PN_SELECTLayout.setHorizontalGroup(
            PN_SELECTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_SELECTLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_SELECTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(RD_FIREFOX)
                    .addComponent(RD_OUTLOOK)
                    .addComponent(RD_FREE))
                .addContainerGap(231, Short.MAX_VALUE))
        );
        PN_SELECTLayout.setVerticalGroup(
            PN_SELECTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_SELECTLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(RD_FIREFOX)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(RD_OUTLOOK)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(RD_FREE)
                .addContainerGap(191, Short.MAX_VALUE))
        );

        TP_PANE.addTab("tab1", PN_SELECT);

        LB_PATH.setText("Folder");

        TXT_PATH.setText("jTextField1");

        BT_SET_PATH.setText("...");
        BT_SET_PATH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_SET_PATHActionPerformed(evt);
            }
        });

        jScrollPane1.setViewportView(JT_DIR);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 310, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel1.setText("Please select the Thunderbird-Profile folder");

        javax.swing.GroupLayout PN_THUNDERBIRDLayout = new javax.swing.GroupLayout(PN_THUNDERBIRD);
        PN_THUNDERBIRD.setLayout(PN_THUNDERBIRDLayout);
        PN_THUNDERBIRDLayout.setHorizontalGroup(
            PN_THUNDERBIRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_THUNDERBIRDLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_THUNDERBIRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addGroup(PN_THUNDERBIRDLayout.createSequentialGroup()
                        .addComponent(LB_PATH)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(TXT_PATH, javax.swing.GroupLayout.DEFAULT_SIZE, 213, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_SET_PATH)))
                .addContainerGap())
        );
        PN_THUNDERBIRDLayout.setVerticalGroup(
            PN_THUNDERBIRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_THUNDERBIRDLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(PN_THUNDERBIRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LB_PATH)
                    .addComponent(TXT_PATH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_SET_PATH))
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        TP_PANE.addTab("tab2", PN_THUNDERBIRD);

        javax.swing.GroupLayout PN_CONFIRMLayout = new javax.swing.GroupLayout(PN_CONFIRM);
        PN_CONFIRM.setLayout(PN_CONFIRMLayout);
        PN_CONFIRMLayout.setHorizontalGroup(
            PN_CONFIRMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 330, Short.MAX_VALUE)
        );
        PN_CONFIRMLayout.setVerticalGroup(
            PN_CONFIRMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 273, Short.MAX_VALUE)
        );

        TP_PANE.addTab("tab3", PN_CONFIRM);

        javax.swing.GroupLayout PN_STATUSLayout = new javax.swing.GroupLayout(PN_STATUS);
        PN_STATUS.setLayout(PN_STATUSLayout);
        PN_STATUSLayout.setHorizontalGroup(
            PN_STATUSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 330, Short.MAX_VALUE)
        );
        PN_STATUSLayout.setVerticalGroup(
            PN_STATUSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 273, Short.MAX_VALUE)
        );

        TP_PANE.addTab("tab4", PN_STATUS);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_BACK, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_NEXT, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(TP_PANE, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ABORT, BT_BACK, BT_NEXT});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TP_PANE, javax.swing.GroupLayout.DEFAULT_SIZE, 298, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_NEXT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_BACK, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        TP_PANE.getAccessibleContext().setAccessibleName(UserMain.getString("Type")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void RD_FIREFOXActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RD_FIREFOXActionPerformed
    {//GEN-HEADEREND:event_RD_FIREFOXActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RD_FIREFOXActionPerformed

    private void BT_SET_PATHActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_SET_PATHActionPerformed
    {//GEN-HEADEREND:event_BT_SET_PATHActionPerformed
        // TODO add your handling code here:
        
    }//GEN-LAST:event_BT_SET_PATHActionPerformed

    private void BT_NEXTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_NEXTActionPerformed
    {//GEN-HEADEREND:event_BT_NEXTActionPerformed
        // TODO add your handling code here:
        set_next_state();

    }//GEN-LAST:event_BT_NEXTActionPerformed

    private void BT_BACKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_BACKActionPerformed
    {//GEN-HEADEREND:event_BT_BACKActionPerformed
        // TODO add your handling code here:
        set_prev_state();
    }//GEN-LAST:event_BT_BACKActionPerformed

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ABORTActionPerformed
    {//GEN-HEADEREND:event_BT_ABORTActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_BT_ABORTActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_BACK;
    private javax.swing.JButton BT_NEXT;
    private javax.swing.JButton BT_SET_PATH;
    private javax.swing.JTree JT_DIR;
    private javax.swing.JLabel LB_PATH;
    private javax.swing.JPanel PN_CONFIRM;
    private javax.swing.JPanel PN_SELECT;
    private javax.swing.JPanel PN_STATUS;
    private javax.swing.JPanel PN_THUNDERBIRD;
    private javax.swing.JRadioButton RD_FIREFOX;
    private javax.swing.JRadioButton RD_FREE;
    private javax.swing.JRadioButton RD_OUTLOOK;
    private javax.swing.JTabbedPane TP_PANE;
    private javax.swing.JTextField TXT_PATH;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public void mouseClicked( MouseEvent e )
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mousePressed( MouseEvent e )
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseReleased( MouseEvent e )
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseEntered( MouseEvent e )
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void mouseExited( MouseEvent e )
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public JButton get_default_button()
    {
        return BT_NEXT;
    }

}
