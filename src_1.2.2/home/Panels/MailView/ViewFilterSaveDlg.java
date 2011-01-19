/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * SingleTextEditPanel.java
 *
 * Created on 18.09.2009, 16:15:48
 */

package dimm.home.Panels.MailView;

import dimm.home.Main;
import dimm.home.Panels.LogicFilter;
import dimm.home.Rendering.*;
import dimm.home.UserMain;
import dimm.home.Utilities.CXStream;
import home.shared.Utilities.ZipUtilities;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.swing.DefaultListModel;
import javax.swing.JButton;

/**
 *
 * @author mw
 */

class FilterListModel extends DefaultListModel
{

    File[] ff;

    public FilterListModel( File[] ff )
    {
        this.ff = ff;
    }


    @Override
    public int getSize()
    {
        return ff.length;
    }

    @Override
    public Object getElementAt( int index )
    {
        String name = ff[index].getName();
        int idx = name.indexOf(ViewFilterSaveDlg.FILTER_SUFFIX);
        String s = name.substring(0, idx);

        try
        {
            String nice = ZipUtilities.fromBase64(s);
            nice = nice.replace('\n', ' ');
            return nice;
        }
        catch ( Exception ex)
        {
            // cannot happen
        }
        return "???";
    }
    File get_file( int idx)
            {
        return ff[idx];
        }
}
public class ViewFilterSaveDlg extends GlossDialogPanel
{
    private boolean okay;
    MailViewPanel panel;
    String last_filter;

    public static final String FILTER_SUFFIX = ".flt";
    FilterListModel model;

    /** Creates new form SingleTextEditPanel */
    public ViewFilterSaveDlg(MailViewPanel _panel, String _last_filter)
    {
        panel = _panel;
        last_filter = _last_filter;
        initComponents();


        File[] ff = get_save_filter_files( );

        model = new FilterListModel(ff);
        JL_FILTER.setModel(model);

        TXT_FILTER.setText(LogicFilter.get_nice_filter_text( last_filter ).replace('\n', ' '));
    }

    File get_filter_dir()
    {
        File f = new File( Main.get_user_path() + "/filter");
        if (!f.exists())
            f.mkdir();
        return f;
    }

    File mk_save_filter_file( String nice_txt )
    {
        String txt = ZipUtilities.toBase64(nice_txt);
        File filter_dir = get_filter_dir();
        File f = new File( filter_dir, txt + FILTER_SUFFIX );
        return f;
    }

    final File[] get_save_filter_files( )
    {
        File filter_dir = get_filter_dir();
        File[] ff = filter_dir.listFiles( new FileFilter() {

            @Override
            public boolean accept( File pathname )
            {
                if (!pathname.isFile())
                    return false;
                if (!pathname.getName().endsWith(FILTER_SUFFIX))
                    return false;
                int i = pathname.getName().indexOf(FILTER_SUFFIX);
                String s = pathname.getName().substring(0, i);

                try
                {
                    String nice = ZipUtilities.fromBase64(s);
                    if (nice.length() > 0)
                        return true;
                }
                catch (UnsupportedEncodingException unsupportedEncodingException)
                {
                }
                return false;
            }
        });
        return ff;
    }

   
    void save_filter()
    {
        String nice_txt = LogicFilter.get_nice_filter_text( last_filter );
        File f = mk_save_filter_file( nice_txt );
        if (f.exists())
        {
            if (!UserMain.errm_ok_cancel(my_dlg, UserMain.Txt("Wollen_Sie_diesen_Filter_überschreiben?")))
                return;
        }
        CXStream xs = new CXStream();

        FileOutputStream fos = null;
        try
        {
            fos = new FileOutputStream(f);
            xs.toXML(last_filter, fos);
        }
        catch (IOException ex)
        {
            UserMain.errm_ok(my_dlg, UserMain.Txt("Filter_kann_nicht_gespeichert_werden:") + " " + ex.getLocalizedMessage());
        }
        finally
        {
            if (fos != null)
            {
                try
                {
                    fos.close();
                }
                catch (IOException ex)
                {
                    
                }
            }
        }
    }
    String load_filter()
    {
        int idx = JL_FILTER.getSelectedIndex();
        if (idx < 0)
            return null;

        CXStream xs = new CXStream();

        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(model.get_file(idx));
            String filter = (String)xs.fromXML(fis);
            return filter;
        }
        catch (Exception ex)
        {
            UserMain.errm_ok(my_dlg, UserMain.Txt("Filter_kann_nicht_geladen_werden:") + " " + ex.getLocalizedMessage());
        }
        finally
        {
            if (fis != null)
            {
                try
                {
                    fis.close();
                }
                catch (IOException ex)
                {
                }
            }
        }
        return null;
    }

    void delete_filter()
    {
        int idx = JL_FILTER.getSelectedIndex();
        if (idx < 0)
            return;

        model.get_file(idx).delete();
        
        File[] ff = get_save_filter_files( );

        model = new FilterListModel(ff);
        JL_FILTER.setModel(model);
    }



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BT_OK = new GlossButton();
        BT_ABORT = new GlossButton();
        BT_SAVE = new GlossButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        JL_FILTER = new javax.swing.JList();
        BT_DEL = new GlossButton();
        TXT_FILTER = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("dimm/home/MA_Properties"); // NOI18N
        BT_OK.setText(bundle.getString("Open")); // NOI18N
        BT_OK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_OKActionPerformed(evt);
            }
        });

        BT_ABORT.setText(bundle.getString("Abort")); // NOI18N
        BT_ABORT.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_ABORTActionPerformed(evt);
            }
        });

        BT_SAVE.setText(bundle.getString("SaveNew")); // NOI18N
        BT_SAVE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_SAVEActionPerformed(evt);
            }
        });

        JL_FILTER.setModel(new javax.swing.AbstractListModel() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5", "6", "7", "8", "9", "10", "11" };
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        });
        JL_FILTER.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        JL_FILTER.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                JL_FILTERMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(JL_FILTER);

        BT_DEL.setText(bundle.getString("Delete")); // NOI18N
        BT_DEL.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                BT_DELActionPerformed(evt);
            }
        });

        TXT_FILTER.setEditable(false);
        TXT_FILTER.setText("jTextField1");

        jLabel1.setText(UserMain.Txt("Actual_Filter")); // NOI18N

        jLabel2.setText(UserMain.Txt("Saved_Filters")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(BT_SAVE)
                        .addGap(18, 18, 18)
                        .addComponent(BT_DEL)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 148, Short.MAX_VALUE)
                        .addComponent(BT_ABORT)
                        .addGap(18, 18, 18)
                        .addComponent(BT_OK))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 6, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(TXT_FILTER, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 376, Short.MAX_VALUE))))
                .addContainerGap())
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {BT_ABORT, BT_OK});

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jLabel1, jLabel2});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(TXT_FILTER, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 164, Short.MAX_VALUE)
                    .addComponent(jLabel2))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(BT_OK)
                    .addComponent(BT_ABORT)
                    .addComponent(BT_SAVE)
                    .addComponent(BT_DEL))
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void BT_OKActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_OKActionPerformed
    {//GEN-HEADEREND:event_BT_OKActionPerformed
        // TODO add your handling code here:
        onOk();

    }//GEN-LAST:event_BT_OKActionPerformed

    private void BT_ABORTActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_ABORTActionPerformed
    {//GEN-HEADEREND:event_BT_ABORTActionPerformed
        okay = false;
        my_dlg.setVisible(false);

        // TODO add your handling code here:
    }//GEN-LAST:event_BT_ABORTActionPerformed

    private void BT_SAVEActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_SAVEActionPerformed
    {//GEN-HEADEREND:event_BT_SAVEActionPerformed
        // TODO add your handling code here:
        save_filter();
        okay = true;
        my_dlg.setVisible(false);

    }//GEN-LAST:event_BT_SAVEActionPerformed

    private void BT_DELActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_BT_DELActionPerformed
    {//GEN-HEADEREND:event_BT_DELActionPerformed
        // TODO add your handling code here:
        delete_filter();
    }//GEN-LAST:event_BT_DELActionPerformed

    private void JL_FILTERMouseClicked(java.awt.event.MouseEvent evt)//GEN-FIRST:event_JL_FILTERMouseClicked
    {//GEN-HEADEREND:event_JL_FILTERMouseClicked
        // TODO add your handling code here:
        if (evt.getClickCount() == 2)
            onOk();

    }//GEN-LAST:event_JL_FILTERMouseClicked


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton BT_ABORT;
    private javax.swing.JButton BT_DEL;
    private javax.swing.JButton BT_OK;
    private javax.swing.JButton BT_SAVE;
    private javax.swing.JList JL_FILTER;
    private javax.swing.JTextField TXT_FILTER;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    // End of variables declaration//GEN-END:variables

    @Override
    public JButton get_default_button()
    {
        return BT_OK;
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

    private void onOk()
    {
        String filter = load_filter();
        if (filter == null)
            return;

        panel.set_filter( filter );

        okay = true;
        my_dlg.setVisible(false);
    }

}
