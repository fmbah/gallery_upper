package com.xs.test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.Test;

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

}
