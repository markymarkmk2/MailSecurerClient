/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dimm.home.native_libs;

import com.ice.jni.registry.RegStringValue;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;
import com.ice.jni.registry.RegistryValue;
import java.io.File;

/**
 *
 * @author mw
 */
public class NativeLoader
{

    public static boolean failed;
    public static String err_text;

    public NativeLoader()
    {
        failed = false;
        err_text = "";

        try
        {
            if (is_win())
            {

                File lib = new File("ICE_JNIRegistry.dll");
                System.load(lib.getAbsolutePath());
            }
        }
        catch (Exception e)
        {
            err_text = "Failed to load ICE_JNIRegistry.dll: " + e.getMessage();
            System.err.println(err_text);
            failed = true;

        }
    }

    public static boolean is_win()
    {
        return (System.getProperty("os.name").startsWith("Win"));
    }

    /**
     * @param args
     */
    void test( String[] args )
    {
        // TODO Auto-generated method stub
        try
        {
            Registry registry = new Registry();
            registry.debugLevel = true;
            RegistryKey regkey = Registry.HKEY_CURRENT_USER;
            RegistryKey key = Registry.openSubkey(regkey, "Software\\Microsoft\\CurrentVersion\\Explorer\\Advanced", RegistryKey.ACCESS_ALL);


            //I suppose there is a javaci value at Software\\Microsoft\\CurrentVersion\\Explorer\\Advanced

            RegStringValue stringValue = new RegStringValue(key, "javaci", RegistryValue.REG_SZ);
            stringValue.setData("Hello");
            key.setValue(stringValue);

        }
        catch (RegistryException ex)
        {
            ex.printStackTrace();
        }

    }
}
