/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.ServerConnect;

import com.thoughtworks.xstream.XStream;
import dimm.home.UserMain;
import java.io.File;
import java.io.IOException;
import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.filechooser.FileSystemView;
import javax.swing.filechooser.FileView;

/**
 *
 * @author mw
 */
class RMXFileView extends FileView
{
    protected Icon directoryIcon = null;
    protected Icon fileIcon = null;

    public RMXFileView()
    {
        directoryIcon    = UIManager.getIcon("FileView.directoryIcon");
	fileIcon         = UIManager.getIcon("FileView.fileIcon");
    }


    @Override
    public String getDescription( File f )
    {
        return f.getName();
    }

    @Override
    public String getName( File f )
    {
        String s =  f.getName();
        if (s.length() == 0)
            s = f.getAbsolutePath();
        return s;
    }

    @Override
    public Boolean isTraversable( File f )
    {
        return f.isDirectory();
    }

    @Override
    public String getTypeDescription( File f )
    {
        return f.toString();
    }

    @Override
    public Icon getIcon( File f )
    {
        return f.isDirectory() ? directoryIcon : fileIcon;
    }

}
public class RMXFileSystemView extends FileSystemView
{
    RMXFileView fileView;

    public RMXFileSystemView()
    {
        fileView = new RMXFileView();
    }

    public FileView getFileView()
    {
        return fileView;
    }

    static File toFile( String s )
    {
        XStream xs = new XStream();
        File f = (File)xs.fromXML( s );
        return f;
    }

    static File[] toFileArray( String s )
    {
        XStream xs = new XStream();
        File f[] = (File[])xs.fromXML( s );
        return f;
    }

    static String fromFile( File f )
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

    @Override
    public File createNewFolder( File containingDir ) throws IOException
    {
        FunctionCallConnect fcc = UserMain.fcc();
        String ret = fcc.call_abstract_function("FSV CMD:createNewFolder FL:\"" + fromFile( containingDir ) + "\"", FunctionCallConnect.MEDIUM_TIMEOUT );

        check_answer( ret );

        File f = toFile(ret.substring(3));
        return f;
    }

    @Override
    public File createFileObject( String path )
    {
        FunctionCallConnect fcc = UserMain.fcc();
        String ret = fcc.call_abstract_function("FSV CMD:createFileObject PA:\"" + path + "\"", FunctionCallConnect.MEDIUM_TIMEOUT );

        try
        {
            check_answer(ret);

            File f = toFile(ret.substring(3));
            return f;
        }
        catch (IOException iOException)
        {
            return null;
        }
    }

    @Override
    public File createFileObject( File dir, String filename )
    {
        return createFileObject(dir.getAbsolutePath() + "/" + filename);
    }

    @Override
    public File getChild( File parent, String fileName )
    {
        return super.getChild(parent, fileName);
    }

    @Override
    protected File createFileSystemRoot( File f )
    {
        return null;
    }

    @Override
    public File getDefaultDirectory()
    {
        FunctionCallConnect fcc = UserMain.fcc();
        String ret = fcc.call_abstract_function("FSV CMD:getDefaultDirectory", FunctionCallConnect.MEDIUM_TIMEOUT );

        try
        {
            check_answer(ret);

            File f = toFile(ret.substring(3));
            return f;
        }
        catch (IOException iOException)
        {
            return null;
        }
    }

    @Override
    public File[] getFiles( File dir, boolean useFileHiding )
    {
        FunctionCallConnect fcc = UserMain.fcc();
        String ret = fcc.call_abstract_function("FSV CMD:getFiles UF:" + (useFileHiding?"1":"0") + " FL:\"" + fromFile( dir ) + "\"", FunctionCallConnect.MEDIUM_TIMEOUT );

        try
        {
            check_answer(ret);

            File[] f = toFileArray(ret.substring(3));
            return f;
        }
        catch (IOException iOException)
        {
            return null;
        }
    }

    @Override
    public File[] getRoots()
    {
        FunctionCallConnect fcc = UserMain.fcc();
        String ret = fcc.call_abstract_function("FSV CMD:getRoots", FunctionCallConnect.MEDIUM_TIMEOUT );

        try
        {
            check_answer(ret);

            File[] f = toFileArray(ret.substring(3));
            return f;
        }
        catch (IOException iOException)
        {
            return null;
        }
    }

    @Override
    public String getSystemDisplayName( File f )
    {
        return f.getName();
        /*FunctionCallConnect fcc = UserMain.fcc();
        String ret = fcc.call_abstract_function("FSV CMD:getSystemDisplayName FL:\"" + fromFile( f ) + "\"", FunctionCallConnect.MEDIUM_TIMEOUT );

        try
        {
            check_answer(ret);

            return ret.substring(3);
        }
        catch (IOException iOException)
        {
            return null;
        }*/
    }

    @Override
    public File getParentDirectory( File dir )
    {
        FunctionCallConnect fcc = UserMain.fcc();
        String ret = fcc.call_abstract_function("FSV CMD:getParentDirectory FL:\"" + fromFile( dir ) + "\"", FunctionCallConnect.MEDIUM_TIMEOUT );

        try
        {
            check_answer(ret);

            File f = toFile(ret.substring(3));
            return f;
        }
        catch (IOException iOException)
        {
            return null;
        }
    }

    @Override
    public boolean isFileSystem( File f )
    {
        FunctionCallConnect fcc = UserMain.fcc();
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

    @Override
    public boolean isFileSystemRoot( File dir )
    {
        FunctionCallConnect fcc = UserMain.fcc();
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

    @Override
    public boolean isParent( File folder, File file )
    {
        FunctionCallConnect fcc = UserMain.fcc();
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

    @Override
    public boolean isRoot( File f )
    {
        FunctionCallConnect fcc = UserMain.fcc();
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

    @Override
    public Boolean isTraversable( File f )
    {
        FunctionCallConnect fcc = UserMain.fcc();
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
    @Override
    public Icon getSystemIcon(File f)
    {
	return UIManager.getIcon(f.isDirectory() ? "FileView.directoryIcon" : "FileView.fileIcon");
    }

    @Override
    public File getHomeDirectory()
    {
        return super.getHomeDirectory();
    }

    @Override
    public String getSystemTypeDescription( File f )
    {
        return super.getSystemTypeDescription(f);
    }

    @Override
    public boolean isComputerNode( File dir )
    {
        return super.isComputerNode(dir);
    }

    @Override
    public boolean isDrive( File dir )
    {
        return super.isDrive(dir);
    }

    @Override
    public boolean isFloppyDrive( File dir )
    {
        return super.isFloppyDrive(dir);
    }

    @Override
    public boolean isHiddenFile( File f )
    {
        return super.isHiddenFile(f);
    }


}