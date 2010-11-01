/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Panels.MailImport;

import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;
import dimm.home.UserMain;
import dimm.home.native_libs.NativeLoader;
import home.shared.CS_Constants;
import java.io.IOException;
import javax.swing.JComboBox;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;

/**
 *
 * @author mw
 */
class OlexpProfileManager extends FileImportProfileManager
{

    ComboProfileOptsPanel opts_panel;

    public OlexpProfileManager(PanelImportMailbox dialog)
    {
        super(dialog);

        this.opts_panel = new ComboProfileOptsPanel(dialog);
        dialog.set_opts_panel( opts_panel );

    }

    @Override
    void init_options_gui( ) throws IOException
    {
        JComboBox cb = opts_panel.get_combo();
        cb.removeAllItems();

        try
        {
            RegistryKey regkey = Registry.HKEY_CURRENT_USER;
            RegistryKey key = Registry.openSubkey(regkey, "Identities", RegistryKey.ACCESS_ALL);

            int nkeys = key.getNumberSubkeys();

            for (int i = 0; i < nkeys; i++)
            {
                String identityname = key.regEnumKey(i);
                RegistryKey identity_key = Registry.openSubkey(key, identityname, RegistryKey.ACCESS_ALL);

                String user_name = identity_key.getStringValue("Username");

                identity_key.closeKey();

                user_name = NativeLoader.conv_win_to_utf8( user_name );

                NamePathEntry pe = new NamePathEntry(identityname, user_name);
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
        return CS_Constants.TYPE_OLEXP;
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
        OlexpRootNode node = null;
        DefaultTreeModel model = new OlexpTreeModel();
        node = new OlexpRootNode( model, path );
        model.setRoot(node);
        tree.setModel(model);
        tree.setCellRenderer( new OlexpTreeCellRenderer() );
    }
    
    void handle_build_tree(NamePathEntry npe_profile, JTree tree) throws IOException
    {
        OlexpRootNode node = null;
        OlexpTreeModel model = new OlexpTreeModel();
        node = new OlexpRootNode( model, npe_profile );
        model.setRoot(node);
        tree.setModel(model);
        tree.setCellRenderer( new OlexpTreeCellRenderer() );
    }

    String build_user_profile_path()
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


            int idx = user_appdata_path.lastIndexOf("\\");
            if (idx > 0)
                user_appdata_path = user_appdata_path.substring(0, idx);

            return user_appdata_path;
        }
        return null;
    }


}