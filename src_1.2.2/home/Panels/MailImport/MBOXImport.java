/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Panels.MailImport;

import dimm.home.Main;
import dimm.home.UserMain;
import home.shared.Utilities.SizeStr;
import home.shared.CS_Constants;
import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;




/**
 *
 * @author mw
 */







class MBOXRootNode extends DefaultMutableTreeNode implements SwitchableNode
{
    NamePathEntry root;
    boolean is_selected;
    String path;
    DefaultTreeModel model;

    // ABS PATH
    MBOXRootNode( DefaultTreeModel _model, String _path )
    {
       path = _path;
       File f = new File(path);
       model = _model;

       children = new Vector<MBOXFileNode>();

       if (f.exists() && f.isDirectory())
       {
            File[] dbx_files = f.listFiles();

            for (int i = 0; i < dbx_files.length; i++)
            {
                File file = dbx_files[i];

                children.add(new MBOXFileNode( model, file ) );
            }
       }
       if (this.getChildCount() > 0)
            is_selected = true;

    }


   



    @Override
    public boolean isLeaf()
    {
        return false;
    }


    @Override
    public boolean is_selected()
    {
        return is_selected;
    }

    @Override
    public void set_selected( boolean s )
    {
        is_selected = s;
        model.nodeChanged(this);
        
        for (int i = 0; i < getChildCount(); i++)
        {
            SwitchableNode mboxTreeNode = (SwitchableNode)children.get(i);
            mboxTreeNode.set_selected( s);
        }
    }

    @Override
    public boolean contains_data()
    {
        return false;
    }

    @Override
    public long get_size()
    {
        return 0;
    }

    @Override
    public String toString()
    {
        return new File(path).getName();
    }

}
class MBOXFileNode  extends FileNode
{
    MBOXFileNode( DefaultTreeModel _model, File f )
    {
        super(_model, f);

       children = new Vector<MBOXFileNode>();

       if (f.exists() && f.isDirectory())
       {
            File[] dbx_files = f.listFiles();

            for (int i = 0; i < dbx_files.length; i++)
            {
                File file = dbx_files[i];

                children.add(new MBOXFileNode( model, file ) );
            }
       }
       
       if (this.getChildCount() > 0)
            is_selected = true;

    }

    @Override
    String get_mbox_name()
    {
        
        return node.getName();

    }

    @Override
    public boolean contains_data()
    {
        return node.isFile();
    }
}
class MBOXTreeModel extends DefaultTreeModel
{
    MBOXTreeModel( )
    {
        super(null);
    }
}
class MBOXTreeCellRenderer implements TreeCellRenderer
{

    JCheckBox jcb;
    JLabel jlb;

    MBOXTreeCellRenderer()
    {
        jcb = new JCheckBox();
        jcb.setOpaque(false);
        jlb = new JLabel();
        jlb.setOpaque(false);
        jlb.setForeground(Main.ui.get_foreground() );
        jlb.setBackground(Main.ui.get_background() );
    }

    @Override
    public Component getTreeCellRendererComponent( JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus )
    {
        if (value instanceof MBOXRootNode)
        {
            MBOXRootNode node = (MBOXRootNode) value;
            if (node.root != null)
            {
                jcb.setText(node.root.name);
                jcb.setSelected( node.is_selected);
                return jcb;
            }
        }

        if (value instanceof MBOXFileNode)
        {
            MBOXFileNode node = (MBOXFileNode) value;

            String text = node.get_mbox_name();
            String len_text = "  (" + new SizeStr( node.node.length() ).toString() + ")";
            text = text + len_text;

            jcb.setText(text);
            jcb.setSelected( node.is_selected);
            return jcb;
        }

        jlb.setText("");
        return jlb;
    }
}



class MBOXProfileManager extends FileImportProfileManager
{
   ComboProfileOptsPanel opts_panel;

    public MBOXProfileManager(PanelImportMailbox dialog)
    {
        super(dialog);

        this.opts_panel = new ComboProfileOptsPanel(dialog);
        dialog.set_opts_panel( opts_panel );

    }

    @Override
    void init_options_gui( ) throws IOException
    {
        opts_panel.set_info_text(UserMain.Txt("Please_select_the_folder_with_MBox_files"));

        JComboBox cb = opts_panel.get_combo();
        cb.removeAllItems();
       
        NamePathEntry tbpe = new NamePathEntry( null, UserMain.Txt("Select_mail_directory_manually") );
        
        cb.addItem(tbpe);
    }

    @Override
    String get_type()
    {
        return CS_Constants.TYPE_MBOX;
    }

    @Override
    void handle_build_tree( JTree tree) throws IOException
    {
        if (opts_panel.getNpe() != null)
        {
            handle_build_tree(opts_panel.getNpe(), tree );
        }
        if (opts_panel.getPath() != null)
        {
            handle_build_tree(opts_panel.getPath(), tree );
        }
    }


  
    void handle_build_tree(String path, JTree tree) throws IOException
    {
        MBOXRootNode node = null;
        DefaultTreeModel model = new MBOXTreeModel();
        node = new MBOXRootNode( model, path );
        model.setRoot(node);
        tree.setModel(model);
        tree.setCellRenderer( new MBOXTreeCellRenderer() );
        tree.setRootVisible(true);
    }

    
    void handle_build_tree( NamePathEntry npe, JTree tree ) throws IOException
    {
        
    }



  
}