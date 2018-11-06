package com.xs.controllers;

import com.xs.beans.AuthUser;
import com.xs.configurer.sannotation.IgnoreAuth;
import com.xs.core.ResultGenerator;
import com.xs.core.scontroller.BaseController;
import com.xs.services.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import java.util.Map;

import static com.xs.core.ProjectConstant.BACK_MANAGER_KEY;

/**
 * @Auther: Fmbah
 * @Date: 18-10-19 上午11:37
 * @Description: 微信小程序登录接口
 */
@RestController
@RequestMapping("api/wx/app/wechat")
@Api(value="微信小程序登录接口", description = "微信小程序-身份认证接口，用户信息接口")
public class WxMiniAppController extends BaseController {

    @Autowired
    private UserService userService;

    /**
    * @Description: 微信小程序授权登录接口
    * @Param:  code:用户授权后返回值
     *          signature 使用 sha1( rawData + sessionkey ) 得到字符串，用于校验用户信息
     *          rawData 不包括敏感信息的原始数据字符串，用于计算签名
     *          encryptedData 包括敏感数据在内的完整用户信息的加密数据
     *          iv 加密算法的初始向量
     *          参照：https://developers.weixin.qq.com/miniprogram/dev/api/open.html#wxgetuserinfoobject
    * @return:
    * @Author: zhaoxin
    * @Date: 2018/5/31
    **/
    @IgnoreAuth
    @PostMapping(value = "/user/login", produces = "application/json;charset=utf-8")
    @ApiOperation(value = "微信小程序身份认证接口", notes = "微信小程序身份认证接口")
    public Object login(@RequestBody AuthUser authUser) {
        Map<String, Object> result = userService.login(authUser.getCode(), authUser.getSignature(), authUser.getRawData(), authUser.getEncryptedData(), authUser.getIv(), this.request);
        return ResultGenerator.genSuccessResult(result);
    }

}
