/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dimm.home.native_libs;

import com.ice.jni.registry.RegMultiStringValue;
import com.ice.jni.registry.RegStringValue;
import com.ice.jni.registry.Registry;
import com.ice.jni.registry.RegistryException;
import com.ice.jni.registry.RegistryKey;
import com.ice.jni.registry.RegistryValue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 *
 * @author mw
 */
public class NativeLoader
{

    public static boolean failed;
    public static String err_text;

    boolean copy_to_file( InputStream in, File dest ) throws FileNotFoundException, IOException
    {

        FileOutputStream out = new FileOutputStream(dest);

        byte[] buf = new byte[8192];
        int len;
        while ((len = in.read(buf)) != -1)
        {
            out.write(buf, 0, len);
        }

        in.close();
        out.close();
        return true;
    }

    void load_dll( String dll_name )
    {
        try
        {
            if (is_win())
            {
                try
                {
                    InputStream in = this.getClass().getResourceAsStream("/dimm/home/native_libs/" + dll_name + ".dll");
                    File tempFile = File.createTempFile("dll_name", ".dll");
                    tempFile.deleteOnExit();

                    copy_to_file(in, tempFile);

                    System.load(tempFile.getAbsolutePath());
                }
                catch (FileNotFoundException ex)
                {
                    ex.printStackTrace();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
                catch (RuntimeException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
        catch (Exception e)
        {
            err_text = "Failed to load " + dll_name + ".dll: " + e.getMessage();
            System.err.println(err_text);
            failed = true;
        }
    }
    public NativeLoader()
    {
        failed = false;
        err_text = "";

        load_dll( "ICE_JNIRegistry" );

    }

    public static boolean is_win()
    {
        return (System.getProperty("os.name").startsWith("Win"));
    }

    public static boolean is_linux()
    {
        return (System.getProperty("os.name").startsWith("Linux"));
    }

    public static boolean is_osx()
    {
        return (System.getProperty("os.name").startsWith("Mac"));
    }

    // "Software\\Microsoft\\CurrentVersion\\Explorer\\Advanced"
    public static String get_HKCU_reg_value( String key_name, String value_name )
    {
        RegistryKey regkey = Registry.HKEY_CURRENT_USER;
        return get_reg_value(regkey, key_name, value_name);
    }

    public static String get_HKLM_reg_value( String key_name, String value_name )
    {
        RegistryKey regkey = Registry.HKEY_LOCAL_MACHINE;
        return get_reg_value(regkey, key_name, value_name);
    }

    public static String get_reg_value( RegistryKey regkey, String key_name, String value_name )
    {
        if (!is_win())
        {
            return null;
        }

        try
        {
            RegistryKey key = Registry.openSubkey(regkey, key_name, RegistryKey.ACCESS_ALL);

            RegistryValue stringValue = key.getValue(value_name);
            if (stringValue instanceof RegStringValue)
            {
                return ((RegStringValue) stringValue).getData();
            }

            if (stringValue instanceof RegMultiStringValue)
            {
                String[] arr = ((RegMultiStringValue) stringValue).getData();
                if (arr.length > 0)
                {
                    return arr[0];
                }
            }
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
        return null;
    }

    public static String[] get_reg_value_arr( RegistryKey regkey, String key_name, String value_name )
    {
        if (!is_win())
        {
            return null;
        }

        try
        {
            RegistryKey key = Registry.openSubkey(regkey, key_name, RegistryKey.ACCESS_ALL);

            RegistryValue stringValue = key.getValue(value_name);
            if (stringValue instanceof RegStringValue)
            {
                String[] arr = new String[1];
                arr[0] = ((RegStringValue) stringValue).getData();
                return arr;
            }
            if (stringValue instanceof RegMultiStringValue)
            {
                String[] arr = ((RegMultiStringValue) stringValue).getData();
                return arr;
            }
        }
        catch (Exception exc)
        {
            exc.printStackTrace();
        }
        return null;
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