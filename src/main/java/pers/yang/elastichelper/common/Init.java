package pers.yang.elastichelper.common;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Properties;
import pers.yang.elastichelper.accessor.IAccessor;
import com.google.common.base.Preconditions;
import pers.yang.elastichelper.util.Check;

/*初始化主类，执行相关初始化操作*/
public class Init
{
    public final static Init init = new Init();
    
    private Init()
    {
        
    }
    
    // 执行初始化检测
    public void excuteCheck(IAccessor accessor)
    {
        if (Constant.INIT)
        {
            new Check(accessor).rebirthPlan();
        }
        if (!Constant.INIT && Constant.IS_CHECK)
        {
            new Check(accessor).check();
        }
    }
    
    /*
     * 初始化相关常量参数
     * 
     * @see InputStream in = ClassLoader.getSystemResourceAsStream("elasticsearch.properties");
     */
    public void initConstants(InputStream in)
    {
        if (in == null)
        {
            throw new IllegalArgumentException("InputStream cant be null!");
        }
        Properties prop = new Properties();
        try
        {
            prop.load(in);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        Constant.INIT = Boolean.parseBoolean(prop.getProperty("init", "false"));
        Constant.IS_CHECK = Boolean.parseBoolean(prop.getProperty("isCheck", "true"));
        Constant.CLUSTER_NAME = prop.getProperty("cluster.name").trim();
        Constant.START = Integer.parseInt(prop.getProperty("start", "0"));
        Constant.ROWS = Integer.parseInt(prop.getProperty("rows", "10"));
        Constant.CLIENT_PORT = Integer.parseInt(prop.getProperty("client.port", "9300").trim());
        Constant.WEB_PORT = Integer.parseInt(prop.getProperty("web.port", "9200").trim());
        Constant.HOSTS = Arrays.asList(prop.getProperty("hosts", "localhost").trim().split(","));
        Constant.MODELS_PACKAGE = prop.getProperty("models.package.dir").trim();
        Constant.HIGHLIGHT_PRE_TAGS = prop.getProperty("highlight.pre.tags", "<span style=\"color:red\"").trim();
        Constant.HIGHLIGHT_POST_TAGS = prop.getProperty("highlight.post.tags", "</span>").trim();
        Constant.BASE_URL = "http://" + Constant.HOSTS.get(0) + ":" + Constant.WEB_PORT + "/";
        Preconditions.checkNotNull(Constant.MODELS_PACKAGE, "请设置实体类所在的包路径（models.package）！");
        
    }
}
