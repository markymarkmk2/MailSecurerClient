/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Panels.MailImport;

import com.microsoft.schemas.exchange.services._2006.messages.ExchangeServicePortType;
import com.microsoft.schemas.exchange.services._2006.types.BaseFolderIdType;
import com.microsoft.schemas.exchange.services._2006.types.BaseFolderType;
import com.microsoft.schemas.exchange.services._2006.types.DistinguishedFolderIdNameType;
import com.microsoft.schemas.exchange.services._2006.types.DistinguishedFolderIdType;
import com.microsoft.schemas.exchange.services._2006.types.ExchangeVersionType;
import com.microsoft.schemas.exchange.services._2006.types.ItemType;
import dimm.home.Main;
import dimm.home.Models.DiskArchiveComboModel;
import dimm.home.UserMain;
import dimm.home.Utilities.SwingWorker;
import home.shared.CS_Constants;
import home.shared.Utilities.ParseToken;
import home.shared.Utilities.SizeStr;
import home.shared.exchange.ExchangeAuthenticator;
import home.shared.exchange.dao.ItemTypeDAO;
import home.shared.exchange.util.ExchangeEnvironmentSettings;
import home.shared.hibernate.DiskArchive;
import java.awt.Component;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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


class ExchangeNode extends DefaultMutableTreeNode implements SwitchableNode
{
    
    boolean is_selected;
    DefaultTreeModel model;

    List<ItemType> mail_list;
    BaseFolderIdType folder;
    String name;

    // ABS PATH
    ExchangeNode( String _name, BaseFolderIdType _folder, DefaultTreeModel _model, List<ItemType> _mail_list )
    {
        name = _name;
        folder = _folder;
        model = _model;
        mail_list = _mail_list;
     

        children = new Vector<ExchangeNode>();
        is_selected = false;

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
        return (getChildCount() > 0 || (mail_list != null && mail_list.size() > 0));
    }

    @Override
    public boolean isLeaf()
    {
        return (children.isEmpty());
    }

    @Override
    public String toString()
    {
        if (name.length() == 0)
            return "Server";
        long size = get_size();
        return name + " " + new SizeStr(size);
    }

    @Override
    public long get_size()
    {
        if (mail_list == null)
            return 0;

        long size = 0;
        for (int i = 0; i < mail_list.size(); i++)
        {
            ItemType mail = mail_list.get(i);
            size += mail.getSize();
        }
        return size;
    }

}


class ExchangeTreeModel extends DefaultTreeModel
{
    ExchangeTreeModel( )
    {
        super(null);
    }
}
class ExchangeTreeCellRenderer implements TreeCellRenderer
{

    JCheckBox jcb;
    JLabel jlb;

    ExchangeTreeCellRenderer()
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
        if (value instanceof ExchangeNode)
        {
            ExchangeNode node = (ExchangeNode) value;
            jcb.setText(node.toString());
            jcb.setSelected( node.is_selected);
            return jcb;
        }
        
        jlb.setText("");
        return jlb;
    }
}



class ExchangeImportManager extends ProfileManager
{
   ExchangeOptsPanel opts_panel;
   PanelImportMailbox dialog;


    public ExchangeImportManager(PanelImportMailbox _dialog)
    {
        dialog = _dialog;
        this.opts_panel = new ExchangeOptsPanel(dialog);
        dialog.set_opts_panel( opts_panel );

    }

    @Override
    void init_options_gui( ) throws IOException
    {
        ExchangeVersionType exv[] = ExchangeEnvironmentSettings.get_exch_versions();

        opts_panel.CB_VERSION.removeAllItems();
        for (int i = 0; i < exv.length; i++)
        {
            ExchangeVersionType exchangeVersionType = exv[i];
            opts_panel.CB_VERSION.addItem(exchangeVersionType.value());
        }                     
    }

    @Override
    String get_type()
    {
        return CS_Constants.TYPE_EXCHANGE;
    }

    
    @Override
    void handle_build_tree( final JTree tree) throws IOException
    {
        UserMain.self.show_busy(dialog.getDlg(), UserMain.Txt("Connecting") + "...", true);

        SwingWorker sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                try
                {

                    ExchangeNode node = null;
                    DefaultTreeModel model = new ExchangeTreeModel();
                    DistinguishedFolderIdType msgFolderRoot = new DistinguishedFolderIdType();
                    msgFolderRoot.setId(DistinguishedFolderIdNameType.MSGFOLDERROOT);
                    node = new ExchangeNode( "", msgFolderRoot, model, null  );

                    model.setRoot(node);

                    ExchangeAuthenticator.reduce_ssl_security();

                    String user = opts_panel.TXT_USER.getText();
                    String domain = opts_panel.TXT_DOMAIN.getText();
                    String server = opts_panel.TXT_SERVER.getText();
                    String pwd = new String( opts_panel.TXTP_PWD.getPassword() );

                    ExchangeServicePortType port = ExchangeAuthenticator.open_exchange_port( user, pwd, domain, server );
                    ExchangeEnvironmentSettings settings = new ExchangeEnvironmentSettings( ExchangeEnvironmentSettings.get_cultures()[0], ExchangeVersionType.EXCHANGE_2007_SP_1 );

                    // Test out the new ItemTypeDAO functionality.
                    ItemTypeDAO itemTypeDAO = new ItemTypeDAO(settings);
                    itemTypeDAO.getFolderItems(port);

                    List<BaseFolderType>folders =  itemTypeDAO.GetFolders( port );

                    for (Iterator<BaseFolderType> it = folders.iterator(); it.hasNext();)
                    {
                        BaseFolderType baseFolderType = it.next();

                        add_exchange_folder( model, node, itemTypeDAO, port, baseFolderType );
                    }


                    tree.setModel(model);
                    tree.setCellRenderer( new ExchangeTreeCellRenderer() );
                    tree.setRootVisible(true);
                }
                catch (Exception exc)
                {
                    exc.printStackTrace();
                    UserMain.self.hide_busy();
                    UserMain.errm_ok(dialog.getDlg(), exc.getMessage());
                }
                finally
                {
                    UserMain.self.hide_busy();
                }
                return null;
            }
        };
        sw.start();
    }

    private void add_exchange_folder( DefaultTreeModel model, ExchangeNode node, ItemTypeDAO itemTypeDAO, ExchangeServicePortType port, BaseFolderType baseFolderType ) throws IOException
    {
        List<ItemType> mails = itemTypeDAO.getFolderItems( port, baseFolderType.getFolderId() );

        ExchangeNode child_node = new ExchangeNode( baseFolderType.getDisplayName(), baseFolderType.getFolderId(), model, mails );

        node.add(child_node);

        if (baseFolderType.getChildFolderCount() > 0)
        {
            List<BaseFolderType>folders =  itemTypeDAO.GetFolders(port, baseFolderType.getFolderId() );

            for (Iterator<BaseFolderType> it = folders.iterator(); it.hasNext();)
            {
                BaseFolderType sub_folder = it.next();

                add_exchange_folder( model, child_node, itemTypeDAO, port, sub_folder );
            }
        }
    }

    @Override
    int run_import( JTree tree )
    {
        ArrayList<SwitchableNode> node_list = alloc_tree_node_array( tree );

        if (node_list.size() > 0)
        {
            return run_import(node_list);
        }
        return 0;
    }

    int run_import( ArrayList<SwitchableNode> node_list )
    {
        int files_uploaded = 0;
        try
        {
            files_uploaded = 0;

            dialog.import_status_list.add(UserMain.Txt("Connecting_server"));
            long total_size = 0;
            long act_size = 0;

            for (int i = 0; i < node_list.size(); i++)
            {
                total_size += node_list.get(i).get_size();
            }

            ArrayList<BaseFolderIdType>folder_list = new ArrayList<BaseFolderIdType>();

            for (int i = 0; i < node_list.size(); i++)
            {
                // DETECT USER ABORT
                if (dialog.abort_import)
                {
                    dialog.import_status_list.add(UserMain.Txt("Aborted_import"));
                    break;
                }

                // GET FILE FROM NODE
                if (!(node_list.get(i) instanceof ExchangeNode))
                {
                    continue;
                }
                ExchangeNode node = (ExchangeNode)node_list.get(i);
                folder_list.add(node.folder);
            }

            int mandant_id = UserMain.sqc().get_act_mandant_id();

            // RETRIEVE DA DROM COMBO
            DiskArchiveComboModel dacm = (DiskArchiveComboModel) dialog.CB_VAULT.getModel();
            DiskArchive da = dacm.get_selected_da();
            int da_id = da.getId();

            // SEND UPLOAD REQUEST
            // IMPORT MAIL RETURNS A HANDLE FOR AN OPEN STREAM
            String xml_list = ParseToken.BuildCompressedObjectString( folder_list );
            String ret = UserMain.fcc().get_sqc().send("import_exchange_folder MA:" + mandant_id + " DA:" + da_id + " FD:" + xml_list);

            // CHECK FOR ERROR
            int idx = ret.indexOf(':');
            dialog.import_err_code = Integer.parseInt(ret.substring(0, idx));
            if (dialog.import_err_code != 0)
            {
                dialog.import_status_list.add(UserMain.Txt("Transfer_failed") + ": " + ret.substring(idx + 2));
            }
            else
            {
                dialog.import_status_list.add(UserMain.Txt("Import_started") + ": " + ret.substring(idx + 2));
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            dialog.import_status_list.add(UserMain.Txt("Transfer_failed") + " " + ex.getMessage());
            UserMain.errm_ok(dialog.getDlg(), UserMain.Txt("Unknown_error_during_import") + " "+ ex.getMessage());
        }
        dialog.finished = true;
        return files_uploaded;
    }
}