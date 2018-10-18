package com.xs.configurer.swxchat.mp.handler;

import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.util.json.WxMpGsonBuilder;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 \* 杭州桃子网络科技股份有限公司
 \* User: zhaoxin
 \* Date: 2018/6/26
 \* Time: 15:00
 \* Description: 
 \*/
@Component
public class LogHandler extends AbstractHandler {
    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage,
                                    Map<String, Object> context, WxMpService wxMpService,
                                    WxSessionManager sessionManager) {
        this.logger.info("\n接收到请求消息，内容：{}", WxMpGsonBuilder.create().toJson(wxMessage));
        return null;
    }

}
