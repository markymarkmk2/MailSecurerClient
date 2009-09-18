/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

/**
 *
 * @author mw
 */
public class StatementID
{
    String id;
    ConnectionID conn_id;
    StatementID( ConnectionID cid, String s )
    {
        conn_id = cid;
        id = s;
    }
    String getId()
    {return id;}
    ConnectionID getConnnId()
    {return conn_id;}
}