/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

/**
 *
 * @author Administrator
 */
public interface CommContainer 
{
    
    public StationEntry get_selected_box();
    public void set_status( String st );
    public boolean do_scan_local();

}
