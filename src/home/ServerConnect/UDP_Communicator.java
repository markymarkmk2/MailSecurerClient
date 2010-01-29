/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

import dimm.home.UserMain;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;

/**
 *
 * @author Administrator
 */
public class UDP_Communicator
{
    public static final String MISS_ARGS = "missing args";
    public static final String WRONG_ARGS = "wrong args";
    public static final String UNKNOWN_CMD = "UNKNOWN_COMMAND";

    CommContainer main;
    String answer;

    private static final int UDP_SEND_TO_MS = 20000;   
    private static final int UDP_LEN = 256;
    public  final int UDP_BIGBLOCK_SIZE = 48000;

    private static final int UDP_CLIENT_PORT = 11511;
    private static final int UDP_SERVER_PORT = 11510;
    private static final int TCP_SERVER_PORT = 11510;

    int answer_station_id;
    DatagramSocket keep_udp_s = null;
    private int ping;
    
    public UDP_Communicator(CommContainer main )
    {
        this.main = main;
        
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
            return "Sorry, dieser Befehl wird von der Box nicht unterst�tzt";
        }

        return answer;
    }

 

    public boolean check_answer( String test_answer )
    {
        boolean ok = false;
        if (test_answer == null || test_answer.length() == 0)
        {
            answer = "Kommunikation fehlgeschlagen!";
            return false;
        }


        if (test_answer.compareTo("--failed--") == 0)
        {
            answer =  "Kommunikation fehlgeschlagen!";
            return false;
        }

        if (test_answer.compareTo("UNKNOWN_COMMAND") == 0)
        {
            answer =  "Oha, dieser Befehl wird von der Box nicht unterst�tzt!";
            return false;
        }


        if (test_answer.length() >= 2 && test_answer.substring(0, 2).compareTo("OK") == 0)
        {
            ok = true;
            if (test_answer.length() > 3)
                answer = test_answer.substring(3);
            else
                answer = "";

            ok = true;
        }
        else if (test_answer.length() >= 3 && test_answer.substring(0, 3).compareTo("NOK") == 0)
        {
            ok = false;
            if (test_answer.length() > 4)
                answer = test_answer.substring(4);
            else
                answer = "";

        }
        return ok;
    }


    
    public int get_answer_station_id()
    {
        return answer_station_id;
    }

   
    
    public int ping( String id, int delay_ms )
    {        
        //System.out.println("Calling ping for " + id + ":...");

        String str = id + ":" + "GETSTATUS MD:SHORT"; 
        boolean ok = false;
        synchronized (this)
        {
        answer = udp_send( str, false, main.do_scan_local(), 0, null, delay_ms );
        ok = check_answer();
        }
        if (ok)
        {
            return ping;
        }
        return -1;
    }
    
    

    public synchronized String send( String str, OutputStream outp)
    {
        
        return udp_send( str, false,  false, 0, outp );
    }
  
    
    public String udp_send( String str, boolean scan, boolean do_scan_local, int retries )
    {
        return udp_send( str, scan, do_scan_local, retries, null);
    }
        
    public boolean send_fast_retry_cmd(String str)
    {

        synchronized (this)
        {

            StationEntry ste = main.get_selected_box();
            if (ste != null)
            {
                String id = Long.toString(ste.get_id() ) + ":";
                str = id + str;

                // 3 TIMES
                answer = udp_send( str, false, main.do_scan_local(), 0, null, 2500 );
                if (check_answer())
                    return true;
    /*
                answer = udp_send( str, false, 0, null, 1 );
                if (check_answer())
                    return true;

                answer = udp_send( str, false, 0, null, 1 );
                return check_answer();
     */
            }
        }
        return false;
    }
            
    public boolean send_cmd(String string)
    {
        boolean ok = false;
        
        StationEntry ste = main.get_selected_box();
        if (ste != null)
        {
            String id = Long.toString(ste.get_id() ) + ":";

        synchronized (this)
        {
            answer = send( id + string, null );

            return check_answer();
        }
        }
        return ok;
    }

    public boolean send_cmd(String string, OutputStream outp)
    {
        boolean ok = false;

        StationEntry ste = main.get_selected_box();
        if (ste != null)
        {
            String id = Long.toString(ste.get_id() ) + ":";

        synchronized (this)
        {
            answer = send( id + string, outp );
       
            return check_answer();
        }            
        }
        return ok;
    }
    
   
    public boolean check_answer() 
    {
        boolean ok = false;
        if (answer   == null || answer.length() == 0)
        {
            answer   = "Kommunikation fehlgeschlagen!";
            return false;
        }
        
        
        if (answer.compareTo("--failed--") == 0)
        {
            answer =  "Kommunikation fehlgeschlagen!";
            return false;
        }
        String magic = "MAILSECURER:";
        if (answer.length() < magic.length() || answer.substring( 0, magic.length() ).compareTo( magic ) != 0)
        {
            answer =  "Unbekannter Kommunikationspartner!";
            return false;
        }

        // CLIP OFF ID
        answer_station_id = -1;
        answer = answer.substring( magic.length() );
        try
        {
            int index = answer.indexOf(":");
            String id_str =  answer.substring( 0, index );
            answer_station_id = Integer.parseInt( id_str );
            answer = answer.substring(index + 1 );
        }
        catch (Exception exc)
        {
            answer =  "Unbekannter Kommunikationspartner!";
            return false;
        }
        
        if (answer.compareTo("UNKNOWN_COMMAND") == 0)
        {            
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
    
    public String udp_send( String str, boolean scan, boolean do_scan_local, int retries, OutputStream outp )
    {
        return udp_send( str, scan, do_scan_local, retries, outp, -1 );
    }
    
    public String udp_send( String str, boolean scan, boolean do_scan_local, int retries, OutputStream outp, int timeout )
    {
        UserMain.debug_msg( 4, "udp_send: " + str + " SC:" + (scan?"1":"0"));
        synchronized (this)
        {
               
        try
        {

            keep_udp_s = null;
            if (keep_udp_s == null)
            {
                if (!do_scan_local)
                    keep_udp_s = new DatagramSocket(UDP_CLIENT_PORT,InetAddress.getByName("0.0.0.0"));
                else
                    keep_udp_s = new DatagramSocket(UDP_CLIENT_PORT,InetAddress.getByName("localhost"));

                if (timeout == -1)
                    timeout = UDP_SEND_TO_MS;

                keep_udp_s.setSoTimeout(timeout );
                keep_udp_s.setReuseAddress(true);
                keep_udp_s.setBroadcast(true);
            }



            byte[] out_data = new byte[UDP_LEN];
            for (int i = 0; i < out_data.length; i++)
            {
                out_data[i] = ' ';
            }
            byte[] str_data = str.getBytes();
            for (int i = 0; i < str_data.length; i++)
            {
                out_data[i] = str_data[i];
            }


            byte[] in_data = new byte[UDP_LEN];

            DatagramPacket out_packet = null;

            if (!do_scan_local)
                out_packet = new DatagramPacket(out_data,out_data.length,InetAddress.getByName("255.255.255.255"),UDP_SERVER_PORT);
            else                
                out_packet = new DatagramPacket(out_data,out_data.length,InetAddress.getByName("localhost"),UDP_SERVER_PORT);


            DatagramPacket in_packet = new DatagramPacket( in_data, in_data.length );


            try
            {
                long start = System.currentTimeMillis();

                keep_udp_s.send( out_packet );           
                if (!scan)
                {
                    //System.out.println("Sending <" + str +">");
                    keep_udp_s.receive( in_packet );

                    ping = (int)(System.currentTimeMillis() - start);
                    //System.out.println("Ping is " + ping  + " ms <" + str + ">");

                    String result = new String( in_packet.getData(), "UTF-8").trim();


                    //System.out.println("Received <" + result +">");

                    // MULTI DATAGRAM MESSAGE?
                    int follow_frames_idx = result.indexOf(":CONTINUE:");
                    if (follow_frames_idx > 0 && follow_frames_idx < 20) // MUST BE MAILSECURER:ID:CONTINUE:NNNNNN
                    {
                        long data_cnt = Long.parseLong(result.substring( follow_frames_idx + 10 ));

                        // SAVE START OF TELEGRAM
                        StringBuffer sb = new StringBuffer( result.substring(0, follow_frames_idx + 1 ) );

                        while (data_cnt > UDP_BIGBLOCK_SIZE)
                        {
                            byte[] big_data = new byte[UDP_BIGBLOCK_SIZE];

                            DatagramPacket in_p2 = new DatagramPacket( big_data, big_data.length );
                            keep_udp_s.receive( in_p2 );

                            if (outp != null)
                            {
                                outp.write(in_p2.getData());
                            }
                            else
                            {                            
                                String _s = new String(in_p2.getData(), "UTF-8" );
                                sb.append( _s.trim() );
                            }

                            data_cnt -= UDP_BIGBLOCK_SIZE;
                        }
                        if (data_cnt > 0)
                        {
                            byte[] big_data = new byte[(int)data_cnt ];

                            DatagramPacket in_p2 = new DatagramPacket( big_data, big_data.length );
                            keep_udp_s.receive( in_p2 );   

                            if (outp != null)
                            {
                                outp.write(in_p2.getData());
                            }
                            else
                            {                            
                                String _s = new String(in_p2.getData(), "UTF-8" );
                                sb.append( _s.trim() );
                            }

                        }
                        result = sb.toString();
                    }
                    else
                    {
                        if (result.substring(0, 11).compareTo("MAILSECURER") != 0)
                        {
                            UserMain.self.debug_msg( 4, "udp_recv: " + result);
                        }
                    }

                    keep_udp_s.close();
                    keep_udp_s = null;

                    main.set_status( "" );
                    return result;
                }
                else
                {

                    String result = "";
                    int start_retries = retries;

                    while (retries > 0)
                    {
                        try
                        {
                            keep_udp_s.receive( in_packet );
                            retries = start_retries; // START AGAIN


                            String ping_str = new String( in_packet.getData()).trim();
                            String ip = in_packet.getSocketAddress().toString();

                            int s_idx = ip.indexOf("/");
                            if (s_idx >= 0)
                                ip = ip.substring( s_idx + 1 );

                            int e_idx = ip.indexOf(":");
                            if (e_idx >= 0)
                                ip = ip.substring( 0, e_idx );

                            ping_str += " IP:" + ip;
                            
                            UserMain.debug_msg( 4, "ping_recv: " + ping_str);

                            main.set_status("Scanning... found: " +  ping_str);

                            if (result.length() > 0)
                                result += "\n";

                            result += ping_str;
                        }
                        catch ( SocketTimeoutException texc)
                        {
                             retries--;                         
                        }
                    }
                    keep_udp_s.close();
                    keep_udp_s = null;

                    UserMain.debug_msg( 4, "finished ping");

                    return result;
                }                                           
            }

            catch ( SocketTimeoutException texc)
            {
                if (keep_udp_s != null)
                    keep_udp_s.close();
                keep_udp_s = null;
                main.set_status("No answer from box" + texc.getMessage() );
                //texc.printStackTrace();
                return "--timeout--";

            }
            catch ( Exception exc )
            {
                if (keep_udp_s != null)
                    keep_udp_s.close();
                keep_udp_s = null;
                main.set_status("Communication failed: " + exc.getMessage() );
                //exc.printStackTrace();
            }
        }
        catch ( Exception exc )
        {      
            if (keep_udp_s != null)
                keep_udp_s.close();
            keep_udp_s = null;
            //exc.printStackTrace();
            main.set_status("Socket failed " + exc.getMessage());
        }
        return "--failed--";
    }
    }
    
        

}
