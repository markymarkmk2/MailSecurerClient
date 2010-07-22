/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dimm.home.Rendering;

/**
 *
 * @author Administrator
 */
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
import javax.swing.JFrame;
import javax.swing.Timer;
import org.jdesktop.fuse.ResourceInjector;

public class YingYang extends JComponent implements ActionListener
{
    private static final int TICK_MS = 10;
    private static final int IMG_WIDTH = 90;
    private static final int IMG_HEIGHT = 90;
    
    private static Dimension size;
    private BufferedImage frame_buffer;
    private BufferedImage yingyang_buffer;
    private BufferedImage light_buffer;
    // RescaleOp op;
    private Graphics2D g_yingyang;
    private Graphics2D g_light;


    
    Composite acomp;
    Timer timer;
    ImageIcon ic;
    ImageIcon ich;
    
    Font pc_font = new Font( Font.SANS_SERIF, Font.PLAIN, 11 );

   

    double percent_val;
    boolean with_percent;
    long start_time;

    private double ROT_INCR = 0.06;

    public YingYang()
    {
        this( false );
    }
    
    public YingYang( boolean _with_percent)
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

        ic = new ImageIcon(this.getClass().getResource("/dimm/home/images/jingjang_raw.png"));
        ich = new ImageIcon(this.getClass().getResource("/dimm/home/images/jingjang_light.png"));

        size = new Dimension( ic.getIconWidth(), ic.getIconHeight() );

        //setOpaque(false);

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
        return IMG_WIDTH;
    }

    @Override
    public int getHeight()
    {
        return IMG_HEIGHT;
    }

    @Override
    public Dimension getSize()
    {
        return new Dimension(IMG_WIDTH, IMG_HEIGHT);
    }

    public void start()
    {
        timer.start();
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
    

    static double dx = 0;
    static double dy = 0;
    @Override
    public void paint(Graphics g)
    {       

        if (yingyang_buffer == null)
        {
            frame_buffer = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
            //frame_buffer.getGraphics().drawImage(ic.getImage(), 0, 0, size.width, size.height, this);

            yingyang_buffer = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
            g_yingyang = (Graphics2D) yingyang_buffer.getGraphics();
            g_yingyang.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // PAINT FRAME ONCE
            g_yingyang.drawImage(ic.getImage(), 0, 0, size.width, size.height, this);
           
            light_buffer = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR);
            g_light = (Graphics2D) light_buffer.getGraphics();
            g_light.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // PAINT FRAME ONCE
            g_light.drawImage(ich.getImage(), 0, 0, size.width, size.height, this);
            
        }
        // PAINT TO SCREENBUFFER
        Graphics2D g2 = (Graphics2D) frame_buffer.getGraphics();
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        // PAINT FRAME ONCE
        g_yingyang.drawImage(ic.getImage(), 0, 0, size.width, size.height, this);
//        g_yingyang.rotate(diff / -1000.0);
        g_yingyang.rotate(ROT_INCR, size.width / 2.0 + dx, size.height / 2.0 + dy);
        
        g2.drawImage(yingyang_buffer, 0, 0, this);
        
        g2.drawImage(light_buffer, 0, 0, this);


        g2 = (Graphics2D)g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.drawImage(frame_buffer, 0, 0, IMG_WIDTH - 2, IMG_HEIGHT - 2, this);

        if (with_percent)
        {
            
            g2.setColor(Color.WHITE);
            String pc_str = "" + (int)(percent_val + 0.5) + "%";
            FontRenderContext frc = g2.getFontRenderContext();
            TextLayout tl = new TextLayout(pc_str, pc_font, frc);
            Rectangle2D rr = tl.getBounds();
            //g2.setXORMode(Color.black);
            tl.draw(g2, (float)(IMG_WIDTH/2.0  - rr.getWidth() / 2) , (float)(IMG_HEIGHT*5/ 6.0 /*- rr.getHeight()/2*/ ));
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

        //JPanel PN_ICON = new javax.swing.JPanel();
        //JXLabel LBX_ICON = new org.jdesktop.swingx.JXLabel();



        //PN_ICON.setOpaque(false);
        //PN_ICON.setLayout(new javax.swing.BoxLayout(PN_ICON, javax.swing.BoxLayout.LINE_AXIS));
        //PN_ICON.add(LBX_ICON);

        YingYang radar = new YingYang( true);
        //PN_ICON.add(radar);
        //dlg.add( PN_ICON );
        dlg.add( radar );
        radar.start();
        
        dlg.pack();
        dlg.setVisible(true);
        

    }
}
