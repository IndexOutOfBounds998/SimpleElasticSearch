package pers.yang.elastichelper.accessor;

import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.common.settings.Settings;
import pers.yang.elastichelper.clientmanager.ClientFactoryBuilder;
import pers.yang.elastichelper.util.SearchUtil;

import java.util.logging.Logger;

/**
 * @Author: yang
 * @Date: 2018/5/4.18:12
 * @Desc: 索引相关的操作
 */
public class IndexAccessorImpl implements IndexAccessor {

    private static Logger LOG = Logger.getLogger(String.valueOf(IndexAccessorImpl.class));

    private static TransportClient client;

    public IndexAccessorImpl() {
        try {
            if (client == null) {
                client = ClientFactoryBuilder.getClient();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IndexAccessorImpl(TransportClient cv) {

        client = cv;
    }

    /* 使用系统默认配置创建索引 */
    @Override
    public boolean createIndex(String indexName) {
        boolean bool = client.admin().indices().prepareCreate(indexName).execute().actionGet().isAcknowledged();
        if (bool) {
            LOG.info("使用默认配置创建索引\"" + indexName + "\"成功！");
        } else {
            throw new RuntimeException("创建索引\"" + indexName + "\"失败！");
        }
        return bool;
    }

    /* 创建带有number_of_shards和number_of_replicas配置的索引 */
    @Override
    public boolean createIndex(Class clazz) {
        Settings indexSettings = Settings.builder()
                .put("number_of_shards", SearchUtil.getShards(clazz))
                .put("number_of_replicas", SearchUtil.getReplicas(clazz))
                .build();
        CreateIndexRequest indexRequest = new CreateIndexRequest(SearchUtil.getIndexName(clazz), indexSettings);
        boolean bool = client.admin().indices().create(indexRequest).actionGet().isAcknowledged();
        String indexName = SearchUtil.getIndexName(clazz);
        if (bool) {
            LOG.info("创建索引\"" + indexName + "\"成功");
        } else {
            // LOG.warn("创建索引:" + indexName + "失败！");
            throw new RuntimeException("创建索引\"" + indexName + "\"失败");
        }
        return bool;
    }

    /* 创建索引 */
    @Override
    public boolean createIndexWithSettings(Class clazz) {
        String indexName = SearchUtil.getIndexName(clazz);
        String settings = SearchUtil.getSettings(clazz);
        if (settings == null) {
            createIndex(clazz);
        } else {
            createIndexWithSettings(indexName, settings);
        }
        return false;
    }


    /* 创建索引 */
    @Override
    public boolean createIndexWithSettings(Class clazz, String json) {
        String indexName = SearchUtil.getIndexName(clazz);
        String settings = json;
        // 如果settings为null(settings.yml文件不存在)，则使用带
        // 有number_of_shards和number_of_replicas配置的创建索引
        if (settings == null) {
            createIndex(clazz);
        } else {
            createIndexWithSettings(indexName, settings);
        }
        return false;
    }

    private boolean createIndexWithSettings(String indexName, String settings) {
        LOG.info("settings内容：" + settings);
        boolean bool = false;
        try {
            bool = client.admin()
                    .indices()
                    .prepareCreate(indexName)
                    .setSettings(settings)
                    .execute()
                    .actionGet()
                    .isAcknowledged();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (bool) {
            LOG.info("使用settings创建索引\"" + indexName + "\"成功！");
        } else {
            // LOG.warn("创建索引:" + indexName + "失败！");
            throw new RuntimeException("创建索引\"" + indexName + "\"失败,可能的问题：您的settings配置不正确！");
        }
        return bool;
    }


    /* 删除索引 */
    @Override
    public boolean deleteIndex(String indexName) {
        return deleteIndexByName(indexName);
    }

    /**
     * @Author cyy
     * @Date 2018/4/12 16:38
     * @Description 通过index 删除索引
     */
    private boolean deleteIndexByName(String indexName) {
        try {
            boolean bool = client.admin().indices().prepareDelete(indexName).execute().actionGet().isAcknowledged();
            LOG.info("删除索引\"" + (bool ? indexName + "\"成功" : "失败"));
            return bool;
        } catch (Exception e) {
            LOG.warning("没有要删除的索引\"" + indexName + "\"");
            return false;
        }

    }

    @Override
    public boolean deleteIndex(Class clazz) {

        String indexName = SearchUtil.getIndexName(clazz);
        return deleteIndexByName(indexName);
    }

    @Override
    public boolean deleteType(String indexName, String typeName) {
        return false;
    }

    /* 判断索引是否存在 */
    @Override
    public boolean hasIndex(String indexName) {
        boolean indexExists = client.admin().indices().prepareExists(indexName).execute().actionGet().isExists();
        LOG.info("存在性检测====>索引\"" + indexName + (indexExists ? "\"存在" : "\"不存在"));
        return indexExists;
    }

    @Override
    public boolean createMapping(Class clazz) {
        try {
            String indexName = SearchUtil.getIndexName(clazz);
            String typeName = SearchUtil.getTypeName((clazz));
            PutMappingRequest mappingRequest =
                    Requests.putMappingRequest(indexName).type(typeName).source(SearchUtil.getMapping(clazz));
            client.admin().indices().putMapping(mappingRequest).actionGet();
            LOG.info("创建mapping\"" + typeName + "\"成功");
            return true;
        } catch (Exception e) {
            LOG.info("创建mapping 发生异常：异常信息" + e.getMessage());
            e.printStackTrace();
        }

        return false;

    }

    @Override
    public boolean hasMapping(Class clazz) {
        String indexName = SearchUtil.getIndexName(clazz);
        String typeName = SearchUtil.getTypeName((clazz));
        return hasMapping(indexName, typeName);
    }

    @Override
    public boolean hasMapping(String indexName, String typeName) {
        ClusterStateResponse clusterStateResponse = client.admin().cluster().prepareState().execute().actionGet();
        MappingMetaData mappingMetaData =
                clusterStateResponse.getState().getMetaData().index(indexName).getMappings().get(typeName);
        boolean mappingExists = mappingMetaData != null ? true : false;
        LOG.info("存在性检测====>" + (mappingExists ? "mapping \"" + indexName + "/" + typeName + "\"存在"
                : "mapping：" + indexName + "/" + typeName + "不存在！"));
        return mappingExists;
    }

    @Override
    public boolean deleteType(Class clazz) {
        return deleteType(SearchUtil.getIndexName(clazz), SearchUtil.getTypeName(clazz));
    }
}
