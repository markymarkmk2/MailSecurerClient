/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dimm.home.Rendering;

/**
 *
 * @author Administrator
 */
import dimm.home.Main;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;

// �1998 Sebastian Wallroth

import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.Timer;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

public class Radar extends JComponent implements ActionListener
{
    private static final int TICK_MS = 10;
    private static final int DECAY_LEN = 31;
    private static final int DECAY_SECTOR_WIDTH = 6;
    
//    private static final int DECAY_LEN = 51;
    private static int size = 100;
    private BufferedImage RadarFrameBuffer;
    private BufferedImage RadarPulsBuffer;
    private BufferedImage PulsBlurBuffer;
    // RescaleOp op;
    private Graphics2D gRadarFrameBuffer;

    double angle_per_tick = 2;
    double act_angle = 0;

    Color color[] = new Color[DECAY_LEN];
    Color rdr_green = new Color( 10, 245, 5 );
    Color rdr_lbrown = new Color( 205, 175, 95 );
    Color rdr_red = new Color(245, 10, 5);
    Color rdr_dark_gray = new Color(15, 15, 15);
    
    Composite acomp;
    Timer timer;
    ImageIcon ic;
    ImageIcon ich;
    Font pc_font = new Font( Font.SANS_SERIF, Font.PLAIN, 10 );

    @InjectedResource
    private Color start_color = new Color( 205, 175, 95 );
    @InjectedResource
    private Color ping_color = new Color(245, 10, 5);
    @InjectedResource
    private Color back_color = new Color( 80, 80, 80 );
    @InjectedResource
    private Color grid_color = new Color( 80, 80, 80 );
    @InjectedResource
    private static float decay_dimm = 0.4f;


    double percent_val;
    boolean with_percent;

    public Radar()
    {
        this(  false );
    }
    
    public Radar( boolean _with_percent)
    {
        try
        {
            ResourceInjector.get().inject(this);
        }
        catch (Exception e)
        {
            e = null;
        }

       

        with_percent = _with_percent;
      
        
        init();

        timer = new Timer(TICK_MS, this);

        ic = new ImageIcon(this.getClass().getResource("/dimm/home/images/radarframe.png"));
        size = (ic.getIconHeight() * 80) / 100 + 5;
        ich = new ImageIcon(this.getClass().getResource("/dimm/home/images/radarhighlight.png"));

        start_time = System.currentTimeMillis();
        percent_val = last_percent_val;
        last_percent_tick = 0;
    }
    
    long last_percent_tick;
    double last_percent_val;
    long start_time = 0;
    public void set_percent( double pc )
    {
        with_percent = true;

        // DEFAULT IS SLOMO
        angle_per_tick = 0.1;

        long now = System.currentTimeMillis();

        if (pc == 0.0)
            start_time = now;

        if (pc == percent_val)
        {
            last_percent_tick = System.currentTimeMillis();
            return;
        }
        if (pc <= percent_val)
        {
            last_percent_tick = System.currentTimeMillis();
            percent_val = pc;
            return;
        }

        percent_val = pc;
        double rest_angle = 360 - act_angle;
        double rest_percent = 100 - pc;


        long time_from_start = now - start_time;

        double rest_time = time_from_start * rest_percent / pc ;

        long rest_ticks = (long)(rest_time / TICK_MS);
        if (rest_ticks <= 0)
            rest_ticks = 1;


        angle_per_tick = rest_angle / rest_ticks;
        if (angle_per_tick > 5)
            angle_per_tick = 5;

    }

   /* public void set_percent( double pc )
    {
        with_percent = true;
        
        // DEFAULT IS SLOMO
        angle_per_tick = 0.1;



        if (pc == percent_val)
        {
            last_percent_tick = System.currentTimeMillis();
            return;
        }
        if (pc <= percent_val)
        {
            last_percent_tick = System.currentTimeMillis();
            percent_val = pc;
            return;
        }


        double real_angle =  360.0 * pc / 100 ;

        System.out.println("S PC: " + pc + " PV:" + percent_val + " APT:" + angle_per_tick + " AA:" + act_angle + " RA:" + real_angle + " DT:" + 0 + " DPC:" + 0);

        // IF WE ARE TOO BIG, JUST SLOW DOWN AND SHOW CORRECT PC 
        if (real_angle < act_angle)
        {
            last_percent_tick = System.currentTimeMillis();
            percent_val = pc;
            return;
        }

        double real_percent_val = act_angle * 100 / 360;

        long now = System.currentTimeMillis();

        double d_pc = pc - real_percent_val;

        if (d_pc < 0)
        {
            last_percent_tick = System.currentTimeMillis();
            percent_val = pc;
            return;
        }

        if (d_pc == 0.0)
            d_pc = 0.01;

        // TIME SINCE LAST SAMPLE
        long d_ticks = now - last_percent_tick;
        if (d_ticks == 0)
            d_ticks = 1;

        // PERCENT PER SECOND
        double pc_per_s = (d_pc * 1000) / d_ticks;


        // TICK KOMMT ALLE 10 ms
        // PERCENT JE TICK
        double pc_per_tick = (pc_per_s * TICK_MS) / 1000;

        angle_per_tick = (pc_per_tick * 360 / 100);
        percent_val = pc;
        last_percent_tick = now;
        //act_angle = pc * 360.0 / 100;

        System.out.println("E PC: " + pc + " RPV:" + real_percent_val + " APT:" + angle_per_tick + " AA:" + act_angle + " DT:" + d_ticks + " DPC:" + d_pc);
    }*/

    @Override
    public int getWidth()
    {
        return size;
    }

    @Override
    public int getHeight()
    {
        return size;
    }

    @Override
    public Dimension getSize()
    {
        return new Dimension(size, size);
    }

    public void start()
    {
        timer.start();
        act_angle = 1;
    }

    public void stop()
    {
        timer.stop();
    }

    @Override
    public void update(Graphics g)
    {
        paint(g);
    }

    public void init()
    {
        color[0] = start_color;
        for (int i = 1; i < DECAY_LEN; i++)
        {
            int r = start_color.getRed() - (start_color.getRed() * i) / DECAY_LEN;
            int g = start_color.getGreen() - (start_color.getGreen() * i) / DECAY_LEN;
            int b = start_color.getBlue() - (start_color.getBlue() * i) / DECAY_LEN;
            r = (int) (r * decay_dimm);
            g = (int) (g * decay_dimm);
            b = (int) (b * decay_dimm);
            if (r < back_color.getRed())
            {
                r = back_color.getRed();
            }
            if (g < back_color.getGreen())
            {
                g = back_color.getGreen();
            }
            if (b < back_color.getBlue())
            {
                b = back_color.getBlue();
            }
            color[i] = new Color(r, g, b);
        }
        // DARKER THAN RDR-BEAM
        //grid_color = color[5];
    }
    int get_angle_int(double a)
    {
        return (int)(a + 0.5);
    }

    @Override
    public void paint(Graphics g)
    {
        //gBuffer = g;
        int ds = 5;
        int rx = ds;
        int ry = ds - 1;
        int rw = size - 2 * ds;
        int rh = size - 2 * ds;
        double angle = 360 - act_angle + 90;


        if (RadarFrameBuffer == null)
        {
            RadarFrameBuffer = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
            RadarPulsBuffer = new BufferedImage(size, size, BufferedImage.TYPE_4BYTE_ABGR);
            PulsBlurBuffer = new BufferedImage(size / 2, size / 2, BufferedImage.TYPE_4BYTE_ABGR);
            gRadarFrameBuffer = (Graphics2D) RadarFrameBuffer.getGraphics();
            
            // PAINT FRAME ONCE
            gRadarFrameBuffer.drawImage(ic.getImage(), 0, 0, size, size, this);
        }

        // PAINT RADAR PULS
        Graphics2D gRadarPulsBuffer = (Graphics2D) RadarPulsBuffer.getGraphics();
        Graphics2D gRadarBlurBuffer = (Graphics2D) PulsBlurBuffer.getGraphics();

        gRadarPulsBuffer.setColor(back_color);
        gRadarPulsBuffer.fillOval(rx, ry, rw, rh);

        // BLIP RED ON SCTRAIGHT UP
        if (angle > 85 && angle < 95)
        {
            gRadarPulsBuffer.setColor(ping_color);
        }
        else
        {
            gRadarPulsBuffer.setColor(start_color);            
        }
        
        // MAIN LINE
        gRadarPulsBuffer.fillArc(rx, ry, rw, rh, get_angle_int(angle) + DECAY_SECTOR_WIDTH - 3, 3);

        // AFTERGLOW
        for (int i = 1; i < DECAY_LEN; i++)
        {
            gRadarPulsBuffer.setColor(color[i]);
            gRadarPulsBuffer.fillArc(rx, ry, rw, rh, get_angle_int(angle) + i * DECAY_SECTOR_WIDTH, DECAY_SECTOR_WIDTH);
        }
        
        // GRID
        gRadarPulsBuffer.setColor(grid_color);
        gRadarPulsBuffer.drawOval(size / 2 - 10, size / 2 - 10, 20, 20);
        gRadarPulsBuffer.drawOval(size / 2 - 24, size / 2 - 24, 48, 48);
        //gRadarPulsBuffer.setColor(grid_color);
        gRadarPulsBuffer.drawLine(0, size / 2, size, size / 2);
        gRadarPulsBuffer.drawLine( size / 2, 0, size / 2, size);
        
        // ADD HIGHLIGHT AFTER RADAR
        gRadarPulsBuffer.drawImage(ich.getImage(), 2, 2, size, size, this);

        // PAINT AND SCALE DOWN
        gRadarBlurBuffer.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        gRadarBlurBuffer.drawImage(RadarPulsBuffer, 0, 0, size / 2, size / 2, null);
        
        // PAINT BACK TO PULS BUFFER AND SCALE UP
        gRadarPulsBuffer.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        gRadarPulsBuffer.setComposite(AlphaComposite.SrcIn);
        gRadarPulsBuffer.drawImage(PulsBlurBuffer, 0, 0, size, size, null);
        
        // PAINT TO SCREENBUFFER
        Graphics2D g2 = (Graphics2D) g;
        g2.drawImage(RadarPulsBuffer, 0, 0, this);
        g2.drawImage(RadarFrameBuffer, 0, 0, this);

        if (with_percent)
        {
            g2.setColor(Main.ui.get_nice_white());
            String pc_str = "" + (int)(percent_val + 0.5) + "%";
            FontRenderContext frc = g2.getFontRenderContext();
            TextLayout tl = new TextLayout(pc_str, pc_font, frc);
            Rectangle2D rr = tl.getBounds();
            tl.draw(g2, (float)(size/2  - rr.getWidth() / 2) , (float)(size*3 / 4 - rr.getHeight()/2 ));
            //g2.drawString( pc_str, DECAY_DIMM, DECAY_DIMM);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e)
    {

        act_angle += angle_per_tick;

        if (with_percent)
        {
            if (act_angle > 360)
            {
                act_angle -= angle_per_tick;
                return;
            }
        }
        else
        {
            if (act_angle >= 360)
            {
                act_angle = 0;
            }
        }
        repaint();
    }
}
