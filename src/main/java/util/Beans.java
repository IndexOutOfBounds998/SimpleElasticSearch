package util;

import accessor.AccessorClientImpl;
import accessor.IAccessor;
import common.Init;

public class Beans {
    static { new Init(); }//调用初始化类

    private static IAccessor accessor = new AccessorClientImpl();

    public static IAccessor getAccessor(){
        return accessor;
    }
}
