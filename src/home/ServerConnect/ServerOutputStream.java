/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author mw
 */
public class ServerOutputStream extends OutputStream
{
    StreamConnect conn;
    OutStreamID id;

    public ServerOutputStream( StreamConnect _conn, String file ) throws IOException
    {
        conn = _conn;
        ServerCall sc = conn.get_sqc();
        id = sc.open_out_stream(file);

        if (id == null)
        {
            throw new IOException( "Cannot open Serverstream " + file + ": " + sc.get_last_err_txt() + " Err: " + sc.get_last_err_code()) ;
        }
    }


    @Override
    public void write( int b ) throws IOException
    {
        ServerCall sc = conn.get_sqc();
        byte data[] = new byte[1];
        data[0] = (byte)b;
        if (sc.write_out_stream(id, data)  == false)
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

        ServerCall sc = conn.get_sqc();
        if (sc.close_out_stream(id)  == false)
        {
            throw new IOException( "Cannot close Serverstream " + id.getId() + ": " + sc.get_last_err_txt() + " Err: " + sc.get_last_err_code()) ;
        }
        id = null;
    }

    @Override
    public void write( byte[] b ) throws IOException
    {
        ServerCall sc = conn.get_sqc();
        if (sc.write_out_stream(id, b)  == false)
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

}
