/*
 * NewJDialog.java
 *
 * Created on 20. M�rz 2008, 22:44
 */
package dimm.home.Models;

import dimm.home.Rendering.GlossButton;
import dimm.home.Rendering.GlossPanel;
import dimm.home.Rendering.GlossTable;
import dimm.home.Rendering.TitlePanel;
import dimm.home.Rendering.SQLDialog;
import dimm.home.ServerConnect.SQLCall;
import dimm.home.ServerConnect.SQLResult;
import dimm.home.UserMain;
import dimm.home.hibernate.Hotfolder;
import java.beans.PropertyChangeEvent;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JButton;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableColumnModel;



class HotfolderTableModel extends OverviewModel
{
    public static final String ID = "Id";
    public static final String PATH = "Path";
    public static final String EMAIL = "Usermailadress";
    
    public HotfolderTableModel(UserMain _main, HotfolderOverview dlg)
    {
        super( _main, dlg );
        
        String[] _col_names = {ID,UserMain.getString(PATH), UserMain.getString(EMAIL), UserMain.getString("Disabled"), UserMain.getString("Bearbeiten"), UserMain.getString("L�schen")};
        Class[] _col_classes = {String.class,  String.class,  String.class,  Boolean.class, JButton.class, JButton.class};
        set_columns( _col_names, _col_classes ); 
            
    }
    
    @Override
    public String get_qry(long mandanten_id)
    {
        String qry = "from Hotfolder where mid='" + mandanten_id + "' order by path";
        return qry;
    }

  

    @Override
    public Object getValueAt(int rowIndex, int columnIndex)
    {
        if (sqlResult == null)
            return null;
        
        switch (columnIndex)
        {
            case 0:
                return sqlResult.getString(rowIndex, ID); // ID
            case 1:
                return sqlResult.getString(rowIndex, PATH); // NAME
            case 2:
                return sqlResult.getString(rowIndex, EMAIL); // LOGINNAME
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

    
}
/**
 *
 * @author  mw
 */
public class HotfolderOverview extends SQLDialog implements PropertyChangeListener
{

    UserMain main;    
    GlossTable table;
    GlossPanel pg_painter;
    
    
        
  
    /** Creates new form NewJDialog */
    public HotfolderOverview(UserMain parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
        
        main = parent;

        TitlePanel titlePanel = new TitlePanel(this);
        PN_TITLE.add(titlePanel);
        titlePanel.installListeners();
        
        model = new HotfolderTableModel(main, this);
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
     
    HotfolderTableModel get_hf_model()
    {
        return (HotfolderTableModel) model;
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
        SQLCall sql = UserMain.get_sqc();
        SQLResult<Hotfolder>  res = new SQLResult<Hotfolder>();

        String qry =  model.get_qry( firmen_id );
        if (sql.call_qry(qry, res))
            model.setSqlResult(res);
        else
            model.setSqlResult(null);

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
      /*      EditBoxUser pnl = new EditBoxUser( row, this );
            GenericGlossyDlg dlg = new GenericGlossyDlg( null, true, pnl );

            pnl.addPropertyChangeListener("REBUILD", this);
                        
            dlg.set_next_location(this);
            dlg.setVisible(true );                */
        }
        if (col == model.get_del_column())
        {
            SQLCall sql = UserMain.get_sqc();
            
            // GET BSTREAMLIST FOR THIS STATIONS

            String path = model.getSqlResult().getString(row, "Path");
            long id = model.getSqlResult().getLong(row, "Id");

            if (UserMain.errm_ok_cancel(UserMain.getString("Wollen_Sie_wirklich_diesen_Eintrag_loeschen") + ": <" + path + "> ?"))
            {
                sql.sql_call( "delete from vb_user where vbu_id=" + id);
                sql.sql_call( "delete from vbu_link where vbul_vbu_id=" + id);
                        
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
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
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
        BT_NEW.setText(UserMain.Txt("Neuen_Hotfolder_hinzufuegen")); // NOI18N
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
    }// </editor-fold>//GEN-END:initComponents

    private void BT_NEWActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BT_NEWActionPerformed
        // TODO add your handling code here:
        
                new_hotfolder();
         
}//GEN-LAST:event_BT_NEWActionPerformed

    private void BT_QUITActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_BT_QUITActionPerformed
        // TODO add your handling code here:
        this.setVisible(false);
    }//GEN-LAST:event_BT_QUITActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_NEW;
    private javax.swing.JButton BT_QUIT;
    private javax.swing.JPanel PN_BUTTONS;
    private org.jdesktop.swingx.JXPanel PN_MAIN;
    private javax.swing.JPanel PN_TABLE;
    private javax.swing.JPanel PN_TITLE;
    private javax.swing.JScrollPane SCP_TABLE;
    // End of variables declaration//GEN-END:variables


    public void new_hotfolder()
    {
    /*    EditBoxUser pnl = new EditBoxUser( -1, this );
        pnl.addPropertyChangeListener("REBUILD", this);
         
        GenericGlossyDlg dlg = new GenericGlossyDlg( null, true, pnl );
        if (dlg.isVisible())
            dlg.set_next_location(this);
        else 
            dlg.setLocationRelativeTo(null);
         
        dlg.setVisible( true ); */
    }
    
    
    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        table.tableChanged(new TableModelEvent(table.getModel()) );        
        gather_sql_result();
        set_tabel_row_height();
        
        this.repaint();
    }
}
