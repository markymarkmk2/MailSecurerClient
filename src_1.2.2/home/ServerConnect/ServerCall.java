/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

import home.shared.SQL.SQLArrayResult;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Administrator
 */
public abstract class ServerCall {
    String last_cmd;
    long last_duration_ms;
    long last_end;
    int last_err_code;
    String last_err_txt;
    Exception last_ex;
    String last_return;
    long last_start;

    public static final int SHORT_CMD_TO = 60;

    public ServerCall()
    {
    }

    public abstract void close();
    public abstract boolean Delete( StatementID sta, Object o );

    public abstract String GetFirstSqlField( ConnectionID connection_id, String qry, int field );

    public abstract int GetFirstSqlFieldInt( ConnectionID connection_id, String qry, int field );

    public abstract boolean Insert( StatementID sta, Object o );

    public abstract boolean Update( StatementID sta, Object o, Object save_o );

    void calc_stat( String ret )
    {
        last_end = System.currentTimeMillis();
        last_duration_ms = last_end - last_start;
        last_return = ret;
    }

    public abstract String close( ConnectionID c );

    public abstract String close( StatementID c );

    public abstract String close( ResultSetID c );

    public abstract boolean close_in_stream( InStreamID id );
    public abstract boolean close_out_stream( OutStreamID id );
    public abstract boolean close_delete_out_stream( OutStreamID id);

    public abstract StatementID createStatement( ConnectionID c );

    public abstract boolean execute( StatementID sta, String qry );

    public abstract ResultSetID executeQuery( StatementID sta, String qry );

    public abstract int executeUpdate( StatementID sta, String qry );

    public long get_last_duration()
    {
        // PROTECT DIV ZERO
        if (last_duration_ms == 0)
            last_duration_ms = 1;

        return last_duration_ms;
    }

    public String get_last_err()
    {
        String ex_str = "";
        if ( last_ex != null )
        {
            ex_str = last_ex.getMessage();
        }
        return "ServerCall <" + last_cmd + "> Code:" + last_err_code + " Txt:" + last_err_txt + " EX:" + ex_str;
    }

    public int get_last_err_code()
    {
        return last_err_code;
    }

    public String get_last_err_txt()
    {
        return last_err_txt;
    }

    public Exception get_last_ex()
    {
        return last_ex;
    }

    public abstract SQLArrayResult get_sql_array_result( ResultSetID r );

    void init_stat( String cmd )
    {
        last_start = System.currentTimeMillis();
        last_err_txt = "";
        last_return = "";
        last_cmd = cmd;
        last_err_code = 0;
        last_duration_ms = 0;
        last_ex = null;
    }

    public abstract ConnectionID open();

    public abstract ConnectionID open( String db );
    public abstract ConnectionID open_audit( String db );

    public abstract InStreamID open_in_stream( String file );

    public abstract OutStreamID open_out_stream( String file );

    public abstract boolean read_in_stream( InStreamID id, OutputStream os );
    public abstract int  read_in_stream( InStreamID id, byte[] data);
    public abstract boolean write_out_stream( OutStreamID id, long len, InputStream is);
    public abstract boolean write_out_stream( OutStreamID id, byte[] data);

    public abstract boolean send_tcp_byteblock( String str, byte[] data);



    public abstract boolean send_fast_retry_cmd(String str);
    public abstract String send( String str, long len, OutputStream outp, int to_s);
    public abstract String send_rmx( String str, long len, InputStream is, int to_s);
    public abstract String send( String str);
    public abstract String send( String str, int to_s);
    public abstract String send_rmx( String str, int to_s);

    
    String get_name_from_hibernate_class( Object o )
    {
        return get_name_from_hibernate_class(o.getClass().getSimpleName());
    }

    String get_name_from_hibernate_class( String rec_name )
    {
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < rec_name.length(); i++ )
        {
            char ch = new Character(rec_name.charAt(i));
            if ( i == 0 )
            {
                ch = Character.toLowerCase(ch);
            }
            else
            {
                if ( Character.isUpperCase(ch) )
                {
                    sb.append('_');
                    ch = Character.toLowerCase(ch);
                }
            }
            sb.append(ch);
        }
        return sb.toString();
    }

        public static String decode_pipe( String s )
    {
        int idx = s.indexOf("^");
        if ( idx == -1 )
        {
            return s;
        }
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < s.length(); i++ )
        {
            char ch = s.charAt(i);
            if ( ch != '^' )
            {
                sb.append(ch);
                continue;
            }
            if ( i + 2 < s.length() )
            {
                if ( s.charAt(i + 1) == '7' && s.charAt(i + 2) == 'C' )
                {
                    sb.append('|');
                    i += 2;
                }
                if ( s.charAt(i + 1) == '5' && s.charAt(i + 2) == 'E' )
                {
                    sb.append('^');
                    i += 2;
                }
            }
        }
        return sb.toString();
    }

    public static String encode_pipe( String s )
    {
        int idx = s.indexOf("|");
        if ( idx == -1 )
        {
            return s;
        }
        StringBuffer sb = new StringBuffer();
        for ( int i = 0; i < s.length(); i++ )
        {
            char ch = s.charAt(i);
            if ( ch != '^' && ch != '|' )
            {
                sb.append(ch);
                continue;
            }
            sb.append('^');
            if ( ch == '^' )
            {
                sb.append("5E");
            }
            else if ( ch == '|' )
            {
                sb.append("7C");
            }
        }
        return sb.toString();
    }

    public abstract boolean init();

    public abstract boolean DeleteObject( Object o );

}
