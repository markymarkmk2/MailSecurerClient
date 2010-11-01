/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Panels.MailImport;

import dimm.home.Models.DiskArchiveComboModel;
import dimm.home.ServerConnect.OutStreamID;
import dimm.home.UserMain;
import home.shared.Utilities.SizeStr;
import home.shared.hibernate.DiskArchive;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JTree;

/**
 *
 * @author mw
 */
abstract public class FileImportProfileManager extends ProfileManager
{

    PanelImportMailbox dialog;

    public FileImportProfileManager( PanelImportMailbox dialog )
    {
        this.dialog = dialog;
    }

    private File get_file_from_node( SwitchableNode node )
    {
        File mbox = null;

        // GET FILE FROM NODE
        if (node instanceof TBirdTreeNode)
        {
            TBirdTreeNode tbn = (TBirdTreeNode) node;
            mbox = tbn.get_mailbox();
        }
        if (node instanceof FileNode)
        {
            FileNode tbn = (FileNode) node;
            mbox = tbn.node;
        }

        return mbox;
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
    
    int run_import( final ArrayList<SwitchableNode> node_list )
    {
        int files_uploaded = 0;
        try
        {
            FileInputStream fis;
            files_uploaded = 0;

            dialog.import_status_list.add(UserMain.Txt("Connecting_server"));
            long total_size = 0;
            long act_size = 0;

            for (int i = 0; i < node_list.size(); i++)
            {
                total_size += node_list.get(i).get_size();
            }


            for (int i = 0; i < node_list.size(); i++)
            {
                // DETECT USER ABORT
                if (dialog.abort_import)
                {
                    dialog.import_status_list.add(UserMain.Txt("Aborted_import"));
                    break;
                }

                // GET FILE FROM NODE
                File mbox = get_file_from_node(node_list.get(i));
                if (mbox == null)
                {
                    continue;
                }

                // GET MANMDANT / DISKARCHIVE / SIZE
                long file_len = mbox.length();
                int mandant_id = UserMain.sqc().get_act_mandant_id();

                // RETRIEVE DA DROM COMBO
                DiskArchiveComboModel dacm = (DiskArchiveComboModel) dialog.CB_VAULT.getModel();
                DiskArchive da = dacm.get_selected_da();
                int da_id = da.getId();

                // SEND UPLOAD REQUEST
                // IMPORT MAIL RETURNS A HANDLE FOR AN OPEN STREAM
                String ret = UserMain.fcc().get_sqc().send("upload_mail_file MA:" + mandant_id + " TY:" + dialog.manager.get_type() + " SI:" + file_len);

                // CHECK FOR ERROR
                int idx = ret.indexOf(':');
                dialog.import_err_code = Integer.parseInt(ret.substring(0, idx));
                if (dialog.import_err_code != 0)
                {
                    dialog.import_status_list.add(UserMain.Txt("Transfer_failed") + ": " + ret.substring(idx + 2));
                    continue;
                }

                String transfer_txt = UserMain.Txt("Transfering") + " " + mbox.getName() + " " + new SizeStr(file_len).toString();
                dialog.import_status_list.add(transfer_txt);

                // GET STREAM HANDLE FROM ANSWER
                OutStreamID oid = new OutStreamID(ret.substring(idx + 2));

                // OPEN I-STREAM
                try
                {
                    fis = new FileInputStream(mbox);
                }
                catch (FileNotFoundException fileNotFoundException)
                {
                    dialog.import_status_list.add(UserMain.Txt("Cannot_open_Mailbox") + " " + mbox.getAbsolutePath());
                    continue;
                }

                // SEND FILE TO SERVER
                boolean bret = UserMain.fcc().get_sqc().write_out_stream(oid, file_len, fis);
                try
                {
                    fis.close();
                }
                catch (IOException iOException)
                {
                }

                // SEND FAILED ?
                if (!bret)
                {
                    dialog.import_status_list.add(UserMain.Txt("Transfer_failed"));
                    UserMain.fcc().get_sqc().close_delete_out_stream(oid);
                    continue;
                }

                if (file_len > 1024 * 1024)
                {
                    // REPLACE LAST ENTRY WITH STATUSTEXT WITH RATIO
                    long duration_ms = UserMain.fcc().get_sqc().get_last_duration();
                    transfer_txt += " " + new SizeStr((file_len * 1000.0f) / duration_ms).toString() + "/s";
                    dialog.import_status_list.set(dialog.import_status_list.size() - 1, transfer_txt);
                }

                act_size += file_len;
                double percent = act_size * 100.0 / total_size;
                UserMain.self.show_busy_val(percent);


                // NOW BEGIN THE IMPORT IN BACKGROUND (BG:1) FOR THE OPEN STREAM
                ret = UserMain.fcc().get_sqc().send("import_mail_file OI:" + oid.getId() + " MA:" + mandant_id + " DA:" + da_id + " BG:1");
                if (ret != null && ret.charAt(0) == '0')
                {
                    dialog.import_status_list.add(UserMain.Txt("Import_started_for") + " " + mbox.getName());

                    // SET AS HANDLES IN TREE, ONLY THE ERROR FILES STAY SELECTED
                    SwitchableNode node = (SwitchableNode) node_list.get(i);
                    node.set_selected(false);
                    files_uploaded++;
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            UserMain.errm_ok(dialog.getDlg(), UserMain.Txt("Unknown_error_during_import") + " "+ ex.getMessage());
        }
        dialog.finished = true;
        return files_uploaded;
    }
}
