/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

import dimm.general.SQL.SQLResult;
import dimm.home.UserMain;
import home.shared.SQL.SQLArrayResult;
import home.shared.hibernate.AccountConnector;
import home.shared.hibernate.DiskArchive;
import home.shared.hibernate.Hotfolder;
import home.shared.hibernate.ImapFetcher;
import home.shared.hibernate.Mandant;
import home.shared.hibernate.Milter;
import home.shared.hibernate.Proxy;
import home.shared.hibernate.Role;



/**
 *
 * @author mw
 */
public class SQLConnect extends Connect
{

    SQLResult<Mandant>  mandant_res;
    SQLResult<Hotfolder>  hf_res;
    SQLResult<DiskArchive>  da_res;
    SQLResult<ImapFetcher>  if_res;
    SQLResult<Milter>  milter_res;
    SQLResult<Proxy>  proxy_res;
    SQLResult<Role>  role_res;
    SQLResult<AccountConnector>  account_res;
    int mandant_id = -1;

    ConnectionID static_c = null;



    public SQLConnect()
    {
        super();
    }


    public void rebuild_da_array( long m_id)
    {
        ConnectionID c = sqc.open("");
        if (c == null)
        {
            UserMain.errm_ok( UserMain.Txt("Cannot_connect_to_database"));
            return;
        }
        StatementID sta = sqc.createStatement(c);
        // DISKARCHIVE
        ResultSetID rs = sqc.executeQuery(sta, "select * from disk_archive where mid=" + m_id);

        SQLArrayResult resarr = sqc.get_sql_array_result(rs);
        da_res = new SQLResult<DiskArchive>(resarr, new DiskArchive().getClass());
        
        sqc.close(rs);

        // FINISH
        sqc.close(sta);
        sqc.close(c);
    }


    StatementID create_lazy_statement()
    {
        StatementID sta = null;

        if (static_c != null)
        {
            sta = sqc.createStatement(static_c);
            if (sta == null)
            {
                sqc.close(static_c);
                static_c = null;
            }
        }


        if (static_c == null)
        {
            static_c = sqc.open("");
            if (static_c == null)
            {
                UserMain.errm_ok( UserMain.Txt("Cannot_connect_to_database"));
                return null;
            }
            sta = sqc.createStatement(static_c);
        }

        if (sta == null)
        {
            UserMain.errm_ok( UserMain.Txt("Cannot_create_statement"));
            return null;
        }
        return sta;
    }
    // USE
    public SQLArrayResult build_sql_arraylist_lazy( String cmd )
    {
        StatementID sta = create_lazy_statement();

        if (sta == null)
            return null;

        // ALLE MANDANTEN
        ResultSetID rs = sqc.executeQuery(sta, cmd);

        SQLArrayResult resarr = sqc.get_sql_array_result(rs);
        sqc.close(rs);
        sqc.close(sta);

        return resarr;
    }

  // USE
    public int sql_lazy_update( String cmd )
    {
        StatementID sta = create_lazy_statement();

        if (sta == null)
            return 0;

        // ALLE MANDANTEN
        int rows = sqc.executeUpdate(sta, cmd);

        sqc.close(sta);

        return rows;
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
        long m_id = -1;
        Mandant m = get_act_mandant();
        if ( m != null)
            m_id = m.getId();


        // HOTFOLDERS
        rs = sqc.executeQuery(sta, "select * from hotfolder where mid=" + m_id);

        resarr = sqc.get_sql_array_result(rs);
        sqc.close(rs);

        hf_res = new SQLResult<Hotfolder>(resarr, new Hotfolder().getClass());

        // DISKARCHIVE
        rs = sqc.executeQuery(sta, "select * from disk_archive where mid=" + m_id);

        resarr = sqc.get_sql_array_result(rs);
        sqc.close(rs);

        da_res = new SQLResult<DiskArchive>(resarr, new DiskArchive().getClass());

        // IMAPFETCHER
        rs = sqc.executeQuery(sta, "select * from imap_fetcher where mid=" + m_id);

        resarr = sqc.get_sql_array_result(rs);
        sqc.close(rs);

        if_res = new SQLResult<ImapFetcher>(resarr, new ImapFetcher().getClass());

        // MILTER
        rs = sqc.executeQuery(sta, "select * from milter where mid=" +m_id);

        resarr = sqc.get_sql_array_result(rs);
        sqc.close(rs);

        milter_res = new SQLResult<Milter>(resarr, new Milter().getClass());

        // PROXY
        rs = sqc.executeQuery(sta, "select * from proxy where mid=" + m_id);

        resarr = sqc.get_sql_array_result(rs);
        sqc.close(rs);

        proxy_res = new SQLResult<Proxy>(resarr, new Proxy().getClass());

        // ROLE
        rs = sqc.executeQuery(sta, "select * from role where mid=" + m_id);

        resarr = sqc.get_sql_array_result(rs);
        sqc.close(rs);

        role_res = new SQLResult<Role>(resarr, new Role().getClass());

        // ACCOUNT-CONNECTOR
        rs = sqc.executeQuery(sta, "select * from account_connector where mid=" + m_id);

        resarr = sqc.get_sql_array_result(rs);
        sqc.close(rs);

        account_res = new SQLResult<AccountConnector>(resarr, new AccountConnector().getClass());

        // FINISH
        sqc.close(sta);
        sqc.close(c);
    }
/*
    private void sql_test()
    {
        ServerWSDLCall sc = new ServerWSDLCall();
        sc.init();

        ConnectionID c = sc.open("");
        StatementID sta = sc.createStatement(c);

         ResultSetID rs = sc.executeQuery(sta, "select * from mandant");

        SQLArrayResult resarr = sc.get_sql_array_result(rs);

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
*/
    public Mandant get_act_mandant()
    {
        for (int i = 0; i < mandant_res.size(); i++)
        {
            Mandant mandant = mandant_res.get(i);
            if (mandant.getId() == mandant_id)
                return mandant;
        }
        return null;
    }
    public int get_act_mandant_id()
    {
        for (int i = 0; i < mandant_res.size(); i++)
        {
            Mandant mandant = mandant_res.get(i);
            if (mandant.getId() == mandant_id)
                return mandant.getId();
        }
        return -1;
    }

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

    public boolean set_mandant_id( int _mandant_id )
    {
        for (int i = 0; i < mandant_res.size(); i++)
        {
            Mandant mandant = mandant_res.get(i);
            if (mandant.getId() ==_mandant_id)
            {
                mandant_id = _mandant_id;
                init_structs();
                return true;
            }
        }
        mandant_id = -1;
        return false;
    }



}
