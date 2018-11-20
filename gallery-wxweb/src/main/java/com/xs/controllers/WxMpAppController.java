package com.xs.controllers;

import com.xs.configurer.sannotation.IgnoreAuth;
import com.xs.core.scontroller.BaseController;
import com.xs.services.SWxAuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import me.chanjar.weixin.mp.api.WxMpMessageRouter;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import springfox.documentation.annotations.ApiIgnore;

/**
 *
 * 功能描述: 
 *
 * @param:
 * @return: 
 * @auther: Fmbah
 * @date: 18-10-22 下午3:15
 */
@Api(value = "WxMpAppController",description = "微信公众号接口")
@RestController
@RequestMapping(value = "/api/wx/wechat")
public class WxMpAppController extends BaseController {


    @Autowired
    private WxMpService wxService;

    @Autowired
    private WxMpMessageRouter router;

    @Autowired
    private SWxAuthService sWxAuthService;
    @Value("${gallery.domain.personUrl}")
    private String personUrl;
    @Value("${gallery.domain.rechargeUrl}")
    private String rechargeUrl;

    @ApiIgnore
    @IgnoreAuth
    @GetMapping(produces = "text/plain;charset=utf-8")
    public String authGet(
            @RequestParam(name = "signature",
                    required = false) String signature,
            @RequestParam(name = "timestamp",
                    required = false) String timestamp,
            @RequestParam(name = "nonce", required = false) String nonce,
            @RequestParam(name = "echostr", required = false) String echostr) {

        this.logger.info("\n接收到来自微信服务器的认证消息：[{}, {}, {}, {}]", signature,
                timestamp, nonce, echostr);

        if (StringUtils.isAnyBlank(signature, timestamp, nonce, echostr)) {
            throw new IllegalArgumentException("请求参数非法，请核实!");
        }

        if (this.wxService.checkSignature(timestamp, nonce, signature)) {
            return echostr;
        }

        return "非法请求";
    }

    @ApiIgnore
    @IgnoreAuth
    @PostMapping(produces = "application/xml; charset=UTF-8")
    public String post(@RequestBody String requestBody,
                       @RequestParam("signature") String signature,
                       @RequestParam("timestamp") String timestamp,
                       @RequestParam("nonce") String nonce,
                       @RequestParam(name = "encrypt_type",
                               required = false) String encType,
                       @RequestParam(name = "msg_signature",
                               required = false) String msgSignature) {
        this.logger.info(
                "\n接收微信请求：[signature=[{}], encType=[{}], msgSignature=[{}],"
                        + " timestamp=[{}], nonce=[{}], requestBody=[\n{}\n] ",
                signature, encType, msgSignature, timestamp, nonce, requestBody);

        if (!this.wxService.checkSignature(timestamp, nonce, signature)) {
            throw new IllegalArgumentException("非法请求，可能属于伪造的请求！");
        }

        String out = null;
        if (encType == null) {
            // 明文传输的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromXml(requestBody);
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            if (outMessage == null) {
                return "";
            }

            out = outMessage.toXml();
        } else if ("aes".equals(encType)) {
            // aes加密的消息
            WxMpXmlMessage inMessage = WxMpXmlMessage.fromEncryptedXml(
                    requestBody, this.wxService.getWxMpConfigStorage(), timestamp,
                    nonce, msgSignature);
            this.logger.debug("\n消息解密后内容为：\n{} ", inMessage.toString());
            WxMpXmlOutMessage outMessage = this.route(inMessage);
            if (outMessage == null) {
                return "";
            }

            out = outMessage
                    .toEncryptedXml(this.wxService.getWxMpConfigStorage());
        }

        this.logger.debug("\n组装回复信息：{}", out);

        return out;
    }

    private WxMpXmlOutMessage route(WxMpXmlMessage message) {
        try {
            return this.router.route(message);
        } catch (Exception e) {
            this.logger.error(e.getMessage(), e);
        }
        return null;
    }

    /**
     *
     * 功能描述:
     *
     * @param:
     * @return:
     * @auther: Fmbah
     * @date: 18-10-22 下午7:53
     */
    @GetMapping(value = "personCenter", produces = "application/json;charset=utf-8")
    @ApiOperation(value = "个人中心按钮", notes = "个人中心按钮")
    @ResponseBody
    @IgnoreAuth
    public ModelAndView personCenter() {
        Object result = sWxAuthService.distributionCenterAuth(this.request);
        if (result != null) {
            return new ModelAndView(new RedirectView(this.personUrl + result));
        }
        return new ModelAndView(new RedirectView(this.personUrl));
    }

    @GetMapping(value = "recharage", produces = "application/json;charset=utf-8")
    @ApiOperation(value = "充值按钮", notes = "充值按钮")
    @ResponseBody
    @IgnoreAuth
    public ModelAndView recharage() {
        Object result = sWxAuthService.distributionCenterAuth(this.request);
        if (result != null) {
            return new ModelAndView(new RedirectView(this.rechargeUrl + result));
        }
        return new ModelAndView(new RedirectView(this.rechargeUrl));
    }

    @GetMapping(value = "getPersonal/{userId}/", produces = "application/json;charset=utf-8")
    @ApiOperation(value = "个人中心", notes = "个人中心")
    public Object getPersonal(@PathVariable Integer userId) {
        return sWxAuthService.getPersonal(userId);
    }

    @PostMapping(value = "askForCash/{userId}/", produces = "application/json;charset=utf-8")
    @ApiOperation(value = "申请提现", notes = "申请提现")
    public Object askForCash(@PathVariable Integer userId) {
        return sWxAuthService.askForCash(userId);
    }





}
