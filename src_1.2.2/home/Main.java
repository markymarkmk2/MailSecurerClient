/*
 * Main.java
 *
 * Created on 8. Oktober 2007, 10:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package dimm.home;

import dimm.home.Rendering.UI_Generic;
import dimm.home.native_libs.NativeLoader;
import java.io.File;
import javax.swing.UIManager;

/**
 *
 * @author Administrator
 */
public class Main
{
    private static String version_str = "1.5.3";
    

    static Main me;
    public static boolean scan_local = false;
    public static boolean enable_admin = false;
    public static boolean with_beta = false;
    public static boolean no_updater = false;
    public static String fixed_ip = null;
    public static final String PREFS_PATH = "";
    public static final String UPDATE_PATH = "update/";
    public static final String LOG_PATH = "logs/";

    private static String local_app_data_path = "MSClient";
    

    public static boolean enable_distributor;

    static void reinit_prefs()
    {
        
    }

    public static String get_version_str()
    {
        return version_str;
    }


    public static int get_port()
    {
        return server_port;
    }
    public static String get_ip()
    {
        return server_ip;
    }

    public static void sleep( int i )
    {
        try
        {
            Thread.sleep(i);
        }
        catch (InterruptedException interruptedException)
        {
        }
    }

    // public static final String SERVER_UPDATEWORKER_PATH = "/websense/v5/update/";
    Preferences prefs;
    public static UI_Generic ui;

    private static String server_ip = "127.0.0.1";
    private static int server_port = 8050;



    /** Creates a new instance of Main */
    public Main()
    {
        me = this;

        prefs = new Preferences();

        File f = new File( LOG_PATH );
        if (!f.exists())
            f.mkdirs();

        if (get_bool_prop(Preferences.CACHE_MAILFILES, false))
        {
            f = new File( get_cache_path() );
            if (!f.exists())
                f.mkdirs();
        }
    }

    public static String get_cache_path()
    {
        File f = new File( get_user_path(), "cache");
        if (!f.exists())
            f.mkdir();

        return f.getAbsolutePath();
    }

    public static String get_user_path()
    {
        String programGroupName = local_app_data_path;

        String userHome = System.getProperty("user.home");

        File workingDirectoryPath  = new File(userHome, "."+ programGroupName);

        if (NativeLoader.is_win())
        {
            String applicationData = System.getenv("APPDATA");
            if (applicationData != null)
            {
                workingDirectoryPath = new File( applicationData, programGroupName);
            }
            else
            {
                workingDirectoryPath = new File( userHome, programGroupName);
            }
        }
        if (!workingDirectoryPath.exists())
        {
            workingDirectoryPath.mkdir();
        }
        return workingDirectoryPath.getAbsolutePath();
    }


    static void print_system_property( String key )
    {
        System.out.println("Property " + key + ": " + System.getProperty(key));
    }

    /**
     * @param args the command line arguments
     */
    public static void main( String[] args )
    {

        if (args.length == 1 && args[0].compareTo("-version") == 0)
        {
            System.out.println(Main.get_version_str());
            return;
        }

        print_system_property( "java.version" );
        print_system_property( "java.vendor" );
        print_system_property( "java.home");
        print_system_property( "java.class.path");
        print_system_property( "os.name");
        print_system_property( "os.arch");
        print_system_property( "os.version");
        print_system_property( "user.dir");


        System.out.println("Look and Feels:");
        UIManager.LookAndFeelInfo[] lfi = UIManager.getInstalledLookAndFeels();
        for (int i = 0; i < lfi.length; i++)
        {
            System.out.println(lfi[i].toString());
        }
        System.out.println("Native version " + version_str);
        System.out.print("Args: ");
        for (int i = 0; i < args.length; i++)
        {
            System.out.print(args[i] + " ");
        }
        System.out.println();

        int lf_idx = -1;
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].compareTo("-server_ip") == 0)
            {
                try
                {
                    server_ip = args[i + 1];
                }
                catch (Exception exception)
                {
                }
                break;
            }
           if (args[i].compareTo("-server_port") == 0)
            {
                try
                {
                    server_port = Integer.parseInt( args[i + 1] );
                }
                catch (Exception exception)
                {
                }
                break;
            }
            if (args[i].compareTo("-LF") == 0)
            {
                try
                {
                    lf_idx = Integer.parseInt(args[i + 1]);
                }
                catch (Exception exception)
                {
                }
                break;
            }
            
        }

        // SUCK IN NATIVE LIBARIES
        new NativeLoader();

        if (NativeLoader.failed)
        {
            System.err.println("Not all native libraries could be loaded, please check our environment");
        }

        if (lf_idx >= 0)
        {
            try
            {
                UIManager.setLookAndFeel(lfi[lf_idx].getClassName());
                System.out.println("Using L&F " + lfi[lf_idx].toString());

            }
            catch (Exception ex)
            {
                System.err.println(ex.getMessage());
            }
        }
        else
        {
            try
            {
                UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel");
            }
            catch (Exception exc)
            {
                try
                {
                    UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
                }
                catch (Exception eexc)
                {
                    try
                    {
                        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                    }
                    catch (Exception ex)
                    {
                    }
                }
            }
        }

        ui = null;
        String force_mandant = null;
        boolean verbose = false;
        int debug = 0;        

        for (int i = 0; i < args.length; i++)
        {
            if (args[i].compareTo("-l") == 0)
            {
                scan_local = true;
            }
            
            if (args[i].compareTo("-b") == 0)
            {
                with_beta = true;
            }
            if (args[i].compareTo("-A") == 0)
            {
                enable_admin = true;
            }
            if (args[i].compareTo("-V") == 0)
            {
                enable_distributor = true;
            }
            if (args[i].compareTo("-v") == 0)
            {
                verbose = true;
            }
            if (args[i].compareTo("-d") == 0)
            {
                try
                {
                    debug = Integer.parseInt(args[i + 1]);
                }
                catch (Exception e)
                {
                    debug = 1;
                }
                System.out.println("Debuglevel: " + UserMain.debug);
            }
            if (args[i].compareTo("-no-updater") == 0)
            {
                no_updater = true;
            }
            if (args[i].compareTo("-set-version") == 0)
            {
                version_str = args[i + 1];
            }
            if (args[i].compareTo("-mandant") == 0)
            {
                force_mandant = args[i + 1];
            }
            if (args[i].compareTo("-server_ip") == 0)
            {
                server_ip = args[i + 1];
            }
            if (args[i].compareTo("-server_port") == 0)
            {
                try
                {
                    server_port = Integer.parseInt(args[i + 1]);
                }
                catch (NumberFormatException numberFormatException)
                {
                }
            }


            if (args[i].compareTo("-i") == 0)
            {
                fixed_ip = args[i + 1];
            }
        }
        Main mm = new Main();
        

        // FIRST SET OUT L&F
        //UI_Generic.save_uid();

        // READ THEMES
        UI_Generic.set_ui_list();

        ui = UI_Generic.create_ui( (int)Main.get_long_prop(Preferences.UI, 0l) );
        ui.set_ui(verbose);

        //StreamConnect.main(args);

        UserMain.init_text_interface(null);

        final SplashDlg splash = new SplashDlg(null, false);

        

        splash.setVisible(true);
        final long start = System.currentTimeMillis() / 1000;



        UserMain mn = null;
        try
        {
            mn = new UserMain(true, fixed_ip, with_beta, no_updater);
        }
        catch (Exception e)
        {
               e.printStackTrace();
        }

        UserMain.debug = debug;
        mn.setLocation(mn.x_pos, mn.y_pos);
        if (force_mandant != null)
        {
            try
            {
                if (!mn.force_mandant_id(Integer.parseInt(force_mandant)))
                    throw new Exception( "Shitty ID!");
            }
            catch (Exception exc)
            {
                UserMain.errm_ok("Kann Mandanten ID nicht setzen: " + exc.getMessage());
        //                SQLListBuilder.set_offline(true);
            }
        }

//            mn.get_update_worker().check_updates();
        splash.set_text( UserMain.Txt("Initializing") + "...");
        while ((System.currentTimeMillis() / 1000 - start) < 3 || splash.prefs_active())
        {
            /*                if (mn.get_update_worker().check_updates_ready())
            {
            splash.set_text("Initializing...");
            }
             * */
            try
            {
                Thread.sleep(100);
            }
            catch (InterruptedException ex)
            {
            }

        }
        //mn.get_update_worker().check_updates_ready();


        splash.setVisible(false);

        mn.setDefaultCloseOperation(UserMain.EXIT_ON_CLOSE);
        mn.setVisible(true);

        

//            mn.init_news( splash.get_check_news() );


    }
   

    public static void err_log_warn( String string )
    {
        System.out.println(string);
    }

    public static void err_log( String string )
    {
        System.out.println(string);
    }

    static public String get_prop( String pref_name )
    {
        if (me != null)
        {
            return me.prefs.get_prop(pref_name);
        }
        return null;
    }
    public static boolean get_bool_prop( String pref_name, boolean dflt )
    {
        if (me != null)
        {
            return me.prefs.get_boolean_prop(pref_name, dflt);
        }
        return dflt;
    }
    static public long get_long_prop( String pref_name, long def )
    {
        if (me != null)
        {
            String ret = me.prefs.get_prop(pref_name);
            if (ret != null)
            {
                try
                {
                    return Long.parseLong(ret);
                }
                catch (Exception exc)
                {
                    Main.err_log("Long preference " + pref_name + " has wrong format");
                }
            }
        }
        return def;
    }

    static public long get_long_prop( String pref_name )
    {
        return get_long_prop(pref_name, 0);
    }

    static public String get_prop( String pref_name, String def )
    {
        String ret = get_prop(pref_name);
        if (ret == null)
        {
            ret = def;
        }

        return ret;
    }

    static public void set_prop( String pref_name, String v )
    {
        if (me != null)
        {
            me.prefs.set_prop(pref_name, v);
        }
    }

    static public void set_long_prop( String pref_name, long v )
    {
        if (me != null)
        {
            me.prefs.set_prop(pref_name, Long.toString(v));
        }
    }

    static public String get_prop( String pref_name, int channel )
    {
        String ret = get_prop(pref_name + "_" + channel);
        if (ret != null)
        {
            return ret;
        }

        return get_prop(pref_name);
    }

    static public void set_prop( String pref_name, String v, int channel )
    {
        set_prop(pref_name + "_" + channel, v);
    }

    static public long get_long_prop( String pref_name, int channel )
    {
        return get_long_prop(pref_name, channel, 0);
    }

    static public long get_long_prop( String pref_name, int channel, long def )
    {
        String v = get_prop(pref_name, channel);
        if (v != null)
        {
            try
            {
                return Long.parseLong(v);
            }
            catch (Exception exc)
            {
                Main.err_log("Long preference " + pref_name + " has wrong format");
            }
        }
        return def;
    }

    static public void set_long_prop( String pref_name, long v, int channel )
    {
        set_prop(pref_name + "_" + channel, Long.toString(v));
    }

    static public Preferences get_prefs()
    {
        if (me != null)
        {
            return me.prefs;
        }
        return null;
    }

    
}
