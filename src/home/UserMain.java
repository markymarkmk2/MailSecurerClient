/*
 * UserMain.java
 *
 * Created on 25. Januar 2008, 10:10
 */

package dimm.home;

import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossErrDialog;
import dimm.home.Rendering.GlossPanel;
import dimm.home.Rendering.NavigationHeader;
import dimm.home.Rendering.SpringGlassPane;
import dimm.home.Rendering.TitlePanel;
import java.awt.BorderLayout;

import java.awt.Color;
import java.awt.Dimension;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;
import  sun.audio.*;    //import the sun.audio package




/**
 *
 * @author  Administrator
 */
public class UserMain extends javax.swing.JFrame
{
    // PANELS
    NavigationHeader navPanel ;
    PanelVerwaltung pn_verwaltung;
    PanelStartup pn_startup;
    
    TitlePanel titlePanel;

    
    String user_name = getString("Unbekannt");

    // GLOBAL DEFINES

    public static final int UL_DUMMY = 1;
    public static final int UL_ADMIN = 2;
    public static final int UL_MULTIADMIN = 3;
    public static final int UL_SYSADMIN = 5;

 /*   public static final Color nice_white = new Color( 199,199,199);
    public static final Color nice_gray = new Color( 100,100,100);
    public static final Color appl_base_color = new Color( 208,82,0);
    public static final Color appl_selected_color = appl_base_color;
    public static final Color appl_dgray = new Color( 51,51,51);
  */
    
    
//    public static final Font small_font = new Font("Tahoma", Font.PLAIN, 11 );
    
    public static String WANT_DB_CHANGE_TXT;


    public static int APPL_X_SIZE = 800;
    public static int APPL_Y_SIZE = 600;
    
    @InjectedResource
    private BufferedImage haloPicture;
    @InjectedResource
    private BufferedImage iconPicture;

    @InjectedResource
    private Color gradientTop = Color.black;
    @InjectedResource
    private Color gradientBottom = Color.gray;

    // GLOBAL HALO PICTURE
    public static BufferedImage ghaloPicture;    
    public static Color ggradientTop = Color.black;
    public static Color ggradientBottom = Color.gray;
    public static UserMain self;
    
    private int userLevel;
    boolean is_udp;
    String fixed_ip;
    boolean no_updater;
    
    
    
/*    public static final int PBC_PLAYER = 0;
    public static final int PBC_TASKS = 1;*/
    public static final int PBC_LOGIN = 2;
    public static final int PBC_LOGOFF = 3;
    public static final int PBC_SEARCH = 4;
    public static final int PBC_ADMIN = 5;
//    public static final int PBC_VIRTUAL_ADMIN = 6;
    
    
    public int x_pos = 100;
    public int y_pos = 100;
    
    private SpringGlassPane glassPane;    
       
    private VKeyboard vkb;
    boolean use_mallorca_proxy;


    public static String get_version_str()
    {
        return Main.version_str;
    }
    private String l_code = "DE";

    public void call_navigation_click()
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    public void restart_gui()
    {
        restart_gui( l_code );
    }

 
  
    void set_mallorca_proxy( boolean b)
    {
        use_mallorca_proxy = b;
    }
    public boolean get_mallorca_proxy( )
    {
        return use_mallorca_proxy;
    }
    
   
    public void setupGlassPane()
    {        
        glassPane = new SpringGlassPane(this);
        this.setGlassPane(glassPane);
        glassPane.setVisible(true);
    }
    public SpringGlassPane get_spring_glass_pane()
    {
        return glassPane;
    }
    
    private int debug = 1;
    
    

    public void err_log(String message)
    {
        System.out.println(message);
    }
    

   // static boolean translation_warned = false;
    static public String getString(String string)
    {
        try
        {
            return java.util.ResourceBundle.getBundle("dimm/home/SR_Properties").getString(string);
        }
        catch (Exception exc)
        {
            System.err.println("Missing translation resource: " + string);
            // REMOVE UNDERSCORES FROM KEY
            string = string.replace('_', ' ');
        }
        return string;
    }

    static public void init_text_interface(String lcode)
    {
        if (lcode == null || lcode.length() == 0)            
            lcode = Main.get_prop( Preferences.COUNTRYCODE, "EN" );                 
        
        if (lcode.compareTo("DE") == 0)
        {
            Locale l = new Locale("de", "DE", "");
            Locale.setDefault(l);
        }
        if (lcode.compareTo("EN") == 0)
        {
            Locale l = new Locale("en", "EN", "");
            Locale.setDefault(l);
        }
        if (lcode.compareTo("DK") == 0)
        {
            Locale l = new Locale("da", "DK", "");
            Locale.setDefault(l);
        }
        
        WANT_DB_CHANGE_TXT = getString("Wollen_Sie_die_ge�nderten_Daten_speichern?");        
    }
    public void show_vkeyboard( UserMain frame, JComponent comp, boolean b)
    {
        vkb.set_component(comp);
//        vkb.set_dlg(null);
        if (b)
            vkb.set_num_keybd();
        else
            vkb.set_full_keybd();

        vkb.open_kbd();
    }    
    public void show_vkeyboard( GenericGlossyDlg parent_dlg, JComponent comp, boolean b)
    {
        vkb.set_component(comp);
//        vkb.set_dlg(parent_dlg);
        if (b)
            vkb.set_num_keybd();
        else
            vkb.set_full_keybd();

        vkb.open_kbd();
    }
    
    public void vk_alpha( float f )
    {
        vkb.setAlpha(f);
    }
        
    /** Creates new form UserMain */
    public UserMain( boolean _is_udp, String _fixed_ip, boolean with_beta, boolean _no_updater) 
    {
        
        haloPicture = null;        
        self = this;       
        is_udp = _is_udp;
        fixed_ip  = _fixed_ip;
        no_updater = _no_updater;

        Dimension auto_size = new Dimension( APPL_X_SIZE, APPL_Y_SIZE );
        this.setSize(auto_size);
        
        init_text_interface(null);

        ResourceInjector.get().inject(this);
        ghaloPicture = haloPicture;
        ggradientTop = gradientTop;
        ggradientBottom = gradientBottom;
        
        initComponents();

        //auto_size = getSize();
        
        if (this.is_touchscreen())
        {
            jLayeredPane1.setPreferredSize(new java.awt.Dimension(APPL_X_SIZE, APPL_Y_SIZE));
            PN_MAIN.setMinimumSize(new java.awt.Dimension(APPL_X_SIZE, APPL_Y_SIZE));
            PN_MAIN.setPreferredSize(new java.awt.Dimension(APPL_X_SIZE, APPL_Y_SIZE));
            PN_USERMAIN.setBounds(0, 0, APPL_X_SIZE, APPL_Y_SIZE);
            PN_GLASS.setPreferredSize(new java.awt.Dimension(APPL_X_SIZE, APPL_Y_SIZE));
            pack();
        }
           
        vkb = new VKeyboard();
                
        


        this.setTitle("JMailClient");
        setIconImage( iconPicture );
        
                
        
//        init_text_interface( l_code );
        
        // GLASSPANE INIT
        setupGlassPane();
        


        x_pos = (int)Main.get_long_prop( Preferences.X_POS, x_pos );                        
        y_pos = (int)Main.get_long_prop( Preferences.Y_POS, y_pos );                        
        
        userLevel = (int)Main.get_long_prop( Preferences.DEFAULT_USER, (long)UL_DUMMY );
        

        /*
        navPanel = new NavigationHeader(this);        
        
        
        titlePanel = new TitlePanel(this);
        
        pn_verwaltung = new PanelVerwaltung( this );
        pn_startup = new PanelStartup(this);
        
        PN_TITLE.add(titlePanel, BorderLayout.NORTH);
        PN_HEADER.add(navPanel, BorderLayout.NORTH);
        titlePanel.installListeners();
        
        
        navPanel.add_button(getString("Suchen"), PBC_SEARCH, pn_startup);
        navPanel.add_button(getString("Verwaltung"), PBC_ADMIN, pn_verwaltung);
        navPanel.enable_button(PBC_ADMIN, true);
        navPanel.add_trail_button(getString("Anmelden"), PBC_LOGIN, null);
        
//        Dimension auto_size = pn_player.getSize();
        
        
        navPanel.setSize(auto_size);
        pn_startup.setSize(auto_size);
        pn_verwaltung.setSize(auto_size);
        
        PN_MAIN.add(navPanel.get_panel_switcher(), BorderLayout.CENTER);
*/
        restart_gui();
        
        
/*        if (!no_updater)
        {
                 //UserMain _main, String _app_name, String _server_path, boolean automatic, boolean _force_upd, boolean _with_gui )
            updateworker = new SRUpdateWorker( this, "sonicremote", Main.SERVER_UPDATEWORKER_PATH + "SR", true, true, true, with_beta );
        }
 * */
        
       // show_busy("Na nu sei mal nich so ungeduldig...");
    }

    @Override
    public void setSize( int width, int height )
    {
        APPL_X_SIZE = width;
        APPL_Y_SIZE = height;
        super.setSize(width, height);
    }



    @Override
    public void setVisible( boolean b )
    {
        super.setVisible( b );
        if (!b)
        {
            Main.get_prefs().set_prop(Preferences.X_POS, Integer.toString(getLocation().x));
            Main.get_prefs().set_prop(Preferences.Y_POS, Integer.toString(getLocation().y));
            Main.get_prefs().store_props();
        
            System.exit(0);
        }
    }

    public void call_pathbutton_code(int code)
    {
        switch( code)
        {
            case PBC_LOGOFF: handle_logoff(); return;
            case PBC_ADMIN: switch_to_admin(); return;
            case PBC_SEARCH: switch_to_search(); return;
        }            
    }
    

    public void restart_gui(String l_code)
    {
        if (navPanel != null)
        {
            PN_MAIN.remove(navPanel.get_panel_switcher());
            PN_HEADER.remove(navPanel);
        }
        if (titlePanel != null)
        {
            titlePanel.removeListeners();
            PN_TITLE.remove(titlePanel);
        }
        // STOP TIMERS
        if (pn_verwaltung != null)
        {
            pn_verwaltung.deactivate_panel();
        }
        // STOP TIMERS
        if (pn_startup != null)
        {
            pn_startup.deactivate_panel();
        }
        
        init_text_interface(l_code);

        
        navPanel = new NavigationHeader(this);        
        pn_startup = new PanelStartup(this);
        titlePanel = new TitlePanel(this);
        
        
        pn_verwaltung = new PanelVerwaltung( this );
        
        PN_TITLE.add(titlePanel, BorderLayout.NORTH);        
        PN_HEADER.add(navPanel, BorderLayout.NORTH);
        titlePanel.installListeners();
        
        
        navPanel.add_button(getString("Suchen"), PBC_SEARCH, pn_startup);
        navPanel.add_button(getString("Verwaltung"), PBC_ADMIN, pn_verwaltung);
        navPanel.enable_button(PBC_ADMIN, true);
        navPanel.add_button(getString("Anmelden"), PBC_LOGIN, null);
        
//        Dimension auto_size = pn_player.getSize();
        Dimension auto_size = this.getSize();
                
        navPanel.setSize(auto_size);
        pn_verwaltung.setSize(auto_size);
        pn_startup.setSize(auto_size);
        PN_USERMAIN.setSize(auto_size);
        PN_GLASS.setSize(auto_size);
        PN_MAIN.setSize(auto_size);
        
        PN_MAIN.add(navPanel.get_panel_switcher(), BorderLayout.CENTER);

        switch_to_search();
        
        
        this.repaint();
        
    }

    public boolean is_touchscreen()
    {
        long ts = Main.get_long_prop( Preferences.TOUCHSCREEN, 0L );           
        return ts > 0 ? true : false;
    }



    private void switch_to_admin()
    {
        navPanel.switch_to_panel( PBC_ADMIN);
        this.repaint();
    }
    private void switch_to_search()
    {
        navPanel.switch_to_panel( PBC_SEARCH);
        this.repaint();
    }

    public int get_act_panel_id()
    {
        return navPanel.get_act_pbc_id();
    }

    
    
    
    
     
   
    private void handle_logoff()
    {
        navPanel.remove_button(PBC_LOGOFF);
        navPanel.enable_button(PBC_ADMIN, false);
        navPanel.add_trail_button(getString("Anmelden"), PBC_LOGIN, null);
        
        
        
        navPanel.update_active_panel();        

    }        


    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jLayeredPane1 = new javax.swing.JLayeredPane();
        PN_USERMAIN = new javax.swing.JPanel();
        PN_TITLE = new javax.swing.JPanel();
        PN_MAIN = new javax.swing.JPanel();
        PN_HEADER = new javax.swing.JPanel();
        PN_GLASS = new org.jdesktop.swingx.JXPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setBackground(new java.awt.Color(0, 0, 0));
        setUndecorated(true);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jLayeredPane1.setPreferredSize(new java.awt.Dimension(872, 604));

        PN_USERMAIN.setBackground(new java.awt.Color(51, 51, 51));
        PN_USERMAIN.setOpaque(false);
        PN_USERMAIN.setLayout(new java.awt.GridBagLayout());

        PN_TITLE.setOpaque(false);
        PN_TITLE.setLayout(new javax.swing.BoxLayout(PN_TITLE, javax.swing.BoxLayout.LINE_AXIS));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 2, 0, 2);
        PN_USERMAIN.add(PN_TITLE, gridBagConstraints);

        PN_MAIN.setBackground(new java.awt.Color(0, 0, 0));
        PN_MAIN.setMinimumSize(new java.awt.Dimension(868, 600));
        PN_MAIN.setOpaque(false);
        PN_MAIN.setPreferredSize(new java.awt.Dimension(868, 600));
        PN_MAIN.setLayout(new java.awt.BorderLayout());

        PN_HEADER.setOpaque(false);
        PN_HEADER.setLayout(new java.awt.BorderLayout());
        PN_MAIN.add(PN_HEADER, java.awt.BorderLayout.NORTH);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 2, 2);
        PN_USERMAIN.add(PN_MAIN, gridBagConstraints);

        PN_USERMAIN.setBounds(0, 0, 872, 604);
        jLayeredPane1.add(PN_USERMAIN, javax.swing.JLayeredPane.DEFAULT_LAYER);

        PN_GLASS.setOpaque(false);
        PN_GLASS.setPaintBorderInsets(false);
        PN_GLASS.setPreferredSize(new java.awt.Dimension(872, 604));
        PN_GLASS.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());
        PN_GLASS.setBounds(0, 0, 872, 604);
        jLayeredPane1.add(PN_GLASS, javax.swing.JLayeredPane.DRAG_LAYER);

        getContentPane().add(jLayeredPane1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formWindowClosed(java.awt.event.WindowEvent evt)//GEN-FIRST:event_formWindowClosed
    {//GEN-HEADEREND:event_formWindowClosed

    }//GEN-LAST:event_formWindowClosed
    
    /**
     * @param args the command line arguments
     */
    /*
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                
                UserMain m = new UserMain(new javax.swing.JFrame(), true);
                m.setVisible(true);                        
                
            }
        });
    }

    */
  
    void close_action()
    {
        setVisible( false );
    }
    
    
    public static boolean errm( JDialog dlg, String str, final int mode, boolean with_cancel, Point p )
    {
        GlossErrDialog errm_dlg;
        /*if (!self.isVisible())
            return false;*/
       
        if (dlg != null)
            errm_dlg = new GlossErrDialog(dlg);
        else    
            errm_dlg = new GlossErrDialog(self);
        
        errm_dlg.setCancel(with_cancel);
        errm_dlg.setText(str);
        errm_dlg.set_mode(mode );
        
        if (p != null)
            errm_dlg.setLocation(p);
        else if (self != null && self.isVisible())
        {
            errm_dlg.setLocation(self.getLocationOnScreen().x + 30, self.getLocationOnScreen().y + 30);
        }
        else
            errm_dlg.setLocationRelativeTo(null);
            
        
        SwingUtilities.invokeLater( new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    InputStream in = null;

                    java.net.URL url = getClass().getResource("/dimm/home/resources/errm_mode_" + mode + ".wav");
                    in = url.openStream();
                    AudioStream as = new AudioStream(in);
                    AudioPlayer.player.start(as);

                } 
                catch (Exception ex)
                {
                } 
            }
        } );
        
        errm_dlg.setVisible(true);
        
        boolean ret = errm_dlg.isOkay();
        
        errm_dlg = null;
        
        return ret;
    }
    
    GlossErrDialog busy_dlg; 
    public void show_busy( JDialog parent, String str, boolean abortable)
    {
        show_busy( parent, str, null, abortable );
    }
    
    public void show_busy( JDialog parent, String str)
    {
        show_busy( parent, str, null, false );
    }
    public void show_busy( String str)
    {
        show_busy( null, str );
    }
    public void show_busy( String str, boolean abortable)
    {
        show_busy( null, str, abortable );
    }
    public void show_busy( JDialog parent, String str,  Point p )
    {
        show_busy( parent, str, p, false );
    }

    public void show_busy( JDialog parent, String str,  Point p, boolean abortable )
    {
       
        if (busy_dlg == null)
        {
            if (parent == null )
                busy_dlg = new GlossErrDialog(this);
            else                
                busy_dlg = new GlossErrDialog(parent);
        }        
        
        busy_dlg.setText(str);
        
        if (!busy_dlg.isVisible())
        {
            busy_dlg.set_mode( abortable ? GlossErrDialog.MODE_BUSY_ABORTABLE : GlossErrDialog.MODE_BUSY );

            if (p != null)
                busy_dlg.setLocation(p);
            else
            {
                if (parent != null && parent.isVisible())
                    busy_dlg.setLocation(parent.getLocationOnScreen().x + 30, parent.getLocationOnScreen().y + 30);
                else if (self.isVisible())
                    busy_dlg.setLocation(self.getLocationOnScreen().x + 30, self.getLocationOnScreen().y + 30);
                else
                    busy_dlg.setLocationRelativeTo(null);
                    
            }

            SwingUtilities.invokeLater( new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        InputStream in = null;

                        java.net.URL url = getClass().getResource("/resources/errm_mode_" + GlossErrDialog.MODE_BUSY + ".wav");
                        in = url.openStream();
                        AudioStream as = new AudioStream(in);
                        AudioPlayer.player.start(as);

                    } 
                    catch (Exception ex)
                    {
                    } 
                }
            } );

            busy_dlg.setVisible(true);    
            busy_dlg.start_radar();
        }
    }
    
    public boolean is_busy_aborted()
    {
        if (busy_dlg != null)
        {
            return busy_dlg.isAborted();
        }
        return false;
    }
    
    public void hide_busy()
    {
        if (busy_dlg != null)
        {
            busy_dlg.stop_radar();
            if (busy_dlg.isVisible())
            {
                busy_dlg.setVisible(false);
                busy_dlg = null;
            }
        }
    }    
    
    public static boolean info_ok_cancel(String txt)
    {
        return info_ok_cancel( null, txt );
    }
     
    public static boolean info_ok_cancel(JDialog dlg, String txt)
    {
        return errm( dlg, txt, GlossErrDialog.MODE_INFO, true, null );
    }
    public static void info_ok(String txt)
    {
        info_ok( null, txt );
    }
    public static void info_ok(JDialog dlg, String txt)
    {
         errm( dlg, txt, GlossErrDialog.MODE_INFO, false, null );
    }
    
    public static boolean info_ok_cancel(String txt, Point p)
    {
        return info_ok_cancel(null, txt, p);
    }
    public static boolean info_ok_cancel(JDialog dlg, String txt, Point p)
    {
        return errm( dlg, txt, GlossErrDialog.MODE_INFO, true, p );
    }
    
    public static void info_ok(String txt, Point p)
    {
        info_ok(null, txt, p);
    }
    public static void info_ok(JDialog dlg, String txt, Point p)
    {
         errm( dlg, txt, GlossErrDialog.MODE_INFO, false, p );
    }
    
    public static boolean errm_ok_cancel(String txt)
    {
        return errm( null, txt, GlossErrDialog.MODE_ERROR, true, null );
    }
    public static boolean errm_ok_cancel(String txt, Point p)
    {
        return errm( null, txt, GlossErrDialog.MODE_ERROR, true, p );
    }
    public static void errm_ok(String txt)
    {
        errm( null, txt, GlossErrDialog.MODE_ERROR, false, null );
    }
    public static void errm_ok(String txt, Point p)
    {
        errm( null, txt, GlossErrDialog.MODE_ERROR, false, p );
    }
    public static boolean errm_ok_cancel(JDialog dlg,String txt)
    {
        return errm( dlg, txt, GlossErrDialog.MODE_ERROR, true, null );
    }
    public static boolean errm_ok_cancel(JDialog dlg,String txt, Point p)
    {
        return errm( dlg, txt, GlossErrDialog.MODE_ERROR, true, p );
    }
    public static void errm_ok(JDialog dlg,String txt)
    {
        errm( dlg, txt, GlossErrDialog.MODE_ERROR, false, null );
    }
    public static void errm_ok(JDialog dlg,String txt, Point p)
    {
        errm( dlg, txt, GlossErrDialog.MODE_ERROR, false, p );
    }
    
    public void debug_msg(int level, String txt)
    {
        if (level >= debug)
            System.out.println( txt );
    }


    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    public org.jdesktop.swingx.JXPanel PN_GLASS;
    public javax.swing.JPanel PN_HEADER;
    public javax.swing.JPanel PN_MAIN;
    public javax.swing.JPanel PN_TITLE;
    public javax.swing.JPanel PN_USERMAIN;
    public javax.swing.JLayeredPane jLayeredPane1;
    // End of variables declaration//GEN-END:variables

    
    
    
    public static String bundle = "dimm/home/SR_Properties";
    
    public static String Txt( String key )
    {
        String str;
        try
        {
            str = java.util.ResourceBundle.getBundle(bundle).getString( key );
        }
        catch ( Exception e )
        {
            System.err.println("Unknown Resourcekey " + key);
            
            return key;
        }
            
        return str;
    }
   


    
}