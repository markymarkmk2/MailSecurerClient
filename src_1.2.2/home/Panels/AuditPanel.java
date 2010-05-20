/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * LogPanel.java
 *
 * Created on 19.01.2010, 20:12:55
 */

package dimm.home.Panels;

import dimm.home.Rendering.BoolButtonCellRenderer;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossDialogPanel;
import dimm.home.Rendering.GlossTable;
import dimm.home.ServerConnect.ConnectionID;
import dimm.home.ServerConnect.ResultSetID;
import dimm.home.ServerConnect.ServerCall;
import dimm.home.ServerConnect.StatementID;
import dimm.home.UserMain;
import home.shared.SQL.SQLArrayResult;
import home.shared.SQL.SQLResult;
import home.shared.hibernate.Mandant;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableRowSorter;



class AuditTableModel extends AbstractTableModel
{
    String[] col_names = {UserMain.getString("Time"), UserMain.getString("Mandant"), UserMain.getString("Usertype"), UserMain.getString("User"), UserMain.getString("Role"), UserMain.getString("Command"), UserMain.getString("Args"), UserMain.getString("Error"), UserMain.getString("Answer")};
    Class[] col_classes = {String.class, String.class, String.class,  String.class,  String.class,  String.class,  String.class,  Boolean.class, String.class};

    protected SQLArrayResult sqlResult;
    AuditPanel panel;

    SimpleDateFormat sdf;

    
    String get_field_list()
    {
        return "ma_id,ts,usertype, username, role_name, cmd, args, answer";
    }

    public AuditTableModel(AuditPanel _panel)
    {
        panel = _panel;
        sdf = new SimpleDateFormat("dd.MM.yyy  HH:mm:ss.SSS");
    }

    public void setSqlResult( SQLArrayResult _sqlResult )
    {
        sqlResult = _sqlResult;        
    }

    @Override
    public String getColumnName(int column)
    {
        return col_names[column];
    }

    @Override
    public Class<?> getColumnClass(int columnIndex)
    {
        return col_classes[columnIndex];
    }

    @Override
    public int getRowCount()
    {
        if (sqlResult != null)
            return sqlResult.getRows();

        return 0;
    }

    @Override
    public int getColumnCount()
    {
        int cols = col_names.length;
/*        if (!panel.show_args())
            cols--;

        if (!panel.show_answer())
            cols--;
*/
        return cols;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (sqlResult == null)
            return null;


        switch (columnIndex)
        {
            case 0: return get_time_str( sqlResult.getLong(rowIndex, "ts") );
            case 1: return get_ma_str( sqlResult.getInt(rowIndex, "ma_id") );
            case 2: return sqlResult.getString(rowIndex, "usertype" );
            case 3: return sqlResult.getString(rowIndex, "username" );
            case 4: return sqlResult.getString(rowIndex, "role_name" );
            case 5: return sqlResult.getString(rowIndex, "cmd" );

            case 6: return (panel.show_args()) ? sqlResult.getString(rowIndex, "args" ) : "";
            case 7: 
            {
                if (!panel.show_answer())
                    return new Boolean(false);
                
                String answer = sqlResult.getString(rowIndex, "answer" );
                if (answer.length() > 1 && Character.isDigit( answer.charAt(0) ) && answer.indexOf(':') > 0)
                {
                    if (answer.charAt(0) != '0')
                        return new Boolean(true);

                }
                return new Boolean(false);
            }
            case 8: 
            {
                if (!panel.show_answer())
                    return "";
                
                return sqlResult.getString(rowIndex, "answer" );
            }
        }
        return "???";
    }

    private Object get_time_str( long aLong )
    {
        Date d = new Date(aLong);
        return sdf.format(d);
    }

    private String get_ma_str( int aLong )
    {
        return  panel.get_ma_str( aLong );
    }

    long get_timeval( String t ) throws ParseException
    {
        Date d = sdf.parse(t);
        return d.getTime();
    }

}
/**
 *
 * @author mw
 */
class MandantEntry
{
    public static final int ME_SYSTEM = -1;
    public static final int ME_ALL = -2;

    Mandant m;
    int id;

    public MandantEntry( Mandant m )
    {
        this.m = m;
        id = m.getId();
    }
    public MandantEntry( int _id )
    {
        this.m = null;
        id = _id;
    }

    public int getId()
    {
        return id;
    }



    @Override
    public String toString()
    {
        if (m != null)
            return m.getName();

        if (id == ME_SYSTEM)
            return UserMain.Txt("System");

        return UserMain.Txt("All");
    }

}
public class AuditPanel extends GlossDialogPanel  implements MouseListener, ActionListener
{

    AuditTableModel model;
    GlossTable table;
    HashMap<String,Mandant> ma_map;
    int ma_id;

    public AuditPanel(int ma_id)
    {
        this.ma_id = ma_id;
        
        initComponents();

        SQLResult<Mandant> mr = UserMain.sqc().get_mandant_result();

        CB_MANDANT.removeAllItems();
        if (ma_id < 0)
        {
            CB_MANDANT.addItem( new MandantEntry(MandantEntry.ME_ALL));
        }
        CB_MANDANT.addItem( new MandantEntry(MandantEntry.ME_SYSTEM));

        ma_map = new HashMap<String,Mandant>();

        for (int i = 0; i < mr.size(); i++)
        {
            Mandant mandant = mr.get(i);
            if (ma_id >= 0)
            {
                if (mandant.getId() != ma_id)
                    continue;
            }
            CB_MANDANT.addItem( new MandantEntry(mandant));

            ma_map.put(Integer.toString(mandant.getId()), mandant);
        }

        model = new AuditTableModel( this);

        RowSorter sorter = new TableRowSorter(model);
        table = new GlossTable();
        table.setDefaultRenderer(Boolean.class, new BoolButtonCellRenderer(false,
                "/dimm/home/images/web_delete.png",
                "/dimm/home/images/ok_empty.png"));
        table.setModel(model);
        table.setRowSorter(sorter);
        table.addMouseListener(this);

        // REGISTER TABLE TO SCROLLPANEL
        table.embed_to_scrollpanel( SCP_TABLE );

        set_table_header();

        
    }


    public void set_table_header()
    {
        TableColumnModel cm = table.getTableHeader().getColumnModel();
        // TS
        cm.getColumn(0).setMinWidth(130);
        cm.getColumn(0).setMaxWidth(130);

        // MA
        cm.getColumn(1).setPreferredWidth(50);
        cm.getColumn(1).setPreferredWidth(50);
        // UTYPE
        cm.getColumn(2).setPreferredWidth(50);
        cm.getColumn(2).setMaxWidth(80);
        // UNAME
        cm.getColumn(3).setPreferredWidth(50);
        // ROLE
        cm.getColumn(4).setPreferredWidth(50);
        cm.getColumn(4).setMaxWidth(120);
        // CMD
        cm.getColumn(5).setPreferredWidth(50);
        cm.getColumn(5).setMaxWidth(120);
        // CMD

        // ARGS
        if (show_args())
        {
            cm.getColumn(6).setMinWidth(0);
            cm.getColumn(6).setMaxWidth(1000);
            cm.getColumn(6).setPreferredWidth(280);
        }
        else
        {
            cm.getColumn(6).setMinWidth(0);
            cm.getColumn(6).setMaxWidth(0);
            cm.getColumn(6).setPreferredWidth(0);
        }
        // ANSWER
        if (show_answer())
        {
            cm.getColumn(7).setMinWidth(30);
            cm.getColumn(7).setMaxWidth(30);
            cm.getColumn(7).setPreferredWidth(30);
            cm.getColumn(8).setMinWidth(0);
            cm.getColumn(8).setMaxWidth(1000);
            cm.getColumn(8).setPreferredWidth(280);
        }
        else
        {
            cm.getColumn(7).setMinWidth(0);
            cm.getColumn(7).setMaxWidth(0);
            cm.getColumn(8).setMinWidth(0);
            cm.getColumn(8).setMaxWidth(0);
            cm.getColumn(8).setPreferredWidth(0);
        }
    }

    void _search() throws ParseException
    {
        int me_id = MandantEntry.ME_ALL;
        if (ma_id >= 0)
        {
            me_id = ma_id;
        }
        else
        {
            MandantEntry me = (MandantEntry) CB_MANDANT.getSelectedItem();
            me_id = me.getId();
        }
        String uname = TXT_USER.getText();
        String cmd  = TXT_CMD.getText();
        String from = TXT_FROM.getText();
        String till = TXT_TILL.getText();



        String qry =  "select " + model.get_field_list() + " from audit_log where 1=1";

        if (me_id != MandantEntry.ME_ALL)
            qry += " and ma_id=" + me_id;

        if (uname.length() > 0)
        {
            qry += " and username like '" + uname + "%'";
        }
        if (cmd.length() > 0)
        {
            qry += " and cmd like '%" + cmd + "%'";
        }
        if (from.length() > 0)
        {
            long l = model.get_timeval(from);
            qry += " and ts>'" + l + "'";
        }
        if (till.length() > 0)
        {
            long l = model.get_timeval(till);
            if (l> 0)
                qry += " and ts<'" + l + "'";
        }

        qry += " order by ts desc";

        ServerCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open_audit("");
        StatementID sid = sql.createStatement(cid);
        ResultSetID rid = sql.executeQuery(sid, qry);
        SQLArrayResult resa = sql.get_sql_array_result(rid);
        

        model.setSqlResult(resa);
        sql.close(cid);

        repaint_table();
    }

    void repaint_table()
    {
        table.tableChanged(new TableModelEvent(table.getModel()) );
        set_tabel_row_height();
        set_table_header();
        table.repaint();
        
    }

    void search()
    {
        try
        {
            // TODO add your handling code here:
            _search();
        }
        catch (ParseException ex)
        {
            UserMain.errm_ok(my_dlg, UserMain.Txt("Invalid_search_criteria"));
        }
    }

    
    String get_ma_str( int aLong )
    {
        if (aLong == -1)
            return "System";
        Mandant m = ma_map.get(Integer.toString(aLong));

        if (m == null)
            return "Mandant " + aLong;

        return m.getName();
    }

    public void set_tabel_row_height()
    {
        packRows( table, table.getRowMargin() );
        table.repaint();
    }

    public int getPreferredRowHeight(JTable table, int rowIndex, int margin)
    {
        // Get the current default height for all rows
        int height = table.getRowHeight();

        // Determine highest cell in the row
        for (int c=0; c<table.getColumnCount(); c++)
        {
            TableCellRenderer renderer = table.getCellRenderer(rowIndex, c);
            Component comp = table.prepareRenderer(renderer, rowIndex, c);
           
            int w = table.getColumnModel().getColumn(c).getWidth();
            if (w == 0)
                w = comp.getPreferredSize().width;
            if (w > 0)
            {
                int n = (comp.getPreferredSize().width + w - 1) / w;

                int h = n*comp.getPreferredSize().height + 2*margin;

                if (h > height)
                    height = h;
            }
        }
        return height;
    }

    public void packRows(JTable table, int margin)
    {
        for (int r=0; r<table.getRowCount(); r++)
        {
            // Get the preferred height
            int h = getPreferredRowHeight(table, r, margin);

            // Now set the row height using the preferred height

            table.setRowHeight(r, h);
            //System.out.println("Rowheight row " + r + " = " + h);

        }
    }
    @Override
    public void setSize(Dimension d)
    {
        set_tabel_row_height();
        super.setSize(d);
    }
 

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        LOG_PANEL = new javax.swing.JPanel();
        SCP_TABLE = new javax.swing.JScrollPane();
        BT_OK = new GlossButton();
        jPanel1 = new javax.swing.JPanel();
        CB_MANDANT = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        TXT_USER = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        TXT_FROM = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        TXT_TILL = new javax.swing.JTextField();
        BT_SEARCH = new javax.swing.JButton();
        CB_SHOW_ANSWER = new javax.swing.JCheckBox();
        CB_SHOW_ARGS = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        TXT_CMD = new javax.swing.JTextField();
        BT_EXPORT = new javax.swing.JButton();

        LOG_PANEL.setMinimumSize(new java.awt.Dimension(600, 200));
        LOG_PANEL.setOpaque(false);
        LOG_PANEL.setPreferredSize(new java.awt.Dimension(600, 200));
        LOG_PANEL.setLayout(new javax.swing.BoxLayout(LOG_PANEL, javax.swing.BoxLayout.LINE_AXIS));
        LOG_PANEL.add(SCP_TABLE);

        BT_OK.setText(UserMain.getString("OK")); // NOI18N
        BT_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKActionPerformed(evt);
            }
        });

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(UserMain.Txt("Searchcriteria"))); // NOI18N

        CB_MANDANT.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setText(UserMain.Txt("Mandant")); // NOI18N

        jLabel2.setText(UserMain.Txt("Username")); // NOI18N

        jLabel3.setText(UserMain.Txt("von")); // NOI18N

        TXT_FROM.setEditable(false);
        TXT_FROM.setOpaque(false);
        TXT_FROM.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_FROMMouseClicked(evt);
            }
        });

        jLabel4.setText(UserMain.Txt("bis")); // NOI18N

        TXT_TILL.setEditable(false);
        TXT_TILL.setOpaque(false);
        TXT_TILL.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                TXT_TILLMouseClicked(evt);
            }
        });

        BT_SEARCH.setIcon(new javax.swing.ImageIcon(getClass().getResource("/dimm/home/images/suchen.png"))); // NOI18N
        BT_SEARCH.setContentAreaFilled(false);
        BT_SEARCH.setFocusPainted(false);
        BT_SEARCH.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_SEARCHActionPerformed(evt);
            }
        });

        CB_SHOW_ANSWER.setText(UserMain.Txt("Show_answer")); // NOI18N
        CB_SHOW_ANSWER.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CB_SHOW_ANSWERMouseClicked(evt);
            }
        });

        CB_SHOW_ARGS.setText(UserMain.Txt("Show_args")); // NOI18N
        CB_SHOW_ARGS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                CB_SHOW_ARGSMouseClicked(evt);
            }
        });

        jLabel5.setText(UserMain.Txt("Command")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(CB_MANDANT, 0, 127, Short.MAX_VALUE)
                    .addComponent(TXT_USER, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                    .addComponent(TXT_CMD, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(TXT_FROM, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(CB_SHOW_ARGS)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 85, Short.MAX_VALUE)
                        .addComponent(BT_SEARCH))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(16, 16, 16)
                        .addComponent(TXT_TILL, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(CB_SHOW_ANSWER)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(CB_MANDANT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TXT_FROM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3)
                            .addComponent(CB_SHOW_ARGS))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(TXT_USER, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TXT_TILL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4)
                            .addComponent(CB_SHOW_ANSWER)))
                    .addComponent(BT_SEARCH))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(TXT_CMD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(13, Short.MAX_VALUE))
        );

        BT_EXPORT.setText(UserMain.Txt("Export")); // NOI18N
        BT_EXPORT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_EXPORTActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(LOG_PANEL, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 627, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BT_EXPORT)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 511, Short.MAX_VALUE)
                        .addComponent(BT_OK))
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(LOG_PANEL, javax.swing.GroupLayout.DEFAULT_SIZE, 247, Short.MAX_VALUE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OK)
                    .addComponent(BT_EXPORT))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKActionPerformed
    {//GEN-HEADEREND:event_BT_OKActionPerformed

        setVisible(false);
}//GEN-LAST:event_BT_OKActionPerformed

    private void TXT_FROMMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXT_FROMMouseClicked
    {//GEN-HEADEREND:event_TXT_FROMMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_TXT_FROMMouseClicked

    private void TXT_TILLMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_TXT_TILLMouseClicked
    {//GEN-HEADEREND:event_TXT_TILLMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_TXT_TILLMouseClicked

    private void BT_EXPORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_EXPORTActionPerformed
    {//GEN-HEADEREND:event_BT_EXPORTActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_BT_EXPORTActionPerformed

    private void BT_SEARCHActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_SEARCHActionPerformed
    {//GEN-HEADEREND:event_BT_SEARCHActionPerformed
       
        search();
    }//GEN-LAST:event_BT_SEARCHActionPerformed

    private void CB_SHOW_ARGSMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_CB_SHOW_ARGSMouseClicked
    {//GEN-HEADEREND:event_CB_SHOW_ARGSMouseClicked
        // TODO add your handling code here:
        repaint_table();
    }//GEN-LAST:event_CB_SHOW_ARGSMouseClicked

    private void CB_SHOW_ANSWERMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_CB_SHOW_ANSWERMouseClicked
    {//GEN-HEADEREND:event_CB_SHOW_ANSWERMouseClicked
        // TODO add your handling code here:
        repaint_table();
    }//GEN-LAST:event_CB_SHOW_ANSWERMouseClicked

    static File last_dir;
    static File last_file;


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_EXPORT;
    private javax.swing.JButton BT_OK;
    private javax.swing.JButton BT_SEARCH;
    private javax.swing.JComboBox CB_MANDANT;
    private javax.swing.JCheckBox CB_SHOW_ANSWER;
    private javax.swing.JCheckBox CB_SHOW_ARGS;
    private javax.swing.JPanel LOG_PANEL;
    private javax.swing.JScrollPane SCP_TABLE;
    private javax.swing.JTextField TXT_CMD;
    private javax.swing.JTextField TXT_FROM;
    private javax.swing.JTextField TXT_TILL;
    private javax.swing.JTextField TXT_USER;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    // End of variables declaration//GEN-END:variables

    @Override
    public JButton get_default_button()
    {
        return BT_OK;
    }

    /** Closes the dialog */
    @Override
    public void mouseExited(java.awt.event.MouseEvent mouseEvent)
    {
    }

    @Override
    public void mouseReleased(java.awt.event.MouseEvent mouseEvent)
    {
    }

    @Override
    public void mousePressed(java.awt.event.MouseEvent mouseEvent)
    {
    }

    @Override
    public void mouseClicked(java.awt.event.MouseEvent mouseEvent)
    {

    }

    @Override
    public void mouseEntered(java.awt.event.MouseEvent mouseEvent)
    {
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent actionEvent)
    {
    }

    @Override
    public void deactivate()
    {
        super.deactivate();
    }

    boolean show_args()
    {
        return CB_SHOW_ARGS.isSelected();
    }

    boolean show_answer()
    {
        return CB_SHOW_ANSWER.isSelected();
    }

}
