/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Panels.MailImport;

import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author mw
 */
abstract class ProfileManager
{
    abstract void init_options_gui() throws IOException;
    abstract void handle_build_tree(JTree tree) throws IOException;
    abstract int run_import(JTree tree);

    /*
    abstract void handle_build_tree(NamePathEntry npe, JTree tree) throws IOException;
    abstract void handle_build_tree(String path, JTree tree) throws IOException;
*/
    void import_olexp_file( String path )
    {
        import_mail_file( "OLEXP", path );
    }
    void import_tbird_file( String path )
    {
        import_mail_file( "TBIRD", path );
    }

    void import_mail_file( String type, String path )
    {
        
    }

    abstract String get_type();

    void build_tn_array( TreeNode tn, ArrayList<SwitchableNode> list )
    {
        int cnt = tn.getChildCount();
        for (int i = 0; i < cnt; i++)
        {
            TreeNode c = tn.getChildAt(i);
            SwitchableNode snc = (SwitchableNode)tn.getChildAt(i);

            if (c.getChildCount() > 0)
            {
                build_tn_array(c, list);
            }

            // NO DIRECTORIES
            if (!snc.contains_data())
                continue;

            if (!snc.is_selected())
                continue;

            list.add(snc);
        }
    }

    ArrayList<SwitchableNode> alloc_tree_node_array( JTree tree )
    {
        ArrayList<SwitchableNode> node_list = new ArrayList<SwitchableNode>();
        if (tree.getModel() != null)
        {
            DefaultTreeModel tm = (DefaultTreeModel) tree.getModel();
            if (tm.getRoot() != null)
            {
                MutableTreeNode root = (MutableTreeNode) tm.getRoot();
                build_tn_array(root, node_list);
            }
        }

        return node_list;
    }

    boolean has_tree_select()
    {
        return true;
    }




}

interface SwitchableNode
{
    public boolean is_selected();
    public void set_selected(  boolean s);
    public long get_size();

    public boolean contains_data();
}
class NamePathEntry extends JLabel
{
    String path;
    String name;

    NamePathEntry( String p, String n )
    {
        path = p;
        name = n;
    }

    @Override
    public String toString()
    {
        return name.toString();
    }


}