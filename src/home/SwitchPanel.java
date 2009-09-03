/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home;

import dimm.home.Rendering.GlossPanel;


/**
 *
 * @author Administrator
 */
public abstract class SwitchPanel extends GlossPanel
{
    int panel_id;
    public SwitchPanel(int _panel_id)
    {
        super();
        panel_id = _panel_id;
    }
    public int get_panel_id()
    {
        return panel_id;
    }

    public abstract void activate_panel();
    public abstract void deactivate_panel();
    public void panel_full_visible()
    {
    }
}
