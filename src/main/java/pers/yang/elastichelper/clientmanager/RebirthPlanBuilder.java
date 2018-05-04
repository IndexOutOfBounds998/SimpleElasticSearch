package pers.yang.elastichelper.clientmanager;

/**
 * @Author: yang
 * @Date: 2018/5/4.17:42
 * @Desc:  用于 重新构建索引 mapping  setting 的构建类
 */
public class RebirthPlanBuilder {

    /**
     * 系统运行是否进行初始化（首先会清空elasticsearch中的一切内容然后根据实体类的注解自动生成相应的索引和type/mapping）
     * <p>
     * #建议不要开启（设置为false）
     */
    private static boolean INIT = false;

    /**
     * #每次运行前是否进行检测（检测相关的索引和type/mapping是否存在，不存在则根据实体类的注解自动生成）
     * <p>
     * #第一次运行可以设置为true,以后更改为false
     */
    private static boolean IS_CHECK = false;



    public class Builder{




    }
}
