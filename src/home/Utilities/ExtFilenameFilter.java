/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dimm.home.Utilities;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 * @author mw
 */
public class ExtFilenameFilter implements FilenameFilter
{
    String extension;
    boolean recursive;

    public ExtFilenameFilter( String ext, boolean rec)
    {
        extension = ext;
        recursive = rec;
    }
    @Override
    public boolean accept( File dir, String name )
    {
        if (name.endsWith(extension))
            return true;

        if (recursive)
        {
            File fdir = new File( dir, name );
            if (fdir.isDirectory())
                return true;
        }


        return false;
    }
}