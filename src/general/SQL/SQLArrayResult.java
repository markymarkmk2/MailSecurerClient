/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.general.SQL;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Administrator
 */
public class SQLArrayResult 
{
    private ArrayList<ArrayList> resultList;
    private String qry;
    private ArrayList<String> fieldList;
    private ArrayList<String> fieldTypeList;
    private Throwable exception;

    public SQLArrayResult(String _qry)
    {
        qry = _qry;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException
    {
        SQLArrayResult cl = new SQLArrayResult(qry);
        
        ArrayList<ArrayList> new_res = new ArrayList<ArrayList>();
        for (int i = 0; i < resultList.size(); i++)
        {
            ArrayList<String> list = (ArrayList<String>)resultList.get(i);
                                            
            ArrayList<String> field_list = new ArrayList<String>();
                
                for (int f = 0; f < list.size(); f++)
                {
                    field_list.add(list.get(f));
                }
                

            new_res.add(list);

        }

        cl.setResultList(new_res);
        cl.setFieldTypeList( (ArrayList<String>) fieldTypeList.clone() );
        cl.setFieldList( (ArrayList<String>) fieldList.clone() );
        
        return cl;
        
    }
    public SQLArrayResult dup()
    {
        try
        {
            return (SQLArrayResult) clone();
        }
        catch (CloneNotSupportedException ex)
        {
            Logger.getLogger(SQLArrayResult.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    

    public ArrayList<ArrayList> getResultList()
    {
        return resultList;
    }

    public void setResultList(ArrayList<ArrayList> resultList)
    {
        this.resultList = resultList;
    }

    public String getQry()
    {
        return qry;
    }

    public void setQry(String qry)
    {
        this.qry = qry;
    }

    public ArrayList<String> getFieldTypeList()
    {
        return fieldTypeList;
    }

    public void setFieldTypeList(ArrayList<String> fieldTypeList)
    {
        this.fieldTypeList = fieldTypeList;
    }

    public ArrayList<String> getFieldList()
    {
        return fieldList;
    }

    public void setFieldList(ArrayList<String> fieldList)
    {
        this.fieldList = fieldList;
    }

    public Throwable getException()
    {
        return exception;
    }

    public void setException(Throwable exception)
    {
        this.exception = exception;
    }

    public String getAllFieldString( int r )
    {
        try
        {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < fieldList.size(); i++)
            {
                Object o = resultList.get(r).get(i);
                if (o != null)
                    sb.append(o.toString() );
            }
            return sb.toString();

        } catch (Exception exc)
        {
            err_log("Ungültiger Zugriff in getAllFieldString( " + r + " ):" + exc.getMessage());
            exc.printStackTrace();
        }
        return null;
    }
    
    public String getString( int r, int c )
    {
        try
        {
            Object o = resultList.get(r).get(c);
            if (o != null)
                return o.toString();

        } catch (Exception exc)
        {
            err_log("Ungültiger Zugriff in getString( " + r + ", " + c + " ):" + exc.getMessage());
            exc.printStackTrace();
            
        }
        return null;
    }
    public String getString( int r, String field )
    {
        int c = getField(field);
        return getString(  r,  c );      
    }
    public long getLong( int r, int c )
    {
        try
        {
            String lstr =  getString( r, c );
            if (lstr.length() == 0)
                return 0;

            return Long.parseLong(lstr);

        } catch (Exception exc)
        {
            err_log("Ungültiger Zugriff in getLong( " + r + ", " + c + " ):" + exc.getMessage());
            exc.printStackTrace();
        }
        return -1;
    }         
    public long getLong( int r, String field )
    {
        int c = getField(field);
        return getLong(  r,  c );      
    }
    public int getInt( int r, int c )
    {

        try
        {
            String lstr =  getString( r, c );
            if (lstr.length() == 0)
                return 0;

            return Integer.parseInt(lstr);

        } catch (Exception exc)
        {
            err_log("Ungültiger Zugriff in getInt( " + r + ", " + c + " ):" + exc.getMessage());
            exc.printStackTrace();
        }
        return -1;
    }         
    public int getInt(int r, String field)
    {
        int c = getField(field);
        return getInt(  r,  c );      
    }
    
    public boolean getBooleanValue( int r, int c )
    {
        try
        {
            Object o = resultList.get(r).get(c);
            if (o != null)
            {
                String lstr =  o.toString();
                if (lstr.length() == 0)
                    return false;

                if (lstr.charAt(0) == '0')
                    return false;
                return true;
            }

        } catch (Exception exc)
        {
            err_log("Ungültiger Zugriff in getBooleanValue( " + r + ", " + c + " ):" + exc.getMessage());
            exc.printStackTrace();
        }
        return false;
    }     
    public boolean getBooleanValue( int r, String field )
    {
        int c = getField(field);
        return getBooleanValue(  r,  c );      
    }
    
    public int getField( String name )
    {
        if (fieldList != null)
        {
            for (int i = 0; i < fieldList.size(); i++)
            {
                if (fieldList.get(i).compareToIgnoreCase(name) == 0)
                    return i;
            }
        }
        err_log("Unbekanntes DB-Feld " + name );
        
        return -1;
    }                    
    public String getFieldName( int c )
    {
        try
        {
            return fieldList.get(c).toString();

        } catch (Exception exc)
        {
            err_log("Ungültige Feldnummer " + c + " in getFieldName: " + exc.getMessage());
            exc.printStackTrace();
        }
        return null;
    }                    
    public String getFieldType( int c )
    {
        try
        {
            return fieldTypeList.get(c).toString();

        } catch (Exception exc)
        {
            err_log("Ungültiger Zugriff in getFieldType:" + exc.getMessage());
            exc.printStackTrace();
            
        }
        return null;
    }           
    public int getCols( )
    {
        if (fieldList != null)
            return fieldList.size();
        return 0;
    }                    
    public int getRows( )
    {
        if (resultList != null)
            return resultList.size();
        
        return 0;
    }                    
    public String getIndexedString( int c, int ic, long idx )
    {
        try
        {
            for (int i = 0; i < getRows(); i++)
            {
                long id = getLong(i, ic);
                if (id == idx)
                return getString( i, c );
            }

        } catch (Exception exc)
        {
            err_log("Ungültiger Zugriff in getIndexedString( " + c + ", " + ic + ", " + idx + " ):" + exc.getMessage());
        }
        return null;
    }
    public long getIndexedLong( int c, int ic, long idx )
    {
        try
        {
            String lstr =  getIndexedString( c, ic, idx );
            if (lstr != null)
                return Long.parseLong(lstr);

        } catch (Exception exc)
        {
            err_log("Ungültiger Zugriff in getIndexedLong( " + c + ", " + ic + ", " + idx + " ):" + exc.getMessage());
        }
        return -1;
    }                    
    
    SQLArrayResult append(SQLArrayResult linked_res)
    {
        if (linked_res.getRows() == 0)
            return this;
        
        if (getCols() != linked_res.getCols())
        {
            err_log("Ungültiges append, columncount nicht gleich");
            return this;
        }
        
        SQLArrayResult clone_result;
        try
        {
            clone_result = (SQLArrayResult) clone();

        }
        catch (CloneNotSupportedException cloneNotSupportedException)
        {
            err_log("Append, Exception: " + cloneNotSupportedException.getMessage());
            return this;            
        }
        for (int i = 0; i < linked_res.getResultList().size(); i++)
        {
            clone_result.getResultList().add( linked_res.getResultList().get(i) );        
        }
        return clone_result;
    }

    private void err_log( String string )
    {
        Logger.getLogger("SQLArrayresult").log(Level.SEVERE, string);
    }


    

}
