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

import home.shared.SQL.SQLArrayResult;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 *
 * @author mw
 */
public class ServerTCPCall
{

    public static final String MISS_ARGS = "missing args";
    public static final String WRONG_ARGS = "wrong args";
    public static final String UNKNOWN_CMD = "UNKNOWN_COMMAND";

/*    MWWebServiceService service;
    MWWebService port;*/
    String name;
    long last_duration_ms;
    long last_start;
    long last_end;
    String last_err_txt;
    String last_return;
    String last_cmd;
    int last_err_code;
    Exception last_ex;


    private static final int TCP_LEN = 32;
    
    public static final int SHORT_CMD_TO = 3000;

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

    public boolean check_answer( String answer )
    {
        boolean ok = false;
        if (answer == null || answer.length() == 0)
        {
            answer = "Kommunikation fehlgeschlagen!";
            return false;
        }


        if (answer.compareTo("--failed--") == 0)
        {
            answer =  "Kommunikation fehlgeschlagen!";
            return false;
        }

        if (answer.compareTo("UNKNOWN_COMMAND") == 0)
        {
            answer =  "Oha, dieser Befehl wird von der Box nicht unterstützt!";
            return false;
        }


        if (answer.length() >= 2 && answer.substring(0, 2).compareTo("OK") == 0)
        {
            ok = true;
            if (answer.length() > 3)
                answer = answer.substring(3);
            else
                answer = "";

            ok = true;
        }
        else if (answer.length() >= 3 && answer.substring(0, 3).compareTo("NOK") == 0)
        {
            ok = false;
            if (answer.length() > 4)
                answer = answer.substring(4);
            else
                answer = "";

        }
        return ok;
    }

    public synchronized String send( String str, OutputStream outp, int to)
    {
        return tcp_send( server, port, str, outp, null, to );
    }

    public synchronized String send( String str, long len, InputStream is, int to)
    {
        return tcp_send( server, port, str, len, is, to );
    }

    public synchronized String send( String str)
    {
        return tcp_send( server, port, str, null, null, -1 );
    }
    public synchronized String send( String str, int to)
    {
        return tcp_send( server, port, str, null, null, to );
    }

    public boolean send_tcp_byteblock( String str, byte[] data)
    {
        answer =  tcp_send( server, port, str, null, data, -1);
        return check_answer(answer);

    }

/*
    public boolean send_cmd(String string)
    {
        boolean ok = false;

        answer = send( string, null );

        return check_answer(answer);
    }

    public boolean send_cmd(String string, OutputStream outp)
    {
        boolean ok = false;

        answer = send( string, outp );

        return check_answer(answer);
    }
*/
    public boolean send_fast_retry_cmd(String str)
    {
        answer = tcp_send( server, port, str, null, null, 3 );
        if (check_answer(answer))
            return true;
        
        return false;
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

            ping_answer = tcp_send( s, "GETSTATUS MD:SHORT", null, null );

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
                    keep_s.setSoTimeout(60* 1000); // DEFAULT TIMEOUT 60 SECONDS

                SocketAddress saddr = new InetSocketAddress( ip, port );

                if (timeout > 0)
                    keep_s.connect( saddr, timeout*1000 );
                else
                    keep_s.connect( saddr);
            }
            else
            {
                if (timeout > 0)
                    keep_s.setSoTimeout(timeout* 1000);
                else
                    keep_s.setSoTimeout(60* 1000); // DEFAULT TIMEOUT 60 SECONDS
            }

    }

    public String tcp_send( String ip, int port, String str, OutputStream outp, byte[] add_data, int timeout)
    {
        try
        {

            reopen( ip, port, timeout );
            
            Socket s = keep_s;

            String ret = tcp_send( s, str, outp, add_data);


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
    public String tcp_send( String ip, int port, String str, long len, InputStream inp, int timeout)
    {
        try
        {

            reopen( ip, port, timeout );

            Socket s = keep_s;

            String ret = tcp_send( s, str, len, inp, null);


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
    public String tcp_send( Socket s, String str, OutputStream outp, byte[] add_data) throws IOException, Exception
    {
        return tcp_send( s, str, 0, null, outp, add_data );
    }
    public String tcp_send( Socket s, String str, long len, InputStream is, OutputStream outp) throws IOException, Exception
    {
        return tcp_send( s, str, len, is, outp, null );
    }


    public synchronized String tcp_send( Socket s, String str, long inp_len, InputStream inp, OutputStream outp, byte[] add_data) throws IOException, Exception
    {
        StringBuffer sb = new StringBuffer();

        sb.append("CMD:RMX_");

        // DO WE HAVE OPT. DATA?
        int opt_index = str.indexOf(" " );
        if (opt_index == -1)
            sb.append( str );  // NO ONLY PUT CMD
        else
        {
            sb.append( str.substring( 0, opt_index ) );  // CUT OFF CMD
        }
        sb.append( " LEN:");
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

        sb.append( (opt_len  + add_data_len + inp_len) );

        // PAD FIRST BLOCK TO 32 BYTE
        while (sb.length() < TCP_LEN)
        {
            sb.append( " " );
        }

        byte[] data = sb.toString().getBytes();
        long start = System.currentTimeMillis();
        ping_connected = false;


        s.getOutputStream().write( data, 0, TCP_LEN );

        // AND PUT OPT DATA IN NEXT BLOCK
        if (opt_len > 0)
        {
            s.getOutputStream().write( opt_data );
        }
        if (add_data_len > 0)
        {
            s.getOutputStream().write( add_data );
        }
        if (inp_len > 0)
        {
            byte[] buff = new byte[64*1024];
            long len = inp_len;
            while( len > 0)
            {
                int rlen = buff.length;
                if (len < rlen)
                    rlen = (int)len;

                int rrlen = inp.read(buff, 0, rlen);
                s.getOutputStream().write(buff, 0, rrlen);

                len -= rrlen;
            }
        }

        s.getOutputStream().flush();


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


        int len_idx = local_answer.indexOf("LEN:");
        if (len_idx <= 0)
            throw new Exception( "Data error" );

        // GET OK / NOK
        String ret = local_answer.substring(0, len_idx );
        int alen = Integer.parseInt( local_answer.substring( len_idx + 4).trim() );

        // MORE DATA?
        if (alen > 0)
        {
            if (outp != null)
            {
                write_output( s.getInputStream(), alen, outp );
            }
            else
            {
                byte[] res_data = new byte[alen];
                rlen = s.getInputStream().read( res_data );
                while (rlen != alen)
                {
                    int rrlen = s.getInputStream().read( res_data, rlen, alen - rlen );
                    rlen +=  rrlen;
                }
                ret += new String( res_data, "UTF-8" );
            }
        }
        return ret;

    }
    void write_output( InputStream ins, int alen, OutputStream outs ) throws IOException
    {
         // PUSH DATA OVER BUFFER
        int buff_len = 8192;
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


    public ConnectionID open()
    {
        return open("");
    }

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

    void calc_stat( String ret )
    {
        last_end = System.currentTimeMillis();
        last_duration_ms = last_end - last_start;
        last_return = ret;
    }

    public String get_last_err_txt()
    {
        return last_err_txt;
    }

    public int get_last_err_code()
    {
        return last_err_code;
    }

    public Exception get_last_ex()
    {
        return last_ex;
    }

    long get_last_duration()
    {
        return last_duration_ms;
    }

    public String get_last_err()
    {
        String ex_str = "";
        if (last_ex != null)
        {
            ex_str = last_ex.getMessage();
        }

        return "ServerCall <" + last_cmd + "> Code:" + last_err_code + " Txt:" + last_err_txt + " EX:" + ex_str;

    }

    public ConnectionID open( String db )
    {

        init_stat("");

        try
        {
            String ret = send( "open " + db, SHORT_CMD_TO );

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

    public StatementID createStatement( ConnectionID c )
    {
        init_stat("");

        try
        {
            String ret = send( "createStatement " + c.getId(), SHORT_CMD_TO);
            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));

            calc_stat(ret);
            if (retcode == 0)
            {
                StatementID cid = new StatementID(ret.substring(idx + 2));
                return cid;
            }
            last_err_code = retcode;
        }
        catch (NumberFormatException numberFormatException)
        {
            last_ex = numberFormatException;
        }
        return null;
    }

    public String close( ConnectionID c )
    {
        String st =  send( "close " + c.getId(), SHORT_CMD_TO);
        return st;
    }

    public String close( StatementID c )
    {
        String st =  send( "close " + c.getId(), SHORT_CMD_TO);
        return st;
    }

    public String close( ResultSetID c )
    {
        String st =  send( "close " + c.getId(), SHORT_CMD_TO);
        return st;
    }

    static public String encode( String cmd )
    {
        cmd = cmd.replace("\\", "\\\\");
        cmd = cmd.replace("'", "\\'");
        cmd = cmd.replace("\"", "\\\"");
        return cmd;
    }

    public SQLArrayResult get_sql_array_result( ResultSetID r )
    {
        if (r == null)
            return null;
        
        init_stat("");
        try
        {
            String ret =  send( "getSQLArrayResult " + r.getId(), SHORT_CMD_TO);

            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));

            if (retcode == 0)
            {
                String xml = ret.substring(idx + 2);

                XStream xstream = new XStream();
                SQLArrayResult retarr = (SQLArrayResult) xstream.fromXML(xml);
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

    public ResultSetID executeQuery( StatementID sta, String qry/*, SQLResult result*/ )
    {
        init_stat(qry);

        String ret = null;
        try
        {
            ret =  send( "executeQuery " + sta.getId() + "|" + qry, SHORT_CMD_TO);

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

    public int executeUpdate( StatementID sta, String qry/*, SQLResult result*/ )
    {
        init_stat(qry);

        String ret = null;
        try
        {

            ret =  send( "executeUpdate " + sta.getId() + "|" + qry, SHORT_CMD_TO);

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

    public boolean execute( StatementID sta, String qry/*, SQLResult result*/ )
    {
        init_stat(qry);

        String ret = null;
        try
        {

            ret =  send( "execute " + sta.getId() + "|" + qry, SHORT_CMD_TO);
           

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
    String  get_name_from_hibernate_class( Object o )
    {
        return get_name_from_hibernate_class(  o.getClass().getSimpleName() );
    }

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
                    }
                    field_idx++;

                    Object str_obj = method.invoke(o);
                    if (str_obj == null)
                    {
                        throw new Exception( "Object " + rec_name + " has not value for method " + meth_name );
                    }
                    String val = method.invoke(o).toString();
                    val = encode(val);

                    vals += "'" + val + "'";
                    fields += field_name;
                }
                else if (ret_type.compareTo("java.lang.Integer") == 0 || ret_type.compareTo("java.lang.Long") == 0)
                {
                    if (field_idx > 0)
                    {
                        vals += ",";
                        fields += ",";
                    }
                    field_idx++;

                    Object str_obj = method.invoke(o);
                    if (str_obj == null)
                    {
                        throw new Exception( "Object " + rec_name + " has not value for method " + meth_name );
                    }
                    String val = method.invoke(o).toString();
                    val = encode(val);

                    vals +=  val;
                    fields += field_name;
                }
                else if (ret_type.compareTo("int") == 0)
                {
                    if (field_idx > 0)
                    {
                        vals += ",";
                        fields += ",";
                    }
                    field_idx++;

                    vals += " " + ((Integer) method.invoke(o)).intValue() + " ";
                    fields += field_name;
                }
                else if (ret_type.compareTo("long") == 0)
                {
                    if (field_idx > 0)
                    {
                        vals += ",";
                        fields += ",";
                    }
                    field_idx++;

                    vals += " " + ((Long) method.invoke(o)).longValue() + " ";
                    fields += field_name;
                }
                else if (ret_type.contains(".DiskArchive"))
                {
                    DiskArchive da = (DiskArchive) method.invoke(o);

                    if (field_idx > 0)
                    {
                        vals += ",";
                        fields += ",";
                    }
                    field_idx++;

                    vals += da.getId();
                    fields += "da_id";
                }
                else if (ret_type.contains(".DiskSpace"))
                {
                    DiskSpace ds = (DiskSpace) method.invoke(o);

                    if (field_idx > 0)
                    {
                        vals += ",";
                        fields += ",";
                    }
                    field_idx++;

                    vals += ds.getId();
                    fields += "ds_id";
                }
                else if (ret_type.contains(".Mandant"))
                {
                    Mandant m = (Mandant) method.invoke(o);

                    if (field_idx > 0)
                    {
                        vals += ",";
                        fields += ",";
                    }
                    field_idx++;

                    vals += m.getId();
                    fields += "mid";
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

            return (rows == 1) ? true : false;

            
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            last_ex = ex;
        }
        return false;
    }

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
                    val = encode(val);

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
                    val = encode(val);

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
    public int GetFirstSqlFieldInt( ConnectionID connection_id, String qry, int field )
    {
        String ret = GetFirstSqlField( connection_id, qry, field );

        return Integer.parseInt(ret);
    }

    public String GetFirstSqlField( ConnectionID connection_id, String qry, int field )
    {

        init_stat(qry);

        try
        {
            String ret =  send( "getSQLFirstRowField " + connection_id.getId() + "|" + qry + "|" + field, SHORT_CMD_TO);

            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));
            if (retcode == 0)
            {
                return ret.substring(idx + 2);
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

    public OutStreamID open_out_stream( String file )
    {
        init_stat(file);

        try
        {
            String ret =  send( "openOutStream " + file , SHORT_CMD_TO);

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
    public boolean close_out_stream( OutStreamID id)
    {
        init_stat("");

        try
        {
            String ret =  send( "closeOutStream " + id.getId() , SHORT_CMD_TO);
           

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
    public boolean write_out_stream( OutStreamID id, long len, InputStream is)
    {
        init_stat("");

        try
        {
            String ret =  send( "writeOutStream " + id.getId() , len, is, SHORT_CMD_TO);

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

    public InStreamID open_in_stream( String file )
    {
        init_stat(file);

        try
        {
            String ret =  send( "openInStream " + file , SHORT_CMD_TO);

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
    public boolean close_in_stream( InStreamID id)
    {
        init_stat("");

        try
        {
            String ret =  send( "closeInStream " + id.getId() , SHORT_CMD_TO);
            

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

    public String read_in_stream( InStreamID id, OutputStream os)
    {
        init_stat("");

        try
        {
            String ret =  send( "readInStream " + id.getId() , os, SHORT_CMD_TO);


            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));

            if (retcode == 0)
            {
                return "0: ";
            }
            
            // HANDLE EOF
            if (retcode == 1)
                return "0: eof";

            last_err_code = retcode;

            return ret;
        }
        catch (Exception exc)
        {
            last_ex = exc;
        }
        return "1: aborted";
    }





}