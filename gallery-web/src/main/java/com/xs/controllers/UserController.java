package com.xs.controllers;
import com.xs.core.ResultGenerator;
import com.xs.beans.User;
import com.xs.services.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xs.core.scontroller.BaseController;
import io.swagger.annotations.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import org.springframework.validation.BindingResult;
import java.util.List;

/**
\* User: zhaoxin
\* Date: 2018/10/19
\* To change this template use File | Settings | File Templates.
\* Description:
\*/

@Api(description = "用户管理")
@RestController
@RequestMapping(value="/api/back/user")
public class UserController extends BaseController{
    @Autowired
    private UserService userService;


//    /***
//    * 新增
//    * @return
//    */
//    @ApiOperation(value = "录入",notes = "录入")
//    @PutMapping(value = "/add",produces = "application/json;charset=utf-8")
//    public Object add(@Valid @RequestBody User user, BindingResult bindingResult) {
//        userService.save(user);
//        return ResultGenerator.genSuccessResult();
//    }
//
//    /***
//    * 删除
//    * @return
//    */
//    @ApiOperation(value = "删除",notes = "删除")
//    @ApiImplicitParams({
//    @ApiImplicitParam(value = "ID",name="id",required = true,paramType = "query")
//    })
//    @DeleteMapping(value = "/delete",produces = "application/json;charset=utf-8")
//    public Object delete(@RequestParam Integer id) {
//        userService.deleteById(id);
//        return ResultGenerator.genSuccessResult();
//    }
//
//    /***
//    * 修改
//    * @return
//    */
//    @ApiOperation(value = "修改",notes = "修改")
//    @PostMapping(value = "/update",produces = "application/json;charset=utf-8")
//    public Object update(@Valid @RequestBody User user, BindingResult bindingResult) {
//        userService.update(user);
//        return ResultGenerator.genSuccessResult();
//    }

    @ApiOperation(value = "开通/取消代理",notes = "开通/取消代理")
    @PostMapping(value = "/modifiedAgentStatus/{id}/{isAgent}/",produces = "application/json;charset=utf-8")
    public Object modifiedAgentStatus(@PathVariable Integer id,
                                      @PathVariable Boolean isAgent) {
        return userService.modifiedAgentStatus(id, isAgent);
    }

    /***
    * 详情
    * @return
    */
    @ApiOperation(value = "详情",notes = "详情")
    @ApiImplicitParams({@ApiImplicitParam(value = "ID",name="id",required = true,paramType = "query")})
    @GetMapping(value = "/detail",produces = "application/json;charset=utf-8")
    public Object detail(@RequestParam Integer id) {
        User user = userService.findById(id);
        return ResultGenerator.genSuccessResult(user);
    }

    /***
    * 分页列表
    * @return
    */
    @ApiOperation(value = "分页列表",notes = "分页列表")
    @ApiImplicitParams({
    @ApiImplicitParam(value = "页码",name = "page", paramType = "query",defaultValue = "0"),
    @ApiImplicitParam(value = "条数",name = "size",paramType = "query",defaultValue = "0"),
            @ApiImplicitParam(value = "是否会员",name = "isMember",paramType = "query"),
            @ApiImplicitParam(value = "会员类型",name = "memberType",paramType = "query"),
            @ApiImplicitParam(value = "是否代理",name = "isAgent",paramType = "query"),
            @ApiImplicitParam(value = "开始时间",name = "sTime",paramType = "query"),
            @ApiImplicitParam(value = "结束时间",name = "eTime",paramType = "query"),
            @ApiImplicitParam(value = "id",name = "id",paramType = "query"),
            @ApiImplicitParam(value = "昵称",name = "nickname",paramType = "query"),
            @ApiImplicitParam(value = "是否导出",name = "isExport",paramType = "query"),
            @ApiImplicitParam(value = "品牌id",name = "brandId",paramType = "query")

    })
    @GetMapping(value = "/list",produces = "application/json;charset=utf-8")
    public Object list(@RequestParam(required = false,defaultValue = "0") Integer page,
                       @RequestParam(required = false,defaultValue = "0") Integer size,
                       @RequestParam(required = false) Boolean isMember,
                       @RequestParam(required = false) Byte memberType,
                                   @RequestParam(required = false) Boolean isAgent,
                                   @RequestParam(required = false) String sTime,
                                   @RequestParam(required = false) String eTime,
                                   @RequestParam(required = false) Integer id,
                                   @RequestParam(required = false) String nickname,
                                    @RequestParam Boolean isExport,
                       @RequestParam(required = false) Integer brandId
    ){
        return userService.queryWithPage(page, size, isMember, memberType, isAgent, sTime,
                eTime, id, nickname, isExport, brandId);
    }
}
