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
    public InStreamID( String s, long l )
    {
        id = s;
        len = l;
    }
    public String getId()
    {return id;}

    public long getLen()
    {
        return len;
    }
}