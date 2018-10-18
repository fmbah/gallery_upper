package com.xs.test;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Set;
import java.util.UUID;

/**
 * @ClassName ProjectTest
 * @Description
 * @Author root
 * @Date 18-10-17 上午11:05
 * @Version 1.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectTest {

    @Autowired
    private JedisPool jedisPool;

    @Test
    public void test() {

        System.out.println(DigestUtils.md5Hex("123456"));

        try (Jedis jedis = jedisPool.getResource()) {

            Set<String> keys = jedis.keys("*");
            System.out.println(keys.size());



        }


    }

}
