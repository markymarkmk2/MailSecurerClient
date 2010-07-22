/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Panels.MailImport;

import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryKey;
import dimm.home.Main;
import home.shared.Utilities.SizeStr;
import dimm.home.native_libs.NativeLoader;
import java.awt.Component;
import java.io.File;
import java.io.FilenameFilter;
import java.util.Vector;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;

/**
 *
 * @author mw
 */
class OlexpFilenameFilter implements FilenameFilter
{
    static String extension = ".dbx";
    boolean recursive;

    OlexpFilenameFilter()
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





class OlexpRootNode extends DefaultMutableTreeNode implements SwitchableNode
{
    NamePathEntry root;
    boolean is_selected;
    String path;
    DefaultTreeModel model;

    // ABS PATH
    OlexpRootNode( DefaultTreeModel _model, String _path )
    {
       path = _path;
       File f = new File(path);
       model = _model;

       children = new Vector<OlexpFileNode>();

       if (f.exists() && f.isDirectory())
       {
            File[] dbx_files = f.listFiles(new OlexpFilenameFilter());

            for (int i = 0; i < dbx_files.length; i++)
            {
                File file = dbx_files[i];

                children.add(new OlexpFileNode( model, file ) );
            }
       }
       if (this.getChildCount() > 0)
            is_selected = true;

    }


    // DATA FROM REGISTRY
    OlexpRootNode( DefaultTreeModel _model, NamePathEntry npe_profile )
    {
        root = npe_profile;
        model = _model;
        children = new Vector<OlexpVersionNode>();

        try
        {
            RegistryKey regkey = Registry.HKEY_CURRENT_USER;
            RegistryKey key = Registry.openSubkey(regkey, "Identities\\" + root.path + "\\Software\\Microsoft\\Outlook Express", RegistryKey.ACCESS_ALL);

            int nkeys = key.getNumberSubkeys();

            for (int i = 0; i < nkeys; i++)
            {
                String olexp_version = key.regEnumKey(i);

                RegistryKey olexp_version_key = Registry.openSubkey(key, olexp_version, RegistryKey.ACCESS_ALL);

                String store_path = olexp_version_key.getStringValue("Store Root");

                store_path = NativeLoader.resolve_win_path(store_path);
                int len = store_path.length();
                while (len > 0)
                {
                    if (store_path.charAt(len - 1) == '\\')
                    {
                        store_path = store_path.substring(0 , len -1);
                        len--;
                        continue;
                    }
                    break;
                }
                olexp_version_key.closeKey();


                NamePathEntry pe = new NamePathEntry(store_path, olexp_version);

                OlexpVersionNode child = new OlexpVersionNode( model, store_path, olexp_version);
                child.setParent(this);
                children.add( child );
            }
            key.closeKey();
        }
        catch (Exception registryException)
        {
            registryException.printStackTrace();
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




class OlexpVersionNode  extends DefaultMutableTreeNode implements SwitchableNode
{
    String path;
    String version;
    boolean is_selected;
    DefaultTreeModel model;

    OlexpVersionNode( DefaultTreeModel _model, String _path, String _version )
    {
        path = _path;
        version = _version;
        model = _model;
        children = new Vector<OlexpFileNode>();

        File f = new File(path);
        if (f.exists() && f.isDirectory())
        {
            File[] dbx_files = f.listFiles(new OlexpFilenameFilter());

            for (int i = 0; i < dbx_files.length; i++)
            {
                File file = dbx_files[i];

                OlexpFileNode child = new OlexpFileNode( model, file );
                child.setParent(this);
                children.add( child );

            }
        }
        if (this.getChildCount() > 0)
            is_selected = true;

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
class OlexpFileNode  extends FileNode
{
    OlexpFileNode( DefaultTreeModel _model, File f )
    {
        super( _model, f );
    }

    @Override
    String get_mbox_name()
    {
        if (node.getName().endsWith(OlexpFilenameFilter.extension))
        {
            return node.getName().substring(0, node.getName().length() - OlexpFilenameFilter.extension.length());
        }
        return "?";
    }

    @Override
    public boolean contains_data()
    {
        return true;
    }
}
class OlexpTreeModel extends DefaultTreeModel
{
    OlexpTreeModel( )
    {
        super(null);
    }
}

class OlexpTreeCellRenderer implements TreeCellRenderer
{

    JCheckBox jcb;
    JLabel jlb;

    OlexpTreeCellRenderer()
    {
        jcb = new JCheckBox();
        jlb = new JLabel();
        jlb.setForeground(Main.ui.get_foreground() );
        jlb.setBackground(Main.ui.get_background() );
    }

    @Override
    public Component getTreeCellRendererComponent( JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus )
    {
        if (value instanceof OlexpRootNode)
        {
            OlexpRootNode node = (OlexpRootNode) value;
            jcb.setText(node.root.name);
            jcb.setSelected( node.is_selected);
            return jcb;
        }
        if (value instanceof OlexpVersionNode)
        {
            OlexpVersionNode node = (OlexpVersionNode) value;
            jcb.setText(node.version);
            jcb.setSelected( node.is_selected);
            return jcb;
        }

        if (value instanceof OlexpFileNode)
        {
            OlexpFileNode node = (OlexpFileNode) value;

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



