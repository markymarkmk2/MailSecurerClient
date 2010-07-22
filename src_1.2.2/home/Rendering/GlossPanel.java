/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dimm.home.Rendering;

import dimm.home.Main;
import dimm.home.UserMain;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.GlossPainter;
import org.jdesktop.swingx.painter.MattePainter;

class PanelGlossPainter<T> extends GlossPainter<T>
{

    public PanelGlossPainter( Paint paint, GlossPosition position )
    {
        super(paint, position);
    }

    @Override
    protected void doPaint( Graphics2D g, T component, int width, int height )
    {
        if (getPaint() != null)
        {
            Ellipse2D ellipse = new Ellipse2D.Double(-width * 2.5,
                    height / 3, width * 6.0,
                    height * 2.0);

            Area gloss = new Area(ellipse);
            if (getPosition() == GlossPosition.TOP)
            {
                Area area = new Area(new Rectangle(0, 0, width, height));
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

/**
 *
 * @author mw
 */
public class GlossPanel extends JXPanel
{

//    protected float panel_alpha = 0.9f;
    protected float panel_alpha = 1.0f;

    public GlossPanel()
    {
        creater_painter(this.getHeight());
        setAlpha(panel_alpha);
    }

    void creater_painter( int h )
    {
        if (!Main.ui.has_rendered_panels())
        {
            return;
        }

        GlossPainter gp = new PanelGlossPainter(Colors.White.alpha(0.2f), GlossPainter.GlossPosition.TOP);
        GradientPaint grp = new GradientPaint(0, 0, UserMain.ggradientTop, 0, h, UserMain.ggradientBottom);
        MattePainter aap = new MattePainter(grp);
        CompoundPainter cp = new CompoundPainter(aap, gp);
        this.setBackgroundPainter(cp);
    }

    @Override
    public void setSize( Dimension d )
    {
        creater_painter(d.height);
        super.setSize(d);
    }

    @Override
    public void setSize( int w, int h )
    {
        creater_painter(h);
        super.setSize(w, h);
    }

    @Override
    public void setBounds( Rectangle r )
    {
        creater_painter(r.height);
        super.setBounds(r);
    }

    @Override
    public void setBounds( int x, int y, int width, int height )
    {
        creater_painter(height);
        super.setBounds(x, y, width, height);
    }

    @Override
    protected void paintBorder( Graphics g )
    {
        super.paintBorder(g);

        if (Main.ui.has_rendered_panels())
        {
            Color old_color = g.getColor();

            g.setColor(UserMain.self.getGradientLight());//new Color( 130, 130, 130));
            g.drawLine(0, 0, getWidth(), 0);
            g.drawLine(0, 0, 0, getHeight());
            g.setColor(UserMain.self.getGradientDark());
            g.drawLine(getWidth() - 1, 1, getWidth() - 1, getHeight());
            g.drawLine(1, getHeight() - 1, getWidth(), getHeight() - 1);

            g.setColor(old_color);
        }
    }
}
