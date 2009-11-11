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
import home.shared.filter.GroupEntry;
import home.shared.filter.LogicEntry;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.text.BadLocationException;




class CB_op_entry
{
    ExprEntry.OPERATION op;

    public CB_op_entry( OPERATION op )
    {
        this.op = op;
    }

    @Override
    public String toString()
    {
        return RoleFilter.get_op_nice_txt(op);
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
public class RoleFilter extends GlossDialogPanel
{
    LogicEntryModel model;

    private boolean okay;

    /** Creates new form RoleFilter */
    public RoleFilter(ArrayList<String> var_names, String compressed_list_str)
    {
        initComponents();

        // READ COMPRESSED FILTER XML DATA
        ArrayList<LogicEntry> list = get_filter_list( compressed_list_str );

        model = new LogicEntryModel(list);

        set_txta_display();

        COMBO_AND_OR.removeAllItems();
        COMBO_NAME.removeAllItems();
        COMBO_OPERATION.removeAllItems();

        COMBO_AND_OR.addItem( UserMain.Txt("And"));
        COMBO_AND_OR.addItem( UserMain.Txt("Or"));

        COMBO_OPERATION.addItem( new CB_op_entry( OPERATION.BEGINS_WITH ) );
        COMBO_OPERATION.addItem( new CB_op_entry( OPERATION.CONTAINS ) );
        COMBO_OPERATION.addItem( new CB_op_entry( OPERATION.ENDS_WITH ) );
        COMBO_OPERATION.addItem( new CB_op_entry( OPERATION.EXACTLY ) );
        COMBO_OPERATION.addItem( new CB_op_entry( OPERATION.REGEXP ) );

        for (int i = 0; i < var_names.size(); i++)
        {
            String string = var_names.get(i);
            COMBO_NAME.addItem(string);            
        }
    }

    String get_compressed_xml_list_data()
    {
        String xml = null;
        XStream xstr = new XStream();
        xml = xstr.toXML(model.getChildren());
        String compressed_list_str = null;
        try
        {
            compressed_list_str = ZipUtilities.compress(xml);
        }
        catch (Exception e)
        {
            UserMain.errm_ok(UserMain.Txt("Invalid_role_filter,_resetting_to_empty_list"));
            compressed_list_str = "";
        }
        return compressed_list_str;
    }

    static String get_op_nice_txt( ExprEntry.OPERATION operation )
    {
        switch( operation )
        {
            case BEGINS_WITH: return UserMain.Txt("Begins_with");
            case ENDS_WITH: return UserMain.Txt("Ends_with");
            case CONTAINS: return UserMain.Txt("Contains");
            case EXACTLY: return UserMain.Txt("Is_Exactly");
            case REGEXP: return UserMain.Txt("Matches_regular_expression");
        }
        return "???";
    }
    static String get_nice_txt( ExprEntry e)
    {
        return (e.isNeg() ? UserMain.Txt("not") + " " : "") + UserMain.Txt(e.getName()) + " " + get_op_nice_txt( e.getOperation() ) + " " + e.getValue();
    }

    public static ArrayList<LogicEntry> get_filter_list( String compressed_list_str )
    {
        ArrayList<LogicEntry> list = null;
        if (compressed_list_str.length() == 0)
        {
            list = new ArrayList<LogicEntry>();
        }
        else
        {
            try
            {
                String xml_list_str = ZipUtilities.uncompress(compressed_list_str);
                XStream xstr = new XStream();

                list = (ArrayList<LogicEntry>) xstr.fromXML(xml_list_str);
            }
            catch (Exception e)
            {
                UserMain.errm_ok(UserMain.Txt("Invalid_role_filter,_resetting_to_empty_list"));
                list = new ArrayList<LogicEntry>();
            }
        }
        return list;

    }
    static String get_nice_filter_text( String compressed_list_str )
    {
        ArrayList<LogicEntry> list = get_filter_list( compressed_list_str );

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

    int get_append_idx(ArrayList<LogicEntry> parent_list)
    {
        LogicEntry last_entry = get_actual_entry();
        int append_idx = -1;
        if (!( last_entry instanceof GroupEntry))
        {
            for (int i = 0; i < parent_list.size(); i++)
            {
                LogicEntry logicEntry = parent_list.get(i);
                if (logicEntry == last_entry)
                {
                    append_idx = i;
                    break;
                }
            }
        }
        return append_idx;
    }


    void add_new_group( boolean neg, boolean previous_is_or )
    {
        ArrayList<LogicEntry> parent_list = get_act_parent_list();

        int append_idx = get_append_idx(parent_list);

        if (append_idx != -1)
            parent_list.add( append_idx, new GroupEntry( parent_list, neg, previous_is_or) );
        else
            parent_list.add( new GroupEntry( parent_list, neg, previous_is_or) );

   //     model.rebuild_list();
    }
    
    void add_new_expression( String name, String value, ExprEntry.OPERATION operation, boolean neg, boolean previous_is_or )
    {
        ArrayList<LogicEntry> parent_list = get_act_parent_list();

        int append_idx = get_append_idx(parent_list);

        if (append_idx != -1)
            parent_list.add( append_idx, new ExprEntry( parent_list, name, value, operation, neg, previous_is_or) );
        else
            parent_list.add( new ExprEntry( parent_list, name, value, operation, neg, previous_is_or) );

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
        jLabel2 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        SCP_LIST = new javax.swing.JScrollPane();
        TXTA_LIST = new javax.swing.JTextArea();
        COMBO_OPERATION = new javax.swing.JComboBox();
        TXT_VAR = new javax.swing.JTextField();
        BT_ADD_EXPR = new javax.swing.JButton();
        BT_DEL_ELEM = new javax.swing.JButton();
        BT_ADD_GROUP = new javax.swing.JButton();
        BT_NEG = new javax.swing.JCheckBox();
        COMBO_AND_OR = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        BT_CLOSE = new GlossButton();
        BT_ABORT = new GlossButton();
        jLabel4 = new javax.swing.JLabel();

        jLabel1.setText("Where");

        COMBO_NAME.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel2.setText("op");

        TXTA_LIST.setColumns(20);
        TXTA_LIST.setRows(5);
        TXTA_LIST.setTabSize(4);
        SCP_LIST.setViewportView(TXTA_LIST);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, 552, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SCP_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, 203, Short.MAX_VALUE)
        );

        COMBO_OPERATION.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        TXT_VAR.setText("jTextField1");

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

        COMBO_AND_OR.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel3.setText("jLabel3");

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

        jLabel4.setFont(new java.awt.Font("Tahoma", 1, 24)); // NOI18N
        jLabel4.setText("Ugly and not ready!!!");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 374, Short.MAX_VALUE)
                        .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(BT_CLOSE, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(BT_ADD_EXPR, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(BT_ADD_GROUP, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(BT_DEL_ELEM, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 147, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 155, Short.MAX_VALUE)
                        .addComponent(jLabel4))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(COMBO_NAME, 0, 139, Short.MAX_VALUE)
                            .addComponent(COMBO_AND_OR, 0, 139, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(COMBO_OPERATION, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(TXT_VAR, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE))
                            .addComponent(BT_NEG))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BT_ADD_EXPR)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_ADD_GROUP)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(BT_DEL_ELEM)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(COMBO_AND_OR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(BT_NEG)))
                    .addComponent(jLabel4))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(COMBO_NAME, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2)
                    .addComponent(COMBO_OPERATION, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TXT_VAR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_CLOSE, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_ADD_EXPRActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ADD_EXPRActionPerformed
    {//GEN-HEADEREND:event_BT_ADD_EXPRActionPerformed
        // TODO add your handling code here:
        String name = COMBO_NAME.getSelectedItem().toString();
        String value = TXT_VAR.getText();
        CB_op_entry ope = (CB_op_entry) COMBO_OPERATION.getSelectedItem();
        boolean neg = BT_NEG.isSelected();
        boolean is_or = false;
        if (COMBO_AND_OR.getSelectedIndex() == 1)
            is_or = true;
        
        add_new_expression( name, value, ope.op, neg, is_or);

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
    private javax.swing.JLabel jLabel2;
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
