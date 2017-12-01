package model;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.util.List;

/**
 * @Author: yang 【youtulu.cn】
 * @Date: 2017/11/27.16:01
 */
public class Result
{
    
    private SearchHits searchHits;
    
    private List<ProductsEntity> list;
    
    public SearchHits getSearchHits()
    {
        return searchHits;
    }
    
    public void setSearchHits(SearchHits searchHits)
    {
        this.searchHits = searchHits;
    }
    
    public List getList()
    {
        return list;
    }
    
    public void setList(List list)
    {
        this.list = list;
    }
}
