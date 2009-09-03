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

    public FunctionCallConnect()
    {
        super();
    }

    public String call_abstract_function( String cmd, int to )
    {
        return sqc.send(cmd, to);
    }

    public String call_rmx_function( String cmd, int to )
    {
        return sqc.send_rmx(cmd, to);
    }


}
