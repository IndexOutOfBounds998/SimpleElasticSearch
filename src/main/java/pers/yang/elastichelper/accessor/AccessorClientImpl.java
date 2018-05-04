package pers.yang.elastichelper.accessor;

import java.util.*;
import java.util.logging.Logger;

import pers.yang.elastichelper.builder.QueryBuilderCondition;
import pers.yang.elastichelper.common.Result;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import pers.yang.elastichelper.builder.BooleanCondtionBuilder;
import pers.yang.elastichelper.clientmanager.ClientFactoryBuilder;
import pers.yang.elastichelper.util.SearchUtil;

/**
 * IAccessor的基于TransportClient API（es java客户端）的实现类
 */
public class AccessorClientImpl implements IAccessor {

    private static Logger LOG = Logger.getLogger(String.valueOf(AccessorClientImpl.class));

    private static TransportClient client;

    public AccessorClientImpl() {
        try {
            client = ClientFactoryBuilder.getClient();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AccessorClientImpl(TransportClient cv) {

        client = cv;
    }

    /* 添加单个对象 */
    @Override
    public <T> boolean add(T model) {
        String indexName = SearchUtil.getIndexName(model);
        String typeName = SearchUtil.getTypeName(model);
        String json = SearchUtil.ModelToJson(model);
        String id = SearchUtil.getidValue(model).toString();
        IndexResponse response;
        // 如果id为null,则不设置，es会自动生成
        if (id == null) {
            response = client.prepareIndex(indexName, typeName).setSource(json).execute().actionGet();
        } else {
            response = client.prepareIndex(indexName, typeName, id).setSource(json).execute().actionGet();
        }
        boolean bool = Integer.parseInt(response.getId()) >= 0;
        LOG.info("添加对象" + model + (bool ? "成功！" : "失败"));
        return bool;
    }

    /* 批量添加对象 */
    @Override
    public <T> boolean add(List<T> models) {
        boolean bool = false;
        if (models.size() == 1) {
            return this.add(models.get(0));
        } else {
            String indexName = SearchUtil.getIndexName(models.get(0));
            String typeName = SearchUtil.getTypeName((models.get(0)));
            BulkRequestBuilder bulkRequest = client.prepareBulk();
            // either use client#prepare, or use Requests# to directly build index/delete requests
            for (T model : models) {
                try {
                    String json = SearchUtil.ModelToJson(model);
                    String id = SearchUtil.getidValue(model).toString();
                    // 如果id为null,则不设置，es会自动生成
                    if (id == null) {
                        bulkRequest.add(client.prepareIndex(indexName, typeName).setSource(json));
                    } else {
                        bulkRequest.add(client.prepareIndex(indexName, typeName, id).setSource(json));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }
            }
            BulkResponse bulkResponse = bulkRequest.execute().actionGet();
            if (!bulkResponse.hasFailures()) {
                // process failures by iterating through each bulk response item
                bool = true;
            } else {
                LOG.info("buildFailureMessage:" + bulkResponse.buildFailureMessage());
            }
        }
        LOG.info("添加对象列表个数为：" + models.size() + (bool ? " 成功！" : " 失败"));
        return bool;
    }

    /* 根据ID获得记录 */
    @Override
    public <T> T get(Class<T> clazz, String id) {
        T result = null;
        try {
            String indexName = SearchUtil.getIndexName(clazz);
            String typeName = SearchUtil.getTypeName((clazz));
            GetResponse response = client.prepareGet(indexName, typeName, id).execute().actionGet();
            Map<String, Object> map = response.getSource();
            if (map != null) {
                // map.put("id",id);//or map.put("id",response.getId());
                map.put(SearchUtil.getidName(clazz), id);
            }
            result = SearchUtil.MapToModel(map, clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        LOG.info("根据id:" + id + ",获取对象：" + result);
        return result;
    }

    /* 根据ID删除记录 */
    @Override
    public boolean delete(String id, Class clazz) {
        DeleteResponse response = client
                .prepareDelete(SearchUtil.getIndexName(clazz), SearchUtil.getTypeName(clazz), id).execute().actionGet();
        boolean bool = !response.getType().equals("");
        LOG.info("根据id:" + id + ",删除对象" + (bool ? "成功！" : "失败,请检查对象是否存在"));
        return bool;
    }


    @Override
    public long count(Class clazz, String params) {
        return 0;
    }

    @Override
    public boolean delete(Class clazz, String params) {
        return false;
    }


    /**
     * // BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery(); // QueryStringQueryBuilder queryFilterBuilder =
     * new QueryStringQueryBuilder("白色"); // queryFilterBuilder.defaultField("productName.pinyin"); //
     * boolQueryBuilder.must(queryFilterBuilder); // boolQueryBuilder.must(QueryBuilders.termQuery("userId", "1")); //
     * boolQueryBuilder.must(QueryBuilders.termQuery("brandId", "1"));
     *
     * @param clazz
     * @param params
     * @param <T>
     * @return
     */
    @Override
    public <T> Result searchFun(Class<T> clazz, BooleanCondtionBuilder params) {
        String indexName = SearchUtil.getIndexName(clazz);
        String typeName = SearchUtil.getTypeName((clazz));
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName);
        searchRequestBuilder.setTypes(typeName);
        // 设置是否按查询匹配度排序
        searchRequestBuilder.setExplain(true);
        // 设置查询类型
        searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        // 设置query
        if (params != null) {
            searchRequestBuilder.setQuery(params.getBoolQueryBuilder());
        }
        if (params.getRow() > 0) {
            // 设置开始位置及大小
            searchRequestBuilder.setFrom(params.getStart()).setSize(params.getRow());
        }
        // 排序字段
        if (params.getSortMap() != null && params.getSortMap().size() > 0) {
            Iterator it = params.getSortMap().entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<String, SortOrder> entity = (Map.Entry) it.next();
                searchRequestBuilder.addSort(entity.getKey(), entity.getValue());
            }
        }
        // 执行查询操作
        SearchResponse response = searchRequestBuilder.execute().actionGet();
        // 处理查询结果
        SearchHits hits = response.getHits();

        // 转换 model
        List<T> list = new ArrayList<T>();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        for (SearchHit hit : hits.getHits()) {
            Map<String, Object> map = hit.getSourceAsMap();
            // 将指定显示的字段放入map
            for (String field : map.keySet()) {
                String value = map.get(field).toString();
                resultMap.put(field, value);
            }
            // OR for(String field : showFields){resultMap.put(field, hit.field(field).getValue().toString());}
            // resultMap.put("id", hit.getId());
            resultMap.put(SearchUtil.getidName(clazz), hit.getId());
            T model = null;
            if (resultMap != null) {
                model = SearchUtil.MapToModel(resultMap, clazz);
            }
            list.add(model);
        }

        // 返回结果集
        Result result = new Result();
        result.setSearchHits(hits);
        result.setList(list);
        return result;
    }

    @Override
    public <T> Result searchFun(Class<T> clazz, QueryBuilderCondition params) {
        String indexName = SearchUtil.getIndexName(clazz);
        String typeName = SearchUtil.getTypeName((clazz));
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName);
        searchRequestBuilder.setTypes(typeName);
        // 设置是否按查询匹配度排序
        searchRequestBuilder.setExplain(true);
        // 设置查询类型
        searchRequestBuilder.setSearchType(SearchType.DFS_QUERY_THEN_FETCH);
        // 设置query
        if (params != null) {
            searchRequestBuilder.setQuery(params.getBoolQueryBuilder());
        }
        if (params.getRow() > 0) {
            // 设置开始位置及大小
            searchRequestBuilder.setFrom(params.getStart()).setSize(params.getRow());
        }
        // 排序字段
        if (params.getSortMap() != null && params.getSortMap().size() > 0) {
            Iterator it = params.getSortMap().entrySet().iterator();

            while (it.hasNext()) {
                Map.Entry<String, SortOrder> entity = (Map.Entry) it.next();
                searchRequestBuilder.addSort(entity.getKey(), entity.getValue());
            }
        }
        // 设置高亮显示
        if (params.isShowHighLight()) {
            HighlightBuilder highlightBuilder = null;

            for (String field : params.getHighLightFileds()) {
                highlightBuilder = new HighlightBuilder().field(field);
                highlightBuilder.preTags("<span style=\"color:red\">");
                highlightBuilder.postTags("</span>");
            }
            searchRequestBuilder.highlighter(highlightBuilder);
        }
        if (!Objects.isNull(params.getMinScore())) {
            searchRequestBuilder.setMinScore(params.getMinScore());
        }

        // 执行查询操作
        SearchResponse response = searchRequestBuilder.execute().actionGet();
        // 处理查询结果
        SearchHits hits = response.getHits();

        // 转换 model
        List<T> list = new ArrayList<T>();
        Map<String, Object> resultMap = new HashMap<String, Object>();
        for (SearchHit hit : hits.getHits()) {
            Map<String, Object> map = hit.getSourceAsMap();
            // 将指定显示的字段放入map
            for (String field : map.keySet()) {
                String value = map.get(field).toString();
                resultMap.put(field, value);
            }
            // OR for(String field : showFields){resultMap.put(field, hit.field(field).getValue().toString());}
            // resultMap.put("id", hit.getId());
            resultMap.put(SearchUtil.getidName(clazz), hit.getId());
            T model = null;
            if (resultMap != null) {
                model = SearchUtil.MapToModel(resultMap, clazz);
            }
            list.add(model);
        }

        // 返回结果集
        Result result = new Result();
        result.setSearchHits(hits);
        result.setList(list);
        return result;
    }


    @Override
    public <T> Collection<Terms.Bucket> groupByAggs(Class<T> clazz, String filed, BooleanCondtionBuilder query) {
        String indexName = SearchUtil.getIndexName(clazz);
        String typeName = SearchUtil.getTypeName((clazz));
        SearchRequestBuilder searchRequestBuilder = client.prepareSearch(indexName).setTypes(typeName);
        if (query != null && query.getBoolQueryBuilder() != null) {
            searchRequestBuilder.setQuery(query.getBoolQueryBuilder());
        }
        if (query != null && query.getStart() > 0) {
            searchRequestBuilder.setFrom(query.getStart());
        }
        if (query != null && query.getStart() > 0) {
            searchRequestBuilder.setSize(query.getRow());
        }
        SearchResponse searchResponse = searchRequestBuilder
                .addAggregation(AggregationBuilders.terms("by_" + filed).field(filed)).execute().actionGet();

        Terms agg = searchResponse.getAggregations().get("by_" + filed);

        for (Terms.Bucket entry : agg.getBuckets()) {
            String key = (String) entry.getKey(); // bucket key
            long docCount = entry.getDocCount(); // Doc count

            System.out.println(key);
            System.out.println(docCount);

        }
        return null;
    }


}
