package com.xs.configurer.swxchat.mp.handler;

import com.xs.beans.User;
import com.xs.services.UserService;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 *
 * 功能描述: 公众号关注结果
 *
 * @param:
 * @return:
 * @auther: Fmbah
 * @date: 18-10-22 下午7:42
 */
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Scope("singleton")
public class SubscribeHandler extends AbstractHandler {

    @Autowired
    UserService userService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMessage, Map<String, Object> map, WxMpService weixinService, WxSessionManager wxSessionManager) throws WxErrorException {
        this.logger.info("新关注用户 OPENID: " + wxMessage.getFromUser());

        WxMpUser userWxInfo = null;
        try {
            userWxInfo = weixinService.getUserService().userInfo(wxMessage.getFromUser(), null);
        } catch (WxErrorException e) {
            e.printStackTrace();
        }

        if (userWxInfo != null) {
            // TODO 可以添加关注用户到本地
            User user = userService.findBy("wxOpenid", userWxInfo.getOpenId());
            if (user == null) {
                user = userService.findBy("wxUnionid", userWxInfo.getUnionId());
            }
            if (user == null) {
                user.setShareProfitAmount(BigDecimal.ZERO);
                user.setWxOpenid(userWxInfo.getOpenId());
                user.setWxUnionid(userWxInfo.getUnionId());
                user.setWxMiniOpenid(StringUtils.EMPTY);
                user.setWxSex(Byte.valueOf(userWxInfo.getSex().toString()));
                user.setWxHeadimgurl(userWxInfo.getHeadImgUrl());
                user.setMemberType(new Byte("0"));
                Calendar instance = Calendar.getInstance();
                instance.set(Calendar.YEAR, 1970);
                instance.set(Calendar.MONTH, 0);
                instance.set(Calendar.DATE, 1);
                instance.set(Calendar.HOUR, 0);
                instance.set(Calendar.MINUTE, 0);
                instance.set(Calendar.SECOND, 0);
                user.setMemberExpired(instance.getTime());
                user.setRecommendId(0);
                user.setGmtCreate(new Date());
                user.setGmtModified(new Date());
                user.setIsAgent(false);
                user.setCashBalance(BigDecimal.ZERO);
                user.setNickname(userWxInfo.getNickname());
                userService.save(user);
            }
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

        try {
            return null;
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
