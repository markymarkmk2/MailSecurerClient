/*
 * NewJDialog.java
 *
 * Created on 20. M�rz 2008, 22:44
 */
package dimm.home.Panels;

import dimm.home.Models.OverviewModel;
import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossPanel;
import dimm.home.Rendering.GlossTable;
import dimm.home.Rendering.TitlePanel;
import dimm.home.Rendering.SQLOverviewDialog;
import dimm.home.ServerConnect.SQLCall;
import dimm.home.UserMain;
import java.beans.PropertyChangeEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumnModel;

import dimm.general.SQL.*;
import dimm.general.hibernate.*;
import dimm.home.ServerConnect.ConnectionID;
import dimm.home.ServerConnect.ResultSetID;
import dimm.home.ServerConnect.StatementID;


class MilterTableModel extends OverviewModel
{
    public static final String ID = "Id";
    public static final String PATH = "Path";
    public static final String EMAIL = "Usermailadress";


    public MilterTableModel(UserMain _main, MilterOverview dlg)
    {
        super( _main, dlg );

        String[] _col_names = {ID,UserMain.getString(PATH), UserMain.getString(EMAIL), UserMain.getString("Disabled"), UserMain.getString("Bearbeiten"), UserMain.getString("Löschen")};
        Class[] _col_classes = {String.class,  String.class,  String.class,  Boolean.class, JButton.class, JButton.class};
        set_columns( _col_names, _col_classes );

    }

    @Override
    public String get_qry(long mandanten_id)
    {
        String qry = "select * from milter where mid='" + mandanten_id + "' order by id";
        return qry;
    }



    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (sqlResult == null)
            return null;

        Milter milter = new Milter();
        milter = (Milter)sqlResult.get(rowIndex);

        switch (columnIndex)
        {
            case 0:
                return milter.getId(); // ID
            case 1:
                return "=";//milter.getPath(); // NAME
            case 2:
                return "L";//milter.getUsermailadress(); // LOGINNAME
            case 3:
                int flags = sqlResult.getInt(rowIndex, "Flags");
                return new Boolean((flags & 0x01) == 0x01); // DISABLED
            default:
                return super.getValueAt(rowIndex, columnIndex);
        }
    }
    @Override
    public int getColumnCount()
    {

        // EDIT IST 2.LAST ROW!!!!
        if (UserMain.self.getUserLevel() < UserMain.UL_ADMIN)
            return col_names.length - 2;

        // DELETE IST LAST ROW!!!!
        if (UserMain.self.getUserLevel() < UserMain.UL_MULTIADMIN)
            return col_names.length - 1;

        return col_names.length;
    }
    public Milter get_object( int index )
    {
        return (Milter) sqlResult.get(index);
    }


}
/**
 *
 * @author  mw
 */
public class MilterOverview extends SQLOverviewDialog implements PropertyChangeListener
{

    UserMain main;
    GlossTable table;
    GlossPanel pg_painter;

    public static final int HF_DISABLED =   0x01;



    /** Creates new form NewJDialog */
    public MilterOverview(UserMain parent, boolean modal)
    {
        super(parent, modal);
        initComponents();

        main = parent;

        TitlePanel titlePanel = new TitlePanel(this);
        PN_TITLE.add(titlePanel);
        titlePanel.installListeners();

        model = new MilterTableModel(main, this);
        table = new GlossTable();

        table.setModel(model);
        table.addMouseListener(this);

        // REGISTER TABLE TO SCROLLPANEL
        table.embed_to_scrollpanel( SCP_TABLE );

        if (UserMain.self.getUserLevel() < UserMain.UL_MULTIADMIN)
            this.BT_NEW.setVisible(false);

        pack();

        create_sql_worker();
    }

    MilterTableModel get_object_model()
    {
        return (MilterTableModel) model;
    }


    @Override
    public void set_tabel_row_height()
    {
        packRows( table, table.getRowMargin() );
        table.repaint();
    }

    @Override
    public void set_table_header()
    {
        TableColumnModel cm = table.getTableHeader().getColumnModel();
        cm.getColumn(0).setMinWidth(40);
        cm.getColumn(0).setMaxWidth(40);
        cm.getColumn(1).setPreferredWidth(150);

        model.set_table_header(cm);
    }


    @Override
    public void gather_sql_result()
    {
        gather_sql_result( main.get_firmen_id() );
    }

    @Override
    public void gather_sql_result(long firmen_id)
    {
        SQLCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open();
        StatementID sid = sql.createStatement(cid);

        String qry =  model.get_qry( firmen_id );

        ResultSetID rid = sql.executeQuery(sid, qry);
        SQLArrayResult resa = sql.get_sql_array_result(rid);

        SQLResult<Milter>  res = new SQLResult<Milter>(resa, new Milter().getClass());

        model.setSqlResult(res);
        table.tableChanged(new TableModelEvent(table.getModel()) );
        set_tabel_row_height();

        sql.close(cid);



    }



    @Override
    public void setSize(Dimension d)
    {
        pg_painter.setSize(d);
        set_tabel_row_height();
        super.setSize(d);
    }
    @Override
    public void mouseClicked(MouseEvent e)
    {
        Component c = table.getComponentAt(e.getPoint());
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());

        if (col == model.get_edit_column())
        {
            EditMilter pnl = new EditMilter( row, this );
            GenericGlossyDlg dlg = new GenericGlossyDlg( null, true, pnl );

            pnl.addPropertyChangeListener("REBUILD", this);

            dlg.set_next_location(this);
            dlg.setVisible(true );                
        }
        
        if (col == model.get_del_column())
        {

            SQLCall sql = UserMain.sqc().get_sqc();
            String path = model.getSqlResult().getString( row, "path") ;


            if (UserMain.errm_ok_cancel(UserMain.getString("Wollen_Sie_wirklich_diesen_Eintrag_loeschen") + ": <" + path + "> ?"))
            {
                //boolean ret = sql.delete(hf);
              //  if (!ret)
                {
                    UserMain.errm_ok( "Delete failed" );
                }
    
                propertyChange( new PropertyChangeEvent(this, "REBUILD", null, null ) );
            }
        }
        //System.out.println("Row " + row + "Col " + col);
    }


    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        PN_MAIN = new GlossPanel();
        PN_TITLE = new javax.swing.JPanel();
        PN_TABLE = new javax.swing.JPanel();
        SCP_TABLE = new javax.swing.JScrollPane();
        PN_BUTTONS = new javax.swing.JPanel();
        BT_NEW = new GlossButton();
        BT_QUIT = new GlossButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        setUndecorated(true);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        PN_MAIN.setBackground(new java.awt.Color(51, 51, 51));
        PN_MAIN.setLayout(new java.awt.GridBagLayout());

        PN_TITLE.setOpaque(false);
        PN_TITLE.setLayout(new javax.swing.BoxLayout(PN_TITLE, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        PN_MAIN.add(PN_TITLE, gridBagConstraints);

        PN_TABLE.setBackground(new java.awt.Color(51, 51, 51));
        PN_TABLE.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        PN_TABLE.setForeground(new java.awt.Color(255, 255, 255));
        PN_TABLE.setOpaque(false);
        PN_TABLE.setLayout(new javax.swing.BoxLayout(PN_TABLE, javax.swing.BoxLayout.LINE_AXIS));

        SCP_TABLE.setBackground(new java.awt.Color(51, 51, 51));
        SCP_TABLE.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
        SCP_TABLE.setOpaque(false);
        SCP_TABLE.setPreferredSize(new java.awt.Dimension(500, 300));
        PN_TABLE.add(SCP_TABLE);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        PN_MAIN.add(PN_TABLE, gridBagConstraints);

        PN_BUTTONS.setOpaque(false);

        BT_NEW.setForeground(new java.awt.Color(204, 204, 204));
        BT_NEW.setText(UserMain.Txt("Neuen_Milter_hinzufuegen")); // NOI18N
        BT_NEW.setActionCommand("        ");
        BT_NEW.setBorder(null);
        BT_NEW.setContentAreaFilled(false);
        BT_NEW.setMargin(new java.awt.Insets(2, 20, 2, 20));
        BT_NEW.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_NEWActionPerformed(evt);
            }
        });

        BT_QUIT.setForeground(new java.awt.Color(204, 204, 204));
        BT_QUIT.setText(UserMain.Txt("CLOSE_DIALOG")); // NOI18N
        BT_QUIT.setBorder(BT_NEW.getBorder());
        BT_QUIT.setContentAreaFilled(false);
        BT_QUIT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_QUITActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PN_BUTTONSLayout = new javax.swing.GroupLayout(PN_BUTTONS);
        PN_BUTTONS.setLayout(PN_BUTTONSLayout);
        PN_BUTTONSLayout.setHorizontalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap(200, Short.MAX_VALUE)
                .addComponent(BT_NEW, javax.swing.GroupLayout.PREFERRED_SIZE, 187, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BT_QUIT, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(14, 14, 14))
        );
        PN_BUTTONSLayout.setVerticalGroup(
            PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_BUTTONSLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PN_BUTTONSLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_QUIT, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(BT_NEW, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        PN_MAIN.add(PN_BUTTONS, gridBagConstraints);

        getContentPane().add(PN_MAIN);

        pack();
    }// </editor-fold>

    private void BT_NEWActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:

                new_Milter();

}

    private void BT_QUITActionPerformed(java.awt.event.ActionEvent evt) {
        // TODO add your handling code here:
        this.setVisible(false);
    }

    // Variables declaration - do not modify
    private javax.swing.JButton BT_NEW;
    private javax.swing.JButton BT_QUIT;
    private javax.swing.JPanel PN_BUTTONS;
    private org.jdesktop.swingx.JXPanel PN_MAIN;
    private javax.swing.JPanel PN_TABLE;
    private javax.swing.JPanel PN_TITLE;
    private javax.swing.JScrollPane SCP_TABLE;
    // End of variables declaration


    public void new_Milter()
    {
        EditMilter pnl = new EditMilter( -1, this );
        pnl.addPropertyChangeListener("REBUILD", this);

        GenericGlossyDlg dlg = new GenericGlossyDlg( null, true, pnl );
        if (dlg.isVisible())
            dlg.set_next_location(this);
        else
            dlg.setLocationRelativeTo(null);

        dlg.setVisible( true ); 
    }


    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        gather_sql_result();
        table.tableChanged(new TableModelEvent(table.getModel()) );
        set_tabel_row_height();

        this.repaint();
    }
}
