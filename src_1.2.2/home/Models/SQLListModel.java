/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Models;

import home.shared.SQL.SQLResult;
import dimm.home.UserMain;
import java.util.ArrayList;
import javax.swing.AbstractListModel;

/**
 *
 * @author Administrator
 */
public class SQLListModel extends AbstractListModel
{
    SQLResult res;
    //int txt_col;
    boolean sort_desc;
    String search_text;
    ArrayList<String> search_array;
    String txt_col_name;
    
    
    public SQLListModel(SQLResult _res, String _txt_col)
    {
        res = _res;
        txt_col_name = _txt_col;
        sort_desc = false;
        search_text = null;
    }
    @Override
    public int getSize()
    {
        if (res == null)
            return 0;
        
        return get_list_size() + 1;
    }
    
    public int get_list_size()
    {
        if (search_array != null)
            return search_array.size();
                    
        return res.getRows();
    }
    public String get_raw_list_elem( int index)
    {
         if (search_array != null)
            return SQLResult.html_to_native( search_array.get(index) );

        String ret = res.getRawString(index, txt_col_name);

        return ret;
    }
    public String get_list_elem( int index)
    {
        String ret = get_raw_list_elem( index );
        if (ret == null)
            return null;

        return SQLResult.html_to_native(ret);
    }

    @Override
    public Object getElementAt( int index )
    {
        if (res == null)
            return null;
        if (index == 0)
        {
            return UserMain.getString("-_alles_-");
        }
        index--;
        
        if (sort_desc)
            index = (get_list_size() - 1) - index;
            
        return get_list_elem(index);
    }
    public Object getRawElementAt( int index )
    {
        if (res == null)
            return null;
        if (index == 0)
        {
            return UserMain.getString("-_alles_-");
        }
        index--;
        
        if (sort_desc)
            index = (get_list_size() - 1) - index;
            
        return get_raw_list_elem(index);
    }

    public void setSearch( String text )
    {
        
        search_text = text.toLowerCase();
        
        if (text.length() == 0)
            search_text = null;
        
        if (search_text == null)
        {
            search_array = null;
        }
        else if (res != null)
        {
            search_array = new ArrayList<String>();
            
            for (int i = 0; i < res.getRows(); i++)
            {
                 String txt = res.getRawString(i, txt_col_name) ;
                 String conv = SQLResult.html_to_native( txt.toLowerCase() );
                 if (conv.indexOf(search_text ) != -1)
                     search_array.add(txt);                     
            }            
        }
    }

    public void setSort( boolean b )
    {
        sort_desc = b;
    }
  
}

