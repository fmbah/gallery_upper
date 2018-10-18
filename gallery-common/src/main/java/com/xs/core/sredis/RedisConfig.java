package com.xs.core.sredis;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @Auther: zx
 * @Date: 2018/5/11 17:44
 * @Description:
 */
@Configuration
@EnableTransactionManagement
public class RedisConfig {

    @Value("${spring.redis.host}")
    private String redisHost;
    @Value("${spring.redis.port}")
    private Integer redisPort;
    @Value("${spring.redis.pass}")
    private String redisPass;
    @Value("${spring.redis.timeout}")
    private int timeout;
    @Value("${spring.redis.database}")
    private int database;
    @Value("${spring.redis.pool.max-idle}")
    private int maxIdle;
    @Value("${spring.redis.pool.max-wait}")
    private long maxWaitMillis;

    @Value("${spring.datasource.drivername}")
    private String drivername;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMaxWaitMillis(maxWaitMillis);
        JedisPool jedisPool;
        if (redisPass != null && !"none".equals(redisPass)) {
            jedisPool = new JedisPool(jedisPoolConfig, redisHost, redisPort, timeout, redisPass, database);
        } else {
            jedisPool = new JedisPool(jedisPoolConfig, redisHost, redisPort, timeout, null, database);
        }
        return jedisPool;
    }

}
