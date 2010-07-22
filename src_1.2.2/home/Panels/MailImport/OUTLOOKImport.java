/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Panels.MailImport;

import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;
import dimm.home.Main;
import dimm.home.UserMain;
import home.shared.Utilities.SizeStr;
import dimm.home.native_libs.NativeLoader;
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


/*
 Inzwischen habe ich den Speicherort der PST Datei innerhalb des Outlook-Profils in der Registry gefunden. Der Speicherort ist an drei Stellen in der Registry festgelegt:

[HKEY_CURRENT_USER\Software\Microsoft\Windows NT\CurrentVersion\Windows Messaging Subsystem\Profiles\Outlook\0a0d020000000000c000000000000046]
"1102039b"



[HKEY_CURRENT_USER\Software\Microsoft\Windows NT\CurrentVersion\Windows Messaging Subsystem\Profiles\Outlook\Variabler Wert]
"001f6700"      Pfad zur PST
"001e3001"      Name des Kontos
"001e3006"      OriginalName des Kontos

Der SubKey des Zweiten Schlüssels ist von Outlook-Profil zu Outlook-Profil immer ein anderer, enthält jedoch immer die gleichen Werte.

 * */

/**
 *
 * @author mw
 */
class OutlookFilenameFilter implements FilenameFilter
{
    static String extension = ".pst";
    boolean recursive;

    OutlookFilenameFilter()
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









class OutlookRootNode extends DefaultMutableTreeNode implements SwitchableNode
{
    NamePathEntry root;
    boolean is_selected;
    String path;
    DefaultTreeModel model;

    // ABS PATH
    OutlookRootNode( DefaultTreeModel _model, String _path )
    {
       path = _path;
       File f = new File(path);
       model = _model;

       children = new Vector<OutlookFileNode>();

       if (f.exists() && f.isDirectory())
       {
            File[] dbx_files = f.listFiles(new OutlookFilenameFilter());

            for (int i = 0; i < dbx_files.length; i++)
            {
                File file = dbx_files[i];

                children.add(new OutlookFileNode( model, file ) );
            }
       }
       if (this.getChildCount() > 0)
            is_selected = true;

    }


    // DATA FROM REGISTRY
    OutlookRootNode( DefaultTreeModel _model, NamePathEntry npe_profile )
    {
        root = npe_profile;
        model = _model;
        children = new Vector<OutlookVersionNode>();

        try
        {
            RegistryKey regkey = Registry.HKEY_CURRENT_USER;
            RegistryKey key = Registry.openSubkey(regkey, "Software\\Microsoft\\Windows NT\\CurrentVersion\\Windows Messaging Subsystem\\" + root.path, RegistryKey.ACCESS_ALL);

            int nkeys = key.getNumberSubkeys();

            for (int i = 0; i < nkeys; i++)
            {
                String ol_scheissname = key.regEnumKey(i);

                RegistryKey ol_scheissname_key = Registry.openSubkey(key, ol_scheissname, RegistryKey.ACCESS_ALL);

                String store_path = ol_scheissname_key.getStringValue("001e6700");
                String store_name = ol_scheissname_key.getStringValue("001e3001");

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
                ol_scheissname_key.closeKey();


                NamePathEntry pe = new NamePathEntry(store_path, store_name);

                OutlookVersionNode child = new OutlookVersionNode( model, store_path, store_name);
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
class OutlookVersionNode  extends DefaultMutableTreeNode implements SwitchableNode
{
    String path;
    String version;
    boolean is_selected;
    DefaultTreeModel model;

    OutlookVersionNode( DefaultTreeModel _model, String _path, String _version )
    {
        path = _path;
        version = _version;
        model = _model;
        children = new Vector<OutlookFileNode>();

        File f = new File(path);
        if (f.exists() && f.isDirectory())
        {
            File[] dbx_files = f.listFiles(new OutlookFilenameFilter());

            for (int i = 0; i < dbx_files.length; i++)
            {
                File file = dbx_files[i];

                OutlookFileNode child = new OutlookFileNode( model, file );
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
class OutlookFileNode  extends FileNode
{
    OutlookFileNode( DefaultTreeModel _model, File f )
    {
        super( _model, f );
    }

    @Override
    String get_mbox_name()
    {
        if (node.getName().endsWith(OutlookFilenameFilter.extension))
        {
            return node.getName().substring(0, node.getName().length() - OutlookFilenameFilter.extension.length());
        }
        return "?";
    }

    @Override
    public boolean contains_data()
    {
        return true;
    }
}
class OutlookTreeModel extends DefaultTreeModel
{
    OutlookTreeModel( )
    {
        super(null);
    }
}
class OutlookTreeCellRenderer implements TreeCellRenderer
{

    JCheckBox jcb;
    JLabel jlb;

    OutlookTreeCellRenderer()
    {
        jcb = new JCheckBox();
        jlb = new JLabel();
        jlb.setForeground(Main.ui.get_foreground() );
        jlb.setBackground(Main.ui.get_background() );
    }

    @Override
    public Component getTreeCellRendererComponent( JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus )
    {
        if (value instanceof OutlookRootNode)
        {
            OutlookRootNode node = (OutlookRootNode) value;
            jcb.setText(node.root.name);
            jcb.setSelected( node.is_selected);
            return jcb;
        }
        if (value instanceof OutlookVersionNode)
        {
            OutlookVersionNode node = (OutlookVersionNode) value;
            jcb.setText(node.version);
            jcb.setSelected( node.is_selected);
            return jcb;
        }

        if (value instanceof OutlookFileNode)
        {
            OutlookFileNode node = (OutlookFileNode) value;

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



class OutlookProfileManager extends ProfileManager
{
   
    @Override
    void fill_profile_combo( JComboBox cb ) throws IOException
    {
        cb.removeAllItems();

        try
        {
            RegistryKey regkey = Registry.HKEY_CURRENT_USER;
            RegistryKey key = Registry.openSubkey(regkey, "Software\\Microsoft\\Windows NT\\CurrentVersion\\Windows Messaging Subsystem\\Profiles", RegistryKey.ACCESS_ALL);

            int nkeys = key.getNumberSubkeys();

            for (int i = 0; i < nkeys; i++)
            {
                String identityname = key.regEnumKey(i);
                RegistryKey identity_key = Registry.openSubkey(key, identityname, RegistryKey.ACCESS_ALL);

                //String user_name = identity_key.getStringValue("Username");

                identity_key.closeKey();

                identityname = NativeLoader.conv_win_to_utf8( identityname );

                NamePathEntry pe = new NamePathEntry(identityname, identityname);
                cb.addItem(pe);
            }

            key.closeKey();
        }
        catch (RegistryException registryException)
        {
        }

        
        NamePathEntry tbpe = new NamePathEntry( null, UserMain.Txt("Select_mail_directory_manually") );
        
        cb.addItem(tbpe);
    }

    @Override
    String get_type()
    {
        return CS_Constants.TYPE_OUTLOOK;
    }

    @Override
    void handle_build_tree(String path, JTree tree) throws IOException
    {
        OutlookRootNode node = null;
        DefaultTreeModel model = new OutlookTreeModel();
        node = new OutlookRootNode( model, path );
        model.setRoot(node);
        tree.setModel(model);
        tree.setCellRenderer( new OutlookTreeCellRenderer() );
        tree.setRootVisible(false);
    }
    @Override
    void handle_build_tree(NamePathEntry npe_profile, JTree tree) throws IOException
    {
        OutlookRootNode node = null;
        OutlookTreeModel model = new OutlookTreeModel();
        node = new OutlookRootNode( model, npe_profile );
        model.setRoot(node);
        tree.setModel(model);
        tree.setCellRenderer( new OutlookTreeCellRenderer() );
        tree.setRootVisible(false);
    }


  
}