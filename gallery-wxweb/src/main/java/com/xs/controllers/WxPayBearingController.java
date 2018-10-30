package com.xs.controllers;

import com.xs.configurer.sannotation.IgnoreAuth;
import com.xs.core.scontroller.BaseController;
import com.xs.services.PayBearingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 *
 * 功能描述: 
 *
 * @param:
 * @return: 
 * @auther: Fmbah
 * @date: 18-10-22 下午3:15
 */
@Api(value = "WxPayBearingController",description = "支付接口调用承接轴承功能（衔接前台提交订单功能、后台支付回调功能）")
@RestController
@RequestMapping(value = "/api/wx/app/payBearing")
public class WxPayBearingController extends BaseController {

    @Autowired
    private PayBearingService payBearingService;

    /**
    * @Description: 订单支付接口
     *                 参照：https://pay.weixin.qq.com/wiki/doc/api/wxa/wxa_api.php?chapter=7_4&index=3
    * @Param: orderIds：订单id集合
    * @return: 微信端调起支付参数集合
    * @Author: zhaoxin
    * @Date: 2018/6/13
    **/
    @RequestMapping(value = "immediatePayment",method = RequestMethod.GET)
    @ApiOperation(value = "点击立即支付调用此接口",notes = "点击立即支付调用此接口,调此接口前,应该已经生成了订单支付")
    public Object immediatePayment(@ApiParam(name = "orderIds", value = "订单id集合，订单号用英文逗号隔开", required = true, type = "string") @RequestParam String orderIds,
                                   @ApiParam(name = "userId", value = "用户id", required = true, type = "string") @RequestParam String userId,
                                   @ApiParam(name = "isMiniApp", value = "是否小程序支付", required = true, type = "boolean") @RequestParam Boolean isMiniApp) {
        return payBearingService.immediatePayment(orderIds, userId, request, isMiniApp);
    }

    /**
     * @Description: 微信支付后，微信系统回调消息通知方法
     * @Param:
     * @return:
     * @Author: zhaoxin
     * @Date: 2018/6/14
     **/
    @IgnoreAuth
    @ResponseBody
    @RequestMapping(value = "payNotify",method = RequestMethod.POST)
    @ApiOperation(value = "支付回调重置订单状态",notes = "支付回调重置订单状态")
    public void payNotify() throws IOException {
        this.response.getWriter().write(payBearingService.payNotify(this.request));
    }
}
