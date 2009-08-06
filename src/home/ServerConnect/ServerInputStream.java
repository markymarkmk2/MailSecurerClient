/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author mw
 */
public class ServerInputStream extends InputStream
{
    StreamConnect conn;
    InStreamID id;

    public ServerInputStream( StreamConnect _conn, String file ) throws IOException
    {
        conn = _conn;
        ServerWSDLCall sc = conn.get_sqc();
        id = sc.open_in_stream(file);

        if (id == null)
        {
            throw new IOException( "Cannot open Serverstream " + file + ": " + sc.get_last_err_txt() + " Err: " + sc.get_last_err_code()) ;
        }
    }


    @Override
    public int read( ) throws IOException
    {
        ServerWSDLCall sc = conn.get_sqc();
        byte data[] = new byte[1];
        data[0] = 0;
        int len = sc.read_in_stream(id, data);
        if (len == -1)
        {
            throw new IOException( "Cannot write Serverstream " + id.getId() + ": " + sc.get_last_err_txt() + " Err: " + sc.get_last_err_code()) ;
        }
        if (len == 0)
            return -1;

        return data[0];
    }

    @Override
    public void close() throws IOException
    {
        // CLOSED?
        if (id == null)
            return;
        
        ServerWSDLCall sc = conn.get_sqc();
        if (sc.close_in_stream(id)  == false)
        {
            throw new IOException( "Cannot close Serverstream " + id.getId() + ": " + sc.get_last_err_txt() + " Err: " + sc.get_last_err_code()) ;
        }
        id = null;
    }

    @Override
    public int read( byte[] b ) throws IOException
    {
        ServerWSDLCall sc = conn.get_sqc();
        int len = sc.read_in_stream(id, b);
        if (len == -1)
        {
            throw new IOException( "Cannot read Serverstream " + id.getId() + ": " + sc.get_last_err_txt() + " Err: " + sc.get_last_err_code()) ;
        }
        return len;
    }

    @Override
    public int read( byte[] b, int off, int len ) throws IOException
    {
        ServerWSDLCall sc = conn.get_sqc();
        byte data[] = new byte[len];
        int rlen = read( data );
        if (rlen == -1)
        {
            throw new IOException( "Cannot read Serverstream " + id.getId() + ": " + sc.get_last_err_txt() + " Err: " + sc.get_last_err_code()) ;
        }
        if (rlen == 0)
            return 0;

        System.arraycopy( data, 0, b, off, rlen);

        return rlen;
    }

    @Override
    protected void finalize() throws Throwable
    {
        close();
    }

}
