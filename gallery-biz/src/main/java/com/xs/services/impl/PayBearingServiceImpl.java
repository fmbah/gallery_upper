package com.xs.services.impl;

import com.github.binarywang.wxpay.bean.request.WxPayUnifiedOrderRequest;
import com.github.binarywang.wxpay.bean.result.WxPayUnifiedOrderResult;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.util.SignUtils;
import com.xs.beans.User;
import com.xs.beans.UserPayment;
import com.xs.core.ResultGenerator;
import com.xs.core.sexception.ServiceException;
import com.xs.core.sservice.SWxPayService;
import com.xs.services.PayBearingService;
import com.xs.services.UserPaymentService;
import com.xs.services.UserService;
import com.xs.utils.DigestUtil;
import com.xs.utils.IpUtils;
import org.apache.commons.collections.map.HashedMap;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Condition;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static com.xs.core.ProjectConstant.WEB_BACK_DOMAIN;

/**
 \* 杭州桃子网络科技股份有限公司
 \* User: zhaoxin
 \* Date: 2018/6/13
 \* Time: 19:23
 \* Description: 
 \*/
@Service
public class PayBearingServiceImpl implements PayBearingService {

    private Logger logger = LoggerFactory.getLogger(PayBearingServiceImpl.class);
    @Autowired
    UserService userService;
    @Autowired
    SWxPayService sWxPayService;
    @Autowired
    UserPaymentService userPaymentService;

    @Value("${wechat.miniapp.appid}")
    private String miniappid;
    @Value("${wechat.mp.appId}")
    private String mpappId;
    @Value("${wechat.pay.mchId}")
    private String mchId;
    @Value("${wechat.pay.mchKey}")
    private String mchKey;


    @Override
    public Object immediatePayment(String orderIds, String userId, HttpServletRequest resp, Boolean isMiniApp) {

        if(orderIds != null && orderIds.length() > 0 &&
                userId != null && userId.length() > 0) {
            User user = userService.findById(Integer.parseInt(userId));
            if(user != null) {
                List<UserPayment> orders = userPaymentService.findByIds(orderIds);
                if(orders != null && !orders.isEmpty()) {
                    WxPayUnifiedOrderRequest request = assembleParam(orders, user, resp, isMiniApp);
                    try {
                        WxPayUnifiedOrderResult wxPayUnifiedOrderResult = sWxPayService.unifiedOrder(request);
                        return ResultGenerator.genSuccessResult(againSign(wxPayUnifiedOrderResult.getPrepayId(), isMiniApp));
                    } catch (WxPayException e) {
                        logger.error(e.getMessage(), e);
                        throw new ServiceException("系统异常");
                    }
                }
                return ResultGenerator.genFailResult("订单数据不存在或已删除");
            }
            return ResultGenerator.genFailResult("用户数据不存在或已删除");
        }
        return ResultGenerator.genFailResult("参数错误");
    }

    @Override
    public String payNotify(HttpServletRequest request) {

        long wxBackTimes = System.currentTimeMillis();
        logger.info("微信支付回调开始....................................");

        String notifyMsg = analysisRequest(request);
        String result = "<xml><return_code>%s</return_code><return_msg>%s</return_msg></xml>";
        try {

            SortedMap<String, String> stringStringSortedMap = dom4jXMLParse(notifyMsg);

            boolean flag = isWechatSign(stringStringSortedMap);

            if(flag) {
                String out_trade_no = stringStringSortedMap.get("out_trade_no");
                String transaction_id = stringStringSortedMap.get("transaction_id");

                if(out_trade_no != null && out_trade_no.length() > 0 &&
                        transaction_id != null && transaction_id.length() > 0) {
                    String[] out_trade_nos = out_trade_no.split("_");
                    if(out_trade_nos != null && out_trade_nos.length > 0) {
                        Condition condition_o = new Condition(UserPayment.class);
                        Example.Criteria criteria_o = condition_o.createCriteria();
                        Iterable<String> orderNos = new ArrayList<>();
//                        for(String orderno : out_trade_nos) {
//                            ((ArrayList<String>) orderNos).add(orderno);
//                        }
                        ((ArrayList<String>) orderNos).add(out_trade_nos[1]);
                        criteria_o.andIn("orderNo", orderNos);
                        List<UserPayment> orders = userPaymentService.findByCondition(condition_o);
                        Date modifyDate = new Date();
                        if(orders != null && !orders.isEmpty() && orders.get(0).getStatus().equals("unpay")) {
                            for (UserPayment bean : orders) {
                                bean.setStatus("paid");
                                bean.setGmtModified(modifyDate);
                                bean.setGmtPayment(modifyDate);
                                bean.setTransactionId(transaction_id);

                            }

                            //支付回调订单完成,处理订单状态,处理获益分摊,处理相应用户余额,收入,分享收益数据

                            userPaymentService.sumOfMoney(orders);

                            return String.format(result, "SUCCESS", "OK");
                        } else if (orders != null && !orders.isEmpty() && orders.size() == out_trade_nos.length && orders.get(0).getStatus().equals("WAIT_SEND_GOODS")) {
                            return String.format(result, "SUCCESS", "OK");
                        }
                        return String.format(result, "FAIL", "NO ORDER DATAS");
                    }
                    return String.format(result, "FAIL", "ORDER NUM ERROR OR EMPTY");
                }
            }

            logger.warn("支付回调签名验证失败...........");

        } catch (DocumentException e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("微信通知内容dom4j转换sortedMap异常");
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServiceException(e.getMessage());
        } finally {
            logger.info("微信支付回调结束，共计耗时：{}ms....................................", System.currentTimeMillis() - wxBackTimes);
        }
        return String.format(result, "FAIL", "ERROR");
    }

    /**
    * @Description:  组装下单参数
    * @Param:
    * @return:
    * @Author: zhaoxin
    * @Date: 2018/6/14
    **/
    private WxPayUnifiedOrderRequest assembleParam(List<UserPayment> orders, User user, HttpServletRequest resp, Boolean isMiniApp) {
        WxPayUnifiedOrderRequest request = new WxPayUnifiedOrderRequest();
        StringBuffer stringBuffer = new StringBuffer();
        if (isMiniApp) {
            request.setAppid(this.miniappid);
            request.setOpenid(user.getWxMiniOpenid());
        } else {
            request.setAppid(this.mpappId);
            request.setOpenid(user.getWxOpenid());
        }
        request.setMchId(this.mchId);
        request.setNonceStr(DigestUtil.getRandomStringByLength(false, 32));
        String body = "火星图库";
        request.setBody(body);
        request.setSpbillCreateIp(IpUtils.getIpAddr(resp));
        request.setNotifyUrl(WEB_BACK_DOMAIN + "/api/wx/app/payBearing/payNotify");
        request.setTradeType("JSAPI");

        String outTradeNo = "";
        Integer totalFee = 0;
        for(UserPayment order : orders) {
            Condition condition = new Condition(UserPayment.class);
            Example.Criteria criteria = condition.createCriteria();
            criteria.andEqualTo("id", order.getId());
            List<UserPayment> orderItemList = userPaymentService.findByCondition(condition);
            if(orderItemList != null && !orderItemList.isEmpty()) {
                orderItemList.forEach(orderItem -> {
                    Byte rechargeType = orderItem.getRechargeType();
                    String rechargeTypeStr = "";
                    if (rechargeType.byteValue() == 5) {
                        rechargeTypeStr = "金卡会员";
                    } else if (rechargeType.byteValue() == 6) {
                        rechargeTypeStr = "一年会员";
                    } else if (rechargeType.byteValue() == 10) {
                        rechargeTypeStr = "钻石会员";
                    } else {
                        rechargeTypeStr = "未知";
                    }
                    stringBuffer.append("[").append(rechargeTypeStr).append("]");
                });
            } else {
                logger.warn("订单支付，当前订单号【{}】未查询到对应的商品，请检查数据是否正确", order.getId());
                continue;
            }
            body.concat(stringBuffer.toString());
            outTradeNo = outTradeNo + order.getOrderNo() + "_";
            totalFee = totalFee + order.getAmount().intValue();
            logger.info("*****************************实际支付金额{}************************",totalFee);
            //TODO 测试所以订单金额为1分钱，上线后还原为订单实际价格
//             totalFee = 1;
        }
        request.setOutTradeNo((isMiniApp ? "mini_" : "mp_") + outTradeNo.substring(0,outTradeNo.length()-1));
        request.setTotalFee(totalFee);
        String sign = SignUtils.createSign(request, null, this.mchKey, false);
        request.setSign(sign);
        return request;
    }

    /** 
    * @Description: 再签名操作 
    * @Param: 预支付订单id 
    * @return: 调起支付参数 
    * @Author: zhaoxin
    * @Date: 2018/6/14 
    **/ 
    private Object againSign(String preparId, Boolean isMiniApp) {
        Map<String, String> params = new HashedMap();
        String time = String.valueOf(System.currentTimeMillis() / 1000);
        String randomStr = DigestUtil.getRandomStringByLength(false, 32);
        String packageStr = "prepay_id=" + preparId;
        if (isMiniApp) {
            params.put("appId", this.miniappid);
        } else {
            params.put("appId", this.mpappId);
        }
        params.put("timeStamp", time);
        params.put("nonceStr", randomStr);
        params.put("package", packageStr);
        params.put("signType", "MD5");
        String againSignStr = SignUtils.createSign(params, null, this.mchKey, false);
        Map<String, String> json = new HashedMap();
        json.put("timeStamp", time);
        json.put("nonceStr", randomStr);
        json.put("package", packageStr);
        json.put("signType", "MD5");
        json.put("paySign", againSignStr);
        return json;
    }

    /**
    * @Description: 解析请求流为字符串
    * @Param: request：请求流
    * @return: 通知内容
    * @Author: zhaoxin
    * @Date: 2018/6/14
    **/
    private String analysisRequest(HttpServletRequest request) {
        ServletInputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        try {
            inputStream = request.getInputStream();
            outputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
            String result = new String(outputStream.toByteArray(), "utf-8");
            return result;
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new ServiceException("解析数据异常");
        } finally {
            try {
                if(inputStream != null) {
                    inputStream.close();
                }
                if(outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new ServiceException("关闭流异常");
            }
        }
    }

    /** 
    * @Description: 传入微信回调返回的XML信息
     *                以Map形式返回便于取值
     *                dom4j解析XML,返回第一级元素键值对。如果第一级元素有子节点，则此节点的值为空
    * @Param:  strXML：微信通知内容
    * @return:
    * @Author: zhaoxin
    * @Date: 2018/6/14 
    **/ 
    public SortedMap<String, String> dom4jXMLParse(String strXML) throws DocumentException {
        SortedMap<String, String> smap = new TreeMap<>();
        Document doc = DocumentHelper.parseText(strXML);
        Element root = doc.getRootElement();
        for (Iterator iterator = root.elementIterator(); iterator.hasNext();) {
            Element e = (Element) iterator.next();
            smap.put(e.getName(), e.getText());
        }
        return smap;
    }

    /**
     * 签名认证
     * @param smap
     * @param macKey 设置的密钥
     * @return 验证结果
     * @Author: zhaoxin
     * @Date: 2018/6/14
     */
    public boolean isWechatSign(SortedMap<String, String> smap) {
        String sign = SignUtils.createSign(smap, null, this.mchKey, false);
        String validSign = smap.get("sign").toUpperCase();
        return validSign.equals(sign);
    }
}
