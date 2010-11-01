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

    public static final int SHORT_TIMEOUT = 10;
    public static final int MEDIUM_TIMEOUT = 30;
    public static final int LONG_TIMEOUT = 600;
    public static final int NO_TIMEOUT = 0;
    
    public FunctionCallConnect()
    {
        super();
    }
    public FunctionCallConnect(String ip, int port, boolean ssl)
    {
        super(ip, port, ssl);
    }

    public String call_abstract_function( String cmd )
    {
        //System.out.println("cab: " + cmd);
        String ret = call_abstract_function(cmd, SHORT_TIMEOUT);
        //System.out.println("ret: " + ret);
        return ret;
    }
    public String call_abstract_function( String cmd, int to_s )
    {
        return sqc.send(cmd, to_s);
    }

    public String call_rmx_function( String cmd )
    {
        return call_rmx_function(cmd, SHORT_TIMEOUT);
    }
    public String call_rmx_function( String cmd, int to_s )
    {
        return sqc.send_rmx(cmd, to_s);
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
