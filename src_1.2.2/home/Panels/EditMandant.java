/*
 * EditChannelPanel.java
 *
 * Created on 25. M�rz 2008, 20:06
 */
package dimm.home.Panels;

import home.shared.SQL.SQLResult;
import dimm.home.Models.OverviewModel;
import dimm.home.Rendering.EditTextList;
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossTable;
import dimm.home.Rendering.SQLOverviewDialog;
import dimm.home.Rendering.SingleTextEditPanel;
import dimm.home.ServerConnect.ConnectionID;
import dimm.home.ServerConnect.ResultSetID;
import dimm.home.ServerConnect.ServerCall;
import dimm.home.ServerConnect.StatementID;
import dimm.home.UserMain;
import dimm.home.Utilities.CXStream;
import dimm.home.Utilities.SwingWorker;
import javax.swing.JButton;
import home.shared.hibernate.Mandant;
import home.shared.Utilities.Validator;
import home.shared.CS_Constants;
import home.shared.CS_Constants.USERMODE;
import home.shared.SQL.SQLArrayResult;
import home.shared.Utilities.CryptTools;
import home.shared.hibernate.MailHeaderVariable;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.table.TableColumnModel;

class HeaderModel extends OverviewModel
{

    ArrayList<MailHeaderVariable> mhv_list;

    HeaderModel( UserMain _main, SQLOverviewDialog _dlg )
    {
        super(_main, _dlg);

        String[] _col_names =
        {
            "Id", UserMain.getString("Name"), UserMain.getString("eMail"), UserMain.getString("Bearbeiten"), UserMain.getString("Löschen")
        };
        Class[] _col_classes =
        {
            String.class, String.class, Boolean.class,  JButton.class, JButton.class
        };
        set_columns(_col_names, _col_classes);

    }

    @Override
    public String get_qry( long station_id )
    {
        return "select id, var_name from mail_header_variable where mid=" + station_id;
    }

    @Override
    public Object getValueAt( int rowIndex, int columnIndex )
    {
        if (sqlResult == null)
        {
            return null;
        }

        MailHeaderVariable mhv;
        mhv = (MailHeaderVariable) sqlResult.get(rowIndex);

        switch (columnIndex)
        {
            case 0:
                return mhv.getId(); // ID
            case 1:
                return mhv.getVarName();
            case 2:
                boolean f = (mhv.getFlags() & CS_Constants.MHV_CONTAINS_EMAIL) == CS_Constants.MHV_CONTAINS_EMAIL;
                return f;
            default:
                return super.getValueAt(rowIndex, columnIndex);
        }
    }

    @Override
    public int getColumnCount()
    {

        // EDIT IST 2.LAST ROW!!!!
        if (UserMain.self.getUserLevel() != USERMODE.UL_SYSADMIN)
        {
            return col_names.length - 2;
        }

        return col_names.length;
    }

    public MailHeaderVariable get_object( int index )
    {
        return (MailHeaderVariable) sqlResult.get(index);
    }
}

/**

@author  Administrator
 */
public class EditMandant extends GenericEditPanel implements PropertyChangeListener, MouseListener
{

    MandantOverview object_overview;
    MandantTableModel model;
    Mandant object;
    Mandant save_object;
    ArrayList<MailHeaderVariable> mhv_list;
    boolean was_new_record;

    /** Creates new form EditChannelPanel */
    public EditMandant( int _row, MandantOverview _overview )
    {
        initComponents();

        object_overview = _overview;
        model = object_overview.get_object_model();

        mhv_list = new ArrayList<MailHeaderVariable>();


        row = _row;
        if (!model.is_new(row))
        {
            object = model.get_object(row);
            save_object = new Mandant( object );

            String db_passwd = object.getPassword();
            String dec_passwd = CryptTools.crypt_internal( db_passwd, UserMain.self, CryptTools.ENC_MODE.DECRYPT);
            if (dec_passwd != null)
                db_passwd = dec_passwd;

            TXT_NAME.setText(object.getName());
            TXT_USER.setText(object.getLoginname());
            TXTP_PWD.setText(db_passwd);
            TXTP_PWD1.setText(db_passwd);
            int port = object.getImap_port();
            if (port > 0)
            {
                CB_IMAP_SSL.setEnabled(true);
                if (test_flag(CS_Constants.MA_IMAP_SSL))
                {
                    CB_IMAP_SSL.setSelected(true);
                }
                TXT_IMAP_PORT.setText("" + port);
                TXT_IMAP_HOST.setText(object.getImap_host());
            }
            else
            {
                CB_IMAP_SSL.setEnabled(false);
            }

            // CALLBACK SETS TEXT AND EDITABLE
            BT_IMAP_ENABLED.setSelected(port != 0);

            TXT_SMTP_HOST.setText(object.getSmtp_host());
            TXT_SMTP_PORT.setText( Integer.toString(object.getSmtp_port()));
            TXT_SMTP_USER.setText(object.getSmtp_user());
            TXTP_SMTP_PWD.setText(object.getSmtp_pwd());
            
            

            if (test_smtp_flag( CS_Constants.ACCT_USE_SSL))
                RB_SSL.setSelected(true);
            else if (test_smtp_flag( CS_Constants.ACCT_USE_TLS_IF_AVAIL))
                RB_TLS_IV_AVAIL.setSelected(true);
            else if (test_smtp_flag( CS_Constants.ACCT_USE_TLS_FORCE))
                RB_TLS_FORCE.setSelected(true);
            else
                RB_INSECURE.setSelected(true);

            CB_CERTIFICATE.setSelected(test_smtp_flag(CS_Constants.ACCT_HAS_TLS_CERT));
            if (CB_CERTIFICATE.isSelected())
            {
                BT_IMPORT_CERT.setVisible(true);
            }

            TXT_MAILTO.setText(object.getNotificationlist());
            TXT_MAILFROM.setText( object.getMailfrom());
            if (test_flag(CS_Constants.MA_HTTPS_ENABLE))
            {
                CB_HTTPD.setSelected(true);
                CB_OWN_HTTPD.setVisible(true);
                CB_OWN_HTTPD.setSelected( test_flag(CS_Constants.MA_HTTPS_OWN));
            }
            else
            {
                CB_HTTPD.setSelected(false);
                CB_OWN_HTTPD.setVisible(false);
                CB_OWN_HTTPD.setSelected( false);
            }

            // SET AUTH VISIBILITY
            CB_SMTP_AUTH.setSelected(!test_flag(CS_Constants.MA_NO_SMTP_AUTH));
            CB_SMTP_AUTHActionPerformed(null);

        }
        else
        {
            object = new Mandant();
            TXT_NAME.setText("New Company");
            TXT_USER.setText("admin");
            BT_IMAP_ENABLED.setSelected(false);
            RB_INSECURE.setSelected(true);
            TXT_IMAP_PORT.setText("143");
            TXT_SMTP_PORT.setText("25");
            was_new_record = true;
            CB_SMTP_AUTH.setSelected(true);
            CB_SMTP_AUTHActionPerformed(null);
        }

        SCP_TABLE.remove(jTable1);
        table = new GlossTable();
        table.addMouseListener(this);

        build_header_list(object);
    }

    final boolean test_flag( int test_flag )
    {
        int flags = Integer.parseInt(object.getFlags());
        return ((flags & test_flag) == test_flag);
    }
    final boolean test_smtp_flag( int test_flag )
    {
        int flags = object.getSmtp_flags();
        return ((flags & test_flag) == test_flag);
    }
    void set_smtp_flag( int flag, boolean state  )
    {
        if (state)
        {
            set_smtp_flag(flag);
        }
        else
        {
            clr_smtp_flag(flag);
        }
    }

   

    /** This method is called from within the constructor to
    initialize the form.
    WARNING: Do NOT modify this code. The content of this method is
    always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        PN_BASE = new javax.swing.JPanel();
        PN_ACTION = new javax.swing.JPanel();
        TXT_NAME = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        BT_DISABLED = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        TXT_USER = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        TXTP_PWD = new javax.swing.JPasswordField();
        jLabel12 = new javax.swing.JLabel();
        TXTP_PWD1 = new javax.swing.JPasswordField();
        CB_HTTPD = new javax.swing.JCheckBox();
        CB_OWN_HTTPD = new javax.swing.JCheckBox();
        BT_TEST_ENCRYPTION = new javax.swing.JButton();
        PN_SMTP = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        TXT_SMTP_HOST = new javax.swing.JTextField();
        TXT_SMTP_PORT = new javax.swing.JTextField();
        LB_USER = new javax.swing.JLabel();
        TXT_SMTP_USER = new javax.swing.JTextField();
        LB_PWD = new javax.swing.JLabel();
        PN_SECURITY = new javax.swing.JPanel();
        RB_INSECURE = new javax.swing.JRadioButton();
        RB_TLS_IV_AVAIL = new javax.swing.JRadioButton();
        RB_TLS_FORCE = new javax.swing.JRadioButton();
        RB_SSL = new javax.swing.JRadioButton();
        CB_CERTIFICATE = new javax.swing.JCheckBox();
        BT_IMPORT_CERT = new javax.swing.JButton();
        TXTP_SMTP_PWD = new javax.swing.JPasswordField();
        BT_TEST = new GlossButton();
        jLabel7 = new javax.swing.JLabel();
        TXT_MAILFROM = new javax.swing.JTextField();
        jLabel14 = new javax.swing.JLabel();
        TXT_MAILTO = new javax.swing.JTextField();
        CB_SMTP_AUTH = new javax.swing.JCheckBox();
        jLabel13 = new javax.swing.JLabel();
        PN_INDEX = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        BT_ADD_HEADER = new GlossButton();
        SCP_TABLE = new javax.swing.JScrollPane();
        jTable1 = new GlossTable();
        PN_IMAP = new javax.swing.JPanel();
        BT_IMAP_ENABLED = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        TXT_IMAP_HOST = new javax.swing.JTextField();
        TXT_IMAP_PORT = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        CB_IMAP_SSL = new javax.swing.JCheckBox();
        jLabel15 = new javax.swing.JLabel();
        PN_BUTTONS = new javax.swing.JPanel();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();
        BT_HELP1 = new GlossButton();
        BT_EXPORT = new GlossButton();

        setDoubleBuffered(false);
        setOpaque(false);

        PN_ACTION.setDoubleBuffered(false);
        PN_ACTION.setOpaque(false);

        TXT_NAME.setText(UserMain.Txt("Neuer_Name")); // NOI18N
        TXT_NAME.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_NAMEMouseClicked(evt);
            }
        });
        TXT_NAME.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TXT_NAMEActionPerformed(evt);
            }
        });

        jLabel2.setText(UserMain.getString("Name")); // NOI18N

        BT_DISABLED.setText(UserMain.getString("Gesperrt")); // NOI18N
        BT_DISABLED.setOpaque(false);

        jLabel5.setText(UserMain.getString("Login_Name")); // NOI18N

        TXT_USER.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_USERMouseClicked(evt);
            }
        });
        TXT_USER.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TXT_USERActionPerformed(evt);
            }
        });

        jLabel6.setText(UserMain.getString("Password")); // NOI18N

        jLabel12.setText(UserMain.getString("Repeat_password")); // NOI18N

        CB_HTTPD.setText(UserMain.Txt("WebClient")); // NOI18N
        CB_HTTPD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_HTTPDActionPerformed(evt);
            }
        });

        CB_OWN_HTTPD.setText(UserMain.Txt("Eigener_Webserver")); // NOI18N
        CB_OWN_HTTPD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_OWN_HTTPDActionPerformed(evt);
            }
        });

        BT_TEST_ENCRYPTION.setText(UserMain.Txt("Test_Encryptionpassword")); // NOI18N
        BT_TEST_ENCRYPTION.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_TEST_ENCRYPTIONActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_ACTIONLayout = new javax.swing.GroupLayout(PN_ACTION);
        PN_ACTION.setLayout(PN_ACTIONLayout);
        PN_ACTIONLayout.setHorizontalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(BT_DISABLED))
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addComponent(CB_OWN_HTTPD))
                            .addComponent(CB_HTTPD)
                            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel12)
                                    .addComponent(jLabel6)
                                    .addComponent(jLabel2))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(TXT_NAME, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(TXT_USER, javax.swing.GroupLayout.DEFAULT_SIZE, 145, Short.MAX_VALUE)
                                    .addComponent(TXTP_PWD, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(TXTP_PWD1, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(BT_TEST_ENCRYPTION)))
                .addContainerGap(303, Short.MAX_VALUE))
        );

        PN_ACTIONLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {TXTP_PWD, TXTP_PWD1, TXT_NAME, TXT_USER});

        PN_ACTIONLayout.setVerticalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_NAME, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_USER, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXTP_PWD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXTP_PWD1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12))
                .addGap(18, 18, 18)
                .addComponent(CB_HTTPD)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(CB_OWN_HTTPD)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BT_TEST_ENCRYPTION)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 54, Short.MAX_VALUE)
                .addComponent(BT_DISABLED)
                .addContainerGap())
        );

        javax.swing.GroupLayout PN_BASELayout = new javax.swing.GroupLayout(PN_BASE);
        PN_BASE.setLayout(PN_BASELayout);
        PN_BASELayout.setHorizontalGroup(
            PN_BASELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BASELayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PN_ACTION, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        PN_BASELayout.setVerticalGroup(
            PN_BASELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_BASELayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PN_ACTION, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dimm/home/MA_Properties"); // NOI18N
        jTabbedPane1.addTab(bundle.getString("Base_Parameter"), PN_BASE); // NOI18N

        jLabel8.setText(bundle.getString("SMTP_Port")); // NOI18N

        jLabel9.setText(bundle.getString("SMTP_Host")); // NOI18N

        LB_USER.setText(bundle.getString("Username")); // NOI18N

        LB_PWD.setText(bundle.getString("Password")); // NOI18N

        PN_SECURITY.setBorder(javax.swing.BorderFactory.createTitledBorder(UserMain.Txt("Security"))); // NOI18N

        buttonGroup1.add(RB_INSECURE);
        RB_INSECURE.setText(UserMain.Txt("unsecure")); // NOI18N
        RB_INSECURE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RB_INSECUREActionPerformed(evt);
            }
        });

        buttonGroup1.add(RB_TLS_IV_AVAIL);
        RB_TLS_IV_AVAIL.setText(UserMain.Txt("TLS_if_available")); // NOI18N
        RB_TLS_IV_AVAIL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RB_TLS_IV_AVAILActionPerformed(evt);
            }
        });

        buttonGroup1.add(RB_TLS_FORCE);
        RB_TLS_FORCE.setText(UserMain.Txt("only_TLS")); // NOI18N
        RB_TLS_FORCE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RB_TLS_FORCEActionPerformed(evt);
            }
        });

        buttonGroup1.add(RB_SSL);
        RB_SSL.setText(UserMain.Txt("SSL")); // NOI18N
        RB_SSL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RB_SSLActionPerformed(evt);
            }
        });

        CB_CERTIFICATE.setText(UserMain.Txt("with_Certificate")); // NOI18N
        CB_CERTIFICATE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_CERTIFICATEActionPerformed(evt);
            }
        });

        BT_IMPORT_CERT.setText(UserMain.Txt("Import_certificate")); // NOI18N
        BT_IMPORT_CERT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_IMPORT_CERTActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_SECURITYLayout = new javax.swing.GroupLayout(PN_SECURITY);
        PN_SECURITY.setLayout(PN_SECURITYLayout);
        PN_SECURITYLayout.setHorizontalGroup(
            PN_SECURITYLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_SECURITYLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_SECURITYLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PN_SECURITYLayout.createSequentialGroup()
                        .addGap(21, 21, 21)
                        .addComponent(BT_IMPORT_CERT)
                        .addContainerGap())
                    .addGroup(PN_SECURITYLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(PN_SECURITYLayout.createSequentialGroup()
                            .addGroup(PN_SECURITYLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(RB_TLS_IV_AVAIL)
                                .addComponent(RB_INSECURE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(RB_TLS_FORCE))
                            .addGap(63, 63, 63))
                        .addGroup(PN_SECURITYLayout.createSequentialGroup()
                            .addGroup(PN_SECURITYLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(RB_SSL)
                                .addComponent(CB_CERTIFICATE))
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))))
        );
        PN_SECURITYLayout.setVerticalGroup(
            PN_SECURITYLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_SECURITYLayout.createSequentialGroup()
                .addComponent(RB_INSECURE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RB_TLS_IV_AVAIL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RB_TLS_FORCE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RB_SSL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CB_CERTIFICATE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(BT_IMPORT_CERT)
                .addContainerGap(60, Short.MAX_VALUE))
        );

        BT_TEST.setText(bundle.getString("Test")); // NOI18N
        BT_TEST.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_TESTActionPerformed(evt);
            }
        });

        jLabel7.setText(UserMain.getString("Sender")); // NOI18N

        jLabel14.setText(UserMain.getString("To_List")); // NOI18N

        TXT_MAILTO.setEditable(false);
        TXT_MAILTO.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_MAILTOMouseClicked(evt);
            }
        });

        CB_SMTP_AUTH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_SMTP_AUTHActionPerformed(evt);
            }
        });

        jLabel13.setText(UserMain.Txt("Authentification")); // NOI18N

        javax.swing.GroupLayout PN_SMTPLayout = new javax.swing.GroupLayout(PN_SMTP);
        PN_SMTP.setLayout(PN_SMTPLayout);
        PN_SMTPLayout.setHorizontalGroup(
            PN_SMTPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_SMTPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_SMTPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PN_SMTPLayout.createSequentialGroup()
                        .addGroup(PN_SMTPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addComponent(jLabel8)
                            .addComponent(jLabel7)
                            .addComponent(jLabel14)
                            .addComponent(jLabel13))
                        .addGap(9, 9, 9)
                        .addGroup(PN_SMTPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PN_SMTPLayout.createSequentialGroup()
                                .addGroup(PN_SMTPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(LB_PWD)
                                    .addComponent(LB_USER))
                                .addGap(9, 9, 9)
                                .addGroup(PN_SMTPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(TXT_SMTP_USER, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                                    .addComponent(TXTP_SMTP_PWD, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)))
                            .addComponent(TXT_MAILFROM, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                            .addComponent(TXT_SMTP_HOST, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                            .addComponent(TXT_MAILTO, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE)
                            .addComponent(TXT_SMTP_PORT, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_SMTP_AUTH))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PN_SECURITY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(BT_TEST))
                .addContainerGap())
        );
        PN_SMTPLayout.setVerticalGroup(
            PN_SMTPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_SMTPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_SMTPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PN_SMTPLayout.createSequentialGroup()
                        .addGroup(PN_SMTPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TXT_SMTP_HOST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(PN_SMTPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TXT_SMTP_PORT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel8))
                        .addGap(11, 11, 11)
                        .addGroup(PN_SMTPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(CB_SMTP_AUTH, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(12, 12, 12)
                        .addGroup(PN_SMTPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TXT_SMTP_USER, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LB_USER))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(PN_SMTPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(LB_PWD)
                            .addComponent(TXTP_SMTP_PWD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(PN_SMTPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(TXT_MAILFROM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(PN_SMTPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel14)
                            .addComponent(TXT_MAILTO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 67, Short.MAX_VALUE)
                        .addComponent(BT_TEST)
                        .addContainerGap())
                    .addGroup(PN_SMTPLayout.createSequentialGroup()
                        .addComponent(PN_SECURITY, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap(84, Short.MAX_VALUE))))
        );

        jTabbedPane1.addTab(UserMain.Txt("SMTP-Parameter"), PN_SMTP); // NOI18N

        jLabel3.setText(bundle.getString("Included_header_fields")); // NOI18N

        BT_ADD_HEADER.setText(bundle.getString("New_Header")); // NOI18N
        BT_ADD_HEADER.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ADD_HEADERActionPerformed(evt);
            }
        });

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        SCP_TABLE.setViewportView(jTable1);

        javax.swing.GroupLayout PN_INDEXLayout = new javax.swing.GroupLayout(PN_INDEX);
        PN_INDEX.setLayout(PN_INDEXLayout);
        PN_INDEXLayout.setHorizontalGroup(
            PN_INDEXLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_INDEXLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_INDEXLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(BT_ADD_HEADER)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PN_INDEXLayout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(SCP_TABLE, javax.swing.GroupLayout.DEFAULT_SIZE, 430, Short.MAX_VALUE)))
                .addContainerGap())
        );
        PN_INDEXLayout.setVerticalGroup(
            PN_INDEXLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_INDEXLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_INDEXLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(SCP_TABLE, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BT_ADD_HEADER)
                .addContainerGap(158, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(bundle.getString("Index_Parameter"), PN_INDEX); // NOI18N

        BT_IMAP_ENABLED.setText(bundle.getString("Enable_IMAP_Server")); // NOI18N
        BT_IMAP_ENABLED.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_IMAP_ENABLEDActionPerformed(evt);
            }
        });

        jLabel4.setText(bundle.getString("IMAP_Host")); // NOI18N

        jLabel1.setText(bundle.getString("IMAP_Port")); // NOI18N

        CB_IMAP_SSL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_IMAP_SSLActionPerformed(evt);
            }
        });

        jLabel15.setText(UserMain.Txt("SSL")); // NOI18N

        javax.swing.GroupLayout PN_IMAPLayout = new javax.swing.GroupLayout(PN_IMAP);
        PN_IMAP.setLayout(PN_IMAPLayout);
        PN_IMAPLayout.setHorizontalGroup(
            PN_IMAPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_IMAPLayout.createSequentialGroup()
                .addGroup(PN_IMAPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PN_IMAPLayout.createSequentialGroup()
                        .addGap(23, 23, 23)
                        .addComponent(BT_IMAP_ENABLED))
                    .addGroup(PN_IMAPLayout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addGroup(PN_IMAPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel1)
                            .addComponent(jLabel15))
                        .addGap(10, 10, 10)
                        .addGroup(PN_IMAPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CB_IMAP_SSL)
                            .addGroup(PN_IMAPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(TXT_IMAP_HOST)
                                .addComponent(TXT_IMAP_PORT, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(310, Short.MAX_VALUE))
        );
        PN_IMAPLayout.setVerticalGroup(
            PN_IMAPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_IMAPLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BT_IMAP_ENABLED)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PN_IMAPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_IMAP_HOST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PN_IMAPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_IMAP_PORT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PN_IMAPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CB_IMAP_SSL)
                    .addComponent(jLabel15))
                .addContainerGap(207, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab(UserMain.getString("IMAP-Parameter"), PN_IMAP); // NOI18N

        PN_BUTTONS.setDoubleBuffered(false);
        PN_BUTTONS.setOpaque(false);

        BT_OK.setText(UserMain.Txt("Okay")); // NOI18N
        BT_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKActionPerformed(evt);
            }
        });

        BT_ABORT.setText(UserMain.Txt("Abbruch")); // NOI18N
        BT_ABORT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ABORTActionPerformed(evt);
            }
        });

        BT_HELP1.setText(UserMain.Txt("?")); // NOI18N
        BT_HELP1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_HELP1ActionPerformed(evt);
            }
        });

        BT_EXPORT.setText("Export alle Mailkonten");
        BT_EXPORT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_EXPORTActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_BUTTONSLayout = new javax.swing.GroupLayout(PN_BUTTONS);
        PN_BUTTONS.setLayout(PN_BUTTONSLayout);
        PN_BUTTONSLayout.setHorizontalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BT_EXPORT)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(BT_HELP1)
                .addGap(18, 18, 18)
                .addComponent(BT_ABORT)
                .addGap(18, 18, 18)
                .addComponent(BT_OK)
                .addContainerGap())
        );

        PN_BUTTONSLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ABORT, BT_OK});

        PN_BUTTONSLayout.setVerticalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OK)
                    .addComponent(BT_ABORT)
                    .addComponent(BT_HELP1)
                    .addComponent(BT_EXPORT))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(PN_BUTTONS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jTabbedPane1))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jTabbedPane1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PN_BUTTONS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents


    @Override
    protected void ok_action(Object o, Object so)
    {
        boolean has_pwd = false;

        boolean ok = save_action( o, so );

        if (!ok)
            return;


        String ret = UserMain.fcc().call_abstract_function("GETSETOPTION CMD:GETPWD MA:" + object.getId(), ServerCall.SHORT_CMD_TO);
        if (ret != null && ret.charAt(0) == '0')
        {
            has_pwd = true;
        }


        if (!has_pwd)
        {
            if (was_new_record)
            {
                while (!has_pwd)
                {
                    set_enc_passord();

                    // DOUBLECHECK
                    ret = UserMain.fcc().call_abstract_function("GETSETOPTION CMD:GETPWD MA:" + object.getId(), ServerCall.SHORT_CMD_TO);
                    if (ret != null && ret.charAt(0) == '0')
                        has_pwd = true;
                    else
                        UserMain.errm_ok(my_dlg, UserMain.Txt("You_must_provide_a_valid_encryption_password") );
                }
            }
            else
            {
                UserMain.errm_ok(my_dlg, UserMain.Txt("You_are_using_the_default_password") );
            }
        
        }
        this.setVisible(false);

    }

    private void BT_OKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKActionPerformed
    {//GEN-HEADEREND:event_BT_OKActionPerformed
      
        // WE HAVE TO SAVE FIRST TO GET A VALID ID!!!!
        ok_action(object, save_object);
    }//GEN-LAST:event_BT_OKActionPerformed

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ABORTActionPerformed
    {//GEN-HEADEREND:event_BT_ABORTActionPerformed
        // TODO add your handling code here:
        abort_action();
    }//GEN-LAST:event_BT_ABORTActionPerformed

    private void TXT_NAMEMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXT_NAMEMouseClicked
    {//GEN-HEADEREND:event_TXT_NAMEMouseClicked
        // TODO add your handling code here:
        if (UserMain.self.is_touchscreen())
        {
            UserMain.self.show_vkeyboard(this.my_dlg, TXT_NAME, false);
        }
}//GEN-LAST:event_TXT_NAMEMouseClicked

    private void TXT_NAMEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TXT_NAMEActionPerformed
    {//GEN-HEADEREND:event_TXT_NAMEActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_TXT_NAMEActionPerformed

    private void TXT_USERMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXT_USERMouseClicked
    {//GEN-HEADEREND:event_TXT_USERMouseClicked
        // TODO add your handling code here:
        if (UserMain.self.is_touchscreen())
        {
            UserMain.self.show_vkeyboard(this.my_dlg, TXT_USER, false);
        }

    }//GEN-LAST:event_TXT_USERMouseClicked

    private void TXT_USERActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TXT_USERActionPerformed
    {//GEN-HEADEREND:event_TXT_USERActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TXT_USERActionPerformed

    private void BT_IMAP_ENABLEDActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_IMAP_ENABLEDActionPerformed
    {//GEN-HEADEREND:event_BT_IMAP_ENABLEDActionPerformed
        // TODO add your handling code here:
        if (!BT_IMAP_ENABLED.isSelected())
        {
            TXT_IMAP_PORT.setText("");
            TXT_IMAP_PORT.setEditable(false);
            TXT_IMAP_HOST.setText("");
            TXT_IMAP_HOST.setEditable(false);
            CB_IMAP_SSL.setEnabled(false);
        }
        else
        {
            // SET THE PORT ACCORDING TO SSL
            CB_IMAP_SSLActionPerformed(null);
                      
            TXT_IMAP_PORT.setEditable(true);
            TXT_IMAP_HOST.setText(object.getImap_host());
            TXT_IMAP_HOST.setEditable(true);
            CB_IMAP_SSL.setEnabled(true);



            UserMain.errm_ok(my_dlg, UserMain.Txt("IMAP_Warning"));
        }
    }//GEN-LAST:event_BT_IMAP_ENABLEDActionPerformed

    private void BT_ADD_HEADERActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ADD_HEADERActionPerformed
    {//GEN-HEADEREND:event_BT_ADD_HEADERActionPerformed
        // TODO add your handling code here:
        new_hmv_object();
    }//GEN-LAST:event_BT_ADD_HEADERActionPerformed

    private void RB_INSECUREActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RB_INSECUREActionPerformed
    {//GEN-HEADEREND:event_RB_INSECUREActionPerformed
        // TODO add your handling code here:
        TXT_SMTP_PORT.setText((RB_SSL.isSelected()?"465":"25") );
       
}//GEN-LAST:event_RB_INSECUREActionPerformed

    private void RB_TLS_IV_AVAILActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RB_TLS_IV_AVAILActionPerformed
    {//GEN-HEADEREND:event_RB_TLS_IV_AVAILActionPerformed
        // TODO add your handling code here:
        TXT_SMTP_PORT.setText((RB_SSL.isSelected()?"465":"25") );
        
    }//GEN-LAST:event_RB_TLS_IV_AVAILActionPerformed

    private void RB_TLS_FORCEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RB_TLS_FORCEActionPerformed
    {//GEN-HEADEREND:event_RB_TLS_FORCEActionPerformed
        // TODO add your handling code here:
        TXT_SMTP_PORT.setText((RB_SSL.isSelected()?"465":"25") );
        
    }//GEN-LAST:event_RB_TLS_FORCEActionPerformed

    private void RB_SSLActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RB_SSLActionPerformed
    {//GEN-HEADEREND:event_RB_SSLActionPerformed
        // TODO add your handling code here:
        TXT_SMTP_PORT.setText((RB_SSL.isSelected()?"465":"25") );
        
    }//GEN-LAST:event_RB_SSLActionPerformed

    private void CB_CERTIFICATEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_CERTIFICATEActionPerformed
    {//GEN-HEADEREND:event_CB_CERTIFICATEActionPerformed
        // TODO add your handling code here:
        BT_IMPORT_CERT.setVisible(CB_CERTIFICATE.isSelected());
}//GEN-LAST:event_CB_CERTIFICATEActionPerformed

    static File last_dir = null;
    SwingWorker sw = null;
    private void BT_IMPORT_CERTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_IMPORT_CERTActionPerformed
    {//GEN-HEADEREND:event_BT_IMPORT_CERTActionPerformed
        // TODO add your handling code here:

        // CHOOSE CERTFILE
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        chooser.setCurrentDirectory(last_dir);
        if (JFileChooser.APPROVE_OPTION != chooser.showDialog(my_dlg, UserMain.Txt("Select_certificate")))
        {
            return;
        }
        File dir = chooser.getSelectedFile();
        last_dir = dir;

        // READ TO CERTBUFF
        ByteBuffer bb = ByteBuffer.allocate( (int)dir.length());
        try
        {
            FileInputStream fr = new FileInputStream(dir);

            fr.read(bb.array());
            fr.close();
        }
        catch (IOException iOException)
        {
            UserMain.errm_ok(my_dlg, UserMain.Txt("Error_while_reading_certificate") + ": " + iOException.getMessage() );
            return;
        }
        // BUILD IMPORT_COMMAND


        CXStream xs = new CXStream();
        String cert_xml = xs.toXML(bb);

        final String cmd = "upload_certificate MA:" + object.getId() + " TY:cacert + CERT:" + cert_xml;

        if (sw != null)
        {
            return;
        }

        // AND SHOVE IT RIGHT OUT
        sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                UserMain.self.show_busy(my_dlg, UserMain.Txt("Importing_certificate") + "...");
                String ret = UserMain.fcc().call_abstract_function(cmd, ServerCall.SHORT_CMD_TO);
                UserMain.self.hide_busy();
                if (ret.charAt(0) == '0')
                {
                    UserMain.info_ok(my_dlg, UserMain.Txt("Certificate_was_imported_successful"));
                }
                else
                {
                    UserMain.errm_ok(my_dlg, UserMain.Txt("Importing_certificate_failed") + ": " + ret.substring(3));
                }
                sw = null;
                return null;
            }
        };

        sw.start();

    }//GEN-LAST:event_BT_IMPORT_CERTActionPerformed

    private void BT_TESTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_TESTActionPerformed
    {//GEN-HEADEREND:event_BT_TESTActionPerformed
        // TODO add your handling code here:
      /*  int flags = 0;
        if (RB_SSL.isSelected())
            flags |= CS_Constants.ACCT_USE_SSL;
        if (RB_TLS_FORCE.isSelected())
            flags |= CS_Constants.ACCT_USE_TLS_FORCE;
        if (RB_TLS_IV_AVAIL.isSelected())
            flags |= CS_Constants.ACCT_USE_TLS_IF_AVAIL;

        int m_id = object.getId();
        if (m_id <= 0)
        {
             UserMain.errm_ok(my_dlg, UserMain.Txt("Please_save_this_record_first"));
             return;
        }
        String name = "";
        String pwd = "";
        if (CB_SMTP_AUTH.isSelected())
        {
            name = TXT_SMTP_USER.getText();
            pwd = get_smtp_pwd();
        }


        final String cmd = "TestLogin CMD:test MA:" + m_id + " NM:'" + name + "' PW:'" + pwd + "' HO:" +
                TXT_SMTP_HOST.getText() + " PO:" + TXT_SMTP_PORT.getText() + " TY:smtp" +  " FL:" + flags;

        if (sw != null)
        {
            return;
        }

        sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                UserMain.self.show_busy(my_dlg, UserMain.Txt("Checking_login") + "...");
                String ret = UserMain.fcc().call_abstract_function(cmd, ServerCall.SHORT_CMD_TO);
                UserMain.self.hide_busy();
                if (ret != null && ret.charAt(0) == '0')
                {
                    UserMain.info_ok(my_dlg, UserMain.Txt("Login_succeeded"));
                }
                else
                {
                    UserMain.errm_ok(my_dlg, UserMain.Txt("Login_failed") + ": " + (ret != null ? ret.substring(3): ""));
                }
                sw = null;
                return null;
            }
        };

        sw.start();
       * */
        int flags = 0;
        if (RB_SSL.isSelected())
            flags |= CS_Constants.ACCT_USE_SSL;
        if (RB_TLS_FORCE.isSelected())
            flags |= CS_Constants.ACCT_USE_TLS_FORCE;
        if (RB_TLS_IV_AVAIL.isSelected())
            flags |= CS_Constants.ACCT_USE_TLS_IF_AVAIL;

       
        String name = "";
        String pwd = "";
        if (CB_SMTP_AUTH.isSelected())
        {
            name = TXT_SMTP_USER.getText();
            pwd = get_smtp_pwd();
        }

        String txt = "Testmail MailSecurer Parameterdialog";
        int lvl = /*Notification.NF_INFORMATIVE*/ 1;

        String from = TXT_MAILFROM.getText();
        String to = TXT_MAILTO.getText();
        if (from.length() == 0 || to.length() == 0)
        {
            UserMain.errm_ok(my_dlg, UserMain.Txt("Mailadressdaten_sind_unvollstaendig"));
            return;
        }


        final String cmd = "TestLogin CMD:test_notification MN:" + TXT_NAME.getText() + " NM:'" + name + "' PW:'" + pwd + "' HO:" +
                TXT_SMTP_HOST.getText() + " PO:" + TXT_SMTP_PORT.getText() + " FL:" + flags + " ST:" + to +
                " FM:" + from + " NA:" + (CB_SMTP_AUTH.isSelected() ? "1":"0") + " LV:" + lvl + " TX:\"" + txt + "\"";

        if (sw != null)
        {
            return;
        }

        sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                UserMain.self.show_busy(my_dlg, UserMain.Txt("Sending_a_test_mail_to") + " " + TXT_MAILTO.getText() + "...");
                String ret = UserMain.fcc().call_abstract_function(cmd, ServerCall.SHORT_CMD_TO);
                UserMain.self.hide_busy();
                if (ret != null && ret.charAt(0) == '0')
                {
                    UserMain.info_ok(my_dlg, UserMain.Txt("Succeeded"));
                }
                else
                {
                    UserMain.errm_ok(my_dlg, UserMain.Txt("Failed") + ": " + (ret != null ? ret.substring(3): ""));
                }
                sw = null;
                return null;
            }
        };

        sw.start();



    }//GEN-LAST:event_BT_TESTActionPerformed

    private void TXT_MAILTOMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXT_MAILTOMouseClicked
    {//GEN-HEADEREND:event_TXT_MAILTOMouseClicked
        // TODO add your handling code here:
        EditTextList etl = new EditTextList( TXT_MAILTO.getText(), CS_Constants.TEXTLIST_DELIM );
        etl.setEmail(true);
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, etl);
        dlg.setTitle(UserMain.Txt("Target_mail_adresses_for_notifications"));
        dlg.setLocation(TXT_MAILTO.getLocationOnScreen());
        dlg.setVisible(true);
        if (etl.isOkay())
        {
            TXT_MAILTO.setText(etl.get_text());
        }

    }//GEN-LAST:event_TXT_MAILTOMouseClicked

    private void CB_IMAP_SSLActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_IMAP_SSLActionPerformed
    {//GEN-HEADEREND:event_CB_IMAP_SSLActionPerformed
        // TODO add your handling code here:
        TXT_IMAP_PORT.setText( (CB_IMAP_SSL.isSelected() ? "993" : "143" ));
    }//GEN-LAST:event_CB_IMAP_SSLActionPerformed

    void show_act_httpd_port()
    {
        int port = 8000;
        if ( CB_OWN_HTTPD.isSelected() )
        {
            port = 8001 + object.getId();
        }
        UserMain.info_ok(this.my_dlg, UserMain.Txt("Das_Webinterface_dieses_Mandanten_erreichen_Sie_unter") + " " + UserMain.fcc().get_ip() + ":" + port );
    }

    private void CB_HTTPDActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_HTTPDActionPerformed
    {//GEN-HEADEREND:event_CB_HTTPDActionPerformed
        // TODO add your handling code here:
        CB_OWN_HTTPD.setVisible( CB_HTTPD.isSelected() );

        if (CB_HTTPD.isSelected())
        {
            show_act_httpd_port();
        }
    }//GEN-LAST:event_CB_HTTPDActionPerformed

    private void BT_TEST_ENCRYPTIONActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_TEST_ENCRYPTIONActionPerformed
    {//GEN-HEADEREND:event_BT_TEST_ENCRYPTIONActionPerformed
        // TODO add your handling code here:
        SingleTextEditPanel pnl = new SingleTextEditPanel(UserMain.Txt("Passwort"));

        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);

        dlg.set_next_location(this);
        dlg.setTitle( BT_TEST_ENCRYPTION.getText() );
        dlg.setVisible(true);

        if (pnl.isOkay())
        {
            String user_pwd = pnl.getText();
            String ret = UserMain.fcc().call_abstract_function("GETSETOPTION CMD:CHECKENC MA:" + object.getId() + " PWD:" + user_pwd, ServerCall.SHORT_CMD_TO);

            if (ret != null && ret.length() > 0 && ret.charAt(0) == '0')
            {
                UserMain.info_ok(this.my_dlg, UserMain.Txt("Das_Passwort_ist_korrekt") );
            }
            else
            {
                UserMain.errm_ok(this.my_dlg, UserMain.Txt("Das_Passwort_ist_nicht_korrekt") );
            }
        }
    }//GEN-LAST:event_BT_TEST_ENCRYPTIONActionPerformed

    private void BT_HELP1ActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_HELP1ActionPerformed
    {//GEN-HEADEREND:event_BT_HELP1ActionPerformed
        // TODO add your handling code here:
        open_help(this.getClass().getSimpleName());
}//GEN-LAST:event_BT_HELP1ActionPerformed

    private void CB_OWN_HTTPDActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_OWN_HTTPDActionPerformed
    {//GEN-HEADEREND:event_CB_OWN_HTTPDActionPerformed
        // TODO add your handling code here:
        show_act_httpd_port();
    }//GEN-LAST:event_CB_OWN_HTTPDActionPerformed

    private void CB_SMTP_AUTHActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_SMTP_AUTHActionPerformed
    {//GEN-HEADEREND:event_CB_SMTP_AUTHActionPerformed
        // TODO add your handling code here:
        boolean sa = CB_SMTP_AUTH.isSelected();

        LB_PWD.setVisible(sa);
        LB_USER.setVisible(sa);
        TXT_SMTP_USER.setVisible(sa);
        TXTP_SMTP_PWD.setVisible(sa);
    }//GEN-LAST:event_CB_SMTP_AUTHActionPerformed

    private void BT_EXPORTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BT_EXPORTActionPerformed
        // TODO add your handling code here:
        startExport();

    }//GEN-LAST:event_BT_EXPORTActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_ADD_HEADER;
    private javax.swing.JCheckBox BT_DISABLED;
    private javax.swing.JButton BT_EXPORT;
    private javax.swing.JButton BT_HELP1;
    private javax.swing.JCheckBox BT_IMAP_ENABLED;
    private javax.swing.JButton BT_IMPORT_CERT;
    private javax.swing.JButton BT_OK;
    private javax.swing.JButton BT_TEST;
    private javax.swing.JButton BT_TEST_ENCRYPTION;
    private javax.swing.JCheckBox CB_CERTIFICATE;
    private javax.swing.JCheckBox CB_HTTPD;
    private javax.swing.JCheckBox CB_IMAP_SSL;
    private javax.swing.JCheckBox CB_OWN_HTTPD;
    private javax.swing.JCheckBox CB_SMTP_AUTH;
    private javax.swing.JLabel LB_PWD;
    private javax.swing.JLabel LB_USER;
    private javax.swing.JPanel PN_ACTION;
    private javax.swing.JPanel PN_BASE;
    private javax.swing.JPanel PN_BUTTONS;
    private javax.swing.JPanel PN_IMAP;
    private javax.swing.JPanel PN_INDEX;
    private javax.swing.JPanel PN_SECURITY;
    private javax.swing.JPanel PN_SMTP;
    private javax.swing.JRadioButton RB_INSECURE;
    private javax.swing.JRadioButton RB_SSL;
    private javax.swing.JRadioButton RB_TLS_FORCE;
    private javax.swing.JRadioButton RB_TLS_IV_AVAIL;
    private javax.swing.JScrollPane SCP_TABLE;
    private javax.swing.JPasswordField TXTP_PWD;
    private javax.swing.JPasswordField TXTP_PWD1;
    private javax.swing.JPasswordField TXTP_SMTP_PWD;
    private javax.swing.JTextField TXT_IMAP_HOST;
    private javax.swing.JTextField TXT_IMAP_PORT;
    private javax.swing.JTextField TXT_MAILFROM;
    private javax.swing.JTextField TXT_MAILTO;
    private javax.swing.JTextField TXT_NAME;
    private javax.swing.JTextField TXT_SMTP_HOST;
    private javax.swing.JTextField TXT_SMTP_PORT;
    private javax.swing.JTextField TXT_SMTP_USER;
    private javax.swing.JTextField TXT_USER;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    // End of variables declaration//GEN-END:variables

    void set_enc_passord()
    {
        String ret = UserMain.fcc().call_abstract_function("GETSETOPTION CMD:GETPWD MA:" + object.getId(), ServerCall.SHORT_CMD_TO);

        if (ret != null)
        {
            if (ret.equals("1: no_pwd"))
            {
                CheckPwdPanel pnl = new CheckPwdPanel(UserMain.self, /*trong*/true);
                GenericGlossyDlg dlg = new GenericGlossyDlg(null, true, pnl);
                dlg.setSize(500, 200);

                dlg.setLocation(UserMain.self.getLocationOnScreen().x + 200, UserMain.self.getLocationOnScreen().y + 50);
                dlg.setTitle(UserMain.getString("Verschluesselungpasswort_setzen"));
                dlg.setVisible(true);

                if (pnl.isOkay())
                {
                    String new_pwd = pnl.get_pwd();
                    ret = UserMain.fcc().call_abstract_function("GETSETOPTION CMD:SETPWD MA:" + object.getId() + " VAL:" + new_pwd, ServerCall.SHORT_CMD_TO);
                    if (ret != null && ret.charAt(0) == '0')
                    {
                        UserMain.info_ok(my_dlg, UserMain.Txt("A_new_encryption_password_was_set._Do_not_loose_it,_without_this_password_you_cannot_encrypt_any_data"));
                    }
                    else
                    {
                        UserMain.errm_ok(my_dlg, UserMain.Txt("The_encryption_password_could_not_be_set" + ": " + (ret != null? ret : "") ));
                    }
                }
            }
            else if (ret.charAt(0) == '0')
            {
                UserMain.info_ok(my_dlg, UserMain.Txt("An_encryption_password_was_already_set"));
            }
        }

    }
    int get_object_flags()
    {
        int flags = 0;
        if (object.getFlags() == null || object.getFlags().length() == 0)
        {
            return 0;
        }

        try
        {
            flags = Integer.parseInt(object.getFlags());
        }
        catch (NumberFormatException numberFormatException)
        {
            String object_name = object.getClass().getSimpleName();
            UserMain.errm_ok(my_dlg, "Invalid flag for " + object_name + " " + numberFormatException);
        }

        return flags;
    }

    void set_object_flag( int flag )
    {
        int flags = get_object_flags();
        flags |= flag;
        object.setFlags(Integer.toString(flags));
    }

    void clr_object_flag( int flag )
    {
        int flags = get_object_flags();
        flags &= ~flag;
        object.setFlags(Integer.toString(flags));
    }
    void set_flag(  int fl, boolean state  )
    {
        if (state)
            set_object_flag(fl);
        else
            clr_object_flag(fl);
    }
    
    void set_smtp_flag( int flag )
    {
        int flags = object.getSmtp_flags();
        flags |= flag;
        object.setSmtp_flags(flags);
    }

    void clr_smtp_flag( int flag )
    {
        int flags = get_object_flags();
        flags &= ~flag;
        object.setSmtp_flags(flags);
    }

    boolean object_is_disabled()
    {
        int flags = 0;

        flags = get_object_flags();

        return ((flags & CS_Constants.MA_DISABLED) == CS_Constants.MA_DISABLED);
    }

    void set_object_disabled( boolean f )
    {
        int flags = get_object_flags();

        if (f)
        {
            set_object_flag(CS_Constants.MA_DISABLED);
        }
        else
        {
            clr_object_flag(CS_Constants.MA_DISABLED);
        }
    }

    @Override
    protected boolean check_changed()
    {
        if (model.is_new(row))
        {
            return true;
        }

        String name = object.getName();
        if (name != null && TXT_NAME.getText().compareTo(name) != 0)
        {
            return true;
        }

        String login = object.getLoginname();
        if (login != null && TXT_USER.getText().compareTo(login) != 0)
        {
            return true;
        }


        String user = object.getLoginname();
        if (user == null || TXT_USER.getText().compareTo(user) != 0)
        {
            return true;
        }

        String pwd = object.getPassword();
        String dec_passwd = CryptTools.crypt_internal( pwd, UserMain.self, CryptTools.ENC_MODE.DECRYPT);
        if (dec_passwd != null)
            pwd = dec_passwd;
        
        if (pwd == null || get_pwd().compareTo(pwd) != 0)
        {
            return true;
        }


        if (BT_DISABLED.isSelected() != object_is_disabled())
        {
            return true;
        }

        if ((object.getImap_port() != 0) != BT_IMAP_ENABLED.isSelected())
        {
            return true;
        }

        if (BT_IMAP_ENABLED.isSelected())
        {
            int port = Integer.parseInt(TXT_IMAP_PORT.getText());
            if (port != object.getImap_port())
            {
                return true;
            }
            if (TXT_IMAP_HOST.getText().compareTo( object.getImap_host()) != 0)
                return true;

            if (CB_IMAP_SSL.isSelected() != test_flag(CS_Constants.MA_IMAP_SSL))
                return true;
        }

        int port = Integer.parseInt(TXT_SMTP_PORT.getText());
        if (port != object.getSmtp_port())
        {
            return true;
        }
        if (TXT_SMTP_HOST.getText().compareTo( object.getSmtp_host()) != 0)
            return true;

        if (TXT_SMTP_USER.getText().compareTo( object.getSmtp_user()) != 0)
            return true;

        if (TXT_SMTP_HOST.getText().compareTo( object.getSmtp_host()) != 0)
            return true;

        if (get_smtp_pwd().compareTo( object.getSmtp_pwd()) != 0)
            return true;

        if (!TXT_MAILTO.getText().equals(object.getNotificationlist()))
            return true;

        if (!TXT_MAILFROM.getText().equals( object.getMailfrom()) )
             return true;

        int smtp_flags = 0;
        if (RB_SSL.isSelected())
            smtp_flags |= CS_Constants.ACCT_USE_SSL;
        if (RB_TLS_FORCE.isSelected())
            smtp_flags |= CS_Constants.ACCT_USE_TLS_FORCE;
        if (RB_TLS_IV_AVAIL.isSelected())
            smtp_flags |= CS_Constants.ACCT_USE_TLS_IF_AVAIL;

        if (object.getSmtp_flags() != smtp_flags)
            return true;

        if (test_flag(CS_Constants.MA_HTTPS_ENABLE) != CB_HTTPD.isSelected())
            return true;
        if (test_flag(CS_Constants.MA_HTTPS_ENABLE))
        {
            if (test_flag(CS_Constants.MA_HTTPS_OWN) != CB_OWN_HTTPD.isSelected())
                return true;
        }
        if (test_flag(CS_Constants.MA_NO_SMTP_AUTH) != !CB_SMTP_AUTH.isSelected())
            return true;
/*
        MandantOverview.MandantLicenseEntry mte = (MandantOverview.MandantLicenseEntry) CB_LICENSE.getSelectedItem();
        if (mte.type.compareTo(object.getLicense()) != 0)
        {
            return true;
        }
*/
        return false;
    }

    String get_pwd()
    {
        char[] pwd = TXTP_PWD.getPassword();
        return new String(pwd);
    }
    String get_pwd1()
    {
        char[] pwd = TXTP_PWD1.getPassword();
        return new String(pwd);
    }
    String get_smtp_pwd()
    {
        char[] pwd = TXTP_SMTP_PWD.getPassword();
        return new String(pwd);
    }
    

    @Override
    protected boolean is_plausible()
    {

        if (!Validator.is_valid_name(TXT_NAME.getText(), 255))
        {
            UserMain.errm_ok(UserMain.getString("Der_Name_ist_nicht_okay"));
            return false;
        }
        if (!Validator.is_valid_user(TXT_USER.getText()))
        {
            UserMain.errm_ok(UserMain.getString("Der_User_ist_nicht_okay"));
            return false;
        }
        if (get_pwd().length() == 0 || get_pwd().length() > 80 || !get_pwd().equals( get_pwd1()))
        {
            UserMain.errm_ok(UserMain.getString("Das_Passwort_ist_nicht_okay"));
            return false;
        }

        if (BT_IMAP_ENABLED.isSelected())
        {
            if (!Validator.is_valid_port(TXT_IMAP_PORT.getText()))
            {
                UserMain.errm_ok(UserMain.getString("Der_IMAP-Port_ist_nicht_okay"));
                return false;
            }
            if (!Validator.is_valid_name(TXT_IMAP_HOST.getText(), 80))
            {
                UserMain.errm_ok(UserMain.getString("Der_IMAP-Host_ist_nicht_okay"));
                return false;
            }
        }

            if (!Validator.is_valid_name(TXT_SMTP_HOST.getText(), 255))
            {
                UserMain.errm_ok(UserMain.getString("Der_SMTP-Host_ist_nicht_okay"));
                return false;
            }
        if (!Validator.is_valid_port(TXT_SMTP_PORT.getText()))
        {
            UserMain.errm_ok(UserMain.getString("Der_SMTP-Port_ist_nicht_okay"));
            return false;
        }
        
        if (CB_SMTP_AUTH.isSelected())
        {
            if (!Validator.is_valid_name(TXT_SMTP_USER.getText(), 255))
            {
                UserMain.errm_ok(UserMain.getString("Der_SMTP-User_ist_nicht_okay"));
                return false;
            }

            if (get_smtp_pwd().length() == 0 || get_smtp_pwd().length() > 80)
            {
                UserMain.errm_ok(UserMain.getString("Das_SMTP-Passwort_ist_nicht_okay"));
                return false;
            }
        }

        if (!Validator.is_valid_email( TXT_MAILFROM.getText()) )
        {
             UserMain.errm_ok(UserMain.getString("Die_Notificationadresse_ist_nicht_okay"));
             return false;
        }



        return true;
    }

    @Override
    protected void set_object_props()
    {
        String name = TXT_NAME.getText();

        String user = TXT_USER.getText();
        String pwd = get_pwd();
        String enc_passwd = CryptTools.crypt_internal( pwd, UserMain.self, CryptTools.ENC_MODE.ENCRYPT);

        boolean de = BT_DISABLED.isSelected();

        object.setFlags("0");
        
        String lic = "";
        object.setName(name);
        set_object_disabled(de);
        set_flag(CS_Constants.MA_HTTPS_ENABLE, CB_HTTPD.isSelected());
        set_flag(CS_Constants.MA_HTTPS_OWN, CB_OWN_HTTPD.isSelected());
        set_flag(CS_Constants.MA_NO_SMTP_AUTH, !CB_SMTP_AUTH.isSelected());
        object.setLoginname(user);
        object.setPassword(enc_passwd);
        object.setLicense(lic);
        int imap_port = 0;
        String imap_host = "";
        if (BT_IMAP_ENABLED.isSelected())
        {
            imap_port = Integer.parseInt(TXT_IMAP_PORT.getText());
            imap_host = TXT_IMAP_HOST.getText();
            if (CB_IMAP_SSL.isSelected())
            {
                set_object_flag(CS_Constants.MA_IMAP_SSL);
            }
        }
        int smtp_flags = 0;
        if (RB_SSL.isSelected())
            smtp_flags |= CS_Constants.ACCT_USE_SSL;
        if (RB_TLS_FORCE.isSelected())
            smtp_flags |= CS_Constants.ACCT_USE_TLS_FORCE;
        if (RB_TLS_IV_AVAIL.isSelected())
            smtp_flags |= CS_Constants.ACCT_USE_TLS_IF_AVAIL;



        object.setImap_port(imap_port);
        object.setImap_host(imap_host);

        object.setSmtp_host(TXT_SMTP_HOST.getText());
        object.setSmtp_user(TXT_SMTP_USER.getText());
        object.setSmtp_port(Integer.parseInt(TXT_SMTP_PORT.getText()));
        object.setSmtp_pwd(get_smtp_pwd());
        object.setSmtp_flags(smtp_flags);
        
        object.setMailfrom( TXT_MAILFROM.getText() );
        object.setNotificationlist( TXT_MAILTO.getText() );


    }

    @Override
    public JButton get_default_button()
    {
        return BT_OK;
    }

    @Override
    protected boolean is_new()
    {
        return model.is_new(row);
    }
    GlossTable table;
    HeaderModel hmodel;

    private void build_header_list( Mandant m )
    {
        ServerCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open();
        StatementID sid = sql.createStatement(cid);


        ResultSetID rid = sql.executeQuery(sid, "select * from mail_header_variable where mid=" + m.getId());
        SQLArrayResult resa = sql.get_sql_array_result(rid);
        SQLResult<MailHeaderVariable> res = new SQLResult<MailHeaderVariable>(UserMain.sqc(), resa, new MailHeaderVariable().getClass());

              
        hmodel = new HeaderModel(UserMain.self, null);
        table.setModel(hmodel);
        table.embed_to_scrollpanel(SCP_TABLE);
       
        hmodel.setSqlResult(res);

        TableColumnModel cm = table.getTableHeader().getColumnModel();
        cm.getColumn(0).setMinWidth(40);
        cm.getColumn(0).setMaxWidth(40);
        cm.getColumn(1).setPreferredWidth(150);
        cm.getColumn(2).setMinWidth(60);
        cm.getColumn(2).setMaxWidth(60);
        hmodel.set_table_header(cm);
    }
    
    void edit_hmv_row( int row )
    {        
        EditHeaderMailVariable pnl = new EditHeaderMailVariable( "MailHeader" );
        pnl.setObject(hmodel.get_object(row));
        pnl.setLabel(UserMain.Txt("Header_Variable"));
        GenericGlossyDlg dlg = new GenericGlossyDlg(null, true, pnl);

        pnl.addPropertyChangeListener("REBUILD", this);

        dlg.set_next_location(this);
        dlg.setVisible(true);
        if (pnl.isOkay())
        {
            MailHeaderVariable hmv = hmodel.get_object(row);
            MailHeaderVariable save_hmv = new MailHeaderVariable(hmv);
            hmodel.get_object(row).setVarName( pnl.getText() );
            hmodel.get_object(row).setFlags( pnl.getFlags() );

            ServerCall sql = UserMain.sqc().get_sqc();
            ConnectionID cid = sql.open();
            StatementID sta = sql.createStatement(cid);
            sql.Update(sta, hmv, save_hmv);

            propertyChange(new PropertyChangeEvent(this, "REBUILD", null, null));
        }
    }

    protected boolean del_hmv_object( int row )
    {
        Object hmv = hmodel.getSqlResult().get(row);

        ServerCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open();
        StatementID sta = sql.createStatement(cid);

        boolean okay = sql.Delete(sta, hmv);

        sql.close(sta);
        sql.close(cid);

        if (!okay)
        {
            String object_name = hmv.getClass().getSimpleName();
            UserMain.errm_ok(my_dlg, UserMain.Txt("Cannot_delete") + " " + object_name + " " + sql.get_last_err());
        }

        propertyChange(new PropertyChangeEvent(this, "REBUILD", null, null));

        return okay;
    }

    public void new_hmv_object()
    {
        boolean was_new = model.is_new(row);

        if (was_new)
        {
            boolean ok = save_action(object, save_object);
            if (!ok)
            {
                return;
            }
            // REBUILD MODEL AND SET VARS
            object_overview.gather_sql_result();
            model = object_overview.get_object_model();
            row = model.get_row_by_id( object.getId() );
            object = model.get_object(row);
        }
        int id = object.getId();

        ServerCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open();
        StatementID sta = sql.createStatement(cid);


          String ins_stmt = "insert into  mail_header_variable ( var_name,mid ) values ('',"+id+")";
          int rows = sql.executeUpdate(sta, ins_stmt);

        sql.close(sta);
        sql.close(cid);

        build_header_list( object );

        int hmv_row = hmodel.getRowCount() - 1;
        if (hmv_row >= 0)
        {
            edit_hmv_row(hmv_row);
        }
    }

    @Override
    public void propertyChange( PropertyChangeEvent evt )
    {
         build_header_list( object );
    }

    @Override
            public void mouseClicked( MouseEvent e )
            {
                Component c = table.getComponentAt(e.getPoint());
                int hrow = table.rowAtPoint(e.getPoint());
                int col = table.columnAtPoint(e.getPoint());

                if (col == hmodel.get_edit_column())
                {
                    edit_hmv_row(hrow);

                 }

                if (col == hmodel.get_del_column())
                {

                    String name = hmodel.getSqlResult().getString(hrow, "var_name");


                    String txt = UserMain.getString("Wollen_Sie_wirklich_diesen_Eintrag_loeschen");
                    if (name != null)
                    {
                        txt += ": <" + name + ">";
                    }

                    if (UserMain.errm_ok_cancel(txt + "?"))
                    {
                        boolean okay = del_hmv_object(hrow);

                        propertyChange(new PropertyChangeEvent(this, "REBUILD", null, null));
                    }
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

    @Override
    protected boolean save_action( Object o, Object so )
    {
        boolean ret =  super.save_action(o, so);

        if (ret)
        {
            UserMain.fcc().call_abstract_function("restart_mandant MA:" + object.getId(), ServerCall.SHORT_CMD_TO);
        }
        return ret;

    }
 
    private void startExport() {
        // TODO add your handling code here:
        ExportPanel pnl = new ExportPanel(object.getId());
        GenericGlossyDlg edlg = new GenericGlossyDlg(UserMain.self, true, pnl);
        edlg.set_next_location(my_dlg);
        edlg.setVisible(true);
    }



}
