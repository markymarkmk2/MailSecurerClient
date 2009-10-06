/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

import dimm.home.Main;
import dimm.home.Preferences;

/**
 *
 * @author mw
 */
public class Connect
{

    ServerCall sqc;

    public Connect()
    {
//        sqc = new ServerWSDLCall();
        String ip = Main.get_prop(Preferences.SERVER_IP, Main.server_ip );
        int port = (int)Main.get_long_prop(Preferences.SERVER_PORT, (long)Main.server_port );
        sqc = new ServerTCPCall(ip, port);
        sqc.init();
    }
    public Connect(String ip, int port )
    {
//        sqc = new ServerWSDLCall();
        sqc = new ServerTCPCall(ip, port);
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
}
