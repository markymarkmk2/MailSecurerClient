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

    static public boolean is_vaild_email(String sEmail)
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

}
