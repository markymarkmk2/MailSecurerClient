/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

import dimm.home.UserMain;
import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import javax.swing.JComponent;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.animation.timing.interpolation.PropertySetter;

/**
 *
 * @author Administrator
 */
public class SpringGlassPane extends JComponent
{

    UserMain main;
    private static final float MAGNIFY_FACTOR = 0.8f;
    private Rectangle bounds;
    private Image image;
    private float zoom = 0.0f;
    public SpringGlassPane( UserMain m )
    {
        main = m;
        
    }

    @Override
    protected void paintComponent( Graphics g )
    {
        
        if ( image != null && bounds != null )
        {
            int width = image.getWidth(this);
            width += (int) (image.getWidth(this) * MAGNIFY_FACTOR * getZoom());

            int height = image.getHeight(this);
            height += (int) (image.getHeight(this) * MAGNIFY_FACTOR * getZoom());

            int x = (bounds.width - width) / 2;
            int y = (bounds.height - height) / 2;


            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g2.setComposite(AlphaComposite.SrcOver.derive(1.0f - getZoom()));
            g2.drawImage(image, x + bounds.x, y + bounds.y,
                    width, height, null);
        }
        super.paintComponent(g);
        
            
        
    }

    public void showSpring( Rectangle bounds, Image image, TimingTarget end_action )
    {
        this.bounds = bounds;
        this.image = image;

        Animator animator = PropertySetter.createAnimator(250, this,
                "zoom", 0.0f, 1.0f);
        animator.setAcceleration(0.2f);
        animator.setDeceleration(0.4f);
        animator.start();
        if ( end_action != null )
        {
            animator.addTarget(end_action);
        }

        main.getGlassPane().repaint();
        main.getGlassPane().setVisible(true);
    }

    public float getZoom()
    {
        return zoom;
    }

    public void setZoom( float zoom )
    {
        this.zoom = zoom;
        main.getGlassPane().repaint();
    }
}
    