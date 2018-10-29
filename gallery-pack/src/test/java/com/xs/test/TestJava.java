package com.xs.test;

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
    }

}
