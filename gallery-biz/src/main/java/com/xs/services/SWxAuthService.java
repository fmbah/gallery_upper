package com.xs.services;

import javax.servlet.http.HttpServletRequest;

/**
 *
 * 功能描述: 公众号接口
 *
 * @param:
 * @return:
 * @auther: Fmbah
 * @date: 18-10-22 下午7:55
 */
public interface SWxAuthService {

    /**
     * @Description: 微信公众号认证并授权用户访问权限
     * @Param:  request：请求流
     * @return: 由于是微信服务器发送请求调用平台接口，然后平台接口需要返回到页面上，不宜带很多数据，所以先把关键的带过去（openId，token）
     * @Author: zhaoxin
     * @Date: 2018/7/4
     **/
    Object distributionCenterAuth(HttpServletRequest request);

    /**
     *
     * 功能描述: 个人中心
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-22 下午8:49
     */
    Object getPersonal(Integer userId);

    /**
     *
     * 功能描述:  申请提现
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-23 下午2:31
     */
    Object askForCash(Integer userId);

}
