package com.xs.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;
import com.google.gson.Gson;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Assert;
import org.junit.Test;

import java.awt.*;
import java.sql.Connection;

/**
 * @ClassName TestJava
 * @Description
 * @Author root
 * @Date 18-10-24 上午9:54
 * @Version 1.0
 **/
public class TestJava {

    @Test
    public void test() {
        Integer integer0 = new Integer(1);
        Integer integer1 = new Integer(1);
        Integer integer2 = Integer.valueOf(1);
        Integer integer3 = Integer.valueOf(1);

        System.out.println(integer0 == integer1);
        System.out.println(integer0 == integer2);
        System.out.println(integer2 == integer3);


        JSONObject jsonObject = JSON.parseObject("{'shareProfitName':'谁充值了的名称', 'recharge':'365', 'profit':'150'}");
        System.out.println(jsonObject);

        JSONObject jsonObject1 = JSON.parseObject("{}");
        jsonObject1.put("a", "av");
        System.out.println(jsonObject1);

    }

    @Test
    public void guavaTest() {
        long l = System.currentTimeMillis();
        BloomFilter<Integer> filter = BloomFilter.create(Funnels.integerFunnel(), 10000000, 0.01);
        for (int i = 0; i < 10000000; i++) {
            filter.put(i);
        }
        Assert.assertTrue(filter.mightContain(1));
        long s = System.currentTimeMillis();
        System.out.println("执行时间: " + (s - l));
    }

    @Test
    public void testFont() throws ClassNotFoundException {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        String[] fontFamilies = ge.getAvailableFontFamilyNames();
        for (String f : fontFamilies) {
            System.out.println(f);
        }


    }

}
