package com.xs.controllers;

import com.xs.beans.SearchTemplates;
import com.xs.core.scontroller.BaseController;
import com.xs.services.UserService;
import com.xs.services.WxAppAllService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.validation.Valid;

/**
 * @Auther: Fmbah
 * @Date: 18-10-19 上午11:37
 * @Description: 微信小程序接口
 */
@RestController
@RequestMapping("api/wx/app/all")
@Api(value="微信小程序接口", description = "微信小程序接口")
public class WxAppAllController extends BaseController {

    @Autowired
    private WxAppAllService wxAppAllService;

    @GetMapping(value = "/user/info/{id}", produces = "application/json;charset=utf-8")
    @ApiOperation(value = "用户详情", notes = "用户详情")
    public Object info(@ApiParam(name = "id", value="用户id", required = true, type = "query") @PathVariable Integer id) {
        return wxAppAllService.findUserById(id);
    }

    @GetMapping(value = "/getSlides/{type}", produces = "application/json;charset=utf-8")
    @ApiOperation(value = "首页轮播图", notes = "首页轮播图")
    public Object getSlides(@ApiParam(name = "type", value="位置（首页：1；分享获益：2；会员权益：3；）", required = true, type = "query") @PathVariable Integer type) {
        return wxAppAllService.getSlides(type);
    }

    @GetMapping(value = "/getIndexCategorys", produces = "application/json;charset=utf-8")
    @ApiOperation(value = "首页分类接口", notes = "首页分类接口")
    public Object getIndexCategorys() {
        return wxAppAllService.getIndexCategorys();
    }

    @GetMapping(value = "/openBrandDatas/{userId}/", produces = "application/json;charset=utf-8")
    @ApiOperation(value = "点击品牌中心分类名称后验证是否是品牌会员", notes = "点击品牌中心分类名称后验证是否是品牌会员")
    public Object openBrandDatas(@ApiParam(name = "userId", value="用户id", required = true, type = "query") @PathVariable Integer userId) {
        return wxAppAllService.openBrandDatas(userId);
    }

    @PostMapping(value = "/searchTemplates", produces = "application/json;charset=utf-8")
    @ApiOperation(value = "搜索功能,按照模板标题,模板类别,模板标签搜索数据, 首页分类", notes = "搜索功能,按照模板标题,模板类别,模板标签搜索数据, 首页分类")
    public Object searchTemplates(@Valid @RequestBody(required = false) SearchTemplates searchTemplates, BindingResult bindingResult
    ) {
        return wxAppAllService.searchTemplates(searchTemplates);
    }


    @GetMapping(value = "/templateInfo/{userId}/{id}/", produces = "application/json;charset=utf-8")
    @ApiOperation(value = "模板详情", notes = "模板详情")
    public Object templateInfo(@ApiParam(name = "userId", value="用户id", type = "query") @PathVariable Integer userId,
                               @ApiParam(name = "id", value="模板名称", type = "query") @PathVariable Integer id) {
        return wxAppAllService.templateInfo(userId, id);
    }

    @GetMapping(value = "/templateCenter/", produces = "application/json;charset=utf-8")
    @ApiOperation(value = "模板中心 分类/筛选 搜索", notes = "模板中心 分类/筛选 搜索")
    public Object templateCenter(@ApiParam(value = "页码",name = "page", type = "query",defaultValue = "0") @RequestParam(required = false, defaultValue = "0") Integer page,
                                 @ApiParam(value = "每页容量",name = "size", type = "query",defaultValue = "0") @RequestParam(required = false, defaultValue = "0") Integer size,
                                 @ApiParam(value = "分类id",name = "categoryId", type = "query") @RequestParam(required = false) Integer categoryId,
                                 @ApiParam(value = "比例,业务方定",name = "ratio", type = "query") @RequestParam(required = false) Byte ratio
                                ,@ApiParam(name = "userId", value="用户id", type = "query", required = true) @RequestParam Integer userId) {
        return wxAppAllService.templateCenter(page, size, categoryId, ratio, userId);
    }


    @PostMapping(value = "/saveCollection/{userId}/{id}/", produces = "application/json;charset=utf-8")
    @ApiOperation(value = "模板收藏与取消收藏", notes = "模板收藏与取消收藏")
    public Object saveCollection(@ApiParam(name = "userId", value="分类名称", type = "query",required = true) @PathVariable Integer userId,
                                 @ApiParam(name = "id", value="模板名称", type = "query",required = true) @PathVariable Integer id) {
        return wxAppAllService.saveCollection(userId, id);
    }

    @GetMapping(value = "/userCollections/{userId}/", produces = "application/json;charset=utf-8")
    @ApiOperation(value = "用户收藏模板集合", notes = "用户收藏模板集合")
    public Object userCollections(@ApiParam(name = "userId", value="用户id", type = "query",required = true) @PathVariable Integer userId
            , @ApiParam(name = "searchText", value="搜索文字", type = "query",required = false) @RequestParam(required = false) String searchText
                                 ) {
        return wxAppAllService.userCollections(userId, searchText);
    }

    @PostMapping(value = "/verifyBrandCode/{userId}/{code}/", produces = "application/json;charset=utf-8")
    @ApiOperation(value = "验证品牌激活码", notes = "验证品牌激活码")
    public Object verifyBrandCode(@ApiParam(name = "userId", value="分类名称", type = "query",required = true) @PathVariable Integer userId,
                                 @ApiParam(name = "code", value="模板名称", type = "query",required = true) @PathVariable String code) {
        return wxAppAllService.verifyBrandCode(userId, code);
    }


    @RequestMapping(value = "orderDown",method = RequestMethod.POST)
    @ApiOperation(value = "下订单",notes = "下订单")
    public Object orderDown(@ApiParam(name = "rechargeType", value = "支付类型：5: 半年会员 6: 全年会员 10: 终身会员", required = true, type = "string") @RequestParam Byte rechargeType,
                            @ApiParam(name = "userId", value = "用户id", required = true, type = "string") @RequestParam Integer userId){

        return wxAppAllService.orderDown(userId, rechargeType);
    }


    @PostMapping(value = "/templateIncr/{userId}/{templateId}/{type}/", produces = "application/json;charset=utf-8")
    @ApiOperation(value = "分享和使用调用此方法,用来统计数据", notes = "分享和使用调用此方法,用来统计数据")
    public Object templateIncr(@ApiParam(name = "userId", value="用户id", type = "query",required = true) @PathVariable Integer userId,
                                  @ApiParam(name = "templateId", value="模板id", type = "query",required = true) @PathVariable Integer templateId,
                               @ApiParam(name = "type", value="类型(1:分享 2:使用 3:查看)", type = "query",required = true) @PathVariable Integer type) {
        return wxAppAllService.templateIncr(userId, templateId, type);
    }


}