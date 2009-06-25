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
public class UI_Bluesea extends UI_Generic
{

    public static final Color nice_white = new Color( 199,199,199);
    public static final Color nice_gray = new Color( 100,100,100);
    public static final Color appl_base_color = new Color( 30,130,220);
    public static final Color appl_selected_color = appl_base_color;
    public static final Color appl_dgray = new Color( 51,51,51);
    public static final Font small_font = new Font("Tahoma", Font.PLAIN, 11 );

    @Override
    public Color get_appl_base_color()
    {
        return appl_base_color;
    }

    @Override
    public Color get_appl_selected_color()
    {
        return appl_selected_color;
    }

    @Override
    public Color get_appl_dgray()
    {
        return appl_dgray;
    }

    @Override
    public Color get_nice_white()
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
    public void set_ui(boolean verbose)
    {
        try
        {
            if (verbose)
                uidftest();
           
            
            ResourceInjector.addModule(new SwingModule());
            ResourceInjector.get().load(UserMain.class, "/dimm/home/resources/mc_blue.uitheme");
            
            //EtchedBorder b = (EtchedBorder)BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
            EtchedBorder b = (EtchedBorder)BorderFactory.createEtchedBorder( new Color( 0x44, 0x44, 0x44 ), new Color( 0x26, 0x26, 0x26 ));
            
            Border empty_border = BorderFactory.createEmptyBorder();
            
            UIManager.put("Button.background", new ColorUIResource(appl_dgray) );
            UIManager.put("Button.foreground", new ColorUIResource(nice_white) );
            UIManager.put("Button.light", new ColorUIResource(nice_gray) );
            UIManager.put("Button.highlight", new ColorUIResource(nice_gray) );
            UIManager.put("Button.border", b );
            

            UIManager.put("Label.font", new FontUIResource(small_font));
            UIManager.put("Label.background", new ColorUIResource(appl_dgray) );
            UIManager.put("Label.foreground", new ColorUIResource(nice_white) );

            UIManager.put("List.font", new FontUIResource(small_font));
            UIManager.put("List.background", new ColorUIResource(appl_dgray) );
            UIManager.put("List.foreground", new ColorUIResource(nice_white) );
            UIManager.put("List.selectionBackground", new ColorUIResource(appl_dgray));
            UIManager.put("List.selectionForeground", new ColorUIResource(appl_selected_color));
            UIManager.put("List.border", b );
           

            UIManager.put("PasswordField.background", new ColorUIResource(appl_dgray) );
            UIManager.put("PasswordField.foreground", new ColorUIResource(nice_white) );
            UIManager.put("PasswordField.selectionForeground", new ColorUIResource(appl_selected_color));
            UIManager.put("PasswordField.caretForeground", new ColorUIResource(appl_selected_color));
            UIManager.put("PasswordField.border", b);        
            
            UIManager.put("TextField.background", new ColorUIResource(appl_dgray) );
            UIManager.put("TextField.foreground", new ColorUIResource(nice_white) );
            UIManager.put("TextField.inactiveBackground", new ColorUIResource(appl_dgray) );
            UIManager.put("TextField.disabledBackground",new ColorUIResource(appl_dgray) );
            UIManager.put("TextField.inactiveForeground", new ColorUIResource(nice_white) );
            UIManager.put("TextField.selectionBackground", new ColorUIResource(appl_dgray));
            UIManager.put("TextField.selectionForeground", new ColorUIResource(appl_selected_color));
            UIManager.put("TextField.caretForeground", new ColorUIResource(appl_selected_color));
            UIManager.put("TextField.font", new FontUIResource(small_font));
            UIManager.put("TextField.border", b);     
            
            UIManager.put("TextArea.background", new ColorUIResource(appl_dgray) );
            UIManager.put("TextArea.foreground", new ColorUIResource(nice_white) );
            UIManager.put("TextArea.inactiveBackground", new ColorUIResource(appl_dgray) );
            UIManager.put("TextArea.disabledBackground",new ColorUIResource(appl_dgray) );
            UIManager.put("TextArea.inactiveForeground", new ColorUIResource(nice_white) );
            UIManager.put("TextArea.selectionBackground", new ColorUIResource(appl_dgray));
            UIManager.put("TextArea.selectionForeground", new ColorUIResource(appl_selected_color));
            UIManager.put("TextArea.caretForeground", new ColorUIResource(appl_selected_color));
            UIManager.put("TextArea.font", new FontUIResource(small_font));
            UIManager.put("TextArea.border", b);     

            UIManager.put("TabbedPane.background", new ColorUIResource(appl_dgray) );
            UIManager.put("TabbedPane.foreground", new ColorUIResource(nice_white) );
            UIManager.put("TabbedPane.highlight", new ColorUIResource(nice_white) );
            UIManager.put("TabbedPane.shadow", new ColorUIResource(appl_dgray) );
            UIManager.put("TabbedPane.contentOpaque", false );
            UIManager.put("TabbedPane.tabsOpaque", false );
            
  /*          
TabbedPane.background	val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: TabbedPane.contentBorderInsets	val: javax.swing.plaf.InsetsUIResource[top=2,left=2,bottom=3,right=3]
 key: TabbedPane.contentOpaque	val: true
 key: TabbedPane.darkShadow	val: javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]
 key: TabbedPane.focus	val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: TabbedPane.focusInputMap	val: javax.swing.plaf.InputMapUIResource@1c5ddd3
 key: TabbedPane.font	val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: TabbedPane.foreground	val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: TabbedPane.highlight	val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: TabbedPane.light	val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: TabbedPane.selectedTabPadInsets	val: javax.swing.plaf.InsetsUIResource[top=2,left=2,bottom=2,right=1]
 key: TabbedPane.shadow	val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: TabbedPane.tabAreaInsets	val: javax.swing.plaf.InsetsUIResource[top=3,left=2,bottom=0,right=2]
 key: TabbedPane.tabInsets	val: javax.swing.plaf.InsetsUIResource[top=0,left=4,bottom=1,right=4]
 key: TabbedPane.tabRunOverlay	val: 2
 key: TabbedPane.tabsOpaque	val: true
 key: TabbedPane.tabsOverlapBorder	val: false
 key: TabbedPane.textIconGap	val: 4
            
*/
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
            
 /*key: ComboBox.buttonBackground	val: javax.swing.plaf.ColorUIResource[r=51,g=51,b=51]
 key: ComboBox.buttonDarkShadow	val: javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]
 key: ComboBox.buttonHighlight	val: javax.swing.plaf.ColorUIResource[r=199,g=199,b=199]
 key: ComboBox.buttonShadow	val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
  */          
            UIManager.put("ComboBox.border", b);        

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
            UIManager.put("Spinner.border", b);        
            
            UIManager.put("Table.border", b );
            
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
            
          /*  UIManager.put("CheckBoxMenuItem.arrowIcon", new ColorUIResource(appl_dgray) );
            UIManager.put("RadioButton.background", new ColorUIResource(appl_dgray) );
            UIManager.put("RadioButton.foreground", new ColorUIResource(nice_white) );*/
            
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
        if (verbose)
            uidftest();        
    
    }


}
