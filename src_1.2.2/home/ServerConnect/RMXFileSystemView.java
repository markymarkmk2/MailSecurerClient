/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

import com.thoughtworks.xstream.XStream;
import home.shared.SQL.RMXFile;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.UIManager;


public class RMXFileSystemView
{
    FunctionCallConnect fcc;
    
    public RMXFileSystemView(FunctionCallConnect fcc)
    {
        this.fcc = fcc;
    }

    
    static RMXFile toFile( String s )
    {
        XStream xs = new XStream();
        RMXFile f = (RMXFile)xs.fromXML( s );
        return f;
    }

    static RMXFile[] toFileArray( String s )
    {
        XStream xs = new XStream();
        RMXFile f[] = (RMXFile[])xs.fromXML( s );
        return f;
    }

    static String fromFile( RMXFile f )
    {
        XStream xs = new XStream();
        return xs.toXML(f);
    }
    private void check_answer( String ret ) throws IOException
    {
        if (ret == null || ret.length() == 0)
        {
            throw new IOException( "Communication error");
        }
        if (ret.charAt(0) != '0')
        {
            throw new IOException( "Application error " + ret);
        }
    }

    public RMXFile createNewFolder( RMXFile containingDir ) throws IOException
    {
        String ret = fcc.call_abstract_function("FSV CMD:createNewFolder FL:\"" + fromFile( containingDir ) + "\"", FunctionCallConnect.MEDIUM_TIMEOUT );

        if (ret == null || ret.charAt(0) != '0')
            return null;
        
        check_answer( ret );

        RMXFile f = toFile(ret.substring(3));
        return f;
    }

  
    public RMXFile createFileObject( String path )
    {
        String ret = fcc.call_abstract_function("FSV CMD:createFileObject PA:\"" + path + "\"", FunctionCallConnect.MEDIUM_TIMEOUT );

        try
        {
            check_answer(ret);

            RMXFile f = toFile(ret.substring(3));
            return f;
        }
        catch (IOException iOException)
        {
            return null;
        }
    }

   
    public RMXFile createFileObject( RMXFile dir, String filename )
    {
        return createFileObject(dir.getAbsolutePath() + "/" + filename);
    }

 

  

   
    public RMXFile getDefaultDirectory()
    {
        String ret = fcc.call_abstract_function("FSV CMD:getDefaultDirectory", FunctionCallConnect.MEDIUM_TIMEOUT );

        try
        {
            check_answer(ret);

            RMXFile f = toFile(ret.substring(3));
            return f;
        }
        catch (IOException iOException)
        {
            return null;
        }
    }


    public RMXFile[] getFiles( RMXFile dir, boolean useFileHiding )
    {
        String ret = fcc.call_abstract_function("FSV CMD:getFiles UF:" + (useFileHiding?"1":"0") + " FL:\"" + fromFile( dir ) + "\"", FunctionCallConnect.MEDIUM_TIMEOUT );

        try
        {
            check_answer(ret);

            RMXFile[] f = toFileArray(ret.substring(3));
            return f;
        }
        catch (IOException iOException)
        {
            return null;
        }
    }


    public RMXFile[] getRoots()
    {
        String ret = fcc.call_abstract_function("FSV CMD:getRoots", FunctionCallConnect.MEDIUM_TIMEOUT );

        try
        {
            check_answer(ret);

            RMXFile[] f = toFileArray(ret.substring(3));
            return f;
        }
        catch (IOException iOException)
        {
            return null;
        }
    }
    public RMXFile getRoot(RMXFile file)
    {
        String ret = fcc.call_abstract_function("FSV CMD:getRoot FL:\"" + fromFile( file ) + "\"", FunctionCallConnect.MEDIUM_TIMEOUT );

        try
        {
            check_answer(ret);

            RMXFile f = toFile(ret.substring(3));
            return f;
        }
        catch (IOException iOException)
        {
            return null;
        }
    }


 

    
    public RMXFile getParentDirectory( RMXFile dir )
    {
        String ret = fcc.call_abstract_function("FSV CMD:getParentDirectory FL:\"" + fromFile( dir ) + "\"", FunctionCallConnect.MEDIUM_TIMEOUT );

        try
        {
            check_answer(ret);

            RMXFile f = toFile(ret.substring(3));
            return f;
        }
        catch (IOException iOException)
        {
            return null;
        }
    }

   
    public boolean isFileSystem( RMXFile f )
    {
        String ret = fcc.call_abstract_function("FSV CMD:isFileSystem FL:\"" + fromFile( f ) + "\"", FunctionCallConnect.MEDIUM_TIMEOUT );

        try
        {
            check_answer(ret);

            return ret.charAt(3) == '1';
        }
        catch (IOException iOException)
        {
            return false;
        }
    }

   
    public boolean isFileSystemRoot( RMXFile dir )
    {
        String ret = fcc.call_abstract_function("FSV CMD:isFileSystemRoot FL:\"" + fromFile( dir ) + "\"", FunctionCallConnect.MEDIUM_TIMEOUT );

        try
        {
            check_answer(ret);

            return ret.charAt(3) == '1';
        }
        catch (IOException iOException)
        {
            return false;
        }
    }

  
    public boolean isParent( RMXFile folder, RMXFile file )
    {
        String ret = fcc.call_abstract_function("FSV CMD:isParent FO:\"" + fromFile( folder ) + "\" FL:\"" + fromFile( file ) + "\"", FunctionCallConnect.MEDIUM_TIMEOUT );

        try
        {
            check_answer(ret);

            return ret.charAt(3) == '1';
        }
        catch (IOException iOException)
        {
            return false;
        }
    }

   
    public boolean isRoot( RMXFile f )
    {
        String ret = fcc.call_abstract_function("FSV CMD:isRoot FL:\"" + fromFile( f ) + "\"", FunctionCallConnect.MEDIUM_TIMEOUT );

        try
        {
            check_answer(ret);

            return ret.charAt(3) == '1';
        }
        catch (IOException iOException)
        {
            return false;
        }
    }


    public Boolean isTraversable( RMXFile f )
    {
        String ret = fcc.call_abstract_function("FSV CMD:isTraversable FL:\"" + fromFile( f ) + "\"", FunctionCallConnect.MEDIUM_TIMEOUT );

        try
        {
            check_answer(ret);

            return ret.charAt(3) == '1';
        }
        catch (IOException iOException)
        {
            return false;
        }
    }

    public Icon getSystemIcon(RMXFile f)
    {
	return UIManager.getIcon(f.isDirectory() ? "FileView.directoryIcon" : "FileView.fileIcon");
    }


}