package com.xs.configurer.swxchat.mp.handler;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutNewsMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 \* 杭州桃子网络科技股份有限公司
 \* User: zhaoxin
 \* Date: 2018/6/26
 \* Time: 14:05
 \* Description: 
 \*/
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Scope("singleton")
public class SubscribeHandler extends AbstractHandler {

//    @Autowired
//    UserService userService;
//    @Autowired
//    MemberService memberService;
    @Autowired
    JedisPool jedisPool;

    public static final Integer virtuNum = 7561;//虚拟数量

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> map, WxMpService weixinService, WxSessionManager wxSessionManager) throws WxErrorException {
        this.logger.info("新关注用户 OPENID: " + wxMessage.getFromUser());

        // 获取微信用户基本信息
        WxMpUser userWxInfo = null;
        Integer userId = null;
        try {
            userWxInfo = weixinService.getUserService().userInfo(wxMessage.getFromUser(), null);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }

        if (userWxInfo != null) {
            // TODO 可以添加关注用户到本地
//            userId = userService.saveUser("weixinUnionid", null, userWxInfo);
        }

        WxMpXmlOutMessage responseResult = null;
        try {
            responseResult = handleSpecial(wxMessage);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }

        if (responseResult != null) {
            return responseResult;
        }

//        Member member = memberService.findBy("userId", userId);
//        String qrCodeUrl = "";
//        try(Jedis jedis = jedisPool.getResource()) {
//            String tmpQrCode = jedis.get(String.format(USER_QRCODE, userId));
//            if(tmpQrCode != null && tmpQrCode.length() > 0) {
//                qrCodeUrl = tmpQrCode;
//            } else {
////                qrCodeUrl = userService.generateQrCodeUrl(String.valueOf(userId), "");
//                qrCodeUrl = userService.generateQrCodeUrl(String.valueOf(userId));
//                jedis.set(String.format(USER_QRCODE, userId), qrCodeUrl);
//            }
//        }
//        String levelName = "";
//        String msg1 = "";
//        String msg2 = "";
//        if(member != null) {
//            int level = member.getLevel().intValue();
//            switch (level) {
//                case 8 :
//                    levelName = "店主";
//                    break;
//                case 4 :
//                    levelName = "超级店主";
//                    break;
//                case 1 :
//                    levelName = "代理商";
//                    break;
//            }
//            msg1 = "恭喜！您已成为水之肤" + levelName + "！现在可以分享自己的店铺啦！";
//            msg2 = "恭喜！您获得10.00元佣金！快去分销中心看看吧！";
//        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        qrCodeUrl = qrCodeUrl.substring(0, qrCodeUrl.indexOf("?"));
//        logger.info("qrCodeUrl: " + qrCodeUrl);
        String msg0 = "恭喜！您已成为水之肤第"+ (virtuNum + userId) +"位会员！" +
                "\r" +
                "时间：" + sdf.format(new Date()) +
                "\r" +
                "点击下方商城首页即可购物哦！快去看看吧！";
        String title = "注册提醒";

        try {
            WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
//            item.setDescription(msg1 + "\r" + msg2);
            item.setDescription(msg0);
//            item.setPicUrl(""+ qrCodeUrl +"");
//            item.setTitle(msg0);
            item.setTitle(title);
//            item.setUrl(qrCodeUrl);
            return WxMpXmlOutMessage.NEWS().fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser()).addArticle(item).build();
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }
        return null;
    }


    /**
     * 处理特殊请求，比如如果是扫码进来的，可以做相应处理
     */
    private WxMpXmlOutMessage handleSpecial(WxMpXmlMessage wxMessage)
            throws Exception {
        //TODO
        return null;
    }

}
