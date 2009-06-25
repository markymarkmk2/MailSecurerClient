/*
 * Main.java
 *
 * Created on 8. Oktober 2007, 10:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package dimm.home;

import dimm.home.Rendering.UI_Bluesea;
import dimm.home.Rendering.UI_Generic;
import dimm.home.Rendering.UI_Pirates;
import javax.swing.UIManager;

/**
 *
 * @author Administrator
 */
public class Main
{

    static Main me;
    public static boolean scan_local = false;
    public static boolean enable_admin = false;
    public static boolean with_beta = false;
    public static boolean no_updater = false;
    public static String fixed_ip = null;
    public static final String PREFS_PATH = "";
    public static final String UPDATE_PATH = "update/";
    public static final String LOG_PATH = "logs/";
    public static String version_str = "0.0.1";
    public static boolean enable_distributor;
    // public static final String SERVER_UPDATEWORKER_PATH = "/websense/v5/update/";
    Preferences prefs;
    public static UI_Generic ui;

    /** Creates a new instance of Main */
    public Main()
    {
        me = this;

        prefs = new Preferences();
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


        // FIRST SET OUT L&F
        ui = new UI_Pirates();
        ui = new UI_Bluesea();

        ui.set_ui(false);


        Main mm = new Main();
        UserMain.init_text_interface(null);

        final SplashDlg splash = new SplashDlg(null, false);

        if (Main.get_long_prop(Preferences.CHECK_NEWS) > 0)
        {
            splash.set_check_news(true);
        }

        splash.setVisible(true);
        final long start = System.currentTimeMillis() / 1000;

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
            if (args[i].compareTo("-no-updater") == 0)
            {
                no_updater = true;
            }
            if (args[i].compareTo("-set-version") == 0)
            {
                version_str = args[i + 1];
            }


            if (args[i].compareTo("-i") == 0)
            {
                fixed_ip = args[i + 1];
            }
        }


        /* Ping p = new Ping("www.sonicsense.de");
        if (p.ping() < 0)
        {
        UserMain.errm_ok(UserMain.Txt("Kein_Internet_nur_lokale_Boxen_sichtbar"));
        //                SQLListBuilder.set_offline(true);
        }
         */

        UserMain mn = null;
        try
        {
            mn = new UserMain(true, fixed_ip, with_beta, no_updater);
        }
        catch (Exception e)
        {
               e.printStackTrace();
        }

        mn.setLocation(mn.x_pos, mn.y_pos);

//            mn.get_update_worker().check_updates();
        splash.set_text("Initializing...");
        while ((System.currentTimeMillis() / 1000 - start) < 3)
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

    static public boolean is_proxy_enabled()
    {
        String px_enable = get_prop(Preferences.PXENABLE);
        if (px_enable != null && px_enable.length() > 0 && px_enable.charAt(0) == '1')
        {
            return true;
        }

        return false;
    }
}