package clientManager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import accessor.AccessorClientImpl;
import accessor.IAccessor;
import org.apache.log4j.Logger;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

/**
 * Created by yang on 2017/7/11.
 */
public class ClientFactoryBuilder
{
    private ClientFactoryBuilder()
    {
    }
    
    // 地址
    private static List<String> HOSTS;
    
    // elasticsearch集群名称
    private static String CLUSTER_NAME;
    
    // elasticsearch 端口
    private static int CLIENT_PORT;
    
    private static Logger LOG = Logger.getLogger(String.valueOf(ClientFactoryBuilder.class));
    
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
    
    public static TransportClient client;
    
    public static TransportClient getClient()
    {
        
        if (client == null)
        {
            Settings settings = null;
            if (CLUSTER_NAME != null)
            {
                settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
            }
            
            if (HOSTS == null || HOSTS.size() <= 0)
            {
                throw new IllegalArgumentException("一些链接es客户端的必要参数需要构建进去 检查你传入的参数!");
            }
            try
            {
                
                for (String host : HOSTS)
                {
                    
                    LOG.info("发现节点：" + host + "...........正在连接该节点>>>>>>>>>");
                    client = new PreBuiltTransportClient(settings == null ? Settings.EMPTY : settings)
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), CLIENT_PORT));
                }
            }
            catch (UnknownHostException e)
            {
                LOG.info("连接es客户端发生错误" + e.toString());
                e.printStackTrace();
            }
        }
        return client;
    }
    
    // 构造 节点数据
    public static class builder
    {
        public builder setHOSTS(List<String> HOSTS)
        {
            ClientFactoryBuilder.HOSTS = HOSTS;
            return this;
            
        }
        
        public builder setCLUSTER_NAME(String CLUSTER_NAME)
        
        {
            ClientFactoryBuilder.CLUSTER_NAME = CLUSTER_NAME;
            return this;
        }
        
        public builder setCLIENT_PORT(int CLIENT_PORT)
        {
            ClientFactoryBuilder.CLIENT_PORT = CLIENT_PORT;
            return this;
        }
        
        public builder setINIT(boolean init)
        {
            ClientFactoryBuilder.INIT = init;
            return this;
        }
        
        public builder setCheck(boolean check)
        {
            ClientFactoryBuilder.IS_CHECK = check;
            return this;
        }
        
        public IAccessor create()
        {
            IAccessor accessor = new AccessorClientImpl();
            return accessor;
        }
        
    }
}
