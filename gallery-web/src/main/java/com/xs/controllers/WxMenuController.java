package com.xs.controllers;

import com.xs.configurer.sannotation.IgnoreAuth;
import com.xs.core.ResultGenerator;
import com.xs.core.scontroller.BaseController;
import com.xs.services.WxMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Auther: Fmbah
 * @Date: 18-10-31 下午4:18
 * @Description:
 */
@RestController
@RequestMapping("/api/back/menu")
@Api(value="维护公众号菜单", description = "后台管理系统-维护公众号菜单")
public class WxMenuController extends BaseController {

    @Autowired
    WxMenuService wxMenuService;

    @PostMapping("/create")
    @ApiOperation(value = "创建菜单，目前固定菜单，暂不维护", notes = "创建菜单，目前固定菜单，暂不维护")
    public Object menuCreate(@ApiParam(name = "json", value = "json字符串", type = "string", required = true) @RequestParam String json){
        return ResultGenerator.genSuccessResult(wxMenuService.menuCreate(json));
    }

    @IgnoreAuth
    @PostMapping("drawpic")
    public Object drawpic(@RequestBody String fontToPics, @RequestParam String pic) {
        return wxMenuService.drawpic(fontToPics, pic);
    }
}
