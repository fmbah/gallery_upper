package com.xs.configurer.swxchat.mp.handler;

import me.chanjar.weixin.common.api.WxConsts;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 \* 杭州桃子网络科技股份有限公司
 \* User: zhaoxin
 \* Date: 2018/6/26
 \* Time: 15:03
 \* Description: 
 \*/
@Component
@Scope("singleton")
public class MsgHandler extends AbstractHandler {

//    @Autowired
//    UserService userService;
//    @Autowired
//    MemberService memberService;

//    public static final Integer virtuNum = 7561;//虚拟数量

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService weixinService,
                                    WxSessionManager sessionManager) {

        if (!wxMessage.getMsgType().equals(WxConsts.XmlMsgType.EVENT)) {
            //TODO 可以选择将消息保存到本地
        }

        //当用户输入关键词如“你好”，“客服”等，并且有客服在线时，把消息转发给在线客服
        try {
            if (StringUtils.startsWithAny(wxMessage.getContent(), "你好", "客服")
                    && weixinService.getKefuService().kfOnlineList()
                    .getKfOnlineList().size() > 0) {
                return WxMpXmlOutMessage.TRANSFER_CUSTOMER_SERVICE()
                        .fromUser(wxMessage.getToUser())
                        .toUser(wxMessage.getFromUser()).build();
            }
        } catch (WxErrorException e) {
            e.printStackTrace();
        }


//        User user = userService.findBy("weixinOpenid", wxMessage.getFromUser());
//        if (user != null ) {
//            Member member = memberService.findBy("userId", user.getId());
//            String qrCodeUrl = userService.generateQrCodeUrl(String.valueOf(user.getId()), "");
//            String levelName = "";
//            String msg1 = "";
//            String msg2 = "";
//            if(member != null) {
//                int level = member.getLevel().intValue();
//                switch (level) {
//                    case 8 :
//                        levelName = "店主";
//                        break;
//                    case 4 :
//                        levelName = "超级店主";
//                        break;
//                    case 1 :
//                        levelName = "代理商";
//                        break;
//                }
//                msg1 = "恭喜！您已成为水之肤" + levelName + "！现在可以分享自己的店铺啦！";
//                msg2 = "恭喜！您获得10.00元佣金！快去分销中心看看吧！";
//            }
//            qrCodeUrl = qrCodeUrl.substring(0, qrCodeUrl.indexOf("?"));
//            logger.info("qrCodeUrl: " + qrCodeUrl);
//            String msg0 = "恭喜！您已成为水之肤第"+ (virtuNum + userService.queryForCount()) +"位会员！";
//
//            WxMpXmlOutNewsMessage.Item item = new WxMpXmlOutNewsMessage.Item();
//            item.setDescription(msg1 + "\r" + msg2);
//            item.setPicUrl(""+ qrCodeUrl +"");
//            item.setTitle(msg0);
//            item.setUrl(qrCodeUrl);
//            return WxMpXmlOutMessage.NEWS().fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser()).addArticle(item).build();
//        } else {
//            String content = "收到信息内容：" + WxMpGsonBuilder.create().toJson(wxMessage);
//            return WxMpXmlOutMessage.TEXT().content(content).fromUser(wxMessage.getToUser()).toUser(wxMessage.getFromUser()).build();
//        }
        return null;
    }


}
