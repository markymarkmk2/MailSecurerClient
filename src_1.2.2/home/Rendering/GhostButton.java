/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

import dimm.home.Main;
import dimm.home.UserMain;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import javax.swing.JButton;
import javax.swing.SwingUtilities;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

/**
 *
 * @author Administrator
 */
public class GhostButton extends JButton
{

    private BufferedImage haloPicture;
    private float ghostValue = 0.0f;

    @InjectedResource
    private Font pathFont;
    @InjectedResource
    private Font subFont;
    @InjectedResource
    float shadowOpacity = 0.8f;
    @InjectedResource
    float subShadowOpacity = 0.8f;


    int text_width;

    public GhostButton()
    {
        ResourceInjector.get().inject(this);

        setFont(pathFont);

        haloPicture = UserMain.ghaloPicture;
        addMouseListener(new HiglightHandler());
    }

    @Override
    public void setText(String text)
    {
        FontMetrics metrics = getFontMetrics(getFont());
        text_width = SwingUtilities.computeStringWidth(metrics, text);
        super.setText(text);
    }


    @Override
    protected void paintComponent( Graphics g )
    {
        if (!isVisible()) 
        {
                return;
        }
           
        Graphics2D g2 = (Graphics2D) g;

        setupGraphics(g2);
        
        paint_icon(g2);
//        super.paintComponent(g);


        paintText(g2);
        
        if ( ghostValue > 0.0f /*&& this != buttonStack.peek()*/ )
        {
            int halo_width = haloPicture.getWidth();
            int halo_height = haloPicture.getHeight();
            int ic_width = 0;
            if (this.getIcon() != null)
                ic_width = this.getIcon().getIconWidth();
            
            Insets insets = getInsets();
            int x = ic_width + insets.left + text_width / 2 - halo_width / 2;

//            int x = ic_width + insets.left + (getWidth() - ic_width) / 2 - halo_width / 2;
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    ghostValue));
            g2.drawImage(haloPicture,
                    x, 0,
                    halo_width, halo_height/* getHeight()*/, null);
        }
        
    }

/*
        private void paintImage(Graphics2D g2, float y) {
            Insets insets = getInsets();

            if (ghostValue > 0.0f) {
                int newWidth = (int) (image.getWidth() * (1.0 + ghostValue / 2.0));
                int newHeight = (int) (image.getHeight() * (1.0 + ghostValue / 2.0));

                Composite composite = g2.getComposite();
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                           0.7f * (1.0f - ghostValue)));
                g2.drawImage(image,
                             insets.left + (image.getWidth() - newWidth) / 2,
                             4 + (int) (y - newHeight / (5.0 / 3.0)) -
                             (image.getWidth() - newWidth) / 2,
                             newWidth, newHeight, null);
                g2.setComposite(composite);
            }

            g2.drawImage(image, null,
                         insets.left,
                         4 + (int) (y - image.getHeight() / (5.0 / 3.0)));
        }
*/
        private float paint_icon(Graphics2D g2) 
        {
            if (getIcon() == null)
                return 0.0f;
            getIcon().paintIcon(this, g2, 0, 0);  
            return 0.0f;
        }
    
        private float paintText(Graphics2D g2) 
        {
            Font categoryFont = getFont();
            //g2.setFont(categoryFont);

            Insets insets = getInsets();

            FontRenderContext context = g2.getFontRenderContext();
            TextLayout layout = new TextLayout(getText(),
                                               categoryFont, context);

            float x = 0;
            if (this.getIcon() != null)
                x += this.getIcon().getIconWidth();
            
            x += insets.left;
            float y = 8.0f + layout.getAscent() - layout.getDescent();
            y += insets.top;

            Color shadowColor = Color.BLACK;

            float shadowOffsetX = 2;
            float shadowOffsetY = 2;
            Composite composite = g2.getComposite();

            if (Main.ui.has_rendered_button())
            {
                g2.setColor(shadowColor);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                           shadowOpacity));
                layout.draw(g2, shadowOffsetX + x, shadowOffsetY + y);
                g2.setComposite(composite);
            }

            g2.setColor(Main.ui.get_foreground());
            layout.draw(g2, x, y);


            String sub_text = getToolTipText();
            if (sub_text != null && sub_text.length() > 0)
            {
                TextLayout sub_layout = new TextLayout(sub_text,
                                               subFont, context);

                y += sub_layout.getAscent() + 6.0f;
                if (Main.ui.has_rendered_button())
                {
                    shadowOffsetX = 1;
                    shadowOffsetY = 1;
                    g2.setColor(shadowColor);
                    g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                               subShadowOpacity));
                    sub_layout.draw(g2, shadowOffsetX + x, shadowOffsetY + y);
                    g2.setComposite(composite);
                }

                g2.setColor(Main.ui.get_foreground());
                sub_layout.draw(g2, x, y);

            }

        /*    y += layout.getDescent();

            String description = "asbg";
            float categorySmallOpacity = 0.5f;
            Font categorySmallFont = getFont();
            layout = new TextLayout(description == null ? " " : description,
                                    categorySmallFont, context);
            y += layout.getAscent();
            composite = g2.getComposite();
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                       categorySmallOpacity));
            layout.draw(g2, x, y);
            g2.setComposite(composite);
*/
            return y;
        }

        private void setupGraphics(Graphics2D g2) {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                                RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        }

    

  /*  private void addMouseListener(GhostButton.HiglightHandler higlightHandler)
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }
*/
    private final class HiglightHandler extends MouseAdapter
    {

        private Animator timer;

        @Override
        public void mouseEntered( MouseEvent e )
        {
            if ( timer != null && timer.isRunning() )
            {
                timer.stop();
            }
            timer = new Animator(300, new AnimateGhost(true));
            timer.start();
        }

        @Override
        public void mouseExited( MouseEvent e )
        {
            if ( timer != null && timer.isRunning() )
            {
                timer.stop();
            }
            timer = new Animator(150, new AnimateGhost(false));
            timer.start();
        }
        }

    private final class AnimateGhost implements TimingTarget
    {

        private boolean forward;
        private float oldValue;

        AnimateGhost( boolean forward )
        {
            this.forward = forward;
            oldValue = ghostValue;
        }

        @Override
        public void repeat()
        {
        }

        @Override
        public void timingEvent( float fraction )
        {
            ghostValue = oldValue + fraction * (forward ? 1.0f : -1.0f);

            if ( ghostValue > 1.0f )
            {
                ghostValue = 1.0f;
            } else
            {
                if ( ghostValue < 0.0f )
                {
                    ghostValue = 0.0f;
                }
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
