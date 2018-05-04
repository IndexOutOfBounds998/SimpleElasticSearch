package pers.yang.elastichelper.common;

import org.elasticsearch.search.SearchHits;

import java.util.List;

/**
 * @Author: yang
 * @Date: 2018/3/1.16:43
 * @Desc: to do?
 */
public class Result<T>
{
    
    private SearchHits searchHits;
    
    private List<T> list;
    
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
