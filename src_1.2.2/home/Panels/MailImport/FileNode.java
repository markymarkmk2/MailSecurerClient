/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Panels.MailImport;

import java.io.File;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author mw
 */
abstract class FileNode  extends DefaultMutableTreeNode implements SwitchableNode
{
    boolean is_selected;
    String[] default_sel_offnames = {"Junk", "Spam", "Trash", "Drafts", "Templates"};
    File node;
    DefaultTreeModel model = null;


    FileNode( DefaultTreeModel _model, File f )
    {
        node = f;
        is_selected = true;
        for (int i = 0; i < default_sel_offnames.length; i++)
        {
            String no_sel = default_sel_offnames[i];
            if (node.getName().indexOf(no_sel) != -1)
                is_selected = false;
        }
        model = _model;
    }
    abstract String get_mbox_name();

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
    public void set_selected(  boolean s )
    {
        is_selected = s;
        model.nodeChanged(this);

        for (int i = 0; i < getChildCount(); i++)
        {
            SwitchableNode mboxTreeNode = (SwitchableNode)children.get(i);
            mboxTreeNode.set_selected( s);
        }
    }
}