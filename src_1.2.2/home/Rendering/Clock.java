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
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;

// �1998 Sebastian Wallroth

import java.awt.event.ActionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.Timer;
import org.jdesktop.fuse.ResourceInjector;

public class Clock extends JComponent implements ActionListener
{
    private static final int TICK_MS = 10;
    
    private static Dimension size = new Dimension(100,100);
    private BufferedImage frame_buffer;
    // RescaleOp op;
    private Graphics2D gRadarFrameBuffer;
   
    
    Composite acomp;
    Timer timer;
    ImageIcon ic;
    ImageIcon ich;
    
    Font pc_font = new Font( Font.SANS_SERIF, Font.PLAIN, 10 );

   

    double percent_val;
    boolean with_percent;
    long start_time;

    public Clock()
    {
        this(  false );
    }
    
    public Clock( boolean _with_percent)
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

        ic = new ImageIcon(this.getClass().getResource("/dimm/home/images/busy_medium.png"));
        size = new Dimension( ic.getIconWidth(), ic.getIconHeight() );
        

        start_time = 0;

    }

    
    


    public void set_percent( double pc )
    {
        with_percent = true;

        if (pc != percent_val)
        {
            percent_val = pc;
            repaint();
        }

    }



    @Override
    public int getWidth()
    {
        return size.width;
    }

    @Override
    public int getHeight()
    {
        return size.height;
    }

    @Override
    public Dimension getSize()
    {
        return size;
    }

    public void start()
    {
   //     timer.start();
        start_time = System.currentTimeMillis();
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
      
    }
    int get_angle_int(double a)
    {
        return (int)(a + 0.5);
    }
    double frac_to_angle( double f )
    {
        double a =  270 - f * 360;
        while (a < 0)
            a += 360;

        return a;
    }

    @Override
    public void paint(Graphics g)
    {
        //gBuffer = g;
        int ds = 5;
        int rx = ds;
        int ry = ds - 1;
       

        int diff = 0;
        if (start_time > 0)
        {
            diff = (int)((start_time - System.currentTimeMillis()) / 1000);
        }

        int s = diff %60;
        int m = (diff / 60) % 60;
        int h = diff / 3600;

        double h_angle = frac_to_angle( h / 12.0 );
        double m_angle = frac_to_angle( m / 60.0 );
        double s_angle = frac_to_angle( s / 60.0 );


        if (frame_buffer == null)
        {
            frame_buffer = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
            gRadarFrameBuffer = (Graphics2D) frame_buffer.getGraphics();

            // PAINT FRAME ONCE
            gRadarFrameBuffer.drawImage(ic.getImage(), 0, 0, size.width, size.height, this);
           
            
        }
        // PAINT TO SCREENBUFFER
        Graphics2D g2 = (Graphics2D) g;
        
        
        g2.drawImage(frame_buffer, 0, 0, this);



        if (with_percent)
        {
            g2.setColor(Main.ui.get_foreground());
            String pc_str = "" + (int)(percent_val + 0.5) + "%";
            FontRenderContext frc = g2.getFontRenderContext();
            TextLayout tl = new TextLayout(pc_str, pc_font, frc);
            Rectangle2D rr = tl.getBounds();
            tl.draw(g2, (float)(size.width/2  - rr.getWidth() / 2) , (float)(size.height*3 / 4 - rr.getHeight()/2 ));
            //g2.drawString( pc_str, DECAY_DIMM, DECAY_DIMM);
        }

    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
      
        repaint();
    }
    
    public static void main( String[] args )
    {
        JFrame  dlg = new JFrame("RadarTest");

        Clock radar = new Clock();
        dlg.add( radar);
        radar.start();
        dlg.pack();
        dlg.setVisible(true);
        

    }
}
