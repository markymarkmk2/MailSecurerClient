/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * TBirdOptsPanel.java
 *
 * Created on 21.10.2010, 17:47:10
 */

package dimm.home.Panels.MailImport;

import com.microsoft.schemas.exchange.services._2006.types.DistinguishedFolderIdNameType;
import dimm.home.Models.AccountConnectorComboModel;
import dimm.home.Panels.UserListPanel;
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.MultiCheckboxPanel;
import dimm.home.ServerConnect.ServerCall;
import dimm.home.UserMain;
import dimm.home.Utilities.SwingWorker;
import home.shared.SQL.SQLResult;
import home.shared.Utilities.ParseToken;
import home.shared.hibernate.AccountConnector;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;



/**
 *
 * @author mw
 */
public class MultiExchangeOptsPanel extends javax.swing.JPanel
{
    PanelImportMailbox panel;

    AccountConnectorComboModel accm;

    ArrayList<String> sel_user_list;

    MultiCheckboxPanel mailboxes;

    ArrayList<DistinguishedFolderIdNameType> folders;
    boolean[] folder_checked_val_array;


    /** Creates new form TBirdOptsPanel */
    public MultiExchangeOptsPanel(PanelImportMailbox _panel)
    {
        panel = _panel;
        initComponents();

        File f = new File("excdfltscan.txt");
        System.out.println(f.getAbsolutePath());
        if (f.exists())
        {
            try
            {
                FileReader fr = new FileReader(f);
                char[] buff = new char[8192];
                int len = fr.read(buff);
                fr.close();
                String str = new String(buff, 0, len);
                ParseToken pt = new ParseToken(str);
                TXT_DOMAIN.setText(pt.GetString("DO:"));
            }
            catch (IOException iOException)
            {
            }
        }
        SQLResult<AccountConnector> da_res = UserMain.sqc().get_account_result();

        // COMBO-MODEL
        accm = new AccountConnectorComboModel(da_res );
        CB_ACCOUNT.setModel(accm);


        fill_defined_folder_list();

//        BT_ALL_BOXES.setSelected(true);
        BT_ALL_USERS.setSelected(true);
    }

    final void fill_defined_folder_list()
    {/*
    @XmlEnumValue("calendar")
    CALENDAR("calendar"),
    @XmlEnumValue("contacts")
    CONTACTS("contacts"),
    @XmlEnumValue("deleteditems")
    DELETEDITEMS("deleteditems"),
    @XmlEnumValue("drafts")
    DRAFTS("drafts"),
    @XmlEnumValue("inbox")
    INBOX("inbox"),
    @XmlEnumValue("journal")
    JOURNAL("journal"),
    @XmlEnumValue("notes")
    NOTES("notes"),
    @XmlEnumValue("outbox")
    OUTBOX("outbox"),
    @XmlEnumValue("sentitems")
    SENTITEMS("sentitems"),
    @XmlEnumValue("tasks")
    TASKS("tasks"),
    @XmlEnumValue("msgfolderroot")
    MSGFOLDERROOT("msgfolderroot"),
    @XmlEnumValue("publicfoldersroot")
    PUBLICFOLDERSROOT("publicfoldersroot"),
    @XmlEnumValue("root")
    ROOT("root"),
    @XmlEnumValue("junkemail")
    JUNKEMAIL("junkemail"),
    @XmlEnumValue("searchfolders")
    SEARCHFOLDERS("searchfolders"),
    @XmlEnumValue("voicemail")
    VOICEMAIL("voicemail");
    */
       folders = new ArrayList<DistinguishedFolderIdNameType>();
       folders.add(DistinguishedFolderIdNameType.INBOX );
       folders.add(DistinguishedFolderIdNameType.SENTITEMS );
       folders.add(DistinguishedFolderIdNameType.DRAFTS );
       folders.add(DistinguishedFolderIdNameType.OUTBOX );
       folders.add(DistinguishedFolderIdNameType.VOICEMAIL );
       folders.add(DistinguishedFolderIdNameType.PUBLICFOLDERSROOT );

       folder_checked_val_array = new boolean[folders.size() + 1];
       String[] name_array = new String[folders.size() + 1];

       folder_checked_val_array[0] = true;
       name_array[0] = UserMain.Txt("User_defined_Folders/Subfolders");


       for (int i = 0; i < folders.size(); i++)
       {
            DistinguishedFolderIdNameType dnt = folders.get(i);
            name_array[i + 1] = dnt.toString();
            folder_checked_val_array[i + 1] = true;
       }

        MultiCheckboxPanel pnl = new MultiCheckboxPanel(name_array, folder_checked_val_array, UserMain.Txt("Mailboxes") );
        pnl.show_buttons(false);
        PN_MBOX_LIST.add(pnl);


    }

    int get_account_id()
    {
        return accm.get_act_id();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        TXT_DOMAIN = new javax.swing.JTextField();
        LB_PATH1 = new javax.swing.JLabel();
        LB_PATH3 = new javax.swing.JLabel();
        CB_VERSION = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        CB_ACCOUNT = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        PN_MBOX_LIST = new javax.swing.JPanel();
        BT_ALL_BOXES = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        BT_ALL_USERS = new javax.swing.JCheckBox();
        PN_USER_LIST = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TXTA_USER_LIST = new javax.swing.JTextArea();

        LB_PATH1.setText(UserMain.getString("Domain")); // NOI18N

        LB_PATH3.setText(UserMain.getString("Exchangeversion")); // NOI18N

        CB_VERSION.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText(UserMain.Txt("Realm")); // NOI18N

        CB_ACCOUNT.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setText(UserMain.Txt("Mailboxes")); // NOI18N

        PN_MBOX_LIST.setLayout(new javax.swing.BoxLayout(PN_MBOX_LIST, javax.swing.BoxLayout.LINE_AXIS));

        BT_ALL_BOXES.setText("all");
        BT_ALL_BOXES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ALL_BOXESActionPerformed(evt);
            }
        });

        jLabel4.setText(UserMain.Txt("Users")); // NOI18N

        BT_ALL_USERS.setText("all");
        BT_ALL_USERS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ALL_USERSActionPerformed(evt);
            }
        });

        TXTA_USER_LIST.setColumns(20);
        TXTA_USER_LIST.setEditable(false);
        TXTA_USER_LIST.setRows(1);
        TXTA_USER_LIST.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXTA_USER_LISTMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(TXTA_USER_LIST);

        javax.swing.GroupLayout PN_USER_LISTLayout = new javax.swing.GroupLayout(PN_USER_LIST);
        PN_USER_LIST.setLayout(PN_USER_LISTLayout);
        PN_USER_LISTLayout.setHorizontalGroup(
            PN_USER_LISTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
        );
        PN_USER_LISTLayout.setVerticalGroup(
            PN_USER_LISTLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(LB_PATH1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(38, 38, 38)
                        .addComponent(BT_ALL_USERS))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(18, 18, 18)
                        .addComponent(BT_ALL_BOXES))
                    .addComponent(jLabel3)
                    .addComponent(LB_PATH3))
                .addGap(37, 37, 37)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CB_VERSION, 0, 243, Short.MAX_VALUE)
                    .addComponent(CB_ACCOUNT, 0, 243, Short.MAX_VALUE)
                    .addComponent(PN_MBOX_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE)
                    .addComponent(PN_USER_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(TXT_DOMAIN, javax.swing.GroupLayout.DEFAULT_SIZE, 243, Short.MAX_VALUE))
                .addGap(93, 93, 93))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(CB_ACCOUNT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_DOMAIN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LB_PATH1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CB_VERSION, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LB_PATH3))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel4)
                        .addComponent(BT_ALL_USERS))
                    .addComponent(PN_USER_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel1)
                        .addComponent(BT_ALL_BOXES))
                    .addComponent(PN_MBOX_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, 102, Short.MAX_VALUE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void TXTA_USER_LISTMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXTA_USER_LISTMouseClicked
    {//GEN-HEADEREND:event_TXTA_USER_LISTMouseClicked
        // TODO add your handling code here:
        handle_user_list_clicked();
    }//GEN-LAST:event_TXTA_USER_LISTMouseClicked

    private void BT_ALL_BOXESActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ALL_BOXESActionPerformed
    {//GEN-HEADEREND:event_BT_ALL_BOXESActionPerformed
        // TODO add your handling code here:
        PN_MBOX_LIST.setVisible(!BT_ALL_BOXES.isSelected());
        panel.getDlg().pack();
            
    }//GEN-LAST:event_BT_ALL_BOXESActionPerformed

    private void BT_ALL_USERSActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ALL_USERSActionPerformed
    {//GEN-HEADEREND:event_BT_ALL_USERSActionPerformed
        // TODO add your handling code here:
        PN_USER_LIST.setVisible(!BT_ALL_USERS.isSelected());
        panel.getDlg().pack();

    }//GEN-LAST:event_BT_ALL_USERSActionPerformed


   

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JCheckBox BT_ALL_BOXES;
    javax.swing.JCheckBox BT_ALL_USERS;
    javax.swing.JComboBox CB_ACCOUNT;
    javax.swing.JComboBox CB_VERSION;
    javax.swing.JLabel LB_PATH1;
    javax.swing.JLabel LB_PATH3;
    javax.swing.JPanel PN_MBOX_LIST;
    javax.swing.JPanel PN_USER_LIST;
    javax.swing.JTextArea TXTA_USER_LIST;
    javax.swing.JTextField TXT_DOMAIN;
    javax.swing.JLabel jLabel1;
    javax.swing.JLabel jLabel3;
    javax.swing.JLabel jLabel4;
    javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

   
    boolean is_in_fill_cb;


    void build_tree_callback()
    {
        try
        {
            if (is_in_fill_cb)
            {
                return;
            }
            if (panel.manager == null)
            {
                return;
            }
            panel.manager.handle_build_tree(panel.get_tree());
            
            panel.set_enable_next(true);
            is_in_fill_cb = false;
        }
        catch (Exception ex)
        {
            UserMain.errm_ok(panel.getDlg(), UserMain.Txt("Cannot_build_exchange_tree") + ": " + ex.getMessage());
        }

    }

    SwingWorker sw;

    private void handle_user_list_clicked()
    {

        sel_user_list = new ArrayList<String>();

        final String cmd = "ListUsers CMD:native_users MA:" + UserMain.self.get_act_mandant().getId() + " AC:" + accm.get_act_id();

        if (sw != null)
        {
            return;
        }

        sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                UserMain.self.show_busy(panel.getDlg(), UserMain.Txt("Checking_userlist") + "...");
                String ret = UserMain.fcc().call_abstract_function(cmd, ServerCall.SHORT_CMD_TO);
                UserMain.self.hide_busy();

                if (ret != null && ret.charAt(0) == '0')
                {
                    Object o = ParseToken.DeCompressObject(ret.substring(3));
                    if (o instanceof ArrayList<?>)
                    {
                        ArrayList<String> list = (ArrayList<String>)o;

                        boolean[] checked_val_array = new boolean[list.size()];
                        for (int i = 0; i < list.size(); i++)
                        {
                            checked_val_array[i] = true;
                        }

                        MultiCheckboxPanel pnl = new MultiCheckboxPanel( list.toArray(new String[0]), checked_val_array, UserMain.Txt("Users") );

                        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
                        dlg.set_next_location(panel.getDlg());
                        dlg.setVisible(true);

                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < list.size(); i++)
                        {
                            if (!pnl.get_val_array()[i])
                                continue;

                            String string = list.get(i);
                            if (sb.length() > 0)
                                sb.append("\n");
                            sb.append(string);
                            sel_user_list.add(string);

                        }
                        TXTA_USER_LIST.setText( sb.toString() );
                    }
                }
                else
                {
                    UserMain.errm_ok(panel.getDlg(), UserMain.Txt("Check_user_failed") + ": " + ((ret != null) ?  ret.substring(3): ""));
                }
                sw = null;
                return null;
            }
        };

        sw.start();
    }

    boolean add_user_folders()
    {
        return folder_checked_val_array[0];
    }
    ArrayList<DistinguishedFolderIdNameType> get_selected_folder_id_list()
    {
        ArrayList<DistinguishedFolderIdNameType> list = new ArrayList<DistinguishedFolderIdNameType>();

        // INDEX 0 IS RESEVED FOR ALL USER FOLDERS
        for (int i = 1; i < folder_checked_val_array.length; i++)
        {
            boolean b = folder_checked_val_array[i];
            if (b)
                list.add(folders.get(i-1));

        }
        return list;
    }
}
