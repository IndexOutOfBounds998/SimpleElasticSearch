package util;

import accessor.AccessorClientImpl;
import accessor.IAccessor;

public class Beans
{
    
    private static IAccessor accessor = new AccessorClientImpl();
    
    public static IAccessor getAccessor()
    {
        return accessor;
    }
}
