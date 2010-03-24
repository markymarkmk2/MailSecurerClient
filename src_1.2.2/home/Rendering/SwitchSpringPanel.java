/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

import dimm.home.SwitchPanel;
import dimm.home.UserMain;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import org.jdesktop.animation.timing.TimingTarget;
import org.jdesktop.fuse.InjectedResource;

/**
 *
 * @author Administrator
 */
public abstract class SwitchSpringPanel extends SwitchPanel
{
    @InjectedResource
    private SpringGlassPane glassPane;
    
    
    protected UserMain main;
    
    public SwitchSpringPanel(UserMain _main, int _panel_id)
    {
        super(_panel_id);
        main = _main;
        glassPane = main.get_spring_glass_pane();
    }
    

    
    public void spring_button_action( Object o, TimingTarget the_end )
    {
        if ( o instanceof JButton )
        {
            JButton bt = (JButton) o;
            Rectangle r = bt.getBounds();
            r.y += 50;
            if ( bt.getIcon() != null)
                glassPane.showSpring(r, ((ImageIcon) bt.getIcon()).getImage(), the_end);
            else
                glassPane.showSpring(r, null, the_end);
                
        }
    }
    
    public TimingTargetAdapter make_spring_button_dlg( final GlossDialogPanel pnl, final Point pos, final int dx, final int dy, final String title )
    {
            
            
            
        TimingTargetAdapter tt = new TimingTargetAdapter()
        {
            GenericGlossyDlg dlg;
            
            @Override
            public void begin()
            {
                super.begin();
                
                dlg = new GenericGlossyDlg(null, true, pnl);
            }                

            @Override
            public void end()
            {
                if (dx >0 && dy > 0)
                    dlg.setSize(dx, dy);
                else
                    dlg.pack();
                
                dlg.setLocation(pos);
                dlg.setTitle(title);
                dlg.setVisible(true);
            }
        };
        return tt;
    }
    
    public TimingTargetAdapter make_spring_button_dlg( final SQLOverviewDialog dlg, final Point pos, final int dx, final int dy, final String title )
    {
        TimingTargetAdapter tt = new TimingTargetAdapter()
        {

           

            @Override
            public void begin()
            {
                super.begin();

                dlg.setLocation( pos );
                dlg.setSize(650, 350);
                dlg.setTitle(title);
            }

            @Override
            public void end()
            {
                dlg.setVisible(true);
            }
        };

        return tt;
    }
    public TimingTargetAdapter make_spring_button_dlg( final GlossDialogPanel pnl, final Point pos, final String title )
    {
        return make_spring_button_dlg( pnl, pos, -1, -1, title );
    }
    
    public TimingTargetAdapter make_spring_button_dlg( final SQLOverviewDialog dlg, final Point pos, final String title )
    {
        return make_spring_button_dlg( dlg, pos, -1, -1, title );
    }
    

}
