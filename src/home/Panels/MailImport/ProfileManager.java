/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Panels.MailImport;

import java.io.IOException;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author mw
 */
abstract class ProfileManager
{
    abstract void fill_profile_combo( JComboBox cb ) throws IOException;
    abstract void handle_build_tree(NamePathEntry npe, JTree tree) throws IOException;
    abstract void handle_build_tree(String path, JTree tree) throws IOException;

}

interface SwitchableNode
{
    public boolean is_selected();
    public void set_selected( DefaultTreeModel model, boolean s);
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