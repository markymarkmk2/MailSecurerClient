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
    ServerCall sqc;

    public Connect()
    {
        sqc = new ServerCall();
        sqc.init();
    }

    public ServerCall get_sqc()
    {
        return sqc;
    }

}
