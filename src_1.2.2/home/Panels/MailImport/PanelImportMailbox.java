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

import home.shared.SQL.SQLResult;
import dimm.home.Main;
import dimm.home.Models.DiskArchiveComboModel;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.ServerConnect.OutStreamID;
import dimm.home.UserMain;
import home.shared.Utilities.SizeStr;
import dimm.home.Utilities.SwingWorker;
import home.shared.hibernate.DiskArchive;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.Timer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

/**
 *
 * @author mw
 */
public class PanelImportMailbox extends GlossDialogPanel implements MouseListener, ActionListener
{

    ProfileManager manager;
    Timer timer;

    private void set_next_state()
    {
        switch (state)
        {
            case IMP_SEL_TYPE:
            {
                TP_PANE.setEnabledAt(0, false);
                TP_PANE.setEnabledAt(1, true);
                TP_PANE.setSelectedIndex(1);

                state = IMPORT_STATE.IMP_SEL_DATA;
                BT_BACK.setVisible(true);

                if (RD_FIREFOX.isSelected())
                {
                    manager = new TBirdProfileManager(this);
                }
                if (RD_OUTLOOKXPRESS.isSelected())
                {
                    manager = new OlexpProfileManager(this);
                }
                if (RD_OUTLOOK.isSelected())
                {
                    manager = new OutlookProfileManager(this);
                }
                if (RD_EML.isSelected())
                {
                    manager = new EMLProfileManager(this);
                }
                if (RD_MBOX.isSelected())
                {
                    manager = new MBOXProfileManager(this);
                }
                if (RD_EXCHANGE.isSelected())
                {
                    manager = new ExchangeImportManager(this);
                }
                if (RD_EXCHANGE_MULTI.isSelected())
                {
                    manager = new ExchangeMultiImportManager(this);
                }

                if (manager != null)
                {
                   
                    try
                    {
                        manager.init_options_gui();                        
                    }
                    catch (IOException ex)
                    {
                        
                        UserMain.errm_ok(my_dlg, ex.getMessage());
                    }
                    finally
                    {
                       
                    }
                }

                break;
            }
            case IMP_SEL_DATA:
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
                DiskArchiveComboModel dacm = (DiskArchiveComboModel) CB_VAULT.getModel();
                DiskArchive da = dacm.get_selected_da();
                if (da == null)
                {
                    UserMain.errm_ok(my_dlg, UserMain.Txt("Please_select_a_storage"));
                    break;
                }


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
        switch (state)
        {
            case IMP_SEL_DATA:
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

                state = IMPORT_STATE.IMP_SEL_DATA;
            }
        }
    }

    void set_enable_next( boolean b )
    {
        BT_NEXT.setVisible(b);
    }

   

    SwingWorker sw_import;
    ArrayList<String> import_status_list;
    int import_err_code;
    boolean abort_import;
    boolean finished = false;



    private void handle_import( )
    {
        import_status_list = new ArrayList<String>();
        abort_import = false;
        finished = false;

        if (sw_import != null)
        {
            if (!sw_import.finished())
            {
                UserMain.errm_ok(UserMain.Txt("Import_is_active"));
                return;
            }
        }


        sw_import = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                BT_NEXT.setEnabled(false);
                int ret = manager.run_import(JT_DIR);
                BT_ABORT.setVisible(false);
                BT_NEXT.setEnabled(true);

                timer.stop();
                actionPerformed(null);
                return new Integer(ret);
            }
        };

        UserMain.self.show_busy(my_dlg, UserMain.Txt("Starting_import"), true);
        UserMain.self.show_busy_val(0.0);

        sw_import.start();
        timer.start();
    }

    

    private void handle_check()
    {

        // BUILD DA COMBO
        SQLResult<DiskArchive> da_res = UserMain.sqc().get_da_result();
        DiskArchiveComboModel dacm = new DiskArchiveComboModel(da_res);
        CB_VAULT.setModel(dacm);


        // IF WE CANNOT DETERMINE AMOUNT OF DATA, WE JUST LEAVE
        if (! manager.has_tree_select())
            return;


        ArrayList<SwitchableNode> list = manager.alloc_tree_node_array(JT_DIR);

        if (list.isEmpty() )
        {
            TXTP_CONFIRM.setText(UserMain.Txt("You_have_not_selected_any_data_to_import"));
            return;
        }
        long file_size = 0;

        for (int i = 0; i < list.size(); i++)
        {
            SwitchableNode c = list.get(i);
            file_size += c.get_size();
            
        }

        String size_str = new SizeStr(file_size).toString();

        String txt = UserMain.Txt("You_have_selected") + " " + list.size() + " " + UserMain.Txt("Mailboxes");

        txt += "\n\n";
        txt += UserMain.Txt("A_total_of") + " " + size_str + " " + UserMain.Txt("will_be_imported");
        TXTP_CONFIRM.setText(txt);
    }


    static String last_status_text = "";

    @Override
    public void actionPerformed( ActionEvent e )
    {
        if (finished)
        {            
            UserMain.self.hide_busy();
            sw_import = null;
            abort_import = false;
        }

        if (UserMain.self.is_busy_aborted())
        {
            if (abort_import == false)
            {
                import_status_list.add(UserMain.Txt("Aborted_import"));
                abort_import = true;
            }
        }
        if (abort_import && !UserMain.self.is_busy_visible())
        {
            UserMain.self.show_busy(my_dlg, UserMain.Txt("Aborting_please_wait"), true);
        }


        int idx = import_status_list.size() - 1;
        if (idx >= 0)
        {
            String last_txt = import_status_list.get(idx);
            if (last_txt.compareTo(last_status_text) != 0)
            {
                if (!finished)
                    UserMain.self.show_busy(my_dlg, last_txt, true);
                
                last_status_text = last_txt;

                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < import_status_list.size(); i++)
                {
                    sb.append( import_status_list.get(i) );
                    sb.append("\n");
                }
                TXTA_STATUS.setText(sb.toString());
                TXTA_STATUS.setCaretPosition( sb.length());
            }
        }
    }

    JTree get_tree()
    {
        return JT_DIR;
    }

    void set_opts_panel( JPanel pnl )
    {
        PN_OTIONS.removeAll();
        PN_OTIONS.add(pnl);
        this.getDlg().pack();
    }

    enum IMPORT_STATE
    {

        IMP_SEL_TYPE,
        IMP_SEL_DATA,
        IMP_CONFIRM,
        IMP_STATUS
    };
    IMPORT_STATE state;

    /** Creates new form MailViewPanel */
    public PanelImportMailbox()
    {
        initComponents();

        JT_DIR.setModel(new DefaultTreeModel(null));

        JT_DIR.setForeground(Main.ui.get_foreground());
        JT_DIR.setBackground(Main.ui.get_background());

        TP_PANE.setEnabledAt(0, true);
        TP_PANE.setEnabledAt(1, false);
        TP_PANE.setEnabledAt(2, false);
        TP_PANE.setEnabledAt(3, false);

        BT_BACK.setVisible(false);

        state = IMPORT_STATE.IMP_SEL_TYPE;

        last_status_text = "";

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
        timer = new Timer(1000, this);

        
    }

    void mySingleClick( int selRow, TreePath selPath )
    {
        SwitchableNode n = (SwitchableNode) selPath.getLastPathComponent();

        boolean s = !n.is_selected();
        n.set_selected(s);


    }

    void myDoubleClick( int selRow, TreePath selPath )
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
        RD_OUTLOOK = new javax.swing.JRadioButton();
        RD_EML = new javax.swing.JRadioButton();
        RD_MBOX = new javax.swing.JRadioButton();
        RD_EXCHANGE = new javax.swing.JRadioButton();
        RD_EXCHANGE_MULTI = new javax.swing.JRadioButton();
        PN_DATA_SELECT = new javax.swing.JPanel();
        PN_TREE = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        JT_DIR = new javax.swing.JTree();
        PN_OTIONS = new javax.swing.JPanel();
        PN_CONFIRM = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TXTP_CONFIRM = new javax.swing.JTextPane();
        jLabel3 = new javax.swing.JLabel();
        CB_VAULT = new javax.swing.JComboBox();
        PN_STATUS = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        TXTA_STATUS = new javax.swing.JTextArea();

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

        buttonGroup1.add(RD_FIREFOX);
        RD_FIREFOX.setText(UserMain.getString("Thunderbird")); // NOI18N
        RD_FIREFOX.setActionCommand(UserMain.getString("Thunderbird")); // NOI18N
        RD_FIREFOX.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RD_FIREFOXActionPerformed(evt);
            }
        });

        buttonGroup1.add(RD_OUTLOOKXPRESS);
        RD_OUTLOOKXPRESS.setText(UserMain.getString("Outlook_Express")); // NOI18N

        buttonGroup1.add(RD_OUTLOOK);
        RD_OUTLOOK.setText(UserMain.getString("Outlook")); // NOI18N

        buttonGroup1.add(RD_EML);
        RD_EML.setText(UserMain.getString("EML_Files")); // NOI18N

        buttonGroup1.add(RD_MBOX);
        RD_MBOX.setText(UserMain.getString("MBOX_Files")); // NOI18N

        buttonGroup1.add(RD_EXCHANGE);
        RD_EXCHANGE.setText(UserMain.Txt("ExchangeAccount")); // NOI18N

        buttonGroup1.add(RD_EXCHANGE_MULTI);
        RD_EXCHANGE_MULTI.setText(UserMain.Txt("ExchangeServer")); // NOI18N

        javax.swing.GroupLayout PN_SELECTLayout = new javax.swing.GroupLayout(PN_SELECT);
        PN_SELECT.setLayout(PN_SELECTLayout);
        PN_SELECTLayout.setHorizontalGroup(
            PN_SELECTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_SELECTLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_SELECTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(RD_FIREFOX)
                    .addComponent(RD_OUTLOOKXPRESS)
                    .addComponent(RD_OUTLOOK)
                    .addComponent(RD_EML)
                    .addComponent(RD_MBOX)
                    .addComponent(RD_EXCHANGE)
                    .addComponent(RD_EXCHANGE_MULTI))
                .addContainerGap(328, Short.MAX_VALUE))
        );
        PN_SELECTLayout.setVerticalGroup(
            PN_SELECTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_SELECTLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(RD_FIREFOX)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(RD_OUTLOOKXPRESS)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(RD_OUTLOOK)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(RD_EML)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(RD_MBOX)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(RD_EXCHANGE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(RD_EXCHANGE_MULTI)
                .addContainerGap(193, Short.MAX_VALUE))
        );

        TP_PANE.addTab(UserMain.getString("Source_type"), PN_SELECT); // NOI18N

        jScrollPane1.setViewportView(JT_DIR);

        javax.swing.GroupLayout PN_TREELayout = new javax.swing.GroupLayout(PN_TREE);
        PN_TREE.setLayout(PN_TREELayout);
        PN_TREELayout.setHorizontalGroup(
            PN_TREELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
        );
        PN_TREELayout.setVerticalGroup(
            PN_TREELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_TREELayout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE)
                .addContainerGap())
        );

        PN_OTIONS.setLayout(new javax.swing.BoxLayout(PN_OTIONS, javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout PN_DATA_SELECTLayout = new javax.swing.GroupLayout(PN_DATA_SELECT);
        PN_DATA_SELECT.setLayout(PN_DATA_SELECTLayout);
        PN_DATA_SELECTLayout.setHorizontalGroup(
            PN_DATA_SELECTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_DATA_SELECTLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PN_TREE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addComponent(PN_OTIONS, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
        );
        PN_DATA_SELECTLayout.setVerticalGroup(
            PN_DATA_SELECTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_DATA_SELECTLayout.createSequentialGroup()
                .addComponent(PN_OTIONS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PN_TREE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        TP_PANE.addTab(UserMain.getString("Select_mailboxes"), PN_DATA_SELECT); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(UserMain.getString("Summary"))); // NOI18N

        TXTP_CONFIRM.setEditable(false);
        jScrollPane2.setViewportView(TXTP_CONFIRM);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addContainerGap())
        );

        jLabel3.setText(UserMain.getString("Speicherziel")); // NOI18N

        CB_VAULT.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout PN_CONFIRMLayout = new javax.swing.GroupLayout(PN_CONFIRM);
        PN_CONFIRM.setLayout(PN_CONFIRMLayout);
        PN_CONFIRMLayout.setHorizontalGroup(
            PN_CONFIRMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_CONFIRMLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_CONFIRMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(PN_CONFIRMLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addGap(18, 18, 18)
                        .addComponent(CB_VAULT, 0, 373, Short.MAX_VALUE)))
                .addContainerGap())
        );
        PN_CONFIRMLayout.setVerticalGroup(
            PN_CONFIRMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_CONFIRMLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_CONFIRMLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(CB_VAULT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        TP_PANE.addTab(UserMain.getString("Confirm_selection"), PN_CONFIRM); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(UserMain.getString("Statistics"))); // NOI18N

        TXTA_STATUS.setColumns(20);
        TXTA_STATUS.setEditable(false);
        TXTA_STATUS.setRows(5);
        jScrollPane3.setViewportView(TXTA_STATUS);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 417, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 331, Short.MAX_VALUE)
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

        TP_PANE.addTab(UserMain.getString("Status"), PN_STATUS); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(BT_ABORT)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_BACK)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_NEXT))
                    .addComponent(TP_PANE, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ABORT, BT_BACK, BT_NEXT});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TP_PANE, javax.swing.GroupLayout.DEFAULT_SIZE, 404, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_NEXT)
                    .addComponent(BT_BACK)
                    .addComponent(BT_ABORT))
                .addContainerGap())
        );

        TP_PANE.getAccessibleContext().setAccessibleName(UserMain.getString("Type")); // NOI18N
    }// </editor-fold>//GEN-END:initComponents

    private void RD_FIREFOXActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RD_FIREFOXActionPerformed
    {//GEN-HEADEREND:event_RD_FIREFOXActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_RD_FIREFOXActionPerformed

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
        if (sw_import != null)
        {
            import_status_list.add(UserMain.Txt("Aborted_import"));
            abort_import = true;
        }

        this.setVisible(false);
    }//GEN-LAST:event_BT_ABORTActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton BT_ABORT;
    javax.swing.JButton BT_BACK;
    javax.swing.JButton BT_NEXT;
    javax.swing.JComboBox CB_VAULT;
    javax.swing.JTree JT_DIR;
    javax.swing.JPanel PN_CONFIRM;
    javax.swing.JPanel PN_DATA_SELECT;
    javax.swing.JPanel PN_OTIONS;
    javax.swing.JPanel PN_SELECT;
    javax.swing.JPanel PN_STATUS;
    javax.swing.JPanel PN_TREE;
    javax.swing.JRadioButton RD_EML;
    javax.swing.JRadioButton RD_EXCHANGE;
    javax.swing.JRadioButton RD_EXCHANGE_MULTI;
    javax.swing.JRadioButton RD_FIREFOX;
    javax.swing.JRadioButton RD_MBOX;
    javax.swing.JRadioButton RD_OUTLOOK;
    javax.swing.JRadioButton RD_OUTLOOKXPRESS;
    javax.swing.JTabbedPane TP_PANE;
    javax.swing.JTextArea TXTA_STATUS;
    javax.swing.JTextPane TXTP_CONFIRM;
    javax.swing.ButtonGroup buttonGroup1;
    javax.swing.JLabel jLabel3;
    javax.swing.JPanel jPanel1;
    javax.swing.JPanel jPanel2;
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JScrollPane jScrollPane2;
    javax.swing.JScrollPane jScrollPane3;
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
