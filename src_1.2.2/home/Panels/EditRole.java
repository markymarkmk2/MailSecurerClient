/*
 * EditChannelPanel.java
 *
 * Created on 25. M�rz 2008, 20:06
 */

package dimm.home.Panels;

import home.shared.SQL.SQLResult;
import dimm.home.Models.AccountConnectorComboModel;
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossTable;
import dimm.home.ServerConnect.ConnectionID;
import dimm.home.ServerConnect.ResultSetID;
import dimm.home.ServerConnect.ServerCall;
import dimm.home.ServerConnect.StatementID;
import dimm.home.UserMain;
import dimm.home.Utilities.SwingWorker;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.JButton;
import home.shared.hibernate.Role;
import home.shared.Utilities.Validator;
import home.shared.CS_Constants;
import home.shared.SQL.OptCBEntry;
import home.shared.SQL.SQLArrayResult;
import home.shared.Utilities.ParseToken;
import home.shared.filter.ExprEntry;
import home.shared.filter.ExprEntry.OPERATION;
import home.shared.filter.ExprEntry.TYPE;
import home.shared.filter.GroupEntry;
import home.shared.filter.LogicEntry;
import home.shared.filter.VarTypeEntry;
import home.shared.hibernate.AccountConnector;
import home.shared.hibernate.RoleOption;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javax.swing.DefaultCellEditor;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;



class SimpleSearchEntryModel extends GroupEntry
{
    SimpleSearchEntryModel(ArrayList<LogicEntry> list)
    {
        if (list != null)
            children = list;
    }
}
class ConditionCBEntry
{
    ExprEntry entry;

    public ConditionCBEntry( ExprEntry entry )
    {
        this.entry = entry;
    }

    static String get_nice_txt( ExprEntry e )
    {
        return (e.isNeg() ? UserMain.Txt("not") + " " : "") + UserMain.Txt(e.getName()) + " " + LogicFilter.get_op_nice_txt( e.getOperation(), e.getType());
    }
    @Override
    public String toString()
    {
        return get_nice_txt(entry);
    }
}
class NegCBEntry
{
    boolean neg;

    public NegCBEntry( boolean _neg )
    {
        this.neg = _neg;
    }

    public boolean isNeg()
    {
        return neg;
    }

    static String get_nice_txt( boolean n )
    {
        return n ? UserMain.Txt("not") + " " : "";
    }
    @Override
    public String toString()
    {
        return get_nice_txt(neg);
    }
}
class SimpleSearchTableModel extends AbstractTableModel implements MouseListener
{
    static int DELETE_COL = 3;

    EditRole pnl;


    SimpleDateFormat sdf;
    JButton ic_delete;

    SimpleSearchEntryModel model;

    SimpleSearchTableModel(EditRole _pnl,  SimpleSearchEntryModel _model)
    {
        super();

        pnl = _pnl;
        model = _model;

        sdf = new SimpleDateFormat("dd.MM.yyyy  HH:mm");

        ic_delete = GlossTable.create_table_button("/dimm/home/images/web_delete.png");
    }

    void set_model(SimpleSearchEntryModel _model)
    {
        model = _model;
        this.fireTableDataChanged();
    }
    public String get_compressed_xml_list_data()
    {
        ArrayList<LogicEntry> al = new ArrayList<LogicEntry>();

        // ADD ONLY VALID ENTRIES
        for (int i = 0; i < model.getChildren().size(); i++)
        {
            LogicEntry logicEntry = model.getChildren().get(i);
            if (logicEntry instanceof ExprEntry)
            {
                ExprEntry e = (ExprEntry)logicEntry;
                if (e.getValue().length() > 0)
                {
                    ExprEntry ee = new ExprEntry( al, e.getName(), e.getValue(), e.getOperation(), e.getType(), e.isNeg(), e.isPrevious_is_or() );
                    // ALL ARE ADDED AS "AND"
                    al.add(ee);
                }
            }
        }

        String compressed_list_str = ParseToken.BuildCompressedObjectString(al);
        
        return compressed_list_str;
    }


    @Override
    public boolean isCellEditable( int row, int column )
    {
        if (column < DELETE_COL)
            return true;
        return false;

    }

    @Override
    public String getColumnName(int column)
    {
        switch( column )
        {
            case 0: return UserMain.Txt("Condition");
            case 1: return "";
            case 2: return UserMain.Txt("Value");
        }
        return "";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        if (columnIndex == DELETE_COL)
            return JButton.class;

        return String.class;
    }

    @Override
    public int getRowCount()
    {
        if (model == null)
            return 0;
        return model.getChildren().size();
    }

    @Override
    public int getColumnCount()
    {
        return 4;
    }




    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        LogicEntry le = model.getChildren().get(rowIndex);
        if (le instanceof ExprEntry)
        {
            ExprEntry ee = (ExprEntry) le;
            if (columnIndex == 0)
            {
                String txt = ConditionCBEntry.get_nice_txt( ee );
                return txt;
            }
            if (columnIndex == 1)
            {
                String txt = NegCBEntry.get_nice_txt( ee.isNeg() );
                return txt;
            }
            if (columnIndex == 2)
            {
                String txt = ee.getValue();
                return txt;
            }
            if (columnIndex == DELETE_COL)
            {
                return ic_delete;
            }
        }
        return "";
    }

    @Override
    public void setValueAt( Object aValue, int rowIndex, int columnIndex )
    {
        LogicEntry le = model.getChildren().get(rowIndex);
        if (le instanceof ExprEntry)
        {
            ExprEntry ee = (ExprEntry) le;
            if (columnIndex == 0 && aValue instanceof ConditionCBEntry)
            {
                ConditionCBEntry ccbe = (ConditionCBEntry)aValue;
                ee.setOperation( ccbe.entry.getOperation() );
                ee.setType( ccbe.entry.getType());
                ee.setName( ccbe.entry.getName());
                ee.setNeg( ccbe.entry.isNeg());
                ee.setPrevious_is_or( ccbe.entry.isPrevious_is_or() );
            }
            if (columnIndex == 1&& aValue instanceof NegCBEntry)
            {
                NegCBEntry ncb = (NegCBEntry)aValue;
                ee.setNeg( ncb.isNeg() );
            }
            if (columnIndex == 2)
            {
                ee.setValue(aValue.toString());
            }
        }
    }


    @Override
    public void mouseClicked( MouseEvent e )
    {
        if (e.getClickCount() == 1 && e.getSource() instanceof JTable)
        {
            JTable tb = (JTable)e.getSource();
            int row = tb.rowAtPoint(e.getPoint());
            int col = tb.columnAtPoint(e.getPoint());
            if (col == DELETE_COL && row >= 0 && row < model.getChildren().size())
            {
                model.getChildren().remove(model.getChildren().get(row));
                fireTableDataChanged();
            }
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
}
class SimpleSearchEditor extends  DefaultCellEditor
{
    public SimpleSearchEditor( JComboBox cb )
    {
        super( cb );
    }
}


/**
 
 @author  Administrator
 */
public final class EditRole extends GenericEditPanel
{
    RoleOverview vbox_overview;
    RoleTableModel model;
    Role object;
    Role save_object;
    
    String object_name;

    AccountConnectorComboModel accm;
    
    SQLResult<RoleOption>  option_res;
    String role_filter_save;
    int role_flags_save;
    boolean was_4eyes;

    GlossTable simple_search_list;
    private static final int SIMPLE_SEARCH = 0;
    private static final int COMPLEX_SEARCH = 1;

    GlossTable simple_search_table;
    SimpleSearchTableModel simple_search_tablemodel;
    TableCellEditor simple_condition_editor;
    TableCellEditor simple_val_editor;

    public static final double DFLT_DIV_POS = 0.3;
    private static int SIMPLE_TAB_COL_CONDITION = 0;
    private static int SIMPLE_TAB_COL_NEG = 1;
    private static int SIMPLE_TAB_COL_VALUE = 2;
    private static int SIMPLE_TAB_COL_DELETE = 3;




    
    /** Creates new form EditChannelPanel */
    public EditRole(int _row, RoleOverview _overview)
    {
        initComponents();     
        
        vbox_overview = _overview;
        model = vbox_overview.get_object_model();

        create_option_buttons();

        SQLResult<AccountConnector> da_res = UserMain.sqc().get_account_result();

        // COMBO-MODEL 
        accm = new AccountConnectorComboModel(da_res );
                                
        row = _row;

        PNL_4EYES.setVisible(false);

        create_simple_search_table();
        
        if (!model.is_new(row))
        {
            object = model.get_object(row);
            save_object = new Role( object );

            TXT_NAME.setText(object.getName());
            String acm_text = vbox_overview.get_account_match_descr(object.getAccountmatch());
            
            TXTA_FILTER.setText( acm_text );

            
            read_opts_buttons( object.getId() );
            TXT_4EYES_USER.setText(object.getUser4eyes());
            TXTP_4EYES_PWD.setText(object.getPwd4eyes());
            
            if (BT_4EYES.isSelected())
            {
                was_4eyes = true;
                PNL_4EYES.setVisible(true);
            }
            BT_DISABLED.setSelected( object_is_disabled() );
            int ac_id = model.getSqlResult().getInt( row, "ac_id");
            accm.set_act_id(ac_id);

            role_filter_save = object.getAccountmatch();

            if (test_flag(CS_Constants.ROLE_ACM_SIMPLE))
            {
                TBP_SEARCH.setSelectedIndex(SIMPLE_SEARCH);
                set_simple_search_table_model(role_filter_save);
            }
            else
            {
                TBP_SEARCH.setSelectedIndex(COMPLEX_SEARCH);
                set_filter_preview( LogicFilter.get_nice_filter_text( role_filter_save ) );
            }

        }
        else
        {
            object = new Role();
            object.setMandant(UserMain.sqc().get_act_mandant());
            role_filter_save = "";
            set_flag(CS_Constants.ROLE_ACM_COMPRESSED);
        }

        CB_ACCOUNT.setModel(accm);
        object_name = object.getClass().getSimpleName();

        set_tbp_callback();

    }

    void set_tbp_callback()
    {
        TBP_SEARCH.addChangeListener( new ChangeListener() {

            @Override
            public void stateChanged( ChangeEvent e )
            {
                if (TBP_SEARCH.getSelectedIndex() == SIMPLE_SEARCH)
                {
                    set_flag(CS_Constants.ROLE_ACM_SIMPLE);
                     set_new_filter_vals(  simple_search_tablemodel.get_compressed_xml_list_data() );
                }
                else
                {
                    clr_flag(CS_Constants.ROLE_ACM_SIMPLE);
                    set_new_filter_vals( object.getAccountmatch() );
                }

            }
        });
    }

    void set_simple_search_table_model(String match)
    {
        if (test_flag(CS_Constants.ROLE_ACM_SIMPLE))
        {
            ArrayList<LogicEntry> list = LogicFilter.get_filter_list( match );
            SimpleSearchEntryModel sse_model = new SimpleSearchEntryModel( list );
            simple_search_tablemodel.set_model(sse_model);
        }
        else
        {
            SimpleSearchEntryModel sse_model = new SimpleSearchEntryModel( null );
            simple_search_tablemodel.set_model(sse_model);
        }
        //simple_search_tablemodel.fireTableDataChanged();
        simple_search_table.tableChanged( new TableModelEvent(simple_search_tablemodel));
    }

    JComboBox CB_CONDITION;
    
    final void create_simple_search_table()
    {
        simple_search_table = new GlossTable(true);
        simple_search_table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        simple_search_table.embed_to_scrollpanel( SCP_LIST );

        simple_search_tablemodel = new SimpleSearchTableModel(this, new SimpleSearchEntryModel(null));

        simple_search_table.setModel(simple_search_tablemodel);
        simple_search_table.addMouseListener(simple_search_tablemodel);

        CB_CONDITION = new JComboBox();
        CB_CONDITION.removeAllItems();
        
           
        CB_CONDITION.addItem( new ConditionCBEntry( new ExprEntry(null, "Username", "", OPERATION.CONTAINS, TYPE.STRING, false, false)) );
        CB_CONDITION.addItem( new ConditionCBEntry( new ExprEntry(null, "Email", "", OPERATION.CONTAINS, TYPE.STRING, false, false)) );
        CB_CONDITION.addItem( new ConditionCBEntry( new ExprEntry(null, "Domain", "", OPERATION.CONTAINS, TYPE.STRING, false, false)) );
        CB_CONDITION.addItem( new ConditionCBEntry( new ExprEntry(null, "Group", "", OPERATION.CONTAINS, TYPE.STRING, false, false)) );
        

        JComboBox CB_NEG = new JComboBox();
        CB_NEG.addItem( new NegCBEntry(false));
        CB_NEG.addItem( new NegCBEntry(true));

        JTextField TXT_VAL = new JTextField();
        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_NEG).setMinWidth(30);
        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_NEG).setMaxWidth(30);
        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_DELETE).setMinWidth(30);
        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_DELETE).setMaxWidth(30);
        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_CONDITION).setCellEditor( new DefaultCellEditor(  CB_CONDITION ) );
        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_NEG).setCellEditor( new DefaultCellEditor(  CB_NEG ) );
        DefaultCellEditor txt_editor = new DefaultCellEditor( TXT_VAL);
        txt_editor.setClickCountToStart(1);
        
        TXT_VAL.addActionListener( new ActionListener()
        {

            @Override
            public void actionPerformed( ActionEvent e )
            {
                simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_VALUE).getCellEditor().stopCellEditing();                
            }
        });

        simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_VALUE).setCellEditor( txt_editor  );

        TBP_SEARCH.setSelectedIndex(SIMPLE_SEARCH);
    }

   


    final void create_option_buttons()
    {
        javax.swing.GroupLayout PN_OPTSLayout = new javax.swing.GroupLayout(PN_OPTS);
        PN_OPTS.setLayout(PN_OPTSLayout);

        ParallelGroup parallel_group = PN_OPTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING);
        SequentialGroup seq_group = PN_OPTSLayout.createSequentialGroup();

        for (int i = 0; i < OptCBEntry.opt_list.length; i++)
        {
            OptCBEntry optCBEntry = OptCBEntry.opt_list[i];

            // 4-EYES IS ALREADY FIXED INCLUDED (BECAUSE OF UER/PWD)
            if (optCBEntry.getTxt().compareTo(OptCBEntry._4EYES) == 0)
            {
                optCBEntry.setCb(BT_4EYES);
                continue;
            }
            
            JCheckBox cb = new javax.swing.JCheckBox();

            optCBEntry.setTxt(UserMain.Txt(optCBEntry.getToken()));

            cb.setText(optCBEntry.getTxt());
            optCBEntry.setCb(cb);

            parallel_group.addComponent(cb);
            seq_group.addComponent(cb);
            if (i +1 == OptCBEntry.opt_list.length)
                seq_group.addContainerGap(26, Short.MAX_VALUE);
            else
                seq_group.addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED);

        }

        PN_OPTSLayout.setHorizontalGroup(
            PN_OPTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_OPTSLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup( parallel_group )
                .addContainerGap(380, Short.MAX_VALUE))
        );
        PN_OPTSLayout.setVerticalGroup(
            PN_OPTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup( seq_group )
        );

    }
    
    
   
    /** This method is called from within the constructor to
     initialize the form.
     WARNING: Do NOT modify this code. The content of this method is
     always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        PN_ACTION = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        TXT_NAME = new javax.swing.JTextField();
        BT_DISABLED = new javax.swing.JCheckBox();
        PN_OPTS = new javax.swing.JPanel();
        CP_OPT1 = new javax.swing.JCheckBox();
        CP_OPT2 = new javax.swing.JCheckBox();
        CP_OPT3 = new javax.swing.JCheckBox();
        CP_OPT4 = new javax.swing.JCheckBox();
        CP_OPT5 = new javax.swing.JCheckBox();
        CP_OPT6 = new javax.swing.JCheckBox();
        jLabel3 = new javax.swing.JLabel();
        CB_ACCOUNT = new javax.swing.JComboBox();
        BT_4EYES = new javax.swing.JCheckBox();
        PNL_4EYES = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        TXT_4EYES_USER = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        TXTP_4EYES_PWD = new javax.swing.JPasswordField();
        TBP_SEARCH = new javax.swing.JTabbedPane();
        PN_SIMPLE = new javax.swing.JPanel();
        BT_ADD = new GlossButton();
        BT_DEL = new GlossButton();
        SCP_LIST = new javax.swing.JScrollPane();
        PN_COMPLEX = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        TXTA_FILTER = new javax.swing.JTextArea();
        PN_BUTTONS = new javax.swing.JPanel();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();
        BT_MATCH_USERS = new GlossButton();

        setDoubleBuffered(false);
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        PN_ACTION.setDoubleBuffered(false);
        PN_ACTION.setOpaque(false);

        jLabel1.setText(UserMain.Txt("Name")); // NOI18N

        TXT_NAME.setText(UserMain.Txt("Neuer_Rolle")); // NOI18N
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

        BT_DISABLED.setText(UserMain.getString("Gesperrt")); // NOI18N
        BT_DISABLED.setOpaque(false);

        PN_OPTS.setBorder(javax.swing.BorderFactory.createTitledBorder(UserMain.getString("Options"))); // NOI18N

        CP_OPT1.setText("jCheckBox1");

        CP_OPT2.setText("jCheckBox1");

        CP_OPT3.setText("jCheckBox1");

        CP_OPT4.setText("jCheckBox1");

        CP_OPT5.setText("jCheckBox1");

        CP_OPT6.setText("jCheckBox1");

        javax.swing.GroupLayout PN_OPTSLayout = new javax.swing.GroupLayout(PN_OPTS);
        PN_OPTS.setLayout(PN_OPTSLayout);
        PN_OPTSLayout.setHorizontalGroup(
            PN_OPTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_OPTSLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_OPTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CP_OPT1)
                    .addComponent(CP_OPT2)
                    .addComponent(CP_OPT3)
                    .addComponent(CP_OPT4)
                    .addComponent(CP_OPT5)
                    .addComponent(CP_OPT6))
                .addContainerGap(307, Short.MAX_VALUE))
        );
        PN_OPTSLayout.setVerticalGroup(
            PN_OPTSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_OPTSLayout.createSequentialGroup()
                .addComponent(CP_OPT1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CP_OPT2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CP_OPT3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CP_OPT4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CP_OPT5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(CP_OPT6)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dimm/home/MA_Properties"); // NOI18N
        jLabel3.setText(bundle.getString("Realm")); // NOI18N

        BT_4EYES.setText(UserMain.Txt("4EYES")); // NOI18N
        BT_4EYES.setOpaque(false);
        BT_4EYES.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_4EYESActionPerformed(evt);
            }
        });

        PNL_4EYES.setOpaque(false);

        jLabel4.setText(UserMain.Txt("User")); // NOI18N

        jLabel5.setText(UserMain.Txt("Password")); // NOI18N

        TXTP_4EYES_PWD.setEditable(false);
        TXTP_4EYES_PWD.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXTP_4EYES_PWDMouseClicked(evt);
            }
        });

        javax.swing.GroupLayout PNL_4EYESLayout = new javax.swing.GroupLayout(PNL_4EYES);
        PNL_4EYES.setLayout(PNL_4EYESLayout);
        PNL_4EYESLayout.setHorizontalGroup(
            PNL_4EYESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PNL_4EYESLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PNL_4EYESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4))
                .addGap(48, 48, 48)
                .addGroup(PNL_4EYESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(TXTP_4EYES_PWD)
                    .addComponent(TXT_4EYES_USER, javax.swing.GroupLayout.DEFAULT_SIZE, 116, Short.MAX_VALUE)))
        );
        PNL_4EYESLayout.setVerticalGroup(
            PNL_4EYESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PNL_4EYESLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PNL_4EYESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(TXT_4EYES_USER, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(PNL_4EYESLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(TXTP_4EYES_PWD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        TBP_SEARCH.setBorder(javax.swing.BorderFactory.createTitledBorder(UserMain.Txt("Filter"))); // NOI18N

        BT_ADD.setText(" + ");
        BT_ADD.setMargin(new java.awt.Insets(2, 1, 2, 1));
        BT_ADD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ADDActionPerformed(evt);
            }
        });

        BT_DEL.setText(" - ");
        BT_DEL.setMargin(new java.awt.Insets(2, 1, 2, 1));
        BT_DEL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_DELActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_SIMPLELayout = new javax.swing.GroupLayout(PN_SIMPLE);
        PN_SIMPLE.setLayout(PN_SIMPLELayout);
        PN_SIMPLELayout.setHorizontalGroup(
            PN_SIMPLELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_SIMPLELayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BT_ADD)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(BT_DEL)
                .addContainerGap(327, Short.MAX_VALUE))
            .addComponent(SCP_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, 389, Short.MAX_VALUE)
        );

        PN_SIMPLELayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ADD, BT_DEL});

        PN_SIMPLELayout.setVerticalGroup(
            PN_SIMPLELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_SIMPLELayout.createSequentialGroup()
                .addGroup(PN_SIMPLELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_ADD)
                    .addComponent(BT_DEL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SCP_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, 84, Short.MAX_VALUE))
        );

        TBP_SEARCH.addTab(UserMain.Txt("Simple_Filter"), PN_SIMPLE); // NOI18N

        TXTA_FILTER.setColumns(20);
        TXTA_FILTER.setEditable(false);
        TXTA_FILTER.setRows(5);
        TXTA_FILTER.setTabSize(4);
        TXTA_FILTER.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXTA_FILTERMouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(TXTA_FILTER);

        javax.swing.GroupLayout PN_COMPLEXLayout = new javax.swing.GroupLayout(PN_COMPLEX);
        PN_COMPLEX.setLayout(PN_COMPLEXLayout);
        PN_COMPLEXLayout.setHorizontalGroup(
            PN_COMPLEXLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_COMPLEXLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 369, Short.MAX_VALUE)
                .addContainerGap())
        );
        PN_COMPLEXLayout.setVerticalGroup(
            PN_COMPLEXLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_COMPLEXLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE)
                .addContainerGap())
        );

        TBP_SEARCH.addTab(UserMain.Txt("Complex_Filter"), PN_COMPLEX); // NOI18N

        javax.swing.GroupLayout PN_ACTIONLayout = new javax.swing.GroupLayout(PN_ACTION);
        PN_ACTION.setLayout(PN_ACTIONLayout);
        PN_ACTIONLayout.setHorizontalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BT_DISABLED)
                    .addComponent(TBP_SEARCH, javax.swing.GroupLayout.DEFAULT_SIZE, 406, Short.MAX_VALUE)
                    .addComponent(PN_OPTS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TXT_NAME, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(CB_ACCOUNT, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 118, Short.MAX_VALUE))
                    .addGroup(PN_ACTIONLayout.createSequentialGroup()
                        .addComponent(BT_4EYES)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(PNL_4EYES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        PN_ACTIONLayout.setVerticalGroup(
            PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_ACTIONLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(TXT_NAME, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(CB_ACCOUNT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(TBP_SEARCH, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(PN_OPTS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(PN_ACTIONLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(BT_4EYES)
                    .addComponent(PNL_4EYES, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(17, 17, 17)
                .addComponent(BT_DISABLED)
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(PN_ACTION, gridBagConstraints);

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

        BT_MATCH_USERS.setText(UserMain.Txt("Show_matchig_users")); // NOI18N
        BT_MATCH_USERS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_MATCH_USERSActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_BUTTONSLayout = new javax.swing.GroupLayout(PN_BUTTONS);
        PN_BUTTONS.setLayout(PN_BUTTONSLayout);
        PN_BUTTONSLayout.setHorizontalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(BT_MATCH_USERS)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 109, Short.MAX_VALUE)
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
                    .addComponent(BT_MATCH_USERS))
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
            UserMain.self.show_vkeyboard( this.my_dlg, TXT_NAME, false);
        }
}//GEN-LAST:event_TXT_NAMEMouseClicked

    private void TXT_NAMEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_TXT_NAMEActionPerformed
    {//GEN-HEADEREND:event_TXT_NAMEActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_TXT_NAMEActionPerformed

    SwingWorker sw = null;
    private void BT_MATCH_USERSActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_MATCH_USERSActionPerformed
    {//GEN-HEADEREND:event_BT_MATCH_USERSActionPerformed
        // TODO add your handling code here:

        if (!is_plausible())
            return;
        
        String role_filter_compressed = object.getAccountmatch();
        final String cmd = "ListUsers CMD:match_filter MA:" + UserMain.self.get_act_mandant().getId() + " AC:" + accm.get_act_id() + " FLC:'" + role_filter_compressed + "'";

        if (sw != null)
        {
            return;
        }

        sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                UserMain.self.show_busy(my_dlg, UserMain.Txt("Checking_userlist") + "...");
                String ret = UserMain.fcc().call_abstract_function(cmd, ServerCall.SHORT_CMD_TO);
                UserMain.self.hide_busy();
                
                if (ret != null && ret.charAt(0) == '0')
                {
                    Object o = ParseToken.DeCompressObject(ret.substring(3));
                    if (o instanceof ArrayList<?>)
                    {
                        ArrayList<String> list = (ArrayList<String>)o;
                        UserListPanel pnl = new UserListPanel(list);
                        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);
                        dlg.set_next_location(my_dlg);
                        dlg.setVisible(true);
                    }
                }
                else
                {
                    UserMain.errm_ok(my_dlg, UserMain.Txt("Check_user_failed") + ": " + ((ret != null) ?  ret.substring(3): ""));
                }
                sw = null;
                return null;
            }
        };

        sw.start();


    }//GEN-LAST:event_BT_MATCH_USERSActionPerformed

    private void BT_4EYESActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_4EYESActionPerformed
    {//GEN-HEADEREND:event_BT_4EYESActionPerformed
        // TODO add your handling code here:
        PNL_4EYES.setVisible(BT_4EYES.isSelected());
        if (BT_4EYES.isSelected())
        {
            TXTP_4EYES_PWD.setText("");
            TXT_4EYES_USER.setText("");
        }
    }//GEN-LAST:event_BT_4EYESActionPerformed

    private void TXTP_4EYES_PWDMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXTP_4EYES_PWDMouseClicked
    {//GEN-HEADEREND:event_TXTP_4EYES_PWDMouseClicked
        // TODO add your handling code here:
        CheckPwdPanel pnl = new CheckPwdPanel(UserMain.self, /*trong*/true);
        GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, pnl);        

        dlg.setLocation(my_dlg.get_next_location());
        dlg.setTitle(UserMain.getString("4Augen-Passwort_setzen"));
        dlg.setVisible(true);

        if (pnl.isOkay())
        {
            TXTP_4EYES_PWD.setText(pnl.get_pwd());
        }
    }//GEN-LAST:event_TXTP_4EYES_PWDMouseClicked

    private void BT_ADDActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ADDActionPerformed
    {//GEN-HEADEREND:event_BT_ADDActionPerformed
        // TODO add your handling code here:
        // DEFAULT: ALL CONTAINS WORD
        ConditionCBEntry cbe = (ConditionCBEntry)CB_CONDITION.getItemAt(0);
        simple_search_tablemodel.model.getChildren().add( cbe.entry );
        simple_search_tablemodel.fireTableDataChanged();
    }//GEN-LAST:event_BT_ADDActionPerformed

    private void BT_DELActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_DELActionPerformed
    {//GEN-HEADEREND:event_BT_DELActionPerformed
        // TODO add your handling code here:
        int local_row = simple_search_table.getSelectedRow();
        if (local_row >= 0)
        {
            simple_search_tablemodel.model.getChildren().remove( simple_search_tablemodel.model.getChildren().get(local_row));
            simple_search_tablemodel.fireTableDataChanged();
        }
}//GEN-LAST:event_BT_DELActionPerformed

    private void TXTA_FILTERMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXTA_FILTERMouseClicked
    {//GEN-HEADEREND:event_TXTA_FILTERMouseClicked
        // TODO add your handling code here:
       clr_flag(CS_Constants.ROLE_ACM_SIMPLE);
       edit_complex_user_filter();
       
    }//GEN-LAST:event_TXTA_FILTERMouseClicked
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox BT_4EYES;
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_ADD;
    private javax.swing.JButton BT_DEL;
    private javax.swing.JCheckBox BT_DISABLED;
    private javax.swing.JButton BT_MATCH_USERS;
    private javax.swing.JButton BT_OK;
    private javax.swing.JComboBox CB_ACCOUNT;
    private javax.swing.JCheckBox CP_OPT1;
    private javax.swing.JCheckBox CP_OPT2;
    private javax.swing.JCheckBox CP_OPT3;
    private javax.swing.JCheckBox CP_OPT4;
    private javax.swing.JCheckBox CP_OPT5;
    private javax.swing.JCheckBox CP_OPT6;
    private javax.swing.JPanel PNL_4EYES;
    private javax.swing.JPanel PN_ACTION;
    private javax.swing.JPanel PN_BUTTONS;
    private javax.swing.JPanel PN_COMPLEX;
    private javax.swing.JPanel PN_OPTS;
    private javax.swing.JPanel PN_SIMPLE;
    private javax.swing.JScrollPane SCP_LIST;
    private javax.swing.JTabbedPane TBP_SEARCH;
    private javax.swing.JTextArea TXTA_FILTER;
    private javax.swing.JPasswordField TXTP_4EYES_PWD;
    private javax.swing.JTextField TXT_4EYES_USER;
    private javax.swing.JTextField TXT_NAME;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JScrollPane jScrollPane2;
    // End of variables declaration//GEN-END:variables

    boolean test_flag( int f )
    {
        int flags = get_object_flags();

        return ((flags & f) == f);
    }
    int get_object_flags()
    {
        return get_object_flags(object);
    }
    int get_object_flags(Role r)
    {
        int flags = 0;
        if (r.getFlags() == null || r.getFlags().length() == 0)
            return 0;

        try
        {
            flags = Integer.parseInt(r.getFlags());
        }
        catch (NumberFormatException numberFormatException)
        {
            Logger.getLogger("").log(Level.SEVERE, "Invalid flag for " + object_name + " " + numberFormatException );
        }

        return flags;
    }
    void set_flag(int flag)
    {
        int flags = get_object_flags();
        flags |= flag;
        object.setFlags(Integer.toString(flags));
    }
    void clr_flag(int flag)
    {
        int flags = get_object_flags();
        flags &= ~flag;
        object.setFlags(Integer.toString(flags));
    }

    boolean object_is_disabled()
    {
        int flags = 0;

        flags = get_object_flags();

        return ((flags & CS_Constants.ROLE_DISABLED) == CS_Constants.ROLE_DISABLED);
    }
    void set_object_disabled( boolean f)
    {
        if (f)
            set_flag( CS_Constants.ROLE_DISABLED);
        else
            clr_flag( CS_Constants.ROLE_DISABLED);
    }

    
    @Override
    protected boolean check_changed()
    {        
        if (model.is_new(row))
            return true;

        String name = object.getName();
        if (name == null || TXT_NAME.getText().compareTo(name ) != 0)
            return true;   
        
        if (BT_DISABLED.isSelected() != object_is_disabled())
            return true;

      
        long ac_id = model.getSqlResult().getLong( row, "ac_id");

        if ( CB_ACCOUNT.getSelectedItem() != null)
        {
            if (accm.get_act_id() != ac_id)
                return true;
        }

        if (check_opts_buttons_changed())
            return true;

        if (object.getAccountmatch().compareTo(role_filter_save) != 0)
            return true;

        if (BT_4EYES.isSelected())
        {
            if (object.getUser4eyes() == null)
                return true;

            if (object.getUser4eyes().compareTo( TXT_4EYES_USER.getText()) != 0)
                return true;

            if (object.getPwd4eyes() == null)
                return true;

            String pwd = new String( TXTP_4EYES_PWD.getPassword() );
            if (object.getPwd4eyes().compareTo( pwd) != 0)
                return true;
        }

        return false;
    }
                        
    @Override
    protected boolean is_plausible()
    {

        if (test_flag(CS_Constants.ROLE_ACM_SIMPLE))
        {
            simple_search_table.getColumnModel().getColumn(SIMPLE_TAB_COL_VALUE).getCellEditor().stopCellEditing();            
            object.setAccountmatch(simple_search_tablemodel.get_compressed_xml_list_data());
        }
        if (!Validator.is_valid_path( TXT_NAME.getText(), 255))
        {
            UserMain.errm_ok(UserMain.getString("Der_Pfad_ist_nicht_okay"));
            return false;
        }
        try
        {
            AccountConnector da = accm.get_selected_ac();
            String n = accm.getName(da);
        }
        catch (Exception e)
        {
            UserMain.errm_ok(UserMain.getString("Realm_ist_nicht_okay"));
            return false;
        }
        if (object.getAccountmatch() == null || object.getAccountmatch().length() == 0)
        {
            UserMain.errm_ok(UserMain.getString("Benutzerfilter_ist_nicht_okay"));
            return false;
        }
        if (BT_4EYES.isSelected())
        {
            if (!Validator.is_valid_name(TXT_4EYES_USER.getText(), 80))
            {
                UserMain.errm_ok(UserMain.getString("Der_4-Augen_Username_ist_nicht_okay"));
                return false;
            }
            String pwd = new String( TXTP_4EYES_PWD.getPassword() );
            if (!Validator.is_valid_name(pwd, 80))
            {
                UserMain.errm_ok(UserMain.getString("Das_4-Augen_Passwort_ist_nicht_okay"));
                return false;
            }
        }

                
        return true;
    }

    

    @Override
    protected void set_object_props()
    {
        String opts = "";
        String name = TXT_NAME.getText();        
        boolean de = BT_DISABLED.isSelected();


        //String ac = TXT_ACCOUNTMATCH.getText();

        object.setName(name);
        object.setOpts(opts);
        
        object.setLicense( new Integer(0));
        object.setAccountConnector( accm.get_selected_ac());

        object.setUser4eyes(TXT_4EYES_USER.getText());
        String pwd = new String( TXTP_4EYES_PWD.getPassword() );
        object.setPwd4eyes(pwd);

        set_object_disabled( de );
    }
    public String get_option_qry(long role_id)
    {
        String qry = "select * from role_option where ro_id=" + role_id + " order by id";
        return qry;
    }

    @Override
    public boolean  update_db( Object o, Object so)
    {
        if (was_4eyes)
        {
            // CHECK WITH THE OLD OBJECT
            if (!Login4EyesPanel.check_login(save_object))
            {
                return false;
            }
        }
        boolean ret = super.update_db(o, so);
        if (ret)
        {
            ret = write_opts_buttons( this.object.getId() );
        }
        return ret;
    }
    
    
        
    @Override
    protected boolean is_new()
    {
        return model.is_new(row);
    }
   

    @Override
    public JButton get_default_button()
    {
        return BT_OK;
    }

    private void read_opts_buttons( int id)
    {
        ServerCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open();
        StatementID sid = sql.createStatement(cid);


        String qry = get_option_qry( id );
        ResultSetID rid = sql.executeQuery(sid, qry);

        SQLArrayResult resa = sql.get_sql_array_result(rid);

        option_res = new SQLResult<RoleOption>(UserMain.sqc(), resa, new RoleOption().getClass());

        for (int i = 0; i < OptCBEntry.opt_list.length; i++)
        {
            OptCBEntry optCBEntry = OptCBEntry.opt_list[i];

            RoleOption  ro = get_option_for_token( optCBEntry.getToken() );

            if (ro != null)
            {
                optCBEntry.getCb().setSelected(true);
            }
            else
            {
                optCBEntry.getCb().setSelected(false);
            }
        }

        sql.close(cid);

    }
    private boolean  write_opts_buttons( int id)
    {
        ServerCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open();
        StatementID sid = sql.createStatement(cid);

        boolean ret = true;


        for (int i = 0; i < OptCBEntry.opt_list.length; i++)
        {
            OptCBEntry optCBEntry = OptCBEntry.opt_list[i];

            RoleOption  ro = get_option_for_token( optCBEntry.getToken() );

            if (ro != null && !optCBEntry.getCb().isSelected())
            {
                sql.Delete(sid, ro);
            }
            if (ro == null && optCBEntry.getCb().isSelected())
            {
                ro = new RoleOption( 0, object, optCBEntry.getToken(), 0);
                if (!sql.Insert(sid, ro))
                    ret = false;
            }
        }

        sql.close(cid);

        return ret;

    }

    private RoleOption get_option_for_token(String token)
    {
        // NEW ?
        if (option_res == null)
            return null;

        for (int j = 0; j < option_res.size(); j++)
        {
            try
            {
                RoleOption roleOption = option_res.get(j);
                if (roleOption.getToken().compareTo(token) == 0)
                {
                    return roleOption;
                }
            }
            catch (Exception e)
            {
                System.out.println("Exception in get_option_for_token(): " + e.getMessage());
            }
        }
        return null;
    }

    private boolean check_opts_buttons_changed()
    {
        boolean changed = false;

        for (int i = 0; i < OptCBEntry.opt_list.length; i++)
        {
            OptCBEntry optCBEntry = OptCBEntry.opt_list[i];

            RoleOption  ro = get_option_for_token( optCBEntry.getToken() );
            if (ro != null && !optCBEntry.getCb().isSelected())
            {
                changed = true;
                break;
            }
            if (ro == null && optCBEntry.getCb().isSelected())
            {
                changed = true;
                break;
            }
        }
        return changed;
    }

    @Override
    protected boolean insert_db(Object object)
    {
        boolean ret = super.insert_db(object);
        if (ret)
        {
            ret = write_opts_buttons( this.object.getId() );
        }
        return ret;

    }

    private void set_filter_preview( String _nice_filter_text )
    {                   
        TXTA_FILTER.setText(_nice_filter_text);
        TXTA_FILTER.setCaretPosition(0);

    }

    private void edit_complex_user_filter()
    {
        try
        {
            ArrayList<VarTypeEntry> var_names = new ArrayList<VarTypeEntry>();
            var_names.add(new VarTypeEntry("Username", ExprEntry.TYPE.STRING) );
            var_names.add(new VarTypeEntry("Email", ExprEntry.TYPE.STRING) );
            var_names.add(new VarTypeEntry("Domain", ExprEntry.TYPE.STRING) );
            var_names.add(new VarTypeEntry("Group", ExprEntry.TYPE.STRING) );
            
            
            LogicFilter rf = new LogicFilter(var_names, object.getAccountmatch());

            GenericGlossyDlg dlg = new GenericGlossyDlg(UserMain.self, true, rf);
            dlg.setVisible(true);

            if (rf.isOkay())
            {
                set_new_filter_vals(rf.get_compressed_xml_list_data());
            }
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
    }

    void set_new_filter_vals(String role_filter_xml)
    {

         object.setAccountmatch(role_filter_xml);
         set_filter_preview( LogicFilter.get_nice_filter_text( role_filter_xml) );

    }
    
   
}
