/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dimm.home.ServerConnect;

import java.lang.reflect.Method;
import dimm.general.SQL.*;
import dimm.general.hibernate.DiskArchive;
import dimm.general.hibernate.DiskSpace;
import dimm.general.hibernate.Mandant;
import dimm.home.httpd.*;
import com.thoughtworks.xstream.XStream;

import java.net.URL;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;

/**
 *
 * @author mw
 */
public class SQLCall
{

    MWWebServiceService service;
    MWWebService port;
    String name;
    String server_url = "http://192.168.1.145:8050/1234";
    long last_duration_ms;
    long last_start;
    long last_end;
    String last_err_txt;
    String last_return;
    String last_cmd;
    int last_err_code;
    Exception last_ex;

    public SQLCall()
    {
        System.setProperty("javax.net.ssl.trustStore", "jxws.ts");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");

    }

    public boolean init()
    {
        try
        {
            // GET LOCAL WDSL FILE
            URL wdsl_url = getClass().getResource("/dimm/home/WSDLServices/MWWebServiceService.wsdl");

            // CREATE SERVICE AND PORT
            service = new MWWebServiceService(wdsl_url, new QName("http://Httpd.home.dimm/", "MWWebServiceService"));
            port = service.getMWWebServicePort();

            // SET NEW ENDPOINTADRESS
            BindingProvider bp = (BindingProvider) port;
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, server_url);

            return true;
        }
        catch (Exception e)
        {
            // TODO
            e.printStackTrace();
            last_ex = e;
            return false;
        }
    }

    public ConnectionID open()
    {
        return open("");
    }

    void init_stat( String cmd )
    {
        last_start = System.currentTimeMillis();
        last_err_txt = "";
        last_return = "";
        last_cmd = cmd;
        last_err_code = 0;
        last_duration_ms = 0;
        last_ex = null;
    }

    void calc_stat( String ret )
    {
        last_end = System.currentTimeMillis();
        last_duration_ms = last_end - last_start;
        last_return = ret;
    }

    public String get_last_err_txt()
    {
        return last_err_txt;
    }

    public int get_last_err_code()
    {
        return last_err_code;
    }

    public Exception get_last_ex()
    {
        return last_ex;
    }

    long get_last_duration()
    {
        return last_duration_ms;
    }

    public String get_last_err()
    {
        String ex_str = "";
        if (last_ex != null)
        {
            ex_str = last_ex.getMessage();
        }

        return "SQLCall <" + last_cmd + "> Code:" + last_err_code + " Txt:" + last_err_txt + " EX:" + ex_str;

    }

    public ConnectionID open( String db )
    {

        init_stat("");

        try
        {
            String ret = port.open(db);

            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));

            if (retcode == 0)
            {
                ConnectionID cid = new ConnectionID(ret.substring(idx + 2));
                return cid;
            }
            last_err_code = retcode;
        }
        catch (Exception exc)
        {
            last_ex = exc;
        }
        return null;
    }

    public StatementID createStatement( ConnectionID c )
    {
        init_stat("");

        try
        {
            String ret = port.createStatement(c.getId());
            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));

            calc_stat(ret);
            if (retcode == 0)
            {
                StatementID cid = new StatementID(ret.substring(idx + 2));
                return cid;
            }
            last_err_code = retcode;
        }
        catch (NumberFormatException numberFormatException)
        {
            last_ex = numberFormatException;
        }
        return null;
    }

    public String close( ConnectionID c )
    {
        String st = port.close(c.getId());
        return st;
    }

    public String close( StatementID c )
    {
        String st = port.close(c.getId());
        return st;
    }

    public String close( ResultSetID c )
    {
        String st = port.close(c.getId());
        return st;
    }

    static public String encode( String cmd )
    {
        cmd = cmd.replace("\\", "\\\\");
        cmd = cmd.replace("'", "\\'");
        cmd = cmd.replace("\"", "\\\"");
        return cmd;
    }

    public SQLArrayResult get_sql_array_result( ResultSetID r )
    {
        init_stat("");
        try
        {

            String ret = port.getSQLArrayResult(r.getId());

            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));

            if (retcode == 0)
            {
                String xml = ret.substring(idx + 2);

                XStream xstream = new XStream();
                SQLArrayResult retarr = (SQLArrayResult) xstream.fromXML(xml);
                return retarr;
            }
            last_err_code = retcode;

        }
        catch (Exception ex)
        {
            last_ex = ex;
            ex.printStackTrace();
            return null;
        }

        return null;
    }

    public ResultSetID executeQuery( StatementID sta, String qry/*, SQLResult result*/ )
    {
        init_stat(qry);

        String ret = null;
        try
        {
            ret = port.executeQuery(sta.getId(), qry);

            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));
            if (retcode == 0)
            {
                ResultSetID cid = new ResultSetID(ret.substring(idx + 2));
                return cid;
            }
            last_err_code = retcode;

        }
        catch (Exception ex)
        {
            last_ex = ex;
            ex.printStackTrace();
        }
        return null;
    }

    public int executeUpdate( StatementID sta, String qry/*, SQLResult result*/ )
    {
        init_stat(qry);

        String ret = null;
        try
        {

            ret = port.executeUpdate(sta.getId(), qry);

            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));
            if (retcode == 0)
            {
                int rows = Integer.parseInt(ret.substring(idx + 2));
                return rows;
            }
            last_err_code = retcode;
        }
        catch (Exception ex)
        {
            last_ex = ex;
            ex.printStackTrace();
        }

        return 0;
    }

    public boolean execute( StatementID sta, String qry/*, SQLResult result*/ )
    {
        init_stat(qry);

        String ret = null;
        try
        {

            ret = port.execute(sta.getId(), qry);

            calc_stat(ret);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));
            if (retcode == 0)
            {
                int ok = Integer.parseInt(ret.substring(idx + 2));
                return ok == 1 ? true : false;
            }
            last_err_code = retcode;
        }
        catch (Exception ex)
        {
            last_ex = ex;
            ex.printStackTrace();
        }

        return false;
    }



    String  get_name_from_hibernate_class( String rec_name )
    {
        
        StringBuffer sb = new StringBuffer();

        for (int i = 0; i < rec_name.length(); i++)
        {
            char ch = new Character( rec_name.charAt(i) );

            if (i == 0)
            {
                ch = Character.toLowerCase(ch);
            }
            else
            {
                if (Character.isUpperCase(ch))
                {
                    sb.append('_');
                    ch = Character.toLowerCase(ch);
                }
            }
            sb.append(ch);
        }


        return sb.toString();
    }
    String  get_name_from_hibernate_class( Object o )
    {
        return get_name_from_hibernate_class(  o.getClass().getSimpleName() );
    }

    public boolean Insert( StatementID sta, Object o )
    {
        boolean ret = false;

        try
        {
            String rec_name = get_name_from_hibernate_class( o );
            Method getId = o.getClass().getDeclaredMethod("getId");
            Object r = getId.invoke(o);
            int id = ((Integer) r).intValue();
            if (id != 0)
            {
                return false;
            }

            String vals = "";
            String fields = "";
            int field_idx = 0;
            Method[] meths = o.getClass().getDeclaredMethods();
            for (int i = 0; i < meths.length; i++)
            {
                Method method = meths[i];

                String ret_type = method.getReturnType().getName();
                String meth_name = method.getName();

                boolean is_getter = meth_name.startsWith("get");
                boolean is_setter = meth_name.startsWith("set");

                if (!is_getter)
                {
                    continue;
                }

                if (method.getName().compareTo("getId") == 0)
                {
                    continue;
                }
                String field_name = get_name_from_hibernate_class( method.getName().substring(3) );

                if (ret_type.compareTo("java.lang.String") == 0 || ret_type.compareTo("java.lang.Integer") == 0 || ret_type.compareTo("java.lang.Long") == 0)
                {
                    if (field_idx > 0)
                    {
                        vals += ",";
                        fields += ",";
                    }
                    field_idx++;

                    Object str_obj = method.invoke(o);
                    if (str_obj == null)
                    {
                        throw new Exception( "Object " + rec_name + " has not value for method " + meth_name );
                    }
                    String val = method.invoke(o).toString();
                    val = encode(val);

                    vals += "'" + val + "'";
                    fields += field_name;
                }
                else if (ret_type.compareTo("int") == 0)
                {
                    if (field_idx > 0)
                    {
                        vals += ",";
                        fields += ",";
                    }
                    field_idx++;

                    vals += " " + ((Integer) method.invoke(o)).intValue() + " ";
                    fields += field_name;
                }
                else if (ret_type.compareTo("long") == 0)
                {
                    if (field_idx > 0)
                    {
                        vals += ",";
                        fields += ",";
                    }
                    field_idx++;

                    vals += " " + ((Long) method.invoke(o)).longValue() + " ";
                    fields += field_name;
                }
                else if (ret_type.contains(".DiskArchive"))
                {
                    DiskArchive da = (DiskArchive) method.invoke(o);

                    if (field_idx > 0)
                    {
                        vals += ",";
                        fields += ",";
                    }
                    field_idx++;

                    vals += "'" + da.getId() + "'";
                    fields += "da_id";
                }
                else if (ret_type.contains(".DiskSpace"))
                {
                    DiskSpace ds = (DiskSpace) method.invoke(o);

                    if (field_idx > 0)
                    {
                        vals += ",";
                        fields += ",";
                    }
                    field_idx++;

                    vals += "'" + ds.getId() + "'";
                    fields += "ds_id";
                }
                else if (ret_type.contains(".Mandant"))
                {
                    Mandant m = (Mandant) method.invoke(o);

                    if (field_idx > 0)
                    {
                        vals += ",";
                        fields += ",";
                    }
                    field_idx++;

                    vals += "'" + m.getId() + "'";
                    fields += "mid";
                }
                else if (!ret_type.contains(".hibernate.") && !ret_type.contains("java.util.Set") )
                {
                    Object unknown = method.invoke(o);
                    if (unknown != null)
                        throw new Exception( "Invalid return type for Method " + meth_name );
                }
            }
            String ins_stmt = "insert into " + rec_name + " (" + fields + ") values (" + vals + ")";

            int rows = executeUpdate(sta, ins_stmt);

            return (rows == 1) ? true : false;

            
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            last_ex = ex;
        }
        return false;
    }

    public boolean Update( StatementID sta, Object o )
    {
        boolean ret = false;

        try
        {
            String rec_name = get_name_from_hibernate_class( o );
            Method getId = o.getClass().getDeclaredMethod("getId");
            Object r = getId.invoke(o);
            int id = ((Integer) r).intValue();
            if (id == 0)
            {
                return false;
            }
            String upd_cmd = "";
            int field_idx = 0;
            Method[] meths = o.getClass().getDeclaredMethods();
            for (int i = 0; i < meths.length; i++)
            {
                Method method = meths[i];

                String ret_type = method.getReturnType().getName();
                String meth_name = method.getName();

                boolean is_getter = meth_name.startsWith("get");
                boolean is_setter = meth_name.startsWith("set");

                if (!is_getter)
                {
                    continue;
                }

                if (method.getName().compareTo("getId") == 0)
                {
                    continue;
                }
                String field_name = get_name_from_hibernate_class( method.getName().substring(3) );

                if (ret_type.compareTo("java.lang.String") == 0 || ret_type.compareTo("java.lang.Integer") == 0 || ret_type.compareTo("java.lang.Long") == 0)
                {
                    if (field_idx > 0)
                    {
                        upd_cmd += ",";
                    }
                    field_idx++;
                    String val = method.invoke(o).toString();
                    val = encode(val);

                    upd_cmd += field_name + "='" + val + "'";
                }
                else if (ret_type.compareTo("int") == 0)
                {
                    if (field_idx > 0)
                    {
                        upd_cmd += ",";
                    }
                    field_idx++;

                    upd_cmd += field_name + "='" + ((Integer) method.invoke(o)).intValue() + "'";
                }
                else if (ret_type.compareTo("long") == 0)
                {
                    if (field_idx > 0)
                    {
                        upd_cmd += ",";
                    }
                    field_idx++;

                    upd_cmd += field_name + "='" + ((Long) method.invoke(o)).longValue() + "'";
                }
                else if (ret_type.contains(".DiskArchive"))
                {
                    DiskArchive da = (DiskArchive) method.invoke(o);

                    if (da != null)
                    {
                        if (field_idx > 0)
                        {
                            upd_cmd += ",";
                        }
                        field_idx++;

                        upd_cmd += "da_id='" + da.getId() + "'";
                    }
                }
                else if (ret_type.contains(".DiskSpace"))
                {
                    DiskSpace ds = (DiskSpace) method.invoke(o);
                    if (ds != null)
                    {
                        if (field_idx > 0)
                        {
                            upd_cmd += ",";
                        }
                        field_idx++;

                        upd_cmd += "ds_id='" + ds.getId() + "'";
                    }
                }
                else if (ret_type.contains(".Mandant"))
                {
                    Mandant m = (Mandant) method.invoke(o);
                    if (m != null)
                    {
                        if (field_idx > 0)
                        {
                            upd_cmd += ",";
                        }
                        field_idx++;

                        upd_cmd += "mid='" + m.getId() + "'";
                    }
                }
                else if (!ret_type.contains(".hibernate.") && !ret_type.contains("java.util.Set") )
                {
                    Object unknown = method.invoke(o);
                    if (unknown != null)
                        throw new Exception( "Invalid return type for Method " + meth_name );
                }
            }
            String upd_stmt = "update " + rec_name + " set " + upd_cmd + " where id='" + id + "'";

            int rows = executeUpdate(sta, upd_stmt);

            return (rows == 1) ? true : false;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            last_ex = ex;
        }
        return false;
    }

    public boolean Delete( StatementID sta, Object o )
    {
        boolean ret = false;

        try
        {
            String rec_name = get_name_from_hibernate_class( o );
            Method getId = o.getClass().getDeclaredMethod("getId");
            Object r = getId.invoke(o);
            int id = ((Integer) r).intValue();
            if (id == 0)
            {
                return false;
            }

            String del_stmt = "delete from " + rec_name + " where id='" + id + "'";

            int rows = executeUpdate(sta, del_stmt);

            return (rows == 1) ? true : false;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            last_ex = ex;
        }
        return false;
    }

}
