/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MailViewPanel.java
 *
 * Created on 15.07.2009, 16:14:22
 */

package dimm.home.Panels.MailImport;

import dimm.home.Main;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.UserMain;
import dimm.home.Utilities.SizeStr;
import java.awt.FileDialog;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;




/**
 *
 * @author mw
 */
public class PanelImportMailbox extends GlossDialogPanel implements MouseListener
{

    boolean is_in_fill_cb;
    ProfileManager manager;


    private void set_next_state()
    {
        switch(state)
        {
            case IMP_SEL_TYPE:
            {
                TP_PANE.setEnabledAt(0, false);
                TP_PANE.setEnabledAt(1, true);
                TP_PANE.setSelectedIndex(1);

                state = IMPORT_STATE.IMP_SEL_MBOXES;
                BT_BACK.setVisible(true);

                if (RD_FIREFOX.isSelected())
                {
                    manager = new TBirdProfileManager();
                }
                if (RD_OUTLOOKXPRESS.isSelected())
                {
                    manager = new OlexpProfileManager();
                }

                if (manager != null)
                {
                    try
                    {
                        is_in_fill_cb = true;
                        manager.fill_profile_combo(CB_PROFILE);
                        CB_PROFILE.setSelectedIndex(0);
                        is_in_fill_cb = false;
                        build_tree_callback();
                    }
                    catch (IOException ex)
                    {
                        Logger.getLogger(PanelImportMailbox.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                break;
            }
            case IMP_SEL_MBOXES:
            {
                TP_PANE.setEnabledAt(1, false);
                TP_PANE.setEnabledAt(2, true);
                TP_PANE.setSelectedIndex(2);

                handle_check();
                
                state = IMPORT_STATE.IMP_CONFIRM;
                break;
            }
            case IMP_CONFIRM:
            {
                TP_PANE.setEnabledAt(2, false);
                TP_PANE.setEnabledAt(3, true);
                TP_PANE.setSelectedIndex(3);
                
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

                state = IMPORT_STATE.IMP_SEL_TYPE;
                BT_BACK.setVisible(false);

                break;
            }
            case IMP_CONFIRM:
            {
                TP_PANE.setEnabledAt(2, false);
                TP_PANE.setEnabledAt(1, true);
                TP_PANE.setSelectedIndex(1);

                state = IMPORT_STATE.IMP_SEL_MBOXES;
            }
        }
    }

    void set_enable_next( boolean b )
    {
        BT_NEXT.setVisible(b);
    }
    void handle_build_outlook_express_tree()
    {
    }
    void build_tree_callback()
    {
        if ( is_in_fill_cb)
            return;

        if (manager == null)
            return;

            String path = null;
            NamePathEntry npe = null;
            if (CB_PROFILE.getSelectedItem() != null)
            {
                npe = (NamePathEntry)CB_PROFILE.getSelectedItem();
            }
            if (npe == null)
            {
                path = TXT_PATH.getText();
            }

            if (npe == null && (path == null || path.length() == 0))
            {
                set_enable_next( false );
                return;
            }
            

            try
            {
                if (npe != null)
                    manager.handle_build_tree(npe, JT_DIR);
                else if (path != null)
                    manager.handle_build_tree(path, JT_DIR);
            }
            catch (IOException ex)
            {
                UserMain.errm_ok(my_dlg, UserMain.Txt("Cannot_build_mail_tree"));
                set_enable_next( false );
                return;
            }
            set_enable_next( true );
    }

    private void handle_import()
    {
        UserMain.errm_ok("Importing...");
    }

    private void handle_check()
    {
        UserMain.errm_ok("Checking...");
        DefaultTreeModel tm = (DefaultTreeModel)JT_DIR.getModel();
        MutableTreeNode root = (MutableTreeNode)tm.getRoot();

        ArrayList<TreeNode> list = new ArrayList<TreeNode>();

        build_tn_array(root,list);

        if (list.size() == 0)
        {
            TXTP_CONFIRM.setText(UserMain.Txt("You_have_not_selected_any_data_to_import"));
            return;
        }
        long file_size = 0;

        for (int i = 0; i < list.size(); i++)
        {
            TreeNode c = list.get(i);
            if (c instanceof TBirdTreeNode)
            {
                TBirdTreeNode tbn = (TBirdTreeNode)c;
                File mbox = tbn.get_mailbox();
                if (mbox != null)
                    file_size += mbox.length();
            }
            if (c instanceof OlexpFileNode)
            {
                OlexpFileNode tbn = (OlexpFileNode)c;
                File mbox = tbn.node;
                if (mbox != null)
                    file_size += mbox.length();
            }
        }

        String size_str = new SizeStr( file_size).toString();

        String txt = UserMain.Txt("You_have_selected") + " " + list.size() + " " + UserMain.Txt("Mailboxes");

        txt += "\n\n";
        txt += UserMain.Txt("A_total_of") + " " + size_str + " " + UserMain.Txt("will_be_imported");
        TXTP_CONFIRM.setText(txt);
    }
    void build_tn_array( TreeNode tn, ArrayList<TreeNode> list)
    {
        int cnt = tn.getChildCount();
        for (int i = 0; i < cnt; i++)
        {
            TreeNode c = tn.getChildAt(i);
            if (c instanceof TBirdTreeNode)
            {
                TBirdTreeNode tbn = (TBirdTreeNode)c;
                if (!tbn.is_selected)
                    continue;

                list.add(c);
            }
            if (c instanceof OlexpFileNode)
            {
                OlexpFileNode ofn = (OlexpFileNode)c;
                if (!ofn.is_selected())
                    continue;

                list.add(c);
            }
            if (c.getChildCount() > 0)
            {
                build_tn_array( c, list );
            }
        }
    }

    static File last_dir;
    private void open_mail_path_select()
    {
        FileDialog fd = new FileDialog(my_dlg, UserMain.Txt("Dateiauswahl"), FileDialog.LOAD );
        if (last_dir != null)
            fd.setDirectory( last_dir.getAbsolutePath() );

        fd.setVisible(true);

        if (fd.getFile() == null)
        {
            //TXT_PATH.setText("");
            return;
        }

        File f = new File(fd.getDirectory() + "/" + fd.getFile());
        last_dir = f.getParentFile();

        TXT_PATH.setText(f.getAbsolutePath());

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

        
        JT_DIR.setForeground(Main.ui.get_nice_white() );
        JT_DIR.setBackground(Main.ui.get_appl_dgray() );

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
        SwitchableNode n = (SwitchableNode)selPath.getLastPathComponent();

        boolean s = !n.is_selected();
        n.set_selected( (DefaultTreeModel)JT_DIR.getModel(), s );
            
        
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
        RD_OUTLOOKXPRESS = new javax.swing.JRadioButton();
        RD_FREE = new javax.swing.JRadioButton();
        PN_THUNDERBIRD = new javax.swing.JPanel();
        LB_PATH = new javax.swing.JLabel();
        TXT_PATH = new javax.swing.JTextField();
        BT_SET_PATH = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        JT_DIR = new javax.swing.JTree();
        jLabel1 = new javax.swing.JLabel();
        CB_PROFILE = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        PN_CONFIRM = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TXTP_CONFIRM = new javax.swing.JTextPane();
        PN_STATUS = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();

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

        RD_FIREFOX.setText(UserMain.getString("Thunderbird")); // NOI18N
        RD_FIREFOX.setActionCommand(UserMain.getString("Thunderbird")); // NOI18N
        RD_FIREFOX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RD_FIREFOXActionPerformed(evt);
            }
        });

        RD_OUTLOOKXPRESS.setText(UserMain.getString("Outlook_Express")); // NOI18N

        RD_FREE.setText(UserMain.getString("EML_Files")); // NOI18N

        javax.swing.GroupLayout PN_SELECTLayout = new javax.swing.GroupLayout(PN_SELECT);
        PN_SELECT.setLayout(PN_SELECTLayout);
        PN_SELECTLayout.setHorizontalGroup(
            PN_SELECTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_SELECTLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_SELECTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(RD_FIREFOX)
                    .addComponent(RD_OUTLOOKXPRESS)
                    .addComponent(RD_FREE))
                .addContainerGap(230, Short.MAX_VALUE))
        );
        PN_SELECTLayout.setVerticalGroup(
            PN_SELECTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_SELECTLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(RD_FIREFOX)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(RD_OUTLOOKXPRESS)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(RD_FREE)
                .addContainerGap(231, Short.MAX_VALUE))
        );

        TP_PANE.addTab("tab1", PN_SELECT);

        LB_PATH.setText("Folder");

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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 194, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel1.setText("Please select the Thunderbird-Profile folder");

        CB_PROFILE.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        CB_PROFILE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_PROFILEActionPerformed(evt);
            }
        });

        jLabel2.setText(UserMain.getString("Profile")); // NOI18N

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
                        .addGroup(PN_THUNDERBIRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LB_PATH)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PN_THUNDERBIRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CB_PROFILE, 0, 285, Short.MAX_VALUE)
                            .addGroup(PN_THUNDERBIRDLayout.createSequentialGroup()
                                .addComponent(TXT_PATH, javax.swing.GroupLayout.DEFAULT_SIZE, 228, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(BT_SET_PATH)))))
                .addContainerGap())
        );
        PN_THUNDERBIRDLayout.setVerticalGroup(
            PN_THUNDERBIRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_THUNDERBIRDLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PN_THUNDERBIRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CB_PROFILE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addGap(15, 15, 15)
                .addGroup(PN_THUNDERBIRDLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(LB_PATH)
                    .addComponent(TXT_PATH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_SET_PATH))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        TP_PANE.addTab("tab2", PN_THUNDERBIRD);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(UserMain.getString("Summary"))); // NOI18N

        TXTP_CONFIRM.setEditable(false);
        jScrollPane2.setViewportView(TXTP_CONFIRM);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 287, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout PN_CONFIRMLayout = new javax.swing.GroupLayout(PN_CONFIRM);
        PN_CONFIRM.setLayout(PN_CONFIRMLayout);
        PN_CONFIRMLayout.setHorizontalGroup(
            PN_CONFIRMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_CONFIRMLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        PN_CONFIRMLayout.setVerticalGroup(
            PN_CONFIRMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_CONFIRMLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        TP_PANE.addTab("tab3", PN_CONFIRM);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(UserMain.getString("Statistics"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 307, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 265, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout PN_STATUSLayout = new javax.swing.GroupLayout(PN_STATUS);
        PN_STATUS.setLayout(PN_STATUSLayout);
        PN_STATUSLayout.setHorizontalGroup(
            PN_STATUSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_STATUSLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        PN_STATUSLayout.setVerticalGroup(
            PN_STATUSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_STATUSLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
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
                    .addComponent(TP_PANE, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ABORT, BT_BACK, BT_NEXT});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TP_PANE, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
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
        open_mail_path_select();
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

    private void CB_PROFILEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_PROFILEActionPerformed
    {//GEN-HEADEREND:event_CB_PROFILEActionPerformed
        // TODO add your handling code here:
        build_tree_callback();
        if (CB_PROFILE.getSelectedItem() != null)
        {
            NamePathEntry nbpe = (NamePathEntry)CB_PROFILE.getSelectedItem();
            if (nbpe.path == null)
            {
                TXT_PATH.setEditable(true);
                BT_SET_PATH.setEnabled(false);
            }
            else
            {
                TXT_PATH.setEditable(false);
                BT_SET_PATH.setEnabled(false);
            }
        }

    }//GEN-LAST:event_CB_PROFILEActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_BACK;
    private javax.swing.JButton BT_NEXT;
    private javax.swing.JButton BT_SET_PATH;
    private javax.swing.JComboBox CB_PROFILE;
    private javax.swing.JTree JT_DIR;
    private javax.swing.JLabel LB_PATH;
    private javax.swing.JPanel PN_CONFIRM;
    private javax.swing.JPanel PN_SELECT;
    private javax.swing.JPanel PN_STATUS;
    private javax.swing.JPanel PN_THUNDERBIRD;
    private javax.swing.JRadioButton RD_FIREFOX;
    private javax.swing.JRadioButton RD_FREE;
    private javax.swing.JRadioButton RD_OUTLOOKXPRESS;
    private javax.swing.JTabbedPane TP_PANE;
    private javax.swing.JTextPane TXTP_CONFIRM;
    private javax.swing.JTextField TXT_PATH;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
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
