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
import dimm.home.Utilities.SizeStr;
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
import javax.swing.JFileChooser;
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

    boolean is_in_fill_cb;
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
                if (RD_OUTLOOK.isSelected())
                {
                    manager = new OutlookProfileManager();
                }
                if (RD_EML.isSelected())
                {
                    manager = new EMLProfileManager();
                }

                if (manager != null)
                {
                    try
                    {
                        is_in_fill_cb = true;
                        manager.fill_profile_combo(CB_PROFILE);
                        is_in_fill_cb = false;
                        CB_PROFILE.setSelectedIndex(0);
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

    boolean is_manual_path()
    {
        return TXT_PATH.isEditable();
    }
   

    void build_tree_callback()
    {
        if (is_in_fill_cb)
            return;

        if (manager == null)
            return;

        String path = null;
        NamePathEntry npe = null;
        if (CB_PROFILE.getSelectedItem() != null)
        {
            npe = (NamePathEntry) CB_PROFILE.getSelectedItem();
        }
        if (npe == null)
        {
            path = TXT_PATH.getText();
        }

        if (npe == null && (path == null || path.length() == 0))
        {
            set_enable_next(false);
            return;
        }


        try
        {
            if (npe != null)
            {
                manager.handle_build_tree(npe, JT_DIR);
            }
            else if (path != null)
            {
                manager.handle_build_tree(path, JT_DIR);
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            UserMain.errm_ok(my_dlg, UserMain.Txt("Cannot_build_mail_tree") + ": " + ex.getMessage());
            set_enable_next(false);
            return;
        }
        set_enable_next(true);
    }
    SwingWorker sw_import;
    ArrayList<String> import_status_list;
    int import_err_code;
    boolean abort_import;
    boolean finished = false;

    private File get_file_from_node( SwitchableNode node )
    {
        File mbox = null;

        // GET FILE FROM NODE
        if (node instanceof TBirdTreeNode)
        {
            TBirdTreeNode tbn = (TBirdTreeNode) node;
            mbox = tbn.get_mailbox();
        }
        if (node instanceof FileNode)
        {
            FileNode tbn = (FileNode) node;
            mbox = tbn.node;
        }
        
        return mbox;
    }

    int run_import( final ArrayList<SwitchableNode> node_list )
    {
        int files_uploaded = 0;
        try
        {
            FileInputStream fis;
            files_uploaded = 0;

            import_status_list.add(UserMain.Txt("Connecting_server"));
            long total_size = 0;
            long act_size = 0;

            for (int i = 0; i < node_list.size(); i++)
            {
                File mbox = get_file_from_node(node_list.get(i));
                if (mbox == null)
                {
                    continue;
                }
                total_size += mbox.length();
            }


            for (int i = 0; i < node_list.size(); i++)
            {
                // DETECT USER ABORT
                if (abort_import)
                {
                    import_status_list.add(UserMain.Txt("Aborted_import"));
                    break;
                }

                // GET FILE FROM NODE
                File mbox = get_file_from_node(node_list.get(i));
                if (mbox == null)
                {
                    continue;
                }

                // GET MANMDANT / DISKARCHIVE / SIZE
                long file_len = mbox.length();
                int mandant_id = UserMain.sqc().get_act_mandant_id();

                // RETRIEVE DA DROM COMBO
                DiskArchiveComboModel dacm = (DiskArchiveComboModel) CB_VAULT.getModel();
                DiskArchive da = dacm.get_selected_da();
                int da_id = da.getId();

                // SEND UPLOAD REQUEST
                // IMPORT MAIL RETURNS A HANDLE FOR AN OPEN STREAM
                String ret = UserMain.fcc().get_sqc().send("upload_mail_file MA:" + mandant_id + " TY:" + manager.get_type() + " SI:" + file_len);

                // CHECK FOR ERROR
                int idx = ret.indexOf(':');
                import_err_code = Integer.parseInt(ret.substring(0, idx));
                if (import_err_code != 0)
                {
                    import_status_list.add(UserMain.Txt("Transfer_failed") + ": " + ret.substring(idx + 2));
                    continue;
                }

                String transfer_txt = UserMain.Txt("Transfering") + " " + mbox.getName() + " " + new SizeStr(file_len).toString();
                import_status_list.add(transfer_txt);

                // GET STREAM HANDLE FROM ANSWER
                OutStreamID oid = new OutStreamID(ret.substring(idx + 2));

                // OPEN I-STREAM
                try
                {
                    fis = new FileInputStream(mbox);
                }
                catch (FileNotFoundException fileNotFoundException)
                {
                    import_status_list.add(UserMain.Txt("Cannot_open_Mailbox") + " " + mbox.getAbsolutePath());
                    continue;
                }

                // SEND FILE TO SERVER
                boolean bret = UserMain.fcc().get_sqc().write_out_stream(oid, file_len, fis);
                try
                {
                    fis.close();
                }
                catch (IOException iOException)
                {
                }

                // SEND FAILED ?
                if (!bret)
                {
                    import_status_list.add(UserMain.Txt("Transfer_failed"));
                    UserMain.fcc().get_sqc().close_delete_out_stream(oid);
                    continue;
                }

                if (file_len > 1024 * 1024)
                {
                    // REPLACE LAST ENTRY WITH STATUSTEXT WITH RATIO
                    long duration_ms = UserMain.fcc().get_sqc().get_last_duration();
                    transfer_txt += " " + new SizeStr((file_len * 1000.0f) / duration_ms).toString() + "/s";
                    import_status_list.set(import_status_list.size() - 1, transfer_txt);
                }

                act_size += file_len;
                double percent = act_size * 100.0 / total_size;
                UserMain.self.show_busy_val(percent);


                // NOW BEGIN THE IMPORT IN BACKGROUND (BG:1) FOR THE OPEN STREAM
                ret = UserMain.fcc().get_sqc().send("import_mail_file OI:" + oid.getId() + " MA:" + mandant_id + " DA:" + da_id + " BG:1");
                if (ret != null && ret.charAt(0) == '0')
                {
                    import_status_list.add(UserMain.Txt("Import_started_for") + " " + mbox.getName());

                    // SET AS HANDLES IN TREE, ONLY THE ERROR FILES STAY SELECTED
                    SwitchableNode node = (SwitchableNode) node_list.get(i);
                    node.set_selected(false);
                    files_uploaded++;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            UserMain.errm_ok(my_dlg, UserMain.Txt("Unknown_error_during_import") + " "+ ex.getMessage());
        }
        finished = true;
        return files_uploaded;
    }

    private void handle_import( final ArrayList<SwitchableNode> node_list )
    {
        import_status_list = new ArrayList<String>();
        abort_import = false;
        finished = false;


        final String type = manager.get_type();
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
                int ret = run_import(node_list);
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

    private void handle_import()
    {
        DefaultTreeModel tm = (DefaultTreeModel) JT_DIR.getModel();
        MutableTreeNode root = (MutableTreeNode) tm.getRoot();

        ArrayList<SwitchableNode> node_list = new ArrayList<SwitchableNode>();
        ArrayList<File> file_list = new ArrayList<File>();

        build_tn_array(root, node_list);

        if (node_list.size() > 0)
        {
            handle_import(node_list);
        }
    }

    private void handle_check()
    {

        DefaultTreeModel tm = (DefaultTreeModel) JT_DIR.getModel();
        MutableTreeNode root = (MutableTreeNode) tm.getRoot();

        // BUILD DA COMBO
        SQLResult<DiskArchive> da_res = UserMain.sqc().get_da_result();
        DiskArchiveComboModel dacm = new DiskArchiveComboModel(da_res);
        CB_VAULT.setModel(dacm);


        ArrayList<SwitchableNode> list = new ArrayList<SwitchableNode>();

        build_tn_array(root, list);

        if (list.size() == 0)
        {
            TXTP_CONFIRM.setText(UserMain.Txt("You_have_not_selected_any_data_to_import"));
            return;
        }
        long file_size = 0;

        for (int i = 0; i < list.size(); i++)
        {
            SwitchableNode c = list.get(i);
            if (c instanceof TBirdTreeNode)
            {
                TBirdTreeNode tbn = (TBirdTreeNode) c;
                File mbox = tbn.get_mailbox();
                if (mbox != null)
                {
                    file_size += mbox.length();
                }
            }
            if (c instanceof FileNode)
            {
                FileNode tbn = (FileNode) c;
                File mbox = tbn.node;
                if (mbox != null)
                {
                    file_size += mbox.length();
                }
            }
        }

        String size_str = new SizeStr(file_size).toString();

        String txt = UserMain.Txt("You_have_selected") + " " + list.size() + " " + UserMain.Txt("Mailboxes");

        txt += "\n\n";
        txt += UserMain.Txt("A_total_of") + " " + size_str + " " + UserMain.Txt("will_be_imported");
        TXTP_CONFIRM.setText(txt);
    }

    void build_tn_array( TreeNode tn, ArrayList<SwitchableNode> list )
    {
        int cnt = tn.getChildCount();
        for (int i = 0; i < cnt; i++)
        {
            TreeNode c = tn.getChildAt(i);
            SwitchableNode snc = (SwitchableNode)tn.getChildAt(i);

            if (c.getChildCount() > 0)
            {
                build_tn_array(c, list);
            }
            
            // NO DIRECTORIES
            if (!snc.contains_data())
                continue;
            
            if (!snc.is_selected())
                continue;

            list.add(snc);

        }
    }
    static File last_dir;

    private void open_mail_path_select()
    {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setCurrentDirectory(last_dir);
        if (JFileChooser.APPROVE_OPTION==chooser.showDialog(my_dlg, "Select"))
          {
               File dir = chooser.getSelectedFile();
               last_dir = dir;
               TXT_PATH.setText(dir.getAbsolutePath());
               
          }
        
        try
        {
            manager.handle_build_tree(TXT_PATH.getText(), JT_DIR);
        }
        catch (IOException iOException)
        {
        }
/*-
        FileDialog fd = new FileDialog(my_dlg, UserMain.Txt("Dateiauswahl"), FileDialog.LOAD);
        if (last_dir != null)
        {
            fd.setDirectory(last_dir.getAbsolutePath());
        }

        fd.setVisible(true);

        if (fd.getFile() == null)
        {
            //TXT_PATH.setText("");
            return;
        }

        File f = new File(fd.getDirectory() + "/" + fd.getFile());
        last_dir = f.getParentFile();

        TXT_PATH.setText(f.getAbsolutePath());
 * */

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


        JT_DIR.setForeground(Main.ui.get_nice_white());
        JT_DIR.setBackground(Main.ui.get_appl_dgray());

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
                    .addComponent(RD_EML))
                .addContainerGap(340, Short.MAX_VALUE))
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
                .addContainerGap(269, Short.MAX_VALUE))
        );

        TP_PANE.addTab(UserMain.getString("Source_type"), PN_SELECT); // NOI18N

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
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 258, Short.MAX_VALUE)
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
                            .addComponent(CB_PROFILE, 0, 395, Short.MAX_VALUE)
                            .addGroup(PN_THUNDERBIRDLayout.createSequentialGroup()
                                .addComponent(TXT_PATH, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
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

        TP_PANE.addTab(UserMain.getString("Select_mailboxes"), PN_THUNDERBIRD); // NOI18N

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
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 278, Short.MAX_VALUE)
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
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
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
                        .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_BACK, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_NEXT, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(TP_PANE, javax.swing.GroupLayout.DEFAULT_SIZE, 454, Short.MAX_VALUE))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ABORT, BT_BACK, BT_NEXT});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TP_PANE, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
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
        if (sw_import != null)
        {
            import_status_list.add(UserMain.Txt("Aborted_import"));
            abort_import = true;
        }

        this.setVisible(false);
    }//GEN-LAST:event_BT_ABORTActionPerformed

    private void CB_PROFILEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_PROFILEActionPerformed
    {//GEN-HEADEREND:event_CB_PROFILEActionPerformed
        // TODO add your handling code here:
        build_tree_callback();
        if (CB_PROFILE.getSelectedItem() != null)
        {
            NamePathEntry nbpe = (NamePathEntry) CB_PROFILE.getSelectedItem();
            if (nbpe.path == null)
            {
                TXT_PATH.setEditable(true);
                BT_SET_PATH.setEnabled(true);
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
    private javax.swing.JComboBox CB_VAULT;
    private javax.swing.JTree JT_DIR;
    private javax.swing.JLabel LB_PATH;
    private javax.swing.JPanel PN_CONFIRM;
    private javax.swing.JPanel PN_SELECT;
    private javax.swing.JPanel PN_STATUS;
    private javax.swing.JPanel PN_THUNDERBIRD;
    private javax.swing.JRadioButton RD_EML;
    private javax.swing.JRadioButton RD_FIREFOX;
    private javax.swing.JRadioButton RD_OUTLOOK;
    private javax.swing.JRadioButton RD_OUTLOOKXPRESS;
    private javax.swing.JTabbedPane TP_PANE;
    private javax.swing.JTextArea TXTA_STATUS;
    private javax.swing.JTextPane TXTP_CONFIRM;
    private javax.swing.JTextField TXT_PATH;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
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
