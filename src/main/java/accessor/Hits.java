package accessor;

import org.elasticsearch.search.SearchHits;

import java.util.List;

/**
 * @Author: yang 【youtulu.cn】
 * @Date: 2017/11/23.10:51
 */
public interface Hits
{
    <T> void getHITS(SearchHits hits, List<T> list);
}
