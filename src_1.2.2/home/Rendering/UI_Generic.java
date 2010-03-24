/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.basic.BasicButtonUI;


class MyButtonUI extends BasicButtonUI
{
    ComponentUI default_ui;
    GlossButton bt;

    public MyButtonUI()
    {
        this.default_ui = UIManager.getUI(new JButton());
        bt = new GlossButton();
        bt.setFont(UI_Pirates.small_font);
        bt.setBorderPainted(false);
        bt.setContentAreaFilled(false);
        bt.setFocusPainted(false);
        bt.setBorder(null);
        bt.setOpaque( false );
    }

    @Override
    public void installUI( JComponent c )
    {
        super.installUI(c);
    }

    @Override
    public void update( Graphics g, JComponent c )
    {
        super.update(g, c);
    }


    @Override
    public void paint( Graphics g, JComponent c )
    {
        if (c instanceof JButton)
        {
            JButton jbt = (JButton)c;
            bt.setText( jbt.getText() );
            bt.setSize( jbt.getSize() );
            bt.setFont(UI_Pirates.small_font);
            bt.setBorderPainted(false);
            bt.setContentAreaFilled(false);
            bt.setFocusPainted(false);
            bt.setBorder(null);
            bt.setOpaque( false );
        }

        //default_ui.paint(g, bt);
        bt.paint(g);
    }



}
/**
 *
 * @author mw
 */
public abstract class UI_Generic
{

    public static UI_Generic create_ui( int ui_id )
    {
        switch( ui_id )
        {
            case 0:    return new UI_Pirates();
            case 1:    return new UI_Milk();
            case 2:    return new UI_Bluesea();
        }
        return new UI_Pirates();
    }
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
