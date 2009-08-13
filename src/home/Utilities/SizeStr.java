package dimm.home.Utilities;


public class SizeStr extends Object
{
    public static final double TB_SIZE = 1.024e3*1.024e3*1.024e3*1.024e3;
    public static final long GB_SIZE = 1024*1024*1024;
    public static final long MB_SIZE = 1024*1024;
    public static final long KB_SIZE = 1024;
    
    double size;
    boolean _is_nok;
    
    public SizeStr( double s )
    {
        size = s;
        _is_nok = false;
    }
    public void set_nok( boolean b )
    {
        _is_nok = b;
    }
    public boolean is_nok()
    {
        return _is_nok;
    }
    public int parse(String s)
    {
        String number = s;
        String dim = "";
        size = 0;
        try
        {
            int space_idx = s.indexOf(1,' ');
            if (space_idx > 0)
            {
                number = s.substring(0, space_idx );
                dim = s.substring(space_idx + 1);
            }
            
            size = Double.parseDouble(number);
            switch(dim.charAt(0))
            {
                case 'T': size *= TB_SIZE; break;
                case 'G': size *= GB_SIZE; break;
                case 'M': size *= MB_SIZE; break;
                case 'k': size *= KB_SIZE; break;
            }
        }
        catch (Exception exc)
        {
            return 1;
        }
        return 0;
    }
        
    public String toString()
    {
        String postfix = "";
        double v = size;
        
        if (v > 1.2e12)
        {
            postfix = "T";
            v /= TB_SIZE;
        }
        else if (v > 1.2e9)
        {
            postfix = "G";
            v /= GB_SIZE;
        }
        else if (v > 1.2e6)
        {
            postfix = "M";
            v /= MB_SIZE;
        }
        else if (v > 1.2e3)
        {
            postfix = "k";
            v /= KB_SIZE;
        }
        
        long l = (long)(v + 0.5);
        v -= l;
        long m = 0;
        if (l == 0)
        {
            m = (long)(v*100 + 0.5);
            v = l + m / 100.0;
        }
        else if (l < 10)
        {
            m = (long)(v*10 + 0.5);
            v = l + m / 10.0;
        }
        
        if (m == 0)
        {
            if (l == 0)
                return "-";
            
            return new Long(l).toString() + " " + postfix;
        }
                
        return new Double(v).toString() + " " + postfix;
    }
    
    public String toString(int digits)
    {
        double v = size;
        long l = (long)(v + 0.5);
        v -= l;
        long m = 0;
        int teiler = 10;
        for (int i = 0; i < (digits-1); i++)
            teiler *= 10;
        m = (long)(v*teiler + 0.5);
        v = l + (double)m / teiler;
        return new Double(v).toString();
    }
    
    public double get_size()
    {
        return size;
    }
}
            
        
