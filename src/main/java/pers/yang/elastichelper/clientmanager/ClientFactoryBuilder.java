package pers.yang.elastichelper.clientmanager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import pers.yang.elastichelper.accessor.AccessorClientImpl;
import pers.yang.elastichelper.accessor.IAccessor;
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

    public static TransportClient client;
    /**
     * @Author cyy
     * @Date 2018/5/4 17:39
     * @Description
     * @exception  抛出异常 hosts 未设置的错误
     */
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
                throw new IllegalArgumentException("HOSTS cant be null !");
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

        public IAccessor create()
        {
            IAccessor accessor = new AccessorClientImpl();
            return accessor;
        }
        
    }
}
