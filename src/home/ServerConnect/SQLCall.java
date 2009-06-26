/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

import dimm.home.hibernate.DiskArchive;
import dimm.home.hibernate.Hotfolder;
import dimm.home.hibernate.Mandant;
import dimm.home.httpd.MWWebServiceService;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;

/**
 *
 * @author mw
 */


public class SQLCall
{
    MWWebServiceService service;
    dimm.home.httpd.MWWebService port;
    String name;
    
    public SQLCall()
    {
        System.setProperty("javax.net.ssl.trustStore", "jxws.ts");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");

        service = new MWWebServiceService();
        port = service.getMWWebServicePort();
    }

    public boolean delete( Object o )
    {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            XMLEncoder enc = new XMLEncoder(bos);
            enc.writeObject(o);
            enc.close();

            //todo: errcode text 
        try
        {
            String ret = port.delete(bos.toString("UTF-8"));

            int idx = ret.indexOf(':');
            int retcode = Integer.parseInt(ret.substring(0, idx));
            if (retcode == 0)
                return true;
        }
        catch (UnsupportedEncodingException unsupportedEncodingException)
        {
        }
        return false;

    }

    public boolean call_qry(String qry, SQLResult<?> result)
    {
        ByteArrayInputStream bis = null;

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
                bis = new ByteArrayInputStream(ret.substring(idx + 2).getBytes("UTF-8"));
            else
                result.setErrText(ret.substring(idx + 2));

        }
        catch (Exception ex)
        {
            result.setEx(ex);
            return false;
        }
        long end = System.currentTimeMillis();
        result.setDuration(end - start);


        try
        {
            if (result.getErrCode() == 0)
            {
                XMLDecoder dec = new XMLDecoder(bis);
                List l = (List) dec.readObject();

                result.setResultList(l);
            }
        }
        catch (Exception e)
        {
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
        txt = txt.replaceAll("&ccedil;", "ç");
        txt = txt.replaceAll("&eacute;", "é");
        txt = txt.replaceAll("&egrave;", "è");
        txt = txt.replaceAll("&aacute;", "á");
        txt = txt.replaceAll("&agrave;", "à");
        txt = txt.replaceAll("&ugrave;", "ù");
        txt = txt.replaceAll("&Egrave;", "È");
        txt = txt.replaceAll("&Agrave;", "À");
        txt = txt.replaceAll("&acute;", "´");
        txt = txt.replaceAll("&szlig;", "ß");
        txt = txt.replaceAll("&euml;", "ë");
        txt = txt.replaceAll("&atilde;", "ã" );
        txt = txt.replaceAll("&aring;", "å" );


        return txt;
    }

    public void sql_call( String string )
    {
        throw new UnsupportedOperationException("Not yet implemented");
    }



}
