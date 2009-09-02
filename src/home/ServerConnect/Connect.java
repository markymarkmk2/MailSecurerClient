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
        String port = Main.get_prop(Preferences.SERVER_PORT, Main.server_port );
        sqc = new ServerTCPCall(ip, port);
        sqc.init();
    }

    public ServerCall get_sqc()
    {
        return sqc;
    }
}
