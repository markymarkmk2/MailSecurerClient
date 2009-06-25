/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

import java.awt.Color;
import java.awt.Font;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeMap;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

/**
 *
 * @author mw
 */
public abstract class UI_Generic
{
    public abstract Color get_appl_base_color();
    public abstract Color get_appl_selected_color();
    public abstract Color get_appl_dgray();
    public abstract Color get_nice_white();
    public abstract Color get_nice_gray();
    public abstract Font get_small_font();
    public abstract void set_ui(boolean verbose);


    static final Color greenish = new Color( 100, 230, 100 );
    static final Color blueish = new Color( 100, 100, 230 );
    static final Color yellowish = new Color( 230, 230, 100 );
    static final Color brownish = new Color( 174, 145, 68 );


    public Color get_brownisch()
    {
        return brownish;
    }

    public static void uidftest()
    {
        System.out.println("UIManager Default Properties");
        UIDefaults df1 = UIManager.getDefaults(); // returns a HashTable
        TreeMap<String,String> tm = new TreeMap<String,String>(  );
        Enumeration dfkeys = df1.keys(); // returns an Enumeration
        while (dfkeys.hasMoreElements())
        {
            String key_str = "null";
            String val_str = "null";
            Object key = dfkeys.nextElement();

            if (key != null)
            {
                if (df1.get(key) != null)
                    val_str = df1.get(key).toString();
                key_str =  key.toString();
            }

            tm.put(key_str, val_str);
        }
        NavigableSet ks = tm.navigableKeySet();
        Iterator i = ks.iterator();
        System.out.println("\n KEY / VALUE list\n");

        while (i.hasNext())
        {
            String key = i.next().toString();
            System.out.println(" key: "+ key + "\tval: " +  tm.get(key) );
        }

/*
        Enumeration dfkeys = tm.keys(); // returns an Enumeration

        ArrayList<String> l = new ArrayList<String>();
        l.

        System.out.println("\n KEY / VALUE list");
        while (dfkeys.hasMoreElements())
        {
            Object key = dfkeys.nextElement();
            System.out.println(" key: "+ key + "\tval: " +  df1.get(key) );
        }
 */
        System.out.println("\n");
    } // end ctor





}
