package builder;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.util.Map;

/**
 * @Author: yang
 * @Date: 2018/3/3.9:29
 * @Desc: to do?
 */
public class QueryBuilderCondition
{
    
    // paixu
    private Map<String, SortOrder> sortMap;
    
    private BoolQueryBuilder boolQueryBuilder;
    
    private int start = 0;
    
    private int row;

    public Map<String, SortOrder> getSortMap() {
        return sortMap;
    }

    public BoolQueryBuilder getBoolQueryBuilder() {
        return boolQueryBuilder;
    }

    public int getStart() {
        return start;
    }

    public int getRow() {
        return row;
    }

    public static class builder
    {
        QueryBuilderCondition queryBuilderCondition = new QueryBuilderCondition();
        
        public builder setSortMap(Map<String, SortOrder> sortMap)
        {
            queryBuilderCondition.sortMap = sortMap;
            return this;
        }
        
        public builder setBoolQueryBuilder(BoolQueryBuilder boolQueryBuilder)
        {
            queryBuilderCondition.boolQueryBuilder = boolQueryBuilder;
            return this;
        }
        
        public builder setStart(int start)
        {
            queryBuilderCondition.start = start;
            return this;
        }
        
        public builder setRow(int row)
        {
            queryBuilderCondition.row = row;
            return this;
        }
        
        public QueryBuilderCondition builder()
        {
            return queryBuilderCondition;
        }
        
    }
}
