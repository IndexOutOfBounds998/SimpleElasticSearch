package pers.yang.elastichelper.clientmanager;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;

import org.elasticsearch.common.Strings;
import pers.yang.elastichelper.accessor.AccessorClientImpl;
import pers.yang.elastichelper.accessor.IAccessor;
import org.apache.log4j.Logger;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import pers.yang.elastichelper.common.Constant;
import pers.yang.elastichelper.util.PropKit;

/**
 * Created by yang on 2017/7/11.
 */
public class ClientFactoryBuilder {
    private ClientFactoryBuilder() {
    }

    //初始化配置文件
    private static boolean initConfig = false;
    //配置文件地址
    private static String configPath;
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
     */
    public static TransportClient getClient() {

        if (client == null) {
            if (Constant.HOSTS != null && Constant.HOSTS.size() > 0 && Constant.CLIENT_PORT != null) {
                return clientByConfig();
            }
            Settings settings = null;
            if (CLUSTER_NAME != null) {
                settings = Settings.builder().put("cluster.name", CLUSTER_NAME).build();
            }

            if (HOSTS == null || HOSTS.size() <= 0) {
                throw new IllegalArgumentException("HOSTS cant be null !");
            }
            try {

                for (String host : HOSTS) {

                    LOG.info("发现节点：" + host + "...........正在连接该节点>>>>>>>>>");
                    client = new PreBuiltTransportClient(settings == null ? Settings.EMPTY : settings)
                            .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), CLIENT_PORT));
                }
            } catch (UnknownHostException e) {
                LOG.info("连接es客户端发生错误" + e.toString());
                e.printStackTrace();
            }
        }
        return client;
    }

    public static TransportClient clientByConfig() {

        Settings settings = Settings.builder().put("cluster.name", Constant.CLUSTER_NAME).build();
        try {

            for (String host : Constant.HOSTS) {

                LOG.info("发现节点：" + host + "...........正在连接该节点>>>>>>>>>");
                client = new PreBuiltTransportClient(settings == null ? Settings.EMPTY : settings)
                        .addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(host), Constant.CLIENT_PORT));
            }
        } catch (UnknownHostException e) {
            LOG.info("连接es客户端发生错误" + e.toString());
            e.printStackTrace();
        }
        return client;

    }

    /**
     * @Author cyy
     * @Date 2018/5/7 11:03
     * @Description 初始化配置文件
     */
    public static void initConfg(String path) {
        PropKit.use(path);
        Constant.CLIENT_PORT = PropKit.getInt("client.port");
        Constant.CLUSTER_NAME = PropKit.get("cluster.name");
        String hosts = PropKit.get("hosts");
        if (!Strings.isNullOrEmpty(hosts)) {
            Constant.HOSTS = Arrays.asList(hosts.split(","));
        }
    }


    // 构造 节点数据
    public static class builder {
        public builder initConfig(boolean b) {
            ClientFactoryBuilder.initConfig = b;
            return this;
        }

        //设置配置文件地址
        public builder setConfigPath(String configPath) {
            ClientFactoryBuilder.configPath = configPath;
            return this;
        }


        public builder setHOSTS(List<String> HOSTS) {
            ClientFactoryBuilder.HOSTS = HOSTS;
            return this;

        }

        public builder setCLUSTER_NAME(String CLUSTER_NAME)

        {
            ClientFactoryBuilder.CLUSTER_NAME = CLUSTER_NAME;
            return this;
        }

        public builder setCLIENT_PORT(int CLIENT_PORT) {
            ClientFactoryBuilder.CLIENT_PORT = CLIENT_PORT;
            return this;
        }

        public IAccessor create() {

            IAccessor accessor = new AccessorClientImpl(ClientFactoryBuilder.getClient());
            return accessor;
        }

    }
}
