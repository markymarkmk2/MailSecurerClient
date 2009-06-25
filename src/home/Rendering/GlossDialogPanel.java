/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

import dimm.home.TSEntryDialog;
import java.awt.Cursor;
import javax.swing.JButton;
import javax.swing.JComponent;

/**
 *
 * @author Administrator
 */
public abstract class GlossDialogPanel extends GlossPanel  implements TSEntryDialog
{
    public abstract JButton get_default_button();
    protected GenericGlossyDlg my_dlg;
    
   
    public void setDlg(GenericGlossyDlg dlg)
    {
        my_dlg = dlg; 
    }
    public GenericGlossyDlg getDlg()
    {
        return my_dlg; 
    }
   @Override
    public void setVisible(boolean aFlag)
    {
        if (my_dlg != null && !aFlag) // DETECT CLOSE
            my_dlg.setVisible(aFlag);
        else
            super.setVisible(aFlag);
            
    }
   
    public void deactivate()
    {
    }

    public void activate()
    {
        
    }
    @Override
    public boolean VKBDOkay( JComponent comp )
    {
        return false; // EVENT NOT HANDLED
    }

    Cursor lastCursor = null;
    public void set_wait_cursor( boolean b )
    {
        if (b)
        {
           Cursor waitCursor = new Cursor(Cursor.WAIT_CURSOR);
           lastCursor = getCursor();
           setCursor(waitCursor);
        }
        else if (lastCursor != null)
        {
            setCursor(lastCursor);
        }
        else
        {
            setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }
        
    }        
    
    
}
