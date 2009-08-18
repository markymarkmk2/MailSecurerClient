/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

import dimm.home.Main;

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
        sqc = new ServerTCPCall(Main.server_ip, Main.server_port);
        sqc.init();
    }

    public ServerCall get_sqc()
    {
        return sqc;
    }
}
