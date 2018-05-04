package pers.yang.elastichelper.util;

import pers.yang.elastichelper.accessor.AccessorClientImpl;
import pers.yang.elastichelper.accessor.IAccessor;
import pers.yang.elastichelper.common.Constant;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

public class Check
{
    private static Logger LOG = Logger.getLogger(Check.class);
    
    private IAccessor accessor;
    
    public Check(IAccessor accessor)
    {
        this.accessor = accessor;
    }
    
    public Check()
    {
        accessor = new AccessorClientImpl();
    }
    
    /**
     * 重生计划，功能犹如“Rebirth”这两个字 清空ES中的一切(删除索引，意味着什么都没了)，然后扫描实体类表根据表的元数据描述重新创建索引和mapping
     */
    public void rebirthPlan()
    {
        LOG.info("=============================== 系统初始化 =====================================");
        Set<Class<?>> classes = ClassUtil.scanPackage(Constant.MODELS_PACKAGE);
        Set<String> indices = new HashSet<String>();
        for (Class<?> clazz : classes)
        {
            
            try
            {
                String indexName = SearchUtil.getIndexName(clazz);
                if (!SearchUtil.isDocument(clazz))
                {// 如果该类不是ES实体类则跳过
                    continue;
                }
                // 避免重复检测index是否存在
                if (!indices.contains(indexName))
                {
                    if (accessor.hasIndex(indexName))
                    {// 如果索引存在
                        accessor.deleteIndex(indexName);// 删除索引
                        accessor.createIndexWithSettings(clazz);// 创建索引
                        indices.add(indexName);
                    }
                    else
                    {
                        accessor.createIndexWithSettings(clazz);// 创建索引
                        indices.add(indexName);
                    }
                }
                LOG.info("=============================== 创建mapping =====================================");
                accessor.createMapping(clazz);// 创建mapping
            }
            catch (Exception e)
            {
                LOG.warn("=============================== 系统未检测到model" + clazz.getName()
                    + " 带有注解 =====================================");
                continue;
            }
        }
    }
    
    /***
     * 扫描实体类表根据表,然后创建相应的index和mapping （如果没有则新建）
     */
    public void check()
    {
        LOG.info("=============================== 系统检测 =====================================");
        Set<Class<?>> classes = ClassUtil.scanPackage(Constant.MODELS_PACKAGE);
        Set<String> indices = new HashSet<String>();// 加入set集合，用于避免重复检测index是否存在
        for (Class<?> clazz : classes)
        {
            String indexName = SearchUtil.getIndexName(clazz);
            if (indexName == null)
            {// 如果该类不是ES实体类则跳过
                continue;
            }
            if (!indices.contains(indexName))
            {// 如果该索引未进行检测
                if (!accessor.hasIndex(indexName))
                {// 如果索引不存在
                    accessor.createIndexWithSettings(clazz);// 创建索引
                    indices.add(indexName);
                }
                else
                {
                    indices.add(indexName);
                }
            }
            /* 如果该mapping初始化检测属性init设置为false则跳过 */
            if (!SearchUtil.getInitValue(clazz))
                continue;
            if (!accessor.hasMapping(clazz))
            {// 如果mapping不存在
                accessor.createMapping(clazz);// 创建mapping
            }
        }
    }
    
}
