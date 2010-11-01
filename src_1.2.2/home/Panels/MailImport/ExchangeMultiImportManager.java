/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Panels.MailImport;

import com.microsoft.schemas.exchange.services._2006.types.BaseFolderIdType;
import com.microsoft.schemas.exchange.services._2006.types.ExchangeVersionType;
import dimm.home.Models.DiskArchiveComboModel;
import dimm.home.UserMain;
import home.shared.CS_Constants;
import home.shared.Utilities.ParseToken;
import home.shared.exchange.util.ExchangeEnvironmentSettings;
import home.shared.hibernate.DiskArchive;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JTree;




/**
 *
 * @author mw
 */





class ExchangeMultiImportManager extends ProfileManager
{
   MultiExchangeOptsPanel opts_panel;
   PanelImportMailbox dialog;


    public ExchangeMultiImportManager(PanelImportMailbox _dialog)
    {
        dialog = _dialog;
        this.opts_panel = new MultiExchangeOptsPanel(dialog);
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
        tree.setVisible(false);
    }

    @Override
    int run_import( JTree tree )
    {
        return run_import();
    }
    
    int run_import( )
    {
        int files_uploaded = 0;
        try
        {
            files_uploaded = 0;

            dialog.import_status_list.add(UserMain.Txt("Connecting_server"));
            long total_size = 0;
            long act_size = 0;
            


            int mandant_id = UserMain.sqc().get_act_mandant_id();

            // RETRIEVE DA DROM COMBO
            DiskArchiveComboModel dacm = (DiskArchiveComboModel) dialog.CB_VAULT.getModel();
            DiskArchive da = dacm.get_selected_da();
            int da_id = da.getId();

            // SEND UPLOAD REQUEST
            // IMPORT MAIL RETURNS A HANDLE FOR AN OPEN STREAM
            String user_opts = "";
            if (!opts_panel.BT_ALL_USERS.isSelected())
            {
                String xml_list = ParseToken.BuildCompressedObjectString( opts_panel.sel_user_list );
                user_opts = " UL:" + xml_list;
            }
            String folder_opts = "";
            if (!opts_panel.BT_ALL_BOXES.isSelected())
            {
                String xml_list = ParseToken.BuildCompressedObjectString( opts_panel.get_selected_folder_id_list() );
                folder_opts = " FD:" + xml_list;

                if (opts_panel.add_user_folders())
                {
                    folder_opts += " UF:1";
                }
            }

            String ret = UserMain.fcc().get_sqc().send("import_exchange MD:folder MA:" + mandant_id + " DA:" + da_id + " AC:" + opts_panel.accm.get_act_id() +  user_opts + folder_opts);

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