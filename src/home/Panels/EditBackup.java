/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * Backup.java
 *
 * Created on 24.02.2010, 17:04:42
 */

package dimm.home.Panels;

import dimm.home.Models.DiskArchiveComboModel;
import dimm.home.Rendering.GlossButton;
import dimm.home.UserMain;
import home.shared.CS_Constants;
import home.shared.SQL.SQLResult;
import home.shared.hibernate.Backup;
import home.shared.hibernate.DiskArchive;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.AbstractSpinnerModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFormattedTextField;
import javax.swing.SpinnerModel;

class BackupAgentEntry
{
    String ip;
    int port;

    public BackupAgentEntry( String ip, int port )
    {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String toString()
    {
        return ip + ":" + port;
    }
}


/**
 *
 * @author mw
 */
public class EditBackup  extends GenericEditPanel
{

    BackupOverview object_overview;
    BackupTableModel model;
    Backup object;
    DiskArchiveComboModel dacm;

    JFormattedTextField[] time_list = new JFormattedTextField[7];
    JCheckBox[] enable_list = new JCheckBox[7];
    private static final int MD_CYCLE = 0;
    private static final int MD_SCHEDULE = 1;


       /** Creates new form EditChannelPanel */
    public EditBackup(int _row, BackupOverview _overview)
    {
        initComponents();

        time_list[0] = TXTF_TIME0;
        time_list[1] = TXTF_TIME1;
        time_list[2] = TXTF_TIME2;
        time_list[3] = TXTF_TIME3;
        time_list[4] = TXTF_TIME4;
        time_list[5] = TXTF_TIME5;
        time_list[6] = TXTF_TIME5;

        enable_list[0] = CB_DAY1;
        enable_list[1] = CB_DAY2;
        enable_list[2] = CB_DAY3;
        enable_list[3] = CB_DAY4;
        enable_list[4] = CB_DAY5;
        enable_list[5] = CB_DAY6;
        enable_list[6] = CB_DAY6;

        object_overview = _overview;
        model = object_overview.get_object_model();

        COMBO_CYCLE_UNITS.removeAllItems();
        for (int i = 0; i < CS_Constants.BY_CYCLE_UNITS.length; i++)
        {
            COMBO_CYCLE_UNITS.addItem( UserMain.Txt(CS_Constants.BY_CYCLE_UNITS[i]));
        }
        

        SQLResult<DiskArchive> da_res = UserMain.sqc().get_da_result();

        // COMBO-MODEL DISK ARCHIVE
        dacm = new DiskArchiveComboModel(da_res );

        SpinnerModel sp_model = new AbstractSpinnerModel()
        {

         int value = 6;

            @Override
         public Object getValue()
         {
            return new Integer(value);
         }

            @Override
         public void setValue(Object v)
         {
             if (v instanceof Integer)
             {
                 value = ((Integer)v).intValue();
             }
             fireStateChanged();
         }

            @Override
         public Object getNextValue()
         {
             return new Integer(value + 1);
         }

            @Override
         public Object getPreviousValue()
         {
             int new_val = value -1;
             if (new_val < 1)
                 new_val = 1;
            return new Integer(new_val);
         }
      };
      SP_CYCLE_CNT.setModel(sp_model);


        row = _row;

        if (!model.is_new(row))
        {
            object = model.get_object(row);           
            int da_id = model.getSqlResult().getInt( row, "da_id");
            dacm.set_act_id(da_id);           
            CB_DISABLED.setSelected( test_flag(CS_Constants.BACK_DISABLED) );
            CB_BACKUP_SYS.setSelected( test_flag( CS_Constants.BACK_SYS ) );
            CB_MODE.setSelectedIndex(test_flag( CS_Constants.BACK_CYCLE ) ? MD_CYCLE : MD_SCHEDULE);

            COMBO_CYCLE_UNITS.setSelectedIndex( get_mode_idx(object.getCycleunit()));
            SP_CYCLE_CNT.setValue( object.getCycleval() );

            timestr_to_gui( object.getSchedtime() );
            enablestr_to_gui( object.getSchedenable() );

            TXT_AGENT.setText(object.getAgentip());
            TXT_AGENT_PORT.setText(object.getAgentport().toString());

            TXT_PATH.setText( object.getAgentpath() );
            TXTF_VALID_FROM.setText(object.getValidfrom());
        }
        else
        {
            object = new Backup();
            object.setMandant(UserMain.sqc().get_act_mandant());
            COMBO_CYCLE_UNITS.setSelectedIndex( 1 ); // HOUR
            SP_CYCLE_CNT.setValue( new Integer(6) );
            set_mode_visibility();
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat(CS_Constants.BACK_STARTDATE_FORMAT);
            TXTF_VALID_FROM.setText( sdf.format(d));
        }

        CB_VAULT.setModel(dacm);
    }
    String get_agent_str( String ip, int port )
    {
        if (ip.length() > 0 && port > 0)
            return ip + ":" + port;
        return "";
    }


    int get_mode_idx( String mode_idx )
    {
        for (int i = 0; i < CS_Constants.BY_CYCLE_UNITS.length; i++)
        {
            String s = CS_Constants.BY_CYCLE_UNITS[i];
            if (s.equals(mode_idx))
                return i;
        }
        return -1;
    }
    void set_mode_visibility()
    {
        if (CB_MODE.getSelectedIndex() == MD_CYCLE)
        {
            PN_SCHEDULE.setVisible(false);
            PN_CYCLE.setVisible(true);
        }
        else
        {
            PN_SCHEDULE.setVisible(true);
            PN_CYCLE.setVisible(false);
        }
        if (my_dlg != null)
            my_dlg.pack();
    }

    // 03:00|05:30|00:00|...
    void timestr_to_gui(String str)
    {
        String[] day_list = str.split(CS_Constants.TEXTLIST_DELIM);
        if (day_list.length == time_list.length)
        {
            for (int i = 0; i < day_list.length; i++)
            {
                String hm_time = "00:00";
                try
                {
                    String day = day_list[i];
                    String[] time = day.split(":");
                    hm_time = String.format("%02d:%02d", time[0], time[1]);
                    time_list[i].setText( hm_time );
                }
                catch (Exception e)
                {
                }
                time_list[i].setText( hm_time );
            }
        }
    }

    // 0|1|1|0|0||0|0|
    void enablestr_to_gui(String str)
    {
        String[] day_list = str.split(CS_Constants.TEXTLIST_DELIM);
        if (day_list.length == enable_list.length)
        {
            for (int i = 0; i < day_list.length; i++)
            {
                boolean enable = false;
                try
                {
                    enable = day_list[i].charAt(0) == '1';
                }
                catch (Exception e)
                {
                }
                enable_list[i].setSelected(enable);
            }
        }
    }

    String timestr_from_gui()
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < time_list.length; i++)
        {
            if (i > 0)
                sb.append(CS_Constants.TEXTLIST_DELIM);

            sb.append( time_list[i].getText() );
        }
        return sb.toString();
    }
    String enablestr_from_gui()
    {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < enable_list.length; i++)
        {
            if (i > 0)
                sb.append(CS_Constants.TEXTLIST_DELIM);

            sb.append( (enable_list[i].isSelected()) ? '1' : '0');
        }
        return sb.toString();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        PN_SCHEDULE = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        CB_DAY1 = new javax.swing.JCheckBox();
        TXTF_TIME0 = new javax.swing.JFormattedTextField();
        jPanel3 = new javax.swing.JPanel();
        CB_DAY2 = new javax.swing.JCheckBox();
        TXTF_TIME1 = new javax.swing.JFormattedTextField();
        jPanel4 = new javax.swing.JPanel();
        CB_DAY3 = new javax.swing.JCheckBox();
        TXTF_TIME2 = new javax.swing.JFormattedTextField();
        jPanel5 = new javax.swing.JPanel();
        CB_DAY4 = new javax.swing.JCheckBox();
        TXTF_TIME3 = new javax.swing.JFormattedTextField();
        jPanel6 = new javax.swing.JPanel();
        CB_DAY5 = new javax.swing.JCheckBox();
        TXTF_TIME4 = new javax.swing.JFormattedTextField();
        jPanel8 = new javax.swing.JPanel();
        CB_DAY6 = new javax.swing.JCheckBox();
        TXTF_TIME5 = new javax.swing.JFormattedTextField();
        jPanel9 = new javax.swing.JPanel();
        CB_DAY7 = new javax.swing.JCheckBox();
        TXTF_TIME6 = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        TXT_PATH = new javax.swing.JTextField();
        BT_PATH = new javax.swing.JButton();
        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();
        PN_CYCLE = new javax.swing.JPanel();
        COMBO_CYCLE_UNITS = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        SP_CYCLE_CNT = new javax.swing.JSpinner();
        CB_MODE = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        CB_DISABLED = new javax.swing.JCheckBox();
        CB_VAULT = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        TXT_AGENT = new javax.swing.JTextField();
        TXT_AGENT_PORT = new javax.swing.JFormattedTextField();
        CB_BACKUP_SYS = new javax.swing.JCheckBox();
        BT_TEST_AGENT = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        TXTF_VALID_FROM = new javax.swing.JFormattedTextField();
        jSeparator1 = new javax.swing.JSeparator();

        PN_SCHEDULE.setOpaque(false);

        jPanel2.setOpaque(false);
        jPanel2.setPreferredSize(new java.awt.Dimension(100, 68));
        jPanel2.setRequestFocusEnabled(false);

        CB_DAY1.setText(UserMain.Txt("Monday")); // NOI18N
        CB_DAY1.setOpaque(false);

        TXTF_TIME0.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));
        TXTF_TIME0.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        TXTF_TIME0.setText("02:00");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TXTF_TIME0, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CB_DAY1))
                .addContainerGap(27, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CB_DAY1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TXTF_TIME0, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setOpaque(false);
        jPanel3.setPreferredSize(new java.awt.Dimension(100, 68));
        jPanel3.setRequestFocusEnabled(false);

        CB_DAY2.setText(UserMain.Txt("Tuesday")); // NOI18N
        CB_DAY2.setOpaque(false);

        TXTF_TIME1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));
        TXTF_TIME1.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        TXTF_TIME1.setText("02:00");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TXTF_TIME1, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CB_DAY2))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CB_DAY2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(TXTF_TIME1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel4.setOpaque(false);
        jPanel4.setPreferredSize(new java.awt.Dimension(100, 68));
        jPanel4.setRequestFocusEnabled(false);

        CB_DAY3.setText(UserMain.Txt("Wednesday")); // NOI18N
        CB_DAY3.setOpaque(false);

        TXTF_TIME2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));
        TXTF_TIME2.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        TXTF_TIME2.setText("02:00");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TXTF_TIME2, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CB_DAY3))
                .addContainerGap(7, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CB_DAY3)
                .addGap(7, 7, 7)
                .addComponent(TXTF_TIME2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setOpaque(false);
        jPanel5.setPreferredSize(new java.awt.Dimension(100, 68));
        jPanel5.setRequestFocusEnabled(false);

        CB_DAY4.setText(UserMain.Txt("Thursday")); // NOI18N
        CB_DAY4.setOpaque(false);

        TXTF_TIME3.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));
        TXTF_TIME3.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        TXTF_TIME3.setText("02:00");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TXTF_TIME3, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CB_DAY4))
                .addContainerGap(19, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CB_DAY4)
                .addGap(7, 7, 7)
                .addComponent(TXTF_TIME3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setOpaque(false);
        jPanel6.setPreferredSize(new java.awt.Dimension(100, 68));
        jPanel6.setRequestFocusEnabled(false);

        CB_DAY5.setText(UserMain.Txt("Friday")); // NOI18N
        CB_DAY5.setOpaque(false);

        TXTF_TIME4.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));
        TXTF_TIME4.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        TXTF_TIME4.setText("02:00");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TXTF_TIME4, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CB_DAY5))
                .addContainerGap(35, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CB_DAY5)
                .addGap(7, 7, 7)
                .addComponent(TXTF_TIME4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel8.setOpaque(false);
        jPanel8.setPreferredSize(new java.awt.Dimension(100, 68));
        jPanel8.setRequestFocusEnabled(false);

        CB_DAY6.setText(UserMain.Txt("Saturday")); // NOI18N
        CB_DAY6.setOpaque(false);

        TXTF_TIME5.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));
        TXTF_TIME5.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        TXTF_TIME5.setText("02:00");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TXTF_TIME5, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CB_DAY6))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CB_DAY6)
                .addGap(7, 7, 7)
                .addComponent(TXTF_TIME5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setOpaque(false);
        jPanel9.setPreferredSize(new java.awt.Dimension(100, 68));
        jPanel9.setRequestFocusEnabled(false);

        CB_DAY7.setText(UserMain.Txt("Sunday")); // NOI18N
        CB_DAY7.setOpaque(false);

        TXTF_TIME6.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter(java.text.DateFormat.getTimeInstance(java.text.DateFormat.SHORT))));
        TXTF_TIME6.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        TXTF_TIME6.setText("02:00");

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TXTF_TIME6, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CB_DAY7))
                .addContainerGap(29, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CB_DAY7)
                .addGap(7, 7, 7)
                .addComponent(TXTF_TIME6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout PN_SCHEDULELayout = new javax.swing.GroupLayout(PN_SCHEDULE);
        PN_SCHEDULE.setLayout(PN_SCHEDULELayout);
        PN_SCHEDULELayout.setHorizontalGroup(
            PN_SCHEDULELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_SCHEDULELayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        PN_SCHEDULELayout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jPanel2, jPanel3, jPanel4, jPanel5, jPanel6, jPanel8, jPanel9});

        PN_SCHEDULELayout.setVerticalGroup(
            PN_SCHEDULELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_SCHEDULELayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(PN_SCHEDULELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel8, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel6, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jLabel1.setText(UserMain.Txt("Agent")); // NOI18N

        jLabel2.setText(UserMain.Txt("Path")); // NOI18N

        BT_PATH.setText(UserMain.Txt("Set")); // NOI18N

        BT_OK.setText(UserMain.Txt("OK")); // NOI18N
        BT_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKActionPerformed(evt);
            }
        });

        BT_ABORT.setText(UserMain.Txt("Abort")); // NOI18N
        BT_ABORT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ABORTActionPerformed(evt);
            }
        });

        PN_CYCLE.setOpaque(false);

        COMBO_CYCLE_UNITS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                COMBO_CYCLE_UNITSActionPerformed(evt);
            }
        });

        jLabel3.setText(UserMain.Txt("Backupcycle")); // NOI18N

        SP_CYCLE_CNT.setValue(1);

        javax.swing.GroupLayout PN_CYCLELayout = new javax.swing.GroupLayout(PN_CYCLE);
        PN_CYCLE.setLayout(PN_CYCLELayout);
        PN_CYCLELayout.setHorizontalGroup(
            PN_CYCLELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_CYCLELayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SP_CYCLE_CNT, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(COMBO_CYCLE_UNITS, 0, 84, Short.MAX_VALUE)
                .addContainerGap())
        );
        PN_CYCLELayout.setVerticalGroup(
            PN_CYCLELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_CYCLELayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PN_CYCLELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(COMBO_CYCLE_UNITS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SP_CYCLE_CNT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addContainerGap())
        );

        CB_MODE.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Cycle", "Schedule" }));
        CB_MODE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                CB_MODEActionPerformed(evt);
            }
        });

        jLabel4.setText(UserMain.Txt("Mode")); // NOI18N

        CB_DISABLED.setText(UserMain.Txt("Disabled")); // NOI18N
        CB_DISABLED.setOpaque(false);

        CB_VAULT.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel5.setText(UserMain.Txt("Speicherziel")); // NOI18N

        TXT_AGENT_PORT.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));
        TXT_AGENT_PORT.setHorizontalAlignment(javax.swing.JTextField.TRAILING);

        CB_BACKUP_SYS.setText(UserMain.Txt("Store_Parameter")); // NOI18N
        CB_BACKUP_SYS.setOpaque(false);

        BT_TEST_AGENT.setText(UserMain.Txt("Test")); // NOI18N
        BT_TEST_AGENT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_TEST_AGENTActionPerformed(evt);
            }
        });

        jLabel6.setText(UserMain.Txt("Start_at")); // NOI18N

        TXTF_VALID_FROM.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.DateFormatter()));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PN_CYCLE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE)
                    .addComponent(CB_BACKUP_SYS)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(layout.createSequentialGroup()
                                    .addComponent(jLabel2)
                                    .addGap(7, 7, 7))
                                .addComponent(jLabel1))
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)
                            .addComponent(jLabel6))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CB_MODE, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addComponent(TXT_AGENT, javax.swing.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(TXT_AGENT_PORT, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(CB_VAULT, 0, 221, Short.MAX_VALUE)
                            .addComponent(TXTF_VALID_FROM, javax.swing.GroupLayout.DEFAULT_SIZE, 221, Short.MAX_VALUE)
                            .addComponent(TXT_PATH, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(259, 259, 259)
                                    .addComponent(BT_ABORT, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(BT_OK, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(layout.createSequentialGroup()
                                    .addGap(18, 18, 18)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                        .addComponent(BT_PATH, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(BT_TEST_AGENT, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                            .addComponent(CB_DISABLED)))
                    .addComponent(PN_SCHEDULE, javax.swing.GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(CB_MODE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(CB_DISABLED))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(TXT_AGENT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(TXT_AGENT_PORT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BT_TEST_AGENT))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(TXT_PATH, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(BT_PATH))))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CB_VAULT, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(TXTF_VALID_FROM, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(CB_BACKUP_SYS)
                .addGap(18, 18, 18)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 5, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PN_SCHEDULE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PN_CYCLE, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 26, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OK)
                    .addComponent(BT_ABORT))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void CB_MODEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_CB_MODEActionPerformed
    {//GEN-HEADEREND:event_CB_MODEActionPerformed
        // TODO add your handling code here:
        set_mode_visibility();
    }//GEN-LAST:event_CB_MODEActionPerformed

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ABORTActionPerformed
    {//GEN-HEADEREND:event_BT_ABORTActionPerformed
        // TODO add your handling code here:
        abort_action();

    }//GEN-LAST:event_BT_ABORTActionPerformed

    private void BT_OKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKActionPerformed
    {//GEN-HEADEREND:event_BT_OKActionPerformed
        // TODO add your handling code here:
        ok_action(object);
    }//GEN-LAST:event_BT_OKActionPerformed

    private void COMBO_CYCLE_UNITSActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_COMBO_CYCLE_UNITSActionPerformed
    {//GEN-HEADEREND:event_COMBO_CYCLE_UNITSActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_COMBO_CYCLE_UNITSActionPerformed

    private void BT_TEST_AGENTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_TEST_AGENTActionPerformed
    {//GEN-HEADEREND:event_BT_TEST_AGENTActionPerformed
        // TODO add your handling code here:
        String ret = call_syncserver( "list_agent_features",  "AG:" + TXT_AGENT.getText() + " PO:" + TXT_AGENT_PORT.getText() );
        if ( ret == null)
        {
            UserMain.errm_ok(my_dlg, UserMain.Txt("Cannot_connect_BackupServer"));
            return;
        }
        if (ret.charAt(0) != '0')
        {
            UserMain.errm_ok(my_dlg, UserMain.Txt("Error_occured_while_connecting_Agent: ") + ret);
            return;
        }

        UserMain.info_ok(my_dlg, UserMain.Txt("Successful"));

    }//GEN-LAST:event_BT_TEST_AGENTActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_OK;
    private javax.swing.JButton BT_PATH;
    private javax.swing.JButton BT_TEST_AGENT;
    private javax.swing.JCheckBox CB_BACKUP_SYS;
    private javax.swing.JCheckBox CB_DAY1;
    private javax.swing.JCheckBox CB_DAY2;
    private javax.swing.JCheckBox CB_DAY3;
    private javax.swing.JCheckBox CB_DAY4;
    private javax.swing.JCheckBox CB_DAY5;
    private javax.swing.JCheckBox CB_DAY6;
    private javax.swing.JCheckBox CB_DAY7;
    private javax.swing.JCheckBox CB_DISABLED;
    private javax.swing.JComboBox CB_MODE;
    private javax.swing.JComboBox CB_VAULT;
    private javax.swing.JComboBox COMBO_CYCLE_UNITS;
    private javax.swing.JPanel PN_CYCLE;
    private javax.swing.JPanel PN_SCHEDULE;
    private javax.swing.JSpinner SP_CYCLE_CNT;
    private javax.swing.JFormattedTextField TXTF_TIME0;
    private javax.swing.JFormattedTextField TXTF_TIME1;
    private javax.swing.JFormattedTextField TXTF_TIME2;
    private javax.swing.JFormattedTextField TXTF_TIME3;
    private javax.swing.JFormattedTextField TXTF_TIME4;
    private javax.swing.JFormattedTextField TXTF_TIME5;
    private javax.swing.JFormattedTextField TXTF_TIME6;
    private javax.swing.JFormattedTextField TXTF_VALID_FROM;
    private javax.swing.JTextField TXT_AGENT;
    private javax.swing.JFormattedTextField TXT_AGENT_PORT;
    private javax.swing.JTextField TXT_PATH;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables

    int get_object_flags()
    {
        return Integer.parseInt( object.getFlags() );
    }
    boolean test_flag( int f)
    {
        int flags = get_object_flags();

        return (flags & f) == f;
    }

    boolean object_is_disabled()
    {
        int flags = 0;

        flags = get_object_flags();

        return ((flags & CS_Constants.BACK_DISABLED) == CS_Constants.BACK_DISABLED);
    }
    void set_object_disabled( boolean f)
    {
        int flags = get_object_flags();

        if (f)
            set_object_flag( ProxyOverview.DISABLED );
        else
            clr_object_flag( ProxyOverview.DISABLED );
    }
    void set_object_flag(int flag)
    {
        int flags = get_object_flags();
        flags |= flag;
        object.setFlags(Integer.toString(flags));
    }
    void clr_object_flag(int flag)
    {
        int flags = get_object_flags();
        flags &= ~flag;
        object.setFlags(Integer.toString(flags));
    }


    @Override
    protected boolean check_changed()
    {
        if (model.is_new(row))
            return true;

        if (CB_DISABLED.isSelected() != object_is_disabled())
            return true;

        // VAULT
        long da_id = model.getSqlResult().getLong( row, "da_id");
        if ( CB_VAULT.getSelectedItem() != null)
        {
            if (dacm.get_act_id() != da_id)
                return true;
        }

        // MODE
        int obj_mode = test_flag( CS_Constants.BACK_CYCLE ) ? MD_CYCLE : MD_SCHEDULE;
        if (CB_MODE.getSelectedIndex() != obj_mode)
            return true;

        // MODE ENTRIES
        if (CB_MODE.getSelectedIndex() == MD_SCHEDULE)
        {
            if (!object.getSchedenable().equals( enablestr_from_gui() ))
                return true;

            if (!object.getSchedtime().equals( timestr_from_gui() ))
                return true;
        }
        if (CB_MODE.getSelectedIndex() == MD_CYCLE)
        {
            if (!SP_CYCLE_CNT.getValue().toString().equals(object.getCycleval().toString()))
                return true;
            if (!CB_MODE.getSelectedItem().toString().equals(UserMain.Txt(object.getCycleunit())))
                return true;
        }

        // AGENT
        if (!TXT_AGENT.getText().equals( object.getAgentip()))
            return true;

        if (get_agent_port() != object.getAgentport().intValue())
            return true;
        
        // PATH
        if (!TXT_PATH.getText().equals(object.getAgentpath()))
            return true;

        if (!TXT_PATH.getText().equals(object.getAgentpath()))
            return true;

        return false;
    }
    
    int get_agent_port()
    {
        try
        {
            return Integer.parseInt(TXT_AGENT_PORT.getText());
        }
        catch (NumberFormatException numberFormatException)
        {
        }
        return 0;
    }

    @Override
    protected boolean is_plausible()
    {

        if (TXT_AGENT.getText().length() == 0 || get_agent_port() <= 0)
        {
            UserMain.errm_ok(UserMain.getString("Agenteintrag_ist_nicht_okay"));
            return false;
        }

        try
        {
            DiskArchive da = dacm.get_selected_da();
            String n = da.getName();
        }
        catch (Exception e)
        {
            UserMain.errm_ok(UserMain.getString("Speicherziel_ist_nicht_okay"));
            return false;
        }
        if (CB_MODE.getSelectedIndex() == MD_CYCLE)
        {
            if (COMBO_CYCLE_UNITS.getSelectedIndex() < 0 || 
                    get_cycleval_from_spinner() == null ||
                    get_cycleval_from_spinner().intValue() <= 0)
            {
                UserMain.errm_ok(UserMain.getString("Zyklus_ist_nicht_okay"));
                return false;
            }
        }
        if (CB_MODE.getSelectedIndex() < 0)
        {
            UserMain.errm_ok(UserMain.getString("Mode_ist_nicht_okay"));
            return false;
        }
        try
        {
            SimpleDateFormat sdf = new SimpleDateFormat(CS_Constants.BACK_STARTDATE_FORMAT);
            Date d = sdf.parse(TXTF_VALID_FROM.getText(SimpleDa));
        }
        catch (Exception parseException)
        {
            UserMain.errm_ok(UserMain.getString("Startdatum_ist_nicht_okay"));
            return false;
        }
        if (TXT_PATH.getText().length() == 0)
        {
            UserMain.errm_ok(UserMain.getString("Pfad_ist_nicht_okay"));
            return false;
        }
      
        return true;
    }
   
    Integer get_cycleval_from_spinner()
    {
        try
        {
            return new Integer(SP_CYCLE_CNT.getValue().toString());
        }
        catch (NumberFormatException numberFormatException)
        {
        }
        return null;
    }


   

    @Override
    protected void set_object_props()
    {
        int flags = 0;
        if (CB_DISABLED.isSelected())
            flags |= CS_Constants.BACK_DISABLED;
        if (CB_MODE.getSelectedIndex() == MD_CYCLE)
            flags |= CS_Constants.BACK_CYCLE;
        if (CB_BACKUP_SYS.isSelected())
            flags |= CS_Constants.BACK_SYS;

        object.setFlags(Integer.toString(flags));
        object.setDiskArchive( dacm.get_selected_da());
        object.setAgentpath(TXT_PATH.getText());

        if (CB_MODE.getSelectedIndex() == MD_CYCLE)
        {
            object.setCycleunit(CS_Constants.BY_CYCLE_UNITS[COMBO_CYCLE_UNITS.getSelectedIndex()]);
            object.setCycleval(new Integer(SP_CYCLE_CNT.getValue().toString()));
        }
        if (CB_MODE.getSelectedIndex() == MD_SCHEDULE)
        {
            object.setSchedenable(enablestr_from_gui());
            object.setSchedtime(timestr_from_gui());
        }
        
        object.setAgentip( TXT_AGENT.getText() );
        object.setAgentport(get_agent_port() );
        object.setValidfrom(TXTF_VALID_FROM.getText());
        object.setAgentpath( TXT_PATH.getText() );

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

    private String call_syncserver( String cmd, String args )
    {
        String scmd = "sync CMD:" + cmd;
        if (args != null && args.length() > 0)
            scmd += " " + args;

        String ret = UserMain.fcc().call_abstract_function(scmd);

        return ret;
    }



}
