/*
 * VKeyboard.java
 *
 * Created on 18. Juli 2008, 17:39
 */

package dimm.home;

import dimm.home.Rendering.GenericGlossyDlg;
import dimm.home.Rendering.GlossDialogPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.JTextComponent;
import org.jdesktop.swingx.JXPanel;
import org.netbeans.lib.awtextra.AbsoluteConstraints;




class VKButton extends JButton
{
    String nice_txt;
    String ch;
    String shift_ch;
    String ctrl_ch;
    String altgr_ch;
    int xpos;
    int ypos;
    int dx = 40;
    int dy = 30;
    
    public static final int button_xspace = 5;
    public static final int button_yspace = 5;
    public static final int button_y_line_space = 35;
    static Font font = new Font("Tahoma", Font.PLAIN, 14 );
    
    int pixel_xpos;
    
    VKeyboard kbd;
    
    void set_kbd( VKeyboard _kbd )
    {
        kbd =_kbd;
    }
    

    public int getDx()
    {
        return dx;
    }
    
    VKButton( int _xpos, int _ypos, String _ch, String _shift_ch, String _ctrl_ch, String _altgr_ch)
    {
        xpos = _xpos;
        ypos = _ypos;
        ch = _ch;
        shift_ch = _shift_ch;
        ctrl_ch = _ctrl_ch;
        altgr_ch = _altgr_ch;
        nice_txt = null;
        
        
        
        pixel_xpos = 0;
        
        setFont( font );
        
        this.setOpaque(false);
        this.setContentAreaFilled(false);
        this.setForeground(Color.white);
        
        
        
        addMouseListener( new MouseAdapter()
        {

            @Override
            public void mouseEntered(MouseEvent e)
            {
                setForeground(Main.ui.get_table_header_color());
                
                
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                setForeground(Color.white);
            }

            @Override
            public void mousePressed( MouseEvent e )
            {
                kbd.call_click( e, VKButton.this );
            }
           
       
            
        } );
    }
    

        
    VKButton(  int _xpos, int _ypos, String _ch, String _shift_ch)
    {
        this(  _xpos, _ypos, _ch, _shift_ch, null, null );
    }
    

    public String getAltgr_ch()
    {
        return altgr_ch;
    }

    public String getShift_ch()
    {
        return shift_ch;
    }

    public String getCh()
    {
        return ch;
    }

    int get_pixel_xpos()
    {
        return pixel_xpos;
    }

    void set_double_height()
    {
        dy += button_y_line_space;
        dy += button_yspace;
    }
    void set_normal_state()
    {
        if (nice_txt != null)
            setText( nice_txt);
        else
            setText( ch );
    }
    String get_display_txt()
    {
        if (nice_txt != null)
            return nice_txt;
        return ch;
    }
        
    void set_shift_state(boolean b)
    {
        if (nice_txt != null)
            setText( nice_txt );
        else
            setText( b ? shift_ch : ch );
    }
    void set_ctrl_state(boolean b)
    {
        if (nice_txt != null)
            setText( nice_txt );
        else
            setText( b ? ctrl_ch : ch );
    }
    void set_altgr_state(boolean b)
    {
        if (nice_txt != null)
            setText( nice_txt );
        else
            setText( b ? altgr_ch : ch );
    }
    void set_nice_txt( String t )
    {
        nice_txt = t;
    }
    
    int get_x()
    {
        return xpos;
    }
    int get_y()
    {
        return ypos;
    }
    int get_dx()
    {
        return dx;
    }
    int get_dy()
    {
        return dy;
    }
    void set_dx( int _dx )
    {
        dx = _dx;
    }
    void set_pixel_xpos( int _dx )
    {
        pixel_xpos = _dx;
    }
}
class MyAbsConstraints extends AbsoluteConstraints
{
 /*   int myx;
    int myy;
    int mydx;
    int mydy;
    */
    public MyAbsConstraints( int x, int y, int dx, int dy )
    {
        super( x, y, dx, dy );
     /*   myx = x;
        myy = y;
        mydx = dx;
        mydy = dy;
      * */
    }

/*
    @Override
    public int getHeight()
    {
        return mydy;
    }

    @Override
    public int getWidth()
    {
        return mydx;
    }

    @Override
    public int getX()
    {
        return myx;
    }

    @Override
    public int getY()
    {
        return myy;
    }

    public void setMyx( int myx )
    {
        this.myx = myx;
    }

    public void setMyy( int myy )
    {
        this.myy = myy;
    }

    */
    public void setHeight( int height )
    {
        this.height = height;
    }

    public void setWidth( int width )
    {
        this.width = width;
    }

    public void setX( int x )
    {
        this.x = x;
    }

    public void setY( int y )
    {
        this.y = y;
    }
    
    
}
/**
 
 @author  Administrator
 */
public class VKeyboard extends JXPanel
{
    int x_offset = 10;
    int y_offset = 40;
    ArrayList<VKButton> button_list;
    
    MyAbsConstraints  ac = new MyAbsConstraints ( 30, 200, 500, 100 );
    
    int old_caret_pos;
    JComponent comp;
    String fall_back_txt;
                
    public AbsoluteConstraints get_constraint()
    {
        return ac;
    }
                
    GenericGlossyDlg dlg;
    
    
    /** Creates new form VKeyboard */
    public VKeyboard()
    {
        initComponents();
        
        int pc_alpha = (int)Main.get_long_prop(Preferences.VKEY_ALPHA, (long)80);
        if (pc_alpha < 10)
            pc_alpha = 10;
        
        this.setAlpha(pc_alpha / 100.0f);
        SL_ALPHA.setValue(pc_alpha);

        button_list = new ArrayList<VKButton>();
        
        create_full_buttons();
        arrange_buttons();
        
        
        
       addMouseListener( new MouseAdapter()
       {
        private boolean isMovingWindow;
        private int dragOffsetX;
        private int dragOffsetY;
        Dimension click_size;
        boolean is_move_cursor = false;
        private static final int BORDER_DRAG_THICKNESS = 2;

            @Override
        public void mousePressed(MouseEvent ev)
        {
            Point dragWindowOffset = ev.getPoint();
            VKeyboard w = (VKeyboard) ev.getSource();
            
            Point convertedDragWindowOffset = SwingUtilities.convertPoint(
                    w, dragWindowOffset, VKeyboard.this);


            if (VKeyboard.this.contains(convertedDragWindowOffset))
            {
                if (dragWindowOffset.y >= BORDER_DRAG_THICKNESS && dragWindowOffset.x >= BORDER_DRAG_THICKNESS && dragWindowOffset.x < w.getWidth() - BORDER_DRAG_THICKNESS)
                {
                    isMovingWindow = true;
                    dragOffsetX = dragWindowOffset.x;
                    dragOffsetY = dragWindowOffset.y;
                }
            }
        }

            @Override
        public void mouseReleased(MouseEvent ev)
        {
            VKeyboard w = (VKeyboard) ev.getSource();
            if (isMovingWindow)
            {
                Point windowPt = ev.getPoint();
                windowPt.x = windowPt.x - dragOffsetX;
                windowPt.y = windowPt.y - dragOffsetY;
                Point was_loc = w.getLocation();
                was_loc.x += windowPt.x;
                was_loc.y += windowPt.y;
                ac.setX(was_loc.x);
                ac.setY(was_loc.y);
                w.setLocation(was_loc);
                System.out.println( "Set Loc: " + was_loc.x + " / " + was_loc.y ); 
            }
                
            isMovingWindow = false;
        }

           
           
       } );
       addMouseMotionListener( new MouseMotionAdapter()
       {
       } );
       addKeyListener( new KeyAdapter()
       {
       } );
        
       addComponentListener(  new ComponentAdapter()
       {
       } );
       this.setFocusTraversalKeysEnabled(false);
                
       
    }
    
    void set_component( JComponent _comp )
    {
        comp = _comp;
        
        get_comp_vals();
        
   /*     if (comp instanceof JPasswordField)
            this.TXT_EDITOR.setVisible(false);
        else
            this.TXT_EDITOR.setVisible(true);
     */   
        this.TXT_EDITOR.requestFocusInWindow();
        this.TXT_EDITOR.setCaretPosition(this.old_caret_pos);
        
    }
    void set_dlg( GenericGlossyDlg _dlg )
    {
        dlg = _dlg;
    }
        
    public void set_num_keybd()
    {
        for (int i = 0; i < button_list.size(); i++)
        {
            VKButton bt = button_list.get(i);
            this.PN_KEYS.remove( bt );
        }                
        PN_KEYS.remove(PN_EDITOR);
        PN_KEYS.add(PN_EDITOR, new org.netbeans.lib.awtextra.AbsoluteConstraints(5, 10, 140, 40));       
        
        button_list.clear();
        create_num_buttons();
        arrange_buttons();
    }
    public void set_full_keybd()
    {
        for (int i = 0; i < button_list.size(); i++)
        {
            VKButton bt = button_list.get(i);
            this.PN_KEYS.remove( bt );
        }          
        PN_KEYS.remove(PN_EDITOR);
        PN_KEYS.add(PN_EDITOR, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, 390, 40));        
        button_list.clear();
        create_full_buttons();
        arrange_buttons();
    }
    
    int act_x = 0;
    int last_y = -1;

    void register_button( VKButton bt, int dx )
    {
        if (last_y != bt.get_y())
        {
            act_x = 0;
            last_y = bt.get_y();
        }
        bt.set_kbd( this );
            
        if (dx > 0)
            bt.set_dx( dx );
        bt.set_pixel_xpos(act_x);
        
        act_x += bt.get_dx();
        act_x += VKButton.button_xspace;
                
        button_list.add( bt );
    }
    void register_button( VKButton bt)
    {
        register_button( bt, 0 );
    }    
    
    void create_num_buttons()
    {
        
        register_button( new VKButton(1,1, "7", "") );
        register_button( new VKButton(2,1, "8", "") );
        register_button( new VKButton(3,1, "9", "") );
        register_button( new VKButton(4,2, "4", "") );
        register_button( new VKButton(5,2, "5", "") );
        register_button( new VKButton(6,2, "6", "") );
        register_button( new VKButton(7,3, "1", "") );
        register_button( new VKButton(8,3, "2", ""));
        register_button( new VKButton(9,3, "3", "") );
        register_button( new VKButton(10,4, ".", "") );
        register_button( new VKButton(11,4, "0", "") );
        register_button( new VKButton(12,4, "DEL", "") );
        register_button( new VKButton(13,5, "ESC", "") );
        
        VKButton bt = new VKButton(14,5, "OK", "");
        act_x += bt.get_dx();
        act_x += VKButton.button_xspace;
        
        register_button( bt );
        
        set_button_nice_txt("DEL", "Del");
        set_button_nice_txt("OK", "Okay");
        set_button_nice_txt("ESC", "Esc");
         
    }    
    
    void create_full_buttons()
    {
        register_button( new VKButton(0,1, "^", "°") );
        register_button( new VKButton(1,1, "1", "!") );
        register_button( new VKButton(2,1, "2", "\""));
        register_button( new VKButton(3,1, "3", "§") );
        register_button( new VKButton(4,1, "4", "$") );
        register_button( new VKButton(5,1, "5", "%") );
        register_button( new VKButton(6,1, "6", "&") );
        register_button( new VKButton(7,1, "7", "/") );
        register_button( new VKButton(8,1, "8", "(") );
        register_button( new VKButton(9,1, "9", ")") );
        register_button( new VKButton(10,1, "0", "=") );
        register_button( new VKButton(11,1, "ß", "?") );
        register_button( new VKButton(12,1, "´", "`") );
        register_button( new VKButton(13,1, "BACKSPACE", ""), 80 );
        
        
        register_button( new VKButton(0,2, "TAB", ""), 60 );
        register_button( new VKButton(1,2, "q", "Q") );
        register_button( new VKButton(2,2, "w", "W") );
        register_button( new VKButton(3,2, "e", "E") );
        register_button( new VKButton(4,2, "r", "R") );
        register_button( new VKButton(5,2, "t", "T") );
        register_button( new VKButton(6,2, "z", "Z") );
        register_button( new VKButton(7,2, "u", "U") );
        register_button( new VKButton(8,2, "i", "I") );
        register_button( new VKButton(9,2, "o", "O") );
        register_button( new VKButton(10,2, "p", "P") );
        register_button( new VKButton(11,2, "ü", "Ü") );
        register_button( new VKButton(12,2, "+", "*", "", "~") );

        VKButton bt = new VKButton(13,2, "RETURN", "");
        bt.set_double_height(); 
        act_x += 10;
        register_button( bt, 50 );

        register_button( new VKButton(0,3, "SHIFTLOCK", "SHIFTLOCK"), 70 );
        register_button( new VKButton(1,3, "a", "A") );
        register_button( new VKButton(2,3, "s", "S") );
        register_button( new VKButton(3,3, "d", "D") );
        register_button( new VKButton(4,3, "f", "F") );
        register_button( new VKButton(5,3, "g", "G") );
        register_button( new VKButton(6,3, "h", "H") );
        register_button( new VKButton(7,3, "j", "J") );
        register_button( new VKButton(8,3, "k", "K") );
        register_button( new VKButton(9,3, "l", "L") );
        register_button( new VKButton(10,3, "ö", "Ö") );
        register_button( new VKButton(11,3, "ä", "Ä") );
        register_button( new VKButton(12,3, "#", "'") );
        
        
        register_button( new VKButton(0,4, "SHIFT", "SHIFT"), 50 );
        register_button( new VKButton(1,4, "<", ">", "", "|") );
        register_button( new VKButton(2,4, "y", "Y") );
        register_button( new VKButton(3,4, "x", "X") );
        register_button( new VKButton(4,4, "c", "C") );
        register_button( new VKButton(5,4, "v", "V") );
        register_button( new VKButton(6,4, "b", "B") );
        register_button( new VKButton(7,4, "n", "N") );
        register_button( new VKButton(8,4, "m", "M") );
        register_button( new VKButton(9,4, ",", ";") );
        register_button( new VKButton(10,4, ".", ":") );
        register_button( new VKButton(11,4, "-", "_") );
        register_button( new VKButton(12,4, "SHIFT", "SHIFT"), 115 );
        
        register_button( new VKButton(0,5, "CTRL", ""), 50 );
        bt = new VKButton(1,5, "ALT", "");
        act_x += bt.get_dx();
        act_x += VKButton.button_xspace;
        
        register_button( bt, 50 );
        register_button( new VKButton(2,5, "SPACE", "SPACE"), 355 );
        register_button( new VKButton(3,5, "ALTGR", ""), 50 );
        bt = new VKButton(4,5, "DEL", "");
        act_x += bt.get_dx();
        act_x += VKButton.button_xspace;
        register_button( bt, 50 );
        
        bt = new VKButton(0,0, "OK", "");
        act_x -= bt.getDx();  
        bt.set_pixel_xpos(act_x);
        bt.set_kbd( this );
        button_list.add( bt );        

        bt = new VKButton(0,0, "ESC", "");
        bt.set_pixel_xpos(0);
        bt.set_kbd( this );
        button_list.add( bt );        
        
        
        set_button_nice_txt("SPACE", "Space");
        set_button_nice_txt("BACKSPACE", "Backspace");
        set_button_nice_txt("SHIFT", "Shift");
        set_button_nice_txt("SHIFTLOCK", "ShiftLock");
        set_button_nice_txt("CTRL", "Strg");
        set_button_nice_txt("DEL", "Del");
        set_button_nice_txt("ALT", "Alt");
        set_button_nice_txt("ALTGR", "Alt Gr");
        set_button_nice_txt("OK", "Okay");
        set_button_nice_txt("ESC", "Esc");
        set_button_nice_txt("RETURN", "Enter");
    }
    
    void set_button_nice_txt( String key, String txt )
    {
        for (int i = 0; i < button_list.size(); i++)
        {
            VKButton bt = button_list.get(i);
            if (bt.getCh().compareTo(key) == 0)
            {
                bt.set_nice_txt(txt);
            }
        }        
    }
    
    void arrange_buttons()
    {
        int max_x = 0;
        int max_y = 0;
        this.PN_KEYS.remove( LB_EMPTY ); 
        for (int i = 0; i < button_list.size(); i++)
        {
            VKButton bt = button_list.get(i);
            int x = bt.get_pixel_xpos() + x_offset;
            int y = bt.get_y() *  (VKButton.button_y_line_space + VKButton.button_yspace) + y_offset;
            if (max_x < x + bt.get_dx())
                max_x = x + bt.get_dx();
            if (max_y < y + bt.get_dy())
                max_y = y + bt.get_dy();
       
            this.PN_KEYS.add(bt, new AbsoluteConstraints(x, y, bt.get_dx(), bt.get_dy()));
            
            bt.set_normal_state();
        }
        this.PN_KEYS.add( LB_EMPTY, new AbsoluteConstraints(max_x + 2*VKButton.button_xspace, max_y  + 2*VKButton.button_yspace, 1, 1));
        
        PN_KEYS.setSize(max_x + 2*VKButton.button_xspace, max_y  + 2*VKButton.button_yspace);
        ac.setWidth(PN_KEYS.getWidth());
        ac.setHeight(PN_KEYS.getHeight());
        this.setSize(PN_KEYS.getSize());
        this.validate();

        
    }
    

    boolean single_shift_mode = false;
    boolean single_ctrl_mode = false;
    boolean single_altgr_mode = false;
    boolean shift_lock_mode = false;
            
    void call_click( MouseEvent e, VKButton bt )
    {
        if (bt.getCh().compareTo("SHIFT") == 0)
        {
            single_shift_mode = !single_shift_mode;
            bt.setSelected(single_shift_mode);
            for (int i = 0; i < button_list.size(); i++)
            {
                VKButton _bt = button_list.get(i);
                _bt.set_shift_state(single_shift_mode);
            }
                
            return;
        }
        if (bt.getCh().compareTo("STRG") == 0)
        {
            single_ctrl_mode = !single_ctrl_mode;
            bt.setSelected(single_ctrl_mode);
            for (int i = 0; i < button_list.size(); i++)
            {
                VKButton _bt = button_list.get(i);
                _bt.set_ctrl_state(single_ctrl_mode);
            }
            return;
        }
        if (bt.getCh().compareTo("ALTGR") == 0)
        {
            single_altgr_mode = !single_altgr_mode;
            bt.setSelected(single_altgr_mode);
            for (int i = 0; i < button_list.size(); i++)
            {
                VKButton _bt = button_list.get(i);
                _bt.set_altgr_state(single_altgr_mode);
            }
            return;
        }
        if (bt.getCh().compareTo("SHIFTLOCK") == 0)
        {
            shift_lock_mode = !shift_lock_mode;
            bt.setSelected(shift_lock_mode);
            for (int i = 0; i < button_list.size(); i++)
            {
                VKButton _bt = button_list.get(i);
                _bt.set_shift_state(shift_lock_mode);
            }
            
            return;
        }
        
        
        String key = bt.getCh();
        if ((single_shift_mode || shift_lock_mode))
        {            
            if (bt.getShift_ch() != null)
                key = bt.getShift_ch();
        }
        else if (single_altgr_mode)
        {     
            if (bt.getAltgr_ch() != null)
                key = bt.getAltgr_ch();
        }     
        
        if (key.compareTo("RETURN") == 0)
        {
            if (comp instanceof JTextArea)
            {
                close_kbd();
                return;
            }
            if (comp instanceof JTextComponent)
            {             
                if (!call_okay_callback())
                    close_kbd();
                return;
            }
        }
        if (single_shift_mode || single_altgr_mode || single_ctrl_mode)
        {
            single_shift_mode = false;
            single_altgr_mode = false;
            single_ctrl_mode = false;
            for (int i = 0; i < button_list.size(); i++)
            {
                VKButton _bt = button_list.get(i);
                _bt.set_normal_state();
            }
            
        }
        if (bt.getCh().compareTo("SPACE") == 0)
        {
            key = " ";
        }                
        else if (key.compareTo("OK") == 0)
        {
            close_kbd();
            call_okay_callback();
            return;
        }
        else if (key.compareTo("ESC") == 0)
        {            
            close_kbd();
            set_comp_text(fall_back_txt);
            return;
        }        
        else if (key.compareTo("BACKSPACE") == 0)
        {            
            backspace_comp_text();
            return;
        }
        else if (key.compareTo("DEL") == 0)
        {            
            del_comp_text();
            return;
        }
        
        
        insert_comp_text( key );
    }
    
    Dimension last_size;
    Point last_pos;
    public void open_kbd()
    {
        if (dlg != null)
        {
            last_size = dlg.getSize();
            last_pos =  dlg.getLocation();
        }
        else
            last_size = UserMain.self.getSize();
            
        Dimension new_size = this.getSize();
        
        if (new_size.height < last_size.height)
            new_size.height = last_size.height;
        if (new_size.width < last_size.width)
            new_size.width = last_size.width;
                                
        if (dlg != null)
        {
            dlg.setSize(new_size);
            if (UserMain.self.is_touchscreen())
            {
                if (new_size.width + last_pos.x > 800)
                {
                    Point new_pos = last_pos;
                    new_pos.x = 800 - new_size.width - 5;
                    dlg.setLocation(new_pos);
                }
            }
            dlg.setGlassPane(this);
            dlg.getGlassPane().setVisible(true);        
        }
        else
        {
            UserMain.self.PN_GLASS.add(this, this.get_constraint() );
            UserMain.self.PN_GLASS.setVisible(true );            
                
        }
    }
    boolean call_okay_callback()
    {
        // CALL OKAY CALLBACK
        if (dlg != null)
        {
            GlossDialogPanel pn = dlg.get_panel();
            return pn.VKBDOkay(comp);                
        }
        return false;
    }
    void close_kbd()
    {
        if (dlg != null)
        {
            dlg.getGlassPane().setVisible(false);  
            dlg.setGlassPane(new JPanel());
            dlg.setSize(last_size);
            if (UserMain.self.is_touchscreen())
            {
                dlg.setLocation(last_pos);
            }            
        }
        else
        {
            UserMain.self.PN_GLASS.setVisible(false );
            UserMain.self.PN_GLASS.remove(this);
            
        }
        
    }
    
    
    void set_comp_text( String txt )
    {
        if (comp instanceof JTextComponent)
        {
            JTextComponent tf = (JTextComponent) comp; 
            tf.setText(txt);
            
            this.TXT_EDITOR.setText(get_comp_tf_txt());
            this.TXT_EDITOR.setCaretPosition(this.old_caret_pos);
        }       
      
       /* if (comp instanceof JPasswordField)
        {
            JPasswordField tf = (JPasswordField) comp; 
            tf.setText(txt);
        }    */    
    }
    void insert_comp_text( String txt )
    {
        if (comp instanceof JTextComponent)
        {
            JTextComponent tf = (JTextComponent) comp; 
            int pos = this.old_caret_pos;
            this.old_caret_pos++;
            String old_txt = tf.getText();
            if (pos < 0)
            {
                tf.setText(txt);
            }
            else if (pos >= old_txt.length())
            {
                tf.setText(old_txt + txt);
            }
            else
            {
                String new_txt = old_txt.substring(0,pos) + txt + old_txt.substring(pos);
                tf.setText( new_txt );
            }
            this.TXT_EDITOR.setText(get_comp_tf_txt());
            this.TXT_EDITOR.setCaretPosition(this.old_caret_pos);
            this.TXT_EDITOR.requestFocusInWindow();
        }    
      /* if (comp instanceof JPasswordField)
        {
            JPasswordField tf = (JPasswordField) comp; 
            int pos = this.old_caret_pos;
            this.old_caret_pos++;
            String old_txt = new String(tf.getPassword());
            if (pos < 0)
            {
                tf.setText(txt);
            }
            else if (pos >= old_txt.length())
            {
                tf.setText(old_txt + txt);
            }
            else
            {
                String new_txt = old_txt.substring(0,pos) + txt + old_txt.substring(pos);
                tf.setText( new_txt );
            }
         }        
        */
    }
    void del_comp_text()
    {
        if (comp instanceof JTextComponent)
        {
            JTextComponent tf = (JTextComponent) comp; 
            int pos = this.old_caret_pos;
            
            String old_txt = tf.getText();
            if (pos < old_txt.length())
            {
                String new_txt = old_txt.substring(0, pos);
                if (old_txt.length() > pos +1)
                    new_txt +=  old_txt.substring(pos+ 1);
                tf.setText( new_txt );
            }
            
            this.TXT_EDITOR.setText(get_comp_tf_txt());
            this.TXT_EDITOR.setCaretPosition(this.old_caret_pos);
            this.TXT_EDITOR.requestFocusInWindow();
        }
     /*   if (comp instanceof JPasswordField)
        {
            JPasswordField tf = (JPasswordField) comp; 
            int pos = this.old_caret_pos;
            
            String old_txt = new String(tf.getPassword());
            if (pos < old_txt.length())
            {
                String new_txt = old_txt.substring(0, pos);
                if (old_txt.length() > pos +1)
                    new_txt +=  old_txt.substring(pos+ 1);
                tf.setText( new_txt );
            }
        }*/
    }
    
    void backspace_comp_text()
    {
        if (comp instanceof JTextComponent)
        {            
            int pos = this.old_caret_pos;
            
            if (pos > 0)
            {
                old_caret_pos--;
                del_comp_text();
            }
        }                    
     /*  if (comp instanceof JPasswordField)
        {
            int pos = this.old_caret_pos;
            
            if (pos > 0)
            {
                old_caret_pos--;
                del_comp_text();
            }
        }*/
    }
    
    String get_comp_tf_txt()
    {
        if (comp instanceof JTextComponent)
        {
            JTextComponent tf = (JTextComponent) comp;  
            String txt = tf.getText();
            if (comp instanceof JPasswordField)
            {
                StringBuffer sb = new StringBuffer();
                for (int i = 0; i < txt.length(); i++)
                {
                    sb.append("*");
                }
                
                txt = sb.toString();
            }
            return txt;
        }
        return null;
            
    }
    
    void get_comp_vals( )
    {
        if (comp instanceof JTextComponent)
        {
            JTextComponent tf = (JTextComponent) comp;  
            old_caret_pos = tf.getCaretPosition();
            this.fall_back_txt = tf.getText();
            
            
            this.TXT_EDITOR.setText(get_comp_tf_txt());
            this.TXT_EDITOR.setCaretPosition(this.old_caret_pos);
            this.TXT_EDITOR.requestFocusInWindow();
            
        }            
     /*   if (comp instanceof JPasswordField)
        {
            JPasswordField tf = (JPasswordField) comp; 
            old_caret_pos = tf.getCaretPosition();
            this.fall_back_txt = new String(tf.getPassword());
        }*/
    }
    
    

 
    /** This method is called from within the constructor to
     initialize the form.
     WARNING: Do NOT modify this code. The content of this method is
     always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {
        java.awt.GridBagConstraints gridBagConstraints;

        PN_KEYS = new javax.swing.JPanel();
        PN_EDITOR = new javax.swing.JPanel();
        TXT_EDITOR = new javax.swing.JTextField();
        SL_ALPHA = new javax.swing.JSlider();
        LB_EMPTY = new javax.swing.JLabel();

        setAlpha(0.8F);
        setPaintBorderInsets(false);
        setLayout(new java.awt.GridBagLayout());

        PN_KEYS.setBackground(new java.awt.Color(79, 71, 66));
        PN_KEYS.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        PN_EDITOR.setOpaque(false);

        TXT_EDITOR.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.LOWERED));
        TXT_EDITOR.addMouseListener(new java.awt.event.MouseAdapter()
        {
            public void mouseClicked(java.awt.event.MouseEvent evt)
            {
                TXT_EDITORMouseClicked(evt);
            }
        });

        SL_ALPHA.setOrientation(javax.swing.JSlider.VERTICAL);
        SL_ALPHA.setOpaque(false);
        SL_ALPHA.addChangeListener(new javax.swing.event.ChangeListener()
        {
            public void stateChanged(javax.swing.event.ChangeEvent evt)
            {
                SL_ALPHAStateChanged(evt);
            }
        });

        javax.swing.GroupLayout PN_EDITORLayout = new javax.swing.GroupLayout(PN_EDITOR);
        PN_EDITOR.setLayout(PN_EDITORLayout);
        PN_EDITORLayout.setHorizontalGroup(
            PN_EDITORLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PN_EDITORLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(TXT_EDITOR, javax.swing.GroupLayout.DEFAULT_SIZE, 348, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SL_ALPHA, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        PN_EDITORLayout.setVerticalGroup(
            PN_EDITORLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_EDITORLayout.createSequentialGroup()
                .addGroup(PN_EDITORLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PN_EDITORLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(TXT_EDITOR, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(SL_ALPHA, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        PN_KEYS.add(PN_EDITOR, new org.netbeans.lib.awtextra.AbsoluteConstraints(180, 10, 390, 40));
        PN_KEYS.add(LB_EMPTY, new org.netbeans.lib.awtextra.AbsoluteConstraints(60, 20, -1, -1));

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        add(PN_KEYS, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void SL_ALPHAStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_SL_ALPHAStateChanged
        // TODO add your handling code here:
        int pc_alpha = SL_ALPHA.getValue();
        this.setAlpha(pc_alpha / 100.0f);
        Main.set_long_prop(Preferences.VKEY_ALPHA, pc_alpha);
        Main.get_prefs().store_props();
}//GEN-LAST:event_SL_ALPHAStateChanged

    private void TXT_EDITORMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_TXT_EDITORMouseClicked
        // TODO add your handling code here:
        old_caret_pos = TXT_EDITOR.getCaretPosition();
        
    }//GEN-LAST:event_TXT_EDITORMouseClicked
    
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel LB_EMPTY;
    private javax.swing.JPanel PN_EDITOR;
    private javax.swing.JPanel PN_KEYS;
    private javax.swing.JSlider SL_ALPHA;
    private javax.swing.JTextField TXT_EDITOR;
    // End of variables declaration//GEN-END:variables
    
}
