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
import dimm.home.Utilities.CmdExecutor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


/*


 WEBSTART NATIVE LIB DESASTER
 * WE CANNOT LOAD DLL FROM WEBSTART, THEREFOR WE LOAD DLL IN JAR, WRITE TO DISK AND THEN LOAD THIS FILE...
 *
 *
 *
 * */
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
                    InputStream in;
                    if (is_win64())
                        in = this.getClass().getResourceAsStream("/dimm/home/native_libs/" + dll_name + "64.dll");
                    else
                        in = this.getClass().getResourceAsStream("/dimm/home/native_libs/" + dll_name + ".dll");
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
    public static boolean is_win64()
    {
        return (System.getProperty("os.arch").endsWith("64"));
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

            String value = key.getStringValue(value_name);

            key.closeKey();

            return value;
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

        RegistryKey key = null;
        try
        {
            key = Registry.openSubkey(regkey, key_name, RegistryKey.ACCESS_ALL);

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
        finally
        {
            try
            {
                if (key != null)
                {
                    key.closeKey();

                }
            }
            catch (RegistryException registryException)
            {
            }
        }

        return null;
    }

    public static String resolve_win_path( String path )
    {
        String cmd[] = new String[4];

        cmd[0] = "cmd";
        cmd[1] = "/C";
        cmd[2] = "echo";
        cmd[3] = path;
        CmdExecutor exec = new CmdExecutor( cmd );
        if (exec.exec() == 0 && exec.get_out_array_len() == 1)
        {
            path = exec.get_out_array()[0].toString();
            int len = path.length();

            // CUT OFF " AND \"
            path = path.substring(1, len - 1);

        }
        return path;
    }
    public static String conv_win_to_utf8( String s )
    {
        try
        {
            char[] ca = s.toCharArray();
            byte[] b = new byte[ca.length];
            for (int j = 0; j < ca.length; j++)
            {
                char c = ca[j];
                b[j] = (byte) (c & 0xFF);
            }
            s = new String(b, "Cp1250");
        }
        catch (UnsupportedEncodingException unsupportedEncodingException)
        {
        }
        return s;
    }

    public String build_user_profile_path()
    {
        /*
         # On Linux, the path is usually ~/.thunderbird/xxxxxxxx.default/
        # On Mac OS X, the path is usually ~/Library/Thunderbird/Profiles/xxxxxxxx.default/
         */
        if (NativeLoader.is_win())
        {
            String user_appdata_path = NativeLoader.get_HKCU_reg_value("Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\Shell Folders", "AppData");
            if (user_appdata_path == null)
            {
                user_appdata_path = NativeLoader.get_HKCU_reg_value("Software\\Microsoft\\Windows\\CurrentVersion\\Explorer\\User Shell Folders", "AppData");
            }
            user_appdata_path = NativeLoader.resolve_win_path(user_appdata_path);


            int idx = user_appdata_path.lastIndexOf("\\");
            if (idx > 0)
                user_appdata_path = user_appdata_path.substring(0, idx);

            return user_appdata_path;
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
