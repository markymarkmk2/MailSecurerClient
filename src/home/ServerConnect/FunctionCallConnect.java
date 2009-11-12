/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;



/**
 *
 * @author mw
 */
public class FunctionCallConnect extends Connect
{

    public static final int SHORT_TIMEOUT = 3000;
    public static final int LONG_TIMEOUT = 30000;
    public static final int NO_TIMEOUT = 0;
    
    public FunctionCallConnect()
    {
        super();
    }
    public FunctionCallConnect(String ip, int port, boolean ssl)
    {
        super(ip, port, ssl);
    }

    public String call_abstract_function( String cmd, int to )
    {
        return sqc.send(cmd, to);
    }

    public String call_rmx_function( String cmd, int to )
    {
        return sqc.send_rmx(cmd, to);
    }

    public String get_last_err_txt()
    {
        return sqc.get_last_err_txt();
    }
    public int get_last_err_code()
    {
        return sqc.get_last_err_code();
    }


}
