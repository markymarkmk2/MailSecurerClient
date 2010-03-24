/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Rendering;

import javax.swing.JCheckBox;

/**
 *
 * @author media
 */
public class KatButton extends JCheckBox
{
    private int katId;
    private int streamId;
    private int id;
    private String description;
    private int station_id;
    boolean is_usertracklist;

    public KatButton(String name)
    {
        this( name, false );
    }

    public KatButton(String name, boolean is_utl)
    {
        super(name);
        
        katId = -1;
        streamId = -1;     
        id = -1;
        station_id = -1;
        is_usertracklist = is_utl;
    }
    
    public int getKatId()
    {
        return katId;
    }

    public boolean isEqual(KatButton cb_combo)
    {
        if (isSelected() != cb_combo.isSelected())
            return false;

        return true;
    }

    public void setKatId( int katId )
    {
        this.katId = katId;
    }

    public int getStationId()
    {
        return katId;
    }

    public void setStationId( int s )
    {
        this.station_id = s;
    }

    public int getStreamId()
    {
        return streamId;
    }

    public void setStreamId( int streamId )
    {
        this.streamId = streamId;
    }
    public void setDescription( String s )
    {
        this.description = s;
    }
    public String getDescription( )
    {
        return this.description;
    }

    public int getId()
    {
        return id;
    }

    public void setId( int id )
    {
        this.id = id;
    }

    @Override
    public String getText()
    {
        String kat = super.getText();
        if (is_usertracklist)
            return "Tracklist: " + kat;
        
        return kat;
    }
    public String get_kat_txt()
    {
        String kat = super.getText();
        if (is_usertracklist)
            return "Tracklist: " + kat;
        return kat;
    }
    public boolean is_usertracklist()
    {
        return is_usertracklist;
    }

}
