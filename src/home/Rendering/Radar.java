/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dimm.home.Rendering;

/**
 *
 * @author Administrator
 */
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;

// �1998 Sebastian Wallroth

import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.Timer;

public class Radar extends JComponent implements ActionListener
{

    private static final int DECAY_LEN = 31;
    private static final int INCR_WIDTH = 2;
    private static final int DECAY_SECTOR_WIDTH = 6;
    private static final float DECAY_DIMM = 0.4f;
//    private static final int DECAY_LEN = 51;
    private static int size = 100;
    private BufferedImage RadarFrameBuffer;
    private BufferedImage RadarPulsBuffer;
    private BufferedImage PulsBlurBuffer;
    // RescaleOp op;
    private Graphics2D gRadarFrameBuffer;
    int act_angle;
    Color color[] = new Color[DECAY_LEN];
    Color rdr_green = new Color( 10, 245, 5 );
    Color rdr_lbrown = new Color( 205, 175, 95 );
    Color rdr_red = new Color(245, 10, 5);
    Color rdr_dark_gray = new Color(15, 15, 15);
    Color start_color;
    Color ping_color;
    Color back_color;
    Color grid_color;
    Composite acomp;
    Timer timer;
    ImageIcon ic;
    ImageIcon ich;

    public Radar()
    {
        this( null, null );
    }
    
    public Radar(Color c, Color p)
    {
        if (c != null)
            start_color = c;
        else
            start_color = rdr_lbrown;
        
        if (p != null)
            ping_color = p;
        else
            ping_color = rdr_red;
        
        back_color = rdr_dark_gray;
        grid_color = rdr_lbrown;
        
        init();

        timer = new Timer(10, this);

        ic = new ImageIcon(this.getClass().getResource("/dimm/home/images/radarframe.png"));
        size = (ic.getIconHeight() * 80) / 100;
        ich = new ImageIcon(this.getClass().getResource("/dimm/home/images/radarhighlight.png"));
    }

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
        act_angle = 91;
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
            r = (int) (r * DECAY_DIMM);
            g = (int) (g * DECAY_DIMM);
            b = (int) (b * DECAY_DIMM);
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
        grid_color = color[5];
    }

    @Override
    public void paint(Graphics g)
    {
        //gBuffer = g;
        int ds = 5;
        int rx = ds;
        int ry = ds;
        int rw = size - 2 * ds;
        int rh = size - 2 * ds;


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
        if (act_angle > 85 && act_angle < 95)
        {
            gRadarPulsBuffer.setColor(ping_color);
        }
        else
        {
            gRadarPulsBuffer.setColor(start_color);            
        }
        
        // MAIN LINE
        gRadarPulsBuffer.fillArc(rx, ry, rw, rh, act_angle + DECAY_SECTOR_WIDTH - 3, 3);

        // AFTERGLOW
        for (int i = 1; i < DECAY_LEN; i++)
        {
            gRadarPulsBuffer.setColor(color[i]);
            gRadarPulsBuffer.fillArc(rx, ry, rw, rh, act_angle + i * DECAY_SECTOR_WIDTH, DECAY_SECTOR_WIDTH);
        }
        
        // GRID
        gRadarPulsBuffer.setColor(grid_color);
        gRadarPulsBuffer.drawOval(size / 2 - 10, size / 2 - 10, 20, 20);
        gRadarPulsBuffer.drawOval(size / 2 - 24, size / 2 - 24, 48, 48);
        gRadarPulsBuffer.setColor(color[1]);
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

    }

    public void actionPerformed(ActionEvent e)
    {
        act_angle -= INCR_WIDTH;
        if (act_angle <= 0)
        {
            act_angle = 360;
        }

        repaint();
    }
}
