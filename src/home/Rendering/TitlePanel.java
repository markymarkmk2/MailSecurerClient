/*
 * TitlePanel.java
 *
 * Created on 11. Mrz 2008, 18:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package dimm.home.Rendering;

/**
 *
 * @author Administrator
 */
import dimm.home.Main;
import dimm.home.UserMain;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.event.MouseInputListener;

import org.jdesktop.fuse.InjectedResource;
import org.jdesktop.fuse.ResourceInjector;

public class TitlePanel extends JComponent
{

    private JButton closeButton;
    private JButton iconifyButton;

    ////////////////////////////////////////////////////////////////////////////
    // THEME SPECIFIC FIELDS
    ////////////////////////////////////////////////////////////////////////////
    @InjectedResource
    private int preferredHeight;
    @InjectedResource
    private Color lightColor;
    @InjectedResource
    private Color shadowColor;
    @InjectedResource
    private BufferedImage grip;
    @InjectedResource
    private BufferedImage backgroundGradient;
    @InjectedResource
    private Color inactiveLightColor;
    @InjectedResource
    private Color inactiveShadowColor;
    @InjectedResource
    private BufferedImage inactiveGrip;
    @InjectedResource
    private BufferedImage inactiveBackgroundGradient;
    @InjectedResource
    private BufferedImage close;
    @InjectedResource
    private BufferedImage closeInactive;
    @InjectedResource
    private BufferedImage closeOver;
    @InjectedResource
    private BufferedImage closePressed;
    @InjectedResource
    private BufferedImage minimize;
    @InjectedResource
    private BufferedImage minimizeInactive;
    @InjectedResource
    private BufferedImage minimizeOver;
    @InjectedResource
    private BufferedImage minimizePressed;
    Component parent;
    @InjectedResource
    private Font titleFont;


    String title;
    
    public TitlePanel(Component _parent)
    {
        parent = _parent;
        ResourceInjector.get().inject(this);
        
        if (UserMain.self != null && UserMain.self.is_touchscreen())
            preferredHeight -= 2;
            

        setLayout(new GridBagLayout());
        
        //if (!UserMain.self.is_touchscreen())
        {
            createButtons();
            this.setCursor(new Cursor(Cursor.HAND_CURSOR));
        }
        
   

    }

    public void setTitle( String title )
    {
        this.title = title;
    }



    MouseInputHandler mouse_handler;
    WindowHandler window_handler;
    
    public void installListeners()
    {
/*        if (UserMain.self.is_touchscreen())
            return;
  */      
        mouse_handler = new MouseInputHandler();
        Window window = SwingUtilities.getWindowAncestor(this);
        window.addMouseListener(mouse_handler);
        window.addMouseMotionListener(mouse_handler);

        window_handler = new WindowHandler();
        window.addWindowListener(window_handler);
        
        if (window instanceof JDialog)
        {
            JRootPane rootPane = this.getRootPane();
            InputMap iMap = rootPane.getInputMap( JComponent.WHEN_IN_FOCUSED_WINDOW);
            iMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "escape");

            ActionMap aMap = rootPane.getActionMap();
            if (aMap.get("escape") == null)
            {
                aMap.put("escape", new AbstractAction()
                    {
                    @Override
                        public void actionPerformed(ActionEvent e)
                        {
                            close();
                        } });
            }                
            
        }        
    }
    
    public void removeListeners()
    {
       /* if (UserMain.self.is_touchscreen())
            return;
        */
        Window window = SwingUtilities.getWindowAncestor(this);
        window.removeMouseListener(mouse_handler);
        window.removeMouseMotionListener(mouse_handler);

        window.removeWindowListener(window_handler);
        
    }
   
    
    
    private void createButtons()
    {
        add(Box.createHorizontalGlue(),
                new GridBagConstraints(0, 0,
                1, 1,
                1.0, 1.0,
                GridBagConstraints.EAST,
                GridBagConstraints.HORIZONTAL,
                new Insets(0, 0, 0, 0),
                0, 0));

        if (UserMain.self != null && !UserMain.self.is_touchscreen())
        {
            add(iconifyButton = createButton(new IconifyAction(),
                    minimize, minimizePressed, minimizeOver),
                    new GridBagConstraints(1, 0,
                    1, 1,
                    0.0, 1.0,
                    GridBagConstraints.NORTHEAST,
                    GridBagConstraints.NONE,
                    new Insets(1, 0, 0, 2),
                    0, 0));
        }
        
        add(closeButton = createButton(new CloseAction(),
                close, closePressed, closeOver),
                new GridBagConstraints(2, 0,
                1, 1,
                0.0, 1.0,
                GridBagConstraints.NORTHEAST,
                GridBagConstraints.NONE,
                new Insets(1, 0, 0, 2),
                0, 0));
    }

    private static JButton createButton(final AbstractAction action,
            final BufferedImage image,
            final Image pressedImage,
            final Image overImage)
    {
        JButton button = new JButton(action);
        button.setIcon(new ImageIcon(image));
        button.setPressedIcon(new ImageIcon(pressedImage));
        button.setRolloverIcon(new ImageIcon(overImage));
        button.setRolloverEnabled(true);
        button.setBorder(null);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setMargin(new Insets(0, 0, 0, 0));
        button.setFocusable(false);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(image.getWidth(),
                image.getHeight()));
        return button;
    }

    private void close()
    {
        Window w = SwingUtilities.getWindowAncestor(this);

        // DO NOT CLOSE APP IF TS IS ACTIVE
        if (w instanceof Frame && UserMain.self != null && UserMain.self.is_touchscreen())
        {
            return;
        }
            
        
        w.dispatchEvent(new WindowEvent(w,
                WindowEvent.WINDOW_CLOSING));
    }

    private void iconify()
    {
        if (UserMain.self.is_touchscreen())
            return;
        
        Window w = SwingUtilities.getWindowAncestor(this);
        if (w instanceof Frame)
        {
            
            Frame frame = (Frame) SwingUtilities.getWindowAncestor(this);
            if (frame != null)
            {
                frame.setExtendedState(frame.getExtendedState() | Frame.ICONIFIED);
            }
        }
    }

    @Override
    public Dimension getPreferredSize()
    {
        Dimension size = super.getPreferredSize();
        size.height = preferredHeight;
        return size;
    }

    @Override
    public Dimension getMinimumSize()
    {
        Dimension size = super.getMinimumSize();
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

    @Override
    protected void paintComponent(Graphics g)
    {
        if (!isVisible())
        {
            return;
        }

        boolean active = SwingUtilities.getWindowAncestor(this).isActive();

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_OFF);



        Rectangle clip = g2.getClipBounds();
        g2.drawImage(active ? backgroundGradient : inactiveBackgroundGradient,
                clip.x, 0, clip.width, getHeight() - 2, null);

        g2.setColor(active ? lightColor : inactiveLightColor);
        g2.drawLine(0, getHeight() - 1, getWidth(), getHeight() - 1);

        g2.setColor(active ? shadowColor : inactiveShadowColor);
        g2.drawLine(0, getHeight() - 2, getWidth(), getHeight() - 2);

        g2.drawImage(active ? grip : inactiveGrip, 0, 0, null);

        if (title == null && parent instanceof JDialog)
        {
            title = ((JDialog) parent).getTitle();
        }
        
        if (title != null)
        {
            g2.setColor( Main.ui.get_nice_white() );
            g2.setFont(titleFont);
            g2.drawString(title, 22, getHeight() - 6);
        }
    }

    private class CloseAction extends AbstractAction
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (parent != null && parent instanceof UserMain && UserMain.self != null && UserMain.self.is_touchscreen())
                return;
            
            parent.setVisible(false);
        }
    }

    private class IconifyAction extends AbstractAction
    {

        @Override
        public void actionPerformed(ActionEvent e)
        {
            iconify();
        }
    }

    private class MouseInputHandler implements MouseInputListener
    {
        private boolean isMovingWindow = false;
        private boolean isDraggingWindow = false;
        private int dragOffsetX = 0;
        private int dragOffsetY = 0;
        Dimension click_size;
        boolean is_move_cursor = false;
        private static final int BORDER_DRAG_THICKNESS = 2;

        @Override
        public void mousePressed(MouseEvent ev)
        {
            Point dragWindowOffset = ev.getPoint();
            Window w = (Window) ev.getSource();
            if (w != null)
            {
                w.toFront();
            }
            Point convertedDragWindowOffset = SwingUtilities.convertPoint(
                    w, dragWindowOffset, TitlePanel.this);

            Frame f = null;
            Dialog d = null;

            if (w instanceof Frame)
            {
                f = (Frame) w;
                if (UserMain.self.is_touchscreen())
                    return;                
            } 
            else if (w instanceof Dialog)
            {
                d = (Dialog) w;
                
            }

            int frameState = (f != null) ? f.getExtendedState() : 0;

            if (TitlePanel.this.contains(convertedDragWindowOffset))
            {
                if ((f != null && ((frameState & Frame.MAXIMIZED_BOTH) == 0) || (d != null)) && dragWindowOffset.y >= BORDER_DRAG_THICKNESS && dragWindowOffset.x >= BORDER_DRAG_THICKNESS && dragWindowOffset.x < w.getWidth() - BORDER_DRAG_THICKNESS)
                {
                    isMovingWindow = true;
                    dragOffsetX = dragWindowOffset.x;
                    dragOffsetY = dragWindowOffset.y;
                }
            }
            else if (f != null && f.isResizable() && ((frameState & Frame.MAXIMIZED_BOTH) == 0) || (d != null && d.isResizable()))
            {
                if (dragWindowOffset.x > w.getWidth() -8 || dragWindowOffset.y > w.getHeight() -8)
                {
                    isDraggingWindow = true;
                    dragWindowOffset = MouseInfo.getPointerInfo().getLocation();
                    dragOffsetX = dragWindowOffset.x;
                    dragOffsetY = dragWindowOffset.y;
                    click_size = w.getSize();
                }

            }
        }

        @Override
        public void mouseReleased(MouseEvent ev)
        {
            if (isDraggingWindow)
            {
                Window w = (Window) ev.getSource();
                Frame f = null;
                Dialog d = null;

                if (w instanceof Frame)
                {
                    f = (Frame) w;
                    if (UserMain.self.is_touchscreen())
                        return;
                }

                if (f != null && f == UserMain.self)
                {
                    UserMain.self.restart_gui();
                }
            }
            isDraggingWindow = false;
            isMovingWindow = false;

        }

        @Override
        public void mouseDragged(MouseEvent ev)
        {
            Window w = (Window) ev.getSource();
            Frame f = null;
            Dialog d = null;

            if (w instanceof Frame)
            {
                f = (Frame) w;
                if (UserMain.self.is_touchscreen())
                    return;
            } 
            else if (w instanceof Dialog)
            {
                d = (Dialog) w;
            }

            if (isMovingWindow)
            {
                Point windowPt = MouseInfo.getPointerInfo().getLocation();
                windowPt.x = windowPt.x - dragOffsetX;
                windowPt.y = windowPt.y - dragOffsetY;
                w.setLocation(windowPt);
            } else if ((d != null && d.isResizable()) || (f != null && f.isResizable()))
            {
                Point windowPt = MouseInfo.getPointerInfo().getLocation();
                windowPt.x = windowPt.x - dragOffsetX;
                windowPt.y = windowPt.y - dragOffsetY;
                if (click_size != null)
                {
                    if ((click_size.width + windowPt.x) > 50 && (click_size.height + windowPt.y) > 50)                            
                        w.setSize(click_size.width + windowPt.x, click_size.height + windowPt.y);

                }
            }
        }

        @Override
        public void mouseClicked(MouseEvent e)
        {
            // CLOSE WINDOW IG DBLCLICK ON LEFT CORNER
            if (e.getClickCount() == 2)
            {
                if (e.getPoint().x < inactiveGrip.getHeight() && e.getPoint().y < inactiveGrip.getHeight())
                {
                    close();
                }
            }
        }

        @Override
        public void mouseEntered(MouseEvent e)
        {

        }

        @Override
        public void mouseExited(MouseEvent ev)
        {
            Window w = (Window) ev.getSource();
            w.setCursor(Cursor.getDefaultCursor());
        }

        @Override
        public void mouseMoved(MouseEvent ev)
        {
            Point dragWindowOffset = ev.getPoint();
            Window w = (Window) ev.getSource();
            Frame f = null;
            Dialog d = null;

            if (w instanceof Frame)
            {
                f = (Frame) w;
                if (UserMain.self.is_touchscreen())
                    return;
            } 
            else if (w instanceof Dialog)
            {
                d = (Dialog) w;
            }

            Point convertedDragWindowOffset = SwingUtilities.convertPoint(
                    w, dragWindowOffset, w);

            if (convertedDragWindowOffset.x > (w.getWidth() - 8) ||
                    convertedDragWindowOffset.y > (w.getHeight() - 8))
            {
                if ( d == null || d.isResizable())
                {
                    w.setCursor(new Cursor(Cursor.HAND_CURSOR));

                    is_move_cursor = true;
                }
            } 
            else
            {
                if (is_move_cursor)
                {
                    w.setCursor(Cursor.getDefaultCursor());
                }
            }
        }
    }

    private class WindowHandler extends WindowAdapter
    {

        @Override
        public void windowActivated(WindowEvent ev)
        {
            closeButton.setIcon(new ImageIcon(close));
            if (iconifyButton != null)
                iconifyButton.setIcon(new ImageIcon(minimize));
            getRootPane().repaint();
        }

        @Override
        public void windowDeactivated(WindowEvent ev)
        {
            closeButton.setIcon(new ImageIcon(closeInactive));
            if (iconifyButton != null)
                iconifyButton.setIcon(new ImageIcon(minimizeInactive));
            getRootPane().repaint();
        }
    }
}
