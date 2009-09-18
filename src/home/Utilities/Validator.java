/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Utilities;

import org.apache.commons.validator.EmailValidator;

/**
 *
 * @author mw
 */
public class Validator
{

    static public boolean is_valid_email(String sEmail)
    {
        try
        {
            EmailValidator emailValidator = EmailValidator.getInstance();
            return emailValidator.isValid(sEmail);
        }
        catch (Exception exception)
        {
        }
        return false;
    }
    static public boolean is_valid_name(String name, int max_len)
    {
        try
        {
            if (name.length() >0 && name.length() < max_len)
                return true;
        }
        catch (Exception exception)
        {
        }
        return false;
    }
    static public boolean is_valid_path(String path, int max_len)
    {
        try
        {
            if (path.length() >0 && path.length() < max_len)
                return true;
        }
        catch (Exception exception)
        {
        }
        return false;
    }
    static public boolean is_valid_port(String port)
    {
        try
        {
            int p = Integer.parseInt(port);
            if (p > 0 && p < 0xFFFF)
                return true;
        }
        catch (Exception exception)
        {
        }
        return false;
    }

    public static boolean is_valid_int( String text, int min, int max )
    {
        try
        {
            int p = Integer.parseInt(text);
            if (min != max)
            {
                if (p < min || p > max)
                    return false;
            }
            return true;
        }
        catch (Exception exception)
        {
        }
        return false;
    }



}
