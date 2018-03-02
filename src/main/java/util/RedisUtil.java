package util;

import java.util.List;

import org.apache.log4j.Logger;

import com.jfinal.plugin.redis.Cache;
import com.jfinal.plugin.redis.Redis;

import org.apache.logging.log4j.util.Strings;
import redis.clients.jedis.Jedis;

/**
 * redis操作类
 * 
 * @author huangjing
 *
 */
public class RedisUtil
{
    private static Logger logger = Logger.getLogger(RedisUtil.class);
    
    /**
     * 前缀
     */
    static String prefix = "appservice_";
    
    /**
     * 获取redis值
     * 
     * @param key 键
     * @return redis值
     */
    public static String get(String key)
    {
        if (Strings.isBlank(key))
            return null;
        Cache newcache = Redis.use();
        Jedis jedis = newcache.getJedis();
        String value = jedis.get(prefix + key);
        if (key.length() == 36)
        {
            logger.info("debug:key=" + key + ",ttl=" + jedis.ttl(prefix + key));
        }
        jedis.close();
        return value;
    }
    
    public static List lrange(String key, int start, int stop)
    {
        Cache newcache = Redis.use();
        Jedis jedis = newcache.getJedis();
        List value = jedis.lrange(prefix + key, start, stop);
        jedis.close();
        return value;
    }
    
    /**
     * 判断redis中key是否存在
     * 
     * @param key 键
     * @return 是否存在
     */
    public static Boolean exists(String key)
    {
        Cache newcache = Redis.use();
        Jedis jedis = newcache.getJedis();
        Boolean value = jedis.exists(prefix + key);
        jedis.close();
        return value;
    }
    
    /**
     * 设置redis缓存，并配置超时时间
     * 
     * @param key 键
     * @param value 值
     * @param timeOut 超时时间(秒)
     */
    public static void set(String key, String value, int timeOut)
    {
        Cache newcache = Redis.use();
        Jedis jedis = newcache.getJedis();
        jedis.set(prefix + key, value);
        newcache.expire(prefix + key, timeOut);
        jedis.close();
    }
    
    /**
     * 将一个值 value 插入到列表 key 的表尾(最右边)。
     * 
     * @param key
     * @param value
     */
    public static void rpush(String key, String value)
    {
        Cache newcache = Redis.use();
        Jedis jedis = newcache.getJedis();
        jedis.rpush(prefix + key, value);
        jedis.close();
    }
    
    /**
     * 移除并返回列表 key 的头元素
     * 
     * @param key
     * @return
     */
    public static String lpop(String key)
    {
        Cache newcache = Redis.use();
        Jedis jedis = newcache.getJedis();
        String value = jedis.lpop(prefix + key);
        jedis.close();
        return value;
    }
    
    /**
     * 删除缓存值
     * 
     * @param key 键
     */
    public static void delete(String key)
    {
        Cache newcache = Redis.use();
        Jedis jedis = newcache.getJedis();
        jedis.del(prefix + key);
        jedis.close();
    }
    
    /**
     * 获取key剩余的过期时间
     * 
     * @param key
     * @return
     */
    public static Long ttl(String key)
    {
        Cache newcache = Redis.use();
        Jedis jedis = newcache.getJedis();
        Long value = jedis.ttl(prefix + key);
        jedis.close();
        return value;
    }
}
