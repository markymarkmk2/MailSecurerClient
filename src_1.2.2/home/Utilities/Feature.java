/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Utilities;

/**
 *
 * @author Administrator
 */
public class Feature
{
    private String name;
    private String optVal;
    
    public Feature( String n, String o )
    {
        name = n;
        optVal = o;
    }

    public String getName()
    {
        return name;
    }

    public String getOptVal()
    {
        return optVal;
    }
}
