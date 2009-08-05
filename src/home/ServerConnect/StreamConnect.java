/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;



/**
 *
 * @author mw
 */


public class StreamConnect extends Connect
{

    public StreamConnect()
    {
        super();
    }


    public OutputStream open_out_stream( String file ) throws IOException
    {
        return new ServerOutputStream( this, file);
    }
    public InputStream open_in_stream( String file ) throws IOException
    {
        return new ServerInputStream( this, file);
    }

    public static void main( String[] args )
    {

        StreamConnect sc = new StreamConnect();

        try
        {
            File src = new File("z:\\tmp\\datasrc.tst" );
            BufferedInputStream bis = new BufferedInputStream( new FileInputStream( src ) );
            OutputStream os = sc.open_out_stream("z:\\tmp\\data.tst");

            byte[] buff = new byte[8192];

            while (true)
            {
                int rlen = bis.read(buff);
                if (rlen == -1)
                    break;
                os.write(buff, 0, rlen);
            }
            os.close();
        }
        catch (IOException iOException)
        {
            System.out.println("Urks: " + iOException.getMessage());
        }

    }
}
