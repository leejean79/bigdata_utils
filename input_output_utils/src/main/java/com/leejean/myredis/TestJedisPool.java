package com.leejean.myredis;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class TestJedisPool {
    public static void main(String[] args) {
        JedisPool jedisPool = MyRedisPool.getJedisPoolInstance();
        Jedis jedis = null;
        try{
            jedis = jedisPool.getResource();
            jedis.set("leejean", "41");
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            MyRedisPool.release(jedisPool, jedis);
        }
    }

}
