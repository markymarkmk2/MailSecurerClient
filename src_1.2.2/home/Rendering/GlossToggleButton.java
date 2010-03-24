/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dimm.home.Rendering;

import dimm.home.Main;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import javax.swing.JToggleButton;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.GlossPainter;
import org.jdesktop.swingx.painter.MattePainter;
import org.jdesktop.swingx.painter.Painter;

/**
 *
 * @author mw
 */
class ButtonGlossPainter<T> extends GlossPainter<T>
{

    public ButtonGlossPainter(Paint paint, GlossPosition position)
    {
        super(paint, position);
        
    }

    @Override
    protected void doPaint(Graphics2D g, T component, int width, int height)
    {
        if (getPaint() != null)
        {
            int click_offset = 0;
            GlossToggleButton bt = (GlossToggleButton) component;
            if (bt.isPressed())
            {
                click_offset = 1;
            }

            Ellipse2D ellipse = new Ellipse2D.Double(-width * 1.3 + click_offset,
                    height / 2.7 + click_offset, width * 4.0,
                    height * 2.0);

            Area gloss = new Area(ellipse);
            if (getPosition() == GlossPosition.TOP)
            {
                Area area = new Area(new Rectangle(0, 0,
                        width, height));
                area.subtract(new Area(ellipse));
                gloss = area;
            }
            /*
            if(getClip() != null) {
            gloss.intersect(new Area(getClip()));
            }*/
            g.setPaint(getPaint());
            g.fill(gloss);
        }
    }
}

public class GlossToggleButton extends JToggleButton //implements MouseListener
{

    private boolean pressed;

    ////////////////////////////////////////////////////////////////////////////
    // THEME SPECIFIC FIELDS
    ////////////////////////////////////////////////////////////////////////////
  /*  @InjectedResource
    private Font pathFont;
    @InjectedResource
    private Color pathColor;
    @InjectedResource
    private float pathShadowOpacity;
    @InjectedResource
    private Color pathShadowColor;
    @InjectedResource
    private BufferedImage backImage;
    @InjectedResource
    float path_y_offset = 4;
    */
    private Rectangle clickable;
    
    private boolean withGlossBorder;
    
    Painter<JToggleButton> painter;

    public GlossToggleButton()
    {
//        ResourceInjector.get().inject(this);
          
        GlossPainter gp = new ButtonGlossPainter(Colors.White.alpha(0.2f),
        GlossPainter.GlossPosition.TOP);
        GradientPaint grp = new GradientPaint(0, 0, Color.black, 0, 25, Main.ui.get_appl_dgray());
        MattePainter aap = new MattePainter(grp);
        painter = new CompoundPainter(aap, gp);
        

     //   setFont(pathFont);
        setFocusable(false);

        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(null);
        
        

      //  this.addMouseListener(this);

  
        pressed = false;

        setMargin(new Insets(0, 0, 0, 0));

    }
/*
    public void mouseClicked(MouseEvent e)
    {
    }

    public void mousePressed(MouseEvent e)
    {
        pressed = true;
        
        this.repaint();
        
    }

    public void mouseReleased(MouseEvent e)
    {
        pressed = false;
        this.repaint();
    }

    public void mouseEntered(MouseEvent e)
    {
    }

    public void mouseExited(MouseEvent e)
    {
    }
*/
    public boolean isPressed()
    {
        return pressed;
    }

    public void setPressed(boolean pressed)
    {
        this.pressed = pressed;
    }
    
    private void invokePainter(Graphics g, Painter<JToggleButton> ptr) {
        if(ptr == null) return;
        
        Graphics2D g2d = (Graphics2D) g.create();
        
        try 
        {
            if(true) 
            {
                ptr.paint(g2d, this, getWidth(), getHeight());
            } 
            else 
            {
                Insets ins = this.getInsets();
                g2d.translate(ins.left, ins.top);
                ptr.paint(g2d, this,
                        this.getWidth() - ins.left - ins.right,
                        this.getHeight() - ins.top - ins.bottom);
            }
        } finally {
            g2d.dispose();
        }
    }
    

   // @Override
    protected void __paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        
        painter.paint(g2, this, getWidth(), getHeight());
        
        /*
        int press_offset = 0;
        if (pressed)
            press_offset = 1;

        Composite composite = g2.getComposite();

        int _dx  = press_offset;
        int _dy  = press_offset;
        
        if (withGlossBorder)
        {
            // LEFT SIDE
            g2.drawImage(backImage,
                    _dx, _dy, _dx + 5, _dy + getHeight(),
                    0, 0, 5, backImage.getHeight(), null );

            // RIGHT SIDE
            _dx  = press_offset + getWidth() - 5;
            _dy  = press_offset;
            int _sx  = backImage.getWidth() - 5;
            g2.drawImage(backImage,
                    _dx, _dy, _dx + 5, _dy + getHeight(),
                    _sx, 0, _sx + 5, backImage.getHeight(), null );

            // MIDDLE
            _dx  = press_offset + 5;
            _dy  = press_offset;
            g2.drawImage(backImage,
                    _dx, _dy, _dx + getWidth() - 10, _dy + getHeight(),
                    5, 0, backImage.getWidth() - 5, backImage.getHeight(), null );
        }           
        */
        super.paintComponent( g );
    }

    

    boolean isWithGlossBorder()
    {
        return withGlossBorder;
    }

    public void setWithGlossBorder(boolean withGlossBorder)
    {
        this.withGlossBorder = withGlossBorder;
    }

}
