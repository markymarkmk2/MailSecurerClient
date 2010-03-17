/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Properties;
import jrdesktop.JRCommons;
import jrdesktop.JRConfig;
import jrdesktop.server.Server;
import jrdesktop.utilities.JRConnectEvent;
import jrdesktop.utilities.JRConnectEventListener;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.StatusLine;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;

/**
 *
 * @author mw
 */
public class JRD_Server
{
    public static final String SERVER_JRDESKTOP_PARAMS = "/mailsecurer/jrd_params.txt";
    public static final String LOCAL_JRDESKTOP_PARAMS = "./jrd_params.txt";
    public static String DEFAULTSERVER = "www.mailsecurer.de";
    public static final String HTTPUSER = "mailsecurer";
    public static final String HTTPPWD = "123456";
    public static final int DEFAULTPORT = 80;


    String upd_server = DEFAULTSERVER;
    String http_user = HTTPUSER;
        String http_pwd = HTTPPWD;

    Header[] response_hlist;

    boolean viewer_is_running;
    Server server;

    ArrayList<JRConnectEventListener> evt_listener;

    public JRD_Server()
    {
        evt_listener = new ArrayList<JRConnectEventListener>();
    }
    void add_listener( JRConnectEventListener l )
    {
        if (!evt_listener.contains(l))
            evt_listener.add(l);
    }
    void remove_listener( JRConnectEventListener l )
    {
        if (evt_listener.contains(l))
            evt_listener.remove(l);
    }
    void notify_listeners( JRConnectEvent.EVENT evt )
    {
        for (int i = 0; i < evt_listener.size(); i++)
        {
            JRConnectEventListener connectEventListener = evt_listener.get(i);
            connectEventListener.connect_event( new JRConnectEvent(evt));
        }
    }

    boolean download_file( String host, int port, String http_user, String http_pwd, String server_path, String local_path, boolean obfuscate)
    {


        HostConfiguration host_conf = new HostConfiguration();


        host_conf.setHost( host,  port );


        GetMethod get = new GetMethod(server_path);

        HttpClient http_client = new HttpClient();
        Credentials defaultcreds = new UsernamePasswordCredentials(http_user, http_pwd);
        http_client.getState().setCredentials(new AuthScope(host, port, AuthScope.ANY_REALM), defaultcreds);



        int data_len = 0;
        try
        {

            http_client.executeMethod(host_conf, get );

            int rcode = get.getStatusCode();
            StatusLine sl = get.getStatusLine();

            if (rcode < 200 || rcode > 210)
            {
                // SERVER ANSWERS WITH ERROR
                return false;
            }
            response_hlist = get.getResponseHeaders();

            long len = 0;

            int last_percent = 0;

            try
            {
                String cont_len_str = get_resonse_header("Content-Length");
                len = Long.parseLong(cont_len_str);
            }
            catch ( NumberFormatException numberFormatException )
            {
            }

            InputStream istr = get.getResponseBodyAsStream();

            BufferedInputStream bis = new BufferedInputStream( istr );
            FileOutputStream fw = new FileOutputStream( local_path );

            int bs = 4096;
            byte[] buffer = new byte[bs];


            while (true)
            {
                int rlen = bis.read( buffer );

                data_len += rlen;

                if (rlen == -1)
                    break;
                if (obfuscate)
                {
                    for (int i = 0; i < rlen; i++)
                    {
                        buffer[i] = (byte)~buffer[i];   // KOMPLEMENT: BILLIG UND GUT
                    }
                }
                fw.write( buffer,0, rlen );
            }
            fw.close();
            bis.close();

        }
        catch ( Exception exc )
        {
            return false;
        }
        finally
        {
        }



        if (data_len == 0)
            return false;

        File f = new File( local_path );
        if (f.exists())
            return true;

        return false;
    }

    public String get_resonse_header( String key)
    {
        if (response_hlist == null)
            return null;

        for (int i = 0; i < response_hlist.length; i++)
        {
            if (response_hlist[i].getName().compareTo(key) == 0)
                return response_hlist[i].getValue();
        }
        return null;
    }

    public boolean start_session(String user)
    {
        if (viewer_is_running)
            return true;

        if (!download_file(upd_server, DEFAULTPORT, http_user, http_pwd, SERVER_JRDESKTOP_PARAMS, LOCAL_JRDESKTOP_PARAMS, false ))
        {
            System.out.println("Cannot load jrd params");
            
        }
        Properties p = new Properties();
        try
        {
            p.load(new FileInputStream(LOCAL_JRDESKTOP_PARAMS));
        }
        catch (IOException iOException)
        {
            notify_listeners( JRConnectEvent.EVENT.CONNECT_FAILED );
            return false;
        }
        String port = p.getProperty("Port", "1099");
        String jrd_server = p.getProperty("Server", "www.mailsecurer.de");
        int jrd_port = 1099;
        try
        {
            jrd_port = Integer.parseInt(port);
        }
        catch (NumberFormatException numberFormatException)
        {
            notify_listeners( JRConnectEvent.EVENT.CONNECT_FAILED );
            return false;
        }

        JRConfig config = new JRConfig(JRCommons.viewerSide, JRCommons.DEFAULT_CONFIG, jrd_server,
                        jrd_port, user, "12345", /*ssl*/false, /*reverse*/true);

        server = new Server(config);
        server._Start();

        if (server.isConnected())
        {
             viewer_is_running = true;
             notify_listeners( JRConnectEvent.EVENT.CONNECTED );
        }
        else
        {
            viewer_is_running = false;
            notify_listeners( JRConnectEvent.EVENT.CONNECT_FAILED );
        }

        return server.isConnected();
    }
    
    public boolean stop_session()
    {
        if (!viewer_is_running)
            return false;

        server._Stop();
        Server.Stop();
        server = null;
        viewer_is_running = false;
        notify_listeners( JRConnectEvent.EVENT.DISCONNECTED );

        return true;
    }

    public boolean is_running()
    {
        return viewer_is_running;
    }

}
