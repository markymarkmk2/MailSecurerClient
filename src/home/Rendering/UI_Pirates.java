/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

import dimm.home.UserMain;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.ComponentUI;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.basic.BasicButtonUI;
import org.jdesktop.fuse.ResourceInjector;
import org.jdesktop.fuse.swing.SwingModule;


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
            
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
        if (verbose)
            uidftest();        
    
    }


}

/*
 *
 * key: AATextInfoPropertyKey        val: sun.swing.SwingUtilities2$AATextInfo@1cb7a1
 key: Application.useSystemFontSettings        val: true
 key: AuditoryCues.allAuditoryCues        val: [Ljava.lang.Object;@165b7e
 key: AuditoryCues.cueList        val: [Ljava.lang.Object;@165b7e
 key: AuditoryCues.noAuditoryCues        val: [Ljava.lang.Object;@ffd135
 key: Button.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: Button.border        val: javax.swing.plaf.BorderUIResource$CompoundBorderUIResource@1eb904d
 key: Button.darkShadow        val: javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]
 key: Button.dashedRectGapHeight        val: 8
 key: Button.dashedRectGapWidth        val: 10
 key: Button.dashedRectGapX        val: 5
 key: Button.dashedRectGapY        val: 4
 key: Button.defaultButtonFollowsFocus        val: true
 key: Button.disabledForeground        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: Button.disabledShadow        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: Button.focus        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: Button.focusInputMap        val: javax.swing.plaf.InputMapUIResource@15fb38
 key: Button.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: Button.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: Button.highlight        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: Button.light        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: Button.margin        val: javax.swing.plaf.InsetsUIResource[top=2,left=14,bottom=2,right=14]
 key: Button.shadow        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: Button.showMnemonics        val: false
 key: Button.textIconGap        val: 4
 key: Button.textShiftOffset        val: 1
 key: ButtonUI        val: com.sun.java.swing.plaf.windows.WindowsButtonUI
 key: CheckBox.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: CheckBox.border        val: javax.swing.plaf.BorderUIResource$CompoundBorderUIResource@17b4703
 key: CheckBox.darkShadow        val: javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]
 key: CheckBox.focus        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: CheckBox.focusInputMap        val: javax.swing.plaf.InputMapUIResource@108f8e0
 key: CheckBox.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: CheckBox.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: CheckBox.highlight        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: CheckBox.icon        val: com.sun.java.swing.plaf.windows.WindowsIconFactory$CheckBoxIcon@103de90
 key: CheckBox.interiorBackground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: CheckBox.light        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: CheckBox.margin        val: javax.swing.plaf.InsetsUIResource[top=2,left=2,bottom=2,right=2]
 key: CheckBox.shadow        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: CheckBox.textIconGap        val: 4
 key: CheckBox.textShiftOffset        val: 0
 key: CheckBox.totalInsets        val: java.awt.Insets[top=4,left=4,bottom=4,right=4]
 key: CheckBoxMenuItem.acceleratorFont        val: javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
 key: CheckBoxMenuItem.acceleratorForeground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: CheckBoxMenuItem.acceleratorSelectionForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: CheckBoxMenuItem.arrowIcon        val: javax.swing.plaf.basic.BasicIconFactory$MenuItemArrowIcon@1827d1
 key: CheckBoxMenuItem.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: CheckBoxMenuItem.border        val: javax.swing.plaf.basic.BasicBorders$MarginBorder@70be88
 key: CheckBoxMenuItem.borderPainted        val: false
 key: CheckBoxMenuItem.checkIcon        val: javax.swing.plaf.basic.BasicIconFactory$CheckBoxMenuItemIcon@238016
 key: CheckBoxMenuItem.commandSound        val: win.sound.menuCommand
 key: CheckBoxMenuItem.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: CheckBoxMenuItem.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: CheckBoxMenuItem.margin        val: javax.swing.plaf.InsetsUIResource[top=2,left=2,bottom=2,right=2]
 key: CheckBoxMenuItem.selectionBackground        val: javax.swing.plaf.ColorUIResource[r=10,g=36,b=106]
 key: CheckBoxMenuItem.selectionForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: CheckBoxMenuItemUI        val: com.sun.java.swing.plaf.windows.WindowsCheckBoxMenuItemUI
 key: CheckBoxUI        val: com.sun.java.swing.plaf.windows.WindowsCheckBoxUI
 key: ColorChooser.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: ColorChooser.font        val: javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
 key: ColorChooser.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: ColorChooser.swatchesDefaultRecentColor        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: ColorChooser.swatchesRecentSwatchSize        val: java.awt.Dimension[width=10,height=10]
 key: ColorChooser.swatchesSwatchSize        val: java.awt.Dimension[width=10,height=10]
 key: ColorChooserUI        val: javax.swing.plaf.basic.BasicColorChooserUI
 key: ComboBox.ancestorInputMap        val: javax.swing.plaf.InputMapUIResource@c6f734
 key: ComboBox.background        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: ComboBox.border        val: javax.swing.plaf.basic.BasicBorders$FieldBorder@19c0bd6
 key: ComboBox.buttonBackground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: ComboBox.buttonDarkShadow        val: javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]
 key: ComboBox.buttonHighlight        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: ComboBox.buttonShadow        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: ComboBox.disabledBackground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: ComboBox.disabledForeground        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: ComboBox.editorBorder        val: javax.swing.border.EmptyBorder@19e11a1
 key: ComboBox.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: ComboBox.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: ComboBox.isEnterSelectablePopup        val: false
 key: ComboBox.selectionBackground        val: javax.swing.plaf.ColorUIResource[r=10,g=36,b=106]
 key: ComboBox.selectionForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: ComboBox.timeFactor        val: 1000
 key: ComboBoxUI        val: com.sun.java.swing.plaf.windows.WindowsComboBoxUI
 key: Desktop.ancestorInputMap        val: javax.swing.plaf.InputMapUIResource@130633a
 key: Desktop.background        val: javax.swing.plaf.ColorUIResource[r=58,g=110,b=165]
 key: Desktop.minOnScreenInsets        val: javax.swing.plaf.InsetsUIResource[top=3,left=3,bottom=3,right=3]
 key: DesktopIcon.border        val: javax.swing.plaf.BorderUIResource$CompoundBorderUIResource@1d2b9b7
 key: DesktopIcon.width        val: 160
 key: DesktopIconUI        val: com.sun.java.swing.plaf.windows.WindowsDesktopIconUI
 key: DesktopPaneUI        val: com.sun.java.swing.plaf.windows.WindowsDesktopPaneUI
 key: EditorPane.background        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: EditorPane.border        val: javax.swing.plaf.basic.BasicBorders$MarginBorder@1a7b0bf
 key: EditorPane.caretBlinkRate        val: 500
 key: EditorPane.caretForeground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: EditorPane.disabledBackground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: EditorPane.focusInputMap        val: javax.swing.plaf.InputMapUIResource@1c18a4c
 key: EditorPane.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: EditorPane.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: EditorPane.inactiveBackground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: EditorPane.inactiveForeground        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: EditorPane.margin        val: javax.swing.plaf.InsetsUIResource[top=3,left=3,bottom=3,right=3]
 key: EditorPane.selectionBackground        val: javax.swing.plaf.ColorUIResource[r=10,g=36,b=106]
 key: EditorPane.selectionForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: EditorPaneUI        val: com.sun.java.swing.plaf.windows.WindowsEditorPaneUI
 key: FileChooser.ancestorInputMap        val: javax.swing.plaf.InputMapUIResource@da90c
 key: FileChooser.detailsViewIcon        val: javax.swing.ImageIcon@1000bcf
 key: FileChooser.fileNameLabelMnemonic        val: 78
 key: FileChooser.filesOfTypeLabelMnemonic        val: 84
 key: FileChooser.homeFolderIcon        val: sun.swing.ImageIconUIResource@22d166
 key: FileChooser.listFont        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: FileChooser.listViewBackground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: FileChooser.listViewBorder        val: javax.swing.plaf.BorderUIResource$BevelBorderUIResource@e4bb3c
 key: FileChooser.listViewIcon        val: javax.swing.ImageIcon@c8769b
 key: FileChooser.listViewWindowsStyle        val: true
 key: FileChooser.lookInLabelMnemonic        val: 73
 key: FileChooser.newFolderIcon        val: javax.swing.ImageIcon@d58939
 key: FileChooser.noPlacesBar        val: false
 key: FileChooser.readOnly        val: false
 key: FileChooser.upFolderIcon        val: javax.swing.ImageIcon@d90453
 key: FileChooser.useSystemExtensionHiding        val: true
 key: FileChooser.usesSingleFilePane        val: true
 key: FileChooserUI        val: com.sun.java.swing.plaf.windows.WindowsFileChooserUI
 key: FileView.computerIcon        val: sun.swing.ImageIconUIResource@167e3a5
 key: FileView.directoryIcon        val: sun.swing.ImageIconUIResource@457d21
 key: FileView.fileIcon        val: sun.swing.ImageIconUIResource@5c3987
 key: FileView.floppyDriveIcon        val: sun.swing.ImageIconUIResource@5a3923
 key: FileView.hardDriveIcon        val: sun.swing.ImageIconUIResource@6963d0
 key: FormattedTextField.background        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: FormattedTextField.border        val: javax.swing.plaf.basic.BasicBorders$FieldBorder@6458a6
 key: FormattedTextField.caretBlinkRate        val: 500
 key: FormattedTextField.caretForeground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: FormattedTextField.disabledBackground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: FormattedTextField.focusInputMap        val: javax.swing.plaf.InputMapUIResource@65b738
 key: FormattedTextField.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: FormattedTextField.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: FormattedTextField.inactiveBackground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: FormattedTextField.inactiveForeground        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: FormattedTextField.margin        val: javax.swing.plaf.InsetsUIResource[top=1,left=1,bottom=1,right=1]
 key: FormattedTextField.selectionBackground        val: javax.swing.plaf.ColorUIResource[r=10,g=36,b=106]
 key: FormattedTextField.selectionForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: FormattedTextFieldUI        val: javax.swing.plaf.basic.BasicFormattedTextFieldUI
 key: InternalFrame.activeBorderColor        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: InternalFrame.activeTitleBackground        val: javax.swing.plaf.ColorUIResource[r=10,g=36,b=106]
 key: InternalFrame.activeTitleForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: InternalFrame.activeTitleGradient        val: javax.swing.plaf.ColorUIResource[r=166,g=202,b=240]
 key: InternalFrame.border        val: javax.swing.plaf.BorderUIResource$CompoundBorderUIResource@5e222e
 key: InternalFrame.borderColor        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: InternalFrame.borderDarkShadow        val: javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]
 key: InternalFrame.borderHighlight        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: InternalFrame.borderLight        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: InternalFrame.borderShadow        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: InternalFrame.borderWidth        val: 1
 key: InternalFrame.closeIcon        val: com.sun.java.swing.plaf.windows.WindowsIconFactory$FrameButtonIcon@c5aa00
 key: InternalFrame.closeSound        val: win.sound.close
 key: InternalFrame.icon        val: com.sun.java.swing.plaf.windows.WindowsInternalFrameTitlePane$ScalableIconUIResource@143bf3d
 key: InternalFrame.iconifyIcon        val: com.sun.java.swing.plaf.windows.WindowsIconFactory$FrameButtonIcon@3a1834
 key: InternalFrame.inactiveBorderColor        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: InternalFrame.inactiveTitleBackground        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: InternalFrame.inactiveTitleForeground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: InternalFrame.inactiveTitleGradient        val: javax.swing.plaf.ColorUIResource[r=192,g=192,b=192]
 key: InternalFrame.layoutTitlePaneAtOrigin        val: false
 key: InternalFrame.maximizeIcon        val: com.sun.java.swing.plaf.windows.WindowsIconFactory$FrameButtonIcon@2b249
 key: InternalFrame.maximizeSound        val: win.sound.maximize
 key: InternalFrame.minimizeIcon        val: com.sun.java.swing.plaf.windows.WindowsIconFactory$FrameButtonIcon@159e154
 key: InternalFrame.minimizeIconBackground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: InternalFrame.minimizeSound        val: win.sound.minimize
 key: InternalFrame.resizeIconHighlight        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: InternalFrame.resizeIconShadow        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: InternalFrame.restoreDownSound        val: win.sound.restoreDown
 key: InternalFrame.restoreUpSound        val: win.sound.restoreUp
 key: InternalFrame.titleButtonHeight        val: 18
 key: InternalFrame.titleButtonToolTipsOn        val: true
 key: InternalFrame.titleButtonWidth        val: 18
 key: InternalFrame.titleFont        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=bold,size=11]
 key: InternalFrame.titlePaneHeight        val: 18
 key: InternalFrame.windowBindings        val: [Ljava.lang.Object;@10c0f66
 key: InternalFrameTitlePane.closeButtonOpacity        val: true
 key: InternalFrameTitlePane.iconifyButtonOpacity        val: true
 key: InternalFrameTitlePane.maximizeButtonOpacity        val: true
 key: InternalFrameUI        val: com.sun.java.swing.plaf.windows.WindowsInternalFrameUI
 key: Label.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: Label.disabledForeground        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: Label.disabledShadow        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: Label.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: Label.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: LabelUI        val: com.sun.java.swing.plaf.windows.WindowsLabelUI
 key: List.background        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: List.cellRenderer        val: javax.swing.DefaultListCellRenderer$UIResource[List.cellRenderer,0,0,0x0,invalid,alignmentX=0.0,alignmentY=0.0,border=javax.swing.border.EmptyBorder@4c6320,flags=25165832,maximumSize=,minimumSize=,preferredSize=,defaultIcon=,disabledIcon=,horizontalAlignment=LEADING,horizontalTextPosition=TRAILING,iconTextGap=4,labelFor=,text=,verticalAlignment=CENTER,verticalTextPosition=CENTER]
 key: List.dropLineColor        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: List.focusCellHighlightBorder        val: com.sun.java.swing.plaf.windows.WindowsBorders$ComplementDashedBorder@1e6e305
 key: List.focusInputMap        val: javax.swing.plaf.InputMapUIResource@185ad79
 key: List.focusInputMap.RightToLeft        val: javax.swing.plaf.InputMapUIResource@8acfc3
 key: List.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: List.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: List.lockToPositionOnScroll        val: true
 key: List.selectionBackground        val: javax.swing.plaf.ColorUIResource[r=10,g=36,b=106]
 key: List.selectionForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: List.timeFactor        val: 1000
 key: ListUI        val: javax.swing.plaf.basic.BasicListUI
 key: Menu.acceleratorFont        val: javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
 key: Menu.acceleratorForeground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: Menu.acceleratorSelectionForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: Menu.arrowIcon        val: com.sun.java.swing.plaf.windows.WindowsIconFactory$MenuArrowIcon@1cc0a7f
 key: Menu.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: Menu.border        val: javax.swing.plaf.basic.BasicBorders$MarginBorder@132ae7
 key: Menu.borderPainted        val: false
 key: Menu.checkIcon        val: javax.swing.plaf.basic.BasicIconFactory$MenuItemCheckIcon@4d2af2
 key: Menu.crossMenuMnemonic        val: false
 key: Menu.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: Menu.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: Menu.margin        val: javax.swing.plaf.InsetsUIResource[top=2,left=2,bottom=2,right=2]
 key: Menu.menuPopupOffsetX        val: 0
 key: Menu.menuPopupOffsetY        val: 0
 key: Menu.selectionBackground        val: javax.swing.plaf.ColorUIResource[r=10,g=36,b=106]
 key: Menu.selectionForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: Menu.shortcutKeys        val: [I@1f82ab4
 key: Menu.submenuPopupOffsetX        val: -4
 key: Menu.submenuPopupOffsetY        val: -3
 key: Menu.useMenuBarBackgroundForTopLevel        val: true
 key: MenuBar.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: MenuBar.border        val: javax.swing.plaf.basic.BasicBorders$MenuBarBorder@63a721
 key: MenuBar.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: MenuBar.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: MenuBar.height        val: 18
 key: MenuBar.highlight        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: MenuBar.rolloverEnabled        val: true
 key: MenuBar.shadow        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: MenuBar.windowBindings        val: [Ljava.lang.Object;@d72200
 key: MenuBarUI        val: com.sun.java.swing.plaf.windows.WindowsMenuBarUI
 key: MenuItem.acceleratorDelimiter        val: +
 key: MenuItem.acceleratorFont        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: MenuItem.acceleratorForeground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: MenuItem.acceleratorSelectionForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: MenuItem.arrowIcon        val: com.sun.java.swing.plaf.windows.WindowsIconFactory$MenuItemArrowIcon@c0a9f9
 key: MenuItem.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: MenuItem.border        val: javax.swing.plaf.basic.BasicBorders$MarginBorder@f894ce
 key: MenuItem.borderPainted        val: false
 key: MenuItem.checkIcon        val: com.sun.java.swing.plaf.windows.WindowsIconFactory$MenuItemCheckIcon@15b0333
 key: MenuItem.commandSound        val: win.sound.menuCommand
 key: MenuItem.disabledAreNavigable        val: true
 key: MenuItem.disabledForeground        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: MenuItem.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: MenuItem.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: MenuItem.margin        val: javax.swing.plaf.InsetsUIResource[top=2,left=2,bottom=2,right=2]
 key: MenuItem.selectionBackground        val: javax.swing.plaf.ColorUIResource[r=10,g=36,b=106]
 key: MenuItem.selectionForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: MenuItemUI        val: com.sun.java.swing.plaf.windows.WindowsMenuItemUI
 key: MenuUI        val: com.sun.java.swing.plaf.windows.WindowsMenuUI
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
 key: OptionPaneUI        val: javax.swing.plaf.basic.BasicOptionPaneUI
 key: Panel.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: Panel.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: Panel.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: PanelUI        val: javax.swing.plaf.basic.BasicPanelUI
 key: PasswordField.background        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: PasswordField.border        val: javax.swing.plaf.basic.BasicBorders$FieldBorder@1d47b2b
 key: PasswordField.caretBlinkRate        val: 500
 key: PasswordField.caretForeground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: PasswordField.disabledBackground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: PasswordField.echoChar        val: *
 key: PasswordField.focusInputMap        val: javax.swing.plaf.InputMapUIResource@2f2295
 key: PasswordField.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: PasswordField.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: PasswordField.inactiveBackground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: PasswordField.inactiveForeground        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: PasswordField.margin        val: javax.swing.plaf.InsetsUIResource[top=1,left=1,bottom=1,right=1]
 key: PasswordField.selectionBackground        val: javax.swing.plaf.ColorUIResource[r=10,g=36,b=106]
 key: PasswordField.selectionForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: PasswordFieldUI        val: com.sun.java.swing.plaf.windows.WindowsPasswordFieldUI
 key: PopupMenu.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: PopupMenu.border        val: javax.swing.plaf.BorderUIResource$CompoundBorderUIResource@e2ecc7
 key: PopupMenu.consumeEventOnClose        val: true
 key: PopupMenu.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: PopupMenu.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: PopupMenu.popupSound        val: win.sound.menuPopup
 key: PopupMenu.selectedWindowInputMapBindings        val: [Ljava.lang.Object;@951a0
 key: PopupMenu.selectedWindowInputMapBindings.RightToLeft        val: [Ljava.lang.Object;@878c4c
 key: PopupMenuSeparatorUI        val: com.sun.java.swing.plaf.windows.WindowsPopupMenuSeparatorUI
 key: PopupMenuUI        val: com.sun.java.swing.plaf.windows.WindowsPopupMenuUI
 key: ProgressBar.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: ProgressBar.border        val: javax.swing.plaf.BorderUIResource$CompoundBorderUIResource@15f157b
 key: ProgressBar.cellLength        val: 7
 key: ProgressBar.cellSpacing        val: 2
 key: ProgressBar.cycleTime        val: 3000
 key: ProgressBar.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: ProgressBar.foreground        val: javax.swing.plaf.ColorUIResource[r=10,g=36,b=106]
 key: ProgressBar.highlight        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: ProgressBar.horizontalSize        val: javax.swing.plaf.DimensionUIResource[width=146,height=12]
 key: ProgressBar.indeterminateInsets        val: java.awt.Insets[top=3,left=3,bottom=3,right=3]
 key: ProgressBar.repaintInterval        val: 50
 key: ProgressBar.selectionBackground        val: javax.swing.plaf.ColorUIResource[r=10,g=36,b=106]
 key: ProgressBar.selectionForeground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: ProgressBar.shadow        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: ProgressBar.verticalSize        val: javax.swing.plaf.DimensionUIResource[width=12,height=146]
 key: ProgressBarUI        val: com.sun.java.swing.plaf.windows.WindowsProgressBarUI
 key: RadioButton.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: RadioButton.border        val: javax.swing.plaf.BorderUIResource$CompoundBorderUIResource@1f9338f
 key: RadioButton.darkShadow        val: javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]
 key: RadioButton.focus        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: RadioButton.focusInputMap        val: javax.swing.plaf.InputMapUIResource@1db9852
 key: RadioButton.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: RadioButton.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: RadioButton.highlight        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: RadioButton.icon        val: com.sun.java.swing.plaf.windows.WindowsIconFactory$RadioButtonIcon@2e323
 key: RadioButton.interiorBackground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: RadioButton.light        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: RadioButton.margin        val: javax.swing.plaf.InsetsUIResource[top=2,left=2,bottom=2,right=2]
 key: RadioButton.shadow        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: RadioButton.textIconGap        val: 4
 key: RadioButton.textShiftOffset        val: 0
 key: RadioButton.totalInsets        val: java.awt.Insets[top=4,left=4,bottom=4,right=4]
 key: RadioButtonMenuItem.acceleratorFont        val: javax.swing.plaf.FontUIResource[family=Dialog,name=Dialog,style=plain,size=12]
 key: RadioButtonMenuItem.acceleratorForeground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: RadioButtonMenuItem.acceleratorSelectionForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: RadioButtonMenuItem.arrowIcon        val: javax.swing.plaf.basic.BasicIconFactory$MenuItemArrowIcon@1827d1
 key: RadioButtonMenuItem.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: RadioButtonMenuItem.border        val: javax.swing.plaf.basic.BasicBorders$MarginBorder@19ecd80
 key: RadioButtonMenuItem.borderPainted        val: false
 key: RadioButtonMenuItem.checkIcon        val: com.sun.java.swing.plaf.windows.WindowsIconFactory$RadioButtonMenuItemIcon@11e67ac
 key: RadioButtonMenuItem.commandSound        val: win.sound.menuCommand
 key: RadioButtonMenuItem.disabledForeground        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: RadioButtonMenuItem.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: RadioButtonMenuItem.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: RadioButtonMenuItem.margin        val: javax.swing.plaf.InsetsUIResource[top=2,left=2,bottom=2,right=2]
 key: RadioButtonMenuItem.selectionBackground        val: javax.swing.plaf.ColorUIResource[r=10,g=36,b=106]
 key: RadioButtonMenuItem.selectionForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: RadioButtonMenuItemUI        val: com.sun.java.swing.plaf.windows.WindowsRadioButtonMenuItemUI
 key: RadioButtonUI        val: com.sun.java.swing.plaf.windows.WindowsRadioButtonUI
 key: RootPane.ancestorInputMap        val: javax.swing.plaf.InputMapUIResource@190a0d6
 key: RootPane.defaultButtonWindowKeyBindings        val: [Ljava.lang.Object;@15c998a
 key: RootPaneUI        val: com.sun.java.swing.plaf.windows.WindowsRootPaneUI
 key: ScrollBar.ancestorInputMap        val: javax.swing.plaf.InputMapUIResource@2f729e
 key: ScrollBar.ancestorInputMap.RightToLeft        val: javax.swing.plaf.InputMapUIResource@8f9a32
 key: ScrollBar.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: ScrollBar.foreground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: ScrollBar.maximumThumbSize        val: javax.swing.plaf.DimensionUIResource[width=4096,height=4096]
 key: ScrollBar.minimumThumbSize        val: javax.swing.plaf.DimensionUIResource[width=8,height=8]
 key: ScrollBar.thumb        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: ScrollBar.thumbDarkShadow        val: javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]
 key: ScrollBar.thumbHighlight        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: ScrollBar.thumbShadow        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: ScrollBar.track        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: ScrollBar.trackForeground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: ScrollBar.trackHighlight        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: ScrollBar.trackHighlightForeground        val: javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]
 key: ScrollBar.width        val: 16
 key: ScrollBarUI        val: com.sun.java.swing.plaf.windows.WindowsScrollBarUI
 key: ScrollPane.ancestorInputMap        val: javax.swing.plaf.InputMapUIResource@15d252d
 key: ScrollPane.ancestorInputMap.RightToLeft        val: javax.swing.plaf.InputMapUIResource@c06258
 key: ScrollPane.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: ScrollPane.border        val: javax.swing.plaf.basic.BasicBorders$FieldBorder@45e228
 key: ScrollPane.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: ScrollPane.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: ScrollPaneUI        val: javax.swing.plaf.basic.BasicScrollPaneUI
 key: Separator.background        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: Separator.foreground        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: Separator.highlight        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: Separator.shadow        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: SeparatorUI        val: com.sun.java.swing.plaf.windows.WindowsSeparatorUI
 key: Slider.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: Slider.focus        val: javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]
 key: Slider.focusInputMap        val: javax.swing.plaf.InputMapUIResource@1b7ae22
 key: Slider.focusInputMap.RightToLeft        val: javax.swing.plaf.InputMapUIResource@32bd65
 key: Slider.focusInsets        val: javax.swing.plaf.InsetsUIResource[top=2,left=2,bottom=2,right=2]
 key: Slider.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: Slider.foreground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: Slider.highlight        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: Slider.horizontalSize        val: java.awt.Dimension[width=200,height=21]
 key: Slider.minimumHorizontalSize        val: java.awt.Dimension[width=36,height=21]
 key: Slider.minimumVerticalSize        val: java.awt.Dimension[width=21,height=36]
 key: Slider.shadow        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: Slider.tickColor        val: java.awt.Color[r=0,g=0,b=0]
 key: Slider.verticalSize        val: java.awt.Dimension[width=21,height=200]
 key: SliderUI        val: com.sun.java.swing.plaf.windows.WindowsSliderUI
 key: Spinner.ancestorInputMap        val: javax.swing.plaf.InputMapUIResource@a0864f
 key: Spinner.arrowButtonInsets        val: null
 key: Spinner.arrowButtonSize        val: java.awt.Dimension[width=17,height=9]
 key: Spinner.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: Spinner.border        val: javax.swing.plaf.basic.BasicBorders$FieldBorder@1a0ae6
 key: Spinner.editorAlignment        val: 11
 key: Spinner.editorBorderPainted        val: false
 key: Spinner.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: Spinner.foreground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: SpinnerUI        val: com.sun.java.swing.plaf.windows.WindowsSpinnerUI
 key: SplitPane.ancestorInputMap        val: javax.swing.plaf.InputMapUIResource@98ce7e
 key: SplitPane.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: SplitPane.border        val: javax.swing.plaf.basic.BasicBorders$SplitPaneBorder@144b18f
 key: SplitPane.darkShadow        val: javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]
 key: SplitPane.dividerSize        val: 5
 key: SplitPane.highlight        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: SplitPane.shadow        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: SplitPaneDivider.border        val: javax.swing.plaf.basic.BasicBorders$SplitPaneDividerBorder@1dc64a5
 key: SplitPaneDivider.draggingColor        val: javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]
 key: SplitPaneUI        val: com.sun.java.swing.plaf.windows.WindowsSplitPaneUI
 key: TabbedPane.ancestorInputMap        val: javax.swing.plaf.InputMapUIResource@1926e90
 key: TabbedPane.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: TabbedPane.contentBorderInsets        val: javax.swing.plaf.InsetsUIResource[top=2,left=2,bottom=3,right=3]
 key: TabbedPane.contentOpaque        val: true
 key: TabbedPane.darkShadow        val: javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]
 key: TabbedPane.focus        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: TabbedPane.focusInputMap        val: javax.swing.plaf.InputMapUIResource@1ed5459
 key: TabbedPane.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: TabbedPane.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: TabbedPane.highlight        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: TabbedPane.light        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: TabbedPane.selectedTabPadInsets        val: javax.swing.plaf.InsetsUIResource[top=2,left=2,bottom=2,right=1]
 key: TabbedPane.shadow        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: TabbedPane.tabAreaInsets        val: javax.swing.plaf.InsetsUIResource[top=3,left=2,bottom=0,right=2]
 key: TabbedPane.tabInsets        val: javax.swing.plaf.InsetsUIResource[top=0,left=4,bottom=1,right=4]
 key: TabbedPane.tabRunOverlay        val: 2
 key: TabbedPane.tabsOpaque        val: true
 key: TabbedPane.tabsOverlapBorder        val: false
 key: TabbedPane.textIconGap        val: 4
 key: TabbedPaneUI        val: com.sun.java.swing.plaf.windows.WindowsTabbedPaneUI
 key: Table.ancestorInputMap        val: javax.swing.plaf.InputMapUIResource@14e0e90
 key: Table.ancestorInputMap.RightToLeft        val: javax.swing.plaf.InputMapUIResource@cffc79
 key: Table.ascendingSortIcon        val: sun.swing.plaf.windows.ClassicSortArrowIcon@754fc
 key: Table.background        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: Table.darkShadow        val: javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]
 key: Table.descendingSortIcon        val: sun.swing.plaf.windows.ClassicSortArrowIcon@c92507
 key: Table.dropLineColor        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: Table.dropLineShortColor        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: Table.focusCellBackground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: Table.focusCellForeground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: Table.focusCellHighlightBorder        val: com.sun.java.swing.plaf.windows.WindowsBorders$ComplementDashedBorder@ce16ad
 key: Table.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: Table.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: Table.gridColor        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: Table.highlight        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: Table.light        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: Table.scrollPaneBorder        val: javax.swing.plaf.BorderUIResource$BevelBorderUIResource@e4bb3c
 key: Table.selectionBackground        val: javax.swing.plaf.ColorUIResource[r=10,g=36,b=106]
 key: Table.selectionForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: Table.shadow        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: Table.sortIconColor        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: Table.sortIconHighlight        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: Table.sortIconLight        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: TableHeader.ancestorInputMap        val: javax.swing.plaf.InputMapUIResource@118223d
 key: TableHeader.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: TableHeader.cellBorder        val: javax.swing.plaf.BorderUIResource$CompoundBorderUIResource@719f1f
 key: TableHeader.focusCellBackground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: TableHeader.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: TableHeader.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: TableHeaderUI        val: com.sun.java.swing.plaf.windows.WindowsTableHeaderUI
 key: TableUI        val: javax.swing.plaf.basic.BasicTableUI
 key: TextArea.background        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: TextArea.border        val: javax.swing.plaf.basic.BasicBorders$MarginBorder@1bb9696
 key: TextArea.caretBlinkRate        val: 500
 key: TextArea.caretForeground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: TextArea.disabledBackground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: TextArea.focusInputMap        val: javax.swing.plaf.InputMapUIResource@979f67
 key: TextArea.font        val: javax.swing.plaf.FontUIResource[family=Monospaced,name=Monospaced,style=plain,size=13]
 key: TextArea.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: TextArea.inactiveBackground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: TextArea.inactiveForeground        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: TextArea.margin        val: javax.swing.plaf.InsetsUIResource[top=1,left=1,bottom=1,right=1]
 key: TextArea.selectionBackground        val: javax.swing.plaf.ColorUIResource[r=10,g=36,b=106]
 key: TextArea.selectionForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: TextAreaUI        val: com.sun.java.swing.plaf.windows.WindowsTextAreaUI
 key: TextField.background        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: TextField.border        val: javax.swing.plaf.basic.BasicBorders$FieldBorder@1de45e2
 key: TextField.caretBlinkRate        val: 500
 key: TextField.caretForeground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: TextField.darkShadow        val: javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]
 key: TextField.disabledBackground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: TextField.focusInputMap        val: javax.swing.plaf.InputMapUIResource@e5355f
 key: TextField.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: TextField.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: TextField.highlight        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: TextField.inactiveBackground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: TextField.inactiveForeground        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: TextField.light        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: TextField.margin        val: javax.swing.plaf.InsetsUIResource[top=1,left=1,bottom=1,right=1]
 key: TextField.selectionBackground        val: javax.swing.plaf.ColorUIResource[r=10,g=36,b=106]
 key: TextField.selectionForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: TextField.shadow        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: TextFieldUI        val: com.sun.java.swing.plaf.windows.WindowsTextFieldUI
 key: TextPane.background        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: TextPane.border        val: javax.swing.plaf.basic.BasicBorders$MarginBorder@53c3f5
 key: TextPane.caretBlinkRate        val: 500
 key: TextPane.caretForeground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: TextPane.disabledBackground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: TextPane.focusInputMap        val: javax.swing.plaf.InputMapUIResource@fa39d7
 key: TextPane.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: TextPane.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: TextPane.inactiveBackground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: TextPane.inactiveForeground        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: TextPane.margin        val: javax.swing.plaf.InsetsUIResource[top=3,left=3,bottom=3,right=3]
 key: TextPane.selectionBackground        val: javax.swing.plaf.ColorUIResource[r=10,g=36,b=106]
 key: TextPane.selectionForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: TextPaneUI        val: com.sun.java.swing.plaf.windows.WindowsTextPaneUI
 key: TitledBorder.border        val: javax.swing.plaf.BorderUIResource$EtchedBorderUIResource@1d9e2c7
 key: TitledBorder.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: TitledBorder.titleColor        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: ToggleButton.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: ToggleButton.border        val: javax.swing.plaf.BorderUIResource$CompoundBorderUIResource@c063ad
 key: ToggleButton.darkShadow        val: javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]
 key: ToggleButton.focus        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: ToggleButton.focusInputMap        val: javax.swing.plaf.InputMapUIResource@1d6747b
 key: ToggleButton.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: ToggleButton.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: ToggleButton.highlight        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: ToggleButton.light        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: ToggleButton.margin        val: javax.swing.plaf.InsetsUIResource[top=2,left=14,bottom=2,right=14]
 key: ToggleButton.shadow        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: ToggleButton.textIconGap        val: 4
 key: ToggleButton.textShiftOffset        val: 1
 key: ToggleButtonUI        val: com.sun.java.swing.plaf.windows.WindowsToggleButtonUI
 key: ToolBar.ancestorInputMap        val: javax.swing.plaf.InputMapUIResource@1ce1bea
 key: ToolBar.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: ToolBar.border        val: com.sun.java.swing.plaf.windows.WindowsBorders$ToolBarBorder@18488ef
 key: ToolBar.darkShadow        val: javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]
 key: ToolBar.dockingBackground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: ToolBar.dockingForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=0,b=0]
 key: ToolBar.floatingBackground        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: ToolBar.floatingForeground        val: javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]
 key: ToolBar.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: ToolBar.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: ToolBar.highlight        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: ToolBar.light        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: ToolBar.shadow        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: ToolBarSeparatorUI        val: com.sun.java.swing.plaf.windows.WindowsToolBarSeparatorUI
 key: ToolBarUI        val: com.sun.java.swing.plaf.windows.WindowsToolBarUI
 key: ToolTip.background        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=225]
 key: ToolTip.border        val: javax.swing.plaf.BorderUIResource$LineBorderUIResource@b0a3f5
 key: ToolTip.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: ToolTip.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: ToolTipManager.enableToolTipMode        val: activeApplication
 key: ToolTipUI        val: javax.swing.plaf.basic.BasicToolTipUI
 key: Tree.ancestorInputMap        val: javax.swing.plaf.InputMapUIResource@b1cd0
 key: Tree.background        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: Tree.changeSelectionWithFocus        val: true
 key: Tree.closedIcon        val: sun.swing.ImageIconUIResource@1be2893
 key: Tree.collapsedIcon        val: com.sun.java.swing.plaf.windows.WindowsTreeUI$CollapsedIcon@1dfd868
 key: Tree.drawDashedFocusIndicator        val: true
 key: Tree.drawsFocusBorderAroundIcon        val: false
 key: Tree.dropLineColor        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: Tree.editorBorder        val: javax.swing.plaf.BorderUIResource$LineBorderUIResource@b0a3f5
 key: Tree.expandedIcon        val: com.sun.java.swing.plaf.windows.WindowsTreeUI$ExpandedIcon@1700391
 key: Tree.focusInputMap        val: javax.swing.plaf.InputMapUIResource@124111a
 key: Tree.focusInputMap.RightToLeft        val: javax.swing.plaf.InputMapUIResource@1ab11b0
 key: Tree.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: Tree.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: Tree.hash        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: Tree.leafIcon        val: sun.swing.ImageIconUIResource@ecb67f
 key: Tree.leftChildIndent        val: 8
 key: Tree.lineTypeDashed        val: true
 key: Tree.openIcon        val: sun.swing.ImageIconUIResource@120540c
 key: Tree.paintLines        val: true
 key: Tree.rightChildIndent        val: 11
 key: Tree.rowHeight        val: 16
 key: Tree.scrollsOnExpand        val: true
 key: Tree.selectionBackground        val: javax.swing.plaf.ColorUIResource[r=10,g=36,b=106]
 key: Tree.selectionBorderColor        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: Tree.selectionForeground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: Tree.textBackground        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: Tree.textForeground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: Tree.timeFactor        val: 1000
 key: TreeUI        val: com.sun.java.swing.plaf.windows.WindowsTreeUI
 key: Viewport.background        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: Viewport.font        val: javax.swing.plaf.FontUIResource[family=Tahoma,name=Tahoma,style=plain,size=11]
 key: Viewport.foreground        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: ViewportUI        val: javax.swing.plaf.basic.BasicViewportUI
 key: activeCaption        val: javax.swing.plaf.ColorUIResource[r=10,g=36,b=106]
 key: activeCaptionBorder        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: activeCaptionText        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: control        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: controlDkShadow        val: javax.swing.plaf.ColorUIResource[r=64,g=64,b=64]
 key: controlHighlight        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: controlLtHighlight        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: controlShadow        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: controlText        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: desktop        val: javax.swing.plaf.ColorUIResource[r=58,g=110,b=165]
 key: html.missingImage        val: sun.swing.ImageIconUIResource@13b9fae
 key: html.pendingImage        val: sun.swing.ImageIconUIResource@afae4a
 key: inactiveCaption        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: inactiveCaptionBorder        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: inactiveCaptionText        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: info        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=225]
 key: infoText        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: menu        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: menuPressedItemB        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: menuPressedItemF        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: menuText        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: scrollbar        val: javax.swing.plaf.ColorUIResource[r=212,g=208,b=200]
 key: text        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: textHighlight        val: javax.swing.plaf.ColorUIResource[r=10,g=36,b=106]
 key: textHighlightText        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: textInactiveText        val: javax.swing.plaf.ColorUIResource[r=128,g=128,b=128]
 key: textText        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: window        val: javax.swing.plaf.ColorUIResource[r=255,g=255,b=255]
 key: windowBorder        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]
 key: windowText        val: javax.swing.plaf.ColorUIResource[r=0,g=0,b=0]



 *
 * */