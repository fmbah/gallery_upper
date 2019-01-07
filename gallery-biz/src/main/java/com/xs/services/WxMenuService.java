package com.xs.services;

/**
 * @Auther: Fmbah
 * @Date: 18-10-31 下午4:16
 * @Description:
 */
public interface WxMenuService {

    /**
     *
     * 功能描述:
     *
     * @param: 
     * @return: 
     * @auther: Fmbah
     * @date: 18-10-31 下午4:17
     */
    String menuCreate(String json);


    Object drawpic(String fontToPics, String pic);
}
