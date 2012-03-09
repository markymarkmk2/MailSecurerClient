/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Panels.MailView;

import java.io.IOException;
import java.io.OutputStream;

/**
 *
 * @author Administrator
 */
public class MBoxFilterOutputStream extends OutputStream
{
    byte[] FROM = {0,'F', 'r', 'o', 'm', ' ' };
    OutputStream os;

    public MBoxFilterOutputStream( OutputStream os)
    {
        this.os = os;
    }

    @Override
    public void write( byte[] b ) throws IOException
    {
        this.write(b, 0, b.length);
    }

    boolean detect_mode = false;
    int detect_cnt = 0;
    @Override
    public void write( byte[] b, int off, int len ) throws IOException
    {
        for( int i = off; i < len && detect_mode; i++)
        {
            this.write(b[i]);
            off++;
        }

        int act_idx = 0;
        for (act_idx = off; act_idx < len; act_idx++)
        {
            // DETECT NL
            if (b[act_idx] == '\n' || b[act_idx] == '\r')
            {
                // SAVE IT
                FROM[0] = b[act_idx];
                break;
            }
        }
        // WRITE CLEAN STUFF
        os.write(b, off, act_idx - off);

        // WRITE REST IF DETECTED TO SINGLE BYTE FUNC
        if (act_idx < len)
        {
            detect_mode = true;
            detect_cnt = 0;
            for( int i = act_idx; i < len; i++)
                this.write(b[i]);
        }

    }

    @Override
    public void write( int b ) throws IOException
    {
        if (!detect_mode)
        {
            // DETECT NL
            if (b == '\n' || b == '\r')
            {
                // SAVE IT
                FROM[0] = (byte)b;
                detect_mode = true;
                detect_cnt = 0;
            }
        }
        // WE ARE IN PROGRESS OF DETECTION
        if (b == FROM[detect_cnt])
        {
            if (detect_cnt == FROM.length - 1)
            {
                os.write(FROM[0]);
                os.write((byte)'>');
                os.write(FROM, 1, FROM.length - 1);
                detect_mode = false;
                detect_cnt = 0;
                return;
            }
            detect_cnt++;
        }
        else
        {
            // OKAY; REST OF FAILED DETECTION TO STREAM ( Fro, F, "From" )
            if (detect_mode)
            {
                os.write(FROM, 0, detect_cnt);
                detect_mode = false;
                detect_cnt = 0;
            }
            os.write(b);
        }
    }

    @Override
    public void flush() throws IOException
    {
        super.flush();
    }

    @Override
    public void close() throws IOException
    {

        flush();
        os.close();
        super.close();
    }

    void write_direct( String string ) throws IOException
    {
        os.write(string.getBytes());
    }



}