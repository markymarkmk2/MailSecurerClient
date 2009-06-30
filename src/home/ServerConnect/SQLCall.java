/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

import dimm.home.hibernate.DiskArchive;
import dimm.home.hibernate.Hotfolder;
import dimm.home.hibernate.Mandant;
import java.net.URL;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import com.thoughtworks.xstream.XStream;
import dimm.home.httpd.*;


/**
 *
 * @author mw
 */

public class SQLCall
{
    MWWebServiceService service;
    dimm.home.httpd.MWWebService port;
    String name;
    String server_url = "http://192.168.1.145:8050/1234";
    
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
            BindingProvider bp= (BindingProvider)port;
            bp.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, server_url );

            return true;
        }
        catch (Exception e)
        {
            // TODO
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete( Object o )
    {
        String xml = encode( o );

            //todo: errcode text 
        try
        {
            String ret = port.delete(xml);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));
            if (retcode == 0)
                return true;
        }
        catch (Exception unsupportedEncodingException)
        {
        }
        return false;

    }
    
    private String encode( Object o )
    {
        XStream xstream = new XStream();
        String xml = xstream.toXML(o);
        return xml;
    }

    public boolean save( Object o )
    {
        
        String xml = encode( o );


            //todo: errcode text
        try
        {
            String ret = port.update(xml);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));
            if (retcode == 0)
                return true;
        }
        catch (Exception unsupportedEncodingException)
        {
        }
        return false;

    }

    public boolean call_qry(String qry, SQLResult<?> result)
    {
        String xml = null;

        result.setQry(qry);
        
        long start = System.currentTimeMillis();

        String ret = null;
        try
        {
            ret = port.getQuery(qry);

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));
            result.setErrCode(retcode);
            if (retcode == 0)
                xml = ret.substring(idx + 2);
            else
                result.setErrText(ret.substring(idx + 2));

        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            result.setEx(ex);
            return false;
        }
        long end = System.currentTimeMillis();
        result.setDuration(end - start);


        try
        {
            if (result.getErrCode() == 0)
            {
                XStream xstream = new XStream();
                List l = (List)xstream.fromXML(xml);

                result.setResultList(l);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            result.setEx(e);
            return false;
        }
        
        return (result.getErrCode() == 0)? true : false;
    }

    public static void test()
    {
        SQLCall sc = new SQLCall();

        SQLResult<Mandant> srm = new SQLResult<Mandant>();
        SQLResult<DiskArchive> srd = new SQLResult<DiskArchive>();
        SQLResult<Hotfolder> srhf = new SQLResult<Hotfolder>();

        if (sc.call_qry("from Mandant", srm))
        {
            for (int i = 0; i < srm.size(); i++)
            {
                Mandant m = srm.get(i);
                Set<DiskArchive> ds = m.getDiskArchives();
                Set<Hotfolder> hfs = m.getHotfolders();

                if (sc.call_qry("from DiskArchive where mid=" + m.getId() , srd))
                {
                    for (int j = 0; j < srd.size(); j++)
                    {
                        DiskArchive diskArchive = srd.get(j);
                        ds.add(diskArchive);
                    }
                }
                if (sc.call_qry("from Hotfolder where mid=" + m.getId() , srhf))
                {
                    for (int j = 0; j < srd.size(); j++)
                    {
                        Hotfolder hf = srhf.get(j);
                        hfs.add(hf);
                    }
                }
            }
        }
    }

    public static String html_to_native( String txt )
    {
        if (txt.indexOf('&') == -1)
            return txt;

        txt = txt.replaceAll("&amp;", "&");
        txt = txt.replaceAll("&auml;", "ä");
        txt = txt.replaceAll("&ouml;", "ö");
        txt = txt.replaceAll("&uuml;", "ü");
        txt = txt.replaceAll("&Auml;", "Ä");
        txt = txt.replaceAll("&Ouml;", "Ö");
        txt = txt.replaceAll("&Uuml;", "Ü");
        txt = txt.replaceAll("&quot;", "\"");
        txt = txt.replaceAll("&lt;", "<");
        txt = txt.replaceAll("&gt;", ">");
        txt = txt.replaceAll("&ccedil;", "c");
        txt = txt.replaceAll("&eacute;", "é");
        txt = txt.replaceAll("&egrave;", "è");
        txt = txt.replaceAll("&aacute;", "á");
        txt = txt.replaceAll("&agrave;", "à");
        txt = txt.replaceAll("&ugrave;", "ù");
        txt = txt.replaceAll("&Egrave;", "È");
        txt = txt.replaceAll("&Agrave;", "À");
        txt = txt.replaceAll("&acute;", "á");
        txt = txt.replaceAll("&szlig;", "ß");
        txt = txt.replaceAll("&euml;", "e");
        txt = txt.replaceAll("&atilde;", "a" );
        txt = txt.replaceAll("&aring;", "a" );


        return txt;
    }

    public void sql_call( String string )
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }



}
