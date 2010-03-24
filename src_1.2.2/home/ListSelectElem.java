/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home;

/**
 *
 * @author Administrator
 */
public class ListSelectElem 
{
    long id;
    String name;

    ListSelectElem( long _id, String _name )
    {
        id = _id;
        name = _name;
    }
    
    public int compare_to( ListSelectElem l2 )
    {
        return toString().compareTo(l2.toString());
    }
    @Override
    public String toString()
    {
        return name;
    }
    long getId()
    {
        return id;
    }

    @Override
    public boolean equals( Object obj )
    {
        if (obj instanceof ListSelectElem)
        {
            ListSelectElem le = (ListSelectElem)obj;
            if (le.id == id && le.name.compareTo(name) == 0)
                return true;
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 29 * hash + (int) (this.id ^ (this.id >>> 32));
        hash = 29 * hash + (this.name != null ? this.name.hashCode() : 0);
        return hash;
    }
    

}
