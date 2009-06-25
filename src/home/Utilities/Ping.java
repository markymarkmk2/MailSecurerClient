/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Utilities;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;

/**
 *
 * @author Administrator
 */
public class Ping 
{
    String host;
    int timeout = 3;

    public Ping( String _host )
    {
        host = _host;
    }
    public void set_timeout( int t)
    {
        timeout = t;
    }
    
    public int ping()
    {

        // TRY CONNECT WITH TIMEOUT AND MEASURE THE TIME FOR BUILD UP CONNECTION
        SwingWorker sw = new SwingWorker()
        {

            @Override
            public Object construct()
            {
                Socket sock = null;
                try
                {
                    sock = new Socket();
                    sock.setTcpNoDelay( true );
                    sock.setReuseAddress( true );

                    sock.setSoTimeout(timeout * 1000 + 500);

                    SocketAddress saddr = new InetSocketAddress(host, 80);
                    sock.connect(saddr, timeout * 1000 + 500);
                }
                catch (Exception exc)
                {}
                finally
                {
                    if ( sock != null)
                    {
                        if (sock.isConnected())
                        {
                            try
                            {
                                sock.close();

                            }
                            catch (IOException iOException)
                            {
                            }
                        }
                    }
                } 
                return null;
            }
        };
        long start_t = System.currentTimeMillis();
        sw.start();
        sw.join(timeout*1000);
        long end_t = System.currentTimeMillis();
        
        if (sw.finished())
        {
            return (int)((end_t - start_t) / 1000);
        }
        return -1;                   
    }
}
