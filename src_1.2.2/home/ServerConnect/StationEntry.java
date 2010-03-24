/*
 * StationEntry.java
 *
 * Created on 15. Oktober 2007, 11:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;


/**
 *
 * @author Administrator
 */
public class StationEntry
{
    long station;
    String ip;
    String version;
    String name;
    int ping;
    int port;
    
    
    
    public StationEntry( String _name, long s, String v, String _ip, int _port)
    {
        station = s;
        version = v;
        ip = _ip;
        name = _name;
        port = _port;
    }
    

    public int get_high_version()
    {
        if (version == null)
            return 0;
        
        try
        {
            int idx = version.indexOf(".");
            return Integer.parseInt(version.substring(0,idx));
        }
        catch (Exception exc)
        {
        }
        return 0;
        
    }
    public int get_low_version()
    {
        if (version == null)
            return 0;
        
        try
        {
            int idx = version.lastIndexOf(".");
            return Integer.parseInt(version.substring(idx + 1));
        }
        catch (Exception exc)
        {
        }
        return 0;
    }
    public int get_mid_version()
    {
        if (version == null)
            return 0;
        
        try
        {
            int idxa = version.lastIndexOf(".");
            int idxe = version.lastIndexOf(".");
            return Integer.parseInt(version.substring(idxa + 1, idxe));
        }
        catch (Exception exc)
        {
        }
        return 0;
    }


    
    @Override
    public String toString()
    {
        return name + " <" + version + "> " + ip + ":" + port;
    }
    
   

    public long get_id()
    {
        return station;
    }
   
    public String get_ip()
    {
        return ip;
    }
    public String get_name()
    {
        return name;
    }
    public int get_ping()
    {
        return ping;
    }

    public void set_ping(int p)
    {
        ping = p;
    }
    public String get_version()
    {
        return version;
    }


    void set_name(String s)
    {
        name = s;
    }
    void set_version(String s)
    {
        version = s;
    }
    void set_ip(String s)
    {
        ip = s;
    }

    public int get_port()
    {
        return port;
    }

}