package dimm.home.Rendering;

import com.sun.animation.transitions.ScreenTransition;
import com.sun.animation.transitions.TransitionTarget;
import dimm.home.SwitchPanel;
import dimm.home.UserMain;
import java.awt.event.ActionEvent;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;
import org.netbeans.lib.awtextra.AbsoluteConstraints;
import org.netbeans.lib.awtextra.AbsoluteLayout;


class PanelSwitcher extends JComponent implements TransitionTarget
        
{
    SwitchPanel new_panel;
    ScreenTransition transition;
    int delay;
    int pbc_id;
    NavigationHeader nav_panel;
    
    PanelSwitcher(int d, NavigationHeader _nav_panel)
    {
        delay = d;
        nav_panel = _nav_panel;
       
        // Setup transition with:
        //      "this" as the transition container
        //      "this" as the TransitionTarget callback object
        //      animator as the animator that drives the transition
         transition = new com.sun.animation.transitions.ScreenTransition(this,this);
         
         pbc_id = -1;
         
       
    }
    void set_panel(SwitchPanel p, int _pbc_id)
    {
        if (new_panel != null)
            new_panel.deactivate_panel();
        
        new_panel = p;
        
        pbc_id = _pbc_id;
        if (new_panel != null)
            new_panel.activate_panel();
        
        nav_panel.set_selected_button( pbc_id );
        
        transition.startTransition(delay);
    }
    int get_act_pbc_id()
    {
        return pbc_id;
    }
            
    @Override
    public void resetCurrentScreen() 
    {
        this.removeAll();
    }

    @Override
    public void setupNextScreen() 
    {
        this.add( this.new_panel );
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void transitionComplete() 
    {
        //throw new UnsupportedOperationException("Not supported yet.");
        if (new_panel != null)
            new_panel.panel_full_visible();
        
    }

}


public class NavigationHeader extends JComponent
       
{

    PanelSwitcher panel_switcher;
    private final PathButtonHandler eventHandler;
    private final ArrayList<PathButton> button_list;

    ////////////////////////////////////////////////////////////////////////////
    // THEME SPECIFIC FIELDS
    ////////////////////////////////////////////////////////////////////////////
    @InjectedResource
    private Color lightColor;
    @InjectedResource
    private Color shadowColor;
    @InjectedResource
    private int preferredHeight;
    @InjectedResource
    private BufferedImage backgroundGradient;
    @InjectedResource
    private float titleAlpha;
    @InjectedResource
    private BufferedImage title;
    @InjectedResource
    private Font pathFont;
    @InjectedResource
    private Color pathColor;
    @InjectedResource
    private Color selpathColor;
    @InjectedResource
    private Color disablepathColor;
    @InjectedResource
    private float pathShadowOpacity;
    @InjectedResource
    private Color pathShadowColor;
    @InjectedResource
    private int pathShadowDistance;
    @InjectedResource
    private int pathShadowDirection;
    @InjectedResource
    private BufferedImage pathSeparatorLeft;
    @InjectedResource
    private BufferedImage pathSeparatorRight;
    @InjectedResource
    private BufferedImage haloPicture;
    UserMain main;
    
    JPanel bt_panel;
    JPanel ic_panel;
    public NavigationHeader(UserMain _main)
    {
        ResourceInjector.get().inject(this);

        main = _main;
        panel_switcher = new PanelSwitcher(200, this);

        bt_panel = new JPanel();
        bt_panel.setOpaque(false );
        ic_panel = new JPanel();
        ic_panel.setOpaque(false);
        bt_panel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        ic_panel.setLayout(new AbsoluteLayout());
        
        bt_panel.add(Box.createRigidArea(new Dimension(2, 2)));
        
        this.setLayout( new AbsoluteLayout() );
        //setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        int xw = main.getWidth() - title.getWidth() - 4;
        this.add( bt_panel,  new AbsoluteConstraints(0, 0, xw, 30));
        this.add( ic_panel,  new AbsoluteConstraints(xw, 0, main.getWidth() - xw, 30));

        this.eventHandler = new PathButtonHandler();
        this.button_list = new ArrayList<PathButton>();
        
        // APPL-BUTTON
        ImageIcon ic_vendor = new ImageIcon( title );
        PathButton bt_web = new PathButton("                                      ", -42, null);
        bt_web.setIcon(ic_vendor);
        bt_web.setToolTipText(UserMain.get_version_str());
        ic_panel.add( bt_web, new AbsoluteConstraints(0, 0, main.getWidth() - xw, 30));
        bt_web.addActionListener( new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                main.call_navigation_click();
            }
        });
        bt_web.setHyperlinkCursor(true);

        
    }

    @Override
    public void setSize(Dimension d)
    {
        super.setSize(d);
        panel_switcher.setSize(d);
    }
    
    public JComponent get_panel_switcher()
    {
        return panel_switcher;
    }
    public int get_act_pbc_id()
    {
        return panel_switcher.get_act_pbc_id();
    }
    public void switch_to_panel( int code )
    {
        // UPDATE EXISTING
        for (int i = 0; i < button_list.size(); i++)
        {
            if (button_list.get(i).get_code() == code)
            {
                PathButton bt = button_list.get(i);
                if (bt.get_panel() != null && bt.is_enabled())
                {
                    panel_switcher.set_panel(bt.get_panel(), code);
                    return;
                }                
            }
        }
        
        // NO VALID PANEL FOUND, TRY FIRST
        for (int i = 0; i < button_list.size(); i++)
        {
            PathButton bt = button_list.get(i);
            if (bt.get_panel() != null && bt.is_enabled())
            {
                panel_switcher.set_panel(bt.get_panel(), code);
                break;
            }
        }
    }
    public void update_active_panel()
    {
        int code = get_act_pbc_id();

        // CHECK EXISTING
        for (int i = 0; i < button_list.size(); i++)
        {
            if (button_list.get(i).get_code() == code)
            {
                PathButton bt = button_list.get(i);
                // OKAY ?
                if (bt.get_panel() != null && bt.is_enabled())
                {
                    // CALL ACTIVATE, MAYBE WE HAVE TO UPDATE GUI -> BAX HAS CHANGED
                    bt.get_panel().activate_panel();
                    // GO
                    return;
                }                
            }
        }
        
        // NO VALID PANEL FOUND, TRY FIRST ENABLED
        for (int i = 0; i < button_list.size(); i++)
        {
            PathButton bt = button_list.get(i);
            if (bt.get_panel() != null && bt.is_enabled())
            {
                panel_switcher.set_panel(bt.get_panel(), bt.get_code());
                return;
            }
        }
        
        // NO VISIBLE PANEL FOUND, TRY FIRST
        PathButton bt = button_list.get(0);
        panel_switcher.set_panel(bt.get_panel(), bt.get_code());

    }
    
    

    public void add_trail_button(final String title, final int code, SwitchPanel p)
    {
        // UPDATE EXISTING
        for (int i = 0; i < button_list.size(); i++)
        {
            if (button_list.get(i).get_code() == code)
            {
                button_list.get(i).setText(title);
                return;
            }
        }

        // ADD NEW
        PathButton pathButton = new PathButton(title, code, p, true);
        bt_panel.add(pathButton);
        button_list.add(pathButton);

        pathButton.setHyperlinkCursor(true);

        revalidate();
        repaint();
    }

    
    public void add_button(final String title, final int code, SwitchPanel p)
    {
        // UPDATE EXISTING
        for (int i = 0; i < button_list.size(); i++)
        {
            if (button_list.get(i).get_code() == code)
            {
                button_list.get(i).setText(title);
                return;
            }
        }
        
        // REMOVE STICKY TRAIL BUTTONS
        ArrayList<PathButton> trail_list = new ArrayList<PathButton>();
        for ( int i = 0; i < button_list.size(); i++ )
        {
            NavigationHeader.PathButton bt = button_list.get(i);
            if (bt.is_trail())
            {
                button_list.remove(bt);
                trail_list.add(bt);
                i--;
            }
        }


        // ADD NEW BT TO LIST
        PathButton pathButton = new PathButton(title, code, p);        
        button_list.add(pathButton);

        // APPEND STICKY BUTTONS
        for ( int i = 0; i < trail_list.size(); i++ )
        {
            button_list.add( trail_list.get(i) );
        }
        trail_list.clear();
        
        
        // EMPTY PANEL
        bt_panel.removeAll();
        
        // ADD ALL TO PANEL
        for ( int i = 0; i < button_list.size(); i++ )
        {
            NavigationHeader.PathButton bt = button_list.get(i);
            bt_panel.add(bt);
        }

        
        pathButton.setHyperlinkCursor(true);

        revalidate();
        repaint();
    }

    public void set_button_title(int code, String title)
    {
        for (int i = 0; i < button_list.size(); i++)
        {
            if (button_list.get(i).get_code() == code)
            {
                button_list.get(i).setText(title);
            }
        }

        revalidate();
        repaint();
    }

    public void remove_button(int code)
    {
        for (int i = 0; i < button_list.size(); i++)
        {
            if (button_list.get(i).get_code() == code)
            {
                bt_panel.remove(button_list.get(i));
                button_list.remove(i);
            }
        }

        revalidate();
        repaint();
    }
    public void enable_button(int code, boolean b)
    {
        for (int i = 0; i < button_list.size(); i++)
        {
            if (button_list.get(i).get_code() == code)
            {
                
                button_list.get(i).set_enabled( b );
            }
        }

        revalidate();
        repaint();
    }
    public boolean is_button_enabled(int code)
    {
        for (int i = 0; i < button_list.size(); i++)
        {
            if (button_list.get(i).get_code() == code)
            {
                return button_list.get(i).is_enabled();
            }
        }
        return false;
    }

    @Override
    public Dimension getPreferredSize()
    {
        Dimension size = super.getPreferredSize();
        size.height = preferredHeight;
        return size;
    }

    @Override
    public Dimension getMaximumSize()
    {
        Dimension size = super.getMaximumSize();
        size.height = preferredHeight;
        return size;
    }

    public void set_selected_button(int pbc_id)
    {
        for (int i = 0; i < button_list.size(); i++)
        {
            PathButton pb = button_list.get(i);
            pb.setSelected(pb.get_code() == pbc_id);
        }
    }


    @Override
    protected void paintComponent(Graphics g)
    {
        if (!isVisible())
        {
            return;
        }

        Graphics2D g2 = (Graphics2D) g;

        setupGraphics(g2);

        paintBackground(g2);
        paintLogo(g2);
        
  
    }

    private static void setupGraphics(final Graphics2D g2)
    {
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    }

    private void paintLogo(final Graphics2D g2)
    {
        Composite composite = g2.getComposite();
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                titleAlpha));
        g2.drawImage(title, getWidth() - title.getWidth(), 0, null);
      /*  g2.drawImage(pathSeparatorLeft,
                getWidth() - title.getWidth() - pathSeparatorLeft.getWidth(), 0,
                null);
        g2.drawImage(pathSeparatorRight, getWidth() - title.getWidth(), 0, null);
*/
        g2.setComposite(composite);
    }

    private void paintBackground(final Graphics2D g2)
    {
        int height = backgroundGradient.getHeight();

        Rectangle bounds = g2.getClipBounds();
        g2.drawImage(backgroundGradient,
                (int) bounds.getX(), 0,
                (int) bounds.getWidth(), height,
                null);

   /*     g2.setColor(lightColor);
        g2.drawLine(0, height, getWidth(), height);

        g2.setColor(shadowColor);
        g2.drawLine(0, height + 1, getWidth(), height + 1);*/
    }

    public SwitchPanel get_switch_panel( int i)
    {
        return button_list.get(i).panel;
    }
    public int get_switch_panels()
    {
        return button_list.size();
    }
  
    private class PathButton extends JButton
    {

        SwitchPanel panel;
        private final float shadowOffsetX;
        private final float shadowOffsetY;
        private int textWidth;
        private Rectangle clickable;
        private float ghostValue = 0.0f;
        int code;
        boolean trail;
        boolean enabled;

        private PathButton(final String item, int _code, SwitchPanel _panel)
        {
            this(item, _code, _panel, false);
        }
        private PathButton(final String item, int _code, SwitchPanel _panel, boolean _trail)
        {
            super(item);

            code = _code;
            trail = _trail;
            panel = _panel;
            enabled = true;

            setFont(pathFont);
            setFocusable(false);

            setBorderPainted(false);
            setContentAreaFilled(false);
            setFocusPainted(false);

            setMargin(new Insets(0, 0, 0, 0));

            FontMetrics metrics = getFontMetrics(pathFont);
            textWidth = SwingUtilities.computeStringWidth(metrics, getText());

            double rads = Math.toRadians(pathShadowDirection);
            shadowOffsetX = (float) Math.cos(rads) * pathShadowDistance;
            shadowOffsetY = (float) Math.sin(rads) * pathShadowDistance;

            addMouseListener(eventHandler);
            addMouseListener(new HiglightHandler());
        }
        private SwitchPanel get_panel()
        {
            return panel;
        }

        private void set_enabled( boolean b )
        {
            enabled = b;
        }
        private boolean is_enabled()
        {
            return enabled;
        }
        private void setHyperlinkCursor(boolean hyperlink)
        {
            if (hyperlink)
            {
                FontMetrics metrics = getFontMetrics(pathFont);
                int textHeight = metrics.getHeight();

                int x = 10;
                if (!is_first(this))
                {
                    x += pathSeparatorRight.getWidth();
                }
                clickable = new Rectangle(x, metrics.getDescent(),
                        textWidth, textHeight);
                HyperlinkHandler.add(this, clickable);
            } else
            {
                HyperlinkHandler.remove(this);
            }
        }

        @Override
        public Dimension getSize()
        {
            return getPreferredSize();
        }

        @Override
        public Dimension getPreferredSize()
        {
            int width = 20 + textWidth;
            if (!is_last(this))
            {
                width += pathSeparatorLeft.getWidth();
            }
            if (!is_first(this))
            {
                width += pathSeparatorRight.getWidth();
            }

            return new Dimension(width, preferredHeight);
        }

        boolean is_last(PathButton bt)
        {
            if (bt != button_list.get(button_list.size() - 1))
            {
                return false;
            }
            return true;
        }
        void set_trail( boolean b)
        {
            trail = b;
        }
        boolean is_trail()
        {
            return trail;
        }

        boolean is_first(PathButton bt)
        {
            if ( button_list.size() == 0 )
                return true;
            if (bt != button_list.get(0))
            {
                return false;
            }
            return true;
        }

        @Override
        protected void paintComponent(Graphics g)
        {
            Graphics2D g2 = (Graphics2D) g;

            Composite composite = g2.getComposite();

            if (enabled && ghostValue > 0.0f /*&& this != buttonStack.peek()*/)
            {
                int x = -5;
                if (is_last(this))
                {
                    x += pathSeparatorLeft.getWidth();
                }
                g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);

                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                        ghostValue));
                g2.drawImage(haloPicture,
                        x, 0,
                        textWidth + 20, getHeight(), null);
            }

            float offset = 10.0f;
            if (!is_first(this))
            {
                offset += pathSeparatorRight.getWidth();
            }

            FontRenderContext context = g2.getFontRenderContext();
            TextLayout layout = new TextLayout(getText(), pathFont, context);

            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,
                    pathShadowOpacity));
            
            if (enabled)
            {
                g2.setColor(pathShadowColor);
                layout.draw(g2, shadowOffsetX + offset,layout.getAscent() + layout.getDescent() + shadowOffsetY);
            }
            else
            {
                g2.setColor(disablepathColor);
                layout.draw(g2, shadowOffsetX + offset, layout.getAscent() + layout.getDescent() );
            }


            g2.setComposite(composite);

            if (enabled)
            {
                if (isSelected())
                {
                    g2.setColor(selpathColor);
                } else
                {
                    g2.setColor(pathColor);
                }
                layout.draw(g2,
                        offset, layout.getAscent() + layout.getDescent());
            }
            if (!is_last(this))
            {
                g2.drawImage(pathSeparatorLeft,
                        getWidth() - pathSeparatorLeft.getWidth(), 0,
                        null);
            }
            if (!is_first(this))
            {
                g2.drawImage(pathSeparatorRight, 0, 0, null);
            }
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
                } else if (ghostValue < 0.0f)
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

        public int get_code()
        {
            return code;
        }
    }

    private class PathButtonHandler extends MouseAdapter
    {

        @Override
        public void mousePressed(MouseEvent e)
        {
            PathButton pathButton = (PathButton) e.getSource();

            int index = button_list.indexOf(pathButton);
            if (/*index == buttonStack.size() - 1 ||*/!pathButton.clickable.contains(e.getPoint()))
            {
                return;
            }

            if (!pathButton.is_enabled())
                return;



            main.call_pathbutton_code(pathButton.get_code());
        }
    }

}
