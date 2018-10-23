package com.xs.services;

import javax.servlet.http.HttpServletRequest;

/**
 * @Auther: zx
 * @Date: 2018/6/13 19:22
 * @Description:支付接口，业务实现类
 */
public interface PayBearingService {


    /**
    * @Description: 订单支付接口
    * @Param: orderIds：订单id集合   userId：用户id
    * @return:  小程序端支付调用参数集合
    * @Author: zhaoxin
    * @Date: 2018/6/13
    **/
    Object immediatePayment(String orderIds, String userId, HttpServletRequest resp, Boolean isMiniApp);


    /**
    * @Description: 微信支付后通知接口
    * @Param: request：获取微信通知参数
    * @return: 返回结果
    * @Author: zhaoxin
    * @Date: 2018/6/14
    **/
    String payNotify(HttpServletRequest request);

}
