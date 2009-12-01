/*
 * SQLBoxListBuilder.java
 *
 * Created on 15. Oktober 2007, 11:25
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;


import home.shared.SQL.SQLResult;
import dimm.home.UserMain;
import home.shared.SQL.SQLArrayResult;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;


/**
 *
 * @author Administrator
 */
public class SQLListBuilder
{
    Connection c;
    
//    private static boolean is_local = true;
    private static boolean is_local = false;
    private static boolean is_offline = false;
   
    private static String net_db_user="sonicbox";
    private static String net_db_pwd="123fckw456";
    private static String net_db_connect = "jdbc:mysql://thales.ebiz-webhosting.de/db1";
    private static String net_db_dataconnect = "jdbc:mysql://thales.ebiz-webhosting.de/sb_result_db";
    
    private static final int ACTIVITYWINDOW_S = 1800; // 30 Minuten
    public static final String OLD_PARA_DB = "db1";

    // SAME AS SQLWORKER
    static SimpleDateFormat sdf = new SimpleDateFormat ("dd.MM.yyyy HH:mm:ss");    
    
    static HashMap<String, Connection> lazy_connections;
    static ArrayList<SQLArrayResult> cache_list;
    
    /** Creates a new instance of SQLBoxListBuilder */
    public SQLListBuilder()
    {   
        if ( is_local)
        {
           /* net_db_user="mw";
            net_db_pwd="helikon";*/
            net_db_connect = "jdbc:mysql://localhost/db1";
            net_db_dataconnect = "jdbc:mysql://localhost/sb_result_db";
        }

        c = null;
        if (cache_list == null)
            cache_list = new ArrayList<SQLArrayResult>();
        if (lazy_connections == null)
            lazy_connections = new HashMap<String, Connection>();       
    }




    @Override
    protected void finalize() throws Throwable
    {
        close();
    }
    
    public boolean close()
    {
        if (c != null)
        {
            try
            {
                c.close();
            } catch (SQLException ex)
            {
                ex.printStackTrace();
                return false;
            }
            finally
            {
                c = null;
            }
        }
        return true;
    }
    public boolean close(Connection conn)
    {
        if (conn != null)
        {
            try
            {
                conn.close();
            } catch (SQLException ex)
            {
                ex.printStackTrace();
                return false;
            }
        }
        return true;
    }

    public static boolean is_offline()
    {
        return is_offline;
    }
    public static void set_offline(boolean o)
    {
        is_offline = o;
    }
    
    public boolean check_online()
    {
        is_offline = false;
        
        Connection c = open(OLD_PARA_DB);
        if (c != null)
        {
            try
            {
                c.close();
                return true;
            }
            catch (SQLException ex)
            {
               
            }
        }
        is_offline = true;
        return false;
    }
    
  
    
    public boolean open()
    {
        if (is_offline())
            return false;
        
  /*      String db_conn_str = Main.get_prop( Preferences.DB_CONNECT, net_db_dataconnect );
        String db_user = Main.get_prop( Preferences.DB_USER, net_db_user );
        String db_pwd = Main.get_prop( Preferences.DB_PWD,net_db_pwd );
        
        try
        {
            // LOAD LOCAL AND REMOTE DB
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            
            String hsql_db_class_name = Main.get_prop( Preferences.DB_CLASSNAME, "org.hsqldb.jdbcDriver" );
            Class.forName(hsql_db_class_name );
            
            DriverManager.setLoginTimeout(5);
            c = DriverManager.getConnection(db_conn_str, db_user, db_pwd);
                         
        } 
        catch (Exception ex)
        {
            is_offline = true;
            ex.printStackTrace();            
            return false;
        }        
    */    return true;
        
    }
    public Connection open(String db_name)
    {
        if (is_offline())
            return null;
        
        Connection conn = null;
        
      /*  String db_conn_str = Main.get_prop( Preferences.DB_CONNECT, net_db_dataconnect );
        String db_user = Main.get_prop( Preferences.DB_USER, net_db_user );
        String db_pwd = Main.get_prop( Preferences.DB_PWD,net_db_pwd );
        int db_start_idx = db_conn_str.lastIndexOf("/");
        db_conn_str = db_conn_str.substring(0, db_start_idx) + "/" + db_name;
        
        try
        {
            // LOAD LOCAL AND REMOTE DB
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            
            String hsql_db_class_name = Main.get_prop( Preferences.DB_CLASSNAME, "org.hsqldb.jdbcDriver" );
            Class.forName(hsql_db_class_name );
            
            DriverManager.setLoginTimeout(5);
            conn = DriverManager.getConnection(db_conn_str, db_user, db_pwd);                                   
        } 
        catch (Exception ex)
        {
            is_offline = true;
            ex.printStackTrace();            
            return null;
        }*/
        return conn;
        
    }
    
    
    
    public static String html_to_native( String txt )
    {
        return SQLResult.html_to_native(txt);
    }        
    
    static public void clear_sql_cache( String qry )
    {
        for (int i = 0; i < cache_list.size(); i++)
        {
            if (cache_list.get(i).getQry().compareToIgnoreCase(qry) == 0)
            {
                cache_list.remove(i);
                i--;
            }                
        }
    }
    static public void clear_sql_cache()
    {
        if (cache_list != null)
            cache_list.clear();
    }
    static SQLArrayResult get_from_sql_cache( String qry )
    {
        if (cache_list != null)
        {
            for (int i = 0; i < cache_list.size(); i++)
            {
                if (cache_list.get(i).getQry().compareToIgnoreCase(qry) == 0)
                {
                    return cache_list.get(i);
                }                
            }
        }
        return null;
    }
    
    public void clear_lazy_connections()
    {
        Collection<Connection> values = lazy_connections.values();        
        for ( Iterator<Connection> it = values.iterator(); it.hasNext();)
        {
            close(it.next());
        }                
    }
    
    public SQLArrayResult build_sql_arraylist_lazy( String db,  String qry )
    {
        return build_sql_arraylist(  db,  qry, /*cachde*/false, /*lazy*/true );
    }
    public SQLArrayResult build_sql_arraylist_lazy_cached( String db,  String qry )
    {
        return build_sql_arraylist(  db,  qry, /*cached*/true, /*lazy*/true );
    }
    public boolean sql_call( String db,  String cmd, boolean lazy )
    {
        boolean result = true;
        Connection conn = null;
        boolean was_opened = false;
        
        try
        {                       
            if (lazy)
            {
                conn = lazy_connections.get(db);
            }               
            if (conn == null)
            {
                conn = open(db);
                was_opened = true;
            }
            if (conn != null)
            {
                Statement sta = conn.createStatement();    

                sta.execute(cmd);

                sta.close();   
            }
            
         }        
        catch (Exception exc)
        {
            UserMain.self.err_log("Call of <" + cmd + "> gave :" + exc.getMessage());
            result = false;
        }
        finally
        {
            handle_lazy_close( conn, db, lazy, was_opened );
        }
        
        // MAYBE AN OPEN CONNECTION WAS CLOSED, HANDLE THIS
        if (result == false && lazy == true)
        {
            result = sql_call( db, cmd, false );
        }
                
        return result;
    }
    public boolean sql_call_lazy(String db, String cmd)
    {
        return sql_call( db, cmd, true );
    }
    
    public int sql_update( String db,  String cmd, boolean lazy )
    {
        int result = 0;
        Connection conn = null;
        boolean was_opened = false;
        boolean failed = false;
        try
        {                       
            if (lazy)
            {
                conn = lazy_connections.get(db);
            }               
            if (conn == null)
            {
                conn = open(db);
                was_opened = true;
            }
            if (conn != null)
            {
                Statement sta = conn.createStatement();    

                result = sta.executeUpdate(cmd);

                sta.close();   
            }            
         }        
        catch (Exception exc)
        {
            UserMain.self.err_log("Update of <" + cmd + "> gave :" + exc.getMessage());
            result = 0;
            failed = true;
        }
        finally
        {
            handle_lazy_close( conn, db, lazy, was_opened );
        }
        if (failed && lazy)
        {
            result = sql_update( db, cmd, false );
        }
                
        return result;
    }
    public int sql_update_lazy(String db, String cmd)
    {
        return sql_update( db, cmd, true );
    }

    void handle_lazy_close( Connection conn, String db, boolean lazy, boolean was_opened )
    {
        if (was_opened)
        {                
            if (lazy)
                lazy_connections.put(db, conn);
            else 
                close(conn);
        }        
    }
    
    public String get_sql_qry_field( String db,  String qry, int f_no, boolean lazy )
    {
        String result = null;
        Connection conn = null;
        boolean was_opened = false;
        try
        {                       
            if (lazy)
            {
                conn = lazy_connections.get(db);
            }               
            if (conn == null)
            {
                conn = open(db);
                was_opened = true;
            }
            if (conn != null)
            {
                Statement sta = conn.createStatement();                                      
                ResultSet rs = sta.executeQuery( qry );       
                if (rs.next() == true)
                {
                    result = rs.getString( f_no + 1);
                }


                rs.close();
                sta.close();   
            }            
        }        
        catch (Exception exc)
        {
            UserMain.self.err_log("GetField of <" + qry + "> gave :" + exc.getMessage());
            result = null;
        }
        finally
        {
            handle_lazy_close( conn, db, lazy, was_opened );
        }
        if (result == null && lazy)
        {
            result = get_sql_qry_field( db, qry, f_no, false );
        }
                
        return result;        
    }
    public String get_sql_first_string_lazy( String db,  String qry )
    {
       
        String res = get_sql_qry_field( db,  qry, 0, true );
        return res;
    }
    public long get_sql_first_long_lazy( String db,  String qry )
    {
        long l = -1;
        try
        {
            l = Long.parseLong(get_sql_first_string_lazy(db, qry));
        } 
        catch (Exception numberFormatException)
        {
            l = -1;
        }
        
        return l;
    }
     
    public int get_sql_first_int_lazy( String db,  String qry )
    {
        int l = -1;
        try
        {
            l = Integer.parseInt(get_sql_first_string_lazy(db, qry));
        } 
        catch (Exception numberFormatException)
        {
            l = -1;
        }
        
        return l;
    }
     
    
  
    public SQLArrayResult build_sql_arraylist( String db,  String qry, boolean cached, boolean lazy )
    {
        if (cached)
        {
            SQLArrayResult r = get_from_sql_cache( qry );
            if (r != null)
                return r;
        }

        
        clear_sql_cache( qry );
        
        SQLArrayResult result = new SQLArrayResult( qry );
        
        Connection conn = null;
        boolean was_opened = false;
        boolean failed = false;
        
        ArrayList<ArrayList> list = new ArrayList<ArrayList>();
        try
        {                       
            if (lazy)
            {
                conn = lazy_connections.get(db);
            }               
            if (conn == null)
            {
                conn = open(db);
                was_opened = true;
            }
            if (conn != null)
            {
                Statement sta = conn.createStatement();                                      
                ResultSet rs = null;
                try
                {
                    rs = sta.executeQuery(qry);

                } catch ( SQLException sQLException )
                {
                    if (sQLException.getMessage().indexOf("Communication link failur") > -1)
                    {
                        conn = open(db);
                        was_opened = true;
                        sta = conn.createStatement(); 
                        rs = sta.executeQuery(qry);
                    }     
                    else
                        throw sQLException;
                }

                while (rs.next() == true)
                {
                    int cnt = rs.getMetaData().getColumnCount();

                    if (result.getFieldList() == null)
                    {
                        ArrayList<String> fieldList = new ArrayList<String>(cnt);
                        ArrayList<String> fieldTypeList = new ArrayList<String>(cnt);

                        for (int i = 1; i <= cnt; i++)
                        {
                            String name =  rs.getMetaData().getColumnName(i);
                            String type =  rs.getMetaData().getColumnTypeName(i);
                            fieldList.add(name);
                            fieldTypeList.add(type);

                        }
                        result.setFieldList(fieldList);
                        result.setFieldTypeList(fieldTypeList);
                    }


                    ArrayList<String> field_list = new ArrayList<String>(cnt);

                    for (int i = 1; i <= cnt; i++)
                    {
                        field_list.add(rs.getString(i));
                    }
                    list.add( field_list );
                }   
                result.setResultList(list);

                rs.close();
                sta.close();   

                if (cached)
                {
                    cache_list.add( result);
                }
            }
        }        
        catch (Exception exc)
        {
            UserMain.self.err_log("ArrayList of <" + qry + "> gave :" + exc.getMessage());
            result.setException(exc);
            failed = true;
        }
        finally
        {
            handle_lazy_close( conn, db, lazy, was_opened );
        }
        if (failed && lazy)
        {
            return build_sql_arraylist( db,  qry, cached, false );
        }
        
        return result;
    }
    
     
     
}
