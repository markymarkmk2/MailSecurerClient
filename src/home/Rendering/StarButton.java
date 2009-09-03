/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 *
 * @author Administrator
 */
public class StarButton extends JButton
{
    static ImageIcon yellow_icon;
    static ImageIcon gray_icon;
    static ImageIcon red_icon;

    private int favVal;
    public static final int MAX_STARS = 3;

    public StarButton()
    {
        if (yellow_icon == null)
        {
            yellow_icon = new ImageIcon(getClass().getResource("/dimm/home/images/stern_y_small.png") );
            red_icon = new ImageIcon(getClass().getResource("/dimm/home/images/stern_rot_small.png") );
            gray_icon = new ImageIcon(getClass().getResource("/dimm/home/images/stern_grau_small.png") );
            
        }

    }
    @Override
    protected void paintComponent(Graphics g) 
    {
        Graphics2D g2 = (Graphics2D) g;
        
        ImageIcon no_highlight = gray_icon;
        ImageIcon highlight = yellow_icon;
        int stars = favVal;
        if (stars < 0)
        {
            highlight = red_icon;
            stars *= -1;
        }
        for (int i = 0; i < MAX_STARS; i++)
        {
            if (i < stars)
                highlight.paintIcon(this, g, i * 11, 1);
            else
                no_highlight.paintIcon(this, g, i * 11, 1);                
        }
//        super.paintComponent(g);
    }
    
/*
    @Override
    public int getWidth()
    {
        return super.getWidth() + 33;
    }
*/
   

    public int getFavVal() 
    {
        return favVal;
    }

    public void setFavVal(int favVal) 
    {
        this.favVal = favVal;
    }
 
}
