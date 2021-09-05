package com.leejean.myredis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class MyRedisPool {

    private static volatile JedisPool jedisPool = null;

    private MyRedisPool(){}

    public static JedisPool getJedisPoolInstance()
    {
        if (null == jedisPool)
        {
            synchronized (MyRedisPool.class)
                {
                    if (null == jedisPool) {
                        JedisPoolConfig poolConfig = new JedisPoolConfig();
                        poolConfig.setMaxActive(1000);
                        poolConfig.setMaxIdle(32);
                        poolConfig.setMaxWait(100 * 1000);
                        poolConfig.setTestOnBorrow(true);

                        jedisPool = new JedisPool(poolConfig, "hadoop001", 6379);
                    }
                }
        }
        return jedisPool;
    }

    public static void release(JedisPool jedisPool, Jedis jedis){
        if(null != jedis)
        {
            jedisPool.returnResourceObject(jedis);
        }
    }
}
