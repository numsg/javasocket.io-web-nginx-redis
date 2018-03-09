package com.gsafety.pivs.notice.service.redis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisSentinelPool;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by gaoqiang on 2016/5/12.
 */
public class JedisUtils {

    private static JedisPool jedisPool;

    private static JedisSentinelPool jedisSentinelPool;

    private JedisUtils() {
    }

    /**
     * 从连接池获取redis连接 @return the jedis
     *
     * @return the jedis
     */
    public synchronized static Jedis getJedis(String redisHost, int redisPort) {
        if (jedisPool == null) {
            jedisPool = new JedisPool(redisHost, redisPort);
        }
        return jedisPool.getResource();
    }

    /**
     * 从哨兵获取redis连接 @return the jedis from sentinel
     *  sentinels.add("172.18.2.76:6379");
     * @return the jedis from sentinel
     */
    public synchronized static Jedis getJedisFromSentinel(String url) {
        if (jedisSentinelPool == null) {
            JedisPoolConfig poolConfig = JedisUtils.createPoolConfig(300, 1000, 300, 300);
            Set<String> sentinels = new HashSet<>();
            sentinels.add(url);
            jedisSentinelPool = new JedisSentinelPool("devmaster", sentinels, poolConfig, 5000);
        }
        return jedisSentinelPool.getResource();
    }

    /**
     * 快速设置JedisPoolConfig, 不执行idle checking。
     *
     * @param maxIdle  the max idle
     * @param maxTotal the max total
     * @return the jedis pool config
     */
    public static JedisPoolConfig createPoolConfig(int maxIdle, int maxTotal) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMaxTotal(maxTotal);
        poolConfig.setTimeBetweenEvictionRunsMillis(-1);
        return poolConfig;
    }

    /**
     * 快速设置JedisPoolConfig, 设置执行idle checking的间隔和可被清除的idle时间.
     * 默认的checkingIntervalSecs是30秒，可被清除时间是60秒。
     *
     * @param maxIdle               the max idle
     * @param maxTotal              the max total
     * @param checkingIntervalSecs  the checking interval secs
     * @param evictableIdleTimeSecs the evictable idle time secs
     * @return the jedis pool config
     */
    public static JedisPoolConfig createPoolConfig(int maxIdle, int maxTotal,
                                                   int checkingIntervalSecs,
                                                   int evictableIdleTimeSecs) {
        JedisPoolConfig poolConfig = createPoolConfig(maxIdle, maxTotal);

        poolConfig.setTimeBetweenEvictionRunsMillis(checkingIntervalSecs * 1000L);
        poolConfig.setMinEvictableIdleTimeMillis(evictableIdleTimeSecs * 1000L);
        return poolConfig;
    }
}
