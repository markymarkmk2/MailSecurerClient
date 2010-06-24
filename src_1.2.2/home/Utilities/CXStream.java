/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dimm.home.Utilities;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.mapper.MapperWrapper;

/**
 *
 * @author mw
 */
public class CXStream extends XStream
{

    @Override
    protected MapperWrapper wrapMapper( MapperWrapper next )
    {
        return new MapperWrapper(next)
        {

            @Override
            public boolean shouldSerializeMember( Class definedIn, String fieldName )
            {
                if (definedIn == Object.class)
                {
                    return false;
                }
                return super.shouldSerializeMember(definedIn, fieldName);
            }
        };
    }
}
