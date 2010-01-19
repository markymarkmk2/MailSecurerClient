/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RoleFilter.java
 *
 * Created on 09.11.2009, 17:01:45
 */

package dimm.home.Panels;

import com.thoughtworks.xstream.XStream;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.UserMain;
import home.shared.Utilities.ZipUtilities;
import home.shared.filter.ExprEntry;
import home.shared.filter.ExprEntry.OPERATION;
import home.shared.filter.ExprEntry.TYPE;
import home.shared.filter.GroupEntry;
import home.shared.filter.LogicEntry;
import home.shared.filter.VarTypeEntry;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.text.BadLocationException;



class CB_op_entry
{
    ExprEntry.OPERATION op;
    ExprEntry.TYPE type;

    public CB_op_entry( OPERATION op, TYPE type )
    {
        this.op = op;
        this.type = type;
    }

    @Override
    public String toString()
    {
        return LogicFilter.get_op_nice_txt(op, type);
    }
}


class LogicEntryModel extends GroupEntry
{
    LogicEntryModel(ArrayList<LogicEntry> list)
    {
        children = list;
    }
    LogicEntryModel()
    {
    }
}


/**
 *
 * @author mw
 */
public class LogicFilter extends GlossDialogPanel
{
    LogicEntryModel model;

    private boolean okay;
    ArrayList<VarTypeEntry> var_names;
    ArrayList<String> var_nice_names;
    boolean in_init;
    
    /** Creates new form RoleFilter */
    public LogicFilter(ArrayList<VarTypeEntry> var_names, String compressed_list_str, boolean compressed)
    {
        initComponents();

        // READ COMPRESSED FILTER XML DATA
        ArrayList<LogicEntry> list = get_filter_list( compressed_list_str, compressed );

        model = new LogicEntryModel(list);

        set_txta_display();

        COMBO_AND_OR.removeAllItems();
        COMBO_NAME.removeAllItems();
        COMBO_OPERATION.removeAllItems();

        COMBO_AND_OR.addItem( UserMain.Txt("And"));
        COMBO_AND_OR.addItem( UserMain.Txt("Or"));

        

        this.var_names = var_names;
       
        

        // ADD TRANSLATIONS TO COMBO
        for (int i = 0; i < this.var_names.size(); i++)
        {
            String string = this.var_names.get(i).getVar();
            COMBO_NAME.addItem(UserMain.Txt(string));            
        }
        COMBO_NAME.setSelectedIndex(0);
        set_operation( 0);
    }
    void set_operation( int var_idx )
    {
        in_init = true;
        if (var_idx <0 || var_idx >= var_names.size())
            return;

        TYPE type = var_names.get( var_idx).getType();
        switch (type)
        {
            case STRING:
            {
                COMBO_OPERATION.removeAllItems();
                COMBO_OPERATION.addItem( new CB_op_entry( OPERATION.CONTAINS, type ) );
                COMBO_OPERATION.addItem( new CB_op_entry( OPERATION.BEGINS_WITH, type ) );
                COMBO_OPERATION.addItem( new CB_op_entry( OPERATION.ENDS_WITH, type ) );
                COMBO_OPERATION.addItem( new CB_op_entry( OPERATION.CONTAINS_SUBSTR, type ) );
                COMBO_OPERATION.addItem( new CB_op_entry( OPERATION.REGEXP, type ) );
                break;
            }
            case INT:
            {
                COMBO_OPERATION.removeAllItems();
                COMBO_OPERATION.addItem( new CB_op_entry( OPERATION.NUM_EQUAL, type ) );
                COMBO_OPERATION.addItem( new CB_op_entry( OPERATION.NUM_LT, type ) );
                COMBO_OPERATION.addItem( new CB_op_entry( OPERATION.NUM_GT, type ) );
                COMBO_OPERATION.addItem( new CB_op_entry( OPERATION.REGEXP, type ) );
            }
            case TIMESTAMP:
            {
                COMBO_OPERATION.removeAllItems();
                COMBO_OPERATION.addItem( new CB_op_entry( OPERATION.NUM_LT, type ) );
                COMBO_OPERATION.addItem( new CB_op_entry( OPERATION.NUM_GT, type ) );
                COMBO_OPERATION.addItem( new CB_op_entry( OPERATION.REGEXP, type ) );
            }
        }
        in_init = false;
    }

    public String get_compressed_xml_list_data( boolean compressed)
    {
        String xml = null;
        XStream xstr = new XStream();
        xml = xstr.toXML(model.getChildren());
        String compressed_list_str = xml;
        if (compressed)
        {
            try
            {
                compressed_list_str = ZipUtilities.compress(xml);
            }
            catch (Exception e)
            {
                UserMain.errm_ok(UserMain.Txt("Invalid_filter,_resetting_to_empty_list"));
                compressed_list_str = "";
            }
        }
        return compressed_list_str;
    }

    public static String get_op_nice_txt( ExprEntry.OPERATION operation, TYPE type )
    {
        switch( operation )
        {
            case BEGINS_WITH: return UserMain.Txt("Begins_with");
            case ENDS_WITH: return UserMain.Txt("Ends_with");
            case CONTAINS_SUBSTR: return UserMain.Txt("Contains_substring");
            case CONTAINS: return UserMain.Txt("Contains");
            case REGEXP: return UserMain.Txt("Matches_regular_expression");
            case NUM_EQUAL : return "=";
            case NUM_LT : return type == TYPE.TIMESTAMP ? UserMain.Txt("Older_than") : "<";
            case NUM_GT : return type == TYPE.TIMESTAMP ? UserMain.Txt("Newer_than") : "<";
        }
        return "???";
    }
    public static String get_nice_txt( ExprEntry e)
    {
        return (e.isNeg() ? UserMain.Txt("not") + " " : "") + UserMain.Txt(e.getName()) + " " + get_op_nice_txt( e.getOperation(), e.getType() ) + " " + e.getValue();
    }
    public static String get_nice_txt( LogicEntry e)
    {
        if (e instanceof ExprEntry)
        {
            return get_nice_txt((ExprEntry)e);
        }
        return (e.isNeg() ? UserMain.Txt("not") + " " : "") ;
    }

    public static ArrayList<LogicEntry> get_filter_list( String list_str, boolean compressed )
    {
        ArrayList<LogicEntry> list = null;
        if (list_str == null || list_str.length() == 0)
        {
            list = new ArrayList<LogicEntry>();
        }
        else
        {
            try
            {
                String xml_list_str = list_str;

                if (compressed)
                    xml_list_str = ZipUtilities.uncompress(list_str);

                XStream xstr = new XStream();

                list = (ArrayList<LogicEntry>) xstr.fromXML(xml_list_str);
            }
            catch (Exception e)
            {
                System.out.println("Invalid_filter,_resetting_to_empty_list");
                list = new ArrayList<LogicEntry>();
            }
        }
        return list;
    }
    
    public static String get_nice_filter_text( String compressed_list_str, boolean compressed )
    {
        ArrayList<LogicEntry> list = get_filter_list( compressed_list_str, compressed );

        StringBuffer sb = new StringBuffer();

        gather_nice_text( sb, list, 0 );

        return sb.toString();
    }


    LogicEntry get_actual_entry()
    {
        return get_row_from_txtpo();
    }
    ArrayList<LogicEntry> get_act_parent_list()
    {
        ArrayList<LogicEntry> parent_list;

        if (model.getChildren().size() == 0)
        {
            parent_list = model.getChildren();
        }
        else
        {
            LogicEntry last_entry = get_actual_entry();
            if ( last_entry instanceof GroupEntry)
            {
                GroupEntry last_g_entry = (GroupEntry)last_entry;
                parent_list = last_g_entry.getChildren();
            }
            else if (last_entry == null)
            {
                parent_list = model.getChildren();
            }
            else
            {
                parent_list = last_entry.getParent_list();
            }
        }
        return parent_list;
    }

    int get_insert_idx(ArrayList<LogicEntry> parent_list)
    {
        LogicEntry last_entry = get_actual_entry();
        int insert_idx = -1;
        if (!( last_entry instanceof GroupEntry))
        {
            for (int i = 0; i < parent_list.size() - 1; i++)
            {
                LogicEntry logicEntry = parent_list.get(i);
                if (logicEntry == last_entry)
                {
                    insert_idx = i + 1;
                    break;
                }
            }
        }
        return insert_idx;
    }


    void add_new_group( boolean neg, boolean previous_is_or )
    {
        ArrayList<LogicEntry> parent_list = get_act_parent_list();

        int insert_idx = get_insert_idx(parent_list);

        // INSERT AT FIRST POS AFTER APPENDIDX
        if (insert_idx != -1)
            parent_list.add( insert_idx, new GroupEntry( parent_list, neg, previous_is_or) );
        else
            parent_list.add( new GroupEntry( parent_list, neg, previous_is_or) );

   //     model.rebuild_list();
    }
    
    void add_new_expression( String name, String value, ExprEntry.OPERATION operation, ExprEntry.TYPE type, boolean neg, boolean previous_is_or )
    {
        ArrayList<LogicEntry> parent_list = get_act_parent_list();

        int insert_idx = get_insert_idx(parent_list);

        if (insert_idx != -1)
            parent_list.add( insert_idx, new ExprEntry( parent_list, name, value, operation, type, neg, previous_is_or) );
        else
            parent_list.add( new ExprEntry( parent_list, name, value, operation, type, neg, previous_is_or) );

      //  model.rebuild_list();
    }

    static void add_level_txt( StringBuffer sb, int level, String txt )
    {
        for (int i = 0; i < level; i++)
        {
            sb.append("\t");
        }
        sb.append( txt );
        sb.append( "\n" );

    }

    static void gather_nice_text( StringBuffer sb, ArrayList<LogicEntry> list, int level )
    {
        for (int i = 0; i < list.size(); i++)
        {
            LogicEntry logicEntry = list.get(i);

            if (i > 0)
            {
                if (logicEntry.isPrevious_is_or())
                    add_level_txt( sb, level, UserMain.Txt("or"));
                else
                    add_level_txt( sb, level, UserMain.Txt("and"));
            }

            if (logicEntry instanceof  GroupEntry)
            {
                if (logicEntry.isNeg())
                    add_level_txt( sb, level, UserMain.Txt("not") + " (");
                else
                    add_level_txt( sb, level, "(");

                gather_nice_text( sb, ((GroupEntry)logicEntry).getChildren(), level + 1 );

                add_level_txt( sb, level, ")");
            }
            if (logicEntry instanceof  ExprEntry)
            {
                ExprEntry expe = (ExprEntry)logicEntry;
                String txt = get_nice_txt(expe);
                add_level_txt( sb, level, txt);
            }
        }
    }
    int cr_count;
    LogicEntry get_elem_from_list( ArrayList<LogicEntry> list)
    {
        for (int i = 0; i < list.size(); i++)
        {
            LogicEntry logicEntry = list.get(i);
            if (cr_count == 0)
                return logicEntry;

            if (i > 0)
            {
                cr_count--;
            }
            if (cr_count == 0)
                return logicEntry;

            if (logicEntry instanceof  GroupEntry)
            {
                cr_count--;
                LogicEntry le = get_elem_from_list( ((GroupEntry)logicEntry).getChildren() );
                if (le != null)
                    return le;

                cr_count--;
                if (cr_count <= 0)
                    return logicEntry;
            }
            else
                cr_count--;
        }
        return null;
    }
    LogicEntry get_row_from_txtpo()
    {
        String s;
        try
        {
            s = TXTA_LIST.getText(0, TXTA_LIST.getCaretPosition());
        }
        catch (BadLocationException badLocationException)
        {
            return null;
        }
        cr_count = 0;
        for (int i = 0; i < s.length(); i++)
        {
            if (s.charAt(i) == '\n')
                cr_count++;
        }

        LogicEntry le = get_elem_from_list( model.getChildren() );


        return le;
    }


    void set_txta_display( )
    {
        int pos = TXTA_LIST.getCaretPosition();

        StringBuffer sb = new StringBuffer();

        ArrayList<LogicEntry> list = model.getChildren();

        gather_nice_text( sb, list, 0 );

        TXTA_LIST.setText(sb.toString());
        if (pos < TXTA_LIST.getText().length())
            TXTA_LIST.setCaretPosition(pos);
        TXTA_LIST.requestFocusInWindow();
    }
    private void remove_selected_entry()
    {
        LogicEntry le = get_row_from_txtpo();

        ArrayList<LogicEntry> list = le.getParent_list();

        for (int i = 0; i < list.size(); i++)
        {
            LogicEntry logicEntry = list.get(i);
            if (logicEntry == le)
            {
                list.remove(le);
                return;
            }
        }
//        model.rebuild_list();

    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        COMBO_NAME = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        SCP_LIST = new javax.swing.JScrollPane();
        TXTA_LIST = new javax.swing.JTextArea();
        COMBO_OPERATION = new javax.swing.JComboBox();
        TXT_VAR = new javax.swing.JTextField();
        BT_ADD_EXPR = new GlossButton();
        BT_DEL_ELEM = new GlossButton();
        BT_ADD_GROUP = new GlossButton();
        BT_NEG = new javax.swing.JCheckBox();
        COMBO_AND_OR = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        BT_CLOSE = new GlossButton();
        BT_ABORT = new GlossButton();
        jLabel4 = new javax.swing.JLabel();

        jLabel1.setText(UserMain.getString("And/Or")); // NOI18N

        COMBO_NAME.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        COMBO_NAME.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                COMBO_NAMEActionPerformed(evt);
            }
        });

        TXTA_LIST.setColumns(20);
        TXTA_LIST.setRows(5);
        TXTA_LIST.setTabSize(4);
        SCP_LIST.setViewportView(TXTA_LIST);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, 602, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE)
        );

        COMBO_OPERATION.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        BT_ADD_EXPR.setText("Add new expression");
        BT_ADD_EXPR.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ADD_EXPRActionPerformed(evt);
            }
        });

        BT_DEL_ELEM.setText("Del expression");
        BT_DEL_ELEM.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_DEL_ELEMActionPerformed(evt);
            }
        });

        BT_ADD_GROUP.setText("Add new group");
        BT_ADD_GROUP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ADD_GROUPActionPerformed(evt);
            }
        });

        BT_NEG.setText("Negate");
        BT_NEG.setOpaque(false);

        COMBO_AND_OR.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText(UserMain.getString("Field")); // NOI18N

        BT_CLOSE.setText(UserMain.Txt("OK")); // NOI18N
        BT_CLOSE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_CLOSEActionPerformed(evt);
            }
        });

        BT_ABORT.setText(UserMain.Txt("Abort")); // NOI18N
        BT_ABORT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ABORTActionPerformed(evt);
            }
        });

        jLabel4.setText(UserMain.getString("Word")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BT_ADD_EXPR)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_ADD_GROUP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_DEL_ELEM, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 44, Short.MAX_VALUE)
                        .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(BT_CLOSE, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TXT_VAR, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                            .addComponent(COMBO_NAME, 0, 131, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(COMBO_AND_OR, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(COMBO_OPERATION, 0, 140, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_NEG)
                        .addGap(135, 135, 135)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(32, 32, 32)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(COMBO_NAME, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(COMBO_OPERATION, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3)
                    .addComponent(BT_NEG))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_VAR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4)
                    .addComponent(jLabel1)
                    .addComponent(COMBO_AND_OR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_CLOSE, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_ADD_EXPR)
                    .addComponent(BT_ADD_GROUP)
                    .addComponent(BT_DEL_ELEM))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_ADD_EXPRActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ADD_EXPRActionPerformed
    {//GEN-HEADEREND:event_BT_ADD_EXPRActionPerformed
        // TODO add your handling code here:
        int name_idx = COMBO_NAME.getSelectedIndex();
        // USE REAL NAME, COMBO CONTAINS TRANSLATION
        String name = var_names.get( name_idx ).getVar();
        ExprEntry.TYPE type = var_names.get( name_idx ).getType();

        String value = TXT_VAR.getText();
        CB_op_entry ope = (CB_op_entry) COMBO_OPERATION.getSelectedItem();
        boolean neg = BT_NEG.isSelected();
        boolean is_or = false;
        if (COMBO_AND_OR.getSelectedIndex() == 1)
            is_or = true;
        
        add_new_expression( name, value, ope.op, type, neg, is_or);

        set_txta_display();

    }//GEN-LAST:event_BT_ADD_EXPRActionPerformed

    private void BT_ADD_GROUPActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ADD_GROUPActionPerformed
    {//GEN-HEADEREND:event_BT_ADD_GROUPActionPerformed
        // TODO add your handling code here:
        boolean neg = BT_NEG.isSelected();
        boolean is_or = false;
        if (COMBO_AND_OR.getSelectedIndex() == 1)
            is_or = true;

        add_new_group( neg, is_or);

        set_txta_display();

    }//GEN-LAST:event_BT_ADD_GROUPActionPerformed

    private void BT_DEL_ELEMActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_DEL_ELEMActionPerformed
    {//GEN-HEADEREND:event_BT_DEL_ELEMActionPerformed
        // TODO add your handling code here:
        remove_selected_entry();
        set_txta_display();

    }//GEN-LAST:event_BT_DEL_ELEMActionPerformed

    private void BT_CLOSEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_CLOSEActionPerformed
    {//GEN-HEADEREND:event_BT_CLOSEActionPerformed
        // TODO add your handling code here:
        setOkay(true);
        this.my_dlg.setVisible(false);
        XStream xstr = new XStream();
        try
        {
            FileOutputStream fos = new FileOutputStream("last_rule.txt");
            xstr.toXML(model.getChildren(), fos);
            fos.close();
        }
        catch (IOException iOException)
        {
        }
    }//GEN-LAST:event_BT_CLOSEActionPerformed

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ABORTActionPerformed
    {//GEN-HEADEREND:event_BT_ABORTActionPerformed
        // TODO add your handling code here:
        setOkay(false);
        this.setVisible(false);
    }//GEN-LAST:event_BT_ABORTActionPerformed

    private void COMBO_NAMEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_COMBO_NAMEActionPerformed
    {//GEN-HEADEREND:event_COMBO_NAMEActionPerformed
        // TODO add your handling code here:
        if (!in_init)
            set_operation( COMBO_NAME.getSelectedIndex() );
    }//GEN-LAST:event_COMBO_NAMEActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_ADD_EXPR;
    private javax.swing.JButton BT_ADD_GROUP;
    private javax.swing.JButton BT_CLOSE;
    private javax.swing.JButton BT_DEL_ELEM;
    private javax.swing.JCheckBox BT_NEG;
    private javax.swing.JComboBox COMBO_AND_OR;
    private javax.swing.JComboBox COMBO_NAME;
    private javax.swing.JComboBox COMBO_OPERATION;
    private javax.swing.JScrollPane SCP_LIST;
    private javax.swing.JTextArea TXTA_LIST;
    private javax.swing.JTextField TXT_VAR;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    @Override
    public JButton get_default_button()
    {
        return BT_CLOSE;
    }

    /**
     * @return the okay
     */
    public boolean isOkay()
    {
        return okay;
    }

    /**
     * @param okay the okay to set
     */
    public void setOkay( boolean okay )
    {
        this.okay = okay;
    }


}
