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
import java.io.FilenameFilter;
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
class EMLFilenameFilter implements FilenameFilter
{
    static String extension = ".eml";
    boolean recursive;

    EMLFilenameFilter()
    {
        recursive = true;
    }
    @Override
    public boolean accept( File dir, String name )
    {
        if (name.toLowerCase().endsWith(extension))
        {
            return true;
        }

        if (recursive)
        {
            File fdir = new File( dir, name );
            if (fdir.isDirectory())
            {
                 return true;
            }
        }


        return false;
    }
}







class EMLRootNode extends DefaultMutableTreeNode implements SwitchableNode
{
    NamePathEntry root;
    boolean is_selected;
    String path;
    DefaultTreeModel model;

    // ABS PATH
    EMLRootNode( DefaultTreeModel _model, String _path )
    {
       path = _path;
       File f = new File(path);
       model = _model;

       children = new Vector<EMLFileNode>();

       if (f.exists() && f.isDirectory())
       {
            File[] dbx_files = f.listFiles(new EMLFilenameFilter());

            for (int i = 0; i < dbx_files.length; i++)
            {
                File file = dbx_files[i];

                children.add(new EMLFileNode( model, file ) );
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
}
class EMLFileNode  extends FileNode
{
    EMLFileNode( DefaultTreeModel _model, File f )
    {
        super(_model, f);

       children = new Vector<EMLFileNode>();

       if (f.exists() && f.isDirectory())
       {
            File[] dbx_files = f.listFiles(new EMLFilenameFilter());

            for (int i = 0; i < dbx_files.length; i++)
            {
                File file = dbx_files[i];

                children.add(new EMLFileNode( model, file ) );
            }
       }
       
       if (this.getChildCount() > 0)
            is_selected = true;

    }

    @Override
    String get_mbox_name()
    {
        if (node.isDirectory())
            return node.getName();

        if (node.getName().toLowerCase().endsWith(EMLFilenameFilter.extension))
        {
            return node.getName().substring(0, node.getName().length() - EMLFilenameFilter.extension.length());
        }
        return "?";
    }

    @Override
    public boolean contains_data()
    {
        return node.isFile();
    }
}
class EMLTreeModel extends DefaultTreeModel
{
    EMLTreeModel( )
    {
        super(null);
    }
}
class EMLTreeCellRenderer implements TreeCellRenderer
{

    JCheckBox jcb;
    JLabel jlb;

    EMLTreeCellRenderer()
    {
        jcb = new JCheckBox();
        jlb = new JLabel();
        jlb.setForeground(Main.ui.get_foreground() );
        jlb.setBackground(Main.ui.get_background() );
    }

    @Override
    public Component getTreeCellRendererComponent( JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus )
    {
        if (value instanceof EMLRootNode)
        {
            EMLRootNode node = (EMLRootNode) value;
            if (node.root != null)
            {
                jcb.setText(node.root.name);
                jcb.setSelected( node.is_selected);
                return jcb;
            }
        }

        if (value instanceof EMLFileNode)
        {
            EMLFileNode node = (EMLFileNode) value;

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



class EMLProfileManager extends ProfileManager
{
   
    @Override
    void fill_profile_combo( JComboBox cb ) throws IOException
    {
        cb.removeAllItems();
       
        NamePathEntry tbpe = new NamePathEntry( null, UserMain.Txt("Select_mail_directory_manually") );
        
        cb.addItem(tbpe);
    }

    @Override
    String get_type()
    {
        return CS_Constants.TYPE_EML;
    }

    @Override
    void handle_build_tree(String path, JTree tree) throws IOException
    {
        EMLRootNode node = null;
        DefaultTreeModel model = new EMLTreeModel();
        node = new EMLRootNode( model, path );
        model.setRoot(node);
        tree.setModel(model);
        tree.setCellRenderer( new EMLTreeCellRenderer() );
        tree.setRootVisible(true);
    }

    @Override
    void handle_build_tree( NamePathEntry npe, JTree tree ) throws IOException
    {
        
    }
  
}