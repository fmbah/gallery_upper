package com.xs.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Calendar;
import java.util.Date;

/**
 * 根据时间生成订单号
 *
 * @author tongyi
 */
public class GenerateOrderno {

    public static String get() {
        Date now = new Date(System.currentTimeMillis());

        Calendar ca = Calendar.getInstance();
        ca.setTime(now);
        int hour = ca.get(Calendar.HOUR_OF_DAY);
        int minute = ca.get(Calendar.MINUTE);
        int second = ca.get(Calendar.SECOND);

        String no = CalendarUtil.toString(now, "yyMMdd") + prefixZero(String.valueOf(hour * 3600 + minute * 60 + second)) + RandomStringUtils.randomNumeric(4);

        return no;
    }

    private static String prefixZero(String v) {
        String s = StringUtils.EMPTY;
        for (int i = 0; i < 5 - v.length(); i++) {
            s += "0";
        }
        return s + v;
    }

    public static void main(String[] args) {
        System.out.println("12345678".substring(0,7));
    }

}
