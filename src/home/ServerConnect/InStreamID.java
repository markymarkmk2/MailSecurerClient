/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

/**
 *
 * @author mw
 */
public class InStreamID
{
    String id;
    long len;
    InStreamID( String s, long l )
    {
        id = s;
        len = l;
    }
    String getId()
    {return id;}

    long getLen()
    {
        return len;
    }
}