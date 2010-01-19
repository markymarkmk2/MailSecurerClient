package dimm.home.Rendering;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import javax.swing.JComponent;

import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

public class FlatBackgroundTitle extends JComponent {
    private String text;
    private Image titleImage;
    private int titleHeight;
    private int titleWidth;
    
    private float shadowOffsetX;
    private float shadowOffsetY;
    
    ////////////////////////////////////////////////////////////////////////////
    // THEME SPECIFIC FIELDS
    ////////////////////////////////////////////////////////////////////////////
    
    @InjectedResource
    private Font titleFont;
    @InjectedResource
    private Color titleColor;
    @InjectedResource
    private float titleOpacity;
    @InjectedResource
    private int preferredHeight;
    @InjectedResource
    private int lineWidth;

    public FlatBackgroundTitle(final String text) {
        ResourceInjector.get().inject(this);
        setOpaque(false);
        
        this.text = text;
        
    }
    
    void setText(final String text) {
        this.text = text;
        titleImage = null;
        repaint();
    }
    
    

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        size.height = preferredHeight;
        return size;
    }
    
    @Override
    public Dimension getMaximumSize() {
        Dimension size = super.getMaximumSize();
        size.height = preferredHeight;
        return size;
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        
        if (titleImage == null) {
            titleImage = createTitleImage(g2);
        }
        
        Composite composite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                                                   titleOpacity));
        g2.drawImage(titleImage,
                     (getWidth() - titleWidth) / 2,
                     (getHeight() - titleHeight) / 2, null);
        g2.setComposite(composite);
    }
    
    private Image createTitleImage(Graphics2D g2) {
        FontRenderContext context = g2.getFontRenderContext();
        TextLayout layout = new TextLayout(text, titleFont, context);
        Rectangle2D bounds = layout.getBounds();
        
        BufferedImage image = new BufferedImage(getWidth() - 120,
                                                (int) bounds.getHeight() + 23,
                                                BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();
        setupGraphics(g2d);

        int[] arrowX  = { getWidth() - 135,
                          getWidth() - 125,
                          getWidth() - 135 };
        int[] arrowY  = { 3 + (int) bounds.getHeight() + 7,
                          7 + (int) bounds.getHeight() + 7,
                          12 + (int) bounds.getHeight() + 7 };
        int   npoints = 3;
        Polygon arrow = new Polygon(arrowX, arrowY, npoints); 
        
       
        
        g2d.setColor(titleColor);
        layout.draw(g2d, 5.0f, 5.0f + layout.getAscent() - layout.getDescent());

        g2d.fillRect(5, 5 + (int) bounds.getHeight() + 2 + lineWidth, getWidth() - 135, lineWidth);
        //g2d.fill(arrow);
        g2d.dispose();
        
        titleWidth = image.getWidth();
        titleHeight = image.getHeight();
        
        

        return image;
    }

    private static void setupGraphics(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON);
    }
}
