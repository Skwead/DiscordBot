package br.com.finalelite.bots.supporter.repository;

import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.Protocol;

public class RedisPool {

    public static final JedisPool DEFAULT = openJedisPool("127.0.0.1", 6397, "password??");

    private static JedisPool openJedisPool(String host, int port, String password) {
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);

        poolConfig.setTestOnCreate(true);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);

        return new JedisPool(poolConfig, host, port, Protocol.DEFAULT_TIMEOUT, password);
    }

}
