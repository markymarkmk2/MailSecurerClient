/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dimm.home.ServerConnect;

import java.lang.reflect.Method;
import home.shared.hibernate.DiskArchive;
import home.shared.hibernate.DiskSpace;
import home.shared.hibernate.Mandant;
import com.thoughtworks.xstream.XStream;

import home.shared.CS_Constants;
import home.shared.SQL.SQLArrayResult;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import org.apache.commons.codec.binary.Base64;

/**
 *
 * @author mw
 */
public class ServerTCPCall extends ServerCall
{

    public static final String MISS_ARGS = "missing args";
    public static final String WRONG_ARGS = "wrong args";
    public static final String UNKNOWN_CMD = "UNKNOWN_COMMAND";

    private static final String RMX_PREFIX = "RMX_";
    private static final String CALL_PREFIX = "call_";

/*    MWWebServiceService service;
    MWWebService port;*/
    String name;
   


    private static final int TCP_LEN = 64;
    
    public static final int SHORT_CMD_TO = 60;

    Socket keep_s;
    boolean keep_tcp_open;
    String last_ip;
    private int ping;
    boolean mallorca_proxy = false;
    private boolean ping_connected = false;

    String status;
    String answer;
    String server;
    int port;

    void set_status( String s )
    {
        status = s;
    }

    public ServerTCPCall(String _server, String _port)
    {
        System.setProperty("javax.net.ssl.trustStore", "jxws.ts");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");


        server = _server;
        port = Integer.parseInt(_port);

    }

    @Override
    public boolean init()
    {
        try
        {
            return true;
        }
        catch (Exception e)
        {
            // TODO
            e.printStackTrace();
            last_ex = e;
            return false;
        }
    }

    public String get_answer()
    {
        return answer;
    }

    public String get_answer_err_text()
    {
        if (answer.indexOf(MISS_ARGS) >= 0)
        {
            return "Fehlende Argumente";
        }
        if (answer.indexOf(WRONG_ARGS) >= 0)
        {
            return "Fehlerhafte Argumente";
        }
        if (answer.indexOf(UNKNOWN_CMD) >= 0)
        {
            return "Sorry, dieser Befehl wird von der Sonicbox nicht unterstützt";
        }

        return answer;
    }

    public boolean check_answer( String a )
    {
        boolean ok = false;
        if (a == null || a.length() == 0)
        {
            answer = "Kommunikation fehlgeschlagen!";
            return false;
        }


        if (a.compareTo("--failed--") == 0)
        {
            answer =  "Kommunikation fehlgeschlagen!";
            return false;
        }

        if (a.compareTo("UNKNOWN_COMMAND") == 0)
        {
            answer =  "Oha, dieser Befehl wird von der Box nicht unterstützt!";
            return false;
        }


        if (a.length() >= 2 && a.substring(0, 2).compareTo("OK") == 0)
        {
            ok = true;
            if (a.length() > 3)
                answer = a.substring(3);
            else
                answer = "";

            ok = true;
        }
        else if (a.length() >= 3 && a.substring(0, 3).compareTo("NOK") == 0)
        {
            ok = false;
            if (a.length() > 4)
                answer = a.substring(4);
            else
                answer = "";

        }
        return ok;
    }


    String ping_answer;
    public String get_ping_answer()
    {
        return ping_answer;
    }
    public int ping( String ip, int port, int delay_ms )
    {
        int ret = -1;
        //System.out.println("Calling ping for " + ip + ":...");

        Socket s;
        boolean keep_sock_open = false;

        if (keep_s == null || last_ip.compareTo( ip ) != 0)
        {
            s = new Socket();
            SocketAddress saddr = new InetSocketAddress( ip, port );
            try
            {
                s.setSoTimeout( delay_ms);
                s.connect( saddr, delay_ms );
                s.setTcpNoDelay(true);
            }
            catch (Exception exc)
            {
                System.out.println( " Fehler: " + exc.getMessage() );
                ret = -1;
                return ret;
            }
        }
        else
        {
            s = keep_s;
            keep_sock_open = true;
        }

        try
        {

            ping_answer = tcp_send( s, "GETSTATUS MD:SHORT", 0, (InputStream)null, null, CALL_PREFIX );

            ret = ping;
            System.out.println(ret + " ms");

        }
        catch (java.net.SocketTimeoutException texc)
        {
            ret = -1;
            keep_s = null;
            keep_sock_open = false;
            System.out.println( " Timeout");
        }
        catch (Exception exc)
        {
            System.out.println( " Error: " + exc.getMessage() );
            keep_s = null;
            keep_sock_open = false;
            ret = -1;
        }
        finally
        {
            try
            {
                if (!keep_sock_open)
                {
                   s.close();
                }
            }
            catch (IOException ex)
            {
            }
        }

        return ret;
    }

    void reopen( String ip, int port, int timeout ) throws SocketException, IOException
    {
            if (last_ip != null)
            {
                if (last_ip.compareTo( ip ) != 0)
                {
                    comm_close();
                }
            }

            if (keep_s == null)
            {
                keep_s = new Socket();
                keep_s.setTcpNoDelay( true );
                keep_s.setReuseAddress( true );

               // keep_s.setSendBufferSize( 6000 );
               // keep_s.setReceiveBufferSize( 60000 );

                if (timeout > 0)
                    keep_s.setSoTimeout(timeout* 1000);
                else
                    keep_s.setSoTimeout(0);

                SocketAddress saddr = new InetSocketAddress( ip, port );

                if (timeout > 0)
                    keep_s.connect( saddr, timeout*1000 );
                else
                    keep_s.connect( saddr, 10*1000);
            }
            else
            {
                if (timeout > 0)
                    keep_s.setSoTimeout(timeout* 1000);
                else
                    keep_s.setSoTimeout(0);
            }
    }

    public String tcp_send( String ip, int port, String str, long len, OutputStream outp, byte[] add_data, int timeout, String prefix)
    {
        try
        {
            reopen( ip, port, timeout );
            
            Socket s = keep_s;

            String ret = tcp_send( s, str, len, outp, add_data, prefix);


            // LATCH IP
            last_ip = ip;

            set_status( "" );

            return ret;
        }

        //  throws SocketException, IOException, Exception
        catch ( SocketTimeoutException texc )
        {
             this.comm_close();
             return "--timeout--";
        }
        catch ( java.net.ConnectException cexc)
        {
            this.comm_close();
            set_status("Kommunikation schlug fehl: " + cexc.getMessage());
        }
        catch ( Exception exc )
        {
             this.comm_close();
             //exc.printStackTrace();
             set_status("Kommunikation schlug fehl: " + exc.getMessage());
        }

        return "--failed--";
    }
    
    public String tcp_send( String ip, int port, String str, long len, InputStream inp, int timeout, String prefix)
    {
        try
        {
            reopen( ip, port, timeout );

            Socket s = keep_s;

            String ret = tcp_send( s, str, len, inp, null, prefix);


            // LATCH IP
            last_ip = ip;

            set_status( "" );

            return ret;
        }

        //  throws SocketException, IOException, Exception
        catch ( SocketTimeoutException texc )
        {
             this.comm_close();
             return "--timeout--";
        }
        catch ( java.net.ConnectException cexc)
        {
            this.comm_close();
            set_status("Kommunikation schlug fehl: " + cexc.getMessage());
        }
        catch ( Exception exc )
        {
             this.comm_close();
             //exc.printStackTrace();
             set_status("Kommunikation schlug fehl: " + exc.getMessage());
        }

        return "--failed--";
    }

    public void comm_close()
    {
        if (keep_s != null)
        {
            try
            {
                keep_s.close();
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        keep_s = null;

        keep_tcp_open = false;
    }

    @Override
    public synchronized String send( String str, long len, OutputStream outp, int to)
    {
        String a =   tcp_send( server, port, str, len, outp, null, to, CALL_PREFIX );

        if (get_last_err_code() == 0)
            return a;

        return null;
    }

    public String send( String str, long len, InputStream is, int to)
    {
        String a =   tcp_send( server, port, str, len, is, to, CALL_PREFIX );

        return a;
    }

    @Override
    public String send( String str)
    {
        String a =  tcp_send( server, port, str, 0, null, null, -1, CALL_PREFIX );

        return a;
    }
    @Override
    public String send( String str, int to)
    {
        String a =  tcp_send( server, port, str, 0, null, null, to, CALL_PREFIX );

        return a;
    }

    @Override
    public String send_rmx( String str, int to)
    {
        String a =  tcp_send( server, port, str, 0, null, null, to, RMX_PREFIX );

        return a;
    }
    private String send_rmx( String str, long len, OutputStream os, int to )
    {
        String a =   tcp_send( server, port, str, len, os, null, to, RMX_PREFIX );

        if (get_last_err_code() == 0)
            return a;

        return null;
    }
    @Override
    public String send_rmx( String str, long len, InputStream is, int to )
    {
        String a =   tcp_send( server, port, str, len, is, to, RMX_PREFIX );

        return a;
    }


    private String send_rmx( String str )
    {
        String a =   tcp_send( server, port, str, 0, null, null, -1, RMX_PREFIX );

        if (get_last_err_code() == 0)
            return a;

        return null;
    }


    @Override
    public synchronized boolean send_tcp_byteblock( String str, byte[] data)
    {
        tcp_send( server, port, str, 0, null, data, -1, CALL_PREFIX);
        return (get_last_err_code() == 0);
    }



    @Override
    public synchronized boolean send_fast_retry_cmd(String str)
    {
        String a = tcp_send( server, port, str, 0, null, null, 3, CALL_PREFIX );
        if (check_answer(a))
            return true;

        return false;
    }

    
    public synchronized String tcp_send( Socket s, String str, long len, OutputStream outp, byte[] add_data, String prefix) throws IOException, Exception
    {
        String a = raw_tcp_send( s, str, len, null, outp, add_data, prefix );
        if (check_answer(a))
            return answer;

        return null;

    }
    
    public synchronized String tcp_send( Socket s, String str, long len, InputStream is, OutputStream outp, String prefix) throws IOException, Exception
    {
        String a = raw_tcp_send( s, str, len, is, outp, null, prefix );
        if (check_answer(a))
            return answer;

        return null;
    }


    private synchronized String raw_tcp_send( Socket s, String str, long stream_len, InputStream inp, OutputStream outp, byte[] add_data, String prefix) throws IOException, Exception
    {
        int buff_len = CS_Constants.STREAM_BUFFER_LEN;
        StringBuffer sb = new StringBuffer();

        sb.append("CMD:");
        sb.append(prefix);  // call_ or RMX_

        // DO WE HAVE OPT. DATA?
        int opt_index = str.indexOf(" " );
        if (opt_index == -1)
            sb.append( str );  // NO ONLY PUT CMD
        else
        {
            sb.append( str.substring( 0, opt_index ) );  // CUT OFF CMD
        }
        if (stream_len > 0)
        {
            sb.append( " SLEN:");

            sb.append( stream_len );
        }

        sb.append( " PLEN:");
        int opt_len = 0;

        byte[] opt_data = null;
        if (opt_index != -1 )
        {
            opt_data = str.substring( opt_index + 1).getBytes();
            opt_len = opt_data.length;
        }
        int add_data_len = 0;
        if (add_data != null)
            add_data_len += add_data.length;

        sb.append( (opt_len  + add_data_len) );

        // PAD FIRST BLOCK TO 32 BYTE
        while (sb.length() < TCP_LEN)
        {
            sb.append( " " );
        }

        byte[] data = sb.toString().getBytes();
        long start = System.currentTimeMillis();
        ping_connected = false;

        OutputStream sock_os = s.getOutputStream();
        if (stream_len > 0)
        {
            BufferedOutputStream bos = new BufferedOutputStream( sock_os, buff_len*2 );
            sock_os = bos;
        }

        sock_os.write( data, 0, TCP_LEN );

        // AND PUT OPT DATA IN NEXT BLOCK
        if (opt_len > 0)
        {
            sock_os.write( opt_data );
        }
        if (add_data_len > 0)
        {
            sock_os.write( add_data );
        }
        if (inp != null && stream_len > 0)
        {
            BufferedInputStream bis = new BufferedInputStream( inp, buff_len*2);
            byte[] buff = new byte[buff_len];
            long len = stream_len;
            while( len > 0)
            {
                int rlen = buff.length;
                if (len < rlen)
                    rlen = (int)len;

                int rrlen = bis.read(buff, 0, rlen);
                sock_os.write(buff, 0, rrlen);

                len -= rrlen;
            }
        }

        sock_os.flush();


        // READ ANSER
        byte[] in_buff = new byte[TCP_LEN];

        int rlen = s.getInputStream().read( in_buff );

        ping = (int)(System.currentTimeMillis() - start);

        // AT LEAST WE HAVE AN ANSWER, MACHINE IS THERE WITH VPN ACTIVE
        ping_connected = true;
        if (rlen <= 0)
            throw new Exception( "Application not responding" );

        //System.out.println("Ping is " + ping  + " ms <" + str + ">");

        // THIS IS THE FORMAT OF IT
        //answer.append( "OK:LEN:");
        String local_answer = new String( in_buff, "UTF-8" );


        long alen = 0; 
        String ret = "OK";
        
        int len_idx = local_answer.indexOf("LEN:");
        if (len_idx <= 0)
        {
            throw new Exception( "Data error" );
        }
        // GET OK / NOK
        ret = local_answer.substring(0, len_idx );
        alen = Long.parseLong( local_answer.substring( len_idx + 4).trim() );

        // MORE DATA?
        if (alen > 0)
        {
            if (outp != null)
            {
                write_output( s.getInputStream(), alen, outp );
            }
            else
            {
                byte[] res_data = new byte[(int)alen];
                rlen = s.getInputStream().read( res_data );
                while (rlen != alen)
                {
                    int rrlen = s.getInputStream().read( res_data, rlen, (int)alen - rlen );
                    rlen +=  rrlen;
                }
                ret += new String( res_data, "UTF-8" );
            }
        }
        return ret;

    }
    void write_output( InputStream ins, long alen, OutputStream outs ) throws IOException
    {
         // PUSH DATA OVER BUFFER
        int buff_len = CS_Constants.STREAM_BUFFER_LEN;
        byte[] buff = new byte[buff_len];

        while (alen > 0)
        {
            long blen = alen;
            if (blen > buff_len)
                blen = buff_len;

            int rlen = ins.read( buff, 0, (int)blen );
            outs.write( buff, 0, rlen );
            alen -= rlen;
        }
        outs.flush();
    }


    @Override
    public ConnectionID open()
    {
        return open("");
    }

    @Override
    public ConnectionID open( String db )
    {

        init_stat("");

        try
        {
            String ret = send_rmx( "open " + db, SHORT_CMD_TO );

            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));

            if (retcode == 0)
            {
                ConnectionID cid = new ConnectionID(ret.substring(idx + 2));
                return cid;
            }
            last_err_code = retcode;
        }
        catch (Exception exc)
        {
            last_ex = exc;
        }
        return null;
    }

    @Override
    public StatementID createStatement( ConnectionID c )
    {
        init_stat("");

        try
        {
            String ret = send_rmx( "createStatement " + c.getId(), SHORT_CMD_TO);
            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));

            calc_stat(ret);
            if (retcode == 0)
            {
                StatementID sid = new StatementID( c, ret.substring(idx + 2));
                return sid;
            }
            last_err_code = retcode;
        }
        catch (NumberFormatException numberFormatException)
        {
            last_ex = numberFormatException;
        }
        return null;
    }

    @Override
    public String close( ConnectionID c )
    {
        String st =  send_rmx( "close " + c.getId(), SHORT_CMD_TO);
        return st;
    }

    @Override
    public String close( StatementID c )
    {
        String st =  send_rmx( "close " + c.getId(), SHORT_CMD_TO);
        return st;
    }

    @Override
    public String close( ResultSetID c )
    {
        String st =  send_rmx( "close " + c.getId(), SHORT_CMD_TO);
        return st;
    }


    @Override
    public SQLArrayResult get_sql_array_result( ResultSetID r )
    {
        if (r == null)
            return null;
        
        init_stat("");
        try
        {
            String ret =  send_rmx( "getSQLArrayResult " + r.getId(), SHORT_CMD_TO);

            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));

            if (retcode == 0)
            {
                String xml = ret.substring(idx + 2);

                XStream xstream = new XStream();
                SQLArrayResult retarr = (SQLArrayResult) xstream.fromXML(xml);
                retarr.decode();
                return retarr;
            }
            last_err_code = retcode;

        }
        catch (Exception ex)
        {
            last_ex = ex;
            ex.printStackTrace();
            return null;
        }

        return null;
    }

    @Override
    public ResultSetID executeQuery( StatementID sta, String qry/*, SQLResult result*/ )
    {
        init_stat(qry);

        String ret = null;
        try
        {
            ret =  send_rmx( "executeQuery " + sta.getId() + "|" + qry, SHORT_CMD_TO);

            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));
            if (retcode == 0)
            {
                ResultSetID cid = new ResultSetID(ret.substring(idx + 2));
                return cid;
            }
            last_err_code = retcode;

        }
        catch (Exception ex)
        {
            last_ex = ex;
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    public int executeUpdate( StatementID sta, String qry/*, SQLResult result*/ )
    {
        init_stat(qry);

        String ret = null;
        try
        {

            ret =  send_rmx( "executeUpdate " + sta.getId() + "|" + qry, SHORT_CMD_TO);

            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));
            if (retcode == 0)
            {
                int rows = Integer.parseInt(ret.substring(idx + 2));
                return rows;
            }
            last_err_code = retcode;
        }
        catch (Exception ex)
        {
            last_ex = ex;
            ex.printStackTrace();
        }

        return 0;
    }

    @Override
    public boolean execute( StatementID sta, String qry/*, SQLResult result*/ )
    {
        init_stat(qry);

        String ret = null;
        try
        {

            ret =  send_rmx( "execute " + sta.getId() + "|" + qry, SHORT_CMD_TO);
           

            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));
            if (retcode == 0)
            {
                int ok = Integer.parseInt(ret.substring(idx + 2));
                return ok == 1 ? true : false;
            }
            last_err_code = retcode;
        }
        catch (Exception ex)
        {
            last_ex = ex;
            ex.printStackTrace();
        }

        return false;
    }


    @Override
    String  get_name_from_hibernate_class( String rec_name )
    {
        
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < rec_name.length(); i++)
        {
            char ch = new Character( rec_name.charAt(i) );

            if (i == 0)
            {
                ch = Character.toLowerCase(ch);
            }
            else
            {
                if (Character.isUpperCase(ch))
                {
                    sb.append('_');
                    ch = Character.toLowerCase(ch);
                }
            }
            sb.append(ch);
        }


        return sb.toString();
    }

    @Override
    String  get_name_from_hibernate_class( Object o )
    {
        return get_name_from_hibernate_class(  o.getClass().getSimpleName() );
    }

    @Override
    public boolean Insert( StatementID sta, Object o )
    {
        boolean ret = false;

        try
        {
            String rec_name = get_name_from_hibernate_class( o );
            Method getId = o.getClass().getDeclaredMethod("getId");
            Object r = getId.invoke(o);
            int id = ((Integer) r).intValue();
            if (id != 0)
            {
                return false;
            }

            String vals = "";
            String fields = "";
            String where_str = "";

            int field_idx = 0;
            Method[] meths = o.getClass().getDeclaredMethods();
            for (int i = 0; i < meths.length; i++)
            {
                Method method = meths[i];

                String ret_type = method.getReturnType().getName();
                String meth_name = method.getName();

                boolean is_getter = meth_name.startsWith("get");
                boolean is_setter = meth_name.startsWith("set");

                if (!is_getter)
                {
                    continue;
                }

                if (method.getName().compareTo("getId") == 0)
                {
                    continue;
                }
                String field_name = get_name_from_hibernate_class( method.getName().substring(3) );

                if (ret_type.compareTo("java.lang.String") == 0)
                {
                    if (field_idx > 0)
                    {
                        vals += ",";
                        fields += ",";
                        where_str += " and ";
                    }
                    field_idx++;

                    Object str_obj = method.invoke(o);
                    if (str_obj == null)
                    {
                        throw new Exception( "Object " + rec_name + " has not value for method " + meth_name );
                    }
                    String val = method.invoke(o).toString();

                    // HANDLE BACKSLASHES, AND QUOTES
                    val = SQLArrayResult.encode(val);

                    vals += "'" + val + "'";
                    fields += field_name;
                    where_str += field_name + "='" + val + "'";

                }
                else if (ret_type.compareTo("java.lang.Integer") == 0 || ret_type.compareTo("java.lang.Long") == 0)
                {
                    if (field_idx > 0)
                    {
                        vals += ",";
                        fields += ",";
                        where_str += " and ";
                    }
                    field_idx++;

                    Object str_obj = method.invoke(o);
                    if (str_obj == null)
                    {
                        throw new Exception( "Object " + rec_name + " has not value for method " + meth_name );
                    }
                    String val = method.invoke(o).toString();                    

                    vals +=  val;
                    fields += field_name;
                    where_str += field_name + "=" + val;
                }
                else if (ret_type.compareTo("int") == 0)
                {
                    if (field_idx > 0)
                    {
                        vals += ",";
                        fields += ",";
                        where_str += " and ";
                    }
                    field_idx++;

                    int val = ((Integer) method.invoke(o)).intValue();
                    vals += " " + val + " ";
                    fields += field_name;
                    where_str += field_name + "=" + val;
                }
                else if (ret_type.compareTo("long") == 0)
                {
                    if (field_idx > 0)
                    {
                        vals += ",";
                        fields += ",";
                        where_str += " and ";
                    }
                    field_idx++;

                    long val = ((Long) method.invoke(o)).longValue();
                    vals += " " + val + " ";
                    fields += field_name;
                    where_str += field_name + "=" + val;
                }
                else if (ret_type.contains(".DiskArchive"))
                {
                    DiskArchive da = (DiskArchive) method.invoke(o);

                    if (field_idx > 0)
                    {
                        vals += ",";
                        fields += ",";
                        where_str += ",";
                    }
                    field_idx++;

                    vals += da.getId();
                    fields += "da_id";
                    where_str += "da_id=" +da.getId();

                }
                else if (ret_type.contains(".DiskSpace"))
                {
                    DiskSpace ds = (DiskSpace) method.invoke(o);

                    if (field_idx > 0)
                    {
                        vals += ",";
                        fields += ",";
                        where_str += " and ";
                    }
                    field_idx++;

                    vals += ds.getId();
                    fields += "ds_id";
                    where_str += "ds_id=" +ds.getId();
                }
                else if (ret_type.contains(".Mandant"))
                {
                    Mandant m = (Mandant) method.invoke(o);

                    if (field_idx > 0)
                    {
                        vals += ",";
                        fields += ",";
                        where_str += " and ";
                    }
                    field_idx++;

                    vals += m.getId();
                    fields += "mid";
                    where_str += "mid=" +m.getId();
                }
                else if (!ret_type.contains(".hibernate.") && !ret_type.contains("java.util.Set") )
                {
                    Object unknown = method.invoke(o);
                    if (unknown != null)
                        throw new Exception( "Invalid return type for Method " + meth_name );
                }
            }
            String ins_stmt = "insert into " + rec_name + " (" + fields + ") values (" + vals + ")";

            int rows = executeUpdate(sta, ins_stmt);

            ret = (rows == 1) ? true : false;
            if (ret)
            {
                // SET NEW ID BACK TO OBJECT
                String sel_stmt = "select max(id) from " + rec_name + " where " + where_str;
                
                int new_id = GetFirstSqlFieldInt( sta.getConnnId() , sel_stmt, 0 );

                if (new_id <= 0)
                    throw new Exception("Cannot retrieve new index for table " + rec_name );

                Method setId = o.getClass().getDeclaredMethod("setId", int.class);
                setId.invoke(o, new Integer(new_id ));

                return true;
            }

            
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            last_ex = ex;
        }
        return false;
    }

    @Override
    public boolean Update( StatementID sta, Object o )
    {
        boolean ret = false;

        try
        {
            String rec_name = get_name_from_hibernate_class( o );
            Method getId = o.getClass().getDeclaredMethod("getId");
            Object r = getId.invoke(o);
            int id = ((Integer) r).intValue();
            if (id == 0)
            {
                return false;
            }
            String upd_cmd = "";
            int field_idx = 0;
            Method[] meths = o.getClass().getDeclaredMethods();
            for (int i = 0; i < meths.length; i++)
            {
                Method method = meths[i];

                String ret_type = method.getReturnType().getName();
                String meth_name = method.getName();

                boolean is_getter = meth_name.startsWith("get");
                boolean is_setter = meth_name.startsWith("set");

                if (!is_getter)
                {
                    continue;
                }

                if (method.getName().compareTo("getId") == 0)
                {
                    continue;
                }
                String field_name = get_name_from_hibernate_class( method.getName().substring(3) );

                if (ret_type.compareTo("java.lang.String") == 0)
                {
                    if (field_idx > 0)
                    {
                        upd_cmd += ",";
                    }
                    field_idx++;
                    String val = method.invoke(o).toString();
                    val = SQLArrayResult.encode(val);

                    upd_cmd += field_name + "='" + val + "'";
                }
                else if (ret_type.compareTo("java.lang.Integer") == 0 || ret_type.compareTo("java.lang.Long") == 0)
                {
                    if (field_idx > 0)
                    {
                        upd_cmd += ",";
                    }
                    field_idx++;
                    String val = method.invoke(o).toString();
                    val = SQLArrayResult.encode(val);

                    upd_cmd += field_name + "=" + val;
                }
                else if (ret_type.compareTo("int") == 0)
                {
                    if (field_idx > 0)
                    {
                        upd_cmd += ",";
                    }
                    field_idx++;

                    upd_cmd += field_name + "=" + ((Integer) method.invoke(o)).intValue() + "";
                }
                else if (ret_type.compareTo("long") == 0)
                {
                    if (field_idx > 0)
                    {
                        upd_cmd += ",";
                    }
                    field_idx++;

                    upd_cmd += field_name + "=" + ((Long) method.invoke(o)).longValue() + "";
                }
                else if (ret_type.contains(".DiskArchive"))
                {
                    DiskArchive da = (DiskArchive) method.invoke(o);

                    if (da != null)
                    {
                        if (field_idx > 0)
                        {
                            upd_cmd += ",";
                        }
                        field_idx++;

                        upd_cmd += "da_id=" + da.getId();
                    }
                }
                else if (ret_type.contains(".DiskSpace"))
                {
                    DiskSpace ds = (DiskSpace) method.invoke(o);
                    if (ds != null)
                    {
                        if (field_idx > 0)
                        {
                            upd_cmd += ",";
                        }
                        field_idx++;

                        upd_cmd += "ds_id=" + ds.getId();
                    }
                }
                else if (ret_type.contains(".Mandant"))
                {
                    Mandant m = (Mandant) method.invoke(o);
                    if (m != null)
                    {
                        if (field_idx > 0)
                        {
                            upd_cmd += ",";
                        }
                        field_idx++;

                        upd_cmd += "mid=" + m.getId();
                    }
                }
                else if (!ret_type.contains(".hibernate.") && !ret_type.contains("java.util.Set") )
                {
                    Object unknown = method.invoke(o);
                    if (unknown != null)
                        throw new Exception( "Invalid return type for Method " + meth_name );
                }
            }
            String upd_stmt = "update " + rec_name + " set " + upd_cmd + " where id=" + id;

            int rows = executeUpdate(sta, upd_stmt);

            return (rows == 1) ? true : false;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            last_ex = ex;
        }
        return false;
    }

    @Override
    public boolean Delete( StatementID sta, Object o )
    {
        boolean ret = false;

        try
        {
            String rec_name = get_name_from_hibernate_class( o );
            Method getId = o.getClass().getDeclaredMethod("getId");
            Object r = getId.invoke(o);
            int id = ((Integer) r).intValue();
            if (id == 0)
            {
                return false;
            }

            String del_stmt = "delete from " + rec_name + " where id=" + id;

            int rows = executeUpdate(sta, del_stmt);

            return (rows == 1) ? true : false;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            last_ex = ex;
        }
        return false;
    }

    @Override
    public int GetFirstSqlFieldInt( ConnectionID connection_id, String qry, int field )
    {
        String ret = GetFirstSqlField( connection_id, qry, field );

        return Integer.parseInt(ret);
    }

    @Override
    public String GetFirstSqlField( ConnectionID connection_id, String qry, int field )
    {

        init_stat(qry);

        try
        {
            String ret =  send_rmx( "getSQLFirstRowField " + connection_id.getId() + "|" + qry + "|" + field, SHORT_CMD_TO);

            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));
            if (retcode == 0)
            {
                return ret.substring(idx + 2);
            }
            last_err_code = retcode;
            last_err_txt = ret.substring(idx + 2);
        }
        catch (Exception ex)
        {
            last_ex = ex;
            ex.printStackTrace();
        }

         return null;
    }

    @Override
    public OutStreamID open_out_stream( String file )
    {
        init_stat(file);

        try
        {
            String ret =  send_rmx( "OpenOutStream " + file , SHORT_CMD_TO);

            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));

            if (retcode == 0)
            {
                OutStreamID cid = new OutStreamID(ret.substring(idx + 2));
                return cid;
            }
            last_err_code = retcode;
        }
        catch (Exception exc)
        {
            last_ex = exc;
        }
        return null;
    }

    @Override
    public boolean close_out_stream( OutStreamID id)
    {
        init_stat("");

        try
        {
            String ret =  send_rmx( "CloseOutStream " + id.getId() , SHORT_CMD_TO);
           

            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));

            if (retcode == 0)
            {
                return true;
            }
            last_err_code = retcode;
        }
        catch (Exception exc)
        {
            last_ex = exc;
        }
        return false;
    }
    @Override
    public boolean close_delete_out_stream( OutStreamID id)
    {
        init_stat("");

        try
        {
            String ret =  send_rmx( "CloseDeleteOutStream " + id.getId() , SHORT_CMD_TO);


            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));

            if (retcode == 0)
            {
                return true;
            }
            last_err_code = retcode;
        }
        catch (Exception exc)
        {
            last_ex = exc;
        }
        return false;
    }
    @Override
    public boolean write_out_stream( OutStreamID id, long len, InputStream is)
    {
        init_stat("");

        try
        {
            String ret =  send_rmx( "WriteOutStream " + id.getId() , len, is, SHORT_CMD_TO);

            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));

            if (retcode == 0)
            {
                return true;
            }
            last_err_code = retcode;
        }
        catch (Exception exc)
        {
            last_ex = exc;
        }
        return false;
    }
    @Override
    public boolean write_out_stream( OutStreamID id, byte[] data)
    {
      init_stat("");

        try
        {
            String sdata = new String( Base64.encodeBase64(data));
            sdata =encode_pipe(sdata);

            String ret =  send_rmx( "WriteOut " + id.getId() + "|" + sdata);

            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));

            if (retcode == 0)
            {
                return true;
            }
            last_err_code = retcode;
            last_err_code = -1;
        }
        catch (Exception exc)
        {
            last_ex = exc;
        }
        return false;
    }

    @Override
    public InStreamID open_in_stream( String file )
    {
        init_stat(file);

        try
        {
            String ret =  send_rmx( "OpenInStream " + file , SHORT_CMD_TO);

            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));

            if (retcode == 0)
            {
                String id = ret.substring(idx + 2);

                long len = 0;
                int lidx = ret.indexOf("LEN:");
                if (lidx != -1)
                {
                    len = Long.parseLong(ret.substring(lidx + 4) );
                    id = ret.substring(idx + 2, lidx - 1);
                }

                InStreamID cid = new InStreamID(id, len);
                return cid;
            }
            last_err_code = retcode;
        }
        catch (Exception exc)
        {
            last_ex = exc;
        }
        return null;
    }
    @Override
    public boolean close_in_stream( InStreamID id)
    {
        init_stat("");

        try
        {
            String ret =  send_rmx( "CloseInStream " + id.getId() , SHORT_CMD_TO);
            

            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));

            if (retcode == 0)
            {
                return true;
            }
            last_err_code = retcode;
        }
        catch (Exception exc)
        {
            last_ex = exc;
        }
        return false;
    }

    @Override
    public boolean  read_in_stream( InStreamID id, OutputStream os)
    {
        init_stat("");

        try
        {
            String ret =  send_rmx( "ReadInStream " + id.getId() , id.getLen(), os, SHORT_CMD_TO);


            calc_stat("");

            return true;
        }
        catch (Exception exc)
        {
            last_ex = exc;
        }
        return false;
    }
    @Override
    public int  read_in_stream( InStreamID id, byte[] data)
    {
        int iret = -1;
        init_stat("");

        try
        {
            String ret =  send_rmx( "ReadIn " + id.getId() + "|" +  data.length, SHORT_CMD_TO);


            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));

            if (retcode == 0)
            {
                byte[] rdata = Base64.decodeBase64(ret.substring(3).getBytes());

                if (rdata.length < data.length)
                {
                    System.arraycopy(rdata, 0, data, 0, rdata.length);
                    iret = rdata.length;
                }
                else
                {
                    System.arraycopy(rdata, 0, data, 0, data.length);
                     iret = data.length;
                }
                return iret;
            }

            // HANDLE EOF
            if (retcode == 1)
                return 0;

            last_err_code = retcode;

            return -1;
        }
        catch (Exception exc)
        {
            last_ex = exc;
        }
        return -1;
    }







}
