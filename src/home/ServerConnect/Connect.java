/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

/**
 *
 * @author mw
 */
public class Connect {
    ServerWSDLCall sqc;

    public Connect()
    {
        sqc = new ServerWSDLCall();
        sqc.init();
    }

    public ServerWSDLCall get_sqc()
    {
        return sqc;
    }

}
