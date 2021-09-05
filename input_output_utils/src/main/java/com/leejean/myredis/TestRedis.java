package com.leejean.myredis;

import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Set;

public class TestRedis {

    public static void main(String[] args){

        Jedis jedis = new Jedis("hadoop001", 6379);

        jedis.sadd("sk1", "1","2","4");

        Set<String> sk1 = jedis.smembers("sk1");

        System.out.println(sk1);

    }
}

