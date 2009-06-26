/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dimm.home.Rendering;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import org.jdesktop.swingx.JXButton;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import javax.swing.SwingUtilities;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

/**
 *
 * @author mw
 */

public class GlossButton extends JXButton implements MouseListener
{

    private boolean pressed;

    ////////////////////////////////////////////////////////////////////////////
    // THEME SPECIFIC FIELDS
    ////////////////////////////////////////////////////////////////////////////
    @InjectedResource
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
    private BufferedImage haloPicture;
    @InjectedResource
    private int pathShadowDirection;
    @InjectedResource
    private int pathShadowDistance;
    @InjectedResource
    float path_y_offset = 4;
    
    private final float shadowOffsetX;
    private final float shadowOffsetY;
    private int textWidth;
    private Rectangle clickable;
    private float ghostValue = 0.0f;

    public GlossButton()
    {
        ResourceInjector.get().inject(this);

        setFont(pathFont);
        setFocusable(false);

        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorder(null);  
        setOpaque( false );

        this.addMouseListener(this);

        addMouseListener(new HiglightHandler());
        pressed = false;

        setMargin(new Insets(0, 22, 0, 22));

        FontMetrics metrics = getFontMetrics(pathFont);
        textWidth = SwingUtilities.computeStringWidth(metrics, getText());

        double rads = Math.toRadians(pathShadowDirection);
        shadowOffsetX = (float) Math.cos(rads) * pathShadowDistance;
        shadowOffsetY = (float) Math.sin(rads) * pathShadowDistance;
    }
    
    @Override
    public void setText(String text)
    {
        FontMetrics metrics = getFontMetrics(pathFont);
        textWidth = SwingUtilities.computeStringWidth(metrics, text);
        super.setText(text);
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        pressed = true;
        this.repaint();
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        pressed = false;
        this.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
    }

    public boolean isPressed()
    {
        return pressed;
    }

    public void setPressed(boolean pressed)
    {
        this.pressed = pressed;
    }

    /*
    @Override
    public void paint(Graphics g)
    {
        paintComponent(g);
    }
    */
    @Override
    protected void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        int press_offset = 0;
        if (pressed)
            press_offset = 1;

        Composite composite = g2.getComposite();

        int _dx  = press_offset;
        int _dy  = press_offset;
        
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
        
        if (ghostValue > 0.0f /*&& this != buttonStack.peek()*/)
        {
            int halo_width = haloPicture.getWidth();
            int x = getWidth()/2 - halo_width/2;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    ghostValue));
            g2.drawImage(haloPicture,
                    x, 0,
                    halo_width, getHeight(), null);
        }

        float offset = getWidth()/2 - textWidth/2 + press_offset;
        

        FontRenderContext context = g2.getFontRenderContext();
        TextLayout layout = new TextLayout(getText(), pathFont, context);

        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                pathShadowOpacity));
        g2.setColor(pathShadowColor);
        layout.draw(g2,
                shadowOffsetX + offset,
                layout.getAscent() + layout.getDescent() + 
                shadowOffsetY + path_y_offset + press_offset);
        g2.setComposite(composite);

        g2.setColor(pathColor);
        layout.draw(g2,
                offset, layout.getAscent() + layout.getDescent() + 
                path_y_offset + press_offset);


    }

    private final class HiglightHandler extends MouseAdapter
    {

        private Animator timer;

        @Override
        public void mouseEntered(MouseEvent e)
        {
            if (timer != null && timer.isRunning())
            {
                timer.stop();
            }
            timer = new Animator(300, new AnimateGhost(true));
            timer.start();
        }

        @Override
        public void mouseExited(MouseEvent e)
        {
            if (timer != null && timer.isRunning())
            {
                timer.stop();
            }
            timer = new Animator(300, new AnimateGhost(false));
            timer.start();
        }
        }

    private final class AnimateGhost implements TimingTarget
    {

        private boolean forward;
        private float oldValue;

        AnimateGhost(boolean forward)
        {
            this.forward = forward;
            oldValue = ghostValue;
        }

        @Override
        public void repeat()
        {
        }

        @Override
        public void timingEvent(float fraction)
        {
            ghostValue = oldValue + fraction * (forward ? 1.0f : -1.0f);

            if (ghostValue > 1.0f)
            {
                ghostValue = 1.0f;
            }
            else if (ghostValue < 0.0f)
            {
                ghostValue = 0.0f;
            }

            repaint();
        }

        @Override
        public void begin()
        {
        }

        @Override
        public void end()
        {
        }
    }
}
