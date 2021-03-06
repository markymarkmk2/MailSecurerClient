/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author mw
 */
public class ServerInputStream extends InputStream
{
    ServerCall sc;
    InStreamID id;

    public ServerInputStream( ServerCall _sc, String file ) throws IOException
    {
        sc = _sc;
        id = sc.open_in_stream(file);

        if (id == null)
        {
            throw new IOException( "Cannot open Serverstream " + file + ": " + sc.get_last_err_txt() + " Err: " + sc.get_last_err_code()) ;
        }
    }
    public ServerInputStream( ServerCall _sc, InStreamID _id ) throws IOException
    {
        sc = _sc;
        id = _id;
    }


    @Override
    public int read() throws IOException
    {
        
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
        
        
        if (sc.close_in_stream(id)  == false)
        {
            throw new IOException( "Cannot close Serverstream " + id.getId() + ": " + sc.get_last_err_txt() + " Err: " + sc.get_last_err_code()) ;
        }
        id = null;
    }

   
    public void read( OutputStream os ) throws IOException
    {
        if(! sc.read_in_stream(id, os) )
        {
            throw new IOException( "Cannot read Serverstream " + id.getId() + ": " + sc.get_last_err_txt() + " Err: " + sc.get_last_err_code()) ;
        }      
    }

    @Override
    public int read( byte[] b, int off, int len ) throws IOException
    {        
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
