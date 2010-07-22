/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

import dimm.home.Main;
import dimm.home.Models.OverviewModel;
import dimm.home.ServerConnect.ConnectionID;
import dimm.home.ServerConnect.ServerCall;
import dimm.home.ServerConnect.StatementID;
import dimm.home.UserMain;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.event.TableModelEvent;
import javax.swing.table.TableCellRenderer;


/**
 *
 * @author media
 */
public abstract class SQLOverviewDialog extends JDialog  implements MouseListener,  PropertyChangeListener
{
    protected SwingWorker sql_worker;
    
    protected OverviewModel model;

    protected UserMain main;
    protected GlossTable table;
    protected GlossPanel pg_painter;
    protected String name_field;
    private static boolean needs_init;


    public abstract void set_table_header();
        
    public abstract void gather_sql_result();
    public abstract void gather_sql_result(long station_id);

    
    public static boolean needs_init()
    {
        return needs_init;
    }

    public static void set_needs_init( boolean needs_init )
    {
        SQLOverviewDialog.needs_init = needs_init;
    }


    public void set_tabel_row_height()
    {
        packRows( table, table.getRowMargin() );
        table.repaint();
    }

    
    public SQLOverviewDialog( JFrame parent, String _name_field, boolean modal)
    {
        super( parent, modal);
        name_field = _name_field;
    }

    @Override
    public void setSize(Dimension d)
    {
        pg_painter.setSize(d);
        set_tabel_row_height();
        super.setSize(d);
    }
    protected boolean check_valid_cid( ServerCall sq, ConnectionID cid )
    {
        if (cid != null)
            return true;

        // UNITIALIZED
        if (sq.get_last_err_code() == 8)
        {
            UserMain.errm_ok( this, UserMain.Txt("Sorry,_you_have_been_logged_out_due_to_inactivity"));
            setVisible(false);
            return false;
        }

        UserMain.errm_ok(this, UserMain.Txt("Cannot_open_database_connection,_please_logout_and_try_again"));
        setVisible(false);

        return false;

    }

    
 @Override
    public void setVisible( boolean b )
    {
        if (b)
        {
            if (sql_worker != null)
            {
                try
                {
                    while ( !sql_worker.isDone() )
                    {
                        sql_worker.wait(100);
                    }
                    sql_worker = null;
                } catch ( Exception ex )
                {
                }
            }
        }
        super.setVisible(b);
    }
 
    public OverviewModel get_model()
    {
        return model; 
    }
    
    protected SwingWorker create_sql_worker()
    {
        sql_worker = new SwingWorker()
        {

            @Override
            protected Object doInBackground() throws Exception
            {
                 set_table_header();
        
                 gather_sql_result();
                
                 set_tabel_row_height();   
                 
                 return null;
            }        
        };
        sql_worker.execute(); 
        return sql_worker;
    }
    public void wait_for_sql_worker()
    {
        try
        {
            if ( sql_worker != null )
            {
                sql_worker.get();
            }
        }
        catch ( Exception ex )
        {
            ex.printStackTrace();
        }
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
            int h = comp.getPreferredSize().height + 2*margin;
            
            height = Math.max(height, h);
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

    protected abstract GlossDialogPanel get_edit_panel( int row);
      
    void open_edit_dlg( int row )
    {
        GlossDialogPanel pnl = get_edit_panel( row );
        GenericGlossyDlg dlg = new GenericGlossyDlg( null, true, pnl );

        pnl.addPropertyChangeListener("REBUILD", this);

        dlg.set_next_location(this);
        dlg.setVisible(true );
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        Component c = table.getComponentAt(e.getPoint());
        int row = table.rowAtPoint(e.getPoint());
        int col = table.columnAtPoint(e.getPoint());

        if (e.getClickCount() == 1)
        {
            if (col == model.get_edit_column())
            {
                open_edit_dlg( row );
            }

            if (col == model.get_del_column())
            {

                String name = null;
                if (name_field != null)
                    name = model.getSqlResult().getString( row, name_field) ;

                String txt = UserMain.getString("Wollen_Sie_wirklich_diesen_Eintrag_loeschen");
                if (name != null)
                {
                    txt += ": <" + name + ">";
                }

                if (UserMain.errm_ok_cancel( txt + "?" ))
                {
                    boolean okay = del_object( row );

                    propertyChange( new PropertyChangeEvent(this, "REBUILD", null, null ) );
                }
            }
        }

        // DBLCLICK OPENS EDIT TOO
        if (e.getClickCount() == 2)
        {           
             open_edit_dlg( row );
        }

        //System.out.println("Row " + row + "Col " + col);
    }


    protected boolean del_object( int row )
    {
        Object object = model.getSqlResult().get(row);

        ServerCall sql = UserMain.sqc().get_sqc();
        boolean okay = sql.DeleteObject( object);

        if (okay)
        {
            set_needs_init( true );
            UserMain.sqc().rebuild_result_array( object.getClass() );
        }
        else
        {
            String txt = UserMain.getString("Dieser_Eintrag_kann_nicht_geloescht_werden,_er_ist_noch_in_Benutzung");
            UserMain.errm_ok( txt);
        }


        return okay;
    }


    protected boolean del_no_hibernate_object( int row )
    {
        Object object = model.getSqlResult().get(row);

        ServerCall sql = UserMain.sqc().get_sqc();
        ConnectionID cid = sql.open();

        if (!check_valid_cid( sql, cid ))
            return false;

         StatementID sta = sql.createStatement(cid);

        boolean okay = sql.Delete( sta, object );

        sql.close(sta);
        sql.close(cid);

        if (!okay)
        {
            String object_name = object.getClass().getSimpleName();
            UserMain.errm_ok(this, UserMain.Txt("Cannot_delete") + " " + object_name + " " + sql.get_last_err());
        }
        set_needs_init( true );

        return okay;
    }

     public void new_edit_dlg()
    {

        GlossDialogPanel pnl = get_edit_panel( -1 );
        pnl.addPropertyChangeListener("REBUILD", this);

        GenericGlossyDlg dlg = new GenericGlossyDlg( null, true, pnl );
        dlg.set_next_location(this);
        dlg.setVisible( true );
    }




    @Override
    public void mousePressed(MouseEvent e)
    {
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt)
    {
        gather_sql_result();
        table.tableChanged(new TableModelEvent(table.getModel()) );
        set_tabel_row_height();

        this.repaint();
    }

    protected void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        PN_MAIN = new GlossPanel();
        PN_TITLE = new javax.swing.JPanel();
        PN_TABLE = new javax.swing.JPanel();
        SCP_TABLE = new javax.swing.JScrollPane();
        PN_BUTTONS = new javax.swing.JPanel();
        BT_NEW = new GlossButton();
        BT_QUIT = new GlossButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        PN_MAIN.setLayout(new java.awt.GridBagLayout());

        if (Main.ui.has_rendered_panels())
        {
            setBackground(new java.awt.Color(0, 0, 0));
            setUndecorated(true);

            PN_MAIN.setBackground(new java.awt.Color(51, 51, 51));

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

            SCP_TABLE.setBackground(new java.awt.Color(51, 51, 51));
            SCP_TABLE.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 3, 1, 3));
            SCP_TABLE.setOpaque(false);
        }
        SCP_TABLE.setPreferredSize(new java.awt.Dimension(600, 250));

        PN_TABLE.setLayout(new javax.swing.BoxLayout(PN_TABLE, javax.swing.BoxLayout.LINE_AXIS));
        PN_TABLE.add(SCP_TABLE);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        PN_MAIN.add(PN_TABLE, gridBagConstraints);

        BT_NEW.setText(UserMain.Txt("New")); // NOI18N
        BT_QUIT.setText(UserMain.Txt("CLOSE_DIALOG")); // NOI18N


        //BT_NEW.setMargin(new java.awt.Insets(2, 20, 2, 20));

        if (Main.ui.has_rendered_panels())
        {
            PN_BUTTONS.setOpaque(false);

            BT_NEW.setForeground(new java.awt.Color(204, 204, 204));
            BT_NEW.setActionCommand("        ");
            BT_NEW.setBorder(null);
            BT_NEW.setContentAreaFilled(false);
            BT_QUIT.setForeground(new java.awt.Color(204, 204, 204));
            
            BT_QUIT.setBorder(BT_NEW.getBorder());
            BT_QUIT.setContentAreaFilled(false);
        }
        BT_NEW.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_NEWActionPerformed(evt);
            }
        });

        BT_QUIT.addActionListener(new java.awt.event.ActionListener() {
            @Override
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
                .addComponent(BT_NEW, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(BT_QUIT, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
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

 protected void BT_NEWActionPerformed(java.awt.event.ActionEvent evt)
    {
        // TODO add your handling code here:

                new_edit_dlg();

    }

    protected void BT_QUITActionPerformed(java.awt.event.ActionEvent evt)
    {
        // TODO add your handling code here:
        this.setVisible(false);
    }


    // Variables declaration - do not modify
    protected javax.swing.JButton BT_NEW;
    protected javax.swing.JButton BT_QUIT;
    protected javax.swing.JPanel PN_BUTTONS;
    protected org.jdesktop.swingx.JXPanel PN_MAIN;
    protected javax.swing.JPanel PN_TABLE;
    protected javax.swing.JPanel PN_TITLE;
    protected javax.swing.JScrollPane SCP_TABLE;
    // End of variables declaration


}
