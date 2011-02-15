/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

/**
 *
 * @author mw
 */
public class Connect
{

    ServerCall sqc;
/*
    public Connect()
    {
//        sqc = new ServerWSDLCall();
        String ip = Main.get_prop(Preferences.SERVER_IP, Main.get_ip() );
        int port = (int)Main.get_long_prop(Preferences.SERVER_PORT, (long)Main.get_port() );
        boolean use_ssl = Main.get_bool_prop(Preferences.SERVER_SSL, true );
        sqc = new ServerTCPCall(ip, port, use_ssl);
        sqc.init();
    }*/
    public Connect(String ip, int port, boolean ssl )
    {
//        sqc = new ServerWSDLCall();
        sqc = new ServerTCPCall(ip, port, ssl);
        sqc.init();
    }

    public ServerCall get_sqc()
    {
        return sqc;
    }
    public void close()
    {
        sqc.close();
    }

    public String get_ip()
    {
        return ((ServerTCPCall)sqc).get_ip();
    }
}
