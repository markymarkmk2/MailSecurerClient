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
/*
    public StreamConnect()
    {
        super();
    }
*/
    public StreamConnect(String ip, int port, boolean ssl)
    {
        super(ip, port, ssl);
    }

    public OutputStream open_out_stream( String file ) throws IOException
    {
        return new ServerOutputStream( get_sqc(), file);
    }
    public InputStream open_in_stream( String file ) throws IOException
    {
        return new ServerInputStream( get_sqc(), file);
    }

    public static void main( String[] args )
    {

        StreamConnect sc = new StreamConnect("192.168.1.145", 12345, true);

        try
        {
            File src = new File("j:\\testdata.txt" );

            int[] bc = {128*1024, 64*1024, 1024*1024};
            for ( int i = 0; i < bc.length; i++ )
            {
                int bs = bc[i];


                long start = System.currentTimeMillis();
                long last_start = System.currentTimeMillis();
                long size = src.length();

                BufferedInputStream bis = new BufferedInputStream( new FileInputStream( src ) );
                OutputStream os = sc.open_out_stream("z:\\tmp\\data.tst");

                byte[] buff = new byte[bs];

                System.out.println("BS: " + bs + "Byte" );
                int calls = 0;
                long real_len = 0;
                while (true)
                {
                    int rlen = bis.read(buff);
                    if (rlen == -1)
                        break;
                    os.write(buff, 0, rlen);
                    calls++;

                    real_len += rlen;
                    if (calls % 10 == 0)
                    {
                        long end = System.currentTimeMillis();
                        float  ratio = (real_len *1.0f)/((end - start) *1024.0f);
                        long diff = end - start;
                                if (diff == 0)
                                    diff = 1;
                        System.out.println(" Speed: "  + (10000 )/(diff) + " C/s, " + ratio + "MB/s");
                        last_start = System.currentTimeMillis();
                    }
                }
                os.close();
                bis.close();
                long end = System.currentTimeMillis();
                float  ratio = (size *1.0f)/((end - start) *1024.0f);
                System.out.println("Overall: " + ratio + "MB/s");
            }
        }
        catch (IOException iOException)
        {
            System.out.println("Urks: " + iOException.getMessage());
        }

    }
}
