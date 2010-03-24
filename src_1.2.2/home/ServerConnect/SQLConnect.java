/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

import dimm.home.Main;
import home.shared.SQL.SQLResult;
import dimm.home.UserMain;
import home.shared.SQL.SQLArrayResult;
import home.shared.SQL.SQLObjectGetter;
import home.shared.hibernate.AccountConnector;
import home.shared.hibernate.Backup;
import home.shared.hibernate.DiskArchive;
import home.shared.hibernate.DiskSpace;
import home.shared.hibernate.Hotfolder;
import home.shared.hibernate.ImapFetcher;
import home.shared.hibernate.MailUser;
import home.shared.hibernate.Mandant;
import home.shared.hibernate.Milter;
import home.shared.hibernate.Proxy;
import home.shared.hibernate.Role;
import java.util.ArrayList;


class SQLListContainer<T>
{
    Class list_class;
    String table_name;
    SQLResult<T>list;
    String mid_link = " where mid=";

    public SQLListContainer(Class lc, String table_name,  String mid_link )
    {
        list_class = lc;
        this.table_name = table_name;
        this.mid_link = mid_link;
    }
    public SQLListContainer(Class lc, String table_name )
    {
        this( lc, table_name, null );
    }
    void fill(SQLConnect sqc, int m_id)
    {
        StatementID sta = sqc.create_lazy_statement();
        
        StringBuffer sb = new StringBuffer();
        sb.append( "select * from ");
        sb.append(table_name);
        if (mid_link != null)
        {
            sb.append( mid_link );
            sb.append(m_id );
        }
        
        ResultSetID rs = sqc.sqc.executeQuery(sta, sb.toString() );

        SQLArrayResult resarr = sqc.sqc.get_sql_array_result(rs);
        list = new SQLResult<T>(sqc, resarr, list_class);

        sqc.sqc.close(rs);
        sqc.sqc.close(sta);
    }
}

/**
 *
 * @author mw
 */
public class SQLConnect extends Connect implements SQLObjectGetter
{

   
    int mandant_id = -1;

    ConnectionID static_c = null;
    ArrayList<SQLListContainer> sql_res_list;



    public SQLConnect()
    {
        super();
        build_sql_res_list();

    }
    public SQLConnect(String ip, int port, boolean ssl)
    {
        super(ip, port, ssl);
        build_sql_res_list();
    }

    void build_sql_res_list()
    {
        sql_res_list = new ArrayList<SQLListContainer>();



        // LIST OF PARAM ARRAYS

        sql_res_list.add( new SQLListContainer<Mandant>(Mandant.class, "mandant", null));
        sql_res_list.add( new SQLListContainer<DiskArchive>(DiskArchive.class, "disk_archive") );
        sql_res_list.add( new SQLListContainer<DiskSpace>(DiskSpace.class, "disk_space") );
        sql_res_list.add( new SQLListContainer<Hotfolder>(Hotfolder.class, "hotfolder") );
        sql_res_list.add( new SQLListContainer<ImapFetcher>(ImapFetcher.class, "imap_fetcher") );
        sql_res_list.add( new SQLListContainer<Milter>(Milter.class, "milter") );
        sql_res_list.add( new SQLListContainer<Proxy>(Proxy.class, "proxy") );
        sql_res_list.add( new SQLListContainer<Hotfolder>(Hotfolder.class, "hotfolder") );
        sql_res_list.add( new SQLListContainer<AccountConnector>(AccountConnector.class, "account_connector") );
        sql_res_list.add( new SQLListContainer<Role>(Role.class, "role") );
        sql_res_list.add( new SQLListContainer<Role>(MailUser.class, "mail_user") );
        sql_res_list.add( new SQLListContainer<Backup>(Backup.class, "backup") );
    }

    
    public void rebuild_result_array( Class list_class )
    {
        SQLListContainer slc = get_sql_list_container( list_class );

        // MISSING WAS ALREADY WARNED, ID A STRUCTURAL ERROR, NOT USER ACCESSABLE
        if (slc != null)
            slc.fill(this, mandant_id);
    }

    SQLListContainer get_sql_list_container( Class list_class)
    {
        for (int i = 0; i < sql_res_list.size(); i++)
        {
            SQLListContainer slc = sql_res_list.get(i);
            if (slc.list_class == list_class)
            {
                if (slc.list == null)
                {
                    Main.err_log("using uninitialized sql_list_container for " + list_class.getName() );
                }
                return slc;
            }
        }
        Main.err_log("get_sql_list_container  with unknown class " + list_class.getName() );
        return null;
    }

    public void rebuild_da_array()
    {
        rebuild_result_array( DiskArchive.class);
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
    
    public SQLResult<Mandant> init_mandant_list()
    {
        ConnectionID c = sqc.open("");
        if (c == null)
        {
            UserMain.errm_ok( UserMain.Txt("Cannot_connect_to_database"));
            return null;
        }
        StatementID sta = sqc.createStatement(c);

        // ALLE MANDANTEN
        ResultSetID rs = sqc.executeQuery(sta, "select * from mandant");

        SQLArrayResult resarr = sqc.get_sql_array_result(rs);

        sqc.close(rs);
        sqc.close(sta);
        sqc.close(c);

        SQLResult<Mandant> ma_res = new SQLResult<Mandant>( this, resarr, new Mandant().getClass());

        return ma_res;
    }
    


    public void init_structs(int _mandant_id)
    {
        mandant_id = _mandant_id;
        for (int i = 0; i < sql_res_list.size(); i++)
        {
            SQLListContainer slc = sql_res_list.get(i);
            slc.fill(this, mandant_id);
        }
    }


    // HELPERS
    @Override
    public Mandant get_mandant( int id )
    {
        SQLResult<Mandant>mandant_res = get_mandant_result();
        for (int i = 0; i < mandant_res.size(); i++)
        {
            Mandant mandant = mandant_res.get(i);
            if (mandant.getId() == id)
                return mandant;
        }
        return null;
    }
    @Override
    public DiskArchive get_disk_archive( int id )
    {
        SQLResult<DiskArchive>da_res = get_sql_list_container(DiskArchive.class).list;
        for (int i = 0; i < da_res.size(); i++)
        {
            DiskArchive da = da_res.get(i);
            if (da.getId() == id)
                return da;
        }
        return null;
    }

    @Override
    public AccountConnector get_account_connector( int id )
    {
        SQLResult<AccountConnector>account_res = get_sql_list_container(AccountConnector.class).list;


        for (int i = 0; i < account_res.size(); i++)
        {
            AccountConnector ac = account_res.get(i);
            if (ac.getId() == id)
                return ac;
        }
        return null;
    }

    public Mandant get_act_mandant()
    {
        SQLResult<Mandant>mandant_res = get_mandant_result();
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
        return mandant_id;
    }

    public SQLResult<Mandant> get_mandant_result()
    {
        return get_sql_list_container(Mandant.class).list;
    }
    public SQLResult<DiskArchive> get_da_result()
    {
        return get_sql_list_container(DiskArchive.class).list;
    }
    public SQLResult<Hotfolder> get_hf_result()
    {
        return get_sql_list_container(Hotfolder.class).list;
    }
    public SQLResult<ImapFetcher> get_if_result()
    {
        return get_sql_list_container(ImapFetcher.class).list;
    }
    public SQLResult<Milter> get_milter_result()
    {
        return get_sql_list_container(Milter.class).list;
    }
    public SQLResult<Proxy> get_proxy_result()
    {
        return get_sql_list_container(Proxy.class).list;
    }
    public SQLResult<Role> get_role_result()
    {
        return get_sql_list_container(Role.class).list;
    }
    public SQLResult<AccountConnector> get_account_result()
    {
        return get_sql_list_container(AccountConnector.class).list;
    }

    public synchronized boolean set_mandant_id( int _mandant_id )
    {
        mandant_id = _mandant_id;
        if (mandant_id == -1)
            return false;

      /*  ConnectionID c = sqc.open("");
        if (c == null)
        {
            UserMain.errm_ok( UserMain.Txt("Cannot_connect_to_database"));
            return false;
        }
*/
        SQLListContainer<Mandant> slc = get_sql_list_container(Mandant.class);
        slc.fill(this,mandant_id);
        
        for (int i = 0; i < slc.list.size(); i++)
        {
            Mandant mandant = slc.list.get(i);
            if (mandant.getId() == _mandant_id)
            {
                mandant_id = _mandant_id;
                init_structs(mandant_id);
                return true;
            }
        }
    
  //      sqc.close(c);
        return false;
    }

    @Override
    public Role get_role( int id )
    {
        SQLResult<Role>role_res = get_sql_list_container(Role.class).list;


        for (int i = 0; i < role_res.size(); i++)
        {
            Role ro = role_res.get(i);
            if (ro.getId() == id)
                return ro;
        }
        return null;
    }



}
