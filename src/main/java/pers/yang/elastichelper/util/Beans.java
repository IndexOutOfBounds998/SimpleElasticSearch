package pers.yang.elastichelper.util;

import pers.yang.elastichelper.accessor.AccessorClientImpl;
import pers.yang.elastichelper.accessor.IAccessor;

public class Beans
{
    
    private static IAccessor accessor = new AccessorClientImpl();
    
    public static IAccessor getAccessor()
    {
        return accessor;
    }
}
