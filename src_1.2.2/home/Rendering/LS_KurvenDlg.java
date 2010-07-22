/*
 * LS_Kurven_Dialog.java
 *
 * Created on 24. Maerz 2009, 21:42
 */

package dimm.home.Rendering;

import dimm.home.Main;
import dimm.home.UserMain;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JTextField;


class DbTimePoint
{
    private int level;
    private int minutes;
    
    public static int MINUTE_RASTER = 5;
    public static int LEVEL_RASTER = 2;
    
    DbTimePoint( int s, int l )
    {
        level = l;
        
        minutes = s;
    }
    int get_level()
    {
        return ((level + LEVEL_RASTER - 1) /LEVEL_RASTER) * LEVEL_RASTER;
    }
    int get_minutes()
    {
        return ((minutes + MINUTE_RASTER - 1) / MINUTE_RASTER) * MINUTE_RASTER;        
    }
    void  set_level( int l )
    {
        level = l;
    }   
    boolean  is_exact(DbTimePoint tp )
    {
        if (tp.get_level() == get_level() && tp.get_minutes() == get_minutes())
            return true;
        
        return false;
    }
    boolean  is_same_time(DbTimePoint tp )
    {
        if (tp.get_minutes() == get_minutes())
            return true;
        
        return false;
    }
        
}


class LS_Achse extends JPanel implements MouseMotionListener
{
    Color clr_legend_bg;
    Color clr_legend_fg;
    Color clr_cursor;
    int legend_height = 25;
    int legend_width = 25;
    int margin = 5;
    Font fnt_legend = new Font(Font.SANS_SERIF , Font.PLAIN, 9 );
    BufferedImage bf_legend = null;
    boolean is_x_axis;
    LS_Panel pnl;
    DbTimePoint last_cursor_pt = new DbTimePoint( 0, 0);
    

    LS_Achse(LS_Panel _pnl, boolean _is_x_axis)
    {
        pnl = _pnl;
        is_x_axis = _is_x_axis;
        clr_legend_bg = Color.black;
        clr_legend_fg = Main.ui.get_foreground();
        clr_cursor = Main.ui.get_table_header_color();
    }
    @Override
    public void setBounds(int x, int y, int width, int height)
    {
        bf_legend = null;
        super.setBounds(x, y, width, height);
    }
    @Override
    public void paint(Graphics g)
    {
        //super.paint(g);
        if (bf_legend == null)
        {
            bf_legend = new BufferedImage( this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB );
            Graphics2D gbf = bf_legend.createGraphics();
            gbf.setColor(clr_legend_bg);
            gbf.fillRect(0, 0, this.getWidth(), getHeight());
            gbf.setColor(clr_legend_fg);
         
            gbf.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            gbf.setFont(fnt_legend);

            if (is_x_axis)
            {
                int xxi = 1;
                int dx = (getWidth() - 2* margin) / 24;
                int rdx = dx;
                while (rdx < 18)
                {
                    xxi++;
                    if (rdx == 0)
                        rdx = 1;                        
                    rdx*=2;
                }
                
                for (int i = 0; i < 24; i+= xxi)
                {              
                    
                    String h = Integer.toString(i);                    
                    int hw = gbf.getFontMetrics().stringWidth(h);
                    int ddx = (i* (getWidth() - 2* margin)) / 24;
                    
                    gbf.drawString( h, margin +  ddx - hw/2, legend_height - 7);
                    if (dx > 10)
                    {
                        int x = margin +  ddx;
                        int lh = 1;
                        if (dx > 20)
                            lh = 3;
                        
                        gbf.drawLine( x , legend_height - 2, x, legend_height - 2 - lh);
                        if (dx > 15 && xxi == 1)
                        {
                            x = margin +  ddx + dx/2;
                            gbf.drawLine( x , legend_height - 2, x, legend_height - 2 - lh);
                        }
                    }
                    if (dx > 25 && xxi == 1)
                    {
                        int x = margin +  ddx + dx/4;
                        gbf.drawLine( x , legend_height - 2, x, legend_height - 3);
                        x = margin +  ddx + dx/4 + dx/2;
                        gbf.drawLine( x , legend_height - 2, x, legend_height - 3);
                    }
                }
                
            }
            else
            {
                int dy = (getHeight() - 2* margin) / 10;
                String l;
                
                int yyi = 1;
                
                int rdy = dy;
                while (rdy < 18)
                {
                    yyi++;
                    if (rdy == 0)
                        rdy = 1;                        
                    rdy*=2;
                }
                
                for (int i = 0; i <= 10; i += yyi)
                {   
                    if ( i <= 9)
                        l = " " +Integer.toString(i*10);
                    else
                        l = Integer.toString(i*10);
                    
                    int y = pnl.level_to_pixel( i*10 );

                    gbf.drawString( l, 2, y + 5);
                    
                    if (dy > 10)
                    {
                        int lw = 1;
                        if (dy > 20)
                            lw = 3;
                        
                        gbf.drawLine( legend_width - 2 - lw , y, legend_height - 2, y);
                        if (dy > 15 &&  yyi == 1)
                        {
                            y = pnl.level_to_pixel( i*10 + 5 );
                            gbf.drawLine( legend_width - 2 - lw , y, legend_height - 2, y);
                        }
                    }
                    if (dy > 24 && yyi == 1)
                    {
                        y = pnl.level_to_pixel( i*10 + 2.5f );
                        gbf.drawLine( legend_width - 2 - 1 , y, legend_height - 2, y);
                        y = pnl.level_to_pixel( i*10 + 7.5f );
                        gbf.drawLine( legend_width - 2 - 1 , y, legend_height - 2, y);
                    }
                }                                    
            }
        }
    
        Rectangle cl = g.getClipBounds();
        if (cl != null)
        {            
            // ADD LEGEND
            draw_buff_rect( g, bf_legend, cl );            
        }
        else
        {
            // ADD LEGEND
            g.drawImage(bf_legend, 0, 0, null);
        }
        
        if (pnl.is_enable_mouse())
        {
            DbTimePoint pt = pnl.get_cursor_pt();
            int x = pnl.minutes_to_pixel( pt.get_minutes() );
            int y = pnl.level_to_pixel( pt.get_level() );
            
            last_cursor_pt = pt;        
            
            g.setColor(clr_cursor);
            if (is_x_axis)
            {
                g.drawLine(x, getHeight() - 10, x, getHeight());
            }
            else
            {
                g.drawLine( getWidth() - 10, y, getWidth(), y);
            }                        
        }        
    }
    void draw_buff_rect( Graphics g, BufferedImage b, Rectangle cl)
    {            
            g.drawImage(b, cl.x, cl.y, cl.x + cl.width, cl.y + cl.height, cl.x, cl.y, cl.x + cl.width, cl.y + cl.height,null);        
    }

    public void mouseDragged(MouseEvent e)
    {
        
    }

    public void mouseMoved(MouseEvent e)
    {
        if (pnl.is_enable_mouse())
        {
            DbTimePoint pt = pnl.get_cursor_pt();
            int x = pnl.minutes_to_pixel( pt.get_minutes() );
            int y = pnl.level_to_pixel( pt.get_level() );
            
            
            Rectangle r = pnl.point_to_rect( pt );
            Rectangle last_r = pnl.point_to_rect( last_cursor_pt );

            r.add(last_r);
            r.grow(r.width, r.height );
            
            
            if (is_x_axis)
            {
                Rectangle rr = new Rectangle( r.x, 0, r.width, getHeight() );
                repaint( rr );
            }
            else
            {
                Rectangle rr = new Rectangle( 0, r.y, getWidth(), r.height);
                repaint( rr );
            }
        }
        else
        {
            repaint();
        }
    }
    
}
class LS_Panel extends JPanel implements MouseListener, MouseMotionListener
{
    int max_x_minutes;
    Color clr_back = new Color( 20,20,20);
    Color clr_line_fg;
    int legend_height = 5;
    int legend_width = 5;
    BufferedImage bf_line = null;
    
    ArrayList<DbTimePoint> point_list;
    
    JTextField TXT_POS;
    DbTimePoint cursor_pt;
    boolean  enable_mouse = false;
    
    
    LS_Panel(JTextField _TXT_POS)
    {
        TXT_POS = _TXT_POS;
        clr_back = Main.ui.get_background();
        clr_line_fg = Main.ui.get_table_header_color();
        
        setBackground(clr_back);
        
        cursor_pt = new DbTimePoint( 0,0);
        last_cursor_pt = cursor_pt;
        
        max_x_minutes = 24*60;
        
        addMouseListener(this);
        addMouseMotionListener(this);
    }
    void set_point_list(ArrayList<DbTimePoint> _point_list)
    {
        point_list = _point_list;
    }
    boolean is_enable_mouse()
    {
        return enable_mouse;
    }
    DbTimePoint get_cursor_pt()
    {
        return cursor_pt;
    }


    @Override
    public void setBounds(int x, int y, int width, int height)
    {
        bf_line = null;
        super.setBounds(x, y, width, height);
    }

    
    Rectangle point_to_rect(DbTimePoint pt)
    {
        int x = minutes_to_pixel( pt.get_minutes() );
        int y = level_to_pixel( pt.get_level()  ) ;
        int dx = minutes_to_pixel( pt.get_minutes() + DbTimePoint.MINUTE_RASTER ) - x;
        int dy = y - level_to_pixel( pt.get_level() + DbTimePoint.LEVEL_RASTER );
        int r = dx;
        if (r < dy)
            r = dy;
        
        if (r > 15)
            r = 15;
        
        if ((r & 1) == 1)
            r--;
        return new Rectangle( x, y + r/2, r, r );
    }

    void draw_buff_rect( Graphics g, BufferedImage b, Rectangle cl)
    {
            
            g.drawImage(b, cl.x, cl.y, cl.x + cl.width, cl.y + cl.height, cl.x, cl.y, cl.x + cl.width, cl.y + cl.height,null);        
    }

    @Override
    public void paint(Graphics g)
    {
        if (bf_line == null)
        {
            bf_line = new BufferedImage( this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_ARGB );
            Graphics2D g2 = bf_line.createGraphics();
            
            g2.setColor(clr_back);
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);        
            g2.setColor(clr_line_fg);
            

            // LEGENDE IN BUFFER MALEN UND DRAUF LADEN
            int last_x = minutes_to_pixel( 0 );
            int last_y = level_to_pixel( 100 );
            
            if (point_list.size() > 0)
                last_y = level_to_pixel( point_list.get(0).get_level() );
                        

            for (int i = 0; i < point_list.size(); i++)
            {
                DbTimePoint pt = point_list.get(i);

                /*// CHECK FOR FIRST POINT
                if (i == 0 && pt.get_minutes() == 0)
                {
                    last_y = level_to_pixel( pt.get_level() );
                    continue;
                }
*/
                int x = minutes_to_pixel( pt.get_minutes()  );
                int y = level_to_pixel( pt.get_level()  );
                g2.setColor(clr_line_fg);
                g2.drawLine(last_x, last_y, x, y );
                last_x = x;
                last_y = y;

                g2.setColor(Color.yellow);
                g2.drawLine(x - 1, y, x + 1, y);
                g2.drawLine(x, y - 1, x, y + 1);

                // APPEND LAST POINT IF NECESSARY
                if (i == point_list.size() - 1)
                {
                    if (pt.get_minutes() < max_x_minutes)
                    {
                        x = minutes_to_pixel( max_x_minutes  );
                        y = level_to_pixel( pt.get_level() );
                        g2.setColor(clr_line_fg);
                        g2.drawLine(last_x, last_y, x, y );
                        g2.setColor(Color.yellow);
                        g2.drawLine(x - 1, y, x + 1, y);
                        g2.drawLine(x, y - 1, x, y + 1);
                    }
                }
            }        
        }
        Rectangle cl = g.getClipBounds();
        if (cl != null)
        {
            
            // ADD LEGEND
            draw_buff_rect( g, bf_line, cl );
            
        }
        else
        {
            // ADD LEGEND
            g.drawImage(bf_line, 0, 0, null);
        }
        
        
        if (enable_mouse)
        {
            Rectangle r = point_to_rect( cursor_pt );
            last_cursor_pt = cursor_pt;        

            g.setColor(Main.ui.get_foreground());

            g.drawOval(r.x - r.width/2, r.y - r.height, r.width, r.height);

        }
    }
    void rout( String s, Rectangle r )
    {
        System.out.println(s + ": " + r.x + "/" + r.y + " " + r.width + "/" + r.height);
    }

     int level_to_pixel(int l)
    {
        int y = (l * (this.getHeight() - 2*legend_height)) / 100; 
        if (y == 0)
            y = 1;
        return  this.getHeight() - y - legend_height;         
   }
     int level_to_pixel(float l)
    {
        int y = (int)((l * (this.getHeight() - 2*legend_height)) / 100 + 1); 
        if (y == 0)
            y = 1;
        return  this.getHeight() - y - legend_height;         
   }
     int level_to_dpixel(int l)
    {
        int y = (l * (this.getHeight() - 2*legend_height)) / 100; 
        if (y == 0)
            y = 1;
        return  y;         
   }
     int pixel_to_level(int y)
    {
        y -= legend_height;
        int l = (y * 100) /  (this.getHeight() - 2*legend_height);
        return 100 - l;
    }
    

     int minutes_to_pixel(int seconds)
    {
        int x = (seconds * (this.getWidth() - 2*legend_width)) / max_x_minutes;
        return x + legend_width; 
    }
     int minutes_to_dpixel(int seconds)
    {
        int x = (seconds * (this.getWidth() - 2*legend_width)) / max_x_minutes;
        return x; 
    }
     int pixel_to_minutes(int x)
    {
        int p = ((x- legend_width) * max_x_minutes) / (this.getWidth() - 2*legend_width);
        return p;
    }

    public void mouseClicked(MouseEvent e)
    {
    }
    
    void clean_point_list()
    {
        boolean done = false;
        while (!done)
        {
            done = true;
            for (int i = 1; i < point_list.size() - 1; i++)
            {
                DbTimePoint pt1 = point_list.get(i-1);
                DbTimePoint pt2 = point_list.get(i);
                DbTimePoint pt3 = point_list.get(i+1);

                int dy13 = pt3.get_level() - pt1.get_level();
                int dx13 = pt3.get_minutes() - pt1.get_minutes();
                int dy12 = pt2.get_level() - pt1.get_level();
                int dx12 = pt2.get_minutes() - pt1.get_minutes();

                if (dx13 > 0)
                {
                    int y2 = pt1.get_level() + dx12 * dy13 / dx13;
                    if (pt2.get_level() == y2)
                    {
                        point_list.remove(i);
                        done = false;
                        break;
                    }
                }
            }
        }
    }
    public void mousePressed(MouseEvent e)
    {
        int level = pixel_to_level( e.getY() ); 
        int minutes = pixel_to_minutes( e.getX() ); 
        cursor_pt = new DbTimePoint( minutes, level );
        
        DbTimePoint cpt = new DbTimePoint( minutes, level );
        
        boolean done = false;
        for (int i = 0; i < point_list.size(); i++)
        {
            DbTimePoint pt = point_list.get(i);
            
            if (cpt.is_same_time(pt))
            {
                if (cpt.is_exact(pt))
                {
                    point_list.remove(i);
                    done = true;
                }
            }
        }
        if (!done)
        {
            for (int i = 0; i < point_list.size(); i++)
            {
                DbTimePoint pt = point_list.get(i);

                if (cpt.is_same_time(pt))
                {
                    //pt.set_level(level);
                    point_list.add(i, cpt);
                    done = true;
                    break;
                }
                if (pt.get_minutes() > cpt.get_minutes())
                {
                    point_list.add(i, cpt);
                    done = true;
                    break;
                }
            }
        }
        if (!done)
        {
            point_list.add(cpt);
        }
            
        clean_point_list();
        bf_line = null;
        repaint();
            
              
    }

    public void mouseReleased(MouseEvent e)
    {
        
    }

    Cursor dlft_cursor;
    public void mouseEntered(MouseEvent e)
    {
        dlft_cursor = Cursor.getDefaultCursor();
        int[] pixels = new int[16 * 16];
        Toolkit tk = Toolkit.getDefaultToolkit();
        Image image = tk.createImage(new MemoryImageSource(16, 16, pixels, 0, 16));
        Cursor transparentCursor = tk.createCustomCursor(image, new Point(0, 0), "invisiblecursor");
        setCursor(transparentCursor);        
        
        int level = pixel_to_level( e.getY() ); 
        int minutes = pixel_to_minutes( e.getX() ); 
        cursor_pt = new DbTimePoint( minutes, level );
        
        repaint();
    }

    public void mouseExited(MouseEvent e)
    {
        setCursor( dlft_cursor);
        enable_mouse = false;
        repaint();

    }

    public void mouseDragged(MouseEvent e)
    {
        mousePressed( e );
    }

    boolean skip_next_event;
    DbTimePoint last_cursor_pt;
    public void mouseMoved(MouseEvent e)
    {
        enable_mouse = false;
//        if (e.getX() >= legend_width && e.getX() <= getWidth() - legend_width && 
//                e.getY() >= legend_height && e.getY() <= getHeight() - legend_height)
        if (e.getX() >= 0 && e.getX() <= getWidth() - 0 && 
                e.getY() >= 0 && e.getY() <= getHeight() - 0)
        {
            enable_mouse = true;
        }
        
        int level = pixel_to_level( e.getY() ); 
        int minutes = pixel_to_minutes( e.getX() ); 
        cursor_pt = new DbTimePoint( minutes, level );
        
//        System.out.println("AX: "+ e.getXOnScreen() + " AY: " + e.getYOnScreen());
//        System.out.println("RX: "+ e.getX() + " RY: " + e.getY());
//        System.out.println("M: "+ cursor_pt.get_minutes() + " L: " + cursor_pt.get_level());
        int h = cursor_pt.get_minutes() / 60;
        int m = cursor_pt.get_minutes() % 60;
        StringBuffer sb = new StringBuffer();

        if (h < 10)
        {
            sb.append("0");
        }
        sb.append(h);
        sb.append(":");
        if (m < 10)
        {
            sb.append("0");
        }
        sb.append(m);


        sb.append(" -> ");
        if (cursor_pt.get_level() < 10)
        {
            sb.append(" ");
        }
        if (cursor_pt.get_level() < 100)
        {
            sb.append(" ");
        }
        sb.append(cursor_pt.get_level());
        sb.append("%");
        TXT_POS.setText(sb.toString());

        Rectangle r = point_to_rect( cursor_pt );
        Rectangle last_r = point_to_rect( last_cursor_pt );
        
        r.add(last_r);
        r.grow(r.width, r.height );
       
    
        if (r.width > 0 || r.height > 0)
        {
            rout( "repa", r );

            repaint(r);
//            repaint();
            
        }        
    }
      
}
/**
 
 @author  Administrator
 */
public class LS_KurvenDlg extends javax.swing.JDialog
{
    
    /** Creates new form LS_Kurven_Dialog */
    public LS_KurvenDlg(java.awt.Frame parent, boolean modal)
    {
        super(parent, modal);
        initComponents();
        
        COMBO_MIN_RASTER.removeAllItems();
        COMBO_MIN_RASTER.addItem("1");
        COMBO_MIN_RASTER.addItem("5");
        COMBO_MIN_RASTER.addItem("10");
        COMBO_MIN_RASTER.addItem("15");
        COMBO_MIN_RASTER.addItem("30");
        COMBO_MIN_RASTER.addItem("60");
        COMBO_MIN_RASTER.setSelectedIndex(1);
        DbTimePoint.MINUTE_RASTER = 5;

        COMBO_LVL_RASTER.removeAllItems();
        COMBO_LVL_RASTER.addItem("1");
        COMBO_LVL_RASTER.addItem("2");
        COMBO_LVL_RASTER.addItem("5");
        COMBO_LVL_RASTER.addItem("10");
        COMBO_LVL_RASTER.setSelectedIndex(1);
        DbTimePoint.LEVEL_RASTER = 2;
        
        ArrayList<DbTimePoint> point_list = new ArrayList<DbTimePoint>();
        
        point_list.add( new DbTimePoint( 0, 80 ) );
        point_list.add( new DbTimePoint( 1*60, 50 ) );
        point_list.add( new DbTimePoint( 3*60, 50 ) );
        point_list.add( new DbTimePoint( 3*60, 0 ) );
        point_list.add( new DbTimePoint( 8*60, 0 ) );
        point_list.add( new DbTimePoint( 8*60, 90 ) );
        point_list.add( new DbTimePoint( 12*60, 90 ) );
        point_list.add( new DbTimePoint( 18*60, 20 ) );
        point_list.add( new DbTimePoint( 19*60, 20 ) );
        point_list.add( new DbTimePoint( 23*60, 100 ) );
        point_list.add( new DbTimePoint( 24*60, 100 ) );
        
        
        LS_Panel pnl = new LS_Panel(TXT_POS);
        pnl.set_point_list(point_list);
        
        PN_LINE.add(pnl);
        LS_Achse x_axis = new LS_Achse( pnl, true );
        LS_Achse y_axis = new LS_Achse( pnl, false);
        PN_XAXIS.add ( x_axis ); 
        PN_YAXIS.add ( y_axis ); 
        pnl.addMouseMotionListener(x_axis);
        pnl.addMouseMotionListener(y_axis);
        
        pack();
        
        
    }
    
    /** This method is called from within the constructor to
     initialize the form.
     WARNING: Do NOT modify this code. The content of this method is
     always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents()
    {

        jPanel1 = new javax.swing.JPanel();
        PN_KURVE = new javax.swing.JPanel();
        PN_YAXIS = new javax.swing.JPanel();
        PN_XAXIS = new javax.swing.JPanel();
        PN_LINE = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        COMBO_MIN_RASTER = new javax.swing.JComboBox();
        COMBO_LVL_RASTER = new javax.swing.JComboBox();
        TXT_POS = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        PN_KURVE.setBackground(new java.awt.Color(0, 0, 0));

        PN_YAXIS.setLayout(new javax.swing.BoxLayout(PN_YAXIS, javax.swing.BoxLayout.LINE_AXIS));

        PN_XAXIS.setLayout(new javax.swing.BoxLayout(PN_XAXIS, javax.swing.BoxLayout.LINE_AXIS));

        PN_LINE.setLayout(new javax.swing.BoxLayout(PN_LINE, javax.swing.BoxLayout.LINE_AXIS));

        javax.swing.GroupLayout PN_KURVELayout = new javax.swing.GroupLayout(PN_KURVE);
        PN_KURVE.setLayout(PN_KURVELayout);
        PN_KURVELayout.setHorizontalGroup(
            PN_KURVELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_KURVELayout.createSequentialGroup()
                .addComponent(PN_YAXIS, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(PN_KURVELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PN_LINE, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)
                    .addComponent(PN_XAXIS, javax.swing.GroupLayout.DEFAULT_SIZE, 449, Short.MAX_VALUE)))
        );
        PN_KURVELayout.setVerticalGroup(
            PN_KURVELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PN_KURVELayout.createSequentialGroup()
                .addComponent(PN_XAXIS, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(PN_KURVELayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PN_LINE, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)
                    .addComponent(PN_YAXIS, javax.swing.GroupLayout.DEFAULT_SIZE, 335, Short.MAX_VALUE)))
        );

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));

        COMBO_MIN_RASTER.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        COMBO_MIN_RASTER.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                COMBO_MIN_RASTERActionPerformed(evt);
            }
        });

        COMBO_LVL_RASTER.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        COMBO_LVL_RASTER.addActionListener(new java.awt.event.ActionListener()
        {
            public void actionPerformed(java.awt.event.ActionEvent evt)
            {
                COMBO_LVL_RASTERActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(COMBO_MIN_RASTER, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(COMBO_LVL_RASTER, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(193, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(COMBO_MIN_RASTER, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(COMBO_LVL_RASTER, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        TXT_POS.setEditable(false);
        TXT_POS.setForeground(new java.awt.Color(255, 153, 0));
        TXT_POS.setText("jTextField1");
        TXT_POS.setBorder(null);
        TXT_POS.setOpaque(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(PN_KURVE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(TXT_POS, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addComponent(PN_KURVE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(TXT_POS, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(0, 0, 0))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void COMBO_MIN_RASTERActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_COMBO_MIN_RASTERActionPerformed
    {//GEN-HEADEREND:event_COMBO_MIN_RASTERActionPerformed
        // TODO add your handling code here:
        try
        {
            if (COMBO_MIN_RASTER.getSelectedItem() != null)
            {
                int r = Integer.parseInt(COMBO_MIN_RASTER.getSelectedItem().toString());
                DbTimePoint.MINUTE_RASTER = r;
            }
        }
        catch (NumberFormatException numberFormatException)
        {
        }
        
}//GEN-LAST:event_COMBO_MIN_RASTERActionPerformed

    private void COMBO_LVL_RASTERActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_COMBO_LVL_RASTERActionPerformed
    {//GEN-HEADEREND:event_COMBO_LVL_RASTERActionPerformed
        // TODO add your handling code here:
        try
        {
            if (COMBO_LVL_RASTER.getSelectedItem() != null)
            {
                int r = Integer.parseInt(COMBO_LVL_RASTER.getSelectedItem().toString());
                DbTimePoint.LEVEL_RASTER = r;
            }
        }
        catch (NumberFormatException numberFormatException)
        {
        }
        
}//GEN-LAST:event_COMBO_LVL_RASTERActionPerformed
    
    /**
     @param args the command line arguments
     */
    public static void main(String args[])
    {
        java.awt.EventQueue.invokeLater(new Runnable()
        {
            public void run()
            {
                LS_KurvenDlg dialog = new LS_KurvenDlg(new javax.swing.JFrame(), true);
                dialog.addWindowListener(new java.awt.event.WindowAdapter()
                {
                    public void windowClosing(java.awt.event.WindowEvent e)
                    {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox COMBO_LVL_RASTER;
    private javax.swing.JComboBox COMBO_MIN_RASTER;
    private javax.swing.JPanel PN_KURVE;
    private javax.swing.JPanel PN_LINE;
    private javax.swing.JPanel PN_XAXIS;
    private javax.swing.JPanel PN_YAXIS;
    private javax.swing.JTextField TXT_POS;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    // End of variables declaration//GEN-END:variables
    
}
