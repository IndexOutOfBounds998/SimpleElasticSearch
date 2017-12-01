package main;

import accessor.IAccessor;
import model.ProductsEntity;
import model.Result;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import util.BooleanCondtionBuilder;
import util.ClientFactoryBuilder;

import java.util.*;
import java.util.logging.Logger;

/**
 * Created by yang on 2017/7/12.
 */
public class DataInputEs
{
    
    public static void main(String[] args)
    {
        // new DataInputThread().start();
        // List list = new ArrayList<String>();
        // list.add("127.0.0.1");
        // IAccessor accessor = new ClientFactoryBuilder.builder().setCLUSTER_NAME("elasticsearch")
        // .setCLIENT_PORT(9300)
        // .setHOSTS(list)
        // .setINIT(true)
        // .create();
        
        // 构造链接属性
        List list = new ArrayList<String>();
        list.add("127.0.0.1");
        IAccessor accessor = new ClientFactoryBuilder.builder().setCLUSTER_NAME("elasticsearch")
            .setCLIENT_PORT(9300)
            .setHOSTS(list)
            .setINIT(false)
            .create();
        // must 相当于and
        Map<String, String> mustMap = new HashMap<>();
        // queryMap 存的是你要查的字段和值
        Map<String, String> queryMap = new HashMap<>();
        
        // String param = "rc";
        mustMap.put("isDelete", "0");
        mustMap.put("userId", "1");
        mustMap.put("systemType", "1");
        mustMap.put("isDelete", "0");
        mustMap.put("sellStatus", "1");
        // queryMap.put("brandName.pinyin", param);
        // queryMap.put("brandCnname.pinyin", param);
        // queryMap.put("productName.pinyin", param);
        // queryMap.put("classificationName.pinyin", param);
        Map<String, SortOrder> sortMap = new LinkedHashMap<>();
        sortMap.put("sort", SortOrder.DESC);
        sortMap.put("createTime", SortOrder.DESC);
        BooleanCondtionBuilder booleanCondtionBuilder = new BooleanCondtionBuilder.Builder().setMustMap(mustMap)
            .setQueryMap(queryMap)
            .setSortMap(sortMap)
            .setStart(0)
            .setRow(5)
            .builder();
        
        Result result = accessor.searchFun(ProductsEntity.class, booleanCondtionBuilder);
        System.out.println(result.getSearchHits().getTotalHits());
        for (SearchHit s : result.getSearchHits())
        {
            // System.out.println(s.getSource().get("brandName").toString());
            // System.out.println(s.getSource().get("classificationName").toString());
            System.out.println(s.getSource().get("createTime").toString());
            System.out.println(s.getSource().get("productName").toString());
            System.out.println(s.getSource().get("sort").toString());
            System.out.println("=====================");
        }
    }
    
}
