/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author mw
 */
public class ServerOutputStream extends OutputStream
{
    ServerCall sc;
    OutStreamID id;

    public ServerOutputStream( ServerCall _sc, String file ) throws IOException
    {
        sc = _sc;
        id = sc.open_out_stream(file);

        if (id == null)
        {
            throw new IOException( "Cannot open Serverstream " + file + ": " + sc.get_last_err_txt() + " Err: " + sc.get_last_err_code()) ;
        }
    }


    @Override
    public void write( int b ) throws IOException
    {
        byte data[] = new byte[1];
        data[0] = (byte)b;
        ByteArrayInputStream bais = new ByteArrayInputStream(data);

        if (sc.write_out_stream(id, 1, bais)  == false)
        {
            throw new IOException( "Cannot write Serverstream " + id.getId() + ": " + sc.get_last_err_txt() + " Err: " + sc.get_last_err_code()) ;
        }
    }

    @Override
    public void close() throws IOException
    {
        // CLOSED?
        if (id == null)
            return;

        if (sc.close_out_stream(id)  == false)
        {
            throw new IOException( "Cannot close Serverstream " + id.getId() + ": " + sc.get_last_err_txt() + " Err: " + sc.get_last_err_code()) ;
        }
        id = null;
    }

    @Override
    public void write( byte[] b ) throws IOException
    {
        ByteArrayInputStream bais = new ByteArrayInputStream(b);

        if (sc.write_out_stream(id, b.length, bais)  == false)
        {
            throw new IOException( "Cannot write Serverstream " + id.getId() + ": " + sc.get_last_err_txt() + " Err: " + sc.get_last_err_code()) ;
        }
    }


    @Override
    public void write( byte[] b, int off, int len ) throws IOException
    {
        if (off == 0 && len == b.length)
        {
            write(b);
        }
        else
        {
            byte data[] = new byte[len];
            System.arraycopy(b, off, data, 0, len);
            write(data);
        }
    }

    @Override
    protected void finalize() throws Throwable
    {
        close();
    }


    public void write( InputStream is, long len) throws IOException
    {
        if (sc.write_out_stream(id, len, is)  == false)
        {
            throw new IOException( "Cannot write Serverstream " + id.getId() + ": " + sc.get_last_err_txt() + " Err: " + sc.get_last_err_code()) ;
        }
    }

}
