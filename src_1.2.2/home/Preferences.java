/*
 * Preferences.java
 *
 * Created on 5. Oktober 2007, 18:58
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package dimm.home;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;


/****
 
 Bestehedne Config:
Playlists=/var/www/localhost/htdocs/websense/login/pls
Songs=/var/www/localhost/htdocs/websense/dev/music_v3

 ****/

/**
 *
 * @author Administrator
 */
public class Preferences
{
    
    ArrayList<String> prop_names;
    
    public static final String PORT = "Port";
    public static final String SERVER = "Server";
    public static final String DB_CONNECT = "DBConnectStr";
    public static final String DB_USER = "DBUser";
    public static final String DB_PWD = "DBPwd";
    public static final String DB_CLASSNAME = "DBClassName";
    public static final String VPN_SERVER = "VPNServer";
    public static final String VPN_PORT = "VPNPort";
    public static final String SONGPATH = "SongPath";
    public static final String PXENABLE = "ProxyEnable";
    public static final String PXSERVER = "ProxyServer";
    public static final String PXPORT = "ProxyPort";
    public static final String DEFAULT_USER = "DefaultUser";
    public static final String DEFAULT_STATION = "DefaultStation";
    public static final String X_POS = "XPos";
    public static final String Y_POS = "YPos";
    public static final String COUNTRYCODE = "CountryCode";
    public static final String CHECK_NEWS = "CheckNews";
    public static final String WEBSITE = "Website";
    public static final String TOUCHSCREEN = "TouchScreen";
    public static final String UPDATESERVER = "UpdateServer";
    public static final String MALLORCAPROXYSERVER = "MallorcaProxyServer";
    public static final String TS_DEVICE_ID  ="TSDeviceID";
    public static final String VKEY_ALPHA = "VKeyboardAlpha";
    
    public static final String SERVER_IP = "ServerIP";
    public static final String SERVER_PORT = "ServerPort";
    public static final String HTML_HQ_RENDERER = "HTMLHQRenderer";
    public static final String SERVER_SSL = "ServerSSL";
    public static final String UI = "UI";

    
    java.util.Properties props;


    
    /** Creates a new instance of Preferences */
    public Preferences()
    {
        prop_names = new ArrayList<String>();
        
        prop_names.add( PORT );
        prop_names.add( SERVER );
        prop_names.add( PXENABLE );
        prop_names.add( PXSERVER );
        prop_names.add( PXPORT );
        prop_names.add( DB_CONNECT );
        prop_names.add( DB_USER );
        prop_names.add( DB_PWD );
        prop_names.add( DB_CLASSNAME );
        prop_names.add( VPN_SERVER );
        prop_names.add( VPN_PORT );
        prop_names.add( SONGPATH );
        prop_names.add( DEFAULT_USER );
        prop_names.add( DEFAULT_STATION );
        prop_names.add( X_POS );
        prop_names.add( Y_POS );
        prop_names.add( COUNTRYCODE );
        prop_names.add( CHECK_NEWS );
        prop_names.add( WEBSITE );
        prop_names.add( TOUCHSCREEN );
        prop_names.add( UPDATESERVER );
        prop_names.add( MALLORCAPROXYSERVER );
        prop_names.add( TS_DEVICE_ID );
        prop_names.add( VKEY_ALPHA );

        prop_names.add( SERVER_IP );
        prop_names.add( SERVER_PORT );
        prop_names.add( HTML_HQ_RENDERER );
        prop_names.add( SERVER_SSL );
        prop_names.add( UI );
        
                
        read_props();
    }
    
    ArrayList<String> get_prop_list()
    {
        return prop_names;
    }
    
    String base_prop_name(String s)
    {
        int idx = s.lastIndexOf( "_" );
        if (idx >= 0)
        {
            try
            {
                int n = Integer.parseInt( s.substring( idx + 1 ) );
                return s.substring(0, idx );
            }
            catch (Exception exc ) {}
        }
        return s;
    }
    
    boolean check_prop( String s )
    {
        for (int i = 0; i < prop_names.size(); i++)
        {
            String base_prop = base_prop_name(s);
            if (prop_names.get(i).compareTo(base_prop) == 0)
                return true;            
        }
        return false;
    }
        
    public String get_prop(String p)
    {
        if (!check_prop(p))
        {
            Main.err_log_warn("Unbekannte property <" + p + ">" );
            return null;
        }
        String ret = props.getProperty( p );
        return ret;
    }
    public boolean get_boolean_prop(String p)
    {
        return get_boolean_prop(p, false);
    }
    public boolean get_boolean_prop(String p, boolean def)
    {
        if (!check_prop(p))
        {
            Main.err_log_warn("Unbekannte property <" + p + ">" );
            return def;
        }
            String ret = props.getProperty( p );
            if (ret == null || ret.length() == 0)
                return def;

            if (ret.charAt(0) == '1')
                return true;

            if (ret.toLowerCase().charAt(0) == 'j')
                return true;
            if (ret.toLowerCase().charAt(0) == 'Y')
                return true;

            return false;
    }
    
    public void set_prop(String p, String v)
    {
        if (!check_prop(p))
        {
            Main.err_log_warn("Unbekannte property <" + p + ">" );
        }
        props.setProperty( p, v );
    }
    
    
    public void read_props()
    {
        File prop_file = new File( Main.PREFS_PATH + "preferences.dat" );
        props = new java.util.Properties();        
        try
        {
            FileInputStream istr = new FileInputStream( prop_file );
            props.load( istr );   
            istr.close();
        }        
        catch (Exception exc)
        {
            System.out.println("Kann Properties nicht lesen: " + exc.getMessage() );
        }
        
        
//        String db_server = props.getProperty("DBServer");
        
    
    }
    public boolean store_props()
    {        
        File prop_file = new File(Main.PREFS_PATH + "preferences.dat");
        try
        {
            FileOutputStream ostr = new FileOutputStream( prop_file );
            props.store( ostr, "JMailClient Properties, please do not edit" );
            ostr.close();
            return true;
        }        
        catch (Exception exc)
        {
            Main.err_log("Kann Properties nicht schreiben: " + exc.getMessage() );
        }
        return false;
    }
    
    
}
