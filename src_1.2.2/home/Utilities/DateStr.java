package dimm.home.Utilities;

import java.text.SimpleDateFormat;
import java.util.*;

public class DateStr extends Object
{
    
    long u_date;
    String d_str;
    boolean with_seconds;
    
    public DateStr(long ud)
    {
        this( ud, false );
    }
    public DateStr(long ud, boolean ws)
    {
        u_date = ud;
        with_seconds = ws;
        try
        {
            SimpleDateFormat formatter = new SimpleDateFormat ( get_fmt_str() );
            Date date = new Date(u_date*1000);
            d_str = formatter.format(date);        
        }
        catch (Exception exc)
        {
            d_str = "?";
        }
    }
        
    public String toString()
    {
        return d_str;
    }
    public String get_fmt_str()
    {
         return with_seconds ? "E dd.MM.yyyy  HH:mm:ss" : "E dd.MM.yyyy  HH:mm";
    }
    
    public long get_u_date()
    {
        return u_date;
    }
    public int parse( String s)
    {
        SimpleDateFormat formatter = new SimpleDateFormat (get_fmt_str());
        try
        {
            Date date = formatter.parse(s);
            u_date = date.getTime() / 1000;            
        }
        catch (Exception exc)
        {
            return 1;
        }
        d_str = s;
        return 0;
    }
    public int parse( String s, String fmt)
    {
        SimpleDateFormat formatter = new SimpleDateFormat (fmt);
        try
        {
            Date date = formatter.parse(s);
            u_date = date.getTime() / 1000;            
        }
        catch (Exception exc)
        {
            return 1;
        }
        d_str = s;
        return 0;
    }
        
}
            
        
