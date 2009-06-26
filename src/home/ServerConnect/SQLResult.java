/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author mw
 */
public class SQLResult<T> extends ArrayList<T>
{
    private String qry;
    private Class retClass;
    private long duration;
    private Exception ex;
    private int errCode;
    private String errText;

    
    public SQLResult()
    {
        duration = 0;
    }

    /**
     * @return the qry
     */
    public String getQry()
    {
        return qry;
    }

    /**
     * @param qry the qry to set
     */
    public void setQry( String qry )
    {
        this.qry = qry;
    }

    /**
     * @return the retClass
     */
    public Class getRetClass()
    {
        return retClass;
    }

    /**
     * @param retClass the retClass to set
     */
    public void setRetClass( Class retClass )
    {
        this.retClass = retClass;
    }

    /**
     * @return the duration
     */
    public long getDuration()
    {
        return duration;
    }

    /**
     * @param duration the duration to set
     */
    public void setDuration( long duration )
    {
        this.duration = duration;
    }

    /**
     * @return the ex
     */
    public Exception getEx()
    {
        return ex;
    }

    /**
     * @param ex the ex to set
     */
    public void setEx( Exception ex )
    {
        this.ex = ex;
    }



    /**
     * @param resultList the resultList to set
     */
    public void setResultList( List resultList )
    {
        this.addAll( resultList );
    }

    /**
     * @return the errCode
     */
    public int getErrCode()
    {
        return errCode;
    }

    /**
     * @param errCode the errCode to set
     */
    public void setErrCode( int errCode )
    {
        this.errCode = errCode;
    }

    /**
     * @return the errText
     */
    public String getErrText()
    {
        return errText;
    }

    /**
     * @param errText the errText to set
     */
    public void setErrText( String errText )
    {
        this.errText = errText;
    }

    public Object getObject( int index )
    {
        Object o = null;
        try
        {
            o = get(index);
        }
        catch (Exception exc)
        {
            System.err.println("Invalid result index " + index + " we have " + size() + ": " + exc.getMessage() );
            return null;
        }
        return o;
    }
    
    public String getRawString( int index, String  txt_col_name )
    {
        Object o = getObject( index );
        if (o == null)
            return null;

        try
        {
            Method m = o.getClass().getDeclaredMethod("get" + txt_col_name);
            Object r = m.invoke(o);
            return r.toString();
        }
        catch (Exception _ex)
        {
            System.err.println("Error while resolving \"get" + txt_col_name + "() for " + o.getClass().getName() + ": " + _ex.getMessage() );
        }
        return null;
    }

    public String getString( int index, String  txt_col_name )
    {
        String ret = getRawString( index, txt_col_name );

        if (ret != null)
        {
            ret = SQLCall.html_to_native(ret);
        }
        return ret;
    }
    
    public int getInt( int index, String col )
    {
        String flags = getRawString(index, col);
        if (flags == null)
            return 0;
        try
        {
            return Integer.parseInt(flags);
        }
        catch (Exception _ex)
        {
            System.err.println("Error while converting Integer Object Method " + col + ": <" + flags + "> " + _ex.getMessage() );
        }

        return 0;
    }

    public long getLong( int index, String col )
    {
        String flags = getRawString(index, col);
        if (flags == null)
            return 0;
        try
        {
            return Long.parseLong(flags);
        }
        catch (Exception _ex)
        {
            System.err.println("Error while converting Long Object Method " + col + ": <" + flags + "> " + _ex.getMessage() );
        }

        return 0;
    }
    
    public boolean getBoolean( int index, String  col_name )
    {
        String ret = getRawString( index, col_name );
        if (ret == null || ret.length() == 0)
            return false;
        
        char ch = ret.charAt(0);
        if (ch == 'j' || ch == 'J' || ch == 'y' || ch == 'Y' || ch == '1')
            return true;
        return false;
    }
    
}
