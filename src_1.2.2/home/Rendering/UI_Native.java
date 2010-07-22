/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

import dimm.home.UserMain;
import java.awt.Color;
import java.awt.Font;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.jdesktop.fuse.ResourceInjector;
import org.jdesktop.fuse.swing.SwingModule;



/**
 *
 * @author Administrator
 */
public class UI_Native extends UI_Generic
{

    private static final Color foreground = new Color( 0,0,0);
    private static final Color nice_gray = new Color( 200,200,200);
    private static final Color table_header_color = new Color( 0,0,0);
    private static final Color appl_selected_color = new Color( 208,82,0);
    private static final Color appl_dgray = new Color( 241,241,241);
    private static final Font small_font = new Font("Ariel", Font.PLAIN, 11 );

    public UI_Native()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException classNotFoundException)
        {
        }
        catch (InstantiationException instantiationException)
        {
        }
        catch (IllegalAccessException illegalAccessException)
        {
        }
        catch (UnsupportedLookAndFeelException unsupportedLookAndFeelException)
        {
        }
    }



    @Override
    public Color get_table_header_color()
    {
        return table_header_color;
    }

    @Override
    public Color get_selected_color()
    {
        return appl_selected_color;
    }

    @Override
    public Color get_background()
    {
        return appl_dgray;
    }

    @Override
    public Color get_foreground()
    {
        return foreground;
    }

    @Override
    public Color get_nice_gray()
    {
        return nice_gray;
    }

    @Override
    public Font get_small_font()
    {
        return small_font;
    }
    
    
    
    @Override
    public void set_ui(boolean verbose)
    {
        try
        {
            if (verbose)
                uidftest();
           
            
            ResourceInjector.addModule(new SwingModule());
            ResourceInjector.get().load(UserMain.class, "/dimm/home/resources/mc_native.uitheme");

        
            UIManager.put("Button.font", small_font );
            UIManager.put("Label.font", small_font );
            UIManager.put("CheckBox.font", small_font );
            UIManager.put("ComboBox.font", small_font );
            UIManager.put("TextArea.font", small_font );
            UIManager.put("TextField.font", small_font );
            UIManager.put("RadioButton.font", small_font );
            UIManager.put("PasswordField.font", small_font );
            UIManager.put("TabbedPane.font", small_font );
            UIManager.put("Spinner.font", small_font );
            UIManager.put("Table.font", small_font );
            UIManager.put("TableHeader.font", small_font );


        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
        if (verbose)
            uidftest();        
    
    }
    @Override
    public boolean has_rendered_button()
    {
        return false;
    }

    @Override
    public boolean has_rendered_panels()
    {
        return false;
    }



}

