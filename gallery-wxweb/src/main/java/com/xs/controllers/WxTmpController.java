package com.xs.controllers;

import com.xs.configurer.sannotation.IgnoreAuth;
import com.xs.core.scontroller.BaseController;
import com.xs.services.WxAppAllService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName WxTmpController
 * @Description
 * @Author root
 * @Date 18-12-21 下午7:21
 * @Version 1.0
 **/
@Controller
@RequestMapping("api/wx/app")
@Api(value="微信小程序接口", description = "微信小程序接口")
public class WxTmpController extends BaseController {
    @Autowired
    private WxAppAllService wxAppAllService;
    @IgnoreAuth
    @ApiOperation(value = "根据文字属性合成图片",notes = "根据文字属性合成图片")
    @PostMapping(value = "/drawFontsToPic1")
    public void drawFontsToPic1(@RequestPart("base64Var") MultipartFile base64Var, @RequestPart(name = "fontToPics") String fontToPics, @RequestPart(name = "filterPic", required = false) String filterPic){
        wxAppAllService.drawFontsToPic1(base64Var, this.response, fontToPics,filterPic);
    }
}
