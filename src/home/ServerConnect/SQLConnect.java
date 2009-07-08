/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

import dimm.general.SQL.SQLArrayResult;
import dimm.general.SQL.SQLResult;
import dimm.general.hibernate.AccountConnector;
import dimm.general.hibernate.DiskArchive;
import dimm.general.hibernate.Hotfolder;
import dimm.general.hibernate.ImapFetcher;
import dimm.general.hibernate.Mandant;
import dimm.general.hibernate.Milter;
import dimm.general.hibernate.Role;
import dimm.general.hibernate.Proxy;
import dimm.home.UserMain;

/**
 *
 * @author mw
 */
public class SQLConnect
{

    SQLCall sqc;

    public SQLConnect()
    {
        sqc = new SQLCall();
        sqc.init();
    }

    public SQLCall get_sqc()
    {
        return sqc;
    }



    public void init_structs()
    {
        ConnectionID c = sqc.open("");
        if (c == null)
        {
            UserMain.errm_ok( UserMain.Txt("Cannot_connect_to_database"));
            return;
        }
        StatementID sta = sqc.createStatement(c);

        // ALLE MANDANTEN
        ResultSetID rs = sqc.executeQuery(sta, "select * from mandant");

        SQLArrayResult resarr = sqc.get_sql_array_result(rs);
        sqc.close(rs);

        mandant_res = new SQLResult<Mandant>(resarr, new Mandant().getClass());

        // SELECT ACTUAL MANDANT
        Mandant m = get_act_mandant();


        // HOTFOLDERS
        rs = sqc.executeQuery(sta, "select * from hotfolder where mid=" + m.getId());

        resarr = sqc.get_sql_array_result(rs);
        sqc.close(rs);

        hf_res = new SQLResult<Hotfolder>(resarr, new Hotfolder().getClass());

        // DISKARCHIVE
        rs = sqc.executeQuery(sta, "select * from disk_archive where mid=" + m.getId());

        resarr = sqc.get_sql_array_result(rs);
        sqc.close(rs);

        da_res = new SQLResult<DiskArchive>(resarr, new DiskArchive().getClass());

        // IMAPFETCHER
        rs = sqc.executeQuery(sta, "select * from imap_fetcher where mid=" + m.getId());

        resarr = sqc.get_sql_array_result(rs);
        sqc.close(rs);

        if_res = new SQLResult<ImapFetcher>(resarr, new ImapFetcher().getClass());

        // MILTER
        rs = sqc.executeQuery(sta, "select * from milter where mid=" + m.getId());

        resarr = sqc.get_sql_array_result(rs);
        sqc.close(rs);

        milter_res = new SQLResult<Milter>(resarr, new Milter().getClass());

        // PROXY
        rs = sqc.executeQuery(sta, "select * from proxy where mid=" + m.getId());

        resarr = sqc.get_sql_array_result(rs);
        sqc.close(rs);

        proxy_res = new SQLResult<Proxy>(resarr, new Proxy().getClass());

        // ROLE
        rs = sqc.executeQuery(sta, "select * from role where mid=" + m.getId());

        resarr = sqc.get_sql_array_result(rs);
        sqc.close(rs);

        role_res = new SQLResult<Role>(resarr, new Role().getClass());

        // ACCOUNT-CONNECTOR
        rs = sqc.executeQuery(sta, "select * from account_connector where mid=" + m.getId());

        resarr = sqc.get_sql_array_result(rs);
        sqc.close(rs);

        account_res = new SQLResult<AccountConnector>(resarr, new AccountConnector().getClass());

        // FINISH
        sqc.close(sta);
        sqc.close(c);
    }

    private void sql_test()
    {
        SQLCall sc = new SQLCall();
        sc.init();

        ConnectionID c = sc.open("");
        StatementID sta = sc.createStatement(c);

         ResultSetID rs = sc.executeQuery(sta, "select * from mandant");

        SQLArrayResult resarr = sc.get_sql_array_result(rs);
//        SQLResult result = new SQLResult();
        mandant_res = new SQLResult<Mandant>(resarr, new Mandant().getClass());

        for (int i = 0; i < resarr.getRows(); i++)
        {
            System.out.println(resarr.getLong(i, "id") + ", " + resarr.getString(i, "name"));
            Mandant m = mandant_res.get(i);

            System.out.println( m.getId() + ": " + m.getName() );
        }

        sc.close(rs);
        sc.close(sta);
        sc.close(c);

    }

    public Mandant get_act_mandant()
    {
        for (int i = 0; i < mandant_res.size(); i++)
        {
            Mandant mandant = mandant_res.get(i);
            if (mandant.getId() ==1)
                return mandant;
        }
        Mandant m = mandant_res.get(0);
        return m;
    }

    SQLResult<Mandant>  mandant_res;
    SQLResult<Hotfolder>  hf_res;
    SQLResult<DiskArchive>  da_res;
    SQLResult<ImapFetcher>  if_res;
    SQLResult<Milter>  milter_res;
    SQLResult<Proxy>  proxy_res;
    SQLResult<Role>  role_res;
    SQLResult<AccountConnector>  account_res;

    public SQLResult<Mandant> get_mandant_result()
    {
        return mandant_res;
    }
    public SQLResult<DiskArchive> get_da_result()
    {
        return da_res;
    }
    public SQLResult<Hotfolder> get_hf_result()
    {
        return hf_res;
    }
    public SQLResult<ImapFetcher> get_if_result()
    {
        return if_res;
    }
    public SQLResult<Milter> get_milter_result()
    {
        return milter_res;
    }
    public SQLResult<Proxy> get_proxy_result()
    {
        return proxy_res;
    }
    public SQLResult<Role> get_role_result()
    {
        return role_res;
    }
    public SQLResult<AccountConnector> get_account_result()
    {
        return account_res;
    }



}
