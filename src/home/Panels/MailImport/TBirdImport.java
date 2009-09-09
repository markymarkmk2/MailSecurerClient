/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Panels.MailImport;

import dimm.home.Main;
import dimm.home.UserMain;
import dimm.home.Utilities.INIFile;
import dimm.home.Utilities.SizeStr;
import dimm.home.Utilities.demork.Demork;
import dimm.home.Utilities.demork.MorkUtils;
import dimm.home.Utilities.demork.database.MorkCell;
import dimm.home.Utilities.demork.database.MorkDatabase;
import dimm.home.Utilities.demork.database.MorkRow;
import dimm.home.Utilities.demork.database.MorkTable;
import dimm.home.native_libs.NativeLoader;
import home.shared.CS_Constants;
import java.awt.Component;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeNode;

/**
 *
 * @author mw
 */
class TBirdFilenameFilter implements FilenameFilter
{
    static String extension = ".msf";
    boolean recursive;

    TBirdFilenameFilter()
    {
        recursive = true;
    }
    @Override
    public boolean accept( File dir, String name )
    {
        if (name.toLowerCase().endsWith(extension))
        {
            // DOES MAILBOX FILE EXIST ALSO?
            if (new File(dir, name.substring(0, name.lastIndexOf(extension))).exists())
                return true;
        }

        if (recursive)
        {
            File fdir = new File( dir, name );
            if (fdir.isDirectory())
            {
                // SBD ARE HANDLED INTERNALLY
                if (!fdir.getName().toLowerCase().endsWith(".sbd"))
                    return true;
            }
        }


        return false;
    }

    static String get_mbox_name_from_msfg( String name )
    {
        if (name.toLowerCase().endsWith(extension))
        {
            return name.substring(0, name.toLowerCase().lastIndexOf(extension));
        }
        return null;
    }
}




class TBirdTreeNode implements MutableTreeNode, SwitchableNode
{
    MutableTreeNode parent;
    File node;
    TBirdFilenameFilter filter;
    TBirdTreeNode[] child_list;
    boolean is_selected;
    String[] default_sel_offnames = {"Junk", "Spam", "Trash", "Drafts", "Templates"};
    String display_name;
    DefaultTreeModel model = null;


    TBirdTreeNode( DefaultTreeModel _model, TBirdTreeNode p, File n )
    {
        parent = p;
        filter = new TBirdFilenameFilter();
        node = n;
        is_selected = true;
        display_name = null;
        model = _model;

        for (int i = 0; i < default_sel_offnames.length; i++)
        {
            String no_sel = default_sel_offnames[i];
            if (node.getName().indexOf(no_sel) != -1)
                is_selected = false;
        }
    }
    TBirdTreeNode( DefaultTreeModel _model, File r, String _display_name )
    {
        parent = null;
        filter = new TBirdFilenameFilter();
        node = r;
        is_selected = true;  // THE WHOLE TREE
        display_name = _display_name;
        model = _model;
    }

    @Override
    public TreeNode getChildAt( int childIndex )
    {
        check_children();

        if (childIndex < child_list.length)
            return child_list[childIndex];

        return null;
    }
    File get_file()
    {
        return node;
    }
    

    @Override
    public int getChildCount()
    {
        check_children();

        return child_list.length;
    }

    @Override
    public TreeNode getParent()
    {
        return parent;
    }

    @Override
    public int getIndex( TreeNode _n )
    {
        TBirdTreeNode n = (TBirdTreeNode)_n;
        check_children();

        for (int i = 0; i < child_list.length; i++)
        {
            File file = child_list[i].get_file();
            if (file.equals( n.get_file() ))
            {
                return i;
            }
        }
        return -1;
    }
    void check_children()
    {
        if (child_list == null)
        {
            File[] _flist;

            if (node.isDirectory())
                _flist = node.listFiles(filter);
            else
            {
                // WE ARE MSF
                _flist = new File[0];

                // MAYBE WE HAVE A SBD-SUBFOLDER
                String fp = node.getAbsolutePath();
                if (fp.endsWith(".msf"))
                {
                    // LOOK IN SBD FOLDER FOR SUB-DIRS
                    File sbd_dir = new File( fp.substring(0, fp.length() - 4) + ".sbd" );
                    if (sbd_dir.exists())
                        _flist = sbd_dir.listFiles(filter);
                }
            }

            child_list = new TBirdTreeNode[_flist.length];
            for (int i = 0; i < _flist.length; i++)
            {
                File file = _flist[i];
                child_list[i] = new TBirdTreeNode( model, this, file );
            }
        }
    }

    @Override
    public boolean getAllowsChildren()
    {
        return true;
    }

    @Override
    public boolean isLeaf()
    {
        check_children();

        if (child_list.length == 0)
            return true;//node.isFile();

        return false;

    }

    @Override
    public Enumeration children()

    {
        Enumeration en = new Enumeration()
        {
            int idx = 0;

            @Override
            public boolean hasMoreElements()
            {
                return (idx < getChildCount());
            }

            @Override
            public Object nextElement()
            {
                return getChildAt(idx++);
            }
        };

        return en;
    }
    String get_mbox_name()
    {
        if (display_name != null)
            return display_name;
        
        if (node.isDirectory())
            return node.getName();

        return TBirdFilenameFilter.get_mbox_name_from_msfg( node.getName() );
    }
    
    File get_mailbox()
    {
        if (node.isDirectory())
            return null;

        String mbox_name = TBirdFilenameFilter.get_mbox_name_from_msfg( node.getAbsolutePath() );
        if (mbox_name != null)
        {
            return new File( mbox_name );
        }
        return null;
    }


    @Override
    public void set_selected(boolean s)
    {
        is_selected = s;
        model.nodeChanged(this);

        check_children();

        for (int i = 0; i < child_list.length; i++)
        {
            TBirdTreeNode mboxTreeNode = child_list[i];
            mboxTreeNode.set_selected(s);
        }
    }

    @Override
    public void insert( MutableTreeNode child, int index )
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove( int index )
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void remove( MutableTreeNode node )
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setUserObject( Object object )
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void removeFromParent()
    {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void setParent( MutableTreeNode newParent )
    {
        parent =  newParent;
    }

    @Override
    public boolean is_selected()
    {
        return is_selected;
    }

    @Override
    public boolean contains_data()
    {
        return node.isFile();  // MSF-DIR AND FILE CONTAIN DATA
    }

   
}
class TBirdTreeModel extends DefaultTreeModel
{
    TBirdTreeModel( )
    {
        super(null);
    }

}
class TBirdTreeCellRenderer implements TreeCellRenderer
{

    JCheckBox jcb;
    JLabel jlb;

    TBirdTreeCellRenderer()
    {
        jcb = new JCheckBox();
        jlb = new JLabel();
        jlb.setForeground(Main.ui.get_nice_white() );
        jlb.setBackground(Main.ui.get_appl_dgray() );
    }

    @Override
    public Component getTreeCellRendererComponent( JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus )
    {
        if (value instanceof TBirdTreeNode)
        {
            TBirdTreeNode node = (TBirdTreeNode) value;
            String text = node.get_mbox_name();
            File f = node.get_mailbox();
            if (f != null)
            {
                String len_text = "  (" + new SizeStr( f.length() ).toString() + ")";
                text = text + len_text;
            }
            jcb.setText( text);
            jcb.setSelected( node.is_selected());
            return jcb;

        }

        jlb.setText("");
        return jlb;
    }
}



class TBirdProfileManager extends ProfileManager
{

    String build_appdata_path()
    {
        /*
         # On Linux, the path is usually ~/.thunderbird/xxxxxxxx.default/
        # On Mac OS X, the path is usually ~/Library/Thunderbird/Profiles/xxxxxxxx.default/
         */
        if (NativeLoader.is_win())
        {
            String user_appdata_path = NativeLoader.get_HKCU_reg_value("Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "AppData");
            if (user_appdata_path == null)
            {
                user_appdata_path = NativeLoader.get_HKCU_reg_value("Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\User Shell Folders", "AppData");
            }
            user_appdata_path = NativeLoader.resolve_win_path(user_appdata_path);

            return user_appdata_path + "\\Thunderbird";
        }
        if (NativeLoader.is_linux())
        {
            File f = new File( "~/.thunderbird");
            if (f.exists())
                return f.getAbsolutePath();
        }
        if (NativeLoader.is_osx())
        {
            File f = new File( "~/Library/Thunderbird");
            if (f.exists())
                return f.getAbsolutePath();
        }
        return null;

    }

    @Override
    String get_type()
    {
        return CS_Constants.TYPE_TBIRD;

    }



/*
TBIRD profiles.ini

[General]
StartWithLastProfile=1

[Profile0]
Name=default
IsRelative=1
Path=Profiles/nl1ice4b.default


*/
    @Override
    void fill_profile_combo( JComboBox cb ) throws IOException
    {
        cb.removeAllItems();
        
        String tbird_appdata = build_appdata_path();

        File prf = new File ( tbird_appdata + "/profiles.ini" );
        if (!prf.exists())
        {
            throw new IOException( UserMain.Txt("Cannot_find_Thunderbird_profiles_path") );
        }
        INIFile objINI = new INIFile(prf.getAbsolutePath());

        try
        {
            for (int i = 0; ; i++)
            {
                String name = objINI.getStringProperty("Profile" + i, "Name");
                String is_rel = objINI.getStringProperty("Profile" + i, "IsRelative");
                String path = objINI.getStringProperty("Profile" + i, "Path");
                if (name == null)
                    break;

                if (is_rel.charAt(0) == '1')
                {
                    path = tbird_appdata + "/" + path;
                }
                
                
                if (new File(path).exists())
                {
                    NamePathEntry tbpe = new NamePathEntry( path , name );
                    cb.addItem(tbpe);
                }

            }
        }
        catch (Exception exc)
        {
        }
        NamePathEntry tbpe = new NamePathEntry( null, UserMain.Txt("Select_mail_directory_manually") );

        cb.addItem(tbpe);
    }

    @Override
    void handle_build_tree(NamePathEntry npe, JTree tree) throws IOException
    {
        ArrayList<String>mail_root_dirs = new ArrayList<String>();

        File panacea = new File( npe.path + "/panacea.dat");
        if (panacea.exists())
        {
            try
            {
                // NOW FIND ALL UNIQUE MAIL PATHS IN PANACEA CONFIG AND ADD TO LIST
                Demork demork = new Demork();

                String filename = panacea.getAbsolutePath();

                String encoding = demork.getEncoding(filename);

                String data = MorkUtils.readWholeFileAsEncoding(filename, encoding);

                // Determine the file type and process accordingly
                if (data.indexOf("<mdb:mork") != -1)
                {
                    MorkDatabase db = demork.inputMork(data);
                    for (String tk : db.tables.keySet())
                    {
                        MorkTable t = db.tables.get(tk);
                        for (String rk : t.rows.keySet())
                        {
                            MorkRow r = t.rows.get(rk);
                            //xml.append("\t<row key='"+rk+"'>\n");

                            for (MorkCell c : r.cells)
                            {
                                if (c.column.compareTo("key") ==  0)
                                {
                                    if (c.atom.contains("Thunderbird\\Profiles") || c.atom.contains("Thunderbird/Profiles") || c.atom.contains("Thunderbird\\\\Profiles"))
                                    {
                                        int idx = c.atom.lastIndexOf("Mail");
                                        if (idx > 0)
                                        {
                                            String mail_path = c.atom.substring(0, idx + 4);
                                            boolean found = false;
                                            mail_path = tb_decode( mail_path );

                                            // SKIP !EXISTING
                                            if (!(new File(mail_path).exists()))
                                                continue;


                                            for (int i = 0; i < mail_root_dirs.size() && !found; i++)
                                            {
                                                String string = mail_root_dirs.get(i);
                                                if (mail_path.compareTo(string) == 0)
                                                    found = true;
                                            }

                                            // SKIP DUPLS
                                            if (!found)
                                                mail_root_dirs.add( mail_path );
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception exception)
            {
                exception.printStackTrace();
            }
            TBirdTreeModel model = new TBirdTreeModel();

            DefaultMutableTreeNode root_node = new DefaultMutableTreeNode("");
            for (int i = 0; i < mail_root_dirs.size(); i++)
            {
                String mail_dir_path = mail_root_dirs.get(i);

                String name = "Profile " + i;
                mail_dir_path = mail_dir_path.replaceAll("\\\\\\\\", "/");
                mail_dir_path = mail_dir_path.replaceAll("\\\\", "/");
                int idxe = mail_dir_path.lastIndexOf("/");
                if (idxe >0)
                {
                    int idxs = mail_dir_path.lastIndexOf("/", idxe - 1);
                    if (idxs >= 0)
                    {
                        name = mail_dir_path.substring(idxs + 1, idxe);
                    }
                }

                TBirdTreeNode node = new TBirdTreeNode( model, new File( mail_dir_path ), name );
                root_node.add(node);
            }

            model.setRoot(root_node);
            tree.setModel(model);
            tree.setCellRenderer( new TBirdTreeCellRenderer() );
        }
    }

    @Override
    void handle_build_tree(String path, JTree tree) throws IOException
    {
        // WE HAVE AN ABSOLUTE MAILPATH, JUST TRY TO BUILD TBIRD TREE
        TBirdTreeModel model = new TBirdTreeModel();
        TBirdTreeNode node = new TBirdTreeNode( model, new File( path ), "Default" );
        model.setRoot(node);
        tree.setModel(model);
        tree.setCellRenderer( new TBirdTreeCellRenderer() );
        tree.setRootVisible(false);
    }

    private String tb_decode( String mail_path )
    {
        int idx = mail_path.indexOf('$');
        if (idx == -1)
            return mail_path;

        while (idx != -1)
        {
            try
            {
                StringBuffer sb = new StringBuffer();
                sb.append( mail_path.substring(0, idx) );

                char ch = (char)Integer.parseInt(mail_path.substring(idx + 1, idx + 3), 16);

                switch( ch)
                {
                    case '\n': sb.append("/n"); break;
                    case '\t': sb.append("/t"); break;
                    case '\b': sb.append("/b"); break;
                    case '\f': sb.append("/f"); break;
                    case '\r': sb.append("/r"); break;
                }
                
                sb.append(mail_path.substring(idx + 3) );

                mail_path = sb.toString();

                idx = mail_path.indexOf('$');
            }
            catch( Exception exc)
            {
                exc.printStackTrace();
                return mail_path;
            }
        }
        return mail_path;
    }
}