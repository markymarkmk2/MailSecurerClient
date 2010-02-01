/*
 * EditChannelPanel.java
 *
 * Created on 25. M�rz 2008, 20:06
 */
package dimm.home.Panels;

import com.thoughtworks.xstream.XStream;
import dimm.home.Rendering.GlossButton;
import dimm.home.UserMain;
import javax.swing.JButton;
import home.shared.hibernate.AccountConnector;
import dimm.home.Models.DiskArchiveComboModel;
import dimm.home.Panels.MailView.GetUserMailPwdPanel;
import dimm.home.Rendering.EditTextList;
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.ServerConnect.ServerCall;
import dimm.home.Utilities.SwingWorker;
import home.shared.Utilities.Validator;
import home.shared.CS_Constants;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.swing.JFileChooser;
import home.shared.AccountConnectorTypeEntry;
import home.shared.filter.ExprEntry;
import home.shared.filter.VarTypeEntry;
import java.util.ArrayList;

/**

@author  Administrator
 */
public class EditAccountConnector extends GenericEditPanel
{

    AccountConnectorOverview object_overview;
    AccountConnectorTableModel model;
    AccountConnector object;
    DiskArchiveComboModel dacm;
    private String exclude_filter_save = null;
    
    /** Creates new form EditChannelPanel */
    public EditAccountConnector( int _row, AccountConnectorOverview _overview )
    {
        initComponents();

        object_overview = _overview;
        model = object_overview.get_object_model();
        CB_TYPE.removeAllItems();

        // FILL COMBOBOX TYPE
        for (int i = 0; i < CS_Constants.get_ac_list_count(); i++)
        {
            AccountConnectorTypeEntry mte = CS_Constants.get_ac(i);
            CB_TYPE.addItem(mte);
        }

        row = _row;

        if (!model.is_new(row))
        {
            try
            {
                object = model.get_object(row);


                TXT_SERVER.setText(object.getIp());
                TXT_PORT.setText(object.getPort().toString());
                TXT_USERNAME.setText(object.getUsername());
                TXTP_PWD.setText(object.getPwd());
                TXTP_PWD1.setText(object.getPwd());
                BT_DISABLED.setSelected(test_flag(CS_Constants.ACCT_DISABLED));

                if (test_flag( CS_Constants.ACCT_USE_SSL))
                    RB_SSL.setSelected(true);
                else if (test_flag( CS_Constants.ACCT_USE_TLS_IF_AVAIL))
                    RB_TLS_IV_AVAIL.setSelected(true);
                else if (test_flag( CS_Constants.ACCT_USE_TLS_FORCE))
                    RB_TLS_FORCE.setSelected(true);
                else
                    RB_INSECURE.setSelected(true);

                CB_USER_IS_EMAIL.setSelected(test_flag( CS_Constants.ACCT_USE_TLS_FORCE));


                CB_CERTIFICATE.setSelected(test_flag(CS_Constants.ACCT_HAS_TLS_CERT));
                if (CB_CERTIFICATE.isSelected())
                {
                    BT_IMPORT_CERT.setVisible(true);
                }
                if (object.getSearchbase() != null && object.getSearchbase().length() > 0)
                {
                    TXT_LDAP_SB.setText(object.getSearchbase());
                    CB_LDAP_SB.setSelected(true);
                }
                if (is_ldap())
                {
                    TXT_SEARCHFIELD.setText(object.getSearchattribute());
                    TXT_MAILFIELDS.setText(object.getMailattribute());
                }
                else if (CB_USER_IS_EMAIL.isSelected())
                {
                    TXT_USERDOMAIN.setText(object.getMailattribute());
                }
                
                String type = object.getType();
                for (int i = 0; i < CS_Constants.get_ac_list_count(); i++)
                {
                    AccountConnectorTypeEntry mte = CS_Constants.get_ac(i);
                    if (mte.getType().compareTo(type) == 0)
                    {
                        CB_TYPE.setSelectedIndex(i);
                        break;
                    }
                }
                TXT_DOMAINLIST.setText(object.getDomainlist());

                exclude_filter_save = object.getExcludefilter();
                if (exclude_filter_save == null)
                    exclude_filter_save = "";

                set_filter_preview( LogicFilter.get_nice_filter_text( exclude_filter_save, true ) );

            }
            catch (Exception exc)
            {
                UserMain.errm_ok(UserMain.getString("Fehler_beim_Lesen_der_Datenbankdaten"));
            }
        }
        else
        {
            object = new AccountConnector();
            exclude_filter_save = "";
            object.setMandant(UserMain.sqc().get_act_mandant());
            CB_TYPE.setSelectedIndex(0);
            TXT_SERVER.setText("127.0.0.1");
            TXT_PORT.setText( Integer.toString( get_dflt_port(CS_Constants.get_ac(0).getType(), false) ) );
            RB_INSECURE.setSelected(true);
            CB_CERTIFICATE.setSelected(false);
        }


    }

    public static int get_dflt_port( String type, boolean secure )
    {
        if (type.compareTo("smtp") == 0)
            return 25;
        if (type.compareTo("pop") == 0)
            return (secure ? 995 : 110);
        if (type.compareTo("imap") == 0)
            return (secure ? 993 : 143);
        if (type.compareTo("ldap") == 0 || type.compareTo("ad") == 0)
            return (secure ? 636 : 389);

        return 0;
    }
    boolean is_ldap()
    {
        AccountConnectorTypeEntry mte = (AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
        String type = mte.getType();
        return (type.compareTo("ldap") != 0);
    }
    boolean needs_user_auth(String type)
    {
        return (type.compareTo("ldap") != 0 && type.compareTo("ad") != 0);
    }

    /** This method is called from within the constructor to
    initialize the form.
    WARNING: Do NOT modify this code. The content of this method is
    always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        PN_ACTION = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        TXT_SERVER = new javax.swing.JTextField();
        LB_SERVER = new javax.swing.JLabel();
        CB_TYPE = new javax.swing.JComboBox();
        TXT_PORT = new javax.swing.JTextField();
        LB_PORT = new javax.swing.JLabel();
        LB_USER = new javax.swing.JLabel();
        TXTP_PWD = new javax.swing.JPasswordField();
        LB_PWD = new javax.swing.JLabel();
        TXT_USERNAME = new javax.swing.JTextField();
        jPanel1 = new javax.swing.JPanel();
        RB_INSECURE = new javax.swing.JRadioButton();
        RB_TLS_IV_AVAIL = new javax.swing.JRadioButton();
        RB_TLS_FORCE = new javax.swing.JRadioButton();
        RB_SSL = new javax.swing.JRadioButton();
        CB_CERTIFICATE = new javax.swing.JCheckBox();
        BT_IMPORT_CERT = new GlossButton();
        jLabel4 = new javax.swing.JLabel();
        TXT_DOMAINLIST = new javax.swing.JTextField();
        jPanel2 = new javax.swing.JPanel();
        LB_SEARCHFIELD = new javax.swing.JLabel();
        LB_MAILFIELDS = new javax.swing.JLabel();
        CB_LDAP_SB = new javax.swing.JCheckBox();
        TXT_LDAP_SB = new javax.swing.JTextField();
        TXT_MAILFIELDS = new javax.swing.JTextField();
        TXT_SEARCHFIELD = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        TXTA_AC_EXCLUDE = new javax.swing.JTextArea();
        CB_USER_IS_EMAIL = new javax.swing.JCheckBox();
        LB_DOMAINSUFFX = new javax.swing.JLabel();
        TXT_USERDOMAIN = new javax.swing.JTextField();
        LB_PWD1 = new javax.swing.JLabel();
        TXTP_PWD1 = new javax.swing.JPasswordField();
        BT_DISABLED = new javax.swing.JCheckBox();
        PN_BUTTONS = new javax.swing.JPanel();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();
        BT_TEST = new GlossButton();
        BT_EDIT_USERS = new GlossButton();

        setDoubleBuffered(false);
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        PN_ACTION.setDoubleBuffered(false);
        PN_ACTION.setOpaque(false);

        jLabel1.setText(UserMain.getString("Typ")); // NOI18N

        TXT_SERVER.setText(UserMain.Txt("Neuer_Server")); // NOI18N
        TXT_SERVER.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_SERVERMouseClicked(evt);
            }
        });
        TXT_SERVER.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TXT_SERVERActionPerformed(evt);
            }
        });

        LB_SERVER.setText(UserMain.getString("Server")); // NOI18N

        CB_TYPE.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        CB_TYPE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_TYPEActionPerformed(evt);
            }
        });

        TXT_PORT.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_PORTMouseClicked(evt);
            }
        });
        TXT_PORT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                TXT_PORTActionPerformed(evt);
            }
        });

        LB_PORT.setText(UserMain.getString("Port")); // NOI18N

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dimm/home/MA_Properties"); // NOI18N
        LB_USER.setText(bundle.getString("User")); // NOI18N

        LB_PWD.setText(bundle.getString("Password")); // NOI18N

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(UserMain.Txt("Security"))); // NOI18N

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

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(RB_TLS_IV_AVAIL)
                                .addComponent(RB_INSECURE, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                                .addComponent(RB_TLS_FORCE))
                            .addGap(63, 63, 63))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(RB_SSL)
                            .addContainerGap(124, Short.MAX_VALUE))
                        .addGroup(jPanel1Layout.createSequentialGroup()
                            .addComponent(CB_CERTIFICATE)
                            .addContainerGap(68, Short.MAX_VALUE)))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(BT_IMPORT_CERT)
                        .addContainerGap())))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(RB_INSECURE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RB_TLS_IV_AVAIL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RB_TLS_FORCE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(RB_SSL)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CB_CERTIFICATE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 13, Short.MAX_VALUE)
                .addComponent(BT_IMPORT_CERT)
                .addContainerGap())
        );

        jLabel4.setText(UserMain.getString("Domainlist")); // NOI18N

        TXT_DOMAINLIST.setEditable(false);
        TXT_DOMAINLIST.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_DOMAINLISTMouseClicked(evt);
            }
        });

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(UserMain.getString("LDAP/AD_Parameter"))); // NOI18N

        LB_SEARCHFIELD.setText(UserMain.getString("LDAP_User_Field")); // NOI18N

        LB_MAILFIELDS.setText(UserMain.getString("LDAP_Mail_Field")); // NOI18N

        CB_LDAP_SB.setText(UserMain.getString("LDAP-Searchbase")); // NOI18N
        CB_LDAP_SB.setOpaque(false);
        CB_LDAP_SB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_LDAP_SBActionPerformed(evt);
            }
        });

        TXT_MAILFIELDS.setEditable(false);
        TXT_MAILFIELDS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_MAILFIELDSMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(CB_LDAP_SB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TXT_LDAP_SB, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(LB_MAILFIELDS)
                            .addComponent(LB_SEARCHFIELD))
                        .addGap(39, 39, 39)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TXT_SEARCHFIELD, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)
                            .addComponent(TXT_MAILFIELDS, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE))))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_SEARCHFIELD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LB_SEARCHFIELD))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_MAILFIELDS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(LB_MAILFIELDS))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CB_LDAP_SB)
                    .addComponent(TXT_LDAP_SB, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(26, 26, 26))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(UserMain.getString("Options"))); // NOI18N

        jLabel2.setText(UserMain.getString("Exclude_Mail")); // NOI18N

        TXTA_AC_EXCLUDE.setColumns(20);
        TXTA_AC_EXCLUDE.setEditable(false);
        TXTA_AC_EXCLUDE.setRows(5);
        TXTA_AC_EXCLUDE.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXTA_AC_EXCLUDEMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(TXTA_AC_EXCLUDE);

        CB_USER_IS_EMAIL.setText(UserMain.getString("Username_is_eMail")); // NOI18N
        CB_USER_IS_EMAIL.setOpaque(false);
        CB_USER_IS_EMAIL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_USER_IS_EMAILActionPerformed(evt);
            }
        });

        LB_DOMAINSUFFX.setText(UserMain.getString("Domainsuffix")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(33, 33, 33)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(CB_USER_IS_EMAIL)
                        .addGap(18, 18, 18)
                        .addComponent(LB_DOMAINSUFFX)
                        .addGap(10, 10, 10)
                        .addComponent(TXT_USERDOMAIN, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CB_USER_IS_EMAIL)
                    .addComponent(LB_DOMAINSUFFX)
                    .addComponent(TXT_USERDOMAIN, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(14, Short.MAX_VALUE))
        );

        LB_PWD1.setText(bundle.getString("Repeat_password")); // NOI18N

        BT_DISABLED.setText(UserMain.getString("Gesperrt")); // NOI18N
        BT_DISABLED.setFocusable(false);
        BT_DISABLED.setOpaque(false);

        javax.swing.GroupLayout PN_ACTIONLayout = new javax.swing.GroupLayout(PN_ACTION);
        PN_ACTION.setLayout(PN_ACTIONLayout);
        PN_ACTIONLayout.setHorizontalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(70, 70, 70)
                                .addComponent(CB_TYPE, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(LB_PWD1, javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_ACTIONLayout.createSequentialGroup()
                                            .addComponent(jLabel4)
                                            .addGap(36, 36, 36)))
                                    .addComponent(LB_PWD)
                                    .addComponent(LB_SERVER)
                                    .addComponent(LB_USER))
                                .addGap(10, 10, 10)
                                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(TXT_DOMAINLIST, javax.swing.GroupLayout.DEFAULT_SIZE, 319, Short.MAX_VALUE)
                                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PN_ACTIONLayout.createSequentialGroup()
                                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                            .addComponent(TXTP_PWD1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                                            .addComponent(TXTP_PWD, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                                            .addComponent(TXT_USERNAME, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE)
                                            .addComponent(TXT_SERVER, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 183, Short.MAX_VALUE))
                                        .addGap(18, 18, 18)
                                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                                                .addComponent(LB_PORT)
                                                .addGap(12, 12, 12)
                                                .addComponent(TXT_PORT, javax.swing.GroupLayout.PREFERRED_SIZE, 86, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addComponent(BT_DISABLED, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                        .addGap(15, 15, 15)
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        PN_ACTIONLayout.setVerticalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(CB_TYPE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(BT_DISABLED))
                            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 14, Short.MAX_VALUE)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TXT_SERVER, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LB_SERVER)
                            .addComponent(TXT_PORT, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LB_PORT))
                        .addGap(18, 18, 18)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TXT_USERNAME, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LB_USER))
                        .addGap(8, 8, 8)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TXTP_PWD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LB_PWD))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TXTP_PWD1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LB_PWD1))
                        .addGap(11, 11, 11)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(TXT_DOMAINLIST, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 38, Short.MAX_VALUE))
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(PN_ACTION, gridBagConstraints);

        PN_BUTTONS.setDoubleBuffered(false);
        PN_BUTTONS.setOpaque(false);

        BT_OK.setText(UserMain.getString("Okay")); // NOI18N
        BT_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKActionPerformed(evt);
            }
        });

        BT_ABORT.setText(UserMain.getString("Abbruch")); // NOI18N
        BT_ABORT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ABORTActionPerformed(evt);
            }
        });

        BT_TEST.setText(bundle.getString("Test")); // NOI18N
        BT_TEST.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_TESTActionPerformed(evt);
            }
        });

        BT_EDIT_USERS.setText(UserMain.Txt("Edit_users_and_addresses")); // NOI18N
        BT_EDIT_USERS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_EDIT_USERSActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_BUTTONSLayout = new javax.swing.GroupLayout(PN_BUTTONS);
        PN_BUTTONS.setLayout(PN_BUTTONSLayout);
        PN_BUTTONSLayout.setHorizontalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BT_TEST)
                .addGap(18, 18, 18)
                .addComponent(BT_EDIT_USERS)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 182, Short.MAX_VALUE)
                .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        PN_BUTTONSLayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ABORT, BT_OK, BT_TEST});

        PN_BUTTONSLayout.setVerticalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_TEST, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_EDIT_USERS, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        add(PN_BUTTONS, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKActionPerformed
    {//GEN-HEADEREND:event_BT_OKActionPerformed
        // TODO add your handling code here:

        ok_action(object);

    }//GEN-LAST:event_BT_OKActionPerformed

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ABORTActionPerformed
    {//GEN-HEADEREND:event_BT_ABORTActionPerformed
        // TODO add your handling code here:
        abort_action();
    }//GEN-LAST:event_BT_ABORTActionPerformed

    private void TXT_SERVERMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXT_SERVERMouseClicked
    {//GEN-HEADEREND:event_TXT_SERVERMouseClicked
        // TODO add your handling code here:
        if (UserMain.self.is_touchscreen())
        {
            UserMain.self.show_vkeyboard(this.my_dlg, TXT_SERVER, false);
        }
}//GEN-LAST:event_TXT_SERVERMouseClicked

    private void TXT_SERVERActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TXT_SERVERActionPerformed
    {//GEN-HEADEREND:event_TXT_SERVERActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_TXT_SERVERActionPerformed

    private void TXT_PORTMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXT_PORTMouseClicked
    {//GEN-HEADEREND:event_TXT_PORTMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_TXT_PORTMouseClicked

    private void TXT_PORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TXT_PORTActionPerformed
    {//GEN-HEADEREND:event_TXT_PORTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_TXT_PORTActionPerformed
    SwingWorker sw;
    private void BT_TESTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_TESTActionPerformed
    {//GEN-HEADEREND:event_BT_TESTActionPerformed
        // TODO add your handling code here:

        AccountConnectorTypeEntry mte = (AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
        String type = mte.getType();
        String user_name = TXT_USERNAME.getText();
        String pwd = get_pwd();

        if (needs_user_auth(type))
        {
            GetUserMailPwdPanel pnl = new GetUserMailPwdPanel(  );
            pnl.enable_mail_list(false, null );
            pnl.enable_user(true, UserMain.self.get_act_username());
            pnl.enable_pwd(true, "");

            GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
            dlg.set_next_location( my_dlg );
            dlg.setVisible(true);
            if (!pnl.isOkay())
                return;

            user_name = pnl.get_user();
            pwd = pnl.get_pwd();
        }

        int flags = 0;
        if (RB_SSL.isSelected())
            flags |= CS_Constants.ACCT_USE_SSL;
        if (RB_TLS_FORCE.isSelected())
            flags |= CS_Constants.ACCT_USE_TLS_FORCE;
        if (RB_TLS_IV_AVAIL.isSelected())
            flags |= CS_Constants.ACCT_USE_TLS_IF_AVAIL;

        
        final String cmd = "TestLogin CMD:test MA:" + UserMain.self.get_act_mandant().getId() + " NM:'" + user_name + "' PW:'" + pwd + "' HO:" +
                TXT_SERVER.getText() + " PO:" + TXT_PORT.getText() + " SB:" + TXT_LDAP_SB.getText() +
                " TY:" + type + " FL:" + flags;

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
                    UserMain.errm_ok(my_dlg, UserMain.Txt("Login_failed") + ": " + ((ret != null) ?  ret.substring(3): ""));
                }
                sw = null;
                return null;
            }
        };

        sw.start();

    }//GEN-LAST:event_BT_TESTActionPerformed

    static File last_dir;
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


        XStream xs = new XStream();
        String cert_xml = xs.toXML(bb);
        
        final String cmd = "upload_certificate MA:" + object.getMandant().getId() + " TY:cacert + CERT:" + cert_xml;

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


    boolean needs_server_param(String type)
    {
        if (type.compareTo("dbs") == 0)
            return false;
        return true;
    }
    boolean needs_search_attr(String type)
    {
        if (type.compareTo("ldap") == 0 || type.compareTo("ad") == 0)
            return true;
        return false;
    }
    boolean needs_user_db(String type)
    {
        if (type.compareTo("dbs") == 0)
            return true;
        if (type.compareTo("ldap") == 0 || type.compareTo("ad") == 0)
            return false;

        if (CB_USER_IS_EMAIL.isSelected())
            return false;

        return true;
    }
    boolean needs_user_is_mail(String type)
    {
        // OPTION USER IS MAIL-ADRESS -> mark@dimm.de
        if (type.compareTo("ldap") == 0 || type.compareTo("ad") == 0 || type.compareTo("dbs") == 0)
        {
            return false;
        }
        return true;
    }


    private void CB_TYPEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_TYPEActionPerformed
    {//GEN-HEADEREND:event_CB_TYPEActionPerformed
        // TODO add your handling code here:
        AccountConnectorTypeEntry mte = (AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
        if (mte == null)
            return;
        String type = mte.getType();
        boolean user_visible = needs_user_auth(type);
        boolean server_visible = needs_server_param(type);
        boolean search_attr_visible = needs_search_attr(type);
        boolean user_db_visible = needs_user_db(type);
        boolean user_is_mail = needs_user_is_mail(type);



        CB_LDAP_SB.setVisible(search_attr_visible);
        TXT_LDAP_SB.setVisible(search_attr_visible);
        TXT_SEARCHFIELD.setVisible(search_attr_visible);
        TXT_MAILFIELDS.setVisible(search_attr_visible);
        LB_SEARCHFIELD.setVisible(search_attr_visible);
        LB_MAILFIELDS.setVisible(search_attr_visible);


        // OPTION USER IS MAIL-ADRESS -> mark@dimm.de
        if (!user_is_mail)
        {
            CB_USER_IS_EMAIL.setSelected(false);
        }
        CB_USER_IS_EMAIL.setVisible(user_db_visible);
        CB_USER_IS_EMAILActionPerformed(null);


        
        // WE SHOW OUR USER DB ONLY IF NOT LDAP
        BT_EDIT_USERS.setVisible(user_db_visible);
        
        TXT_USERNAME.setVisible(user_visible);
        TXTP_PWD.setVisible(user_visible);
        LB_USER.setVisible(user_visible);
        LB_PWD.setVisible(user_visible);

        
        TXT_SERVER.setVisible(server_visible);
        TXT_PORT.setVisible(server_visible);
        LB_SERVER.setVisible(server_visible);
        LB_PORT.setVisible(server_visible);


        if (my_dlg != null)
            my_dlg.pack();


    }//GEN-LAST:event_CB_TYPEActionPerformed

    private void CB_CERTIFICATEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_CERTIFICATEActionPerformed
    {//GEN-HEADEREND:event_CB_CERTIFICATEActionPerformed
        // TODO add your handling code here:
        BT_IMPORT_CERT.setVisible(CB_CERTIFICATE.isSelected());
    }//GEN-LAST:event_CB_CERTIFICATEActionPerformed

    private void RB_INSECUREActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RB_INSECUREActionPerformed
    {//GEN-HEADEREND:event_RB_INSECUREActionPerformed
        // TODO add your handling code here:
        AccountConnectorTypeEntry mte = (AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
        String type = mte.getType();
        TXT_PORT.setText( "" + get_dflt_port( type, false ) );
    }//GEN-LAST:event_RB_INSECUREActionPerformed

    private void RB_TLS_IV_AVAILActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RB_TLS_IV_AVAILActionPerformed
    {//GEN-HEADEREND:event_RB_TLS_IV_AVAILActionPerformed
        // TODO add your handling code here:
        AccountConnectorTypeEntry mte = (AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
        String type = mte.getType();
        TXT_PORT.setText( "" + get_dflt_port( type, false ) );

    }//GEN-LAST:event_RB_TLS_IV_AVAILActionPerformed

    private void RB_TLS_FORCEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RB_TLS_FORCEActionPerformed
    {//GEN-HEADEREND:event_RB_TLS_FORCEActionPerformed
        // TODO add your handling code here:
        AccountConnectorTypeEntry mte = (AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
        String type = mte.getType();
        TXT_PORT.setText( "" + get_dflt_port( type, false ) );

    }//GEN-LAST:event_RB_TLS_FORCEActionPerformed

    private void RB_SSLActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_RB_SSLActionPerformed
    {//GEN-HEADEREND:event_RB_SSLActionPerformed
        // TODO add your handling code here:
        AccountConnectorTypeEntry mte = (AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
        String type = mte.getType();
        TXT_PORT.setText( "" + get_dflt_port( type, true ) );

    }//GEN-LAST:event_RB_SSLActionPerformed

    private void BT_EDIT_USERSActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_EDIT_USERSActionPerformed
    {//GEN-HEADEREND:event_BT_EDIT_USERSActionPerformed
        // TODO add your handling code here:
        boolean _is_new = is_new();

        // IF WE WANT TO INSERT DISKSPACE IN A NEW OBJECT , WE HAVE TO SAVE FIRST
        if (_is_new)
        {
            // IN CASE OF ERROR -> LEAVE, MESSAGE WAS ALREADY SHOWN
            if (!save_action(object) || model.getRowCount() <= 0)
                return;

            // NOW THE LAST OBJECT IN OVERVIEWLIST IS OUR NEW OBJECT, OBJECTS ARE ORDERED BY ID
            int size = model.getRowCount();
            AccountConnector new_object = model.get_object(size - 1);
            if (new_object.getUsername().compareTo(object.getUsername()) != 0)
            {
                return;
            }

            // SET INTERNAL VARS
            object = new_object;
            row = size - 1;
        }

        MailUserOverview dlg = new MailUserOverview(UserMain.self, object, true);
        dlg.pack();

        dlg.setLocation(this.getLocationOnScreen().x + 30, this.getLocationOnScreen().y + 30);
        dlg.setTitle(UserMain.Txt("MailUser"));
        dlg.setVisible(true);


    }//GEN-LAST:event_BT_EDIT_USERSActionPerformed

    private void CB_LDAP_SBActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_LDAP_SBActionPerformed
    {//GEN-HEADEREND:event_CB_LDAP_SBActionPerformed
        // TODO add your handling code here:
        if (!CB_LDAP_SB.isSelected())
        {
            TXT_LDAP_SB.setText("");
            TXT_LDAP_SB.setEnabled(false);
        }
        else
        {
            TXT_LDAP_SB.setText(object.getSearchbase());
            TXT_LDAP_SB.setEnabled(false);
        }
        if (my_dlg != null)
            my_dlg.pack();
    }//GEN-LAST:event_CB_LDAP_SBActionPerformed

    private void CB_USER_IS_EMAILActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_USER_IS_EMAILActionPerformed
    {//GEN-HEADEREND:event_CB_USER_IS_EMAILActionPerformed
        // TODO add your handling code here:
        TXT_USERDOMAIN.setVisible(CB_USER_IS_EMAIL.isSelected());
        LB_DOMAINSUFFX.setVisible(CB_USER_IS_EMAIL.isSelected());
        
        if (my_dlg != null)
            my_dlg.pack();
    }//GEN-LAST:event_CB_USER_IS_EMAILActionPerformed

    private void TXT_MAILFIELDSMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXT_MAILFIELDSMouseClicked
    {//GEN-HEADEREND:event_TXT_MAILFIELDSMouseClicked
        // TODO add your handling code here:
        EditTextList etl = new EditTextList( TXT_MAILFIELDS.getText(), CS_Constants.TEXTLIST_DELIM);
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, etl);
        dlg.setTitle(UserMain.Txt("Fields_which_contain_emailaddress"));
        dlg.setLocation(TXT_MAILFIELDS.getLocationOnScreen());
        dlg.setVisible(true);
        if (etl.isOkay())
        {
            TXT_MAILFIELDS.setText(etl.get_text());
        }

    }//GEN-LAST:event_TXT_MAILFIELDSMouseClicked

    private void TXT_DOMAINLISTMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXT_DOMAINLISTMouseClicked
    {//GEN-HEADEREND:event_TXT_DOMAINLISTMouseClicked
        // TODO add your handling code here:
        EditTextList etl = new EditTextList( TXT_DOMAINLIST.getText(), CS_Constants.TEXTLIST_DELIM);
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, etl);
        dlg.setTitle(UserMain.Txt("List_of_domains_to_archive"));
        dlg.setLocation(TXT_DOMAINLIST.getLocationOnScreen());
        dlg.setVisible(true);
        if (etl.isOkay())
        {
            TXT_DOMAINLIST.setText(etl.get_text());
        }

    }//GEN-LAST:event_TXT_DOMAINLISTMouseClicked

    private void TXTA_AC_EXCLUDEMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXTA_AC_EXCLUDEMouseClicked
    {//GEN-HEADEREND:event_TXTA_AC_EXCLUDEMouseClicked
        // TODO add your handling code here:
        edit_account_filter();

    }//GEN-LAST:event_TXTA_AC_EXCLUDEMouseClicked

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JCheckBox BT_DISABLED;
    private javax.swing.JButton BT_EDIT_USERS;
    private javax.swing.JButton BT_IMPORT_CERT;
    private javax.swing.JButton BT_OK;
    private javax.swing.JButton BT_TEST;
    private javax.swing.JCheckBox CB_CERTIFICATE;
    private javax.swing.JCheckBox CB_LDAP_SB;
    private javax.swing.JComboBox CB_TYPE;
    private javax.swing.JCheckBox CB_USER_IS_EMAIL;
    private javax.swing.JLabel LB_DOMAINSUFFX;
    private javax.swing.JLabel LB_MAILFIELDS;
    private javax.swing.JLabel LB_PORT;
    private javax.swing.JLabel LB_PWD;
    private javax.swing.JLabel LB_PWD1;
    private javax.swing.JLabel LB_SEARCHFIELD;
    private javax.swing.JLabel LB_SERVER;
    private javax.swing.JLabel LB_USER;
    private javax.swing.JPanel PN_ACTION;
    private javax.swing.JPanel PN_BUTTONS;
    private javax.swing.JRadioButton RB_INSECURE;
    private javax.swing.JRadioButton RB_SSL;
    private javax.swing.JRadioButton RB_TLS_FORCE;
    private javax.swing.JRadioButton RB_TLS_IV_AVAIL;
    private javax.swing.JTextArea TXTA_AC_EXCLUDE;
    private javax.swing.JPasswordField TXTP_PWD;
    private javax.swing.JPasswordField TXTP_PWD1;
    private javax.swing.JTextField TXT_DOMAINLIST;
    private javax.swing.JTextField TXT_LDAP_SB;
    private javax.swing.JTextField TXT_MAILFIELDS;
    private javax.swing.JTextField TXT_PORT;
    private javax.swing.JTextField TXT_SEARCHFIELD;
    private javax.swing.JTextField TXT_SERVER;
    private javax.swing.JTextField TXT_USERDOMAIN;
    private javax.swing.JTextField TXT_USERNAME;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    int get_object_flags()
    {
        return object.getFlags();

    }

    void set_object_flag( int flag )
    {
        int flags = get_object_flags();
        flags |= flag;
        object.setFlags(flags);
    }

    void clr_object_flag( int flag )
    {
        int flags = get_object_flags();
        flags &= ~flag;
        object.setFlags(flags);
    }
/*
    boolean object_is_disabled()
    {
        int flags = 0;

        flags = get_object_flags();

        return ((flags & CS_Constants.ACCT_DISABLED) == CS_Constants.ACCT_DISABLED);
    }
*/
    boolean test_flag( int test_flag )
    {
        int flags = get_object_flags();
        return ((flags & test_flag) == test_flag);
    }
    void set_flag( boolean state, int flag )
    {
        if (state)
        {
            set_object_flag(flag);
        }
        else
        {
            clr_object_flag(flag);
        }
    }
    
    String get_pwd()
    {
        char[] pwd = TXTP_PWD.getPassword();
        return new String(pwd);
    }

    @Override
    protected boolean check_changed()
    {
        if (model.is_new(row))
        {
            return true;
        }

        String server = object.getIp();
        if (server != null && TXT_SERVER.getText().compareTo(server) != 0)
        {
            return true;
        }

        if (object.getPort() == null)
        {
            return true;
        }

        int port = object.getPort();
        if (Integer.parseInt(TXT_PORT.getText()) != port)
        {
            return true;
        }


        if (BT_DISABLED.isSelected() != test_flag(CS_Constants.ACCT_DISABLED))
        {
            return true;
        }

        if (CB_CERTIFICATE.isSelected() != test_flag(CS_Constants.ACCT_HAS_TLS_CERT))
        {
            return true;
        }
        if (RB_SSL.isSelected() != test_flag(CS_Constants.ACCT_USE_SSL))
        {
            return true;
        }
        if (RB_TLS_FORCE.isSelected() != test_flag(CS_Constants.ACCT_USE_TLS_FORCE))
        {
            return true;
        }
        if (RB_TLS_IV_AVAIL.isSelected() != test_flag(CS_Constants.ACCT_USE_TLS_IF_AVAIL))
        {
            return true;
        }

        String user = object.getUsername();
        if (user != null && TXT_USERNAME.getText().compareTo(user) != 0)
        {
            return true;
        }

        String pwd = object.getPwd();
        if (pwd != null && get_pwd().compareTo(pwd) != 0)
        {
            return true;
        }

        AccountConnectorTypeEntry mte = (AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
        String typ = mte.getType();
        if (object.getType() != null && object.getType().compareTo(typ) != 0)
        {
            return true;
        }

        if (object.getMailattribute() != null && object.getMailattribute().compareTo(TXT_MAILFIELDS.getText()) != 0)
            return true;

        if (object.getDomainlist() != null && TXT_DOMAINLIST.getText().compareTo(object.getDomainlist()) != 0)
            return true;

        if (object.getExcludefilter() != null && object.getExcludefilter().compareTo(exclude_filter_save) != 0)
            return true;

        return false;
    }

    @Override
    protected boolean is_plausible()
    {
        if (!Validator.is_valid_name(TXT_SERVER.getText(), 255))
        {
            UserMain.errm_ok(UserMain.getString("Der_Server_ist_nicht_okay"));
            return false;
        }
        if (!Validator.is_valid_port(TXT_PORT.getText()))
        {
            UserMain.errm_ok(UserMain.getString("Port_ist_nicht_okay"));
            return false;
        }
        AccountConnectorTypeEntry mte;

        try
        {
            mte = (AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
            String name = mte.getName();
        }
        catch (Exception e)
        {
            UserMain.errm_ok(UserMain.getString("Typ_ist_nicht_okay"));
            return false;
        }

        
        String type = mte.getType();
        if (needs_user_auth(type))
        {

            if (!Validator.is_valid_name(TXT_USERNAME.getText(), 80))
            {
                if (TXT_USERNAME.getText().length() == 0)
                {
                    if (!UserMain.errm_ok_cancel(UserMain.getString("Wollen_Sie_einen_anonymen_Login?")))
                        return false;

                }
                else
                {
                    UserMain.errm_ok(UserMain.getString("Der_User_ist_nicht_okay"));
                    return false;
                }
            }
            if (TXT_USERNAME.getText().length() > 0 && (get_pwd().length() == 0 || get_pwd().length() > 80))
            {
                UserMain.errm_ok(UserMain.getString("Das_Passwort_ist_nicht_okay"));
                return false;
            }
            if (CB_LDAP_SB.isSelected() && TXT_LDAP_SB.getText().length() == 0)
            {
                UserMain.errm_ok(UserMain.getString("Die_LDAP-Suchbasis_ist_nicht_okay"));
                return false;
            }

        }
        if (CB_CERTIFICATE.isSelected())
        {
            if (RB_INSECURE.isSelected())
            {
                UserMain.errm_ok(UserMain.getString("Zertifikate_werden_nur_sicheren_Verbindungen_verwendet"));
                return false;
            }
        }
        if (TXT_DOMAINLIST.getText().length() == 0)
        {
            UserMain.errm_ok(UserMain.getString("Sie_müssen_mindestens_eine_zu_archivierende_Domain_angeben"));
            return false;
        }

        return true;
    }

    @Override
    protected void set_object_props()
    {
        String server1 = TXT_SERVER.getText();
        int port1 = Integer.parseInt(TXT_PORT.getText());
        
        String name = TXT_USERNAME.getText();
        String pwd = get_pwd();
        String type = "";
        String ldap_sb_text = "";
        String mail_attribute = "";
        String search_attribute = "";

        AccountConnectorTypeEntry mte = (AccountConnectorTypeEntry) CB_TYPE.getSelectedItem();
        type = mte.getType();

        object.setFlags(0);
        object.setIp(server1);
        object.setPort(new Integer(port1));
        
        set_flag( BT_DISABLED.isSelected(), CS_Constants.ACCT_DISABLED);
        if (RB_TLS_IV_AVAIL.isSelected())
            set_flag(true, CS_Constants.ACCT_USE_TLS_IF_AVAIL);
        if (RB_TLS_FORCE.isSelected())
            set_flag(true, CS_Constants.ACCT_USE_TLS_FORCE);
        if (RB_SSL.isSelected())
            set_flag(true, CS_Constants.ACCT_USE_SSL);

        if (CB_USER_IS_EMAIL.isSelected())
        {
            set_flag(true, CS_Constants.ACCT_USER_IS_MAIL);
            object.setMailattribute(TXT_USERDOMAIN.getText());
        }
        else
        {
            object.setMailattribute("");
        }


        if (type.compareTo("ldap") == 0)
        {
            if (CB_LDAP_SB.isSelected())
            {
                ldap_sb_text = TXT_LDAP_SB.getText();
            }
            search_attribute = TXT_SEARCHFIELD.getText();
            mail_attribute = TXT_MAILFIELDS.getText();
        }


        object.setSearchbase(ldap_sb_text);
        object.setSearchattribute(search_attribute);
        object.setMailattribute(mail_attribute);
        object.setType(type);
        object.setUsername(name);
        object.setPwd(pwd);
        object.setDomainlist(TXT_DOMAINLIST.getText());
        
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
     private void set_filter_preview( String _nice_filter_text )
    {
        TXTA_AC_EXCLUDE.setText(_nice_filter_text);
        TXTA_AC_EXCLUDE.setCaretPosition(0);

    }
    private void edit_account_filter()
    {
        try
        {
            ArrayList<VarTypeEntry> var_names = new ArrayList<VarTypeEntry>();
            var_names.add(new VarTypeEntry("Email", ExprEntry.TYPE.STRING) );
            var_names.add(new VarTypeEntry("Subject", ExprEntry.TYPE.STRING) );
            var_names.add(new VarTypeEntry("Mailheader", ExprEntry.TYPE.STRING) );


            boolean compressed = true;
            LogicFilter rf = new LogicFilter(var_names, object.getExcludefilter(), compressed );

            GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, rf);
            dlg.setVisible(true);

            if (rf.isOkay())
            {
                 String role_filter_xml = rf.get_compressed_xml_list_data(compressed);
                 object.setExcludefilter(role_filter_xml);
                 set_filter_preview( LogicFilter.get_nice_filter_text( role_filter_xml, compressed ) );
            }
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
    }

}
