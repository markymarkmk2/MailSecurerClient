/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

import dimm.home.UserMain;
import java.awt.Color;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import org.jdesktop.fuse.ResourceInjector;
import org.jdesktop.fuse.swing.SwingModule;



/**
 *
 * @author Administrator
 */
public class UI_Pirates extends UI_Generic
{

    public static final Color nice_white = new Color( 199,199,199);
    public static final Color nice_gray = new Color( 100,100,100);
    public static final Color appl_base_color = new Color( 208,82,0);
    public static final Color appl_selected_color = appl_base_color;
    public static final Color appl_dgray = new Color( 51,51,51);
    public static final Font small_font = new Font("Tahoma", Font.PLAIN, 11 );

    @Override
    public Color get_table_header_color()
    {
        return appl_base_color;
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
        return nice_white;
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
    public String toString()
    {
        return "Pirate";
    }
    
    
    @Override
    public void set_ui(boolean verbose)
    {
        try
        {
            if (verbose)
                uidftest();
           
            
            ResourceInjector.addModule(new SwingModule());
            ResourceInjector.get().load(UserMain.class, "/dimm/home/resources/mc_black.uitheme");
            
            //EtchedBorder b = (EtchedBorder)BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
            EtchedBorder etched_border = (EtchedBorder)BorderFactory.createEtchedBorder( new Color( 0x44, 0x44, 0x44 ), new Color( 0x26, 0x26, 0x26 ));
            
            Border empty_border = BorderFactory.createEmptyBorder();

            MyButtonUI bt_ui = new MyButtonUI();

            
            UIManager.put("Button.background", new ColorUIResource(appl_dgray) );
            UIManager.put("Button.foreground", new ColorUIResource(nice_white) );
            UIManager.put("Button.light", new ColorUIResource(nice_gray) );
            UIManager.put("Button.highlight", new ColorUIResource(nice_gray) );
  /*          UIManager.put("Button.border", etched_border );
            UIManager.put("Button.foreground", new ColorUIResource(Color.black) );
            UIManager.put("Button.font", new FontUIResource(small_font));
*/
            Object o = UIManager.get( "ButtonUI");
            UIManager.put( "ButtonUI", bt_ui.getClass().getName() );



            UIManager.put("Label.font", new FontUIResource(small_font));
            UIManager.put("Label.background", new ColorUIResource(appl_dgray) );
            UIManager.put("Label.foreground", new ColorUIResource(nice_white) );

            UIManager.put("List.font", new FontUIResource(small_font));
            UIManager.put("List.background", new ColorUIResource(appl_dgray) );
            UIManager.put("List.foreground", new ColorUIResource(nice_white) );
            UIManager.put("List.selectionBackground", new ColorUIResource(appl_dgray));
            UIManager.put("List.selectionForeground", new ColorUIResource(appl_selected_color));
            UIManager.put("List.border", etched_border );
           

            UIManager.put("PasswordField.background", new ColorUIResource(appl_dgray) );
            UIManager.put("PasswordField.foreground", new ColorUIResource(nice_white) );
            UIManager.put("PasswordField.inactiveBackground", new ColorUIResource(appl_dgray) );
            UIManager.put("PasswordField.disabledBackground",new ColorUIResource(appl_dgray) );
            UIManager.put("PasswordField.inactiveForeground", new ColorUIResource(nice_white) );
            UIManager.put("PasswordField.selectionBackground", new ColorUIResource(appl_dgray));
            UIManager.put("PasswordField.selectionForeground", new ColorUIResource(appl_selected_color));
            UIManager.put("PasswordField.selectionForeground", new ColorUIResource(appl_selected_color));
            UIManager.put("PasswordField.caretForeground", new ColorUIResource(appl_selected_color));
            UIManager.put("PasswordField.border", etched_border);
            
            UIManager.put("TextField.background", new ColorUIResource(appl_dgray) );
            UIManager.put("TextField.foreground", new ColorUIResource(nice_white) );
            UIManager.put("TextField.inactiveBackground", new ColorUIResource(appl_dgray) );
            UIManager.put("TextField.disabledBackground",new ColorUIResource(appl_dgray) );
            UIManager.put("TextField.inactiveForeground", new ColorUIResource(nice_white) );
            UIManager.put("TextField.selectionBackground", new ColorUIResource(appl_dgray));
            UIManager.put("TextField.selectionForeground", new ColorUIResource(appl_selected_color));
            UIManager.put("TextField.caretForeground", new ColorUIResource(appl_selected_color));
            UIManager.put("TextField.font", new FontUIResource(small_font));
            UIManager.put("TextField.border", etched_border);

            UIManager.put("FormattedTextField.background", new ColorUIResource(appl_dgray) );
            UIManager.put("FormattedTextField.foreground", new ColorUIResource(nice_white) );
            UIManager.put("FormattedTextField.inactiveBackground", new ColorUIResource(appl_dgray) );
            UIManager.put("FormattedTextField.disabledBackground",new ColorUIResource(appl_dgray) );
            UIManager.put("FormattedTextField.inactiveForeground", new ColorUIResource(nice_white) );
            UIManager.put("FormattedTextField.selectionBackground", new ColorUIResource(appl_dgray));
            UIManager.put("FormattedTextField.selectionForeground", new ColorUIResource(appl_selected_color));
            UIManager.put("FormattedTextField.caretForeground", new ColorUIResource(appl_selected_color));
            UIManager.put("FormattedTextField.font", new FontUIResource(small_font));
            UIManager.put("FormattedTextField.border", etched_border);

            UIManager.put("TextArea.background", new ColorUIResource(appl_dgray) );
            UIManager.put("TextArea.foreground", new ColorUIResource(nice_white) );
            UIManager.put("TextArea.inactiveBackground", new ColorUIResource(appl_dgray) );
            UIManager.put("TextArea.disabledBackground",new ColorUIResource(appl_dgray) );
            UIManager.put("TextArea.inactiveForeground", new ColorUIResource(nice_white) );
            UIManager.put("TextArea.selectionBackground", new ColorUIResource(appl_dgray));
            UIManager.put("TextArea.selectionForeground", new ColorUIResource(appl_selected_color));
            UIManager.put("TextArea.caretForeground", new ColorUIResource(appl_selected_color));
            UIManager.put("TextArea.font", new FontUIResource(small_font));
            UIManager.put("TextArea.border", etched_border);

            UIManager.put("TabbedPane.background", new ColorUIResource(appl_dgray) );
            UIManager.put("TabbedPane.foreground", new ColorUIResource(nice_white) );
            UIManager.put("TabbedPane.highlight", new ColorUIResource(nice_gray) );
            UIManager.put("TabbedPane.shadow", new ColorUIResource(appl_dgray) );
            UIManager.put("TabbedPane.contentOpaque", false );
            UIManager.put("TabbedPane.tabsOpaque", false );
            
 
            UIManager.put("ComboBox.background", new ColorUIResource(appl_dgray) );
            UIManager.put("ComboBox.foreground", new ColorUIResource(nice_white) );
            
            UIManager.put("ComboBox.font", new FontUIResource(small_font));
            UIManager.put("ComboBox.selectionBackground", new ColorUIResource(appl_dgray));
            UIManager.put("ComboBox.selectionForeground", new ColorUIResource(appl_selected_color));
            UIManager.put("ComboBox.disabledBackground", new ColorUIResource(appl_dgray));
            UIManager.put("ComboBox.disabledForeground", new ColorUIResource(nice_white));
            
            UIManager.put("ComboBox.buttonHighlight", new ColorUIResource(appl_dgray) );
            UIManager.put("ComboBox.buttonShadow", new ColorUIResource(appl_dgray)); 
            
            UIManager.put("ComboBox.buttonBackground", new ColorUIResource(appl_dgray)); 
            UIManager.put("ComboBox.buttonDarkShadow", new ColorUIResource(nice_gray)); 
            
         
            UIManager.put("ComboBox.border", etched_border);

            UIManager.put("ScrollBar.background", appl_dgray);
            UIManager.put("ScrollBar.foreground", nice_gray);
            UIManager.put("ScrollBar.track", appl_dgray);
            UIManager.put("ScrollBar.trackHighlight", appl_dgray);
            UIManager.put("ScrollBar.thumb", nice_gray);
            UIManager.put("ScrollBar.thumbHighlight", nice_gray);
            UIManager.put("ScrollBar.thumbDarkShadow", appl_dgray);
            UIManager.put("ScrollBar.thumbLightShadow", nice_gray);   
            UIManager.put("ScrollBar.width", new Integer(11) );            
            UIManager.put("ScrollPane.background", appl_dgray );
            UIManager.put("ScrollPane.foreground", nice_gray );
            UIManager.put("ScrollPane.border", empty_border);        
            UIManager.put("ScrollPane.viewportBorder", empty_border);        
            
            UIManager.put("Slider.border", empty_border );
            
            UIManager.put("Spinner.background", appl_dgray);
            UIManager.put("Spinner.foreground", nice_gray);
            UIManager.put("Spinner.border", etched_border);
            
            UIManager.put("Table.border", etched_border );
            
            UIManager.put("CheckBoxMenuItem.background", new ColorUIResource(appl_dgray) );     
            
            Border tb = BorderFactory.createLineBorder(nice_gray);
            UIManager.put("TitledBorder.border", tb );
            UIManager.put("TitledBorder.titleColor", new ColorUIResource(nice_white) );
            
            UIManager.put("Panel.background", new ColorUIResource(appl_dgray) );     
            UIManager.put("Desktop.background", new ColorUIResource(appl_dgray) );     
            UIManager.put("Viewport.background", new ColorUIResource(appl_dgray) );     
            
            
            UIManager.put("CheckBox.background", new ColorUIResource(appl_dgray) );
            UIManager.put("CheckBox.foreground", new ColorUIResource(nice_white) );
            UIManager.put("CheckBox.interiorBackground", new ColorUIResource(appl_dgray) );
            UIManager.put("CheckBox.opaque", false );
            UIManager.put("RadioButton.background", new ColorUIResource(appl_dgray) );
            UIManager.put("RadioButton.foreground", new ColorUIResource(nice_white) );
            UIManager.put("RadioButton.interiorBackground", new ColorUIResource(appl_dgray) );
            UIManager.put("RadioButton.opaque", false );
            
            UIManager.put("desktop", new ColorUIResource(appl_dgray) );
            UIManager.put("control", new ColorUIResource(appl_dgray) );
            UIManager.put("activeCaption", new ColorUIResource(appl_dgray) );
            UIManager.put("ControlLtHighlight", new ColorUIResource(appl_dgray) );
            UIManager.put("ControlShadow", new ColorUIResource(appl_dgray) );
            UIManager.put("info", new ColorUIResource(appl_dgray) );
            UIManager.put("menu", new ColorUIResource(appl_dgray) );
            UIManager.put("text", new ColorUIResource(appl_dgray) );
            UIManager.put("window", new ColorUIResource(appl_dgray) );
            
          /*  UIManager.put("CheckBoxMenuItem.arrowIcon", new ColorUIResource(appl_dgray) );
            UIManager.put("RadioButton.background", new ColorUIResource(appl_dgray) );
            UIManager.put("RadioButton.foreground", new ColorUIResource(nice_white) );*/
            
            UIManager.put("OptionPane.background", new ColorUIResource(appl_dgray) );
            UIManager.put("OptionPane.foreground", new ColorUIResource(nice_white) );
            UIManager.put("OptionPane.messageForeground", new ColorUIResource(nice_white) );
/*
key: OptionPane.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: OptionPane.border        val: javax.swing.plaf.BorderUIResource$EmptyBorderUIResource@1f0aecc
 key: OptionPane.buttonAreaBorder        val: javax.swing.plaf.BorderUIResource$EmptyBorderUIResource@1f1680f
 key: OptionPane.buttonClickThreshhold        val: 500
 key: OptionPane.buttonFont        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: OptionPane.buttonMinimumWidth        val: 75
 key: OptionPane.errorIcon        val: javax.swing.ImageIcon@11c0d60
 key: OptionPane.errorSound        val: win.sound.hand
 key: OptionPane.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: OptionPane.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: OptionPane.informationIcon        val: javax.swing.ImageIcon@147917a
 key: OptionPane.informationSound        val: win.sound.asterisk
 key: OptionPane.messageAreaBorder        val: javax.swing.plaf.BorderUIResource$EmptyBorderUIResource@1284fd4
 key: OptionPane.messageFont        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: OptionPane.messageForeground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: OptionPane.minimumSize        val: javax.swing.plaf.DimensionUIResource[width=262,height=90]
 key: OptionPane.questionIcon        val: javax.swing.ImageIcon@1eb5666
 key: OptionPane.questionSound        val: win.sound.question
 key: OptionPane.warningIcon        val: javax.swing.ImageIcon@14275d4
 key: OptionPane.warningSound        val: win.sound.exclamation
 key: OptionPane.windowBindings        val: [Ljava.lang.Object;@1d0d124
            */
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
        return true;
    }

    @Override
    public boolean has_rendered_panels()
    {
        return true;
    }



}

