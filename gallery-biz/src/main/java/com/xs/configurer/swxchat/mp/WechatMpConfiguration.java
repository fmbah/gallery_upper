package com.xs.configurer.swxchat.mp;

import com.xs.configurer.swxchat.mp.handler.*;
import me.chanjar.weixin.mp.api.WxMpConfigStorage;
import me.chanjar.weixin.mp.api.WxMpInMemoryConfigStorage;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import static me.chanjar.weixin.common.api.WxConsts.EventType;
import static me.chanjar.weixin.common.api.WxConsts.XmlMsgType;

/**
 * @Auther: zx
 * @Date: 2018/5/16 14:02
 * @Description:
 */
@Configuration
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnClass(WxMpService.class)
@EnableConfigurationProperties(WechatMpProperties.class)
@Scope("singleton")
public class WechatMpConfiguration {//implements CommandLineRunner
    public final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    protected LogHandler logHandler;
    //    @Autowired
//    protected NullHandler nullHandler;
//    @Autowired
//    protected KfSessionHandler kfSessionHandler;
    //    @Autowired
//    protected StoreCheckNotifyHandler storeCheckNotifyHandler;
    @Autowired
    private WechatMpProperties properties;
    //    @Autowired
//    private LocationHandler locationHandler;
//    @Autowired
//    private MenuHandler menuHandler;
    @Autowired
    private MsgHandler msgHandler;
    @Autowired
    private UnsubscribeHandler unsubscribeHandler;
    @Autowired
    private SubscribeHandler subscribeHandler;

    @Bean
    @Scope("singleton")
    @ConditionalOnMissingBean(WxMpMessageRouter.class)
    public WxMpMessageRouter wxMpMessageRouter(WxMpService wxMpService) {
        logger.info("=================================================================");
        logger.info(this.getClass().getSimpleName() + "，are you in！！！WxMpMessageRouter");
        logger.info("=================================================================");
        final WxMpMessageRouter newRouter = new WxMpMessageRouter(wxMpService);

        // 记录所有事件的日志 （异步执行）
        newRouter.rule().handler(this.logHandler).next();

        // 接收客服会话管理事件
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(WxMpEventConstants.CustomerService.KF_CREATE_SESSION)
//                .handler(this.kfSessionHandler).end();
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(WxMpEventConstants.CustomerService.KF_CLOSE_SESSION)
//                .handler(this.kfSessionHandler)
//                .end();
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(WxMpEventConstants.CustomerService.KF_SWITCH_SESSION)
//                .handler(this.kfSessionHandler).end();

//        // 门店审核事件
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(WxMpEventConstants.POI_CHECK_NOTIFY)
//                .handler(this.storeCheckNotifyHandler).end();
//
//        // 自定义菜单事件
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(MenuButtonType.CLICK).handler(this.getMenuHandler()).end();
//
//        // 点击菜单连接事件
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(MenuButtonType.VIEW).handler(this.nullHandler).end();
//
        // 关注事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
                .event(EventType.SUBSCRIBE).handler(this.getSubscribeHandler())
                .end();
//
//        // 取消关注事件
        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
                .event(EventType.UNSUBSCRIBE)
                .handler(this.getUnsubscribeHandler()).end();
//
//        // 上报地理位置事件
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(EventType.LOCATION).handler(this.getLocationHandler())
//                .end();
//
//        // 接收地理位置消息
//        newRouter.rule().async(false).msgType(XmlMsgType.LOCATION)
//                .handler(this.getLocationHandler()).end();
//
//        // 扫码事件
//        newRouter.rule().async(false).msgType(XmlMsgType.EVENT)
//                .event(EventType.SCAN).handler(this.getScanHandler()).end();
//
//        // 默认
        newRouter.rule().async(false).handler(this.getMsgHandler()).end();

        return newRouter;
    }

    @Bean
    @ConditionalOnMissingBean
    public WxMpConfigStorage wxMpConfigStorage() {
        WxMpInMemoryConfigStorage configStorage = new WxMpInMemoryConfigStorage();
        configStorage.setAppId(this.properties.getAppId());
        configStorage.setSecret(this.properties.getSecret());
        configStorage.setToken(this.properties.getToken());
        configStorage.setAesKey(this.properties.getAesKey());
        return configStorage;
    }

    @Bean
    @Scope("singleton")
    @ConditionalOnMissingBean
    public WxMpService wxMpService(WxMpConfigStorage configStorage) {
//        WxMpService wxMpService = new me.chanjar.weixin.mp.api.impl.okhttp.WxMpServiceImpl();
//        WxMpService wxMpService = new me.chanjar.weixin.mp.api.impl.jodd.WxMpServiceImpl();
//        WxMpService wxMpService = new me.chanjar.weixin.mp.api.impl.apache.WxMpServiceImpl();
        WxMpService wxMpService = new me.chanjar.weixin.mp.api.impl.WxMpServiceImpl();
        wxMpService.setWxMpConfigStorage(configStorage);
        return wxMpService;
    }

    //    protected MenuHandler getMenuHandler() {
//        return this.menuHandler;
//    }
//
    protected SubscribeHandler getSubscribeHandler() {
        return this.subscribeHandler;
    }

    protected UnsubscribeHandler getUnsubscribeHandler() {
        return this.unsubscribeHandler;
    }
    //
//    protected AbstractHandler getLocationHandler() {
//        return this.locationHandler;
//    }
//
    protected MsgHandler getMsgHandler() {
        return this.msgHandler;
    }

//    @Override
//    public void run(String... args) throws Exception {
//        this.wxMpMessageRouter(this.wxMpService(this.wxMpConfigStorage()));
//    }
//
//    protected AbstractHandler getScanHandler() {
//        return null;
//    }

}
